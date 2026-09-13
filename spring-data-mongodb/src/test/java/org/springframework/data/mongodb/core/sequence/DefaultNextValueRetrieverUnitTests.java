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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Unit tests for {@link DefaultNextValueRetriever}.
 *
 * @author Jeongkyun An
 */
@ExtendWith(MockitoExtension.class)
class DefaultNextValueRetrieverUnitTests {

	@Mock
	private MongoOperations mockMongoOperations;

	@Captor
	private ArgumentCaptor<Update> updateCaptor;

	@Captor
	private ArgumentCaptor<FindAndModifyOptions> optionsCaptor;

	@Test // GH-4823
	void shouldIncrementAndReturnNewValue() {

		SequenceDocument resultDoc = new SequenceDocument();
		resultDoc.setId("test-seq");
		resultDoc.setValue(42L);

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("sequences")))
				.thenReturn(resultDoc);

		DefaultNextValueRetriever<Long> retriever = new DefaultNextValueRetriever<>(mockMongoOperations, "test-seq",
				"sequences", 1L);

		Long value = retriever.nextValue();

		assertThat(value).isEqualTo(42L);

		verify(mockMongoOperations).findAndModify(any(), updateCaptor.capture(), optionsCaptor.capture(),
				eq(SequenceDocument.class), eq("sequences"));

		// Verify $inc was used
		Update capturedUpdate = updateCaptor.getValue();
		assertThat(capturedUpdate.toString()).contains("$inc");

		// Verify options: returnNew=true (get incremented value)
		FindAndModifyOptions capturedOptions = optionsCaptor.getValue();
		assertThat(capturedOptions.isReturnNew()).isTrue();
	}

	@Test // GH-4823
	void shouldUseCustomIncrement() {

		SequenceDocument resultDoc = new SequenceDocument();
		resultDoc.setValue(105L); // 100 + 5

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("sequences")))
				.thenReturn(resultDoc);

		DefaultNextValueRetriever<Long> retriever = new DefaultNextValueRetriever<>(mockMongoOperations, "test-seq",
				"sequences", 5L);

		Long value = retriever.nextValue();

		assertThat(value).isEqualTo(105L);
	}

	@Test // GH-4823
	void shouldThrowExceptionWhenDocumentNotFound() {

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("sequences")))
				.thenReturn(null); // Document doesn't exist

		DefaultNextValueRetriever<Long> retriever = new DefaultNextValueRetriever<>(mockMongoOperations, "test-seq",
				"sequences", 1L);

		assertThatIllegalStateException().isThrownBy(() -> retriever.nextValue())
				.withMessageContaining("Sequence document 'test-seq' not found").withMessageContaining("sequences");
	}

	@Test // GH-4823
	void shouldRejectNullMongoOperations() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DefaultNextValueRetriever<>(null, "test-seq", "sequences", 1L))
				.withMessageContaining("MongoOperations must not be null");
	}
}
