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

import java.util.Arrays;
import java.util.List;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class Operator implements AbstractToken {

	public static final Operator AND = new Operator("AND", "&&", 2);
	public static final Operator OR = new Operator("OR", "||", 3);
	public static final Operator EQU = new Operator("=", "==", 1);
	public static final Operator NEQ = new Operator("!=", 1);

	public static final List<Operator> KNOWN_OPERATORS = Arrays.asList(AND, OR, EQU, NEQ);

	public static List<Operator> getKnownOperators() {
		return KNOWN_OPERATORS;
	}

	private String _symbol;
	private String _alternativeSymbol;
	private int _priority;

	public Operator(String symbol, String alternativeSymbol, int priority) {
		this(symbol, priority);
		_alternativeSymbol = alternativeSymbol;
	}

	public Operator(String symbol, int priority) {
		_priority = priority;
		_symbol = symbol;
		_alternativeSymbol = null;
	}

	public String getSymbol() {
		return _symbol;
	}

	public String getAlternativeSymbol() {
		return _alternativeSymbol;
	}

	@Override
	public String toString() {
		return _symbol;
	}

	public int getPriority() {
		return _priority;
	}

}
