/*
 *  Copyright (c) 2018 Otávio Santana and others
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

package org.eclipse.jnosql.communication.dynamodb;

import jakarta.nosql.Settings;

final class DynamoDBBuilders {



    private DynamoDBBuilders() {
    }

    static void load(Settings settings, DynamoDBBuilder dynamoDB) {
        settings.get(DynamoDBConfigurations.ENDPOINT.get()).map(Object::toString).ifPresent(dynamoDB::endpoint);
        settings.get(DynamoDBConfigurations.REGION.get()).map(Object::toString).ifPresent(dynamoDB::region);
        settings.get(DynamoDBConfigurations.PROFILE.get()).map(Object::toString).ifPresent(dynamoDB::profile);
        settings.get(DynamoDBConfigurations.AWS_ACCESSKEY.get()).map(Object::toString).ifPresent(dynamoDB::awsAccessKey);
        settings.get(DynamoDBConfigurations.AWS_SECRET_ACCESS.get()).map(Object::toString).ifPresent(dynamoDB::awsSecretAccess);
    }

}
