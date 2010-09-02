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
import com.atlassian.jira.restjavaclient.ExpandableProperty;
import com.google.common.base.Objects;

import java.net.URI;
import java.util.Collection;

/**
 * TODO: Document this class / interface here
 *
 * @since v0.1
 */
public class Watchers implements AddressableEntity {
	private final URI self;
	private final boolean isWatching;
	private final Collection<User> list;

	public Watchers(URI self, boolean watching, Collection<User> list) {
		this.self = self;
		isWatching = watching;
		this.list = list;
	}

	@Override
	public URI getSelf() {
		return self;
	}

	public boolean isWatching() {
		return isWatching;
	}

	public Iterable<User> getList() {
		return list;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).
				add("self", self).
				add("isWatching", isWatching).
				add("list", list).
				toString();
	}
}
