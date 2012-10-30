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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openflexo.AdvancedPrefs;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.dm.dm.DMDataModification;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ontology.dm.OEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.event.SGDataModification;
import org.openflexo.foundation.toc.TOCModification;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectNeedsRefresh;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.WKFDataModification;
import org.openflexo.foundation.ws.dm.WSDataModification;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;

/**
 * Represent an item that is browsable inside a {@link ProjectBrowser}
 * 
 * @author sguerin
 * 
 */
public abstract class BrowserElement implements TreeNode, FlexoObserver {

	static final Logger logger = Logger.getLogger(BrowserElement.class.getPackage().getName());

	protected BrowserElement _parent;

	protected Vector<BrowserElement> _childs;

	protected FlexoModelObject _object;

	protected ProjectBrowser _browser;

	protected BrowserElementType _elementType;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public BrowserElement(FlexoModelObject object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent) {
		super();
		// System.out.println("build "+_id);
		if (object == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Try to initialize browser element with a null object.");
			}
		} else {
			_object = object;
			_browser = browser;
			_elementType = elementType;
			_parent = parent;
			if (BrowserElementType.getBrowserElementTypeForClassName(object.getClass().getName()) != _elementType) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Element type do not match: "
							+ BrowserElementType.getBrowserElementTypeForClassName(object.getClass().getName()) + "!=" + _elementType);
				}
			}
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Create BrowserElement " + this + " for object " + _object);
			}
			_object.addObserver(this);
			_childs = new Vector<BrowserElement>();
			if (browser != null) {
				_browser.setIsRebuildingStructure();
				buildChildrenVector();
				_browser.resetIsRebuildingStructure();
				browser.registerElementForObject(this, object);
			}
		}
	}

	public String getToolTip() {
		return null;
	}

	protected FlexoProject getProject() {
		if (_object != null) {
			return _object.getProject();
		}
		return null;
	}

	private volatile boolean isDeleted = false;

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean mustBeHighlighted() {
		return false;
	}

	public boolean mustBeItalic() {
		return false;
	}

	public void delete() {
		if (!isDeleted) {
			if (!SwingUtilities.isEventDispatchThread() || _browser.isHoldingStructure()) {
				SwingUtilities.invokeLater(new Runnable() {
					/**
					 * Overrides run
					 * 
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						delete();
					}
				});
				return;
			}
			// System.gc();
			// System.out.println("delete "+_id+" map size="+map.size());

			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Delete BrowserElement " + this + " for object " + _object);
			}
			if (_object != null) {
				_browser.unregisterElementForObject(this, _object);
				_object.deleteObserver(this);
			}
			recursiveDeleteChilds();
			if (_parent != null) {
				_parent.removeFromChilds(this);
			}
			_parent = null;
			_object = null;
			_browser = null;
			isDeleted = true;
		}
	}

	private void recursiveDeleteChilds() {
		if (_childs != null) {
			Vector<BrowserElement> childs = new Vector<BrowserElement>();
			childs.addAll(_childs);
			for (Enumeration<BrowserElement> e = childs.elements(); e.hasMoreElements();) {
				BrowserElement child = e.nextElement();
				child.recursiveDeleteChilds();
				child.delete();
			}
			_childs.clear();// To be sure, but normally, they are all gone since delete() invokes parent.removeFromChilds()! Beware of
							// concurrent modification of the _childs Vector
		}
	}

	protected TreePath getTreePath() {
		int depth = getDepth();
		Object[] path = new Object[depth];
		int i = depth - 1;
		BrowserElement current = this;
		while (i >= 0) {
			path[i] = current;
			current = (BrowserElement) current.getParent();
			i--;
		}
		return new TreePath(path);
	}

	protected int getDepth() {
		if (_parent == null) {
			return 1;
		} else {
			return _parent.getDepth() + 1;
		}
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	public FlexoModelObject getObject() {
		if (isDeleted) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Try to access a deleted BrowserElement !");
			}
		}
		if (_object == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Object is null !");
			}
		}
		return _object;
	}

	public FlexoModelObject getSelectableObject() {
		return getObject();
	}

	protected abstract void buildChildrenVector();

	public String getName() {
		return getElementType().getLocalizedName();
	}

	public String getSuffixName() {
		return null;
	}

	public Icon getIcon() {
		return decorateIcon(getElementType().getIcon());
	}

	protected final Icon decorateIcon(Icon returned) {
		if (AdvancedPrefs.getHightlightUncommentedItem() && getObject() != null && getObject().isDescriptionImportant()
				&& !getObject().hasDescription()) {
			if (returned instanceof ImageIcon) {
				returned = IconFactory.getImageIcon((ImageIcon) returned, new IconMarker[] { IconLibrary.WARNING });
			} else {
				logger.severe("CANNOT decorate a non ImageIcon for " + this);
			}
		}
		return returned;
	}

	public Icon getExpandedIcon() {
		if (getElementType().getExpandedIcon() != null) {
			return decorateIcon(getElementType().getExpandedIcon());
		}
		return getIcon();
	}

	protected ElementTypeBrowserFilter getBrowserFilter() {
		return _browser.getFilterForElement(this);
	}

	protected ElementTypeBrowserFilter newBrowserFilter(BrowserFilterStatus initialFilterStatus) {
		// DVA june 06.
		// getIcon is only valid when accessed from the BrowserElement.getIcon method, because it
		// can be overridden. Am I wrong?

		/*return new ElementTypeBrowserFilter(getFilteredElementType().getName(), getFilteredElementType()
		    .getIcon());*/
		if (initialFilterStatus == null) {
			initialFilterStatus = BrowserFilterStatus.SHOW;
		}
		return new ElementTypeBrowserFilter(getFilteredElementType().getName(), getIcon(), initialFilterStatus);
	}

	public BrowserElementType getElementType() {
		return _elementType;
	}

	protected BrowserElementType getFilteredElementType() {
		return _elementType;
	}

	protected void removeFromChilds(BrowserElement element) {
		_childs.remove(element);
	}

	protected void addToChilds(FlexoModelObject modelObject) {
		// Check if any of declared filters matches element
		if (!matchAnyCustomFilter(modelObject)) {
			return;
		}
		if (modelObject != null) {
			boolean elementHasBeenAdded = false;
			BrowserElement newElement = _browser.makeNewElement(modelObject, this);
			if (newElement != null) {
				boolean deepBrowsing = false;
				if (_browser.activateBrowsingFor(newElement)) {
					newElement._parent = this;
					_childs.add(newElement);
					elementHasBeenAdded = true;
				} else if (deepBrowsing = _browser.requiresDeepBrowsing(newElement)) {
					Vector<BrowserElement> childrenToRemove = new Vector<BrowserElement>();
					for (Enumeration<BrowserElement> e = newElement.children(); e.hasMoreElements();) {
						BrowserElement newElement2 = e.nextElement();
						addToChilds(newElement2.getObject());
					}
				}
				// DVA April 06: if deepBrowsing is on, do not delete element and children...
				if (!elementHasBeenAdded && !deepBrowsing) {
					newElement.recursiveDeleteChilds();
					newElement.delete();
				}
			}
		} else {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Unexpected null value in browser... but it may occurs during a loading for ComponentNameChange notification, or an IEOperator insertion.");
			}

		}
	}

	private boolean matchAnyCustomFilter(FlexoModelObject modelObject) {
		// If no filters defined, take object
		if (_browser.getCustomFilters().size() == 0) {
			return true;
		}
		for (CustomBrowserFilter filter : _browser.getCustomFilters()) {
			if (filter.getStatus() == BrowserFilterStatus.SHOW && filter.accept(modelObject)) {
				return true;
			}
		}
		return false;
	}

	// ==========================================================================
	// ================= FlexoObserver default implementation
	// ===================
	// ==========================================================================

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (isDeleted()) {
			logger.warning("I am deleted but receive a notification from :" + observable + " I am " + this);
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(getClass().getName() + " receive DataModification " + dataModification.getClass().getName());
		}
		if (_browser != null) {
			if ((dataModification instanceof WKFDataModification || dataModification instanceof IEDataModification
					|| dataModification instanceof DKVDataModification || dataModification instanceof DMDataModification
					|| dataModification instanceof WSDataModification || dataModification instanceof OEDataModification
					|| dataModification instanceof CGDataModification || dataModification instanceof SGDataModification
					|| dataModification instanceof ObjectDeleted || dataModification instanceof TOCModification || dataModification instanceof NameChanged)
					&& !(dataModification instanceof ObjectLocationChanged)
					&& !(dataModification instanceof ObjectSizeChanged)
					&& !(dataModification instanceof ObjectNeedsRefresh)) {
				refreshWhenPossible();
			}
		}
	}

	private boolean repaintRequested = false;

	public void updateViewWhenPossible() {
		if (isDeleted || repaintRequested || refreshRequested) {
			return;
		}
		synchronized (this) {
			if (!repaintRequested) {
				repaintRequested = true;
			}
		}
		if (!SwingUtilities.isEventDispatchThread() || _browser.isRebuildingStructure()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("updateView() DO IT NOW");
					}
					updateView();
				}
			});
			return;
		}
		updateView();
	}

	protected void updateView() {
		if (isDeleted) {
			return;
		}
		_browser.nodeChanged(this);
		repaintRequested = false;
	}

	private boolean refreshRequested = false;

	/*    protected void refresh()
	    {
	        refreshRequestedWhenPossible = false;
	        rebuildChildren();
	    }
	    */
	// public boolean refreshRequestedWhenPossible = false;

	public void refreshWhenPossible() {
		synchronized (this) {
			if (isDeleted || _browser.isHoldingStructure() || refreshRequested || _parent != null && _parent.refreshRequested) {
				return;
			}
			refreshRequested = true;
		}
		// logger.info("Call to refreshWhenPossible()");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// if (logger.isLoggable(Level.INFO))
				// logger.info("refreshWhenPossible() DO IT NOW");
				try {
					rebuildChildren();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void rebuildChildren() {
		// logger.info("_browser.isHoldingStructure()="+_browser.isHoldingStructure());
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("BrowserElement: rebuild children for " + this);
		}
		if (isDeleted) {
			return;
		}
		if (_browser.isHoldingStructure()) {
			return;
		}
		if (!SwingUtilities.isEventDispatchThread() || _browser.isRebuildingStructure()) {
			refreshWhenPossible();
			return;
		}
		_browser.setIsRebuildingStructure();
		try {
			Vector<FlexoModelObject> expanded = _browser.getExpandedObjects();
			_browser.fireResetSelection();// Clears the selection in the JTree
			recursiveDeleteChilds();
			_childs = new Vector<BrowserElement>();
			buildChildrenVector();
			_browser.reload(this);
			_browser.updateSelection(); // Resynch this project browser with the Selection manager-->Jtree will then be resynched with this
										// browser

			Enumeration<FlexoModelObject> en1 = expanded.elements();
			while (en1.hasMoreElements()) {
				FlexoModelObject o = en1.nextElement();
				_browser.expand(o, false);
			}
		} finally {
			_browser.resetIsRebuildingStructure();
			refreshRequested = false;
			repaintRequested = false;
		}
	}

	protected boolean refreshHasBeenRequested() {
		return refreshRequested;
	}

	// ==========================================================================
	// ======================= TreeNode implementation
	// ==========================
	// ==========================================================================

	@Override
	public TreeNode getChildAt(int childIndex) {
		if (childIndex < getChildCount()) {
			return _childs.get(childIndex);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Element " + getClass().getName() + " request for child at index " + childIndex + " out of bounds ("
						+ getChildCount() + ")");
			}
			return null;
		}
	}

	@Override
	public int getChildCount() {
		return _childs.size();
	}

	@Override
	public TreeNode getParent() {
		return _parent;
	}

	@Override
	public Enumeration<BrowserElement> children() {
		return _childs.elements();
	}

	public boolean contains(FlexoModelObject object) {
		for (BrowserElement e : _childs) {
			if (e.getObject() == object) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getIndex(TreeNode node) {
		int returned = 0;
		for (Enumeration<BrowserElement> e = children(); e.hasMoreElements(); returned++) {
			if (node == e.nextElement()) {
				return returned;
			}
		}
		return -1;
	}

	@Override
	public boolean getAllowsChildren() {
		return getChildCount() > 0;
	}

	@Override
	public boolean isLeaf() {
		return getChildCount() == 0;
	}

	@Override
	public String toString() {
		StringTokenizer st = new StringTokenizer(getClass().getName(), ".");
		String className = "";
		while (st.hasMoreTokens()) {
			className = st.nextToken();
		}
		return className + (isDeleted ? "(DELETED)[" : "[") + getObject() + "]";
		/*
		return getClass().getName() + (isDeleted ? "(DELETED)[" : "[") + getObject() + "]/"
		        + hashCode() + "/"
		        + ((_browser == null) ? "browser=null" : "browser=" + _browser.hashCode());*/
	}

	/**
	 * Always return false, must be overriden if used
	 * 
	 * @return
	 */
	public boolean isNameEditable() {
		return false;
	}

	/**
	 * Does nothing, must be overriden if used
	 * 
	 * @return
	 */
	public void setName(String aName) throws FlexoException {
		// Must be overriden if relevant
	}

	protected void _setName(String name) {
		// TODO: find an editor somewhere
		SetPropertyAction set = SetPropertyAction.actionType.makeNewAction(getObject(), null, null);
		set.setKey("name");
		set.setValue(name);
		set.doAction();
	}

	public boolean isSelectable() {
		return _browser.getSelectionController().isSelectable(_object);
	}

	public ProjectBrowser getProjectBrowser() {
		return _browser;
	}

	private boolean synchronizeExpansion = true;

	public void disableSynchronizeExpansion() {
		synchronizeExpansion = true;
	}

	public void enableSynchronizeExpansion() {
		synchronizeExpansion = false;
	}

	public boolean isSynchronizeExpansionEnabled() {
		return synchronizeExpansion;
	}

	protected BrowserElement findNearestAncestor(BrowserElementType... types) {
		if (_parent == null) {
			return null;
		}
		for (BrowserElementType t : types) {
			if (_parent._elementType == t) {
				return _parent;
			}
		}
		return _parent.findNearestAncestor(types);
	}

}
