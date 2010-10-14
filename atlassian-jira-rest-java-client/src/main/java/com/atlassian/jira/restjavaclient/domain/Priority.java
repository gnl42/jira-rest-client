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

import java.net.URI;

/**
 * TODO: Document this class / interface here
 *
 * @since v0.1
 */
public class Priority extends BasicPriority {
	private final String statusColor;
	private final String description;
	private final URI iconUrl;

	public Priority(URI self, String name, String statusColor, String description, URI iconUri) {
		super(self, name);
		this.statusColor = statusColor;
		this.description = description;
		this.iconUrl = iconUri;
	}

	public String getStatusColor() {
		return statusColor;
	}

	public String getDescription() {
		return description;
	}

	public URI getIconUri() {
		return iconUrl;
	}

	@Override
	public String toString() {
		return getToStringHelper().
				add("description", description).
				add("statusColor", statusColor).
				add("iconUrl", iconUrl).
				toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Priority) {
			Priority that = (Priority) obj;
			return super.equals(obj) && Objects.equal(this.description, that.description)
					&& Objects.equal(this.statusColor, that.statusColor)
					&& Objects.equal(this.iconUrl, that.iconUrl);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), description, statusColor, iconUrl);
	}

}
