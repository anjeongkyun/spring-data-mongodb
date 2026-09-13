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

import org.springframework.util.Assert;

/**
 * Implementation of {@link MongoSequence} for numeric (Long) sequences.
 * <p>
 * This implementation uses double-checked locking to ensure thread-safe, one-time initialization of the sequence. Once
 * initialized, subsequent calls to {@link #nextValue()} proceed without synchronization overhead.
 * <p>
 * The sequence is backed by MongoDB's atomic findAndModify operations, ensuring that values are unique even across
 * multiple application instances.
 * <p>
 * This class is for internal use only and subject to change.
 *
 * @author Jeongkyun An
 * @since 4.5
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
class NumericMongoSequence implements MongoSequence<Long> {

	private final String sequenceName;
	private final String collectionName;
	private final SequenceInitializer<Long> sequenceInitializer;
	private final NextValueRetriever<Long> valueRetriever;

	// Thread-safety: volatile for double-checked locking visibility
	private volatile boolean initialized = false;
	private final Object initLock = new Object();

	/**
	 * Create a new NumericMongoSequence.
	 *
	 * @param sequenceName the name of the sequence, must not be {@literal null}
	 * @param collectionName the collection name for storing sequences, must not be {@literal null}
	 * @param sequenceInitializer the initializer to use, must not be {@literal null}
	 * @param valueRetriever the value retriever to use, must not be {@literal null}
	 */
	NumericMongoSequence(String sequenceName, String collectionName, SequenceInitializer<Long> sequenceInitializer,
			NextValueRetriever<Long> valueRetriever) {

		Assert.notNull(sequenceName, "Sequence name must not be null");
		Assert.notNull(collectionName, "Collection name must not be null");
		Assert.notNull(sequenceInitializer, "SequenceInitializer must not be null");
		Assert.notNull(valueRetriever, "NextValueRetriever must not be null");

		this.sequenceName = sequenceName;
		this.collectionName = collectionName;
		this.sequenceInitializer = sequenceInitializer;
		this.valueRetriever = valueRetriever;
	}

	@Override
	public Long nextValue() {

		// Fast path: if already initialized, skip synchronization
		if (!initialized) {
			synchronized (initLock) {
				// Double-checked locking: re-check after acquiring lock
				if (!initialized) {
					InitializationResult<Long> result = sequenceInitializer.initialize();

					// If document was just created, return the start value
					// Otherwise, the document already existed, so proceed to normal increment
					if (result.isNewlyCreated()) {
						initialized = true;
						return result.currentValue();
					}

					initialized = true;
				}
			}
		}

		// Normal path: increment and return
		return valueRetriever.nextValue();
	}

	@Override
	public String getName() {
		return sequenceName;
	}
}
