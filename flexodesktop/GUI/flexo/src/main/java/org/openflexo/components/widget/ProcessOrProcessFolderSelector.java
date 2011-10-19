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
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;


/**
 * Widget allowing to select a Process while browsing the workflow
 *
 * @author sguerin
 *
 */
public class ProcessOrProcessFolderSelector extends AbstractBrowserSelector<RepresentableFlexoModelObject>
{

	protected static final String EMPTY_STRING = "";

	public ProcessOrProcessFolderSelector(FlexoProject project, FlexoProcess process)
	{
		this(project, (RepresentableFlexoModelObject)process, -1);
	}

	public ProcessOrProcessFolderSelector(FlexoProject project, FlexoProcess process, int cols)
	{
		this(project, (RepresentableFlexoModelObject)process, cols);
	}

	public ProcessOrProcessFolderSelector(FlexoProject project, ProcessFolder folder)
	{
		this(project, (RepresentableFlexoModelObject)folder, -1);
	}

	public ProcessOrProcessFolderSelector(FlexoProject project, ProcessFolder folder, int cols)
	{
		this(project, (RepresentableFlexoModelObject)folder, cols);
	}

	private ProcessOrProcessFolderSelector(FlexoProject project, RepresentableFlexoModelObject obj, int cols)
	{
		super(project, obj, RepresentableFlexoModelObject.class, cols);
	}

	@Override
	public boolean isSelectable(FlexoModelObject object)
	{
		return object instanceof FlexoProcess || object instanceof ProcessFolder;
	}

	@Override
	protected ProcessSelectorPanel makeCustomPanel(RepresentableFlexoModelObject editedObject)
	{
		return new ProcessSelectorPanel();
	}

	@Override
	public String renderedString(RepresentableFlexoModelObject editedObject)
	{
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class ProcessSelectorPanel extends AbstractSelectorPanel<RepresentableFlexoModelObject>
	{
		protected ProcessSelectorPanel()
		{
			super(ProcessOrProcessFolderSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project)
		{
			return new ProcessBrowser();
		}

		@Override
		public Dimension getDefaultSize()
		{
			Dimension returned = _browserView.getDefaultSize();
			returned.width = returned.width;
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class ProcessBrowser extends ProjectBrowser
	{

		protected ProcessBrowser()
		{
			super(ProcessOrProcessFolderSelector.this.getProject(), false);
			init();
		}

		@Override
		public void configure()
		{
			setFilterStatus(BrowserElementType.PRECONDITION, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.POSTCONDITION, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ROLE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.STATUS, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DEADLINE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ACTIVITY_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ACTION_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATION_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.SUBPROCESS_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_AND_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_OR_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_IF_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_COMPLEX_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_EXCLUSIVE_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_INCLUSIVE_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_SWITCH_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OPERATOR_LOOP_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.EVENT_NODE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PORT_REGISTERY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.SERVICE_INTERFACE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.GROUP, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.COMPONENT_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.MENU_ITEM, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.CALC_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.OE_SHEMA_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.ONTOLOGY_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.WS_LIBRARY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.PROCESS_FOLDER, BrowserFilterStatus.SHOW,true);
		}

		@Override
		public FlexoModelObject getDefaultRootObject()
		{
			return getProject();
		}

		@Override
		public boolean showRootNode() {
			return false;
		}
	}

}
