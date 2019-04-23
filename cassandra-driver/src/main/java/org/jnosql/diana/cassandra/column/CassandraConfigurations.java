/*
 *  Copyright (c) 2019 Otávio Santana and others
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
package org.jnosql.diana.cassandra.column;

import java.util.function.Supplier;

public enum CassandraConfigurations implements Supplier<String> {

    HOST("cassandra.host"),
    NAME("cassandra.name"),
    PORT("cassandra.port"),
    QUERY("cassandra.query"),
    SSL("cassandra.ssl"),
    METRICS("cassandra.metrics"),
    JMX("cassandra.jmx");

    private final String configuration;

    CassandraConfigurations(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String get() {
        return configuration;
    }
}
