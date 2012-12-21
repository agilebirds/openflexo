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
package org.openflexo.components.widget;

import java.awt.Dimension;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.ViewDefinition;

/**
 * Widget allowing to select a View while browsing the project
 * 
 * @author sguerin
 * 
 */
public class ViewSelector extends AbstractBrowserSelector<ViewDefinition> {

	protected static final String EMPTY_STRING = "";

	public ViewSelector(FlexoProject project, ViewDefinition view) {
		super(project, view, ViewDefinition.class);
	}

	public ViewSelector(FlexoProject project, ViewDefinition view, int cols) {
		super(project, view, ViewDefinition.class, cols);
	}

	@Override
	protected ViewSelectorPanel makeCustomPanel(ViewDefinition editedObject) {
		return new ViewSelectorPanel();
	}

	@Override
	public String renderedString(ViewDefinition editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class ViewSelectorPanel extends AbstractSelectorPanel<ViewDefinition> {
		protected ViewSelectorPanel() {
			super(ViewSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new ViewBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class ViewBrowser extends ProjectBrowser {

		protected ViewBrowser() {
			super(ViewSelector.this.getProject(), false);
			init();
		}

		@Override
		public void configure() {
			// setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.HIDE);
			// setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.HIDE);
			// setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.HIDE);
			// setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_CALC, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.OE_SHEMA_FOLDER, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.OE_SHEMA_DEFINITION, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.OE_SHEMA, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.OE_SHAPE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OE_CONNECTOR, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.WORKFLOW, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.IMPORTED_PROCESS_LIBRARY, BrowserFilterStatus.HIDE);
		}

		@Override
		public boolean showRootNode() {
			return false;
		}
	}

}
