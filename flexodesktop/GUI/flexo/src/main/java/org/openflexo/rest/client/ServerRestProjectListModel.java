package org.openflexo.rest.client;

import java.awt.Color;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

import org.openflexo.AdvancedPrefs;
import org.openflexo.GeneralPreferences;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.rest.client.model.Session;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.controller.FlexoController;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;

public class ServerRestProjectListModel extends AbstractServerRestClientModel implements HasPropertyChangeSupport {

	public static final String PROJECT_DOWNLOAD_DIRECTORY = "projectDownloadDirectory";

	public static class ServerRestProjectListController extends FIBController {

		public ServerRestProjectListController(FIBComponent rootComponent) {
			super(rootComponent);
		}

		@Override
		public void validateAndDispose() {
			ServerRestProjectListModel listModel = (ServerRestProjectListModel) getDataObject();
			if (listModel.getSelectedProject() != null && listModel.canOpen(listModel.getSelectedProject())) {
				FlexoFileChooser chooser = new FlexoFileChooser(getWindow());
				chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_folder_where_to_download_project"));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setSelectedFile(AdvancedPrefs.getProjectsDownloadDirectory());
				int retval = chooser.showSaveDialog(getWindow());
				if (retval == JFileChooser.APPROVE_OPTION) {
					AdvancedPrefs.setProjectsDownloadDirectory(chooser.getSelectedFile());
					AdvancedPrefs.save();
					((ServerRestProjectListModel) getDataObject()).setProjectDownloadDirectory(chooser.getSelectedFile());
					super.validateAndDispose();
				}
			}
		}
	}

	public static final File FIB_FILE = new FileResource("Fib/ServerProjectList.fib");
	private static final String PROJECTS = "projects";
	private static final String SELECTED_PROJECT = "selectedProject";

	private Map<Project, ProjectVersion> lastVersion = new HashMap<Project, ProjectVersion>();
	private Map<Project, Session> editionSession = new HashMap<Project, Session>();

	public class UpdateProjects implements ServerRestClientOperation {

		@Override
		public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
			lastVersion.clear();
			editionSession.clear();
			if (getUser() == null) {
				setProjects(Collections.<Project> emptyList());
				return;
			}
			URI uri = UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectUri!", "null").build();
			Client jerseyClient = client.createClient();
			List<Project> projects = client.projects(jerseyClient, uri).getAsXml(-1, -1, null, new GenericType<List<Project>>() {

			});
			if (projects.size() > 0) {
				progress.setSteps(projects.size() * 2);
			}
			uri = UriBuilder.fromUri(client.getBASE_URI()).queryParam("isMerged", "true").build();
			for (Iterator<Project> iterator = projects.iterator(); iterator.hasNext();) {
				Project project = iterator.next();
				progress.increment(FlexoLocalization.localizedForKey("fetching_last_version_for") + " " + project.getName());
				List<ProjectVersion> versions = client.projectsProjectIDVersions(jerseyClient, uri, project.getProjectId()).getAsXml(0, 1,
						"creationDate desc", new GenericType<List<ProjectVersion>>() {
						});
				if (versions.size() == 0) {
					iterator.remove();
				} else {
					lastVersion.put(project, versions.get(0));
				}
				progress.increment(FlexoLocalization.localizedForKey("fetching_edition_session_for") + " " + project.getName());
				List<Session> sessions = client.projectsProjectIDSessions(jerseyClient, uri, project.getProjectId()).getAsXml(-1, -1,
						"downloadDate desc", new GenericType<List<Session>>() {
						});
				if (sessions.size() > 0) {
					editionSession.put(project, sessions.get(0));
				}
			}
			final List<String> lastServerProjects = GeneralPreferences.getLastServerProjects();
			Collections.sort(projects, new Comparator<Project>() {
				@Override
				public int compare(Project o1, Project o2) {
					int index1 = lastServerProjects.indexOf(o1.getProjectUri());
					int index2 = lastServerProjects.indexOf(o2.getProjectUri());
					if (index1 == index2) {
						if (o1.getLastAccessDate() != null && o2.getLastAccessDate() != null) {
							return o1.getLastAccessDate().compare(o2.getLastAccessDate());
						} else {
							return 0;
						}
					}
					if (index1 == -1) {
						if (index2 == -1) {
							return 0;
						} else {
							return 1;
						}
					} else if (index2 == -1) {
						return -1;
					} else {
						return index1 - index2;
					}
				}
			});

