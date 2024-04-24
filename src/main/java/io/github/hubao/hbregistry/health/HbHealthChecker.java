package io.github.hubao.hbregistry.health;

import io.github.hubao.hbregistry.model.InstanceMeta;
import io.github.hubao.hbregistry.service.HbRegistryService;
import io.github.hubao.hbregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Desc: Default implementation of HealthChecker.
 *
 * @author hubao
 * @see 2024/4/24 22:50
 */
@Slf4j
public class HbHealthChecker implements HealthChecker {

    RegistryService registryService;

    public HbHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 20_000;

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(
                () -> {
                    log.info(" ====> Health checker running...");
                    long now = System.currentTimeMillis();
                    HbRegistryService.TIMESTAMPS.keySet().forEach(serviceAndInst -> {
                        long timestamp = HbRegistryService.TIMESTAMPS.get(serviceAndInst);
                        if (now - timestamp > timeout) {
                            log.info(" ====> Health checker: {} is down", serviceAndInst);
                            int index = serviceAndInst.indexOf("@");
                            String service = serviceAndInst.substring(0, index);
                            String url = serviceAndInst.substring(index + 1);
                            InstanceMeta instance = InstanceMeta.from(url);
                            registryService.unregister(service, instance);
                            HbRegistryService.TIMESTAMPS.remove(serviceAndInst);
                        }
                    });
        },
        10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
