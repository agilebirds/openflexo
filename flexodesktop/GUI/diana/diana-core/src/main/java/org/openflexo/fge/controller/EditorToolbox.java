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
package org.openflexo.fge.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JToolBar;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;

@SuppressWarnings("serial")
public class EditorToolbox {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditorToolbox.class.getPackage().getName());

	private ToolPanel toolPanel;

	private JToolBar stylesToolBar;
	private LayoutToolBar layoutToolBar;
	private FIBForegroundStyleSelector foregroundSelector;
	private FIBBackgroundStyleSelector backgroundSelector;
	private FIBTextStyleSelector textStyleSelector;
	private FIBShadowStyleSelector shadowStyleSelector;
	private FIBShapeSelector shapeSelector;

	private DrawingControllerImpl<?> controller;

	private List<ShapeNode<?>> selectedShapes = new ArrayList<ShapeNode<?>>();
	private List<ConnectorNode<?>> selectedConnectors = new ArrayList<ConnectorNode<?>>();
	private List<DrawingTreeNode<?, ?>> selection = new ArrayList<DrawingTreeNode<?, ?>>();

	public EditorToolbox(DrawingControllerImpl<?> controller) {
		super();
		this.controller = controller;
	}

	public void delete() {

		if (backgroundSelector != null) {
			backgroundSelector.delete();
			backgroundSelector = null;
		}
		if (foregroundSelector != null) {
			foregroundSelector.delete();
			foregroundSelector = null;
		}
		if (shadowStyleSelector != null) {
			shadowStyleSelector.delete();
			shadowStyleSelector = null;
		}
		if (shapeSelector != null) {
			shapeSelector.delete();
			shapeSelector = null;
		}
		if (textStyleSelector != null) {
			textStyleSelector.delete();
			textStyleSelector = null;
		}
		selectedShapes.clear();
		selectedConnectors.clear();
		selection.clear();
		controller = null;// Don't delete, we did not create it
	}

	public DrawingControllerImpl<?> getController() {
		return controller;
	}

	public ToolPanel getToolPanel() {
		if (toolPanel == null) {
			toolPanel = new ToolPanel(this);
		}
		return toolPanel;
	}

	public JToolBar getStyleToolBar() {
		if (stylesToolBar == null) {
			stylesToolBar = new JToolBar();
			stylesToolBar.setRollover(true);
			foregroundSelector = new FIBForegroundStyleSelector(controller.getCurrentForegroundStyle()) {
				@Override
				public void apply() {
					super.apply();
					if (selection.size() > 0) {
						for (ShapeNode<?> shape : selectedShapes) {
							shape.getGraphicalRepresentation().setForeground(getEditedObject().clone());
						}
						for (ConnectorNode<?> connector : selectedConnectors) {
							connector.getGraphicalRepresentation().setForeground(getEditedObject().clone());
						}
					} else {
						controller.setCurrentForegroundStyle(getEditedObject().clone());
					}
				}
			};
			backgroundSelector = new FIBBackgroundStyleSelector(controller.getCurrentBackgroundStyle()) {
				@Override
				public void apply() {
					super.apply();
					if (selectedShapes.size() > 0) {
						for (ShapeNode<?> shape : selectedShapes) {
							shape.getGraphicalRepresentation().setBackground(getEditedObject().clone());
						}
					} else {
						controller.setCurrentBackgroundStyle(getEditedObject().clone());
					}
				}
			};
			textStyleSelector = new FIBTextStyleSelector(controller.getCurrentTextStyle()) {
				@Override
				public void apply() {
					super.apply();
					if (selection.size() > 0) {
						for (DrawingTreeNode<?, ?> gr : selection) {
							gr.getGraphicalRepresentation().setTextStyle(getEditedObject().clone());
						}
					} else {
						controller.setCurrentTextStyle(getEditedObject().clone());
					}
				}
			};
			shadowStyleSelector = new FIBShadowStyleSelector(controller.getCurrentShadowStyle()) {
				@Override
				public void apply() {
					super.apply();
					if (selectedShapes.size() > 0) {
						for (ShapeNode<?> shape : selectedShapes) {
							shape.getGraphicalRepresentation().setShadowStyle(getEditedObject().clone());
						}
					} else {
						controller.setCurrentShadowStyle(getEditedObject().clone());
					}
				}
			};
			shapeSelector = new FIBShapeSelector(controller.getCurrentShape()) {
				@Override
				public void apply() {
					super.apply();
					if (selectedShapes.size() > 0) {
						for (ShapeNode<?> shape : selectedShapes) {
							shape.getGraphicalRepresentation().setShapeSpecification(getEditedObject().clone());
						}

					} else {
						controller.setCurrentShape(getEditedObject().clone());
					}
				}
			};
			stylesToolBar.add(getToolPanel());
			stylesToolBar.addSeparator();
			stylesToolBar.add(foregroundSelector);
			stylesToolBar.add(backgroundSelector);
			stylesToolBar.add(shapeSelector);
			stylesToolBar.add(shadowStyleSelector);
			stylesToolBar.add(textStyleSelector);
			stylesToolBar.add(Box.createHorizontalGlue());
			stylesToolBar.validate();
		}
		return stylesToolBar;
	}

	public JToolBar getLayoutToolBar() {
		if (layoutToolBar == null) {
			layoutToolBar = new LayoutToolBar(this);
		}
		return layoutToolBar;
	}

	public void update() {
		selectedShapes.clear();
		selectedConnectors.clear();
		selection.clear();
		for (DrawingTreeNode<?, ?> node : controller.getSelectedObjects()) {
			if (node instanceof ShapeNode) {
				selection.add(node);
				selectedShapes.add((ShapeNode<?>) node);
			}
			if (node instanceof ConnectorNode) {
				selection.add(node);
				selectedConnectors.add((ConnectorNode<?>) node);
			}
		}
		if (stylesToolBar == null) {
			return;
		}
		if (selection.size() > 0) {
			textStyleSelector.setEditedObject(selection.get(0).getGraphicalRepresentation().getTextStyle());
			if (selectedShapes.size() > 0) {
				foregroundSelector.setEditedObject(selectedShapes.get(0).getGraphicalRepresentation().getForeground());
			} else if (selectedConnectors.size() > 0) {
				foregroundSelector.setEditedObject(selectedConnectors.get(0).getGraphicalRepresentation().getForeground());
			}
		} else {
			textStyleSelector.setEditedObject(controller.getCurrentTextStyle());
			foregroundSelector.setEditedObject(controller.getCurrentForegroundStyle());
		}
		if (selectedShapes.size() > 0) {
			shapeSelector.setEditedObject(selectedShapes.get(0).getGraphicalRepresentation().getShapeSpecification());
			backgroundSelector.setEditedObject(selectedShapes.get(0).getGraphicalRepresentation().getBackground());
			shadowStyleSelector.setEditedObject(selectedShapes.get(0).getGraphicalRepresentation().getShadowStyle());
		} else {
			shapeSelector.setEditedObject(controller.getCurrentShape());
			backgroundSelector.setEditedObject(controller.getCurrentBackgroundStyle());
			shadowStyleSelector.setEditedObject(controller.getCurrentShadowStyle());
		}
	}

	public List<ShapeNode<?>> getSelectedShapes() {
		return selectedShapes;
	}

	public List<DrawingTreeNode<?, ?>> getSelection() {
		return selection;
	}
}
