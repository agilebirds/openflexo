package org.openflexo.ws.jira;

import org.openflexo.ws.jira.model.JIRAErrors;

public class JIRAException extends Exception {

	private JIRAErrors errors;

	public JIRAException(JIRAErrors errors) {
		super();
		this.errors = errors;
	}

	public JIRAErrors getErrors() {
		return errors;
	}
}
