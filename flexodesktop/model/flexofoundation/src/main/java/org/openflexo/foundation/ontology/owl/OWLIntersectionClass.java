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

import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;

import com.hp.hpl.jena.ontology.IntersectionClass;

public class OWLIntersectionClass extends OWLOperatorClass {

	private static final Logger logger = Logger.getLogger(OWLIntersectionClass.class.getPackage().getName());

	private final IntersectionClass ontClass;

	protected OWLIntersectionClass(IntersectionClass anOntClass, OWLOntology ontology) {
		super(anOntClass, ontology);
		this.ontClass = anOntClass;
		init();
	}

	@Override
	public boolean isSuperClassOf(OntologyClass aClass) {
		boolean superClassOf = super.isSuperClassOf(aClass);
		if (superClassOf) {
			return true;
		}
		for (OWLClass c : getOperands()) {
			if (!c.isSuperClassOf(aClass)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsOntologyObject(OntologyObject o, boolean inherited) {
		boolean contains = super.containsOntologyObject(o, inherited);
		if (contains) {
			return true;
		}
		for (OWLClass c : getOperands()) {
			if (!c.containsOntologyObject(o, inherited)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getClassNameKey() {
		return "ontology_intersection_class";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OntologyIntersectionClass:" + ontClass.getURI();
	}

	@Override
	public IntersectionClass getOntResource() {
		return ontClass;
	}

	@Override
	public String getDisplayableDescription() {
		return "Intersection" + getOperandListDisplayableDescription();
	}

}
