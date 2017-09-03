package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * {@link HealthIndicator} for {@link Client}
 *
 * @author ScienJus
 */
@AllArgsConstructor
public class EtcdHealthIndicator extends AbstractHealthIndicator {

	private final Client etcdClient;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		// todo: how to check cluster health using v3 api, /health or statusMember?
		builder.up();
	}

}
