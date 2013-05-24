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
package org.openflexo.foundation.param;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.inspector.widget.DenaliWidget;

public class OntologyIndividualParameter extends ParameterDefinition<OntologyIndividual> {

	public OntologyIndividualParameter(String name, String label, OntologyIndividual defaultValue) {
		super(name, label, defaultValue);
		addParameter("className", "org.openflexo.components.widget.OntologyIndividualInspectorWidget");
	}

	public OntologyIndividualParameter(String name, String label, OntologyClass ontologyClass, OntologyIndividual defaultValue) {
		this(name, label, defaultValue);
		if (ontologyClass != null) {
			setOntologyClass(ontologyClass);
		}
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.CUSTOM;
	}

	private OntologyClass _ontologyClass;

	public void setOntologyClass(OntologyClass ontologyClass) {
		System.out.println("Class " + ontologyClass.getURI());
		_ontologyClass = ontologyClass;
		addParameter("ontologyClass", "params." + getName() + ".ontologyClass");
	}

	public FlexoModelObject getOntologyClass() {
		return (FlexoModelObject) _ontologyClass;
	}

}
