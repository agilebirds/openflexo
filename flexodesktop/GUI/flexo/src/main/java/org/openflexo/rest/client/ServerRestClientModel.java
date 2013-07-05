package org.openflexo.rest.client;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.model.DocFormat;
import org.openflexo.rest.client.model.Job;
import org.openflexo.rest.client.model.JobType;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.User;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.WebServiceURLDialog.ServerRestClientParameter;

import com.sun.jersey.api.client.GenericType;

public class ServerRestClientModel implements HasPropertyChangeSupport {

	public static final File FIB_FILE = new FileResource("Fib/ServerClientModelView.fib");
	public static final File DOC_GENERATION_CHOOSER_FIB_FILE = new FileResource("Fib/DocGenerationChooser.fib");

	private static final String DOC_TYPE = "docType";
	private static final String DOC_FORMAT = "docFormat";

	private interface Progress {
		public void increment(String message);
	}

	public class DocGenerationChoice implements HasPropertyChangeSupport {
		private PropertyChangeSupport pcSupport;
		private String docType;
		private DocFormat docFormat;

		public void delete() {
			this.pcSupport.firePropertyChange(DELETED, false, true);
			this.pcSupport = null;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return this.pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return DELETED;
		}

		public ServerRestClientModel getServerRestClientModel() {
			return ServerRestClientModel.this;
		}

		public String getDocType() {
			return docType;
		}

		public void setDocType(String docType) {
			this.docType = docType;
			pcSupport.firePropertyChange(DOC_TYPE, null, docType);
		}

		public DocFormat getDocFormat() {
			return docFormat;
		}

		public void setDocFormat(DocFormat docFormat) {
			this.docFormat = docFormat;
			pcSupport.firePropertyChange(DOC_FORMAT, null, docFormat);
		}
	}

	private interface ServerRestClientOperation {
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException;

		public String getLocalizedTitle();

		public int getSteps();
	}

	private class UpdateUserOperation implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			setUser(client.users(client.createClient()).id(client.getUserName()).getAsUserXml());
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_user_information");
		}

	}

	private class UpdateServerProject implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			List<Project> projects = client.projects(client.createClient(),
					UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectUri", flexoProject.getProjectURI()).build()).getAsXml(0, 1,
					"creationDate desc", new GenericType<List<Project>>() {
					});
			if (projects.size() > 0) {
				setServerProject(projects.get(0));
			} else {
				FlexoController.notify(FlexoLocalization.localizedForKey("your_project_is_not_handled_by_the_server"));
				setVersions(Collections.<ProjectVersion> emptyList());
			}
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_project_information");
		}

	}

	private class UpdateVersions implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (serverProject == null) {
				return;
			}
			setVersions(client.projectsProjectIDVersions(serverProject.getProjectId()).getAsXml(page * pageSize, (page + 1) * pageSize, "",
					new GenericType<List<ProjectVersion>>() {
					}));
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_versions");
		}
	}

	private class GenerateDocumentation implements ServerRestClientOperation {

		private final Job job;

		public GenerateDocumentation(Job job) {
			super();
			this.job = job;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			// client.jobs().pu
			// TODO:
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("sending_job_request");
		}
	}

	private static final String DELETED = "deleted";

	private static final String SERVER_PROJECT = "serverProject";
	private static final String USER = "user";
	private static final String VERSIONS = "versions";

	private final FlexoController controller;

	private final FlexoProject flexoProject;

	private Project serverProject;

	private User user;

	private List<ProjectVersion> versions;

	private PropertyChangeSupport pcSupport;

	private int pageSize = 5;
	private int page = 0;

	public ServerRestClientModel(FlexoController controller, FlexoProject flexoProject) {
		super();
		this.controller = controller;
		this.flexoProject = flexoProject;
		this.pcSupport = new PropertyChangeSupport(this);
	}

	private ServerRestClient getServerRestClient(boolean forceDialog) {
		ServerRestClientParameter params = controller.getServerRestClientParameter(forceDialog);
		if (params == null) {
			return null;
		}
		ServerRestClient client;
		try {
			client = new ServerRestClient(new URI(params.getWSURL()));
			client.setUserName(params.getWSLogin());
			client.setPassword(params.getWSPassword());
			return client;
		} catch (URISyntaxException e) {
			// Should not happen
			e.printStackTrace();
			return null;
		}
	}

	public void delete() {
		pcSupport.firePropertyChange(DELETED, false, true);
	}

	private void performOperations(ServerRestClientOperation... operations) {
		boolean firstAttempt = true;
		ProgressWindow.makeProgressWindow("", operations.length);
		try {
			for (ServerRestClientOperation operation : operations) {
				ServerRestClient client = getServerRestClient(!firstAttempt);
				ProgressWindow.setProgressInstance(operation.getLocalizedTitle());
				ProgressWindow.resetSecondaryProgressInstance(operation.getSteps());
				try {
					operation.doOperation(client, new Progress() {
						@Override
						public void increment(String message) {
							ProgressWindow.setSecondaryProgressInstance(message);
						}
					});
				} catch (WebApplicationException e) {
					e.printStackTrace();
					if (!controller.handleWSException(e)) {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (!controller.handleWSException(e)) {
						return;
					}
				}
				firstAttempt = false;
			}
		} finally {
			ProgressWindow.hideProgressWindow();
		}
	}

	public void refresh() {
		performOperations(new UpdateUserOperation(), new UpdateServerProject(), new UpdateVersions());
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public FlexoProject getFlexoProject() {
		return flexoProject;
	}

	public Project getServerProject() {
		return serverProject;
	}

	public User getUser() {
		return user;
	}

	public List<ProjectVersion> getVersions() {
		return versions;
	}

	private void setServerProject(Project serverProject) {
		this.serverProject = serverProject;
		pcSupport.firePropertyChange(SERVER_PROJECT, null, serverProject);
	}

	private void setUser(User user) {
		this.user = user;
		pcSupport.firePropertyChange(USER, null, user);
	}

	private void setVersions(List<ProjectVersion> versions) {
		this.versions = versions;
		pcSupport.firePropertyChange(VERSIONS, null, versions);
	}

	public void generateDocumentation(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}
		DocGenerationChoice choice = new DocGenerationChoice();
		choice.setDocFormat(DocFormat.WORD);
		choice.setDocType(serverProject.getDocTypes().get(0));
		FIBDialog<DocGenerationChoice> dialog = FIBDialog.instanciateAndShowDialog(DOC_GENERATION_CHOOSER_FIB_FILE, choice,
				controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
			Job job = new Job();
			job.setCreator(getUser());
			job.setJobType(JobType.DOC_BUILDER);
			job.setVersion(version);
			job.setDocFormat(choice.getDocFormat());
			job.setDocType(choice.getDocType());

		}

	}
}
