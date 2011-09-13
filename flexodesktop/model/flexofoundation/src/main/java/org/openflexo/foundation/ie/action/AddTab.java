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
package org.openflexo.foundation.ie.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;


public class AddTab extends FlexoAction<AddTab,IEWidget,IEWidget>
{
    private String tabName;

    private FlexoComponentFolder aFolder;

    private TabComponentDefinition tabDef;

    private IESequenceTab tabContainer;

    private String tabTitle;
    
    private int tabIndex=-1;

    
    public static FlexoActionType<AddTab,IEWidget,IEWidget> actionType
    = new FlexoActionType<AddTab,IEWidget,IEWidget>(
    		"add_tab", 
    		FlexoActionType.defaultGroup, 
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddTab makeNewAction(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor)
        {
            return new AddTab(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(IEWidget object, Vector<IEWidget> globalSelection)
        {
            return globalSelection != null && globalSelection.size() == 1;
        }

        @Override
		protected boolean isEnabledForSelection(IEWidget object, Vector<IEWidget> globalSelection)
        {
            return ((object != null) && ((object instanceof IESequenceTab)||(object instanceof IETabWidget)));
        }

    };

    AddTab(IEWidget focusedObject, Vector<IEWidget> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws FlexoException
    {
        if (tabDef == null) {
                try {
					tabDef = new TabComponentDefinition(tabName, aFolder.getComponentLibrary(), aFolder.getComponentLibrary()
					        .getRootFolder().getFolderTyped(FolderType.TAB_FOLDER), aFolder.getProject());
				} catch (DuplicateResourceException e) {
					throw new FlexoException("A tab named :"+tabName+" has already been created in this project. Please choose another name",e);
				}
        }
        tabContainer.addNewTab(tabDef, tabTitle,tabIndex);
    }

    public void setFolder(FlexoComponentFolder folder)
    {
        aFolder = folder;
    }

    public void setTabContainer(IESequenceTab tab_container)
    {
        this.tabContainer = tab_container;
    }

    public void setTabName(String tab_name)
    {
        this.tabName = tab_name;
    }

    public void setTabDef(TabComponentDefinition tabdef)
    {
        this.tabDef = tabdef;
    }

    public TabComponentDefinition getTabDef()
    {
        return tabDef;
    }

    public String getTabTitle()
    {
        return tabTitle;
    }

    public void setTabTitle(String tab_title)
    {
        this.tabTitle = tab_title;
    }

	public void setTabIndex(int i) {
		this.tabIndex = i;
	}

}
