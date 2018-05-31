package com.scienjus.spring.cloud.etcd.serviceregistry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.Optional;

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
        return Optional.ofNullable(registration.getPort()).orElse(0);
    }

    @Override
    protected void setConfiguredPort(int port) {
        // do nothing
        registration.setPort(port);
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
