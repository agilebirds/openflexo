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

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

/**
 * Represents an inspector entry for an ontology individual
 * 
 * @author sylvain
 * 
 */
public class IndividualInspectorEntry extends InspectorEntry {

	private String conceptURI;
	private DataBinding<IFlexoOntologyClass> conceptValue;
	private String renderer;

	public IndividualInspectorEntry(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getConcept() != null) {
			return IndividualOfClass.getIndividualOfClass(getConcept());
		}
		return super.getType();
	}

	@Override
	public Class getDefaultDataClass() {
		return IFlexoOntologyIndividual.class;
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

	public IFlexoOntologyClass getConcept() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getOntologyClass(_getConceptURI());
		}
		return null;
	}

	public void setConcept(IFlexoOntologyClass c) {
		_setConceptURI(c != null ? c.getURI() : null);
	}

	public DataBinding<IFlexoOntologyClass> getConceptValue() {
		if (conceptValue == null) {
			conceptValue = new DataBinding<IFlexoOntologyClass>(this, IFlexoOntologyClass.class, BindingDefinitionType.GET);
			conceptValue.setBindingName("conceptValue");
		}
		return conceptValue;
	}

	public void setConceptValue(DataBinding<IFlexoOntologyClass> conceptValue) {
		if (conceptValue != null) {
			conceptValue.setOwner(this);
			conceptValue.setBindingName("conceptValue");
			conceptValue.setDeclaredType(IFlexoOntologyClass.class);
			conceptValue.setBindingDefinitionType(BindingDefinitionType.GET);
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

	public IFlexoOntologyClass evaluateConceptValue(BindingEvaluationContext parameterRetriever) {
		if (getConceptValue().isValid()) {
			try {
				return getConceptValue().getBindingValue(parameterRetriever);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Return renderer for this individual, under the form eg individual.name
	 * 
	 * @return
	 */
	public String getRenderer() {
		return renderer;
	}

	/**
	 * Sets renderer for this individual, under the form eg individual.name
	 * 
	 * @param renderer
	 */
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
}
