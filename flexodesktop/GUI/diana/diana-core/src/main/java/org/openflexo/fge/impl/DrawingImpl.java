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
package org.openflexo.fge.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.ContainerGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.GeometricGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.notifications.DrawingTreeNodeHierarchyRebuildEnded;
import org.openflexo.fge.notifications.DrawingTreeNodeHierarchyRebuildStarted;

/**
 * This class is the default implementation for all objects representing a graphical drawing, that is a complex graphical representation
 * involving an object tree where all objects have their own graphical representation.
 * 
 * @author sylvain
 * 
 * @param <M>
 *            Type of object which is handled as root object
 */
public abstract class DrawingImpl<M> extends Observable implements Drawing<M> {

	static final Logger logger = Logger.getLogger(DrawingImpl.class.getPackage().getName());

	private Hashtable<GRBinding<?, ?>, Hashtable<Object, DrawingTreeNode<?, ?>>> nodes;
	private RootNodeImpl<M> rootNode;
	private M model;
	private List<PendingConnector<?>> pendingConnectors;

	private DrawingGRBinding<M> drawingBinding;

	private boolean editable = true;

	private FGEModelFactory factory;
	private PersistenceMode persistenceMode;

	public DrawingImpl(M model, FGEModelFactory factory, PersistenceMode persistenceMode) {
		this.model = model;
		this.factory = factory;
		this.persistenceMode = persistenceMode;
		nodes = new Hashtable<GRBinding<?, ?>, Hashtable<Object, DrawingTreeNode<?, ?>>>();
		pendingConnectors = new ArrayList<PendingConnector<?>>();
		init();
	}

	public abstract void init();

