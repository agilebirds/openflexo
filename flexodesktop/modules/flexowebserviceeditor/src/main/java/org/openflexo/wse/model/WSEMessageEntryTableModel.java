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

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.EntitySelectorColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.ws.MessageEntry;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEMessageEntryTableModel extends AbstractModel<ServiceMessageDefinition,MessageEntry>
{

    protected static final Logger logger = Logger.getLogger(WSEMessageEntryTableModel.class.getPackage().getName());

    public WSEMessageEntryTableModel(ServiceMessageDefinition model, FlexoProject project, boolean readOnly)
    {
        super(model, project);
        if(readOnly){
        addToColumns(new StringColumn<MessageEntry>("name", 190) {
            @Override
			public String getValue(MessageEntry object)
            {
                return (object).getVariableName();
            }
        });
        addToColumns(new StringColumn<MessageEntry>("type", 150) {
            @Override
			public String getValue(MessageEntry object)
            {
                return (object).getTypeClassName();
            }

         /*   public void setValue(FlexoModelObject object, String aValue)
            {
                
                    ((MessageEntry) object).setTypeClassName(aValue);
                selectObject(object);
            }*/
        });
        }
        else{
            addToColumns(new EditableStringColumn<MessageEntry>("name", 190) {
                @Override
				public String getValue(MessageEntry object)
                {
                    return (object).getVariableName();
                }
                @Override
				public void setValue(MessageEntry object, String aValue)
                {
                    (object).setVariableName(aValue);
                    selectObject(object);
                }
                
            });
            addToColumns(new EntitySelectorColumn<MessageEntry,DMEntity>("type", 150, project,DMEntity.class) {
                @Override
				public DMEntity getValue(MessageEntry object)
                {
                    return (object).getType().getBaseEntity();
                }

                @Override
				public void setValue(MessageEntry object, DMEntity aValue)
                {
                    (object).getType().setBaseEntity(aValue);
                }
            });
        }
        setRowHeight(20);
    }

    public ServiceMessageDefinition getMessageDefinition(){
    		return getModel();
    }
    
    public Vector<MessageEntry> getEntries()
    {	
        return getMessageDefinition().getEntries();
    }
    
    
    @Override
	public MessageEntry elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return getEntries().get(row);
         } else {
            return null;
        }
    }

    public MessageEntry entryAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getMessageDefinition() != null) {
            return getEntries().size();
        }
        return 0;
    }

   

   

}
