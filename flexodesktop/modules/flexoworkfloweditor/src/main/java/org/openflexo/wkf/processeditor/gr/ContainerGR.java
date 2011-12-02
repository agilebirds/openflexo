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
package org.openflexo.wkf.processeditor.gr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.CapStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.fge.graphics.ForegroundStyle.JoinStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ArtefactInserted;
import org.openflexo.foundation.wkf.dm.ArtefactRemoved;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.PetriGraphHasBeenOpened;
import org.openflexo.foundation.wkf.dm.PostRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.wkf.WKFPreferences;
import org.openflexo.wkf.processeditor.AnnotationMouseClickControl;
import org.openflexo.wkf.processeditor.ProcessGraphicalRepresentation;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public abstract class ContainerGR<O extends WKFObject> extends WKFObjectGR<O> implements GraphicalFlexoObserver {

	static final Logger logger = Logger.getLogger(OperationNodeGR.class.getPackage().getName());

	protected ForegroundStyle decorationForeground;
	protected ForegroundStyle closingBoxForeground;
	protected BackgroundStyle decorationBackground;

	protected Color mainColor, backColor;

	public ContainerGR(O object, ProcessRepresentation aDrawing, Color aMainColor, Color aBackColor) {
		super(object, ShapeType.RECTANGLE, aDrawing);
		/*setX(object.getPosX());
		setY(object.getPosY());
		setWidth(object.getWidth());
		setHeight(object.getHeight());*/

		setMinimalWidth(180);
		setMinimalHeight(80);

		setDimensionConstraints(DimensionConstraints.CONTAINER);

		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(0, CONTAINER_LABEL_HEIGHT, 0, CONTAINER_LABEL_HEIGHT));

		updateBackground(aMainColor, aBackColor);

		updatePropertiesFromWKFPreferences();

		setDecorationPainter(new DecorationPainter() {
			@Override
			public void paintDecoration(org.openflexo.fge.graphics.FGEShapeDecorationGraphics g) {
				double arcSize = 25;
				g.useBackgroundStyle(decorationBackground);
				g.fillRoundRect(0, 0, g.getWidth() - 1, g.getHeight() - 1 + CONTAINER_LABEL_HEIGHT, arcSize, arcSize);
				g.useForegroundStyle(decorationForeground);
				g.drawRoundRect(0, 0, g.getWidth() - 1, g.getHeight() - 1 + CONTAINER_LABEL_HEIGHT, arcSize, arcSize);
				g.fillArc(0, g.getHeight() + CONTAINER_LABEL_HEIGHT - arcSize, arcSize, arcSize, 180, 90);
				g.fillArc(g.getWidth() - arcSize, g.getHeight() + CONTAINER_LABEL_HEIGHT - arcSize, arcSize, arcSize, 270, 90);
				g.fillRect(arcSize / 2, g.getHeight() - arcSize / 2 + CONTAINER_LABEL_HEIGHT, g.getWidth() - arcSize + 1, arcSize / 2);
				g.fillRect(0, g.getHeight() - arcSize / 2 - 2 + CONTAINER_LABEL_HEIGHT, g.getWidth(), 3);

				Rectangle closingBoxRect = new Rectangle((int) g.getWidth() - 20, 5, 15, 15);
				int crossBorder = 4;

				g.useBackgroundStyle(BackgroundStyle.makeColoredBackground(Color.WHITE));
				g.fillRoundRect(closingBoxRect.x, closingBoxRect.y, closingBoxRect.width, closingBoxRect.height, 10, 10);
				g.useForegroundStyle(closingBoxForeground);
				g.drawRoundRect(closingBoxRect.x, closingBoxRect.y, closingBoxRect.width, closingBoxRect.height, 10, 10);
				g.useForegroundStyle(ForegroundStyle.makeStyle(mainColor, 2.0f, JoinStyle.JOIN_MITER, CapStyle.CAP_ROUND,
						DashStyle.PLAIN_STROKE));
				g.drawLine(closingBoxRect.x + crossBorder, closingBoxRect.y + crossBorder, closingBoxRect.x + closingBoxRect.width
						- crossBorder, closingBoxRect.y + closingBoxRect.height - crossBorder);
				g.drawLine(closingBoxRect.x + closingBoxRect.width - crossBorder, closingBoxRect.y + crossBorder, closingBoxRect.x
						+ crossBorder, closingBoxRect.y + closingBoxRect.height - crossBorder);

				g.useTextStyle(TextStyle.makeTextStyle(Color.WHITE, FGEConstants.DEFAULT_TEXT_FONT));
				g.drawString(getLabel(), g.getWidth() / 2, g.getHeight() - 9 + CONTAINER_LABEL_HEIGHT, TextAlignment.CENTER);
			};

			@Override
			public boolean paintBeforeShape() {
				return true;
			}
		});

		setForeground(ForegroundStyle.makeNone());
		setBackground(BackgroundStyle.makeEmptyBackground());

		addToMouseDragControls(new ContainerCloser(), true);
		if (object instanceof FlexoPetriGraph && ((FlexoPetriGraph) object).getContainer() != null) {
			addToMouseClickControls(new AnnotationMouseClickControl());
			((FlexoPetriGraph) object).getContainer().addObserver(this);
		}
	}

	@Override
	public boolean supportAlignOnGrid() {
		return false;
	}

	@Override
	public void delete() {
		WKFObject o = null;
		if (getModel() instanceof FlexoPetriGraph && ((FlexoPetriGraph) getModel()).getContainer() != null) {
			o = (((FlexoPetriGraph) getModel()).getContainer());
		}
		super.delete();
		if (o != null) {
			o.deleteObserver(this);
		}
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();

		updateDecorationBackground();
		updateDecorationForeground();
	}

	protected void updateDecorationBackground() {
		if ((getWorkflow() != null && getWorkflow().getUseTransparency(WKFPreferences.getUseTransparency()))
				|| (getWorkflow() == null && WKFPreferences.getUseTransparency())) {
			decorationBackground = BackgroundStyle.makeColorGradientBackground(backColor, Color.WHITE,
					ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
			decorationBackground.setUseTransparency(true);
			decorationBackground.setTransparencyLevel(0.9f);
		} else {
			decorationBackground = BackgroundStyle.makeColoredBackground(backColor);
		}
	}

	protected void updateDecorationForeground() {
		decorationForeground = ForegroundStyle.makeStyle(mainColor);
		decorationForeground.setLineWidth(0.2);

		closingBoxForeground = ForegroundStyle.makeStyle(mainColor);
		closingBoxForeground.setLineWidth(0.2);
	}

	public abstract String getLabel();

	@Override
	public double getX() {
		if (!getDrawable().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			getDrawable().getX(BASIC_PROCESS_EDITOR, getDefaultX());
		}
		return getDrawable().getX(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setXNoNotification(double posX) {
		getDrawable().setX(posX, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getY() {
		if (!getDrawable().hasLocationForContext(BASIC_PROCESS_EDITOR)) {
			getDrawable().getY(BASIC_PROCESS_EDITOR, getDefaultY());
		}
		return getDrawable().getY(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setYNoNotification(double posY) {
		getDrawable().setY(posY, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getWidth() {
		if (!getDrawable().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
			getDrawable().getWidth(BASIC_PROCESS_EDITOR, getDefaultWidth());
		}
		return getDrawable().getWidth(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getDrawable().setWidth(width, BASIC_PROCESS_EDITOR);
	}

	@Override
	public double getHeight() {
		if (!getDrawable().hasDimensionForContext(BASIC_PROCESS_EDITOR)) {
			getDrawable().getHeight(BASIC_PROCESS_EDITOR, getDefaultHeight());
		}
		return getDrawable().getHeight(BASIC_PROCESS_EDITOR);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getDrawable().setHeight(height, BASIC_PROCESS_EDITOR);
	}

	public class ContainerCloser extends MouseDragControl {

		public ContainerCloser() {
			super("Closer", MouseButton.LEFT, new CustomDragControlAction() {
				@Override
				public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					logger.info("handleMousePressed");
					if (isInsideClosingBox(graphicalRepresentation, controller, event)) {
						logger.info("Closing container");
						closingRequested();
						return true;
					}
					return false;
				}

				@Override
				public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event, boolean isSignificativeDrag) {
					return false;
				}

				@Override
				public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					return false;
				}
			}, false, false, false, false);
		}

		@Override
		public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e) {
			return super.isApplicable(graphicalRepresentation, controller, e) && isInsideClosingBox(graphicalRepresentation, controller, e);
		}

	}

	protected static boolean isInsideClosingBox(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
			MouseEvent event) {
		if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
			ShapeView view = (ShapeView) controller.getDrawingView().viewForObject(graphicalRepresentation);
			Rectangle closingBoxRect = new Rectangle(
					(int) ((((ShapeGraphicalRepresentation) graphicalRepresentation).getWidth() - 20) * controller.getScale()),
					(int) (5 * controller.getScale()), (int) (15 * controller.getScale()), (int) (15 * controller.getScale()));
			Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
			return closingBoxRect.contains(clickLocation);
		}
		return false;
	}

	public abstract void closingRequested();

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getModel()) {
			if ((dataModification instanceof NodeInserted) || (dataModification instanceof NodeRemoved)
					|| (dataModification instanceof ArtefactInserted) || (dataModification instanceof ArtefactRemoved)
					|| (dataModification instanceof PostRemoved)) {
				getDrawing().updateGraphicalObjectsHierarchy();
				notifyShapeNeedsToBeRedrawn();
				notifyObjectMoved();
				notifyObjectResized();
			} else if (dataModification instanceof WKFAttributeDataModification) {
				if ((((WKFAttributeDataModification) dataModification).getAttributeName().equals("posX"))
						|| (((WKFAttributeDataModification) dataModification).getAttributeName().equals("posY"))) {
					notifyObjectMoved();
				} else if ((((WKFAttributeDataModification) dataModification).getAttributeName().equals("width"))
						|| (((WKFAttributeDataModification) dataModification).getAttributeName().equals("height"))) {
					notifyObjectResized();
				} else {
					notifyShapeNeedsToBeRedrawn();
				}
			} else if (dataModification instanceof ObjectLocationChanged) {
				notifyObjectMoved();
			} else if (dataModification instanceof ObjectSizeChanged) {
				notifyObjectResized();
			} else if (dataModification instanceof PetriGraphHasBeenOpened) {
				if (getParentGraphicalRepresentation() != null) {
					getParentGraphicalRepresentation().moveToTop(this);
				}
				switchToSelectionLayer();
				restoreNormalLayer();

			}
		}
	}

	private int regularLayer;
	private boolean switchedToSelectionLayer = false;

	protected void switchToSelectionLayer() {
		// logger.info("switchToSelectionLayer()");

		if (switchedToSelectionLayer) {
			return;
		}

		for (WKFConnectorGR containedGR : getAllContainedConnectors()) {
			if (containedGR.isConnectorFullyVisible()) {
				containedGR.switchToSelectionLayer();
			}
		}
		// Set layer AFTER in order not to perturbate edge layer computing
		regularLayer = getLayer();
		setLayer(SELECTION_LAYER);

		switchedToSelectionLayer = true;
	}

	protected void restoreNormalLayer() {
		setLayer(regularLayer);
		for (WKFConnectorGR<?> containedGR : getAllContainedConnectors()) {
			containedGR.restoreNormalLayer();
		}

		ProcessGraphicalRepresentation processGR = (ProcessGraphicalRepresentation) getDrawingGraphicalRepresentation();
		processGR.updateAllEdgeLayers();

		switchedToSelectionLayer = false;

	}

	@Override
	public void notifyObjectHasMoved() {
		super.notifyObjectHasMoved();
		if (getIsSelected()) {
			for (WKFConnectorGR containedGR : getAllContainedConnectors()) {
				if (containedGR.isConnectorFullyVisible()) {
					containedGR.switchToSelectionLayer();
				} else {
					containedGR.restoreNormalLayer();
				}
			}
		} else {
			for (WKFConnectorGR containedGR : getAllContainedConnectors()) {
				containedGR.restoreNormalLayer();
			}
		}
		// for (WKFConnectorGR containedGR : getAllContainedConnectors()) containedGR.restoreNormalLayer();
	}

	@Override
	public void setIsSelected(boolean aFlag) {
		// logger.info("setIsSelected() with "+aFlag);
		if (aFlag != getIsSelected()) {
			super.setIsSelected(aFlag);
			if (aFlag) {
				switchToSelectionLayer();
			} else {
				restoreNormalLayer();
			}
		}
	}

	private Vector<WKFConnectorGR> getAllContainedConnectors() {
		Vector<WKFConnectorGR> returned = new Vector<WKFConnectorGR>();

		Enumeration<GraphicalRepresentation<?>> en = getDrawing().getAllGraphicalRepresentations();

		while (en.hasMoreElements()) {
			GraphicalRepresentation<?> next = en.nextElement();
			if (next instanceof WKFConnectorGR
					&& ((WKFConnectorGR<?>) next).getStartObject() != null
					&& ((WKFConnectorGR<?>) next).getEndObject() != null
					/*&& (!(((WKFConnectorGR<?>)next).getStartObject().getDrawable() instanceof FlexoNode)
							|| !(((FlexoNode)(((WKFConnectorGR<?>)next).getStartObject().getDrawable())).isEndNode()))*/
					&& (isAncestorOf(((WKFConnectorGR<?>) next).getStartObject()) || isAncestorOf(((WKFConnectorGR<?>) next).getEndObject()))) {
				returned.add((WKFConnectorGR<?>) next);
			}
		}

		return returned;
	}

	// Override to implement defaut automatic layout
	public double getDefaultX() {
		return 0;
	}

	// Override to implement defaut automatic layout
	public double getDefaultY() {
		return 0;
	}

	// Override to implement defaut automatic layout
	public double getDefaultWidth() {
		return 100;
	}

	// Override to implement defaut automatic layout
	public double getDefaultHeight() {
		return 50;
	}

	protected void updateBackground(Color aMainColor, Color aBackColor) {
		backColor = aBackColor;
		mainColor = aMainColor;
		updateDecorationBackground();
		updateDecorationForeground();
		notifyShapeNeedsToBeRedrawn();
	}

}
