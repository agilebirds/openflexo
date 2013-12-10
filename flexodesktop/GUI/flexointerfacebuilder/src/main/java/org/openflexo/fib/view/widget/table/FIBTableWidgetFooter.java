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
package org.openflexo.fib.view.widget.table;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTableAction;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.localization.FlexoLocalization;

public class FIBTableWidgetFooter extends JPanel {

	protected static final Logger logger = Logger.getLogger(FIBTableWidgetFooter.class.getPackage().getName());

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	protected FIBTableWidget _widget;
	protected FIBTable _fibTable;

	protected JButton plusButton;

	protected JButton minusButton;

	protected JButton optionsButton;

	protected JPopupMenu popupMenu = null;

	/**
	 * Stores controls: key is the JButton and value the FIBTableActionListener
	 */
	// private Hashtable<JButton,FIBTableActionListener> _controls;

	class BorderLessButton extends JButton {

		public BorderLessButton() {
			super();
		}

		public BorderLessButton(Action a) {
			super(a);
		}

		public BorderLessButton(Icon icon) {
			super(icon);
		}

		public BorderLessButton(String text, Icon icon) {
			super(text, icon);
		}

		public BorderLessButton(String text) {
			super(text);
		}

		@Override
		public void updateUI() {
			super.updateUI();
			setContentAreaFilled(false);
			setBorderPainted(false);
			setBorder(BorderFactory.createEmptyBorder());
		}
	}

