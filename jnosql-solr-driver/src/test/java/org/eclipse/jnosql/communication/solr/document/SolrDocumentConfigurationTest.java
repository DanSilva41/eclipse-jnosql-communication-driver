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

package org.eclipse.jnosql.communication.solr.document;

import jakarta.nosql.document.DocumentCollectionManagerFactory;
import jakarta.nosql.document.DocumentConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SolrDocumentConfigurationTest {

    @Test
    public void shouldCreateDocumentCollectionManagerFactoryByFile() {
        DocumentConfiguration configuration = new SolrDocumentConfiguration();
        DocumentCollectionManagerFactory managerFactory = configuration.get();
        assertNotNull(managerFactory);
    }

    @Test
    public void shouldReturnErrorWhendSettingsIsNull() {
        DocumentConfiguration configuration = new SolrDocumentConfiguration();
        assertThrows(NullPointerException.class, () -> configuration.get(null));
    }


    @Test
    public void shouldReturnFromConfiguration() {
        DocumentConfiguration configuration = DocumentConfiguration.getConfiguration();
        Assertions.assertNotNull(configuration);
        Assertions.assertTrue(configuration instanceof DocumentConfiguration);
    }

    @Test
    public void shouldReturnFromConfigurationQuery() {
        SolrDocumentConfiguration configuration = DocumentConfiguration
                .getConfiguration(SolrDocumentConfiguration.class);
        Assertions.assertNotNull(configuration);
        Assertions.assertTrue(configuration instanceof SolrDocumentConfiguration);
    }

}
