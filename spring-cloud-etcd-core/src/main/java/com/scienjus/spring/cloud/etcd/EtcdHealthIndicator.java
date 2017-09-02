package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * {@link HealthIndicator} for {@link Client}
 *
 * @author ScienJus
 */
public class EtcdHealthIndicator extends AbstractHealthIndicator {

	private Client etcd;

	public EtcdHealthIndicator(Client etcd) {
		this.etcd = etcd;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		// todo: how to check cluster health using v3 api, /health or statusMember?
		builder.up();
	}

}
