package org.openflexo.rest.client;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.openflexo.LocalDBAccess;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.model.Account;
import org.openflexo.rest.client.model.DocFormat;
import org.openflexo.rest.client.model.Job;
import org.openflexo.rest.client.model.JobType;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.Session;
import org.openflexo.rest.client.model.TocEntryDefinition;
import org.openflexo.rest.client.model.User;
import org.openflexo.rest.client.model.UserType;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.Holder;
import org.openflexo.view.controller.FlexoController;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class ServerRestClientModel extends AbstractServerRestClientModel implements HasPropertyChangeSupport {

	public static final File FIB_FILE = new FileResource("Fib/ServerClientModelView.fib");
	public static final File DOC_GENERATION_CHOOSER_FIB_FILE = new FileResource("Fib/DocGenerationChooser.fib");
	public static final File NEW_SERVER_PROJECT_FIB_FILE = new FileResource("Fib/NewServerProject.fib");

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yy hh:mm");

	private static final String AUTOMATICALLY_OPEN_FILE = "automaticallyOpenFile";
	private static final String DOC_TYPE = "docType";
	private static final String DOC_FORMAT = "docFormat";
	private static final String FOLDER = "folder";
	private static final String TOC_ENTRY = "tocEntry";

	public class DocGenerationChoice implements HasPropertyChangeSupport {
		private final ProjectVersion version;
		private PropertyChangeSupport pcSupport;
		private String docType;
		private DocFormat docFormat;
		private File folder;
		private boolean automaticallyOpenFile = true;
		private TocEntryDefinition tocEntry;

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

	public class NewProjectParameter {
		private static final String COMMENT = "comment";
		private static final String PROJECT = "project";
		private Project project;
		private PropertyChangeSupport pcSupport;
		private String comment;

		public NewProjectParameter() {
			project = new Project();
			project.setAvailableProtoToken(5);
			project.setAvailableDocToken(5);
			pcSupport = new PropertyChangeSupport(this);
			if (getUser().getClientAccount() != null) {
				Account account = new Account();
				account.setClientAccountId(getUser().getClientAccount());
				project.setClientAccount(account);
			}
		}

		public List<Account> getAvailableAccounts() {
			if (getUser().getUserType() == UserType.ADMIN) {
				try {
					return accountCache.get("");
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Collections.emptyList();
			} else {
				return Collections.emptyList();
			}
		}

		public List<User> getAvailableUsers(final Account account) {
			try {
				List<User> accountUsers = accountUserCache.get(account);
				List<User> adminUsers = adminUserCache.get("");
				List<User> all = new ArrayList<User>();
				all.addAll(accountUsers);
				all.addAll(adminUsers);
				return all;
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return Collections.emptyList();
		}

		public Project getProject() {
			return project;
		}

		public void setProject(Project project) {
			this.project = project;
			pcSupport.firePropertyChange(PROJECT, null, project);
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
			pcSupport.firePropertyChange(COMMENT, null, comment);
		}

		public ServerRestClientModel getServerRestClientModel() {
			return ServerRestClientModel.this;
		}

	}

	public class UpdateServerProject implements ServerRestClientOperation {

		private final boolean notifyUserIfProjectNotHandled;

		public UpdateServerProject() {
			this(true);
		}

		public UpdateServerProject(boolean notifyUserIfProjectNotHandled) {
			super();
			this.notifyUserIfProjectNotHandled = notifyUserIfProjectNotHandled;
		}

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
					setVersions(Collections.<ProjectVersion> emptyList());
					if (notifyUserIfProjectNotHandled) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								FlexoController.notify(FlexoLocalization.localizedForKey("your_project_is_not_handled_by_the_server"));
							}
						});
					}
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

	public class UpdateProjectEditionSession implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (serverProject == null) {
				return;
			}
			List<Session> sessions = client.projectsProjectIDSessions(serverProject.getProjectId()).getAsXml(
					new GenericType<List<Session>>() {
					});
			if (sessions.size() == 0) {
				setSession(null);
			} else {
				setSession(sessions.get(0));
			}
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_edition_session_information");
		}

	}

	public class UpdateVersions implements ServerRestClientOperation {
		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (serverProject == null) {
				return;
			}
			validationInProgress.clear();
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("isMerged", Boolean.TRUE)
					.queryParam("isIntermediate", Boolean.FALSE).queryParam("isIntermediate", "null");
			Client restClient = client.createClient();
			setVersions(client.projectsProjectIDVersions(restClient, builder.build(), serverProject.getProjectId()).getAsXml(
					page * pageSize, (page + 1) * pageSize, "creationDate desc", new GenericType<List<ProjectVersion>>() {
					}));
			progress.increment(FlexoLocalization.localizedForKey("retrieving_status"));
			for (ProjectVersion version : getVersions()) {
				progress.increment(FlexoLocalization.localizedForKey("retrieving_status"));
				Boolean value = isValidationInProgress(client, restClient, version);
				setValidationInProgress(version, value);
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

	public class SendProjectToServer implements ServerRestClientOperation {

		private final String comment;

		public SendProjectToServer(String comment) {
			this.comment = comment;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (getUser() == null) {
				return;
			}
			if (serverProject == null) {
				return;
			}
			boolean success = false;
			String oldVersion = flexoProject.getVersion();
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("retrieving_next_version"), 6);
				String nextVersion = client.projectsProjectIDVersions(serverProject.getProjectId()).next().getAsTextPlain(String.class);
				flexoProject.setVersion(nextVersion);
				File zipFile = null;
				zipFile = File.createTempFile(StringUtils.rightPad(flexoProject.getProjectName(), 3, '_'), ".zip");
				zipFile.deleteOnExit();
				ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("saving"));
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
							ProgressWindow.instance().setProgress(
									String.format("%1$.2f%1$%"/*+" (%2$d/%3$d)"*/, percent, progress, length));
							lastUpdate = progress;
						}
					}
				};
				ProjectVersion version = new ProjectVersion();
				version.setProject(serverProject.getProjectId());
				version.setCreator(getUser().getLogin());
				version.setComment(comment);
				FormDataMultiPart mp = new FormDataMultiPart();
				mp.field("version", version, MediaType.APPLICATION_XML_TYPE);
				mp.bodyPart(new StreamDataBodyPart("file", inputStream, zipFile.getName()));
				zipFile.delete();
				ProjectVersion response = client.projectsProjectIDVersions(serverProject.getProjectId()).postMultipartFormDataAsXml(mp,
						ProjectVersion.class);
				success = true;
			} finally {
				if (!success) {
					flexoProject.setVersion(oldVersion);
				}
			}
			performOperationsInSwingWorker(new UpdateVersions());
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("sending_project");
		}

		@Override
		public int getSteps() {
			return 6;
		}
	}

	public class GenerateDocumentation implements ServerRestClientOperation {

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
			final Job returned = client.jobs().postXmlAsJob(job);
			if (returned != null && choice.getFolder() != null) {
				WatchedRemoteDocJob watchedRemoteJob = new WatchedRemoteDocJob();
				watchedRemoteJob.setRemoteJobId(returned.getJobId());
				watchedRemoteJob.setSaveToFolder(choice.getFolder().getAbsolutePath());
				watchedRemoteJob.setOpenDocument(choice.isAutomaticallyOpenFile());
				watchedRemoteJob.setUnzip(job.getDocFormat() == DocFormat.HTML);
				watchedRemoteJob.setProjectURI(flexoProject.getProjectURI());
				watchedRemoteJob.setLogin(getUser().getLogin());
				EntityManager em = LocalDBAccess.getInstance().getEntityManager();
				try {
					em.getTransaction().begin();
					em.persist(watchedRemoteJob);
					em.getTransaction().commit();
				} finally {
					if (em.getTransaction().isActive()) {
						em.getTransaction().rollback();
					}
					em.close();
				}
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

	public class GeneratePrototype implements ServerRestClientOperation {

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
			final Job returned = client.jobs().postXmlAsJob(job);
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
			Boolean value = isValidationInProgress(client, client.createClient(), version);
			setValidationInProgress(version, value);
		}

		@Override
		public String getLocalizedTitle() {
			return null;
		}

		@Override
		public int getSteps() {
			return -1;
		}

	}

	/*
	private class StartSession implements ServerRestClientOperation {

		public StartSession() {
			super();
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			Session session = new Session();
			session.setUser(getUser());
			client.projectsProjectIDSessions(serverProject.getProjectId()).postXmlAsSession();
					performOperationsInSwingWorker(new UpdateProjectEditionSession());
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("starting_session");
		}

		@Override
		public int getSteps() {
			return 0;
		}

	}
	*/

	public class CloseSession implements ServerRestClientOperation {

		private Session session;

		public CloseSession(Session session) {
			super();
			this.session = session;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			client.projectsProjectIDSessions(serverProject.getProjectId()).id(session.getEditSessionId()).deleteAsClientResponse();
			performOperationsInSwingWorker(new UpdateProjectEditionSession());
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("closing_session");
		}

		@Override
		public int getSteps() {
			return 0;
		}

	}

	protected Boolean isValidationInProgress(ServerRestClient client, Client restClient, ProjectVersion version) {
		UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("jobType", JobType.DOC_REINJECTER)
				.queryParam("jobType", JobType.PROJECT_MERGER).queryParam("version.versionID", version.getVersionID());
		List<Job> jobs = client.jobs(restClient, builder.build()).getAsXml(null, null, null, new GenericType<List<Job>>() {
		});
		Boolean validationInProgressValue = jobs.size() > 0;
		return validationInProgressValue;
	}

	public class CreateProjectAndUploadFirstVersion implements ServerRestClientOperation {

		private NewProjectParameter newProjectParameter;

		public CreateProjectAndUploadFirstVersion(NewProjectParameter newProjectParameter) {
			super();
			this.newProjectParameter = newProjectParameter;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			Project createdProject = client.projects().postXmlAsProject(newProjectParameter.getProject());
			setServerProject(createdProject);
			SendProjectToServer send = new SendProjectToServer(newProjectParameter.getComment());
			send.doOperation(client, progress);
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("creating_project");
		}

		@Override
		public int getSteps() {
			return 2;
		}

	}

	public List<Account> loadAccounts() {
		ServerRestClient client = getServerRestClient(false);
		if (client == null) {
			throw new RuntimeException("Operation cancelled");
		}
		UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("active", "true");
		return client.accounts(client.createClient(), builder.build()).getAsXml(null, null, "accountName asc",
				new GenericType<List<Account>>() {
				});
	}

	protected void setValidationInProgress(ProjectVersion version, Boolean value) {
		Boolean oldValue = validationInProgress.put(version, value);
		if (value != oldValue && oldValue != null) {
			performOperationsInSwingWorker(false, false, new UpdateVersions());
		}
		if (value && validationJobChecker == null) {
			validationJobChecker = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					for (Entry<ProjectVersion, Boolean> e : validationInProgress.entrySet()) {
						if (e.getValue()) {
							try {
								performOperations(false, new ValidationInProgress(e.getKey()));
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}, 10, 10, TimeUnit.SECONDS);
		} else if (!value && validationJobChecker != null) {
			boolean stop = true;
			for (Entry<ProjectVersion, Boolean> e : validationInProgress.entrySet()) {
				if (e.getValue()) {
					stop = false;
					break;
				}
			}
			if (stop) {
				validationJobChecker.cancel(true);
				validationJobChecker = null;
			}
		}
	}

	private static final String SERVER_PROJECT = "serverProject";
	private static final String SESSION = "session";
	private static final String VERSIONS = "versions";

	private final FlexoProject flexoProject;

	private Project serverProject;

	private Session session;

	private List<ProjectVersion> versions;

	private Map<ProjectVersion, Boolean> validationInProgress;

	private int pageSize = 5;
	private int page = 0;
	private ScheduledFuture<?> validationJobChecker;
	private LoadingCache<String, List<Account>> accountCache;
	private LoadingCache<String, List<User>> adminUserCache;
	private LoadingCache<Account, List<User>> accountUserCache;
	private FlexoController controller;

	public ServerRestClientModel(FlexoController controller, FlexoProject flexoProject) {
		super(controller.getApplicationContext().getServerRestService(), controller.getFlexoFrame());
		this.controller = controller;
		this.flexoProject = flexoProject;
		this.validationInProgress = new Hashtable<ProjectVersion, Boolean>();
		this.accountCache = CacheBuilder.newBuilder().build(new CacheLoader<String, List<Account>>() {
			@Override
			public List<Account> load(String key) throws Exception {
				return loadAccounts();
			}
		});
		this.accountUserCache = CacheBuilder.newBuilder().build(new CacheLoader<Account, List<User>>() {
			@Override
			public List<User> load(final Account account) throws Exception {
				final Holder<List<User>> result = new Holder<List<User>>();
				performOperationsInSwingWorker(true, true, new ServerRestClientOperation() {

					@Override
					public int getSteps() {
						return 0;
					}

					@Override
					public String getLocalizedTitle() {
						return FlexoLocalization.localizedForKey("retrieving_users_for_account");
					}

					@Override
					public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
						UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("active", "true")
								.queryParam("clientAccount", account.getClientAccountId());
						result.set(client.users(client.createClient(), builder.build()).getAsXml(null, null, "login asc",
								new GenericType<List<User>>() {
								}));

					}
				});
				if (result.get() != null) {
					return result.get();
				} else {
					throw new RuntimeException("Could not load users for account " + account.getAccountName());
				}
			}
		});
		this.adminUserCache = CacheBuilder.newBuilder().build(new CacheLoader<String, List<User>>() {
			@Override
			public List<User> load(String key) throws Exception {
				ServerRestClient client = getServerRestClient(false);
				if (client == null) {
					throw new RuntimeException("Operation cancelled");
				}
				UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("active", "true").queryParam("userType", "DNL");
				return client.users(client.createClient(), builder.build()).getAsXml(null, null, "login asc",
						new GenericType<List<User>>() {
						});
			}
		});

	}

	@Override
	public void delete() {
		if (validationJobChecker != null) {
			validationJobChecker.cancel(true);
			validationJobChecker = null;
		}
		super.delete();
	}

	public void refresh() {
		accountCache.invalidateAll();
		accountUserCache.invalidateAll();
		goOnline();
		performOperationsInSwingWorker(new UpdateUserOperation(), new UpdateServerProject(), new UpdateProjectEditionSession(),
				new UpdateVersions());
	}

	public void refreshVersions() {
		goOnline();
		performOperationsInSwingWorker(new UpdateVersions());
	}

	public FlexoProject getFlexoProject() {
		return flexoProject;
	}

	public Project getServerProject() {
		return serverProject;
	}

	public List<ProjectVersion> getVersions() {
		return versions;
	}

	public Session getSession() {
		return session;
	}

	private void setServerProject(Project serverProject) {
		this.serverProject = serverProject;
		pcSupport.firePropertyChange(SERVER_PROJECT, null, serverProject);
	}

	private void setVersions(List<ProjectVersion> versions) {
		this.versions = versions;
		pcSupport.firePropertyChange(VERSIONS, null, versions);
	}

	private void setSession(Session session) {
		this.session = session;
		pcSupport.firePropertyChange(SESSION, null, session);
	}

	public Icon getConsistencyIcon(ProjectVersion version) {
		if (isValidationInProgress(version)) {
			return IconLibrary.IN_PROGRESS_ICON;
		}
		if (version.isIsProtoValidationSuccessful()) {
			return IconLibrary.VALID_ICON;
		} else {
			return IconLibrary.INVALID_ICON;
		}
	}

	public String formatDate(XMLGregorianCalendar date) {
		return FORMATTER.format(date.toGregorianCalendar().getTime());
	}

	public boolean isValidationInProgress(ProjectVersion version) {
		Boolean b = validationInProgress.get(version);
		if (b == null) {
			// performOperations(false, new ValidationInProgress(version));
		}
		return b != null && b;
	}

	public void sendProjectToServer() {
		if (serverProject != null) {
			String comment = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
			if (comment == null) {
				return;
			}
			performOperationsInSwingWorker(new SendProjectToServer(comment));
		}
	}

	public void generateDocumentation(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}
		goOnline();
		DocGenerationChoice choice = new DocGenerationChoice(version);
		choice.setDocFormat(DocFormat.WORD);
		choice.setDocType(serverProject.getDocTypes().get(0));
		FIBDialog<DocGenerationChoice> dialog = FIBDialog.instanciateAndShowDialog(DOC_GENERATION_CHOOSER_FIB_FILE, choice,
				controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
			performOperationsInSwingWorker(new GenerateDocumentation(version, choice));
		}

	}

	public void generatePrototype(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (getUser() == null) {
			return;
		}
		goOnline();
		Job job = new Job();
		job.setCreator(getUser());
		job.setJobType(JobType.PROTOTYPE_BUILDER);
		job.setVersion(version);
		performOperationsInSwingWorker(new GeneratePrototype(version));
	}

	public void startSession() {
		// performOperationsInSwingWorker(new StartSession());
	}

	public void closeSession() {
		if (session != null) {
			performOperationsInSwingWorker(new CloseSession(session));
		}
	}

	public void createProject() {
		if (getUser() == null) {
			return;
		}
		if (serverProject != null) {
			FlexoController.notify(FlexoLocalization.localizedForKey("project_already_exists_on_server"));
			return;
		}
		NewProjectParameter data = new NewProjectParameter();
		data.getProject().setName(flexoProject.getDisplayName());
		data.setComment(FlexoLocalization.localizedForKey("first_import_of_project"));
		FIBDialog<NewProjectParameter> dialog = FIBDialog.instanciateAndShowDialog(NEW_SERVER_PROJECT_FIB_FILE, data,
				controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
			performOperationsInSwingWorker(new CreateProjectAndUploadFirstVersion(data));
		}
	}

}
