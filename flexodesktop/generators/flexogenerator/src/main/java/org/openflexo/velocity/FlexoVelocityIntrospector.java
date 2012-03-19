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
package org.openflexo.velocity;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelMethod;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.openflexo.logging.FlexoLogger;

public class FlexoVelocityIntrospector extends UberspectImpl {

	private static final Logger logger = FlexoLogger.getLogger(FlexoVelocityIntrospector.class.getPackage().getName());

	@Override
	public VelMethod getMethod(Object obj, String methodName, Object[] args, Info i) throws Exception {
		if (obj == null) {
			return null;
		}

		// Allow to use the 'magic' methods provided by Velocity on array (ex. get(x))
		if (obj instanceof Object[]) {
			return super.getMethod(obj, methodName, args, i);
		}

		Class objClass = obj.getClass();

		Method m = introspector.getMethod(objClass, methodName, args);
		// if it's an array
		if (m == null) {
			if (objClass == Class.class) {
				m = introspector.getMethod((Class) obj, methodName, args);
				if (m != null) {
					return new VelMethodImpl(m);
				}
			}

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Method '" + methodName + "' could not be found on " + objClass.getName() + " called in " + i.getTemplateName()
						+ " at line " + i.getLine() + " column " + i.getColumn());
			}
			return null;
		}
		// Fix a bug in security manager of JDK1.5 where access to a public method of a non-visible inherited class is refused although it
		// shouldn't (e.g, the method length() on StringBuilder is inherited from AbstractStringBuilder which has a package visibility)
		try {
			m.setAccessible(true);
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Security exception for method: " + objClass + "." + methodName, e);
			}
		}
		return m != null ? new VelMethodImpl(m) : null;
	}

	@Override
	public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
		VelPropertyGet get = super.getPropertyGet(obj, identifier, i);
		if (get == null) {
			Class objClass = obj.getClass();
			try {
				try {
					return new FlexoVelocityPropertyGet(objClass, identifier);
				} catch (NoSuchFieldException e) {
					if (objClass == Class.class) {
						return new FlexoVelocityPropertyGet((Class) obj, identifier);
					}
				}
			} catch (NoSuchFieldException e) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Field '" + identifier + "' could not be found on " + objClass.getName() + " called in "
							+ i.getTemplateName() + " at line " + i.getLine() + " column " + i.getColumn());
				}
			} catch (SecurityException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, "Security exception for field: " + objClass + "." + identifier, e);
				}
			}
		}

		return get;
	}
}
