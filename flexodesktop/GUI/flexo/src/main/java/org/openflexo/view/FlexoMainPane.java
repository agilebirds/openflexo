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
package org.openflexo.view;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;

import org.openflexo.ColorCst;
import org.openflexo.GeneralPreferences;
import org.openflexo.ch.FCH;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.MouseOverButton;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;

/**
 * Abstract view managing global layout of a FlexoModule
 * 
 * @author sguerin
 */
public abstract class FlexoMainPane extends JPanel implements GraphicalFlexoObserver {

	protected static final Logger logger = Logger.getLogger(FlexoMainPane.class.getPackage().getName());

	protected JSplitPane _splitPane;

	protected FlexoController _controller;

	private JComponent _centerView;

	// private JSplitPane _middlePane;

	protected ModuleView _moduleView;

	private ControlPanel _controlPanel;

	private JComponent _rightPanel;

	protected Vector<FlexoModelObject> _availableObjects;

	protected Stack<HistoryLocation> previousHistory;
	protected Stack<HistoryLocation> nextHistory;
	protected HistoryLocation currentLocation;

	protected boolean isGoingForward = false;

	protected boolean isGoingBackward = false;

	private JComponent _leftView;

	private JComponent _rightView;

	final private boolean _rightPaneIsSplitPane;
	final private boolean _verticalOrientation;

	public FlexoMainPane(ModuleView moduleView, FlexoFrame mainFrame, FlexoController controller) {
		this(moduleView, mainFrame, controller, false, true);
	}

