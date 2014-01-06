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
package org.openflexo.foundation.ontology;

import java.util.logging.Logger;

import org.openflexo.foundation.DefaultFlexoObject;

/**
 * This is the default abstract implementation of all objects encoding models or metamodels conform to FlexoOntology layer
 * 
 * @author sylvain
 * 
 */
public abstract class FlexoOntologyObjectImpl extends DefaultFlexoObject {

	private static final Logger logger = Logger.getLogger(FlexoOntologyObjectImpl.class.getPackage().getName());

	public FlexoOntologyObjectImpl() {
		super();
	}

	public IFlexoOntology getOntology() {
		return getFlexoOntology();
	}

	/**
	 * Name of Object.
	 * 
	 * @return
	 */
	public abstract String getName();

	public abstract IFlexoOntology getFlexoOntology();

	public abstract String getDisplayableDescription();

	public final boolean isOntology() {
		return this instanceof IFlexoOntology;
	}

	public final boolean isOntologyClass() {
		return this instanceof IFlexoOntologyClass;
	}

	public final boolean isOntologyIndividual() {
		return this instanceof IFlexoOntologyIndividual;
	}

	public final boolean isOntologyObjectProperty() {
		return this instanceof IFlexoOntologyObjectProperty;
	}

	public final boolean isOntologyDataProperty() {
		return this instanceof IFlexoOntologyDataProperty;
	}

}
