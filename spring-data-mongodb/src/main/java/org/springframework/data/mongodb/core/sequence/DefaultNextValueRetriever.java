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
 * Default implementation of {@link NextValueRetriever}.
 * <p>
 * Uses MongoDB's atomic findAndModify operation with $inc to increment the sequence value and return the updated value
 * in a single atomic operation. This ensures thread-safety and correctness even under high concurrency.
 * <p>
 * This class is for internal use only and subject to change.
 *
 * @param <T> the type of sequence value
 * @author Jeongkyun An
 * @since 4.5
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
class DefaultNextValueRetriever<T extends Number> implements NextValueRetriever<T> {

	private final MongoOperations mongoOperations;
	private final String sequenceName;
	private final String collectionName;
	private final Number incrementBy;

	/**
	 * Create a new DefaultNextValueRetriever.
	 *
	 * @param mongoOperations the MongoOperations to use for database access, must not be {@literal null}
	 * @param sequenceName the name of the sequence, must not be {@literal null}
	 * @param collectionName the collection name for storing sequences, must not be {@literal null}
	 * @param incrementBy the amount to increment by, must not be {@literal null}
	 */
	DefaultNextValueRetriever(MongoOperations mongoOperations, String sequenceName, String collectionName,
			Number incrementBy) {

		Assert.notNull(mongoOperations, "MongoOperations must not be null");
		Assert.notNull(sequenceName, "Sequence name must not be null");
		Assert.notNull(collectionName, "Collection name must not be null");
		Assert.notNull(incrementBy, "Increment value must not be null");

		this.mongoOperations = mongoOperations;
		this.sequenceName = sequenceName;
		this.collectionName = collectionName;
		this.incrementBy = incrementBy;
	}

	@Override
	public T nextValue() {

		// Increment the value atomically
		Update update = new Update().inc("value", incrementBy);

		// returnNew(true) returns the NEW (incremented) value
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

		SequenceDocument result = mongoOperations.findAndModify(query(where("_id").is(sequenceName)), update, options,
				SequenceDocument.class, collectionName);

		if (result == null) {
			throw new IllegalStateException(String.format(
					"Sequence document '%s' not found in collection '%s'. "
							+ "This may indicate the document was deleted or initialization failed.",
					sequenceName, collectionName));
		}

		@SuppressWarnings("unchecked")
		T value = (T) result.getValue();
		return value;
	}
}
