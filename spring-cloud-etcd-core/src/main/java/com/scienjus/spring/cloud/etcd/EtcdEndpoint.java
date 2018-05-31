package com.scienjus.spring.cloud.etcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.maintenance.StatusResponse;
import com.scienjus.spring.cloud.etcd.exception.EtcdOperationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * {@link Endpoint} to report etcd cluster information.
 *
 * @author ScienJus
 */
public class EtcdEndpoint extends AbstractEndpoint<EtcdEndpoint.EtcdStatus> {

    private final Client etcdClient;

    public EtcdEndpoint(Client etcdClient) {
        super("etcd", true, true);
        this.etcdClient = etcdClient;
    }

    @Override
    public EtcdStatus invoke() {
        try {
            List<EtcdMemberStatus> memberStatuses = etcdClient.getClusterClient().listMember()
                    .get().getMembers().stream()
                    .flatMap(member ->
                            member.getClientURLS().stream()
                                    .map(url -> {
                                        try {
                                            StatusResponse response = etcdClient.getMaintenanceClient().statusMember(url).get();
                                            return new EtcdMemberStatus(member.getId(), member.getName(), url, response.getVersion());
                                        } catch (InterruptedException | ExecutionException e) {
                                            throw new IllegalStateException(e);
                                        }
                                    })
                    ).collect(Collectors.toList());
            return new EtcdStatus(memberStatuses);
        } catch (InterruptedException | ExecutionException e) {
            throw new EtcdOperationException(e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class EtcdStatus {

        private List<EtcdMemberStatus> members;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class EtcdMemberStatus {

        private long id;

        private String name;

        private String url;

        private String version;
    }
}
