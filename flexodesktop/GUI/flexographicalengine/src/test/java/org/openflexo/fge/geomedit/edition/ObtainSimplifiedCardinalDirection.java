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

import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geomedit.GeomEditController;


public class ObtainSimplifiedCardinalDirection extends EditionInput<SimplifiedCardinalDirection> 
{
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;
	
	public ObtainSimplifiedCardinalDirection(String anInputLabel, SimplifiedCardinalDirection defaultValue, GeomEditController controller) 
	{
		super(anInputLabel, controller);

		availableMethods.add(new KeyboardSelection(anInputLabel,defaultValue));
	}

	public ObtainSimplifiedCardinalDirection(String anInputLabel, SimplifiedCardinalDirection defaultValue, GeomEditController controller, boolean appendEndSelection) 
	{
		this(anInputLabel,defaultValue,controller);
		if (appendEndSelection) availableMethods.add(new EndEditionSelection());
		endOnRightClick = appendEndSelection;
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

	public class KeyboardSelection extends EditionInputMethod<SimplifiedCardinalDirection,ObtainSimplifiedCardinalDirection> {

		private InputComponentComboBox<SimplifiedCardinalDirection> inputComponent;
		
		public KeyboardSelection(String labelString, SimplifiedCardinalDirection defaultValue) {
			super("Choose", ObtainSimplifiedCardinalDirection.this);
			/*panel = new JPanel(new FlowLayout());
			label = new JLabel(labelString);
			tf = new JTextField();*/
			inputComponent = new InputComponentComboBox<SimplifiedCardinalDirection>(KeyboardSelection.this,defaultValue) {

				@Override
				public void dataEntered(SimplifiedCardinalDirection data)
				{
					setInputData(data);
					done();
				}

			};
		}		

		@Override
		public InputComponent getInputComponent()
		{
			return inputComponent;
		}

	}

	@Override
	public boolean endOnRightClick()
	{
		return endOnRightClick;
	}
	

}