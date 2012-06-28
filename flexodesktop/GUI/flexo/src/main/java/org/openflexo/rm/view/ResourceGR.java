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
package org.openflexo.rm.view;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundImage;
import org.openflexo.fge.graphics.BackgroundStyle.BackgroundImage.ImageBackgroundType;
import org.openflexo.fge.graphics.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.graphics.DecorationPainter;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.icon.IconLibrary;

public class ResourceGR extends ShapeGraphicalRepresentation<FlexoResource<? extends FlexoResourceData>> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ResourceGR.class.getPackage().getName());

	public static final int WIDTH = 100;
	public static final int HEIGHT = 40;

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private final TextStyle resourceTypeStyle;
	private final TextStyle lastUpdateStyle;

	public ResourceGR(FlexoResource<? extends FlexoResourceData> aResource, Drawing<?> aDrawing) {
		super(ShapeType.RECTANGLE, aResource, aDrawing);
		setWidth(40);
		setHeight(60);
		// setText(getRole().getName());
		setIsFloatingLabel(false);
		getShape().setIsRounded(true);
		setDimensionConstraints(DimensionConstraints.FREELY_RESIZABLE);
		updateStyles();
		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(10, 10, 10, 10));

		setAdjustMinimalWidthToLabelWidth(true);
		setMinimalWidth(150);

		setDecorationPainter(new ResourceDecorationPainter(aResource));

		resourceTypeStyle = TextStyle.makeTextStyle(aResource.getResourceType().getMainColor().darker(), new Font("SansSerif", Font.BOLD,
				10));
		lastUpdateStyle = TextStyle.makeTextStyle(Color.GRAY, new Font("SansSerif", Font.ITALIC, 10));

		setShapePainter(new ShapePainter() {
			@Override
			public void paintShape(FGEShapeGraphics g) {
				g.useTextStyle(resourceTypeStyle);
				g.drawString(getResource().getResourceType().getName(), new FGEPoint(0.5, 0.25), HorizontalTextAlignment.CENTER);
				g.useTextStyle(lastUpdateStyle);
				g.drawString(new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getResource().getLastUpdate()), new FGEPoint(0.5, 0.75),
						HorizontalTextAlignment.CENTER);
			};
		});

	}

	private void updateStyles() {
		foreground = ForegroundStyle.makeStyle(getResource().getResourceType().getMainColor());
		foreground.setLineWidth(0.5);
		background = BackgroundStyle.makeColorGradientBackground(getResource().getResourceType().getMainColor(), Color.WHITE,
				ColorGradientDirection.SOUTH_WEST_NORTH_EAST);
		setForeground(foreground);
		setBackground(background);
	}

	public static class ResourceDecorationPainter implements DecorationPainter, Cloneable {
		private final FlexoResource<? extends FlexoResourceData> resource;
		protected ForegroundStyle decorationForeground;
		protected BackgroundImage decorationBackground;

		@Override
		public ResourceDecorationPainter clone() {
			return new ResourceDecorationPainter(resource);
		}

		public ResourceDecorationPainter(FlexoResource<? extends FlexoResourceData> aResource) {
			resource = aResource;

			updateDecorationBackground();

			decorationForeground = ForegroundStyle.makeStyle(resource.getResourceType().getMainColor());
			decorationForeground.setLineWidth(1);
		}

		private void updateDecorationBackground() {
			decorationBackground = BackgroundStyle.makeImageBackground(IconLibrary.getIconForResourceType(resource.getResourceType()));
			decorationBackground.setImageBackgroundType(ImageBackgroundType.OPAQUE);
			decorationBackground.setImageBackgroundColor(Color.WHITE);
			decorationBackground.setDeltaX(8);
			decorationBackground.setDeltaY(-2);
			decorationBackground.setUseTransparency(true);
			decorationBackground.setTransparencyLevel(0.9f);
		}

		@Override
		public void paintDecoration(FGEShapeDecorationGraphics g) {

			/*if (!decorationForeground.getColor().equals(role.getColor())) {
				decorationForeground.setColor(role.getColor());
			}*/

			/*if (role.getIsSystemRole() != isSystemRole) {
				updateDecorationBackground();
			}*/

			g.useBackgroundStyle(decorationBackground);
			g.fillRect(new FGEPoint(15, 5), new FGEDimension(22, 22));
			g.useForegroundStyle(decorationForeground);
			g.drawRect(new FGEPoint(15, 5), new FGEDimension(22, 22));

		};

		@Override
		public boolean paintBeforeShape() {
			return false;
		}
	}

	@Override
	public String getText() {
		return getResource().getName();
	}

	public FlexoResource<? extends FlexoResourceData> getResource() {
		return getDrawable();
	}

	@Override
	public Rectangle getShape() {
		return (Rectangle) super.getShape();
	}

}
