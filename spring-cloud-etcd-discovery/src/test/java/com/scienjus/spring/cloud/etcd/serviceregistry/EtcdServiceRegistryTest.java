package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.lease.LeaseTimeToLiveResponse;
import com.coreos.jetcd.options.LeaseOption;
import com.scienjus.spring.cloud.etcd.EtcdContainer;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.HeartbeatProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EtcdServiceRegistryTest {

    private static EtcdContainer container = new EtcdContainer();
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void startEtcdContainer() {
        container.start();
    }

    @AfterAll
    public static void closeEtcdContainer() {
        container.close();
    }

    @Test
    public void testRegister() throws ExecutionException, InterruptedException, JsonProcessingException {
        Client client = Client.builder()
                .endpoints(container.clientEndpoint())
                .build();
        HeartbeatProperties heartbeatProperties = new HeartbeatProperties();
        heartbeatProperties.setInterval(5);
        EtcdHeartbeatLease heartbeatLease = new EtcdHeartbeatLease(client, heartbeatProperties);
        EtcdDiscoveryProperties discoveryProperties = new EtcdDiscoveryProperties();
        EtcdServiceRegistry registry = new EtcdServiceRegistry(client, discoveryProperties, heartbeatLease);

        EtcdRegistration registration = new EtcdRegistration(
                "test-app",
                "127.0.0.1",
                8080
        );
        String key = registration.etcdKey(discoveryProperties.getPrefix());

        registry.register(registration);

        // check lease
        Long leaseId = heartbeatLease.getLeaseId();
        assertNotNull(leaseId);
        LeaseTimeToLiveResponse leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.newBuilder().withAttachedKeys().build()).get();
        Thread.sleep(10);
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), heartbeatProperties.getInterval());
        assertTrue(leaseTimeToLiveResponse.getTTl() > 0);
        assertTrue(leaseTimeToLiveResponse.getTTl() < heartbeatProperties.getInterval());

        List<ByteSequence> keys = leaseTimeToLiveResponse.getKeys();
        assertEquals(keys.size(), 1);
        assertEquals(keys.get(0).toStringUtf8(), key);

        // check key-val
        GetResponse getResponse = client.getKVClient().get(fromString(key)).get();

        assertEquals(getResponse.getCount(), 1);
        assertEquals(getResponse.getKvs().get(0).getValue().toStringUtf8(), objectMapper.writeValueAsString(registration));
    }

    @Test
    public void testDeregister() throws ExecutionException, InterruptedException, JsonProcessingException {
        Client client = Client.builder()
                .endpoints(container.clientEndpoint())
                .build();
        HeartbeatProperties heartbeatProperties = new HeartbeatProperties();
        heartbeatProperties.setInterval(5);
        EtcdHeartbeatLease heartbeatLease = new EtcdHeartbeatLease(client, heartbeatProperties);
        EtcdDiscoveryProperties discoveryProperties = new EtcdDiscoveryProperties();
        EtcdServiceRegistry registry = new EtcdServiceRegistry(client, discoveryProperties, heartbeatLease);

        EtcdRegistration registration = new EtcdRegistration(
                "test-app",
                "127.0.0.1",
                8080
        );
        String key = registration.etcdKey(discoveryProperties.getPrefix());

        registry.register(registration);

        // check lease
        Long leaseId = heartbeatLease.getLeaseId();
        assertNotNull(leaseId);
        LeaseTimeToLiveResponse leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.newBuilder().withAttachedKeys().build()).get();
        Thread.sleep(10);
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), heartbeatProperties.getInterval());
        assertTrue(leaseTimeToLiveResponse.getTTl() > 0);
        assertTrue(leaseTimeToLiveResponse.getTTl() < heartbeatProperties.getInterval());

        List<ByteSequence> keys = leaseTimeToLiveResponse.getKeys();
        assertEquals(keys.size(), 1);
        assertEquals(keys.get(0).toStringUtf8(), key);

        // check key-val
        GetResponse getResponse = client.getKVClient().get(fromString(key)).get();

        assertEquals(getResponse.getCount(), 1);
        assertEquals(getResponse.getKvs().get(0).getValue().toStringUtf8(), objectMapper.writeValueAsString(registration));

        // deregister
        registry.deregister(registration);

        // check lease
        leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.newBuilder().withAttachedKeys().build()).get();
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), 0);
        assertEquals(leaseTimeToLiveResponse.getTTl(), -1);

        // check key-val
        getResponse = client.getKVClient().get(fromString(key)).get();

        assertEquals(getResponse.getCount(), 0);
    }
}