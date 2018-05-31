package com.scienjus.spring.cloud.etcd.discovery;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;
import com.netflix.loadbalancer.ServerList;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.coreos.jetcd.data.ByteSequence.fromString;

/**
 * ribbon {@link ServerList} for etcd
 *
 * @author ScienJus
 */
@Slf4j
@AllArgsConstructor
public class EtcdServerList extends AbstractServerList<EtcdServer> {

    private final Client etcdClient;

    private final EtcdDiscoveryProperties properties;

    private String serviceId;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        this.serviceId = iClientConfig.getClientName();
    }

    @Override
    public List<EtcdServer> getInitialListOfServers() {
        return getServers();
    }

    @Override
    public List<EtcdServer> getUpdatedListOfServers() {
        return getServers();
    }

    private List<EtcdServer> getServers() {
        if (etcdClient == null) {
            return Collections.emptyList();
        }

        try {
            String prefix = properties.getPrefix() + "/" + serviceId;
            GetOption option = GetOption.newBuilder()
                    .withPrefix(fromString(prefix))
                    .withKeysOnly(true)
                    .build();

            GetResponse response = etcdClient.getKVClient().get(fromString(prefix), option)
                    .get();

            return response.getKvs().stream()
                    .map(KeyValue::getKey)
                    .map(ByteSequence::toStringUtf8)
                    .map(key -> {
                        String address = key.replace(prefix, "").substring(1);
                        String[] ipAndPort = address.split(":");
                        return new EtcdServer(serviceId, ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Fetch services[{}] from etcd failed", serviceId, e);
            return Collections.emptyList();
        }
    }

}
