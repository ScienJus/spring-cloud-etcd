package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.scienjus.spring.cloud.etcd.ConditionalOnEtcdEnabled;
import com.scienjus.spring.cloud.etcd.EtcdAutoConfiguration;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.HeartbeatProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnEtcdEnabled
@ConditionalOnProperty(value = "spring.cloud.service-registry.enabled", matchIfMissing = true)
@EnableConfigurationProperties(HeartbeatProperties.class)
@AutoConfigureAfter(EtcdAutoConfiguration.class)
public class EtcdServiceRegistryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EtcdHeartbeatLease heartbeatLease(Client etcdClient, HeartbeatProperties properties) {
        return new EtcdHeartbeatLease(etcdClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdServiceRegistry etcdServiceRegistry(Client etcdClient, EtcdDiscoveryProperties properties, EtcdHeartbeatLease etcdHeartbeatLease) {
        return new EtcdServiceRegistry(etcdClient, properties, etcdHeartbeatLease);
    }
}
