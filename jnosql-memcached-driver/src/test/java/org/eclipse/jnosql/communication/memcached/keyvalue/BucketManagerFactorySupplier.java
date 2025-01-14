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
package org.eclipse.jnosql.communication.memcached.keyvalue;

import jakarta.nosql.Settings;
import jakarta.nosql.keyvalue.BucketManagerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.function.Supplier;

public enum BucketManagerFactorySupplier implements Supplier<BucketManagerFactory> {

    INSTANCE;

    private final GenericContainer memcached =
            new GenericContainer("memcached:latest")
                    .withExposedPorts(11211)
                    .waitingFor(Wait.defaultWaitStrategy());

    {
        memcached.start();
    }

    @Override
    public BucketManagerFactory get() {
        String host  = memcached.getContainerIpAddress() +':' + memcached.getFirstMappedPort();
        Settings settings = Settings.builder().put("memcached.host.1", host).build();
        MemcachedKeyValueConfiguration configuration = new MemcachedKeyValueConfiguration();
        return configuration.get(settings);
    }
}
