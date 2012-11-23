package org.openflexo.ws.jira.result;

import java.util.List;

import org.openflexo.ws.jira.model.JIRAProject;

public class CreateMetaResult extends JIRAResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4034968502390107552L;
	private List<JIRAProject> projects;

	public List<JIRAProject> getProjects() {
		return projects;
	}

	public void setProjects(List<JIRAProject> projects) {
		this.projects = projects;
	}

}
