package io.github.hubao.hbregistry.cluster;

import io.github.hubao.hbregistry.HbRegistryConfigProperties;
import io.github.hubao.hbregistry.http.HttpInvoker;
import io.github.hubao.hbregistry.model.Server;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
 * Desc: Registry Cluster.
 *
 * @author hubao
 * @see 2024/4/25 21:02
 */
@Slf4j
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;

    HbRegistryConfigProperties hbRegistryConfigProperties;

    public Cluster(HbRegistryConfigProperties hbRegistryConfigProperties) {
        this.hbRegistryConfigProperties = hbRegistryConfigProperties;
    }

    @Getter
    private List<Server> servers;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 5_000;

    public void init(){

        try {
            host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
            log.info(" ====> findFirstNonLoopbackHostInfo = {}", host);
        } catch (Exception e) {
            host = "127.0.0.1";
        }

        MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        log.info(" ====> myself = {}", MYSELF);


        List<Server> servers = new ArrayList<>();
        for (String url : hbRegistryConfigProperties.getServerList()) {
            Server server = new Server();
            if (url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }
            if (url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L);
                servers.add(server);
            }
        }
        this.servers = servers;

        executor.scheduleAtFixedRate(()->{
                    try {
                        updateServers();
                        electLeader();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        , 0, timeout, TimeUnit.MILLISECONDS);
    }

    private void electLeader() {

        List<Server> masters = this.servers.stream()
                .filter(Server::isStatus).filter(Server::isLeader).toList();
        if (masters.isEmpty()) {
            log.info(" ====> elect for no leader: {}", servers);
            elect();
        } else if (masters.size() > 1) {
            elect();
            log.info(" ====> elect for more than one leader: {}", servers);
        } else {
            masters.get(0).setLeader(true);
            log.info(" ====> no need election for leader: {}", masters.get(0));
        }
    }

    private void elect() {
        Server candidate = null;
        for (Server server : servers) {
            server.setLeader(false);
            if (server.isStatus()) {
                if (candidate == null) {
                    candidate = server;
                } else {
                    if (candidate.hashCode() > server.hashCode()) {
                        candidate = server;
                    }
                }
            }
        }

        if (candidate != null) {
            candidate.setLeader(true);
            log.info(" ====> elect for leader: {}", servers);
        } else {
            log.info(" ====> elect failed for no leaders: {}", servers);
        }
    }

    private void updateServers() {

        servers.forEach(server -> {
            try {
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                log.info(" ====> health check success for {}", serverInfo);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception e) {
                log.info(" ====> health check failed for {}", server);
                server.setStatus(false);
            }
        });
    }

    public Server self() {
        return MYSELF;
    }

    public Server leader() {
        return  this.servers.stream()
                .filter(Server::isStatus).filter(Server::isLeader).findFirst().orElse(null);
    }
}
