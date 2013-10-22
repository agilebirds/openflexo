package org.openflexo.rest.client;

import java.awt.Desktop;
import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openflexo.AdvancedPrefs;
import org.openflexo.GeneralPreferences;
import org.openflexo.LocalDBAccess;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.toc.TOCDocumentationPresets;
import org.openflexo.foundation.toc.TOCRepositoryDefinition;
import org.openflexo.foundation.toc.action.AddTOCDocumentationPresets;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.model.Account;
import org.openflexo.rest.client.model.DocFormat;
import org.openflexo.rest.client.model.Document;
import org.openflexo.rest.client.model.Job;
import org.openflexo.rest.client.model.JobType;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.Session;
import org.openflexo.rest.client.model.TocEntryDefinition;
import org.openflexo.rest.client.model.User;
import org.openflexo.rest.client.model.UserType;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.Holder;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.controller.FlexoController;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class ServerRestClientModel extends AbstractServerRestClientModel implements HasPropertyChangeSupport {

	private static final List<String> HTML_FILE_ORDER = Arrays.asList("index.html", "main.html");

	public static final File FIB_FILE = new FileResource("Fib/ServerClientModelView.fib");
	public static final File DOCUMENT_LIST_FIB_FILE = new FileResource("Fib/ServerDocumentList.fib");
	public static final File DOC_GENERATION_CHOOSER_FIB_FILE = new FileResource("Fib/DocGenerationChooser.fib");
	public static final File NEW_SERVER_PROJECT_FIB_FILE = new FileResource("Fib/NewServerProject.fib");

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yy HH:mm");

	private static final String AUTOMATICALLY_OPEN_FILE = "automaticallyOpenFile";
	private static final String DOC_TYPE = "docType";
	private static final String DOC_FORMAT = "docFormat";
	private static final String FOLDER = "folder";
	private static final String TOC_ENTRY = "tocEntry";
	private static final String PRESETS_NAME = "presetsName";

	public class DocGenerationChoice implements HasPropertyChangeSupport {
		private static final String PRESETS = "presets";
		private final ProjectVersion version;
		private PropertyChangeSupport pcSupport;
		private TOCDocumentationPresets presets;
		private String presetsName;
		private String docType;
		private DocFormat docFormat;
		private TocEntryDefinition tocEntry;
		private File folder;
		private boolean automaticallyOpenFile = true;
		private List<TOCDocumentationPresets> availablePresets;

		public DocGenerationChoice(ProjectVersion version) {
			this.version = version;
			pcSupport = new PropertyChangeSupport(this);
			availablePresets = new ArrayList<TOCDocumentationPresets>();
			availablePresets.add(null);
			if (flexoProject.getTOCData(false) != null) {
				for (TOCDocumentationPresets presets : flexoProject.getTOCData().getPresets()) {
					if (serverProject.getDocTypes().contains(presets.getDocType())) {
						if (presets.getToc() == null || getCorrespondingTocEntry(presets.getToc()) != null) {
							availablePresets.add(presets);
						}
					}
				}
			}
		}

		public List<TOCDocumentationPresets> getAvailablePresets() {
			return availablePresets;
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

		public TocEntryDefinition getTocEntry() {
			if (presets != null) {
				return getCorrespondingTocEntry(presets.getToc());
			} else {
				return tocEntry;
			}
		}

		public String getPresetsName() {
			return presetsName;
		}

		public void setPresetsName(String presetsName) {
			this.presetsName = presetsName;
			pcSupport.firePropertyChange(PRESETS_NAME, null, presetsName);
		}

		private TocEntryDefinition getCorrespondingTocEntry(TOCRepositoryDefinition other) {
			if (other == null) {
				return null;
			}
			for (TocEntryDefinition def : version.getTocEntries()) {
				if (def.getFlexoID() == null) {
					if (other.getFlexoID() != null) {
						continue;
					}
				} else if (!def.getFlexoID().equals(other.getFlexoID())) {
					continue;
				}
				if (def.getTitle() == null) {
					if (other.getTitle() != null) {
						continue;
					}
				} else if (!def.getTitle().equals(other.getTitle())) {
					continue;
				}
				if (def.getUserID() == null) {
					if (other.getUserID() != null) {
						continue;
					}
				} else if (!def.getUserID().equals(other.getUserID())) {
					continue;
				}
				return def;
			}
			return null;
		}

		public void setTocEntry(TocEntryDefinition tocEntry) {
			this.tocEntry = tocEntry;
			pcSupport.firePropertyChange(TOC_ENTRY, null, tocEntry);
		}

		public String getDocType() {
			if (presets != null) {
				if (getServerProject().getDocTypes().contains(presets.getDocType())) {
					return presets.getDocType();
				} else {
					return null;
				}
			} else {
				return docType;
			}
		}

		public void setDocType(String docType) {
			this.docType = docType;
			pcSupport.firePropertyChange(DOC_TYPE, null, docType);
		}

		public DocFormat getDocFormat() {
			if (presets != null) {
				Format format = presets.getFormat();
				if (format == null) {
					return null;
				}
				switch (presets.getFormat()) {
				case HTML:
					return DocFormat.HTML;
				case DOCX:
					return DocFormat.WORD;
				}
				return null;
			}
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

		public TOCDocumentationPresets getPresets() {
			return presets;
		}

		public void setPresets(TOCDocumentationPresets presets) {
			this.presets = presets;
			// Little hack to update the UI
			pcSupport.firePropertyChange(PRESETS, null, presets);
			pcSupport.firePropertyChange(TOC_ENTRY, null, getTocEntry());
			pcSupport.firePropertyChange(DOC_FORMAT, null, getDocFormat());
			pcSupport.firePropertyChange(DOC_TYPE, null, getDocType());
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
			if (getAccount() != null) {
				project.setClientAccount(getAccount());
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
				if (getAccount() != null) {
					return Arrays.asList(getAccount());
				} else {
					return Collections.emptyList();
				}
			}
		}

		public List<User> getAvailableUsers(final Account account) {
			if (account == null) {
				return Collections.emptyList();
			}
			List<User> all = new ArrayList<User>();
			try {
				List<User> accountUsers = accountUserCache.get(account);
				List<User> adminUsers = adminUserCache.get("");
				all.addAll(accountUsers);
				all.addAll(adminUsers);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			return all;
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

	public class RetrieveGeneratedDocuments implements ServerRestClientOperation {

		private final ProjectVersion version;
		private List<Document> documents;

		public RetrieveGeneratedDocuments(ProjectVersion version) {
			super();
			this.version = version;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (version == null) {
				return;
			}
			if (documents == null) {
				documents = client.documents(
						client.createClient(),
						UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectVersion", version.getVersionID())
								.queryParam("docType", "GENERATED").build()).getAsXml(0, 100, "creationDate desc",
						new GenericType<List<Document>>() {
						});
			}
		}

		public String getFormat(String docFormat) {
			if (docFormat == null) {
				return null;
			}
			docFormat = docFormat.toUpperCase();
			if (docFormat.equals("WORD") || docFormat.equals("DOCX")) {
				return "WORD";
			} else if (docFormat.equals("HTML")) {
				return "HTML";
			} else if (docFormat.equals("PDF") || docFormat.equals("LATEX")) {
				return "PDF";
			} else {
				return docFormat;
			}
		}

		public void download(final Document document, final boolean openDocument, Window parentWindow) {
			if (document.getDocUuid() == null) {
				FlexoController.notify(FlexoLocalization.localizedForKey("sorry_but_document_file_has_been_removed"));
				return;
			}
			FlexoFileChooser chooser = new FlexoFileChooser(parentWindow);
			chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_where_to_save_document") + " " + document.getTitle());
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			String extension = getExtensionForDocument(document);
			String fileName = document.getTitle() + extension;
			chooser.setCurrentDirectory(AdvancedPrefs.getLastDocumentDirectory());
			chooser.setSelectedFile(new File(fileName));
			while (true) {
				int ret = chooser.showSaveDialog(parentWindow);
				File file = chooser.getSelectedFile();
				if (ret == JFileChooser.APPROVE_OPTION && file != null) {
					if (file.isDirectory()) {
						file = new File(file, fileName);
					} else if (!file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
						file = new File(file.getParentFile(), file.getName() + extension);
					}
					file.getParentFile().mkdirs();
					boolean canWrite = false;
					try {
						canWrite = file.getParentFile().exists() && file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (!canWrite) {
						FlexoController.notify(FlexoLocalization.localizedForKey("you_cant_write_in") + " "
								+ file.getParentFile().getAbsolutePath());
						continue;
					}
					AdvancedPrefs.setLastDocumentDirectory(file.getParentFile());
					AdvancedPrefs.save();
					final File selectedFile = file;
					performOperationsInSwingWorker(true, true, new ServerRestClientOperation() {

						@Override
						public int getSteps() {
							return 1000;
						}

						@Override
						public String getLocalizedTitle() {
							return FlexoLocalization.localizedForKey("downloading") + " " + selectedFile.getName();
						}

						@Override
						public void doOperation(ServerRestClient client, final Progress progress) throws IOException,
								WebApplicationException {
							ClientResponse response = client.files().getAsOctetStream(document.getDocUuid(), ClientResponse.class);
							if (response.getStatus() >= 400) {
								String message = "";
								try {
									message = IOUtils.toString(response.getEntityInputStream(), "utf-8");
								} catch (IOException e) {
									e.printStackTrace();
								}
								throw new WebApplicationException(Response
										.fromResponse(Response.status(response.getClientResponseStatus()).build()).entity(message).build());
							}
							List<String> list = response.getHeaders().get("Content-Length");
							long length = -1;
							if (list != null) {
								for (String string : list) {
									try {
										length = Long.valueOf(string);
										break;
									} catch (NumberFormatException e) {
										e.printStackTrace();
									}
								}
							}
							InputStream input = null;
							final int stepSize = length > 0 ? (int) length / 1000 : -1;
							FileOutputStream fos = new FileOutputStream(selectedFile) {
								long bytesRead = 0;
								long lastByteProgressReport = 0;

								@Override
								public void write(byte[] b) throws IOException {
									super.write(b);
									bytesRead += b.length;
									updateDisplay(progress, stepSize);
								}

								@Override
								public void write(byte[] b, int off, int len) throws IOException {
									super.write(b, off, len);
									bytesRead += len;
									updateDisplay(progress, stepSize);
								}

								@Override
								public void write(int b) throws IOException {
									super.write(b);
									bytesRead++;
									updateDisplay(progress, stepSize);
								}

								private void updateDisplay(final Progress progress, final int stepSize) {
									while (stepSize > 0 && bytesRead - lastByteProgressReport > stepSize) {
										double percent = (double) bytesRead / (10 * stepSize);
										percent = Math.min(percent, 100.0);
										progress.increment(String.format("%1$.2f", percent) + "%");
										lastByteProgressReport += stepSize;
									}
									lastByteProgressReport = bytesRead;
								}
							};
							BufferedOutputStream bos = new BufferedOutputStream(fos);
							try {
								IOUtils.copy(response.getEntity(InputStream.class), bos);
							} finally {
								IOUtils.closeQuietly(bos);
								File fileToOpen = selectedFile;
								if (getFormat(document.getGenerationDocFormat()).equals("HTML")) {
									final File outputDir = new File(fileToOpen.getParentFile(), document.getTitle());
									outputDir.mkdir();
									ZipUtils.unzip(fileToOpen, outputDir);
									Collection<File> listFiles = FileUtils.listFiles(outputDir, new String[] { "html" }, false);
									if (listFiles.size() == 0) {
										listFiles = FileUtils.listFiles(outputDir, new String[] { "html" }, true);
									}
									if (listFiles.size() == 0) {
										fileToOpen = selectedFile;
									} else if (listFiles.size() == 1) {
										fileToOpen = listFiles.iterator().next();
									} else {
										fileToOpen = null;
										int curIndex = Integer.MAX_VALUE;
										for (File f : listFiles) {
											if (fileToOpen == null) {
												fileToOpen = f;
											} else {
												int index = HTML_FILE_ORDER.indexOf(f.getName().toLowerCase());
												if (index > -1 && index < curIndex) {
													curIndex = index;
													fileToOpen = f;
												}
											}
										}
									}
								}
								if (openDocument) {
									if (Desktop.isDesktopSupported()) {
										try {
											Desktop.getDesktop().open(fileToOpen);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					});
					return;
				} else {
					return;
				}
			}
		}

		private String getExtensionForDocument(Document document) {
			String docFormat = document.getGenerationDocFormat();
			if (docFormat == null) {
				return "";
			}
			docFormat = docFormat.toUpperCase();
			if (docFormat.equals("WORD") || docFormat.equals("DOCX")) {
				return ".docx";
			} else if (docFormat.equals("HTML")) {
				return ".zip";
			} else if (docFormat.equals("PDF") || docFormat.equals("LATEX")) {
				return ".pdf";
			} else {
				return docFormat.toLowerCase();
			}
		}

		public ProjectVersion getVersion() {
			return version;
		}

		public Project getProject() {
			return serverProject;
		}

		public List<Document> getDocuments() {
			return documents;
		}

		@Override
		public int getSteps() {
			return 0;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_documents");
		}

	}

	public class UpdateVersion implements ServerRestClientOperation {
		private final ProjectVersion version;

		public UpdateVersion(ProjectVersion version) {
			super();
			this.version = version;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			ProjectVersion updateVersion = client.projectsProjectIDVersions(version.getProject()).id(version.getVersionID())
					.getAsProjectVersionXml();
			int index = getVersions().indexOf(version);
			getVersions().set(index, updateVersion);
			setVersions(getVersions());
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("updating_version") + " " + version.getVersionNumber();
		}

		@Override
		public int getSteps() {
			return 1;
		}

	}

	public class UpdateVersions implements ServerRestClientOperation {

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (serverProject == null) {
				return;
			}

			Map<ProjectVersion, List<Job>> old = jobsInProgress;
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("isMerged", Boolean.TRUE)
					.queryParam("isIntermediate", Boolean.FALSE).queryParam("isIntermediate", "null");
			Client restClient = client.createClient();
			setVersions(client.projectsProjectIDVersions(restClient, builder.build(), serverProject.getProjectId()).getAsXml(
					page * pageSize, (page + 1) * pageSize, "creationDate desc", new GenericType<List<ProjectVersion>>() {
					}));

		}

		@Override
		public int getSteps() {
			return 1;
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_versions");
		}
	}

	public class UpdateJobsForVersion implements ServerRestClientOperation {

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			if (getVersions() == null) {
				return;
			}
			Client restClient = client.createClient();
			for (ProjectVersion version : getVersions()) {
				progress.increment(FlexoLocalization.localizedForKey("retrieving_status_for") + " " + version.getVersionNumber());
				List<Job> jobs = jobsInProgress(client, restClient, version);
				setJobsInProgress(jobs, version);
			}
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("retrieving_jobs_in_progress");
		}

		@Override
		public int getSteps() {
			return getVersions() == null ? 0 : getVersions().size();
		}

	}

	public class SendProjectToServer implements ServerRestClientOperation {

		private final String comment;
		private final boolean closeSession;

		public SendProjectToServer(String comment, boolean closeSession) {
			this.comment = comment;
			this.closeSession = closeSession;
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
			BufferedInputStream bis = null;
			try {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("retrieving_next_version"), 6);
				String nextVersion;
				if (closeSession) {
					nextVersion = client.projectsProjectIDVersions(serverProject.getProjectId()).nextMajor().getAsTextPlain(String.class);
				} else {
					nextVersion = client.projectsProjectIDVersions(serverProject.getProjectId()).next().getAsTextPlain(String.class);
				}
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

					@Override
					public int read(byte[] b) throws IOException {
						progress += b.length;
						updateProgress();
						return super.read(b);
					}

					@Override
					public int read(byte[] b, int off, int len) throws IOException {
						progress += len;
						updateProgress();
						return super.read(b, off, len);
					}

					private void updateProgress() {
						while (lastUpdate == 0 || progress - lastUpdate > stepSize || progress == length) {
							double percent = progress * 100.0d / length;
							percent = Math.min(100.0, percent);
							ProgressWindow instance = ProgressWindow.instance();
							if (instance != null) {
								instance.setProgress(String.format("%1$.2f%1$%"/*+" (%2$d/%3$d)"*/, percent, progress, length));
							}
							lastUpdate += stepSize;
							if (progress == length) {
								return;
							}
						}
						lastUpdate = progress;
					}
				};
				ProjectVersion version = new ProjectVersion();
				version.setProject(serverProject.getProjectId());
				version.setCreator(getUser().getLogin());
				version.setComment(comment);
				FormDataMultiPart mp = new FormDataMultiPart();
				mp.field("version", version, MediaType.APPLICATION_XML_TYPE);
				mp.bodyPart(new StreamDataBodyPart("file", bis = new BufferedInputStream(inputStream), zipFile.getName()));
				zipFile.delete();
				ProjectVersion response = client.projectsProjectIDVersions(serverProject.getProjectId()).postMultipartFormDataAsXml(mp,
						closeSession, ProjectVersion.class);
				success = true;
			} finally {
				IOUtils.closeQuietly(bis);
				if (!success) {
					flexoProject.setVersion(oldVersion);
				}
			}
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
			if (returned != null) {
				if (choice.getFolder() != null) {
					WatchedRemoteDocJob watchedRemoteJob = new WatchedRemoteDocJob();
					watchedRemoteJob.setRemoteJobId(returned.getJobId());
					watchedRemoteJob.setProjectID(version.getProject());
					watchedRemoteJob.setProjectVersionID(version.getVersionID());
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
				try {
					performOperations(true, new UpdateJobsForVersion());
				} catch (InterruptedException e) {
					e.printStackTrace();
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
			if (returned != null) {
				WatchedRemoteProtoJob watchedRemoteJob = new WatchedRemoteProtoJob();
				watchedRemoteJob.setProjectURI(flexoProject.getProjectURI());
				watchedRemoteJob.setLogin(getUser().getLogin());
				watchedRemoteJob.setProjectID(version.getProject());
				watchedRemoteJob.setProjectVersionID(version.getVersionID());
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
				try {
					performOperations(true, new UpdateJobsForVersion());
				} catch (InterruptedException e) {
					e.printStackTrace();
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

	private class JobInProgress implements ServerRestClientOperation {

		private ProjectVersion version;

		public JobInProgress(ProjectVersion version) {
			super();
			this.version = version;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("version.versionID", version.getVersionID());
			List<Job> jobs = client.jobs(client.createClient(), builder.build()).getAsXml(new GenericType<List<Job>>() {
			});
			setJobsInProgress(jobs, version);
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
			if (session != null) {
				try {
					client.projectsProjectIDSessions(serverProject.getProjectId()).id(session.getEditSessionId()).deleteAsClientResponse();
					GeneralPreferences.removeFromLastOpenedProjects(controller.getProjectDirectory());
					GeneralPreferences.save();
				} catch (UniformInterfaceException e) {
					if (e.getResponse().getStatus() != 204) {
						throw e;
					}
				}
				performOperationsInSwingWorker(new UpdateProjectEditionSession());
			}
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

	protected List<Job> jobsInProgress(ServerRestClient client, Client restClient, ProjectVersion version) {
		UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("version.versionID", version.getVersionID());
		List<Job> jobs = client.jobs(restClient, builder.build()).getAsXml(null, null, null, new GenericType<List<Job>>() {
		});
		return jobs;
	}

	public class CreateProjectAndUploadFirstVersion implements ServerRestClientOperation {

		private NewProjectParameter newProjectParameter;

		public CreateProjectAndUploadFirstVersion(NewProjectParameter newProjectParameter) {
			super();
			this.newProjectParameter = newProjectParameter;
		}

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			progress.increment(FlexoLocalization.localizedForKey("creating_project"));
			Project createdProject = client.projects().postXmlAsProject(newProjectParameter.getProject());
			setServerProject(createdProject);
			SendProjectToServer send = new SendProjectToServer(newProjectParameter.getComment(), false);
			progress.increment(send.getLocalizedTitle());
			send.doOperation(client, progress);
			UpdateVersions update = new UpdateVersions();
			progress.increment(update.getLocalizedTitle());
			update.doOperation(client, progress);
			if (session == null) {
				UpdateProjectEditionSession updateSession = new UpdateProjectEditionSession();
				progress.increment(updateSession.getLocalizedTitle());
				updateSession.doOperation(client, progress);
			}
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("creating_project");
		}

		@Override
		public int getSteps() {
			return session == null ? 4 : 3;
		}

	}

	public List<Account> loadAccounts() {
		if (getUser().getClientAccount() != null) {
			return Arrays.asList(getAccount());
		}
		ServerRestClient client = getServerRestClient(false);
		if (client == null) {
			throw new RuntimeException("Operation cancelled");
		}
		UriBuilder builder = UriBuilder.fromUri(client.getBASE_URI()).queryParam("active", "true");
		return client.accounts(client.createClient(), builder.build()).getAsXml(null, null, "accountName asc",
				new GenericType<List<Account>>() {
				});
	}

	protected void setJobsInProgress(List<Job> jobs, ProjectVersion version) {
		List<Job> oldJobs = jobsInProgress.put(version, jobs);
		if (!jobListIsEqual(oldJobs, jobs)) {
			for (Job job : jobs) {
				boolean found = false;
				if (oldJobs != null) {
					for (Job oldJob : oldJobs) {
						if (job.getJobId().equals(oldJob.getJobId())) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					firePropertyChangeForJob(job, version);
				}
			}
			if (oldJobs != null) {
				for (Job oldJob : oldJobs) {
					boolean found = false;
					for (Job job : jobs) {
						if (job.getJobId().equals(oldJob.getJobId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						performOperationsInSwingWorker(false, false, new UpdateVersion(version));
						firePropertyChangeForJob(oldJob, version);
					}
				}
			}
		} else if (oldJobs == null) {
			for (Job job : jobs) {
				firePropertyChangeForJob(job, version);
			}
		}
		if (jobs.size() > 0 && validationJobChecker == null) {
			validationJobChecker = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					for (Entry<ProjectVersion, List<Job>> e : jobsInProgress.entrySet()) {
						if (e.getValue() != null && e.getValue().size() > 0) {
							try {
								performOperations(false, new JobInProgress(e.getKey()));
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}, 10, 10, TimeUnit.SECONDS);
		} else if (jobs.size() == 0 && validationJobChecker != null) {
			boolean stop = true;
			for (Entry<ProjectVersion, List<Job>> e : jobsInProgress.entrySet()) {
				if (e.getValue() != null && e.getValue().size() > 0) {
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

	private void firePropertyChangeForJob(Job job, ProjectVersion version) {
		// Hack to make the table widget update
		version.getPropertyChangeSupport().firePropertyChange("jobStatus", null, null);
		/*
		switch (job.getJobType()) {
		case DOC_BUILDER:
			break;
		case DOC_REINJECTER:
		case PROJECT_MERGER:
			break;
		case PROTOTYPE_BUILDER:
			break;
		default:
			break;

		}
		*/
	}

	private boolean jobListIsEqual(List<Job> oldJobs, List<Job> jobs) {
		if (oldJobs == null) {
			return jobs == null;
		}
		if (jobs == null) {
			return oldJobs == null;
		}
		if (oldJobs.size() != jobs.size()) {
			return false;
		}
		for (Job job : jobs) {
			boolean found = false;
			for (Job oldJob : oldJobs) {
				if (job.getJobId().equals(oldJob.getJobId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	private static final String SERVER_PROJECT = "serverProject";
	private static final String SESSION = "session";
	private static final String VERSIONS = "versions";

	private final FlexoProject flexoProject;

	private Project serverProject;

	private Session session;

	private List<ProjectVersion> versions;

	private Map<ProjectVersion, List<Job>> jobsInProgress;

	private int pageSize = 5;
	private int page = 0;
	private ScheduledFuture<?> validationJobChecker;
	private LoadingCache<String, List<Account>> accountCache;
	private LoadingCache<String, List<User>> adminUserCache;
	private LoadingCache<Account, List<User>> accountUserCache;
	private FlexoController controller;

	private boolean editable;

	public ServerRestClientModel(FlexoController controller, FlexoProject flexoProject, boolean editable) {
		super(controller.getApplicationContext().getServerRestService(), controller.getFlexoFrame());
		this.controller = controller;
		this.flexoProject = flexoProject;
		this.editable = editable;
		this.jobsInProgress = new Hashtable<ProjectVersion, List<Job>>();
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
				if (getUser() == null || getUser().getUserType() != UserType.ADMIN) {
					return Collections.emptyList();
				}
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

	public boolean isEditable() {
		return editable;
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
				new UpdateVersions(), new UpdateJobsForVersion());
	}

	public void refreshVersions() {
		goOnline();
		performOperationsInSwingWorker(new UpdateVersions(), new UpdateJobsForVersion());
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

	public Icon getDocumentationJobInProgress(ProjectVersion version) {
		if (isDocGenInProgress(version)) {
			return IconLibrary.IN_PROGRESS_ICON;
		} else {
			return null;
		}
	}

	public Icon getPrototypeJobInProgress(ProjectVersion version) {
		if (isProtoGenInProgress(version)) {
			return IconLibrary.IN_PROGRESS_ICON;
		} else {
			return null;
		}
	}

	public String formatDate(XMLGregorianCalendar date) {
		return FORMATTER.format(date.toGregorianCalendar().getTime());
	}

	public boolean isValidationInProgress(ProjectVersion version) {
		return hasJobInProgress(version, JobType.PROJECT_MERGER, JobType.DOC_REINJECTER);
	}

	public boolean isDocGenInProgress(ProjectVersion version) {
		return hasJobInProgress(version, JobType.DOC_BUILDER);
	}

	public boolean isProtoGenInProgress(ProjectVersion version) {
		return hasJobInProgress(version, JobType.PROTOTYPE_BUILDER);
	}

	private boolean hasJobInProgress(ProjectVersion version, JobType... jobTypes) {
		List<Job> jobs = jobsInProgress.get(version);
		if (jobs == null || jobs.size() == 0) {
			return false;
		} else {
			for (Job job : jobs) {
				for (JobType type : jobTypes) {
					if (job.getJobType() == type) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public void sendToServerAndCloseSession() {
		sendToServer(true);
	}

	public void sendProjectToServer() {
		sendToServer(false);
	}

	private void sendToServer(boolean closeSession) {
		if (serverProject != null && getUser() != null && isEditable()) {
			if (canSendToServer()) {
				if (closeSession) {
					if (!FlexoController
							.confirm(FlexoLocalization
									.localizedForKey("once_you_have_closed_the_session_you_should_no_longer_modify_this_project. are_you_sure_you_want_to_close_this_session")
									+ "?")) {
						return;
					}
				}
				String comment = FlexoController.askForString(FlexoLocalization.localizedForKey("please_provide_some_comments"));
				if (comment == null) {
					return;
				}
				List<ServerRestClientOperation> operations = new ArrayList<AbstractServerRestClientModel.ServerRestClientOperation>(4);
				operations.add(new SendProjectToServer(comment, closeSession));
				if (session == null || closeSession) {
					operations.add(new UpdateProjectEditionSession());
				}
				operations.add(new UpdateVersions());
				operations.add(new UpdateJobsForVersion());
				performOperationsInSwingWorker(operations.toArray(new ServerRestClientOperation[operations.size()]));
			} else {
				FlexoController.notify(FlexoLocalization.localizedForKey("cannot_send_project_to_server_because") + "\n"
						+ FlexoLocalization.localizedForKey("project_is_currently_edited_by") + " " + session.getUser().getFirstName()
						+ " " + session.getUser().getLastName() + " (" + session.getUser().getLogin() + ")");
			}
		}
	}

	public String cantSendToServerReason() {
		if (isEditable() && !canSendToServer() && getSession() != null) {
			return FlexoLocalization.localizedForKey("cannot_send_project_to_server_because") + " "
					+ FlexoLocalization.localizedForKey("project_is_currently_edited_by") + " " + session.getUser().getFirstName() + " "
					+ session.getUser().getLastName() + " (" + session.getUser().getLogin() + ")";
		} else {
			return null;
		}
	}

	public boolean canSendToServer() {
		return isEditable() && (getSession() == null || getSession().getUser().getLogin().equals(getUser().getLogin()));
	}

	public void showDocuments(ProjectVersion version, Window parentWindow) {
		if (version == null) {
			return;
		}
		goOnline();
		RetrieveGeneratedDocuments retrieveDocuments = new RetrieveGeneratedDocuments(version);
		performOperationsInSwingWorker(true, true, retrieveDocuments);
		if (retrieveDocuments.getDocuments() != null) {
			FIBDialog.instanciateAndShowDialog(DOCUMENT_LIST_FIB_FILE, retrieveDocuments, parentWindow, true,
					FlexoLocalization.getMainLocalizer());
		}
	}

	public void showPrototypes(ProjectVersion version) {
		if (version == null) {
			return;
		}
		if (version.getProtoUrl() == null) {
			FlexoController.notify(FlexoLocalization.localizedForKey("version") + " " + version.getVersionNumber() + " "
					+ FlexoLocalization.localizedForKey("has_no_prototype_generated"));
			return;
		}
		FlexoController.notify(FlexoLocalization.localizedForKey("login") + ": " + version.getProtoLogin() + "\n"
				+ FlexoLocalization.localizedForKey("password") + ": " + version.getProtoPassword());
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(version.getProtoUrl()));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				FlexoController.notify(FlexoLocalization.localizedForKey("unable_to_open_prototype_for_version") + " "
						+ version.getVersionNumber());
				return;
			}
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
		choice.setFolder(AdvancedPrefs.getLastDocumentDirectory());
		while (true) {
			FIBDialog<DocGenerationChoice> dialog = FIBDialog.instanciateAndShowDialog(DOC_GENERATION_CHOOSER_FIB_FILE, choice,
					controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
			if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
				if (choice.getDocFormat() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("doc_format") + " "
							+ FlexoLocalization.localizedForKey("is_mandatory"));
					continue;
				}
				if (choice.getDocType() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("doc_type") + " "
							+ FlexoLocalization.localizedForKey("is_mandatory"));
					continue;
				}
				if (choice.getTocEntry() == null && choice.getDocFormat() != DocFormat.HTML) {
					FlexoController.notify(FlexoLocalization.localizedForKey("toc") + " "
							+ FlexoLocalization.localizedForKey("is_mandatory"));
					continue;
				}
				if (choice.getPresets() == null) {
					if (choice.getPresetsName() == null || choice.getPresetsName().trim().length() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("presets_name") + " "
								+ FlexoLocalization.localizedForKey("cannot_be_empty"));
						continue;
					}
					AddTOCDocumentationPresets addPresets = AddTOCDocumentationPresets.actionType.makeNewAction(getFlexoProject()
							.getTOCData(), null, controller.getEditor());
					addPresets.doAction();
					TOCDocumentationPresets presets = addPresets.getPresets();
					try {
						presets.setName(choice.getPresetsName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (choice.getTocEntry() != null && choice.getDocFormat() != DocFormat.HTML) {
						presets.setToc(new TOCRepositoryDefinition(choice.getTocEntry().getFlexoID(), choice.getTocEntry().getUserID(),
								choice.getTocEntry().getTitle()));
					}
					switch (choice.getDocFormat()) {
					case HTML:
						presets.setFormat(Format.HTML);
						break;
					case WORD:
						presets.setFormat(Format.DOCX);
						break;
					}
					presets.setDocType(choice.getDocType());
				}
				if (choice.getFolder() != null) {
					AdvancedPrefs.setLastDocumentDirectory(choice.getFolder());
					AdvancedPrefs.save();
				}
				goOnline();
				performOperationsInSwingWorker(new GenerateDocumentation(version, choice));
				return;
			} else {
				return;
			}
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
			goOnline();
			if (FlexoController
					.confirm(FlexoLocalization
							.localizedForKey("once_you_have_closed_the_session_you_should_no_longer_modify_this_project. are_you_sure_you_want_to_close_this_session")
							+ "?")) {
				performOperationsInSwingWorker(new CloseSession(session), new UpdateProjectEditionSession(), new UpdateVersions(),
						new UpdateJobsForVersion());
			}
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
		goOnline();
		NewProjectParameter data = new NewProjectParameter();
		data.getProject().setName(flexoProject.getDisplayName());
		data.setComment(FlexoLocalization.localizedForKey("first_import_of_project"));
		FIBDialog<NewProjectParameter> dialog = FIBDialog.instanciateAndShowDialog(NEW_SERVER_PROJECT_FIB_FILE, data,
				controller.getFlexoFrame(), true, FlexoLocalization.getMainLocalizer());
		if (dialog.getController().getStatus() == FIBController.Status.VALIDATED) {
			performOperationsInSwingWorker(new CreateProjectAndUploadFirstVersion(data), new UpdateVersions(), new UpdateJobsForVersion());
		}
	}

}
