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
package org.openflexo.fge.geomedit.edition;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.fge.geomedit.Line;
import org.openflexo.fge.geomedit.construction.LineConstruction;
import org.openflexo.fge.geomedit.construction.LineReference;


public class ObtainLine extends EditionInput<FGELine> 
{
	public static int preferredMethodIndex = 0;

	public ObtainLine(String anInputLabel, GeomEditController controller) 
	{
		super(anInputLabel, controller);

		availableMethods.add(new LineSelection());
	}

	@Override
	protected int getPreferredMethodIndex()
	{
		return preferredMethodIndex;
	}

	@Override
	public void setActiveMethod(EditionInputMethod aMethod)
	{
		super.setActiveMethod(aMethod);
		preferredMethodIndex = availableMethods.indexOf(aMethod);
	}

	public class LineSelection extends EditionInputMethod<FGELine,ObtainLine> {

		private GeometricGraphicalRepresentation focusedObject;

		public LineSelection() {
			super("With mouse", ObtainLine.this);
		}		

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (focusedObject != null) {
				focusedObject.setIsFocused(false);
				referencedLine = (Line)focusedObject.getDrawable();
				setConstruction(new LineReference(referencedLine.getConstruction()));
				done();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			GraphicalRepresentation focused = getFocusRetriever().getFocusedObject(e);

			if (focusedObject != null && focusedObject != focused) {
				focusedObject.setIsFocused(false);
			}

			if (focused instanceof GeometricGraphicalRepresentation 
					&& ((GeometricGraphicalRepresentation)focused).getGeometricObject() instanceof FGELine) {
				focusedObject = (GeometricGraphicalRepresentation)focused;
				focusedObject.setIsFocused(true);
			}
			else {
				focusedObject = null;
			}

		}

		@Override
		public InputComponent getInputComponent()
		{
			return new InputComponentButton(LineSelection.this);
		}


	}

	@Override
	public LineConstruction getConstruction()
	{
		return (LineConstruction)super.getConstruction();
	}
	
	@Override
	public boolean endOnRightClick()
	{
		return false;
	}
	
	private Line referencedLine;
	
	public Line getReferencedLine()
	{
		return referencedLine;
	}


}