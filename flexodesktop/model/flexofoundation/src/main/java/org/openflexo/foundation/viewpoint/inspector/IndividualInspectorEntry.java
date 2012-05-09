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
package org.openflexo.foundation.viewpoint.inspector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

/**
 * Represents an inspector entry for an ontology individual
 * 
 * @author sylvain
 * 
 */
public class IndividualInspectorEntry extends InspectorEntry {

	private String conceptURI;

	private ViewPointDataBinding conceptValue;

	private BindingDefinition CONCEPT_VALUE = new BindingDefinition("conceptValue", OntologyClass.class, BindingDefinitionType.GET, false);

	@Override
	public Class getDefaultDataClass() {
		return OntologyIndividual.class;
	}

	@Override
	public String getWidgetName() {
		return "OntologyIndividualSelector";
	}

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
	}

	public OntologyClass getConcept() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		return getOntologyLibrary().getClass(_getConceptURI());
	}

	public void setConcept(OntologyClass c) {
		_setConceptURI(c != null ? c.getURI() : null);
	}

	public BindingDefinition getConceptValueBindingDefinition() {
		return CONCEPT_VALUE;
	}

	public ViewPointDataBinding getConceptValue() {
		if (conceptValue == null) {
			conceptValue = new ViewPointDataBinding(this, InspectorEntryBindingAttribute.conceptValue, getConceptValueBindingDefinition());
		}
		return conceptValue;
	}

	public void setConceptValue(ViewPointDataBinding conceptValue) {
		if (conceptValue != null) {
			conceptValue.setOwner(this);
			conceptValue.setBindingAttribute(InspectorEntryBindingAttribute.conceptValue);
			conceptValue.setBindingDefinition(getConceptValueBindingDefinition());
		}
		this.conceptValue = conceptValue;
	}

	private boolean isDynamicConceptValueSet = false;

	public boolean getIsDynamicConceptValue() {
		return getConceptValue().isSet() || isDynamicConceptValueSet;
	}

	public void setIsDynamicConceptValue(boolean isDynamic) {
		if (isDynamic) {
			isDynamicConceptValueSet = true;
		} else {
			conceptValue = null;
			isDynamicConceptValueSet = false;
		}
	}

	public OntologyClass evaluateConceptValue(BindingEvaluationContext parameterRetriever) {
		if (getConceptValue().isValid()) {
			return (OntologyClass) getConceptValue().getBindingValue(parameterRetriever);
		}
		return null;
	}

}
