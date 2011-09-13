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
package org.openflexo.components.tabularbrowser;
/*
 * %W% %E%
 *
 * Copyright 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowserListener;
import org.openflexo.components.browser.ProjectBrowser.DisableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.EnableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.ExpansionNotificationEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectAddedToSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectRemovedFromSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.SelectionClearedEvent;
import org.openflexo.components.tabular.model.HeightAdjustableColumn;
import org.openflexo.components.tabular.model.RowHeightListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.selection.SelectionListener;


/**
 * Basic representation of a JTreeTable given a TabularBrowserModel  
 *
 * @see http://java.sun.com/products/jfc/tsc/articles/treetable1/
 * Updated to fit Flexo architecture
 *
 * @author Philip Milne
 * @author Scott Violet
 * @author Sylvain Guerin
 */

public class JTreeTable extends JTable implements SelectionListener, RowHeightListener, ProjectBrowserListener, TreeSelectionListener, TreeExpansionListener
{
 
    private static final Logger logger = Logger.getLogger(JTreeTable.class.getPackage().getName());

    protected TreeTableCellRenderer tree;
    private TabularBrowserModel _treeTableModel;
    /** This adaptor makes the link between the treemodel (ProjectBrowser) and the tablemodel*/
    private TreeTableModelAdapter _modelAdapter;
    
    private JTreeTableMouseAdapter _mouseAdapter;
    protected ListSelectionModel _listSelectionModel;

    protected Vector _selectedObjects;
    protected boolean _selectedObjectsNeedsRecomputing = true;

