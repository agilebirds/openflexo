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
package org.openflexo.selection;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.openflexo.AdvancedPrefs;
import org.openflexo.FlexoCst;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.components.tabular.TabularView;
import org.openflexo.components.tabularbrowser.JTreeTable;
import org.openflexo.components.tabularbrowser.TabularBrowserView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.ActionMenu;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.action.ActionSchemeActionType;
import org.openflexo.foundation.view.action.NavigationSchemeActionType;
import org.openflexo.foundation.view.diagram.viewpoint.NavigationScheme;
import org.openflexo.foundation.viewpoint.ActionScheme;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.action.EditionAction;

public abstract class ContextualMenuManager {

	protected static final Logger logger = Logger.getLogger(ContextualMenuManager.class.getPackage().getName());

	private static final MenuFilter ALL = new MenuFilter() {

		@Override
		public boolean acceptActionType(FlexoActionType<?, ?, ?> actionType) {
			return true;
		}
	};

	private final SelectionManager _selectionManager;

	private boolean _isPopupMenuDisplayed = false;

	private boolean _isPopupTriggering = false;

	private Component _invoker = null;

	private final FlexoController controller;

	public ContextualMenuManager(SelectionManager selectionManager, FlexoController controller) {
		super();
		_selectionManager = selectionManager;
		this.controller = controller;
	}

	public FlexoEditor getEditor() {
		return controller.getEditor();
	}

