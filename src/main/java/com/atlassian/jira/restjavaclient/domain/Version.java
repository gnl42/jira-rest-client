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

import com.atlassian.jira.restjavaclient.AddressableEntity;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * TODO: Document this class / interface here
 *
 * @since v0.1
 */
public class Version implements AddressableEntity {
	private final URI self;
	private final String description;
	private final String name;
	private final boolean isArchived;
	private final boolean isReleased;
	@Nullable
	private final DateTime releaseDate;

	public Version(URI self, String name, String description, boolean archived, boolean released, DateTime releaseDate) {
		this.self = self;
		this.description = description;
		this.name = name;
		isArchived = archived;
		isReleased = released;
		this.releaseDate = releaseDate;
	}

	public URI getSelf() {
		return self;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public boolean isArchived() {
		return isArchived;
	}

	public boolean isReleased() {
		return isReleased;
	}

	@Nullable
	public DateTime getReleaseDate() {
		return releaseDate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("name", name).
				add("description", description).
				add("isArchived", isArchived).
				add("isReleased", isReleased).
				add("releaseDate", releaseDate).
				toString();
	}
}
