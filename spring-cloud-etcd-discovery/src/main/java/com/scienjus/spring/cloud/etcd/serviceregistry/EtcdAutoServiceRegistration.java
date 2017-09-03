package com.scienjus.spring.cloud.etcd.serviceregistry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

public class EtcdAutoServiceRegistration extends AbstractAutoServiceRegistration<EtcdRegistration> {

  private final EtcdRegistration registration;

  protected EtcdAutoServiceRegistration(ServiceRegistry<EtcdRegistration> serviceRegistry, EtcdRegistration registration) {
    super(serviceRegistry);
    this.registration = registration;
  }

  @Override
  protected EtcdRegistration getRegistration() {
    return registration;
  }

  @Override
  protected EtcdRegistration getManagementRegistration() {
    // TODO: management registration
    return null;
  }

  @Override
  protected int getConfiguredPort() {
    return registration.getPort();
  }

  @Override
  protected void setConfiguredPort(int i) {
    // do nothing
  }

  @Override
  protected Object getConfiguration() {
    return null;
  }

  @Override
  protected boolean isEnabled() {
    // TODO: enabled
    return true;
  }
}
