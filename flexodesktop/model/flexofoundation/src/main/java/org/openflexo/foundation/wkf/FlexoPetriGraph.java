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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.dm.ArtefactInserted;
import org.openflexo.foundation.wkf.dm.ArtefactRemoved;
import org.openflexo.foundation.wkf.dm.GroupInserted;
import org.openflexo.foundation.wkf.dm.GroupRemoved;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.edge.InvalidEdgeException;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.Node;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoPetriGraph extends WKFObject implements LevelledObject {

	private static final Logger logger = Logger.getLogger(FlexoPetriGraph.class.getPackage().getName());

	/**
	 * Stores all the node of this Petri Graph
	 */
	protected Vector<PetriGraphNode> _nodes;

	protected WKFObject _container;
	protected String containerContext;

	private Vector<WKFArtefact> artefacts;

	private Vector<WKFGroup> groups;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public FlexoPetriGraph(FlexoProcess process) {
		super(process);
		_nodes = new Vector<PetriGraphNode>();
		artefacts = new Vector<WKFArtefact>();
		groups = new Vector<WKFGroup>();
	}

	public static FlexoPetriGraph createPetriGraph(FlexoLevel level) {
		FlexoPetriGraph returned = null;
		if (level == FlexoLevel.ACTIVITY) {
			returned = new ActivityPetriGraph((FlexoProcess) null);
		} else if (level == FlexoLevel.OPERATION) {
			returned = new OperationPetriGraph((FlexoProcess) null);
		} else if (level == FlexoLevel.ACTION) {
			returned = new ActionPetriGraph((FlexoProcess) null);
		}
		return returned;
	}

	public void setContainer(WKFObject container, String context) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(" setContainer() for " + this.getClass().getName() + " with " + container.getClass().getName() + " with "
					+ getNodes().size() + " nodes");
		}
		_container = container;
		for (Enumeration<PetriGraphNode> e = getNodes().elements(); e.hasMoreElements();) {
			PetriGraphNode node = e.nextElement();
			node.setParentPetriGraph(this);
		}
	}

	public TokenEdge createTokenEdge(PetriGraphNode begin, PetriGraphNode end) {
		try {
			Node pc = null;
			if (end instanceof FlexoNode) {
				FlexoNode endNode = (FlexoNode) end;
				if (endNode.getPreConditions().size() == 0) {
					if (endNode instanceof FatherNode) {
						if (!((FatherNode) endNode).hasContainedPetriGraph()) {
							if (endNode instanceof AbstractActivityNode) {
								new OperationPetriGraph((AbstractActivityNode) endNode);
							} else if (endNode instanceof OperationNode) {
								new ActionPetriGraph((OperationNode) endNode);
							} else if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Unknown type of FatherNode " + end.getClass().getName());
							}
						}
						if (((FatherNode) end).hasContainedPetriGraph()) {
							Vector<FlexoNode> v = ((FatherNode) end).getContainedPetriGraph().getAllBeginNodes();
							FlexoNode attachedBeginNode;
							if (v.size() == 0) {
								attachedBeginNode = ((FatherNode) end).getContainedPetriGraph().createBeginNode(null);
							} else {
								attachedBeginNode = v.firstElement();
							}
							pc = new FlexoPreCondition(endNode, attachedBeginNode);
						} else {
							pc = new FlexoPreCondition(endNode);
						}
					} else {
						pc = new FlexoPreCondition(endNode);
					}
				} else {
					pc = endNode.getPreConditions().firstElement();
				}
			} else {
				pc = end;
			}
			return new TokenEdge(begin, pc);
		} catch (InvalidEdgeException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not create edge between begin and end node");
			}
			return null;
		}
	}

	public abstract FlexoNode createBeginNode(String context);

	public abstract FlexoNode createEndNode(String context);

	public WKFObject getContainer() {
		return _container;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getContainer() != null) {
			return getContainer().getFullyQualifiedName() + ".PETRI_GRAPH";
		}
		return "???";
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	public boolean isRootPetriGraph() {
		return getProcess() != null && getProcess().getActivityPetriGraph() == this;
	}

	@Override
	public boolean isContainedIn(WKFObject obj) {
		return getContainer().isContainedIn(obj);
	}

	@Override
	public boolean contains(WKFObject obj) {
		if (obj instanceof PetriGraphNode) {
			for (Enumeration<PetriGraphNode> en = _nodes.elements(); en.hasMoreElements();) {
				PetriGraphNode item = en.nextElement();
				if (item == obj) {
					return true;
				}
			}
			return false;
		}
		return super.contains(obj);
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(DropWKFElement.actionType);
		return returned;
	}

	public Vector<PetriGraphNode> getNodes() {
		return _nodes;
	}

	public Enumeration<PetriGraphNode> getSortedNodes() {
		disableObserving();
		PetriGraphNode[] o = FlexoIndexManager.sortArray(getNodes().toArray(new PetriGraphNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	/**
	 * Returns a sorted vector of all nodes of class <code>klass</code>, including the ones embedded by SelfExecutableNode and LOOPOperator,
	 * sorted according to the order defined in the petrigraphs. Whenever a node (a SelfExecutableNode or a LOOPOperator) embeds nodes, if
	 * the node matches the <code>klass</code> parameter, it is first inserted and then it adds all the matching embedded nodes, if
	 * relevant).
	 * 
	 * @param <T>
	 *            the type of the typed vector you want
	 * @param klass
	 *            a class that extends PetriGraphNode
	 * @return a sorted vector of all nodes of class <code>klass</code>, including the ones embedded by SelfExecutableNode and LOOPOperator.
	 */
	@SuppressWarnings("unchecked")
	public <T extends PetriGraphNode> Vector<T> getAllEmbeddedSortedNodesOfClass(Class<T> klass) {
		disableObserving();
		Vector<T> vector = new Vector<T>();
		Enumeration<PetriGraphNode> en = getSortedNodes();
		while (en.hasMoreElements()) {
			PetriGraphNode node = en.nextElement();
			if (klass.isAssignableFrom(node.getClass())) {
				vector.add((T) node);
			}
			if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {
				vector.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedSortedNodesOfClass(klass));
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {
				vector.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedSortedNodesOfClass(klass));
			}
		}
		return vector;
	}

	public void setNodes(Vector<PetriGraphNode> someNodes) {
		_nodes.removeAllElements();
		for (Enumeration<PetriGraphNode> en = someNodes.elements(); en.hasMoreElements();) {
			PetriGraphNode item = en.nextElement();
			addToNodes(item);
		}
	}

	public void addToNodes(PetriGraphNode aNode) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("insertNode() with " + aNode + " of " + aNode.getClass().getName());
		}
		if ((getLevel() != aNode.getLevel()) && (!(aNode instanceof OperatorNode)) && (!(aNode instanceof EventNode))) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Invalid level: cannot insert node");
			}
		} else {
			if (!_nodes.contains(aNode)) {
				_nodes.add(aNode);
				aNode.setParentPetriGraph(this);
				if (!isDeserializing() && !isCreatedByCloning()) {
					aNode.setIndexValue(aNode.getCollection().length);
					FlexoIndexManager.reIndexObjectOfArray(aNode.getCollection());
				}
				setChanged();
				notifyObservers(new NodeInserted(aNode, this, getContainer()));
				if (getProcess() != null) {
					getProcess().clearCachedObjects();
					// return true;
				}
			}
		}
		// return false;
	}

	public boolean removeFromNodes(PetriGraphNode node) {
		if (_nodes.contains(node)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Remove node as observer of PG");
			}
			for (WKFGroup group : getGroups()) {
				if (group.contains(node)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Remove node " + node + " from group " + group);
					}
					group.removeFromNodes(node);
					group.notifyGroupUpdated();
				}
			}
			_nodes.remove(node);
			node.setParentPetriGraph(null);
			setChanged();
			notifyObservers(new NodeRemoved(node));
			FlexoIndexManager.reIndexObjectOfArray(getNodes().toArray(new PetriGraphNode[0]));
			if (getProcess() != null) {
				getProcess().clearCachedObjects();
			}
			return true;
		}
		return false;
	}

	/**
	 * Return a vector of all BEGIN_NODE of this Petri Graph
	 * 
	 * @return Vector of FlexoNode
	 */
	public Vector<FlexoNode> getAllBeginNodes() {
		// TODO: optimize me later !!!
		Vector<FlexoNode> returned = new Vector<FlexoNode>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode current = en.nextElement();
			if (current instanceof FlexoNode) {
				if (((FlexoNode) current).isBeginNode()) {
					returned.add((FlexoNode) current);
				}
			}
		}
		return returned;
	}

	/**
	 * Returns all nodes of this petri graph that are instances or sub-class instances of the class <code>klass</code>
	 * 
	 * @param <T>
	 *            a typed parameter that extends PetriGraphNode so that the returned vector you received is properly typed
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T extends PetriGraphNode> Vector<T> getAllNodesOfClass(Class<T> klass) {
		Vector<T> returned = new Vector<T>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode current = en.nextElement();
			if (klass.isAssignableFrom(current.getClass())) {
				returned.add((T) current);
			}
		}
		return returned;
	}

	/**
	 * Returns all nodes of this petri graph that are instances or sub-class instances of the class <code>klass</code>
	 * 
	 * @param <T>
	 *            a typed parameter that extends PetriGraphNode so that the returned vector you received is properly typed
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T> Vector<T> getAllNodesOfInterface(Class<T> klass) {
		Vector<T> returned = new Vector<T>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode current = en.nextElement();
			if (klass.isAssignableFrom(current.getClass())) {
				returned.add((T) current);
			}
		}
		return returned;
	}

	/**
	 * Returns all embedded nodes of the same level of this petri graph that are instances or sub-class instances of the class
	 * <code>klass</code>
	 * 
	 * @param <T>
	 *            a typed parameter that extends PetriGraphNode so that the returned vector you received is properly typed
	 * @param klass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <T extends AbstractNode> Vector<T> getAllEmbeddedNodesOfClassOfSameLevel(Class<T> klass) {
		Vector<T> v = new Vector<T>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode node = en.nextElement();
			if (klass.isAssignableFrom(node.getClass())) {
				v.add((T) node);
			}
			if (node instanceof FatherNode && ((FatherNode) node).getContainedPetriGraph() != null
					&& ((FatherNode) node).getContainedPetriGraph().getLevel() == getLevel()) {
				v.addAll(((FatherNode) node).getContainedPetriGraph().getAllEmbeddedNodesOfClassOfSameLevel(klass));
			} else if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {// These are
																															// always of
																															// this level
				v.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedNodesOfClassOfSameLevel(klass));
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {// These are always of this
																												// level
				v.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedNodesOfClassOfSameLevel(klass));
			}
		}
		return v;
	}

	/**
	 * Returns a vector of all operator nodes that are embedded by this petri graph. This will drill down into all nodes embedded by this
	 * petri graph
	 * 
	 * @return all operator nodes embedded in this petri graph
	 */
	public final Vector<OperatorNode> getAllEmbeddedOperators() {
		Vector<OperatorNode> v = new Vector<OperatorNode>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			AbstractNode node = en.nextElement();
			if (node instanceof OperatorNode) {
				v.add((OperatorNode) node);
			}
			if (node instanceof FatherNode && ((FatherNode) node).getContainedPetriGraph() != null) {
				v.addAll(((FatherNode) node).getContainedPetriGraph().getAllEmbeddedOperators());
			} else if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {
				v.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedOperators());
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {
				v.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedOperators());
			}
		}
		return v;
	}

	public final Vector<OperatorNode> getAllEmbeddedOperatorsOfSameLevel() {
		Vector<OperatorNode> v = new Vector<OperatorNode>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode node = en.nextElement();
			if (node instanceof OperatorNode) {
				v.add((OperatorNode) node);
			}
			if (node instanceof FatherNode && ((FatherNode) node).getContainedPetriGraph() != null
					&& ((FatherNode) node).getContainedPetriGraph().getLevel() == getLevel()) {
				v.addAll(((FatherNode) node).getContainedPetriGraph().getAllEmbeddedOperatorsOfSameLevel());
			} else if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {// These are
																															// always of
																															// this level
				v.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedOperatorsOfSameLevel());
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {// These are always of this
																												// level
				v.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedOperatorsOfSameLevel());
			}
		}
		return v;
	}

	/**
	 * Returns all nodes embedded by this petri graph that are of the class <code>klass</code>.
	 * 
	 * @param <T>
	 * @param klass
	 * @return all nodes embedded by this petri graph that are of the class <code>klass</code>.
	 */
	@SuppressWarnings("unchecked")
	public final <T extends AbstractNode> Vector<T> getAllEmbeddedNodesOfClass(Class<T> klass) {
		Vector<T> v = new Vector<T>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode node = en.nextElement();
			if (klass.isAssignableFrom(node.getClass())) {
				v.add((T) node);
			}
			if (node instanceof FatherNode && ((FatherNode) node).getContainedPetriGraph() != null) {
				v.addAll(((FatherNode) node).getContainedPetriGraph().getAllEmbeddedNodesOfClass(klass));
			}
			if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {
				v.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedNodesOfClass(klass));
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {
				v.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedNodesOfClass(klass));
			}
		}
		return v;
	}

	/**
	 * Return a vector of all END_NODE of this Petri Graph
	 * 
	 * @return Vector of FlexoNode
	 */
	public Vector<FlexoNode> getAllEndNodes() {
		// TODO: optimize me later !!!
		Vector<FlexoNode> returned = new Vector<FlexoNode>();
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode current = en.nextElement();
			if (current instanceof FlexoNode) {
				if (((FlexoNode) current).isEndNode()) {
					returned.add((FlexoNode) current);
				}
			}
		}
		return returned;
	}

	public boolean hasOtherNodesThanBeginEndNodes() {
		for (PetriGraphNode node : getNodes()) {
			if (!(node instanceof FlexoNode)) {
				return true;
			}
			if (!((FlexoNode) node).isBeginOrEndNode()) {
				return true;
			}
		}
		return artefacts.size() > 0;
	}

	public Vector<AbstractNode> getAllEmbeddedAbstractNodes() {
		return getAllEmbeddedNodesOfClass(AbstractNode.class);
	}

	/**
	 * Return a vector of all Operator nodes of this Petri Graph
	 * 
	 * @return Vector of OperatorNode
	 */
	public final Vector<OperatorNode> getAllOperatorNodes() {
		return getAllNodesOfClass(OperatorNode.class);
	}

	/**
	 * Return a vector of all Operator nodes of this Petri Graph
	 * 
	 * @return Vector of OperatorNode
	 */
	public Vector<SelfExecutableNode> getAllSelfExecutableNodes() {
		return getAllNodesOfInterface(SelfExecutableNode.class);
	}

	/**
	 * Return a vector of all LOOPOperator nodes of this Petri Graph
	 * 
	 * @return Vector of LOOPOperator
	 */
	public Vector<LOOPOperator> getAllLoopOperators() {
		return getAllNodesOfClass(LOOPOperator.class);
	}

	/**
	 * Return a vector of all Event nodes of this Petri Graph
	 * 
	 * @return Vector of EventNode
	 */
	public Vector<EventNode> getAllEventNodes() {
		return getAllNodesOfClass(EventNode.class);
	}

	/**
	 * Return a vector of all BEGIN_NODE of this Petri Graph
	 * 
	 * @return Vector of FlexoNode
	 */
	public Vector<EventNode> getAllStartNodes() {
		Vector<EventNode> returned = getAllNodesOfClass(EventNode.class);
		Iterator<EventNode> i = returned.iterator();
		while (i.hasNext()) {
			EventNode current = i.next();
			if (!current.isStart()) {
				i.remove();
			}
		}
		return returned;
	}

	/**
	 * Returns all artefacts embedded by this petri graph
	 * 
	 * @return all artefacts embedded by this petri graph.
	 */
	public final Vector<WKFArtefact> getAllEmbeddedArtefacts() {
		Vector<WKFArtefact> v = new Vector<WKFArtefact>();
		v.addAll(artefacts);
		Enumeration<PetriGraphNode> en = _nodes.elements();
		while (en.hasMoreElements()) {
			PetriGraphNode node = en.nextElement();
			if (node instanceof FatherNode && ((FatherNode) node).getContainedPetriGraph() != null) {
				v.addAll(((FatherNode) node).getContainedPetriGraph().getAllEmbeddedArtefacts());
			}
			if (node instanceof SelfExecutableNode && ((SelfExecutableNode) node).getExecutionPetriGraph() != null) {
				v.addAll(((SelfExecutableNode) node).getExecutionPetriGraph().getAllEmbeddedArtefacts());
			} else if (node instanceof LOOPOperator && ((LOOPOperator) node).getExecutionPetriGraph() != null) {
				v.addAll(((LOOPOperator) node).getExecutionPetriGraph().getAllEmbeddedArtefacts());
			}
		}
		return v;
	}

	public OperatorNode getOperatorNodeNamed(String name) {
		if (name == null) {
			return null;
		}
		for (OperatorNode node : getAllOperatorNodes()) {
			if (name.equals(node.getName())) {
				return node;
			}
		}
		return null;
	}

	public abstract FlexoNode createNewBeginNode();

	public abstract FlexoNode createNewBeginNode(String nodeName);

	public abstract FlexoNode createNewEndNode();

	public abstract FlexoNode createNewEndNode(String nodeName);

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	@Override
	public void setIsVisible(boolean b) {
		if (b != getIsVisible()) {
			if (!b) {
				Enumeration<PetriGraphNode> en = getNodes().elements();
				PetriGraphNode item = null;
				while (en.hasMoreElements()) {
					item = en.nextElement();
					if (item instanceof FatherNode) {
						if (((FatherNode) item).getContainedPetriGraph() != null) {
							((FatherNode) item).getContainedPetriGraph().setIsVisible(b);
						}
					}
				}
			}
			super.setIsVisible(b);

			if (b) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Make PG visible");
				}
				setChanged();
				notifyObservers(new PetriGraphHasBeenOpened(this));
			}
		}
	}

	/**
	 * Returns the level of this FlexoPetriGraph
	 * 
	 * @see org.openflexo.foundation.wkf.LevelledObject#getLevel()
	 */
	@Override
	public abstract FlexoLevel getLevel();

	// ==========================================================================
	// ================================= Delete ===============================
	// ==========================================================================

	@Override
	public final void delete() {
		Enumeration<AbstractNode> en = new Vector<AbstractNode>(_nodes).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}
		Enumeration<WKFArtefact> en1 = new Vector<WKFArtefact>(artefacts).elements();
		while (en1.hasMoreElements()) {
			en1.nextElement().delete();
		}
		super.delete();
		deleteObservers();
		_container = null;
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during process deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		return getAllEmbeddedWKFObjects();
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
		Enumeration<AbstractNode> en = new Vector<AbstractNode>(_nodes).elements();
		while (en.hasMoreElements()) {
			returned.addAll((en.nextElement()).getAllEmbeddedDeleted());
		}
		for (WKFGroup group : getGroups()) {
			returned.addAll(group.getAllEmbeddedWKFObjects());
		}
		for (WKFArtefact artefact : getArtefacts()) {
			returned.addAll(artefact.getAllEmbeddedWKFObjects());
		}
		return returned;
	}

	public Vector<FlexoNode> getUnboundBeginNodes() {
		Vector<FlexoNode> allBeginNodes = getAllBeginNodes();
		Vector<FlexoNode> returned = new Vector<FlexoNode>();
		for (FlexoNode n : allBeginNodes) {
			if (n.getAttachedPreCondition() == null) {
				returned.add(n);
			}
		}
		return returned;
	}

	public Vector<FlexoNode> getBoundBeginNodes() {
		Vector<FlexoNode> allBeginNodes = getAllBeginNodes();
		Vector<FlexoNode> returned = new Vector<FlexoNode>();
		for (FlexoNode n : allBeginNodes) {
			if (n.getAttachedPreCondition() != null) {
				returned.add(n);
			}
		}
		return returned;
	}

	public Vector<WKFArtefact> getArtefacts() {
		return artefacts;
	}

	public void setArtefacts(Vector<WKFArtefact> artefacts) {
		this.artefacts = artefacts;
	}

	public void addToArtefacts(WKFArtefact annotation) {
		annotation.setParentPetriGraph(this);
		artefacts.add(annotation);
		setChanged();
		notifyObservers(new ArtefactInserted(annotation));
		if (getProcess() != null) {
			getProcess().clearCachedObjects();
		}
	}

	public void removeFromArtefacts(WKFArtefact annotation) {
		annotation.setParentPetriGraph(null);
		artefacts.remove(annotation);
		setChanged();
		notifyObservers(new ArtefactRemoved(annotation));
		if (getProcess() != null) {
			getProcess().clearCachedObjects();
		}
	}

	public Vector<WKFGroup> getGroups() {
		return groups;
	}

	public void setGroups(Vector<WKFGroup> groups) {
		this.groups = groups;
	}

	public void addToGroups(WKFGroup group) {
		group.setParentPetriGraph(this);
		groups.add(group);
		setChanged();
		notifyObservers(new GroupInserted(group));
	}

	public void removeFromGroups(WKFGroup group) {
		group.setParentPetriGraph(null);
		groups.remove(group);
		setChanged();
		notifyObservers(new GroupRemoved(group));
	}

	public void notifyNodeUngroup(WKFGroup group, Vector<PetriGraphNode> nodesThatWereInGroup) {
		setChanged();
		notifyObservers(new GroupRemoved(group, nodesThatWereInGroup));
	}

	public boolean acceptsObject(WKFObject object) {
		if (object instanceof LevelledObject) {
			return getLevel() == ((LevelledObject) object).getLevel() || ((LevelledObject) object).getLevel() == null;
		}
		return false;
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class PetriGraphMustHaveAtLeastOneBeginNode extends
			ValidationRule<PetriGraphMustHaveAtLeastOneBeginNode, FlexoPetriGraph> {
		public PetriGraphMustHaveAtLeastOneBeginNode() {
			super(FlexoPetriGraph.class, "petri_graph_must_have_at_least_one_begin_node");
		}

		@Override
		public ValidationIssue<PetriGraphMustHaveAtLeastOneBeginNode, FlexoPetriGraph> applyValidation(FlexoPetriGraph pg) {
			if (pg.getAllBeginNodes().size() == 0) {
				ValidationError<PetriGraphMustHaveAtLeastOneBeginNode, FlexoPetriGraph> error = new ValidationError<PetriGraphMustHaveAtLeastOneBeginNode, FlexoPetriGraph>(
						this, pg, "petri_graph_must_have_at_least_one_begin_node");
				String newBeginNodeName = "";
				if (pg instanceof ActivityPetriGraph) {
					newBeginNodeName = pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("begin_node"));
				} else if (pg instanceof OperationPetriGraph) {
					newBeginNodeName = pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("begin_node"),
							((OperationPetriGraph) pg).getContainer());
				} else if (pg instanceof ActionPetriGraph) {
					newBeginNodeName = pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("begin_node"),
							((ActionPetriGraph) pg).getContainer());
				}
				error.addToFixProposals(new CreateNewBeginNode(newBeginNodeName));
				return error;
			}
			return null;
		}

		public class CreateNewBeginNode extends ParameteredFixProposal<PetriGraphMustHaveAtLeastOneBeginNode, FlexoPetriGraph> {
			public CreateNewBeginNode(String newBeginNodeName) {
				super("create_new_begin_node", "newBeginNodeName", "enter_a_name_for_the_new_begin_node", newBeginNodeName);
			}

			@Override
			protected void fixAction() {
				String newBeginNodeName = (String) getValueForParameter("newBeginNodeName");
				FlexoPetriGraph pg = getObject();
				if (pg instanceof ActivityPetriGraph) {
					((ActivityPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				} else if (pg instanceof OperationPetriGraph) {
					((OperationPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				} else if (pg instanceof ActionPetriGraph) {
					((ActionPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				}
			}
		}

	}

	public static class ExecutionPetriGraphMustHaveExactelyOneBeginNode extends
			ValidationRule<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> {
		public ExecutionPetriGraphMustHaveExactelyOneBeginNode() {
			super(FlexoPetriGraph.class, "execution_petri_graph_must_have_exactely_one_begin_node");
		}

		@Override
		public ValidationIssue<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> applyValidation(FlexoPetriGraph pg) {
			if (pg.getContainer() instanceof SelfExecutableNode || pg.getContainer() instanceof LOOPOperator) {
				if (pg.getAllBeginNodes().size() == 0) {
					ValidationError<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> error = new ValidationError<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph>(
							this, pg, "execution_petri_graph_must_have_exactely_one_begin_node");
					error.addToFixProposals(new CreateBeginNode(pg));
					return error;
				} else if (pg.getAllBeginNodes().size() > 1) {
					ValidationError<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> error = new ValidationError<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph>(
							this, pg, "execution_petri_graph_must_have_exactely_one_begin_node");
					for (PetriGraphNode node : pg.getAllBeginNodes()) {
						error.addToFixProposals(new ChooseBeginNode(pg, node));
					}
					return error;
				}
			}
			return null;
		}

		public static class ChooseBeginNode extends FixProposal<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> {
			private PetriGraphNode node;

			public ChooseBeginNode(FlexoPetriGraph pg, PetriGraphNode nodeThatWillBeKept) {
				super("choose_node_($node.name)_and_remove_others");
				node = nodeThatWillBeKept;
			}

			public PetriGraphNode getNode() {
				return node;
			}

			@Override
			protected void fixAction() {
				Vector<PetriGraphNode> allNodes = new Vector<PetriGraphNode>(getObject().getAllBeginNodes());
				for (PetriGraphNode n : allNodes) {
					if (n != node) {
						n.delete();
					}
				}
			}
		}

		public static class CreateBeginNode extends FixProposal<ExecutionPetriGraphMustHaveExactelyOneBeginNode, FlexoPetriGraph> {
			public CreateBeginNode(FlexoPetriGraph pg) {
				super("create_new_begin_node");
			}

			@Override
			protected void fixAction() {
				// TODO: use FlexoAction
				getObject().createNewBeginNode();
			}
		}

	}

	public static class ExecutionPetriGraphMustHaveExactelyOneEndNode extends
			ValidationRule<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> {
		public ExecutionPetriGraphMustHaveExactelyOneEndNode() {
			super(FlexoPetriGraph.class, "execution_petri_graph_must_have_exactely_one_end_node");
		}

		@Override
		public ValidationIssue<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> applyValidation(FlexoPetriGraph pg) {
			if (pg.getContainer() instanceof SelfExecutableNode || pg.getContainer() instanceof LOOPOperator) {
				if (pg.getAllEndNodes().size() == 0) {
					ValidationError<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> error = new ValidationError<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph>(
							this, pg, "execution_petri_graph_must_have_exactely_one_end_node");
					error.addToFixProposals(new CreateEndNode(pg));
					return error;
				} else if (pg.getAllEndNodes().size() > 1) {
					ValidationError<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> error = new ValidationError<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph>(
							this, pg, "execution_petri_graph_must_have_exactely_one_end_node");
					for (PetriGraphNode node : pg.getAllEndNodes()) {
						error.addToFixProposals(new ChooseEndNode(pg, node));
					}
					return error;
				}
			}
			return null;
		}

		public static class ChooseEndNode extends FixProposal<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> {
			private PetriGraphNode node;

			public ChooseEndNode(FlexoPetriGraph pg, PetriGraphNode nodeThatWillBeKept) {
				super("choose_node_($node.name)_and_remove_others");
				node = nodeThatWillBeKept;
			}

			public PetriGraphNode getNode() {
				return node;
			}

			@Override
			protected void fixAction() {
				Vector<PetriGraphNode> allNodes = new Vector<PetriGraphNode>(getObject().getAllEndNodes());
				for (PetriGraphNode n : allNodes) {
					if (n != node) {
						n.delete();
					}
				}
			}
		}

		public static class CreateEndNode extends FixProposal<ExecutionPetriGraphMustHaveExactelyOneEndNode, FlexoPetriGraph> {
			public CreateEndNode(FlexoPetriGraph pg) {
				super("create_new_end_node");
			}

			@Override
			protected void fixAction() {
				// TODO: use FlexoAction
				getObject().createNewEndNode();
			}
		}

	}

	public int getIndexForBeginNode(PetriGraphNode beginNode) {
		int idx = 0;
		for (FlexoNode n : getAllBeginNodes()) {
			if (n == beginNode) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public int getIndexForEndNode(FlexoNode endNode) {
		int idx = 0;
		for (FlexoNode n : getAllEndNodes()) {
			if (n == endNode) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public int getIndexForNormalNode(PetriGraphNode node) {
		int idx = 0;
		for (FlexoNode n : getAllNodesOfClass(FlexoNode.class)) {
			if (n == node) {
				return idx;
			}
			if (n.isNormalNode()) {
				idx++;
			}
		}
		return -1;
	}

}
