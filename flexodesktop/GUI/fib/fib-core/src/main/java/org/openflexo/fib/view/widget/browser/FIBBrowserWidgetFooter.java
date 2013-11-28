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
package org.openflexo.fib.view.widget.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ImageButton;

public class FIBBrowserWidgetFooter<T> extends JPanel implements MouseListener, WindowListener {

	protected static final Logger logger = Logger.getLogger(FIBBrowserWidgetFooter.class.getPackage().getName());

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	protected FIBBrowserWidget<T> _widget;

	// protected JPopupMenu popupMenu = null;

	private Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>> _addActions;
	private Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>> _removeActions;
	private Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>> _otherActions;

	private class BrowserButton implements ActionListener {
		private Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>> actions;
		private JButton button;

		public BrowserButton(Map<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>> actions, Icon icon, Icon disabledIcon,
				Icon pressedIcon) {
			super();
			this.actions = actions;
			button = new ImageButton(icon);
			button.setDisabledIcon(disabledIcon);
			button.setPressedIcon(pressedIcon);
			button.addActionListener(this);
		}

		public JButton getButton() {
			return button;
		}

		public void handleSelectionChanged(Object selection) {
			boolean active = true;
			if (selection != null) {
				FIBBrowserElement element = elementForObject(selection);
				if (element != null) {
					Map<FIBBrowserAction, FIBBrowserActionListener> browserActions = actions.get(element);
					int activeActionCount = 0;
					if (browserActions != null && browserActions.size() > 0) {
						for (Entry<FIBBrowserAction, FIBBrowserActionListener> e : browserActions.entrySet()) {
							if (e.getValue().isActive(selection)) {
								activeActionCount++;
							}
						}
					}
					active = activeActionCount > 0;
				} else {
					active = false;
				}
			} else {
				active = false;
			}
			button.setEnabled(active);
		}

		private FIBBrowserElement elementForObject(Object object) {
			if (object == null) {
				return null;
			} else {
				return _widget.getComponent().elementForClass(object.getClass());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			FIBBrowserElement element = elementForObject(_widget.getSelectedObject());
			if (element != null) {
				List<FIBBrowserActionListener> listeners = new ArrayList<FIBBrowserActionListener>();
				Map<FIBBrowserAction, FIBBrowserActionListener> browserActions = actions.get(element);
				if (browserActions != null && browserActions.size() > 0) {
					for (Entry<FIBBrowserAction, FIBBrowserActionListener> entry : browserActions.entrySet()) {
						if (entry.getValue().isActive(_widget.getSelectedObject())) {
							listeners.add(entry.getValue());
						}
					}
				}
				if (listeners.size() == 1) {
					listeners.get(0).setSelectedObject(_widget.getSelectedObject());
					listeners.get(0).actionPerformed(e);
				} else if (listeners.size() > 1) {
					JPopupMenu popupMenu = new JPopupMenu();
					for (FIBBrowserActionListener actionListener : listeners) {
						actionListener.setSelectedObject(_widget.getSelectedObject());
						JMenuItem menuItem = new JMenuItem(getLocalized(actionListener.getBrowserAction().getName()));
						menuItem.addActionListener(actionListener);
						popupMenu.add(menuItem);
					}
					popupMenu.setInvoker(button);
					Point location = button.getLocationOnScreen();
					location.y -= popupMenu.getPreferredSize().getHeight();
					popupMenu.setLocation(location);
					popupMenu.setVisible(true);
				}
			}
		}
	}

	private BrowserButton plusButton;
	private BrowserButton minusButton;
	private BrowserButton optionsButton;
	private JButton filtersButton;

	/**
	 * Stores controls: key is the JButton and value the FIBTableActionListener
	 */
	// private Hashtable<JButton,FIBTableActionListener> _controls;

	public FIBBrowserWidgetFooter(FIBBrowserWidget widget) {
		super();
		setOpaque(false);
		_widget = widget;

		initializeActions(widget);

		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout());

		plusButton = new BrowserButton(_addActions, FIBIconLibrary.BROWSER_PLUS_ICON, FIBIconLibrary.BROWSER_PLUS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
		minusButton = new BrowserButton(_removeActions, FIBIconLibrary.BROWSER_MINUS_ICON, FIBIconLibrary.BROWSER_MINUS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
		optionsButton = new BrowserButton(_otherActions, FIBIconLibrary.BROWSER_OPTIONS_ICON, FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON,
				FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);

		filtersButton = new ImageButton(FIBIconLibrary.BROWSER_FILTERS_ICON);
		filtersButton.setDisabledIcon(FIBIconLibrary.BROWSER_FILTERS_DISABLED_ICON);
		filtersButton.setPressedIcon(FIBIconLibrary.BROWSER_FILTERS_SELECTED_ICON);
		filtersButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point point = filtersButton.getLocationOnScreen();
				JPopupMenu popupMenu = getFiltersPopupMenu();
				popupMenu.pack();
				point.y -= popupMenu.getHeight();
				popupMenu.setInvoker(filtersButton);
				popupMenu.setLocation(point);
				popupMenu.setVisible(true);
			}
		});
		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.add(plusButton.getButton());
		plusMinusPanel.add(minusButton.getButton());
		plusMinusPanel.setOpaque(false);
		JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		optionsPanel.add(optionsButton.getButton());
		optionsPanel.setOpaque(false);
		if (hasFilters()) {
			optionsPanel.add(filtersButton);
		}

