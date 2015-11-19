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

import com.atlassian.httpclient.apache.httpcomponents.MultiPartEntityBuilder;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.*;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.internal.json.*;
import com.atlassian.jira.rest.client.internal.json.gen.*;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Asynchronous implementation of IssueRestClient.
 *
 * @since v2.0
 */
public class AsynchronousIssueRestClient extends AbstractAsynchronousRestClient implements IssueRestClient {

	private static final EnumSet<Expandos> DEFAULT_EXPANDS = EnumSet.of(Expandos.NAMES, Expandos.SCHEMA, Expandos.TRANSITIONS);
	private static final Function<IssueRestClient.Expandos, String> EXPANDO_TO_PARAM = new Function<Expandos, String>() {
		@Override
		public String apply(Expandos from) {
			return from.name().toLowerCase();
		}
	};
	private final SessionRestClient sessionRestClient;
	private final MetadataRestClient metadataRestClient;

	private final IssueJsonParser issueParser = new IssueJsonParser();
	private final BasicIssueJsonParser basicIssueParser = new BasicIssueJsonParser();
	private final JsonObjectParser<Watchers> watchersParser = WatchersJsonParserBuilder.createWatchersParser();
	private final TransitionJsonParser transitionJsonParser = new TransitionJsonParser();
	private final JsonObjectParser<Transition> transitionJsonParserV5 = new TransitionJsonParserV5();
	private final VotesJsonParser votesJsonParser = new VotesJsonParser();
	private final CreateIssueMetadataJsonParser createIssueMetadataJsonParser = new CreateIssueMetadataJsonParser();
	private static final String FILE_BODY_TYPE = "file";
	private final URI baseUri;
	private ServerInfo serverInfo;

	public AsynchronousIssueRestClient(final URI baseUri, final HttpClient client, final SessionRestClient sessionRestClient,
			final MetadataRestClient metadataRestClient) {
		super(client);
		this.baseUri = baseUri;
		this.sessionRestClient = sessionRestClient;
		this.metadataRestClient = metadataRestClient;
	}

	private synchronized ServerInfo getVersionInfo() {
		if (serverInfo == null) {
			serverInfo = metadataRestClient.getServerInfo().claim();
		}
		return serverInfo;
	}

