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
package org.eclipse.jnosql.communication.arangodb.document;


import com.arangodb.ArangoDB;
import jakarta.nosql.document.DocumentCollectionManagerFactory;

final class ArangoDBDocumentCollectionManagerFactory implements DocumentCollectionManagerFactory {


    private final ArangoDB arangoDB;

    ArangoDBDocumentCollectionManagerFactory(ArangoDB arangoDB) {
        this.arangoDB = arangoDB;
    }

    @Override
    public ArangoDBDocumentCollectionManager get(String database) {
        ArangoDBUtil.checkDatabase(database, arangoDB);
        return new DefaultArangoDBDocumentCollectionManager(database, arangoDB);
    }

    @Override
    public void close() {
        arangoDB.shutdown();
    }
}
