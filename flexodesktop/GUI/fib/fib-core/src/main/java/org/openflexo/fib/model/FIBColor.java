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

import java.awt.Color;
import java.lang.reflect.Type;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBColor.FIBColorImpl.class)
@XMLElement(xmlTag = "Color")
public interface FIBColor extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String ALLOWS_NULL_KEY = "allowsNull";

	@Getter(value = ALLOWS_NULL_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getAllowsNull();

	@Setter(ALLOWS_NULL_KEY)
	public void setAllowsNull(boolean allowsNull);

	public static abstract class FIBColorImpl extends FIBWidgetImpl implements FIBColor {

		private boolean allowsNull = false;

		public FIBColorImpl() {
		}

		@Override
		public String getBaseName() {
			return "ColorSelector";
		}

		@Override
		public Type getDefaultDataClass() {
			return Color.class;
		}

		@Override
		public boolean getAllowsNull() {
			return allowsNull;
		}

		@Override
		public void setAllowsNull(boolean allowsNull) {
			FIBPropertyNotification<Boolean> notification = requireChange(ALLOWS_NULL_KEY, allowsNull);
			if (notification != null) {
				this.allowsNull = allowsNull;
				hasChanged(notification);
			}
		}

	}
}
