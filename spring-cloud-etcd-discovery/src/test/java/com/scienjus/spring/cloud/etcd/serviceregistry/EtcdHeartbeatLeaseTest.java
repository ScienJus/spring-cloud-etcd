package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.lease.LeaseTimeToLiveResponse;
import com.coreos.jetcd.options.LeaseOption;
import com.scienjus.spring.cloud.etcd.EtcdContainer;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.HeartbeatProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EtcdHeartbeatLeaseTest {

    private static EtcdContainer container = new EtcdContainer();

    @BeforeAll
    public static void startEtcdContainer() {
        container.start();
    }

    @AfterAll
    public static void closeEtcdContainer() {
        container.close();
    }

    @Test
    public void testEtcdLeaseIsAvailable() throws ExecutionException, InterruptedException {
        Client client = Client.builder()
                .endpoints(container.clientEndpoint())
                .build();
        HeartbeatProperties heartbeatProperties = new HeartbeatProperties();
        heartbeatProperties.setInterval(5);
        EtcdHeartbeatLease heartbeatLease = new EtcdHeartbeatLease(client, heartbeatProperties);
        Long leaseId = heartbeatLease.getLeaseId();
        assertNotNull(leaseId);
        LeaseTimeToLiveResponse leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.DEFAULT).get();
        Thread.sleep(10);
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), heartbeatProperties.getInterval());
        assertTrue(leaseTimeToLiveResponse.getTTl() > 0);
        assertTrue(leaseTimeToLiveResponse.getTTl() < heartbeatProperties.getInterval());
    }

    @Test
    public void testEtcdLeaseShutdown() throws Exception {
        Client client = Client.builder()
                .endpoints(container.clientEndpoint())
                .build();
        HeartbeatProperties heartbeatProperties = new HeartbeatProperties();
        heartbeatProperties.setInterval(5);
        EtcdHeartbeatLease heartbeatLease = new EtcdHeartbeatLease(client, heartbeatProperties);
        Long leaseId = heartbeatLease.getLeaseId();
        assertNotNull(leaseId);
        LeaseTimeToLiveResponse leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.DEFAULT).get();
        Thread.sleep(10);
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), heartbeatProperties.getInterval());
        assertTrue(leaseTimeToLiveResponse.getTTl() > 0);
        assertTrue(leaseTimeToLiveResponse.getTTl() < heartbeatProperties.getInterval());

        heartbeatLease.close();
        leaseTimeToLiveResponse =
                client.getLeaseClient().timeToLive(leaseId, LeaseOption.DEFAULT).get();
        assertEquals(leaseTimeToLiveResponse.getGrantedTTL(), 0);
        assertEquals(leaseTimeToLiveResponse.getTTl(), -1);
    }
}