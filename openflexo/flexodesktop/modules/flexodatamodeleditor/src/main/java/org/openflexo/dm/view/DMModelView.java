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
import org.openflexo.dm.model.DMRepositoryFolderTableModel;
import org.openflexo.dm.model.DMRepositoryTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.UpdateDMRepository;


/**
 * View allowing to represent/edit a DMModel object
 * 
 * @author sguerin
 * 
 */
public class DMModelView extends DMView<DMModel>
{

    private DMTabularView repositoryFolderTable;
    private DMRepositoryFolderTableModel repositoryFolderTableModel;

    private DMTabularView repositoriesTable;
    private DMRepositoryTableModel repositoriesTableModel;

    public DMModelView(DMModel model, DMController controller)
    {
        super(model, controller, "data_model");

        addAction(new TabularViewAction(CreateDMRepository.actionType,"add_repository",controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMModel();
            }           
        });
        addAction(new TabularViewAction(UpdateDMRepository.actionType,controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getSelectedDMRepository();
            }           
        });
       addAction(new TabularViewAction(DMDelete.actionType,"delete_repository",controller.getEditor()) {
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
        DMModel model = getDMObject();
        repositoryFolderTableModel = new DMRepositoryFolderTableModel(model, getDMController().getProject());
        addToMasterTabularView(repositoryFolderTable = new DMTabularView(getDMController(), repositoryFolderTableModel,5));
       repositoriesTableModel = new DMRepositoryTableModel(null, getDMController().getProject());
        addToSlaveTabularView(repositoriesTable = new DMTabularView(getDMController(), repositoriesTableModel,10),repositoryFolderTable);

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, repositoryFolderTable, repositoriesTable);
    }

    public DMModel getDMModel()
    {
        return getDMObject();
    }

    public DMRepository getSelectedDMRepository()
    {
        DMSelectionManager sm = getDMController().getDMSelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof DMRepository)) {
            return (DMRepository) selection.firstElement();
        }
        return null;
    }

    public DMTabularView getRepositoriesTable() {
        return repositoriesTable;
    }

    public DMTabularView getRepositoryFolderTable() {
        return repositoryFolderTable;
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
