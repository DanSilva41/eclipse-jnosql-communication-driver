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
package org.eclipse.jnosql.communication.cassandra.column;

import java.util.function.Supplier;

/**
 * An enumeration to show the available options to connect to the Cassandra database.
 * It implements {@link Supplier}, where its it returns the property name that might be
 * overwritten by the system environment using Eclipse Microprofile or Jakarta Config API.
 *
 * @see jakarta.nosql.Settings
 */
public enum CassandraConfigurations implements Supplier<String> {

    /**
     * The user's credential.
     */
    USER("cassandra.user"),

    /**
     * The password's credential
     */
    PASSWORD("cassandra.password"),
    /**
     * Database's host. It is a prefix to enumerate hosts. E.g.: cassandra.host.1=localhost
     */
    HOST("cassandra.host"),
    /**
     * The name of the application using the created session.
     */
    NAME("cassandra.name"),
    /**
     * The cassandra's port
     */
    PORT("cassandra.port"),
    /**
     * The Cassandra CQL to execute when the configuration starts. It uses as a prefix. E.g.: cassandra.query.1=CQL
     */
    QUERY("cassandra.query"),
    /**
     * The datacenter that is considered "local" by the load balancing policy.
     */
    DATA_CENTER("cassandra.data.center");

    private final String configuration;

    CassandraConfigurations(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String get() {
        return configuration;
    }
}
