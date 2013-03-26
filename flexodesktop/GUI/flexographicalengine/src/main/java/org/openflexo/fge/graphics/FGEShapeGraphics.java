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

import java.util.logging.Logger;

import org.openflexo.fge.ShapeGraphicalRepresentation;

public class FGEShapeGraphics extends FGEGraphics {

	private static final Logger logger = Logger.getLogger(FGEShapeGraphics.class.getPackage().getName());

	public FGEShapeGraphics(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		super(aGraphicalRepresentation);
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	protected void applyCurrentBackgroundStyle() {
		super.applyCurrentBackgroundStyle();

		if (currentBackground instanceof BackgroundImageBackgroundStyle && ((BackgroundImageBackgroundStyle) currentBackground).getFitToShape()) {
			BackgroundImageBackgroundStyle bgImage = (BackgroundImageBackgroundStyle) currentBackground;
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

}
