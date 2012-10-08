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
package org.openflexo.components.browser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionSynchronizedComponent;
import org.openflexo.view.controller.FlexoController;

/**
 * Object that will act as a model for a JTree to represent a browsing perspective of a project
 * 
 * @author sguerin
 * 
 */
public abstract class ProjectBrowser extends DefaultTreeModel implements SelectionSynchronizedComponent {

	static final Logger logger = Logger.getLogger(ProjectBrowser.class.getPackage().getName());

	private static final int DEFAULT_ROW_HEIGHT = 18;

	protected FlexoProject _project;
	protected FlexoEditor _editor;

	private BrowserElement _rootElement;

	private FlexoModelObject _rootObject = null;

	protected Map<BrowserElementType, BrowserFilterStatus> _filterStatus;

	protected Map<BrowserElementType, Boolean> _filterDeepBrowsing;

	protected Map<BrowserElementType, ElementTypeBrowserFilter> _filters;

	// private Vector<ElementTypeBrowserFilter> _elementTypeFilters;

	private List<CustomBrowserFilter> _customFilters;

	// hashtable where keys are FlexoModelObject objects while values are either
	// BrowserElement
	// or a Vector of BrowserElement
	private Map<FlexoModelObject, Object> _elements = null;

	private final List<ProjectBrowserListener> _browserListeners;

	private SelectionManager _selectionManager;

	private SelectionController _selectionController;

	private boolean _handlesControlPanel = true;

	private boolean isRebuildingStructure = false;

	private boolean holdStructure = false;

	int rowHeight = DEFAULT_ROW_HEIGHT;

	private FlexoController controller;

	@Deprecated
	public ProjectBrowser(FlexoProject project) {
		this(project, true);
	}

	@Deprecated
	public ProjectBrowser(FlexoProject project, boolean initNow) {
		this(project, null, initNow);
	}

	@Deprecated
	public ProjectBrowser(FlexoEditor editor, SelectionManager selectionManager) {
		this(editor != null ? editor.getProject() : null, selectionManager, true);
		_editor = editor;
	}

	@Deprecated
	public ProjectBrowser(FlexoEditor editor, SelectionManager selectionManager, boolean initNow) {
		this(editor != null ? editor.getProject() : null, selectionManager, initNow);
		_editor = editor;
	}

	@Deprecated
	public ProjectBrowser(FlexoProject project, SelectionManager selectionManager) {
		this(project, selectionManager, true);
	}

	@Deprecated
	public ProjectBrowser(FlexoProject project, SelectionManager selectionManager, boolean initNow) {
		super(null);
		_project = project;
		_filterStatus = new Hashtable<BrowserElementType, BrowserFilterStatus>();
		_filterDeepBrowsing = new Hashtable<BrowserElementType, Boolean>();
		_filters = new Hashtable<BrowserElementType, ElementTypeBrowserFilter>();
		// _elementTypeFilters = new Vector<ElementTypeBrowserFilter>();
		_browserListeners = new Vector<ProjectBrowserListener>();
		_customFilters = new Vector<CustomBrowserFilter>();
		configure();
		if (selectionManager != null) {
			_selectionManager = selectionManager;
			_selectionManager.addToSelectionListeners(this);
		}
		_selectionController = new SelectionController.DefaultSelectionController();
		if (initNow) {
			init();
		}
	}

	@Deprecated
	protected ProjectBrowser(BrowserConfiguration configuration, SelectionManager selectionManager, boolean initNow) {
		super(null);
		_filterStatus = new Hashtable<BrowserElementType, BrowserFilterStatus>();
		_filterDeepBrowsing = new Hashtable<BrowserElementType, Boolean>();
		_filters = new Hashtable<BrowserElementType, ElementTypeBrowserFilter>();
		// _elementTypeFilters = new Vector<ElementTypeBrowserFilter>();
		_browserListeners = new Vector<ProjectBrowserListener>();
		_customFilters = new Vector<CustomBrowserFilter>();
		if (configuration != null) {
			_project = configuration.getProject();
			_browserElementFactory = configuration.getBrowserElementFactory();
			configuration.configure(this);
		}
		if (selectionManager != null) {
			_selectionManager = selectionManager;
			_selectionManager.addToSelectionListeners(this);
		}
		_selectionController = new SelectionController.DefaultSelectionController();
	}

	protected ProjectBrowser(FlexoController controller) {
		this(null, controller);
	}

