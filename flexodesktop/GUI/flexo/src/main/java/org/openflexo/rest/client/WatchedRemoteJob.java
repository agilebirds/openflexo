package org.openflexo.rest.client;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.openflexo.DBObject;

@Entity(name = WatchedRemoteJob.ENTITY_NAME)
@NamedQueries({ @NamedQuery(name = WatchedRemoteJob.FindByProjectURI.NAME, query = WatchedRemoteJob.FindByProjectURI.QUERY),
		@NamedQuery(name = WatchedRemoteJob.FindByProjectURIAndLogin.NAME, query = WatchedRemoteJob.FindByProjectURIAndLogin.QUERY) })
public class WatchedRemoteJob extends DBObject<Integer> {

	public static final String ENTITY_NAME = "WatchedRemoteJob";

	public static final class FindByProjectURI {
		public static final String NAME = "findByProjectURI";
		public static final String PROJECT_URI_PARAM = "projectURI";
		public static final String QUERY = "select j from " + ENTITY_NAME + " j where j.projectURI=:" + PROJECT_URI_PARAM;
	}

	public static final class FindByProjectURIAndLogin {
		public static final String NAME = "findByProjectURIAndLogin";
		public static final String PROJECT_URI_PARAM = "projectURI";
		public static final String LOGIN_PARAM = "login";
		public static final String QUERY = "select j from " + ENTITY_NAME + " j where j.projectURI=:" + PROJECT_URI_PARAM
				+ " and j.login=:" + LOGIN_PARAM;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private Integer remoteJobId;

	private String projectURI;

	private int failedAttempt = 0;

	private String login;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRemoteJobId() {
		return remoteJobId;
	}

	public void setRemoteJobId(Integer remoteJobId) {
		this.remoteJobId = remoteJobId;
	}

	public String getProjectURI() {
		return projectURI;
	}

	public void setProjectURI(String projectURI) {
		this.projectURI = projectURI;
	}

	public int getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(int failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
