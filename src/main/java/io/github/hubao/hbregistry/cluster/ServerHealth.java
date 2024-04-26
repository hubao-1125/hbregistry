package io.github.hubao.hbregistry.cluster;

import io.github.hubao.hbregistry.http.HttpInvoker;
import io.github.hubao.hbregistry.service.HbRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/4/26 22:53
 */
@Slf4j
public class ServerHealth {

    Cluster cluster;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long interval = 5_000;

    public void checkServerHealth() {
        executor.scheduleAtFixedRate(()->{
                    try {
                        updateServers();
                        doElect();
                        syncSnapshotFromLeader();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                , 0, interval, TimeUnit.MILLISECONDS);
    }

    private void doElect() {
        new Election().electLeader(cluster.getServers());
    }

    private void syncSnapshotFromLeader() {
        Server leader = cluster.leader();
        Server self = cluster.self();
        log.debug(" ====> leader version: {} my version: {}",leader.getVersion(), self.getVersion());
        if (!self.isLeader() && self.getVersion() < leader.getVersion()) {
            log.debug(" ====> sync snapshot from leader: {}", leader);
            Snapshot snapshot = HttpInvoker.httpGet(leader.getUrl() + "/snapshot", Snapshot.class);
            log.debug(" ====> snapshot: {}", snapshot);
            HbRegistryService.restore(snapshot);
        }
    }



    private void updateServers() {
        List<Server> servers = cluster.getServers();
        servers.stream().parallel().forEach(server -> {
            try {
                if (server.equals(cluster.self())) {
                    return;
                }
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/debug", Server.class);
                log.debug(" ====> health check success for {}", serverInfo);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception e) {
                log.debug(" ====> health check failed for {}", server);
                server.setStatus(false);
            }
        });
    }
}
