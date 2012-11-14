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

package org.openflexo.xmlcode;

import java.lang.reflect.Method;

/**
 * <p>
 * <code>AccessorMethod</code> is a class representing a KeyValueProperty accessor method.
 * </p>
 * <p>
 * Because many differents accessors could be defined in a class, all implementing different class-specific levels (more or less
 * specialized, regarding parameters classes), we store these <code>AccessorMethods</code> in a particular order depending on the parameters
 * specialization. This order is implemented in this class through {@link Comparable} interface implementation. Note: this class has a
 * natural ordering that is inconsistent with equals, which means that <code>(x.compareTo(y)==0) == (x.equals(y))</code> condition is
 * violated.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see KeyValueProperty
 */
public class AccessorMethod implements Comparable {

	/** Stores the related <code>KeyValueProperty</code> */
	protected KeyValueProperty keyValueProperty;

	/** Stores the related <code>Method</code> */
	protected Method method;

	/**
	 * Creates a new <code>AccessorMethod</code> instance.
	 * 
	 * @param aKeyValueProperty
	 *            a <code>KeyValueProperty</code> value
	 * @param aMethod
	 *            a <code>Method</code> value
	 */
	public AccessorMethod(KeyValueProperty aKeyValueProperty, Method aMethod) {

		super();
		keyValueProperty = aKeyValueProperty;
		method = aMethod;
	}

	/**
	 * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 * 
	 * @param object
	 *            an <code>Object</code> value
	 * @return an <code>int</code> value
	 * @exception ClassCastException
	 *                if an error occurs
	 */
	@Override
	public int compareTo(Object object) throws ClassCastException {

		if (object instanceof AccessorMethod) {

			AccessorMethod comparedAccessorMethod = (AccessorMethod) object;

			if (getMethod().getParameterTypes().length != comparedAccessorMethod.getMethod().getParameterTypes().length) {

				// Those objects could not be compared and should be treated as
				// equals
				// regarding the specialization of their parameters
				return 2;
			}

			else {

				for (int i = 0; i < getMethod().getParameterTypes().length; i++) {

					Class localParameterType = getMethod().getParameterTypes()[i];
					Class comparedParameterType = comparedAccessorMethod.getMethod().getParameterTypes()[i];

					if (!localParameterType.equals(comparedParameterType)) {

						boolean localParamIsParentOfComparedParam = localParameterType.isAssignableFrom(comparedParameterType);

						boolean localParamIsChildOfComparedParam = comparedParameterType.isAssignableFrom(localParameterType);

						if (localParamIsParentOfComparedParam) {
							return 1;
						}
						if (localParamIsChildOfComparedParam) {
							return -1;
						}
						// Those objects could not be compared
						return 2;
					}

				} // end of for

				// Those objects are equals regarding the specialization of
				// their parameters
				return 0;
			}

		}

		else {
			throw new ClassCastException();
		}
	}

	/**
	 * Return the related <code>Method</code>
	 * 
	 * @return
	 */
	public Method getMethod() {

		return method;
	}

}
