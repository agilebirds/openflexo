package org.openflexo.module;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import javax.swing.JFileChooser;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.UserResourceCenter.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rest.client.AbstractServerRestClientModel;
import org.openflexo.rest.client.ServerRestClient;
import org.openflexo.rest.client.ServerRestService;
import org.openflexo.rest.client.model.Project;
import org.openflexo.rest.client.model.ProjectVersion;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FileUtils.CopyStrategy;
import org.openflexo.toolbox.ZipUtils;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

import com.google.common.io.Files;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;

public class InteractiveFlexoProjectReferenceLoader implements FlexoProjectReferenceLoader {

	private ApplicationContext applicationContext;

	public InteractiveFlexoProjectReferenceLoader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private class ServerRestVersionRetrieverModel extends AbstractServerRestClientModel {

		public ServerRestVersionRetrieverModel(ServerRestService serverRestService, Window owner) {
			super(serverRestService, owner);
		}

		public class RetrieveVersionOperation implements ServerRestClientOperation {

			private final FlexoProjectReference reference;

			private ProjectVersion version;

			private Project project;

			public RetrieveVersionOperation(FlexoProjectReference reference) {
				super();
				this.reference = reference;
			}

			@Override
			public void doOperation(ServerRestClient client, Progress progress) throws IOException, WebApplicationException {
				progress.increment(FlexoLocalization.localizedForKey("retrieving_project"));
				Client restClient = client.createClient();
				URI uri = UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectUri", reference.getURI())
						.queryParam("isActive", Boolean.TRUE).build();
				List<Project> projects = client.projects(restClient, uri).getAsXml(new GenericType<List<Project>>() {
				});
				if (projects.size() > 0) {
					project = projects.get(0);
					progress.increment(FlexoLocalization.localizedForKey("retrieving_version"));
					uri = UriBuilder.fromUri(client.getBASE_URI()).queryParam("projectRevision", reference.getRevision())
							.queryParam("isMerged", "true").build();
					List<ProjectVersion> versions = client.projectsProjectIDVersions(restClient, uri, project.getProjectId()).getAsXml(-1,
							-1, "creationDate desc", new GenericType<List<ProjectVersion>>() {
							});
					if (versions.size() > 0) {
						version = versions.get(0);
						return;
					}
					progress.increment(FlexoLocalization.localizedForKey("retrieving_version"));
					uri = UriBuilder.fromUri(client.getBASE_URI())
							.queryParam(URLEncoder.encode("projectRevision>", "utf-8"), reference.getRevision())
							.queryParam("isMerged", "true").build();
					versions = client.projectsProjectIDVersions(restClient, uri, project.getProjectId()).getAsXml(-1, -1,
							"projectRevision desc", new GenericType<List<ProjectVersion>>() {
							});
					if (versions.size() > 0) {
						version = versions.get(0);
						return;
					}
				}
			}

			@Override
			public String getLocalizedTitle() {
				return FlexoLocalization.localizedForKey("retrieving") + " " + reference.getName() + " " + reference.getVersion();
			}

			@Override
			public int getSteps() {
				return 3;
			}

			public Project getProject() {
				return project;
			}

			public ProjectVersion getVersion() {
				return version;
			}
		}

		public File downloadToFolder(FlexoProjectReference ref, File folder) throws IOException {
			RetrieveVersionOperation retrieveVersion = new RetrieveVersionOperation(ref);
			performOperationsInSwingWorker(true, true, retrieveVersion);
			Project project = retrieveVersion.getProject();
			ProjectVersion version = retrieveVersion.getVersion();
			if (project != null && version != null) {
				DownloadProjectVersion download = new DownloadProjectVersion(project, version, false);
				performOperationsInSwingWorker(true, true, download);
				if (download.getFile() != null) {
					File tempDir = Files.createTempDir();
					ZipUtils.unzip(download.getFile(), tempDir);
					File tempProjectDirectory = FlexoProject.searchProjectDirectory(tempDir);
					File projectDirectory = new File(folder, project.getName() + ".prj");
					FileUtils.copyContentDirToDir(tempProjectDirectory, projectDirectory, CopyStrategy.REPLACE);
					return projectDirectory;
				} else {
					return null;
				}
			}
			return null;
		}
	}

