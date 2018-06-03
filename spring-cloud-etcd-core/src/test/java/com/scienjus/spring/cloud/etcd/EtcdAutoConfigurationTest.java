package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.util.EnvironmentTestUtils.addEnvironment;

public class EtcdAutoConfigurationTest {

    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void testAfterConfigurationLoadedEtcdClientIsAvailable() throws ExecutionException, InterruptedException {
        ByteSequence key = ByteSequence.fromString("test_key");
        ByteSequence val = ByteSequence.fromString("test_val");

        // start an etcd container
        try (EtcdContainer container = new EtcdContainer()) {
            container.start();
            addEnvironment(this.context, "spring.cloud.etcd.endpoints[0]=" + container.clientEndpoint());
            this.context.register(EtcdAutoConfiguration.class);
            this.context.refresh();
            Client etcdClient = this.context.getBean(Client.class);
            KV kvClient = etcdClient.getKVClient();
            kvClient.put(key, val).get();

            GetResponse getResponse = kvClient.get(key).get();
            assertEquals(getResponse.getCount(), 1);

            KeyValue kv = getResponse.getKvs().get(0);
            assertEquals(key, kv.getKey());
            assertEquals(val, kv.getValue());
        }
    }

    @After
    public void closeContext() {
        this.context.close();
    }
}
