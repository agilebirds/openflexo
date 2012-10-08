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
package org.openflexo.inspector.widget.propertylist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.inspector.model.PropertyListModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.toolbox.ToolBox;

public class PropertyListWidgetFooter extends JPanel {

	protected static final Logger logger = Logger.getLogger(PropertyListWidgetFooter.class.getPackage().getName());

	@Deprecated
	public static final Color GUI_BACK_COLOR = ToolBox.getPLATFORM() == ToolBox.MACOS ? null : Color.WHITE;

	// deprecated: will be removed while inspector project will disappear
	// For now, icons are retrieved inside FIB project
	public static final ImageIcon BROWSER_PLUS_ICON = new ImageIconResource("Icons/GUI/BrowserPlus.gif");
	public static final ImageIcon BROWSER_PLUS_DISABLED_ICON = new ImageIconResource("Icons/GUI/BrowserPlusDisabled.gif");
	public static final ImageIcon BROWSER_PLUS_SELECTED_ICON = new ImageIconResource("Icons/GUI/BrowserPlusSelected.gif");
	public static final ImageIcon BROWSER_MINUS_ICON = new ImageIconResource("Icons/GUI/BrowserMinus.gif");
	public static final ImageIcon BROWSER_MINUS_DISABLED_ICON = new ImageIconResource("Icons/GUI/BrowserMinusDisabled.gif");
	public static final ImageIcon BROWSER_MINUS_SELECTED_ICON = new ImageIconResource("Icons/GUI/BrowserMinusSelected.gif");
	public static final ImageIcon BROWSER_OPTIONS_ICON = new ImageIconResource("Icons/GUI/BrowserOptions.gif");
	public static final ImageIcon BROWSER_OPTIONS_DISABLED_ICON = new ImageIconResource("Icons/GUI/BrowserOptionsDisabled.gif");
	public static final ImageIcon BROWSER_OPTIONS_SELECTED_ICON = new ImageIconResource("Icons/GUI/BrowserOptionsSelected.gif");
	public static final ImageIcon BROWSER_FILTERS_ICON = new ImageIconResource("Icons/GUI/BrowserFilters.gif");
	public static final ImageIcon BROWSER_FILTERS_DISABLED_ICON = new ImageIconResource("Icons/GUI/BrowserFiltersDisabled.gif");
	public static final ImageIcon BROWSER_FILTERS_SELECTED_ICON = new ImageIconResource("Icons/GUI/BrowserFiltersSelected.gif");

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 200;

	protected PropertyListWidget _widget;
	protected PropertyListModel _propertyListModel;
	protected PropertyListTableModel _tableModel;

	protected JButton plusButton;

	protected JButton minusButton;

	protected JButton optionsButton;

	protected JPopupMenu popupMenu = null;

	/**
	 * Stores controls: key is the JButton and value the PropertyListActionListener
	 */
	// private Hashtable<JButton,PropertyListActionListener> _controls;

	public PropertyListWidgetFooter(PropertyListModel propertyListModel, PropertyListTableModel tableModel, PropertyListWidget widget) {
		super();
		_widget = widget;
		_tableModel = tableModel;
		_propertyListModel = propertyListModel;

		initializeActions(propertyListModel, tableModel);

		setBorder(BorderFactory.createEmptyBorder());
		setBackground(GUI_BACK_COLOR);
		setLayout(new BorderLayout());
		// setPreferredSize(new
		// Dimension(FlexoCst.MINIMUM_BROWSER_VIEW_WIDTH,FlexoCst.MINIMUM_BROWSER_CONTROL_PANEL_HEIGHT));
		setPreferredSize(new Dimension(MINIMUM_BROWSER_VIEW_WIDTH, 20));

		JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		plusMinusPanel.setBackground(GUI_BACK_COLOR);
		plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());

