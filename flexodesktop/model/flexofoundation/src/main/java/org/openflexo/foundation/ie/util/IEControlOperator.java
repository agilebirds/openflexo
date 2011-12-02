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
package org.openflexo.foundation.ie.util;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * @author gpolet
 * 
 */
public abstract class IEControlOperator extends FlexoObject implements StringConvertable, ChoiceList {

	private static final Logger logger = Logger.getLogger(IEControlOperator.class.getPackage().getName());

	public static final IEControlOperator SMALLER = new SmallerOperator();

	public static final IEControlOperator GREATER = new GreaterOperator();

	public static final IEControlOperator GREATER_OR_EQUAL = new GreaterOrEqualOperator();

	public static final IEControlOperator SMALLER_OR_EQUAL = new SmallerOrEqualOperator();

	public static final IEControlOperator EQUAL = new EqualOperator();

	public static final IEControlOperator DIFFERENT = new DifferentOperator();

	public static final IEControlOperator CASEINSENSITIVELIKE = new CaseInsensitiveLikeOperator();

	public static final IEControlOperator LIKE = new LikeOperator();

	private static final Vector<IEControlOperator> availableValues;

	static {
		availableValues = new Vector<IEControlOperator>();
		availableValues.add(SMALLER);
		availableValues.add(SMALLER_OR_EQUAL);
		availableValues.add(GREATER);
		availableValues.add(GREATER_OR_EQUAL);
		availableValues.add(EQUAL);
		availableValues.add(DIFFERENT);
		availableValues.add(CASEINSENSITIVELIKE);
		availableValues.add(LIKE);
	}

	public static final StringEncoder.Converter<IEControlOperator> controlOperatorConverter = new Converter<IEControlOperator>(
			IEControlOperator.class) {

		@Override
		public IEControlOperator convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(IEControlOperator value) {
			return value.getName();
		}

	};

	public static IEControlOperator get(String name) {
		if (name == null) {
			return null;
		}
		Enumeration en = availableValues.elements();
		while (en.hasMoreElements()) {
			IEControlOperator op = (IEControlOperator) en.nextElement();
			if (name.equals(op.getName())) {
				return op;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find an IEControlOperator named: " + name);
		}
		return null;
	}

	public String getName() {
		return getSign();
	}

	public abstract String getSign();

	@Override
	public String toString() {
		return getSign();
	}

	public static class SmallerOperator extends IEControlOperator {
		protected SmallerOperator() {
		}

		@Override
		public String getName() {
			return "Smaller";
		}

		@Override
		public String getSign() {
			return "<";
		}

	}

	public static class SmallerOrEqualOperator extends IEControlOperator {
		protected SmallerOrEqualOperator() {
		}

		@Override
		public String getName() {
			return "Smaller Or Equal";
		}

		@Override
		public String getSign() {
			return "<=";
		}

	}

	public static class GreaterOperator extends IEControlOperator {
		protected GreaterOperator() {
		}

		@Override
		public String getName() {
			return "Greater";
		}

		@Override
		public String getSign() {
			return ">";
		}

	}

	public static class GreaterOrEqualOperator extends IEControlOperator {
		protected GreaterOrEqualOperator() {
		}

		@Override
		public String getName() {
			return "Greater Or Equal";
		}

		@Override
		public String getSign() {
			return ">=";
		}

	}

	public static class EqualOperator extends IEControlOperator {
		protected EqualOperator() {
		}

		@Override
		public String getName() {
			return "Equal";
		}

		@Override
		public String getSign() {
			return "=";
		}

	}

	public static class DifferentOperator extends IEControlOperator {
		protected DifferentOperator() {
		}

		@Override
		public String getName() {
			return "Different";
		}

		@Override
		public String getSign() {
			return "!=";
		}

	}

	public static class CaseInsensitiveLikeOperator extends IEControlOperator {
		protected CaseInsensitiveLikeOperator() {
		}

		@Override
		public String getName() {
			return "caseinsensitivelike";
		}

		@Override
		public String getSign() {
			return "caseinsensitivelike";
		}

	}

	public static class LikeOperator extends IEControlOperator {
		protected LikeOperator() {
		}

		@Override
		public String getName() {
			return "like";
		}

		@Override
		public String getSign() {
			return "like";
		}

	}

	/**
	 * Overrides getConverter
	 * 
	 * @see org.openflexo.xmlcode.StringConvertable#getConverter()
	 */
	@Override
	public Converter getConverter() {
		return controlOperatorConverter;
	}

	/**
	 * Overrides getAvailableValues
	 * 
	 * @see org.openflexo.kvc.ChoiceList#getAvailableValues()
	 */
	@Override
	public Vector getAvailableValues() {
		return availableValues;
	}

	public static Vector availableValues() {
		return SMALLER.getAvailableValues();
	}

}
