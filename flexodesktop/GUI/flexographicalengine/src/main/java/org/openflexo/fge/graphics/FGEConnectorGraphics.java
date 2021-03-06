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
package org.openflexo.fge.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSymbol;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;

public class FGEConnectorGraphics extends FGEGraphics {

	private static final Logger logger = Logger.getLogger(FGEConnectorGraphics.class.getPackage().getName());

	private FGESymbolGraphics symbolGraphics;

	public FGEConnectorGraphics(ConnectorGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation);
		symbolGraphics = new FGESymbolGraphics(aGraphicalRepresentation);
	}

	@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return (ConnectorGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	@Override
	public void createGraphics(Graphics2D graphics2D, DrawingController controller) {
		super.createGraphics(graphics2D, controller);
		symbolGraphics.createGraphics(graphics2D, controller);
	}

	@Override
	public void releaseGraphics() {
		super.releaseGraphics();
		symbolGraphics.releaseGraphics();
	}

	/**
	 * 
	 * @param point
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	public void drawSymbol(FGEPoint point, ConnectorSymbol symbol, double size, double angle) {
		drawSymbol(point.x, point.y, symbol, size, angle);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	public void drawSymbol(double x, double y, ConnectorSymbol symbol, double size, double angle) {
		Point p = convertNormalizedPointToViewCoordinates(x, y);

		if (getGraphicalRepresentation().getApplyForegroundToSymbols()) {
			symbolGraphics.setDefaultForeground(symbol.getForegroundStyle(getGraphicalRepresentation().getForeground()));
		}

		Color fgColor = getGraphicalRepresentation().getForeground().getColor();
		Color bgColor = Color.WHITE;
		symbolGraphics.setDefaultBackground(symbol.getBackgroundStyle(fgColor, bgColor));

		FGEArea symbolShape = symbol.getSymbol();

		// Debug: to see bounds
		// symbolShape = new FGEUnionArea(symbolShape,new FGERectangle(0,0,1,1,Filling.NOT_FILLED));

		symbolShape = symbolShape.transform(AffineTransform.getTranslateInstance(-0.5, -0.5));
		symbolShape = symbolShape.transform(AffineTransform.getRotateInstance(-angle));
		symbolShape = symbolShape.transform(AffineTransform.getScaleInstance(size * getScale(), size * getScale()));
		symbolShape = symbolShape.transform(AffineTransform.getTranslateInstance(p.x - size / 2 * Math.cos(-angle) * getScale(), p.y - size
				/ 2 * Math.sin(-angle) * getScale()));

		// System.out.println("Ce que je dessine: "+symbolShape);

		symbolShape.paint(symbolGraphics);

	}

}
