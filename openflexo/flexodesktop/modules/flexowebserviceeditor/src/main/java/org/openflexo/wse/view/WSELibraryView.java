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
package org.openflexo.wse.view;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.WSFolder;
import org.openflexo.foundation.ws.action.CreateNewWebService;
import org.openflexo.foundation.ws.action.WSDelete;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSEFolderTableModel;
import org.openflexo.wse.model.WSEServiceTableModel;


/**
 * View allowing to represent/edit a DMModel object
 * 
 * @author sguerin
 * 
 */
public class WSELibraryView extends WSEView<FlexoWSLibrary>
{

    private static final Logger logger = Logger.getLogger(WSELibraryView.class.getPackage().getName());

    private WSETabularView wsFoldersTable;
    private WSEFolderTableModel wsFoldersTableModel;

    private WSETabularView wsServicesTable;
    private WSEServiceTableModel wsServicesTableModel;

    public WSELibraryView(FlexoWSLibrary model, WSEController controller)
    {
        super(model, controller, "ws_library");

        addAction(new TabularViewAction(CreateNewWebService.actionType,"ws_add_webservice", controller.getEditor()) {
            @Override
			protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            @Override
			protected FlexoModelObject getFocusedObject() 
            {
                return getWSLibrary();
            }           
        });
    /*    addAction(new TabularViewAction(UpdateDMRepository.actionType) {
            protected Vector getGlobalSelection()
            {
                return getViewSelection();
            }

            protected FlexoModelObject getFocusedObject() 
            {
                return getSelectedWSFolder();
            }           
        });*/
       addAction(new TabularViewAction(WSDelete.actionType,"delete_webservice", controller.getEditor()) {
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
        FlexoWSLibrary model = getWSLibrary();
        wsFoldersTableModel = new WSEFolderTableModel(model, getWSEController().getProject());
        wsFoldersTable = new WSETabularView(getWSEController(),wsFoldersTableModel,3);
        addToMasterTabularView(wsFoldersTable);
        wsServicesTableModel = new WSEServiceTableModel(null, getWSEController().getProject(), true);
        wsServicesTable = new WSETabularView(getWSEController(), wsServicesTableModel,10);
        addToSlaveTabularView(wsServicesTable, wsFoldersTable);

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, wsFoldersTable, wsServicesTable);
        
    }

    public FlexoWSLibrary getWSLibrary()
    {
        return getModelObject();
    }

    public WSFolder getSelectedWSFolder()
    {
        WSESelectionManager sm = getWSEController().getWSESelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof WSFolder)) {
            return (WSFolder) selection.firstElement();
        }
        return null;
    }

    public WSETabularView getWSFoldersTable() {
        return wsFoldersTable;
    }

    public WSETabularView getWSServicesTable() {
        return wsServicesTable;
    }

 
}
