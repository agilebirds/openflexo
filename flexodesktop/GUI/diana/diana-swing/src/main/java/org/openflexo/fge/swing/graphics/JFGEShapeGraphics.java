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
package org.openflexo.fge.swing.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.swing.view.JShapeView;

public class JFGEShapeGraphics extends JFGEGraphics implements FGEShapeGraphics {

	private static final Logger logger = Logger.getLogger(JFGEShapeGraphics.class.getPackage().getName());

	private JFGEShapeDecorationGraphics shapeDecorationGraphics;

	public <O> JFGEShapeGraphics(ShapeNode<O> node, JShapeView<O> view) {
		super(node, view);
		shapeDecorationGraphics = new JFGEShapeDecorationGraphics(node, view);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	public JFGEShapeDecorationGraphics getShapeDecorationGraphics() {
		return shapeDecorationGraphics;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	public void createGraphics(Graphics2D graphics2D) {
		super.createGraphics(graphics2D);
		shapeDecorationGraphics.createGraphics(graphics2D);
	}

	public void releaseGraphics() {
		super.releaseGraphics();
		shapeDecorationGraphics.releaseGraphics();
	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		super.applyCurrentBackgroundStyle();

		if (getCurrentBackground() instanceof BackgroundImageBackgroundStyle
				&& ((BackgroundImageBackgroundStyle) getCurrentBackground()).getFitToShape()) {
			BackgroundImageBackgroundStyle bgImage = (BackgroundImageBackgroundStyle) getCurrentBackground();
			bgImage.setDeltaX(0);
			bgImage.setDeltaY(0);
			if (bgImage.getImage() != null) {
				// SGU: Big performance issue here
				// I add to declare new methods without notification because in case of
				// the inspector is shown, an instability is raising: the shape is
				// continuously switching between two values
				// Please investigate
				bgImage.setScaleXNoNotification(getGraphicalRepresentation().getWidth() / bgImage.getImage().getWidth(null));
				bgImage.setScaleYNoNotification(getGraphicalRepresentation().getHeight() / bgImage.getImage().getHeight(null));
			}
		}

	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	private static final FGEModelFactory SHADOW_FACTORY = FGECoreUtils.TOOLS_FACTORY;

	public void paintShadow() {

		double deep = getGraphicalRepresentation().getShadowStyle().getShadowDepth();
		int blur = getGraphicalRepresentation().getShadowStyle().getShadowBlur();
		double viewWidth = getViewWidth(1.0);
		double viewHeight = getViewHeight(1.0);
		AffineTransform shadowTranslation = AffineTransform.getTranslateInstance(deep / viewWidth, deep / viewHeight);

		int darkness = getGraphicalRepresentation().getShadowStyle().getShadowDarkness();

		Graphics2D oldGraphics = cloneGraphics();

		Area clipArea = new Area(new java.awt.Rectangle(0, 0, getViewWidth(getController().getScale()), getViewHeight(getController()
				.getScale())));
		Area a = new Area(getNode().getFGEShape());
		a.transform(getNode().convertNormalizedPointToViewCoordinatesAT(getController().getScale()));
		clipArea.subtract(a);
		getGraphics().clip(clipArea);

		Color shadowColor = new Color(darkness, darkness, darkness);
		ForegroundStyle foreground = SHADOW_FACTORY.makeForegroundStyle(shadowColor);
		foreground.setUseTransparency(true);
		foreground.setTransparencyLevel(0.5f);
		BackgroundStyle background = SHADOW_FACTORY.makeColoredBackground(shadowColor);
		background.setUseTransparency(true);
		background.setTransparencyLevel(0.5f);
		setDefaultForeground(foreground);
		setDefaultBackground(background);

		for (int i = blur - 1; i >= 0; i--) {
			float transparency = 0.4f - i * 0.4f / blur;
			foreground.setTransparencyLevel(transparency);
			background.setTransparencyLevel(transparency);
			AffineTransform at = AffineTransform.getScaleInstance((i + 1 + viewWidth) / viewWidth, (i + 1 + viewHeight) / viewHeight);
			at.concatenate(shadowTranslation);
			getNode().getFGEShape().transform(at).paint(this);
		}
		releaseClonedGraphics(oldGraphics);
	}

}
