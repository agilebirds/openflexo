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

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

public class ObjectPropertyParameter extends PropertyParameter {

	private String rangeURI;
	private DataBinding<IFlexoOntologyClass> rangeValue;
	private boolean isDynamicRangeValueSet = false;

	public ObjectPropertyParameter(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		return IFlexoOntologyObjectProperty.class;
	};

	@Override
	public WidgetType getWidget() {
		return WidgetType.OBJECT_PROPERTY;
	}

	public String _getRangeURI() {
		return rangeURI;
	}

	public void _setRangeURI(String rangeURI) {
		this.rangeURI = rangeURI;
	}

	public IFlexoOntologyClass getRange() {
		return getVirtualModel().getOntologyClass(_getRangeURI());
	}

	public void setRange(IFlexoOntologyClass c) {
		_setRangeURI(c != null ? c.getURI() : null);
	}

	public DataBinding<IFlexoOntologyClass> getRangeValue() {
		if (rangeValue == null) {
			rangeValue = new DataBinding<IFlexoOntologyClass>(this, IFlexoOntologyClass.class, BindingDefinitionType.GET);
			rangeValue.setBindingName("rangeValue");
		}
		return rangeValue;
	}

	public void setRangeValue(DataBinding<IFlexoOntologyClass> rangeValue) {
		if (rangeValue != null) {
			rangeValue.setOwner(this);
			rangeValue.setBindingName("rangeValue");
			rangeValue.setDeclaredType(IFlexoOntologyClass.class);
			rangeValue.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.rangeValue = rangeValue;
	}

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

	public IFlexoOntologyClass evaluateRangeValue(BindingEvaluationContext parameterRetriever) {
		if (getRangeValue().isValid()) {
			try {
				return getRangeValue().getBindingValue(parameterRetriever);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
