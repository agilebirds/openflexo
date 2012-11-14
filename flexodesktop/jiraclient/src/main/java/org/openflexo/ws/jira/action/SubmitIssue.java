package org.openflexo.ws.jira.action;

import org.openflexo.ws.jira.model.JIRAIssue;
import org.openflexo.ws.jira.result.JIRAResult;

public class SubmitIssue extends JIRAAction<JIRAResult> {
	private JIRAIssue fields;

	public SubmitIssue() {
	}

	public SubmitIssue(JIRAIssue fields) {
		super();
		this.fields = fields;
	}

	public JIRAIssue getFields() {
		return fields;
	}

	public void setFields(JIRAIssue fields) {
		this.fields = fields;
	}

	@Override
	public Class<JIRAResult> getResultClass() {
		return JIRAResult.class;
	}
}
