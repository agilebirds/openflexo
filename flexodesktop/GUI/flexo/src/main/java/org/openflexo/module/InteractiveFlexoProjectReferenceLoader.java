package org.openflexo.module;

import java.io.File;

import javax.swing.JFileChooser;

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
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

public class InteractiveFlexoProjectReferenceLoader implements FlexoProjectReferenceLoader {

	private ApplicationContext applicationContext;

	public InteractiveFlexoProjectReferenceLoader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public FlexoProject loadProject(FlexoProjectReference ref, boolean silentlyOnly) {
		boolean retrievedFromResourceCenter = false;
		FlexoResource<FlexoProject> retrievedResource = getApplicationContext().getResourceCenterService().getUserResourceCenter()
				.retrieveResource(ref.getURI(), ref.getVersion(), ref.getResourceDataClass(), null);
		File selectedFile = null;

		if (retrievedResource instanceof FlexoFileResource) {
			selectedFile = ((FlexoFileResource<?>) retrievedResource).getFile();
			retrievedFromResourceCenter = true;
		}
		ProjectChooserComponent projectChooser = null;
		while (true) {
			if (selectedFile == null || !selectedFile.exists()) {
				if (silentlyOnly) {
					return null;
				}
				if (projectChooser == null) {
					projectChooser = new ProjectChooserComponent(FlexoFrame.getActiveFrame()) {
					};
					projectChooser.setOpenMode();
					projectChooser.setTitle(FlexoLocalization.localizedForKey("locate_project") + " " + ref.getName() + " "
							+ ref.getVersion());
				}
				int ret = projectChooser.showOpenDialog();
				if (ret == JFileChooser.APPROVE_OPTION) {
					selectedFile = projectChooser.getSelectedFile();
					retrievedFromResourceCenter = false;
				} else {
					return null;
				}
			}
			boolean openedProject = applicationContext.getProjectLoader().hasEditorForProjectDirectory(selectedFile);
			if (!openedProject && silentlyOnly) {
				return null;
			}
			FlexoEditor editor = null;
			try {
				editor = applicationContext.getProjectLoader().loadProject(selectedFile, true);
			} catch (ProjectLoadingCancelledException e) {
				return null;
			} catch (ProjectInitializerException e) {
				e.printStackTrace();
				if (!retrievedFromResourceCenter) {
					FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_project_at") + " "
							+ selectedFile.getAbsolutePath());
					selectedFile = null;
				}
			}
			FlexoProject project = editor.getProject();
			if (project.getProjectURI().equals(ref.getURI())) {
				// Project URI do match
				boolean versionEqual = project.getVersion() == null && ref.getVersion() == null || project.getVersion() != null
						&& project.getVersion().equals(ref.getVersion());

				if (versionEqual) {
					return project;
				} else {
					boolean ok = FlexoController.confirm(FlexoLocalization.localizedForKey("project_version_do_not_match") + ". "
							+ project.getVersion() + " " + FlexoLocalization.localizedForKey("was_found")
							+ FlexoLocalization.localizedForKey("but") + " " + ref.getVersion() + " "
							+ FlexoLocalization.localizedForKey("was_expected") + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_switch_to_version:") + " " + project.getVersion());
					if (ok) {
						return project;
					} else if (!openedProject) {
						applicationContext.getProjectLoader().closeProject(project);
					}
				}
			} else {
				if (retrievedFromResourceCenter) {
					selectedFile = null;
					continue;
				}
				FlexoController.notify(FlexoLocalization.localizedForKey("project_uri_do_not_match") + ".\n"
						+ FlexoLocalization.localizedForKey("uri") + " " + project.getProjectURI() + " "
						+ FlexoLocalization.localizedForKey("was_found") + "\n" + FlexoLocalization.localizedForKey("but") + " " + ref
						+ " " + FlexoLocalization.localizedForKey("was_expected"));
			}
			selectedFile = null;
		}
	}
}
