/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.xsd.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XSDataPropertyValue;
import org.openflexo.technologyadapter.xsd.model.XSObjectPropertyValue;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;

/**
 * @author xtof
 *
 */
public class AttributeObjectPropertyPathElement extends SimplePathElement {

	private XSOntObjectProperty property;

	public AttributeObjectPropertyPathElement(BindingPathElement parent, XSOntObjectProperty property) {
		super(parent, property.getName(), property.getRange());
		this.property = property;
	}

	@Override
	public Type getType() {
		if (property  != null) {
			if (property.getUpperBound() == null || (property.getUpperBound() >= 0 && property.getUpperBound() <= 1)) {
				// Single cardinality
				if (property.getRange() != null) {
					return property.getRange();
				}
				return Object.class;
			} else {
				if (property.getRange() != null) {
					return new ParameterizedTypeImpl(List.class, IFlexoOntologyIndividual.class);
				}
				return new ParameterizedTypeImpl(List.class, Object.class);
			}
		}
		return null;

	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}


	@Override
	public String getTooltipText(Type resultingType) {
		return "DataAttribute " + property.getDisplayableDescription();
	}

	@Override
	public Object getBindingValue(Object target,
			BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException, InvocationTargetTransformException {
		if (property != null){
			XSObjectPropertyValue xsdAnswer = (XSObjectPropertyValue) ((XSOntIndividual) target).getPropertyValue(property);
			// FIXME simple for now but...
			return xsdAnswer.getValues();
		}
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target,
			BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {

		XSOntProperty prop = ((XSOntIndividual) target).getAttributeByName(getPropertyName());
		((XSOntIndividual) target).addToPropertyValue(prop, value);
	}

}
