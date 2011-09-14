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

import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSEMessageEntryTableModel;



/**
 * View allowing to represent/edit a DMModel object
 * 
 * @author sguerin
 * 
 */
public class WSEMessageView extends WSEView<ServiceMessageDefinition>
{

    private static final Logger logger = Logger.getLogger(WSEMessageView.class.getPackage().getName());

    private WSETabularView entryTable;
    private WSEMessageEntryTableModel entryTableModel;

    public WSEMessageView(ServiceMessageDefinition model, WSEController controller)
    {
        super(model, controller, "ws_message_($name)");

  
        finalizeBuilding();
    }

    @Override
	protected JComponent buildContentPane()
    {
    		//BIDOUILLE: not read Only because we cannot distinguish the context...
    		WSService service = getMessageDefinition().getOperation().getServiceInterface().getParentService();
    		boolean readOnly=false;
    		if(service!=null && service instanceof ExternalWSService) readOnly=true;
    	
        entryTableModel = new WSEMessageEntryTableModel(getMessageDefinition(), getWSEController().getProject(), readOnly);
        entryTable = new WSETabularView(getWSEController(),entryTableModel,15);
        
        addToMasterTabularView(entryTable);
        
        return entryTable;

    }

    public ServiceMessageDefinition getMessageDefinition()
    {
        return getModelObject();
    }

  

    public MessageEntry getSelectedMessageEntry()
    {
        WSESelectionManager sm = getWSEController().getWSESelectionManager();
        Vector selection = sm.getSelection();
        if ((selection.size() == 1) && (selection.firstElement() instanceof MessageEntry)) {
            return (MessageEntry) selection.firstElement();
        }
        return null;
    }
    
   
  

    public WSETabularView getEntryTable() {
        return entryTable;
    }

}
