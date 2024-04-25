package io.github.hubao.hbregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HbRegistryConfigProperties.class)
public class HbregistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbregistryApplication.class, args);
    }

}
