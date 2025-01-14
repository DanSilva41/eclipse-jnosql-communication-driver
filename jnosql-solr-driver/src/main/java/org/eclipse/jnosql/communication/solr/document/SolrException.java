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

import jakarta.nosql.CommunicationException;

/**
 * Root of exception on Solr/Jakarta NoSQL integration
 */
public class SolrException extends CommunicationException {

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    SolrException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param message the message
     */
    SolrException(String message) {
        super(message);
    }
}
