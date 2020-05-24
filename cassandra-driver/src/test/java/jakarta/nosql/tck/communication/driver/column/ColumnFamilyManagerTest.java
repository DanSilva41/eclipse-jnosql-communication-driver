/*
 *  Copyright (c) 2020 Otávio Santana and others
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
package jakarta.nosql.tck.communication.driver.column;

import jakarta.nosql.NonUniqueResultException;
import jakarta.nosql.ServiceLoaderProvider;
import jakarta.nosql.column.Column;
import jakarta.nosql.column.ColumnDeleteQuery;
import jakarta.nosql.column.ColumnEntity;
import jakarta.nosql.column.ColumnFamilyManager;
import jakarta.nosql.column.ColumnQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static jakarta.nosql.column.ColumnDeleteQuery.delete;
import static jakarta.nosql.column.ColumnQuery.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ColumnFamilyManagerTest {

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldInsert(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));

        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));
        ColumnDeleteQuery deleteQuery = delete().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldReturnErrorWhenInsertIsNull(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.insert((ColumnEntity) null));
    }

    @ParameterizedTest
    @ColumnSource("column_ttl.properties")
    public void shouldInsertTTL(ColumnArgument argument) throws InterruptedException {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));

        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));

        TimeUnit.SECONDS.sleep(2L);
        final ColumnQuery query = select().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        final long count = manager.select(query).count();
        assertEquals(0L, count);
    }

    @ParameterizedTest
    @ColumnSource("column_ttl.properties")
    public void shouldReturnErrorWhenInsertTTLHasNullParameter(ColumnArgument argument) throws InterruptedException {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class,
                () -> manager.insert((ColumnEntity) null, Duration.ZERO));
        assertThrows(NullPointerException.class,
                () -> manager.insert(ColumnEntity.of("entity"), null));
    }

    @ParameterizedTest
    @ColumnSource("column_iterable.properties")
    public void shouldInsertIterable(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        List<ColumnEntity> entities = argument.getQuery().stream().flatMap(manager::query)
                .collect(Collectors.toList());

        final List<Object> ids = entities.stream()
                .map(c -> c.find(argument.getIdName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Column::get)
                .collect(Collectors.toList());

        assertEquals(argument.getQuery().size(), ids.size());

        ColumnDeleteQuery deleteQuery = delete().from(entities.get(0).getName())
                .where(argument.getIdName()).in(ids).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column_iterable.properties")
    public void shouldReturnErrorWhenInsertIterableIsNull(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.insert((Iterable<ColumnEntity>) null));
    }

    @ParameterizedTest
    @ColumnSource("column_iterable_ttl.properties")
    public void shouldInsertIterableTTL(ColumnArgument argument) throws InterruptedException {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        List<ColumnEntity> entities = argument.getQuery().stream().flatMap(manager::query)
                .collect(Collectors.toList());
        Assertions.assertEquals(argument.getQuery().size(), entities.size());

        final List<Object> ids = entities.stream().map(c -> c.find(argument.getIdName()))
                .filter(Optional::isPresent)
                .map(Optional::get).map(Column::get).collect(Collectors.toList());

        TimeUnit.SECONDS.sleep(2L);
        final ColumnQuery query = select().from(entities.get(0).getName())
                .where(argument.getIdName()).in(ids).build();
        final long count = manager.select(query).count();
        assertEquals(0L, count);
    }

    @ParameterizedTest
    @ColumnSource("column_iterable_ttl.properties")
    public void shouldReturnErrorWhenInsertIterableTTL(ColumnArgument argument) throws InterruptedException {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();

        assertThrows(NullPointerException.class, () -> manager.insert((Iterable<ColumnEntity>) null,
                null));
        assertThrows(NullPointerException.class, () -> manager.insert((Iterable<ColumnEntity>) null,
                Duration.ZERO));
        assertThrows(NullPointerException.class, () -> manager
                .insert(Collections.singletonList(ColumnEntity.of("entity")),
                        null));
    }


    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldUpdate(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));
        assertNotNull(manager.update(entity));
        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));
        ColumnDeleteQuery deleteQuery = delete().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldReturnErrorWhenUpdateIsNull(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.update((ColumnEntity) null));
    }

    @ParameterizedTest
    @ColumnSource("column_iterable.properties")
    public void shouldUpdateIterable(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        List<ColumnEntity> entities = argument.getQuery().stream().flatMap(manager::query)
                .collect(Collectors.toList());

        assertNotNull(manager.update(entities));
        final List<Object> ids = entities.stream()
                .map(c -> c.find(argument.getIdName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Column::get)
                .collect(Collectors.toList());

        assertEquals(argument.getQuery().size(), ids.size());

        ColumnDeleteQuery deleteQuery = delete().from(entities.get(0).getName())
                .where(argument.getIdName()).in(ids).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column_iterable.properties")
    public void shouldReturnErrorWhenUpdateIterableIsNull(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.update((Iterable<ColumnEntity>) null));
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldDelete(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));

        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));
        ColumnDeleteQuery deleteQuery = delete().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        manager.delete(deleteQuery);

        ColumnQuery query = select().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        assertEquals(0L, manager.select(query).count());
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldReturnErrorWhenDelete(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.delete(null));
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldSelect(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));

        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));

        ColumnQuery query = select().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        assertEquals(1L, manager.select(query).count());
        ColumnDeleteQuery deleteQuery = delete().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldReturnErrorWhenSelect(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.select(null));
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldSingleResult(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        Optional<ColumnEntity> entityOptional = argument.getQuery().stream().flatMap(manager::query)
                .findFirst();
        Assertions.assertTrue(entityOptional.isPresent());
        final ColumnEntity entity = entityOptional
                .orElseThrow(() -> new ColumnDriverException("Should return an entity when the entity is saved"));

        final Column id = entity.find(argument.getIdName())
                .orElseThrow(() -> new ColumnDriverException("Should return the id in the entity"));

        ColumnQuery query = select().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        final Optional<ColumnEntity> optional = manager.singleResult(query);
        assertTrue(optional.isPresent());
        ColumnDeleteQuery deleteQuery = delete().from(entity.getName()).where(id.getName()).eq(id.get()).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column_iterable.properties")
    public void shouldAnEmptySingleResult(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        List<ColumnEntity> entities = argument.getQuery().stream().flatMap(manager::query)
                .collect(Collectors.toList());

        assertNotNull(manager.update(entities));
        final List<Object> ids = entities.stream()
                .map(c -> c.find(argument.getIdName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Column::get)
                .collect(Collectors.toList());

        ColumnQuery query = select().from(entities.get(0).getName())
                .where(argument.getIdName()).in(ids).build();

        assertThrows(NonUniqueResultException.class, () -> manager.singleResult(query));
        ColumnDeleteQuery deleteQuery = delete().from(entities.get(0).getName())
                .where(argument.getIdName()).in(ids).build();
        manager.delete(deleteQuery);
    }

    @ParameterizedTest
    @ColumnSource("column.properties")
    public void shouldReturnErrorWhenSingleResult(ColumnArgument argument) {
        assumeTrue(argument.isEmpty());
        ColumnFamilyManager manager = getManager();
        assertThrows(NullPointerException.class, () -> manager.singleResult(null));
    }


    private ColumnFamilyManager getManager() {
        final ColumnFamilyManagerSupplier supplier = ServiceLoaderProvider.get(ColumnFamilyManagerSupplier.class);
        return supplier.get();
    }

}
