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

public class FIBTextField extends FIBWidget {

	// TODO: handle font
	public boolean validateOnReturn = false;
	public Integer columns;
	public boolean passwd = false;
	public String text = null;

	public FIBTextField() {
	}

	@Override
	protected String getBaseName() {
		return "TextField";
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

}
