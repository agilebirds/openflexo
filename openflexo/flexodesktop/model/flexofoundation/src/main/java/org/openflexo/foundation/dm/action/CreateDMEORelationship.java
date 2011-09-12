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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOJoin;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.FlattenRelationshipDefinition;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;


public class CreateDMEORelationship extends FlexoAction<CreateDMEORelationship,DMEOEntity,DMObject> 
{

    private static final Logger logger = Logger.getLogger(CreateDMEORelationship.class.getPackage().getName());

    public static FlexoActionType<CreateDMEORelationship,DMEOEntity,DMObject> actionType 
    = new FlexoActionType<CreateDMEORelationship,DMEOEntity,DMObject> (
    		"add_eo_relationship",
    		FlexoActionType.newMenu,
    		FlexoActionType.defaultGroup,
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public CreateDMEORelationship makeNewAction(DMEOEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) 
        {
            return new CreateDMEORelationship(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(DMEOEntity object, Vector<DMObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(DMEOEntity object, Vector<DMObject> globalSelection) 
        {
            return ((object != null) 
                    && (!(object).getIsReadOnly()));
        }
                
    };
    
    private String _newRelationshipName;
    private boolean _isFlattenRelationShip;
    private DMEOEntity _destinationEntity;
    private boolean _isMultipleRelation;
    private Vector<DMEOJoin> _joins;
    private FlattenRelationshipDefinition _flattenRelationshipDefinition;
    
    private DMEORelationship _newEORelationship;
    
    CreateDMEORelationship (DMEOEntity focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
	protected void doAction(Object context) throws FlexoException 
    {
         if (getEntity() != null) {
        	if (isFlattenRelationShip()) {
        		logger.info ("Create flatten DMEORelationship with "+getFlattenRelationshipDefinition().getStringRepresentation());
           		_newEORelationship = DMEORelationship.createsNewFlattenDMEORelationship(
        				getEntity().getDMModel(), 
        				getEntity(), 
        				getNewRelationshipName(), 
        				getFlattenRelationshipDefinition().getStringRepresentation(), false, // Is
                                                                                                // read
                                                                                                // only
                                                                                                // =
                                                                                                // false
                        true, // Is settable = true
                        DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
                getEntity().registerProperty(_newEORelationship);
            } else {
                logger.info("Create normal DMEORelationship");
                _newEORelationship = DMEORelationship.createsNewDMEORelationship(getEntity().getDMModel(), getEntity(),
                        getNewRelationshipName(), false, // Is read only = false
                        true, // Is settable = true
                        DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
                FlexoException exception = null;
                if (getJoins() == null || getJoins().size() == 0) {
                        _newEORelationship.setDestinationEntity(getDestinationEntity(), true);
                } else {
                    _newEORelationship.setDestinationEntity(getDestinationEntity(), false);
                    for (DMEOJoin join : getJoins()) {
                        try {
                            join.setDMEORelationship(_newEORelationship);
                            _newEORelationship.addToDMEOJoins(join);
                        } catch (InvalidJoinException e) {
                            throw new FlexoException(e.getMessage(), e);
                        }

                    }
                }
                _newEORelationship.setIsToMany(isMultipleRelation());
        		getEntity().registerProperty(_newEORelationship);
        	}
         }
    }

   public String getNewRelationshipName()
   {
	   if (_newRelationshipName == null) {
           _newRelationshipName = getEntity().getDMModel().getNextDefautRelationshipName(getEntity());
	   }
       return _newRelationshipName;
   }
   
   public DMEOEntity getEntity()
   {
       return getFocusedObject();
   }

   public DMEORelationship getNewEORelationship()
   {
       return _newEORelationship;
   }

   public void setNewRelationshipName(String newRelationshipName) {
	   _newRelationshipName = newRelationshipName;
   }

   public DMEOEntity getDestinationEntity() {
	   return _destinationEntity;
   }

   public void setDestinationEntity(DMEOEntity destinationEntity) {
	   _destinationEntity = destinationEntity;
   }

   public FlattenRelationshipDefinition getFlattenRelationshipDefinition() {
	   return _flattenRelationshipDefinition;
   }

   public void setFlattenRelationshipDefinition(
		   FlattenRelationshipDefinition flattenRelationshipDefinition) {
	   _flattenRelationshipDefinition = flattenRelationshipDefinition;
   }

   public boolean isFlattenRelationShip() {
	   return _isFlattenRelationShip;
   }

   public void setFlattenRelationShip(boolean isFlattenRelationShip) {
	   _isFlattenRelationShip = isFlattenRelationShip;
   }

   public Vector<DMEOJoin> getJoins() {
	   return _joins;
   }

   public void setJoins(Vector<DMEOJoin> joins) {
	   _joins = joins;
   }

   public boolean isMultipleRelation() {
	   return _isMultipleRelation;
   }

   public void setMultipleRelation(boolean isMultipleRelation) {
	   _isMultipleRelation = isMultipleRelation;
   }

}
