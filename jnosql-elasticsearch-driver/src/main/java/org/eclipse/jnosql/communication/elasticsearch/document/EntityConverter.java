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
package org.eclipse.jnosql.communication.elasticsearch.document;


import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentQuery;
import org.eclipse.jnosql.communication.driver.ValueUtil;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static jakarta.nosql.SortType.ASC;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

final class EntityConverter {

    static final String ID_FIELD = "_id";

    static final String ENTITY = "@entity";

    private EntityConverter() {
    }


    static Map<String, Object> getMap(DocumentEntity entity) {
        Map<String, Object> jsonObject = new HashMap<>();

        entity.getDocuments().stream()
                .filter(d -> !d.getName().equals(ID_FIELD))
                .forEach(feedJSON(jsonObject));
        jsonObject.put(ENTITY, entity.getName());
        return jsonObject;
    }

    static Stream<DocumentEntity> query(DocumentQuery query, RestHighLevelClient client, String index) {
        QueryConverterResult select = QueryConverter.select(query);

        try {
            Stream<DocumentEntity> idQueryStream = Stream.empty();
            Stream<DocumentEntity> statementQueryStream = Stream.empty();
            if (select.hasId()) {
                idQueryStream = executeId(client, index, select);
            }
            if (select.hasStatement()) {
                statementQueryStream = executeStatement(query, client, index, select);
            }
            return Stream.concat(idQueryStream, statementQueryStream);
        } catch (IOException e) {
            throw new ElasticsearchException("An error to execute a query on elasticsearch", e);
        }
    }

    private static Stream<DocumentEntity> executeStatement(DocumentQuery query, RestHighLevelClient client, String index,
                                                           QueryConverterResult select) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);

        setQueryBuilder(query, select, searchRequest);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return Stream.of(response.getHits())
                .flatMap(h -> Stream.of(h.getHits()))
                .map(ElasticsearchEntry::of)
                .filter(ElasticsearchEntry::isNotEmpty)
                .map(ElasticsearchEntry::toEntity);
    }



    private static Consumer<Document> feedJSON(Map<String, Object> jsonObject) {
        return d -> {
            Object value = ValueUtil.convert(d.getValue());
            if (value instanceof Document) {
                Document subDocument = Document.class.cast(value);
                jsonObject.put(d.getName(), singletonMap(subDocument.getName(), subDocument.get()));
            } else if (isSudDocument(value)) {
                Map<String, Object> subDocument = getMap(value);
                jsonObject.put(d.getName(), subDocument);
            } else if (isSudDocumentList(value)) {
                jsonObject.put(d.getName(), StreamSupport.stream(Iterable.class.cast(value).spliterator(), false)
                        .map(EntityConverter::getMap).collect(toList()));
            } else {
                jsonObject.put(d.getName(), value);
            }
        };
    }

    private static Map<String, Object> getMap(Object value) {
        Map<String, Object> subDocument = new HashMap<>();
        StreamSupport.stream(Iterable.class.cast(value).spliterator(),
                false).forEach(feedJSON(subDocument));
        return subDocument;
    }

    private static boolean isSudDocument(Object value) {
        return value instanceof Iterable && StreamSupport.stream(Iterable.class.cast(value).spliterator(), false).
                allMatch(jakarta.nosql.document.Document.class::isInstance);
    }

    private static boolean isSudDocumentList(Object value) {
        return value instanceof Iterable && StreamSupport.stream(Iterable.class.cast(value).spliterator(), false).
                allMatch(d -> d instanceof Iterable && isSudDocument(d));
    }

    private static Stream<DocumentEntity> executeId(RestHighLevelClient client, String index,
                                                    QueryConverterResult select) throws IOException {

        MultiGetRequest multiGetRequest = new MultiGetRequest();

        select.getIds().stream()
                .map(id -> new MultiGetRequest.Item(index, id))
                .forEach(multiGetRequest::add);

        MultiGetResponse responses = client.mget(multiGetRequest, RequestOptions.DEFAULT);
        return Stream.of(responses.getResponses())
                .map(MultiGetItemResponse::getResponse)
                .map(ElasticsearchEntry::of)
                .filter(ElasticsearchEntry::isNotEmpty)
                .map(ElasticsearchEntry::toEntity);

    }

    private static void setQueryBuilder(DocumentQuery query, QueryConverterResult select, SearchRequest searchRequest) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (select.hasQuery()) {
            searchSourceBuilder.query(select.getStatement());
        }
        feedBuilder(query, searchSourceBuilder);

        searchRequest.source(searchSourceBuilder);
    }

    private static void feedBuilder(DocumentQuery query, SearchSourceBuilder searchSource) {
        query.getSorts().forEach(d -> {
            if (ASC.equals(d.getType())) {
                searchSource.sort(d.getName(), SortOrder.ASC);
            } else {
                searchSource.sort(d.getName(), SortOrder.DESC);
            }
        });

        int from = (int) query.getSkip();
        int size = (int) query.getLimit();
        if (from > 0) {
            searchSource.from(from);
        }
        if (size > 0) {
            searchSource.size(size);
        }
    }


}
