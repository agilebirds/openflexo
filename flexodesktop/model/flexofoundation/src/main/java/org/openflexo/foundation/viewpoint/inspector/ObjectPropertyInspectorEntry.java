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
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

/**
 * Represents an inspector entry for an ontology object property
 * 
 * @author sylvain
 * 
 */
public class ObjectPropertyInspectorEntry extends PropertyInspectorEntry {

	private String rangeURI;

	private ViewPointDataBinding rangeValue;

	private BindingDefinition RANGE_VALUE = new BindingDefinition("rangeValue", OntologyClass.class, BindingDefinitionType.GET, false);

	public ObjectPropertyInspectorEntry(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class getDefaultDataClass() {
		return OntologyObjectProperty.class;
	}

	@Override
	public String getWidgetName() {
		return "ObjectPropertySelector";
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String domainURI) {
		this.rangeURI = domainURI;
	}

	public OntologyClass getRange() {
		getViewPoint().loadWhenUnloaded();
		return getViewPoint().getOntologyClass(_getRangeURI());
	}

	public void setRange(OntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

	public BindingDefinition getRangeValueBindingDefinition() {
		return RANGE_VALUE;
	}

	public ViewPointDataBinding getRangeValue() {
		if (rangeValue == null) {
			rangeValue = new ViewPointDataBinding(this, InspectorEntryBindingAttribute.rangeValue, getRangeValueBindingDefinition());
		}
		return rangeValue;
	}

	public void setRangeValue(ViewPointDataBinding rangeValue) {
		rangeValue.setOwner(this);
		rangeValue.setBindingAttribute(InspectorEntryBindingAttribute.rangeValue);
		rangeValue.setBindingDefinition(getRangeValueBindingDefinition());
		this.rangeValue = rangeValue;
	}

	private boolean isDynamicRangeValueSet = false;

	public boolean getIsDynamicRangeValue() {
		return getRangeValue().isSet() || isDynamicRangeValueSet;
	}

	public void setIsDynamicRangeValue(boolean isDynamic) {
		if (isDynamic) {
			isDynamicRangeValueSet = true;
		} else {
			rangeValue = null;
			isDynamicRangeValueSet = false;
		}
	}

	public OntologyClass evaluateRangeValue(BindingEvaluationContext parameterRetriever) {
		if (getRangeValue().isValid()) {
			return (OntologyClass) getRangeValue().getBindingValue(parameterRetriever);
		}
		return null;
	}

}
