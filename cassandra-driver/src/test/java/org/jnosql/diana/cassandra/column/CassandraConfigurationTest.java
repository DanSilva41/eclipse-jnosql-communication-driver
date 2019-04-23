/*
 *  Copyright (c) 2017 Otávio Santana and others
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


import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.column.ColumnFamilyManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CassandraConfigurationTest {



    @Test
    public void shoudlCreateDocumentEntityManagerFactoryFromSettings() {
        Settings settings = ManagerFactorySupplier.INSTANCE.getSettings();
        CassandraConfiguration cassandraConfiguration = new CassandraConfiguration();
        ColumnFamilyManagerFactory entityManagerFactory = cassandraConfiguration.get(settings);
        assertNotNull(entityManagerFactory);
    }


    @Test
    public void shoudlCreateDocumentEntityManagerFactoryFromFile() {
        Settings settings = ManagerFactorySupplier.INSTANCE.getSettings();
        CassandraConfiguration cassandraConfiguration = new CassandraConfiguration();
        ColumnFamilyManagerFactory entityManagerFactory = cassandraConfiguration.get(settings);
        assertNotNull(entityManagerFactory);
    }


}