	protected ProjectBrowser(TreeConfiguration configuration, FlexoController controller) {
		super(null);
		this.controller = controller;
		if (configuration != null) {
			_browserElementFactory = configuration.getBrowserElementFactory();
		}
		_filterStatus = new Hashtable<BrowserElementType, BrowserFilterStatus>();
		_filterDeepBrowsing = new Hashtable<BrowserElementType, Boolean>();
		_filters = new Hashtable<BrowserElementType, ElementTypeBrowserFilter>();
		// _elementTypeFilters = new Vector<ElementTypeBrowserFilter>();
		_browserListeners = new Vector<ProjectBrowserListener>();
		_customFilters = new Vector<CustomBrowserFilter>();
		if (configuration != null) {
			configuration.configure(this);
		} else {
			configure();
		}
		if (controller != null && controller.getSelectionManager() != null) {
			_selectionManager = controller.getSelectionManager();
			_selectionManager.addToSelectionListeners(this);
		}
		_selectionController = new SelectionController.DefaultSelectionController();
	}

	public boolean showRootNode() {
		return true;
	}

	public boolean isRootCollapsable() {
		return false;
	}

	public void setSelectionManager(SelectionManager aSelectionManager) {
		_selectionManager = aSelectionManager;
		_selectionManager.addToSelectionListeners(this);
	}

	public void init() {
		if (getRootElement() != null) {
			setRoot(getRootElement());
		}
	}

	private BrowserElementFactory _browserElementFactory = null;

	public BrowserElementFactory getBrowserElementFactory() {
		if (_browserElementFactory == null) {
			return DefaultBrowserElementFactory.DEFAULT_FACTORY;
		} else {
			return _browserElementFactory;
		}
	}

	protected void setBrowserElementFactory(BrowserElementFactory browserElementFactory) {
		_browserElementFactory = browserElementFactory;
	}

	public BrowserElement makeNewElement(FlexoModelObject object, BrowserElement parent) {
		return getBrowserElementFactory().makeNewElement(object, this, parent);
	}

	public void changeFilterStatus(BrowserElementType elementType, BrowserFilterStatus initialFilterStatus) {
		if (getFilterForElementType(elementType) != null) {
			getFilterForElementType(elementType).setInitialFilterStatus(initialFilterStatus);
			notifyListeners(new OptionalFilterAddedEvent(getFilterForElementType(elementType)));
		}
	}