			setProjects(projects);
		}

		@Override
		public String getLocalizedTitle() {
			return FlexoLocalization.localizedForKey("fetching_project");
		}

		@Override
		public int getSteps() {
			return 1;
		}

	}

	private List<Project> projects;

	private Project selectedProject;
	private boolean forEdition;

	private File projectDownloadDirectory;

	public ServerRestProjectListModel(ServerRestService serverRestService, Window owner, boolean forEdition) {
		super(serverRestService, owner);
		this.forEdition = forEdition;
		projects = new ArrayList<Project>();
	}

	public void refresh() {
		performOperationsInSwingWorker(true, false, new UpdateUserOperation(), new UpdateProjects());
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
		pcSupport.firePropertyChange(PROJECTS, null, projects);
	}

	public ProjectVersion lastVersion(Project project) {
		return lastVersion.get(project);
	}

	public ImageIcon getProjectIcon() {
		return IconLibrary.OPENFLEXO_NOTEXT_16;
	}

	public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
		pcSupport.firePropertyChange(SELECTED_PROJECT, null, selectedProject);
	}

	public boolean canOpen(Project project) {
		if (!forEdition) {
			return true;
		}
		Session session = editionSession.get(project);
		return session == null /*|| getUser().getLogin().equals(session.getUser().getLogin())*/;
	}

	public String getTooltip(Project project) {
		Session session = editionSession.get(project);
		if (session != null && !getUser().getLogin().equals(session.getUser())) {
			return FlexoLocalization.localizedForKey("project_is_currently_edited_by") + " " + getUser().getFirstName() + " "
					+ getUser().getLastName() + " (" + getUser().getLogin() + ")";
		}
		return null;
	}

	public Color getTextColor(Project project) {
		if (canOpen(project)) {
			return null;
		} else {
			return UIManager.getDefaults().getColor("Label.disabledForeground");
		}
	}

	public File downloadToFolder(Project project, File folder) throws IOException {
		if (project == null) {
			return null;
		}
		if (folder.exists() && !folder.isDirectory()) {
			throw new IOException(folder.getAbsolutePath() + " " + FlexoLocalization.localizedForKey("is_not_a_directory"));
		}
		folder.mkdirs();
		if (!folder.canWrite()) {
			throw new IOException(FlexoLocalization.localizedForKey("you_dont_have_access_to") + " " + folder.getAbsolutePath());
		}
		DownloadProjectVersion download = new DownloadProjectVersion(project, lastVersion(project), forEdition
				&& editionSession.get(project) == null);
		performOperationsInSwingWorker(true, true, download);
		if (download.getFile() != null) {
			File tempDir = Files.createTempDir();
			ZipUtils.unzip(download.getFile(), tempDir);
			File tempProjectDirectory = FlexoProject.searchProjectDirectory(tempDir);
			File projectDirectory = new File(folder, project.getName() + ".prj");
			if (projectDirectory.exists()) {
				if (!FlexoController.confirm(FlexoLocalization.localizedForKey("folder") + " " + projectDirectory.getAbsolutePath() + " "
						+ FlexoLocalization.localizedForKey("already_exists") + "\n"
						+ FlexoLocalization.localizedForKey("do_you_want_to_overwrite_local_changes") + "?")) {
					return null;
				}
			}
			FileUtils.copyContentDirToDir(tempProjectDirectory, projectDirectory, CopyStrategy.REPLACE);
			return projectDirectory;
		} else {
			return null;
		}
	}

	public File getProjectDownloadDirectory() {
		return projectDownloadDirectory;
	}

	public void setProjectDownloadDirectory(File projectDownloadDirectory) {
		this.projectDownloadDirectory = projectDownloadDirectory;
		pcSupport.firePropertyChange(PROJECT_DOWNLOAD_DIRECTORY, null, projectDownloadDirectory);
	}
}
