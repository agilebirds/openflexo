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
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.gen.FlexoProcessImageBuilder;
import org.openflexo.foundation.gen.FlexoProcessImageNotificationCenter;
import org.openflexo.foundation.wkf.action.OpenEmbeddedProcess;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.swing.ImageUtils;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class SubProcessNodeGR extends NormalAbstractActivityNodeGR<SubProcessNode> {

	private static final Logger logger = Logger.getLogger(SubProcessNodeGR.class.getPackage().getName());

	private static final int MIN_SPACE = 4;
	private static final int ICONS_HEIGHT = 20;

	public SubProcessNodeGR(SubProcessNode subProcessNode, ProcessRepresentation aDrawing, boolean isInPalet) {
		super(subProcessNode, ShapeType.RECTANGLE, aDrawing, isInPalet);
		setVerticalTextAlignment(VerticalTextAlignment.TOP);
		addToMouseClickControls(new ProcessOpener(), true);
		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {

				if (!getSubProcessNode().getDisplaySubProcessImage()) {
					paintDefaultDecoration(g);
				} else {
					ImageIcon snapshot = null;
					if (getSubProcessNode().getSubProcess() != null) {
						try {
							snapshot = FlexoProcessImageBuilder.getSnapshot(getSubProcessNode().getSubProcess());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (snapshot != null) {
						Image image = ImageUtils.getThumbnail(snapshot, (int) getWidth() - 13, (int) getHeight() - 13).getImage();
						double x = Math.max(10.0, (getWidth() - 10.0 - image.getWidth(null)) / 2);
						double y = Math.max(10.0, (getHeight() - 10.0 - image.getHeight(null)) / 2);
						FGEPoint point = new FGEPoint(x / getWidth(), y / getHeight());
						g.drawImage(image, point);
						setBackground(BackgroundStyle.makeColoredBackground(Color.WHITE));
						setHasText(false);
						ImageIcon typeIcon = getImageIcon(getSubProcessNode());
						if (typeIcon != null) {
							FGERectangle additionalSymbolRect = getAdditionalSymbolRectWithEmbedded();
							g.drawImage(typeIcon.getImage(), new FGEPoint(additionalSymbolRect.x, additionalSymbolRect.y));
						}
					} else {
						paintDefaultDecoration(g);
					}
				}
			}

			public void paintDefaultDecoration(FGEShapeGraphics g) {
				g.useTextStyle(roleLabelTextStyle);
				g.drawString(getSubLabel(), new FGEPoint(0.5,
						(roleLabelTextStyle.getFont().getSize() / 2 + MIN_SPACE + getExtraSpaceAbove()) / getHeight()),
						HorizontalTextAlignment.CENTER);

				FGERectangle expandingRect = getExpandingRect();
				g.drawImage(WKFIconLibrary.EXPANDABLE_ICON.getImage(), new FGEPoint(expandingRect.x, expandingRect.y));

				ImageIcon typeIcon = getImageIcon(getSubProcessNode());
				if (typeIcon != null) {
					FGERectangle additionalSymbolRect = getAdditionalSymbolRect();
					g.drawImage(typeIcon.getImage(), new FGEPoint(additionalSymbolRect.x, additionalSymbolRect.y));
				}
			};
		});
		FlexoProcessImageNotificationCenter.getInstance().addObserver(this);
		updatePropertiesFromWKFPreferences();

	}

	@Override
	public void delete() {
		FlexoProcessImageNotificationCenter.getInstance().deleteObserver(this);
		super.delete();

	}

	@Override
	public int getTopBorder() {
		return isInPalette ? super.getTopBorder() : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	public int getBottomBorder() {
		return isInPalette ? super.getBottomBorder() : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	public int getLeftBorder() {
		return isInPalette ? super.getLeftBorder() : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	public int getRightBorder() {
		return isInPalette ? super.getRightBorder() : PORTMAP_REGISTERY_WIDTH;
	}

	@Override
	public double getRelativeTextY() {
		return (roleLabelTextStyle.getFont().getSize() + MIN_SPACE + getExtraSpaceAbove()) / getHeight();
	}

	@Override
	public double getRequiredHeight(double labelHeight) {
		return labelHeight + getRoleFont().getSize() + ICONS_HEIGHT + getExtraSpaceAbove() + getExtraSpaceBelow() + 4 * MIN_SPACE;
	}

	private double getExtraSpaceBelow() {
		return getPortmapRegisteryOrientation() == SimplifiedCardinalDirection.SOUTH ? heightForPortmapRegistery() : 0;
	}

	private double getExtraSpaceAbove() {
		return getPortmapRegisteryOrientation() == SimplifiedCardinalDirection.NORTH ? heightForPortmapRegistery() : 0;
	}

	public FGERectangle getExpandingRect() {
		double absoluteIconY = getHeight() - getExtraSpaceBelow() - MIN_SPACE - WKFIconLibrary.EXPANDABLE_ICON.getIconHeight();
		double absoluteIconX;
		ImageIcon typeIcon = getImageIcon(getSubProcessNode());
		if (typeIcon == null) {
			absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()) / 2;
		} else {
			absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() - typeIcon.getIconWidth() - MIN_SPACE) / 2;
		}
		return new FGERectangle(absoluteIconX / getWidth(), absoluteIconY / getHeight(), WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()
				/ getWidth(), WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight());

	}

	public FGERectangle getAdditionalSymbolRect() {
		ImageIcon typeIcon = getImageIcon(getSubProcessNode());
		if (typeIcon == null) {
			return null;
		}
		double absoluteIconY = getHeight() - getExtraSpaceBelow() - MIN_SPACE - WKFIconLibrary.EXPANDABLE_ICON.getIconHeight();
		double absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() - typeIcon.getIconWidth() - MIN_SPACE) / 2
				+ WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() + MIN_SPACE;
		return new FGERectangle(absoluteIconX / getWidth(), absoluteIconY / getHeight(), WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()
				/ getWidth(), WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight());

	}

	public FGERectangle getAdditionalSymbolRectWithEmbedded() {
		ImageIcon typeIcon = getImageIcon(getSubProcessNode());
		if (typeIcon == null) {
			return null;
		}
		double absoluteIconY = getHeight() - 10 - WKFIconLibrary.EXPANDABLE_ICON.getIconHeight();
		double absoluteIconX = (getWidth() - WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() - typeIcon.getIconWidth() - MIN_SPACE) / 2
				+ WKFIconLibrary.EXPANDABLE_ICON.getIconWidth() + MIN_SPACE;
		return new FGERectangle(absoluteIconX / getWidth(), absoluteIconY / getHeight(), WKFIconLibrary.EXPANDABLE_ICON.getIconWidth()
				/ getWidth(), WKFIconLibrary.EXPANDABLE_ICON.getIconHeight() / getHeight());

	}

	public class ProcessOpener extends MouseClickControl {

		public ProcessOpener() {
			super("BPE-Process opener", MouseButton.LEFT, 1, new MouseClickControlAction() {

				@Override
				public boolean handleClick(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
						MouseEvent event) {
					logger.info("Opening process");
					OpenEmbeddedProcess.actionType.makeNewAction(getSubProcessNode(), null, getDrawing().getEditor()).doAction();
					getDrawing().updateGraphicalObjectsHierarchy();
					return true;
				}

				@Override
				public MouseClickControlActionType getActionType() {
					return MouseClickControlActionType.CUSTOM;
				}
			}, false, false, false, false);
		}

		@Override
		public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e) {
			return super.isApplicable(graphicalRepresentation, controller, e) && isInsideClosingBox(graphicalRepresentation, controller, e);
		}

		@Override
		public boolean isModelEditionAction() {
			return false;
		}

	}

	protected boolean isInsideClosingBox(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
			MouseEvent event) {
		ShapeView view = (ShapeView) controller.getDrawingView().viewForObject(graphicalRepresentation);
		FGERectangle expandingRect = getExpandingRect();
		java.awt.Rectangle scaledExpandingRect = convertNormalizedRectangleToViewCoordinates(expandingRect, controller.getScale());
		Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
		return scaledExpandingRect.contains(clickLocation);
	}

	public static ImageIcon getImageIcon(SubProcessNode spNode) {
		if (spNode instanceof MultipleInstanceSubProcessNode) {
			if (((MultipleInstanceSubProcessNode) spNode).getIsSequential()) {
				return WKFIconLibrary.SEQUENTIAL_MULTIPLE_INSTANCE_SUBPROCESS_ICON;
			} else {
				return WKFIconLibrary.MULTIPLE_INSTANCE_SUBPROCESS_ICON;
			}
		} else if (spNode instanceof SingleInstanceSubProcessNode) {
			return null;
		} else if (spNode instanceof LoopSubProcessNode) {
			return WKFIconLibrary.LOOP_SUBPROCESS_ICON;
		} else if (spNode instanceof WSCallSubProcessNode) {
			return WKFIconLibrary.WS_CALL_SUBPROCESS_ICON;
		} else {
			return null;
		}
	}

	private double heightForPortmapRegistery() {
		return ProcessEditorConstants.PORTMAP_REGISTERY_WIDTH / 2;
	}

	protected SimplifiedCardinalDirection getPortmapRegisteryOrientation() {
		if (getSubProcessNode().getPortMapRegistery() != null) {
			PortmapRegisteryGR portmapRegisteryGR = (PortmapRegisteryGR) getGraphicalRepresentation(getSubProcessNode()
					.getPortMapRegistery());
			if (portmapRegisteryGR != null) {
				return portmapRegisteryGR.getOrientation();
			}
		}
		return null;
	}

	public SubProcessNode getSubProcessNode() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (MultipleInstanceSubProcessNode.IS_SEQUENTIAL.equals(dataModification.propertyName())) {
			notifyShapeNeedsToBeRedrawn();
		} else if ("subProcess".equals(dataModification.propertyName())) {
			getDrawing().updateGraphicalObjectsHierarchy();
		} else if ("displaySubProcessImage".equals(dataModification.propertyName())) {
			notifyShapeNeedsToBeRedrawn();
		}
		super.update(observable, dataModification);
	}
}
