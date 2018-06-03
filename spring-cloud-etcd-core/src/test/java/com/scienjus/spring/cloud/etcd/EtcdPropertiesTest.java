package com.scienjus.spring.cloud.etcd;

import com.scienjus.spring.cloud.etcd.properties.EtcdProperties;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.util.EnvironmentTestUtils.addEnvironment;

public class EtcdPropertiesTest {

    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    private static Stream<Arguments> etcdEndpoints() {
        return Stream.of(
                Arguments.of(
                        emptyList(),
                        singletonList("http://127.0.0.1:2379")),
                Arguments.of(
                        singletonList("http://10.0.0.1:2379"),
                        singletonList("http://10.0.0.1:2379")),
                Arguments.of(
                        asList("http://10.0.0.1:2379", "http://10.0.0.2:2379"),
                        asList("http://10.0.0.1:2379", "http://10.0.0.2:2379"))
        );
    }

    @MethodSource("etcdEndpoints")
    @ParameterizedTest(name = "[{index}] user input: {0}, actual endpoints: {1}")
    public void testEtcdPropertiesWithEndpoints(List<String> endpoints, List<String> actualEndpoints) {
        for (int i = 0; i < endpoints.size(); i++) {
            addEnvironment(this.context, String.format("spring.cloud.etcd.endpoints[%d]=%s", i, endpoints.get(i)));
        }
        this.context.register(EtcdAutoConfiguration.class);
        this.context.refresh();
        EtcdProperties etcdProperties = this.context.getBean(EtcdProperties.class);
        assertEquals(etcdProperties.getEndpoints(), actualEndpoints);
    }

    @Test
    public void testEtcdDisabled() {
        addEnvironment(this.context, "spring.cloud.etcd.enabled=false");
        this.context.register(EtcdAutoConfiguration.class);
        this.context.refresh();
        assertThrows(NoSuchBeanDefinitionException.class, () -> this.context.getBean(EtcdProperties.class));
    }

    @AfterEach
    public void closeContext() {
        this.context.close();
    }
}
