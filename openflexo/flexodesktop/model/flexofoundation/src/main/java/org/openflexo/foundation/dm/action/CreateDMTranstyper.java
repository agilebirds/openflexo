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
package org.openflexo.foundation.dm.action;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMTranstyper;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMTranstyper.DMTranstyperEntry;


public class CreateDMTranstyper extends FlexoAction<CreateDMTranstyper,DMEntity,DMObject>
{

    private static final Logger logger = Logger.getLogger(CreateDMTranstyper.class.getPackage().getName());

    public static FlexoActionType<CreateDMTranstyper,DMEntity,DMObject> actionType
    = new FlexoActionType<CreateDMTranstyper,DMEntity,DMObject>  (
    		"add_transtyper",
    		FlexoActionType.newMenu,
    		FlexoActionType.defaultGroup,
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CreateDMTranstyper makeNewAction(DMEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor)
        {
            return new CreateDMTranstyper(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(DMEntity object, Vector<DMObject> globalSelection)
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(DMEntity object, Vector<DMObject> globalSelection)
        {
            return object != null && object.getRepository()!=null && !object.getRepository().isReadOnly();
        }

    };

    private DMEntity _entity;
    private String _newTranstyperName;
    private DMType _newTranstyperType;
    private Vector<DMTranstyperEntry> entries;
    private boolean isMappingDefined;

    private DMTranstyper _newTranstyper;

    CreateDMTranstyper (DMEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
        entries = new Vector<DMTranstyperEntry>();
    }

   @Override
protected void doAction(Object context)
    {
      logger.info ("CreateDMTranstyper "+_newTranstyperName);
      if (getEntity() != null) {
     	  _newTranstyper = new DMTranstyper(getEntity().getDMModel(),getEntity(),getNewTranstyperName(),getNewTranstyperType());
     	 _newTranstyper.setEntries(getEntries());
    	 _newTranstyper.setIsMappingDefined(getIsMappingDefined());
     	  getEntity().addToDeclaredTranstypers(_newTranstyper);
      }
    }

	public boolean getIsMappingDefined()
	{
		return isMappingDefined;
	}

	public void setIsMappingDefined(boolean aFlag)
	{
		isMappingDefined = aFlag;
	}

   public String getNewTranstyperName()
   {
	   if(_newTranstyperName==null)
		   _newTranstyperName = getEntity().getDMModel().getNextDefautTranstyperName(getEntity());
	   return _newTranstyperName;
   }

   public void setNewTranstyperName(String value)
   {
	   _newTranstyperName = value;
   }

   public DMType getNewTranstyperType()
   {
	   if(_newTranstyperType==null)
		   _newTranstyperType = DMType.makeResolvedDMType(getEntity());
	   return _newTranstyperType;
   }

   public void setNewTranstyperType(DMType newTranstyperType) {
	   _newTranstyperType = newTranstyperType;
   }

	public Vector<DMTranstyperEntry> getEntries()
	{
		return entries;
	}

	public void setEntries(List<DMTranstyperEntry> someEntries)
	{
		this.entries.addAll(someEntries);
	}


   public DMEntity getEntity()
   {
       if (_entity == null) {
           if (getFocusedObject() != null) {
               _entity = getFocusedObject();
            }
       }
       return _entity;
   }

   public DMTranstyper getNewTranstyper()
   {
       return _newTranstyper;
   }

}
