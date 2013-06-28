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
package org.openflexo.technologyadapter.xsd.model;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.technologyadapter.xml.model.IXMLAttribute;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;

public abstract class XSOntFeatureAssociation extends AbstractXSOntObject implements IFlexoOntologyFeatureAssociation {

	private final XSOntClass domainClass;

	protected XSOntFeatureAssociation(XSOntology ontology, XSOntClass domainClass, XSDTechnologyAdapter adapter) {
		super(ontology, null, null, adapter);
		this.domainClass = domainClass;
	}

	public boolean isAttributeRestriction() {
		return false;
	}


}
