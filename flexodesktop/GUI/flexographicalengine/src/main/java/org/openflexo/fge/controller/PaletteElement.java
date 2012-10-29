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
package org.openflexo.fge.controller;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.controller.DrawingPalette.PaletteDrawing;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.PaletteElementView;
import org.openflexo.fge.view.ShapeView;

public interface PaletteElement extends Serializable {

	public PaletteElementGraphicalRepresentation getGraphicalRepresentation();

	public boolean acceptDragging(GraphicalRepresentation<?> target);

	public boolean elementDragged(GraphicalRepresentation<?> target, FGEPoint dropLocation);

	public DrawingPalette getPalette();

	public static class PaletteElementGraphicalRepresentation extends ShapeGraphicalRepresentationImpl<PaletteElement> {
		private ShapeGraphicalRepresentation<?> originalGR;

		public PaletteElementGraphicalRepresentation(ShapeType shapeType, PaletteElement paletteElement, PaletteDrawing paletteDrawing) {
			super(shapeType, paletteElement, paletteDrawing);
			// setValidated(true);
		}

		public PaletteElementGraphicalRepresentation(ShapeGraphicalRepresentation<?> shapeGR, PaletteElement paletteElement,
				PaletteDrawing paletteDrawing) {
			this(shapeGR.getShapeType(), paletteElement, paletteDrawing);
			// Copy parameters...
			setsWith(shapeGR);
			this.originalGR = shapeGR;
			// setValidated(true);
		}

		@Override
		public void delete() {
			if (originalGR != null) {
				originalGR.delete();
			}
			super.delete();
		}

		@Override
		public ShapeView<PaletteElement> makeShapeView(DrawingController controller) {
			return new PaletteElementView(this, controller);
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

	}

	public static class PaletteElementTransferable implements Transferable {

		private static DataFlavor _defaultFlavor;

		private TransferedPaletteElement _transferedData;

		public PaletteElementTransferable(PaletteElement element, Point dragOrigin) {
			_transferedData = new TransferedPaletteElement(element, dragOrigin);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { defaultFlavor() };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return true;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return _transferedData;
		}

		public static DataFlavor defaultFlavor() {
			if (_defaultFlavor == null) {
				_defaultFlavor = new DataFlavor(PaletteElementTransferable.class, "PaletteElement");
			}
			return _defaultFlavor;
		}

	}

	public static class TransferedPaletteElement {
		private Point _offset;

		private PaletteElement _transfered;

		public TransferedPaletteElement(PaletteElement element, Point dragOffset) {
			super();
			_transfered = element;
			_offset = dragOffset;
		}

		public Point getOffset() {
			return _offset;
		}

		public PaletteElement getPaletteElement() {
			return _transfered;
		}

	}

}
