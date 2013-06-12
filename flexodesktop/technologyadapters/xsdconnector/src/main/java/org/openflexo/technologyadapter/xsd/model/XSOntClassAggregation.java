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
package org.openflexo.technologyadapter.xsd.model;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;

import com.sun.xml.xsom.XSAttributeUse;

public class XSOntClassAggregation extends XSOntFeatureAssociation{


	private final XSOntObjectProperty objectProperty;
	
	protected XSOntClassAggregation(XSOntology ontology,
			XSOntClass domainClass, XSDTechnologyAdapter adapter) {
		super(ontology, domainClass, adapter);
		objectProperty = null; 
	}


	protected XSOntClassAggregation(XSOntology ontology, XSOntClass domainClass, XSAttributeUse attributeUse,
			XSDTechnologyAdapter adapter) {
		super(ontology,domainClass,adapter);
		String propertyURI = ontology.getFetcher().getUri(attributeUse.getDecl());
		this.objectProperty = ontology.getObjectProperty(propertyURI);
	}

	protected XSOntClassAggregation(XSOntology ontology, XSOntClass domainClass, XSOntObjectProperty property, XSDTechnologyAdapter adapter) {
		super(ontology,domainClass,adapter);
		this.objectProperty = property;
	}

	
	@Override
	public XSOntObjectProperty getProperty() {
		return getAttributeProperty();
	}

	@Override
	public boolean isAttributeRestriction() {
		return false;
	}


	public XSOntObjectProperty getAttributeProperty() {
		return objectProperty;
	}

	public boolean hasDefaultValue() {
		return StringUtils.isNotEmpty(getDefaultValue());
	}

	public String getDefaultValue() {
		return null;
	}

	public boolean hasFixedValue() {
		return StringUtils.isNotEmpty(getFixedValue());
	}

	public String getFixedValue() {
		return null;
	}

	public boolean isRequired() {
		return false;
	}

	@Override
	public String getDisplayableDescription() {
		StringBuffer buffer = new StringBuffer("InnerElement ");
		buffer.append(" (").append(objectProperty.getRange().toString()).append(") is ");
		if (isRequired()) {
			buffer.append("required");
		} else {
			buffer.append("optional");
		}
		if (hasDefaultValue()) {
			buffer.append(", default: '").append(getDefaultValue()).append("'");
		}
		if (hasFixedValue()) {
			buffer.append(", fixed: '").append(getFixedValue()).append("'");
		}
		return buffer.toString();
	}

	@Override
	public XSOntObjectProperty getFeature() {
		return objectProperty;
	}

	@Override
	public IFlexoOntologyObject getRange() {
		if (getFeature() instanceof IFlexoOntologyStructuralProperty) {
			return ((IFlexoOntologyStructuralProperty) getFeature()).getRange();
		}
		return null;
	}

	@Override
	public Integer getLowerBound() {
		if (isRequired())
			return 1;
		else
			return 0;
	}

	@Override
	public Integer getUpperBound() {
		return 1;
	}

}
