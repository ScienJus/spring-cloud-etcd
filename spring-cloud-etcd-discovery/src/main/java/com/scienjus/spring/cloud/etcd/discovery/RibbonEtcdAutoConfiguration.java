package com.scienjus.spring.cloud.etcd.discovery;

import com.scienjus.spring.cloud.etcd.ConditionalOnEtcdEnabled;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Configuration;

/**
 * ribbon auto configuration
 *
 * @author ScienJus
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnEtcdEnabled
@ConditionalOnBean(SpringClientFactory.class)
@ConditionalOnProperty(value = "spring.cloud.etcd.ribbon.enabled", matchIfMissing = true)
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@RibbonClients(defaultConfiguration = EtcdRibbonClientConfiguration.class)
public class RibbonEtcdAutoConfiguration {

}
