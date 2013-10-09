/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.fge;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.model.annotations.PropertyIdentifier;

/**
 * A GRParameter encodes a typed property access associated to a class of FGE model
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class GRParameter<T> {

	private static final Logger logger = Logger.getLogger(GRParameter.class.getPackage().getName());

	public static void main(String[] args) {
		System.out.println("Hop: " + getGRParameter(GraphicalRepresentation.class, GraphicalRepresentation.IDENTIFIER_KEY));
	}

	private static Map<String, GRParameter<?>> retrieveParameters(Class<?> ownerClass) {
		Map<String, GRParameter<?>> returned = new HashMap<String, GRParameter<?>>();
		for (Field f : ownerClass.getFields()) {
			PropertyIdentifier parameter = f.getAnnotation(PropertyIdentifier.class);
			if (parameter != null) {
				GRParameter p = new GRParameter(f, parameter);
				// System.out.println("Found " + p);
				returned.put(p.getName(), p);
			}
		}
		return returned;
	}

	private static Map<Class<?>, Map<String, GRParameter<?>>> cachedParameters = new HashMap<Class<?>, Map<String, GRParameter<?>>>();

	public static <T> GRParameter<T> getGRParameter(Class<?> declaringClass, String name, Class<T> type) {
		return (GRParameter<T>) getGRParameter(declaringClass, name);
	}

	public static GRParameter<?> getGRParameter(Class<?> declaringClass, String name) {
		Map<String, GRParameter<?>> cacheForClass = cachedParameters.get(declaringClass);
		if (cacheForClass == null) {
			cacheForClass = retrieveParameters(declaringClass);
			cachedParameters.put(declaringClass, cacheForClass);
		}
		GRParameter<?> returned = cacheForClass.get(name);
		if (returned == null && declaringClass.getSuperclass() != null) {
			return getGRParameter(declaringClass.getSuperclass(), name);
		}
		if (returned == null) {
			logger.warning("Not found GRParameter " + name + " for " + declaringClass);
		}
		return returned;
	}

	public static Collection<GRParameter<?>> getGRParameters(Class<?> declaringClass) {
		Map<String, GRParameter<?>> cacheForClass = cachedParameters.get(declaringClass);
		if (cacheForClass == null) {
			cacheForClass = retrieveParameters(declaringClass);
			cachedParameters.put(declaringClass, cacheForClass);
		}
		Collection<GRParameter<?>> returned = new ArrayList<GRParameter<?>>();
		returned.addAll(cacheForClass.values());
		if (declaringClass.getSuperclass() != null) {
			returned.addAll(getGRParameters(declaringClass.getSuperclass()));
		}
		return returned;
	}

	private Field field;
	private String name;
	private Class<T> type;

	private GRParameter(Field field, PropertyIdentifier p) {
		this.field = field;
		try {
			name = (String) field.get(field.getDeclaringClass());
		} catch (IllegalArgumentException e1) {
			name = field.getName();
		} catch (IllegalAccessException e1) {
			name = field.getName();
		}
		type = (Class<T>) p.type();
	}

	public String getFieldName() {
		return field.getName();
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	public Class<?> getDeclaringClass() {
		return field.getDeclaringClass();
	}

	@Override
	public String toString() {
		return "GRParameter: " + getFieldName() + " " + getName() + " " + getType().getSimpleName();
	}
}
