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

import org.springframework.data.annotation.Id;

/**
 * Internal document structure for sequence storage.
 * <p>
 * Document format: {@code {_id: "sequenceName", value: 123}}
 * <p>
 * This class is for internal use only and subject to change.
 *
 * @author Jeongkyun An
 * @since 4.5
 */
class SequenceDocument {

	@Id
	private String id;

	private Number value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}
}
