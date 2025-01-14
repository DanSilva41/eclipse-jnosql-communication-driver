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

package org.eclipse.jnosql.communication.redis.keyvalue;


import jakarta.nosql.keyvalue.BucketManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisQueueStringTest {


    private BucketManagerFactory keyValueEntityManagerFactory;

    private Queue<String> lineBank;

    @BeforeEach
    public void init() {
        keyValueEntityManagerFactory = RedisBucketManagerFactorySupplier.INSTANCE.get();
        lineBank = keyValueEntityManagerFactory.getQueue("physical-bank-string", String.class);
    }

    @Test
    public void shouldPushInTheLine() {
        assertTrue(lineBank.add("Otavio"));
        assertTrue(lineBank.size() == 1);
        String otavio = lineBank.poll();
        assertEquals(otavio, "Otavio");
        assertNull(lineBank.poll());
        assertTrue(lineBank.isEmpty());
    }

    @Test
    public void shouldPeekInTheLine() {
        lineBank.add("Otavio");
        String otavio = lineBank.peek();
        assertNotNull(otavio);
        assertNotNull(lineBank.peek());
        String otavio2 = lineBank.remove();
        assertEquals(otavio, otavio2);
        boolean happendException = false;
        try {
            lineBank.remove();
        } catch (NoSuchElementException e) {
            happendException = true;
        }
        assertTrue(happendException);
    }

    @Test
    public void shouldElementInTheLine() {
        lineBank.add("Otavio");
        assertNotNull(lineBank.element());
        assertNotNull(lineBank.element());
        lineBank.remove("Otavio");
        boolean happendException = false;
        try {
            lineBank.element();
        } catch (NoSuchElementException e) {
            happendException = true;
        }
        assertTrue(happendException);
    }

    @SuppressWarnings("unused")
    @Test
    public void shouldIterate() {
        lineBank.add("Otavio");
        lineBank.add("Gama");
        int count = 0;
        for (String line : lineBank) {
            count++;
        }
        assertTrue(count == 2);
        lineBank.remove();
        lineBank.remove();
        count = 0;
        for (String line : lineBank) {
            count++;
        }
        assertTrue(count == 0);
    }

    @Test
    public void shouldClear() {
        lineBank.add("Otavio");
        lineBank.clear();
        assertTrue(lineBank.isEmpty());
    }

    @AfterEach
    public void dispose() {
        lineBank.clear();
    }
}
