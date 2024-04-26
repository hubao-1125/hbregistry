package io.github.hubao.hbregistry;

import io.github.hubao.hbregistry.cluster.Cluster;
import io.github.hubao.hbregistry.cluster.Snapshot;
import io.github.hubao.hbregistry.model.InstanceMeta;
import io.github.hubao.hbregistry.cluster.Server;
import io.github.hubao.hbregistry.service.HbRegistryService;
import io.github.hubao.hbregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
 * Desc: Rest controller for registry service.
 *
 * @author hubao
 * @see 2024/4/24$ 21:48$
 */
@RestController
@Slf4j
public class HbRegistryController {

    @Autowired
    RegistryService registryService;

    @Autowired
    Cluster cluster;


    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info(" ====> register service:{}, instance:{}", service, instance);
        checkLeader();
        return registryService.register(service, instance);
    }

    private void checkLeader() {
        if (!cluster.self().isLeader()) {
            throw new RuntimeException("current server is not a leader, the leader is {}" + cluster.leader().getUrl());
        }
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance) {
        log.info(" ====> unregister service:{}, instance:{}", service, instance);
        checkLeader();
        return registryService.unregister(service, instance);
    }

    @RequestMapping("/findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam String service) {
        log.info(" ====> findAllInstances service:{}", service);
        return registryService.getAllInstances(service);
    }

    @RequestMapping("/renew")
    public long renew(@RequestBody InstanceMeta instance, @RequestParam String service) {
        log.info(" ====> renew instance:{}, service:{}", instance, service);
        checkLeader();
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public long renews(@RequestBody InstanceMeta instance, @RequestParam String services) {
        log.info(" ====> renew instance:{}, services:{}", instance, services);
        checkLeader();
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public long version(@RequestParam String service) {
        log.info(" ====> version service:{}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam String services) {
        log.info(" ====> versions services:{}", services);
        return registryService.versions(services.split(","));
    }


    @RequestMapping("/info")
    public Server info() {
        log.info(" ====> info: {}", cluster.self());
        return cluster.self();
    }

    @RequestMapping("/cluster")
    public List<Server> cluster() {
        log.info(" ====> cluster: {}", cluster.getServers());
        return cluster.getServers();
    }

    @RequestMapping("/leader")
    public Server leader() {
        log.info(" ====> leader: {}", cluster.leader());
        return cluster.leader();
    }

    @RequestMapping("/sl")
    public Server sl() {
        cluster.self().setLeader(true);
        log.info(" ====> sl: {}", cluster.self());
        return cluster.self();
    }

    @RequestMapping("/snapshot")
    public Snapshot snapshot() {
        return HbRegistryService.snapshot();
    }
}
