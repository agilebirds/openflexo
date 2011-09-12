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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ProcessInstanceRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.eo.DMEORepository;


public class CreateDMPackage extends FlexoAction 
{

    private static final Logger logger = Logger.getLogger(CreateDMPackage.class.getPackage().getName());

    public static FlexoActionType actionType = new FlexoActionType ("add_package",FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) 
        {
            return new CreateDMPackage(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) 
        {
            return ((object != null) 
                    && (object instanceof DMRepository) && !(object instanceof DMEORepository)
                    && (! (object instanceof ComponentRepository))
                    && (! (object instanceof WORepository))
                    && (! (object instanceof ProcessInstanceRepository))
                    && (!((DMRepository)object).isReadOnly()));
         }
                
    };
    
    CreateDMPackage (FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    private String _newPackageName;
    private DMPackage _newPackage;
    private DMRepository _repository;

    @Override
	protected void doAction(Object context) 
    {
      logger.info ("CreateDMPackage"); 
      if (getRepository() != null && getNewPackageName()!=null) {
          _newPackage = getRepository().createPackage(getNewPackageName());
      }
    }
    
    public DMRepository getRepository() 
    {
        if (_repository == null) {
            if ((getFocusedObject() != null) && (getFocusedObject() instanceof DMRepository)) {
                _repository = (DMRepository)getFocusedObject();
             }           
        }
        return _repository;
    }

    public String getNewPackageName() 
    {
        return _newPackageName;
    }
   
    public void setNewPackageName(String newPackageName) 
    {
        _newPackageName = newPackageName;
    }

    public DMPackage getNewPackage() 
    {
        return _newPackage;
    }
   
}
