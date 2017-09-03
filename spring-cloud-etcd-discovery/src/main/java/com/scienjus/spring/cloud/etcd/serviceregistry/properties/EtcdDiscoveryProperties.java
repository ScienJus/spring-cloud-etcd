package com.scienjus.spring.cloud.etcd.serviceregistry.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.cloud.etcd.discovery")
public class EtcdDiscoveryProperties {

  @Value("${spring.application.name:application}")
  private String name;

  private String address;

  // todo how to get the running port
  @Value("${server.port:0}")
  private int port;
}
