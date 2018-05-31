package com.scienjus.spring.cloud.etcd.discovery;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.scienjus.spring.cloud.etcd.exception.EtcdOperationException;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.coreos.jetcd.data.ByteSequence.fromString;

/**
 * {@link DiscoveryClient} implementation for etcd
 *
 * @author ScienJus
 */
@AllArgsConstructor
public class EtcdDiscoveryClient implements DiscoveryClient {

    private Client etcdClient;

    private EtcdDiscoveryProperties properties;

    @Override
    public String description() {
        return "Spring Cloud Etcd Discovery Client";
    }

    @Override
    public ServiceInstance getLocalServiceInstance() {
        return new DefaultServiceInstance(properties.getName(), properties.getAddress(), properties.getPort(), false);
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        String prefix = properties.getPrefix() + "/" + serviceId;
        GetOption option = GetOption.newBuilder()
                .withPrefix(fromString(prefix))
                .withKeysOnly(true)
                .build();

        try {
            GetResponse response = etcdClient.getKVClient().get(fromString(prefix), option)
                    .get();

            return response.getKvs().stream()
                    .map(KeyValue::getKey)
                    .map(ByteSequence::toStringUtf8)
                    .map(key -> {
                        String address = key.replace(prefix, "").substring(1);
                        String[] ipAndPort = address.split(":");
                        return new DefaultServiceInstance(serviceId, ipAndPort[0], Integer.parseInt(ipAndPort[1]), false);
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }

    @Override
    public List<String> getServices() {
        String prefix = properties.getPrefix();
        GetOption option = GetOption.newBuilder()
                .withPrefix(fromString(prefix))
                .withKeysOnly(true)
                .build();
        try {
            // todo etcdv3 can not use 'dir', will return all keys with prefix
            GetResponse response = etcdClient.getKVClient().get(fromString(prefix), option)
                    .get();

            return response.getKvs().stream()
                    .map(KeyValue::getKey)
                    .map(ByteSequence::toStringUtf8)
                    .map(key -> key.split("/")[2])
                    .distinct()
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }
}
