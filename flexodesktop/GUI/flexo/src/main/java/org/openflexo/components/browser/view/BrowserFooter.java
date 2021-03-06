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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import org.openflexo.FlexoCst;
import org.openflexo.ch.FCH;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.components.browser.ElementTypeBrowserFilter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.icon.IconLibrary;
import org.openflexo.selection.ContextualMenuManager;
import org.openflexo.swing.ImageButton;

public class BrowserFooter extends JPanel implements MouseListener, WindowListener {

	protected static final Logger logger = Logger.getLogger(BrowserFooter.class.getPackage().getName());

	protected BrowserView _browserView;

	protected JButton plusButton;

	protected JButton minusButton;

	protected JButton optionsButton;

	protected JPopupMenu popupMenu = null;

	public BrowserFooter(BrowserView browserView) {
		super();
		_browserView = browserView;
		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout());
		// setPreferredSize(new
		// Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH,FlexoCst.MINIMUM_BROWSER_CONTROL_PANEL_HEIGHT));
		setPreferredSize(new Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH, 20));

		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());

		plusButton = new ImageButton(IconLibrary.BROWSER_PLUS_ICON);
		plusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultiplePlusActions()) {
					BrowserFooter.this.<FlexoAction, FlexoModelObject, FlexoModelObject> plusPressed(e);
					plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
				}
			}

		});
		plusButton.setDisabledIcon(IconLibrary.BROWSER_PLUS_DISABLED_ICON);
		// plusButton.setSelectedIcon(FlexoCst.BROWSER_PLUS_SELECTED_ICON);
		plusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(IconLibrary.BROWSER_PLUS_SELECTED_ICON);
				}
				if (hasMultiplePlusActions()) {
					BrowserFooter.this.<FlexoAction, FlexoModelObject, FlexoModelObject> getPlusActionMenu().show(
							mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
				}
				if (hasMultiplePlusActions()) {
					BrowserFooter.this.<FlexoAction, FlexoModelObject, FlexoModelObject> getPlusActionMenu().show(
							mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});
		FCH.setHelpItem(plusButton, "plus");

		minusButton = new ImageButton(IconLibrary.BROWSER_MINUS_ICON);
		minusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Multiple DELETE ACTION popup menu not implemented yet
				// If you need it, do the same as for ADD ACTION
				BrowserFooter.this.<FlexoAction, FlexoModelObject, FlexoModelObject> minusPressed(e);
				minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
			}

		});
		minusButton.setDisabledIcon(IconLibrary.BROWSER_MINUS_DISABLED_ICON);
		// minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);
		minusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(IconLibrary.BROWSER_MINUS_SELECTED_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
				}
			}
		});
		FCH.setHelpItem(minusButton, "minus");

		plusMinusPanel.add(plusButton);
		plusMinusPanel.add(minusButton);

		add(plusMinusPanel, BorderLayout.WEST);
		optionsButton = new ImageButton(IconLibrary.BROWSER_OPTIONS_ICON);
		if (browserView.getBrowser().showOptionsButton()) {
			optionsButton.setDisabledIcon(IconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
			add(optionsButton, BorderLayout.EAST);

			optionsButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent mouseEvent) {
					if (hasFilters()) {
						optionsButton.setIcon(IconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
						getPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					}
				}

				@Override
				public void mouseReleased(MouseEvent mouseEvent) {
					if (hasFilters() && (popupMenu == null || !popupMenu.isVisible())) {
						getPopupMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
						getPopupMenu().grabFocus();
					}
				}
			});

			// if (browserView.getBrowser().getOptionalFilters().size() == 0)
			optionsButton.setEnabled(hasFilters());
			FCH.setHelpItem(optionsButton, "options");

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Browser " + browserView.getBrowser() + " has "
						+ browserView.getBrowser().getConfigurableElementTypeFilters().size() + " filters");
			}
		}

		handleSelectionCleared();

		validate();
	}

	public void handleOptionalFilterAdded() {
		optionsButton.setEnabled(hasFilters());
		closePopup();
	}

	protected boolean hasFilters() {
		return _browserView.getBrowser().getConfigurableElementTypeFilters().size() + _browserView.getBrowser().getCustomFilters().size() > 0;
	}

	/* protected void elementTypeFilterChanged()
	 {
	 	if (popupMenu != null) {
	 		popupMenu.setVisible(false);
	 	}
	 	popupMenu = null;
	     optionsButton.setEnabled(hasFilters());
	}*/

	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = makePopupMenu();
		}
		for (Component menuItem : popupMenu.getComponents()) {
			if (menuItem instanceof BrowserFilterMenuItem) {
				((BrowserFilterMenuItem) menuItem).update();
			}
		}
		return popupMenu;
	}

	private JPopupMenu makePopupMenu() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Browser "
					+ _browserView.getBrowser()
					+ " has now "
					+ (_browserView.getBrowser().getConfigurableElementTypeFilters().size() + _browserView.getBrowser().getCustomFilters()
							.size()) + " filters");
		}

		JPopupMenu returned = new JPopupMenu() {
			@Override
			public void setVisible(boolean b) {
				if (b && !isVisible()) {
					addPopupClosers(getWindow(BrowserFooter.this));
				} else if (!b && isVisible()) {
					removePopupClosers(getWindow(BrowserFooter.this));
				}
				super.setVisible(b);
				if (!b) {
					optionsButton.setIcon(IconLibrary.BROWSER_OPTIONS_ICON);
				}
			}

			@Override
			public void menuSelectionChanged(boolean isIncluded) {
				super.menuSelectionChanged(true);
			}
		};

		if (_browserView.getBrowser().getCustomFilters().size() > 0) {
			for (CustomBrowserFilter filter : _browserView.getBrowser().getCustomFilters()) {
				returned.add(new BrowserFilterMenuItem(_browserView.getBrowser(), filter));
			}
		}

		if (_browserView.getBrowser().getConfigurableElementTypeFilters().size() > 0) {
			if (_browserView.getBrowser().getCustomFilters().size() > 0) {
				returned.addSeparator();
			}
			for (ElementTypeBrowserFilter filter : _browserView.getBrowser().getConfigurableElementTypeFilters()) {
				returned.add(new BrowserFilterMenuItem(_browserView.getBrowser(), filter));
			}
		}

		returned.addMenuKeyListener(new MenuKeyListener() {

			@Override
			public void menuKeyPressed(MenuKeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && popupMenu != null && popupMenu.isVisible()) {
					closePopup();
				}
			}

			@Override
			public void menuKeyReleased(MenuKeyEvent e) {
			}

			@Override
			public void menuKeyTyped(MenuKeyEvent e) {
			}

		});
		return returned;
	}

	protected void closePopup() {
		if (popupMenu != null && popupMenu.isVisible()) {
			popupMenu.setVisible(false);
		}
		popupMenu = null;
	}

	/**
	 * Added mouselistners to each component of the root container c, exept this button, and the calendar popup, because mouseclicks in them
	 * are not supposed to clsoe the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	protected void addPopupClosers(Container c) {
		if (c == getWindow(this) && c != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("addPopupClosers");
			}
			((Window) c).addWindowListener(this);
		}
		if (c != popupMenu && c != null) {
			c.addMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				addPopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	/**
	 * Added mouselistners to each component of the root container c, exept this button, and the calendar popup, because mouseclicks in them
	 * are not supposed to clsoe the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	protected void removePopupClosers(Container c) {
		if (c instanceof Window) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("removePopupClosers");
			}
			((Window) c).removeWindowListener(this);
		}
		if (c != popupMenu && c != null) {
			c.removeMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				removePopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	/**
	 * Copied directly from BasicPopupMenuUI - PK 06-08-04
	 * 
	 * @param c
	 *            componenet of which we want to find the owning window
	 * @return the window that is contins after plenty of leves the component c
	 */
	protected Window getWindow(Component c) {
		Component w = c;
		while (!(w instanceof Window) && w != null) {
			w = w.getParent();
		}
		return (Window) w;
	}

	protected void handleSelectionChanged() {
		FlexoModelObject focusedObject = getFocusedObject();
		Vector globalSelection = buildGlobalSelection();
		plusButton.setEnabled(_browserView.getController() != null && focusedObject != null
				&& getActionTypesWithAddType(focusedObject, (Vector) null).size() > 0);
		minusButton.setEnabled(_browserView.getController() != null && focusedObject != null
				&& getActionTypesWithDeleteType(focusedObject, globalSelection).size() > 0);
		plusActionMenuNeedsRecomputed = true;
	}

	private <A2 extends FlexoAction<A2, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> List<FlexoActionType<A2, T1, T2>> getActionTypesWithAddType(
			FlexoModelObject focusedObject, Vector<? extends FlexoModelObject> globalSelection) {
		return _browserView.getContextualMenuManager().getActionTypesWithAddType(focusedObject, globalSelection);
	}

	private <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> List<FlexoActionType<A, T1, T2>> getActionTypesWithDeleteType(
			FlexoModelObject focusedObject, Vector<? extends FlexoModelObject> globalSelection) {
		return _browserView.getContextualMenuManager().getActionTypesWithDeleteType(focusedObject, globalSelection);

	}

	protected void handleSelectionCleared() {
		plusButton.setEnabled(false);
		minusButton.setEnabled(false);
		plusActionMenuNeedsRecomputed = true;
	}

	<A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void plusPressed(ActionEvent e) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Pressed on plus");
		}
		FlexoActionType<A, T1, T2> actionType = this.<A, T1, T2> getActionTypesWithAddType(getFocusedObject(),
				(Vector<FlexoModelObject>) null).get(0);
		if (getEditor() != null) {
			getEditor().performActionType(actionType, (T1) getFocusedObject(), (Vector<T2>) getGlobalSelection(), e);
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("No editor available. Ignoring action " + actionType);
		}
	}

	protected FlexoEditor getEditor() {
		return _browserView.getEditor();
	}

	boolean hasMultiplePlusActions() {
		if (getFocusedObject() == null) {
			return false;
		}
		return getActionTypesWithAddType(getFocusedObject(), (Vector) null).size() > 1;
	}

	private JPopupMenu plusActionMenu = null;

	private boolean plusActionMenuNeedsRecomputed = true;

	<A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> JPopupMenu getPlusActionMenu() {
		if (_browserView.getContextualMenuManager() != null) {
			return _browserView.getContextualMenuManager().makePopupMenu(getFocusedObject(), new ContextualMenuManager.MenuFilter() {

				@Override
				public boolean acceptActionType(FlexoActionType<?, ?, ?> actionType) {
					return actionType.getActionCategory() == FlexoActionType.ADD_ACTION_TYPE;
				}
			});
		}
		if (plusActionMenuNeedsRecomputed) {
			plusActionMenu = new JPopupMenu();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build plus menu");
			}
			for (final FlexoActionType<A, T1, T2> action : this.<A, T1, T2> getActionTypesWithAddType(getFocusedObject(),
					getGlobalSelection())) {
				JMenuItem menuItem = new JMenuItem(action.getLocalizedName());
				if (getEditor().getEnabledIconFor(action) != null) {
					menuItem.setIcon(getEditor().getEnabledIconFor(action));
				}
				if (getEditor().getDisabledIconFor(action) != null) {
					menuItem.setDisabledIcon(getEditor().getDisabledIconFor(action));
				}
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getEditor().performActionType(action, (T1) getFocusedObject(), (Vector<T2>) getGlobalSelection(), e);
					}
				});
				plusActionMenu.add(menuItem);
			}
			plusActionMenuNeedsRecomputed = false;
		}
		return plusActionMenu;
	}

	<A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void minusPressed(ActionEvent e) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Pressed on plus");
		}
		Vector globalSelection = buildGlobalSelection();
		FlexoActionType<A, T1, T2> actionType = this.<A, T1, T2> getActionTypesWithDeleteType(getFocusedObject(), globalSelection).get(0);
		if (getEditor() != null) {
			getEditor().performActionType(actionType, (T1) getFocusedObject(), (Vector<T2>) getGlobalSelection(), e);
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("No editor available. Ignoring action " + actionType);
		}
	}

	/*
	 * private boolean hasMultipleMinusActions() { Vector globalSelection =
	 * buildGlobalSelection(); return
	 * getActionTypesWithDeleteType(getFocusedObject(),globalSelection).size() >
	 * 1; }
	 */

	/**
	 * Returns focused object, considering focused object is the last selected object. If none object are selected, return null.
	 */
	public FlexoModelObject getFocusedObject() {
		return _browserView.getFocusedObject();
	}

	public Vector<FlexoModelObject> getGlobalSelection() {
		return buildGlobalSelection();
	}

	private Vector<FlexoModelObject> buildGlobalSelection() {
		Vector<FlexoModelObject> returned = new Vector<FlexoModelObject>();
		Vector<BrowserElement> elements = _browserView.getSelectedElements();
		for (Enumeration<BrowserElement> en = elements.elements(); en.hasMoreElements();) {
			BrowserElement next = en.nextElement();
			returned.add(next.getObject());
		}
		return returned;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() != popupMenu && e.getSource() != optionsButton) {
			closePopup();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closePopup();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		closePopup();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		closePopup();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
