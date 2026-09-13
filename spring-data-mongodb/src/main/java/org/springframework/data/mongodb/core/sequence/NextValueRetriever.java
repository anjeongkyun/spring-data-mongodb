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

/**
 * Strategy interface for retrieving the next value from a sequence.
 * <p>
 * Implementations use MongoDB's atomic findAndModify operation with $inc to increment the sequence value and return the
 * updated value in a single atomic operation.
 * <p>
 * This interface is for internal use only and subject to change.
 *
 * @param <T> the type of sequence value
 * @author Jeongkyun An
 * @since 4.5
 */
interface NextValueRetriever<T extends Number> {

	/**
	 * Retrieve the next sequence value atomically.
	 * <p>
	 * Uses MongoDB's findAndModify with $inc to atomically increment the sequence value and return the updated value.
	 * This ensures that each value is returned exactly once, even under high concurrency.
	 *
	 * @return the next sequence value
	 * @throws IllegalStateException if the sequence document is not found
	 */
	T nextValue();
}
