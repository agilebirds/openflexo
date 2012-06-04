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
package org.openflexo.wkf.swleditor.gr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundImage;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundImage.ImageBackgroundType;
import org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.ConvertedIntoLocalObject;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.toolbox.FileResource;
import org.openflexo.wkf.swleditor.AnnotationMouseClickControl;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class RoleContainerGR extends SWLObjectGR<Role> implements SWLContainerGR {

	static final Logger logger = Logger.getLogger(OperationNodeGR.class.getPackage().getName());

	protected BackgroundStyle background;
	protected ForegroundStyle decorationForeground;
	protected BackgroundImage decorationBackground;

	private static final FileResource USER_ROLE_ICON = new FileResource("Resources/WKF/SmallRole.gif");
	private static final FileResource SYSTEM_ROLE_ICON = new FileResource("Resources/WKF/SmallSystemRole.gif");

	protected Color mainColor, backColor, emphasizedMainColor, emphasizedBackColor;

	protected SWLContainerControls controlsArea;

	public RoleContainerGR(Role role, SwimmingLaneRepresentation aDrawing) {
		super(role, ShapeType.RECTANGLE, aDrawing);

		setLayer(SWLEditorConstants.ROLE_LAYER);

		setMinimalWidth(180);
		setMinimalHeight(80);

		// setDimensionConstraints(DimensionConstraints.CONTAINER);

		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(0, CONTAINER_LABEL_HEIGHT, 0, 0));

		/*mainColor = role.getColor();
		backColor = new Color ((255*3+mainColor.getRed())/4,(255*3+mainColor.getGreen())/4,(255*3+mainColor.getBlue())/4);

		setIsFloatingLabel(true);
		setTextStyle(TextStyle.makeTextStyle(mainColor,new Font("SansSerif", Font.BOLD, 12)));

		updatePropertiesFromWKFPreferences();*/
		setIsLabelEditable(!role.isImported());
		updateColors();

		setDecorationPainter(new DecorationPainter() {
			@Override
			public void paintDecoration(org.openflexo.fge.graphics.FGEShapeDecorationGraphics g) {
				double arcSize = 25;
				g.useBackgroundStyle(background);
				g.fillRoundRect(0, 0, g.getWidth() - 1, g.getHeight() - 1 + CONTAINER_LABEL_HEIGHT, arcSize, arcSize);
				g.useForegroundStyle(decorationForeground);
				g.drawRoundRect(0, 0, g.getWidth() - 1, g.getHeight() - 1 + CONTAINER_LABEL_HEIGHT, arcSize, arcSize);
				g.fillArc(0, g.getHeight() + CONTAINER_LABEL_HEIGHT - arcSize, arcSize, arcSize, 180, 90);
				g.fillArc(g.getWidth() - arcSize, g.getHeight() + CONTAINER_LABEL_HEIGHT - arcSize, arcSize, arcSize, 270, 90);
				g.fillRect(arcSize / 2, g.getHeight() - arcSize / 2 + CONTAINER_LABEL_HEIGHT, g.getWidth() - arcSize + 1, arcSize / 2);
				g.fillRect(0, g.getHeight() - arcSize / 2 - 2 + CONTAINER_LABEL_HEIGHT, g.getWidth(), 3);

				Rectangle labelBoundsRect = getNormalizedLabelBounds();
				labelBoundsRect.x = labelBoundsRect.x - (int) getX();
				labelBoundsRect.y = labelBoundsRect.y - (int) getY();

				g.useBackgroundStyle(BackgroundStyle.makeColoredBackground(Color.WHITE));
				g.fillRoundRect(labelBoundsRect.x, labelBoundsRect.y, labelBoundsRect.width, labelBoundsRect.height, 10, 10);
				g.useForegroundStyle(decorationForeground);
				g.drawRoundRect(labelBoundsRect.x, labelBoundsRect.y, labelBoundsRect.width, labelBoundsRect.height, 10, 10);

				Color bestColor = FGEUtils.chooseBestColor(mainColor, Color.WHITE, mainColor, FGEUtils.emphasizedColor(mainColor),
						emphasizedMainColor);
				g.useTextStyle(TextStyle.makeTextStyle(bestColor, FGEConstants.DEFAULT_TEXT_FONT));
				g.drawString(getRole().getName(), g.getWidth() / 2, g.getHeight() - 9 + CONTAINER_LABEL_HEIGHT,
						HorizontalTextAlignment.CENTER);

				g.useBackgroundStyle(decorationBackground);
				g.fillCircle(new FGEPoint(15, 5), new FGEDimension(22, 22));
				g.useForegroundStyle(decorationForeground);
				g.drawCircle(new FGEPoint(15, 5), new FGEDimension(22, 22));

			};

			@Override
			public boolean paintBeforeShape() {
				return false;
			}
		});

		setForeground(ForegroundStyle.makeNone());
		setBackground(BackgroundStyle.makeEmptyBackground());

		role.addObserver(this);

		setDimensionConstraints(DimensionConstraints.UNRESIZABLE);

		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		setLocationConstrainedArea(FGEHalfLine.makeHalfLine(new FGEPoint(SWIMMING_LANE_BORDER, SWIMMING_LANE_BORDER),
				SimplifiedCardinalDirection.SOUTH));

		anchorLocation();

		updateControlArea();
		addToMouseClickControls(new AnnotationMouseClickControl());
	}

	/*@Override
	public ShapeView<Role> makeShapeView(DrawingController<?> controller)
	{
		return new RoleContainerView(this,controller);
	}

	public class RoleContainerView extends ShapeView<Role>
	{
		public RoleContainerView(RoleContainerGR aGraphicalRepresentation,DrawingController<?> controller)
		{
			super(aGraphicalRepresentation,controller);
			JButton plus = new JButton(IconLibrary.PLUS);
			plus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Prout le plus");
				}
			});
			add(plus);
			plus.setBounds(10,10,20,20);
			validate();
		}
	}*/

	protected static boolean isInsideRectangle(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
			MouseEvent event, FGERectangle rect) {
		ShapeView view = (ShapeView) controller.getDrawingView().viewForObject(graphicalRepresentation);
		Rectangle boxRect = new Rectangle((int) (rect.getX() * controller.getScale()), (int) (rect.getY() * controller.getScale()),
				(int) (rect.getWidth() * controller.getScale()), (int) (rect.getHeight() * controller.getScale()));
		Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
		return boxRect.contains(clickLocation);
	}

	private double roleNameX = -1;

	@Override
	public double getAbsoluteTextX() {
		if (roleNameX == -1) {
			roleNameX = 40 + getNormalizedLabelSize().width / 2;
		}
		return roleNameX;
	}

	@Override
	public double getAbsoluteTextY() {
		return 15;
	}

	@Override
	public String getText() {
		return getRole().getName();
	}

	@Override
	public void setTextNoNotification(String text) {
		if (!getRole().isImported()) {
			try {
				getRole().setName(text);
				roleNameX = -1;
			} catch (DuplicateRoleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Role getRole() {
		return getDrawable();
	}

	@Override
	protected boolean supportShadow() {
		return false;
	}

	@Override
	public void updatePropertiesFromWKFPreferences() {
		super.updatePropertiesFromWKFPreferences();

		setIsFloatingLabel(true);
		setTextStyle(TextStyle.makeTextStyle(emphasizedMainColor, new Font("SansSerif", Font.BOLD, 12)));

		// Those are the styles used by border painter (not the one used for shape itself, which are empty)

		background = BackgroundStyle.makeColorGradientBackground(backColor, Color.WHITE, ColorGradientDirection.SOUTH_EAST_NORTH_WEST);

		decorationForeground = ForegroundStyle.makeStyle(mainColor);
		decorationForeground.setLineWidth(0.2);

		if (getRole().getIsSystemRole()) {
			decorationBackground = BackgroundStyle.makeImageBackground(SYSTEM_ROLE_ICON);
		} else {
			decorationBackground = BackgroundStyle.makeImageBackground(USER_ROLE_ICON);
		}

		decorationBackground.setImageBackgroundType(ImageBackgroundType.OPAQUE);
		decorationBackground.setImageBackgroundColor(Color.WHITE);
		decorationBackground.setDeltaX(16);
		decorationBackground.setDeltaY(6);
		decorationBackground.setUseTransparency(true);
		decorationBackground.setTransparencyLevel(0.9f);
	}

	/*@Override
	public double getX()
	{
		return SWIMMING_LANE_BORDER;
	}

	@Override
	public void setXNoNotification(double posX)
	{
		// not applicable
	}

	@Override
	public double getY()
	{
		double returned = SWIMMING_LANE_BORDER+PORT_REGISTERY_HEIGHT;
		for (Role r : getRole().getRoleList().getRoles()) {
			if (r == getRole()) {
				return returned;
			}
			GraphicalRepresentation<?> gr = getGraphicalRepresentation(r);
			if (gr instanceof RoleContainerGR) { // What else could it be ???
				RoleContainerGR roleGR = (RoleContainerGR)gr;
				returned += roleGR.getHeight()+2*SWIMMING_LANE_BORDER;
			}
		}
		logger.warning("Unexpected situation here");
		return returned;
	}

	@Override
	public void setYNoNotification(double posY)
	{
		// not applicable
	}
	 */

	private void anchorLocation() {
		setX(SWIMMING_LANE_BORDER);
		setY(getDrawing().yForObject(getRole()));
	}

	@Override
	public double getWidth() {
		return getDrawingGraphicalRepresentation().getWidth() - 2 * SWIMMING_LANE_BORDER;
	}

	@Override
	public double getHeight() {
		return getSwimmingLaneHeight() * getSwimmingLaneNb();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getModel()) {
			if (dataModification instanceof NodeInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
				notifyShapeNeedsToBeRedrawn();
				notifyObjectMoved();
				notifyObjectResized();
			} else if (dataModification instanceof NodeRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof RoleColorChange || "color".equals(dataModification.propertyName())) {
				updateColors();
				notifyShapeNeedsToBeRedrawn();
			} else if (dataModification instanceof RoleNameChange || "name".equals(dataModification.propertyName())) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if (dataModification instanceof ObjectLocationChanged) {
				notifyObjectMoved();
			} else if (dataModification instanceof ObjectSizeChanged) {
				notifyObjectResized();
			} else if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals(getDrawing().SWIMMING_LANE_NB_KEY())) {
					getDrawing().invalidateGraphicalObjectsHierarchy(getRole());
					getDrawing().updateGraphicalObjectsHierarchy();
					for (GraphicalRepresentation<?> gr : getDrawing().getDrawingGraphicalRepresentation()
							.getContainedGraphicalRepresentations()) {
						if (gr instanceof RoleContainerGR) {
							((RoleContainerGR) gr).notifyObjectHasMoved();
						}
					}
					getDrawingGraphicalRepresentation().notifyObjectResized(null);
				} else if (((WKFAttributeDataModification) dataModification).getAttributeName().equals(
						getDrawing().SWIMMING_LANE_HEIGHT_KEY())) {
					getDrawing().invalidateGraphicalObjectsHierarchy(getRole());
					getDrawing().updateGraphicalObjectsHierarchy();
					for (GraphicalRepresentation<?> gr : getDrawing().getDrawingGraphicalRepresentation()
							.getContainedGraphicalRepresentations()) {
						if (gr instanceof RoleContainerGR) {
							((RoleContainerGR) gr).notifyObjectHasMoved();
						}
					}
					getDrawingGraphicalRepresentation().notifyObjectResized(null);
				} else if (((WKFAttributeDataModification) dataModification).getAttributeName().equals(
						getDrawing().SWIMMING_LANE_INDEX_KEY())) {
					getDrawing().reindexForNewObjectIndex(getRole());
				} else if ("isSystemRole".equals(((WKFAttributeDataModification) dataModification).getAttributeName())) {
					updatePropertiesFromWKFPreferences();
					notifyShapeNeedsToBeRedrawn();
				} else {
					notifyShapeNeedsToBeRedrawn();
				}
			} else if (dataModification instanceof ConvertedIntoLocalObject) {
				setIsLabelEditable(!getRole().isImported());
			}
		}
	}

	private void updateColors() {
		if (getRole() != null) {
			mainColor = getRole().getColor();
			if (mainColor == null) {
				mainColor = Color.RED; // See also org.openflexo.wkf.roleeditor.RoleGR.getRoleColor() and
			}
			// org.openflexo.components.browser.wkf.RoleElement.buildCustomIcon(Color)
			// org.openflexo.wkf.processeditor.gr.AbstractActivityNodeGR.getMainBgColor()
			backColor = new Color((255 * 3 + mainColor.getRed()) / 4, (255 * 3 + mainColor.getGreen()) / 4,
					(255 * 3 + mainColor.getBlue()) / 4);
			emphasizedMainColor = FGEUtils.chooseBestColor(Color.WHITE, FGEUtils.emphasizedColor(mainColor), mainColor);
			emphasizedBackColor = FGEUtils.emphasizedColor(backColor);
			updatePropertiesFromWKFPreferences();
		}
	}

	@Override
	public FGEArea getLocationConstrainedAreaForChild(AbstractNodeGR node) {
		Vector<FGESegment> lines = new Vector<FGESegment>();
		for (int i = 0; i < getSwimmingLaneNb(); i++) {
			double x1 = SWIMMING_LANE_BORDER - node.getBorder().left;
			double x2 = getWidth() - SWIMMING_LANE_BORDER - node.getWidth() - node.getBorder().left;
			double y = i * getHeight() / getSwimmingLaneNb() + getHeight() / getSwimmingLaneNb() / 2 - node.getHeight() / 2
					- node.getBorder().top;
			lines.add(new FGESegment(x1, y, x2, y));
		}
		return FGEUnionArea.makeUnion(lines);
	}

	@Override
	public int getSwimmingLaneNb() {
		return getDrawing().getSwimmingLaneNb(getRole());
	}

	@Override
	public void setSwimmingLaneNb(int swlNb) {
		getDrawing().setSwimmingLaneNb(swlNb, getRole());
	}

	@Override
	public int getSwimmingLaneHeight() {
		return getDrawing().getSwimmingLaneHeight(getRole());
	}

	@Override
	public void setSwimmingLaneHeight(int height) {
		getDrawing().setSwimmingLaneHeight(height, getRole());
	}

	private boolean objectIsBeeingDragged = false;

	@Override
	public void notifyObjectWillMove() {
		super.notifyObjectWillMove();
		objectIsBeeingDragged = true;
	}

	@Override
	public void notifyObjectHasMoved() {
		if (objectIsBeeingDragged) {
			getDrawing().reindexObjectForNewVerticalLocation(getRole(), getY());
			anchorLocation();
			for (GraphicalRepresentation<?> gr : getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations()) {
				if (gr instanceof ShapeGraphicalRepresentation && gr != this) {
					((ShapeGraphicalRepresentation<?>) gr).notifyObjectHasMoved();
				}
			}
		}
		objectIsBeeingDragged = false;
		super.notifyObjectHasMoved();
		anchorLocation();
	}

	@Override
	public void notifyObjectHasResized() {
		for (GraphicalRepresentation gr : getContainedGraphicalRepresentations()) {
			if (gr instanceof AbstractNodeGR) {
				((AbstractNodeGR) gr).resetLocationConstrainedArea();
			}
		}
		super.notifyObjectHasResized();
		updateControlArea();
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return concatenedList;
	}

	private FGEArea lanes;
	private ControlArea<?> lanesArea;
	private ConcatenedList<ControlArea<?>> concatenedList;

	private void updateControlArea() {
		Vector<FGESegment> lines = new Vector<FGESegment>();
		for (int i = 0; i < getSwimmingLaneNb(); i++) {
			double y = i / (double) getSwimmingLaneNb() + 1 / (double) getSwimmingLaneNb() / 2;
			lines.add(new FGESegment(0, y, 1, y));
		}
		lanes = FGEUnionArea.makeUnion(lines);
		lanesArea = new ControlArea<FGEArea>(this, lanes) {
			@Override
			public Cursor getDraggingCursor() {
				return Cursor.getDefaultCursor();
			}

			@Override
			public boolean isDraggable() {
				return false;
			}

			@Override
			public Rectangle paint(FGEGraphics drawingGraphics) {
				Graphics2D oldGraphics = drawingGraphics.cloneGraphics();
				drawingGraphics.setDefaultForeground(ForegroundStyle.makeStyle(Color.LIGHT_GRAY, 0.4f, DashStyle.BIG_DASHES));
				AffineTransform at = GraphicalRepresentation.convertNormalizedCoordinatesAT(RoleContainerGR.this,
						drawingGraphics.getGraphicalRepresentation());
				getArea().transform(at).paint(drawingGraphics);
				drawingGraphics.releaseClonedGraphics(oldGraphics);
				return null;
			}
		};
		controlsArea = new SWLContainerControls(this);
		concatenedList = new ConcatenedList<ControlArea<?>>();
		concatenedList.addElementList(super.getControlAreas());
		concatenedList.addElement(lanesArea);
		concatenedList.addElement(controlsArea);
	}

	public ControlArea getLanesArea() {
		return lanesArea;
	}
}
