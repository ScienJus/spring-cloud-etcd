package com.scienjus.spring.cloud.etcd.serviceregistry.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * etcd heartbeat properties
 *
 * @author ScienJus
 */
@Data
@ConfigurationProperties("spring.cloud.etcd.discovery.heartbeat")
public class HeartbeatProperties {

    private int interval = 30;
}
