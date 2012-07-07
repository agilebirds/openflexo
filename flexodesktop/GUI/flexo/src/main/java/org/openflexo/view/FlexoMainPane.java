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
import org.openflexo.view.controller.model.RootControllerModel;

/**
 * Abstract view managing global layout of a FlexoModule
 * 
 * @author sguerin
 */
public abstract class FlexoMainPane extends JPanel implements PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(FlexoMainPane.class.getPackage().getName());

	public static final String LEFT = "left";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";

	public static final String TOP = "top";
	public static final String MIDDLE = "middle";
	public static final String BOTTOM = "bottom";

	private FlexoController controller;

	private ModuleView<?> moduleView;

	private MainPaneTopBar topBar;

	private JXMultiSplitPane centralPanel;

	private JComponent footer;

	private MultiSplitLayout centralLayout;

	public FlexoMainPane(FlexoController controller) {
		super(new BorderLayout());
		this.controller = controller;
		this.centralLayout = new MultiSplitLayout(false);
		this.controller.getControllerModel().getPropertyChangeSupport().addPropertyChangeListener(RootControllerModel.PERSPECTIVES, this);
		add(topBar = new MainPaneTopBar(controller.getControllerModel()), BorderLayout.NORTH);
		add(centralPanel = new JXMultiSplitPane(centralLayout));
	}

	public void resetModuleView() {
		setModuleView(null);
	}

	public void setModuleView(ModuleView<?> moduleView) {
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

		JComponent newTopLeftView = getController().getCurrentPerspective().getTopLeftView();
		JComponent newTopRightView = getController().getCurrentPerspective().getTopRightView();
		JComponent newBottomLeftView = getController().getCurrentPerspective().getBottomLetfView();
		JComponent newBottomRightView = getController().getCurrentPerspective().getBottomRightView();
		JComponent newTopCentraltView = getController().getCurrentPerspective().getTopCentralView();
		JComponent newBottomCentralView = getController().getCurrentPerspective().getBottomCentralView();
		JComponent newCentralView = null;

		if (moduleView != null) {
			if (moduleView.isAutoscrolled()) {
				newCentralView = (JComponent) moduleView;
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
				newCentralView = scrollPane;
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
			newCentralView = new JPanel();
		}

		restoreLayout();

		updateComponent(newTopLeftView, TOP + LEFT);
		updateComponent(newTopRightView, TOP + RIGHT);
		updateComponent(newBottomLeftView, BOTTOM + LEFT);
		updateComponent(newBottomRightView, BOTTOM + RIGHT);
		updateComponent(newTopCentraltView, CENTER + RIGHT);
		updateComponent(newBottomCentralView, CENTER + LEFT);
		updateComponent(newCentralView, CENTER + MIDDLE);

		centralPanel.revalidate();

		if (controller.getFlexoFrame().isValid()) {
			FCH.validateWindow(controller.getFlexoFrame());
		}
		if (moduleView != null) {
			topBar.setHeader(controller.getCurrentPerspective().getHeader());
		} else {
			topBar.setHeader(null);
		}
		if (footer != controller.getCurrentPerspective().getFooter()) {
			if (footer != null) {
				remove(footer);
			}
			if (controller.getCurrentPerspective().getFooter() != null) {
				add(controller.getCurrentPerspective().getFooter(), BorderLayout.SOUTH);
			}
			footer = controller.getCurrentPerspective().getFooter();
		}
		controller.getFlexoFrame().updateTitle();
		revalidate();
		repaint();
	}

	private void updateComponent(JComponent next, String name) {
		JComponent previous = getComponentForName(name);
		if (previous != next) {
			if (previous != null) {
				centralPanel.remove(previous);
			}
			centralLayout.displayNode(name, next != null);
			if (next != null) {
				centralPanel.add(next, name);
			}
		}
	}

	private void saveLayout() {
		getController().getControllerModel().setLayoutForPerspective(controller.getCurrentPerspective(), centralLayout.getModel());
	}

	private void restoreLayout() {
		Node layoutModel = getController().getControllerModel().getLayoutForPerspective(controller.getCurrentPerspective());
		if (layoutModel == null) {
			layoutModel = getDefaultLayout();
			controller.getCurrentPerspective().setupDefaultLayout(layoutModel);
		}
		centralLayout.setModel(layoutModel);
		centralPanel.revalidate();
	}

	private Split getDefaultLayout() {
		Split root = new Split();
		root.setName("ROOT");
		Split left = getVerticalSplit(LEFT, 0.5, 0.5);
		left.setWeight(0);
		left.setName(LEFT);
		Split center = new Split();
		Leaf top = new Leaf();
		top.setName(TOP + CENTER);
		Leaf middle = new Leaf();
		middle.setName(MIDDLE + CENTER);
		middle.setWeight(1.0);
		Leaf bottom = new Leaf();
		middle.setName(BOTTOM + CENTER);
		center.setRowLayout(false);
		center.setChildren(top, new Divider(), middle, new Divider(), bottom);
		center.setWeight(1.0);
		center.setName(CENTER);
		Split right = getVerticalSplit(RIGHT, 0.5, 0.5);
		right.setWeight(0);
		right.setName(RIGHT);
		root.setChildren(left, new Divider(), center, new Divider(), right);
		return root;
	}

	private Split getVerticalSplit(String name, double topWeight, double bottomWeight) {
		Split split = new Split();
		split.setRowLayout(false);
		Leaf top = new Leaf(TOP + name);
		top.setWeight(topWeight);
		Leaf bottom = new Leaf(BOTTOM + name);
		bottom.setWeight(bottomWeight);
		split.setChildren(top, new Divider(), bottom);
		return split;
	}

	protected JComponent getComponentForName(String name) {
		return (JComponent) centralLayout.getComponentForNode(centralLayout.getNodeForName(name));
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

	public FlexoController getController() {
		return controller;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}
}
