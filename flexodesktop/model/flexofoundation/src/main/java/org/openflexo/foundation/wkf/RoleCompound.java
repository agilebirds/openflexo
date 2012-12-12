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

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents a connex sub-graph composed of Role elements linked with RoleSpecialization. Note that a RoleCompound is created by cloning
 * and thus, contains clones of initial nodes !
 * 
 * @author sguerin
 * 
 */
public final class RoleCompound extends WorkflowModelObject {

	private static final Logger logger = Logger.getLogger(RoleCompound.class.getPackage().getName());

	private Vector<Role> _roles;

	/**
	 * Constructor used during deserialization
	 */
	public RoleCompound(FlexoWorkflowBuilder builder) {
		this(builder.getProject(), builder.workflow);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public RoleCompound(FlexoProject project, FlexoWorkflow workflow) {
		super(project, workflow);
		_roles = new Vector<Role>();
	}

	/**
	 * Default constructor
	 */
	public RoleCompound(FlexoWorkflow workflow, Vector<Role> roles) {
		this(workflow.getProject(), workflow);

		// 2. We set a copy of the vector as our current data
		_roles = new Vector<Role>(roles);

		// 3. We create a clone
		RoleCompound clone = (RoleCompound) cloneUsingXMLMapping();
		if (clone != null) {
			// 4. We set the objects as our objects
			_roles = clone.getRoles();
			makeConnex(clone, roles);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Serialized selection is: ");
				logger.fine(getXMLRepresentation());
			}
		} else {
			_roles = new Vector<Role>();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not clone this RoleCompound");
			}
		}
	}

	private void makeConnex(RoleCompound clone, Vector<Role> originalSelectedRoles) {
		XMLMapping mapping = new FlexoXMLMappings().getWorkflowMapping();
		HashSet<RoleSpecialization> embedded = new HashSet<RoleSpecialization>();
		mapping.getEmbeddedObjectsForObject(clone, embedded, RoleSpecialization.class, true);
		HashSet<WorkflowModelObject> originalSet = new HashSet<WorkflowModelObject>();
		Iterator<Role> originalIterator = clone.getRoles().iterator();
		while (originalIterator.hasNext()) {
			Role role = originalIterator.next();
			mapping.getRestrictedEmbeddedObjectsForObject(role, originalSet, WorkflowModelObject.class, true);
		}
		Iterator<RoleSpecialization> i = embedded.iterator();
		while (i.hasNext()) {
			RoleSpecialization rs = i.next();
			if (rs.getRole() == null || rs.getParentRole() == null || !originalSet.contains(rs.getRole())
					|| !originalSet.contains(rs.getParentRole())) {
				rs.delete();
			}
		}
	}

	/*	@Override
		public FlexoXMLSerializableObject cloneUsingXMLMapping()
	    {
	        //logger.info("getNodes()="+getNodes());
	        RoleCompound returned = (RoleCompound) super.cloneUsingXMLMapping();
	        if (returned !=null) {
	        //logger.info("returned.getNodes()="+returned.getNodes());
		        for (Enumeration<PetriGraphNode> e = returned.getNodes().elements(); e.hasMoreElements();) {
		        	PetriGraphNode temp = e.nextElement();
		            if (temp instanceof SubProcessNode) {
		                if (((SubProcessNode) temp).getPortMapRegistery() != null) {
		                    ((SubProcessNode) temp).getPortMapRegistery().lookupServiceInterface();
		                }
		            }
		        }
	        }
	        return returned;
	    }*/

	@Override
	public void initializeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initializeDeserialization() for " + this.getClass().getName());
		}
		super.initializeDeserialization(builder);
		if (getWorkflow() != null) {
			getWorkflow().initializeDeserialization(builder);
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalizeDeserialization() for " + this.getClass().getName());
		}
		super.finalizeDeserialization(builder);
		if (getWorkflow() != null) {
			getWorkflow().finalizeDeserialization(builder);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return "ROLE_COMPOUND";
	}

	public void setLocation(Point2D location, String context) {
		Point2D offset = null;
		Point2D upperLeftCorner = null;
		for (Role role : getRoles()) {
			if (upperLeftCorner == null) {
				upperLeftCorner = new Point2D.Double();
				upperLeftCorner.setLocation(role.getLocation(context));
			} else {
				upperLeftCorner.setLocation(Math.min(upperLeftCorner.getX(), role.getX(context)),
						Math.min(upperLeftCorner.getY(), role.getY(context)));
			}
		}
		if (upperLeftCorner == null) {
			upperLeftCorner = new Point2D.Double();
		}
		offset = new Point2D.Double();
		offset.setLocation(location.getX() - upperLeftCorner.getX(), location.getY() - upperLeftCorner.getY());
		for (Role role : getRoles()) {
			setLocation(role, offset, context);
		}
	}

	private void setLocation(Role role, Point2D offset, String context) {
		if (role != null) {
			role.setX(Math.max(role.getX(context) + offset.getX(), 0), context);
			role.setY(Math.max(role.getY(context) + offset.getY(), 0), context);
		}
	}

	public Vector<Role> getRoles() {
		return _roles;
	}

	public void setRoles(Vector<Role> nodes) {
		_roles = nodes;
	}

	public void addToRoles(Role aRole) {
		_roles.add(aRole);
	}

	public void removeFromRoles(Role aRole) {
		_roles.remove(aRole);
	}

	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	public boolean isSingleRole() {
		return _roles.size() == 1;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "roles_compound";
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		return null;
	}

	@Override
	public Collection<Role> getEmbeddedValidableObjects() {
		return getRoles();
	}

}
