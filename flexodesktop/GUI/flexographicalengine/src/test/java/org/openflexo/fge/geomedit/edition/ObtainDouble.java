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

import org.openflexo.fge.geomedit.GeomEditController;
import org.openflexo.xmlcode.StringEncoder;

public class ObtainDouble extends EditionInput<Double> {
	public static int preferredMethodIndex = 0;

	private boolean endOnRightClick = false;

	public ObtainDouble(String anInputLabel, double defaultValue, GeomEditController controller) {
		super(anInputLabel, controller);

		availableMethods.add(new KeyboardSelection(anInputLabel, defaultValue));
	}

	public ObtainDouble(String anInputLabel, double defaultValue, GeomEditController controller, boolean appendEndSelection) {
		this(anInputLabel, defaultValue, controller);
		if (appendEndSelection)
			availableMethods.add(new EndEditionSelection());
		endOnRightClick = appendEndSelection;
	}

	@Override
	protected int getPreferredMethodIndex() {
		return preferredMethodIndex;
	}

	@Override
	public void setActiveMethod(EditionInputMethod aMethod) {
		super.setActiveMethod(aMethod);
		preferredMethodIndex = availableMethods.indexOf(aMethod);
	}

	public class KeyboardSelection extends EditionInputMethod<Double, ObtainDouble> {

		private InputComponentTextField<Double> inputComponent;

		/*private JTextField tf;
		private JLabel label;
		private JPanel panel;*/

		public KeyboardSelection(String labelString, double defaultValue) {
			super("Typing", ObtainDouble.this);
			/*panel = new JPanel(new FlowLayout());
			label = new JLabel(labelString);
			tf = new JTextField();*/
			inputComponent = new InputComponentTextField<Double>(KeyboardSelection.this, defaultValue) {

				@Override
				public int getColumnSize() {
					return 8;
				}

				@Override
				public String convertDataToString(Double data) {
					if (data == null)
						return "";
					StringEncoder.getDefaultInstance();
					return StringEncoder.encodeDouble(data);
				}

				@Override
				public Double convertStringToData(String string) {
					if (string == null || string.trim().equals(""))
						return null;
					StringEncoder.getDefaultInstance();
					return StringEncoder.decodeAsDouble(string);
				}

				@Override
				public void dataEntered(Double data) {
					setInputData(data);
					done();
				}

			};
		}

		@Override
		public InputComponent getInputComponent() {
			return inputComponent;
		}

	}

	@Override
	public boolean endOnRightClick() {
		return endOnRightClick;
	}

}