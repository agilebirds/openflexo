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

public class FIBCheckBox extends FIBWidget {

	private boolean negate = false;
	private boolean selected = false;

	public static enum Parameters implements FIBModelAttribute {
		negate, selected
	}

	public FIBCheckBox() {
	}

	@Override
	public Type getDefaultDataClass() {
		return Boolean.class;
	}

	public boolean getNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.negate, negate);
		if (notification != null) {
			this.negate = negate;
			hasChanged(notification);
		}
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.selected, selected);
		if (notification != null) {
			this.selected = selected;
			hasChanged(notification);
		}
	}
}
