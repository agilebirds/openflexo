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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBBrowserDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellEditor;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellRenderer;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;
import org.openflexo.fib.view.widget.browser.FIBBrowserWidgetFooter;
import org.openflexo.toolbox.ToolBox;

/**
 * Widget allowing to display a browser (a tree of various objects)
 * 
 * @author sguerin
 */
public class FIBBrowserWidget extends FIBWidgetView<FIBBrowser, JTree, Object> implements FIBSelectable, TreeSelectionListener {

	private static final Logger logger = Logger.getLogger(FIBBrowserWidget.class.getPackage().getName());

	// private static final int DEFAULT_WIDTH = 200;

	private JTree _tree;
	private final JPanel _dynamicComponent;
	private final FIBBrowser _fibBrowser;
	private FIBBrowserModel _browserModel;
	private FIBBrowserWidgetFooter _footer;
	// private ListSelectionModel _listSelectionModel;
	private JScrollPane scrollPane;
	private Object selectedObject;
	private final Vector<Object> selection;

	public FIBBrowserWidget(FIBBrowser fibBrowser, FIBController controller) {
		super(fibBrowser, controller);
		_fibBrowser = fibBrowser;
		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());
		selection = new Vector<Object>();
		_footer = new FIBBrowserWidgetFooter(this);
		buildBrowser();
	}

	@Override
	public synchronized void delete() {
		_footer.delete();
		super.delete();
	}

	public FIBBrowser getBrowser() {
		return _fibBrowser;
	}

	public FIBBrowserModel getBrowserModel() {
		if (_browserModel == null) {
			_browserModel = new FIBBrowserModel(_fibBrowser, this, getController());
		}
		return _browserModel;
	}

	public JTree getJTree() {
		return _tree;
	}

	public FIBBrowserWidgetFooter getFooter() {
		return _footer;
	}

	public Object getRootValue() {
		try {
			return getWidget().getRoot().getBindingValue(getBindingEvaluationContext());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	// private static final Vector EMPTY_VECTOR = new Vector();

	@Override
	public synchronized boolean updateWidgetFromModel() {
		// List valuesBeforeUpdating = getBrowserModel().getValues();
		Object wasSelected = getSelectedObject();

		// boolean debug = false;
		// if (getWidget().getName() != null && getWidget().getName().equals("PatternRoleTable")) debug=true;

		// if (debug) System.out.println("valuesBeforeUpdating: "+valuesBeforeUpdating);
		// if (debug) System.out.println("wasSelected: "+wasSelected);

		if (_tree.isEditing()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getComponent().getName() + " - Tree is currently editing");
			}
			_tree.getCellEditor().cancelCellEditing();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getComponent().getName() + " - Tree is NOT currently edited ");
			}
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getComponent().getName() + " updateWidgetFromModel() with " + getValue() + " dataObject=" + getDataObject());
		}

		boolean returned = getBrowserModel().updateRootObject(getRootValue());
		/*if (!getBrowser().getRootVisible() && ((BrowserCell) getBrowserModel().getRoot()).getChildCount() == 1) {
			// Only one cell and roots are hidden, expand this first cell
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (((BrowserCell) getBrowserModel().getRoot()).getChildCount() > 0) {
						getJTree().expandPath(
								new TreePath(new Object[] { (BrowserCell) getBrowserModel().getRoot(),
										((BrowserCell) getBrowserModel().getRoot()).getChildAt(0) }));
					}
				}
			});
		}*/
		// getBrowserModel().setModel(getDataObject());

		// We restore value if and only if we represent same browser
		/*if (getBrowserModel().getValues() == valuesBeforeUpdating && wasSelected != null) {
			setSelectedObject(wasSelected);
		}
		else {*/

		// logger.info("Bon, je remets a jour la selection du browser, value="+getComponent().getSelected().getBindingValue(getController())+" was: "+getSelectedObject());
		// System.out.println("getComponent().getSelected()="+getComponent().getSelected());
		// System.out.println("getComponent().getSelected().isValid()="+getComponent().getSelected().isValid());
		// System.out.println("value="+getComponent().getSelected().getBindingValue(getController()));

		try {
			if (getComponent().getSelected().isValid()
					&& getComponent().getSelected().getBindingValue(getBindingEvaluationContext()) != null) {
				Object newSelectedObject = getComponent().getSelected().getBindingValue(getBindingEvaluationContext());
				if (returned = notEquals(newSelectedObject, getSelectedObject())) {
					setSelectedObject(newSelectedObject);
				}
			}
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// }

		return returned;
	}

	public TreeSelectionModel getTreeSelectionModel() {
		return _tree.getSelectionModel();
	}

	public void setSelectedObject(Object object) {
		setSelectedObject(object, false);
	}

	public void setSelectedObject(Object object, boolean force) {
		// logger.info("Select " + object);
		if (getRootValue() == null) {
			return;
		}
		if (object == getSelectedObject() && !force) {
			logger.fine("Ignore set selected object");
			return;
		}
		// logger.info("---------------------> FIBBrowserWidget, setSelectedObject from "+getSelectedObject()+" to "+object);
		if (object != null) {
			Collection<BrowserCell> cells = getBrowserModel().getBrowserCell(object);
			// logger.info("Select " + cells);
			getTreeSelectionModel().clearSelection();
			if (cells != null) {
				TreePath scrollTo = null;
				for (BrowserCell cell : cells) {
					TreePath treePath = cell.getTreePath();
					if (scrollTo == null) {
						scrollTo = treePath;
					}
					getTreeSelectionModel().addSelectionPath(treePath);
				}
				if (scrollTo != null) {
					_tree.scrollPathToVisible(scrollTo);
				}
			}
		} else {
			clearSelection();
		}
	}

	public void clearSelection() {
		getTreeSelectionModel().clearSelection();
	}

	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	@Override
	public JPanel getJComponent() {
		return _dynamicComponent;
	}

	@Override
	public JTree getDynamicJComponent() {
		return _tree;
	}

	@Override
	public FIBBrowserDynamicModel createDynamicModel() {
		return new FIBBrowserDynamicModel(null);
	}

	@Override
	public FIBBrowserDynamicModel getDynamicModel() {
		return (FIBBrowserDynamicModel) super.getDynamicModel();
	}

	@Override
	public void updateLanguage() {
		super.updateLanguage();
		updateBrowser();
		System.out.println("Ne pas oublier de localiser les actions ici");
		/*for (FIBTableAction a : getWidget().getActions()) {
			if (getWidget().getLocalize()) getLocalized(a.getName());
		}*/
	}

	public void updateBrowser() {
		deleteBrowser();

		if (_browserModel != null) {
			_browserModel.delete();
			_browserModel = null;
		}

		buildBrowser();

		updateDataObject(getDataObject());
	}

	private void deleteBrowser() {
		if (_tree != null) {
			_tree.removeFocusListener(this);
		}
		for (MouseListener l : _tree.getMouseListeners()) {
			_tree.removeMouseListener(l);
		}
	}

	private void buildBrowser() {
		_tree = new JTree(getBrowserModel()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (ToolBox.isMacOSLaf()) {
					if (getSelectionRows() != null && getSelectionRows().length > 0) {
						for (int selected : getSelectionRows()) {
							Rectangle rowBounds = getRowBounds(selected);
							if (getVisibleRect().intersects(rowBounds)) {
								Object value = getPathForRow(selected).getLastPathComponent();
								Component treeCellRendererComponent = getCellRenderer().getTreeCellRendererComponent(this, value, true,
										isExpanded(selected), getModel().isLeaf(value), selected, getLeadSelectionRow() == selected);
								Color bgColor = treeCellRendererComponent.getBackground();
								if (treeCellRendererComponent instanceof DefaultTreeCellRenderer) {
									bgColor = ((DefaultTreeCellRenderer) treeCellRendererComponent).getBackgroundSelectionColor();
								}
								if (bgColor != null) {
									g.setColor(bgColor);
									g.fillRect(0, rowBounds.y, getWidth(), rowBounds.height);
									Graphics g2 = g.create(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);
									treeCellRendererComponent.setBounds(rowBounds);
									treeCellRendererComponent.paint(g2);
									g2.dispose();
								}
							}
						}
					}
				}
			}
		};
		if (ToolBox.isMacOS()) {
			_tree.setSelectionModel(new DefaultTreeSelectionModel() {
				@Override
				public int[] getSelectionRows() {
					int[] selectionRows = super.getSelectionRows();
					// MacOS X does not support that we return null here during DnD operations.
					if (selectionRows == null) {
						return new int[0];
					}
					return selectionRows;
				}
			});
		}
		_tree.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_tree.addFocusListener(this);
		_tree.setEditable(true);
		_tree.setScrollsOnExpand(true);
		FIBBrowserCellRenderer renderer = new FIBBrowserCellRenderer(this);
		_tree.setCellRenderer(renderer);
		_tree.setCellEditor(new FIBBrowserCellEditor(_tree, renderer));
		ToolTipManager.sharedInstance().registerComponent(_tree);
		_tree.setAutoscrolls(true);

		/** Beginning of model dependent settings */
		_tree.setRootVisible(getBrowser().getRootVisible());
		_tree.setShowsRootHandles(getBrowser().getShowRootsHandle());

		// If a double-click action is set, desactivate tree expanding/collabsing with double-click
		if (getBrowser().getDoubleClickAction().isSet()) {
			_tree.setToggleClickCount(-1);
		}

		if (_fibBrowser.getRowHeight() != null) {
			_tree.setRowHeight(_fibBrowser.getRowHeight());
		} else {
			_tree.setRowHeight(0);
		}
		if (_fibBrowser.getVisibleRowCount() != null) {
			_tree.setVisibleRowCount(_fibBrowser.getVisibleRowCount());
		}

		getTreeSelectionModel().setSelectionMode(getBrowser().getSelectionMode().getMode());
		getTreeSelectionModel().addTreeSelectionListener(this);

		scrollPane = new JScrollPane(_tree);

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (_fibBrowser.getShowFooter()) {
			_dynamicComponent.add(getFooter(), BorderLayout.SOUTH);
		}
		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();
		getBrowserModel().addTreeModelListener(new TreeModelListener() {

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				ensureRootExpanded();
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {

			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				ensureRootExpanded();
			}

			private void ensureRootExpanded() {
				if (!getBrowser().getRootVisible() && (BrowserCell) getBrowserModel().getRoot() != null
						&& ((BrowserCell) getBrowserModel().getRoot()).getChildCount() == 1) {
					// Only one cell and roots are hidden, expand this first cell
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							getJTree().expandPath(
									new TreePath(new Object[] { (BrowserCell) getBrowserModel().getRoot(),
											((BrowserCell) getBrowserModel().getRoot()).getChildAt(0) }));
						}
					});
				}
			}

			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController() != null && getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o) {
		return _browserModel.containsObject(o);
	}

	@Override
	public void objectAddedToSelection(Object o) {
		addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o) {
		removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted() {
		resetSelectionNoNotification();
	}

	@Override
	public void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		super.updateDataObject(dataObject);
		getBrowserModel().fireTreeRestructured();
	}

	@Override
	public Object getSelectedObject() {
		return selectedObject;
	}

	@Override
	public Vector<Object> getSelection() {
		return selection;
	}

	private boolean ignoreNotifications = false;

	public synchronized void addToSelectionNoNotification(Object o) {
		ignoreNotifications = true;
		addToSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void removeFromSelectionNoNotification(Object o) {
		ignoreNotifications = true;
		removeFromSelection(o);
		ignoreNotifications = false;
	}

	public synchronized void resetSelectionNoNotification() {
		ignoreNotifications = true;
		resetSelection();
		ignoreNotifications = false;
	}

	@Override
	public void addToSelection(Object o) {
		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTreeSelectionModel().addSelectionPath(path);
			getJTree().scrollPathToVisible(path);
		}
	}

	@Override
	public void removeFromSelection(Object o) {
		selection.remove(o);
		for (TreePath path : getBrowserModel().getPaths(o)) {
			getTreeSelectionModel().removeSelectionPath(path);
		}
	}

	@Override
	public void resetSelection() {
		selection.clear();
		getTreeSelectionModel().clearSelection();
	}

	@Override
	public synchronized void valueChanged(TreeSelectionEvent e) {
		Vector<Object> oldSelection = new Vector<Object>();
		oldSelection.addAll(selection);
		// System.out.println("Selection: " + e);

		/*System.out.println("Paths=" + e.getPaths());
		for (TreePath tp : e.getPaths()) {
			System.out.println("> " + tp.getLastPathComponent() + " added=" + e.isAddedPath(tp));
		}
		System.out.println("New LEAD="
				+ (e.getNewLeadSelectionPath() != null ? e.getNewLeadSelectionPath().getLastPathComponent() : "null"));
		System.out.println("Old LEAD="
				+ (e.getOldLeadSelectionPath() != null ? e.getOldLeadSelectionPath().getLastPathComponent() : "null")); */

		if (e.getNewLeadSelectionPath() == null || (BrowserCell) e.getNewLeadSelectionPath().getLastPathComponent() == null) {
			selectedObject = null;
		} else {
			selectedObject = ((BrowserCell) e.getNewLeadSelectionPath().getLastPathComponent()).getRepresentedObject();
		}
		for (TreePath tp : e.getPaths()) {
			Object obj = ((BrowserCell) tp.getLastPathComponent()).getRepresentedObject();
			if (obj != null
					&& (getBrowser().getIteratorClass() == null || getBrowser().getIteratorClass().isAssignableFrom(obj.getClass()))) {
				if (e.isAddedPath(tp)) {
					if (!selection.contains(obj)) {
						selection.add(obj);
					}
				} else {
					selection.remove(obj);
				}
			}
		}

		// logger.info("BrowserModel, selected object is now "+selectedObject);

		if (selectedObject == null) {
			getDynamicModel().selected = null;
		} else if (getBrowser().getIteratorClass() == null || getBrowser().getIteratorClass().isAssignableFrom(selectedObject.getClass())) {
			getDynamicModel().selected = selectedObject;
		} else {
			// If selected element is not of expected class, set selected to be null
			// (we want to be sure that selected is an instance of IteratorClass)
			getDynamicModel().selected = null;
		}
		getDynamicModel().selection = selection;

		/*System.out.println("selectedObject=" + selectedObject);
		System.out.println("getComponent().getSelected()=" + getComponent().getSelected() + " of "
				+ getComponent().getSelected().getClass());
		System.out.println("getComponent().getSelected().isValid()=" + getComponent().getSelected().isValid());*/

		if (getComponent().getSelected().isValid()) {
			logger.fine("Sets SELECTED binding with " + getDynamicModel().selected);
			try {
				getComponent().getSelected().setBindingValue(getDynamicModel().selected, getBindingEvaluationContext());
			} catch (TypeMismatchException e1) {
				e1.printStackTrace();
			} catch (NullReferenceException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NotSettableContextException e1) {
				e1.printStackTrace();
			}
		}

		notifyDynamicModelChanged();

		updateFont();

		if (!ignoreNotifications) {
			getController().updateSelection(this, oldSelection, selection);
		}

		_footer.setFocusedObject(selectedObject);

	}
}
