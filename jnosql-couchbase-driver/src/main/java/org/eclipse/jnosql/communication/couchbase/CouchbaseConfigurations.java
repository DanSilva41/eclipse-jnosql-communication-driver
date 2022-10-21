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
package org.eclipse.jnosql.communication.couchbase;

import com.couchbase.client.java.Bucket;

import java.util.function.Supplier;

/**
 * This class is a {@link Supplier} of properties settings available at Couchbase client.
 */
public enum CouchbaseConfigurations implements Supplier<String> {

    /**
     * Define the host at the database. It is a {@link jakarta.nosql.Configurations#HOST} alias
     */
    HOST("couchbase.host"),

    /**
     * Define the user at the database. It is a {@link jakarta.nosql.Configurations#USER} alias
     */
    USER("couchbase.user"),

    /**
     * Define the host at the database. It is a {@link jakarta.nosql.Configurations#PASSWORD} alias
     */
    PASSWORD("couchbase.password"),
    /**
     * Define the scope to use at couchbase otherwise, it will use the default.
     */
    SCOPE("couchbase.scope"),
    /**
     * couchbase collection split by a comma.
     * At the start-up of a {@link CouchbaseConfiguration}, there is this option to check if
     * these collections exist; if not, it will create using the default settings.
     */
    COLLECTIONS("couchbase.collections"),
    /**
     * A default couchbase collection.
     * When it is not defined the default value comes from {@link Bucket#defaultCollection()}
     */
    COLLECTION("couchbase.collection"),
    /**
     * A couchbase collection index.
     * At the start-up of a {@link CouchbaseConfiguration}, it will read this property to check if the index does exist,
     * if not it will create combined by scope and the database.
     */
    INDEX("couchbase.index");

    private final String configuration;

    CouchbaseConfigurations(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String get() {
        return configuration;
    }
}
