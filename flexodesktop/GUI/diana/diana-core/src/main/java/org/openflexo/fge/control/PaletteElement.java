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
package org.openflexo.fge.control;

import java.io.Serializable;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;

public interface PaletteElement extends Serializable {

	public static final Logger logger = Logger.getLogger(PaletteElement.class.getPackage().getName());

	public ShapeGraphicalRepresentation getGraphicalRepresentation();

	public boolean acceptDragging(DrawingTreeNode<?, ?> target);

	public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation);

	// public DrawingPalette getPalette();

	public void delete();

	/*public static class PaletteElementGraphicalRepresentation extends ShapeGraphicalRepresentationImpl {
		private ShapeGraphicalRepresentation originalGR;

		public PaletteElementGraphicalRepresentation(ShapeType shapeType, DrawingPalette palette, PaletteElement paletteElement,
				PaletteDrawing paletteDrawing) {
			super();
			setFactory(palette.getFactory());
			setDrawing(paletteDrawing);
			setShapeType(shapeType);

		}

		public PaletteElementGraphicalRepresentation(ShapeGraphicalRepresentation shapeGR, DrawingPalette palette,
				PaletteElement paletteElement, PaletteDrawing paletteDrawing) {
			this(shapeGR.getShapeType(), palette, paletteElement, paletteDrawing);
			// Copy parameters...
			setsWith(shapeGR);
			this.originalGR = shapeGR;
			// setValidated(true);

			logger.info("Created " + this);
		}

		@Override
		public void delete() {
			if (originalGR != null) {
				originalGR.delete();
			}
			super.delete();
		}

		@Override
		public final void setIsFocusable(boolean isFocusable) {
			super.setIsFocusable(isFocusable);
		}

		@Override
		public final boolean getIsFocusable() {
			return super.getIsFocusable();
		}

		@Override
		public final void setIsSelectable(boolean isSelectable) {
			super.setIsSelectable(isSelectable);
		}

		@Override
		public final boolean getIsSelectable() {
			return super.getIsSelectable();
		}

		@Override
		public final void setIsReadOnly(boolean readOnly) {
			super.setIsReadOnly(readOnly);
		}

		@Override
		public final boolean getIsReadOnly() {
			return super.getIsReadOnly();
		}

		@Override
		public final void setLocationConstraints(LocationConstraints locationConstraints) {
			super.setLocationConstraints(locationConstraints);
		}

		@Override
		public final LocationConstraints getLocationConstraints() {
			return super.getLocationConstraints();
		}

	}*/

}
