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
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.ShapeView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.toolbox.FileResource;
import org.openflexo.wkf.swleditor.SWLEditorConstants;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class RoleContainerGR extends SWLObjectGR<Role> implements SWLContainerGR {

	static final Logger logger = Logger.getLogger(OperationNodeGR.class.getPackage().getName());

	private static final TextStyle ROLE_LABEL_TEXT_STYLE = TextStyle.makeTextStyle(Color.BLACK, new Font("Arial", Font.PLAIN, 12));

	protected BackgroundStyle background;
	protected ForegroundStyle decorationForeground;

	private static final FileResource USER_ROLE_ICON = new FileResource("Resources/WKF/SmallRole.gif");
	private static final FileResource SYSTEM_ROLE_ICON = new FileResource("Resources/WKF/SmallSystemRole.gif");

	protected Color mainColor, backColor, emphasizedMainColor, emphasizedBackColor;

	private SWLContainerResizeAreas controlAreas;

	public RoleContainerGR(Role role, SwimmingLaneRepresentation aDrawing) {
		super(role, ShapeType.RECTANGLE, aDrawing);
		((org.openflexo.fge.shapes.Rectangle) getShapeSpecification()).setIsRounded(false);
		setLayer(SWLEditorConstants.ROLE_LAYER);

		setMinimalWidth(180);
		setMinimalHeight(80);
		setBorder(new ShapeBorder(0, 0, 0, 0));
		// setDimensionConstraints(DimensionConstraints.CONTAINER);

		/*mainColor = role.getColor();
		backColor = new Color ((255*3+mainColor.getRed())/4,(255*3+mainColor.getGreen())/4,(255*3+mainColor.getBlue())/4);

		setIsFloatingLabel(true);
		setTextStyle(TextStyle.makeTextStyle(mainColor,new Font("SansSerif", Font.BOLD, 12)));

		updatePropertiesFromWKFPreferences();*/
		updateColors();

		setDecorationPainter(new DecorationPainter() {
			@Override
			public void paintDecoration(org.openflexo.fge.graphics.FGEShapeDecorationGraphics g) {
				g.useBackgroundStyle(background);
				g.fillRect(0, 0, g.getWidth() - 1, g.getHeight() - 1);
				g.useForegroundStyle(decorationForeground);
				g.drawRect(0, 0, g.getWidth() - 1, g.getHeight() - 1);
				double x = 10 + ROLE_LABEL_TEXT_STYLE.getFont().getSize();
				g.drawLine(x, 0, x, getHeight());
				g.useTextStyle(ROLE_LABEL_TEXT_STYLE);
				double y = getHeight() / 2;
				AffineTransform at = AffineTransform.getScaleInstance(g.getScale(), g.getScale());
				int orientation = -90;
				at.concatenate(AffineTransform.getRotateInstance(Math.toRadians(orientation)));

				g.drawString(getDrawable().getName(), x, y, orientation, HorizontalTextAlignment.CENTER);
			};

			@Override
			public boolean paintBeforeShape() {
				return false;
			}
		});
		setForeground(ForegroundStyle.makeNone());
		setBackground(BackgroundStyle.makeEmptyBackground());

		role.addObserver(this);

		setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);

		setLocationConstraints(LocationConstraints.AREA_CONSTRAINED);
		setLocationConstrainedArea(FGEHalfLine.makeHalfLine(new FGEPoint(SWIMMING_LANE_BORDER, SWIMMING_LANE_BORDER),
				SimplifiedCardinalDirection.SOUTH));
		setMinimalHeight(120);
		anchorLocation();
		controlAreas = new SWLContainerResizeAreas(this);
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		return controlAreas.getControlAreas();
	}

	/*@Override
	public ShapeView<Role> makeShapeView(DianaEditor controller)
	{
		return new RoleContainerView(this,controller);
	}

	public class RoleContainerView extends ShapeView<Role>
	{
		public RoleContainerView(RoleContainerGR aGraphicalRepresentation,DianaEditor controller)
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

	protected static boolean isInsideRectangle(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
			MouseEvent event, FGERectangle rect) {
		ShapeView view = (ShapeView) controller.getDrawingView().viewForNode(graphicalRepresentation);
		Rectangle boxRect = new Rectangle((int) (rect.getX() * controller.getScale()), (int) (rect.getY() * controller.getScale()),
				(int) (rect.getWidth() * controller.getScale()), (int) (rect.getHeight() * controller.getScale()));
		Point clickLocation = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), view);
		return boxRect.contains(clickLocation);
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
		if (backColor == null) {
			backColor = Color.BLACK;
		}
		background = BackgroundStyle.makeColorGradientBackground(backColor, new Color(backColor.getRed() / 2 + 128,
				backColor.getGreen() / 2 + 128, backColor.getBlue() / 2 + 128), ColorGradientDirection.SOUTH_EAST_NORTH_WEST);

		decorationForeground = ForegroundStyle.makeStyle(mainColor);
		decorationForeground.setLineWidth(0.4);
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
			GraphicalRepresentation gr = getGraphicalRepresentation(r);
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

	@Override
	public void anchorLocation() {
		setX(SWIMMING_LANE_BORDER);
		setY(getDrawing().yForObject(getRole()));
	}

	@Override
	public double getWidth() {
		return getDrawingGraphicalRepresentation().getWidth() - 2 * SWIMMING_LANE_BORDER;
	}

	@Override
	public double getHeight() {
		return getDrawing().getHeight(getRole());
	}

	@Override
	public void setHeightNoNotification(double height) {
		getDrawing().setHeight(getRole(), height);
	}

	@Override
	public void setWidthNoNotification(double aValue) {
		getDrawingGraphicalRepresentation().setWidth(aValue + 2 * SWIMMING_LANE_BORDER);
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
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals(
						getDrawing().SWIMMING_LANE_INDEX_KEY(getRole()))) {
					getDrawing().reindexForNewObjectIndex(getRole());
				} else if ("isSystemRole".equals(((WKFAttributeDataModification) dataModification).getAttributeName())) {
					updatePropertiesFromWKFPreferences();
					notifyShapeNeedsToBeRedrawn();
				} else {
					notifyShapeNeedsToBeRedrawn();
				}
			} else if (dataModification instanceof RoleRemoved) {
				getDrawing().requestRebuildCompleteHierarchy();
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
			for (GraphicalRepresentation gr : getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations()) {
				if (gr instanceof ShapeGraphicalRepresentation && gr != this) {
					((ShapeGraphicalRepresentation) gr).notifyObjectHasMoved();
				}
			}
		}
		objectIsBeeingDragged = false;
		super.notifyObjectHasMoved();
		anchorLocation();
	}

	@Override
	public void notifyObjectResized(FGEDimension oldSize) {
		super.notifyObjectResized(oldSize);
		if (isRegistered()) {
			getDrawing().relayoutRoleContainers();
		}
	}

	@Override
	public void notifyObjectHasResized() {
		super.notifyObjectHasResized();
		getDrawing().relayoutRoleContainers();
	}
}
