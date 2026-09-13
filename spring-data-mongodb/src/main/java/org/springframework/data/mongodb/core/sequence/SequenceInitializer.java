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
 * Strategy interface for initializing sequence documents in MongoDB.
 * <p>
 * Implementations use MongoDB's $setOnInsert operator to create sequence documents only if they don't already exist,
 * ensuring that existing sequences are not overwritten.
 * <p>
 * This interface is for internal use only and subject to change.
 *
 * @param <T> the type of sequence value
 * @author Jeongkyun An
 * @since 4.5
 */
interface SequenceInitializer<T extends Number> {

	/**
	 * Initialize the sequence document if it doesn't exist.
	 * <p>
	 * Uses MongoDB's $setOnInsert operator with upsert to atomically create the document with the configured start
	 * value if it doesn't already exist. If the document exists, no modification is made.
	 *
	 * @return initialization result containing whether document was newly created and the current value
	 */
	InitializationResult<T> initialize();
}
