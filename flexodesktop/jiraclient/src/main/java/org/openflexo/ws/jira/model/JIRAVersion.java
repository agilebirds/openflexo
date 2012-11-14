package org.openflexo.ws.jira.model;

public class JIRAVersion extends JIRAObject<JIRAVersion> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892267955351510820L;
	private String name;
	private String iconUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
}
