/*
 * Copyright (C) 2012 Atlassian
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
package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClient;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.httpclient.spi.ThreadLocalContextManagers;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.util.concurrent.Effect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

/**
 * Factory for asynchronous http clients.
 *
 * @since v2.0
 */
public class AsynchronousHttpClientFactory {

	@SuppressWarnings("unchecked")
	public DisposableHttpClient createClient(final URI serverUri, final AuthenticationHandler authenticationHandler) {
		final HttpClientOptions options = new HttpClientOptions();
		options.setRequestPreparer(new Effect<Request>() {
			@Override
			public void apply(final Request request) {
				authenticationHandler.configure(request);
			}
		});
		final DefaultHttpClient defaultHttpClient = new DefaultHttpClient(new NoOpEventPublisher(),
				new RestClientApplicationProperties(serverUri),
				ThreadLocalContextManagers.noop(), options);
		return new AtlassianHttpClientDecorator(defaultHttpClient) {

			@Override
			public void destroy() throws Exception {
				defaultHttpClient.destroy();
			}
		};
	}

	public DisposableHttpClient createClient(final HttpClient client) {
		return new AtlassianHttpClientDecorator(client) {

			@Override
			public void destroy() throws Exception {
				// This should never be implemented. This is simply creation of a wrapper
				// for AtlassianHttpClient which is extended by a destroy method.
				// Destroy method should never be called for AtlassianHttpClient coming from
				// a client! Imagine you create a RestClient, pass your own HttpClient there
				// and it gets destroy.
			}
		};
	}

	private static class NoOpEventPublisher implements EventPublisher {
		@Override
		public void publish(Object o) {
		}

		@Override
		public void register(Object o) {
		}

		@Override
		public void unregister(Object o) {
		}

		@Override
		public void unregisterAll() {
		}
	}

	/**
	 * These properties are used to present JRJC as a User-Agent during http requests.
	 */
	@SuppressWarnings("deprecation")
	private static class RestClientApplicationProperties implements ApplicationProperties {

		private final String baseUrl;

		private RestClientApplicationProperties(URI jiraURI) {
			this.baseUrl = jiraURI.getPath();
		}

		@Override
		public String getBaseUrl() {
			return baseUrl;
		}

		@Override
		public String getDisplayName() {
			return "Atlassian JIRA Rest Java Client";
		}

		@Override
		public String getVersion() {
			return MavenUtils.getVersion("com.atlassian.jira", "jira-rest-java-com.atlassian.jira.rest.client");
		}

		@Override
		public Date getBuildDate() {
			// TODO implement using MavenUtils, JRJC-123
			throw new UnsupportedOperationException();
		}

		@Override
		public String getBuildNumber() {
			// TODO implement using MavenUtils, JRJC-123
			return String.valueOf(0);
		}

		@Override
		public File getHomeDirectory() {
			return new File(".");
		}

		@Override
		public String getPropertyValue(final String s) {
			throw new UnsupportedOperationException("Not implemented");
		}
	}

	private static final class MavenUtils {
		private static final Logger logger = LoggerFactory.getLogger(MavenUtils.class);

		private static final String UNKNOWN_VERSION = "unknown";

		static String getVersion(String groupId, String artifactId) {
			final Properties props = new Properties();
			InputStream resourceAsStream = null;
			try {
				resourceAsStream = MavenUtils.class.getResourceAsStream(String
						.format("/META-INF/maven/%s/%s/pom.properties", groupId, artifactId));
				props.load(resourceAsStream);
				return props.getProperty("version", UNKNOWN_VERSION);
			} catch (Exception e) {
				logger.debug("Could not find version for maven artifact {}:{}", groupId, artifactId);
				logger.debug("Got the following exception", e);
				return UNKNOWN_VERSION;
			} finally {
				if (resourceAsStream != null) {
					try {
						resourceAsStream.close();
					} catch (IOException ioe) {
						// ignore
					}
				}
			}
		}
	}

}
