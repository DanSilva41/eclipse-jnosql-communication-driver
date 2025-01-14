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
package org.eclipse.jnosql.communication.orientdb.document;


import jakarta.nosql.Configurations;
import jakarta.nosql.Settings;
import jakarta.nosql.Settings.SettingsBuilder;
import jakarta.nosql.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.driver.ConfigurationReader;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * The orientDB implementation of {@link DocumentConfiguration}  that returns
 * {@link OrientDBDocumentCollectionManagerFactory}.
 *
 * @see OrientDBDocumentConfigurations
 */
public class OrientDBDocumentConfiguration implements DocumentConfiguration {

    private static final String FILE_CONFIGURATION = "diana-orientdb.properties";

    private String host;

    private String user;

    private String password;

    private String storageType;

    public OrientDBDocumentConfiguration() {
        Map<String, String> properties = ConfigurationReader.from(FILE_CONFIGURATION);
        SettingsBuilder builder = Settings.builder();
        properties.forEach((key, value) -> builder.put(key, value));
        Settings settings = builder.build();

        this.host = getHost(settings);
        this.user = getUser(settings);
        this.password = getPassword(settings);
        this.storageType = getStorageType(settings);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public OrientDBDocumentCollectionManagerFactory get() {
        return new OrientDBDocumentCollectionManagerFactory(host, user, password, storageType);
    }

    @Override
    public OrientDBDocumentCollectionManagerFactory get(Settings settings) throws NullPointerException {
        return getOrientDBDocumentCollectionManagerFactory(settings);
    }

    private OrientDBDocumentCollectionManagerFactory getOrientDBDocumentCollectionManagerFactory(Settings settings) {
        requireNonNull(settings, "settings is required");

        String host = getHost(settings);
        String user = getUser(settings);
        String password = getPassword(settings);
        String storageType = getStorageType(settings);
        return new OrientDBDocumentCollectionManagerFactory(host, user, password, storageType);
    }

    private String getHost(Settings settings) {
        return find(settings, OrientDBDocumentConfigurations.HOST,
                Configurations.HOST);
    }

    private String getUser(Settings settings) {
        return find(settings, OrientDBDocumentConfigurations.USER,
                Configurations.USER);
    }

    private String getPassword(Settings settings) {
        return find(settings, OrientDBDocumentConfigurations.PASSWORD,
                Configurations.PASSWORD);
    }

    private String getStorageType(Settings settings) {
        return find(settings, OrientDBDocumentConfigurations.STORAGE_TYPE);
    }

    private String find(Settings settings, Supplier<String>... keys) {
        return settings.get(Stream.of(keys)
                        .map(Supplier::get).collect(toList()))
                .map(Object::toString)
                .orElse(null);
    }
}
