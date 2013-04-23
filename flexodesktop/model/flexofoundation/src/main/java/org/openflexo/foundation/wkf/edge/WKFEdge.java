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
package org.openflexo.foundation.wkf.edge;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.inspector.InspectableObject;

public abstract class WKFEdge<S extends WKFNode, E extends WKFNode> extends WKFObject implements InspectableObject {

	private static final Logger logger = Logger.getLogger(WKFEdge.class.getPackage().getName());

	private static final String LOCATION_CONSTRAINT_FLAG = "locationConstraintFlag";

	private S startNode;

	private E endNode;

	private String _name;

	public WKFEdge(FlexoProcess process) {
		super(process);
	}

	public boolean isEdgeDisplayable() {
		return getStartNode() != null && getEndNode() != null && getStartNode().isNodeValid() && getEndNode().isNodeValid();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalizeDeserialization() for " + this.getClass().getName());
		}
		if (!isEdgeDisplayable() && !isCreatedByCloning()) {
			delete();
			return;
		}
		super.finalizeDeserialization(builder);
	}

	@Override
	public void delete() {
		if (getStartNode() != null) {
			removeOutgoingEdgeFromStartNode(getStartNode());
		}
		if (getEndNode() != null) {
			removeIncomingEdgeFromEndNode(getEndNode());
		}
		super.delete();
	}

	protected void resetStartAndEndNode() {
		setStartNode(null);
		setEndNode(null);
	}

	public abstract Class<S> getStartNodeClass();

	public abstract Class<E> getEndNodeClass();

	public final S getStartNode() {
		return startNode;
	}

	public void setStartNode(S startNode) {
		if (this.startNode == startNode) {
			return;
		}
		if (this.startNode != null) {
			removeOutgoingEdgeFromStartNode(this.startNode);
			this.startNode = null;
		}
		if (startNode == null || getStartNodeClass().isAssignableFrom(startNode.getClass())) {
			this.startNode = startNode;
			if (this.startNode != null) {
				addOutgoingEdgeToStartNode(startNode);
			}
		}
	}

	public final E getEndNode() {
		return endNode;
	}

	public void setEndNode(E endNode) {
		if (this.endNode == endNode) {
			return;
		}
		if (this.endNode != null) {
			removeIncomingEdgeFromEndNode(this.endNode);
			this.endNode = null;
		}
		if (endNode == null || getEndNodeClass().isAssignableFrom(endNode.getClass())) {
			this.endNode = endNode;
			if (this.endNode != null) {
				addIncomingEdgeToEndNode(endNode);
			}
		}
	}

	public abstract void addOutgoingEdgeToStartNode(S startNode);

	public abstract void addIncomingEdgeToEndNode(E endNode);

	public abstract void removeOutgoingEdgeFromStartNode(S startNode);

	public abstract void removeIncomingEdgeFromEndNode(E endNode);

	@Override
	public String toString() {
		return getClass().getSimpleName() + " name=" + getName() + " start=" + getStartNode() + " end=" + getEndNode();
	}

	@Override
	public String getFullyQualifiedName() {
		return (getStartNode() != null ? getStartNode().getFullyQualifiedName() : "UNLINK") + ".EDGE_TO."
				+ (getEndNode() != null ? getEndNode().getFullyQualifiedName() : "UNLINK");
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		returned.add(this);
		return returned;
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) {
		String oldValue = getName();
		if (oldValue == null || !oldValue.equals(aName)) {
			_name = aName;
			setChanged();
			notifyObservers(new NameChanged(oldValue, aName));
		}
	}

	public boolean hasLocationConstraintFlag() {
		return _booleanGraphicalPropertyForKey(LOCATION_CONSTRAINT_FLAG, false);
	}

	public void setLocationConstraintFlag(boolean flag) {
		_setGraphicalPropertyForKey(flag, LOCATION_CONSTRAINT_FLAG);
	}

	/*@Override
	public Object _graphicalPropertyForKey(String key)
	{
		if (key.startsWith("curve_cp_")) logger.info("Getting CP for "+this);
		return super._graphicalPropertyForKey(key);
	}
	
	@Override
	public void _setGraphicalPropertyForKey(Object value, String key)
	{
		if (key.startsWith("curve_cp_")) logger.info("Setting CP "+value+" for "+this);
		super._setGraphicalPropertyForKey(value, key);
	}*/
}
