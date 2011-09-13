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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabular.model.EditableColumn;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.rm.FlexoProject;


/**
 * 
 * A TabularBrowserModel defines a model that will be used to build 
 * a JTable or TabularBrowserView.
 * 
 * @author sguerin
 */
public class TabularBrowserModel extends ConfigurableProjectBrowser implements TreeTableModel, DataFlexoObserver
{
    
    private static final Logger logger = Logger.getLogger(TabularBrowserModel.class.getPackage().getName());

    private Vector _columns;

    private int _rowHeight = -1;

    private FlexoProject _project;

    public TabularBrowserModel(BrowserConfiguration configuration, String browsableColumnName, int browsableColumnWidth)
    {
        this(configuration, true, browsableColumnName, browsableColumnWidth);
    }

    public TabularBrowserModel(BrowserConfiguration configuration, boolean initNow, String browsableColumnName, int browsableColumnWidth)
    {
        super(configuration,null,initNow);
        _project = configuration.getProject();
        _columns = new Vector();
        addToColumns(_browsableColumn = new TreeColumn(browsableColumnName, browsableColumnWidth));
    }
    
   private  TreeColumn _browsableColumn;
    
    protected TreeColumn getBrowsableColumn()
    {
        return _browsableColumn;
    }
    
    protected class TreeColumn extends AbstractColumn implements EditableColumn
    {
        public TreeColumn(String title, int defaultWidth)
        {
            this(title, defaultWidth, true);
        }

        public TreeColumn(String title, int defaultWidth, boolean isResizable)
        {
            this(title, defaultWidth, isResizable, true);
        }

        public TreeColumn(String title, int defaultWidth, boolean isResizable, boolean displayTitle)
        {
            super(title, defaultWidth, isResizable, displayTitle);
        }

        @Override
		public Class getValueClass()
        {
            return TreeTableModel.class;
        }

        @Override
		public Object getValueFor(FlexoModelObject object)
        {
            return object;
        }

        @Override
		public void setValueFor(FlexoModelObject object, Object value)
        {
            // We dont care !
        }

        @Override
		public boolean isCellEditableFor(FlexoModelObject object)
        {
            return true;
        }

        @Override
		public String toString()
        {
            return "TreeColumn " + "@" + Integer.toHexString(hashCode());
        }
        
    }

     @Override
	public Object getRoot() 
    {
        return getRootElement();
    }

    @Override
	public int getRowHeight()
    {
        return _rowHeight;
    }

    @Override
	public void setRowHeight(int aRowHeight)
    {
        _rowHeight = aRowHeight;
    }

    public void addToColumns(AbstractColumn aColumn)
    {
        _columns.add(aColumn);
    }

    public void insertColumnAtIndex(AbstractColumn aColumn, int index)
    {
        _columns.insertElementAt(aColumn,index);
    }

    public void removeFromColumns(AbstractColumn aColumn)
    {
        _columns.remove(aColumn);
    }

    public AbstractColumn columnAt(int index)
    {
        AbstractColumn returned = (AbstractColumn) _columns.elementAt(index);
        return returned;
    }

    public int getTotalPreferredWidth()
    {
        int returned = 0;
        for (int i= 0; i<getColumnCount(); i++) {
            returned += getDefaultColumnSize(i);
        }
        return returned;
    }
    
    @Override
	public int getColumnCount()
    {
        return _columns.size();
    }

    @Override
	public String getColumnName(int col)
    {
        AbstractColumn column = columnAt(col);
        if (column != null) {
            return column.getLocalizedTitle();
        }
        return "???";
    }

    public int getDefaultColumnSize(int col)
    {
        AbstractColumn column = columnAt(col);
        if (column != null) {
            return column.getDefaultWidth();
        }
        return 75;
    }

    public boolean getColumnResizable(int col)
    {
        AbstractColumn column = columnAt(col);
        if (column != null) {
            return column.getResizable();
        }
        return true;
    }

    @Override
	public Object getValueAt(Object el, int col) 
    {
        if (el instanceof BrowserElement) {
            BrowserElement element = (BrowserElement)el;
            AbstractColumn column = columnAt(col);
            return column.getValueFor(element.getObject());
        }
        logger.warning("TabularBrowserModel shound contain only BrowserElement instances "+el);
                return null;
    }

    @Override
	public Object getChild(Object parent, int index) 
    {
        if (parent instanceof BrowserElement) {
            BrowserElement element = (BrowserElement)parent;
            return element.getChildAt(index);
        }
        logger.warning("TabularBrowserModel shound contain only BrowserElement instances "+parent);
        return null;
    }

    @Override
	public int getChildCount(Object parent) 
    {
        if (parent instanceof BrowserElement) {
            BrowserElement element = (BrowserElement)parent;
            return element.getChildCount();
        }
        logger.warning("TabularBrowserModel shound contain only BrowserElement instances "+parent);
        return 0;
    }

    @Override
	public Class getColumnClass(int col)
    {
        AbstractColumn column = columnAt(col);
        return column.getValueClass();
    }
    
     @Override
	public boolean isCellEditable(Object element, int col) 
    { 
        if (element instanceof BrowserElement) {
            AbstractColumn column = columnAt(col);
            return column.isCellEditableFor(((BrowserElement)element).getObject());
        }
        return false;
    }
    
    @Override
	public void setValueAt(Object aValue, Object el, int col) 
    {
        if (el instanceof BrowserElement) {
            BrowserElement element = (BrowserElement)el;
            AbstractColumn column = columnAt(col);
            if ((column.isCellEditableFor((element).getObject()))
                    && (column instanceof EditableColumn)) {
                ((EditableColumn)column).setValueFor(element.getObject(),aValue);
                return;
            }
        }
        logger.warning("TabularBrowserModel shound contain only BrowserElement instances "+el);
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification) {
        // TODO Auto-generated method stub        
    }

   /* @Override
    public BrowserElement makeNewElement(FlexoModelObject object) 
    {
    	logger.info("makeNewElement with "+object);
    	return super.makeNewElement(object);
    }*/
    
    @Override
    public void update() {
    	logger.info("Update called");
    	super.update();
    }

}