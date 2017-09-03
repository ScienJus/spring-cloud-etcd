package com.scienjus.spring.cloud.etcd.serviceregistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cloud.client.serviceregistry.Registration;

@Data
@AllArgsConstructor
public class EtcdRegistration implements Registration {

  private String serviceName;

  private String address;

  private int port;

  @Override
  public String getServiceId() {
    return serviceName;
  }

  public String etcdKey() {
    return String.format("/services/%s/%s:%d",
            this.getServiceName(),
            this.getAddress(),
            this.getPort());
  }

}
