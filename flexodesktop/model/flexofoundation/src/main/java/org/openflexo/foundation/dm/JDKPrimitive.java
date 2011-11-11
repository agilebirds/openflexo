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
package org.openflexo.foundation.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a Java primitive
 * 
 * @author sguerin
 * 
 */
public class JDKPrimitive extends LoadableDMEntity {

	private static final Logger logger = Logger.getLogger(JDKPrimitive.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	private Class type;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public JDKPrimitive(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public JDKPrimitive(DMModel dmModel) {
		super(dmModel);
	}

	/**
	 * Constructor used for dynamic creation
	 */
	public JDKPrimitive(DMRepository repository, Class aClass) {
		this(repository.getDMModel());
		setRepository(repository);
		type = aClass;
		if (!type.isPrimitive()) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Supplied class " + type + " is NOT a primitive !");
		}
		initializeFromPrimitive();
		repository.registerEntity(this);
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	@Override
	public String getFullyQualifiedName() {
		return entityClassName;
	}

	/**
	 * Update from data in ClassLoader
	 */
	private void initializeFromPrimitive() {
		entityPackageName = null;
		name = type.getName();
		entityClassName = name;

		if (logger.isLoggable(Level.FINE))
			logger.fine("Registering " + getFullyQualifiedName());

		setParentType(null, true);

	}

	@Override
	public Class retrieveJavaType() {
		if (entityClassName.equals("int"))
			type = Integer.TYPE;
		if (entityClassName.equals("long"))
			type = Long.TYPE;
		if (entityClassName.equals("short"))
			type = Short.TYPE;
		if (entityClassName.equals("float"))
			type = Float.TYPE;
		if (entityClassName.equals("double"))
			type = Double.TYPE;
		if (entityClassName.equals("char"))
			type = Character.TYPE;
		if (entityClassName.equals("byte"))
			type = Byte.TYPE;
		if (entityClassName.equals("boolean"))
			type = Boolean.TYPE;
		if (entityClassName.equals("void"))
			type = Void.TYPE;
		return type;
	}

	@Override
	public boolean isAncestorOf(DMEntity entity) {
		if (entity == null)
			return false;
		if (entity.getPackage().getName().equals("java.lang")) {
			if (entityClassName.equals("boolean") && entity.getName().equals("Boolean"))
				return true;
			if (entityClassName.equals("int") && entity.getName().equals("Integer"))
				return true;
			if (entityClassName.equals("long") && entity.getName().equals("Long"))
				return true;
			if (entityClassName.equals("short") && entity.getName().equals("Short"))
				return true;
			if (entityClassName.equals("float") && entity.getName().equals("Float"))
				return true;
			if (entityClassName.equals("double") && entity.getName().equals("Double"))
				return true;
			if (entityClassName.equals("char") && entity.getName().equals("Character"))
				return true;
			if (entityClassName.equals("byte") && entity.getName().equals("Byte"))
				return true;
		}
		return super.isAncestorOf(entity);
	}

}
