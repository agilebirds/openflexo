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
package org.openflexo.wkf.roleeditor;

import java.awt.Color;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundImageBackgroundStyle.ImageBackgroundType;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.foundation.wkf.dm.RoleNameChange;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.toolbox.ConcatenedList;

public class RoleGR extends ShapeGraphicalRepresentationImpl implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(RoleGR.class.getPackage().getName());

	public static final int WIDTH = 100;
	public static final int HEIGHT = 40;

	private ForegroundStyle foreground;
	private BackgroundStyle background;
	private TextStyle textStyle;

	public RoleGR(Role aRole, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aRole, aDrawing);
		// setText(getRole().getName());
		setIsFloatingLabel(false);
		getShape().setIsRounded(true);
		setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		updateStyles();
		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(25, 25, 25, 25));

		setDecorationPainter(new RoleDecorationPainter(aRole));

		setIsMultilineAllowed(true);
		setAdjustMinimalWidthToLabelWidth(true);
		setAdjustMinimalHeightToLabelHeight(true);

		addToMouseClickControls(new RoleEditorController.ShowContextualMenuControl());
		addToMouseDragControls(new DrawRoleSpecializationControl());

		aRole.addObserver(this);

	}

	@Override
	public double getWidth() {
		return getRole().getWidth(RepresentableFlexoModelObject.DEFAULT, 100);
	}

	@Override
	public void setWidthNoNotification(double width) {
		getRole().setWidth(width, RepresentableFlexoModelObject.DEFAULT);
	}

	@Override
	public double getHeight() {
		return getRole().getHeight(RepresentableFlexoModelObject.DEFAULT, 40);
	}

	@Override
	public void setHeightNoNotification(double height) {
		getRole().setHeight(height, RepresentableFlexoModelObject.DEFAULT);
	}

	@Override
	public RoleListRepresentation getDrawing() {
		return (RoleListRepresentation) super.getDrawing();
	}

	@Override
	public void delete() {
		Role role = getRole();
		super.delete();
		role.deleteObserver(this);
	}

	private void updateStyles() {
		foreground = ForegroundStyle.makeStyle(getRoleColor());
		foreground.setLineWidth(2);
		background = BackgroundStyle.makeColorGradientBackground(getRoleColor(), Color.WHITE, ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		textStyle = TextStyle.makeDefault();
		textStyle.setColor(FGEUtils.chooseBestColor(getRoleColor(), Color.WHITE, Color.BLACK));
		setForeground(foreground);
		setBackground(background);
		setTextStyle(textStyle);
	}

	/**
	 * @return
	 */
	protected Color getRoleColor() {
		// See also org.openflexo.wkf.swleditor.gr.RoleContainerGR.updateColors() and
		// org.openflexo.components.browser.wkf.RoleElement.buildCustomIcon(Color)
		// org.openflexo.wkf.processeditor.gr.AbstractActivityNodeGR.getMainBgColor()
		if (getRole().getColor() != null) {
			return getRole().getColor();
		}
		return Color.RED;
	}

	public class RoleDecorationPainter implements DecorationPainter, Cloneable {
		private Role role;
		protected ForegroundStyle decorationForeground;
		protected BackgroundImageBackgroundStyle decorationBackground;
		private boolean isSystemRole;

		@Override
		public RoleDecorationPainter clone() {
			return new RoleDecorationPainter(role);
		}

		public RoleDecorationPainter(Role aRole) {
			role = aRole;

			updateDecorationBackground();

			decorationForeground = ForegroundStyle.makeStyle(getRoleColor());
			decorationForeground.setLineWidth(2);
		}

		private void updateDecorationBackground() {
			if (role.getIsSystemRole()) {
				isSystemRole = true;
				decorationBackground = BackgroundStyle.makeImageBackground(WKFIconLibrary.SYSTEM_ROLE_ICON);
			} else {
				isSystemRole = false;
				decorationBackground = BackgroundStyle.makeImageBackground(WKFIconLibrary.ROLE_ICON);
			}

			decorationBackground.setImageBackgroundType(ImageBackgroundType.OPAQUE);
			decorationBackground.setImageBackgroundColor(Color.WHITE);
			decorationBackground.setDeltaX(6);
			decorationBackground.setDeltaY(-4);
			decorationBackground.setUseTransparency(true);
			decorationBackground.setTransparencyLevel(0.9f);
		}

		@Override
		public void paintDecoration(FGEShapeDecorationGraphics g) {

			if (!decorationForeground.getColor().equals(getRoleColor())) {
				decorationForeground.setColor(getRoleColor());
			}

			if (role.getIsSystemRole() != isSystemRole) {
				updateDecorationBackground();
			}

			g.useBackgroundStyle(decorationBackground);
			g.fillCircle(new FGEPoint(30, 20), new FGEDimension(22, 22));
			g.useForegroundStyle(decorationForeground);
			g.drawCircle(new FGEPoint(30, 20), new FGEDimension(22, 22));

		};

		@Override
		public boolean paintBeforeShape() {
			return false;
		}
	}

	private boolean isUpdatingPosition = false;

	@Override
	public double getX() {
		if (!getRole().hasLocationForContext(RepresentableFlexoModelObject.DEFAULT)) {
			getRole().getX(RepresentableFlexoModelObject.DEFAULT, getDefaultX());
		}
		return getRole().getX(RepresentableFlexoModelObject.DEFAULT);
	}

	@Override
	public void setXNoNotification(double posX) {
		isUpdatingPosition = true;
		getRole().setX(posX, RepresentableFlexoModelObject.DEFAULT);
		isUpdatingPosition = false;
	}

	@Override
	public double getY() {
		if (!getRole().hasLocationForContext(RepresentableFlexoModelObject.DEFAULT)) {
			getRole().getY(RepresentableFlexoModelObject.DEFAULT, getDefaultY());
		}
		return getRole().getY(RepresentableFlexoModelObject.DEFAULT);
	}

	@Override
	public void setYNoNotification(double posY) {
		isUpdatingPosition = true;
		getRole().setY(posY, RepresentableFlexoModelObject.DEFAULT);
		isUpdatingPosition = false;
	}

	private int defaultX = -1;
	private int defaultY = -1;

	// Override to implement defaut automatic layout
	public double getDefaultX() {
		if (defaultX < 0) {
			doDefaultLayout(10, 10);
		}
		return defaultX;
	}

	// Override to implement defaut automatic layout
	public double getDefaultY() {
		if (defaultY < 0) {
			doDefaultLayout(10, 10);
		}
		return defaultY;
	}

	private void doDefaultLayout(int x, int y) {
		boolean ok = true;
		Enumeration<GraphicalRepresentation> en = getDrawing().getAllGraphicalRepresentations();
		while (en.hasMoreElements()) {
			GraphicalRepresentation gr = en.nextElement();
			if (gr instanceof RoleGR) {
				RoleGR rgr = (RoleGR) gr;
				if (rgr != this) {
					if (rgr.getRole().hasLocationForContext(RepresentableFlexoModelObject.DEFAULT)) {
						java.awt.Rectangle viewBounds = gr.getViewBounds(1.0);
						if (viewBounds.intersects(new java.awt.Rectangle(x, y, WIDTH, HEIGHT))) {
							ok = false;
							if (viewBounds.x + viewBounds.width + WIDTH > getDrawingGraphicalRepresentation().getWidth()) {
								// End of line, we go to the next one
								if (y + 10 + HEIGHT < getDrawingGraphicalRepresentation().getHeight()) {
									doDefaultLayout(10, y + 10 + HEIGHT);
								} else {
									if (logger.isLoggable(Level.WARNING)) {
										logger.warning("Could not find suitable location for role: " + getRole());
									}
									defaultX = 10;
									defaultY = 10;
								}
							} else {
								doDefaultLayout(viewBounds.x + viewBounds.width + 10, y);
							}
						}
					}
				}
			}
		}
		if (ok) {
			defaultX = x;
			defaultY = y;
		}
	}

	private boolean isEditingLabel = false;
	private String editingText;

	// private Color textColor;

	@Override
	public void notifyLabelWillBeEdited() {
		editingText = getText();
		// textColor = getTextStyle().getColor();
		isEditingLabel = true;
		super.notifyLabelWillBeEdited();
	}

	@Override
	public void notifyLabelHasBeenEdited() {
		isEditingLabel = false;
		setTextNoNotification(editingText);
		editingText = null;
		// textColor = null;
		super.notifyLabelHasBeenEdited();
	}

	@Override
	public String getText() {
		if (isEditingLabel) {
			return editingText;
		} else {
			return getRole().getName();
		}
	}

	@Override
	public void setTextNoNotification(String text) {
		if (isEditingLabel) {
			editingText = text;
			/*Role roleWithName = getRole().getRoleList().roleWithName(text); GPO: I abandon this possibility because notifications are not taken into account by textComponent during edition
			if (roleWithName!=null && roleWithName!=getRole()) {
				getTextStyle().setColor(Color.RED);
			} else {
				getTextStyle().setColor(textColor);
			}*/
			return;
		}
		SetPropertyAction set = SetPropertyAction.actionType.makeNewAction(getRole(), null, getDrawing().getEditor());
		set.setKey("name");
		set.setValue(text);
		set.doAction();
		/*try {
			getRole().setName(text);
		} catch (DuplicateRoleException e) {
			e.printStackTrace();
		}*/
	}

	public Role getRole() {
		return getDrawable();
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getRole()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("posX")
						|| ((WKFAttributeDataModification) dataModification).getAttributeName().equals("posY")) {
					if (!isUpdatingPosition) {
						notifyObjectMoved();
					}
				} else if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("roleSpecializations")) {
					if (getDrawing() instanceof RoleListRepresentation) {
						getDrawing().updateGraphicalObjectsHierarchy();
					}
					// This might be not the case when used in palette !!!
				} else if ("isSystemRole".equals(((WKFAttributeDataModification) dataModification).getAttributeName())) {
					notifyShapeNeedsToBeRedrawn();
				} else {
					notifyShapeNeedsToBeRedrawn();
				}
			} else if (dataModification instanceof ObjectSizeChanged) {
				if (!isResizing()) {
					notifyObjectResized();
				}
			} else if (dataModification instanceof ObjectLocationChanged) {
				if (!isUpdatingPosition) {
					notifyObjectMoved();
				}
			} else if (dataModification instanceof RoleNameChange) {
				notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			} else if (dataModification instanceof RoleColorChange) {
				updateStyles();
				notifyShapeNeedsToBeRedrawn();
			}
		}
	}

	private ConcatenedList<ControlArea<?>> controlAreas;

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (controlAreas == null) {
			controlAreas = new ConcatenedList<ControlArea<?>>();
			controlAreas.addElementList(super.getControlAreas());
			controlAreas.addElement(new FloatingPalette(this, getDrawable().getRoleList(), SimplifiedCardinalDirection.EAST));
			controlAreas.addElement(new FloatingPalette(this, getDrawable().getRoleList(), SimplifiedCardinalDirection.WEST));
			controlAreas.addElement(new FloatingPalette(this, getDrawable().getRoleList(), SimplifiedCardinalDirection.NORTH));
			controlAreas.addElement(new FloatingPalette(this, getDrawable().getRoleList(), SimplifiedCardinalDirection.SOUTH));
		}
		return controlAreas;
	}

}
