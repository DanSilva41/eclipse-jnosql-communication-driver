/*
 *
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
 *
 */
package org.jnosql.diana.couchdb.document;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.document.Documents;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultCouchDBDocumentCollectionManagerTest {

    public static final String COLLECTION_NAME = "person";

    private CouchDBDocumentCollectionManager entityManager;

    {
        CouchDBDocumentConfiguration configuration = new CouchDBDocumentConfiguration();
        CouchDBDocumentCollectionManagerFactory managerFactory = configuration.get();
        entityManager = managerFactory.get("people");
    }

    @Test
    public void shouldInsert() {
        DocumentEntity entity = getEntity();
        DocumentEntity documentEntity = entityManager.insert(entity);
        assertEquals(entity, documentEntity);
    }

    @Test
    public void shouldInsertNotId() {
        DocumentEntity entity = getEntity();
        entity.remove("_id");
        DocumentEntity documentEntity = entityManager.insert(entity);
        assertTrue(documentEntity.find("_id").isPresent());
    }

    @Test
    public void shouldUpdate() {
        DocumentEntity entity = getEntity();
        entity.remove("_id");
        DocumentEntity documentEntity = entityManager.insert(entity);
        Document newField = Documents.of("newField", "10");
        entity.add(newField);
        DocumentEntity updated = entityManager.update(entity);
        assertEquals(newField, updated.find("newField").get());
    }

    @Test
    public void shouldReturnErrorOnUpdate() {
        assertThrows(NullPointerException.class, () -> entityManager.update((DocumentEntity) null));
        assertThrows(CouchDBHttpClientException.class, () -> {
            DocumentEntity entity = getEntity();
            entity.remove("_id");
            entityManager.update(entity);

        });

        assertThrows(CouchDBHttpClientException.class, () -> {
            DocumentEntity entity = getEntity();
            entity.add("_id", "not_found");
            entityManager.update(entity);

        });
    }


    @Test
    public void shouldSelect() {
        DocumentEntity entity = getEntity();
        entity.remove("_id");
        entity = entityManager.insert(entity);
        Object id = entity.find("_id").map(Document::get).get();
        DocumentQuery query = select().from(COLLECTION_NAME).where("_id").eq(id).build();
        DocumentEntity documentFound = entityManager.singleResult(query).get();
        assertEquals(entity, documentFound);
    }

    private DocumentEntity getEntity() {
        DocumentEntity entity = DocumentEntity.of(COLLECTION_NAME);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Poliana");
        map.put("city", "Salvador");
        map.put("_id", "id");

        List<Document> documents = Documents.of(map);
        documents.forEach(entity::add);
        return entity;
    }
}