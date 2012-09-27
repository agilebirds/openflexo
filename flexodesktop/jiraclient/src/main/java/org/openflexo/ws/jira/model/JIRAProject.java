package org.openflexo.ws.jira.model;

import java.util.List;

import org.openflexo.ws.jira.model.JIRAIssue.IssueType;

public class JIRAProject extends JIRAObject<JIRAProject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5973875816103952556L;

	private String name;

	private String description;

	private JIRAUser lead;

	private List<IssueType> issuetypes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IssueType> getIssuetypes() {
		return issuetypes;
	}

	public void setIssuetypes(List<IssueType> issuetypes) {
		this.issuetypes = issuetypes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JIRAUser getLead() {
		return lead;
	}

	public void setLead(JIRAUser lead) {
		this.lead = lead;
	}
}
