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
package org.openflexo.antar.expr.parser;

import org.openflexo.toolbox.Duration;

public class DurationValue extends Value {

	private Duration _value;

	public static DurationValue createDurationValue(Duration value) {
		return new DurationValue(value);
	}

	public DurationValue(Duration value) {
		super();
		_value = value;
	}

	public Duration getDurationValue() {
		return _value;
	}

	@Override
	public String getStringValue() {
		return _value.toString();
	}

	@Override
	public String toString() {
		return getPrefix() + "Duration[" + _value + "]";
	}

	public String getSerializationValue() {
		return "$\"" + _value + "\"";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DurationValue) {
			return getDurationValue().equals(((DurationValue) obj).getDurationValue());
		}
		return super.equals(obj);
	}
}
