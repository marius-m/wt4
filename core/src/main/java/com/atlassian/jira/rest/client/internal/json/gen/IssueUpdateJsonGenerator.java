package com.atlassian.jira.rest.client.internal.json.gen;

import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class IssueUpdateJsonGenerator implements JsonGenerator<Iterable<FieldInput>> {
	private final ComplexIssueInputFieldValueJsonGenerator generator = new ComplexIssueInputFieldValueJsonGenerator();

	@Override
	public JSONObject generate(Iterable<FieldInput> fieldInputs) throws JSONException {
		final JSONObject fields = new JSONObject();
		if (fieldInputs != null) {
			for (final FieldInput field : fieldInputs) {
				final Object fieldValue = (field.getValue() == null) ? JSONObject.NULL
						: generator.generateFieldValueForJson(field.getValue());

				fields.put(field.getId(), fieldValue);
			}
		}
		return fields;
	}
}
