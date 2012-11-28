package org.openflexo.module;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import org.openflexo.ApplicationContext;
import org.openflexo.components.ProjectChooserComponent;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.UserResourceCenter.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.FlexoProjectReferenceLoader;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

public class InteractiveFlexoProjectReferenceLoader implements FlexoProjectReferenceLoader {

	public static final FileResource FIB_FILE = new FileResource("Fib/FIBProjectLoaderDialog.fib");

	private ApplicationContext applicationContext;

	public InteractiveFlexoProjectReferenceLoader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@ModelEntity
	public static interface ProjectReferenceFileAssociation extends AccessibleProxyObject {
		public static final String REFERENCE = "reference";
		public static final String SELECTED_FILE = "selectedFile";
		public static final String MESSAGE = "message";

		@Initializer
		public ProjectReferenceFileAssociation init(@Parameter(REFERENCE) FlexoProjectReference reference,
				@Parameter(SELECTED_FILE) File selectedFile);

		@Getter(REFERENCE)
		public FlexoProjectReference getReference();

		@Setter(REFERENCE)
		public void setReference(FlexoProjectReference reference);

		@Getter(SELECTED_FILE)
		public File getSelectedFile();

		@Setter(SELECTED_FILE)
		public void setSelectedFile(File selectedFile);

		@Getter(MESSAGE)
		public String getMessage();

		@Setter(MESSAGE)
		public void setMessage(String message);
	}

	public static class ProjectReferenceLoaderData implements HasPropertyChangeSupport {

		private ProjectChooserComponent projectChooserComponent;

		private PropertyChangeSupport propertyChangeSupport;

		private final List<ProjectReferenceFileAssociation> associations;

		private final Dialog parent;

		public ProjectReferenceLoaderData(Dialog parent, List<ProjectReferenceFileAssociation> associations) {
			this.parent = parent;
			this.associations = associations;
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		}

		public List<ProjectReferenceFileAssociation> getAssociations() {
			return associations;
		}

		public File showProjectChooserComponent(ProjectReferenceFileAssociation association) {
			getProjectChooserComponent().setSelectedFile(association.getSelectedFile());
			if (getProjectChooserComponent().showOpenDialog() == JFileChooser.APPROVE_OPTION) {
				association.setSelectedFile(getProjectChooserComponent().getSelectedFile());
				boolean isValid = isValid();
				getPropertyChangeSupport().firePropertyChange("isValid", !isValid, isValid);
			}
			return getProjectChooserComponent().getSelectedFile();
		}

		public ProjectChooserComponent getProjectChooserComponent() {
			if (projectChooserComponent == null) {
				projectChooserComponent = new ProjectChooserComponent(parent) {
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

		public boolean isValid() {
			for (ProjectReferenceFileAssociation a : associations) {
				if (a.getSelectedFile() == null || !a.getSelectedFile().exists()) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Loads a project reference statelessly (ie, it does not access any instance variable). The fact that it is stateless is fundamental
	 * since many imported projects could need to be loaded.
	 */
	@Override
	public void loadProjects(List<FlexoProjectReference> references) throws ProjectLoadingCancelledException {
		try {
			boolean done = false;
			ModelFactory factory = new ModelFactory().importClass(ProjectReferenceFileAssociation.class);
			List<ProjectReferenceFileAssociation> associations = new ArrayList<InteractiveFlexoProjectReferenceLoader.ProjectReferenceFileAssociation>();
			for (FlexoProjectReference ref : references) {
				FlexoResource<FlexoProject> retrievedResource = getApplicationContext().getResourceCenterService().getUserResourceCenter()
						.retrieveResource(ref.getURI(), ref.getVersion(), ref.getResourceDataClass(), null);
				ProjectReferenceFileAssociation association = factory.newInstance(ProjectReferenceFileAssociation.class, ref,
						retrievedResource instanceof FlexoFileResource<?> ? ((FlexoFileResource<?>) retrievedResource).getFile() : null);
				associations.add(association);
			}

			FIBDialog<ProjectReferenceLoaderData> dialog = FIBDialog.instanciateDialog(FIB_FILE, null, FlexoFrame.getActiveFrame(), true,
					FlexoLocalization.getMainLocalizer());
			ProjectReferenceLoaderData data = new ProjectReferenceLoaderData(dialog, associations);
			dialog.getController().setDataObject(data);
			while (!done) {
				dialog.setMinimumSize(new Dimension(800, 250));
				dialog.showDialog();
				dialog.dispose();
				if (dialog.getStatus() == Status.VALIDATED) {
					done = true;
					for (ProjectReferenceFileAssociation a : data.getAssociations()) {
						FlexoEditor editor;
						FlexoProjectReference reference = a.getReference();
						if (reference.getReferredProject() == null
								|| !reference.getReferredProject().getProjectDirectory().equals(a.getSelectedFile())) {
							try {
								editor = applicationContext.getProjectLoader().loadProject(a.getSelectedFile(), false);
								if (editor != null) {
									FlexoProject project = editor.getProject();
									if (project.getProjectURI().equals(reference.getURI())) {
										boolean versionEqual = project.getVersion() == null && reference.getVersion() == null
												|| project.getVersion() != null && project.getVersion().equals(reference.getVersion());

										if (versionEqual) {
											reference.setReferredProject(project);
										} else {
											boolean ok = FlexoController.confirm(FlexoLocalization
													.localizedForKey("project_version_do_not_match")
													+ ". "
													+ project.getVersion()
													+ " "
													+ FlexoLocalization.localizedForKey("was_found")
													+ FlexoLocalization.localizedForKey("but")
													+ " "
													+ reference.getVersion()
													+ " "
													+ FlexoLocalization.localizedForKey("was_expected")
													+ "\n"
													+ FlexoLocalization.localizedForKey("would_you_like_to_update_to_version:")
													+ " "
													+ project.getVersion());
											if (!ok) {
												a.setSelectedFile(null);
												applicationContext.getProjectLoader().closeProject(project);
											}
											done &= ok;
										}
									} else {
										a.setMessage(FlexoLocalization.localizedForKey("project_uri_do_not_match") + ".\n"
												+ FlexoLocalization.localizedForKey("uri") + " " + project.getProjectURI() + " "
												+ FlexoLocalization.localizedForKey("was_found") + "\n"
												+ FlexoLocalization.localizedForKey("but") + " " + reference + " "
												+ FlexoLocalization.localizedForKey("was_expected"));
										done = false;
										break;
									}
								}
							} catch (ProjectInitializerException e) {
								a.setMessage(FlexoLocalization.localizedForKey("could_not_open_project_located_at")
										+ a.getSelectedFile().getAbsolutePath());
								a.setSelectedFile(null);
								done = false;
								break;
							} catch (ProjectLoadingCancelledException e) {
								done = false;
								break;
							}
						}

					}
					if (done) {
						return;
					}
				} else {
					throw new ProjectLoadingCancelledException("project_loading_cancelled_by_user");
				}
			}
			throw new ProjectLoadingCancelledException("project_loading_cancelled_by_user");
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
