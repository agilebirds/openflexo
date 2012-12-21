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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.foundation.wkf.ws.ServiceInterface;

/**
 * selector of either a ServiceInterface, either a Process (defaultServiceInterface), either a defaultServiceInterface (=portRegistry)
 * 
 * 
 * @author dvanvyve
 * 
 */
public class ServiceInterfaceSelector extends AbstractBrowserSelector<WKFObject> {

	protected static final String EMPTY_STRING = "";

	public ServiceInterfaceSelector(FlexoProject project, WKFObject object) {
		super(project, object, WKFObject.class);
	}

	public ServiceInterfaceSelector(FlexoProject project, WKFObject object, int cols) {
		super(project, object, WKFObject.class, cols);
	}

	FlexoWorkflow getWorkflow() {
		if (getProject() != null) {
			return getProject().getFlexoWorkflow();
		}
		return null;
	}

	@Override
	protected WKFObjectSelectorPanel makeCustomPanel(WKFObject editedObject) {
		return new WKFObjectSelectorPanel();
	}

	@Override
	public String renderedString(WKFObject editedObject) {
		if (editedObject != null) {
			if (editedObject instanceof FlexoProcess) {
				return ((FlexoProcess) editedObject).getName();
			} else if (editedObject instanceof ServiceInterface) {
				return ((ServiceInterface) editedObject).getName();
			} else if (editedObject instanceof PortRegistery) {
				return ((PortRegistery) editedObject).getName();
			}
		}
		return EMPTY_STRING;
	}

	protected class WKFObjectSelectorPanel extends AbstractSelectorPanel<WKFObject> {
		protected WKFObjectSelectorPanel() {
			super(ServiceInterfaceSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new WKFObjectBrowser();
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class WKFObjectBrowser extends ProjectBrowser {

		protected WKFObjectBrowser() {
			super(getWorkflow().getProject(), false);
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
			setFilterStatus(BrowserElementType.OPERATOR_LOOP_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.GROUP, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.EVENT_NODE, BrowserFilterStatus.HIDE);

			setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROJECT, BrowserFilterStatus.HIDE, true);
			setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.SHOW, true);
			// setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilter.ACTIVATE);
			// no DefaultServiceInterface Element yet.... So we show the portRegistry instead...
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getProject();
		}

		@Override
		public boolean showRootNode() {
			return false;
		}
	}

	@Override
	public boolean isSelectable(FlexoObject object) {
		if (object instanceof FlexoProcess) {
			FlexoProcess p = (FlexoProcess) object;
			return !p.isImported() || p.isTopLevelProcess();
		}
		if (object instanceof PortRegistery || object instanceof ServiceInterface) {
			return true;
		}
		return false;
	}

}
