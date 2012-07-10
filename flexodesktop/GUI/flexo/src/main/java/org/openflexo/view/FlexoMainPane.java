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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.openflexo.ch.FCH;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.controller.model.RootControllerModel;

/**
 * Abstract view managing global layout of a FlexoModule
 * 
 * @author sguerin
 */
public abstract class FlexoMainPane extends JPanel implements PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(FlexoMainPane.class.getPackage().getName());

	public static enum LayoutPosition {
		TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT, TOP_CENTER, MIDDLE_CENTER, BOTTOM_CENTER, TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT;
	}

	public static enum LayoutColumns {
		LEFT, CENTER, RIGHT;
	}

	private FlexoController controller;

	private ModuleView<?> moduleView;

	private MainPaneTopBar topBar;

	private JXMultiSplitPane centerPanel;

	private JComponent footer;

	private MultiSplitLayout centerLayout;

	public FlexoMainPane(FlexoController controller) {
		super(new BorderLayout());
		this.controller = controller;
		this.centerLayout = new MultiSplitLayout(false);
		this.controller.getControllerModel().getPropertyChangeSupport().addPropertyChangeListener(RootControllerModel.CURRENT_OBJECT, this);
		this.controller.getControllerModel().getPropertyChangeSupport()
				.addPropertyChangeListener(RootControllerModel.CURRENT_PERPSECTIVE, this);
		this.controller.getControllerModel().getPropertyChangeSupport()
				.addPropertyChangeListener(RootControllerModel.LEFT_VIEW_VISIBLE, this);
		this.controller.getControllerModel().getPropertyChangeSupport()
				.addPropertyChangeListener(RootControllerModel.RIGHT_VIEW_VISIBLE, this);
		add(topBar = new MainPaneTopBar(controller.getControllerModel()), BorderLayout.NORTH);
		add(centerPanel = new JXMultiSplitPane(centerLayout));
		updateLeftViewVisibility();
		updateRightViewVisibility();
	}

	public void resetModuleView() {
		setModuleView(null);
	}

	private void setModuleView(ModuleView<?> moduleView) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setModuleView() with " + moduleView + " perspective " + moduleView.getPerspective());
		}
		saveLayout();
		try {
			if (this.moduleView != null) {
				this.moduleView.willHide();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("willHide call failed on " + moduleView);
			}
		}

		if (this.moduleView != null && this.moduleView instanceof SelectionSynchronizedModuleView) {
			controller.getSelectionManager().removeFromSelectionListeners(
					((SelectionSynchronizedModuleView<?>) this.moduleView).getSelectionListeners());
		}
		this.moduleView = moduleView;
		if (moduleView != null && moduleView instanceof SelectionSynchronizedModuleView) {
			controller.getSelectionManager().addToSelectionListeners(
					((SelectionSynchronizedModuleView<?>) moduleView).getSelectionListeners());
		}

		JComponent newCenterView = null;

		if (moduleView != null) {
			if (moduleView.isAutoscrolled()) {
				newCenterView = (JComponent) moduleView;
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
				scrollPane.getHorizontalScrollBar().setFocusable(false);
				scrollPane.getVerticalScrollBar().setFocusable(false);

				if (moduleView instanceof ChangeListener) {
					scrollPane.getViewport().addChangeListener((ChangeListener) moduleView);
				}
				newCenterView = scrollPane;
			}
			try {
				moduleView.willShow();
			} catch (RuntimeException e) {
				e.printStackTrace();
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("willShow call failed on " + moduleView);
				}
			}
			FCH.setHelpItem((JComponent) moduleView, FCH.getModuleViewItemFor(controller.getModule(), moduleView));
		} else {
			newCenterView = new JPanel();
		}

		restoreLayout();

		updateTopLeftView();
		updateTopRightView();
		updateTopCenterView();
		updateMiddleLeftView();
		updateMiddleRightView();
		updateBottomLeftView();
		updateBottomRightView();
		updateBottomCenterView();
		updateHeader();
		updateFooter();

		updateComponent(newCenterView, LayoutPosition.MIDDLE_CENTER);
		centerPanel.revalidate();

		if (controller.getFlexoFrame().isValid()) {
			FCH.validateWindow(controller.getFlexoFrame());
		}
		controller.getFlexoFrame().updateTitle();
		revalidate();
		repaint();
	}

	private void updateBottomCenterView() {
		JComponent newBottomCenterView = getController().getCurrentPerspective().getBottomCenterView();
		updateComponent(newBottomCenterView, LayoutPosition.BOTTOM_CENTER);
	}

	private void updateBottomRightView() {
		JComponent newBottomRightView = getController().getCurrentPerspective().getBottomRightView();
		updateComponent(newBottomRightView, LayoutPosition.BOTTOM_RIGHT);
	}

	private void updateBottomLeftView() {
		JComponent newBottomLeftView = getController().getCurrentPerspective().getBottomLeftView();
		updateComponent(newBottomLeftView, LayoutPosition.BOTTOM_LEFT);
	}

	private void updateMiddleRightView() {
		JComponent newMiddleRightView = getController().getCurrentPerspective().getMiddleRightView();
		updateComponent(newMiddleRightView, LayoutPosition.MIDDLE_RIGHT);
	}

	private void updateMiddleLeftView() {
		JComponent newMiddleLeftView = getController().getCurrentPerspective().getMiddleLeftView();
		updateComponent(newMiddleLeftView, LayoutPosition.MIDDLE_LEFT);
	}

	private void updateTopCenterView() {
		JComponent newTopCenterView = getController().getCurrentPerspective().getTopCenterView();
		updateComponent(newTopCenterView, LayoutPosition.TOP_CENTER);
	}

	private void updateTopRightView() {
		JComponent newTopRightView = getController().getCurrentPerspective().getTopRightView();
		updateComponent(newTopRightView, LayoutPosition.TOP_RIGHT);
	}

	private void updateTopLeftView() {
		JComponent newTopLeftView = getController().getCurrentPerspective().getTopLeftView();
		updateComponent(newTopLeftView, LayoutPosition.TOP_LEFT);
	}

	private void updateFooter() {
		if (footer != controller.getCurrentPerspective().getFooter()) {
			if (footer != null) {
				remove(footer);
			}
			if (controller.getCurrentPerspective().getFooter() != null) {
				add(controller.getCurrentPerspective().getFooter(), BorderLayout.SOUTH);
			}
			footer = controller.getCurrentPerspective().getFooter();
		}
	}

	private void updateHeader() {
		if (moduleView != null) {
			topBar.setHeader(controller.getCurrentPerspective().getHeader());
		} else {
			topBar.setHeader(null);
		}
	}

	private void updateComponent(JComponent next, LayoutPosition position) {
		JComponent previous = getComponentForPosition(position);
		JComponent toAdd = next != null ? next : new JPanel();
		if (previous != next) {
			if (previous != null) {
				centerPanel.remove(previous);
			}
			centerPanel.add(toAdd, position.name());
			centerLayout.displayNode(position.name(), next != null);
			Node node = centerLayout.getNodeForName(position.name());
			Split parent = node.getParent();
			if (parent != centerLayout.getNodeForName(LayoutColumns.CENTER.name())) {
				int visibleChildren = 0;
				for (Node child : parent.getChildren()) {
					if (!(child instanceof Divider) && child.isVisible()) {
						visibleChildren++;
					}
				}
				for (Node child : parent.getChildren()) {
					if (!(child instanceof Divider) && child.isVisible()) {
						child.setWeight(1.0 / visibleChildren);
					}
				}
			}
			centerPanel.revalidate();
		}
	}

	private void saveLayout() {
		if (controller.getCurrentPerspective() != null) {
			getController().getControllerModel().setLayoutForPerspective(controller.getCurrentPerspective(), centerLayout.getModel());
		}
	}

	private void restoreLayout() {
		Node layoutModel = getController().getControllerModel().getLayoutForPerspective(controller.getCurrentPerspective());
		if (layoutModel == null) {
			layoutModel = getDefaultLayout();
			controller.getCurrentPerspective().setupDefaultLayout(layoutModel);
		}
		centerLayout.setModel(layoutModel);
		centerPanel.revalidate();
	}

	private Split getDefaultLayout() {
		Split root = new Split();
		root.setName("ROOT");
		Split left = getVerticalSplit(LayoutPosition.TOP_LEFT, LayoutPosition.MIDDLE_LEFT, LayoutPosition.BOTTOM_LEFT);
		left.setWeight(0);
		left.setName(LayoutColumns.LEFT.name());
		Split center = getVerticalSplit(LayoutPosition.TOP_CENTER, LayoutPosition.MIDDLE_CENTER, LayoutPosition.BOTTOM_CENTER);
		center.setWeight(0);
		center.setName(LayoutColumns.CENTER.name());
		Split right = getVerticalSplit(LayoutPosition.TOP_RIGHT, LayoutPosition.MIDDLE_RIGHT, LayoutPosition.BOTTOM_RIGHT);
		right.setWeight(0);
		right.setName(LayoutColumns.RIGHT.name());
		root.setChildren(left, new Divider(), center, new Divider(), right);
		return root;
	}

	private Split getVerticalSplit(LayoutPosition position1, LayoutPosition position2, LayoutPosition position3) {
		Split split = new Split();
		split.setRowLayout(false);
		Leaf l1 = new Leaf(position1.name());
		l1.setWeight(0);
		Leaf l2 = new Leaf(position2.name());
		l2.setWeight(1);
		Leaf l3 = new Leaf(position3.name());
		l3.setWeight(0);
		split.setChildren(l1, new Divider(), l2, new Divider(), l3);
		return split;
	}

	private JComponent getComponentForPosition(LayoutPosition position) {
		return (JComponent) centerLayout.getComponentForNode(centerLayout.getNodeForName(position.name()));
	}

	public ModuleView<?> getModuleView() {
		return moduleView;
	}

	public void showControlPanel() {
		topBar.setVisible(true);
	}

	public void hideControlPanel() {
		topBar.setVisible(false);
	}

	private void updateLeftViewVisibility() {
		centerLayout.displayNode(LayoutColumns.LEFT.name(), controller.getControllerModel().isLeftViewVisible());
	}

	private void updateRightViewVisibility() {
		centerLayout.displayNode(LayoutColumns.RIGHT.name(), controller.getControllerModel().isRightViewVisible());
	}

	public FlexoController getController() {
		return controller;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == controller.getControllerModel()) {
			if (evt.getPropertyName().equals(RootControllerModel.CURRENT_PERPSECTIVE)) {
				FlexoPerspective previous = (FlexoPerspective) evt.getOldValue();
				FlexoPerspective next = (FlexoPerspective) evt.getNewValue();
				updatePropertyChangeListener(previous, next);
				updateLayoutForPerspective();
			} else if (evt.getPropertyName().equals(RootControllerModel.LEFT_VIEW_VISIBLE)) {
				updateLeftViewVisibility();
			} else if (evt.getPropertyName().equals(RootControllerModel.RIGHT_VIEW_VISIBLE)) {
				updateRightViewVisibility();
			} else if (evt.getPropertyName().equals(RootControllerModel.CURRENT_OBJECT)) {
				setModuleView(controller.moduleViewForObject(controller.getControllerModel().getCurrentObject()));
			}
		} else if (evt.getSource() == controller.getCurrentPerspective()) {
			if (evt.getPropertyName().equals(FlexoPerspective.HEADER)) {
				updateHeader();
			} else if (evt.getPropertyName().equals(FlexoPerspective.FOOTER)) {
				updateFooter();
			} else if (evt.getPropertyName().equals(FlexoPerspective.TOP_LEFT_VIEW)) {
				updateTopLeftView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.TOP_RIGHT_VIEW)) {
				updateTopRightView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.TOP_CENTER_VIEW)) {
				updateTopCenterView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.MIDDLE_LEFT_VIEW)) {
				updateMiddleLeftView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.MIDDLE_RIGHT_VIEW)) {
				updateMiddleRightView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.BOTTOM_LEFT_VIEW)) {
				updateBottomLeftView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.BOTTOM_RIGHT_VIEW)) {
				updateBottomRightView();
			} else if (evt.getPropertyName().equals(FlexoPerspective.BOTTOM_CENTER_VIEW)) {
				updateBottomCenterView();
			}
		}
	}

	private void updateLayoutForPerspective() {
		restoreLayout();

		updateTopLeftView();
		updateTopRightView();
		updateTopCenterView();
		updateMiddleLeftView();
		updateMiddleRightView();
		updateBottomLeftView();
		updateBottomRightView();
		updateBottomCenterView();
		updateHeader();
		updateFooter();

	}

	private void updatePropertyChangeListener(FlexoPerspective previous, FlexoPerspective next) {
		if (previous != null) {
			for (String property : FlexoPerspective.PROPERTIES) {
				previous.getPropertyChangeSupport().removePropertyChangeListener(property, this);
			}
		}
		if (next != null) {
			for (String property : FlexoPerspective.PROPERTIES) {
				next.getPropertyChangeSupport().addPropertyChangeListener(property, this);
			}
		}

	}
}
