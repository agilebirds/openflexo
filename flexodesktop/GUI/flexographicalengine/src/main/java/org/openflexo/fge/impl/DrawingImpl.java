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
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildEnded;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildStarted;

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

	private Hashtable<GRBinding<?, ?>, Hashtable<Object, DrawingTreeNode<?, ?>>> _hashMap;
	private RootNodeImpl<M> _root;
	private M model;

	private DrawingGRBinding<M> drawingBinding;

	private boolean editable = true;

	private FGEModelFactory factory;

	public DrawingImpl(M model, FGEModelFactory factory) {
		this.model = model;
		this.factory = factory;
		_hashMap = new Hashtable<GRBinding<?, ?>, Hashtable<Object, DrawingTreeNode<?, ?>>>();
	}

	public abstract void init();

	@Override
	public FGEModelFactory getFactory() {
		return factory;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;

	}

	@Override
	public RootNode<M> getRoot() {
		if (_root == null) {
			_root = buildRoot();
		}
		return _root;
	}

	private RootNodeImpl<M> buildRoot() {
		if (drawingBinding != null) {
			RootNodeImpl<M> _root = new RootNodeImpl<M>(this, model, drawingBinding, null);
			Hashtable<Object, DrawingTreeNode<?>> hash = retrieveHash(drawingBinding);
			hash.put(model, _root);
			return _root;
		} else {
			return null;
		}
	}

	Hashtable<Object, DrawingTreeNode<?, ?>> retrieveHash(GRBinding<?, ?> grBinding) {
		Hashtable<Object, DrawingTreeNode<?, ?>> hash = _hashMap.get(grBinding);
		if (hash == null) {
			hash = new Hashtable<Object, DrawingTreeNode<?, ?>>();
			_hashMap.put(grBinding, hash);
		}
		return hash;
	}

	private DrawingTreeNode<?> retrieveDrawingTreeNode(GRBinding<?, ?> grBinding, Object drawable) {
		Hashtable<Object, DrawingTreeNode<?>> hash = retrieveHash(grBinding);
		return hash.get(drawable);
	}

	public DrawingGRBinding<M> bindDrawing(Class<M> drawingClass, String name) {
		return drawingBinding = new DrawingGRBinding<M>();
	}

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name) {
		return null;
	}

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, ShapeGRBinding<?> parentBinding) {
		return null;
	}

	public <R> ShapeGRBinding<R> bindShape(Class<R> shapeObjectClass, String name, DrawingGRBinding<?> parentBinding) {
		return null;
	}

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name) {
		return null;
	}

	public <R> ConnectorGRBinding<R> bindConnector(Class<R> connectorObjectClass, String name, ShapeGRBinding<?> fromBinding,
			ShapeGRBinding<?> toBinding) {
		return null;
	}

	protected <O> DrawingTreeNode<O> getDrawingTreeNode(O representable) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> DrawingTreeNode<O> getDrawingTreeNode(O drawable, GRBinding<O, ?> grBinding) {
		Hashtable<Object, DrawingTreeNode<?>> hash = retrieveHash(grBinding);
		return (DrawingTreeNode<O>) hash.get(drawable);
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

	private boolean isUpdatingObjectHierarchy = false;

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

	public final void updateGraphicalObjectsHierarchy() {

		/*if (!isGraphicalHierarchyEnabled) {
			isGraphicalHierarchyDirty = true;
			return;
		}*/
		if (isUpdatingObjectHierarchy) {
			return;
		}

		getRoot().invalidate();

		// logger.info("******************** BEGIN updateGraphicalObjectsHierarchy() ");
		setChanged();
		notifyObservers(new GraphicalObjectsHierarchyRebuildStarted(this));
		// beginUpdateObjectHierarchy();

		updateGraphicalObjectsHierarchy(getRoot());

		// buildGraphicalObjectsHierarchy(model);

		// endUpdateObjectHierarchy();

		// isGraphicalHierarchyDirty = false;

		setChanged();
		notifyObservers(new GraphicalObjectsHierarchyRebuildEnded(this));
		// logger.info("******************** END updateGraphicalObjectsHierarchy() ");

	}

	/**
	 * Invalidate the whole hierarchy. All nodes of drawing tree are invalidated, which means that a complete recomputing of the whole tree
	 * will be performed during next updateGraphicalHierarchy() call<br>
	 * Existing graphical representation are kept.
	 */
	public void invalidateGraphicalObjectsHierarchy() {
		invalidateGraphicalObjectsHierarchy(getModel());
	}

	/**
	 * Invalidate the whole hierarchy under current node designated by supplied object All nodes of drawing tree under supplied node are
	 * invalidated, which means that a recomputing of the whole tree under supplied node will be performed during next
	 * updateGraphicalHierarchy() call.<br>
	 * If flag deleteGraphicalRepresentation is set to true, associated graphical representation will be deleted and nullified, and then new
	 * graphical representations will be instanciated during next update request.
	 * 
	 * @param deleteGraphicalRepresentation
	 */
	public <O> void invalidateGraphicalObjectsHierarchy(O drawable) {
		for (DrawingTreeNode<O> dtn : getDrawingTreeNodes(drawable)) {
			dtn.invalidate();
		}
	}

	/**
	 * Force updating of all DrawingTreeNode representing supplied drawable
	 * 
	 * @param drawable
	 */
	public final <O> void updateGraphicalObjectsHierarchy(O drawable) {
		for (DrawingTreeNode<O> dtn : getDrawingTreeNodes(drawable)) {
			dtn.invalidate();
			updateGraphicalObjectsHierarchy(dtn);
		}
	}

	private <O> List<DrawingTreeNode<O>> getDrawingTreeNodes(O drawable) {
		List<DrawingTreeNode<O>> returned = new ArrayList<DrawingTreeNode<O>>();
		for (GRBinding<?, ?> grBinding : _hashMap.keySet()) {
			if (getDrawingTreeNode(drawable, (GRBinding<O, ?>) grBinding) != null) {
				returned.add(getDrawingTreeNode(drawable, (GRBinding<O, ?>) grBinding));
			}
			// TODO: fix ClassCastException (try/catch then ignore)
		}
		return returned;
	}

	public void printGraphicalObjectHierarchy() {
		logger.info("Graphical object hierarchy");
		if (_root != null) {
			_printGraphicalObjectHierarchy(_root, 0);
		} else {
			logger.info(" > Root node is null !");
		}
	}

	private void _printGraphicalObjectHierarchy(DrawingTreeNodeImpl<?> dtn, int level) {
		logger.info(buildWhiteSpaceIndentation(level * 5)
				+ " > "
				+ (dtn.getGraphicalRepresentation() != null ? dtn.getGraphicalRepresentation().getClass().getSimpleName() + " "
						+ Integer.toHexString(dtn.getGraphicalRepresentation().hashCode()) : " null ") + " object=" + dtn.getDrawable());
		if (dtn.getChildNodes() != null) {
			for (DrawingTreeNodeImpl<?> child : dtn.getChildNodes()) {
				_printGraphicalObjectHierarchy(child, level + 1);
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

	private final <O> void updateGraphicalObjectsHierarchy(DrawingTreeNode<O> dtn) {
		if (dtn.isInvalidated()) {
			GRBinding<O, ? extends GraphicalRepresentation> grBinding = dtn.getGRBinding();
			List<DrawingTreeNode<?>> nodesToRemove = new ArrayList<DrawingTreeNode<?>>(dtn.getChildNodes());
			for (GRStructureWalker<O> walker : grBinding.getWalkers()) {
				walker.startWalking(dtn);
				walker.walk(dtn.getDrawable());
				List<DrawingTreeNode<?>> updatedNodes = walker.stopWalking(dtn);
				for (DrawingTreeNode<?> updatedNode : updatedNodes) {
					updateGraphicalObjectsHierarchy(updatedNode);
					nodesToRemove.remove(updatedNode);
				}
			}
			for (DrawingTreeNode<?> nodeToRemove : nodesToRemove) {
				deleteNode(nodeToRemove);
			}
			((DrawingTreeNodeImpl<?>) dtn).isInvalidated = false;
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

	private <O> boolean deleteNode(DrawingTreeNode<?> node) {
		node.delete();
		notifyNodeRemoved(node);
		return true;
	}

	public void notifyNodeAdded(DrawingTreeNode<?> addedNode) {
		logger.info(">>>> Added node: " + addedNode);
		// See parentGR.notifyDrawableAdded(removedGR);
	}

	public void notifyNodeRemoved(DrawingTreeNode<?> removedNode) {
		logger.info(">>>> Removed node: " + removedNode);
		// See parentGR.notifyDrawableRemoved(removedGR);
	}

	@Override
	public <O> DrawingTreeNode<O> drawShape(DrawingTreeNode<?> parentNode, ShapeGRBinding<O> grBinding, O aDrawable) {

		if (parentNode == null) {
			logger.warning("Cannot register drawable above null parent");
			return null;
		}

		if (parentNode.hasShapeFor(grBinding, aDrawable)) {
			return parentNode.getShapeFor(grBinding, aDrawable);
		} else {
			DrawingTreeNode<O> returned = new DrawingTreeNodeImpl<O>(this, aDrawable, grBinding, (DrawingTreeNodeImpl<?>) parentNode);
			if (isUpdatingObjectHierarchy) {
				notifyNodeAdded(returned);
			}
			return returned;
		}

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
			GraphicalRepresentation parentGR = getGraphicalRepresentation(aParentDrawable);
			GraphicalRepresentation removedGR = getGraphicalRepresentation(aDrawable);
			parentNode.removeChild(nodeToRemove);
			parentGR.notifyDrawableRemoved(removedGR);
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
		if (_hashMap != null) {
			for (GRBinding grBinding : _hashMap.keySet()) {
				for (DrawingTreeNode<?> dtn : retrieveHash(grBinding).values()) {
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
			_hashMap.clear();
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
