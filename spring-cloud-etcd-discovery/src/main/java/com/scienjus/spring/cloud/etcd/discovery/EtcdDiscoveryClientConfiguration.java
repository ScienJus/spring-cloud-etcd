package com.scienjus.spring.cloud.etcd.discovery;

import com.coreos.jetcd.Client;
import com.scienjus.spring.cloud.etcd.ConditionalOnEtcdEnabled;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableDiscoveryClient} configuration
 *
 * @author ScienJus
 */
@Configuration
@ConditionalOnEtcdEnabled
@ConditionalOnProperty(value = "spring.cloud.etcd.discovery.enabled", matchIfMissing = true)
@EnableConfigurationProperties(EtcdDiscoveryProperties.class)
public class EtcdDiscoveryClientConfiguration {

    @Bean
    public EtcdDiscoveryClient etcdDiscoveryClient(Client etcdClient, EtcdDiscoveryProperties properties) {
        return new EtcdDiscoveryClient(etcdClient, properties);
    }

}
