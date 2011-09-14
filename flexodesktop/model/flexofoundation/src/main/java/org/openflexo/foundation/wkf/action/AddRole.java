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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WorkflowModelObject;


public class AddRole extends FlexoAction<AddRole,WorkflowModelObject,WorkflowModelObject> 
{

    private static final Logger logger = Logger.getLogger(AddRole.class.getPackage().getName());
    
    public static FlexoActionType<AddRole,WorkflowModelObject,WorkflowModelObject>  actionType 
    = new FlexoActionType<AddRole,WorkflowModelObject,WorkflowModelObject> (
    		"add_new_role",
    		FlexoActionType.newMenu,
    		FlexoActionType.newMenuGroup1,
    		FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
    	@Override
        public AddRole makeNewAction(WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor) 
        {
            return new AddRole(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) 
        {
            return object!=null && (object instanceof RoleList && !((RoleList)object).isImportedRoleList()) || (object instanceof Role && !((Role)object).isImported());
        }

        @Override
		protected boolean isEnabledForSelection(WorkflowModelObject object, Vector<WorkflowModelObject> globalSelection) 
        {
            return isVisibleForSelection(object, globalSelection);
        }
                
    };
    
    private String _newRoleName;
    private FlexoColor _newColor;
    private String _newDescription;
    private boolean _isSystemRole = false;
    
    private double x=-1;
    private double y=-1;
    
    private boolean automaticallyCreateRole = false;
    
    private Role _newRole;

    AddRole (WorkflowModelObject focusedObject, Vector<WorkflowModelObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

     public FlexoWorkflow getWorkflow() 
    {
         if (getFocusedObject() != null)  {
             return (getFocusedObject()).getWorkflow();
         }
        return null;
    }
    
    public String getNewRoleName() 
    {
        return _newRoleName;
    }

    public void setNewRoleName(String newRoleName) 
    {
        _newRoleName = newRoleName;
    }
    
    @Override
	protected void doAction(Object context) throws DuplicateRoleException 
    {
        logger.info ("Add role");
        if (getWorkflow() != null)  {
            RoleList roleList = getWorkflow().getRoleList();
            roleList.addToRoles(_newRole = new Role(getWorkflow(), getNewRoleName()));
            if (x!=-1 && y!=-1) {
            	_newRole.setX(x,RepresentableFlexoModelObject.DEFAULT);
               	_newRole.setY(y,RepresentableFlexoModelObject.DEFAULT);
            }
            if (getNewColor() != null) _newRole.setColor(getNewColor());
            if (getNewDescription() != null) _newRole.setDescription(getNewDescription());
            _newRole.setIsSystemRole(isSystemRole());
       }
        else {
            logger.warning("Cannot access workflow !");
        }
    }

    public Role getNewRole() 
    {
        return _newRole;
    }

    public FlexoColor getNewColor()
    {
        return _newColor;
    }

    public void setNewColor(FlexoColor newColor) 
    {
        _newColor = newColor;
    }

    public String getNewDescription() 
    {
        return _newDescription;
    }

    public void setNewDescription(String newDescription) 
    {
        _newDescription = newDescription;
    }

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean getRoleAutomaticallyCreated() 
	{
		return automaticallyCreateRole;
	}

	public void setRoleAutomaticallyCreated(boolean automaticallyCreateRole)
	{
		this.automaticallyCreateRole = automaticallyCreateRole;
	}

	public boolean isSystemRole() {
		return _isSystemRole;
	}

	public void setIsSystemRole(boolean isSystemRole) {
		_isSystemRole = isSystemRole;
	}

 
}
