package org.openflexo.ws.jira.model;

public class JIRAPriority extends JIRAObject<JIRAPriority> {

	private String name;
	private String description;
	private Boolean overdue;
	private Boolean released;
	private Boolean archived;

	private String userReleaseDate; // Format is dd/M/yy (TBC)
	private String releaseDate; // Format is yyyy-MM-dd (TBC)

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isOverdue() {
		return overdue;
	}

	public void setOverdue(Boolean overdue) {
		this.overdue = overdue;
	}

	public Boolean isReleased() {
		return released;
	}

	public void setReleased(Boolean released) {
		this.released = released;
	}

	public Boolean isArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public String getUserReleaseDate() {
		return userReleaseDate;
	}

	public void setUserReleaseDate(String userReleaseDate) {
		this.userReleaseDate = userReleaseDate;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
}
