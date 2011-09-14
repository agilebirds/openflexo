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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.LoadableDMEntity;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.localization.FlexoLocalization;

public class UpdateDMRepository extends FlexoAction 
{

    private static final Logger logger = Logger.getLogger(UpdateDMRepository.class.getPackage().getName());

    public static FlexoActionType actionType = new FlexoActionType ("update_repository",FlexoActionType.defaultGroup) {

        /**
         * Factory method
         */
        @Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) 
        {
            return new UpdateDMRepository(focusedObject, globalSelection,editor);
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
                    && (object instanceof DMRepository)
                    && (((DMRepository)object).isUpdatable()));
        }
                
    };
    
    UpdateDMRepository (FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

     private DMRepository _repository;
     private DMSet _updatedSet;

     public DMSet getUpdatedSet()
     {
         if ((_updatedSet == null) && (getRepository() instanceof ExternalRepository)) {
             _updatedSet = new DMSet(getRepository().getProject(),(ExternalRepository)getRepository(),false,null);
         }
         return _updatedSet;
     }

     public void setUpdatedSet(DMSet updatedSet)
     {
         _updatedSet = updatedSet;
     }

     @Override
	protected void doAction(Object context) 
     {
    	 
         makeFlexoProgress(FlexoLocalization
                 .localizedForKey("updating_repository"), 3);
         setProgress(FlexoLocalization
                 .localizedForKey("updating_classes"));
         
         logger.info("repository = "+getRepository());
         logger.info("getUpdatedSet() = "+getUpdatedSet());
         logger.info("getUpdatedSet().getSelectedObjects() = "+getUpdatedSet().getSelectedObjects());
         resetSecondaryProgress(getUpdatedSet().getSelectedObjects().size());
         
         for (Enumeration en=getUpdatedSet().getSelectedObjects().elements(); en.hasMoreElements();) {
             FlexoModelObject next = (FlexoModelObject)en.nextElement();
             if (next instanceof ClassReference) {
                 ClassReference classReference = (ClassReference)next;
                 setSecondaryProgress(FlexoLocalization.localizedForKey("updating")+" "+classReference.getName());
                 LoadableDMEntity entity = (LoadableDMEntity)getRepository().getDMEntity(classReference.getPackageName(),classReference.getName());
                 if (entity != null) {
                     logger.info("Update entity for "+classReference.getReferencedClass());
                    entity.update(getUpdatedSet().getImportGetOnlyProperties(),getUpdatedSet().getImportMethods());
                 }
                 else if (classReference.getReferencedClass() != null) {
                     logger.info("Create entity for "+classReference.getReferencedClass());
                     LoadableDMEntity.createLoadableDMEntity(classReference.getReferencedClass(),getRepository().getDMModel(), getUpdatedSet().getImportGetOnlyProperties(),getUpdatedSet().getImportMethods());
                 }
             }
         }
         setProgress(FlexoLocalization
                 .localizedForKey("updating_classes_done"));
         
         hideFlexoProgress();
         
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

}
