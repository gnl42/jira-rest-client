/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.restjavaclient.domain;

import com.google.common.base.Objects;

/**
 * TODO: Document this class / interface here
 *
 * @since v0.1
 */
public class Field {
	private final String name;

    private final String type;

    private final Object value;

	public Field(String name, String type, Object value) {
		this.name = name;
        this.type = type;
        this.value = value;
    }

	public String getName() {
		return name;
	}

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("name", name).
				add("value", getValue()).
				toString();
	}
}
