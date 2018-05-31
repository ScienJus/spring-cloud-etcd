package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.scienjus.spring.cloud.etcd.ConditionalOnEtcdEnabled;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnEtcdEnabled
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
@EnableConfigurationProperties(EtcdDiscoveryProperties.class)
@AutoConfigureAfter(EtcdServiceRegistryAutoConfiguration.class)
public class EtcdAutoSerivceRegistrationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EtcdRegistration etcdAutoRegistration(InetUtils inetUtils, EtcdDiscoveryProperties properties) {
        if (StringUtils.isEmpty(properties.getAddress())) {
            String ipAddress = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
            properties.setAddress(ipAddress);
        }
        return new EtcdRegistration(properties.getName(), properties.getAddress(), properties.getPort());
    }

    @Bean
    @ConditionalOnMissingBean
    public EtcdAutoServiceRegistration etcdAutoServiceRegistration(EtcdServiceRegistry registry, EtcdRegistration etcdRegistration) {
        return new EtcdAutoServiceRegistration(registry, etcdRegistration);
    }
}
