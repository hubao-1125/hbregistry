package io.github.hubao.hbregistry;

import io.github.hubao.hbregistry.cluster.Cluster;
import io.github.hubao.hbregistry.health.HbHealthChecker;
import io.github.hubao.hbregistry.health.HealthChecker;
import io.github.hubao.hbregistry.service.HbRegistryService;
import io.github.hubao.hbregistry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Desc: configuration for all beans.
 *
 * @author hubao
 * @see 2024/4/24$ 21:50$
 */
@Configuration
public class HbRegistryConfig {


    @Bean
    public RegistryService registryService() {
        return new HbRegistryService();
    }

//    @Bean(initMethod = "start", destroyMethod = "")
//    public HealthChecker hbHealthChecker(@Autowired RegistryService registryService) {
//        return new HbHealthChecker(registryService);
//    }

    @Bean(initMethod = "init")
    public Cluster cluster(@Autowired HbRegistryConfigProperties hbRegistryConfigProperties) {
        return new Cluster(hbRegistryConfigProperties);
    }
}
