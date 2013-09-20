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

import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoXMLSerializableObject;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Represents a connex sub-graph composed of AbstractNode elements linked with FlexoPreCondition and FlexoPostCondition Note that a
 * NodeCompound is created by cloning and thus, contains clones of initial nodes !
 * 
 * @author sguerin
 * 
 */
public final class NodeCompound extends WKFObject implements LevelledObject {

	private static final Logger logger = Logger.getLogger(NodeCompound.class.getPackage().getName());

	private Vector<PetriGraphNode> _nodes;

	private Vector<WKFArtefact> artefacts;

	private FlexoLevel _level = null;

	/**
	 * Constructor used during deserialization
	 */
	public NodeCompound(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public NodeCompound(FlexoProcess process) {
		super(process);
		_nodes = new Vector<PetriGraphNode>();
		artefacts = new Vector<WKFArtefact>();
	}

	/**
	 * Default constructor
	 */
	public NodeCompound(FlexoProcess process, Vector<PetriGraphNode> nodes, Vector<WKFArtefact> artefacts) {
		this(process);
		// 1. First we select only the elements that are that have a common petri-graph
		FlexoPetriGraph parent = null;
		Iterator<PetriGraphNode> i = nodes.iterator();
		while (i.hasNext()) {
			PetriGraphNode node = i.next();
			if (parent == null) {
				parent = node.getParentPetriGraph();
			} else if (parent != node.getParentPetriGraph()) {
				i.remove();
			}
		}
		Iterator<WKFArtefact> i2 = artefacts.iterator();
		while (i2.hasNext()) {
			WKFArtefact artefact = i2.next();
			if (parent == null) {
				parent = artefact.getParentPetriGraph();
			} else if (parent != artefact.getParentPetriGraph()) {
				i2.remove();
			}
		}

		// 2. We set a copy of the vector as our current data
		this.artefacts = new Vector<WKFArtefact>(artefacts);
		_nodes = new Vector<PetriGraphNode>(nodes);

		// 3. We create a clone
		NodeCompound clone = (NodeCompound) cloneUsingXMLMapping();
		if (clone != null) {

			// 4. We set the objects as our objects
			_nodes = clone.getNodes();
			this.artefacts = clone.getArtefacts();
			_level = clone.getLevel(); // Level is automatically set during deserialization
			makeConnex(clone, nodes, artefacts);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Serialized selection is: ");
				logger.fine(getXMLRepresentation());
			}
		} else {
			_nodes = new Vector<PetriGraphNode>();
			this.artefacts = new Vector<WKFArtefact>();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not clone this NodeCompound");
			}
		}
	}

	private void makeConnex(NodeCompound clone, Vector<PetriGraphNode> originalSelectedNodes, Vector<WKFArtefact> originalSelectedArtefacts) {
		XMLMapping mapping = getProject().getServiceManager().getXMLSerializationService().getWKFMapping();
		HashSet<WKFEdge> embedded = new HashSet<WKFEdge>();
		mapping.getEmbeddedObjectsForObject(clone, embedded, WKFEdge.class, true);
		HashSet<WKFObject> originalSet = new HashSet<WKFObject>();
		Iterator<PetriGraphNode> originalIterator = clone.getNodes().iterator();
		while (originalIterator.hasNext()) {
			PetriGraphNode node = originalIterator.next();
			mapping.getRestrictedEmbeddedObjectsForObject(node, originalSet, WKFObject.class, true);
		}
		Iterator<WKFArtefact> originalAnnotationIterator = clone.getArtefacts().iterator();
		while (originalAnnotationIterator.hasNext()) {
			WKFArtefact a = originalAnnotationIterator.next();
			mapping.getRestrictedEmbeddedObjectsForObject(a, originalSet, WKFObject.class, true);
		}

		Iterator<WKFEdge> i = embedded.iterator();
		while (i.hasNext()) {
			WKFEdge post = i.next();
			if (post.getStartNode() == null || post.getEndNode() == null || !originalSet.contains(post.getStartNode())
					|| !originalSet.contains(post.getEndNode())) {
				post.delete();
			}
		}
	}

	@Override
	public FlexoXMLSerializableObject cloneUsingXMLMapping() {
		// logger.info("getNodes()="+getNodes());
		NodeCompound returned = (NodeCompound) super.cloneUsingXMLMapping();
		if (returned != null) {
			// logger.info("returned.getNodes()="+returned.getNodes());
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
	}

	@Override
	public void initializeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initializeDeserialization() for " + this.getClass().getName());
		}
		super.initializeDeserialization(builder);
		if (getProcess() != null) {
			getProcess().initializeDeserialization(builder);
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("finalizeDeserialization() for " + this.getClass().getName());
		}
		super.finalizeDeserialization(builder);
		if (getProcess() != null) {
			getProcess().finalizeDeserialization(builder);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return "NODE_COMPOUND";
	}

	public void setLocation(Point2D location, String context) {
		Point2D offset = null;
		Point2D upperLeftCorner = null;
		for (Enumeration<WKFObject> e = getAllEmbeddedWKFObjects().elements(); e.hasMoreElements();) {
			WKFObject node = e.nextElement();
			if (node == this) {
				continue;
			}
			if (upperLeftCorner == null) {
				upperLeftCorner = new Point2D.Double();
				upperLeftCorner.setLocation(node.getLocation(context));
			} else {
				upperLeftCorner.setLocation(Math.min(upperLeftCorner.getX(), node.getX(context)),
						Math.min(upperLeftCorner.getY(), node.getY(context)));
			}
		}
		if (upperLeftCorner == null) {
			upperLeftCorner = new Point2D.Double();
		}
		offset = new Point2D.Double();
		offset.setLocation(location.getX() - upperLeftCorner.getX(), location.getY() - upperLeftCorner.getY());
		for (Enumeration<WKFObject> e = getAllEmbeddedWKFObjects().elements(); e.hasMoreElements();) {
			WKFObject node = e.nextElement();
			setLocation(node, offset, context);
			if (node instanceof SelfExecutableNode) {
				SelfExecutableNode self = (SelfExecutableNode) node;
				if (self.hasExecutionPetriGraph()) {
					setLocation(self.getExecutionPetriGraph(), offset, context);
				}
			} else {
				if (node instanceof AbstractActivityNode) {
					AbstractActivityNode activity = (AbstractActivityNode) node;
					if (activity.hasContainedPetriGraph()) {
						setLocation(activity.getOperationPetriGraph(), offset, context);
						Vector allOperations = activity.getAllOperationNodes();
						for (Enumeration e2 = allOperations.elements(); e2.hasMoreElements();) {
							OperationNode operation = (OperationNode) e2.nextElement();
							if (operation.hasContainedPetriGraph()) {
								setLocation(operation.getActionPetriGraph(), offset, context);
							}
						}
					}
				} else if (node instanceof OperationNode) {
					OperationNode operation = (OperationNode) node;
					if (operation.hasContainedPetriGraph()) {
						setLocation(operation.getActionPetriGraph(), offset, context);
					}
				} else if (node instanceof LOOPOperator) {
					LOOPOperator operator = (LOOPOperator) node;
					if (operator.hasExecutionPetriGraph()) {
						setLocation(operator.getExecutionPetriGraph(), offset, context);
					}
				}
			}
		}
	}

	private void setLocation(WKFObject object, Point2D offset, String context) {
		if (object != null) {
			object.setX(Math.max(object.getX(context) + offset.getX(), 0), context);
			object.setY(Math.max(object.getY(context) + offset.getY(), 0), context);
		}
	}

	public Vector<PetriGraphNode> getNodes() {
		return _nodes;
	}

	public void setNodes(Vector<PetriGraphNode> nodes) {
		_nodes = nodes;
	}

	public void addToNodes(PetriGraphNode aNode) {
		if (aNode.getLevel() != null) {
			if (_level != null) {
				if (_level != aNode.getLevel()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Inconsistent data in NodeCompound");
					}
					return;
				}
			} else {
				_level = aNode.getLevel();
			}
		}
		_nodes.add(aNode);
	}

	public void removeFromNodes(PetriGraphNode aNode) {
		_nodes.remove(aNode);
	}

	public Vector<WKFArtefact> getArtefacts() {
		return artefacts;
	}

	public void setArtefacts(Vector<WKFArtefact> artefacts) {
		this.artefacts = artefacts;
	}

	public void addToArtefacts(WKFArtefact artefact) {
		if (artefact.getLevel() != null) {
			if (_level != null) {
				if (_level != artefact.getLevel()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Inconsistent data in NodeCompound");
					}
					return;
				}
			} else {
				_level = artefact.getLevel();
			}
		}
		artefacts.add(artefact);
	}

	public void removeFromArtefacts(WKFArtefact artefact) {
		artefacts.remove(artefact);
	}

	@Override
	public FlexoLevel getLevel() {
		return _level;
	}

	@Override
	public final boolean delete() {
		super.delete();
		deleteObservers();
		return true;
	}

	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return EmptyVector.EMPTY_VECTOR(WKFObject.class);
	}

	public boolean isSingleNode() {
		return _nodes.size() == 1;
	}

	public PetriGraphNode getFirstNode() {
		if (isSingleNode()) {
			return _nodes.firstElement();
		}
		return null;
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
		returned.addAll(_nodes);
		returned.addAll(artefacts);
		return returned;
	}

	/**
	 * Return all operation components contained in this compound
	 * 
	 * @return a Vector of OperationNode
	 */
	public Vector<OperationNode> getAllOperationNodes() {
		Vector<OperationNode> returned = new Vector<OperationNode>();
		if (getLevel() == FlexoLevel.ACTIVITY) {
			for (Enumeration e = getNodes().elements(); e.hasMoreElements();) {
				AbstractNode node = (AbstractNode) e.nextElement();
				if (node instanceof AbstractActivityNode) {
					returned.addAll(((AbstractActivityNode) node).getAllOperationNodes());
				}
			}
		} else if (getLevel() == FlexoLevel.OPERATION) {
			for (Enumeration e = getNodes().elements(); e.hasMoreElements();) {
				AbstractNode node = (AbstractNode) e.nextElement();
				if (node instanceof OperationNode) {
					returned.add((OperationNode) node);
				}
			}
		}
		return returned;
	}

	/**
	 * Return all operation components contained in this compound containing a WOComponent
	 * 
	 * @return a Vector of OperationNode containing a WOComponent
	 */
	public Vector getAllOperationNodesContainingAWO() {
		Vector<OperationNode> returned = new Vector<OperationNode>();
		Vector<OperationNode> allOperationNodes = getAllOperationNodes();
		OperationNode cur = null;
		for (Enumeration<OperationNode> e = allOperationNodes.elements(); e.hasMoreElements();) {
			cur = e.nextElement();
			if (cur.getComponentInstance() != null && cur.getComponentInstance().getComponentDefinition() != null) {
				returned.add(cur);
			}
		}
		return returned;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "node_compound";
	}

}
