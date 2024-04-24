package io.github.hubao.hbregistry.service;

import io.github.hubao.hbregistry.model.InstanceMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述: Interface for registry service.
 * @author hubao
 * @see 2024/4/24 20:49
 */
public interface RegistryService {

    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // todo
    long renew(InstanceMeta instance, String ... services);

    Long version(String service);

    Map<String, Long> versions(String... services);
}
