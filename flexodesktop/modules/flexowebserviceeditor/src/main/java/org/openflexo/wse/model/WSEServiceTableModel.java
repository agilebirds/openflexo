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
package org.openflexo.wse.model;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wse.controller.WSEController;


import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.ws.DuplicateWSObjectException;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.WSFolder;
import org.openflexo.foundation.ws.WSService;

/**
 * table model for a service.
 * 
 * @author dvanvyve
 * 
 */
public class WSEServiceTableModel extends AbstractModel<WSFolder,WSService>
{

    protected static final Logger logger = Logger.getLogger(WSEServiceTableModel.class.getPackage().getName());

    public WSEServiceTableModel(WSFolder folder, FlexoProject project, boolean readOnly)
    {
        super(folder, project);
        addToColumns(new IconColumn<WSService>("ws_service_icon", 30) {
            @Override
			public Icon getIcon(WSService object)
            {
                if(object instanceof ExternalWSService)
            		return WSEIconLibrary.EXTERNAL_WS_SERVICE_ICON;
                else return WSEIconLibrary.INTERNAL_WS_SERVICE_ICON;
            }
            
            @Override
			public String getLocalizedTooltip(WSService object) {
            	if (object instanceof ExternalWSService)
					return FlexoLocalization.localizedForKey("external_ws");
				else
					return FlexoLocalization.localizedForKey("internal_ws");
			}
        });

         if(readOnly){
             addToColumns(new StringColumn<WSService>("name", 300) {
                 @Override
				public String getValue(WSService object)
                 {
                     return (object).getLocalizedName();
                 }
             });
             addToColumns(new StringColumn<WSService>("description", 570) {
             @Override
			public String getValue(WSService object)
             {
                 return (object).getDescription();
             }
         });
         
         }
         else {
             addToColumns(new EditableStringColumn<WSService>("name", 300) {
                 @Override
				public String getValue(WSService object)
                 {
                     return (object).getLocalizedName();
                 }
                 @Override
				public void setValue(WSService object, String aValue)
                 {
                     try {
                         (object).setName(aValue);
                     } catch (DuplicateWSObjectException e) {
                         FlexoController.notify(FlexoLocalization.localizedForKey(e.getLocalizationKey()));
                     }
                     selectObject(object);
                 }
             });
             addToColumns(new EditableStringColumn<WSService>("description", 570) {
                 @Override
				public String getValue(WSService object)
                 {
                     return (object).getDescription();
                 }
                 @Override
				public void setValue(WSService object, String aValue)
                 {
                     (object).setDescription(aValue);
                 }
             });
         }
        setRowHeight(20);
    }

    public WSFolder getWSFolder()
    {
        return getModel();
    }
    
    public Vector getWSServices()
    {	
        return getWSFolder().getWSServices();
    }
    
    
    @Override
	public WSService elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return (WSService) getWSServices().get(row);
         } else {
            return null;
        }
    }

    public WSService serviceAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getWSFolder() != null) {
            return getWSServices().size();
        }
        return 0;
    }

}
