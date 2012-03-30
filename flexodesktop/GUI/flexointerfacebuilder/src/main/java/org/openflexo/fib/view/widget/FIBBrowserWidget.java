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
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.fib.controller.FIBBrowserDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBSelectable;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellEditor;
import org.openflexo.fib.view.widget.browser.FIBBrowserCellRenderer;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;

/**
 * Widget allowing to display a browser (a tree of various objects)
 * 
 * @author sguerin
 */
public class FIBBrowserWidget extends FIBWidgetView<FIBBrowser, JTree, Object> implements FIBSelectable, TreeModelListener {

	private static final Logger logger = Logger.getLogger(FIBBrowserWidget.class.getPackage().getName());

	private static final int DEFAULT_WIDTH = 200;

	private JTree _tree;
	private final JPanel _dynamicComponent;
	private final FIBBrowser _fibBrowser;
	private FIBBrowserModel _browserModel;
	// private ListSelectionModel _listSelectionModel;
	private JScrollPane scrollPane;

	public FIBBrowserWidget(FIBBrowser fibBrowser, FIBController controller) {
		super(fibBrowser, controller);
		_fibBrowser = fibBrowser;

		_dynamicComponent = new JPanel();
		_dynamicComponent.setOpaque(false);
		_dynamicComponent.setLayout(new BorderLayout());

		buildBrowser();
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

	public void setVisibleRowCount(int rows) {
		int height = _fibBrowser.getRowHeight() * _fibBrowser.getVisibleRowCount();
		int width = DEFAULT_WIDTH;
		_dynamicComponent.setMinimumSize(new Dimension(width, height));
		_dynamicComponent.setPreferredSize(new Dimension(width, height));
	}

	public JTree getJTree() {
		return _tree;
	}

	public Object getRootValue() {
		return getWidget().getRoot().getBindingValue(getController());
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

		if (getComponent().getSelected().isValid() && getComponent().getSelected().getBindingValue(getController()) != null) {
			Object newSelectedObject = getComponent().getSelected().getBindingValue(getController());
			if (returned = notEquals(newSelectedObject, getSelectedObject())) {
				setSelectedObject(newSelectedObject);
			}
		}

		// }

		return returned;
	}

	public TreeSelectionModel getTreeSelectionModel() {
		return _tree.getSelectionModel();
	}

	@Override
	public Object getSelectedObject() {
		return _browserModel.getSelectedObject();
	}

	@Override
	public Vector<Object> getSelection() {
		return _browserModel.getSelection();
	}

	public void setSelectedObject(Object object) {
		if (getRootValue() == null) {
			return;
		}
		if (object == getSelectedObject()) {
			logger.fine("Ignore set selected object");
			return;
		}
		// logger.info("---------------------> FIBBrowserWidget, setSelectedObject from "+getSelectedObject()+" to "+object);
		if (object != null) {
			Collection<BrowserCell> cells = getBrowserModel().getBrowserCell(object);
			// logger.info("Select "+cell);
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
	public List<AbstractBinding> getDependencyBindings() {
		List<AbstractBinding> returned = super.getDependencyBindings();
		appendToDependingObjects(getWidget().getSelected(), returned);
		appendToDependingObjects(getWidget().getRoot(), returned);
		return returned;
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
			_browserModel.removeTreeModelListener(this);
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
		if (getTreeSelectionModel() != null) {
			getTreeSelectionModel().removeTreeSelectionListener(getBrowserModel());
		}
		for (MouseListener l : _tree.getMouseListeners()) {
			_tree.removeMouseListener(l);
		}
	}

	private void buildBrowser() {
		getBrowserModel().addTreeModelListener(this);

		_tree = new JTree(getBrowserModel());
		_tree.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		_tree.addFocusListener(this);
		FIBBrowserCellRenderer renderer = new FIBBrowserCellRenderer(this);
		_tree.setCellRenderer(renderer);
		_tree.setCellEditor(new FIBBrowserCellEditor(_tree, renderer));

		_tree.setEditable(true);
		_tree.setScrollsOnExpand(true);
		// _tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
		_tree.setRootVisible(getBrowser().getRootVisible());
		_tree.setShowsRootHandles(getBrowser().getShowRootsHandle());
		_tree.setAutoscrolls(true);
		ToolTipManager.sharedInstance().registerComponent(_tree);

		if (_fibBrowser.getRowHeight() > 0) {
			_tree.setRowHeight(_fibBrowser.getRowHeight());
		}

		_tree.getSelectionModel().addTreeSelectionListener(getBrowserModel());
		_tree.getSelectionModel().setSelectionMode(getBrowser().getSelectionMode().getMode());

		// _listSelectionModel = _table.getSelectionModel();
		// _listSelectionModel.addListSelectionListener(this);

		scrollPane = new JScrollPane(_tree);

		/*_tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				getController().fireMouseClicked(getDynamicModel(),e.getClickCount());
			}
		});*/

		_dynamicComponent.removeAll();
		_dynamicComponent.add(scrollPane, BorderLayout.CENTER);

		if (_fibBrowser.getShowFooter()) {
			_dynamicComponent.add(getBrowserModel().getFooter(), BorderLayout.SOUTH);
		}

		setVisibleRowCount(_fibBrowser.getVisibleRowCount());
		_dynamicComponent.revalidate();
		_dynamicComponent.repaint();
	}

	@Override
	public boolean synchronizedWithSelection() {
		return getWidget().getBoundToSelectionManager();
	}

	public boolean isLastFocusedSelectable() {
		return getController().getLastFocusedSelectable() == this;
	}

	@Override
	public boolean mayRepresent(Object o) {
		return _browserModel.containsObject(o);
	}

	@Override
	public void objectAddedToSelection(Object o) {
		getBrowserModel().addToSelectionNoNotification(o);
	}

	@Override
	public void objectRemovedFromSelection(Object o) {
		getBrowserModel().removeFromSelectionNoNotification(o);
	}

	@Override
	public void selectionResetted() {
		getBrowserModel().resetSelectionNoNotification();
	}

	@Override
	public void addToSelection(Object o) {
		getBrowserModel().addToSelection(o);
	}

	@Override
	public void removeFromSelection(Object o) {
		getBrowserModel().removeFromSelection(o);
	}

	@Override
	public void resetSelection() {
		getBrowserModel().resetSelection();
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		// logger.fine("treeNodesChanged "+e);
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		// logger.fine("treeNodesInserted "+e);
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// logger.fine("treeNodesRemoved "+e);
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// logger.fine("treeStructureChanged "+e);
	}

	@Override
	public void updateDataObject(Object aDataObject) {
		super.updateDataObject(aDataObject);
		getBrowserModel().fireTreeRestructured();
	}

}
