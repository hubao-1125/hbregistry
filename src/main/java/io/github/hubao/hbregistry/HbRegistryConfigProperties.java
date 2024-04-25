package io.github.hubao.hbregistry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/*
 * Desc: registry config properties.
 *
 * @author hubao
 * @see 2024/4/25 21:09
 */
@Data
@ConfigurationProperties(prefix = "hbregistry")
public class HbRegistryConfigProperties {

    private List<String> serverList;
}
