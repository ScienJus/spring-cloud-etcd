package com.scienjus.spring.cloud.etcd.discovery;

import com.coreos.jetcd.Client;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ribbon configuration
 *
 * @author ScienJus
 */
@Configuration
public class EtcdRibbonClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServerList<?> ribbonServerList(IClientConfig config, Client etcdClient, EtcdDiscoveryProperties properties) {
        return new EtcdServerList(etcdClient, properties, config.getClientName());
    }
}