	public FIBTableWidgetFooter(FIBTableWidget widget) {
		super();
		_widget = widget;

		initializeActions(widget);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout());

		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());
		plusMinusPanel.setOpaque(false);
		plusButton = new BorderLessButton(FIBIconLibrary.BROWSER_PLUS_ICON);
		plusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultiplePlusActions()) {
					plusPressed();
				} else {
					getPlusActionMenu().show(plusButton, 0, 0);
				}
			}

		});
		plusButton.setPressedIcon(FIBIconLibrary.BROWSER_PLUS_SELECTED_ICON);
		plusButton.setDisabledIcon(FIBIconLibrary.BROWSER_PLUS_DISABLED_ICON);

		minusButton = new BorderLessButton(FIBIconLibrary.BROWSER_MINUS_ICON);
		minusButton.setPressedIcon(FIBIconLibrary.BROWSER_MINUS_SELECTED_ICON);
		minusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultipleMinusActions()) {
					minusPressed();
				} else {
					getMinusActionMenu().show(minusActionMenu, 0, 0);
				}
			}

		});
		minusButton.setBorder(BorderFactory.createEmptyBorder());
		minusButton.setDisabledIcon(FIBIconLibrary.BROWSER_MINUS_DISABLED_ICON);
		// minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);

		plusMinusPanel.add(plusButton);
		plusMinusPanel.add(minusButton);

		add(plusMinusPanel, BorderLayout.WEST);
		optionsButton = new BorderLessButton(FIBIconLibrary.BROWSER_OPTIONS_ICON);
		optionsButton.setDisabledIcon(FIBIconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
		add(optionsButton, BorderLayout.EAST);
		optionsButton.setPressedIcon(FIBIconLibrary.BROWSER_OPTIONS_SELECTED_ICON);
		optionsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getOptionActionMenu().show(optionsButton, 0, 0);
			}
		});
		handleSelectionChanged();

		revalidate();
	}

	public void handleSelectionChanged() {
		// System.out.println("handleSelectionChanged");
		plusActionMenuNeedsRecomputed = true;
		minusActionMenuNeedsRecomputed = true;
		optionsActionMenuNeedsRecomputed = true;

		if (hasMultiplePlusActions()) {
			plusButton.setEnabled(true && _widget.isEnabled());
		} else {
			boolean isActive = false;
			for (FIBTableAction action : _addActions.keySet()) {
				FIBTableActionListener actionListener = _addActions.get(action);
				if (actionListener.isActive(_widget.getSelectedObject())) {
					isActive = true;
				}
			}
			plusButton.setEnabled(isActive && _widget.isEnabled());
		}

		boolean isMinusActive = false;
		for (FIBTableAction action : _removeActions.keySet()) {
			FIBTableActionListener actionListener = _removeActions.get(action);
			if (actionListener.isActive(_widget.getSelectedObject())) {
				isMinusActive = true;
			}
		}
		minusButton.setEnabled(isMinusActive && _widget.isEnabled());

		optionsButton.setEnabled(_otherActions.size() > 0 && _widget.isEnabled());

	}

	void plusPressed() {
		for (FIBTableAction action : _addActions.keySet()) {
			FIBTableActionListener actionListener = _addActions.get(action);
			if (actionListener.isActive(_widget.getSelectedObject())) {
				actionListener.performAction(_widget.getSelectedObject());
			}
		}
	}

	void minusPressed() {
		for (FIBTableAction action : _removeActions.keySet()) {
			FIBTableActionListener actionListener = _removeActions.get(action);
			if (actionListener.isActive(_widget.getSelectedObject())) {
				// actionListener.performAction(_tableModel.getSelectedObject(), _tableModel.getSelectedObjects());
				actionListener.performAction(_widget.getSelectedObject());
			}
		}
	}

	boolean hasMultiplePlusActions() {
		return _addActions.size() > 1;
	}

	boolean hasMultipleMinusActions() {
		return _removeActions.size() > 1;
	}

	private JPopupMenu plusActionMenu = null;
	private JPopupMenu minusActionMenu = null;
	private JPopupMenu optionsActionMenu = null;

	private boolean plusActionMenuNeedsRecomputed = true;
	private boolean minusActionMenuNeedsRecomputed = true;
	private boolean optionsActionMenuNeedsRecomputed = true;

	private JPopupMenu getPlusActionMenu() {
		if (plusActionMenuNeedsRecomputed) {
			plusActionMenu = new JPopupMenu();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build plus menu");
			}

			for (FIBTableAction action : _addActions.keySet()) {
				FIBTableActionListener actionListener = _addActions.get(action);
				actionListener.setSelectedObject(_widget.getSelectedObject());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				plusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_widget.getSelectedObject()));
			}

			plusActionMenuNeedsRecomputed = false;
		}
		return plusActionMenu;
	}

	private JPopupMenu getMinusActionMenu() {
		if (minusActionMenuNeedsRecomputed) {
			minusActionMenu = new JPopupMenu();

			for (FIBTableAction action : _removeActions.keySet()) {
				FIBTableActionListener actionListener = _removeActions.get(action);
				actionListener.setSelectedObject(_widget.getSelectedObject());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				minusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_widget.getSelectedObject()));
			}

			minusActionMenuNeedsRecomputed = false;
		}
		return minusActionMenu;
	}

	private JPopupMenu getOptionActionMenu() {
		if (optionsActionMenuNeedsRecomputed) {
			optionsActionMenu = new JPopupMenu();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build plus menu");
			}

			for (FIBTableAction action : _otherActions.keySet()) {
				FIBTableActionListener actionListener = _otherActions.get(action);
				actionListener.setSelectedObject(_widget.getSelectedObject());
				// actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(getLocalized(action.getName()));
				menuItem.addActionListener(actionListener);
				optionsActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_widget.getSelectedObject()));
			}

			optionsActionMenuNeedsRecomputed = false;
		}
		return optionsActionMenu;
	}

	private Map<FIBTableAction, FIBTableActionListener> _addActions;
	private Map<FIBTableAction, FIBTableActionListener> _removeActions;
	private Map<FIBTableAction, FIBTableActionListener> _otherActions;

	private void initializeActions(FIBTableWidget tableWidget) {
		_addActions = new LinkedHashMap<FIBTableAction, FIBTableActionListener>();
		_removeActions = new LinkedHashMap<FIBTableAction, FIBTableActionListener>();
		_otherActions = new LinkedHashMap<FIBTableAction, FIBTableActionListener>();

		for (FIBTableAction plAction : tableWidget.getComponent().getActions()) {
			FIBTableActionListener plActionListener = new FIBTableActionListener(plAction, tableWidget);
			if (plActionListener.isAddAction()) {
				_addActions.put(plAction, plActionListener);
			} else if (plActionListener.isRemoveAction()) {
				_removeActions.put(plAction, plActionListener);
			} else if (plActionListener.isCustomAction()) {
				_otherActions.put(plAction, plActionListener);
			}
		}
	}

	public void delete() {
		for (FIBTableActionListener a : _addActions.values()) {
			a.delete();
		}
		for (FIBTableActionListener a : _removeActions.values()) {
			a.delete();
		}
		for (FIBTableActionListener a : _otherActions.values()) {
			a.delete();
		}

		_widget = null;
		_fibTable = null;
	}

	public Collection<FIBTableActionListener> getAddActionListeners() {
		return _addActions.values();
	}

	public void setModel(Object model) {
		// logger.info("Set model with "+model);
		for (FIBTableAction action : _addActions.keySet()) {
			FIBTableActionListener actionListener = _addActions.get(action);
			actionListener.setModel(model);
		}
		for (FIBTableAction action : _removeActions.keySet()) {
			FIBTableActionListener actionListener = _removeActions.get(action);
			actionListener.setModel(model);
		}
		for (FIBTableAction action : _otherActions.keySet()) {
			FIBTableActionListener actionListener = _otherActions.get(action);
			actionListener.setModel(model);
		}
		handleSelectionChanged();
		/* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		      FIBTableActionListener actionListener = (FIBTableActionListener) en.nextElement();
		  	actionListener.setModel(model);
		  }
		  updateControls(null);*/
	}

	public FIBController getController() {
		return _widget.getController();
	}

	public String getLocalized(String key) {
		return FlexoLocalization.localizedForKey(getController().getLocalizerForComponent(_fibTable), key);
	}

}