    public JTreeTable(TabularBrowserModel treeTableModel) 
    {
        super();
                
        _treeTableModel = treeTableModel;
        _selectedObjects = new Vector();
        // Create the tree. It will be used as a renderer and editor. 
        tree = new TreeTableCellRenderer(this, treeTableModel); 
        
        // Install a tableModel representing the visible rows in the tree. 
        super.setModel(_modelAdapter=new TreeTableModelAdapter(treeTableModel.getDefaultRootObject(),treeTableModel.getProject(), treeTableModel, tree));
        
        // Force the JTable and JTree to share their row selection models. 
        tree.setSelectionModel(new DefaultTreeSelectionModel() { 
            // Extend the implementation of the constructor, as if: 
            /* public this() */ {
                setSelectionModel(listSelectionModel); 
                _listSelectionModel = listSelectionModel;
          } 
        }); 
        // Make the tree and table row heights the same. 
        tree.setRowHeight(getRowHeight());
        
        
        
        for (int i = 0; i < treeTableModel.getColumnCount(); i++) {
            TableColumn col = getColumnModel().getColumn(i);
            col.setPreferredWidth(treeTableModel.getDefaultColumnSize(i));
            if (treeTableModel.getColumnResizable(i)) {
                col.setResizable(true);
            } else {
                col.setWidth(treeTableModel.getDefaultColumnSize(i));
                col.setMinWidth(treeTableModel.getDefaultColumnSize(i));
                col.setMaxWidth(treeTableModel.getDefaultColumnSize(i));
                col.setResizable(false);
            }
            treeTableModel.columnAt(i).setModel(_modelAdapter);
            if (treeTableModel.columnAt(i) instanceof TabularBrowserModel.TreeColumn) 
            {
                col.setCellRenderer(tree);
                col.setCellEditor(new TreeTableCellEditor());               
            }
            else {
                if (treeTableModel.columnAt(i).requireCellRenderer()) {
                    col.setCellRenderer(treeTableModel.columnAt(i).getCellRenderer());
                }
                if (treeTableModel.columnAt(i).requireCellEditor()) {
                    col.setCellEditor(treeTableModel.columnAt(i).getCellEditor());
                }
            }
            if (treeTableModel.columnAt(i) instanceof HeightAdjustableColumn) {
                ((HeightAdjustableColumn)treeTableModel.columnAt(i)).addRowHeightListener(this);
            }   
         }
        
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0)); 	      
        
        _mouseAdapter = new JTreeTableMouseAdapter(this);
        addMouseListener(_mouseAdapter);
        addMouseMotionListener(_mouseAdapter);
        
        treeTableModel.addBrowserListener(this);

    }
    
    protected void treeStructureChanged()
    {
        ((TreeTableModelAdapter)getModel()).fireTableDataChanged();
        for (int i = 0; i < _treeTableModel.getColumnCount(); i++) {
            TableColumn col = getColumnModel().getColumn(i);
             if (_treeTableModel.columnAt(i) instanceof HeightAdjustableColumn) {
                 HeightAdjustableColumn column = (HeightAdjustableColumn)_treeTableModel.columnAt(i);
                 for (int row = 0; row<getRowCount(); row++) {
                     notifyRowHeightChanged(row,column.getRowHeight(row));
                 }
             }   
         }
    }
    

    @Override
	public void setRowHeight(int rowHeight)
    {
        super.setRowHeight(rowHeight);
        if (tree != null) tree.setRowHeight(rowHeight);
    }

   /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to 
     * paint the editor. The UI currently uses different techniques to 
     * paint the renderers and editors and overriding setBounds() below 
     * is not the right thing to do for an editor. Returning -1 for the 
     * editing row in this case, ensures the editor is never painted. 
     */
    @Override
	public int getEditingRow() {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;  
    }
    
    // 
    // The renderer used to display the tree nodes, a JTree.  
    //
    
    public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
		public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int r, int c) {
            return tree;
        }
    }

    public TabularBrowserModel getTreeTableModel()
    {
        return _treeTableModel;
    }

    public BrowserElement getElementAt(int row)
    {
        return tree.getElementAt(row);
    }
    
    public FlexoModelObject getObjectAt(int row)
    {
        BrowserElement element = getElementAt(row);
        if (element != null) return element.getObject();
        return null;
    }
    
    public Vector getSelectedObjects()
    {
        if (_selectedObjectsNeedsRecomputing) {
            _selectedObjects.clear();
            for (int i = 0; i < getRowCount(); i++) {
                if (_listSelectionModel.isSelectedIndex(i)) {
                    _selectedObjects.add(getObjectAt(i));
                }
            }
            _selectedObjectsNeedsRecomputing = false;
        }
        return _selectedObjects;
    }

   public boolean isSelected(FlexoModelObject object)
    {
        return getSelectedObjects().contains(object);
    }


    public ListSelectionModel getTreeTableSelectionModel() 
    {
        return _listSelectionModel;
    }
    
    public ProjectBrowser getProjectBrowser() 
    {
        return _treeTableModel;
    }
        
    @Override
	public void fireObjectSelected(FlexoModelObject object)
    {
         if (logger.isLoggable(Level.FINE))
         logger.fine ("JTreeTable fireObjectSelected() with "+object);
         
         TreePath[] paths = getProjectBrowser().treePathForObject(object);
         if (paths == null) return;
         for (int i=0; i<paths.length; i++) {
                 TreePath path = paths[i];
                 tree.makeVisible(path);
                 int row = tree.getRowForPath(path);
                 _listSelectionModel.addSelectionInterval(row, row);
         }
         
         _selectedObjectsNeedsRecomputing = true;
     }
    
    @Override
	public void fireObjectDeselected(FlexoModelObject object)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine ("JTreeTable fireObjectDeselected() with "+object);

            TreePath[] paths = getProjectBrowser().treePathForObject(object);
            if (paths == null)
                return;
                for (int i=0; i<paths.length; i++) {
                    TreePath path = paths[i];
                     if (tree.isVisible(path)) {
                        int row = tree.getRowForPath(path);
                        _listSelectionModel.removeSelectionInterval(row, row);                       
                    }
                }
                tree.removeSelectionPaths(paths);
            _selectedObjectsNeedsRecomputing = true;

     }
    
    @Override
	public void fireResetSelection()
    {
        _selectedObjectsNeedsRecomputing = true;
        _listSelectionModel.clearSelection();
     }


    public boolean mayRepresents (FlexoModelObject anObject)
    {
        //logger.warning("Implements me later !");
        return true;
    }
    
    @Override
	public void fireBeginMultipleSelection()
    {
    }

    @Override
	public void fireEndMultipleSelection() 
    {
    }

    @Override
	public void valueChanged(ListSelectionEvent e)
    {
        super.valueChanged(e);
        
        // Ignore extra messages.
        if (e.getValueIsAdjusting())
            return;
        
        _selectedObjectsNeedsRecomputing = true;
    }

    @Override
	public void notifyRowHeightChanged(int row, int newRowHeight)
    {
        if (newRowHeight > 0) {
            if (logger.isLoggable(Level.FINE))
                logger.fine("notifyRowHeightChanged row="+row+" new height="+newRowHeight);
            setRowHeight(row,newRowHeight);
            tree.setRowHeight(row,newRowHeight);
        }
    }
    
    public TreeTableCellRenderer getTree() 
    {
        return tree;
    }

    private boolean superviseExpansion = false;

    private Vector<BrowserElement> expansionSupervisedElements;


    @Override
	public void objectAddedToSelection(ObjectAddedToSelectionEvent event)
    {
    	//logger.info("**** objectAddedToSelection() with "+event);
        TreePath[] paths = _treeTableModel.treePathForObject(event.getAddedObject());
        if (paths == null)
            return;
        if (logger.isLoggable(Level.FINE))
            logger.fine("BrowserView.objectAddedToSelection() " + event.getAddedObject() + " paths=" + paths);
            superviseExpansion = true;
            expansionSupervisedElements = new Vector();
            for (int i = 0; i < paths.length; i++) {
                BrowserElement element = (BrowserElement) paths[i].getLastPathComponent();
                expansionSupervisedElements.add(element);
            }
            tree.removeTreeSelectionListener(this);
            tree.addSelectionPaths(paths);
            tree.addTreeSelectionListener(this);
            if (logger.isLoggable(Level.FINE))
                logger.fine("Added " + event.getAddedObject());
            superviseExpansion = false;
        //selectedElementsNeedRecomputing = true;
        /*if (_treeTableModel.handlesControlPanel()) {
            controlPanel.handleSelectionChanged();
        }*/
    }

    @Override
	public void objectRemovedFromSelection(ObjectRemovedFromSelectionEvent event)
    {
    	//logger.info("**** objectRemovedFromSelection() with "+event);
        if (event.getRemovedObject() != null) {
            TreePath[] paths = _treeTableModel.treePathForObject(event.getRemovedObject());
            if (paths == null)
                return;
            if (logger.isLoggable(Level.FINE))
                logger.fine("BrowserView.objectRemovedFromSelection() " + event.getRemovedObject() + " paths=" + paths);
            tree.removeTreeSelectionListener(this);
            tree.removeSelectionPaths(paths);
            tree.addTreeSelectionListener(this);
            if (logger.isLoggable(Level.FINE))
            	logger.fine("Removed " + event.getRemovedObject());
        }
    }

    @Override
	public void selectionCleared(SelectionClearedEvent event)
    {
    	//logger.info("**** selectionCleared() with "+event);
        /* if (_treeTableModel.handlesControlPanel()) {
            controlPanel.handleSelectionCleared();
        }*/
        tree.removeTreeSelectionListener(this);
        tree.clearSelection();
        tree.addTreeSelectionListener(this);
    }


    @Override
	public void optionalFilterAdded(ProjectBrowser.OptionalFilterAddedEvent event)
    {
      /*   if (_treeTableModel.handlesControlPanel()) {
            controlPanel.handleOptionalFilterAdded();
        } */
    }

    @Override
	public void notifyExpansions(ExpansionNotificationEvent event)
    {
    	//logger.info("**** notifyExpansions() with "+event);
    	_treeTableModel.deleteBrowserListener(this);
    	tree.removeTreeExpansionListener(this);
        for (Enumeration e = event.pathsToExpand().elements(); e.hasMoreElements();) {
            TreePath path = (TreePath) e.nextElement();
            if (tree.isCollapsed(path)) {
            	tree.expandPath(path);
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Expand " + path);
            }
        }
        for (Enumeration e = event.pathsToCollabse().elements(); e.hasMoreElements();) {
            TreePath path = (TreePath) e.nextElement();
            if (tree.isExpanded(path)) {
            	tree.collapsePath(path);
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Collabse " + path);
            }
        }
        tree.addTreeExpansionListener(this);
        _treeTableModel.addBrowserListener(this);
    }

    @Override
	public void enableExpandingSynchronization(EnableExpandingSynchronizationEvent event)
    {
    	//logger.info("**** enableExpandingSynchronization() with "+event);
        if (logger.isLoggable(Level.FINE))
            logger.fine("enableExpandingSynchronization()");
        tree.addTreeExpansionListener(this);
    }

    @Override
	public void disableExpandingSynchronization(DisableExpandingSynchronizationEvent event)
    {
    	//logger.info("**** disableExpandingSynchronization() with "+event);
        if (logger.isLoggable(Level.FINE))
            logger.fine("disableExpandingSynchronization()");
        tree.removeTreeExpansionListener(this);
    }

    /**
     * Implements
     *
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
	public void valueChanged(TreeSelectionEvent e)
    {
    	//logger.info("**** valueChanged() with "+e);
       if (logger.isLoggable(Level.FINE))
            logger.fine("valueChanged() " + e);
        //selectedElementsNeedRecomputing = true;
        if (_treeTableModel.getSelectionManager() != null) {
        	_treeTableModel.deleteBrowserListener(this);
            TreePath[] selectionChanges = e.getPaths();
            for (int i = 0; i < selectionChanges.length; i++) {
                BrowserElement element = (BrowserElement) selectionChanges[i].getLastPathComponent();
                if (e.isAddedPath(selectionChanges[i])) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("valueChanged() for ADDITION " + element.getSelectableObject());
                    _treeTableModel.getSelectionManager().addToSelected(element.getSelectableObject());
                    // _browser.addToSelected(element.getSelectableObject());
                } else {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("valueChanged() for REMOVING " + element.getSelectableObject());
                    _treeTableModel.getSelectionManager().removeFromSelected(element.getSelectableObject());
                    // _browser.removeFromSelected(element.getSelectableObject());
                }
            }
            _treeTableModel.addBrowserListener(this);
            _treeTableModel.getSelectionManager().updateSelectionForMaster(_treeTableModel);
            if (logger.isLoggable(Level.FINE)) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine(_treeTableModel.getSelectionManager().toString());
            }
            if (_treeTableModel.getSelectionManager().getContextualMenuManager() != null)
                if (_treeTableModel.getSelectionManager().getContextualMenuManager().isPopupMenuDisplayed())
                	_treeTableModel.getSelectionManager().getContextualMenuManager().hidePopupMenu();
        }
        /*if (_treeTableModel.handlesControlPanel()) {
            controlPanel.handleSelectionChanged();
        }*/
    }

    /**
     * Implements
     *
     * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
     * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
     */
    @Override
	public void treeExpanded(TreeExpansionEvent event)
    {
    	//logger.info("**** treeExpanded() with "+event);
        BrowserElement element = (BrowserElement) event.getPath().getLastPathComponent();
        if (logger.isLoggable(Level.FINE))
            logger.fine("Tree may expand for " + element);

         // Lets look at the expansion supervising
        
        if (_treeTableModel.isExpansionSynchronizedElement(element) && element.isSynchronizeExpansionEnabled()) {
        	
        	// At this level, element is considered as expansion supervised
            boolean doExpand = true;
            ExpansionSynchronizedElement elementToExpand = (ExpansionSynchronizedElement) element;
            if (superviseExpansion) {
            	// BUT.... here, the flag superviseExpansion indicates that this tree expansion
            	// has its origin in the fact that one or more objects were selected.
            	// In this case, this is not sure that the expansion whould be synchronized
            	// We have to look that at least one of the selected element requires expansion
                doExpand = false;
                for (Enumeration e = expansionSupervisedElements.elements(); e.hasMoreElements();) {
                    BrowserElement next = (BrowserElement) e.nextElement();
                    if (elementToExpand.requiresExpansionFor(next)) {
                        // Selecting next requires expansion for elementToExpand
                        doExpand = true;
                    }
                }
            }
            if (doExpand) {
                // Finally i decide to expand
                elementToExpand.expand();
            }
        }
    }

    /**
     * Implements
     *
     * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
     * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
     */
    @Override
	public void treeCollapsed(TreeExpansionEvent event)
    {
    	//logger.info("**** treeCollapsed() with "+event);
        BrowserElement element = (BrowserElement) event.getPath().getLastPathComponent();
        if (logger.isLoggable(Level.FINE))
            logger.fine("Tree collabsed for " + element);
        if (_treeTableModel.isExpansionSynchronizedElement(element)) {
            ((ExpansionSynchronizedElement) element).collapse();
        }
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) 
    {
    	TableCellRenderer returned = super.getCellRenderer(row, column);
    	if (returned == null) {
    		logger.warning("null TableCellRenderer for row="+row+" column="+column);
    		// SGU: I don't understand this bug: big HACK to avoid problem !!!
    		return new DefaultTableCellRenderer();
    	}
    	return returned;
    }

       
}

