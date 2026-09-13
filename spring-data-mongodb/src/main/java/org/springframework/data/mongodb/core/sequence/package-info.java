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

/**
 * MongoDB-backed sequence support for generating unique, monotonically increasing numeric identifiers.
 * <p>
 * This package provides a factory-based API for creating sequences that use MongoDB's atomic findAndModify operations
 * to ensure thread-safe, distributed counter behavior. Sequences are persisted in MongoDB and survive application
 * restarts.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create factory from MongoTemplate
 * MongoSequences factory = mongoTemplate.sequenceFactory();
 *
 * // Simple sequence with defaults (starts at 1, increments by 1)
 * MongoSequence<Long> orderIds = factory.numericSequence("order-id");
 * long nextOrderId = orderIds.nextValue();  // 1, 2, 3...
 *
 * // Customized sequence
 * MongoSequence<Long> invoiceIds = factory.numericSequence("invoice-id",
 *     cfg -> cfg.startWith(1000L)
 *               .incrementBy(10L)
 *               .collection("counters"));
 * long nextInvoiceId = invoiceIds.nextValue();  // 1000, 1010, 1020...
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * Sequences are thread-safe both within a single application instance and across multiple instances. MongoDB's atomic
 * findAndModify operation ensures that each call to {@link MongoSequence#nextValue()} returns a unique value even under
 * high concurrency.
 * <p>
 * Initialization is protected by double-checked locking to ensure thread-safe, one-time initialization per sequence
 * instance.
 *
 * <h2>MongoDB Document Structure</h2>
 * <p>
 * Sequences are stored as documents in MongoDB with the following structure:
 *
 * <pre>
 * {
 *   "_id": "sequence-name",
 *   "value": NumberLong(123)
 * }
 * </pre>
 * <p>
 * By default, sequences are stored in a collection named "sequences", but this can be customized via
 * {@link SequenceConfiguration#collection(String)}.
 *
 * @author Jeongkyun An
 * @since 4.5
 * @see MongoSequences
 * @see MongoSequence
 * @see SequenceConfiguration
 */
package org.springframework.data.mongodb.core.sequence;
