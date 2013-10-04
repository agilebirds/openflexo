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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSymbol;
import org.openflexo.fge.geom.FGEPoint;

public interface FGEConnectorGraphics extends FGEGraphics {

	public abstract ConnectorGraphicalRepresentation getGraphicalRepresentation();

	/**
	 * 
	 * @param point
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	public abstract void drawSymbol(FGEPoint point, ConnectorSymbol symbol, double size, double angle);

	/**
	 * 
	 * @param x
	 * @param y
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	public abstract void drawSymbol(double x, double y, ConnectorSymbol symbol, double size, double angle);

}