		plusButton = new JButton(BROWSER_PLUS_ICON);
		plusButton.setBackground(GUI_BACK_COLOR);
		plusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultiplePlusActions()) {
					plusPressed();
					plusButton.setIcon(BROWSER_PLUS_ICON);
				}
			}

		});
		plusButton.setBorder(BorderFactory.createEmptyBorder());
		plusButton.setDisabledIcon(BROWSER_PLUS_DISABLED_ICON);
		// plusButton.setSelectedIcon(FlexoCst.BROWSER_PLUS_SELECTED_ICON);
		plusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(BROWSER_PLUS_SELECTED_ICON);
				}
				if (hasMultiplePlusActions()) {
					getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					plusButton.setIcon(BROWSER_PLUS_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (plusButton.isEnabled()) {
					plusButton.setIcon(BROWSER_PLUS_ICON);
				}
				if (hasMultiplePlusActions()) {
					getPlusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});

		minusButton = new JButton(BROWSER_MINUS_ICON);
		minusButton.setBackground(GUI_BACK_COLOR);
		minusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!hasMultipleMinusActions()) {
					minusPressed();
					minusButton.setIcon(BROWSER_MINUS_ICON);
				}
			}

		});
		minusButton.setBorder(BorderFactory.createEmptyBorder());
		minusButton.setDisabledIcon(BROWSER_MINUS_DISABLED_ICON);
		// minusButton.setSelectedIcon(FlexoCst.BROWSER_MINUS_SELECTED_ICON);
		minusButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(BROWSER_MINUS_SELECTED_ICON);
				}
				if (hasMultipleMinusActions()) {
					getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
					minusButton.setIcon(BROWSER_MINUS_ICON);
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (minusButton.isEnabled()) {
					minusButton.setIcon(BROWSER_MINUS_ICON);
				}
				if (hasMultipleMinusActions()) {
					getMinusActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		});

		plusMinusPanel.add(plusButton);
		plusMinusPanel.add(minusButton);

		add(plusMinusPanel, BorderLayout.WEST);
		optionsButton = new JButton(BROWSER_OPTIONS_ICON);
		optionsButton.setBorder(BorderFactory.createEmptyBorder());
		optionsButton.setDisabledIcon(BROWSER_OPTIONS_DISABLED_ICON);
		add(optionsButton, BorderLayout.EAST);

		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (optionsButton.isEnabled()) {
					optionsButton.setIcon(BROWSER_OPTIONS_SELECTED_ICON);
				}
				getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				if (optionsButton.isEnabled()) {
					optionsButton.setIcon(BROWSER_OPTIONS_ICON);
				}
				getOptionActionMenu().show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
			}

		});

		handleSelectionCleared();

		validate();
	}

	protected void handleSelectionChanged() {
		// System.out.println("handleSelectionChanged");
		plusActionMenuNeedsRecomputed = true;
		minusActionMenuNeedsRecomputed = true;
		optionsActionMenuNeedsRecomputed = true;

		if (hasMultiplePlusActions()) {
			plusButton.setEnabled(true);
		} else {
			boolean isActive = false;
			for (PropertyListAction action : _addActions.keySet()) {
				PropertyListActionListener actionListener = _addActions.get(action);
				if (actionListener.isActive(_tableModel.getSelectedObject())) {
					isActive = true;
				}
			}
			plusButton.setEnabled(isActive);
		}

		boolean isMinusActive = false;
		for (PropertyListAction action : _removeActions.keySet()) {
			PropertyListActionListener actionListener = _removeActions.get(action);
			if (actionListener.isActive(_tableModel.getSelectedObject())) {
				isMinusActive = true;
			}
		}
		minusButton.setEnabled(isMinusActive);

		optionsButton.setEnabled(_otherActions.size() > 0);

		/*FlexoModelObject focusedObject = getFocusedObject();
		Vector<FlexoModelObject> globalSelection = buildGlobalSelection();
		plusButton.setEnabled((focusedObject != null) && (getActionTypesWithAddType(focusedObject).size() > 0));
		minusButton.setEnabled((focusedObject != null) && (getActionTypesWithDeleteType(focusedObject, globalSelection).size() > 0));
		plusActionMenuNeedsRecomputed = true;*/
	}

	protected void handleSelectionCleared() {
		handleSelectionChanged();

		/*System.out.println("handleSelectionCleared");
		
		plusButton.setEnabled(false);
		minusButton.setEnabled(false);
		
		plusActionMenuNeedsRecomputed = true;
		minusActionMenuNeedsRecomputed = true;
		optionsActionMenuNeedsRecomputed = true;
		*/
	}

	void plusPressed() {
		for (PropertyListAction action : _addActions.keySet()) {
			PropertyListActionListener actionListener = _addActions.get(action);
			if (actionListener.isActive(_tableModel.getSelectedObject())) {
				actionListener.performAction();
			}
		}
	}

	void minusPressed() {
		for (PropertyListAction action : _removeActions.keySet()) {
			PropertyListActionListener actionListener = _removeActions.get(action);
			if (actionListener.isActive(_tableModel.getSelectedObject())) {
				actionListener.performAction(_tableModel.getSelectedObject(), _tableModel.getSelectedObjects());
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

			for (PropertyListAction action : _addActions.keySet()) {
				PropertyListActionListener actionListener = _addActions.get(action);
				actionListener.setSelectedObject(_tableModel.getSelectedObject());
				actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(action.name));
				menuItem.addActionListener(actionListener);
				plusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
			}

			plusActionMenuNeedsRecomputed = false;
		}
		return plusActionMenu;
	}

	private JPopupMenu getMinusActionMenu() {
		if (minusActionMenuNeedsRecomputed) {
			minusActionMenu = new JPopupMenu();

			for (PropertyListAction action : _removeActions.keySet()) {
				PropertyListActionListener actionListener = _removeActions.get(action);
				actionListener.setSelectedObject(_tableModel.getSelectedObject());
				actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(action.name));
				menuItem.addActionListener(actionListener);
				minusActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
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

			for (PropertyListAction action : _otherActions.keySet()) {
				PropertyListActionListener actionListener = _otherActions.get(action);
				actionListener.setSelectedObject(_tableModel.getSelectedObject());
				actionListener.setSelectedObjects(_tableModel.getSelectedObjects());
				JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(action.name));
				menuItem.addActionListener(actionListener);
				optionsActionMenu.add(menuItem);
				menuItem.setEnabled(actionListener.isActive(_tableModel.getSelectedObject()));
			}

			optionsActionMenuNeedsRecomputed = false;
		}
		return optionsActionMenu;
	}

	private Hashtable<PropertyListAction, PropertyListActionListener> _addActions;
	private Hashtable<PropertyListAction, PropertyListActionListener> _removeActions;
	private Hashtable<PropertyListAction, PropertyListActionListener> _otherActions;

	private void initializeActions(PropertyListModel propertyListModel, PropertyListTableModel tableModel) {
		_addActions = new Hashtable<PropertyListAction, PropertyListActionListener>();
		_removeActions = new Hashtable<PropertyListAction, PropertyListActionListener>();
		_otherActions = new Hashtable<PropertyListAction, PropertyListActionListener>();

		for (Enumeration en = propertyListModel.getActions().elements(); en.hasMoreElements();) {
			PropertyListAction plAction = (PropertyListAction) en.nextElement();
			PropertyListActionListener plActionListener = new PropertyListActionListener(plAction, tableModel);
			if (plAction.type.equals("ADD")) {
				_addActions.put(plAction, plActionListener);
			} else if (plAction.type.equals("DELETE")) {
				_removeActions.put(plAction, plActionListener);
			} else if (plAction.type.equals("ACTION")) {
				_otherActions.put(plAction, plActionListener);
			} else if (plAction.type.equals("STATIC_ACTION")) {
				_otherActions.put(plAction, plActionListener);
			}
		}
	}

	protected Enumeration<PropertyListActionListener> getAddActionListeners() {
		return _addActions.elements();
	}

	protected void setModel(InspectableObject model) {
		// logger.info("Set model with "+model);
		for (PropertyListAction action : _addActions.keySet()) {
			PropertyListActionListener actionListener = _addActions.get(action);
			actionListener.setModel(model);
		}
		for (PropertyListAction action : _removeActions.keySet()) {
			PropertyListActionListener actionListener = _removeActions.get(action);
			actionListener.setModel(model);
		}
		for (PropertyListAction action : _otherActions.keySet()) {
			PropertyListActionListener actionListener = _otherActions.get(action);
			actionListener.setModel(model);
		}
		handleSelectionChanged();
		/* for (Enumeration en = _controls.elements(); en.hasMoreElements();) {
		      PropertyListActionListener actionListener = (PropertyListActionListener) en.nextElement();
		  	actionListener.setModel(model);
		  }
		  updateControls(null);*/
	}

}