	public FlexoMainPane(ModuleView moduleView, FlexoFrame mainFrame, FlexoController controller, boolean rightPaneIsSplitPane,
			boolean verticalOrientation) {
		super(new BorderLayout());
		_rightPaneIsSplitPane = rightPaneIsSplitPane;
		_verticalOrientation = verticalOrientation;
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		new FlexoSplitPaneLocationSaver(_splitPane, controller.getModule().getShortName() + "MainSplitPane");
		_controller = controller;
		_availableObjects = new Vector<FlexoModelObject>();
		previousHistory = new Stack<HistoryLocation>();
		nextHistory = new Stack<HistoryLocation>();
		_leftView = null;
		_rightView = null;
		_splitPane.setLeftComponent(new JPanel());
		_splitPane.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));
		if (rightPaneIsSplitPane) {
			_rightPanel = new JSplitPane((!_verticalOrientation ? JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT));
			_rightPanel.setBorder(BorderFactory.createEmptyBorder());
			new FlexoSplitPaneLocationSaver(_splitPane, controller.getModule().getShortName() + "RightSplitPane");
			_rightPanel.setBackground(ColorCst.GUI_BACK_COLOR);
			((JSplitPane) _rightPanel).setResizeWeight(1.0);
		} else {
			_rightPanel = new JPanel();
			_rightPanel.setBackground(ColorCst.GUI_BACK_COLOR);
			_rightPanel.setLayout(new BorderLayout());
		}
		/* _rightPanel. */add(_controlPanel = new ControlPanel(), BorderLayout.NORTH);
		_splitPane.setRightComponent(_rightPanel);
		add(_splitPane, BorderLayout.CENTER);
		setModuleView(moduleView);
	}

	public void resetModuleView() {
		// _moduleView = null;
		setModuleView(null);
	}

	private JComponent _footer;
	private JComponent _header;

	public void setModuleView(ModuleView moduleView) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setModuleView() with " + moduleView + " perspective " + moduleView.getPerspective());
		}
		try {
			if (_moduleView != null) {
				_moduleView.willHide();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("willHide call failed on " + _moduleView);
			}
		}
		if (_centerView != null) {
			_rightPanel.remove(_centerView);
			// _middlePane.setLeftComponent(null);
		}

		if (_controller instanceof SelectionManagingController) {
			if ((_moduleView != null) && (_moduleView instanceof SelectionSynchronizedModuleView)) {
				((SelectionManagingController) _controller).getSelectionManager().removeFromSelectionListeners(
						((SelectionSynchronizedModuleView) _moduleView).getSelectionListeners());
			}
			if ((moduleView != null) && (moduleView instanceof SelectionSynchronizedModuleView)) {
				((SelectionManagingController) _controller).getSelectionManager().addToSelectionListeners(
						((SelectionSynchronizedModuleView) moduleView).getSelectionListeners());
			}
		}

		_moduleView = moduleView;
		if (moduleView != null) {
			if (moduleView.getRepresentedObject() != null) {
				if (!_availableObjects.contains(moduleView.getRepresentedObject())) {
					_availableObjects.add(moduleView.getRepresentedObject());
					moduleView.getRepresentedObject().addObserver(this);
				}
				if (!isGoingForward && !isGoingBackward) {
					// _history.add(moduleView.getRepresentedObject());
					if ((currentLocation == null) || (currentLocation.getObject() != moduleView.getRepresentedObject())) {
						if (currentLocation != null) {
							previousHistory.push(currentLocation);
						}
						nextHistory.clear();
						currentLocation = new HistoryLocation(moduleView.getRepresentedObject(), moduleView.getPerspective());
					}
				}
			}
			if (_controlPanel != null) {
				_controlPanel.update();
			}

			if (moduleView.isAutoscrolled()) {
				_centerView = (JComponent) moduleView;
			} else {
				JScrollPane scrollPane = new JScrollPane((JComponent) moduleView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());

				if (scrollPane.getVerticalScrollBar() != null) {
					scrollPane.getVerticalScrollBar().setUnitIncrement(10);
					scrollPane.getVerticalScrollBar().setBlockIncrement(50);

				}
				if (scrollPane.getHorizontalScrollBar() != null) {
					scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
					scrollPane.getHorizontalScrollBar().setBlockIncrement(50);
				}

				// _scrollPane.getViewport().setBackground(FlexoCst.GUI_BACK_COLOR);
				scrollPane.getHorizontalScrollBar().setFocusable(false);
				scrollPane.getVerticalScrollBar().setFocusable(false);

				if (moduleView instanceof ChangeListener) {
					scrollPane.getViewport().addChangeListener((ChangeListener) moduleView);
				}

				_centerView = scrollPane;
			}

			_centerView.setFocusable(false);

			if (_rightPaneIsSplitPane) {
				JSplitPane splitPane = (JSplitPane) _rightPanel;
				int dl = splitPane.getDividerLocation();
				splitPane.setLeftComponent(_centerView);
				/*if (splitPane.getRightComponent() != null) {
					splitPane.getRightComponent().setPreferredSize(splitPane.getRightComponent().getSize());
				}
				splitPane.revalidate();
				splitPane.repaint();*/
				splitPane.setDividerLocation(dl);
			} else {
				_rightPanel.add(_centerView, BorderLayout.CENTER);
			}

			((JComponent) moduleView).validate();
			((JComponent) moduleView).repaint();
		}

		if (moduleView != null) {
			if (moduleView.getPerspective() == null) {
				if (_controller.getDefaultPespective() != null) {
					logger.warning("null perspective declared for " + moduleView.getClass().getName());
				}
			} else {

				// What about left view ?
				if (moduleView.getPerspective().doesPerspectiveControlLeftView()) {
					// logger.info("Set left view with "+moduleView.getPerspective().getLeftView());
					setLeftView(moduleView.getPerspective().getLeftView());
				}

				// What about right view ?
				if (moduleView.getPerspective().doesPerspectiveControlRightView()) {
					// logger.info("Set right view with "+moduleView.getPerspective().getRightView());
					setRightView(moduleView.getPerspective().getRightView());
				}

				// Do job for header
				if (_header != moduleView.getPerspective().getHeader()) {
					if (_header != null) {
						_controlPanel.centerPanel.remove(_header);
						_controlPanel.validate();
					}
					if (moduleView.getPerspective().getHeader() != null) {
						_controlPanel.centerPanel.add(moduleView.getPerspective().getHeader(), BorderLayout.EAST);
						_controlPanel.validate();
					}
				}
				_header = moduleView.getPerspective().getHeader();

				// Do job for footer
				if (_footer != moduleView.getPerspective().getFooter()) {
					if (_footer != null) {
						remove(_footer);
						validate();
					}
					if (moduleView.getPerspective().getFooter() != null) {
						add(moduleView.getPerspective().getFooter(), BorderLayout.SOUTH);
						validate();
					}
				}
				_footer = moduleView.getPerspective().getFooter();

			}
		}

		if (moduleView != null) {
			try {
				moduleView.willShow();
			} catch (RuntimeException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("willShow call failed on " + moduleView);
				}
			}
		}

		if (_moduleView != null) {
			FCH.setHelpItem((JComponent) _moduleView, FCH.getModuleViewItemFor(_controller.getModule(), _moduleView));
		}

		if (_controller.getFlexoFrame().isValid()) {
			FCH.validateWindow(_controller.getFlexoFrame());
		}

		_controller.getFlexoFrame().updateTitle();
		revalidate();
		repaint();
	}

	public ModuleView getModuleView() {
		return _moduleView;
	}

	public void goBackward() {
		if (canGoBackward()) {
			isGoingBackward = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Back to " + previousHistory.peek());
			}
			HistoryLocation historyLocation = previousHistory.peek();
			FlexoModelObject backObject = historyLocation.getObject();
			switchToModuleViewForObjectAndPerspective(backObject, historyLocation.getPerspective());
			nextHistory.push(currentLocation);
			currentLocation = previousHistory.pop();
			_controlPanel.update();
			isGoingBackward = false;
		}
	}

	public void goForward() {
		if (canGoForward()) {
			isGoingForward = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Forward to " + nextHistory.peek());
			}
			HistoryLocation historyLocation = nextHistory.peek();
			FlexoModelObject forwardObject = historyLocation.getObject();
			switchToModuleViewForObjectAndPerspective(forwardObject, historyLocation.getPerspective());
			previousHistory.push(currentLocation);
			currentLocation = nextHistory.pop();
			_controlPanel.update();
			isGoingForward = false;
		}
	}

	public void goUp() {
		switchToParentModuleViewForObject(getCurrentEditedObject());
	}

	public class ControlPanel extends JPanel {
		private final JLabel backwardButton;

		private final JLabel forwardButton;

		private final JLabel upButton;

		protected JLabel closeModuleViewButton;
		protected JLabel collapseAllButton;
		protected JLabel autoLayoutButton;

		protected JComboBox availableObjects;

		protected AvailablePerspectives availablePerspectives;

		protected ActionListener availableObjectsActionListener;

		private final JPanel centerPanel;

		private final JPanel viewControlsPanel;

		private final ModuleBar moduleBar;

		private class NavigationButton extends JLabel {
			private final Icon enabledIcon;

			private final Icon disabledIcon;

			protected NavigationButton(Icon enabled, Icon disabled) {
				this.enabledIcon = enabled;
				this.disabledIcon = disabled;
			}

			/**
			 * Overrides setEnabled
			 * 
			 * @see javax.swing.JComponent#setEnabled(boolean)
			 */
			@Override
			public void setEnabled(boolean enabled) {
				if (enabled) {
					setIcon(enabledIcon);
					setSize(enabledIcon.getIconWidth(), enabledIcon.getIconHeight());
				} else {
					setIcon(disabledIcon);
					setSize(disabledIcon.getIconWidth(), disabledIcon.getIconHeight());
				}
			}
		}

		public ControlPanel() {
			super();
			moduleBar = new ModuleBar();
			setLayout(new BorderLayout());

			centerPanel = new JPanel(new BorderLayout());

			availableObjects = new JComboBox(_availableObjects);
			availableObjectsActionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					switchToModuleViewForObjectAndPerspective((FlexoModelObject) availableObjects.getSelectedItem(),
							_controller.getCurrentPerspective());
				}
			};
			availableObjects.addActionListener(availableObjectsActionListener);
			availableObjects.setFocusable(false);
			availableObjects.setRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if ((value != null) && (!((FlexoModelObject) value).isDeleted())) {
						String title = _controller.getWindowTitleforObject((FlexoModelObject) value);
						if (title == null) {
							logger.warning("Unexpected object " + value + " asked for controller " + _controller + " perspective="
									+ _controller.getCurrentPerspective());
							title = "???";
						}
						returned.setText(title);
						returned.setPreferredSize(new Dimension(returned.getFontMetrics(returned.getFont()).stringWidth(title) + 15,
								returned.getFontMetrics(returned.getFont()).getHeight()));
					} else {
						if (value == null) {
							returned.setText(FlexoLocalization.localizedForKey("no_selection"));
						} else {
							returned.setText(FlexoLocalization.localizedForKey("deleted"));
						}
					}
					setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
					return returned;
				}
			});
			FCH.setHelpItem(availableObjects, "available-objects");

			closeModuleViewButton = new JLabel(IconLibrary.CLOSE_ICON);
			closeModuleViewButton.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if ((_moduleView != null) && (_moduleView.getRepresentedObject() != null)) {
						ModuleView previous = _moduleView;
						updateControlsForObjectRemovedFromHistory(previous.getRepresentedObject());
						previous.deleteModuleView();
					}
				}
			});
			FCH.setHelpItem(closeModuleViewButton, "close-module");
			if (isCollapseEnabled()) {
				collapseAllButton = new JLabel(IconLibrary.COLLAPSE_ALL_ICON);
				collapseAllButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						performCollapseAll();
					}
				});
				FCH.setHelpItem(collapseAllButton, "collapse-all");
			}

			if (_controller.getPerspectives().size() > 0) {
				// _controller.getPerspectives()
				availablePerspectives = new AvailablePerspectives(_controller);
				availablePerspectives.setFocusable(false);
				FCH.setHelpItem(availablePerspectives, "available-perspectives");
			}

			backwardButton = new NavigationButton(IconLibrary.NAVIGATION_BACKWARD_ICON, IconLibrary.NAVIGATION_DISABLED_BACKWARD_ICON);
			backwardButton.setSize(IconLibrary.NAVIGATION_BACKWARD_ICON.getIconWidth(),
					IconLibrary.NAVIGATION_BACKWARD_ICON.getIconHeight());
			backwardButton.setEnabled(false);
			backwardButton.setFocusable(false);
			backwardButton.addMouseListener(new MouseAdapter() {
				/**
				 * Overrides mouseClicked
				 * 
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					goBackward();
				}
			});

			FCH.setHelpItem(backwardButton, "back");

			forwardButton = new NavigationButton(IconLibrary.NAVIGATION_FORWARD_ICON, IconLibrary.NAVIGATION_DISABLED_FORWARD_ICON);
			forwardButton.setSize(27, 19);
			forwardButton.setEnabled(false);
			forwardButton.addMouseListener(new MouseAdapter() {
				/**
				 * Overrides mouseClicked
				 * 
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					goForward();
				}
			});
			forwardButton.setFocusable(false);

			FCH.setHelpItem(forwardButton, "forward");

			upButton = new NavigationButton(IconLibrary.NAVIGATION_UP_ICON, IconLibrary.NAVIGATION_DISABLED_UP_ICON);
			upButton.setSize(22, 19);
			upButton.setEnabled(false);
			upButton.setFocusable(false);
			upButton.addMouseListener(new MouseAdapter() {
				/**
				 * Overrides mouseClicked
				 * 
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					goUp();
				}
			});

			FCH.setHelpItem(upButton, "up");

			JPanel commonButtonsPanel = new JPanel(new FlowLayout());
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			buttonPanel.add(backwardButton);
			buttonPanel.add(upButton);
			buttonPanel.add(forwardButton);
			if (availablePerspectives != null) {
				commonButtonsPanel.add(availablePerspectives);
			}

			viewControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

			setupViewControlsPanel();

			commonButtonsPanel.add(availableObjects);
			commonButtonsPanel.add(viewControlsPanel);
			commonButtonsPanel.add(buttonPanel);
			centerPanel.add(moduleBar, BorderLayout.WEST);
			centerPanel.add(commonButtonsPanel, BorderLayout.CENTER);

			if (_controller.getCustomActionPanel() != null) {
				centerPanel.add(_controller.getCustomActionPanel(), BorderLayout.EAST);
			}

			toggleHideShowLeftPanel = new MouseOverButton();
			// toggleHideShowLeftPanel.setBackground(FlexoCst.GUI_BACK_COLOR);
			toggleHideShowLeftPanel.setBorder(BorderFactory.createEmptyBorder());
			toggleHideShowLeftPanel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (leftViewIsVisible()) {
						_controller.hideLeftView();
					} else {
						_controller.showLeftView();
					}
				}
			});
			FCH.setHelpItem(toggleHideShowLeftPanel, "toggle-hide-show-left-panel");

			updateToggleHideShowLeftPanelButton(leftViewIsVisible());

			toggleHideShowRightPanel = new MouseOverButton();
			// toggleHideShowRightPanel.setBackground(FlexoCst.GUI_BACK_COLOR);
			toggleHideShowRightPanel.setBorder(BorderFactory.createEmptyBorder());
			toggleHideShowRightPanel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (rightViewIsVisible()) {
						_controller.hideRightView();
					} else {
						_controller.showRightView();
					}
				}
			});
			FCH.setHelpItem(toggleHideShowRightPanel, "toggle-hide-show-right-panel");

			updateToggleHideShowRightPanelButton(rightViewIsVisible());
			CSH.setHelpIDString(toggleHideShowRightPanel, "OperatorIFNode");

			add(centerPanel, BorderLayout.CENTER);

			// setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		private void setupViewControlsPanel() {
			if (isAutoLayoutEnabled()) {
				autoLayoutButton = new JLabel(IconLibrary.AUTO_LAYOUT_ICON);
				autoLayoutButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						performAutolayout();
					}
				});
				FCH.setHelpItem(autoLayoutButton, "auto-layout");
			} else {
				autoLayoutButton = null;
			}

			viewControlsPanel.removeAll();
			viewControlsPanel.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_LEFT));
			viewControlsPanel.add(closeModuleViewButton);

			if (collapseAllButton != null) {
				viewControlsPanel.add(new JLabel(IconLibrary.NAVIGATION_SPACER));
				viewControlsPanel.add(collapseAllButton);
			}

			if (autoLayoutButton != null) {
				viewControlsPanel.add(new JLabel(IconLibrary.NAVIGATION_SPACER));
				viewControlsPanel.add(autoLayoutButton);
			}
			viewControlsPanel.add(new JLabel(IconLibrary.NAVIGATION_CLOSE_RIGHT));
			viewControlsPanel.validate();

		}

		private void updateToggleHideShowLeftPanelButton(boolean status) {
			if (!status) { // Hide
				toggleHideShowLeftPanel.setNormalIcon(IconLibrary.TOGGLE_ARROW_TOP_ICON);
				toggleHideShowLeftPanel.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				toggleHideShowLeftPanel.setToolTipText(FlexoLocalization.localizedForKey("show_browser"));
			} else { // Show
				toggleHideShowLeftPanel.setNormalIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				toggleHideShowLeftPanel.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				toggleHideShowLeftPanel.setToolTipText(FlexoLocalization.localizedForKey("hide_browser"));
			}
		}

		private void updateToggleHideShowRightPanelButton(boolean status) {
			if (!status) { // Hide
				toggleHideShowRightPanel.setNormalIcon(IconLibrary.TOGGLE_ARROW_TOP_ICON);
				toggleHideShowRightPanel.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				toggleHideShowRightPanel.setToolTipText(FlexoLocalization.localizedForKey("show_palette"));
			} else { // Show
				toggleHideShowRightPanel.setNormalIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				toggleHideShowRightPanel.setMouseOverIcon(IconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				toggleHideShowRightPanel.setToolTipText(FlexoLocalization.localizedForKey("hide_palette"));
			}
		}

		protected MouseOverButton toggleHideShowLeftPanel;

		protected MouseOverButton toggleHideShowRightPanel;

		protected void update() {
			/*
			 * System.out.println ("History:"); for (int i=0; i<_history.size();
			 * i++) { System.out.println (((i==_historyPosition)?"*":"
			 * ")+((FlexoProcess)_history.get(i)).toString()); }
			 */
			if (_moduleView != null) {
				availableObjects.removeActionListener(availableObjectsActionListener);
				availableObjects.setSelectedItem(_moduleView.getRepresentedObject());
				if (availablePerspectives != null) {
					availablePerspectives.refresh();
				}
				availableObjects.addActionListener(availableObjectsActionListener);
				if (getParentObject(_moduleView.getRepresentedObject()) != null) {
					upButton.setEnabled(true);
				} else {
					upButton.setEnabled(false);
				}
			}
			setupViewControlsPanel();
			backwardButton.setEnabled(canGoBackward());
			forwardButton.setEnabled(canGoForward());

			availableObjects.updateUI();
		}

	}

	protected boolean canGoForward() {
		return nextHistory.size() > 0;
	}

	protected boolean canGoBackward() {
		return previousHistory.size() > 0;
	}

	public ControlPanel getControlPanel() {
		return _controlPanel;
	}

	public void showControlPanel() {
		if (_controlPanel != null) {
			_controlPanel.setVisible(true);
		}
	}

	public void hideControlPanel() {
		if (_controlPanel != null) {
			_controlPanel.setVisible(false);
		}
	}

	public JComponent getLeftView() {
		return _leftView;
	}

	public void setLeftView(JComponent leftView) {
		if (_leftView != leftView) {
			FCH.setHelpItem(leftView, FCH.getLeftViewItemFor(_controller.getModule()));
			_leftView = leftView;
			_controlPanel.add(_controlPanel.toggleHideShowLeftPanel, BorderLayout.WEST);
			if (_controller.getFlexoFrame().isValid()) {
				FCH.validateWindow(_controller.getFlexoFrame());
			}
			revalidate();
			repaint();
		}
		updateLeftViewVisibilityWithPreferences();
	}

	/**
	 *
	 */
	public void updateLeftViewVisibilityWithPreferences() {
		if (GeneralPreferences.getShowLeftView(getController().getModule().getShortName())) {
			_controller.showLeftView();
		} else {
			_controller.hideLeftView();
		}
	}

	public boolean leftViewIsVisible() {
		if (_leftView == null) {
			return false;
		}
		return _leftView.getParent() != null;
	}

	public void showLeftView() {
		if (_leftView != null) {
			_splitPane.setLeftComponent(_leftView);
			_controlPanel.updateToggleHideShowLeftPanelButton(true);
			_splitPane.resetToPreferredSizes();
			revalidate();
			repaint();
			if (GeneralPreferences.getShowLeftView(getController().getModule().getShortName()) == false) {
				GeneralPreferences.setShowLeftView(getController().getModule().getShortName(), true);
				FlexoPreferences.savePreferences(true);
			}
		}
	}

	public void hideLeftView() {
		if (_leftView != null) {
			_splitPane.setLeftComponent(null);
			_controlPanel.updateToggleHideShowLeftPanelButton(false);
			revalidate();
			repaint();
			if (GeneralPreferences.getShowLeftView(getController().getModule().getShortName()) == true) {
				GeneralPreferences.setShowLeftView(getController().getModule().getShortName(), false);
				FlexoPreferences.savePreferences(true);
			}
		}
	}

	public JComponent getRightView() {
		return _rightView;
	}

	public void setRightView(JComponent rightView) {
		if (_rightView != rightView) {

			if ((rightView.getParent() != null) && (rightView.getParent() != _rightPanel)) {
				rightView.getParent().remove(rightView);
			}
			FCH.setHelpItem(rightView, FCH.getRightViewItemFor(_controller.getModule()));
			if (_rightView != null) {
				_rightPanel.remove(_rightView);
			}
			_rightView = rightView;
			if (_rightPaneIsSplitPane) {
				((JSplitPane) _rightPanel).setRightComponent(_rightView);
			} else {
				_rightPanel.add(_rightView, BorderLayout.EAST);
			}
			_controlPanel.add(_controlPanel.toggleHideShowRightPanel, BorderLayout.EAST);
			if (_controller.getFlexoFrame().isValid()) {
				FCH.validateWindow(_controller.getFlexoFrame());
			}
		}
		updateRightViewVisibilityWithPreferences();
	}

	/**
	 *
	 */
	public void updateRightViewVisibilityWithPreferences() {
		if (GeneralPreferences.getShowRightView(getController().getModule().getShortName())) {
			_controller.showRightView();
		} else {
			_controller.hideRightView();
		}
	}

	public JComponent getBottomView() {
		return getRightView();
	}

	public void setBottomView(JComponent bottomView) {
		setRightView(bottomView);
	}

	public boolean rightViewIsVisible() {
		if (_rightView == null) {
			return false;
		}
		return _rightView.isVisible();
	}

	public void showRightView() {
		if (_rightView != null) {
			_rightView.setVisible(true);
			_controlPanel.updateToggleHideShowRightPanelButton(true);
			if (_rightPaneIsSplitPane && (defaultDL > -1)) {
				JSplitPane splitPane = (JSplitPane) _rightPanel;
				splitPane.setDividerLocation(defaultDL);
				// logger.info("Sets dividerLocation to "+defaultDL);
			}
			revalidate();
			repaint();
			logger.fine("_rightView.getSize()=" + _rightView.getSize());
			/*
			 * _middlePane.resetToPreferredSizes(); _middlePane.revalidate();
			 * _middlePane.repaint();
			 */
			if (GeneralPreferences.getShowRightView(getController().getModule().getShortName()) == false) {
				GeneralPreferences.setShowRightView(getController().getModule().getShortName(), true);
				FlexoPreferences.savePreferences(true);
			}
		}
	}

	private int defaultDL = -1;

	public void hideRightView() {
		if (_rightView != null) {
			if (_rightPaneIsSplitPane) {
				JSplitPane splitPane = (JSplitPane) _rightPanel;
				defaultDL = splitPane.getDividerLocation();
				// logger.info("DividerLocation is "+defaultDL);
			}
			_rightView.setVisible(false);
			_controlPanel.updateToggleHideShowRightPanelButton(false);
			revalidate();
			repaint();
			if (GeneralPreferences.getShowRightView(getController().getModule().getShortName()) == true) {
				GeneralPreferences.setShowRightView(getController().getModule().getShortName(), false);
				FlexoPreferences.savePreferences(true);
			}
			/*
			 * _middlePane.revalidate(); _middlePane.repaint();
			 */
		}
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted) {
			FlexoModelObject removedObject = ((ObjectDeleted) dataModification).getDeletedObject();
			updateControlsForObjectRemovedFromHistory(removedObject);
		} else if (dataModification instanceof NameChanged) {
			if (_controlPanel != null) {
				_controlPanel.update();
			}
		}
	}

	/**
	 * @param removedObject
	 */
	protected void updateControlsForObjectRemovedFromHistory(FlexoModelObject removedObject) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Synchronize control panel for Object deletion with " + removedObject + " of " + removedObject.getClass().getName());
		}
		if (removedObject == getCurrentEditedObject()) {
			if (canGoBackward()) {
				goBackward();
			} else {
				if (_controller.getModule().getDefaultObjectToSelect() != removedObject) {
					_controller.setCurrentEditedObjectAsModuleView(_controller.getModule().getDefaultObjectToSelect());
				} else {
					resetModuleView();
				}
			}
		}
		removeHistoryLocationWithObject(removedObject, previousHistory);
		removeHistoryLocationWithObject(removedObject, nextHistory);
		while (_availableObjects.indexOf(removedObject) > -1) {
			int removedIndex = _availableObjects.indexOf(removedObject);
			_availableObjects.remove(removedIndex);
		}
		if (_controlPanel != null) {
			_controlPanel.update();
		}
		if (removedObject != null) {
			removedObject.deleteObserver(this);
		}
	}

	private void removeHistoryLocationWithObject(FlexoModelObject object, Stack<HistoryLocation> locations) {
		Iterator<HistoryLocation> i = locations.iterator();
		while (i.hasNext()) {
			FlexoMainPane.HistoryLocation l = i.next();
			if (l.getObject() == object) {
				i.remove();
			}
		}
	}

	protected void switchToPerspective(FlexoPerspective perspective) {
		_controller.switchToPerspective(perspective);
	}

	protected void switchToModuleViewForObject(FlexoModelObject object) {
		_controller.setCurrentEditedObjectAsModuleView(object);
	}

	protected void switchToModuleViewForObjectAndPerspective(FlexoModelObject object, FlexoPerspective perspective) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Switch to object " + object + " and perspective " + perspective);
		}
		_controller.setCurrentEditedObjectAsModuleView(object, perspective);
	}

	protected void switchToParentModuleViewForObject(FlexoModelObject object) {
		if (getParentObject(object) != null) {
			switchToModuleViewForObject(getParentObject(object));
		}
	}

	protected abstract FlexoModelObject getParentObject(FlexoModelObject object);

	protected FlexoModelObject getCurrentEditedObject() {
		return _controller.getCurrentDisplayedObjectAsModuleView();
	}

	protected ModuleView getModuleViewForObject(FlexoModelObject object) {
		return _controller.moduleViewForObject(object);
	}

	public class HistoryLocation {
		private final FlexoModelObject _object;

		private final FlexoPerspective _perspective;

		protected HistoryLocation(FlexoModelObject object, FlexoPerspective perspective) {
			super();
			_object = object;
			_perspective = perspective;
		}

		public FlexoModelObject getObject() {
			return _object;
		}

		public FlexoPerspective getPerspective() {
			return _perspective;
		}
	}

	public FlexoController getController() {
		return _controller;
	}

	public boolean isCollapseEnabled() {
		return false;
	}

	public void performCollapseAll() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Perform collapse all not implemented for: " + this);
		}
	}

	public boolean isAutoLayoutEnabled() {
		return false;
	}

	public void performAutolayout() {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Perform collapse all not implemented for: " + this);
		}
	}
}
