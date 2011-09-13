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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.dm.eo.EOAccessException;


public class CreateDMEOEntity extends FlexoAction<CreateDMEOEntity,DMEOModel,DMObject>
{

    private static final Logger logger = Logger.getLogger(CreateDMEOEntity.class.getPackage().getName());

    public static FlexoActionType<CreateDMEOEntity,DMEOModel,DMObject> actionType = new FlexoActionType<CreateDMEOEntity,DMEOModel,DMObject>("add_eo_entity",FlexoActionType.newMenu,FlexoActionType.defaultGroup,FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CreateDMEOEntity makeNewAction(DMEOModel focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) 
        {
            return new CreateDMEOEntity(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(DMEOModel object, Vector<DMObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(DMEOModel object, Vector<DMObject> globalSelection) 
        {
            return object != null && !object.getRepository().isReadOnly();
        }
                
    };
    
    private DMEOModel _dmEOModel;
    private String _newEntityName;
    private DMEOEntity _newEntity;
    
    CreateDMEOEntity (DMEOModel focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

   @Override
protected void doAction(Object context) throws EOAccessException 
    {
      logger.info ("CreateDMEntity");
      if (getDMEOModel() != null) {
          _newEntityName = _dmEOModel.getNextDefautEntityName(getRepository().getPackageWithName(getDMEOModel().derivePackageName()));
          _newEntity = DMEOEntity.createsNewDMEOEntity(getRepository().getDMModel(), _newEntityName, getDMEOModel(), _newEntityName);
          getRepository().registerEntity(_newEntity);
          _dmEOModel.notifyEOEntityAdded(_newEntity);
      }
    }

   public String getNewEntityName()
   {
       return _newEntityName;
   }
   
   public DMEOModel getDMEOModel()
   {
       if (_dmEOModel == null) {
           if (getFocusedObject() != null) {
               _dmEOModel = getFocusedObject();
            }           
       }
       return _dmEOModel;
   }

   public DMEORepository getRepository()
   {
       return getDMEOModel().getRepository();
   }
   
   public DMEOEntity getNewEntity()
   {
       return _newEntity;
   }

}
