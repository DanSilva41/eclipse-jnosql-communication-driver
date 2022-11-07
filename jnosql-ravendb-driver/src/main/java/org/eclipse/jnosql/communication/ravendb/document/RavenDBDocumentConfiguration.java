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

package org.eclipse.jnosql.communication.ravendb.document;

import jakarta.nosql.Configurations;
import jakarta.nosql.Settings;
import jakarta.nosql.Settings.SettingsBuilder;
import jakarta.nosql.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.driver.ConfigurationReader;

import java.util.Arrays;
import java.util.Map;

import static java.util.Objects.requireNonNull;


/**
 * The RavenDB implementation to both {@link DocumentConfiguration}
 * that returns  {@link RavenDBDocumentCollectionManagerFactory}
 *
 */
public class RavenDBDocumentConfiguration implements DocumentConfiguration {

    private static final String FILE_CONFIGURATION = "diana-ravendb.properties";

    @Override
    public RavenDBDocumentCollectionManagerFactory get() {
        Map<String, String> configuration = ConfigurationReader.from(FILE_CONFIGURATION);
        return get(configuration);
    }

    @Override
    public RavenDBDocumentCollectionManagerFactory get(Settings settings) {
        requireNonNull(settings, "configurations is required");

        String[] servers = settings.prefix(Arrays.asList("ravendb.host", Configurations.HOST.get()))
                .stream().map(Object::toString)
                .toArray(String[]::new);
        return new RavenDBDocumentCollectionManagerFactory(servers);
    }

    private RavenDBDocumentCollectionManagerFactory get(Map<String, String> configurations) throws NullPointerException {
        requireNonNull(configurations, "configurations is required");

        SettingsBuilder builder = Settings.builder();
        configurations.entrySet().stream().forEach(e -> builder.put(e.getKey(), e.getValue()));
        return get(builder.build());
    }
}
