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
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link NumericMongoSequence}.
 *
 * @author Jeongkyun An
 */
@ExtendWith(MockitoExtension.class)
class NumericMongoSequenceUnitTests {

	@Mock
	private SequenceInitializer<Long> mockInitializer;

	@Mock
	private NextValueRetriever<Long> mockRetriever;

	@Test // GH-4823
	void shouldInitializeOnFirstCall() {

		when(mockInitializer.initialize()).thenReturn(new InitializationResult<>(true, 1L));

		NumericMongoSequence sequence = new NumericMongoSequence("test-seq", "sequences", mockInitializer,
				mockRetriever);

		Long value = sequence.nextValue();

		assertThat(value).isEqualTo(1L);
		verify(mockInitializer, times(1)).initialize();
		verify(mockRetriever, never()).nextValue();
	}

	@Test // GH-4823
	void shouldSkipRetrieverWhenDocumentIsNewlyCreated() {

		when(mockInitializer.initialize()).thenReturn(new InitializationResult<>(true, 100L));

		NumericMongoSequence sequence = new NumericMongoSequence("test-seq", "sequences", mockInitializer,
				mockRetriever);

		sequence.nextValue();

		verify(mockRetriever, never()).nextValue();
	}

	@Test // GH-4823
	void shouldUseRetrieverWhenDocumentAlreadyExists() {

		when(mockInitializer.initialize()).thenReturn(new InitializationResult<>(false, 50L));
		when(mockRetriever.nextValue()).thenReturn(51L);

		NumericMongoSequence sequence = new NumericMongoSequence("test-seq", "sequences", mockInitializer,
				mockRetriever);

		Long value = sequence.nextValue();

		assertThat(value).isEqualTo(51L);
		verify(mockInitializer, times(1)).initialize();
		verify(mockRetriever, times(1)).nextValue();
	}

	@Test // GH-4823
	void shouldOnlyInitializeOnce() {

		when(mockInitializer.initialize()).thenReturn(new InitializationResult<>(false, 1L));
		when(mockRetriever.nextValue()).thenReturn(2L, 3L, 4L);

		NumericMongoSequence sequence = new NumericMongoSequence("test-seq", "sequences", mockInitializer,
				mockRetriever);

		sequence.nextValue();
		sequence.nextValue();
		sequence.nextValue();

		verify(mockInitializer, times(1)).initialize();
		verify(mockRetriever, times(3)).nextValue();
	}

	@Test // GH-4823
	void shouldReturnSequenceName() {

		NumericMongoSequence sequence = new NumericMongoSequence("my-sequence", "sequences", mockInitializer,
				mockRetriever);

		assertThat(sequence.getName()).isEqualTo("my-sequence");
	}

	@Test // GH-4823
	void shouldRejectNullSequenceName() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> new NumericMongoSequence(null, "sequences", mockInitializer, mockRetriever))
				.withMessageContaining("Sequence name must not be null");
	}

	@Test // GH-4823
	void shouldRejectNullInitializer() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> new NumericMongoSequence("test-seq", "sequences", null, mockRetriever))
				.withMessageContaining("SequenceInitializer must not be null");
	}

	@Test // GH-4823
	void shouldRejectNullRetriever() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> new NumericMongoSequence("test-seq", "sequences", mockInitializer, null))
				.withMessageContaining("NextValueRetriever must not be null");
	}
}
