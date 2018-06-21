package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.scienjus.spring.cloud.etcd.exception.EtcdOperationException;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.HeartbeatProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EtcdHeartbeatLease implements AutoCloseable {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Client etcdClient;

    private final HeartbeatProperties properties;

    private Long leaseId;

    public EtcdHeartbeatLease(Client etcdClient, HeartbeatProperties properties) {
        this.etcdClient = etcdClient;
        this.properties = properties;
    }

    public void initLease() {
        if (leaseId == null) {
            synchronized (this) {
                if (leaseId == null) {
                    try {
                        // init lease
                        leaseId = etcdClient.getLeaseClient().grant(properties.getInterval()).get().getID();
                        log.info("Etcd init lease success. lease id: {}, hex: {}", leaseId, Long.toHexString(leaseId));
                        executor.execute(() -> {
                            try {
                                etcdClient.getLeaseClient().keepAlive(leaseId).listen();
                            } catch (InterruptedException e) {
                                throw new EtcdOperationException(e);
                            }
                        });
                    } catch (ExecutionException | InterruptedException e) {
                        throw new EtcdOperationException(e);
                    }
                }
            }
        }
    }

    public Long getLeaseId() {
        initLease();
        return leaseId;
    }

    public void revoke() {
        try {
            etcdClient.getLeaseClient().revoke(leaseId).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new EtcdOperationException(e);
        }
    }


    @Override
    public void close() throws Exception {
        revoke();
        executor.shutdown();
    }
}
