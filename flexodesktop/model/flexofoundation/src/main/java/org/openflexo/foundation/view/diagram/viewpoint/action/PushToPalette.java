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
package org.openflexo.foundation.view.diagram.viewpoint.action;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.gen.ScreenshotGenerator.ScreenshotImage;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement.ConnectorOverridingGraphicalRepresentation;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement.ShapeOverridingGraphicalRepresentation;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class PushToPalette extends FlexoAction<PushToPalette, ExampleDiagramShape, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(PushToPalette.class.getPackage().getName());

	public static FlexoActionType<PushToPalette, ExampleDiagramShape, ExampleDiagramObject> actionType = new FlexoActionType<PushToPalette, ExampleDiagramShape, ExampleDiagramObject>(
			"push_to_palette", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public PushToPalette makeNewAction(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection,
				FlexoEditor editor) {
			return new PushToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return shape != null && shape.getVirtualModel().getPalettes().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(PushToPalette.actionType, ExampleDiagramShape.class);
	}

	public Object graphicalRepresentation;
	public DiagramPalette palette;
	private EditionPattern editionPattern;
	public DropScheme dropScheme;
	public String newElementName;
	public boolean takeScreenshotForTopLevelElement = false;
	public boolean overrideDefaultGraphicalRepresentations = false;

	private ScreenshotImage screenshot;
	public int imageWidth;
	public int imageHeight;

	private DiagramPaletteElement _newPaletteElement;

	PushToPalette(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		drawingObjectEntries = new Vector<ExampleDrawingObjectEntry>();
		updateDrawingObjectEntries();
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Push to palette");

		if (getFocusedObject() != null && palette != null) {

			if (takeScreenshotForTopLevelElement) {
				File screenshotFile = saveScreenshot();
				ShapeGraphicalRepresentation gr = new ShapeGraphicalRepresentation();
				gr.setShapeType(ShapeType.RECTANGLE);
				gr.setForeground(ForegroundStyle.makeNone());
				gr.setBackground(new BackgroundImageBackgroundStyle(screenshotFile));
				gr.setShadowStyle(ShadowStyle.makeNone());
				gr.setTextStyle(TextStyle.makeDefault());
				gr.setText("");
				gr.setWidth(imageWidth);
				gr.setHeight(imageHeight);
				gr.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
				gr.setIsFloatingLabel(false);
				graphicalRepresentation = gr;
			} else {
				GraphicalRepresentation gr = getFocusedObject().getGraphicalRepresentation();
				if (gr instanceof ShapeGraphicalRepresentation) {
					graphicalRepresentation = new ShapeGraphicalRepresentation();
					((ShapeGraphicalRepresentation) graphicalRepresentation).setsWith(gr);
				} else if (gr instanceof ConnectorGraphicalRepresentation) {
					graphicalRepresentation = new ConnectorGraphicalRepresentation();
					((ConnectorGraphicalRepresentation) graphicalRepresentation).setsWith(gr);
				}
			}
			_newPaletteElement = palette.addPaletteElement(newElementName, graphicalRepresentation);
			_newPaletteElement.setEditionPattern(editionPattern);
			_newPaletteElement.setDropScheme(dropScheme);
			_newPaletteElement.setBoundLabelToElementName(!takeScreenshotForTopLevelElement);

			if (overrideDefaultGraphicalRepresentations) {
				for (ExampleDrawingObjectEntry entry : drawingObjectEntries) {
					if (entry.getSelectThis()) {
						if (entry.graphicalObject instanceof ExampleDiagramShape) {
							_newPaletteElement.addToOverridingGraphicalRepresentations(new ShapeOverridingGraphicalRepresentation(
									entry.patternRole, (ShapeGraphicalRepresentation) entry.graphicalObject.getGraphicalRepresentation()));
						} else if (entry.graphicalObject instanceof ExampleDiagramConnector) {
							_newPaletteElement.addToOverridingGraphicalRepresentations(new ConnectorOverridingGraphicalRepresentation(
									entry.patternRole, (ConnectorGraphicalRepresentation) entry.graphicalObject
											.getGraphicalRepresentation()));
						}
					}
				}
			}

		} else {
			logger.warning("Focused role is null !");
		}
	}

	public DiagramPaletteElement getNewPaletteElement() {
		return _newPaletteElement;
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotEmpty(newElementName) && palette != null && editionPattern != null && dropScheme != null;
	}

	public Vector<ExampleDrawingObjectEntry> drawingObjectEntries;

	public class ExampleDrawingObjectEntry {
		private boolean selectThis;
		public ExampleDiagramObject graphicalObject;
		public String elementName;
		public GraphicalElementPatternRole patternRole;

		public ExampleDrawingObjectEntry(ExampleDiagramObject graphicalObject, String elementName) {
			super();
			this.graphicalObject = graphicalObject;
			this.elementName = elementName;
			this.selectThis = isMainEntry();
			if (isMainEntry() && editionPattern != null) {
				patternRole = editionPattern.getDefaultPrimaryRepresentationRole();
			}
		}

		public boolean isMainEntry() {
			return graphicalObject == getFocusedObject();
		}

		public boolean getSelectThis() {
			if (isMainEntry()) {
				return true;
			}
			return selectThis;
		}

		public void setSelectThis(boolean aFlag) {
			selectThis = aFlag;
			if (patternRole == null && graphicalObject instanceof ExampleDiagramShape) {
				GraphicalElementPatternRole parentEntryPatternRole = getParentEntry().patternRole;
				for (ShapePatternRole r : editionPattern.getShapePatternRoles()) {
					if (r.getParentShapePatternRole() == parentEntryPatternRole && patternRole == null) {
						patternRole = r;
					}
				}
			}
		}

		public ExampleDrawingObjectEntry getParentEntry() {
			return getEntry(graphicalObject.getParent());
		}

		public List<? extends GraphicalElementPatternRole> getAvailablePatternRoles() {
			if (graphicalObject instanceof ExampleDiagramShape) {
				return editionPattern.getPatternRoles(ShapePatternRole.class);
			} else if (graphicalObject instanceof ExampleDiagramConnector) {
				return editionPattern.getPatternRoles(ConnectorPatternRole.class);
			}
			return null;
		}
	}

	public int getSelectedEntriesCount() {
		int returned = 0;
		for (ExampleDrawingObjectEntry e : drawingObjectEntries) {
			if (e.selectThis) {
				returned++;
			}
		}
		return returned;
	}

	public ExampleDrawingObjectEntry getEntry(ExampleDiagramObject o) {
		for (ExampleDrawingObjectEntry e : drawingObjectEntries) {
			if (e.graphicalObject == o) {
				return e;
			}
		}
		return null;
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern) {
		this.editionPattern = editionPattern;
		updateDrawingObjectEntries();
	}

	private void updateDrawingObjectEntries() {
		drawingObjectEntries.clear();
		int shapeIndex = 1;
		int connectorIndex = 1;
		for (ExampleDiagramObject o : getFocusedObject().getDescendants()) {
			if (o instanceof ExampleDiagramShape) {
				ExampleDiagramShape shape = (ExampleDiagramShape) o;
				String shapeRoleName = StringUtils.isEmpty(shape.getName()) ? "shape" + (shapeIndex > 1 ? shapeIndex : "") : shape
						.getName();
				drawingObjectEntries.add(new ExampleDrawingObjectEntry(shape, shapeRoleName));
				shapeIndex++;
			}
			if (o instanceof ExampleDiagramConnector) {
				ExampleDiagramConnector connector = (ExampleDiagramConnector) o;
				String connectorRoleName = "connector" + (connectorIndex > 1 ? connectorIndex : "");
				drawingObjectEntries.add(new ExampleDrawingObjectEntry(connector, connectorRoleName));
				connectorIndex++;
			}
		}

	}

	public ScreenshotImage getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(ScreenshotImage screenshot) {
		this.screenshot = screenshot;
		imageWidth = screenshot.image.getWidth(null);
		imageHeight = screenshot.image.getHeight(null);
	}

	public File saveScreenshot() {
		File imageFile = new File(getFocusedObject().getViewPoint().getResource().getDirectory(), JavaUtils.getClassName(newElementName)
				+ ".palette-element" + ".png");
		logger.info("Saving " + imageFile);
		try {
			ImageUtils.saveImageToFile(getScreenshot().image, imageFile, ImageType.PNG);
			return imageFile;
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Could not save " + imageFile.getAbsolutePath());
			return null;
		}

	}
}
