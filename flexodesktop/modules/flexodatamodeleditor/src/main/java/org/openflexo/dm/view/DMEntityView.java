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
package org.openflexo.dm.view;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMMethodTableModel;
import org.openflexo.dm.model.DMPropertyTableModel;
import org.openflexo.dm.model.DMReadOnlyMethodTableModel;
import org.openflexo.dm.model.DMReadOnlyPropertyTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.action.DMDelete;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEntityView extends DMView<DMEntity>
{

    private DMPropertyTableModel propertyTableModel;
    protected DMTabularView propertyTable;

    private DMMethodTableModel methodTableModel;
    protected DMTabularView methodTable;

    public DMEntityView(DMEntity entity, DMController controller)
    {
        super(entity, controller, "entity_($name)");
        addAction(new TabularViewAction(CreateDMProperty.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMEntity();
            }           
        });
        addAction(new TabularViewAction(CreateDMMethod.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMEntity();
            }           
        });
        addAction(new TabularViewAction(DMDelete.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                 return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return null;
            }           
        });
         finalizeBuilding();
    }

    @Override
	protected JComponent buildContentPane()
    {
        DMEntity entity = getDMObject();
        if (getDMEntity().getRepository().isReadOnly())
        	propertyTableModel = new DMReadOnlyPropertyTableModel(entity, getDMController().getProject());
        else
        	propertyTableModel = new DMPropertyTableModel(entity, getDMController().getProject());
        addToMasterTabularView(propertyTable = new DMTabularView(getDMController(), propertyTableModel, 15));
        if (getDMEntity().getRepository().isReadOnly())
            methodTableModel = new DMReadOnlyMethodTableModel(entity, getDMController().getProject());
        else
            methodTableModel = new DMMethodTableModel(entity, getDMController().getProject());
        addToMasterTabularView(methodTable = new DMTabularView(getDMController(), methodTableModel, 15));

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, propertyTable, methodTable);
    }

    public DMEntity getDMEntity()
    {
        return getDMObject();
    }

    public DMProperty getSelectedDMProperty()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMProperty)) {
            return (DMProperty) selection.firstElement();
        }
        return null;
    }

    public DMMethod getSelectedDMMethod()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMMethod)) {
            return (DMMethod) selection.firstElement();
        }
        return null;
    }

    public DMTabularView getPropertyTable() 
    {
        return propertyTable;
    }

    public DMTabularView getMethodTable() 
    {
        return methodTable;
    }

    /**
     * Overrides willShow
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * Overrides willHide
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
        // TODO Auto-generated method stub
        
    }


}
