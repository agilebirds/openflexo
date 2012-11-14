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
package org.openflexo.sgmodule.view.popup;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFolder;
import org.openflexo.foundation.cg.CGPathElement;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.SGIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.browser.CGFileElement;
import org.openflexo.sgmodule.controller.browser.CGFolderElement;

public class SelectFilesPopup extends MultipleObjectSelectorPopup {

	static final Logger logger = Logger.getLogger(SelectFilesPopup.class.getPackage().getName());

	public SelectFilesPopup(String label, String description, String unlocalizedValidateButtonLabel, Vector<AbstractCGFile> files,
			FlexoProject project, SGController controller) {
		super(FlexoLocalization.localizedForKey("file_selection"), label, description, unlocalizedValidateButtonLabel,
				new SelectFilesPopupBrowserConfiguration(files, project), project, controller.getFlexoFrame(), controller.getEditor());
		choicePanel.setSelectedObjects(getFileSet()._allObjects);
	}

	@Override
	protected String getPopupTitle() {
		if (super.getPopupTitle() == null) {
			return FlexoLocalization.localizedForKey("file_selection");
		} else {
			return super.getPopupTitle();
		}
	}

	protected JPanel _additionalPanel = null;
	protected Hashtable<String, Object> params;

	public Object getParam(String key) {
		return getParams().get(key);
	}

	public void setParam(String key, Object value) {
		getParams().put(key, value);
	}

	private Hashtable<String, Object> getParams() {
		if (params == null) {
			params = new Hashtable<String, Object>();
		}
		return params;
	}

	@Override
	public JPanel getAdditionalPanel() {
		return _additionalPanel;
	}

	public CGFilesSet getFileSet() {
		return ((SelectFilesPopupBrowserConfiguration) getBrowserConfiguration()).fileSet;
	}

	public static class CGFilesSet extends TemporaryFlexoModelObject {
		protected Hashtable<CGSymbolicDirectory, CGFolder> _symbDirs;
		private Vector<AbstractCGFile> _selectedFiles;
		protected Vector<FlexoModelObject> _allObjects;

		public CGFilesSet(Vector<AbstractCGFile> files) {
			super();
			_symbDirs = new Hashtable<CGSymbolicDirectory, CGFolder>();
			_selectedFiles = new Vector<AbstractCGFile>();
			_allObjects = new Vector<FlexoModelObject>();
			for (AbstractCGFile file : files) {
				add(file);
				_selectedFiles.add(file);
			}
			_allObjects.add(this);
		}

		public Vector<AbstractCGFile> getSelectedFiles() {
			return _selectedFiles;
		}

		public void setSelectedFiles(Vector<AbstractCGFile> someSelectedFiles) {
			_selectedFiles = someSelectedFiles;
		}

		private String getRelativePathFrom(CGSymbolicDirectory symbDir, FlexoProjectFile aFile) {
			if (aFile.getExternalRepository() == symbDir.getDirectory().getExternalRepository() && aFile.getExternalRepository() != null) {
				String symbDirPath = symbDir.getDirectory().getRelativePath();
				String searchedPath = aFile.getRelativePath();
				if (searchedPath.indexOf(symbDirPath) > -1) {
					return searchedPath.substring(searchedPath.indexOf(symbDirPath) + symbDirPath.length());
				}
			}
			return null;
		}

		protected void add(CGFile file) {
			CGSymbolicDirectory symbDir = file.getSymbolicDirectory();

			if (_symbDirs.get(symbDir) == null) {
				CGFolder newFolder = new CGFolder(file.getRepository(), symbDir.getName(), null);
				_symbDirs.put(symbDir, newFolder);
				_allObjects.add(newFolder);
			}
			CGFolder symbDirFolder = _symbDirs.get(file.getSymbolicDirectory());
			FlexoProjectFile projectFile = file.getResource().getResourceFile();
			if (projectFile != null) {
				String relativePath = getRelativePathFrom(symbDir, projectFile);
				if (relativePath != null) {
					StringTokenizer st = new StringTokenizer(relativePath, "/");
					CGPathElement parent = symbDirFolder;
					String dirName;
					while (st.hasMoreTokens() && (dirName = st.nextToken()) != null && st.hasMoreTokens()) {
						if (parent.getDirectoryNamed(dirName) == null) {
							CGFolder newFolder = new CGFolder(file.getRepository(), dirName, parent);
							parent.getSubFolders().add(newFolder);
							_allObjects.add(newFolder);
						}
						if (file.isEnabled()) {
							parent.getDirectoryNamed(dirName).setEnabled(true);
						}
						parent = parent.getDirectoryNamed(dirName);
					}
					if (file.isEnabled()) {
						symbDirFolder.setEnabled(true);
					}
					file.setParent(parent);
					parent.getFiles().add(file);
				} else {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("relative path: " + relativePath + " for file : " + projectFile.getStringRepresentation());
					}
				}
			}
			_allObjects.add(file);
		}

	}

	protected static class SelectFilesPopupBrowserConfiguration implements BrowserConfiguration {
		protected CGFilesSet fileSet;
		private final SelectFilesBrowserElementFactory _factory;
		private final FlexoProject _project;

		protected SelectFilesPopupBrowserConfiguration(Vector<AbstractCGFile> files, FlexoProject project) {
			_project = project;
			fileSet = new CGFilesSet(files);
			_factory = new SelectFilesBrowserElementFactory();
		}

		@Override
		public FlexoProject getProject() {
			return _project;
		}

		@Override
		public void configure(ProjectBrowser browser) {
		}

		@Override
		public CGFilesSet getDefaultRootObject() {
			return fileSet;
		}

		@Override
		public BrowserElementFactory getBrowserElementFactory() {
			return _factory;
		}

		class SelectFilesBrowserElementFactory implements BrowserElementFactory {
			@Override
			public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent) {
				if (object instanceof CGFilesSet) {
					return new CGFilesSetElement((CGFilesSet) object, browser, parent);
				} else if (object instanceof CGFolder) {
					return new CGFolderElement((CGFolder) object, browser, parent) {
						@Override
						public ImageIcon getBaseIcon() {
							if (getFolder().getParent() == null) {
								return SGIconLibrary.SYMBOLIC_FOLDER_ICON;
							}
							return IconLibrary.FOLDER_ICON;
						}
					};
				} else if (object instanceof CGFile) {
					return new CGFileElement((CGFile) object, browser, parent);
				}
				return null;
			}

			private class CGFilesSetElement extends BrowserElement {
				public CGFilesSetElement(CGFilesSet fileSet, ProjectBrowser browser, BrowserElement parent) {
					super(fileSet, BrowserElementType.UNDEFINED_FOLDER, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					for (CGFolder folder : ((CGFilesSet) getObject())._symbDirs.values()) {
						addToChilds(folder);
					}
				}

				@Override
				public String getName() {
					return FlexoLocalization.localizedForKey("selected_files");
				}
			}

		}

	}

	@Override
	public void performConfirm() {
		super.performConfirm();
		Vector<AbstractCGFile> selectedFiles = new Vector<AbstractCGFile>();
		for (FlexoModelObject o : choicePanel.getSelectedObjects()) {
			if (o instanceof AbstractCGFile) {
				selectedFiles.add((AbstractCGFile) o);
			}
		}
		getFileSet().setSelectedFiles(selectedFiles);
	}

}