	public void setFilterStatus(BrowserElementType elementType, BrowserFilterStatus status) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getClass().getName() + ": Setting filter status for " + elementType.getName() + " status=" + status);
		}
		_filterStatus.put(elementType, status);
	}

	public void setFilterStatus(BrowserElementType elementType, BrowserFilterStatus status, boolean isDeepBrowsing) {
		setFilterStatus(elementType, status);
		_filterDeepBrowsing.put(elementType, new Boolean(isDeepBrowsing));
	}

	protected boolean activateBrowsingFor(BrowserElement newElement) {
		boolean returned = newElement.getBrowserFilter().getStatus() == BrowserFilterStatus.SHOW;
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("activateBrowsingFor() " + newElement + " return " + returned);
		}
		return returned;
	}

	public boolean activateBrowsingFor(FlexoModelObject object) {
		BrowserElement browserElement = makeNewElement(object, null);
		boolean returned = activateBrowsingFor(browserElement);
		browserElement.delete();
		return returned;
	}

	public ElementTypeBrowserFilter getFilterForObject(FlexoModelObject object) {
		BrowserElement browserElement = makeNewElement(object, null);
		ElementTypeBrowserFilter returned = getFilterForElement(browserElement);
		browserElement.delete();
		return returned;
	}

	protected boolean requiresDeepBrowsing(BrowserElement newElement) {
		return newElement.getBrowserFilter().isDeepBrowsing();
	}

	public ElementTypeBrowserFilter getFilterForElementType(BrowserElementType elementType) {
		return _filters.get(elementType);
	}

	public ElementTypeBrowserFilter getFilterForElement(BrowserElement element) {
		BrowserElementType elementType = element.getFilteredElementType();
		if (elementType == null) {
			return null;
		}
		if (_filters.get(elementType) == null) {
			// first time we see this element type
			ElementTypeBrowserFilter newBrowserFilter = element.newBrowserFilter(_filterStatus.get(elementType));
			/*  if (_filterStatus.get(elementType) != null) {
			      newBrowserFilter.setStatus((_filterStatus.get(elementType)));
			      if (logger.isLoggable(Level.FINE))
			          logger.fine(getClass().getName() + ": Setting filter " + newBrowserFilter.getName() + " status to "
			                  + (_filterStatus.get(elementType)));
			      if ((newBrowserFilter.getStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN)
			              || (newBrowserFilter.getStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN)) {
			          if (logger.isLoggable(Level.FINE))
			              logger.fine(getClass().getName() + ": Adding optional filter " + newBrowserFilter.getName());
			          //_elementTypeFilters.add(newBrowserFilter);
			          if ((newBrowserFilter.getStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN)) {
			              newBrowserFilter.setStatus(BrowserFilterStatus.HIDE);
			          } else if ((newBrowserFilter.getStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN)) {
			              newBrowserFilter.setStatus(BrowserFilterStatus.SHOW);
			          }
			        //  notifyListeners(new OptionalFilterAddedEvent(newBrowserFilter));
			      }
			  }*/
			if (_filterDeepBrowsing.get(elementType) != null) {
				newBrowserFilter.setDeepBrowsing(_filterDeepBrowsing.get(elementType).booleanValue());
			}
			_filters.put(elementType, newBrowserFilter);
			notifyListeners(new OptionalFilterAddedEvent(newBrowserFilter));
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(getClass().getName() + ": Registering filter " + newBrowserFilter.getName());
			}
		}
		return _filters.get(elementType);
	}

	@Deprecated
	public FlexoProject getProject() {
		return _project;
	}

	/*
	 * Pour le moment parce que j'ai pas trop le temps, on reconstruit a chaque
	 * fois tout l'arbre. On pourrait faire un truc beaucoup plus chiade avec
	 * des MutableNodeTree qui recoivent plein de DataModification toutes plus
	 * chiadees les unes que les autres, faire des insert nodes, en notifier le
	 * modele, tout ca....
	 */
	/**
	 * Brutal update of the whole browser. You rarely need to invoke it, except when you have done big structural changes which are not
	 * notified (for example filter status modification)
	 */
	public void update() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					update();
				}
			});
			return;
		}
		Vector<FlexoModelObject> selectionBeforeUpdate = null;
		if (getSelectedObjects() != null) {
			selectionBeforeUpdate = new Vector<FlexoModelObject>(getSelectedObjects());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Before update, selected objects are " + selectionBeforeUpdate);
			}
		}
		Vector<FlexoModelObject> expandedObjects = null;
		if (leadingView != null) {
			expandedObjects = new Vector<FlexoModelObject>();
			Vector<BrowserElement> expandedElements = leadingView.getExpandedElements();
			for (Enumeration<BrowserElement> en = expandedElements.elements(); en.hasMoreElements();) {
				BrowserElement next = en.nextElement();
				expandedObjects.add(next.getObject());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Before update, expanded objects are " + expandedObjects);
			}
		}
		rebuildTree();
		update(getRootElement());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Notifying expansions");
		}
		notifyListeners(new ExpansionNotificationEvent());
		if (selectionBeforeUpdate != null) {
			for (Enumeration<FlexoModelObject> en = selectionBeforeUpdate.elements(); en.hasMoreElements();) {
				FlexoModelObject next = en.nextElement();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Select " + next);
				}
				fireObjectSelected(next);
			}
		}
		if (expandedObjects != null) {
			for (Enumeration<FlexoModelObject> en = expandedObjects.elements(); en.hasMoreElements();) {
				FlexoModelObject next = en.nextElement();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Expand " + next);
				}
				expand(next, false);
				// expandAll(next);
			}
		}
	}

	/*
	 * private void expandAll(FlexoModelObject object) { BrowserElement[]
	 * elements = elementForObject(object); for (int i=0; i<elements.length;
	 * i++) { BrowserElement el = elements[i]; for (int j=0; j<el.getChildCount();
	 * j++) { BrowserElement el2 = (BrowserElement)el.getChildAt(j);
	 * expand(el2.getObject()); } } }
	 */

	private BrowserView leadingView;

	protected BrowserView getLeadingView() {
		return leadingView;
	}

	public void setLeadingView(BrowserView leadingView) {
		this.leadingView = leadingView;
	}

	protected Vector<BrowserElement> getSelectedElements() {
		if (leadingView != null) {
			return leadingView.getSelectedElements();
		}
		return null;
	}

	protected Vector<FlexoModelObject> getSelectedObjects() {
		if (leadingView != null) {
			return leadingView.getSelectedObjects();
		}
		return null;
	}

	protected Vector<FlexoModelObject> getExpandedObjects() {
		Vector<FlexoModelObject> expandedObjects = new Vector<FlexoModelObject>();
		if (leadingView != null) {
			Vector<BrowserElement> expandedElements = leadingView.getExpandedElements();
			for (Enumeration<BrowserElement> en = expandedElements.elements(); en.hasMoreElements();) {
				BrowserElement next = en.nextElement();
				expandedObjects.add(next.getObject());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Before update, expanded objects are " + expandedObjects);
			}
		}
		return expandedObjects;
	}

	/**
	 * Update the TreeModel juste below element
	 * 
	 * @param element
	 */
	protected void update(BrowserElement element) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update tree from " + element + " in ProjectBrowser");
		}
		reload(element);
	}

	public BrowserElement getRootElement() {
		if (_rootElement == null) {
			rebuildTree();
		}
		return _rootElement;
	}

	/**
	 * This has no modifier because sub-classes should call this directly.
	 */
	void rebuildTree() {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("refreshWhenPossible() DO IT NOW");
						}
						rebuildTree();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Rebuild whole tree in ProjectBrowser");
		}
		if (getRootObject() != null) {
			if (_elements != null) {
				clearTree();
			}
			_elements = new Hashtable<FlexoModelObject, Object>();
			_rootElement = makeNewElement(getRootObject(), null);
		}
	}

	public void setRootObject(FlexoModelObject aRootObject) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Setting root object to be " + aRootObject);
		}
		_rootObject = aRootObject;
		update();
		if (_rootObject != null && !isRootCollapsable()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (showRootNode()) {
						expand(_rootObject, false);
					} else {
						BrowserElement[] elements = elementForObject(_rootObject);
						List<BrowserElement> children = new ArrayList<BrowserElement>();
						for (BrowserElement el : elements) {
							Enumeration<BrowserElement> en = el.children();
							while (en.hasMoreElements()) {
								children.add(en.nextElement());
							}
						}
						notifyListeners(new ExpansionNotificationEvent(children.toArray(new BrowserElement[children.size()]), true));
					}
				}
			});
		}
	}

	public FlexoModelObject getRootObject() {
		if (_rootObject != null) {
			return _rootObject;
		} else {
			return getDefaultRootObject();
		}
	}

	public void delete() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Delete browser " + getClass().getName());
		}

		clearTree();
		_project = null;
		_rootElement = null;
		_filterStatus.clear();
		_filterDeepBrowsing.clear();
		_filters.clear();
		_browserListeners.clear();
		_selectionManager = null;
	}

	private void clearTree() {
		if (_rootElement != null) {
			_rootElement.delete();
		}
		for (Object obj : _elements.values()) {
			if (obj instanceof BrowserElement) {
				((BrowserElement) obj).delete();
			} else if (obj instanceof Vector) {
				Vector<BrowserElement> elementList = (Vector<BrowserElement>) obj;
				for (Enumeration<BrowserElement> e2 = elementList.elements(); e2.hasMoreElements();) {
					BrowserElement element = e2.nextElement();
					element.delete();
				}
			}
		}
		_elements.clear();
		_expansionSynchronizedElements.removeAllElements();
		_expansionSynchronizedElements = new Vector<ExpansionSynchronizedElement>();
		_elements = null;
	}

	Vector<ExpansionSynchronizedElement> _expansionSynchronizedElements = new Vector<ExpansionSynchronizedElement>();

	protected void registerElementForObject(BrowserElement element, FlexoModelObject modelObject) {
		if (element == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to register a null element for " + modelObject + " in ProjectBrowser !");
			}
			return;
		}
		if (_elements == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("_elements is null in browser " + getClass().getName());
			}
			return;
		}
		Object existingValue = _elements.get(modelObject);
		if (existingValue != null) {
			if (existingValue instanceof BrowserElement) {
				Vector<BrowserElement> newVector = new Vector<BrowserElement>();
				newVector.add((BrowserElement) existingValue);
				newVector.add(element);
				_elements.put(modelObject, newVector);
			} else if (existingValue instanceof Vector) {
				Vector<BrowserElement> newVector = (Vector<BrowserElement>) _elements.get(modelObject);
				newVector.add(element);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Found unexpected " + existingValue.getClass().getName() + " in ProjectBrowser !");
				}
			}
		} else {
			_elements.put(modelObject, element);
		}
		if (element instanceof ExpansionSynchronizedElement) {
			// logger.info("What about "+element+" isExpansionSynchronizedWithData()="+((ExpansionSynchronizedElement)
			// element).isExpansionSynchronizedWithData());
			if (((ExpansionSynchronizedElement) element).isExpansionSynchronizedWithData()) {
				_expansionSynchronizedElements.add((ExpansionSynchronizedElement) element);
			}
		}
		if (element.getSelectableObject() != modelObject) {
			registerElementForObject(element, element.getSelectableObject());
		}
	}

	protected void unregisterElementForObject(BrowserElement element, FlexoModelObject modelObject) {
		if (element.getSelectableObject() != modelObject) {
			unregisterElementForObject(element, element.getSelectableObject());
		}
		Object existingValue = _elements.get(modelObject);
		if (existingValue != null) {
			if (existingValue instanceof BrowserElement) {
				_elements.remove(modelObject);
			} else if (existingValue instanceof Vector) {
				Vector newVector = (Vector) _elements.get(modelObject);
				newVector.remove(element);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Found unexpected " + existingValue.getClass().getName() + " in ProjectBrowser !");
				}
			}
		}
		if (element instanceof ExpansionSynchronizedElement) {
			_expansionSynchronizedElements.remove(element);
		}
	}

	public boolean isExpansionSynchronizedElement(BrowserElement element) {
		return element instanceof ExpansionSynchronizedElement && _expansionSynchronizedElements.contains(element);
	}

	/**
	 * Return an array of TreePath of all paths where this observable is defined
	 * 
	 * @param object
	 * @return
	 */
	public TreePath[] treePathForObject(FlexoModelObject object) {
		Vector<TreePath> builtVector = new Vector<TreePath>();
		if (object == null) {
			return null;
		}
		if (_elements == null) {
			return null;
		}
		Object found = _elements.get(object);
		if (found instanceof BrowserElement) {
			BrowserElement element = (BrowserElement) found;
			builtVector.add(element.getTreePath());
		} else if (found instanceof Vector) {
			Vector elementList = (Vector) found;
			for (Enumeration e = elementList.elements(); e.hasMoreElements();) {
				BrowserElement element = (BrowserElement) e.nextElement();
				builtVector.add(element.getTreePath());
			}
		} else if (found != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found unexpected " + found.getClass().getName() + " in ProjectBrowser !");
			}
			return null;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found null in ProjectBrowser for object " + object);
			}
			return null;
		}
		TreePath[] returned = new TreePath[builtVector.size()];
		for (int i = 0; i < builtVector.size(); i++) {
			returned[i] = builtVector.get(i);
		}
		return returned;
	}

	/**
	 * Return an array of TreePath of all paths where this observable is defined
	 * 
	 * @param object
	 * @return
	 */
	protected BrowserElement[] elementForObject(FlexoModelObject object) {
		Vector<BrowserElement> builtVector = new Vector<BrowserElement>();
		Object found = object != null ? _elements.get(object) : null;
		if (found instanceof BrowserElement) {
			BrowserElement element = (BrowserElement) found;
			builtVector.add(element);
		} else if (found instanceof Vector) {
			Vector elementList = (Vector) found;
			for (Enumeration e = elementList.elements(); e.hasMoreElements();) {
				BrowserElement element = (BrowserElement) e.nextElement();
				builtVector.add(element);
			}
		} else if (found != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found unexpected " + found.getClass().getName() + " in ProjectBrowser !");
			}
			return null;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Found null in ProjectBrowser for object " + object);
			}
			return null;
		}
		BrowserElement[] returned = new BrowserElement[builtVector.size()];
		for (int i = 0; i < builtVector.size(); i++) {
			returned[i] = builtVector.get(i);
		}
		return returned;
	}

	@Deprecated
	public FlexoModelObject getDefaultRootObject() {
		return null;
	}

	public abstract void configure();

	@Override
	public Object getRoot() {
		return getRootElement();
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((BrowserElement) parent).getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((BrowserElement) parent).getChildCount();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((BrowserElement) node).isLeaf();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((BrowserElement) parent).getIndex((TreeNode) child);
	}

	public synchronized void addBrowserListener(ProjectBrowserListener l) {
		_browserListeners.add(l);
	}

	public synchronized void deleteBrowserListener(ProjectBrowserListener l) {
		_browserListeners.remove(l);
	}

	public void notifyExpansionChanged(final ExpansionSynchronizedElement element) {
		if (element instanceof BrowserElement) {
			if (((BrowserElement) element).refreshHasBeenRequested()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!((BrowserElement) element).isDeleted()) {
							notifyListeners(new ExpansionNotificationEvent(element));
						}
					}
				});
				return;
			}
		}
		notifyListeners(new ExpansionNotificationEvent(element));
	}

	public synchronized void notifyListeners(BrowserEvent event) {
		// TODO: Fix this. It seems that sole Listeners (BrowserView) are removing/adding themselves
		// during the notification phase (for ExpansionNotificationEvent, for example)
		for (ProjectBrowserListener l : new ArrayList<ProjectBrowserListener>(_browserListeners)) {
			if (event instanceof OptionalFilterAddedEvent) {
				l.optionalFilterAdded((OptionalFilterAddedEvent) event);
			} else if (event instanceof ObjectAddedToSelectionEvent) {
				l.objectAddedToSelection((ObjectAddedToSelectionEvent) event);
			} else if (event instanceof ObjectRemovedFromSelectionEvent) {
				l.objectRemovedFromSelection((ObjectRemovedFromSelectionEvent) event);
			} else if (event instanceof SelectionClearedEvent) {
				l.selectionCleared((SelectionClearedEvent) event);
			} else if (event instanceof ExpansionNotificationEvent) {
				l.notifyExpansions((ExpansionNotificationEvent) event);
			} else if (event instanceof EnableExpandingSynchronizationEvent) {
				l.enableExpandingSynchronization((EnableExpandingSynchronizationEvent) event);
			} else if (event instanceof DisableExpandingSynchronizationEvent) {
				l.disableExpandingSynchronization((DisableExpandingSynchronizationEvent) event);
			}
		}
	}

	public class BrowserEvent {
	}

	public class OptionalFilterAddedEvent extends BrowserEvent {

		private final ElementTypeBrowserFilter _browserFilter;

		protected OptionalFilterAddedEvent(ElementTypeBrowserFilter browserFilter) {
			super();
			_browserFilter = browserFilter;
		}

		public ElementTypeBrowserFilter getFilter() {
			return _browserFilter;
		}
	}

	public class ObjectAddedToSelectionEvent extends BrowserEvent {

		private final FlexoModelObject _object;

		protected ObjectAddedToSelectionEvent(FlexoModelObject object) {
			super();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("ObjectAddedToSelectionEvent");
			}
			_object = object;
		}

		public FlexoModelObject getAddedObject() {
			return _object;
		}
	}

	public class ObjectRemovedFromSelectionEvent extends BrowserEvent {

		private final FlexoModelObject _object;

		protected ObjectRemovedFromSelectionEvent(FlexoModelObject object) {
			super();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("ObjectRemovedFromSelectionEvent");
			}
			_object = object;
		}

		public FlexoModelObject getRemovedObject() {
			return _object;
		}
	}

	public class SelectionClearedEvent extends BrowserEvent {

		protected SelectionClearedEvent() {
			super();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("SelectionClearedEvent");
			}
		}
	}

	public class EnableExpandingSynchronizationEvent extends BrowserEvent {

		protected EnableExpandingSynchronizationEvent() {
			super();
		}
	}

	public class DisableExpandingSynchronizationEvent extends BrowserEvent {

		protected DisableExpandingSynchronizationEvent() {
			super();
		}
	}

	public class ExpansionNotificationEvent extends BrowserEvent {

		protected List<TreePath> _pathsToExpand;

		protected List<TreePath> _pathsToCollapse;

		public ExpansionNotificationEvent() {
			super();
			_pathsToExpand = new Vector<TreePath>();
			_pathsToCollapse = new Vector<TreePath>();
			for (Enumeration<ExpansionSynchronizedElement> e = _expansionSynchronizedElements.elements(); e.hasMoreElements();) {
				ExpansionSynchronizedElement element = e.nextElement();
				TreePath path = element.getTreePath();
				if (element.isExpanded()) {
					_pathsToExpand.add(path);
				} else {
					_pathsToCollapse.add(path);
				}
			}
		}

		public ExpansionNotificationEvent(ExpansionSynchronizedElement element) {
			super();
			_pathsToExpand = new Vector<TreePath>();
			_pathsToCollapse = new Vector<TreePath>();
			TreePath path = ((BrowserElement) element).getTreePath();
			if (element.isExpanded()) {
				_pathsToExpand.add(path);
			} else {
				_pathsToCollapse.add(path);
			}
		}

		public ExpansionNotificationEvent(BrowserElement[] elements, boolean expand) {
			super();
			_pathsToExpand = new ArrayList<TreePath>();
			_pathsToCollapse = new ArrayList<TreePath>();
			for (int i = 0; i < elements.length; i++) {
				BrowserElement element = elements[i];
				TreePath path = element.getTreePath();
				if (expand) {
					_pathsToExpand.add(path);
				} else {
					_pathsToCollapse.add(path);
				}
			}
		}

		public List<TreePath> pathsToExpand() {
			return _pathsToExpand;
		}

		public List<TreePath> pathsToCollapse() {
			return _pathsToCollapse;
		}
	}

	/**
	 * Adds specified object to selection
	 * 
	 * @param object
	 */
	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		if (object instanceof IEWOComponent) {
			fireObjectSelected(((IEWOComponent) object).getComponentDefinition());
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ProjectBrowser.addToSelection() " + object);
		}
		notifyListeners(new ObjectAddedToSelectionEvent(object));
	}

	/**
	 * Removes specified object from selection
	 * 
	 * @param object
	 */
	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
		if (object instanceof IEWOComponent) {
			fireObjectDeselected(((IEWOComponent) object).getComponentDefinition());
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("ProjectBrowser.removeFromSelection() " + object);
		}
		notifyListeners(new ObjectRemovedFromSelectionEvent(object));
	}

	/**
	 * Clear selection
	 */
	@Override
	public void fireResetSelection() {
		notifyListeners(new SelectionClearedEvent());
	}

	/**
	 * Update selection
	 */
	public void updateSelection() {
		if (_selectionManager != null) {
			_selectionManager.fireUpdateSelection(this);
		}
	}

	/**
	 * Notify that the selection manager is performing a multiple selection
	 */
	@Override
	public void fireBeginMultipleSelection() {
		notifyListeners(new DisableExpandingSynchronizationEvent());
	}

	/**
	 * Notify that the selection manager has finished to perform a multiple selection
	 */
	@Override
	public void fireEndMultipleSelection() {
		notifyListeners(new EnableExpandingSynchronizationEvent());
	}

	/**
	 * Notify that selection must be saved to be restored later (modification of the structure)
	 */
	/*
	 * public void saveSelection() { notifyListeners(new SaveSelectionEvent()); }
	 */

	/**
	 * Notify that selection must be saved to be restored later (modification of the structure)
	 */
	/*
	 * public void restoreSelection() { notifyListeners(new
	 * RestoreSelectionEvent()); }
	 */

	@Override
	public SelectionManager getSelectionManager() {
		return _selectionManager;
	}

	public void focusOn(FlexoModelObject object) {
		// logger.info("Focus on "+object);
		// fireResetSelection();
		// fireObjectSelected(object);
		resetSelection();
		addToSelected(object);
	}

	public void focusOff(FlexoModelObject object) {
		collapse(object);
	}

	public void expand(FlexoModelObject object, boolean synchronizeExpansion) {
		BrowserElement[] elements = elementForObject(object);
		if (elements == null) {
			// logger.info("Elements representing object "+object+" could not be
			// found");
		} else {
			if (!synchronizeExpansion) {
				for (BrowserElement e : elements) {
					e.enableSynchronizeExpansion();
				}
			}
			notifyListeners(new ExpansionNotificationEvent(elements, true));
			if (!synchronizeExpansion) {
				for (BrowserElement e : elements) {
					e.disableSynchronizeExpansion();
				}
			}
		}
	}

	public void collapse(FlexoModelObject object) {
		BrowserElement[] elements = elementForObject(object);
		if (elements != null) {
			notifyListeners(new ExpansionNotificationEvent(elements, false));
		}
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		BrowserElement element = (BrowserElement) path.getLastPathComponent();
		if (element.isNameEditable()) {
			try {
				element.setName((String) newValue);
			} catch (FlexoException e) {
				// TODO handle those error properly
				// Instead of doing setName() on the model, use a FlexoAction
				// This is the FlexoAction exception handler that will catch
				// those errors !!!
				e.printStackTrace();
			}
		}
	}

	public Iterator<FlexoModelObject> getAllObjects() {
		return _elements.keySet().iterator();
	}

	public SelectionController getSelectionController() {
		return _selectionController;
	}

	public void setSelectionController(SelectionController selectionController) {
		_selectionController = selectionController;
	}

	public boolean handlesControlPanel() {
		return _handlesControlPanel;
	}

	public void setHandlesControlPanel(boolean handlesControlPanel) {
		_handlesControlPanel = handlesControlPanel;
	}

	public Vector<ElementTypeBrowserFilter> getConfigurableElementTypeFilters() {
		Vector<ElementTypeBrowserFilter> returned = new Vector<ElementTypeBrowserFilter>();
		for (BrowserElementType t : _filters.keySet()) {
			ElementTypeBrowserFilter f = _filters.get(t);
			if (f.getInitialFilterStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN
					|| f.getInitialFilterStatus() == BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN) {
				returned.add(f);
			}
		}
		return returned;
	}

	@Override
	public Vector<FlexoModelObject> getSelection() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getSelection();
		}
		return null;
	}

	@Override
	public void resetSelection() {
		if (getSelectionManager() != null) {
			getSelectionManager().resetSelection();
		} else {
			fireResetSelection();
		}
	}

	@Override
	public void addToSelected(FlexoModelObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().addToSelected(object);
			} else {
				fireObjectSelected(object);
			}
		}
	}

	@Override
	public void removeFromSelected(FlexoModelObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().removeFromSelected(object);
			} else {
				fireObjectDeselected(object);
			}
		}
	}

	@Override
	public void addToSelected(Vector<? extends FlexoModelObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().addToSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration<? extends FlexoModelObject> en = objects.elements(); en.hasMoreElements();) {
				FlexoModelObject next = en.nextElement();
				fireObjectSelected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void removeFromSelected(Vector<? extends FlexoModelObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().removeFromSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration<? extends FlexoModelObject> en = objects.elements(); en.hasMoreElements();) {
				FlexoModelObject next = en.nextElement();
				fireObjectDeselected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoModelObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().setSelectedObjects(objects);
		} else {
			resetSelection();
			addToSelected(objects);
		}
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getFocusedObject();
		}
		return null;
	}

	@Override
	public boolean mayRepresents(FlexoModelObject anObject) {
		if (anObject instanceof IEWOComponent) {
			return _elements.get(anObject) != null || mayRepresents(((IEWOComponent) anObject).getComponentDefinition());
		}
		return _elements != null && anObject != null && _elements.get(anObject) != null;
	}

	public List<CustomBrowserFilter> getCustomFilters() {
		return _customFilters;
	}

	public void setCustomFilters(Vector<CustomBrowserFilter> customFilters) {
		_customFilters = customFilters;
	}

	public void addToCustomFilters(CustomBrowserFilter aCustomFilter) {
		_customFilters.add(aCustomFilter);
	}

	public void removeFromCustomFilters(CustomBrowserFilter aCustomFilter) {
		_customFilters.remove(aCustomFilter);
	}

	// TODO: should NOT be handled at this level
	public static enum DMViewMode {
		Repositories, Packages, Hierarchy, Diagrams
	}

	// TODO: should NOT be handled at this level
	private DMViewMode _viewMode = DMViewMode.Repositories;

	// TODO: should NOT be handled at this level
	public DMViewMode getDMViewMode() {
		return _viewMode;
	}

	// TODO: should NOT be handled at this level
	public void setDMViewMode(DMViewMode viewMode) {
		if (_viewMode != viewMode) {
			_viewMode = viewMode;
			update();
		}
	}

	// TODO: should NOT be handled at this level
	public static enum OEViewMode {
		NoHierarchy, PartialHierarchy, FullHierarchy
	}

	// TODO: should NOT be handled at this level
	private OEViewMode _oeViewMode = OEViewMode.NoHierarchy;

	// TODO: should NOT be handled at this level
	public OEViewMode getOEViewMode() {
		return _oeViewMode;
	}

	// TODO: should NOT be handled at this level
	public void setOEViewMode(OEViewMode viewMode) {
		if (_oeViewMode != viewMode) {
			_oeViewMode = viewMode;
			update();
		}
	}

	// TODO: should NOT be handled at this level
	private boolean _showOnlyAnnotationProperties = false;

	// TODO: should NOT be handled at this level
	public boolean showOnlyAnnotationProperties() {
		return _showOnlyAnnotationProperties;
	}

	// TODO: should NOT be handled at this level
	public void setShowOnlyAnnotationProperties(boolean showOnlyAnnotationProperties) {
		_showOnlyAnnotationProperties = showOnlyAnnotationProperties;
	}

	// TODO: should NOT be handled at this level
	private RoleViewMode roleViewMode = RoleViewMode.TOP_DOWN;

	// TODO: should NOT be handled at this level
	public enum RoleViewMode {
		TOP_DOWN {
			@Override
			public ImageIcon getIcon() {
				return UtilsIconLibrary.ARROW_DOWN;
			}
		},
		BOTTOM_UP {
			@Override
			public ImageIcon getIcon() {
				return UtilsIconLibrary.ARROW_UP;
			}
		},
		FLAT {
			@Override
			public ImageIcon getIcon() {
				return WKFIconLibrary.FLAT_ICON;
			}
		};
		public abstract ImageIcon getIcon();

		public String getLocalizedName() {
			return FlexoLocalization.localizedForKey(name().toLowerCase());
		}
	}

	@Override
	public void reload(TreeNode node) {
		if (node instanceof BrowserElement && ((BrowserElement) node).isDeleted()) {
			return;
		}
		super.reload(node);
	}

	// TODO: should NOT be handled at this level
	public RoleViewMode getRoleViewMode() {
		return roleViewMode;
	}

	// TODO: should NOT be handled at this level
	public void setRoleViewMode(RoleViewMode viewMode) {
		this.roleViewMode = viewMode;
		update();
	}

	public boolean isRebuildingStructure() {
		return isRebuildingStructure;
	}

	public void setIsRebuildingStructure() {
		isRebuildingStructure = true;
	}

	public void resetIsRebuildingStructure() {
		isRebuildingStructure = false;
	}

	public boolean isHoldingStructure() {
		return holdStructure;
	}

	public void setHoldStructure() {
		holdStructure = true;
	}

	public void resetHoldStructure() {
		holdStructure = false;
	}

	/**
	 * Return editor when available False otherwise
	 * 
	 * @return
	 */
	public FlexoEditor getEditor() {
		if (controller != null) {
			return controller.getEditor();
		}
		return _editor;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}
}
