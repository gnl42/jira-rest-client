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

package com.atlassian.jira.rest.client.domain;

import com.atlassian.jira.rest.client.IdentifiableEntity;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * Basic information (served together with the issue) about current resolution.
 *
 * @since v0.1
 */
public class BasicResolution extends AddressableNamedEntity implements IdentifiableEntity<Long> {

    @Nullable
    private final Long id;

    public BasicResolution(URI self, String name, @Nullable Long id) {
		super(self, name);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}