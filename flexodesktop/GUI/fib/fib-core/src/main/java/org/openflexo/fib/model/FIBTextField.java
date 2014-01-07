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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTextField.FIBTextFieldImpl.class)
@XMLElement(xmlTag = "TextField")
public interface FIBTextField extends FIBTextWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String PASSWD_KEY = "passwd";

	@Getter(value = PASSWD_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isPasswd();

	@Setter(PASSWD_KEY)
	public void setPasswd(boolean passwd);

	public static abstract class FIBTextFieldImpl extends FIBTextWidgetImpl implements FIBTextField {

		private boolean passwd = false;

		public FIBTextFieldImpl() {
		}

		@Override
		public String getBaseName() {
			return "TextField";
		}

		@Override
		public boolean isPasswd() {
			return passwd;
		}

		@Override
		public void setPasswd(boolean passwd) {
			this.passwd = passwd;
		}

	}
}
