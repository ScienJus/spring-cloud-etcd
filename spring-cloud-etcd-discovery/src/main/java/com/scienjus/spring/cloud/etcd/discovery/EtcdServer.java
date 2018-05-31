package com.scienjus.spring.cloud.etcd.discovery;

import com.netflix.loadbalancer.Server;

/**
 * ribbon {@link Server} for etcd
 *
 * @author ScienJus
 */
public class EtcdServer extends Server {

    private final MetaInfo metaInfo;

    public EtcdServer(String appName, String host, int port) {
        super(host, port);
        metaInfo = new MetaInfo() {
            @Override
            public String getAppName() {
                return appName;
            }

            @Override
            public String getServerGroup() {
                return null;
            }

            @Override
            public String getServiceIdForDiscovery() {
                return null;
            }

            @Override
            public String getInstanceId() {
                return null;
            }
        };
    }

    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }
}
