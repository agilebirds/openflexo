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
package org.openflexo.technologyadapter.xsd.metamodel;

import java.lang.reflect.Type;
import java.util.logging.Level;

import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataType;
import org.openflexo.foundation.ontology.W3URIDefinitions;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XSOntology;

import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSSimpleType;

public class XSDDataType extends AbstractXSOntObject implements IFlexoOntologyDataType, W3URIDefinitions, Type {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSOntology.class.getPackage()
			.getName());

	private BuiltInDataType builtInDataType;

	protected XSDDataType(XSSimpleType simpleType, XSOntology ontology, XSDTechnologyAdapter adapter) {
		super(ontology, simpleType.getName(), isFromW3(simpleType) ? W3_URI + "#" + simpleType.getName() : W3_URI + "#"
				+ simpleType.getBaseType().getName(), adapter);
		builtInDataType = BuiltInDataType.fromURI(getURI());
		if (builtInDataType == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not map a data type: " + getURI() + ", String will be used instead.");
			}
			builtInDataType = BuiltInDataType.String;
		}
	}

	public static boolean isFromW3(XSDeclaration decl) {
		return decl.getTargetNamespace().equals(W3_NAMESPACE);
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

}
