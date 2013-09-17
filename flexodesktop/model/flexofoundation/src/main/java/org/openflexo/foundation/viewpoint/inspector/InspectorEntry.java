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
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.StringUtils;

/**
 * Represents an inspector entry (a data related to an edition pattern which can be inspected)
 * 
 * @author sylvain
 * 
 */
public abstract class InspectorEntry extends EditionPatternObject implements Bindable {

	static final Logger logger = Logger.getLogger(InspectorEntry.class.getPackage().getName());

	public static enum InspectorEntryBindingAttribute implements InspectorBindingAttribute {
		data, conditional, domainValue, rangeValue, parentClassValue, conceptValue, list, format;
	}

	public static BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);
	private BindingDefinition DATA;

	public BindingDefinition getDataBindingDefinition() {
		if (DATA == null) {
			DATA = new BindingDefinition("data", getType(), BindingDefinitionType.GET_SET, true) {
				@Override
				public BindingDefinitionType getBindingDefinitionType() {
					if (getIsReadOnly()) {
						return BindingDefinitionType.GET;
					} else {
						return BindingDefinitionType.GET_SET;
					}
				}

				@Override
				public Type getType() {
					return InspectorEntry.this.getType();
				}
			};
		}
		return DATA;
	}

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	private EditionPatternInspector inspector;
	private String name;
	private String label;
	private boolean readOnly;

	private ViewPointDataBinding data;
	private ViewPointDataBinding conditional;

	public InspectorEntry(ViewPointBuilder builder) {
		super(builder);
	}

	public Type getType() {
		return getDefaultDataClass();
	}

	public abstract Class getDefaultDataClass();

	public abstract String getWidgetName();

	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getViewPoint();
		}
		return null;
	}

	@Override
	public EditionPattern getEditionPattern() {
		if (getInspector() != null) {
			return getInspector().getEditionPattern();
		}
		return null;
	}

	public EditionPatternInspector getInspector() {
		return inspector;
	}

	public void setInspector(EditionPatternInspector inspector) {
		this.inspector = inspector;
	}

	public String getLabel() {
		if (label == null || StringUtils.isEmpty(label)) {
			return getName();
		}
		return label;
	}

	public void setLabel(String label) {
		if (label != null && label.equals(name)) {
			return;
		}
		this.label = label;
	}

	public boolean isSingleEntry() {
		return true;
	}

	public int getIndex() {
		return getInspector().getEntries().indexOf(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsReadOnly() {
		return readOnly;
	}

	public void setIsReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public ViewPointDataBinding getData() {
		if (data == null) {
			data = new ViewPointDataBinding(this, InspectorEntryBindingAttribute.data, getDataBindingDefinition());
		}
		return data;
	}

	public void setData(ViewPointDataBinding data) {
		if (data != null) {
			data.setOwner(this);
			data.setBindingAttribute(InspectorEntryBindingAttribute.data);
			data.setBindingDefinition(getDataBindingDefinition());
		}
		this.data = data;
		notifyBindingChanged(this.data);
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, InspectorEntryBindingAttribute.conditional, CONDITIONAL);
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(InspectorEntryBindingAttribute.conditional);
		conditional.setBindingDefinition(CONDITIONAL);
		this.conditional = conditional;
	}

	@Override
	public BindingModel getBindingModel() {
		return getInspector().getBindingModel();
	}

	public static class DataBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<InspectorEntry> {
		public DataBindingIsRequiredAndMustBeValid() {
			super("'data'_binding_is_not_valid", InspectorEntry.class);
		}

		@Override
		public ViewPointDataBinding getBinding(InspectorEntry object) {
			return object.getData();
		}

		@Override
		public BindingDefinition getBindingDefinition(InspectorEntry object) {
			return object.getDataBindingDefinition();
		}

	}

}
