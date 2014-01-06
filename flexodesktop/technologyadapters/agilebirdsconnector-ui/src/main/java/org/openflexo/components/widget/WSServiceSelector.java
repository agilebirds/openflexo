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
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.WSService;

/**
 * Widget allowing to select a Service while browsing the workflow
 * 
 * @author sguerin
 * 
 */
public class WSServiceSelector extends AbstractBrowserSelector<WSService> {

	protected static final String EMPTY_STRING = "";

	public static final int INTERNAL_WSService = 1;
	public static final int EXTERNAL_WSService = 2;
	public static final int ALL_WSService = 3;

	public WSServiceSelector(FlexoProject project, WSService group, int groupType) {
		super(project, group, WSService.class);
		wsServiceType = groupType;
	}

	public WSServiceSelector(FlexoProject project, WSService group, int groupType, int cols) {
		super(project, group, WSService.class, cols);
		wsServiceType = groupType;
	}

	int wsServiceType;

	FlexoWSLibrary getLibrary() {
		if (getProject() != null) {
			return getProject().getFlexoWSLibrary();
		}
		return null;
	}

	@Override
	protected WSServiceSelectorPanel makeCustomPanel(WSService editedObject) {
		return new WSServiceSelectorPanel();
	}

	@Override
	public String renderedString(WSService editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class WSServiceSelectorPanel extends AbstractSelectorPanel<WSService> {
		protected WSServiceSelectorPanel() {
			super(WSServiceSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new WSServiceBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class WSServiceBrowser extends ProjectBrowser {

		protected WSServiceBrowser() {
			super(getLibrary().getProject(), false);
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
			setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.MESSAGE_DEFINITION, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.WS_PORTTYPE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_PORTTYPE_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_REPOSITORY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_REPOSITORY_FOLDER, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_REPOSITORY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_PACKAGE, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.PROCESS, BrowserFilterStatus.HIDE);

		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			if (wsServiceType == INTERNAL_WSService) {
				return getLibrary().getInternalWSFolder();
			} else if (wsServiceType == EXTERNAL_WSService) {
				return getLibrary().getExternalWSFolder();
			} else {
				return getLibrary();
			}
		}
	}

}
