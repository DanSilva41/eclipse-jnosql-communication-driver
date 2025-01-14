/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.eclipse.jnosql.communication.redis.keyvalue;


import jakarta.nosql.Settings;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum RedisBucketManagerFactorySupplier implements Supplier<RedisBucketManagerFactory> {
    INSTANCE;

    private final GenericContainer redis =
            new GenericContainer("redis:latest")
                    .withExposedPorts(6379)
                    .waitingFor(Wait.defaultWaitStrategy());

    {
        redis.start();
    }

    @Override
    public RedisBucketManagerFactory get() {
        RedisConfiguration configuration = new RedisConfiguration();
        Map<String, Object> settings = new HashMap<>();
        settings.put("redis-master-host", redis.getContainerIpAddress());
        settings.put("redis-master-port", redis.getFirstMappedPort());
        return configuration.get(Settings.of(settings));
    }
}