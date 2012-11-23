package org.openflexo.ws.jira.action;

import org.openflexo.ws.jira.result.JIRAResult;

public abstract class JIRAAction<R extends JIRAResult> {

	public abstract Class<R> getResultClass();
}
