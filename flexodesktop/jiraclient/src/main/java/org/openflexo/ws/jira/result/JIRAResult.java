package org.openflexo.ws.jira.result;

import java.util.List;

import org.openflexo.ws.jira.model.JIRAObject;

public class JIRAResult extends JIRAObject<JIRAResult> {
	private List<String> errorMessages;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
}
