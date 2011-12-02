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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openflexo.ColorCst;
import org.openflexo.application.FlexoApplication;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.view.BrowserViewCellEditor;
import org.openflexo.components.browser.view.BrowserViewCellRenderer;
import org.openflexo.foundation.FlexoModelObject;

/**
 * Cell renderer for a JTreeTable
 * 
 * @author sguerin
 */
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

	private static final Logger logger = Logger.getLogger(TreeTableCellRenderer.class.getPackage().getName());

	private final JTreeTable _table;
	protected int visibleRow;
	protected Hashtable<Integer, BrowserElement> _elementsForRow;
	protected Hashtable<FlexoModelObject, Integer> _rowForObjects;
	protected Hashtable<FlexoModelObject, Integer> _heightForObjects;
	private JTreeMouseEventPreprocessor _mouseEventPreprocessor;
	protected boolean pointerOnTree = false;

	public TreeTableCellRenderer(JTreeTable table, TreeModel model) {
		super(model);
		_elementsForRow = new Hashtable<Integer, BrowserElement>();
		_rowForObjects = new Hashtable<FlexoModelObject, Integer>();
		_heightForObjects = new Hashtable<FlexoModelObject, Integer>();
		_table = table;
		JTreeCellRenderer renderer = new JTreeCellRenderer();
		setCellRenderer(renderer);
		setCellEditor(new BrowserViewCellEditor(this, renderer));
		addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						treeStructureChanged();
					}
				});
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						treeStructureChanged();
					}
				});
			}
		});
		JTreeMouseAdapter _mouseAdapter = new JTreeMouseAdapter();
		_table.addMouseListener(_mouseAdapter);
		_table.addMouseMotionListener(_mouseAdapter);
		_mouseEventPreprocessor = new JTreeMouseEventPreprocessor();
	}

	private class JTreeMouseAdapter extends MouseAdapter implements MouseMotionListener {
		JTreeMouseAdapter() {
			super();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			notifyPointerOn(e.getPoint());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			notifyPointerOn(e.getPoint());
			if (pointerOnTree) {
				pointerExitedTree();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			notifyPointerOn(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			notifyPointerOn(e.getPoint());
		}
	}

	@Override
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		/**
		 * GPO: The code hereunder is here to prevent a same tree selection listener from being added twice to the same view making all
		 * further add/remove (treeSelectionListener()) call completely useless. This was called "Bug critique difficilement reproductible"
		 * reported by FVA.
		 */
		TreeSelectionListener[] t = listenerList.getListeners(TreeSelectionListener.class);
		for (int i = 0; i < t.length; i++) {
			TreeSelectionListener treeSelectionListener = t[i];
			if (treeSelectionListener == tsl) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Adding twice the same tsl: " + tsl + ". Preventing this by returning");
				}
				return;
			}
		}
		super.addTreeSelectionListener(tsl);
	}

	@Override
	public void removeTreeSelectionListener(TreeSelectionListener tsl) {
		/**
		 * GPO: The code hereunder is here to track when we are not really removing <code>BrowserView.this</code> from the tree selection
		 * listeners because another action has already performed this. This trace is very interesting to find which event is starting the
		 * other one. You should see next a log "Adding twice the same tsl: " following this log. This was called
		 * "Bug critique difficilement reproductible" reported by FVA.
		 */
		boolean isInList = false;
		TreeSelectionListener[] t = listenerList.getListeners(TreeSelectionListener.class);
		for (int i = 0; i < t.length; i++) {
			TreeSelectionListener treeSelectionListener = t[i];
			if (treeSelectionListener == tsl) {
				isInList = true;
			}
		}
		if (!isInList) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Nothing to remove!");
			}
		}
		super.removeTreeSelectionListener(tsl);
	}

	protected void notifyPointerOn(Point p) {
		// logger.info("notifyPointerOn "+p);
		if ((_table.columnAtPoint(p) == 0) && (!pointerOnTree)) {
			pointerEnteredTree();
		} else if ((_table.columnAtPoint(p) != 0) && (pointerOnTree)) {
			pointerExitedTree();
		}
	}

	protected void pointerEnteredTree() {
		// logger.info("Mouse entering, applying event translator ");
		pointerOnTree = true;
		FlexoApplication.eventProcessor.setPreprocessor(_mouseEventPreprocessor);
	}

	protected void pointerExitedTree() {
		// logger.info("Mouse exiting, removing event translator ");
		pointerOnTree = false;
		FlexoApplication.eventProcessor.setPreprocessor(null);
	}

	protected class JTreeMouseEventPreprocessor implements FlexoApplication.EventPreprocessor {
		@Override
		public void preprocessEvent(AWTEvent e) {
			if (e instanceof MouseEvent) {
				MouseEvent event = (MouseEvent) e;
				// logger.info("called preprocessEvent() "+event.getPoint()+" from "+event.getComponent());
				Point p = SwingUtilities.convertPoint(event.getComponent(), event.getX(), event.getY(), _table);
				// logger.info("Related to table, point values "+p);
				int currentRowBeginning = 0;
				int deltaY = 0;
				int currentRowHeight;
				for (int i = 0; i < getRowCount(); i++) {
					currentRowHeight = getRowHeight(i);
					if (p.y < currentRowBeginning + currentRowHeight) {
						p.y = p.y - deltaY;
						// logger.info("Related to table, point FINALLY set to "+p);
						event.translatePoint(0, -deltaY);
						// logger.info("finally preprocessEvent() "+event.getPoint());
						return;
					}
					deltaY = deltaY + currentRowHeight - getRowHeight();
					currentRowBeginning = currentRowBeginning + currentRowHeight;
				}
			}
		}

	}

	public void treeStructureChanged() {
		_elementsForRow.clear();
		/*_rowForObjects.clear();
		_heightForObjects.clear();*/
		_table.treeStructureChanged();
		/*logger.info("On redessine toute la JTreeTable");
		_table.revalidate();
		_table.repaint();*/
	}

	public void setRowHeightForObject(FlexoModelObject obj, int height) {
		if (obj != null) {
			_heightForObjects.put(obj, new Integer(height));
		}
	}

	public void setRowHeight(int row, int height) {
		setRowHeightForObject(getObjectAt(row), height);
	}

	public int getRowHeight(int row) {
		return getRowHeightForObject(getObjectAt(row));
	}

	public int getRowHeightForObject(FlexoModelObject object) {
		if (object == null) {
			return getRowHeight();
		}
		Integer returned = _heightForObjects.get(object);
		if (returned == null) {
			return getRowHeight();
		}
		return returned.intValue();
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, _table.getHeight());
	}

	@Override
	public TreePath getClosestPathForLocation(int x, int y) {
		// logger.info("called getClosestPathForLocation() "+x+","+y);
		int currentRowBeginning = 0;
		int deltaY = 0;
		int currentRowHeight;
		for (int i = 0; i < getRowCount(); i++) {
			currentRowHeight = getRowHeight(i);
			if (y < currentRowBeginning + currentRowHeight) {
				// logger.info("finally call getClosestPathForLocation() "+x+","+(y-deltaY));
				return super.getClosestPathForLocation(x, y - deltaY);
			}
			deltaY = deltaY + currentRowHeight - getRowHeight();
			currentRowBeginning = currentRowBeginning + currentRowHeight;
		}
		return super.getClosestPathForLocation(x, y);
	}

	@Override
	public Rectangle getPathBounds(TreePath path) {
		// logger.info("getPathBounds() called");
		Rectangle returned = super.getPathBounds(path);
		FlexoModelObject obj = ((BrowserElement) path.getLastPathComponent()).getObject();
		if (getRowHeightForObject(obj) != getRowHeight()) {
			returned.height = getRowHeightForObject(obj);
		}
		return returned;
	}

	@Override
	public void paint(Graphics g) {
		// logger.info("paint() in TreeTableCellRenderer for visible row !"+visibleRow);
		int translation = 0;
		for (int i = 0; i < visibleRow; i++) {
			int rowHeight = getRowHeight(i);
			translation = translation + rowHeight;
			// logger.info("Row height for row "+i+" values "+rowHeight);
		}
		// g.translate(0, -translation);
		g.translate(0, -visibleRow * getRowHeight());
		// logger.info("was: "+(-visibleRow*getRowHeight())+" is "+(-translation));
		super.paint(g);
		g.setColor(getComponentBackground(false, false, visibleRow, 0));
		g.fillRect(0, getRowHeight() + visibleRow * getRowHeight(), getWidth(), getRowHeight(visibleRow) - getRowHeight());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setComponentBackground(this, hasFocus, isSelected, row, column);

		visibleRow = row;
		return this;
	}

	protected void setComponentBackground(Component component, boolean hasFocus, boolean isSelected, int row, int column) {
		if ((hasFocus) && (isSelected)) {
			component.setForeground(ColorCst.SELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
		} else {
			component.setForeground(ColorCst.UNSELECTED_CELL_TABULAR_VIEW_FOREGROUND_COLOR);
		}
		component.setBackground(getComponentBackground(hasFocus, isSelected, row, column));
	}

	protected Color getComponentBackground(boolean hasFocus, boolean isSelected, int row, int column) {
		if (isSelected) {
			return ColorCst.SELECTED_LINES_TABULAR_VIEW_COLOR;
		} else {
			if (row % 2 == 0) {
				return ColorCst.ODD_LINES_TABULAR_VIEW_COLOR;
			} else {
				return ColorCst.NON_ODD_LINES_TABULAR_VIEW_COLOR;
			}
		}
	}

	protected BrowserElement getElementAt(int row) {
		return _elementsForRow.get(new Integer(row));
	}

	protected FlexoModelObject getObjectAt(int row) {
		if (getElementAt(row) != null) {
			return getElementAt(row).getObject();
		}
		return null;
	}

	/**
	 * Cell renderer for a browser view
	 * 
	 * @author sguerin
	 */
	public class JTreeCellRenderer extends BrowserViewCellRenderer {

		/*public int getHeight()
		{
		    return 50;
		}*/

		@Override
		public Component getTreeCellRendererComponent(JTree arg0, Object object, boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(arg0, object, selected, expanded, leaf, row, hasFocus);
			_row = row;
			if ((object instanceof BrowserElement) && (((BrowserElement) object).getObject() != null)) {
				Integer rowInt = new Integer(_row);
				_elementsForRow.put(rowInt, (BrowserElement) object);
				_rowForObjects.put(((BrowserElement) object).getObject(), rowInt);
			}
			setSize(getWidth(), 50);
			return this;
		}

		private int _row = 0;

		@Override
		public Color getBackgroundNonSelectionColor() {
			if (_row % 2 == 0) {
				return ColorCst.ODD_LINES_TABULAR_VIEW_COLOR;
			} else {
				return ColorCst.NON_ODD_LINES_TABULAR_VIEW_COLOR;
			}
		}

	}

}