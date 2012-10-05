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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;

import org.openflexo.ch.FCH;
import org.openflexo.swing.TabbedPane;
import org.openflexo.swing.TabbedPane.TabHeaderRenderer;
import org.openflexo.swing.layout.JXMultiSplitPane;
import org.openflexo.swing.layout.JXMultiSplitPane.DividerPainter;
import org.openflexo.swing.layout.MultiSplitLayout;
import org.openflexo.swing.layout.MultiSplitLayout.Divider;
import org.openflexo.swing.layout.MultiSplitLayout.Leaf;
import org.openflexo.swing.layout.MultiSplitLayout.Node;
import org.openflexo.swing.layout.MultiSplitLayout.Split;
import org.openflexo.toolbox.PropertyChangeListenerRegistrationManager;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.ControllerModel;
import org.openflexo.view.controller.model.FlexoPerspective;

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

	private class EmptyPanel extends JPanel {
		@Override
		protected final void addImpl(Component comp, Object constraints, int index) {
		}
	}

	private FlexoController controller;

	private FlexoPerspective perspective;

	private ModuleView<?> moduleView;

	private MainPaneTopBar topBar;

	private JXMultiSplitPane centerPanel;

	private JComponent moduleViewContainerPanel;

	private JComponent footer;

	private MultiSplitLayout centerLayout;

	private PropertyChangeListenerRegistrationManager registrationManager;

	private TabbedPane<JComponent> tabbedPane;

	private static final int KNOB_SIZE = 5;
	private static final int KNOB_SPACE = 2;
	private static final int DIVIDER_SIZE = KNOB_SIZE + 2 * KNOB_SPACE;
	private static final int DIVIDER_KNOB_SIZE = 3 * KNOB_SIZE + 2 * KNOB_SPACE;

	private static final int ROUNDED_CORNER_SIZE = 8;

	private static final Paint KNOB_PAINTER = new RadialGradientPaint(new Point((KNOB_SIZE - 1) / 2, (KNOB_SIZE - 1) / 2),
			(KNOB_SIZE - 1) / 2, new float[] { 0.0f, 1.0f }, new Color[] { Color.GRAY, Color.LIGHT_GRAY });

	public FlexoMainPane(FlexoController controller) {
		super(new BorderLayout());
		this.controller = controller;
		this.centerLayout = new MultiSplitLayout(false);
		this.centerLayout.setLayoutMode(MultiSplitLayout.NO_MIN_SIZE_LAYOUT);
		registrationManager = new PropertyChangeListenerRegistrationManager();
		registrationManager.new PropertyChangeListenerRegistration(ControllerModel.CURRENT_OBJECT, this, controller.getControllerModel());
		registrationManager.new PropertyChangeListenerRegistration(ControllerModel.CURRENT_PERPSECTIVE, this,
				controller.getControllerModel());
		registrationManager.new PropertyChangeListenerRegistration(ControllerModel.LEFT_VIEW_VISIBLE, this, controller.getControllerModel());
		registrationManager.new PropertyChangeListenerRegistration(ControllerModel.RIGHT_VIEW_VISIBLE, this,
				controller.getControllerModel());
		registrationManager.new PropertyChangeListenerRegistration(FlexoController.MODULE_VIEWS, this, controller);
		perspective = controller.getCurrentPerspective();
		tabbedPane = new TabbedPane<JComponent>(new TabHeaderRenderer<JComponent>() {

			@Override
			public Icon getTabHeaderIcon(JComponent tab) {
				if (tab instanceof ModuleView) {
					return FlexoMainPane.this.controller.iconForObject(((ModuleView<?>) tab).getRepresentedObject());
				}
				return null;
			}

			@Override
			public String getTabHeaderTitle(JComponent tab) {
				if (tab instanceof ModuleView) {
					return FlexoMainPane.this.controller.getWindowTitleforObject(((ModuleView<?>) tab).getRepresentedObject());
				}
				return null;
			}

			@Override
			public String getTabHeaderTooltip(JComponent tab) {
				return null;
			}

		});
		tabbedPane.setUseTabBody(false);
		tabbedPane.addToTabListeners(new TabbedPane.TabListener<JComponent>() {

			@Override
			public void tabSelected(JComponent tab) {
				if (tab != null) {
					FlexoMainPane.this.controller.getControllerModel().setCurrentObjectAndPerspective(
							((ModuleView<?>) tab).getRepresentedObject(), ((ModuleView<?>) tab).getPerspective());
				}
			}

			@Override
			public void tabClosed(JComponent tab) {
				((ModuleView<?>) tab).deleteModuleView();
			}
		});
		add(topBar = new MainPaneTopBar(controller.getControllerModel()), BorderLayout.NORTH);
		add(centerPanel = new JXMultiSplitPane(centerLayout));
		centerPanel.setDividerSize(DIVIDER_SIZE);
		centerPanel.setDividerPainter(new DividerPainter() {

			@Override
			protected void doPaint(Graphics2D g, Divider divider, int width, int height) {
				if (!divider.isVisible()) {
					return;
				}
				if (divider.isVertical()) {
					int x = (width - KNOB_SIZE) / 2;
					int y = (height - DIVIDER_KNOB_SIZE) / 2;
					for (int i = 0; i < 3; i++) {
						Graphics2D graph = (Graphics2D) g.create(x, y + i * (KNOB_SIZE + KNOB_SPACE), KNOB_SIZE + 1, KNOB_SIZE + 1);
						graph.setPaint(KNOB_PAINTER);
						graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
					}
				} else {
					int x = (width - DIVIDER_KNOB_SIZE) / 2;
					int y = (height - KNOB_SIZE) / 2;
					for (int i = 0; i < 3; i++) {
						Graphics2D graph = (Graphics2D) g.create(x + i * (KNOB_SIZE + KNOB_SPACE), y, KNOB_SIZE + 1, KNOB_SIZE + 1);
						graph.setPaint(KNOB_PAINTER);
						graph.fillOval(0, 0, KNOB_SIZE, KNOB_SIZE);
					}
				}

			}
		});
		moduleViewContainerPanel = new JPanel();
		moduleViewContainerPanel.setLayout(new BorderLayout());
		moduleViewContainerPanel.add(tabbedPane.getTabHeaders(), BorderLayout.NORTH);
	}

	public FlexoController getController() {
		return controller;
	}

	public void dispose() {
		saveLayout();
		registrationManager.delete();
	}

	public void resetModuleView() {
		setModuleView(null);
	}

	private void setModuleView(ModuleView<?> moduleView) {
		if (this.moduleView == moduleView) {
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setModuleView() with " + moduleView + " perspective " + moduleView.getPerspective());
		}
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
				JScrollPane scrollPane = new JScrollPane((JComponent) moduleView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
		newCenterView.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.LIGHT_GRAY));
		updateLayoutForPerspective();
		Component centerComponent = ((BorderLayout) moduleViewContainerPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if (centerComponent != null) {
			moduleViewContainerPanel.remove(centerComponent);
		}
		moduleViewContainerPanel.add(newCenterView);
		moduleViewContainerPanel.revalidate();
		updateComponent(moduleViewContainerPanel, LayoutPosition.MIDDLE_CENTER);
		centerPanel.revalidate();
		if (moduleView != null) {
			controller.getCurrentPerspective().notifyModuleViewDisplayed(moduleView);
		}
		if (controller.getFlexoFrame().isValid()) {
			FCH.validateWindow(controller.getFlexoFrame());
		}
		controller.getFlexoFrame().updateTitle();
		revalidate();
		repaint();
	}

	private boolean updateBottomCenterView() {
		JComponent newBottomCenterView = perspective.getBottomCenterView();
		updateComponent(newBottomCenterView, LayoutPosition.BOTTOM_CENTER);
		return newBottomCenterView != null;
	}

	private boolean updateBottomRightView() {
		JComponent newBottomRightView = perspective.getBottomRightView();
		updateComponent(newBottomRightView, LayoutPosition.BOTTOM_RIGHT);
		return newBottomRightView != null;
	}

	private boolean updateBottomLeftView() {
		JComponent newBottomLeftView = perspective.getBottomLeftView();
		updateComponent(newBottomLeftView, LayoutPosition.BOTTOM_LEFT);
		return newBottomLeftView != null;
	}

	private boolean updateMiddleRightView() {
		JComponent newMiddleRightView = perspective.getMiddleRightView();
		updateComponent(newMiddleRightView, LayoutPosition.MIDDLE_RIGHT);
		return newMiddleRightView != null;
	}

	private boolean updateMiddleLeftView() {
		JComponent newMiddleLeftView = perspective.getMiddleLeftView();
		updateComponent(newMiddleLeftView, LayoutPosition.MIDDLE_LEFT);
		return newMiddleLeftView != null;
	}

	private boolean updateTopCenterView() {
		JComponent newTopCenterView = perspective.getTopCenterView();
		updateComponent(newTopCenterView, LayoutPosition.TOP_CENTER);
		return newTopCenterView != null;
	}

	private boolean updateTopRightView() {
		JComponent newTopRightView = perspective.getTopRightView();
		updateComponent(newTopRightView, LayoutPosition.TOP_RIGHT);
		return newTopRightView != null;
	}

	private boolean updateTopLeftView() {
		JComponent newTopLeftView = perspective.getTopLeftView();
		updateComponent(newTopLeftView, LayoutPosition.TOP_LEFT);
		return newTopLeftView != null;
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
		JComponent toAdd = next != null ? next : new EmptyPanel();
		if (previous != toAdd) {
			if (previous != null) {
				centerPanel.remove(previous);
			}
			toAdd.setPreferredSize(new Dimension(0, 0));
			centerPanel.add(toAdd, position.name());
			centerLayout.displayNode(position.name(), next != null);
			Node node = centerLayout.getNodeForName(position.name());
			Split parent = node.getParent();
			if (parent != centerLayout.getNodeForName(LayoutColumns.CENTER.name())) {
				fixWeightForNodeChildren(parent);
			}
			centerPanel.revalidate();
		}
	}

	protected void fixWeightForNodeChildren(Split split) {
		int visibleChildren = 0;
		// double weight = 0.0;
		for (Node child : split.getChildren()) {
			if (!(child instanceof Divider) && child.isVisible()) {
				visibleChildren++;
				// weight+=child.getWeight();
			}
		}
		for (Node child : split.getChildren()) {
			if (!(child instanceof Divider) && child.isVisible()) {
				child.setWeight(1.0 / visibleChildren);
			}
		}
	}

	private void saveLayout() {
		if (perspective != null) {
			getController().getControllerModel().setLayoutForPerspective(perspective, centerLayout.getModel());
		}
	}

	private void restoreLayout() {
		if (perspective == null) {
			return;
		}
		Node layoutModel = getController().getControllerModel().getLayoutForPerspective(perspective);
		if (layoutModel == null) {
			layoutModel = getDefaultLayout();
			perspective.setupDefaultLayout(layoutModel);
		}
		centerLayout.setModel(layoutModel);
		centerPanel.revalidate();
	}

	private Split getDefaultLayout() {
		Split root = new Split();
		root.setName("ROOT");
		Split left = getVerticalSplit(LayoutPosition.TOP_LEFT, LayoutPosition.MIDDLE_LEFT, LayoutPosition.BOTTOM_LEFT);
		left.setWeight(0.2);
		left.setName(LayoutColumns.LEFT.name());
		Split center = getVerticalSplit(LayoutPosition.TOP_CENTER, LayoutPosition.MIDDLE_CENTER, LayoutPosition.BOTTOM_CENTER);
		center.setWeight(0.6);
		center.setName(LayoutColumns.CENTER.name());
		Split right = getVerticalSplit(LayoutPosition.TOP_RIGHT, LayoutPosition.MIDDLE_RIGHT, LayoutPosition.BOTTOM_RIGHT);
		right.setWeight(0.2);
		right.setName(LayoutColumns.RIGHT.name());
		root.setChildren(left, new Divider(), center, new Divider(), right);
		return root;
	}

	private Split getVerticalSplit(LayoutPosition position1, LayoutPosition position2, LayoutPosition position3) {
		Split split = new Split();
		split.setRowLayout(false);
		Leaf l1 = new Leaf(position1.name());
		l1.setWeight(0.2);
		Leaf l2 = new Leaf(position2.name());
		l2.setWeight(0.6);
		Leaf l3 = new Leaf(position3.name());
		l3.setWeight(0.2);
		split.setChildren(l1, new Divider(), l2, new Divider(), l3);
		return split;
	}

	private Node getNodeForName(Node root, String name) {
		if (root instanceof Split) {
			Split split = (Split) root;
			if (name.equals(split.getName())) {
				return split;
			}
			for (Node child : split.getChildren()) {
				Node n = getNodeForName(child, name);
				if (n != null) {
					return n;
				}
			}
		} else if (root instanceof Leaf) {
			Leaf leaf = (Leaf) root;
			if (name.equals(leaf.getName())) {
				return leaf;
			}
		}
		return null;
	}

	private JComponent getComponentForPosition(LayoutPosition position) {
		Node node = centerLayout.getNodeForName(position.name());
		if (node != null) {
			return (JComponent) centerLayout.getComponentForNode(node);
		} else {
			return null;
		}
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
		Node left = getNodeForName(centerLayout.getModel(), LayoutColumns.LEFT.name());
		updateVisibility(left, controller.getControllerModel().isLeftViewVisible());
		centerPanel.revalidate();
	}

	private void updateRightViewVisibility() {
		Node right = getNodeForName(centerLayout.getModel(), LayoutColumns.RIGHT.name());
		updateVisibility(right, controller.getControllerModel().isRightViewVisible());
		centerPanel.revalidate();
	}

	private void updateVisibility(Node root, boolean visible) {
		if (root instanceof Leaf) {
			Component componentForNode = centerLayout.getComponentForNode(root);
			if (componentForNode instanceof EmptyPanel) {
				// EmptyPanel means that there is nothing to display/hide here
			} else {
				centerLayout.displayNode(((Leaf) root).getName(), visible);
			}
		} else if (root instanceof Split) {
			Split split = (Split) root;
			centerLayout.displayNode(split.getName(), visible);
			for (Node child : split.getChildren()) {
				updateVisibility(child, visible);
			}
			fixWeightForNodeChildren(split);
		}
		centerPanel.revalidate();
		centerPanel.repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == controller.getControllerModel()) {
			if (evt.getPropertyName().equals(ControllerModel.CURRENT_PERPSECTIVE)) {
				FlexoPerspective previous = (FlexoPerspective) evt.getOldValue();
				FlexoPerspective next = (FlexoPerspective) evt.getNewValue();
				saveLayout();
				perspective = next;
				setModuleView(controller.moduleViewForObject(controller.getCurrentDisplayedObjectAsModuleView()));
				updatePropertyChangeListener(previous, next);
				updateLayoutForPerspective();
			} else if (evt.getPropertyName().equals(ControllerModel.LEFT_VIEW_VISIBLE)) {
				updateLeftViewVisibility();
			} else if (evt.getPropertyName().equals(ControllerModel.RIGHT_VIEW_VISIBLE)) {
				updateRightViewVisibility();
			} else if (evt.getPropertyName().equals(ControllerModel.CURRENT_OBJECT)) {
				ModuleView<?> moduleView = controller.moduleViewForObject(controller.getControllerModel().getCurrentObject());
				if (moduleView != null) {
					setModuleView(moduleView);
					tabbedPane.selectTab((JComponent) moduleView);
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find module view for object " + controller.getControllerModel().getCurrentObject());
					}
				}
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
		} else if (evt.getSource() == controller) {
			if (evt.getPropertyName().equals(FlexoController.MODULE_VIEWS)) {
				if (evt.getNewValue() != null) {
					tabbedPane.addTab((JComponent) evt.getNewValue());
				} else if (evt.getOldValue() != null) {
					tabbedPane.removeTab((JComponent) evt.getOldValue());
				}
			}
		}
	}

	private void updateLayoutForPerspective() {
		if (perspective == null) {
			return;
		}
		restoreLayout();

		boolean hasLeftView = false;
		boolean hasRightView = false;

		hasLeftView |= updateTopLeftView();
		hasRightView |= updateTopRightView();
		updateTopCenterView();

		hasLeftView |= updateMiddleLeftView();
		hasRightView |= updateMiddleRightView();

		hasLeftView |= updateBottomLeftView();
		hasRightView |= updateBottomRightView();
		updateBottomCenterView();

		updateHeader();
		updateFooter();

		updateLeftViewVisibility();
		updateRightViewVisibility();

		topBar.setLeftViewToggle(hasLeftView);
		topBar.setRightViewToggle(hasRightView);

	}

	private void updatePropertyChangeListener(FlexoPerspective previous, FlexoPerspective next) {
		if (previous != null) {
			for (String property : FlexoPerspective.PROPERTIES) {
				registrationManager.removeListener(property, this, next);
			}
		}
		if (next != null) {
			for (String property : FlexoPerspective.PROPERTIES) {
				registrationManager.new PropertyChangeListenerRegistration(property, this, next);
			}
		}

	}
}
