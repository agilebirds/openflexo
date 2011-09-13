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

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMRepositoryTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMRepositoryFolder;
import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.action.UpdateDMRepository;


/**
 * View allowing to represent/edit a DMRepositoryFolder object
 * 
 * @author sguerin
 * 
 */
public class DMRepositoryFolderView extends DMView<DMRepositoryFolder>
{

    private DMTabularView repositoriesTable;
    private DMRepositoryTableModel repositoriesTableModel;

    public DMRepositoryFolderView(DMRepositoryFolder repositoryFolder, DMController controller)
    {
        super(repositoryFolder, controller, "folders_($localizedName)");

        addAction(new TabularViewAction(CreateDMRepository.actionType,"add_repository",controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getDMRepositoryFolder().getDMModel();
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
       repositoriesTableModel = new DMRepositoryTableModel(getDMRepositoryFolder(), getDMController().getProject());
       addToMasterTabularView(repositoriesTable = new DMTabularView(getDMController(), repositoriesTableModel,15));

        return repositoriesTable;
    }

    public DMRepositoryFolder getDMRepositoryFolder()
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

    public DMTabularView getRepositoriesTable() 
    {
        return repositoriesTable;
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
