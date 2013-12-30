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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.ScreenshotBuilder.ScreenshotImage;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPaletteFactory;
import org.openflexo.technologyadapter.diagram.fml.DropScheme;
import org.openflexo.technologyadapter.diagram.fml.FMLDiagramPaletteElementBinding;
import org.openflexo.technologyadapter.diagram.fml.GraphicalElementPatternRole;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;
import org.openflexo.technologyadapter.diagram.model.DiagramShape;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class PushToPalette extends FlexoAction<PushToPalette, DiagramShape, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(PushToPalette.class.getPackage().getName());

	public static FlexoActionType<PushToPalette, DiagramShape, DiagramElement<?>> actionType = new FlexoActionType<PushToPalette, DiagramShape, DiagramElement<?>>(
			"push_to_palette", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public PushToPalette makeNewAction(DiagramShape focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
			return new PushToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramShape shape, Vector<DiagramElement<?>> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramShape shape, Vector<DiagramElement<?>> globalSelection) {
			return shape != null /* && shape.getVirtualModel().getPalettes().size() > 0*/;
		}

	};

	private TypedDiagramModelSlot diagramModelSlot;

	public GraphicalRepresentation graphicalRepresentation;
	public DiagramPalette palette;
	public int xLocation;
	public int yLocation;;
	private EditionPattern editionPattern;
	public DropScheme dropScheme;
	private String newElementName;
	public boolean takeScreenshotForTopLevelElement = true;
	public boolean overrideDefaultGraphicalRepresentations = false;

	private ScreenshotImage<DiagramShape> screenshot;
	public int imageWidth;
	public int imageHeight;

	private DiagramPaletteElement _newPaletteElement;

	static {
		FlexoObjectImpl.addActionForClass(PushToPalette.actionType, DiagramShape.class);
	}

	protected PushToPalette(DiagramShape focusedObject, Vector<DiagramElement<?>> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		drawingObjectEntries = new Vector<DrawingObjectEntry>();
		updateDrawingObjectEntries();
	}

	/**
	 * Return the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 * 
	 * @return
	 */
	public TypedDiagramModelSlot getDiagramModelSlot() {
		return diagramModelSlot;
	}

	/**
	 * Sets the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 * 
	 * @return
	 */
	public void setDiagramModelSlot(TypedDiagramModelSlot diagramModelSlot) {
		this.diagramModelSlot = diagramModelSlot;
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Push to palette");

		if (getFocusedObject() != null && palette != null) {

			DiagramPaletteFactory factory = palette.getFactory();

			if (takeScreenshotForTopLevelElement) {
				File screenshotFile = saveScreenshot();
				ShapeGraphicalRepresentation gr = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
				gr.setForeground(factory.makeNoneForegroundStyle());
				gr.setBackground(factory.makeImageBackground(screenshotFile));
				gr.setShadowStyle(factory.makeNoneShadowStyle());
				gr.setTextStyle(factory.makeDefaultTextStyle());
				gr.setText("");
				gr.setWidth(imageWidth);
				gr.setHeight(imageHeight);
				gr.setDimensionConstraints(DimensionConstraints.UNRESIZABLE);
				gr.setIsFloatingLabel(false);
				gr.setX(xLocation);
				gr.setY(yLocation);
				graphicalRepresentation = gr;
			} else {
				GraphicalRepresentation gr = getFocusedObject().getGraphicalRepresentation();
				if (gr instanceof ShapeGraphicalRepresentation) {
					graphicalRepresentation = factory.makeShapeGraphicalRepresentation();
					graphicalRepresentation.setsWith(gr);
					((ShapeGraphicalRepresentation) graphicalRepresentation).setX(xLocation);
					((ShapeGraphicalRepresentation) graphicalRepresentation).setY(yLocation);
				} else if (gr instanceof ConnectorGraphicalRepresentation) {
					graphicalRepresentation = factory.makeConnectorGraphicalRepresentation();
					((ConnectorGraphicalRepresentation) graphicalRepresentation).setsWith(gr);
				}
			}

			_newPaletteElement = palette.addPaletteElement(newElementName, graphicalRepresentation);

			FMLDiagramPaletteElementBinding newBinding = new FMLDiagramPaletteElementBinding();
			newBinding.setPaletteElement(_newPaletteElement);
			newBinding.setEditionPattern(editionPattern);
			newBinding.setDropScheme(dropScheme);
			newBinding.setBoundLabelToElementName(!takeScreenshotForTopLevelElement);

			diagramModelSlot.addToPaletteElementBindings(newBinding);

			/*for (DrawingObjectEntry entry : drawingObjectEntries) {
				if(!entry.isMainEntry()){
					if (entry.graphicalObject.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
						ShapeGraphicalRepresentation subGr = new ShapeGraphicalRepresentation();
						ShapeGraphicalRepresentation test = ((ShapeGraphicalRepresentation)graphicalRepresentation);
						((ShapeGraphicalRepresentation) subGr).setsWith((ShapeGraphicalRepresentation)entry.graphicalObject.getGraphicalRepresentation());
						List grs = test.getOrderedContainedGraphicalRepresentations();
						if(grs!=null){
							grs.add(subGr);
						}
						else{
							grs=new ArrayList();
						}
						
					}
				}
			}*/

			if (overrideDefaultGraphicalRepresentations) {
				for (DrawingObjectEntry entry : drawingObjectEntries) {
					if (entry.getSelectThis()) {
						// TODO
						/*if (entry.graphicalObject instanceof GRShapeTemplate) {
							_newPaletteElement.addToOverridingGraphicalRepresentations(new ShapeOverridingGraphicalRepresentation(
									entry.patternRole, (ShapeGraphicalRepresentation) entry.graphicalObject.getGraphicalRepresentation()));
						} else if (entry.graphicalObject instanceof GRConnectorTemplate) {
							_newPaletteElement.addToOverridingGraphicalRepresentations(new ConnectorOverridingGraphicalRepresentation(
									entry.patternRole, (ConnectorGraphicalRepresentation) entry.graphicalObject
											.getGraphicalRepresentation()));
						}*/
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

	public String getNewElementName() {
		return newElementName;
	}

	public void setNewElementName(String newElementName) {
		this.newElementName = newElementName;
	}

	@Override
	public boolean isValid() {
		return StringUtils.isNotEmpty(newElementName) && palette != null && editionPattern != null && dropScheme != null;
	}

	private Vector<DrawingObjectEntry> drawingObjectEntries;

	public Vector<DrawingObjectEntry> getDrawingObjectEntries() {
		return drawingObjectEntries;
	}

	public void setDrawingObjectEntries(Vector<DrawingObjectEntry> drawingObjectEntries) {
		this.drawingObjectEntries = drawingObjectEntries;
	}

	public class DrawingObjectEntry {
		private boolean selectThis;
		public GRTemplate graphicalObject;
		public String elementName;
		public GraphicalElementPatternRole<?, ?> patternRole;

		public DrawingObjectEntry(GRTemplate graphicalObject, String elementName) {
			super();
			this.graphicalObject = graphicalObject;
			this.elementName = elementName;
			this.selectThis = isMainEntry();
			/*if (isMainEntry() && editionPattern != null) {
				patternRole = editionPattern.getDefaultPrimaryRepresentationRole();
			}*/
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
			if (patternRole == null && graphicalObject instanceof GRShapeTemplate) {
				GraphicalElementPatternRole<?, ?> parentEntryPatternRole = getParentEntry().patternRole;
				for (ShapePatternRole r : editionPattern.getPatternRoles(ShapePatternRole.class)) {
					if (r.getParentShapePatternRole() == parentEntryPatternRole && patternRole == null) {
						patternRole = r;
					}
				}
			}
		}

		public DrawingObjectEntry getParentEntry() {
			return getEntry(graphicalObject.getParent());
		}

		public List<? extends GraphicalElementPatternRole<?, ?>> getAvailablePatternRoles() {
			if (graphicalObject instanceof GRShapeTemplate) {
				return editionPattern.getPatternRoles(ShapePatternRole.class);
			} else if (graphicalObject instanceof GRConnectorTemplate) {
				return editionPattern.getPatternRoles(ConnectorPatternRole.class);
			}
			return null;
		}
	}

	public int getSelectedEntriesCount() {
		int returned = 0;
		for (DrawingObjectEntry e : drawingObjectEntries) {
			if (e.selectThis) {
				returned++;
			}
		}
		return returned;
	}

	public DrawingObjectEntry getEntry(GRTemplate o) {
		for (DrawingObjectEntry e : drawingObjectEntries) {
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
		for (GRTemplate o : getFocusedObject().getDescendants()) {
			if (o instanceof GRShapeTemplate) {
				GRShapeTemplate shape = (GRShapeTemplate) o;
				String shapeRoleName = StringUtils.isEmpty(shape.getName()) ? "shape" + (shapeIndex > 1 ? shapeIndex : "") : shape
						.getName();
				drawingObjectEntries.add(new DrawingObjectEntry(shape, shapeRoleName));
				shapeIndex++;
			}
			if (o instanceof GRConnectorTemplate) {
				GRConnectorTemplate connector = (GRConnectorTemplate) o;
				String connectorRoleName = "connector" + (connectorIndex > 1 ? connectorIndex : "");
				drawingObjectEntries.add(new DrawingObjectEntry(connector, connectorRoleName));
				connectorIndex++;
			}
		}

	}

	public ScreenshotImage<DiagramShape> getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(ScreenshotImage<DiagramShape> screenshot) {
		this.screenshot = screenshot;
		imageWidth = screenshot.image.getWidth(null);
		imageHeight = screenshot.image.getHeight(null);
	}

	public File saveScreenshot() {
		File imageFile = new File(getDiagramSpecification().getResource().getDirectory(), JavaUtils.getClassName(newElementName)
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

	public DiagramSpecification getDiagramSpecification() {
		return diagramModelSlot.getMetaModelResource().getLoadedResourceData();
	}
}
