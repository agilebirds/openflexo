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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.view.widget.FIBBackgroundStyleSelector;
import org.openflexo.fge.view.widget.FIBForegroundStyleSelector;
import org.openflexo.fge.view.widget.FIBShadowStyleSelector;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.FIBTextStyleSelector;

public class EditorToolbox {

	private static final Logger logger = Logger.getLogger(EditorToolbox.class.getPackage().getName());

	private ToolPanel toolPanel;

	private JToolBar stylesToolBar;
	private LayoutToolBar layoutToolBar;
	private FIBForegroundStyleSelector foregroundSelector;
	private FIBBackgroundStyleSelector backgroundSelector;
	private FIBTextStyleSelector textStyleSelector;
	private FIBShadowStyleSelector shadowStyleSelector;
	private FIBShapeSelector shapeSelector;

	DrawingController controller;

	public EditorToolbox(DrawingController controller) {
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
		selectedGR.clear();
		controller = null;// Don't delete, we did not create it
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
					if (selectedGR.size() > 0) {
						for (ShapeGraphicalRepresentation shape : selectedShapes) {
							shape.setForeground(getEditedObject().clone());
						}
						for (ConnectorGraphicalRepresentation connector : selectedConnectors) {
							connector.setForeground(getEditedObject().clone());
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
						for (ShapeGraphicalRepresentation shape : selectedShapes) {
							shape.setBackground(getEditedObject().clone());
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
					if (selectedGR.size() > 0) {
						for (GraphicalRepresentation gr : selectedGR) {
							gr.setTextStyle(getEditedObject().clone());
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
						for (ShapeGraphicalRepresentation shape : selectedShapes) {
							shape.setShadowStyle(getEditedObject().clone());
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
						for (ShapeGraphicalRepresentation shape : selectedShapes) {
							shape.setShape(getEditedObject().clone());
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

	private List<ShapeGraphicalRepresentation> selectedShapes = new ArrayList<ShapeGraphicalRepresentation>();
	private List<ConnectorGraphicalRepresentation> selectedConnectors = new ArrayList<ConnectorGraphicalRepresentation>();
	private List<GraphicalRepresentation> selectedGR = new ArrayList<GraphicalRepresentation>();

	public void update() {
		selectedShapes.clear();
		selectedConnectors.clear();
		selectedGR.clear();
		for (GraphicalRepresentation gr : controller.getSelectedObjects()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				selectedGR.add(gr);
				selectedShapes.add((ShapeGraphicalRepresentation) gr);
			}
			if (gr instanceof ConnectorGraphicalRepresentation) {
				selectedGR.add(gr);
				selectedConnectors.add((ConnectorGraphicalRepresentation) gr);
			}
		}
		if (stylesToolBar == null) {
			return;
		}
		if (selectedGR.size() > 0) {
			textStyleSelector.setEditedObject(selectedGR.get(0).getTextStyle());
			if (selectedShapes.size() > 0) {
				foregroundSelector.setEditedObject(selectedShapes.get(0).getForeground());
			} else if (selectedConnectors.size() > 0) {
				foregroundSelector.setEditedObject(selectedConnectors.get(0).getForeground());
			}
		} else {
			textStyleSelector.setEditedObject(controller.getCurrentTextStyle());
			foregroundSelector.setEditedObject(controller.getCurrentForegroundStyle());
		}
		if (selectedShapes.size() > 0) {
			shapeSelector.setEditedObject(selectedShapes.get(0).getShape());
			backgroundSelector.setEditedObject(selectedShapes.get(0).getBackground());
			shadowStyleSelector.setEditedObject(selectedShapes.get(0).getShadowStyle());
		} else {
			shapeSelector.setEditedObject(controller.getCurrentShape());
			backgroundSelector.setEditedObject(controller.getCurrentBackgroundStyle());
			shadowStyleSelector.setEditedObject(controller.getCurrentShadowStyle());
		}
	}

	public List<ShapeGraphicalRepresentation> getSelectedShapes() {
		return selectedShapes;
	}

	public List<GraphicalRepresentation> getSelectedGR() {
		return selectedGR;
	}
}
