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
import org.openflexo.dm.model.DMEntityTableModel;
import org.openflexo.dm.model.DMPackageTableModel;
import org.openflexo.dm.model.DMReadOnlyEntityTableModel;
import org.openflexo.dm.model.DMReadOnlyPackageTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.action.CreateDMEntity;
import org.openflexo.foundation.dm.action.CreateDMPackage;
import org.openflexo.foundation.dm.action.DMDelete;


/**
 * View allowing to represent/edit a DMRepository object
 * 
 * @author sguerin
 * 
 */
public class DMRepositoryView extends DMView<DMRepository>
{

    private DMPackageTableModel packageTableModel;

    protected DMTabularView packageTable;

    private DMEntityTableModel entityTableModel;

    protected DMTabularView entityTable;

    public DMRepositoryView(DMRepository repository, DMController controller)
    {
        super(repository, controller, "repository_($name)");
        addAction(new TabularViewAction(CreateDMPackage.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMRepository();
            }           
        });
        addAction(new TabularViewAction(CreateDMEntity.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getSelectedDMPackage();
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
        DMRepository repository = getDMObject();
        if (repository.isReadOnly())
            packageTableModel = new DMReadOnlyPackageTableModel(repository,getDMController().getProject());
        else 
            packageTableModel = new DMPackageTableModel(repository, getDMController().getProject());
        addToMasterTabularView(packageTable = new DMTabularView(getDMController(), packageTableModel, 10));
        if (repository.isReadOnly())
            entityTableModel = new DMReadOnlyEntityTableModel(null, getDMController().getProject());
        else
            entityTableModel = new DMEntityTableModel(null, getDMController().getProject());
        addToSlaveTabularView(entityTable = new DMTabularView(getDMController(), entityTableModel, 15),packageTable);

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, packageTable, entityTable);
    }

    public DMRepository getDMRepository()
    {
        return getDMObject();
    }

    public DMPackage getSelectedDMPackage()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMPackage)) {
            return (DMPackage) selection.firstElement();
        }
        if (getSelectedDMEntity() != null) {
            return getSelectedDMEntity().getPackage();
        }
        return null;
    }

    public DMEntity getSelectedDMEntity()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMEntity)) {
            return (DMEntity) selection.firstElement();
        }
        return null;
    }

    public DMTabularView getEntityTable() {
        return entityTable;
    }

    public DMTabularView getPackageTable() {
        return packageTable;
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
