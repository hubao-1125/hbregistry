package io.github.hubao.hbregistry.cluster;

import io.github.hubao.hbregistry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/4/26 22:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Snapshot {

    LinkedMultiValueMap<String, InstanceMeta> REGISTRY;
    Map<String, Long> VERSIONS;
    Map<String, Long> TIMESTAMPS;
    long version;
}
