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
package org.openflexo.dgmodule.controller.browser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.doceditor.controller.browser.DocTypeElement;
import org.openflexo.doceditor.controller.browser.TOCEntryElement;
import org.openflexo.doceditor.controller.browser.TOCRepositoryElement;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGPathElement;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGTemplateSet;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.version.CGFileIntermediateVersion;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;

/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class DGBrowser extends ProjectBrowser implements FlexoObserver {
	// GPO: DGBrowser does not extends DEBrowser because they are
	// different browsers (even though, they have some objects from the model in
	// common, they are not displayed the same way at all).
	private static final Logger logger = Logger.getLogger(DGBrowser.class.getPackage().getName());

	protected abstract class CGFileFilter extends CustomBrowserFilter {
		public CGFileFilter(String name, Icon icon, GenerationStatus... status) {
			super(name, icon);
		}

		@Override
		public boolean accept(FlexoModelObject object) {
			if (object instanceof CGFile) {
				return acceptFile((CGFile) object);
			}
			if (object instanceof CGPathElement) {
				return acceptCGPathElement((CGPathElement) object);
			}
			return true;
		}

		public boolean acceptCGPathElement(CGPathElement pathElement) {
			for (CGFile file : new ArrayList<CGFile>(pathElement.getFiles())) {
				if (acceptFile(file)) {
					return true;
				}
			}
			for (CGPathElement folder : new ArrayList<CGPathElement>(pathElement.getSubFolders())) {
				if (acceptCGPathElement(folder)) {
					return true;
				}
			}
			return false;
		}

		public abstract boolean acceptFile(CGFile file);

	}

	class GeneratorBrowserConfigurationElementFactory implements BrowserElementFactory {

		GeneratorBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
			if (object instanceof GeneratedDoc) {
				return new GeneratedDocElement((GeneratedDoc) object, browser, parent);
			} else if (object instanceof GenerationRepository) {
				return new GeneratedDocRepositoryElement((GenerationRepository) object, browser, parent);
			} else if (object instanceof CGSymbolicDirectory) {
				return new GeneratedDocSymbDirElement((CGSymbolicDirectory) object, browser, parent);
			} else if (object instanceof CGFolder) {
				return new GeneratedDocFolderElement((CGFolder) object, browser, parent);
			} else if (object instanceof CGFile) {
				return new GeneratedDocFileElement((CGFile) object, browser, parent);
			} else if (object instanceof CGFileReleaseVersion) {
				return new FileReleaseVersionElement((CGFileReleaseVersion) object, browser, parent);
			} else if (object instanceof CGFileIntermediateVersion) {
				return new FileIntermediateVersionElement((CGFileIntermediateVersion) object, browser, parent);
			} else if (object instanceof CGTemplates) {
				return new DGTemplatesElement((CGTemplates) object, browser, parent);
			} else if (object instanceof CGTemplateRepository) {
				return new DGTemplateRepositoryElement((CGTemplateRepository) object, browser, parent);
			} else if (object instanceof CGTemplateSet) {
				return new DGTemplateSetElement((CGTemplateSet) object, browser, parent);
			} else if (object instanceof CGTemplate) {
				return new DGTemplateFileElement((CGTemplate) object, browser, parent);
			} else if (object instanceof TOCRepository) {
				return new TOCRepositoryElement((TOCRepository) object, browser, parent);
			} else if (object instanceof TOCEntry) {
				return new TOCEntryElement((TOCEntry) object, browser, parent);
			} else if (object instanceof DocType) {
				return new DocTypeElement(object, browser, parent);
			}
			return null;
		}

	}

	public DGBrowser(DGController controller) {
		super(controller);
		setBrowserElementFactory(new GeneratorBrowserConfigurationElementFactory());
	}

	@Override
	public void configure() {
		// Custom filters
		setAllFilesAndDirectoryFilter(new CustomBrowserFilter("all_files_and_directories", null) {
			@Override
			public boolean accept(FlexoModelObject object) {
				return true;
			}
		});
		addToCustomFilters(getAllFilesAndDirectoryFilter());

		setUpToDateFilesFilter(new CGFileFilter("up_to_date_files", FilesIconLibrary.SMALL_UNKNOWN_FILE_ICON) {
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

}
