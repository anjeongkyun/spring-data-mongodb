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
 * Result holder for sequence initialization operations.
 * <p>
 * Indicates whether a sequence document was newly created during initialization, and provides the current value of the
 * sequence.
 * <p>
 * This class is for internal use only and subject to change.
 *
 * @param <T> the type of sequence value
 * @author Jeongkyun An
 * @since 4.5
 */
class InitializationResult<T extends Number> {

	private final boolean newlyCreated;
	private final T currentValue;

	/**
	 * Create a new InitializationResult.
	 *
	 * @param newlyCreated whether the sequence document was just created
	 * @param currentValue the current value of the sequence
	 */
	InitializationResult(boolean newlyCreated, T currentValue) {

		this.newlyCreated = newlyCreated;
		this.currentValue = currentValue;
	}

	/**
	 * Check if the sequence document was newly created.
	 *
	 * @return {@literal true} if the document was just created, {@literal false} if it already existed
	 */
	boolean isNewlyCreated() {
		return newlyCreated;
	}

	/**
	 * Get the current value of the sequence.
	 *
	 * @return the current value
	 */
	T currentValue() {
		return currentValue;
	}
}
