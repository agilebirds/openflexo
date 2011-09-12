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
package org.openflexo.fib.editor.controller;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.fib.editor.notifications.FIBEditorNotification;
import org.openflexo.fib.editor.notifications.SelectedObjectChange;
import org.openflexo.fib.model.FIBAddingNotification;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRemovingNotification;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;


public class FIBEditorBrowser implements TreeSelectionListener, Observer {

	private final JTree tree;
	private final FIBComponentTreeModel treeModel;
	private final JScrollPane scrollPane;
	private final FIBEditorController controller;
	
	public FIBEditorBrowser(FIBComponent fibComponent, FIBEditorController controller) 
	{
		super();
		
		this.controller = controller;
		
		controller.addObserver(this);
		
		treeModel = new FIBComponentTreeModel(fibComponent);
		/*treeModel.addTreeModelListener(new TreeModelListener() {
			
			public void treeStructureChanged(TreeModelEvent e)
			{
				System.out.println("treeStructureChanged "+e.getTreePath());
			}
			
			public void treeNodesRemoved(TreeModelEvent e)
			{
				System.out.println("treeNodesRemoved "+e.getTreePath());
			}
			
			public void treeNodesInserted(TreeModelEvent e)
			{
				System.out.println("treeNodesInserted "+e.getTreePath());
			}
			
			public void treeNodesChanged(TreeModelEvent e)
			{
				System.out.println("treeNodesChanged "+e.getTreePath());
			}
		});*/
		tree = new JTree(treeModel);
		tree.setFocusable(true);
	
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() || (e.getButton()==MouseEvent.BUTTON3)) {
					Object node = tree.getLastSelectedPathComponent();
					if (node instanceof FIBModelObject) {
						getController().getContextualMenu().displayPopupMenu((FIBModelObject)node, tree, e);
					}
				}
			}
		});
		
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
		    @Override
			public Component getTreeCellRendererComponent(JTree tree, Object value,
					  boolean sel,
					  boolean expanded,
					  boolean leaf, int row,
					  boolean hasFocus) {
		    	Component returned = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		    	if ((returned instanceof DefaultTreeCellRenderer) && (value instanceof FIBComponent)) {
		    		((DefaultTreeCellRenderer)returned).setIcon(iconFor((FIBComponent)value));
		    		((DefaultTreeCellRenderer)returned).setText(textFor((FIBComponent)value));
		    	}
		    	//System.out.println("value="+value+" returned="+returned);
		    	return returned;
		    }
		});
		
	    tree.getSelectionModel().setSelectionMode
	            (TreeSelectionModel.SINGLE_TREE_SELECTION);

	    //Listen for when the selection changes.
	    tree.addTreeSelectionListener(this);
	    
	    tree.setPreferredSize(new Dimension(300,600));

		scrollPane = new JScrollPane(tree);

	}
	
	public FIBEditorController getController()
	{
		return controller;
	}
	
	private ImageIcon iconFor(FIBComponent component)
	{
		if (component.isRootComponent()) {
			return FIBEditorIconLibrary.ROOT_COMPONENT_ICON;
		} else if (component instanceof FIBTabPanel) {
			return FIBEditorIconLibrary.TABS_ICON;
		} else if (component instanceof FIBPanel) {
			return FIBEditorIconLibrary.PANEL_ICON;
		} else if (component instanceof FIBCheckBox) {
			return FIBEditorIconLibrary.CHECKBOX_ICON;
		} else if (component instanceof FIBLabel) {
			return FIBEditorIconLibrary.LABEL_ICON;
		} else if (component instanceof FIBTable) {
			return FIBEditorIconLibrary.TABLE_ICON;
		} else if (component instanceof FIBBrowser) {
			return FIBEditorIconLibrary.TREE_ICON;
		} else if (component instanceof FIBTextArea) {
			return FIBEditorIconLibrary.TEXTAREA_ICON;
		} else if (component instanceof FIBTextField) {
			return FIBEditorIconLibrary.TEXTFIELD_ICON;
		} else if (component instanceof FIBNumber) {
			return FIBEditorIconLibrary.NUMBER_ICON;
		} else if (component instanceof FIBDropDown) {
			return FIBEditorIconLibrary.DROPDOWN_ICON;
		}
		return null;
		
	}
	
	private String textFor(FIBComponent component)
	{
		if (component.getName() != null) {
			return component.getName()+" ("+component.getClass().getSimpleName()+")";
		}
		else if (component.getIdentifier() != null) {
			return component.getIdentifier()+" ("+component.getClass().getSimpleName()+")";
		} else {
			return "<"+component.getClass().getSimpleName()+">";
		}
	}

	public JTree getTree() {
		return tree;
	}

	public TreeModel getTreeModel() {
		return treeModel;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public void valueChanged(TreeSelectionEvent e) 
	{
		Object node = tree.getLastSelectedPathComponent();

		if (node instanceof FIBComponent) {
			controller.setSelectedObject((FIBComponent)node);
		}
		
	}
	
	public void update(Observable o, Object notification) 
	{
		if (notification instanceof FIBEditorNotification) {
			if (notification instanceof SelectedObjectChange) {
				SelectedObjectChange selectionChange = (SelectedObjectChange)notification;
				if (selectionChange.newValue() != null) {
					tree.setSelectionPath(pathForObject(selectionChange.newValue()));
				} else {
					tree.clearSelection();
				}
			}
		}
	}
	
	private TreePath pathForObject(FIBComponent o)
	{
		if (o.getParent() != null) {
			return pathForObject(o.getParent()).pathByAddingChild(o);
		} else {
			return new TreePath(o);
		}
	}
	
	class FIBComponentTreeModel extends DefaultTreeModel implements Observer
	{
		private final Hashtable<FIBModelObject,Boolean> contents;
		
		public FIBComponentTreeModel(FIBComponent component)
		{
			super(component);
			contents = new Hashtable<FIBModelObject,Boolean>();
		}
		
		@Override
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
		}
	}

}
