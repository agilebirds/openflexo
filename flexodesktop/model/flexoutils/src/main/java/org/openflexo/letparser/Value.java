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
package org.openflexo.letparser;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public abstract class Value extends Token {

	private boolean prefixedBy$ = false;

	public static Value createValue(double value) {
		if (value - (int) value < 0.0001) {
			return new IntValue((int) value);
		} else {
			return new DoubleValue(value);
		}
	}

	public abstract String getStringValue();

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Value) {
			return getStringValue().equals(((Value) obj).getStringValue());
		}
		return super.equals(obj);
	}

	public boolean isPrefixedBy$() {
		return prefixedBy$;
	}

	public void setPrefixedBy$(boolean prefBy$) {
		this.prefixedBy$ = prefBy$;
	}

	public String getPrefix() {
		return prefixedBy$ ? "$" : "";
	}
}
