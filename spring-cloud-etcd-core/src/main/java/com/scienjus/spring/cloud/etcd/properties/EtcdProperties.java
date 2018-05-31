package com.scienjus.spring.cloud.etcd.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * properties for etcd
 *
 * @author ScienJus
 */
@Data
@ConfigurationProperties("spring.cloud.etcd")
public class EtcdProperties {

    private List<String> endpoints = new ArrayList<>(singletonList("http://127.0.0.1:2379"));
}
