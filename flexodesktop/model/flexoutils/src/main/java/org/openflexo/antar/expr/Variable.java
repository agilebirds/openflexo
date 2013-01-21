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
package org.openflexo.antar.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a basic access to a variable<br>
 * An instance of Variable is a specialized {@link BindingValue} with a BindingVariable and an empty BindingPath.
 * 
 * @author sylvain
 * 
 */
public class Variable extends BindingValue {

	private static final Logger logger = Logger.getLogger(Variable.class.getPackage().getName());

	public Variable(String variableName) {
		super(makeSingleton(variableName));
	}

	private static List<AbstractBindingPathElement> makeSingleton(String aVariableName) {
		List<AbstractBindingPathElement> returned = new ArrayList<BindingValue.AbstractBindingPathElement>();
		returned.add(new NormalBindingPathElement(aVariableName));
		return returned;
	}

	public String getName() {
		return getVariableName();
	}

	@Override
	public boolean isSimpleVariable() {
		return true;
	}

}
