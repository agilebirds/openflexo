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

import com.hp.hpl.jena.ontology.ComplementClass;

public class OWLComplementClass extends OWLOperatorClass {

	private static final Logger logger = Logger.getLogger(OWLComplementClass.class.getPackage().getName());

	private final ComplementClass ontClass;

	protected OWLComplementClass(ComplementClass anOntClass, OWLOntology ontology) {
		super(anOntClass, ontology);
		this.ontClass = anOntClass;
		init();
	}

	@Override
	public String getClassNameKey() {
		return "ontology_complement_class";
	}

	@Override
	public String getFullyQualifiedName() {
		return "OntologyComplementClass:" + ontClass.getURI();
	}

	@Override
	public ComplementClass getOntResource() {
		return ontClass;
	}

	@Override
	public String getDisplayableDescription() {
		return "Complement" + getOperandListDisplayableDescription();
	}

}
