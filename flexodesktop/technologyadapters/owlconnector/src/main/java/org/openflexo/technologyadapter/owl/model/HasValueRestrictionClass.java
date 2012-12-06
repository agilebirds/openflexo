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

import java.util.logging.Logger;

import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;

import com.hp.hpl.jena.ontology.HasValueRestriction;
import com.hp.hpl.jena.rdf.model.Resource;

public class HasValueRestrictionClass extends OWLRestriction {

	private static final Logger logger = Logger.getLogger(HasValueRestrictionClass.class.getPackage().getName());

	private final HasValueRestriction restriction;
	private OWLConcept<?> object;

	protected HasValueRestrictionClass(HasValueRestriction aRestriction, OWLOntology ontology, OWLTechnologyAdapter adapter) {
		super(aRestriction, ontology, adapter);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	@Override
	protected void retrieveRestrictionInformations() {
		super.retrieveRestrictionInformations();
		if (restriction.getHasValue() != null) {
			if (restriction.getHasValue().canAs(Resource.class)) {
				object = getOntology().retrieveOntologyObject(restriction.getHasValue().as(Resource.class));
			}
		}
	}

	@Override
	public String getClassNameKey() {
		return "has_value_restriction";
	}

	@Override
	public String getFullyQualifiedName() {
		return "HasValueRestrictionClass:" + getDisplayableDescription();
	}

	@Override
	public HasValueRestriction getOntResource() {
		return restriction;
	}

	@Override
	public String getDisplayableDescription() {
		return (getProperty() != null ? getProperty().getName() : "null") + " hasValue "
				+ (object != null ? object.getName() : getDataRange());
	}

	@Override
	public OWLConcept<?> getObject() {
		return object;
	}

	@Override
	public OWLDataType getDataRange() {
		return null;
	}

	@Override
	public Integer getLowerBound() {
		return 1;
	}

	@Override
	public Integer getUpperBound() {
		return null;
	}

}
