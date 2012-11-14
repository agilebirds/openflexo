package org.openflexo.module;

import java.beans.PropertyChangeSupport;
import java.io.File;

import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

public class InteractiveFlexoProjectReferenceLoader implements FlexoProjectReferenceLoader {

	private static final FileResource FIB_FILE = new FileResource("Fib/FIBProjectLoaderDialog.fib");

	private ApplicationContext applicationContext;

	public InteractiveFlexoProjectReferenceLoader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public class ProjectReferenceLoaderData implements HasPropertyChangeSupport {

		private final FlexoProjectReference reference;

		private ProjectChooserComponent projectChooserComponent;

		private File selectedFile;

		private PropertyChangeSupport propertyChangeSupport;

		private String message;

		public ProjectReferenceLoaderData(FlexoProjectReference reference, File selectedFile) {
			this.reference = reference;
			this.selectedFile = selectedFile;
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			String old = this.message;
			this.message = message;
			getPropertyChangeSupport().firePropertyChange("message", old, message);
		}

		public InteractiveFlexoProjectReferenceLoader getProjectReferenceLoader() {
			return InteractiveFlexoProjectReferenceLoader.this;
		}

		public FlexoProjectReference getReference() {
			return reference;
		}

		public File getSelectedFile() {
			return selectedFile;
		}

		public void setSelectedFile(File selectedFile) {
			File old = this.selectedFile;
			this.selectedFile = selectedFile;
			getPropertyChangeSupport().firePropertyChange("selectedFile", old, selectedFile);
		}

		public File showProjectChooser() {
			getProjectChooserComponent().showOpenDialog();
			return selectedFile = getProjectChooserComponent().getSelectedFile();
		}

		public boolean isValid() {
			return selectedFile != null && selectedFile.exists();
		}

		public ProjectChooserComponent getProjectChooserComponent() {
			if (projectChooserComponent == null) {
				projectChooserComponent = new ProjectChooserComponent(null) {
				};

			}
			return projectChooserComponent;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return propertyChangeSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	/**
	 * Loads a project reference statelessly (ie, it does not access any instance variable). The fact that it is stateless is fundamental
	 * since many imported projects could need to be loaded.
	 */
	@Override
	public FlexoProject loadProject(FlexoProjectReference reference) throws ProjectLoadingCancelledException {
		File selectedFile = GeneralPreferences.getLocationForResource(reference.getProjectURI());
		boolean done = false;
		FIBDialog<ProjectReferenceLoaderData> dialog = null;
		while (!done) {
			if (selectedFile == null || !selectedFile.exists()) {
				if (dialog == null) {
					dialog = FIBDialog.instanciateAndShowDialog(FIB_FILE, new ProjectReferenceLoaderData(reference, selectedFile),
							FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				}
				if (dialog.getStatus() == Status.VALIDATED) {
					selectedFile = dialog.getData().getSelectedFile();
				} else {
					done = true;
				}
			}
			if (!done) {
				FlexoEditor editor;
				try {
					editor = applicationContext.getProjectLoader().loadProject(selectedFile);
					if (editor != null) {
						FlexoProject project = editor.getProject();
						if (project.getProjectURI().equals(reference.getProjectURI())) {
							boolean versionEqual = project.getVersion() == null && reference.getProjectVersion() == null
									|| project.getVersion() != null && project.getVersion().equals(reference.getProjectVersion());
							boolean revisionEqual = reference.getProjectRevision() != null
									&& project.getRevision() == reference.getProjectRevision();
							if (versionEqual && revisionEqual) {
								return editor.getProject();
							}
						} else {
							done = FlexoController.confirm(FlexoLocalization.localizedForKey("project_uri_do_not_match") + ".\n"
									+ FlexoLocalization.localizedForKey("uri") + " " + project.getProjectURI() + " "
									+ FlexoLocalization.localizedForKey("was_found") + "\n" + FlexoLocalization.localizedForKey("but")
									+ " " + reference + " " + FlexoLocalization.localizedForKey("was_expected") + "\n"
									+ FlexoLocalization.localizedForKey("would_you_like_to_provide_another_project"));
						}
					}
				} catch (ProjectInitializerException e) {
					e.printStackTrace();
				}
			}
		}
		throw new ProjectLoadingCancelledException("project_loading_cancelled_by_user");
	}
}
