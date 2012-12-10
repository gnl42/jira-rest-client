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

package com.atlassian.jira.rest.client.internal.jersey;

import com.atlassian.jira.rest.client.domain.util.ErrorCollection;
import com.atlassian.jira.rest.client.internal.json.ResourceUtil;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class AbstractJerseyRestClientTest {

	private static final int BAD_REQUEST = 400;

	@Test
	public void testExtractErrors() throws JSONException {
		final String str = ResourceUtil.getStringFromResource("/json/error/valid.json");
		final Collection<ErrorCollection> errors = AbstractJerseyRestClient.extractErrors(BAD_REQUEST, str);
		final ErrorCollection errorCollection = Iterators.getOnlyElement(errors.iterator());
		Assert.assertThat(errorCollection.getErrors().values(), containsInAnyOrder("abcfsd"));
	}

	@Test
	public void testExtractErrors2() throws JSONException {
		final String str = ResourceUtil.getStringFromResource("/json/error/valid2.json");
		final Collection<ErrorCollection> errors = AbstractJerseyRestClient.extractErrors(BAD_REQUEST, str);
		final ErrorCollection errorCollection = Iterators.getOnlyElement(errors.iterator());
		Assert.assertThat(errorCollection.getErrorMessages(), containsInAnyOrder("a", "b", "xxx"));
	}

	@Test
	public void testExtractErrors3() throws JSONException {
		final String str = ResourceUtil.getStringFromResource("/json/error/valid3.json");
		final Collection<ErrorCollection> errors = AbstractJerseyRestClient.extractErrors(BAD_REQUEST, str);
		final ErrorCollection errorCollection = Iterators.getOnlyElement(errors.iterator());
		Assert.assertThat(errorCollection.getErrors().values(), containsInAnyOrder("aa", "bb"));
	}

	@Test
	public void testExtractErrors4() throws JSONException {
		final String str = ResourceUtil.getStringFromResource("/json/error/valid4.json");
		final Collection<ErrorCollection> errors = AbstractJerseyRestClient.extractErrors(BAD_REQUEST, str);
		final ErrorCollection errorCollection = Iterators.getOnlyElement(errors.iterator());
        final List<String> lista = Lists.newArrayList(errorCollection.getErrorMessages());
		lista.addAll(errorCollection.getErrors().values());

		Assert.assertThat(lista, containsInAnyOrder("a", "b", "y", "z"));
	}

}
