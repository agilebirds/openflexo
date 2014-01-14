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

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;

/**
 * Represents an inspector entry for an ontology property
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(PropertyInspectorEntry.PropertyInspectorEntryImpl.class)
@XMLElement(xmlTag="Property")
public interface PropertyInspectorEntry extends InspectorEntry{

@PropertyIdentifier(type=String.class)
public static final String PARENT_PROPERTY_URI_KEY = "parentPropertyURI";
@PropertyIdentifier(type=String.class)
public static final String DOMAIN_URI_KEY = "domainURI";
@PropertyIdentifier(type=DataBinding.class)
public static final String DOMAIN_VALUE_KEY = "domainValue";

@Getter(value=PARENT_PROPERTY_URI_KEY)
@XMLAttribute(xmlTag="parentProperty")
public String _getParentPropertyURI();

@Setter(PARENT_PROPERTY_URI_KEY)
public void _setParentPropertyURI(String parentPropertyURI);


@Getter(value=DOMAIN_URI_KEY)
@XMLAttribute(xmlTag="domain")
public String _getDomainURI();

@Setter(DOMAIN_URI_KEY)
public void _setDomainURI(String domainURI);


@Getter(value=DOMAIN_VALUE_KEY)
@XMLAttribute
public DataBinding getDomainValue();

@Setter(DOMAIN_VALUE_KEY)
public void setDomainValue(DataBinding domainValue);


public static abstract  class PropertyInspectorEntryImpl extends InspectorEntryImpl implements PropertyInspectorEntry
{

	private String parentPropertyURI;
	private String domainURI;
	private boolean isDynamicDomainValueSet = false;
	private DataBinding<IFlexoOntologyClass> domainValue;

	public PropertyInspectorEntryImpl() {
		super();
	}

	@Override
	public Class getDefaultDataClass() {
		return IFlexoOntologyStructuralProperty.class;
	}

	@Override
	public String getWidgetName() {
		return "PropertySelector";
	}

	public String _getParentPropertyURI() {
		return parentPropertyURI;
	}

	public void _setParentPropertyURI(String parentPropertyURI) {
		this.parentPropertyURI = parentPropertyURI;
	}

	public IFlexoOntologyStructuralProperty getParentProperty() {
		return getVirtualModel().getOntologyProperty(_getParentPropertyURI());
	}

	public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty) {
		parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
	}

	public String _getDomainURI() {
		return domainURI;
	}

	public void _setDomainURI(String domainURI) {
		this.domainURI = domainURI;
	}

	public IFlexoOntologyClass getDomain() {
		return getVirtualModel().getOntologyClass(_getDomainURI());
	}

	public void setDomain(IFlexoOntologyClass c) {
		_setDomainURI(c != null ? c.getURI() : null);
	}

	public DataBinding<IFlexoOntologyClass> getDomainValue() {
		if (domainValue == null) {
			domainValue = new DataBinding<IFlexoOntologyClass>(this, IFlexoOntologyClass.class, BindingDefinitionType.GET);
			domainValue.setBindingName("domainValue");
		}
		return domainValue;
	}

	public void setDomainValue(DataBinding<IFlexoOntologyClass> domainValue) {
		if (domainValue != null) {
			domainValue.setOwner(this);
			domainValue.setBindingName("domainValue");
			domainValue.setDeclaredType(IFlexoOntologyClass.class);
			domainValue.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.domainValue = domainValue;
	}

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

}
}
