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
package org.openflexo.components.browser.view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.openflexo.GeneralPreferences;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser.DisableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.EnableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.ExpansionNotificationEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectAddedToSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectRemovedFromSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.SelectionClearedEvent;
import org.openflexo.components.browser.ProjectBrowserListener;
import org.openflexo.components.browser.dnd.TreeDragSource;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.ContextualMenuManager;
import org.openflexo.selection.DefaultContextualMenuManager;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.utils.FlexoAutoScroll;
import org.openflexo.view.controller.FlexoController;

/**
 * Abstract view related to a ProjectBrowser
 * 
 * @author sguerin
 */
public abstract class BrowserView extends JPanel implements FlexoActionSource, ProjectBrowserListener, TreeSelectionListener,
		TreeExpansionListener {
	static final Logger logger = Logger.getLogger(BrowserView.class.getPackage().getName());

	protected static final Point noOffset = new Point(0, 0);

	protected abstract class ViewModeButton extends JButton implements MouseListener, ActionListener {
		protected ViewModeButton(ImageIcon icon, String unlocalizedDescription) {
			super(icon);
			setToolTipText(FlexoLocalization.localizedForKey(unlocalizedDescription));
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			addMouseListener(this);
			addActionListener(this);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setBorder(BorderFactory.createEtchedBorder());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setFilters();
			getBrowser().update();
		}

		public abstract void setFilters();
	}

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	protected ProjectBrowser _browser;

	// protected JPanel filterView = null;

	protected FlexoJTree treeView;

	protected JScrollPane treeScrollPane;

	protected BrowserFooter controlPanel;

	protected TreeDragSource ds;

	protected TreeDropTarget dt;

	// private JPanel noSelectionPanel;

	private DefaultContextualMenuManager defaultContextualMenuManager;

	protected static BufferedImage capturedDraggedNodeImage;

	private SelectionPolicy _selectionPolicy;

	private final FlexoController controller;

	public static enum SelectionPolicy {
		/**
		 * This policy indicates that selections performed by this browser are repercuted to current selection. An object addition or
		 * removing are notified to Selection Manager which handle it. If Selection Manager contains an object that this browser cannot
		 * represent, this object is still maintained by Selection Manager With this policy, a selection could be represented by many
		 * objects in many browsers
		 */
		ParticipateToSelection,
		/**
		 * This policy indicates that selection maintained by Selection Manager is performed only by this browser. Any selection
		 * modification performed in this browser are repercuted into Selection Manager which takes as selection all but only all objects
		 * selected in this browser.
		 */
		ForceSelection
	}

	private JPanel northPanel;

	public BrowserView(ProjectBrowser browser, FlexoController controller) {
		this(browser, controller, SelectionPolicy.ParticipateToSelection);
	}

	public BrowserView(ProjectBrowser browser, FlexoController controller, SelectionPolicy selectionPolicy) {
		super();
		_browser = browser;
		this.controller = controller;
		_selectionPolicy = selectionPolicy;
		_browser.setLeadingView(this);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		add(northPanel, BorderLayout.NORTH);

		treeView = createTreeView(browser);
		treeView.setExpandsSelectedPaths(true);
		ds = new TreeDragSource(treeView, DnDConstants.ACTION_COPY_OR_MOVE);
		dt = createTreeDropTarget(treeView, _browser);
		BrowserViewCellRenderer renderer = new BrowserViewCellRenderer();
		treeView.setCellRenderer(renderer);
		treeView.setCellEditor(new BrowserViewCellEditor(treeView, renderer));
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TreePath selPath = treeView.getPathForLocation(e.getX(), e.getY());
				if (treeView.getRowForPath(selPath) != -1) {
					if (e.getClickCount() == 1) {
						processSingleClick(selPath, e);
					} else if (e.getClickCount() == 2) {
						if (e.isAltDown()) {
							treeDoubleClickWithAltDown(getSelectedObject());
						} else {
							treeDoubleClick(getSelectedObject());
						}
					}
				}
			}
		};
		treeView.addMouseListener(ml);

		treeView.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (getContextualMenuManager() != null) {
					getContextualMenuManager().processMousePressed(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				/*
				 * TreePath selPath = treeView.getPathForLocation(e.getX(), e.getY()); if (selPath != null && selPath.getLastPathComponent()
				 * instanceof BrowserElement) if (_browser.getSelectionManager() != null) {
				 * _browser.deleteBrowserListener(BrowserView.this); if (_browser.getSelectionManager().selectionContains(((BrowserElement)
				 * selPath.getLastPathComponent()).getObject())) { if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) ==
				 * InputEvent.SHIFT_DOWN_MASK) { _browser.getSelectionManager().removeFromSelected( ((BrowserElement)
				 * selPath.getLastPathComponent()).getObject()); } } else { if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) ==
				 * InputEvent.SHIFT_DOWN_MASK) { _browser.getSelectionManager().addToSelected(((BrowserElement)
				 * selPath.getLastPathComponent()).getObject()); } else { _browser.getSelectionManager().resetSelection();
				 * _browser.getSelectionManager().addToSelected(((BrowserElement) selPath.getLastPathComponent()).getObject()); } }
				 * _browser.addBrowserListener(BrowserView.this); }
				 */
				if (getContextualMenuManager() != null) {
					getContextualMenuManager().processMouseReleased(e);
				}
			}
		});
		treeView.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (getContextualMenuManager() != null) {
					getContextualMenuManager().processMouseMoved(e);
				}
			}
		});

		treeView.addTreeSelectionListener(this);
		treeView.addTreeExpansionListener(this);
		treeView.setEditable(true);
		treeView.setScrollsOnExpand(true);
		treeView.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
		treeScrollPane = new JScrollPane(treeView);
		treeScrollPane.setAutoscrolls(true);
		treeScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		treeScrollPane.getVerticalScrollBar().setBlockIncrement(50);
		controlPanel = new BrowserFooter(this);

		add(treeScrollPane, BorderLayout.CENTER);
		if (_browser.handlesControlPanel()) {
			add(controlPanel, BorderLayout.SOUTH);
		}
		browser.addBrowserListener(this);
		validate();
	}

	protected void addHeaderComponent(Component component) {
		northPanel.add(component);
	}

	public FlexoController getController() {
		return controller;
	}

	protected ContextualMenuManager getContextualMenuManager() {
		if (!_browser.handlesControlPanel()) {
			return null;
		}
		if (_browser.getSelectionManager() != null) {
			return _browser.getSelectionManager().getContextualMenuManager();
		}
		if (defaultContextualMenuManager == null) {
			defaultContextualMenuManager = new DefaultContextualMenuManager(getController());
		}
		return defaultContextualMenuManager;
	}

	public SelectionPolicy getSelectionPolicy() {
		return _selectionPolicy;
	}

	/**
	 * @param browser
	 */
	protected FlexoJTree createTreeView(ProjectBrowser browser) {
		return new FlexoJTree(browser);
	}

	protected TreeDropTarget createTreeDropTarget(FlexoJTree treeView, ProjectBrowser browser) {
		return new TreeDropTarget(treeView, browser);
	}

	public ProjectBrowser getBrowser() {
		return _browser;
	}

	public BrowserElement getSelectedElement() {
		return (BrowserElement) treeView.getLastSelectedPathComponent();
	}

	public FlexoObject getSelectedObject() {
		if (getSelectedElement() != null) {
			return getSelectedElement().getObject();
		}
		return null;
	}

	private Vector<BrowserElement> selectedElements = new Vector<BrowserElement>();

	private Vector<FlexoObject> selectedObjects = new Vector<FlexoObject>();

	private boolean selectedElementsNeedRecomputing = true;

	private void rebuildSelectedElementsAndObjects() {
		selectedElements.clear();
		selectedObjects.clear();
		TreePath[] selectionPaths = treeView.getSelectionPaths();
		if (selectionPaths != null) {
			for (int i = 0; i < selectionPaths.length; i++) {
				Object obj = selectionPaths[i].getLastPathComponent();
				if (obj instanceof BrowserElement) {
					selectedElements.add((BrowserElement) obj);
					selectedObjects.add(((BrowserElement) obj).getObject());
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Recomputing selected elements with " + selectedElements.size() + " elements");
		}
		selectedElementsNeedRecomputing = false;
	}

	/**
	 * Return current selection
	 * 
	 * @return a Vector of BrowserElement
	 */
	public Vector<BrowserElement> getSelectedElements() {
		if (selectedElementsNeedRecomputing) {
			rebuildSelectedElementsAndObjects();
		}
		return selectedElements;
	}

	/**
	 * Return current selection
	 * 
	 * @return a Vector of BrowserElement
	 */
	public Vector<FlexoObject> getSelectedObjects() {
		if (selectedElementsNeedRecomputing) {
			rebuildSelectedElementsAndObjects();
		}
		return selectedObjects;
	}

	/**
	 * Build and return a vector of all expanded elements
	 * 
	 * @return a Vector of BrowserElement
	 */
	public Vector<BrowserElement> getExpandedElements() {
		Vector<BrowserElement> expandedElements = new Vector<BrowserElement>();
		if (treeView != null) {
			TreePath rootTreePath = treeView.getPathForRow(0);
			Enumeration<TreePath> selectionPaths = treeView.getExpandedDescendants(rootTreePath);
			if (selectionPaths != null) {
				while (selectionPaths.hasMoreElements()) {
					TreePath next = selectionPaths.nextElement();
					// logger.info("Expanded "+next);
					// logger.info("Expanded object
					// "+next.getLastPathComponent()+next.getLastPathComponent().getClass().getName());
					expandedElements.add((BrowserElement) next.getLastPathComponent());
				}
			}
		}
		return expandedElements;
	}

	public void processSingleClick(TreePath path, MouseEvent e) {
		if (getSelectedElement() == null) {
			return;
		}
		if (e.isAltDown()) {
			treeSingleClickWithAltDown(getSelectedObject());
		} else {
			treeSingleClick(getSelectedObject());
		}
	}

	public abstract void treeSingleClick(FlexoObject object);

	public abstract void treeDoubleClick(FlexoObject object);

	public void treeSingleClickWithAltDown(FlexoObject object) {
		treeSingleClick(object);
	}

	public void treeDoubleClickWithAltDown(FlexoObject object) {
		treeDoubleClick(object);
	}

	@Override
	public void optionalFilterAdded(ProjectBrowser.OptionalFilterAddedEvent event) {
		if (_browser.handlesControlPanel()) {
			controlPanel.handleOptionalFilterAdded();
		}
	}

	private boolean superviseExpansion = false;

	private Vector<BrowserElement> expansionSupervisedElements;

	@Override
	public void objectAddedToSelection(ObjectAddedToSelectionEvent event) {
		TreePath[] paths = _browser.treePathForObject(event.getAddedObject());
		if (paths == null) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("BrowserView.objectAddedToSelection() " + event.getAddedObject());
		}
		superviseExpansion = true;
		expansionSupervisedElements = new Vector<BrowserElement>();
		for (int i = 0; i < paths.length; i++) {
			BrowserElement element = (BrowserElement) paths[i].getLastPathComponent();
			expansionSupervisedElements.add(element);
		}
		treeView.removeTreeSelectionListener(this);
		treeView.addSelectionPaths(paths);
		treeView.addTreeSelectionListener(this);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Added " + event.getAddedObject());
		}
		superviseExpansion = false;
		selectedElementsNeedRecomputing = true;
		if (_browser.handlesControlPanel()) {
			controlPanel.handleSelectionChanged();
		}
	}

	@Override
	public void objectRemovedFromSelection(ObjectRemovedFromSelectionEvent event) {
		if (event.getRemovedObject() != null) {
			TreePath[] paths = _browser.treePathForObject(event.getRemovedObject());
			if (paths == null) {
				return;
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("BrowserView.objectRemovedFromSelection() " + event.getRemovedObject());
			}
			treeView.removeTreeSelectionListener(this);
			treeView.removeSelectionPaths(paths);
			treeView.addTreeSelectionListener(this);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Removed " + event.getRemovedObject());
			}
			selectedElementsNeedRecomputing = true;
			if (_browser.handlesControlPanel()) {
				controlPanel.handleSelectionChanged();
			}
		}
	}

	@Override
	public void selectionCleared(SelectionClearedEvent event) {
		if (_browser.handlesControlPanel()) {
			controlPanel.handleSelectionCleared();
		}
		treeView.removeTreeSelectionListener(this);
		treeView.clearSelection();
		treeView.addTreeSelectionListener(this);
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		/*
		 * if (getSelectionPolicy() == SelectionPolicy.ForceSelection) { System.out.println("value changed with "+e); if
		 * (_browser.getSelectionManager() != null) { selectedElementsNeedRecomputing = true; _browser.deleteBrowserListener(this);
		 * Vector<FlexoObject> objectsToSelect = getSelectedObjects(); _browser.getSelectionManager().resetSelection(); for
		 * (FlexoObject o : objectsToSelect) { System.out.println("Selected: "+o); _browser.getSelectionManager().addToSelected(o); }
		 * _browser.addBrowserListener(this); return; } }
		 */
		// Code initial suit:

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("valueChanged() " + e);
		}
		selectedElementsNeedRecomputing = true;
		if (_browser.getSelectionManager() != null) {
			_browser.deleteBrowserListener(this);
			if (getSelectionPolicy() == SelectionPolicy.ParticipateToSelection) {
				TreePath[] selectionChanges = e.getPaths();
				for (int i = 0; i < selectionChanges.length; i++) {
					BrowserElement element = (BrowserElement) selectionChanges[i].getLastPathComponent();
					if (e.isAddedPath(selectionChanges[i])) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("valueChanged() for ADDITION " + element.getSelectableObject());
						}
						_browser.getSelectionManager().addToSelected(element.getSelectableObject());
						// _browser.addToSelected(element.getSelectableObject());
					} else {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("valueChanged() for REMOVING " + element.getSelectableObject());
						}
						_browser.getSelectionManager().removeFromSelected(element.getSelectableObject());
						// _browser.removeFromSelected(element.getSelectableObject());
					}
				}
			} else if (getSelectionPolicy() == SelectionPolicy.ForceSelection) {
				Vector<FlexoObject> objectsToSelect = getSelectedObjects();
				_browser.getSelectionManager().resetSelection();
				for (FlexoObject o : objectsToSelect) {
					_browser.getSelectionManager().addToSelected(o);
				}
			}
			_browser.addBrowserListener(this);
			_browser.getSelectionManager().updateSelectionForMaster(_browser);
			if (logger.isLoggable(Level.FINE)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(_browser.getSelectionManager().toString());
				}
			}
			if (_browser.getSelectionManager().getContextualMenuManager() != null) {
				if (_browser.getSelectionManager().getContextualMenuManager().isPopupMenuDisplayed()) {
					_browser.getSelectionManager().getContextualMenuManager().hidePopupMenu();
				}
			}
		}
		if (_browser.handlesControlPanel()) {
			controlPanel.handleSelectionChanged();
		}
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		BrowserElement element = (BrowserElement) event.getPath().getLastPathComponent();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Tree may expand for " + element);
		}

		// Lets look at the expansion supervising
		if (_browser.isExpansionSynchronizedElement(element) && element.isSynchronizeExpansionEnabled()) {
			// At this level, element is considered as expansion supervised
			boolean doExpand = true;
			ExpansionSynchronizedElement elementToExpand = (ExpansionSynchronizedElement) element;
			if (superviseExpansion) {
				// BUT.... here, the flag superviseExpansion indicates that this tree expansion
				// has its origin in the fact that one or more objects were selected.
				// In this case, this is not sure that the expansion whould be synchronized
				// We have to look that at least one of the selected element requires expansion

				// GPO: If we come here, it means that we have been notified by the selection manager and we want to know if the expansion
				// caused by
				// the new selection (selected objects in JTree are automatically shown (thus all its parents are automatically expanded))
				// must be
				// forwarded to the model which may cause other expansion synchronized views to expand (typically, if you select a
				// pre-condition which is located under its attachedNode in the JTree, you don't want the tree to expand, and even less a
				// potential petri graph to be displayed in the view. The same goes for messages, portmaps, portmapregisteries, etc...

				doExpand = false;
				for (Enumeration<BrowserElement> e = expansionSupervisedElements.elements(); e.hasMoreElements();) {
					BrowserElement next = e.nextElement();
					if (elementToExpand.requiresExpansionFor(next)) {
						// Selecting next requires expansion for elementToExpand
						doExpand = true;
					}
				}
			} else {
				// If we come here, the tree expansion event was originated by the user pressing the + in the JTree
				// Therefore, if we are on a node that
				if (_browser.getSelectionManager() != null) {
					for (FlexoObject o : _browser.getSelectionManager().getSelection()) {
						if (element.contains(o)) {
							// browser
							// view with the selection given by the selection manager
							_browser.updateSelection();
						}
					}
				}
			}
			if (doExpand) {
				// Finally i decide to expand
				elementToExpand.expand();
			} else {
				// Keeps the model and view synched (therefore, I revert the expand)
				treeView.removeTreeExpansionListener(this);
				treeView.collapsePath(event.getPath());
				treeView.addTreeExpansionListener(this);
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
	public void treeCollapsed(TreeExpansionEvent event) {
		BrowserElement element = (BrowserElement) event.getPath().getLastPathComponent();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Tree collabsed for " + element);
		}
		if (_browser.isExpansionSynchronizedElement(element)) {
			((ExpansionSynchronizedElement) element).collapse();
		}
	}

	@Override
	public void notifyExpansions(ExpansionNotificationEvent event) {
		_browser.deleteBrowserListener(this);
		treeView.removeTreeExpansionListener(this);
		for (TreePath path : event.pathsToExpand()) {
			if (treeView.isCollapsed(path)) {
				treeView.expandPath(path);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Expand " + path);
				}
			}
		}
		for (TreePath path : event.pathsToCollapse()) {
			if (treeView.isExpanded(path)) {
				treeView.collapsePath(path);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Collapse " + path);
				}
			}
		}
		treeView.addTreeExpansionListener(this);
		_browser.addBrowserListener(this);
	}

	@Override
	public void enableExpandingSynchronization(EnableExpandingSynchronizationEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("enableExpandingSynchronization()");
		}
		treeView.addTreeExpansionListener(this);
	}

	@Override
	public void disableExpandingSynchronization(DisableExpandingSynchronizationEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("disableExpandingSynchronization()");
		}
		treeView.removeTreeExpansionListener(this);
	}

	public class FlexoJTree extends JTree implements Autoscroll {
		private int margin = 20;

		/**
		 * @param browser
		 */
		public FlexoJTree(ProjectBrowser browser) {
			super(browser);
			setRootVisible(browser.showRootNode());
			setShowsRootHandles(browser.isRootCollapsable());
			setAutoscrolls(true);
			ToolTipManager.sharedInstance().registerComponent(this);
			setRowHeight(_browser.getRowHeight());
		}

		@Override
		public void paint(Graphics g) {
			_browser.setHoldStructure();
			try {
				super.paint(g);
			} finally {
				_browser.resetHoldStructure();
			}
		}

		/**
		 * Overrides addSelectionPaths
		 * 
		 * @see javax.swing.JTree#addSelectionPaths(javax.swing.tree.TreePath[])
		 */
		@Override
		public void addSelectionPaths(TreePath[] paths) {
			super.addSelectionPaths(paths);
			if (GeneralPreferences.getSynchronizedBrowser() && paths != null && paths.length > 0) {
				TreePath treePath = paths[0];
				while (treePath != null) {
					if (!treeView.isExpanded(treePath.getParentPath())) {
						treePath = treePath.getParentPath();
					} else {
						break;
					}
				}
				if (treePath != null) {
					scrollPathToVisible(treePath);
				}
			}
		}

		@Override
		public void addTreeSelectionListener(TreeSelectionListener tsl) {
			/**
			 * GPO: The code hereunder is here to prevent a same tree selection listener from being added twice to the same view making all
			 * further add/remove (treeSelectionListener()) call completely useless. This was called
			 * "Bug critique difficilement reproductible" reported by FVA.
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
			 * GPO: The code hereunder is here to track when we are not really removing <code>BrowserView.this</code> from the tree
			 * selection listeners because another action has already performed this. This trace is very interesting to find which event is
			 * starting the other one. You should see next a log "Adding twice the same tsl: " following this log. This was called
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

		@Override
		public void autoscroll(Point p) {
			FlexoAutoScroll.autoscroll(this, p, margin);
		}

		@Override
		public Insets getAutoscrollInsets() {
			return FlexoAutoScroll.getAutoscrollInsets(this, margin);
		}

		/**
		 * Overrides updateUI
		 * 
		 * @see javax.swing.JTree#updateUI()
		 */
		@Override
		public void updateUI() {
			if (getCellRenderer() != null && getCellRenderer() instanceof JComponent) {
				((JComponent) getCellRenderer()).updateUI();
			}
			super.updateUI();
		}

		public void handleAutoExpand(TreePath path) {
			if (path != null && !isExpanded(path) && !path.equals(expandedPath)) {
				startExpandCountDownForNode(path);
			}
		}

		protected Thread expandCountDown = null;

		TreePath expandedPath;

		/**
		 * @param path
		 */
		public void startExpandCountDownForNode(final TreePath path) {
			stopExpandCountDown();
			expandedPath = path;
			expandCountDown = new Thread(new Runnable() {
				/**
				 * Overrides run
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						expandCountDown = null;
						return;
					}
					expandPath(path);
					expandCountDown = null;
					expandedPath = null;
				}
			});
			expandCountDown.start();
		}

		public void stopExpandCountDown() {
			if (expandCountDown == null) {
				return;
			}
			try {
				expandCountDown.interrupt();
				expandCountDown = null;
				expandedPath = null;
			} catch (NullPointerException e) {
				// Just in case
			}
		}

		private Rectangle rect2D = new Rectangle();

		private Point offset = noOffset;

		public void captureDraggedNode(TreePath path, Point offset) {
			this.offset = offset;
			Rectangle pathBounds = getPathBounds(path); // getpathbounds of selectionpath
			JComponent lbl = (JComponent) getCellRenderer().getTreeCellRendererComponent(this, path.getLastPathComponent(), false,
					isExpanded(path), getModel().isLeaf(path.getLastPathComponent()), 0, false);// returning the label
			lbl.setBounds(pathBounds);// setting bounds to lbl
			capturedDraggedNodeImage = new BufferedImage(lbl.getWidth(), lbl.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);// buffered
																																			// image
																																			// reference
																																			// passing
																																			// the
																																			// label's
																																			// ht
																																			// and
																																			// width
			Graphics2D graphics = capturedDraggedNodeImage.createGraphics();// creating the graphics for buffered image
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Sets the Composite for the Graphics2D
																								// context
			lbl.setOpaque(false);
			lbl.paint(graphics); // painting the graphics to label
			graphics.dispose();
		}

		public final void paintDraggedNode(Point pt) {
			if (pt == null || capturedDraggedNodeImage == null) {
				return;
			}
			if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
				pt.x -= offset.x;
				pt.y -= offset.y;
			}
			if (rect2D.getLocation().equals(pt)) {
				return;
			}
			paintImmediately(rect2D.getBounds());
			rect2D.setRect((int) pt.getX(), (int) pt.getY(), capturedDraggedNodeImage.getWidth(), capturedDraggedNodeImage.getHeight());
			getGraphics().drawImage(capturedDraggedNodeImage, (int) pt.getX(), (int) pt.getY(), this);
		}

		public final void clearDraggedNode() {
			paintImmediately(rect2D.getBounds());
			capturedDraggedNodeImage = null;
		}

		public BrowserView getBrowserView() {
			return BrowserView.this;
		}
	}

	public JScrollPane getTreeScrollPane() {
		return treeScrollPane;
	}

	@Override
	public FlexoEditor getEditor() {
		if (getController() != null) {
			return getController().getEditor();
		}
		return null;
	}

	@Override
	public FlexoObject getFocusedObject() {
		/*
		 * if(_browser.getSelectionManager()!=null) return _browser.getSelectionManager().getFocusedObject();
		 */
		if (getSelectedObject() == null && _browser.showRootNode()) {
			return _browser.getRootObject();
		}
		return getSelectedObject();
	}

	@Override
	public Vector<FlexoObject> getGlobalSelection() {
		/*
		 * if(_browser.getSelectionManager()!=null) return _browser.getSelectionManager().getSelection();
		 */
		return getSelectedObjects();
	}

	/**
	 * Overrides updateUI
	 * 
	 * @see javax.swing.JPanel#updateUI()
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		if (treeView != null) {
			treeView.updateUI();
		}
	}

	public FlexoJTree getTreeView() {
		return treeView;
	}

	/*
	 * // TODO: use observable/observer scheme to handle this public void elementTypeFilterChanged() {
	 * controlPanel.elementTypeFilterChanged(); }
	 */

}
