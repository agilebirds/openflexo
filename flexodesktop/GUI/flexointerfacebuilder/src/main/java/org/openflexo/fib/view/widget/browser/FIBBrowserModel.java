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
package org.openflexo.fib.view.widget.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.antar.binding.AbstractBinding.TargetObject;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.toolbox.HasPropertyChangeSupport;


public class FIBBrowserModel extends DefaultTreeModel implements TreeSelectionListener, TreeModel
{

	private static final Logger logger = Logger.getLogger(FIBBrowserModel.class.getPackage().getName());

	private Hashtable<FIBBrowserElement,FIBBrowserElementType> _elementTypes;
	private final FIBBrowserWidgetFooter _footer;
	private FIBBrowser _fibBrowser;
	private FIBBrowserWidget _widget;
	private Object selectedObject;
	private final Vector<Object> selection;

	/**
	 * Stores controls: key is the JButton and value the
	 * PropertyListActionListener
	 */
	//private Hashtable<JButton,PropertyListActionListener> _controls;

	public FIBBrowserModel(FIBBrowser fibBrowser, FIBBrowserWidget widget, FIBController controller)
	{
		super(null);
		contents = new Hashtable<Object,BrowserCell>();
		selection = new Vector<Object>();
		_fibBrowser = fibBrowser;
		_widget = widget;
		_elementTypes = new Hashtable<FIBBrowserElement,FIBBrowserElementType>();
		for (FIBBrowserElement browserElement : fibBrowser.getElements()) {
			addToElementTypes(browserElement,buildBrowserElementType(browserElement,controller));
		}

		_footer = new FIBBrowserWidgetFooter(fibBrowser, this, widget);
	}

	public void delete()
	{
		for (FIBBrowserElement c : _elementTypes.keySet()) {
			(_elementTypes.get(c)).delete();
		}

		_footer.delete();

		_elementTypes.clear();

		_elementTypes = null;
		_widget = null;
		_fibBrowser = null;
	}

	public FIBBrowserElement elementForClass(Class aClass)
	{
		return _fibBrowser.elementForClass(aClass);
	}
	
	public FIBBrowserElementType elementTypeForClass(Class aClass)
	{
		FIBBrowserElement element = elementForClass(aClass);
		if (element != null) {
			return _elementTypes.get(element);
		}
		else {
			logger.warning("Could not find element for class "+aClass);
			return null;
		}
	}
	
	public FIBBrowserWidget getWidget()
	{
		return _widget;
	}

	/**
	 * @param root
	 * @return flag indicating if change was required
	 */
	public boolean updateRootObject(Object root)
	{
		if (root == null) return false;
		BrowserCell rootCell = retrieveBrowserCell(root,null);
		if (getRoot() != rootCell) {
			logger.fine("updateRootObject() with "+root+" rootCell="+rootCell);
			setRoot(rootCell);
			return true;
		}
		return false;
	}
	
