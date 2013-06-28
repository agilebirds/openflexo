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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlot;

public class PropertyParameter extends InnerModelSlotParameter<TypeSafeModelSlot<?, ?>> {

	private String domainURI;
	private String parentPropertyURI;
	private boolean isDynamicDomainValueSet = false;

	private DataBinding<IFlexoOntologyClass> domainValue;

	public PropertyParameter(VirtualModel.VirtualModelBuilder builder) {
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

	public IFlexoOntologyClass evaluateDomainValue(BindingEvaluationContext parameterRetriever) {
		if (getDomainValue().isValid()) {
			try {
				return getDomainValue().getBindingValue(parameterRetriever);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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
		return getVirtualModel().getOntologyProperty(_getParentPropertyURI());
	}

	public void setParentProperty(IFlexoOntologyStructuralProperty ontologyProperty) {
		parentPropertyURI = ontologyProperty != null ? ontologyProperty.getURI() : null;
	}

	@Override
	public TypeSafeModelSlot<?, ?> getModelSlot() {
		TypeSafeModelSlot<?, ?> returned = super.getModelSlot();
		if (returned == null) {
			if (getVirtualModel() != null && getVirtualModel().getModelSlots(TypeSafeModelSlot.class).size() > 0) {
				return getVirtualModel().getModelSlots(TypeSafeModelSlot.class).get(0);
			}
		}
		return returned;
	}

}
