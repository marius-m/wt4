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

package com.atlassian.jira.rest.client.api.domain.input;

import com.atlassian.jira.rest.client.api.domain.*;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

/**
 * Builder for IssueInput class.
 *
 * @since 1.0
 */
public class IssueInputBuilder {

	private static final DateTimeFormatter JIRA_DATE_FORMATTER = ISODateTimeFormat.date();

	private final ValueTransformerManager valueTransformerManager = new ValueTransformerManager()
			.registerTransformer(new BaseValueTransformer());

	private Map<String, FieldInput> fields = Maps.newHashMap();

	/**
	 * Creates {@link IssueInputBuilder} without any fields pre-populated. Remember to fill required fields for the target
	 * issue action.
	 */
	public IssueInputBuilder() {
	}

	public IssueInputBuilder(String projectKey, Long issueTypeId) {
		setProjectKey(projectKey);
		setIssueTypeId(issueTypeId);
	}

	public IssueInputBuilder(BasicProject project, IssueType issueType) {
		setProject(project);
		setIssueType(issueType);
	}

	@SuppressWarnings("unused")
	public IssueInputBuilder(String projectKey, Long issueTypeId, String summary) {
		this(projectKey, issueTypeId);
		setSummary(summary);
	}

	@SuppressWarnings("unused")
	public IssueInputBuilder(BasicProject project, IssueType issueType, String summary) {
		this(project, issueType);
		setSummary(summary);
	}

	public IssueInputBuilder setSummary(String summary) {
		return setFieldInput(new FieldInput(IssueFieldId.SUMMARY_FIELD, summary));
	}

	public IssueInputBuilder setProjectKey(String projectKey) {
		return setFieldInput(new FieldInput(IssueFieldId.PROJECT_FIELD, ComplexIssueInputFieldValue.with("key", projectKey)));
	}

	public IssueInputBuilder setProject(BasicProject project) {
		return setProjectKey(project.getKey());
	}

	public IssueInputBuilder setIssueTypeId(Long issueTypeId) {
		return setFieldInput(new FieldInput(
				IssueFieldId.ISSUE_TYPE_FIELD,
				ComplexIssueInputFieldValue.with("id", issueTypeId.toString())
		));
	}

	public IssueInputBuilder setIssueType(IssueType issueType) {
		return setIssueTypeId(issueType.getId());
	}

	/**
	 * Puts given FieldInput into fields collection. <strong><br/>
	 * <p/>
	 * <br/>
	 * <strong>Recommended</strong> way to set field value is to use {@link IssueInputBuilder#setFieldValue(String, Object)}.
	 *
	 * @param fieldInput FieldInput to insert.
	 * @return this
	 */
	public IssueInputBuilder setFieldInput(FieldInput fieldInput) {
		fields.put(fieldInput.getId(), fieldInput);
		return this;
	}

	/**
	 * Puts new {@link FieldInput} with given id and value into fields collection.<br/>
	 * <p/>
	 * <br/>
	 * <strong>Recommended</strong> way to set field value is to use {@link IssueInputBuilder#setFieldValue(String, Object)}.
	 *
	 * @param id    Field's id
	 * @param value Complex value for field
	 * @return this
	 */
	@SuppressWarnings("unused")
	public IssueInputBuilder setFieldValue(String id, ComplexIssueInputFieldValue value) {
		return setFieldInput(new FieldInput(id, value));
	}

	/**
	 * Sets value of field. This method transforms given value to one of understandable by input generator.
	 *
	 * @param id    Field's id
	 * @param value Field's value
	 * @return this
	 * @throws CannotTransformValueException When transformer cannot transform given value
	 */
	public IssueInputBuilder setFieldValue(String id, Object value) throws CannotTransformValueException {
		return setFieldInput(new FieldInput(id, valueTransformerManager.apply(value)));
	}

	public IssueInputBuilder setDescription(String summary) {
		return setFieldInput(new FieldInput(IssueFieldId.DESCRIPTION_FIELD, summary));
	}

	public IssueInputBuilder setAssignee(BasicUser assignee) {
		return setAssigneeName(assignee.getName());
	}

	public IssueInputBuilder setAssigneeName(String assignee) {
		return setFieldInput(new FieldInput(IssueFieldId.ASSIGNEE_FIELD, ComplexIssueInputFieldValue.with("name", assignee)));
	}

	public IssueInput build() {
		return new IssueInput(fields);
	}

	@SuppressWarnings("unused")
	public IssueInputBuilder setAffectedVersions(Iterable<Version> versions) {
		return setAffectedVersionsNames(EntityHelper.toNamesList(versions));
	}

	public IssueInputBuilder setAffectedVersionsNames(Iterable<String> names) {
		return setFieldInput(new FieldInput(IssueFieldId.AFFECTS_VERSIONS_FIELD, toListOfComplexIssueInputFieldValueWithSingleKey(names, "name")));
	}

	public IssueInputBuilder setComponentsNames(Iterable<String> names) {
		return setFieldInput(new FieldInput(IssueFieldId.COMPONENTS_FIELD, toListOfComplexIssueInputFieldValueWithSingleKey(names, "name")));
	}

	public IssueInputBuilder setComponents(Iterable<BasicComponent> basicComponents) {
		return setComponentsNames(EntityHelper.toNamesList(basicComponents));
	}

	public IssueInputBuilder setComponents(BasicComponent... basicComponents) {
		return setComponents(Lists.newArrayList(basicComponents));
	}

	public IssueInputBuilder setDueDate(DateTime date) {
		return setFieldInput(new FieldInput(IssueFieldId.DUE_DATE_FIELD, JIRA_DATE_FORMATTER.print(date)));
	}

	public IssueInputBuilder setFixVersionsNames(Iterable<String> names) {
		return setFieldInput(new FieldInput(IssueFieldId.FIX_VERSIONS_FIELD, toListOfComplexIssueInputFieldValueWithSingleKey(names, "name")));
	}

	@SuppressWarnings("unused")
	public IssueInputBuilder setFixVersions(Iterable<Version> versions) {
		return setFixVersionsNames(EntityHelper.toNamesList(versions));
	}

	public IssueInputBuilder setPriority(BasicPriority priority) {
		return setPriorityId(priority.getId());
	}

	public IssueInputBuilder setPriorityId(Long id) {
		return setFieldInput(new FieldInput(IssueFieldId.PRIORITY_FIELD, ComplexIssueInputFieldValue.with("id", id.toString())));
	}

	public IssueInputBuilder setReporter(BasicUser reporter) {
		return setReporterName(reporter.getName());
	}

	public IssueInputBuilder setReporterName(String reporterName) {
		return setFieldInput(new FieldInput(IssueFieldId.REPORTER_FIELD, ComplexIssueInputFieldValue.with("name", reporterName)));
	}

	/**
	 * This method returns value transformer manager used to transform values by {@link IssueInputBuilder#setFieldValue(String, Object)}.
	 * You may use this manager if you want register new custom transformer.
	 *
	 * @return value transformer manager
	 */
	@SuppressWarnings("UnusedDeclaration")
	public ValueTransformerManager getValueTransformerManager() {
		return valueTransformerManager;
	}

	private <T> Iterable<ComplexIssueInputFieldValue> toListOfComplexIssueInputFieldValueWithSingleKey(final Iterable<T> items, final String key) {
		return Iterables.transform(items, new Function<T, ComplexIssueInputFieldValue>() {

			@Override
			public ComplexIssueInputFieldValue apply(T value) {
				return ComplexIssueInputFieldValue.with(key, value);
			}
		});
	}

}