	public void processMousePressed(MouseEvent e) {
		resetContextualMenuTriggering();
		_isPopupTriggering = e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3;

		if (e.getSource() instanceof Component) {
			_invoker = (Component) e.getSource();
			if (_isPopupTriggering) {
				FlexoObject o = getFocusedObject(_invoker, e);
				if (o == null) {
					return;
				}
				// Return now when no selection manager defined
				if (_selectionManager == null) {
					return;
				}

				if (_selectionManager.selectionContains(o)) {
					e.consume();
					return;
				} else {

					boolean isCtrlDown = (e.getModifiersEx() & FlexoCst.MULTI_SELECTION_MASK) == FlexoCst.MULTI_SELECTION_MASK;

					// BMA : mega hack
					// le probleme : sous OSX : click-droit renvoie e.isMetaDown==true
					// le symptaume : multiple-select dans IE lorsqu'on a une selection et que l'on click-droit sur un widget
					// si qqun veut un jour modifier ce hack il faut :
					// 1) verifier qu'on ne refait pas de multiple-select dans IE
					// 2) vérifier que le multiple-select fonctionne dans le browserr du GC pour notament faire un "compare with each other"
					// avec un seul bouton : la manip a faire est
					// click sur file1
					// pomme+click sur file2
					// pomme+ctrl+click sur file1 ou file2 pour avoir le menu contextuel avec l'item "compare with each other"

					if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
						isCtrlDown = e.getButton() == MouseEvent.BUTTON1 && e.isMetaDown();
					}
					// fin du mega hack

					if (isCtrlDown) {
						_selectionManager.addToSelected(o);
					} else {
						_selectionManager.setSelectedObject(o);
					}
				}
			}
		}
	}

	public void processMouseReleased(MouseEvent e) {
		_isPopupTriggering = _isPopupTriggering || e.isPopupTrigger();

		if (_isPopupTriggering) {
			if (e.getSource() == _invoker /* && (hasSelection()) */) {
				displayPopupMenu((Component) e.getSource(), e);
				e.consume();
				resetContextualMenuTriggering();
			}
		}
	}

	public void processMouseMoved(MouseEvent e) {
		if (_isPopupMenuDisplayed && AdvancedPrefs.getCloseOnMouseOut()) {
			Rectangle menuBounds = _popupMenu.getBounds();
			menuBounds.width = menuBounds.width + 40;
			menuBounds.height = menuBounds.height + 40;
			menuBounds.x = menuBounds.x - 20;
			menuBounds.y = menuBounds.y - 20;
			if (e.getSource() instanceof Component) {
				Point pointRelatingToMenu = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), _popupMenu);
				// logger.info ("event="+e);
				// logger.info ("pointRelatingToMenu="+pointRelatingToMenu);
				if (!menuBounds.contains(pointRelatingToMenu)) {
					// Mouse leaves menu
					hidePopupMenu();
				}
			}
		}
	}

	public void resetContextualMenuTriggering() {
		_isPopupTriggering = false;
		_invoker = null;
	}

	// ==========================================================================
	// ============================= Filters ===================================
	// ==========================================================================

	public boolean acceptAction(FlexoActionType<?, ?, ?> action) {
		// override this method to exclude some actions.
		return true;
	}

	@SuppressWarnings("unchecked")
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoObject, T2 extends FlexoObject> List<FlexoActionType<A, T1, T2>> getActionTypesWithAddType(
			FlexoObject focusedObject, Vector<? extends FlexoObject> globalSelection) {
		List<FlexoActionType<A, T1, T2>> returned = new ArrayList<FlexoActionType<A, T1, T2>>();
		if (getEditor() == null) {
			return returned;
		}
		for (FlexoActionType<?, ?, ?> actionType : focusedObject.getActionList()) {
			if (TypeUtils.isAssignableTo(focusedObject, actionType.getFocusedObjectType())
					&& (globalSelection == null || TypeUtils.isAssignableTo(globalSelection, actionType.getGlobalSelectionType()))) {
				FlexoActionType<A, T1, T2> cast = (FlexoActionType<A, T1, T2>) actionType;
				if (cast.getActionCategory() == FlexoActionType.ADD_ACTION_TYPE) {
					if (getEditor().isActionVisible(cast, (T1) focusedObject, (Vector<T2>) globalSelection)) {
						if (getEditor().isActionEnabled(cast, (T1) focusedObject, (Vector<T2>) globalSelection)) {
							returned.add(cast);
						}
					}
				}
			}
		}
		return returned;
	}

	@SuppressWarnings("unchecked")
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoObject, T2 extends FlexoObject> List<FlexoActionType<A, T1, T2>> getActionTypesWithDeleteType(
			FlexoObject focusedObject, Vector<? extends FlexoObject> globalSelection) {
		List<FlexoActionType<A, T1, T2>> returned = new ArrayList<FlexoActionType<A, T1, T2>>();
		if (getEditor() == null) {
			return returned;
		}
		for (FlexoActionType<?, ?, ?> actionType : focusedObject.getActionList()) {
			if (TypeUtils.isAssignableTo(focusedObject, actionType.getFocusedObjectType())
					&& (globalSelection == null || TypeUtils.isAssignableTo(globalSelection, actionType.getGlobalSelectionType()))) {
				@SuppressWarnings("unchecked")
				FlexoActionType<A, T1, T2> cast = (FlexoActionType<A, T1, T2>) actionType;
				if (cast.getActionCategory() == FlexoActionType.DELETE_ACTION_TYPE) {
					if (getEditor().isActionVisible(cast, (T1) focusedObject, (Vector<T2>) globalSelection)) {
						if (getEditor().isActionEnabled(cast, (T1) focusedObject, (Vector<T2>) globalSelection)) {
							returned.add(cast);
						}
					}
				}
			}
		}
		return returned;

	}

	// ==========================================================================
	// ============================= Jpopup ===================================
	// ==========================================================================

	public void displayPopupMenu(Component invoker, MouseEvent e) {
		_invoker = invoker;
		if (getFocusedObject(invoker, e) != null) {
			makePopupMenu(invoker, e);
			// if (hasSelection())
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("displayPopupMenu() for  " + getFocusedObject(invoker, e));
			}
			if (_popupMenu.getComponentCount() > 0) {
				_popupMenu.show(_invoker, e.getPoint().x, e.getPoint().y);
				_isPopupMenuDisplayed = true;
			} else {
				resetContextualMenuTriggering();
			}
		}
	}

	public void hidePopupMenu() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("hidePopupMenu()");
		}
		resetContextualMenuTriggering();
		_popupMenu.setVisible(false);
		_isPopupMenuDisplayed = false;
		_popupMenu = null;
	}

	private JPopupMenu makePopupMenu(Component invoker, MouseEvent e) {
		_popupMenu = null;
		return makePopupMenu(getFocusedObject(invoker, e), ALL);
	}

	public JPopupMenu makePopupMenu(FlexoObject focusedObject, MenuFilter filter) {
		if (focusedObject != null) {
			ContextualMenu contextualMenu = new ContextualMenu();
			for (FlexoActionType next : focusedObject.getActionList()) {
				if (filter.acceptActionType(next)
						&& getEditor().isActionVisible(next, focusedObject,
								_selectionManager != null ? _selectionManager.getSelection() : null)) {
					contextualMenu.putAction(next);
				}
			}
			if (focusedObject instanceof FlexoModelObject) {
				if (((FlexoModelObject) focusedObject).getEditionPatternReferences() != null) {
					for (FlexoModelObjectReference<EditionPatternInstance> ref : ((FlexoModelObject) focusedObject)
							.getEditionPatternReferences()) {
						EditionPatternInstance epi = ref.getObject();
						if (epi != null && epi.getEditionPattern() != null && epi.getEditionPattern().hasActionScheme()) {
							for (ActionScheme as : epi.getEditionPattern().getActionSchemes()) {
								contextualMenu.putAction(new ActionSchemeActionType(as, epi));
							}
						}
					}
					for (FlexoModelObjectReference<EditionPatternInstance> ref : ((FlexoModelObject) focusedObject)
							.getEditionPatternReferences()) {
						EditionPatternInstance epi = ref.getObject();
						if (epi != null && epi.getEditionPattern() != null && epi.getEditionPattern().hasNavigationScheme()) {
							for (NavigationScheme ns : epi.getEditionPattern().getNavigationSchemes()) {
								contextualMenu.putAction(new NavigationSchemeActionType(ns, epi));
							}
						}
					}
				}
			}
			_popupMenu = contextualMenu.makePopupMenu(focusedObject);
		} else {
			_popupMenu = new JPopupMenu();
		}
		return _popupMenu;
	}

	public void showPopupMenuForObject(FlexoObject focusedObject, Component invoker, Point position) {
		_invoker = invoker;
		if (focusedObject != null) {
			makePopupMenu(focusedObject, ALL);
			// if (hasSelection())
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("displayPopupMenu() for  " + focusedObject);
			}
			if (_popupMenu.getComponentCount() > 0) {
				_popupMenu.show(_invoker, position.x, position.y);
				_isPopupMenuDisplayed = true;
			} else {
				resetContextualMenuTriggering();
			}
		}
	}

	protected class ContextualMenu extends Hashtable<ActionGroup, ContextualMenuGroup> {
		private final Hashtable<ActionMenu, ContextualSubMenu> _subMenus = new Hashtable<ActionMenu, ContextualSubMenu>();

		public Enumeration<ContextualMenuGroup> orderedGroups() {
			Vector<ContextualMenuGroup> orderedGroups = new Vector<ContextualMenuGroup>(values());
			Collections.sort(orderedGroups, new Comparator<ContextualMenuGroup>() {
				@Override
				public int compare(ContextualMenuGroup o1, ContextualMenuGroup o2) {
					return o1.getActionGroup().getIndex() - o2.getActionGroup().getIndex();
				}
			});
			return orderedGroups.elements();
		}

		public void putAction(FlexoActionType actionType) {
			if (acceptAction(actionType)) {
				if (actionType.getActionMenu() != null) {
					ContextualSubMenu subMenu = _subMenus.get(actionType.getActionMenu());
					if (subMenu == null) {
						subMenu = new ContextualSubMenu(actionType.getActionMenu());
						addSubMenu(subMenu);
						_subMenus.put(actionType.getActionMenu(), subMenu);
					}
					subMenu.addAction(actionType);
				} else {
					addAction(actionType);
				}
			}
		}

		public void addAction(FlexoActionType actionType) {
			if (acceptAction(actionType)) {
				ContextualMenuGroup contextualMenuGroup = get(actionType.getActionGroup());
				if (contextualMenuGroup == null) {
					contextualMenuGroup = new ContextualMenuGroup(actionType.getActionGroup());
					put(actionType.getActionGroup(), contextualMenuGroup);
				}
				contextualMenuGroup.addAction(actionType);
			}
		}

		public void addSubMenu(ContextualSubMenu subMenu) {
			ContextualMenuGroup contextualMenuGroup = get(subMenu.getActionMenu().getActionGroup());
			if (contextualMenuGroup == null) {
				contextualMenuGroup = new ContextualMenuGroup(subMenu.getActionMenu().getActionGroup());
				put(subMenu.getActionMenu().getActionGroup(), contextualMenuGroup);
			}
			contextualMenuGroup.addSubMenu(subMenu);
		}

		public JPopupMenu makePopupMenu(FlexoObject focusedObject) {

			boolean addSeparator = false;
			JPopupMenu returned = new JPopupMenu();
			for (Enumeration<ContextualMenuGroup> en = orderedGroups(); en.hasMoreElements();) {
				ContextualMenuGroup menuGroup = en.nextElement();
				if (addSeparator) {
					returned.addSeparator();
					// System.out.println("------- Ajout de separator -------");
				}
				addSeparator = true;
				// System.out.println("============= Groupe
				// "+menuGroup._actionGroup.getLocalizedName());
				for (Enumeration en2 = menuGroup.elements(); en2.hasMoreElements();) {
					Object nextElement = en2.nextElement();
					if (nextElement instanceof FlexoActionType) {
						// System.out.println("Ajout de "+nextElement);
						makeMenuItem((FlexoActionType) nextElement, focusedObject, returned);
					} else if (nextElement instanceof ContextualSubMenu) {
						// System.out.println("Ajout de "+nextElement);
						JMenuItem item = ((ContextualSubMenu) nextElement).makeMenu(focusedObject);
						returned.add(item);
					}
				}
			}
			return returned;
		}

	}

	protected class ContextualMenuGroup extends Vector {
		private final ActionGroup _actionGroup;

		public ContextualMenuGroup(ActionGroup actionGroup) {
			_actionGroup = actionGroup;
		}

		public void addAction(FlexoActionType actionType) {
			// should have already been checked, but it's more secure.
			if (acceptAction(actionType)) {
				add(actionType);
			}
		}

		public void addSubMenu(ContextualSubMenu subMenu) {
			add(subMenu);
		}

		public ActionGroup getActionGroup() {
			return _actionGroup;
		}
	}

	protected class ContextualSubMenu extends ContextualMenu {
		private final ActionMenu _actionMenu;

		public ContextualSubMenu(ActionMenu actionMenu) {
			_actionMenu = actionMenu;
		}

		public ActionMenu getActionMenu() {
			return _actionMenu;
		}

		public JMenu makeMenu(FlexoObject focusedObject) {
			boolean addSeparator = false;
			JMenu returned = new JMenu();
			returned.setText(getActionMenu().getLocalizedName());
			if (getActionMenu().getSmallIcon() != null) {
				returned.setIcon(getActionMenu().getSmallIcon());
			}
			for (Enumeration en = orderedGroups(); en.hasMoreElements();) {
				ContextualMenuGroup menuGroup = (ContextualMenuGroup) en.nextElement();
				if (addSeparator) {
					returned.addSeparator();
				}
				addSeparator = true;
				for (Enumeration en2 = menuGroup.elements(); en2.hasMoreElements();) {
					Object nextElement = en2.nextElement();
					if (nextElement instanceof FlexoActionType) {
						makeMenuItem((FlexoActionType) nextElement, focusedObject, returned);
					}
				}
			}
			return returned;
		}

	}

	public interface MenuFilter {
		public boolean acceptActionType(FlexoActionType<?, ?, ?> actionType);
	}

	private <A extends FlexoAction<A, T1, T2>, T1 extends FlexoObject, T2 extends FlexoObject> JMenuItem makeMenuItem(
			FlexoActionType<A, T1, T2> actionType, FlexoObject focusedObject, JPopupMenu menu) {
		try {
			Vector<T2> globalSelection = new Vector<T2>();
			if (_selectionManager != null) {
				for (FlexoObject o : _selectionManager.getSelection()) {
					try {
						globalSelection.add((T2) o);
					} catch (ClassCastException e) {
						// This is not good type, discard this object
						logger.warning("Discard from selection " + o);
					}
				}
			}
			EditionAction<A, T1, T2> action = new EditionAction<A, T1, T2>(actionType, (T1) focusedObject, globalSelection, getEditor());
			JMenuItem item = menu.add(action);
			item.setText(actionType.getLocalizedName());
			if (getEditor().getKeyStrokeFor(actionType) != null) {
				item.setAccelerator(getEditor().getKeyStrokeFor(actionType));
			}
			if (getEditor().getEnabledIconFor(actionType) != null) {
				item.setIcon(getEditor().getEnabledIconFor(actionType));
			}
			if (getEditor().getDisabledIconFor(actionType) != null) {
				item.setDisabledIcon(getEditor().getDisabledIconFor(actionType));
			}
			return item;
		} catch (ClassCastException exception) {
			logger.warning("ClassCastException raised while trying to build FlexoAction " + actionType + " Exception: "
					+ exception.getMessage());
			return null;
		}
	}

	<A extends FlexoAction<A, T1, T2>, T1 extends FlexoObject, T2 extends FlexoObject> JMenuItem makeMenuItem(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, JMenu menu) {
		try {
			EditionAction<A, T1, T2> action = new EditionAction<A, T1, T2>(actionType, focusedObject,
					_selectionManager != null ? (Vector<T2>) _selectionManager.getSelection() : null, getEditor());
			JMenuItem item = menu.add(action);

			item.setText(actionType.getLocalizedName());
			if (getEditor().getKeyStrokeFor(actionType) != null) {
				item.setAccelerator(getEditor().getKeyStrokeFor(actionType));
			}
			if (getEditor().getEnabledIconFor(actionType) != null) {
				item.setIcon(getEditor().getEnabledIconFor(actionType));
			}
			if (getEditor().getDisabledIconFor(actionType) != null) {
				item.setDisabledIcon(getEditor().getDisabledIconFor(actionType));
			}
			return item;
		} catch (ClassCastException exception) {
			logger.warning("ClassCastException raised while trying to build FlexoAction " + actionType + " Exception: "
					+ exception.getMessage());
			return null;
		}
	}

	private JPopupMenu _popupMenu;

	public boolean isPopupMenuDisplayed() {
		return _isPopupMenuDisplayed;
	}

	public FlexoObject getFocusedObject(Component focusedComponent, MouseEvent e) {
		// Try to handle TabularBrowserView
		if (e.getSource() instanceof JTreeTable) {
			Component c = (Component) e.getSource();
			while (c != null && !(c instanceof TabularBrowserView)) {
				c = c.getParent();
			}
			if (c != null && c instanceof TabularBrowserView) {
				return ((TabularBrowserView) c).getFocusedObject();
			}
		}
		// Try to handle TabularView
		if (e.getSource() instanceof JTable) {
			Component c = (Component) e.getSource();
			while (c != null && !(c instanceof TabularView)) {
				c = c.getParent();
			}
			if (c != null && c instanceof TabularView) {
				return ((TabularView) c).getFocusedObject();
			}

		}
		// Finally handle browsers
		if (e.getSource() instanceof JTree) {
			Component c = (Component) e.getSource();
			while (c != null && !(c instanceof BrowserView)) {
				c = c.getParent();
			}
			if (c instanceof BrowserView) {
				TreePath path = ((BrowserView) c).getTreeView().getClosestPathForLocation(e.getX(), e.getY());
				if (path != null) {
					return ((BrowserElement) path.getLastPathComponent()).getObject();
				}
				return ((BrowserView) c).getSelectedObject();
			}

		}
		return null;
	}

}
