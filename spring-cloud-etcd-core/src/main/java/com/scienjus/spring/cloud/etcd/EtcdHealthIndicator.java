package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.coreos.jetcd.data.ByteSequence.fromString;

/**
 * {@link HealthIndicator} for {@link Client}
 *
 * @author ScienJus
 */
@Slf4j
@AllArgsConstructor
public class EtcdHealthIndicator extends AbstractHealthIndicator {

    private final Client etcdClient;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // todo: how to check cluster health using v3 api, /health or statusMember?
        // currently just trying to get a random key
        String randomKey = UUID.randomUUID().toString();
        etcdClient.getKVClient().get(fromString(randomKey)).get(1, TimeUnit.SECONDS);
        builder.up();
    }

}
