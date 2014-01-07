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
package org.openflexo.fib.model;

import java.util.logging.Logger;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBDropDown.FIBDropDownImpl.class)
@XMLElement(xmlTag = "DropDown")
public interface FIBDropDown extends FIBMultipleValues {

	@PropertyIdentifier(type = boolean.class)
	public static final String SHOW_RESET_KEY = "showReset";

	@Getter(value = SHOW_RESET_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getShowReset();

	@Setter(SHOW_RESET_KEY)
	public void setShowReset(boolean showReset);

	public static abstract class FIBDropDownImpl extends FIBMultipleValuesImpl implements FIBDropDown {

		public boolean showReset = false;

		private static final Logger logger = Logger.getLogger(FIBDropDown.class.getPackage().getName());

		public FIBDropDownImpl() {
		}

		@Override
		public String getBaseName() {
			return "DropDown";
		}

	}
}
