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

import java.lang.reflect.Type;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBCheckBox.FIBCheckBoxImpl.class)
@XMLElement(xmlTag = "CheckBox")
public interface FIBCheckBox extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String NEGATE_KEY = "negate";
	@PropertyIdentifier(type = boolean.class)
	public static final String SELECTED_KEY = "selected";

	@Getter(value = NEGATE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getNegate();

	@Setter(NEGATE_KEY)
	public void setNegate(boolean negate);

	@Getter(value = SELECTED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getSelected();

	@Setter(SELECTED_KEY)
	public void setSelected(boolean selected);

	public static abstract class FIBCheckBoxImpl extends FIBWidgetImpl implements FIBCheckBox {

		private boolean negate = false;
		private boolean selected = false;

		@Override
		public String getBaseName() {
			return "Checkbox";
		}

		@Override
		public Type getDefaultDataClass() {
			return Boolean.class;
		}

		@Override
		public boolean getNegate() {
			return negate;
		}

		@Override
		public void setNegate(boolean negate) {
			FIBPropertyNotification<Boolean> notification = requireChange(NEGATE_KEY, negate);
			if (notification != null) {
				this.negate = negate;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getSelected() {
			return selected;
		}

		@Override
		public void setSelected(boolean selected) {
			FIBPropertyNotification<Boolean> notification = requireChange(SELECTED_KEY, selected);
			if (notification != null) {
				this.selected = selected;
				hasChanged(notification);
			}
		}
	}
}
