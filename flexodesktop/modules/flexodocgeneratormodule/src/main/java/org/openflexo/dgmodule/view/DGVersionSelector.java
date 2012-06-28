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
package org.openflexo.dgmodule.view;

import java.awt.Dimension;

import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.components.widget.AbstractBrowserSelector;
import org.openflexo.components.widget.AbstractSelectorPanel;
import org.openflexo.dgmodule.controller.browser.DGBrowser;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select a AbstractCGFileVersion for a given CGFile
 * 
 * @author sguerin
 * 
 */
public class DGVersionSelector extends AbstractBrowserSelector<AbstractCGFileVersion> {

	protected static final String EMPTY_STRING = "";
	protected CGFile file;

	public DGVersionSelector(CGFile aFile, AbstractCGFileVersion version) {
		super(aFile != null ? aFile.getProject() : null, version, AbstractCGFileVersion.class);
		setFile(aFile);
	}

	public DGVersionSelector(CGFile aFile, AbstractCGFileVersion version, int cols) {
		super(aFile.getProject(), version, AbstractCGFileVersion.class, cols);
		setFile(aFile);
	}

	@Override
	public void delete() {
		super.delete();
		setFile(null);
	}

	@Override
	protected DGVersionSelectorPanel makeCustomPanel(AbstractCGFileVersion editedObject) {
		return new DGVersionSelectorPanel();
	}

	@Override
	public String renderedString(AbstractCGFileVersion editedObject) {
		if (editedObject != null) {
			return editedObject.getStringRepresentation();
		}
		return EMPTY_STRING;
	}

	@Override
	public DGVersionSelectorPanel getSelectorPanel() {
		return (DGVersionSelectorPanel) super.getSelectorPanel();
	}

	protected class DGVersionSelectorPanel extends AbstractSelectorPanel<AbstractCGFileVersion> {
		protected DGVersionSelectorPanel() {
			super(DGVersionSelector.this);
		}

		@Override
		protected CGVersionBrowser createBrowser(FlexoProject project) {
			return new CGVersionBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.width = returned.width;
			returned.height = returned.height - 100;
			return returned;
		}

		@Override
		public CGVersionBrowser getBrowser() {
			return (CGVersionBrowser) super.getBrowser();
		}
	}

	protected class CGVersionBrowser extends ConfigurableProjectBrowser {
		protected CGVersionBrowser() {
			super(DGBrowser.makeBrowserConfigurationForFileHistory(file));
		}

	}

	public CGFile getFile() {
		return file;
	}

	public void setFile(CGFile aFile) {
		file = aFile;
		if (getSelectorPanel() != null) {
			if (getSelectorPanel().getBrowser() != null) {
				getSelectorPanel().getBrowser().setRootObject(aFile);
			}
		}
	}

}
