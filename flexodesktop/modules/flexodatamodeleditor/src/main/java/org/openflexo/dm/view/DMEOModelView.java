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
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMEOAttributeTableModel;
import org.openflexo.dm.model.DMEOEntityTableModel;
import org.openflexo.dm.model.DMEORelationshipTableModel;
import org.openflexo.dm.model.DMMethodTableModel;
import org.openflexo.dm.model.DMPropertyTableModel;
import org.openflexo.dm.model.DMReadOnlyEOAttributeTableModel;
import org.openflexo.dm.model.DMReadOnlyEOEntityTableModel;
import org.openflexo.dm.model.DMReadOnlyMethodTableModel;
import org.openflexo.dm.model.DMReadOnlyPropertyTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.action.CreateDMEOAttribute;
import org.openflexo.foundation.dm.action.CreateDMEOEntity;
import org.openflexo.foundation.dm.action.CreateDMEORelationship;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOModelView extends DMView<DMEOModel>
{

    private DMEOEntityTableModel eoEntityTableModel;
    protected DMTabularView eoEntityTable;

    private DMPropertyTableModel propertyTableModel;
    protected DMTabularView propertyTable;

    private DMEOAttributeTableModel eoAttributeTableModel;
    protected DMTabularView eoAttributeTable;

    private DMEORelationshipTableModel eoRelationshipTableModel;
    protected DMTabularView eoRelationshipTable;

    private DMMethodTableModel methodTableModel;
    protected DMTabularView methodTable;

    protected JTabbedPane tabbedPane;

    public DMEOModelView(DMEOModel anEOModel, DMController controller)
    {
        super(anEOModel, controller, "eomodel_($name)");

        addAction(new TabularViewAction(CreateDMEOEntity.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMEOModel();
            }           
        });

         addAction(new TabularViewAction(CreateDMProperty.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                if (tabbedPane.getSelectedIndex() == 1) return getSelectedDMEOEntity();
                return null;
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
                if (tabbedPane.getSelectedIndex() == 1) return getSelectedDMEOEntity();
                return null;
            }           
        });

        addAction(new TabularViewAction(CreateDMEOAttribute.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                if (tabbedPane.getSelectedIndex() == 0) return getSelectedDMEOEntity();
                return null;
            }           
        });

        addAction(new TabularViewAction(CreateDMEORelationship.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                if (tabbedPane.getSelectedIndex() == 0) return getSelectedDMEOEntity();
                return null;
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
        tabbedPane = new JTabbedPane();
        
        if (getDMEOModel().getRepository().isReadOnly())
            eoEntityTableModel = new DMReadOnlyEOEntityTableModel(getDMEOModel(), getDMController().getProject());
        else
            eoEntityTableModel = new DMEOEntityTableModel(getDMEOModel(), getDMController().getProject());
        
        addToMasterTabularView(eoEntityTable = new DMTabularView(getDMController(), eoEntityTableModel, 10));
        eoEntityTable.setName("EOEntityTable");
        if (getDMEOModel().getRepository().isReadOnly())
            eoAttributeTableModel = new DMReadOnlyEOAttributeTableModel(null, getDMController());
        else
            eoAttributeTableModel = new DMEOAttributeTableModel(null, getDMController());
        addToSlaveTabularView(eoAttributeTable = new DMTabularView(getDMController(), eoAttributeTableModel, 7), eoEntityTable);
        eoAttributeTable.setName("EOAttributeTable");
        eoRelationshipTableModel = new DMEORelationshipTableModel(null, getDMController());
        addToSlaveTabularView(eoRelationshipTable = new DMTabularView(getDMController(), eoRelationshipTableModel, 7), eoEntityTable);
        eoRelationshipTable.setName("EORelationshipTable");
        tabbedPane.add(FlexoLocalization.localizedForKey("database_design"),new JSplitPane(JSplitPane.VERTICAL_SPLIT, eoAttributeTable, eoRelationshipTable)); 
        if (getDMEOModel().getRepository().isReadOnly())
        	propertyTableModel = new DMReadOnlyPropertyTableModel(null, getDMController().getProject());
        else
        	propertyTableModel = new DMPropertyTableModel(null, getDMController().getProject());
        addToSlaveTabularView(propertyTable = new DMTabularView(getDMController(), propertyTableModel, 7), eoEntityTable);
        if (getDMEOModel().getRepository().isReadOnly())
            methodTableModel = new DMReadOnlyMethodTableModel(null, getDMController().getProject());
        else
            methodTableModel = new DMMethodTableModel(null, getDMController().getProject());
        addToSlaveTabularView(methodTable = new DMTabularView(getDMController(), methodTableModel, 7), eoEntityTable);
 
        tabbedPane.add(FlexoLocalization.localizedForKey("common_design"),new JSplitPane(JSplitPane.VERTICAL_SPLIT, propertyTable, methodTable)); 
        
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
			public void stateChanged(ChangeEvent e) {
                updateControls();
             }
        });
        
        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, eoEntityTable, tabbedPane);
    }

    public DMEOModel getDMEOModel()
    {
        return getDMObject();
    }

    public DMEOEntity getSelectedDMEOEntity()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMEOEntity)) {
            return (DMEOEntity) selection.firstElement();
        }
        if (getSelectedDMProperty() != null) {
            if (getSelectedDMProperty().getEntity() instanceof DMEOEntity)
                return (DMEOEntity) getSelectedDMProperty().getEntity();
            return null;
        }
        if (getSelectedDMMethod() != null) {
            if (getSelectedDMMethod().getEntity() instanceof DMEOEntity)
                return (DMEOEntity) getSelectedDMMethod().getEntity();
            return null;
        }
        if (getSelectedDMEOAttribute() != null) {
            return getSelectedDMEOAttribute().getDMEOEntity();
        }
        if (getSelectedDMEORelationship() != null) {
            return getSelectedDMEORelationship().getDMEOEntity();
        }
        return null;
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

    public DMEOAttribute getSelectedDMEOAttribute()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMEOAttribute)) {
            return (DMEOAttribute) selection.firstElement();
        }
        return null;
    }

    public DMEORelationship getSelectedDMEORelationship()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMEORelationship)) {
            return (DMEORelationship) selection.firstElement();
        }
        return null;
    }

    public DMTabularView getEoEntityTable()
    {
        return eoEntityTable;
    }

    public DMTabularView getEoAttributeTable() 
    {
        return eoAttributeTable;
    }

    public DMTabularView getEoRelationshipTable()
    {
        return eoRelationshipTable;
    }

    public DMTabularView getMethodTable()
    {
        return methodTable;
    }

    public DMTabularView getPropertyTable() 
    {
        return propertyTable;
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