	public void addToElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType)
	{
		_elementTypes.put(element,elementType);
		elementType.setModel(this);
	}

	public void removeFromElementTypes(FIBBrowserElement element, FIBBrowserElementType elementType)
	{
		_elementTypes.remove(element);
		elementType.setModel(null);
	}

	public Hashtable<FIBBrowserElement,FIBBrowserElementType> getElementTypes()
	{
		return _elementTypes;
	}

	public FIBBrowserWidgetFooter getFooter()
	{
		return _footer;
	}

	/*public void setModel(Object model)
	{
		updateRootObject(model);
	}*/

	protected FIBBrowserWidget getBrowserWidget() 
	{
		return _widget;
	}

	private FIBBrowserElementType buildBrowserElementType(FIBBrowserElement browserElement, FIBController controller)
	{
		return new FIBBrowserElementType(browserElement, this, controller);
	}

	public Object getSelectedObject()
	{
		return selectedObject;
	}

	public Vector<Object> getSelection()
	{
		return selection;
	}

	public TreeSelectionModel getTreeSelectionModel()
	{
		return _widget.getTreeSelectionModel();
	}

	private boolean ignoreNotifications = false;
	
	public synchronized void addToSelectionNoNotification(Object o)
	{
		ignoreNotifications = true;
		addToSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void removeFromSelectionNoNotification(Object o)
	{
		ignoreNotifications = true;
		removeFromSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void resetSelectionNoNotification()
	{
		ignoreNotifications = true;
		resetSelection();
		ignoreNotifications = false;
	}

	public void addToSelection(Object o)
	{
		BrowserCell cell = getBrowserCell(o);
		if (cell != null) {
			TreePath path = getTreePath(cell);
			getWidget().getTreeSelectionModel().addSelectionPath(path);
			getWidget().getJTree().scrollPathToVisible(path);
		}
	}

	public void removeFromSelection(Object o)
	{
		BrowserCell cell = getBrowserCell(o);
		if (cell != null) {
			TreePath path = getTreePath(cell);
			getWidget().getTreeSelectionModel().removeSelectionPath(path);
		}
	}
	
	public void resetSelection()
	{
		getWidget().getTreeSelectionModel().clearSelection();
	}

	public void fireTreeRestructured() 
	{
		if (getRoot() instanceof BrowserCell) {
			((BrowserCell)getRoot()).update(true);
			//nodeStructureChanged((BrowserCell)getRoot());
		}
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		BrowserCell cell = (BrowserCell) path.getLastPathComponent();
		if (cell.getBrowserElementType().isLabelEditable() && newValue instanceof String) {
			cell.getBrowserElementType().setEditableLabelFor(cell.getRepresentedObject(), (String)newValue);
		}
	}

	@Override
	public synchronized void valueChanged(TreeSelectionEvent e) 
	{
		Vector<Object> oldSelection = new Vector<Object>();
		oldSelection.addAll(selection);
		/*System.out.println("Selection: "+e);

		System.out.println("Paths="+e.getPaths());
		for (TreePath tp : e.getPaths()) {
			System.out.println("> "+tp.getLastPathComponent()+" added="+e.isAddedPath(tp));
		}
		System.out.println("New LEAD="+(e.getNewLeadSelectionPath()!=null?e.getNewLeadSelectionPath().getLastPathComponent():"null"));
		System.out.println("Old LEAD="+(e.getOldLeadSelectionPath()!=null?e.getOldLeadSelectionPath().getLastPathComponent():"null"));
		*/
		if (e.getNewLeadSelectionPath() == null
				|| (BrowserCell)e.getNewLeadSelectionPath().getLastPathComponent() == null) {
			selectedObject = null;
		}
		else {
			selectedObject = ((BrowserCell)e.getNewLeadSelectionPath().getLastPathComponent()).getRepresentedObject();
		}
		for (TreePath tp : e.getPaths()) {
			if (e.isAddedPath(tp)) {
				selection.add(((BrowserCell)tp.getLastPathComponent()).getRepresentedObject());
			}
			else {
				selection.remove(((BrowserCell)tp.getLastPathComponent()).getRepresentedObject());
			}
		}
		
		//logger.info("BrowserModel, selected object is now "+selectedObject);
		
		_widget.getDynamicModel().selected = selectedObject;
		_widget.getDynamicModel().selection = selection;
		_widget.notifyDynamicModelChanged();       

       	if (_widget.getComponent().getSelected().isValid()) {
       		logger.fine("Sets SELECTED binding with "+selectedObject);
       		_widget.getComponent().getSelected().setBindingValue(selectedObject,_widget.getController());
    	}
		
		_widget.updateFont();

		if (!ignoreNotifications) {
			_widget.getController().updateSelection(_widget,oldSelection,selection);
		}
		
		_footer.setFocusedObject(selectedObject);

	}
	
	private TreePath getTreePath(BrowserCell cell)
	{
		Object[] path = getPathToRoot(cell);
		TreePath returned = new TreePath(path);
		return returned;
	}
	


	private final Hashtable<Object,BrowserCell> contents;

	public BrowserCell retrieveBrowserCell(Object representedObject, BrowserCell father)
	{
		BrowserCell returned = contents.get(representedObject);
		if (returned != null) {
			if (returned.getParent() != null) {
				if (!returned.getParent().equals(father)) {
					logger.warning("Found object at different places in browser: "+representedObject);
				}
			}
			else if (father != null) {
				logger.warning("Found object at different places in browser: "+representedObject);
			}
			return returned;
		}
		returned = new BrowserCell(representedObject, father);
		contents.put(representedObject,returned);
		return returned;
	}
	
	public boolean containsObject(Object representedObject)
	{
		return contents.get(representedObject) != null;
	}
	
	public BrowserCell getBrowserCell(Object representedObject)
	{
		return contents.get(representedObject);
	}
	
	public class BrowserCell implements TreeNode, Observer, PropertyChangeListener
	{
		private Object representedObject;
		private FIBBrowserElementType browserElementType;
		private BrowserCell father;
		private final Vector<BrowserCell> children;
		private boolean isDeleted = false;
		private boolean isVisible = true;
		
		public BrowserCell(Object representedObject, BrowserCell father) 
		{
			//logger.info("Build new BrowserCell for "+representedObject);
			this.representedObject = representedObject;
			browserElementType = elementTypeForClass(representedObject.getClass());
			this.father = father;
			children = new Vector<BrowserCell>();
			
			if (representedObject instanceof HasPropertyChangeSupport && getBrowserElementType() != null) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport)representedObject).getPropertyChangeSupport();
				List<TargetObject> dependingObjectList = getBrowserElementType().getDependingObjects(representedObject);
				//System.out.println("For cell "+this+" target objects are: "+targetObjectList);
				if (dependingObjectList != null) {
					for (TargetObject targetObject : dependingObjectList) {
						if (targetObject.target == representedObject) {
							pcSupport.addPropertyChangeListener(targetObject.propertyName,this);
						}
					}
				}
			}
			else if (representedObject instanceof Observable) {
				((Observable)representedObject).addObserver(this);
			}
			
		
			update(false);
		}
		
		public void delete()
		{
			logger.fine("Delete BrowserCell for "+representedObject);

			for (BrowserCell c : children) c.delete();


			if (representedObject instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport)representedObject).getPropertyChangeSupport();
				List<TargetObject> dependingObjectList = getBrowserElementType().getDependingObjects(representedObject);
				if (dependingObjectList != null) {
					for (TargetObject targetObject : dependingObjectList) {
						if (targetObject.target == representedObject) {
							pcSupport.removePropertyChangeListener(targetObject.propertyName,this);
						}
					}
				}
			}
			else if (representedObject instanceof Observable) {
				((Observable)representedObject).deleteObserver(this);
			}

			if (representedObject != null)
				contents.remove(representedObject);
			
			if (selection.contains(representedObject))
				selection.remove(representedObject);
			
			this.representedObject = null;
			browserElementType = null;
			this.father = null;
 			children.clear();	
 			isDeleted = true;
 		}
		
		public void update(boolean recursively)
		{
			//logger.info("**************** update() "+this);
			if (browserElementType == null) {
				logger.warning("Not element type registered for "+representedObject);
				return;
			}
			
			// Special case for cells that were declared as invisible
			// When becomes visible, must tells to parent to update
			if (!isVisible) {
				if (browserElementType.isVisible(representedObject)) {
					logger.fine("Cell "+this+" becomes visible");
					father.update(recursively);
				}
			}
			
			Vector<BrowserCell> oldChildren = new Vector<BrowserCell>();
			Vector<BrowserCell> removedChildren = new Vector<BrowserCell>();
			Vector<BrowserCell> newChildren = new Vector<BrowserCell>();
			for (BrowserCell c : children) {
				removedChildren.add(c);
				oldChildren.add(c);
			}
			boolean isEnabled = browserElementType.isEnabled(representedObject);
			List newChildrenObjects = /*(isEnabled ?*/ browserElementType.getChildrenFor(representedObject) /*: new Vector())*/;
			int index = 0;
			
			
			for (Object o : newChildrenObjects) {
				BrowserCell cell = retrieveBrowserCell(o,this);
				FIBBrowserElementType childElementType = elementTypeForClass(o.getClass());
				if (childElementType != null && childElementType.isVisible(o)) {
					if (children.contains(cell)) {
						// OK, child still here
						removedChildren.remove(cell);
						if (recursively) cell.update(true);
						index = children.indexOf(cell)+1;
					}
					else {
						newChildren.add(cell);
						children.insertElementAt(cell,index);
						index++;
					}
				}
				else {
					cell.isVisible = false;
				}
			}
			for (BrowserCell c : removedChildren) {
				children.remove(c);
				c.delete();
			}

			// System.out.println("removedChildren ["+removedChildren.size()+"] "+removedChildren);
			// System.out.println("newChildren ["+newChildren.size()+"] "+newChildren);
			// System.out.println("children ["+children.size()+"] "+children);
			
			if (removedChildren.size() > 0 || newChildren.size() > 0) {
				if (oldChildren.size() == 0) {
					// Special case, i don't undertand why (SGU)
					nodeStructureChanged(this);
				}
				else {
					if (removedChildren.size() > 0) {
						int[] childIndices = new int[removedChildren.size()];
						Object[] removedChildrenObjects = new Object[removedChildren.size()];
						for (int i=0; i<removedChildren.size(); i++) {
							childIndices[i] = oldChildren.indexOf(removedChildren.get(i));
							removedChildrenObjects[i] = removedChildren.get(i);
						}
						nodesWereRemoved(this, childIndices, removedChildrenObjects);
					}
					if (newChildren.size() > 0) {
						int[] childIndices = new int[newChildren.size()];
						for (int i=0; i<newChildren.size(); i++) {
							childIndices[i] = children.indexOf(newChildren.get(i));
						}
						nodesWereInserted(this, childIndices);
					}
				}
			}

			nodeChanged(this);
		}
		
		@Override
		public void update(Observable o, Object arg) 
		{
			if (!isDeleted && o == representedObject) {
				update(false);
			}
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (!isDeleted && evt.getSource() == representedObject) {
				//System.out.println("cell "+this+" propertyChanged "+evt.getPropertyName()+" for "+evt.getSource());
				update(false);
			}
		}
		
		public Object getRepresentedObject() 
		{
			return representedObject;
		}

		public FIBBrowserElement getBrowserElement() 
		{
			return browserElementType.getBrowserElement();
		}

		public FIBBrowserElementType getBrowserElementType()
		{
			return browserElementType;
		}

		@Override
		public Enumeration children() 
		{
			return children.elements();
		}

		@Override
		public boolean getAllowsChildren() 
		{
			return true;
		}

		@Override
		public TreeNode getChildAt(int childIndex) 
		{
			return children.get(childIndex);
		}

		@Override
		public int getChildCount() 
		{
			return children.size();
		}

		@Override
		public int getIndex(TreeNode node) 
		{
			return children.indexOf(node);
		}

		@Override
		public TreeNode getParent() 
		{
			return father;
		}

		@Override
		public boolean isLeaf() 
		{
			return getChildCount() == 0;
		}
		
		@Override
		public String toString() {
			return "BrowserCell["+getRepresentedObject()+"]";
		}

		public TreePath getTreePath() 
		{
			return new TreePath(getPathToRoot(this));
		}
	}
	
	
	/*@Override
	public FIBModelObject getRoot()
	{
		FIBModelObject returned = (FIBModelObject)super.getRoot();
		ensureObjectIsRegistered(returned);
		return returned;
	}
	
	@Override
	public Object getChild(Object parent, int index)
	{
		FIBModelObject returned = (FIBModelObject)super.getChild(parent,index);
		ensureObjectIsRegistered(returned);
		return returned;
	}
	
	private void ensureObjectIsRegistered(FIBModelObject o)
	{
		if (o != null) {
		if (contents.get(o) == null) {
			contents.put(o,true);
			o.addObserver(this);
			//System.out.println("addObserver() for "+o);
		}
		}
		// else already registered, do nothing
	}
	
	public void update(Observable o, Object arg)
	{
		if (o instanceof FIBModelObject) {
			if (arg instanceof FIBAddingNotification) {
				nodeStructureChanged((FIBComponent)o);
			}
			else if (arg instanceof FIBRemovingNotification) {
				nodeStructureChanged((FIBComponent)o);
			}
			else if (arg instanceof FIBAttributeNotification) {
				nodeChanged((FIBComponent)o);
			}
		}
	}*/

	public Vector<BrowserCell> getExpandedElements(BrowserCell from)
	{
		Vector<BrowserCell> expandedElements = new Vector<BrowserCell>();
		if (getWidget().getJTree() != null) {
			TreePath treePath = getTreePath(from);
			//TreePath rootTreePath = getWidget().getJTree().getPathForRow(0);
			//System.out.println("treePath="+treePath);
			Enumeration<TreePath> selectionPaths = getWidget().getJTree().getExpandedDescendants(treePath);
			if (selectionPaths != null) {
				while (selectionPaths.hasMoreElements()) {
					TreePath next = selectionPaths.nextElement();
					// logger.info("Expanded "+next);
					logger.info("Expanded object "+next.getLastPathComponent());
					expandedElements.add((BrowserCell) next.getLastPathComponent());
				}
			}
		}
		return expandedElements;
	}


}
