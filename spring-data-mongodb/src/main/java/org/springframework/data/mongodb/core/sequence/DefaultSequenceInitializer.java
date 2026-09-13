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

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link SequenceInitializer}.
 * <p>
 * Uses MongoDB's $setOnInsert operator to atomically create sequence documents only if they don't exist. This ensures
 * that existing sequences are never overwritten.
 * <p>
 * This class is for internal use only and subject to change.
 *
 * @param <T> the type of sequence value
 * @author Jeongkyun An
 * @since 4.5
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
class DefaultSequenceInitializer<T extends Number> implements SequenceInitializer<T> {

	private final MongoOperations mongoOperations;
	private final String sequenceName;
	private final String collectionName;
	private final T startValue;

	/**
	 * Create a new DefaultSequenceInitializer.
	 *
	 * @param mongoOperations the MongoOperations to use for database access, must not be {@literal null}
	 * @param sequenceName the name of the sequence, must not be {@literal null}
	 * @param collectionName the collection name for storing sequences, must not be {@literal null}
	 * @param startValue the starting value for new sequences, must not be {@literal null}
	 */
	DefaultSequenceInitializer(MongoOperations mongoOperations, String sequenceName, String collectionName,
			T startValue) {

		Assert.notNull(mongoOperations, "MongoOperations must not be null");
		Assert.notNull(sequenceName, "Sequence name must not be null");
		Assert.notNull(collectionName, "Collection name must not be null");
		Assert.notNull(startValue, "Start value must not be null");

		this.mongoOperations = mongoOperations;
		this.sequenceName = sequenceName;
		this.collectionName = collectionName;
		this.startValue = startValue;
	}

	@Override
	public InitializationResult<T> initialize() {

		// Use $setOnInsert to only set value if document doesn't exist
		Update update = new Update().setOnInsert("value", startValue);

		// returnNew(false) returns the OLD document (null if just created)
		FindAndModifyOptions options = FindAndModifyOptions.options().upsert(true).returnNew(false);

		SequenceDocument result = mongoOperations.findAndModify(query(where("_id").is(sequenceName)), update, options,
				SequenceDocument.class, collectionName);

		// If result is null, document was just created with our start value
		boolean newlyCreated = (result == null);
		@SuppressWarnings("unchecked")
		T currentValue = newlyCreated ? startValue : (T) result.getValue();

		return new InitializationResult<>(newlyCreated, currentValue);
	}
}