	@Override
	public Promise<BasicIssue> createIssue(final IssueInput issue) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("issue");
		return postAndParse(uriBuilder.build(), issue, new IssueInputJsonGenerator(), basicIssueParser);
	}

	@Override
	public Promise<Void> updateIssue(final String issueKey, final IssueInput issue) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("issue").path(issueKey);
		return put(uriBuilder.build(), issue, new IssueInputJsonGenerator());
	}

	@Override
	public Promise<BulkOperationResult<BasicIssue>> createIssues(Collection<IssueInput> issues) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("issue/bulk");

		return postAndParse(uriBuilder.build(), issues, new IssuesInputJsonGenerator(), new BasicIssuesJsonParser());
	}

	@Override
	public Promise<Iterable<CimProject>> getCreateIssueMetadata(@Nullable GetCreateIssueMetadataOptions options) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("issue/createmeta");

		if (options != null) {
			if (options.projectIds != null) {
				uriBuilder.queryParam("projectIds", Joiner.on(",").join(options.projectIds));
			}

			if (options.projectKeys != null) {
				uriBuilder.queryParam("projectKeys", Joiner.on(",").join(options.projectKeys));
			}

			if (options.issueTypeIds != null) {
				uriBuilder.queryParam("issuetypeIds", Joiner.on(",").join(options.issueTypeIds));
			}

			final Iterable<String> issueTypeNames = options.issueTypeNames;
			if (issueTypeNames != null) {
				for (final String name : issueTypeNames) {
					uriBuilder.queryParam("issuetypeNames", name);
				}
			}

			final Iterable<String> expandos = options.expandos;
			if (expandos != null && expandos.iterator().hasNext()) {
				uriBuilder.queryParam("expand", Joiner.on(",").join(expandos));
			}
		}

		return getAndParse(uriBuilder.build(), createIssueMetadataJsonParser);
	}

	@Override
	public Promise<Issue> getIssue(final String issueKey) {
		return getIssue(issueKey, Collections.<Expandos>emptyList());
	}

	@Override
	public Promise<Issue> getIssue(final String issueKey, final Iterable<Expandos> expand) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
		final Iterable<Expandos> expands = Iterables.concat(DEFAULT_EXPANDS, expand);
		uriBuilder.path("issue").path(issueKey).queryParam("expand",
				Joiner.on(',').join(Iterables.transform(expands, EXPANDO_TO_PARAM)));
		return getAndParse(uriBuilder.build(), issueParser);
	}

	@Override
	public Promise<Void> deleteIssue(String issueKey, boolean deleteSubtasks) {
		return delete(UriBuilder.fromUri(baseUri).path("issue").path(issueKey)
				.queryParam("deleteSubtasks", deleteSubtasks).build());
	}

	@Override
	public Promise<Watchers> getWatchers(final URI watchersUri) {
		return getAndParse(watchersUri, watchersParser);
	}

	@Override
	public Promise<Votes> getVotes(final URI votesUri) {
		return getAndParse(votesUri, votesJsonParser);
	}

	@Override
	public Promise<Iterable<Transition>> getTransitions(final URI transitionsUri) {
		return callAndParse(client().newRequest(transitionsUri).get(),
				new AbstractAsynchronousRestClient.ResponseHandler<Iterable<Transition>>() {
					@Override
					public Iterable<Transition> handle(Response response) throws JSONException, IOException {
						final JSONObject jsonObject = new JSONObject(response.getEntity());
						if (jsonObject.has("transitions")) {
							return JsonParseUtil.parseJsonArray(jsonObject.getJSONArray("transitions"), transitionJsonParserV5);
						} else {
							final Collection<Transition> transitions = new ArrayList<Transition>(jsonObject.length());
							@SuppressWarnings("unchecked")
							final Iterator<String> iterator = jsonObject.keys();
							while (iterator.hasNext()) {
								final String key = iterator.next();
								try {
									final int id = Integer.parseInt(key);
									final Transition transition = transitionJsonParser.parse(jsonObject.getJSONObject(key), id);
									transitions.add(transition);
								} catch (JSONException e) {
									throw new RestClientException(e);
								} catch (NumberFormatException e) {
									throw new RestClientException(
											"Transition id should be an integer, but found [" + key + "]", e);
								}
							}
							return transitions;
						}
					}
				}
		);
	}

	@Override
	public Promise<Iterable<Transition>> getTransitions(final Issue issue) {
		if (issue.getTransitionsUri() != null) {
			return getTransitions(issue.getTransitionsUri());
		} else {
			final UriBuilder transitionsUri = UriBuilder.fromUri(issue.getSelf());
			return getTransitions(transitionsUri.path("transitions").queryParam("expand", "transitions.fields").build());
		}
	}

	@Override
	public Promise<Void> transition(final URI transitionsUri, final TransitionInput transitionInput) {
		final int buildNumber = getVersionInfo().getBuildNumber();
		try {
			JSONObject jsonObject = new JSONObject();
			if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
				jsonObject.put("transition", new JSONObject().put("id", transitionInput.getId()));
			} else {
				jsonObject.put("transition", transitionInput.getId());
			}
			if (transitionInput.getComment() != null) {
				if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
					jsonObject.put("update", new JSONObject().put("comment",
							new JSONArray().put(new JSONObject().put("add",
									new CommentJsonGenerator(getVersionInfo())
											.generate(transitionInput.getComment())))));
				} else {
					jsonObject.put("comment", new CommentJsonGenerator(getVersionInfo())
							.generate(transitionInput.getComment()));
				}
			}
			final Iterable<FieldInput> fields = transitionInput.getFields();
			final JSONObject fieldsJs = new IssueUpdateJsonGenerator().generate(fields);
			if (fieldsJs.keys().hasNext()) {
				jsonObject.put("fields", fieldsJs);
			}
			if (fieldsJs.keys().hasNext()) {
				jsonObject.put("fields", fieldsJs);
			}
			return post(transitionsUri, jsonObject);
		} catch (JSONException ex) {
			throw new RestClientException(ex);
		}
	}

	@Override
	public Promise<Void> transition(final Issue issue, final TransitionInput transitionInput) {
		if (issue.getTransitionsUri() != null) {
			return transition(issue.getTransitionsUri(), transitionInput);
		} else {
			final UriBuilder uriBuilder = UriBuilder.fromUri(issue.getSelf());
			uriBuilder.path("transitions");
			return transition(uriBuilder.build(), transitionInput);
		}
	}

	@Override
	public Promise<Void> vote(final URI votesUri) {
		return post(votesUri);
	}

	@Override
	public Promise<Void> unvote(final URI votesUri) {
		return delete(votesUri);
	}

	@Override
	public Promise<Void> watch(final URI watchersUri) {
		return post(watchersUri);
	}

	@Override
	public Promise<Void> unwatch(final URI watchersUri) {
		return removeWatcher(watchersUri, getLoggedUsername());
	}

	@Override
	public Promise<Void> addWatcher(final URI watchersUri, final String username) {
		return post(watchersUri, JSONObject.quote(username));
	}

	@Override
	public Promise<Void> removeWatcher(final URI watchersUri, final String username) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(watchersUri);
		if (getVersionInfo().getBuildNumber() >= ServerVersionConstants.BN_JIRA_4_4) {
			uriBuilder.queryParam("username", username);
		} else {
			uriBuilder.path(username).build();
		}
		return delete(uriBuilder.build());
	}

	@Override
	public Promise<Void> linkIssue(final LinkIssuesInput linkIssuesInput) {
		final URI uri = UriBuilder.fromUri(baseUri).path("issueLink").build();
		return post(uri, linkIssuesInput, new LinkIssuesInputGenerator(getVersionInfo()));
	}

	@Override
	public Promise<Void> addAttachment(final URI attachmentsUri, final InputStream inputStream, final String filename) {
		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.defaultCharset());
		entity.addPart(FILE_BODY_TYPE, new InputStreamBody(inputStream, filename));
		return postAttachments(attachmentsUri, entity);
	}

	@Override
	public Promise<Void> addAttachments(final URI attachmentsUri, final AttachmentInput... attachments) {
		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.defaultCharset());
		for (final AttachmentInput attachmentInput : attachments) {
			entity.addPart(FILE_BODY_TYPE, new InputStreamBody(attachmentInput.getInputStream(), attachmentInput.getFilename()));
		}
		return postAttachments(attachmentsUri, entity);
	}

	@Override
	public Promise<Void> addAttachments(final URI attachmentsUri, final File... files) {
		final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.defaultCharset());
		for (final File file : files) {
			entity.addPart(FILE_BODY_TYPE, new FileBody(file));
		}
		return postAttachments(attachmentsUri, entity);
	}

	@Override
	public Promise<Void> addComment(final URI commentsUri, final Comment comment) {
		return post(commentsUri, comment, new CommentJsonGenerator(getVersionInfo()));
	}

	@Override
	public Promise<InputStream> getAttachment(URI attachmentUri) {
		return callAndParse(client().newRequest(attachmentUri).get(),
				new ResponseHandler<InputStream>() {
					@Override
					public InputStream handle(final Response request) throws JSONException, IOException {
						return request.getEntityStream();
					}
				}
		);
	}

	@Override
	public Promise<Void> addWorklog(URI worklogUri, WorklogInput worklogInput) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(worklogUri)
				.queryParam("adjustEstimate", worklogInput.getAdjustEstimate().restValue);

		switch (worklogInput.getAdjustEstimate()) {
			case NEW:
				uriBuilder.queryParam("newEstimate", Strings.nullToEmpty(worklogInput.getAdjustEstimateValue()));
				break;
			case MANUAL:
				uriBuilder.queryParam("reduceBy", Strings.nullToEmpty(worklogInput.getAdjustEstimateValue()));
				break;
		}

		return post(uriBuilder.build(), worklogInput, new WorklogInputJsonGenerator());
	}

	private Promise<Void> postAttachments(final URI attachmentsUri, final MultipartEntity multipartEntity) {
		final ResponsePromise responsePromise = client()
				.newRequest(attachmentsUri)
				.setEntity(new MultiPartEntityBuilder(multipartEntity))
				.setHeader("X-Atlassian-Token", "nocheck")
				.post();
		return call(responsePromise);
	}

	private String getLoggedUsername() {
		return sessionRestClient.getCurrentSession().claim().getUsername();
	}
}
