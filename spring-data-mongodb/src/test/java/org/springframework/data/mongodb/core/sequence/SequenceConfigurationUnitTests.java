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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link SequenceConfiguration}.
 *
 * @author Jeongkyun An
 */
@ExtendWith(MockitoExtension.class)
class SequenceConfigurationUnitTests {

	@Test // GH-4823
	void shouldUseDefaultValues() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThat(config.getStartValue()).isEqualTo(1L);
		assertThat(config.getIncrementBy()).isEqualTo(1L);
		assertThat(config.getCollectionName()).isEqualTo("sequences");
	}

	@Test // GH-4823
	void shouldAllowFluentConfiguration() {

		SequenceConfiguration config = new SequenceConfiguration().startWith(100L).incrementBy(5L)
				.collection("my-sequences");

		assertThat(config.getStartValue()).isEqualTo(100L);
		assertThat(config.getIncrementBy()).isEqualTo(5L);
		assertThat(config.getCollectionName()).isEqualTo("my-sequences");
	}

	@Test // GH-4823
	void shouldRejectNullStartValue() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.startWith(null))
				.withMessageContaining("Start value must not be null");
	}

	@Test // GH-4823
	void shouldRejectNullIncrement() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.incrementBy(null))
				.withMessageContaining("Increment value must not be null");
	}

	@Test // GH-4823
	void shouldRejectNegativeIncrement() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.incrementBy(-1L))
				.withMessageContaining("Increment must be positive");
	}

	@Test // GH-4823
	void shouldRejectZeroIncrement() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.incrementBy(0L))
				.withMessageContaining("Increment must be positive");
	}

	@Test // GH-4823
	void shouldRejectEmptyCollectionName() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.collection(""))
				.withMessageContaining("Collection name must not be null or empty");
	}

	@Test // GH-4823
	void shouldRejectNullCollectionName() {

		SequenceConfiguration config = new SequenceConfiguration();

		assertThatIllegalArgumentException().isThrownBy(() -> config.collection(null))
				.withMessageContaining("Collection name must not be null or empty");
	}
}
