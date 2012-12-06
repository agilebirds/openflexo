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
package org.openflexo.technologyadapter.owl.model;

import java.util.logging.Level;

import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.W3URIDefinitions;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.rdf.model.Literal;

public class OWLDataType extends AbstractOWLObject implements IFlexoOntologyDataType, W3URIDefinitions {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(OWLDataType.class.getPackage()
			.getName());

	private BuiltInDataType builtInDataType;

	protected OWLDataType(String dataTypeURI, OWLTechnologyAdapter adapter) {
		super(adapter);
		builtInDataType = BuiltInDataType.fromURI(getURI());
		if (builtInDataType == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not map a data type: " + getURI() + ", String will be used instead.");
			}
			builtInDataType = BuiltInDataType.String;
		}
	}

	@Override
	public String getDisplayableDescription() {
		return getURI();
	}

	@Override
	public Class<?> getAccessedType() {
		return getBuiltInDataType().getAccessedType();
	}

	@Override
	public BuiltInDataType getBuiltInDataType() {
		return builtInDataType;
	}

	public Object valueFromLiteral(Literal literal) {
		if (getBuiltInDataType() != null) {
			switch (getBuiltInDataType()) {
			case String:
				return literal.getString();
			case Boolean:
				return literal.getBoolean();
			case Byte:
				return literal.getByte();
			case Int:
				return literal.getInt();
			case Integer:
				return literal.getInt();
			case Short:
				return literal.getShort();
			case Long:
				return literal.getLong();
			case Float:
				return literal.getFloat();
			case Double:
				return literal.getDouble();
			default:
				logger.warning("Unexpected type: " + getBuiltInDataType());
				break;
			}
		}
		return null;
	}

	@Override
	@Deprecated
	public String getClassNameKey() {
		return null;
	}

	@Override
	@Deprecated
	public String getInspectorName() {
		return null;
	}

	@Override
	public OWLOntology getFlexoOntology() {
		return null;
	}

	@Override
	@Deprecated
	public String getFullyQualifiedName() {
		return null;
	}

}