		add(plusMinusPanel, BorderLayout.WEST);
		add(optionsPanel, BorderLayout.EAST);

		revalidate();
	}

	private void initializeActions(FIBBrowserWidget<T> widget) {
		_addActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>>();
		_removeActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>>();
		_otherActions = new LinkedHashMap<FIBBrowserElement, Map<FIBBrowserAction, FIBBrowserActionListener>>();

		for (FIBBrowserElement element : widget.getComponent().getElements()) {

			Map<FIBBrowserAction, FIBBrowserActionListener> addActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener>();
			Map<FIBBrowserAction, FIBBrowserActionListener> removeActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener>();
			Map<FIBBrowserAction, FIBBrowserActionListener> otherActions = new LinkedHashMap<FIBBrowserAction, FIBBrowserActionListener>();

			for (FIBBrowserAction plAction : element.getActions()) {
				FIBBrowserActionListener plActionListener = new FIBBrowserActionListener(_widget, plAction);
				if (plActionListener.isAddAction()) {
					addActions.put(plAction, plActionListener);
				} else if (plActionListener.isRemoveAction()) {
					removeActions.put(plAction, plActionListener);
				} else if (plActionListener.isCustomAction()) {
					otherActions.put(plAction, plActionListener);
				}
			}

			_addActions.put(element, addActions);
			_removeActions.put(element, removeActions);
			_otherActions.put(element, otherActions);
		}
	}

	public void delete() {
		if (_widget != null && _widget.getComponent() != null) {
			for (FIBBrowserElement element : _widget.getComponent().getElements()) {
				Map<FIBBrowserAction, FIBBrowserActionListener> hashtable = _addActions != null ? _addActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}
				hashtable = _removeActions != null ? _removeActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}
				hashtable = _otherActions != null ? _otherActions.get(element) : null;
				if (hashtable != null) {
					for (Map.Entry<FIBBrowserAction, FIBBrowserActionListener> e : hashtable.entrySet()) {
						e.getValue().delete();
					}
				}

			}
		}
		_widget = null;
	}

	public void setFocusedObject(Object object) {
		plusButton.handleSelectionChanged(object);
		minusButton.handleSelectionChanged(object);
		optionsButton.handleSelectionChanged(object);
	}

	public FIBController getController() {
		return _widget.getController();
	}

	public String getLocalized(String key) {
		return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(_widget.getComponent()), key);
	}

	private JPopupMenu filtersPopupMenu;

	protected JPopupMenu getFiltersPopupMenu() {
		if (filtersPopupMenu == null) {
			filtersPopupMenu = makeFiltersPopupMenu();
		}
		for (Component menuItem : filtersPopupMenu.getComponents()) {
			if (menuItem instanceof FIBBrowserFilterMenuItem) {
				((FIBBrowserFilterMenuItem) menuItem).update();
			}
		}
		return filtersPopupMenu;
	}

	private boolean hasFilters() {
		for (FIBBrowserElement el : _widget.getComponent().getElements()) {
			if (el.getFiltered()) {
				return true;
			}
		}
		return false;
	}

	private JPopupMenu makeFiltersPopupMenu() {

		JPopupMenu returned = new JPopupMenu() {
			@Override
			public void setVisible(boolean b) {
				if (b && !isVisible()) {
					addPopupClosers(getWindow(FIBBrowserWidgetFooter.this));
				} else if (!b && isVisible()) {
					removePopupClosers(getWindow(FIBBrowserWidgetFooter.this));
				}
				super.setVisible(b);
				if (!b) {
					filtersButton.setIcon(FIBIconLibrary.BROWSER_FILTERS_ICON);
				}
			}

			@Override
			public void menuSelectionChanged(boolean isIncluded) {
				super.menuSelectionChanged(true);
			}
		};

		for (Map.Entry<FIBBrowserElement, FIBBrowserElementType> e : _widget.getBrowserModel().getElementTypes().entrySet()) {
			if (e.getKey().getFiltered()) {
				returned.add(new FIBBrowserFilterMenuItem(e.getValue()));
			}
		}

		returned.addMenuKeyListener(new MenuKeyListener() {

			@Override
			public void menuKeyPressed(MenuKeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && filtersPopupMenu != null && filtersPopupMenu.isVisible()) {
					closeFiltersPopupMenu();
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

	protected void closeFiltersPopupMenu() {
		logger.info("closeFiltersPopupMenu()");
		if (filtersPopupMenu != null && filtersPopupMenu.isVisible()) {
			filtersPopupMenu.setVisible(false);
		}
		filtersPopupMenu = null;
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
		if (c != filtersPopupMenu && c != null) {
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
		if (c != filtersPopupMenu && c != null) {
			c.removeMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				removePopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() != filtersPopupMenu && e.getSource() != filtersButton) {
			closeFiltersPopupMenu();
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
		closeFiltersPopupMenu();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		closeFiltersPopupMenu();
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
		closeFiltersPopupMenu();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
