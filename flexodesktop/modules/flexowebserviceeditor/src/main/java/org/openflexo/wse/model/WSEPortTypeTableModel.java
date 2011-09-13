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
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.DuplicateWSObjectException;
import org.openflexo.foundation.ws.WSPortType;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSService;

/**
 * Please comment this class
 * 
 * @author dvanvyve
 * 
 */
public class WSEPortTypeTableModel extends AbstractModel<WSService,ServiceInterface>
{

    protected static final Logger logger = Logger.getLogger(WSEPortTypeTableModel.class.getPackage().getName());

    public WSEPortTypeTableModel(WSService model, FlexoProject project, boolean readOnly)
    {
        super(model, project);
        addToColumns(new IconColumn<ServiceInterface>("portType_icon", 30) {
            @Override
			public Icon getIcon(ServiceInterface object)
            {
                return WSEIconLibrary.WS_PORTTYPE_ICON;
            }
        });
        if(readOnly){
        addToColumns(new StringColumn<ServiceInterface>("name", 190) {
            @Override
			public String getValue(ServiceInterface object)
            {
                return (object).getName();
            }
        });
        addToColumns(new StringColumn<ServiceInterface>("description", 365) {
            @Override
			public String getValue(ServiceInterface object)
            {
                return (object).getDescription();
            }
/*
            public void setValue(FlexoModelObject object, String aValue)
            {
                ((FlexoProcess) object).setDescription(aValue);
            }*/
        });
        }
        else{
        	  addToColumns(new EditableStringColumn<ServiceInterface>("name", 190) {
                  @Override
				public String getValue(ServiceInterface object)
                  {
                      return (object).getName();
                  }
                  @Override
				public void setValue(ServiceInterface object, String aValue)
                  {
                      try {
                          (object).setName(aValue);
                      } catch (DuplicateWKFObjectException e) {
                          FlexoController.notify(FlexoLocalization.localizedForKey(e.getLocalizationKey()));
                      }
                      catch (DuplicateWSObjectException e) {
                          FlexoController.notify(FlexoLocalization.localizedForKey(e.getLocalizationKey()));
                      }
                      //selectObject(object);
                  }
              });
              addToColumns(new EditableStringColumn<ServiceInterface>("description", 365) {
                  @Override
				public String getValue(ServiceInterface object)
                  {
                      return (object).getDescription();
                  }
                  @Override
				public void setValue(ServiceInterface object, String aValue)
                  {
                      (object).setDescription(aValue);
                  }
              });
        }
        setRowHeight(20);
    }

    public WSService getWSService(){
    		return getModel();
    }
    
    public WSPortTypeFolder getWSPortTypeFolder(){
      		if(getWSService()!=null) return getWSService().getWSPortTypeFolder();
      		return null;
    }
    public Vector getWSPortTypes()
    {	
        return getWSPortTypeFolder().getWSPortTypes();
    }
    
    
    @Override
	public ServiceInterface elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return ((WSPortType) getWSPortTypes().get(row)).getServiceInterface();
         } else {
            return null;
        }
    }

    public ServiceInterface processAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getWSPortTypeFolder() != null) {
            return getWSPortTypes().size();
        }
        return 0;
    }

   

   

}