	@Override
	public FlexoProject loadProject(FlexoProjectReference ref, boolean silentlyOnly) {
		FlexoProject project = loadAndCheck(ref, silentlyOnly, retrieveFromUserResourceCenter(ref), false);
		if (project != null) {
			return project;
		}
		if (!silentlyOnly) {
			ServerRestVersionRetrieverModel model = new ServerRestVersionRetrieverModel(getApplicationContext().getServerRestService(),
					FlexoFrame.getActiveFrame());
			File documentDirectory = FileUtils.getDocumentDirectory();
			if (!documentDirectory.exists() || !documentDirectory.canWrite()) {
				documentDirectory = org.apache.commons.io.FileUtils.getUserDirectory();
			}
			File downloadToFolder = new File(documentDirectory, "OpenFlexo Projects");
			try {
				project = loadAndCheck(ref, silentlyOnly, model.downloadToFolder(ref, downloadToFolder), false);
				if (project != null) {
					return project;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (silentlyOnly) {
			return null;
		}
		ProjectChooserComponent projectChooser = new ProjectChooserComponent(FlexoFrame.getActiveFrame()) {
		};
		projectChooser.setOpenMode();
		projectChooser.setTitle(FlexoLocalization.localizedForKey("locate_project") + " " + ref.getName() + " " + ref.getVersion());
		while (true) {
			int ret = projectChooser.showOpenDialog();
			if (ret == JFileChooser.APPROVE_OPTION) {
				project = loadAndCheck(ref, silentlyOnly, projectChooser.getSelectedFile(), true);
				if (project != null) {
					return project;
				}
			} else {
				return null;
			}
		}
	}

	private FlexoProject loadAndCheck(FlexoProjectReference ref, boolean silentlyOnly, File selectedFile, boolean manuallySelected) {
		if (selectedFile == null || !selectedFile.exists()) {
			return null;
		}
		boolean wasOpened = applicationContext.getProjectLoader().hasEditorForProjectDirectory(selectedFile);
		FlexoEditor editor = load(selectedFile, silentlyOnly, manuallySelected);
		if (editor == null) {
			return null;
		}
		FlexoProject project = editor.getProject();
		if (project.getProjectURI().equals(ref.getURI())) {
			// Project URI do match
			boolean versionEqual = project.getVersion() == null && ref.getVersion() == null || project.getVersion() != null
					&& project.getVersion().equals(ref.getVersion());

			if (versionEqual) {
				return project;
			} else {
				boolean ok = !silentlyOnly
						&& FlexoController.confirm(FlexoLocalization.localizedForKey("project_version_do_not_match") + ". "
								+ project.getVersion() + " " + FlexoLocalization.localizedForKey("was_found")
								+ FlexoLocalization.localizedForKey("but") + " " + ref.getVersion() + " "
								+ FlexoLocalization.localizedForKey("was_expected") + "\n"
								+ FlexoLocalization.localizedForKey("would_you_like_to_switch_to_version:") + " " + project.getVersion());
				if (ok) {
					return project;
				} else if (!wasOpened) {
					applicationContext.getProjectLoader().closeProject(project);
				}
			}
		} else {
			if (!manuallySelected) {
				return null;
			}
			if (!silentlyOnly) {
				FlexoController.notify(FlexoLocalization.localizedForKey("project_uri_do_not_match") + ".\n"
						+ FlexoLocalization.localizedForKey("uri") + " " + project.getProjectURI() + " "
						+ FlexoLocalization.localizedForKey("was_found") + "\n" + FlexoLocalization.localizedForKey("but") + " " + ref
						+ " " + FlexoLocalization.localizedForKey("was_expected"));
			}
		}
		return null;
	}

	private FlexoEditor load(File selectedFile, boolean silentlyOnly, boolean manuallySelected) {
		boolean openedProject = applicationContext.getProjectLoader().hasEditorForProjectDirectory(selectedFile);
		if (!openedProject && silentlyOnly) {
			return null;
		}
		try {
			return applicationContext.getProjectLoader().loadProject(selectedFile, true);
		} catch (ProjectLoadingCancelledException e) {
			return null;
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			if (manuallySelected) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_project_at") + " "
						+ selectedFile.getAbsolutePath());
			}
		}
		return null;
	}

	private File retrieveFromUserResourceCenter(FlexoProjectReference ref) {
		FlexoResource<FlexoProject> retrievedResource = getApplicationContext().getResourceCenterService().getUserResourceCenter()
				.retrieveResource(ref.getURI(), ref.getVersion(), ref.getResourceDataClass(), null);

		if (retrievedResource instanceof FlexoFileResource) {
			return ((FlexoFileResource<?>) retrievedResource).getFile();
		}
		return null;
	}
}
