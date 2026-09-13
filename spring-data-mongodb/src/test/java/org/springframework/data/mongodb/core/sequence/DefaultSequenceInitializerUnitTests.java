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
 * Unit tests for {@link DefaultSequenceInitializer}.
 *
 * @author Jeongkyun An
 */
@ExtendWith(MockitoExtension.class)
class DefaultSequenceInitializerUnitTests {

	@Mock
	private MongoOperations mockMongoOperations;

	@Captor
	private ArgumentCaptor<Update> updateCaptor;

	@Captor
	private ArgumentCaptor<FindAndModifyOptions> optionsCaptor;

	@Test // GH-4823
	void shouldUseSetOnInsertForNewDocument() {

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("sequences")))
				.thenReturn(null); // null means document was just created

		DefaultSequenceInitializer<Long> initializer = new DefaultSequenceInitializer<>(mockMongoOperations, "test-seq",
				"sequences", 100L);

		InitializationResult<Long> result = initializer.initialize();

		assertThat(result.isNewlyCreated()).isTrue();
		assertThat(result.currentValue()).isEqualTo(100L);

		verify(mockMongoOperations).findAndModify(any(), updateCaptor.capture(), optionsCaptor.capture(),
				eq(SequenceDocument.class), eq("sequences"));

		// Verify $setOnInsert was used
		Update capturedUpdate = updateCaptor.getValue();
		assertThat(capturedUpdate.toString()).contains("$setOnInsert");
		assertThat(capturedUpdate.toString()).contains("value");

		// Verify options: upsert=true, returnNew=false
		FindAndModifyOptions capturedOptions = optionsCaptor.getValue();
		assertThat(capturedOptions.isUpsert()).isTrue();
		assertThat(capturedOptions.isReturnNew()).isFalse();
	}

	@Test // GH-4823
	void shouldDetectExistingDocument() {

		SequenceDocument existingDoc = new SequenceDocument();
		existingDoc.setId("test-seq");
		existingDoc.setValue(50L);

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("sequences")))
				.thenReturn(existingDoc);

		DefaultSequenceInitializer<Long> initializer = new DefaultSequenceInitializer<>(mockMongoOperations, "test-seq",
				"sequences", 1L);

		InitializationResult<Long> result = initializer.initialize();

		assertThat(result.isNewlyCreated()).isFalse();
		assertThat(result.currentValue()).isEqualTo(50L);
	}

	@Test // GH-4823
	void shouldUseCustomCollection() {

		when(mockMongoOperations.findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("my-counters")))
				.thenReturn(null);

		DefaultSequenceInitializer<Long> initializer = new DefaultSequenceInitializer<>(mockMongoOperations, "test-seq",
				"my-counters", 1L);

		initializer.initialize();

		verify(mockMongoOperations).findAndModify(any(), any(), any(), eq(SequenceDocument.class), eq("my-counters"));
	}

	@Test // GH-4823
	void shouldRejectNullMongoOperations() {

		assertThatIllegalArgumentException()
				.isThrownBy(() -> new DefaultSequenceInitializer<>(null, "test-seq", "sequences", 1L))
				.withMessageContaining("MongoOperations must not be null");
	}
}
