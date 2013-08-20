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

import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.atlassian.jira.rest.client.ComponentRestClient;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.MetadataRestClient;
import com.atlassian.jira.rest.client.ProjectRestClient;
import com.atlassian.jira.rest.client.ProjectRolesRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.SessionRestClient;
import com.atlassian.jira.rest.client.UserRestClient;
import com.atlassian.jira.rest.client.VersionRestClient;
import com.sun.jersey.api.client.AsyncViewResource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ViewResource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Jersey-based implementation of JIRA REST client.
 *
 * @since v0.1
 */
public class JerseyJiraRestClient implements JiraRestClient {

    private final URI baseUri;
    private final IssueRestClient issueRestClient;
    private final SessionRestClient sessionRestClient;
    private final UserRestClient userRestClient;
    private final ProjectRestClient projectRestClient;
    private final ComponentRestClient componentRestClient;
    private final MetadataRestClient metadataRestClient;
    private final SearchRestClient searchRestClient;
    private final VersionRestClient versionRestClient;
    private final ProjectRolesRestClient projectRolesRestClient;
    private final ApacheHttpClient client;

    public JerseyJiraRestClient(final URI serverUri, final AuthenticationHandler authenticationHandler, boolean followRedirects) {
        this.baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, followRedirects);
        authenticationHandler.configure(config);

        client = createDefaultClient(config, authenticationHandler);

        metadataRestClient = new JerseyMetadataRestClient(baseUri, client, followRedirects);
        sessionRestClient = new JerseySessionRestClient(client, serverUri, followRedirects);
        issueRestClient = new JerseyIssueRestClient(baseUri, client, sessionRestClient, metadataRestClient, followRedirects);
        userRestClient = new JerseyUserRestClient(baseUri, client, followRedirects);
        projectRestClient = new JerseyProjectRestClient(baseUri, client, followRedirects);
        componentRestClient = new JerseyComponentRestClient(baseUri, client, followRedirects);
        searchRestClient = new JerseySearchRestClient(baseUri, client, followRedirects);
        versionRestClient = new JerseyVersionRestClient(baseUri, client, followRedirects);
        projectRolesRestClient = new JerseyProjectRolesRestClient(baseUri, client, serverUri, followRedirects);
    }

    public JerseyJiraRestClient(final URI serverUri, final ApacheHttpClient client, boolean followRedirects) {
        this.baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
        this.client = client;

        metadataRestClient = new JerseyMetadataRestClient(baseUri, client, followRedirects);
        sessionRestClient = new JerseySessionRestClient(client, serverUri, followRedirects);
        issueRestClient = new JerseyIssueRestClient(baseUri, client, sessionRestClient, metadataRestClient, followRedirects);
        userRestClient = new JerseyUserRestClient(baseUri, client, followRedirects);
        projectRestClient = new JerseyProjectRestClient(baseUri, client, followRedirects);
        componentRestClient = new JerseyComponentRestClient(baseUri, client, followRedirects);
        searchRestClient = new JerseySearchRestClient(baseUri, client, followRedirects);
        versionRestClient = new JerseyVersionRestClient(baseUri, client, followRedirects);
        projectRolesRestClient = new JerseyProjectRolesRestClient(baseUri, client, serverUri, followRedirects);
    }

    @Override
    public IssueRestClient getIssueClient() {
        return issueRestClient;
    }

    @Override
    public SessionRestClient getSessionClient() {
        return sessionRestClient;
    }

    @Override
    public UserRestClient getUserClient() {
        return userRestClient;
    }

    @Override
    public ProjectRestClient getProjectClient() {
        return projectRestClient;
    }

    @Override
    public ComponentRestClient getComponentClient() {
        return componentRestClient;
    }

    @Override
    public MetadataRestClient getMetadataClient() {
        return metadataRestClient;
    }

    @Override
    public SearchRestClient getSearchClient() {
        return searchRestClient;
    }

    @Override
    public VersionRestClient getVersionRestClient() {
        return versionRestClient;
    }

    @Override
    public ProjectRolesRestClient getProjectRolesRestClient() {
        return projectRolesRestClient;
    }

    @Override
    public ApacheHttpClient getTransportClient() {
        return client;
    }

    public static ApacheHttpClientHandler createDefaultClientHander(DefaultApacheHttpClientConfig config) {
        final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        return new ApacheHttpClientHandler(client, config);
    }

    public static ApacheHttpClient createDefaultClient(DefaultApacheHttpClientConfig config, final AuthenticationHandler authenticationHandler) {
        return new ApacheHttpClient(createDefaultClientHander(config)) {
            @Override
            public WebResource resource(URI u) {
                final WebResource resource = super.resource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public AsyncWebResource asyncResource(URI u) {
                final AsyncWebResource resource = super.asyncResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public ViewResource viewResource(URI u) {
                final ViewResource resource = super.viewResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }

            @Override
            public AsyncViewResource asyncViewResource(URI u) {
                final AsyncViewResource resource = super.asyncViewResource(u);
                authenticationHandler.configure(resource, this);
                return resource;
            }
        };
    }
}

