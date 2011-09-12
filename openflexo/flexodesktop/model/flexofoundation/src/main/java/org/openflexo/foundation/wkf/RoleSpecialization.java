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
package org.openflexo.foundation.wkf;

import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.action.DeleteRole;
import org.openflexo.foundation.wkf.action.DeleteRoleSpecialization;
import org.openflexo.foundation.wkf.action.WKFCopy;
import org.openflexo.foundation.wkf.action.WKFCut;
import org.openflexo.foundation.wkf.action.WKFPaste;
import org.openflexo.foundation.wkf.action.WKFSelectAll;
import org.openflexo.foundation.wkf.dm.RoleSpecializationRemoved;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;


/**
 * Represents a role specialization in the workflow
 * This is a relation between two roles
 * 
 * @author sguerin
 * 
 */
public final class RoleSpecialization extends WorkflowModelObject implements DataFlexoObserver, Serializable, DeletableObject, InspectableObject
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RoleSpecialization.class.getPackage().getName());

	private Role role;
	private Role parentRole;
	private String annotation;
	
	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public RoleSpecialization(FlexoWorkflowBuilder builder)
	{
		super(builder.getProject(),builder.workflow);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public RoleSpecialization(FlexoProject project)
	{
		super(project, (FlexoWorkflow)null);
	}

	public RoleSpecialization(Role aRole, Role aParentRole, String anAnnotation)
	{
		this(aRole.getProject());
		role = aRole;
		parentRole = aParentRole;
		annotation = anAnnotation;
	}

	public RoleSpecialization(Role aRole, Role aParentRole)
	{
		this(aRole,aParentRole,null);
	}

	
	@Override
	public String getFullyQualifiedName()
	{
		return getProject().getFullyQualifiedName() + ".ROLE_SPECIALIZATION." + getRole().getName()+"."+getParentRole().getName();
	}

	/**
	 * Default inspector name
	 */
	@Override
	public String getInspectorName()
	{
		return Inspectors.WKF.ROLE_SPECIALIZATION_INSPECTOR;
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass()
	{
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(DeleteRoleSpecialization.actionType);
        returned.add(WKFCut.actionType);
        returned.add(WKFCopy.actionType);
        returned.add(WKFPaste.actionType);
        returned.add(WKFSelectAll.actionType);
		returned.add(DeleteRole.actionType);
		return returned;
	}

	public String getAnnotation()
	{
		if (annotation == null && !isSerializing()) return "";
		return annotation;
	}

	public void setAnnotation(String anAnnotation)
	{
		String oldValue = annotation;
		annotation = anAnnotation;
		notifyAttributeModification("annotation", oldValue, annotation);
	}

	public Role getParentRole() 
	{
		return parentRole;
	}

	// Deserialization only
	public void _setParentRole(Role parentRole) 
	{
		if (isDeserializing()) 
			this.parentRole = parentRole;
	}

	public Role getRole() 
	{
		return role;
	}

	public void setRole(Role role) 
	{
		this.role = role;
	}

	/**
	 * Return a Vector of all embedded Validable
	 * 
	 * @return a Vector of Validable instances
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects()
	{
		Vector<Validable> returned = new Vector<Validable>();
		returned.add(this);
		return returned;
	}

	@Override
	public final void delete()
	{
		if (getRole()!=null && getRole().getRoleSpecializations().contains(this))
			getRole().removeFromRoleSpecializations(this);
		super.delete();
		setChanged();
		notifyObservers(new RoleSpecializationRemoved(this));
		deleteObservers();
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<RoleSpecialization> getAllEmbeddedDeleted()
	{
		Vector<RoleSpecialization> returned = new Vector<RoleSpecialization>();
		returned.add(this);
		return returned;
	}

	public RoleList getRoleList()
	{
		if (isDeserializing()) return null;
		if (getProject() != null) {
			return getProject().getWorkflow().getRoleList();
		}
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey()
	{
		return "role_specialization";
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof RoleSpecialization) {
			RoleSpecialization rs = (RoleSpecialization)obj;
			return getRole().equals(rs.getRole()) && getParentRole().equals(rs.getParentRole());
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() 
	{
		return getFullyQualifiedName();
	}
	// ===================================================================
	// =========================== FlexoObserver =========================
	// ===================================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		// TODO Auto-generated method stub

	}

}
