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
package org.openflexo.foundation.wkf.node;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.AssociationInserted;
import org.openflexo.foundation.wkf.dm.AssociationRemoved;
import org.openflexo.foundation.wkf.edge.WKFAssociation;
import org.openflexo.foundation.wkf.edge.WKFEdge;

public abstract class WKFNode extends WKFObject {

	private Vector<WKFAssociation> incomingAssociations;
	private Vector<WKFAssociation> outgoingAssociations;

	/**
	 * Default constructor
	 */
	public WKFNode(FlexoProcess process) {
		super(process);
		incomingAssociations = new Vector<WKFAssociation>();
		outgoingAssociations = new Vector<WKFAssociation>();
	}

	@Override
	public boolean delete() {
		Enumeration<WKFAssociation> en = new Vector<WKFAssociation>(incomingAssociations).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
		en = new Vector<WKFAssociation>(outgoingAssociations).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
		return super.delete();
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
		returned.addAll(getIncomingAssociations());
		returned.addAll(getOutgoingAssociations());
		return returned;
	}

	public Vector<WKFAssociation> getIncomingAssociations() {
		return incomingAssociations;
	}

	public void setIncomingAssociations(Vector<WKFAssociation> incomingAssociations) {
		this.incomingAssociations = incomingAssociations;
	}

	public void addToIncomingAssociations(WKFAssociation association) {
		if (!incomingAssociations.contains(association)) {
			incomingAssociations.add(association);
			association.setEndNode(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new AssociationInserted(association));
				notifyAssociationInsertedToProcess(association);
			}
		}
	}

	public void removeFromIncomingAssociations(WKFAssociation association) {
		if (incomingAssociations.contains(association)) {
			incomingAssociations.remove(association);
			association.setEndNode(null);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new AssociationRemoved(association));
			}
		}
	}

	public boolean hasIncomingAssociations() {
		return getIncomingAssociations().size() > 0;
	}

	/**
	 * Returns all the start nodes of the incoming associations of this node
	 * 
	 * @return all the start nodes of the incoming associations of this node.
	 */
	public Vector<WKFNode> getFromAssociatedNodes() {
		Vector<WKFNode> v = new Vector<WKFNode>();
		for (WKFAssociation a : getIncomingAssociations()) {
			if (!v.contains(a.getStartNode())) {
				v.add(a.getStartNode());
			}
		}
		return v;
	}

	/**
	 * Returns all the edges that come into this node. Edges can either be Associations or PostConditions
	 * 
	 * @return all the edges that come into this node.
	 */
	public Vector<WKFEdge<?, ?>> getAllIncomingEdges() {
		Vector<WKFEdge<?, ?>> edges = new Vector<WKFEdge<?, ?>>();
		edges.addAll(getIncomingAssociations());
		return edges;
	}

	/**
	 * Returns all nodes this node is related from.
	 * 
	 * @return all nodes this node is related from.
	 */
	public final Vector<WKFNode> getAllRelatedFromNodes() {
		Vector<WKFNode> v = new Vector<WKFNode>();
		for (WKFEdge<?, ?> a : getAllIncomingEdges()) {
			if (!v.contains(a.getStartNode())) {
				v.add(a.getStartNode());
			}
		}
		return v;
	}

	public Vector<WKFAssociation> getOutgoingAssociations() {
		return outgoingAssociations;
	}

	public void setOutgoingAssociations(Vector<WKFAssociation> outgoingAssociations) {
		this.outgoingAssociations = outgoingAssociations;
	}

	public void addToOutgoingAssociations(WKFAssociation association) {
		if (!outgoingAssociations.contains(association)) {
			outgoingAssociations.add(association);
			association.setStartNode(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new AssociationInserted(association));
				notifyAssociationInsertedToProcess(association);
			}
		}
	}

	public void removeFromOutgoingAssociations(WKFAssociation association) {
		if (outgoingAssociations.contains(association)) {
			outgoingAssociations.remove(association);
			association.setStartNode(null);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new AssociationRemoved(association));
			}
		}
	}

	public boolean hasOutgoingAssociations() {
		return getOutgoingAssociations().size() > 0;
	}

	/**
	 * Returns all the end nodes of the outgoing associations of this node
	 * 
	 * @return all the end nodes of the outgoing associations of this node.
	 */
	public Vector<WKFNode> getToAssociatedNodes() {
		Vector<WKFNode> v = new Vector<WKFNode>();
		for (WKFAssociation a : getOutgoingAssociations()) {
			if (!v.contains(a.getEndNode())) {
				v.add(a.getEndNode());
			}
		}
		return v;
	}

	/**
	 * Returns all the edges that come into this node. Edges can either be Associations or PostConditions
	 * 
	 * @return all the edges that come into this node.
	 */
	public Vector<WKFEdge<?, ?>> getAllOutgoingEdges() {
		Vector<WKFEdge<?, ?>> edges = new Vector<WKFEdge<?, ?>>();
		edges.addAll(getOutgoingAssociations());
		return edges;
	}

	/**
	 * Returns all nodes this node is related to.
	 * 
	 * @return all nodes this node is related to.
	 */
	public final Vector<WKFNode> getAllRelatedToNodes() {
		return getToAssociatedNodes();
	}

	public abstract boolean isNodeValid();

	protected void notifyAssociationInsertedToProcess(WKFAssociation association) {
		if (getProcess() != null) {
			getProcess().notifyAssociationInserted(association);
		}
	}
}
