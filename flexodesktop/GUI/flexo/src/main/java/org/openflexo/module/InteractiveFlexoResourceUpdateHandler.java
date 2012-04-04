/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.UnexpectedException;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.rm.CustomTemplatesResource;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoImportedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResource.DependencyAlgorithmScheme;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoResourceUpdateHandler;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.ImportedResourceData;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class InteractiveFlexoResourceUpdateHandler extends FlexoResourceUpdateHandler {

	protected static final Logger logger = Logger.getLogger(InteractiveFlexoResourceUpdateHandler.class.getPackage().getName());

	public InteractiveFlexoResourceUpdateHandler() {
		super();
	}

	@Override
	protected OptionWhenStorageResourceFoundAsConflicting getOptionWhenStorageResourceFoundAsConflicting(
			FlexoStorageResource storageResource) {
		String[] options;
		if (storageResource == null || !storageResource.implementsResourceMerge()) {
			options = new String[3];
		} else {
			options = new String[4];
		}
		options[0] = FlexoLocalization.localizedForKey("update_from_disk");
		options[1] = FlexoLocalization.localizedForKey("overwrite_disk_change");
		options[2] = FlexoLocalization.localizedForKey("ignore");
		if (options.length == 4) {
			options[3] = FlexoLocalization.localizedForKey("merge_changes");
		}
		int choice = FlexoController.selectOption(FlexoLocalization.localizedForKey("conflict_detected_on_resource") + " "
				+ (storageResource != null ? storageResource : "") + "\n" + FlexoLocalization.localizedForKey("possible_actions_are"),
				options, options[2]);
		switch (choice) {
		case 0:
			return OptionWhenStorageResourceFoundAsConflicting.UpdateFromDisk;
		case 1:
			return OptionWhenStorageResourceFoundAsConflicting.OverwriteDiskChange;
		case 2:
			return OptionWhenStorageResourceFoundAsConflicting.Ignore;
		case 3:
			return OptionWhenStorageResourceFoundAsConflicting.MergeChanges;
		default:
			return null;
		}
	}

	@Override
	protected OptionWhenStorageResourceFoundAsModifiedOnDisk getOptionWhenStorageResourceFoundAsModifiedOnDisk(
			FlexoStorageResource storageResource) {
		String[] options = new String[3];
		options[0] = FlexoLocalization.localizedForKey("update_from_disk");
		options[1] = FlexoLocalization.localizedForKey("overwrite_disk_change");
		options[2] = FlexoLocalization.localizedForKey("ignore");
		int choice = FlexoController.selectOption(FlexoLocalization.localizedForKey("disk_change_detected_on_resource") + " "
				+ (storageResource != null ? storageResource : "") + "\n" + FlexoLocalization.localizedForKey("possible_actions_are"),
				options, options[2]);
		switch (choice) {
		case 0:
			return OptionWhenStorageResourceFoundAsModifiedOnDisk.UpdateFromDisk;
		case 1:
			return OptionWhenStorageResourceFoundAsModifiedOnDisk.OverwriteDiskChange;
		case 2:
			return OptionWhenStorageResourceFoundAsModifiedOnDisk.Ignore;
		default:
			return null;
		}
	}

	@Override
	protected OptionWhenImportedResourceFoundAsModifiedOnDisk getOptionWhenImportedResourceFoundAsModifiedOnDisk(
			FlexoImportedResource importedResource) {
		String[] options = new String[2];
		options[0] = FlexoLocalization.localizedForKey("update_from_disk");
		options[1] = FlexoLocalization.localizedForKey("ignore");
		int choice = FlexoController.selectOption(FlexoLocalization.localizedForKey("disk_change_detected_on_imported_resource") + " "
				+ (importedResource != null ? importedResource : "") + "\n" + FlexoLocalization.localizedForKey("possible_actions_are"),
				options, options[1]);

		switch (choice) {
		case 0:
			return OptionWhenImportedResourceFoundAsModifiedOnDisk.UpdateFromDisk;
		case 1:
			return OptionWhenImportedResourceFoundAsModifiedOnDisk.Ignore;
		default:
			return null;
		}
	}

	@Override
	public void handlesResourceUpdate(final FlexoFileResource fileResource) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("handlesResourceUpdate() for " + fileResource);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (fileResource instanceof FlexoStorageResource) {

					FlexoStorageResource storageResource = (FlexoStorageResource) fileResource;

					if (storageResource.isModified()) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Conflict detected on resource " + storageResource);
						}
						OptionWhenStorageResourceFoundAsConflicting choice = getOptionWhenStorageResourceFoundAsConflicting(storageResource);
						if (choice == OptionWhenStorageResourceFoundAsConflicting.UpdateFromDisk) {
							try {
								reloadProject(storageResource);
							} catch (FlexoException e) {
								handleException("problem_when_trying_to_update_from_disk", e);
							} catch (ProjectInitializerException e) {
								handleException("problem_when_trying_to_update_from_disk", new UnexpectedException(e));
							}
						} else if (choice == OptionWhenStorageResourceFoundAsConflicting.OverwriteDiskChange) {
							try {
								storageResource.saveResourceData();
							} catch (FlexoException e) {
								handleException("error_during_resource_saving", e);
							}
						} else if (choice == OptionWhenStorageResourceFoundAsConflicting.Ignore) {
							// Does nothing
						} else if (choice == OptionWhenStorageResourceFoundAsConflicting.MergeChanges) {
							// Not implemented
							try {
								storageResource.performMerge();
							} catch (FlexoException e) {
								handleException("error_during_resource_merge", e);
							}
						}
					} else {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Update detected on resource " + storageResource);
						}
						OptionWhenStorageResourceFoundAsModifiedOnDisk choice = getOptionWhenStorageResourceFoundAsModifiedOnDisk(storageResource);
						if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.UpdateFromDisk) {
							try {
								storageResource.performDiskUpdate();
							} catch (FlexoException e) {
								handleException("problem_when_trying_to_update_from_disk", e);
							}
						} else if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.OverwriteDiskChange) {
							try {
								storageResource.saveResourceData();
							} catch (FlexoException e) {
								handleException("error_during_resource_saving", e);
							}
						} else if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.Ignore) {
							// Does nothing
						}
					}
				}

				else if (fileResource instanceof FlexoImportedResource) {

					FlexoImportedResource importedResource = (FlexoImportedResource) fileResource;

					if (logger.isLoggable(Level.INFO)) {
						logger.info("Update detected on resource " + importedResource);
					}
					OptionWhenImportedResourceFoundAsModifiedOnDisk choice = getOptionWhenImportedResourceFoundAsModifiedOnDisk(importedResource);
					if (choice == OptionWhenImportedResourceFoundAsModifiedOnDisk.UpdateFromDisk) {
						try {
							importedResource.performDiskUpdate();
						} catch (FlexoException e) {
							handleException("problem_when_trying_to_update_from_disk", e);
						}
					} else if (choice == OptionWhenImportedResourceFoundAsModifiedOnDisk.Ignore) {
						// Does nothing
					}
				}

				else if (fileResource instanceof FlexoGeneratedResource) {

					FlexoGeneratedResource generatedResource = (FlexoGeneratedResource) fileResource;

					if (logger.isLoggable(Level.INFO)) {
						logger.info("Update detected on resource " + generatedResource);
					}

					generatedResourceModified(generatedResource);
				}

				else if (fileResource instanceof CustomTemplatesResource) {
					logger.info("Updating " + fileResource);
					CustomCGTemplateRepository rep = fileResource.getProject().getGeneratedCode().getTemplates()
							.getCustomCGTemplateRepository((CustomTemplatesResource) fileResource);
					if (rep == null) {
						rep = fileResource.getProject().getGeneratedDoc().getTemplates()
								.getCustomCGTemplateRepository((CustomTemplatesResource) fileResource);
					}
					if (rep == null) {
						rep = fileResource.getProject().getGeneratedSources().getTemplates()
								.getCustomCGTemplateRepository((CustomTemplatesResource) fileResource);
					}
					rep.update();
				}

				else {
					logger.warning("Dont known what to do with a " + fileResource + " of " + fileResource.getClass().getName());
				}
			}
		});
	}

	@Override
	public void handlesResourcesUpdate(final List<FlexoFileResource<? extends FlexoResourceData>> updatedResources) {
		String[] options = new String[2];
		options[0] = FlexoLocalization.localizedForKey("batch_decision");
		options[1] = FlexoLocalization.localizedForKey("one_by_one_decidions");
		int choice = FlexoController.selectOption(FlexoLocalization.localizedForKey("disk_change_detected_on_resource"), options[0],
				options);
		switch (choice) {
		case 1:
			for (FlexoFileResource<? extends FlexoResourceData> fileResource : updatedResources) {
				handlesResourceUpdate(fileResource);
			}
			break;
		default:
			handlesResourcesUpdateWithBatchDecision(updatedResources);
		}
	}

	private void handlesResourcesUpdateWithBatchDecision(List<FlexoFileResource<? extends FlexoResourceData>> updatedResources) {
		List<FlexoStorageResource<? extends StorageResourceData>> updatedStorageResource = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
		List<FlexoStorageResource<? extends StorageResourceData>> conflictingStorageResource = new ArrayList<FlexoStorageResource<? extends StorageResourceData>>();
		List<FlexoImportedResource<? extends ImportedResourceData>> importedResource = new ArrayList<FlexoImportedResource<? extends ImportedResourceData>>();
		List<FlexoGeneratedResource<? extends GeneratedResourceData>> generatedResource = new ArrayList<FlexoGeneratedResource<? extends GeneratedResourceData>>();
		List<CustomTemplatesResource> templates = new ArrayList<CustomTemplatesResource>();
		for (FlexoFileResource<? extends FlexoResourceData> r : updatedResources) {
			if (r instanceof FlexoStorageResource<?>) {
				if (((FlexoStorageResource<?>) r).isModified()) {
					conflictingStorageResource.add((FlexoStorageResource<? extends StorageResourceData>) r);
				} else {
					updatedStorageResource.add((FlexoStorageResource<? extends StorageResourceData>) r);
				}
			} else if (r instanceof FlexoImportedResource<?>) {
				importedResource.add((FlexoImportedResource<? extends ImportedResourceData>) r);
			} else if (r instanceof FlexoGeneratedResource<?>) {
				generatedResource.add((FlexoGeneratedResource<? extends GeneratedResourceData>) r);
			} else if (r instanceof CustomTemplatesResource) {
				templates.add((CustomTemplatesResource) r);
			}
		}
		if (importedResource.size() > 0) {
			OptionWhenImportedResourceFoundAsModifiedOnDisk choice = getOptionWhenImportedResourceFoundAsModifiedOnDisk(null);
			if (choice == OptionWhenImportedResourceFoundAsModifiedOnDisk.UpdateFromDisk) {
				for (FlexoImportedResource<? extends ImportedResourceData> importedResource2 : importedResource) {
					try {
						importedResource2.performDiskUpdate();
					} catch (FlexoException e) {
						handleException("problem_when_trying_to_update_from_disk", e);
					}

				}
			} else if (choice == OptionWhenImportedResourceFoundAsModifiedOnDisk.Ignore) {
				// Does nothing
			}
		}
		if (generatedResource.size() > 0) {
			generatedResourcesModified(generatedResource);
		}
		if (templates.size() > 0) {
			for (CustomTemplatesResource templatesResource : templates) {
				CustomCGTemplateRepository rep = templatesResource.getProject().getGeneratedCode().getTemplates()
						.getCustomCGTemplateRepository(templatesResource);
				if (rep == null) {
					rep = templatesResource.getProject().getGeneratedDoc().getTemplates().getCustomCGTemplateRepository(templatesResource);
				}
				if (rep == null) {
					rep = templatesResource.getProject().getGeneratedSources().getTemplates()
							.getCustomCGTemplateRepository(templatesResource);
				}
				if (rep != null) {
					rep.update();
				} else if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find repository for template " + templatesResource);
				}
			}
		}
		if (updatedStorageResource.size() > 0) {
			OptionWhenStorageResourceFoundAsModifiedOnDisk choice = getOptionWhenStorageResourceFoundAsModifiedOnDisk(null);
			if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.UpdateFromDisk) {
				for (FlexoStorageResource<? extends StorageResourceData> storageResource : updatedStorageResource) {
					try {
						storageResource.performDiskUpdate();
					} catch (FlexoException e) {
						handleException("problem_when_trying_to_update_from_disk", e);
					}

				}
			} else if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.OverwriteDiskChange) {
				FlexoProject project = getModuleLoader().getProject();
				DependencyAlgorithmScheme scheme = project.getDependancyScheme();
				// Pessimistic dependancy scheme is cheaper and is not intended for this situation
				project.setDependancyScheme(DependencyAlgorithmScheme.Pessimistic);
				FlexoResource.sortResourcesWithDependancies(updatedStorageResource);
				project.setDependancyScheme(scheme);
				for (FlexoStorageResource<? extends StorageResourceData> storageResource : updatedStorageResource) {
					try {
						storageResource.saveResourceData();
					} catch (FlexoException e) {
						handleException("error_during_resource_saving", e);
					}

				}
			} else if (choice == OptionWhenStorageResourceFoundAsModifiedOnDisk.Ignore) {
				// Does nothing
			}
		}
		if (conflictingStorageResource.size() > 0) {
			OptionWhenStorageResourceFoundAsConflicting choice = getOptionWhenStorageResourceFoundAsConflicting(null);
			if (choice == OptionWhenStorageResourceFoundAsConflicting.UpdateFromDisk) {
				try {
					reloadProject(conflictingStorageResource);
				} catch (ProjectLoadingCancelledException e) {
					// TODO handle this
				} catch (ModuleLoadingException e) {
					// TODO handle this
				} catch (ProjectInitializerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (choice == OptionWhenStorageResourceFoundAsConflicting.OverwriteDiskChange) {
				try {
					FlexoProject project = getModuleLoader().getProject();
					;
					DependencyAlgorithmScheme scheme = project.getDependancyScheme();
					// Pessimistic dependancy scheme is cheaper and is not intended for this situation
					project.setDependancyScheme(DependencyAlgorithmScheme.Pessimistic);
					FlexoResource.sortResourcesWithDependancies(conflictingStorageResource);
					project.setDependancyScheme(scheme);
					for (FlexoStorageResource<? extends StorageResourceData> flexoStorageResource : conflictingStorageResource) {
						flexoStorageResource.saveResourceData();
					}
				} catch (FlexoException e) {
					handleException("error_during_resource_saving", e);
				}
			} else if (choice == OptionWhenStorageResourceFoundAsConflicting.Ignore) {
				// Does nothing
			}
		}
	}

	private void reloadProject(List<FlexoStorageResource<? extends StorageResourceData>> updatedStorageResource)
			throws ProjectLoadingCancelledException, ModuleLoadingException, ProjectInitializerException {
		for (FlexoStorageResource<? extends StorageResourceData> flexoStorageResource : updatedStorageResource) {
			flexoStorageResource.getResourceData().clearIsModified(true);
		}
		getModuleLoader().reloadProject();
	}

	@Override
	public void reloadProject(FlexoStorageResource fileResource) throws ProjectLoadingCancelledException, ModuleLoadingException,
			ProjectInitializerException {
		fileResource.getResourceData().clearIsModified(true);
		getModuleLoader().reloadProject();
	}

	private ModuleLoader getModuleLoader() {
		return ModuleLoader.instance();
	}

	private ProjectLoader getProjectLoader() {
		return ProjectLoader.instance();
	}

	@Override
	protected void handleException(String unlocalizedMessage, FlexoException exception) {
		exception.printStackTrace();
		FlexoController.showError(FlexoLocalization.localizedForKey(unlocalizedMessage));
	}

	@Override
	protected void generatedResourceModified(FlexoGeneratedResource generatedResource) {
		if (_generatedResourceModifiedHook == null) {
			super.generatedResourceModified(generatedResource);
		} else {
			_generatedResourceModifiedHook.handleGeneratedResourceModified(generatedResource);
		}
	}

	@Override
	protected void generatedResourcesModified(List<FlexoGeneratedResource<? extends GeneratedResourceData>> generatedResource) {
		if (_generatedResourceModifiedHook == null) {
			super.generatedResourcesModified(generatedResource);
		} else {
			for (FlexoGeneratedResource<? extends GeneratedResourceData> resource : generatedResource) {

				_generatedResourceModifiedHook.handleGeneratedResourceModified(resource);
			}
		}
	}

	public interface GeneratedResourceModifiedHook {
		public void handleGeneratedResourceModified(FlexoGeneratedResource generatedResource);
	}

	private GeneratedResourceModifiedHook _generatedResourceModifiedHook = null;

	public GeneratedResourceModifiedHook getGeneratedResourceModifiedHook() {
		return _generatedResourceModifiedHook;
	}

	public void setGeneratedResourceModifiedHook(GeneratedResourceModifiedHook generatedResourceModifiedHook) {
		_generatedResourceModifiedHook = generatedResourceModifiedHook;
	}
}
