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
package org.openflexo.foundation.ie.operator;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BooleanStaticBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.util.ListType;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class ConditionalOperator extends IEOperator {

	protected static final Logger logger = FlexoLogger.getLogger(ConditionalOperator.class.getPackage().getName());

	private AbstractBinding _bindingConditional;

	private boolean isNegate;

	public ConditionalOperator(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public ConditionalOperator(IEWOComponent wo, IESequence sequence, FlexoProject project) {
		super(wo, sequence, project);
		isNegate = false;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Conditional";
	}

	@Override
	public String getClassNameKey() {
		return "conditional";
	}

	@Override
	public String getDefaultInspectorName() {
		return "Conditional.inspector";
	}

	public void setBindingConditional(AbstractBinding bindingConditional) {
		AbstractBinding oldBindingConditional = _bindingConditional;
		_bindingConditional = bindingConditional;
		if (_bindingConditional != null) {
			_bindingConditional.setOwner(this);
			_bindingConditional.setBindingDefinition(getBindingConditionalDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification("bindingConditional", oldBindingConditional, bindingConditional));
	}

	public WidgetBindingDefinition getBindingConditionalDefinition() {
		return WidgetBindingDefinition.get(this, "bindingConditional", Boolean.class, BindingDefinitionType.GET, true);
	}

	public AbstractBinding getBindingConditional() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingConditional;
	}

	@Override
	public ListType getListType() {
		return null;
	}

	@Override
	public void setListType(ListType lt) {
	}

	public boolean getIsNegate() {
		return isNegate;
	}

	public void setIsNegate(boolean isNegate) {
		this.isNegate = isNegate;
		setChanged();
		notifyModification("isNegate", null, isNegate);
	}

	public String evaluateConditionalValueString() {
		if (getBindingConditional() != null) {
			if (getBindingConditional() instanceof BooleanStaticBinding) {
				return ((BooleanStaticBinding) getBindingConditional()).getValue() ? "true" : "false";
			} else {
				return getBindingConditional().getCodeStringRepresentation();
			}
		} else {
			return "true";
		}
	}

	public String javaConditionalValueString() {
		if (getBindingConditional() != null) {
			if (getBindingConditional() instanceof BooleanStaticBinding) {
				return ((BooleanStaticBinding) getBindingConditional()).getValue() ? "true" : "false";
			} else {
				return getBindingConditional().getJavaCodeStringRepresentation();
			}
		} else {
			return "true";
		}
	}

	public boolean javaConditionalValueStringIsNotPrimitive() {
		return !"true".equals(javaConditionalValueString()) && !"false".equals(javaConditionalValueString());
	}
}
