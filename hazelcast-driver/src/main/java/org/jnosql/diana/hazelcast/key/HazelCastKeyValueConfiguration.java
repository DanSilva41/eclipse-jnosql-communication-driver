/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jnosql.diana.hazelcast.key;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.jnosql.diana.api.key.KeyValueConfiguration;
import org.jnosql.diana.driver.ConfigurationReader;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HazelCastKeyValueConfiguration implements KeyValueConfiguration<HazelCastKeyValueEntityManagerFactory> {

    private static final String HAZELCAST_FILE_CONFIGURATION = "diana-hazelcast.properties";


    public HazelCastKeyValueEntityManagerFactory getManagerFactory(Map<String, String> configurations) {

        List<String> servers = configurations.keySet().stream().filter(s -> s.startsWith("hazelcast-hoster-"))
                .collect(Collectors.toList());
        Config config = new Config(configurations.getOrDefault("hazelcast-instanceName", "hazelcast-instanceName"));

        HazelcastInstance hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
        return new HazelCastKeyValueEntityManagerFactory(hazelcastInstance);
    }

    public HazelCastKeyValueEntityManagerFactory getManagerFactory(Config config) {
        Objects.requireNonNull(config, "config is required");
        HazelcastInstance hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
        return new HazelCastKeyValueEntityManagerFactory(hazelcastInstance);
    }

    @Override
    public HazelCastKeyValueEntityManagerFactory getManagerFactory() {
        Map<String, String> configuration = ConfigurationReader.from(HAZELCAST_FILE_CONFIGURATION);
        return getManagerFactory(configuration);
    }
}
