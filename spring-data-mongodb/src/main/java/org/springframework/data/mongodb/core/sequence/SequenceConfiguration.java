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
 * Configuration for a MongoDB sequence.
 * <p>
 * Use with {@link MongoSequences#numericSequence(String, java.util.function.Consumer)} to customize sequence behavior.
 * <p>
 * Example:
 *
 * <pre>{@code
 * MongoSequence<Long> seq = factory.numericSequence("my-sequence",
 *     cfg -> cfg.startWith(1000L)
 *               .incrementBy(5L)
 *               .collection("counters"));
 * }</pre>
 *
 * @author Jeongkyun An
 * @since 4.5
 * @see MongoSequences
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
public class SequenceConfiguration {

	private Long startValue = 1L;
	private Long incrementBy = 1L;
	private String collectionName = "sequences";

	/**
	 * Set the starting value for the sequence. Default is 1.
	 * <p>
	 * This value is only used when the sequence is first created. If the sequence already exists in MongoDB, this
	 * setting has no effect.
	 *
	 * @param startValue the start value, must not be {@literal null}
	 * @return this configuration instance for method chaining
	 */
	public SequenceConfiguration startWith(Long startValue) {

		Assert.notNull(startValue, "Start value must not be null");
		this.startValue = startValue;
		return this;
	}

	/**
	 * Set the increment amount for the sequence. Default is 1.
	 * <p>
	 * Each call to {@link MongoSequence#nextValue()} will increment the sequence by this amount.
	 *
	 * @param incrementBy the increment amount, must not be {@literal null} and must be positive
	 * @return this configuration instance for method chaining
	 */
	public SequenceConfiguration incrementBy(Long incrementBy) {

		Assert.notNull(incrementBy, "Increment value must not be null");
		Assert.isTrue(incrementBy > 0, "Increment must be positive, but was: " + incrementBy);
		this.incrementBy = incrementBy;
		return this;
	}

	/**
	 * Set the collection name for storing sequence documents. Default is "sequences".
	 * <p>
	 * All sequences using the same collection name will be stored in the same MongoDB collection, identified by their
	 * sequence name as the document _id.
	 *
	 * @param collectionName the collection name, must not be {@literal null} or empty
	 * @return this configuration instance for method chaining
	 */
	public SequenceConfiguration collection(String collectionName) {

		Assert.hasText(collectionName, "Collection name must not be null or empty");
		this.collectionName = collectionName;
		return this;
	}

	/**
	 * Get the configured start value.
	 *
	 * @return the start value, never {@literal null}
	 */
	Long getStartValue() {
		return startValue;
	}

	/**
	 * Get the configured increment amount.
	 *
	 * @return the increment amount, never {@literal null}
	 */
	Long getIncrementBy() {
		return incrementBy;
	}

	/**
	 * Get the configured collection name.
	 *
	 * @return the collection name, never {@literal null}
	 */
	String getCollectionName() {
		return collectionName;
	}
}
