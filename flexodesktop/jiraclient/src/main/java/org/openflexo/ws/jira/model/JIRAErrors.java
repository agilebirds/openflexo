package org.openflexo.ws.jira.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openflexo.ws.jira.JIRAGson;

public class JIRAErrors {

	private List<String> errorMessages;

	private Map<String, String> errors;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public static void main(String[] args) {
		JIRAErrors errors = JIRAGson.getInstance().fromJson(
				" {\"errorMessage1s\":\"\",\"errors\":{\"summary\":\"Summary must be less than 255 characters.\"}}", JIRAErrors.class);
		StringBuilder sb = new StringBuilder();
		if (errors.getErrorMessages() != null) {
			for (String s : errors.getErrorMessages()) {
				if (sb.length() > 0) {
					sb.append('\n');
				}
				sb.append(s);
			}
		}
		if (errors.getErrors() != null) {
			for (Entry<String, String> e : errors.getErrors().entrySet()) {
				if (sb.length() > 0) {
					sb.append('\n');
				}
				sb.append(e.getKey() + " : " + e.getValue());
			}
		}
		System.err.println(sb.toString());
	}
}
