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
package org.openflexo.dre;

import java.awt.Dimension;

import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.widget.AbstractBrowserSelector;
import org.openflexo.components.widget.AbstractSelectorPanel;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select a DocItem while browsing the DocResourceCenter
 * 
 * @author sguerin
 * 
 */
public class DocItemSelector extends AbstractBrowserSelector<DocItem> {

	protected static final String EMPTY_STRING = "";

	public DocItemSelector(FlexoProject project, DocItem docItem) {
		super(project, docItem, DocItem.class);
	}

	public DocItemSelector(FlexoProject project, DocItem docItem, int cols) {
		super(project, docItem, DocItem.class, cols);
	}

	@Override
	protected DocItemSelectorPanel makeCustomPanel(DocItem editedObject) {
		return new DocItemSelectorPanel();
	}

	@Override
	public String renderedString(DocItem editedObject) {
		if (editedObject != null) {
			return editedObject.getIdentifier();
		}
		return EMPTY_STRING;
	}

	protected class DocItemSelectorPanel extends AbstractSelectorPanel<DocItem> {
		protected DocItemSelectorPanel() {
			super(DocItemSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new DocItemBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.width = returned.width;
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class DocItemBrowser extends ConfigurableProjectBrowser {
		protected DocItemBrowser() {
			super(DREBrowser.makeBrowserConfiguration(DocItemSelector.this.getProject(), DocResourceManager.instance()
					.getDocResourceCenter()));
		}

	}

}