	@Override
	public PersistenceMode getPersistenceMode() {
		return persistenceMode;
	}

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;

	}

	@Override
	public RootNode<M> getRoot() {
		if (rootNode == null) {
			rootNode = buildRoot();
			updateGraphicalObjectsHierarchy();
		}
		return rootNode;
	}

	private RootNodeImpl<M> buildRoot() {
		if (drawingBinding != null) {
			RootNodeImpl<M> _root = new RootNodeImpl<M>(this, model, drawingBinding);
			Hashtable<Object, DrawingTreeNode<?, ?>> hash = retrieveHash(drawingBinding);
			hash.put(model, _root);
			return _root;
		} else {
			return null;
		}
	}

	protected Hashtable<Object, DrawingTreeNode<?, ?>> retrieveHash(GRBinding<?, ?> grBinding) {
		Hashtable<Object, DrawingTreeNode<?, ?>> hash = nodes.get(grBinding);
		if (hash == null) {
			hash = new Hashtable<Object, DrawingTreeNode<?, ?>>();
			nodes.put(grBinding, hash);
		}
		return hash;
	}

	private DrawingTreeNode<?, ?> retrieveDrawingTreeNode(GRBinding<?, ?> grBinding, Object drawable) {
		Hashtable<Object, DrawingTreeNode<?, ?>> hash = retrieveHash(grBinding);
		return hash.get(drawable);
	}

	@Override
	public DrawingGRBinding<M> bindDrawing(Class<M> drawingClass, String name, DrawingGRProvider<M> grProvider) {
		return drawingBinding = new DrawingGRBinding<M>(name, drawingClass, grProvider);
	}

	@Override
	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ShapeGRProvider<R> grProvider) {
		ShapeGRBinding<R> returned = new ShapeGRBinding<R>(name, shapeObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			ShapeGRProvider<R> grProvider) {
		ShapeGRBinding<R> returned = new ShapeGRBinding<R>(name, shapeObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, GeometricGRProvider<R> grProvider) {
		GeometricGRBinding<R> returned = new GeometricGRBinding<R>(name, geometricObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> GeometricGRBinding<R> bindGeometric(Class<R> geometricObjectClass, String name, ContainerGRBinding<?, ?> parentBinding,
			GeometricGRProvider<R> grProvider) {
		GeometricGRBinding<R> returned = new GeometricGRBinding<R>(name, geometricObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ConnectorGRProvider<R> grProvider) {
		ConnectorGRBinding<R> returned = new ConnectorGRBinding<R>(name, connectorObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ConnectorGRProvider<R> grProvider) {
		ConnectorGRBinding<R> returned = new ConnectorGRBinding<R>(name, connectorObjectClass, grProvider);
		return returned;
	}

	@Override
	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding, ContainerGRBinding<?, ?> parentBinding, ConnectorGRProvider<R> grProvider) {
		ConnectorGRBinding<R> returned = new ConnectorGRBinding<R>(name, connectorObjectClass, grProvider);
		return returned;
	}

	/**
	 * Retrieve first drawing tree node matching supplied drawable<br>
	 * Note that GRBinding is not specified here, so if a given drawable is represented through multiple GRBinding, there is no guarantee
	 * that you receive the right object. Use {@link #getDrawingTreeNode(Object, GRBinding)} instead
	 * 
	 * @param aDrawable
	 * @return
	 */
	@Override
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O drawable) {
		for (GRBinding grBinding : nodes.keySet()) {
			DrawingTreeNode<O, GR> returned = getDrawingTreeNode(drawable, grBinding);
			if (returned != null) {
				return returned;
			}
		}
		return null;
	}

	/**
	 * Retrieve drawing tree node matching supplied drawable and grBinding<br>
	 * GRBinding value should not be null
	 * 
	 * @param aDrawable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <O, GR extends GraphicalRepresentation> DrawingTreeNode<O, GR> getDrawingTreeNode(O drawable, GRBinding<O, GR> grBinding) {
		Hashtable<Object, DrawingTreeNode<?, ?>> hash = retrieveHash(grBinding);
		return (DrawingTreeNode<O, GR>) hash.get(drawable);
	}

	/**
	 * Retrieve drawing tree node matching supplied identifier<br>
	 * If GRBinding is null, return first DrawingTreeNode representing supplied drawable
	 * 
	 * @param identifier
	 * @return
	 */
	@Override
	public <O> org.openflexo.fge.Drawing.DrawingTreeNode<O, ?> getDrawingTreeNode(
			org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier<O> identifier) {
		if (identifier.getGRBinding() == null) {
			return getDrawingTreeNode(identifier.getDrawable());
		}
		return getDrawingTreeNode(identifier.getDrawable(), identifier.getGRBinding());
	}

	/*public Enumeration<GraphicalRepresentation> getAllGraphicalRepresentations() {
		Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		for (Enumeration<DrawingTreeNode<?>> en = _hashMap.elements(); en.hasMoreElements();) {
			returned.add(en.nextElement().getGraphicalRepresentation());
		}
		return returned.elements();
	}*/

	/*public Enumeration<GraphicalRepresentation> getAllSortedGraphicalRepresentations() {
		Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		for (Enumeration<DrawingTreeNode<?>> en = getAllSortedNodes(); en.hasMoreElements();) {
			returned.add(en.nextElement().getGraphicalRepresentation());
		}
		return returned.elements();
	}*/

	/*private Enumeration<DrawingTreeNode<?>> getAllSortedNodes() {
		Vector<DrawingTreeNode<?>> dtnList = new Vector<DrawingTreeNode<?>>();
		for (Enumeration<DrawingTreeNode<?>> en = _hashMap.elements(); en.hasMoreElements();) {
			dtnList.add(en.nextElement());
		}
		Collections.sort(dtnList, new Comparator<DrawingTreeNode<?>>() {
			@Override
			public int compare(DrawingTreeNode<?> o1, DrawingTreeNode<?> o2) {
				int res = o2.getDepth() - o1.getDepth();
				if (res != 0) {
					return res;
				}
				if (o2.parentNode == o1.parentNode) {
					return o2.parentNode.childNodes.indexOf(o1) - o2.parentNode.childNodes.indexOf(o2);
				}
				// 1. We first find a common ancestor
				DrawingTreeNode<?> ancestor = o1.getCommonAncestor(o2);
				if (ancestor != null) {
					DrawingTreeNode<?> p1 = null, p2 = null;
					// 2. We look for a direct child of the common ancestor which is an ancestor of the compared nodes
					DrawingTreeNode<?> c1 = o1;
					while (c1.parentNode != ancestor) {
						c1 = c1.parentNode;
					}
					p1 = c1;
					DrawingTreeNode<?> c2 = o2;
					while (c2.parentNode != ancestor) {
						c2 = c2.parentNode;
					}
					p2 = c2;
					// 3. We now return the difference of index between those 2.
					if (p1 != null && p2 != null) {
						return ancestor.childNodes.indexOf(p1) - ancestor.childNodes.indexOf(p2);
					}
				}
				if (o1.isAncestorOf(o2)) {
					return -1;
				}
				if (o2.isAncestorOf(o1)) {
					return 1;
				}
				logger.warning("Could not compare " + o1 + " and " + o2);
				return 0;
			}
		});
		//Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		//for (Enumeration<DrawingTreeNode> en = dtnList.elements(); en.hasMoreElements();) {
		//	returned.add(en.nextElement().graphicalRepresentation);
		//}
		return dtnList.elements();
	}*/

	/**
	 * This flag indicates that object hierarchy rebuilding has been started somewhere
	 */
	// private boolean isUpdatingObjectHierarchy = false;

	// private Vector<DrawingTreeNode<?>> nodesToUpdate;
	// private Vector<DrawingTreeNode<?>> nodesToNotifyAdding;

	// private boolean isGraphicalHierarchyEnabled = true;
	// private boolean isGraphicalHierarchyDirty = true;

	/*public final void enableGraphicalHierarchy() {
		isGraphicalHierarchyEnabled = true;
		if (isGraphicalHierarchyDirty) {
			updateGraphicalObjectsHierarchy();
		}
	}

	public final void disableGraphicalHierarchy() {
		isGraphicalHierarchyEnabled = false;
	}*/

	private void fireGraphicalObjectHierarchyRebuildStarted() {
		setChanged();
		notifyObservers(new DrawingTreeNodeHierarchyRebuildStarted(this));
	}

	private void fireGraphicalObjectHierarchyRebuildEnded() {
		setChanged();
		notifyObservers(new DrawingTreeNodeHierarchyRebuildEnded(this));
	}

	/**
	 * Update the whole tree of graphical object hierarchy<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 */
	@Override
	public final void updateGraphicalObjectsHierarchy() {

		fireGraphicalObjectHierarchyRebuildStarted();
		updateGraphicalObjectsHierarchy(getRoot());
		fireGraphicalObjectHierarchyRebuildEnded();

	}

	/**
	 * Update of all DrawingTreeNode representing supplied drawable<br>
	 * Recursively navigate in the tree to find invalidated nodes. Only invalidated nodes are recomputed (and eventually rebuild if the
	 * graphical object hierarchy structure has changed)
	 * 
	 * @param drawable
	 */
	@Override
	public final <O> void updateGraphicalObjectsHierarchy(O drawable) {
		fireGraphicalObjectHierarchyRebuildStarted();
		for (DrawingTreeNode<O, ?> dtn : getDrawingTreeNodes(drawable)) {
			// dtn.invalidate();
			updateGraphicalObjectsHierarchy(dtn);
		}
		fireGraphicalObjectHierarchyRebuildEnded();
	}

	/**
	 * Invalidate the whole hierarchy.<br>
	 * All nodes of drawing tree are invalidated, which means that a complete recomputing of the whole tree will be performed during next
	 * updateGraphicalHierarchy() call<br>
	 * Existing graphical representation are kept.
	 */
	@Override
	public void invalidateGraphicalObjectsHierarchy() {
		invalidateGraphicalObjectsHierarchy(getModel());
	}

	/**
	 * Invalidate the graphical object hierarchy under all nodes where supplied object is represented.<br>
	 * All nodes of drawing tree under supplied node are invalidated, which means that a recomputing of the whole tree under one (or many)
	 * node (the nodes representing supplied drawable) will be performed during next updateGraphicalObjectHierarchy() call.<br>
	 * 
	 */
	@Override
	public <O> void invalidateGraphicalObjectsHierarchy(O drawable) {
		for (DrawingTreeNode<O, ?> dtn : getDrawingTreeNodes(drawable)) {
			dtn.invalidate();
		}
	}

	private <O> List<DrawingTreeNode<O, ?>> getDrawingTreeNodes(O drawable) {
		List<DrawingTreeNode<O, ?>> returned = new ArrayList<DrawingTreeNode<O, ?>>();
		for (GRBinding<?, ?> grBinding : nodes.keySet()) {
			if (getDrawingTreeNode(drawable, (GRBinding<O, ?>) grBinding) != null) {
				returned.add(getDrawingTreeNode(drawable, (GRBinding<O, ?>) grBinding));
			}
			// TODO: fix ClassCastException (try/catch then ignore)
		}
		return returned;
	}

	public void printGraphicalObjectHierarchy() {
		logger.info("Graphical object hierarchy");
		if (getRoot() != null) {
			_printGraphicalObjectHierarchy((RootNodeImpl<?>) getRoot(), 0);
		} else {
			logger.info(" > Root node is null !");
		}
	}

	private void _printGraphicalObjectHierarchy(DrawingTreeNodeImpl<?, ?> dtn, int level) {
		String nodePrettyPrint = dtn.toString();
		/*if (dtn instanceof RootNode) {
			nodePrettyPrint = "Root[" + ((RootNode<?>) dtn).getWidth() + "x" + ((RootNode<?>) dtn).getHeight() + "]:" + dtn.getDrawable();
		} else if (dtn instanceof ShapeNode) {
			nodePrettyPrint = "Shape-" + dtn.getIndex() + "[" + ((ShapeNode<?>) dtn).getX() + ";" + ((ShapeNode<?>) dtn).getY() + "]["
					+ ((ShapeNode<?>) dtn).getWidth() + "x" + ((ShapeNode<?>) dtn).getHeight() + "][" + ((ShapeNode<?>) dtn).getFGEShape()
					+ "]:" + dtn.getDrawable();
		} else if (dtn instanceof ConnectorNode) {
			nodePrettyPrint = "Connector-" + dtn.getIndex() + "[Shape-" + ((ConnectorNode<?>) dtn).getStartNode().getIndex() + "][Shape-"
					+ ((ConnectorNode<?>) dtn).getEndNode().getIndex() + "]:" + dtn.getDrawable();
		}*/
		logger.info(buildWhiteSpaceIndentation(level * 5) + " > " + nodePrettyPrint);
		if (dtn instanceof ContainerNode) {
			if (((ContainerNode<?, ?>) dtn).getChildNodes() != null) {
				for (DrawingTreeNode<?, ?> child : ((ContainerNode<?, ?>) dtn).getChildNodes()) {
					if (child instanceof DrawingTreeNodeImpl) {
						_printGraphicalObjectHierarchy((DrawingTreeNodeImpl<?, ?>) child, level + 1);
					}
				}
			}
		}
	}

	public static String buildString(char c, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String buildWhiteSpaceIndentation(int length) {
		return buildString(' ', length);
	}

	/*private final void buildGraphicalObjectsHierarchy(M model, boolean force) {
		if (getRoot() == null) {
			return;
		}
		// Now root has been recomputed
		if (force) {
			getRoot().invalidate();
		}
		updateGraphicalObjectsHierarchy(getRoot());
	}*/

	/**
	 * Internally called to update graphical object hierarchy at a given node
	 * 
	 * @param dtn
	 */
	private final <O> void updateGraphicalObjectsHierarchy(DrawingTreeNode<O, ?> dtn) {
		if (dtn.isInvalidated()) {
			System.out.println("Updating " + dtn);
			GRBinding<O, ? extends GraphicalRepresentation> grBinding = dtn.getGRBinding();
			// List<DrawingTreeNode<?, ?>> nodesToRemove;
			// if (dtn instanceof ContainerNode) {
			// nodesToRemove = new ArrayList<DrawingTreeNode<?, ?>>(((ContainerNode<?, ?>) dtn).getChildNodes());

			List<DrawingTreeNode<?, ?>> createdNodes = new ArrayList<DrawingTreeNode<?, ?>>();
			List<DrawingTreeNode<?, ?>> deletedNodes = new ArrayList<DrawingTreeNode<?, ?>>();
			List<DrawingTreeNode<?, ?>> updatedNodes = new ArrayList<DrawingTreeNode<?, ?>>();

			// boolean wasUpdatingObjectHierarchy = isUpdatingObjectHierarchy;

			/*if (!wasUpdatingObjectHierarchy) {
				isUpdatingObjectHierarchy = true;
			}*/

			for (GRStructureWalker<O> walker : grBinding.getWalkers()) {
				walker.startWalking(dtn);
				walker.walk(dtn.getDrawable());
				walker.stopWalking();
				createdNodes.addAll(walker.getCreatedNodes());
				createdNodes.addAll(walker.getDeletedNodes());
				createdNodes.addAll(walker.getUpdatedNodes());
			}
			for (DrawingTreeNode<?, ?> nodeToRemove : deletedNodes) {
				deleteNode(nodeToRemove);
			}
			for (DrawingTreeNode<?, ?> createdNode : createdNodes) {
				updateGraphicalObjectsHierarchy(createdNode);
				// nodesToRemove.remove(updatedNode);
			}
			for (DrawingTreeNode<?, ?> updatedNode : updatedNodes) {
				updateGraphicalObjectsHierarchy(updatedNode);
				// nodesToRemove.remove(updatedNode);
			}
			// } else {
			// nodesToRemove = Collections.emptyList();
			// }

			// Try now to handle pending connectors
			for (PendingConnector<?> pendingConnector : new ArrayList<PendingConnector<?>>(pendingConnectors)) {
				if (pendingConnector.tryToResolve(this)) {
					System.out.println("Resolved " + pendingConnector);
					pendingConnectors.remove(pendingConnector);
				}
			}

			((DrawingTreeNodeImpl<?, ?>) dtn).validate();

			/*if (!wasUpdatingObjectHierarchy) {
				isUpdatingObjectHierarchy = false;
			}*/
		} else {
			if (dtn instanceof ContainerNode) {
				for (DrawingTreeNode<?, ?> child : ((ContainerNode<?, ?>) dtn).getChildNodes()) {
					updateGraphicalObjectsHierarchy(child);
				}
			}
		}
	}

	/*public <O> void updateDrawable(O aDrawable) {
		DrawingTreeNode<?> alreadyExistingNode = _hashMap.get(aDrawable);
		if (alreadyExistingNode == null) {
			logger.warning("Cannot find DrawingTreeNode for " + aDrawable);
			return;
		}
		if (alreadyExistingNode.parentNode != null) {
			Object parentDrawable = alreadyExistingNode.parentNode.drawable;
			removeDrawable(aDrawable, parentDrawable);
			addDrawable(aDrawable, parentDrawable);
		}
	}*/

	private <O> boolean deleteNode(DrawingTreeNode<?, ?> node) {
		ContainerNode<?, ?> parentNode = node.getParentNode();
		node.delete();
		if (parentNode != null) {
			parentNode.notifyNodeRemoved(node);
		}
		return true;
	}

	public void notifyNodeAdded(DrawingTreeNode<?, ?> addedNode, ContainerNode<?, ?> parentNode) {
		logger.info(">>>> Added node: " + addedNode);
		// See parentNode.notifyDrawableAdded(removedGR);
		parentNode.notifyNodeAdded(addedNode);
	}

	public void notifyNodeRemoved(DrawingTreeNode<?, ?> removedNode, ContainerNode<?, ?> parentNode) {
		logger.info(">>>> Removed node: " + removedNode);
		// See parentNode.notifyDrawableRemoved(removedGR);
		parentNode.notifyNodeRemoved(removedNode);
	}

	@Override
	public <O> ShapeNode<O> createNewShapeNode(ContainerNode<?, ?> parentNode, ShapeGRBinding<O> binding, O drawable) {

		System.out.println("draw shape with " + binding + " drawable=" + drawable + " parent=" + parentNode /*+ " isUpdatingObjectHierarchy="
																											+ isUpdatingObjectHierarchy*/);

		if (parentNode == null) {
			logger.warning("Cannot register drawable above null parent");
			return null;
		}

		ShapeNodeImpl<O> returned = new ShapeNodeImpl<O>(this, drawable, binding, (ContainerNodeImpl<?, ?>) parentNode);
		parentNode.addChild(returned);

		// Now start to observe drawable for drawing structural modifications
		returned.startDrawableObserving();

		// if (isUpdatingObjectHierarchy) {
		notifyNodeAdded(returned, parentNode);
		// }
		return returned;

		/*if (parentNode.childs.contains(aDrawable)) {
			DrawingTreeNode<?> alreadyExistingNode = _hashMap.get(aDrawable);
			if (alreadyExistingNode == null) {
				logger.warning("Cannot find DrawingTreeNode for " + aDrawable);
				return;
			}
			if (parentNode.nodesToRemove == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("parentNode.nodesToRemove == null");
					logger.fine("Adding drawable " + aDrawable + " under " + aParentDrawable);
				}
			}
			parentNode.nodesToRemove.remove(alreadyExistingNode);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("No need to insert " + aDrawable + " under " + aParentDrawable + " as it is already done");
			}
			return;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("add drawable " + aDrawable + " under " + aParentDrawable);
			}
			new DrawingTreeNode<O>(aDrawable, aParentDrawable);
			if (getGraphicalRepresentation(aDrawable) == null) {
				logger.warning("Could not find graphical representation for " + aDrawable);
			} else {
				if (isUpdatingObjectHierarchy) {
					graphicalRepresentationToNotifyAdding.add(getGraphicalRepresentation(aDrawable));
				} else {
					// Do it now
					getGraphicalRepresentation(aDrawable).setValidated(true);
					getGraphicalRepresentation(aParentDrawable).notifyDrawableAdded(getGraphicalRepresentation(aDrawable));
				}
			}
		}*/
	}

	@Override
	public <O> ConnectorNode<O> createNewConnectorNode(ContainerNode<?, ?> parentNode, ConnectorGRBinding<O> binding, O drawable,
			ShapeNode<?> fromNode, ShapeNode<?> toNode) {

		System.out.println("draw connector with " + binding + " drawable=" + drawable + " parent=" + parentNode + " fromNode=" + fromNode
				+ " toNode=" + toNode);

		if (parentNode == null) {
			logger.warning("Cannot register drawable above null parent");
			return null;
		}

		ConnectorNodeImpl<O> returned = new ConnectorNodeImpl<O>(this, drawable, binding, (ContainerNodeImpl<?, ?>) parentNode);
		returned.setStartNode((ShapeNodeImpl<?>) fromNode);
		returned.setEndNode((ShapeNodeImpl<?>) toNode);

		// Now start to observe drawable for drawing structural modifications
		returned.startDrawableObserving();

		parentNode.addChild(returned);
		// if (isUpdatingObjectHierarchy) {
		notifyNodeAdded(returned, parentNode);
		// }
		return returned;

	}

	@Override
	public <O> boolean hasPendingConnector(ConnectorGRBinding<O> binding, O drawable, DrawingTreeNodeIdentifier<?> parentNodeIdentifier,
			DrawingTreeNodeIdentifier<?> startNodeIdentifier, DrawingTreeNodeIdentifier<?> endNodeIdentifier) {
		return getPendingConnector(binding, drawable, parentNodeIdentifier, startNodeIdentifier, endNodeIdentifier) != null;
	}

	@Override
	public <O> PendingConnector<O> getPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier) {
		for (PendingConnector<?> pendingConnector : pendingConnectors) {
			if (pendingConnector.getConnectorNode().getDrawable() == drawable
					&& pendingConnector.getConnectorNode().getGRBinding() == binding
					&& pendingConnector.getParentNodeIdentifier().equals(parentNodeIdentifier)
					&& pendingConnector.getStartNodeIdentifier().equals(startNodeIdentifier)
					&& pendingConnector.getEndNodeIdentifier().equals(endNodeIdentifier)) {
				return (PendingConnector<O>) pendingConnector;
			}
		}
		return null;
	}

	@Override
	public <O> PendingConnector<O> createPendingConnector(ConnectorGRBinding<O> binding, O drawable,
			DrawingTreeNodeIdentifier<?> parentNodeIdentifier, DrawingTreeNodeIdentifier<?> startNodeIdentifier,
			DrawingTreeNodeIdentifier<?> endNodeIdentifier) {
		ContainerNode<?, ?> parentNode = (ContainerNode<?, ?>) getDrawingTreeNode(parentNodeIdentifier);
		// ShapeNode<?> startNode = (ShapeNode<?>) getDrawingTreeNode(startNodeIdentifier);
		// ShapeNode<?> endNode = (ShapeNode<?>) getDrawingTreeNode(endNodeIdentifier);
		ConnectorNodeImpl<O> connectorNode = new ConnectorNodeImpl<O>(this, drawable, binding, (ContainerNodeImpl<?, ?>) parentNode);
		// ConnectorNode<O> connectorNode = createNewConnector(parentNode, binding, drawable, startNode, endNode);
		PendingConnector<O> returned = new PendingConnectorImpl<O>(connectorNode, parentNodeIdentifier, startNodeIdentifier,
				endNodeIdentifier);
		pendingConnectors.add(returned);
		System.out.println("Nouveau pending connector, " + returned);
		return returned;
	}

	/*public <O> void removeDrawable(O aDrawable, Object aParentDrawable) {
		if (aParentDrawable == null) {
			logger.warning("Cannot unregister drawable above null parent");
			return;
		}
		DrawingTreeNode<?> parentNode = _hashMap.get(aParentDrawable);
		if (parentNode == null) {
			logger.warning("Cannot find DrawingTreeNode for " + aParentDrawable);
			return;
		}
		if (!parentNode.childs.contains(aDrawable)) {
			logger.warning("Cannot remove " + aDrawable + " under " + aParentDrawable + " as it is not registered");
			return;
		} else {
			DrawingTreeNode<?> nodeToRemove = _hashMap.get(aDrawable);
			if (nodeToRemove == null) {
				logger.warning("Cannot find DrawingTreeNode for " + nodeToRemove);
				return;
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("remove drawable " + aDrawable + " under " + aParentDrawable);
			}
			GraphicalRepresentation parentNode = getGraphicalRepresentation(aParentDrawable);
			GraphicalRepresentation removedGR = getGraphicalRepresentation(aDrawable);
			parentNode.removeChild(nodeToRemove);
			parentNode.notifyDrawableRemoved(removedGR);
			removedGR.delete();
		}
	}*/

	/*protected void beginUpdateObjectHierarchy() {

		// System.out.println("*************** Hop, DEBUT pour " + this);

		nodesToUpdate = new Vector<DrawingTreeNode<?>>();
		Enumeration<DrawingTreeNode<?>> allNodes = getAllSortedNodes();
		while (allNodes.hasMoreElements()) {
			DrawingTreeNode<?> next = allNodes.nextElement();
			if (next.getGraphicalRepresentation() != null) {
				// logger.info("What about "+next.getClass().getSimpleName());
				next.getGraphicalRepresentation().notifyObjectHierarchyWillBeUpdated();
			}
			nodesToUpdate.add(next);
		}
		isUpdatingObjectHierarchy = true;

		for (DrawingTreeNode<?> n : nodesToUpdate) {
			if (n.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
				ConnectorGraphicalRepresentation connector = (ConnectorGraphicalRepresentation) n.getGraphicalRepresentation();
				if (!connector.isConnectorConsistent()) {
					n.invalidate();
					continue;
				}
				DrawingTreeNode<?> startTreeNode = _hashMap.get(connector.getStartObject().getDrawable());
				DrawingTreeNode<?> endTreeNode = _hashMap.get(connector.getEndObject().getDrawable());
				if (startTreeNode != null && startTreeNode.isInvalidated) {
					// System.out.println("Invalidate "+n.graphicalRepresentation.getDrawable()+" because "+startTreeNode.graphicalRepresentation.getDrawable()+" is invalidated");
					n.invalidate();
				}
				if (endTreeNode != null && endTreeNode.isInvalidated) {
					// System.out.println("Invalidate "+n.graphicalRepresentation.getDrawable()+" because "+endTreeNode.graphicalRepresentation.getDrawable()+" is invalidated");
					n.invalidate();
				}
			}
		}

		for (DrawingTreeNode<?> n : nodesToUpdate) {
			n.beginUpdateObjectHierarchy();
		}
		graphicalRepresentationToNotifyAdding = new Vector<GraphicalRepresentation>();
	}*/

	/*protected void endUpdateObjectHierarchy() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called endUpdateObjectHierarchy()");
		}

		// First validate all shape graphical representations
		Enumeration<GraphicalRepresentation> allGr = getAllSortedGraphicalRepresentations();
		while (allGr.hasMoreElements()) {
			GraphicalRepresentation next = allGr.nextElement();
			if (next instanceof ShapeGraphicalRepresentation) {
				next.setValidated(true);
			}
		}

		// First validate all connector graphical representations
		allGr = getAllSortedGraphicalRepresentations();
		while (allGr.hasMoreElements()) {
			GraphicalRepresentation next = allGr.nextElement();
			if (next instanceof ConnectorGraphicalRepresentation) {
				next.setValidated(true);
			}
		}

		for (DrawingTreeNode<?> n : nodesToUpdate) {
			n.endUpdateObjectHierarchy();
		}
		for (GraphicalRepresentation gr : graphicalRepresentationToNotifyAdding) {
			if (gr.getContainerGraphicalRepresentation() != null) {
				gr.getContainerGraphicalRepresentation().notifyDrawableAdded(gr);
			} else {
				logger.warning("null ContainerGraphicalRepresentation for " + gr);
			}
		}
		graphicalRepresentationToNotifyAdding.clear();
		isUpdatingObjectHierarchy = false;

		// logger.info("**************************** endUpdateObjectHierarchy()");
		allGr = getAllSortedGraphicalRepresentations();
		while (allGr.hasMoreElements()) {
			GraphicalRepresentation next = allGr.nextElement();
			if (next != null) {
				// logger.info("> notifyObjectHierarchyHasBeenUpdated() for "+next);
				next.notifyObjectHierarchyHasBeenUpdated();
			}
		}

		// System.out.println("*************** Hop, FIN pour " + this);

		// printGraphicalObjectHierarchy();
	}*/

	/*@Override
	public List<?> getContainedObjects(Object aDrawable) {
		DrawingTreeNode<?> treeNode = _hashMap.get(aDrawable);
		// logger.info("getContainedObjects() for "+aDrawable+" > "+ _hashMap.get(aDrawable)+" childs="+treeNode.childs);
		if (treeNode != null) {
			return new Vector<Object>(treeNode.childs);
		}
		return null;
	}*/

	/*@Override
	public Object getContainer(Object aDrawable) {
		DrawingTreeNode<?> treeNode = _hashMap.get(aDrawable);
		if (treeNode != null && treeNode.parentNode != null) {
			return treeNode.parentNode.drawable;
		}
		return null;
	}*/

	@Override
	public M getModel() {
		return model;
	}

	/*@Override
	public <O> GraphicalRepresentation getGraphicalRepresentation(O aDrawable) {
		if (aDrawable == getModel()) {
			return getDrawingGraphicalRepresentation();
		}
		DrawingTreeNode<O> treeNode = (DrawingTreeNode<O>) _hashMap.get(aDrawable);
		if (treeNode != null) {
			return treeNode.getGraphicalRepresentation();
		}
		return null;
	}*/

	// public abstract <O> GraphicalRepresentation retrieveGraphicalRepresentation(O aDrawable);

	@Override
	public String toString() {
		return "Drawing of " + model;
	}

	public void delete() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("deleting " + this);
		}
		if (nodes != null) {
			for (GRBinding grBinding : nodes.keySet()) {
				for (DrawingTreeNode<?, ?> dtn : retrieveHash(grBinding).values()) {
					dtn.delete();
				}
			}

			/*for (Entry<Object, DrawingTreeNode<?>> e : new ArrayList<Entry<Object, DrawingTreeNode<?>>>(_hashMap.entrySet())) {
				DrawingTreeNode<?> dtn = e.getValue();
				if (dtn != null) {
					if (dtn.graphicalRepresentation != null) {
						dtn.graphicalRepresentation.delete();
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("No GR for " + e.getKey());
						}
					}
					dtn.delete();
				}
			}
			if (getDrawingGraphicalRepresentation() != null) {
				getDrawingGraphicalRepresentation().delete();
			}*/
			nodes.clear();
		}
		model = null;
		// _hashMap = null;
	}

	/**
	 * This hook is called whenever a drawing will be displayed (when a DrawingView will be build)
	 */
	public void prepareVisualization() {
	}

}
