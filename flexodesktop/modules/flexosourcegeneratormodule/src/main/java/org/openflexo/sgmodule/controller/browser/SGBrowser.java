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
package org.openflexo.sgmodule.controller.browser;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGPathElement;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFolder;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGTemplateSet;
import org.openflexo.foundation.cg.version.CGFileIntermediateVersion;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SGTemplates;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.sgmodule.SGModule;
import org.openflexo.sgmodule.TechnologyModuleGUIFactory;
import org.openflexo.sgmodule.controller.SGController;

/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class SGBrowser extends ProjectBrowser implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(SGBrowser.class.getPackage().getName());

	private SGController _controller;

	class GeneratorBrowserConfigurationElementFactory implements BrowserElementFactory {

		GeneratorBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public BrowserElement makeNewElement(FlexoObject object, ProjectBrowser browser, BrowserElement parent) {
			if (object instanceof GeneratedSources) {
				return new GeneratedSourcesElement((GeneratedSources) object, browser, parent);
			} else if (object instanceof SourceRepository) {
				return new SourceRepositoryElement((SourceRepository) object, browser, parent);
			} else if (object instanceof CGSymbolicDirectory) {
				return new CGSymbolicDirectoryElement((CGSymbolicDirectory) object, browser, parent);
			} else if (object instanceof CGFolder) {
				return new CGFolderElement((CGFolder) object, browser, parent);
			} else if (object instanceof CGFile) {
				return new CGFileElement((CGFile) object, browser, parent);
			} else if (object instanceof CGFileReleaseVersion) {
				return new FileReleaseVersionElement((CGFileReleaseVersion) object, browser, parent);
			} else if (object instanceof CGFileIntermediateVersion) {
				return new FileIntermediateVersionElement((CGFileIntermediateVersion) object, browser, parent);
			} else if (object instanceof SGTemplates) {
				return new SGTemplatesElement((SGTemplates) object, browser, parent);
			} else if (object instanceof CGTemplateRepository) {
				return new CGTemplateRepositoryElement((CGTemplateRepository) object, browser, parent);
			} else if (object instanceof CGTemplateSet) {
				return new CGTemplateSetElement((CGTemplateSet) object, browser, parent);
			} else if (object instanceof CGTemplateFolder) {
				return new CGTemplateFolderElement((CGTemplateFolder) object, browser, parent);
			} else if (object instanceof CGTemplate) {
				return new CGTemplateFileElement((CGTemplate) object, browser, parent);
			} else if (object instanceof ImplementationModel) {
				return new ImplementationModelElement((ImplementationModel) object, browser, parent);
			} else if (object instanceof TechnologyModelObject) {

				TechnologyModuleGUIFactory technologyModuleGUIFactory = SGModule
						.getTechnologyModuleGUIFactory(((TechnologyModelObject) object).getTechnologyModuleImplementation().getClass());
				if (technologyModuleGUIFactory != null) {
					TechnologyModuleBrowserElement<?> element = technologyModuleGUIFactory.createBrowserElement(
							(TechnologyModelObject) object, browser, parent);
					if (element != null) {
						return element;
					}
				}

				// Not found via factory, use the generic one
				return new TechnologyModuleBrowserElement((TechnologyModelObject) object, browser, parent);
			}
			return null;
		}

	}

	protected abstract class CGFileFilter extends CustomBrowserFilter {
		public CGFileFilter(String name, Icon icon, GenerationStatus... status) {
			super(name, icon);
		}

		@Override
		public boolean accept(FlexoObject object) {
			if (object instanceof CGFile) {
				return acceptFile((CGFile) object);
			}
			if (object instanceof CGPathElement) {
				return acceptCGPathElement((CGPathElement) object);
			}
			return true;
		}

		public boolean acceptCGPathElement(CGPathElement pathElement) {
			for (CGFile file : pathElement.getFiles()) {
				if (acceptFile(file)) {
					return true;
				}
			}
			for (CGPathElement folder : pathElement.getSubFolders()) {
				if (acceptCGPathElement(folder)) {
					return true;
				}
			}
			return false;
		}

		public abstract boolean acceptFile(CGFile file);

	}

	public SGBrowser(SGController controller) {
		super(controller);
		setBrowserElementFactory(new GeneratorBrowserConfigurationElementFactory());
		_controller = controller;
		update();
	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		return null;
	}

	@Override
	public void configure() {
		// Custom filters
		setAllFilesAndDirectoryFilter(new CustomBrowserFilter("all_files_and_directories", null) {
			@Override
			public boolean accept(FlexoObject object) {
				return true;
			}
		});
		addToCustomFilters(getAllFilesAndDirectoryFilter());

		setUpToDateFilesFilter(new CGFileFilter("up_to_date_files", FilesIconLibrary.SMALL_MISC_FILE_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus() == GenerationStatus.UpToDate;
			}
		});
		addToCustomFilters(getUpToDateFilesFilter());

		setNeedsGenerationFilter(new CGFileFilter("needs_generation", GeneratorIconLibrary.GENERATE_CODE_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.needsMemoryGeneration();
			}
		});
		addToCustomFilters(getNeedsGenerationFilter());

		setGenerationErrorFilter(new CGFileFilter("generation_errors", IconLibrary.UNFIXABLE_ERROR_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus() == GenerationStatus.GenerationError;
			}
		});
		addToCustomFilters(getGenerationErrorFilter());

		setGenerationModifiedFilter(new CGFileFilter("generation_modified", UtilsIconLibrary.LEFT_MODIFICATION_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus().isGenerationModified()
						|| file.getGenerationStatus() == GenerationStatus.ConflictingMarkedAsMerged;
			}
		});
		addToCustomFilters(getGenerationModifiedFilter());

		setDiskModifiedFilter(new CGFileFilter("disk_modified", UtilsIconLibrary.RIGHT_MODIFICATION_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus().isDiskModified();
			}
		});
		addToCustomFilters(getDiskModifiedFilter());

		setUnresolvedConflictsFilter(new CGFileFilter("unresolved_conflicts", UtilsIconLibrary.CONFLICT_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus() == GenerationStatus.ConflictingUnMerged;
			}
		});
		addToCustomFilters(getUnresolvedConflictsFilter());

		setNeedsReinjectingFilter(new CGFileFilter("needs_model_reinjection", GeneratorIconLibrary.NEEDS_MODEL_REINJECTION_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.needsModelReinjection();
			}
		});
		addToCustomFilters(getNeedsReinjectingFilter());

		setOtherFilesFilter(new CGFileFilter("other_files", IconLibrary.UNFIXABLE_WARNING_ICON) {
			@Override
			public boolean acceptFile(CGFile file) {
				return file.getGenerationStatus().isAbnormal();
			}
		});
		addToCustomFilters(getOtherFilesFilter());

		// Element type filters
		setFilterStatus(BrowserElementType.FILE_RELEASE_VERSION, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
	}

	@Override
	public void update(FlexoObservable o, DataModification arg) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("GeneratorBrowser update");
		}
	}

	private CustomBrowserFilter allFilesAndDirectoryFilter;
	private CustomBrowserFilter upToDateFilesFilter;
	private CustomBrowserFilter needsGenerationFilter;
	private CustomBrowserFilter generationErrorFilter;
	private CustomBrowserFilter generationModifiedFilter;
	private CustomBrowserFilter diskModifiedFilter;
	private CustomBrowserFilter unresolvedConflictsFilter;
	private CustomBrowserFilter needsReinjectingFilter;
	private CustomBrowserFilter otherFilesFilter;

	public CustomBrowserFilter getAllFilesAndDirectoryFilter() {
		return allFilesAndDirectoryFilter;
	}

	public void setAllFilesAndDirectoryFilter(CustomBrowserFilter allFilesAndDirectoryFilter) {
		this.allFilesAndDirectoryFilter = allFilesAndDirectoryFilter;
	}

	public CustomBrowserFilter getDiskModifiedFilter() {
		return diskModifiedFilter;
	}

	public void setDiskModifiedFilter(CustomBrowserFilter diskModifiedFilter) {
		this.diskModifiedFilter = diskModifiedFilter;
	}

	public CustomBrowserFilter getGenerationErrorFilter() {
		return generationErrorFilter;
	}

	public void setGenerationErrorFilter(CustomBrowserFilter generationErrorFilter) {
		this.generationErrorFilter = generationErrorFilter;
	}

	public CustomBrowserFilter getGenerationModifiedFilter() {
		return generationModifiedFilter;
	}

	public void setGenerationModifiedFilter(CustomBrowserFilter generationModifiedFilter) {
		this.generationModifiedFilter = generationModifiedFilter;
	}

	public CustomBrowserFilter getNeedsGenerationFilter() {
		return needsGenerationFilter;
	}

	public void setNeedsGenerationFilter(CustomBrowserFilter needsGenerationFilter) {
		this.needsGenerationFilter = needsGenerationFilter;
	}

	public CustomBrowserFilter getOtherFilesFilter() {
		return otherFilesFilter;
	}

	public void setOtherFilesFilter(CustomBrowserFilter otherFilesFilter) {
		this.otherFilesFilter = otherFilesFilter;
	}

	public CustomBrowserFilter getUnresolvedConflictsFilter() {
		return unresolvedConflictsFilter;
	}

	public void setUnresolvedConflictsFilter(CustomBrowserFilter unresolvedConflictsFilter) {
		this.unresolvedConflictsFilter = unresolvedConflictsFilter;
	}

	public CustomBrowserFilter getUpToDateFilesFilter() {
		return upToDateFilesFilter;
	}

	public void setUpToDateFilesFilter(CustomBrowserFilter upToDateFilesFilter) {
		this.upToDateFilesFilter = upToDateFilesFilter;
	}

	public CustomBrowserFilter getNeedsReinjectingFilter() {
		return needsReinjectingFilter;
	}

	public void setNeedsReinjectingFilter(CustomBrowserFilter needsReinjectingFilter) {
		this.needsReinjectingFilter = needsReinjectingFilter;
	}

	public SGController getController() {
		return _controller;
	}

}
