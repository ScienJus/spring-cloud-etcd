package com.scienjus.spring.cloud.etcd;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

@Slf4j
public class EtcdContainer implements AutoCloseable {

    private static final String ETCD_DOCKER_IMAGE = "quay.io/coreos/etcd:latest";

    private static final int ETCD_CLIENT_PORT = 2379;

    private final GenericContainer container;

    public EtcdContainer() {
        this.container = new GenericContainer(ETCD_DOCKER_IMAGE)
                .withExposedPorts(ETCD_CLIENT_PORT, 2380)
                .withCommand(
                        "/usr/local/bin/etcd",
                        "--name", "node1",
                        "--advertise-client-urls", "http://0.0.0.0:2379",
                        "--listen-client-urls", "http://0.0.0.0:2379")
                .withLogConsumer(new Slf4jLogConsumer(log))
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*ready to serve client requests\n"));
    }

    public String clientEndpoint() {
        final String host = container.getContainerIpAddress();
        final int port = container.getMappedPort(ETCD_CLIENT_PORT);

        return "http://" + host + ":" + port;
    }

    public void start() {
        this.container.start();
    }

    @Override
    public void close() {
        if (this.container != null) {
            this.container.stop();
        }
    }
}
