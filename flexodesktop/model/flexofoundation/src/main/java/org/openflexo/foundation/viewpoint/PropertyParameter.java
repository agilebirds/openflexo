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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;

public class PropertyParameter extends EditionSchemeParameter {

	private String domainURI;
	private String parentPropertyURI;

	private ViewPointDataBinding domainValue;

	private BindingDefinition DOMAIN_VALUE = new BindingDefinition("domainValue", IFlexoOntologyClass.class, BindingDefinitionType.GET,
			false);

	public PropertyParameter(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public WidgetType getWidget() {
		return WidgetType.PROPERTY;
	}

	@Override
	public Type getType() {
		return IFlexoOntologyStructuralProperty.class;
	};

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public IFlexoOntologyClass getDomain() {
		return getViewPoint().getOntologyClass(_getDomainURI());
	}

	public void setDomain(IFlexoOntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

	public BindingDefinition getDomainValueBindingDefinition() {
		return DOMAIN_VALUE;
	}

	public ViewPointDataBinding getDomainValue() {
		if (domainValue == null) {
			domainValue = new ViewPointDataBinding(this, ParameterBindingAttribute.domainValue, getDomainValueBindingDefinition());
		}
		return domainValue;
	}

	public void setDomainValue(ViewPointDataBinding domainValue) {
		if (domainValue != null) {
			domainValue.setOwner(this);
			domainValue.setBindingAttribute(ParameterBindingAttribute.domainValue);
			domainValue.setBindingDefinition(getDomainValueBindingDefinition());
		}
		this.domainValue = domainValue;
	}

	private boolean isDynamicDomainValueSet = false;

	public boolean getIsDynamicDomainValue() {
		return getDomainValue().isSet() || isDynamicDomainValueSet;
	}

	public void setIsDynamicDomainValue(boolean isDynamic) {
		if (isDynamic) {
			isDynamicDomainValueSet = true;
		} else {
			domainValue = null;
			isDynamicDomainValueSet = false;
		}
	}

	public IFlexoOntologyClass evaluateDomainValue(BindingEvaluationContext parameterRetriever) {
		if (getDomainValue().isValid()) {
			return (IFlexoOntologyClass) getDomainValue().getBindingValue(parameterRetriever);
		}
		return null;
	}

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getParentProperty() {
		return getViewPoint().getOntologyProperty(_getParentPropertyURI());
	}

	public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty) {
		parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
	}

}
