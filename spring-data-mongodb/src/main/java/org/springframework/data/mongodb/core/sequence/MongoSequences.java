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

/**
 * Factory for creating MongoDB-backed sequences.
 * <p>
 * Sequences provide monotonically increasing numeric identifiers backed by MongoDB's atomic operations. They are
 * thread-safe and work correctly across multiple application instances.
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * // Get factory from MongoTemplate
 * MongoSequences factory = mongoTemplate.sequenceFactory();
 *
 * // Simple sequence with default configuration
 * MongoSequence<Long> orderIds = factory.numericSequence("order-id");
 * long nextOrderId = orderIds.nextValue();  // 1, 2, 3...
 *
 * // Customized sequence
 * MongoSequence<Long> invoiceIds = factory.numericSequence("invoice-id",
 *     cfg -> cfg.startWith(1000L).incrementBy(10L).collection("counters"));
 * long nextInvoiceId = invoiceIds.nextValue();  // 1000, 1010, 1020...
 * }</pre>
 *
 * @author Jeongkyun An
 * @since 4.5
 * @see MongoSequence
 * @see SequenceConfiguration
 * @see <a href="https://github.com/spring-projects/spring-data-mongodb/issues/4823">GH-4823</a>
 */
public interface MongoSequences {

	/**
	 * Create a numeric sequence with default configuration.
	 * <p>
	 * Default configuration:
	 * <ul>
	 * <li>Start value: 1
	 * <li>Increment: 1
	 * <li>Collection: "sequences"
	 * </ul>
	 *
	 * @param sequenceName the name of the sequence, must not be {@literal null} or empty
	 * @return a new MongoSequence instance, never {@literal null}
	 */
	MongoSequence<Long> numericSequence(String sequenceName);

	/**
	 * Create a numeric sequence with custom configuration.
	 * <p>
	 * Example:
	 *
	 * <pre>{@code
	 * MongoSequence<Long> seq = factory.numericSequence("my-sequence",
	 *     cfg -> cfg.startWith(1000L).incrementBy(5L).collection("counters"));
	 * }</pre>
	 *
	 * @param sequenceName the name of the sequence, must not be {@literal null} or empty
	 * @param configurer a consumer to customize the sequence configuration, must not be {@literal null}
	 * @return a new MongoSequence instance, never {@literal null}
	 */
	MongoSequence<Long> numericSequence(String sequenceName, Consumer<SequenceConfiguration> configurer);
}
