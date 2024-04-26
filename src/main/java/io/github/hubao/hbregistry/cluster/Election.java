package io.github.hubao.hbregistry.cluster;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/4/26 23:00
 */
@Slf4j
public class Election {

    public void electLeader(List<Server> servers) {
        List<Server> masters = servers.stream()
                .filter(Server::isStatus).filter(Server::isLeader).toList();
        if (masters.isEmpty()) {
            log.warn(" ====> elect for no leader: {}", servers);
            elect(servers);
        } else if (masters.size() > 1) {
            elect(servers);
            log.warn(" ====> elect for more than one leader: {}", servers);
        } else {
            masters.get(0).setLeader(true);
            log.debug(" ====> no need election for leader: {}", masters.get(0));
        }
    }

    private void elect(List<Server> servers) {
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
            log.debug(" ====> elect for leader: {}", servers);
        } else {
            log.debug(" ====> elect failed for no leaders: {}", servers);
        }
    }
}
