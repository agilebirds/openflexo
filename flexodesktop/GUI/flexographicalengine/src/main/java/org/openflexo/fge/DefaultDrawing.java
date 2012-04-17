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
package org.openflexo.fge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildEnded;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildStarted;

/**
 * This class is the default implementation for all objects representing a graphical drawing, that is a complex graphical representation
 * involving an object tree where all objects have their own graphical representation.
 * 
 * To perform this, two major features are required here:
 * <ul>
 * <li>First, a
 * 
 * <pre>
 * Drawing
 * </pre>
 * 
 * must indicate how to map a given object (called drawable) to its graphical representation (@see
 * {@link #getGraphicalRepresentation(Object)})</li>
 * <li>Then, this
 * 
 * <pre>
 * Drawing
 * </pre>
 * 
 * must encode the objects hierarchy, by implementing following methods: (@see {@link #getContainer(Object)} and @see
 * {@link #getContainedObjects(Object)})</li>
 * </ul>
 * 
 * Note that at top level, this drawing is associated with its own {@link GraphicalRepresentation} which is this case is a
 * {@link DrawingGraphicalRepresentation<M>} of
 * 
 * <pre>
 * M
 * </pre>
 * 
 * .
 * 
 * 
 * @author sylvain
 * 
 * @param <M>
 *            Type of object which is handled as root object
 */
public abstract class DefaultDrawing<M> extends Observable implements Drawing<M> {

	private static final Logger logger = Logger.getLogger(DefaultDrawing.class.getPackage().getName());

	private Hashtable<Object, DrawingTreeNode<?>> _hashMap;
	private DrawingTreeNode<M> _root;
	// private Hashtable<Object,GraphicalRepresentation> _graphicalRepresentations;
	private M model;

	public DefaultDrawing(M aModel) {
		model = aModel;
		_hashMap = new Hashtable<Object, DrawingTreeNode<?>>();
		_root = new DrawingTreeNode<M>(model, null);
		// logger.info("Store: "+_root+" for "+model);
		_hashMap.put(model, _root);
	}

	public <O> void updateDrawable(O aDrawable) {
		DrawingTreeNode<?> alreadyExistingNode = _hashMap.get(aDrawable);
		if (alreadyExistingNode == null) {
			logger.warning("Cannot find DrawingTreeNode for " + aDrawable);
			return;
		}
		// alreadyExistingNode.update();
		if (alreadyExistingNode.parentNode != null) {
			Object parentDrawable = alreadyExistingNode.parentNode.drawable;
			removeDrawable(aDrawable, parentDrawable);
			addDrawable(aDrawable, parentDrawable);
		}
	}

	public <O> void addDrawable(O aDrawable, Object aParentDrawable) {
		// logger.fine("> add drawable "+aDrawable+" under "+aParentDrawable);

		if (aParentDrawable == null) {
			logger.warning("Cannot register drawable above null parent");
			return;
		}
		DrawingTreeNode<?> parentNode = _hashMap.get(aParentDrawable);
		if (parentNode == null) {
			logger.warning("Cannot find DrawingTreeNode for " + aParentDrawable);
			return;
		}
		if (parentNode.childs.contains(aDrawable)) {
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
		}
	}

