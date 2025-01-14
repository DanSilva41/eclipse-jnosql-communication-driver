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
package org.eclipse.jnosql.communication.cassandra.column;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnQuery;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

enum QueryExecutorType implements QueryExecutor {
    PAGING_STATE {
        @Override
        public Stream<ColumnEntity> execute(String keyspace, ColumnQuery query, DefaultCassandraColumnFamilyManager manager) {
            return execute(keyspace, query, null, manager);
        }

        @Override
        public Stream<ColumnEntity> execute(String keyspace, ColumnQuery q, ConsistencyLevel level,
                                            DefaultCassandraColumnFamilyManager manager) {

            CassandraQuery query = CassandraQuery.class.cast(q);

            if (query.isExhausted()) {
                return Stream.empty();
            }
            Select select = QueryUtils.select(query, keyspace);
            SimpleStatement simpleStatement = select.build();
            if (Objects.nonNull(level)) {
                simpleStatement = simpleStatement.setConsistencyLevel(level);
            }

            if (query.toPaginate().isPresent()) {
                simpleStatement = simpleStatement.setPagingState(query.toPaginate().get());
            }

            ResultSet resultSet = manager.getSession().execute(simpleStatement);

            final ByteBuffer pagingState = resultSet.getExecutionInfo().getPagingState();
            query.setPagingState(pagingState);

            List<ColumnEntity> entities = new ArrayList<>();

            for (Row row : resultSet) {
                entities.add(CassandraConverter.toDocumentEntity(row));
                if (resultSet.getAvailableWithoutFetching() == 0) {
                    query.setExhausted(resultSet.isFullyFetched());
                    break;
                }
            }
            return entities.stream();
        }

    },
    DEFAULT {
        @Override
        public Stream<ColumnEntity> execute(String keyspace, ColumnQuery query, DefaultCassandraColumnFamilyManager manager) {
            return execute(keyspace, query, null, manager);
        }

        @Override
        public Stream<ColumnEntity> execute(String keyspace, ColumnQuery query, ConsistencyLevel level,
                                            DefaultCassandraColumnFamilyManager manager) {

            Select cassandraSelect = QueryUtils.select(query, keyspace);

            if (query.getLimit() > 0 && query.getSkip() == 0) {
                cassandraSelect = cassandraSelect.limit((int) query.getLimit());
            }

            SimpleStatement select = cassandraSelect.build();
            if (Objects.nonNull(level)) {
                select = select.setConsistencyLevel(level);
            }
            ResultSet resultSet = manager.getSession().execute(select);
            if (query.getLimit() > 0 && query.getSkip() > 0) {
                return resultSet.all().stream().skip(query.getSkip()).limit(query.getLimit()).map(CassandraConverter::toDocumentEntity);
            }
            return resultSet.all().stream().map(CassandraConverter::toDocumentEntity);
        }
    }
}
