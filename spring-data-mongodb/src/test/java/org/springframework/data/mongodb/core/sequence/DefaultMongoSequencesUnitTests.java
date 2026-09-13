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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Unit tests for {@link DefaultMongoSequences}.
 *
 * @author Jeongkyun An
 */
@ExtendWith(MockitoExtension.class)
class DefaultMongoSequencesUnitTests {

	@Mock
	private MongoOperations mockMongoOperations;

	@Test // GH-4823
	void shouldCreateSequenceWithDefaults() {

		DefaultMongoSequences factory = new DefaultMongoSequences(mockMongoOperations);
		MongoSequence<Long> sequence = factory.numericSequence("test-seq");

		assertThat(sequence).isNotNull();
		assertThat(sequence.getName()).isEqualTo("test-seq");
	}

	@Test // GH-4823
	void shouldApplyCustomConfiguration() {

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("my-sequences")))
				.thenReturn(null);

		DefaultMongoSequences factory = new DefaultMongoSequences(mockMongoOperations);
		MongoSequence<Long> sequence = factory.numericSequence("test-seq",
				cfg -> cfg.startWith(100L).incrementBy(5L).collection("my-sequences"));

		assertThat(sequence).isNotNull();

		// Trigger initialization to verify configuration was applied
		sequence.nextValue();

		verify(mockMongoOperations).findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("my-sequences"));
	}

	@Test // GH-4823
	void shouldAcceptEmptyConfigurer() {

		DefaultMongoSequences factory = new DefaultMongoSequences(mockMongoOperations);
		MongoSequence<Long> sequence = factory.numericSequence("test-seq", cfg -> {
		});

		assertThat(sequence).isNotNull();
	}

	@Test // GH-4823
	void shouldRejectNullMongoOperations() {

		assertThatIllegalArgumentException().isThrownBy(() -> new DefaultMongoSequences(null))
				.withMessageContaining("MongoOperations must not be null");
	}

	@Test // GH-4823
	void shouldRejectEmptySequenceName() {

		DefaultMongoSequences factory = new DefaultMongoSequences(mockMongoOperations);

		assertThatIllegalArgumentException().isThrownBy(() -> factory.numericSequence(""))
				.withMessageContaining("Sequence name must not be null or empty");
	}

	@Test // GH-4823
	void shouldRejectNullConfigurer() {

		DefaultMongoSequences factory = new DefaultMongoSequences(mockMongoOperations);

		assertThatIllegalArgumentException().isThrownBy(() -> factory.numericSequence("test-seq", null))
				.withMessageContaining("Configurer must not be null");
	}
}
