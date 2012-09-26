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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.ws.FlexoPort;

/**
 * Widget allowing to select a Process while browsing the workflow
 * 
 * @author sguerin
 * 
 */
public class FlexoPortSelector extends AbstractBrowserSelector<FlexoPort> {

	protected static final String EMPTY_STRING = "";

	public FlexoPortSelector(FlexoProject project, FlexoPort port) {
		super(project, port, FlexoPort.class);

	}

	public FlexoPortSelector(FlexoProject project, FlexoPort port, int cols) {
		super(project, port, FlexoPort.class, cols);
	}

	@Override
	public FlexoProject getProject() {
		return super.getProject();
	}

	@Override
	protected FlexoPortSelectorPanel makeCustomPanel(FlexoPort editedObject) {
		return new FlexoPortSelectorPanel();
	}

	@Override
	public String renderedString(FlexoPort editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class FlexoPortSelectorPanel extends AbstractSelectorPanel<FlexoPort> {
		protected FlexoPortSelectorPanel() {
			super(FlexoPortSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new FlexoPortBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class FlexoPortBrowser extends ProjectBrowser {

		protected FlexoPortBrowser() {
			super(FlexoPortSelector.this.getProject(), false);
			init();
		}

		@Override
		public void configure() {
			setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.SUBPROCESS_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_AND_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_OR_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_IF_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.EVENT_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilterStatus.HIDE, true);

			setFilterStatus(BrowserElementType.SERVICE_INTERFACE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.SERVICE_OPERATION, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.MESSAGE_DEFINITION, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.WS_PORTTYPE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_PORTTYPE_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_REPOSITORY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_REPOSITORY_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_REPOSITORY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_PACKAGE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_LOOP_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.GROUP, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.SHOW, true);
			// setFilterStatus(BrowserElementType.PROCESS, BrowserFilter.ACTIVATE);

		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			// mis à jour via le parameter process=....
			return FlexoPortSelector.this.getProject().getFlexoWorkflow();
		}
	}

}
