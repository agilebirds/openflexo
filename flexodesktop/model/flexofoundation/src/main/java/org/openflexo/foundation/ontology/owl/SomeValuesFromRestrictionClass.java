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
package org.openflexo.foundation.ontology.owl;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.OntologicDataType;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.SomeValuesFromRestriction;

public class SomeValuesFromRestrictionClass extends OntologyRestrictionClass {

	private static final Logger logger = Logger.getLogger(SomeValuesFromRestrictionClass.class.getPackage().getName());

	private final SomeValuesFromRestriction restriction;
	private OWLClass object;
	private OntologicDataType dataRange;

	protected SomeValuesFromRestrictionClass(SomeValuesFromRestriction aRestriction, OWLOntology ontology) {
		super(aRestriction, ontology);
		this.restriction = aRestriction;
		retrieveRestrictionInformations();
	}

	@Override
	protected void retrieveRestrictionInformations() {
		super.retrieveRestrictionInformations();
		if (restriction.getSomeValuesFrom() != null) {
			if (restriction.getSomeValuesFrom().canAs(OntClass.class)) {
				object = getOntology().retrieveOntologyClass(restriction.getSomeValuesFrom().as(OntClass.class));
			} else {
				dataRange = OntologicDataType.fromURI(restriction.getSomeValuesFrom().getURI());
			}
		}
	}

	@Override
	public String getClassNameKey() {
		return "some_values_from_restriction";
	}

	@Override
	public String getFullyQualifiedName() {
		return "SomeValuesFromRestrictionClass:" + getDisplayableDescription();
	}

	@Override
	public SomeValuesFromRestriction getOntResource() {
		return restriction;
	}

	@Override
	public String getDisplayableDescription() {
		return (getProperty() != null ? getProperty().getName() : "null") + " some " + (object != null ? object.getName() : getDataRange());
	}

	@Override
	public OWLClass getObject() {
		return object;
	}

	@Override
	public OntologicDataType getDataRange() {
		return dataRange;
	}

}
