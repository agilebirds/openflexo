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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.selection.ContextualMenuManager;
import org.openflexo.selection.DefaultContextualMenuManager;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionSynchronizedComponent;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;


/**
 * Tabular and browsable view representing an TabularBrowserModel
 * 
 * @author sguerin
 * 
 */
public class TabularBrowserView extends JPanel 
implements TableModelListener, ListSelectionListener, GraphicalFlexoObserver, SelectionSynchronizedComponent
{

    protected static final Logger logger = Logger.getLogger(TabularBrowserView.class.getPackage().getName());

    protected FlexoController _controller;
    protected SelectionManager _selectionManager;

    protected JTreeTable _treeTable;

    protected TabularBrowserModel _model;

    private JScrollPane scrollPane;
    
    private TabularBrowserFooter _footer;
    
    private FlexoEditor _editor;
    
    private boolean _synchronizeWithSelectionManager = false;
    
     public TabularBrowserView(FlexoController controller, TabularBrowserModel model, int visibleRowCount, FlexoEditor editor)
    {
        this(controller, model,editor);
        setVisibleRowCount(visibleRowCount);
    }

    public TabularBrowserView(FlexoController controller, TabularBrowserModel model, FlexoEditor editor)
    {
        super();
        _model = model;
        _editor = editor;
        _controller = controller;
        if (_controller instanceof SelectionManagingController) {
            _selectionManager = ((SelectionManagingController)_controller).getSelectionManager();
        }
 
        _treeTable = new JTreeTable(model);
        //_treeTable.setPreferredSize(new Dimension(model.getTotalPreferredWidth(),100));

        if (model.getRowHeight() > 0) {
            _treeTable.setRowHeight(model.getRowHeight());
        }

        _treeTable.setShowVerticalLines(true);

        _treeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         getSelectionModel().addListSelectionListener(this);

        scrollPane = new JScrollPane(_treeTable);
        setLayout(new BorderLayout());
        //_treeTable.getTableHeader().setPreferredSize(new Dimension(model.getTotalPreferredWidth(),100));
        add(_treeTable.getTableHeader(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        //add(new JPanel(),BorderLayout.SOUTH);
        
        if (getBrowser().handlesControlPanel()) {
            _footer = new TabularBrowserFooter(this);
            add(_footer, BorderLayout.SOUTH);
        }

        _treeTable.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e)
            {
                if (getContextualMenuManager() != null)
                    getContextualMenuManager().processMousePressed(e);
            }
            @Override
			public void mouseReleased(MouseEvent e)
            {
                if (getContextualMenuManager() != null)
                    getContextualMenuManager().processMouseReleased(e);
            }
        });
        _treeTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
			public void mouseMoved(MouseEvent e)
            {
                if (getContextualMenuManager() != null)
                    getContextualMenuManager().processMouseMoved(e);
            }
         });
    

        validate();

    }
    
    /**
     * !!!!!!!!!! IMPORTANT !!!!!!!!
     * 
     * This hack is a workaround to prevent arrival on a NPE while
     * such a component is added to the container hierarchy in a 
     * context i still don't understand, but certainly due to adding
     * of JTextArea when using TextColumn. Seems that this is not
     * so important, but....
     * 
     * Overrides @see java.awt.Component#addNotify()
     * @see java.awt.Component#addNotify()
     */
    @Override
	public void addNotify() 
    {
        logger.fine("BEGIN addNotify() NPE catcher");
        try {
            super.addNotify();
        }
        catch (NullPointerException e) {
            logger.fine("NPE caught!");
            for (int i = 0 ; i < getComponentCount() ; i++) {
                logger.fine("addNotify() for "+getComponent(i));
                getComponent(i).addNotify();
            }
        }
        logger.fine("END addNotify() NPE catcher");
    }
    
    public ProjectBrowser getBrowser()
    {
        return getTreeTable().getProjectBrowser();
    }

    public BrowserElement getSelectedElement()
    {
        return (BrowserElement) getTreeTable().getTree().getLastSelectedPathComponent();
    }

   public FlexoModelObject getSelectedObject()
    {
        if (getSelectedElement() != null) {
            return getSelectedElement().getObject();
        }
        return null;
    }

    public JTreeTable getTreeTable() 
    {
        return _treeTable;
    }

    private DefaultContextualMenuManager defaultContextualMenuManager;

    protected ContextualMenuManager getContextualMenuManager()
    {
        if ((_selectionManager != null) && (_synchronizeWithSelectionManager)) {
            return _selectionManager.getContextualMenuManager();
        }
        if (defaultContextualMenuManager == null) {
            defaultContextualMenuManager = new DefaultContextualMenuManager();
        }
        return defaultContextualMenuManager;
    }
    

    public ListSelectionModel getSelectionModel() 
    {
        return _treeTable.getTreeTableSelectionModel();
    }
    

    public TabularBrowserModel getModel()
    {
        return _model;
    }

    public void focusOn (FlexoModelObject object)
    {
        setSelectedObject(object);
    }
    
    public void setVisibleRowCount(int rows)
    {
        int height = 0;
        for (int row = 0; row < rows; row++)
            height += _treeTable.getRowHeight(row);
        _treeTable.setPreferredScrollableViewportSize(new Dimension(_treeTable.getPreferredScrollableViewportSize().width, height));
    }

    public boolean isSelected(FlexoModelObject object)
    {
        return _treeTable.isSelected(object);
    }


    public Vector getObjects()
    {
        return getSelectedObjects();
    }

    public Vector getSelectedObjects()
    {
        return _treeTable.getSelectedObjects();
    }

    private boolean _isSelected = false;

    public boolean isSelected()
    {
        return _isSelected;
    }

    public void setIsSelected(boolean b)
    {
        _isSelected = b;
    }

    public FlexoModelObject getRootObject()
    {
        return _model.getRootObject();
    }

    /**
     * Implements
     * 
     * @see org.openflexo.view.InspectableObjectView#getInspectedObject()
     * @see org.openflexo.view.InspectableObjectView#getInspectedObject()
     */
    public InspectableObject getInspectedObject()
    {
        if (getRootObject() instanceof InspectableObject)
            return (InspectableObject)getRootObject();
        return null;
    }

    /**
     * Implements
     * 
     * @see org.openflexo.view.MultipleInspectableObjectView#getInspectedObjects()
     * @see org.openflexo.view.MultipleInspectableObjectView#getInspectedObjects()
     */
    public Vector getInspectedObjects()
    {
        return getObjects();
    }

    /**
     * Overrides
     * 
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable o, DataModification dataModification)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("update received in TabularBrowserView for " + o+" dataModification="+dataModification);
    }

    /**
     * Overrides
     * 
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    @Override
	public void tableChanged(TableModelEvent e)
    {
    }

    public FlexoModelObject getObject() 
    {
        return getRootObject();
    }

   public boolean isSelected(Vector objectList)
    {
        return getSelectedObjects().containsAll(objectList);
    }

    public boolean isSynchronizedWithSelectionManager()
    {
        return _synchronizeWithSelectionManager;
    }

    public void setSynchronizeWithSelectionManager(boolean synchronizeWithSelectionManager) 
    {
        _synchronizeWithSelectionManager = synchronizeWithSelectionManager;
        if (synchronizeWithSelectionManager) {
        	if (_selectionManager != null) {
        		_treeTable.getTreeTableModel().setSelectionManager(_selectionManager);
                _selectionManager.addToSelectionListeners(this);
       	}
            updateSelection();
        }
    }
    
    @Override
	public SelectionManager getSelectionManager() 
    {
        return _selectionManager;
    }

    @Override
	public Vector<FlexoModelObject> getSelection()
    {
        if (getSelectionManager() != null)
            return getSelectionManager().getSelection();
        return null;
    }

    @Override
	public void resetSelection() 
    {
        if (getSelectionManager() != null) {
            getSelectionManager().resetSelection();
        }
        else {
            fireResetSelection();
        }
    }

   @Override
public void addToSelected(FlexoModelObject object)
    {
       if (mayRepresents(object)) {
           if (getSelectionManager() != null) {
               getSelectionManager().addToSelected(object);
           }
           else {
               fireObjectSelected(object);
           }    
       }
   }

   @Override
public void removeFromSelected(FlexoModelObject object) 
   {
       if (mayRepresents(object)) {
           if (getSelectionManager() != null) {
               getSelectionManager().removeFromSelected(object);
           }
           else {
               fireObjectDeselected(object);
           }
       }
   }

    @Override
	public void addToSelected(Vector<? extends FlexoModelObject> objects) 
    {
        if (getSelectionManager() != null) {
            getSelectionManager().addToSelected(objects);
        }
        else {
            fireBeginMultipleSelection();
            for (Enumeration en=objects.elements(); en.hasMoreElements();) {
                FlexoModelObject next = (FlexoModelObject)en.nextElement();
                fireObjectSelected(next);
            }
            fireEndMultipleSelection();
       }
    }

    @Override
	public void removeFromSelected(Vector<? extends FlexoModelObject> objects) 
    {
        if (getSelectionManager() != null) {
            getSelectionManager().removeFromSelected(objects);
        }
        else {
            fireBeginMultipleSelection();
            for (Enumeration en=objects.elements(); en.hasMoreElements();) {
                FlexoModelObject next = (FlexoModelObject)en.nextElement();
                fireObjectDeselected(next);
            }
            fireEndMultipleSelection();           
        }
    }

    @Override
	public void setSelectedObjects(Vector<? extends FlexoModelObject> objects)
    {
        if (getSelectionManager() != null) {
            getSelectionManager().setSelectedObjects(objects);
        }
        else {
            resetSelection();
            addToSelected(objects);
        }
    }

    public void setSelectedObject(FlexoModelObject object)
    {
        if (getSelectionManager() != null) {
            getSelectionManager().setSelectedObject(object);
        }
        else {
            resetSelection();
            addToSelected(object);
        }
    }

    @Override
	public boolean mayRepresents (FlexoModelObject anObject)
    {
        return _treeTable.mayRepresents(anObject);
    }
    

    @Override
	public FlexoModelObject getFocusedObject() 
    {
        if (getSelectionManager() != null)
            return getSelectionManager().getFocusedObject();
        return null;
    }

    @Override
	public void fireObjectSelected(FlexoModelObject object)
    {
    	if (mayRepresents(object)) {
    		getSelectionModel().removeListSelectionListener(this);
    		_treeTable.fireObjectSelected(object);
    		getSelectionModel().addListSelectionListener(this);
    	}
    	else {
    		fireResetSelection();
    	}
        if (getBrowser().handlesControlPanel()) {
            _footer.handleSelectionChanged();
        }

    }
    
    @Override
	public void fireObjectDeselected(FlexoModelObject object)
    {
    	if (mayRepresents(object)) {
    		getSelectionModel().removeListSelectionListener(this);
    		_treeTable.fireObjectDeselected(object);
    		getSelectionModel().addListSelectionListener(this);
    	}
    	if (getBrowser().handlesControlPanel()) {
            _footer.handleSelectionChanged();
        }
    }
    
    @Override
	public void fireResetSelection()
    {
        getSelectionModel().removeListSelectionListener(this);
        _treeTable.fireResetSelection();
        getSelectionModel().addListSelectionListener(this);
        if (getBrowser().handlesControlPanel()) {
            _footer.handleSelectionCleared();
        }
    }
      
    @Override
	public void fireBeginMultipleSelection()
    {
        _treeTable.fireBeginMultipleSelection();
    }
    
    @Override
	public void fireEndMultipleSelection() 
    {
        _treeTable.fireEndMultipleSelection();
    }
    
    /**
     * Update selection
     */
    public void updateSelection()
    {
        if (getSelectionManager() != null) {
            getSelectionManager().fireUpdateSelection(this);
        }
        if (getBrowser().handlesControlPanel()) {
            _footer.handleSelectionChanged();
        }
    }
   
    @Override
	public void valueChanged(ListSelectionEvent e)
    {
        // Ignore extra messages.
        if (e.getValueIsAdjusting())
            return;

        if ((_selectionManager != null) && (_synchronizeWithSelectionManager)) {
            
            if (logger.isLoggable(Level.FINE))
                logger.fine("valueChanged() ListSelectionEvent=" + e + " ListSelectionModel=" + getSelectionModel().toString());
            
            /* At least one of this item has change */
            int beginIndex = e.getFirstIndex();
            int endIndex = e.getLastIndex();
            
            Vector toBeRemovedFromSelection = new Vector();
            Vector toBeAddedToSelection = new Vector();
                        
            for (int i = beginIndex; i <= endIndex; i++) {
                FlexoModelObject object = _treeTable.getObjectAt(i);
                if (getSelectionModel().isSelectedIndex(i) 
                        != _selectionManager.selectionContains(object)) {
                    //logger.info("Selection status for object "+object+" at index "+i+" has changed");
                    if (getSelectionModel().isSelectedIndex(i)) {
                        // Change for addition
                        toBeAddedToSelection.add(object);
                    }
                    else {
                        // Change for removing
                        toBeRemovedFromSelection.add(object);
                    }
                }
            }
            
            for (Enumeration en=toBeAddedToSelection.elements(); en.hasMoreElements();) {
                FlexoModelObject next = (FlexoModelObject)en.nextElement();
                getSelectionManager().addToSelected(next);
            }
            
            for (Enumeration en=toBeRemovedFromSelection.elements(); en.hasMoreElements();) {
                FlexoModelObject next = (FlexoModelObject)en.nextElement();
                getSelectionManager().removeFromSelected(next);
            }
                 
            
        }
        
        if (getBrowser().handlesControlPanel()) {
            _footer.handleSelectionChanged();
        }

    }

	public FlexoEditor getEditor() {
		return _editor;
	}
}
