package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.PutOption;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scienjus.spring.cloud.etcd.exception.EtcdOperationException;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.concurrent.ExecutionException;

import static com.coreos.jetcd.data.ByteSequence.fromString;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UP;

@AllArgsConstructor
public class EtcdServiceRegistry implements ServiceRegistry<EtcdRegistration> {

    private final Client etcdClient;

    private final EtcdDiscoveryProperties properties;

    private final EtcdHeartbeatLease lease;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void register(EtcdRegistration registration) {
        String etcdKey = registration.etcdKey(properties.getPrefix());
        try {
            long leaseId = lease.getLeaseId();
            PutOption putOption = PutOption.newBuilder()
                    .withLeaseId(leaseId)
                    .build();
            etcdClient.getKVClient()
                    .put(fromString(etcdKey), fromString(objectMapper.writeValueAsString(registration)), putOption)
                    .get();
        } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }

    @Override
    public void deregister(EtcdRegistration registration) {
        String etcdKey = registration.etcdKey(properties.getPrefix());
        try {
            etcdClient.getKVClient()
                    .delete(fromString(etcdKey))
                    .get();
            lease.revoke();
        } catch (InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void setStatus(EtcdRegistration registration, String status) {
        if (status.equalsIgnoreCase(OUT_OF_SERVICE.getCode())) {
            deregister(registration);
        } else if (status.equalsIgnoreCase(UP.getCode())) {
            register(registration);
        } else {
            throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    @Override
    public Object getStatus(EtcdRegistration registration) {
        String etcdKey = registration.etcdKey(properties.getPrefix());
        try {
            GetResponse response = etcdClient.getKVClient()
                    .get(fromString(etcdKey))
                    .get();
            if (response.getKvs().isEmpty()) {
                return OUT_OF_SERVICE.getCode();
            }
            return UP.getCode();
        } catch (InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }
}
