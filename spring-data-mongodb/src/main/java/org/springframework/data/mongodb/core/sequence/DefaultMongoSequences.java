/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.sequence;

import java.util.function.Consumer;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link MongoSequences}.
 * <p>
 * This factory operates on {@link MongoOperations} internally to create sequences without exposing MongoDB operations
 * in the public API. Each sequence instance manages its own initialization and value retrieval using MongoDB's atomic
 * operations.
 * <p>
 * <strong>Note:</strong> This class is intended for internal use by the framework and is subject to change. Users
 * should obtain instances via {@code MongoTemplate.sequenceFactory()} rather than instantiating this class directly.
 *
 * @author Jeongkyun An
 * @since 4.5
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
public class DefaultMongoSequences implements MongoSequences {

	private final MongoOperations mongoOperations;

	/**
	 * Create a new DefaultMongoSequences factory.
	 *
	 * @param mongoOperations the MongoOperations to use for database access, must not be {@literal null}
	 */
	public DefaultMongoSequences(MongoOperations mongoOperations) {

		Assert.notNull(mongoOperations, "MongoOperations must not be null");
		this.mongoOperations = mongoOperations;
	}

	@Override
	public MongoSequence<Long> numericSequence(String sequenceName) {
		return numericSequence(sequenceName, cfg -> {
		});
	}

	@Override
	public MongoSequence<Long> numericSequence(String sequenceName, Consumer<SequenceConfiguration> configurer) {

		Assert.hasText(sequenceName, "Sequence name must not be null or empty");
		Assert.notNull(configurer, "Configurer must not be null");

		// Apply configuration
		SequenceConfiguration config = new SequenceConfiguration();
		configurer.accept(config);

		// Create initializer with configured start value
		SequenceInitializer<Long> initializer = new DefaultSequenceInitializer<>(mongoOperations, sequenceName,
				config.getCollectionName(), config.getStartValue());

		// Create retriever with configured increment
		NextValueRetriever<Long> retriever = new DefaultNextValueRetriever<>(mongoOperations, sequenceName,
				config.getCollectionName(), config.getIncrementBy());

		// Assemble sequence
		return new NumericMongoSequence(sequenceName, config.getCollectionName(), initializer, retriever);
	}
}
