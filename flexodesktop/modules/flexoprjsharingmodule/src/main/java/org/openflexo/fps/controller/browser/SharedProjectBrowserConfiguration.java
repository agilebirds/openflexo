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
package org.openflexo.fps.controller.browser;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.fps.CVSContainer;
import org.openflexo.fps.CVSDirectory;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.SharedProject;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;

class SharedProjectBrowserConfiguration implements BrowserConfiguration {
	private final SharedProject _project;
	private final SharedProjectBrowserConfigurationElementFactory _factory;

	protected SharedProjectBrowserConfiguration(SharedProject project) {
		super();
		_project = project;
		_factory = new SharedProjectBrowserConfigurationElementFactory();
	}

	@Override
	public FlexoProject getProject() {
		return null;
	}

	protected abstract class FPSFileFilter extends CustomBrowserFilter {
		public FPSFileFilter(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public boolean accept(FlexoModelObject object) {
			if (object instanceof CVSFile) {
				return acceptFile((CVSFile) object);
			}
			if (object instanceof CVSContainer) {
				return acceptCVSContainer((CVSContainer) object);
			}
			return true;
		}

		public boolean acceptCVSContainer(CVSContainer container) {
			for (CVSFile file : container.getFiles()) {
				if (acceptFile(file)) {
					return true;
				}
			}
			for (CVSDirectory dir : container.getDirectories()) {
				if (acceptCVSContainer(dir)) {
					return true;
				}
			}
			return false;
		}

		public abstract boolean acceptFile(CVSFile file);

	}

	@Override
	public void configure(ProjectBrowser aBrowser) {
		SharedProjectBrowser browser = (SharedProjectBrowser) aBrowser;

		// Custom filters
		browser.setAllFilesAndDirectoryFilter(new FPSFileFilter("all_files_and_directories", null) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return !file.getStatus().isIgnored();
			}
		});
		browser.addToCustomFilters(browser.getAllFilesAndDirectoryFilter());

		browser.setUpToDateFilesFilter(new FPSFileFilter("up_to_date_files", FilesIconLibrary.SMALL_MISC_FILE_ICON) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return file.getStatus().isUpToDate();
			}
		});
		browser.addToCustomFilters(browser.getUpToDateFilesFilter());

		browser.setLocallyModifiedFilter(new FPSFileFilter("locally_modified_files", UtilsIconLibrary.LEFT_MODIFICATION_ICON) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return (file.getStatus().isLocallyModified());
			}
		});
		browser.addToCustomFilters(browser.getLocallyModifiedFilter());

		browser.setRemotelyModifiedFilter(new FPSFileFilter("remotely_modified_files", UtilsIconLibrary.RIGHT_MODIFICATION_ICON) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return (file.getStatus().isRemotelyModified());
			}
		});
		browser.addToCustomFilters(browser.getRemotelyModifiedFilter());

		browser.setConflictingFileFilter(new FPSFileFilter("unresolved_conflicting_files", UtilsIconLibrary.CONFLICT_ICON) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return file.getStatus().isConflicting();
			}
		});
		browser.addToCustomFilters(browser.getConflictingFileFilter());

		;
		browser.setIgnoredFileFilter(new FPSFileFilter("ignored_files", IconFactory.getImageIcon(FilesIconLibrary.SMALL_MISC_FILE_ICON,
				IconLibrary.QUESTION)) {
			@Override
			public boolean acceptFile(CVSFile file) {
				return file.getStatus().isIgnored();
			}
		});
		browser.addToCustomFilters(browser.getIgnoredFileFilter());

	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		return _project;
	}

	@Override
	public BrowserElementFactory getBrowserElementFactory() {
		return _factory;
	}

	class SharedProjectBrowserConfigurationElementFactory implements BrowserElementFactory {

		SharedProjectBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
			if (object instanceof SharedProject) {
				return new SharedProjectElement((SharedProject) object, browser, parent);
			} else if (object instanceof CVSDirectory) {
				return new CVSDirectoryElement((CVSDirectory) object, browser, parent);
			} else if (object instanceof CVSFile) {
				return new CVSFileElement((CVSFile) object, browser, parent);
			}
			return null;
		}

	}
}