package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import com.scienjus.spring.cloud.etcd.properties.EtcdProperties;
import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration} for {@link Client}.
 *
 * @author ScienJus
 */
@Configuration
@ConditionalOnEtcdEnabled
@EnableConfigurationProperties(EtcdProperties.class)
public class EtcdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client etcdClient(EtcdProperties properties) {
        return Client.builder()
                .endpoints(properties.getEndpoints())
                .build();
    }

    @Configuration
    @ConditionalOnClass(Endpoint.class)
    protected static class EtcdHealthConfig {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnEnabledEndpoint("etcd")
        public EtcdEndpoint etcdEndpoint(Client etcdClient, EtcdProperties properties) {
            return new EtcdEndpoint(etcdClient, properties);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnEnabledHealthIndicator("etcd")
        public EtcdHealthIndicator etcdHealthIndicator(Client etcdClient) {
            return new EtcdHealthIndicator(etcdClient);
        }
    }
}
