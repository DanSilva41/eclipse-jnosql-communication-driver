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

package org.eclipse.jnosql.communication.mongodb.document;

import java.util.function.Supplier;

/**
 * An enumeration to show the available options to connect to the Mongodb database.
 * It implements {@link Supplier}, where its it returns the property name that might be
 * overwritten by the system environment using Eclipse Microprofile or Jakarta Config API.
 *
 * @see jakarta.nosql.Settings
 */
public enum MongoDBDocumentConfigurations implements Supplier<String> {
    /**
     * The database host as prefix. E.g.: mongodb.host.1=localhost:27017
     */
    HOST("mongodb.host"),
    /**
     * The user's credential.
     */
    USER("mongodb.user"),
    /**
     * The password's credential
     */
    PASSWORD("mongodb.password"),
    /**
     * MongoDB's connection string
     */
    URL("mongodb.url"),
    /**
     * The source where the user is defined.
     */
    AUTHENTICATION_SOURCE("mongodb.authentication.source"),
    /**
     * Authentication mechanisms {@link com.mongodb.AuthenticationMechanism}
     */
    AUTHENTICATION_MECHANISM("mongodb.authentication.mechanism");

    private final String configuration;

    MongoDBDocumentConfigurations(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public String get() {
        return configuration;
    }
}
