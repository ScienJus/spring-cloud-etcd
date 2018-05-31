package com.scienjus.spring.cloud.etcd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * properties for etcd
 *
 * @author ScienJus
 */
@Data
@ConfigurationProperties("spring.cloud.etcd")
public class EtcdProperties {

  private List<String> endpoints = Arrays.asList("http://127.0.0.1:2379");
}