	public <O> void removeDrawable(O aDrawable, Object aParentDrawable) {
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
			GraphicalRepresentation<?> parentGR = getGraphicalRepresentation(aParentDrawable);
			GraphicalRepresentation<?> removedGR = getGraphicalRepresentation(aDrawable);
			parentNode.removeChild(nodeToRemove);
			parentGR.notifyDrawableRemoved(removedGR);
			removedGR.delete();
		}
	}

	public Enumeration<GraphicalRepresentation<?>> getAllGraphicalRepresentations() {
		Vector<GraphicalRepresentation<?>> returned = new Vector<GraphicalRepresentation<?>>();
		for (Enumeration<DrawingTreeNode<?>> en = _hashMap.elements(); en.hasMoreElements();) {
			returned.add(en.nextElement().getGraphicalRepresentation());
		}
		return returned.elements();
	}

	public Enumeration<GraphicalRepresentation<?>> getAllSortedGraphicalRepresentations() {
		Vector<GraphicalRepresentation<?>> returned = new Vector<GraphicalRepresentation<?>>();
		for (Enumeration<DrawingTreeNode<?>> en = getAllSortedNodes(); en.hasMoreElements();) {
			returned.add(en.nextElement().getGraphicalRepresentation());
		}
		return returned.elements();
	}

	private Enumeration<DrawingTreeNode<?>> getAllSortedNodes() {
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
				return res;
				/*if (o1 == o2) return 0;
				if (o1.isAncestorOf(o2)) return -1;
				if (o2.isAncestorOf(o1)) return 1;
				logger.warning("Could not compare "+o1+" and "+o2);
				return 0;*/
			}
		});
		/*Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		for (Enumeration<DrawingTreeNode> en = dtnList.elements(); en.hasMoreElements();) {
			returned.add(en.nextElement().graphicalRepresentation);
		}*/
		return dtnList.elements();
	}

	private boolean isUpdatingObjectHierarchy = false;
	private Vector<DrawingTreeNode<?>> nodesToUpdate;
	private Vector<GraphicalRepresentation<?>> graphicalRepresentationToNotifyAdding;

	private boolean isGraphicalHierarchyEnabled = true;
	private boolean isGraphicalHierarchyDirty = true;

	public final void enableGraphicalHierarchy() {
		isGraphicalHierarchyEnabled = true;
		if (isGraphicalHierarchyDirty) {
			updateGraphicalObjectsHierarchy();
		}
	}

	public final void disableGraphicalHierarchy() {
		isGraphicalHierarchyEnabled = false;
	}

	public final void updateGraphicalObjectsHierarchy() {
		if (!isGraphicalHierarchyEnabled) {
			isGraphicalHierarchyDirty = true;
			return;
		}
		if (isUpdatingObjectHierarchy) {
			return;
		}
		// logger.info("******************** BEGIN updateGraphicalObjectsHierarchy() ");
		setChanged();
		notifyObservers(new GraphicalObjectsHierarchyRebuildStarted(this));
		beginUpdateObjectHierarchy();

		buildGraphicalObjectsHierarchy();

		endUpdateObjectHierarchy();
		isGraphicalHierarchyDirty = false;
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
	public void invalidateGraphicalObjectsHierarchy(Object object) {
		DrawingTreeNode<?> dtn = _hashMap.get(object);
		// System.out.println("invalidateGraphicalObjectsHierarchy with " + object + " dtn=" + dtn);
		if (dtn != null) {
			dtn.invalidate();
		}
	}

	public void printGraphicalObjectHierarchy() {
		logger.info("Graphical object hierarchy");
		if (_root != null) {
			_printGraphicalObjectHierarchy(_root, 0);
		} else {
			logger.info(" > Root node is null !");
		}
	}

	private void _printGraphicalObjectHierarchy(DrawingTreeNode<?> dtn, int level) {
		logger.info(buildWhiteSpaceIndentation(level * 5)
				+ " > "
				+ (dtn.getGraphicalRepresentation() != null ? dtn.getGraphicalRepresentation().getClass().getSimpleName() + " "
						+ Integer.toHexString(dtn.getGraphicalRepresentation().hashCode()) : " null ") + " object=" + dtn.drawable);
		if (dtn.childNodes != null) {
			for (DrawingTreeNode<?> child : dtn.childNodes) {
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

	protected abstract void buildGraphicalObjectsHierarchy();

	protected void beginUpdateObjectHierarchy() {

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
				ConnectorGraphicalRepresentation<?> connector = (ConnectorGraphicalRepresentation<?>) n.getGraphicalRepresentation();
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
		graphicalRepresentationToNotifyAdding = new Vector<GraphicalRepresentation<?>>();
	}

	protected void endUpdateObjectHierarchy() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called endUpdateObjectHierarchy()");
		}

		// First validate all shape graphical representations
		Enumeration<GraphicalRepresentation<?>> allGr = getAllSortedGraphicalRepresentations();
		while (allGr.hasMoreElements()) {
			GraphicalRepresentation<?> next = allGr.nextElement();
			if (next instanceof ShapeGraphicalRepresentation) {
				next.setValidated(true);
			}
		}

		// First validate all connector graphical representations
		allGr = getAllSortedGraphicalRepresentations();
		while (allGr.hasMoreElements()) {
			GraphicalRepresentation<?> next = allGr.nextElement();
			if (next instanceof ConnectorGraphicalRepresentation) {
				next.setValidated(true);
			}
		}

		for (DrawingTreeNode<?> n : nodesToUpdate) {
			n.endUpdateObjectHierarchy();
		}
		for (GraphicalRepresentation<?> gr : graphicalRepresentationToNotifyAdding) {
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
			GraphicalRepresentation<?> next = allGr.nextElement();
			if (next != null) {
				// logger.info("> notifyObjectHierarchyHasBeenUpdated() for "+next);
				next.notifyObjectHierarchyHasBeenUpdated();
			}
		}

		// System.out.println("*************** Hop, FIN pour " + this);

		// printGraphicalObjectHierarchy();
	}

	private class DrawingTreeNode<O> {
		private O drawable;
		DrawingTreeNode<?> parentNode;
		private Vector<DrawingTreeNode<?>> childNodes;
		Vector<Object> childs;
		GraphicalRepresentation<O> graphicalRepresentation;

		boolean isInvalidated = false;

		protected void invalidate() {
			// System.out.println("* Invalidate " + drawable.getClass().getSimpleName() + " : " + drawable);
			isInvalidated = true;
			for (DrawingTreeNode<?> dtn : childNodes) {
				dtn.invalidate();
			}
		}

		private boolean isAncestorOf(DrawingTreeNode<?> o2) {
			if (o2 == null) {
				return false;
			}
			DrawingTreeNode<?> current = o2;
			while (current != DrawingTreeNode.this && current != null) {
				current = current.parentNode;
			}
			if (current == null) {
				return false;
			}
			return true;
		}

		private int getDepth() {
			int returned = 0;
			DrawingTreeNode<?> current = this;
			while (current.parentNode != null) {
				returned++;
				current = current.parentNode;
			}
			return returned;
		}

		private DrawingTreeNode(O aDrawable, Object aParentDrawable) {
			// logger.info("New DrawingTreeNode for "+aDrawable+" under "+aParentDrawable+" (is "+this+")");
			drawable = aDrawable;
			if (aParentDrawable != null) {
				parentNode = _hashMap.get(aParentDrawable);
				if (parentNode == null) {
					logger.warning("Cannot find GR for " + aParentDrawable);
				} else {
					parentNode.childNodes.add(this);
					parentNode.childs.add(aDrawable);
				}
			}
			childNodes = new Vector<DrawingTreeNode<?>>();
			childs = new Vector<Object>();
			_hashMap.put(aDrawable, this);
			/*if (aParentDrawable == null) { // This is the root node
				graphicalRepresentation = (GraphicalRepresentation<O>) getDrawingGraphicalRepresentation();
			} else {
				graphicalRepresentation = retrieveGraphicalRepresentation(aDrawable);
			}*/
		}

		private GraphicalRepresentation<O> getGraphicalRepresentation() {
			if (graphicalRepresentation == null) {
				if (parentNode == null) { // This is the root node
					graphicalRepresentation = (GraphicalRepresentation<O>) getDrawingGraphicalRepresentation();
				} else {
					graphicalRepresentation = retrieveGraphicalRepresentation(drawable);
				}
			}
			return graphicalRepresentation;
		}

		/*private void update()
		{
			if (parentNode == null) { // This is the root node
				graphicalRepresentation = (GraphicalRepresentation<O>)getDrawingGraphicalRepresentation();
			}
			else {
				GraphicalRe
				parentGR.notifyDrawableRemoved(removedGR);
				graphicalRepresentation = retrieveGraphicalRepresentation(drawable);
				System.out.println("Tiens maintenant la GR c'est "+graphicalRepresentation);
			}
		}*/

		private void removeChild(DrawingTreeNode<?> aChildNode) {
			if (aChildNode == null) {
				logger.warning("Cannot remove null node");
				return;
			}
			if (childNodes.contains(aChildNode)) {
				childNodes.remove(aChildNode);
			} else {
				logger.warning("Cannot remove node: not present");
				return;
			}
			Object child = aChildNode.drawable;
			if (childs.contains(child)) {
				childs.remove(child);
			} else {
				logger.warning("Cannot remove child: not present");
				return;
			}
			aChildNode.delete();
		}

		protected void delete() {
			// Normally, it is already done, but check and do it when required...
			if (parentNode != null && parentNode.childNodes.contains(this)) {
				parentNode.removeChild(this);
			}

			if (childNodes != null) {
				for (DrawingTreeNode<?> n : new ArrayList<DrawingTreeNode<?>>(childNodes)) {
					removeChild(n);
				}
				childNodes.clear();
			}

			childNodes = null;

			if (childs != null) {
				childs.clear();
			}
			childs = null;

			if (_hashMap != null && drawable != null) {
				_hashMap.remove(drawable);
			}

			if (graphicalRepresentation != null) {
				graphicalRepresentation.delete();
			}

			drawable = null;
			parentNode = null;
			graphicalRepresentation = null;

		}

		Vector<DrawingTreeNode<?>> nodesToRemove = new Vector<DrawingTreeNode<?>>();

		protected void beginUpdateObjectHierarchy() {

			// Invalidated nodes are to be removed rigth now
			// (we are sure that we don't want to keep it)
			if (childNodes != null) {
				for (DrawingTreeNode<?> n : new ArrayList<DrawingTreeNode<?>>(childNodes)) {
					if (n.isInvalidated) {
						removeDrawable(n.drawable, drawable);
					}
				}
			}

			// Remaining nodes are marked to potential deletion
			if (childNodes != null) {
				for (DrawingTreeNode<?> n : childNodes) {
					nodesToRemove.add(n);
				}
			}
		}

		protected void endUpdateObjectHierarchy() {
			// Nodes that keep marked for deletion are deleted now
			for (DrawingTreeNode<?> n : nodesToRemove) {
				removeDrawable(n.drawable, drawable);
			}
			nodesToRemove.clear();
		}

	}

	@Override
	public List<?> getContainedObjects(Object aDrawable) {
		DrawingTreeNode<?> treeNode = _hashMap.get(aDrawable);
		// logger.info("getContainedObjects() for "+aDrawable+" > "+ _hashMap.get(aDrawable)+" childs="+treeNode.childs);
		if (treeNode != null) {
			return new Vector<Object>(treeNode.childs);
		}
		return null;
	}

	@Override
	public Object getContainer(Object aDrawable) {
		/*if (_hashMap == null) {
			logger.info("Ici, on demande _hashMap pour le Drawing "+this+" "+Integer.toHexString(hashCode()));
		}*/
		DrawingTreeNode<?> treeNode = _hashMap.get(aDrawable);
		/*logger.info("Pour "+aDrawable+" le DTN c'est "+treeNode);
		if (treeNode != null && treeNode.parentNode != null) {
			logger.info("Pour "+aDrawable+" le parent DTN c'est "+treeNode.parentNode);
			logger.info("Pour "+aDrawable+" et le drawable c'est "+treeNode.parentNode.drawable);
		}*/
		if (treeNode != null && treeNode.parentNode != null) {
			return treeNode.parentNode.drawable;
		}
		return null;
	}

	@Override
	public M getModel() {
		return model;
	}

	@Override
	public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable) {
		if (aDrawable == getModel()) {
			return (GraphicalRepresentation<O>) getDrawingGraphicalRepresentation();
		}
		DrawingTreeNode<O> treeNode = (DrawingTreeNode<O>) _hashMap.get(aDrawable);
		if (treeNode != null) {
			return treeNode.getGraphicalRepresentation();
		}
		return null;
	}

	public abstract <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable);

	public void delete() {
		logger.info("deleting drawing " + this + " " + Integer.toHexString(hashCode()));
		if (_hashMap != null) {
			Vector<Object> drawableList = new Vector<Object>();
			drawableList.addAll(_hashMap.keySet());
			for (Object drawable : drawableList) {
				DrawingTreeNode<?> dtn = _hashMap.get(drawable);
				if (dtn != null) {
					if (dtn.graphicalRepresentation != null) {
						dtn.graphicalRepresentation.delete();
					}
					dtn.delete();
				}
			}
			_hashMap.clear();
		}
		// _hashMap = null;
	}

	/**
	 * This hook is called whenever a drawing will be displayed (when a DrawingView will be build)
	 */
	public void prepareVisualization() {
	}

}
