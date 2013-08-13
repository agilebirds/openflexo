package org.openflexo.rest.client;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.LocalDBAccess;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.WebServiceURLDialog.ServerRestClientParameter;
import org.openflexo.rest.client.model.DocFormat;
import org.openflexo.rest.client.model.Job;
import org.openflexo.rest.client.model.JobType;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.TocEntryDefinition;
import org.openflexo.rest.client.model.User;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.controller.FlexoController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class ServerRestClientModel implements HasPropertyChangeSupport {

	public static final File FIB_FILE = new FileResource("Fib/ServerClientModelView.fib");
	public static final File DOC_GENERATION_CHOOSER_FIB_FILE = new FileResource("Fib/DocGenerationChooser.fib");

	private static final String STATUS = "status";

	private static final String AUTOMATICALLY_OPEN_FILE = "automaticallyOpenFile";
	private static final String DOC_TYPE = "docType";
	private static final String DOC_FORMAT = "docFormat";
	private static final String FOLDER = "folder";
	private static final String TOC_ENTRY = "tocEntry";

	private interface Progress {
		public void increment(String message);
	}

	public class DocGenerationChoice implements HasPropertyChangeSupport {
		private PropertyChangeSupport pcSupport;
		private String docType;
		private DocFormat docFormat;
		private File folder;
		private boolean automaticallyOpenFile = true;
		private TocEntryDefinition tocEntry;
		private final ProjectVersion version;

		public DocGenerationChoice(ProjectVersion version) {
			this.version = version;
			pcSupport = new PropertyChangeSupport(this);
		}

		public ProjectVersion getVersion() {
			return version;
		}

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

		public File getFolder() {
			return folder;
		}

		public void setFolder(File folder) {
			this.folder = folder;
			pcSupport.firePropertyChange(FOLDER, null, folder);
		}

		public boolean isAutomaticallyOpenFile() {
			return automaticallyOpenFile;
		}

		public void setAutomaticallyOpenFile(boolean automaticallyOpenFile) {
			this.automaticallyOpenFile = automaticallyOpenFile;
			pcSupport.firePropertyChange(AUTOMATICALLY_OPEN_FILE, !automaticallyOpenFile, automaticallyOpenFile);
		}

		public TocEntryDefinition getTocEntry() {
			return tocEntry;
		}

		public void setTocEntry(TocEntryDefinition tocEntry) {
			this.tocEntry = tocEntry;
			pcSupport.firePropertyChange(TOC_ENTRY, null, tocEntry);
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
			try {
				setUser(client.users(client.createClient()).id(client.getUserName()).getAsUserXml());
			} finally {
				if (getUser() == null) {
					setStatus(FlexoLocalization.localizedForKey("could_not_identify_user"));
				}
			}
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
			try {
				List<Project> projects = client.projects(client.createClient(),
						UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectUri", flexoProject.getProjectURI()).build()).getAsXml(
						0, 1, "creationDate desc", new GenericType<List<Project>>() {
						});
				if (projects.size() > 0) {
					setServerProject(projects.get(0));
				} else {
					setServerProject(null);
					FlexoController.notify(FlexoLocalization.localizedForKey("your_project_is_not_handled_by_the_server"));
					setVersions(Collections.<ProjectVersion> emptyList());
				}
			} finally {
				if (getServerProject() == null) {
					setStatus(FlexoLocalization.localizedForKey("could_not_find_project_on_server"));
				} else {
					setStatus("");
				}
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
			validationInProgress.clear();
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("isMerged", Boolean.TRUE);
			Client restClient = client.createClient();
			setVersions(client.projectsProjectIDVersions(restClient, builder.build(), serverProject.getProjectId()).getAsXml(
					page * pageSize, (page + 1) * pageSize, "creationDate desc", new GenericType<List<ProjectVersion>>() {
					}));
			progress.increment(FlexoLocalization.localizedForKey("retrieving_status"));
			for (ProjectVersion version : getVersions()) {
				progress.increment(FlexoLocalization.localizedForKey("retrieving_status"));
				UriBuilder builder2 = UriBuilder.fromUri(client.getBASE_URI()).queryParam("jobType", JobType.DOC_REINJECTER)
						.queryParam("jobType", JobType.PROJECT_MERGER).queryParam("version", version.getVersionID());
				List<Job> jobs = client.jobs(restClient, builder2.build()).getAsXml(null, null, null, new GenericType<List<Job>>() {
				});
				validationInProgress.put(version, jobs.size() > 0);
			}
		}

		@Override
		public int getSteps() {
			return pageSize + 1;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_versions");
		}
	}

	private class SendProjectToServer implements ServerRestClientOperation {

		private final String comment;

		public SendProjectToServer(String comment) {
			this.comment = comment;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("saving"), 5);
			File zipFile = null;
			zipFile = File.createTempFile(StringUtils.rightPad(flexoProject.getProjectName(), 3, '_'), ".zip");
			zipFile.deleteOnExit();
			try {
				flexoProject.saveAsZipFile(zipFile, ProgressWindow.instance(), true, true);
			} catch (final SaveResourceException e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						FlexoController.notify(FlexoLocalization.localizedForKey("error_during_saving") + " " + e.getMessage());
					}
				});
				return;
			}
			ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("sending_project"));
			final long length = zipFile.length();
			final int numberOfStates = 1000; // We will display tenth of percent, hence 1000 states
			ProgressWindow.resetSecondaryProgressInstance(numberOfStates);
			FileInputStream inputStream = new FileInputStream(zipFile) {

				long progress = 0;
				long lastUpdate = 0;
				int stepSize = (int) (length / numberOfStates);

				@Override
				public int read() throws IOException {
					progress++;
					updateProgress();
					return super.read();
				}

				private void updateProgress() {
					if (lastUpdate == 0 || progress - lastUpdate > stepSize || progress == length) {
						double percent = progress * 100.0d / length;
						ProgressWindow.instance().setProgress(String.format("%1$.2f%1$%"/*+" (%2$d/%3$d)"*/, percent, progress, length));
						lastUpdate = progress;
					}
				}
			};
			ProjectVersion version = new ProjectVersion();
			version.setProject(serverProject.getProjectId());
			version.setCreator(user.getLogin());
			version.setComment(comment);
			FormDataMultiPart mp = new FormDataMultiPart();
			mp.field("version", version, MediaType.APPLICATION_XML_TYPE);
			mp.bodyPart(new StreamDataBodyPart("file", inputStream, zipFile.getName()));
			zipFile.delete();
			ProjectVersion response = client.projectsProjectIDVersions(serverProject.getProjectId()).postMultipartFormDataAsXml(mp,
					ProjectVersion.class);
			performOperations(new UpdateVersions());
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("sending_project");
		}

		@Override
		public int getSteps() {
			return 0;
		}
	}

	private class GenerateDocumentation implements ServerRestClientOperation {

		private final DocGenerationChoice choice;
		private final ProjectVersion version;

		public GenerateDocumentation(ProjectVersion version, DocGenerationChoice choice) {
			super();
			this.version = version;
			this.choice = choice;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			Job job = new Job();
			job.setCreator(getUser());
			job.setJobType(JobType.DOC_BUILDER);
			job.setVersion(version);
			job.setDocFormat(choice.getDocFormat());
			job.setDocType(choice.getDocType());
			job.setTocEntry(choice.getTocEntry());
			final Job returned = client.jobs().postXml(job, Job.class);
			if (returned != null && choice.getFolder() != null) {
				WatchedRemoteDocJob watchedRemoteJob = new WatchedRemoteDocJob();
				watchedRemoteJob.setRemoteJobId(returned.getJobId());
				watchedRemoteJob.setSaveToFolder(choice.getFolder().getAbsolutePath());
				watchedRemoteJob.setOpenDocument(choice.isAutomaticallyOpenFile());
				LocalDBAccess.getInstance().getEntityManager().persist(watchedRemoteJob);
			}
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

	private class GeneratePrototype implements ServerRestClientOperation {

		private final ProjectVersion version;

		public GeneratePrototype(ProjectVersion version) {
			super();
			this.version = version;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			Job job = new Job();
			job.setCreator(getUser());
			job.setJobType(JobType.PROTOTYPE_BUILDER);
			job.setVersion(version);
			final Job returned = client.jobs().postXml(job, Job.class);
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

	private class ValidationInProgress implements ServerRestClientOperation {

		private ProjectVersion version;

		public ValidationInProgress(ProjectVersion version) {
			super();
			this.version = version;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("version.versionID", version.getVersionID())
					.queryParam("jobType", JobType.PROJECT_MERGER).queryParam("jobType", JobType.DOC_REINJECTER);
			List<Job> jobs = client.jobs(client.createClient(), builder.build()).getAsXml(new GenericType<List<Job>>() {
			});
			validationInProgress.put(version, jobs.size() > 0);

		}

		@Override
		public String getLocalizedTitle() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getSteps() {
			return -1;
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

	private String status;

	private Map<ProjectVersion, Boolean> validationInProgress;

	private PropertyChangeSupport pcSupport;

	private int pageSize = 5;
	private int page = 0;

	public ServerRestClientModel(FlexoController controller, FlexoProject flexoProject) {
		super();
		this.controller = controller;
		this.flexoProject = flexoProject;
		this.validationInProgress = new Hashtable<ProjectVersion, Boolean>();
		this.pcSupport = new PropertyChangeSupport(this);
		controller.getApplicationContext().getServerRestService().init();
		refresh();
	}

	private ServerRestClient getServerRestClient(boolean forceDialog) {
		ServerRestClientParameter params = controller.getApplicationContext().getServerRestService()
				.getServerRestClientParameter(forceDialog);
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

	private void performOperations(final ServerRestClientOperation... operations) {
		performOperations(true, operations);
	}

	private void performOperations(final boolean useProgressWindow, final ServerRestClientOperation... operations) {
		if (useProgressWindow) {
			ProgressWindow.makeProgressWindow("", operations.length);
		}
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				boolean firstAttempt = true;
				try {
					for (ServerRestClientOperation operation : operations) {
						boolean done = false;
						while (!done) {
							ServerRestClient client = getServerRestClient(!firstAttempt);
							if (useProgressWindow) {
								ProgressWindow.setProgressInstance(operation.getLocalizedTitle());
								int steps = operation.getSteps();
								ProgressWindow.resetSecondaryProgressInstance(steps);
							}
							try {
								operation.doOperation(client, new Progress() {
									@Override
									public void increment(String message) {
										if (useProgressWindow) {
											ProgressWindow.setSecondaryProgressInstance(message);
										}
									}
								});
								done = true;
							} catch (WebApplicationException e) {
								e.printStackTrace();
								if (!controller.handleWSException(e)) {
									return null;
								}
								firstAttempt = false;
							} catch (IOException e) {
								e.printStackTrace();
								if (!controller.handleWSException(e)) {
									return null;
								}
								firstAttempt = false;
							} catch (RuntimeException e) {
								e.printStackTrace();
								if (!controller.handleWSException(e)) {
									return null;
								}
								firstAttempt = false;
							}
						}
					}
				} finally {
					if (useProgressWindow) {
						ProgressWindow.hideProgressWindow();
					}
				}

				return null;
			}

		};
		worker.execute();
	}

	public void refresh() {
		performOperations(new UpdateUserOperation(), new UpdateServerProject(), new UpdateVersions());
	}

	public void refreshVersions() {
		performOperations(new UpdateVersions());
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
		pcSupport.firePropertyChange(STATUS, null, status);
	}

	public ImageIcon getConsistencyIcon(ProjectVersion version) {
		if (isValidationInProgress(version)) {
			return IconLibrary.IN_PROGRESS_ICON;
		}
		if (version.isIsProtoValidationSuccessful()) {
			return IconLibrary.VALID_ICON;
		} else {
			return IconLibrary.INVALID_ICON;
		}
	}

	public boolean isValidationInProgress(ProjectVersion version) {
		Boolean b = validationInProgress.get(version);
		if (b == null) {
			performOperations(false, new ValidationInProgress(version));
		}
		return b != null && b;
	}

	public void sendProjectToServer() {
		if (serverProject != null) {
			String comment = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
			if (comment == null) {
				return;
			}
			performOperations(new SendProjectToServer(comment));
		}
	}

	public void generateDocumentation(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}
		DocGenerationChoice choice = new DocGenerationChoice(version);
		choice.setDocFormat(DocFormat.WORD);
		choice.setDocType(serverProject.getDocTypes().get(0));
		FIBDialog<DocGenerationChoice> dialog = FIBDialog.instanciateAndShowDialog(DOC_GENERATION_CHOOSER_FIB_FILE, choice,
				controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
			performOperations(new GenerateDocumentation(version, choice));
		}

	}

	public void generatePrototype(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}
		Job job = new Job();
		job.setCreator(getUser());
		job.setJobType(JobType.PROTOTYPE_BUILDER);
		job.setVersion(version);
		performOperations(new GeneratePrototype(version));

	}
}
