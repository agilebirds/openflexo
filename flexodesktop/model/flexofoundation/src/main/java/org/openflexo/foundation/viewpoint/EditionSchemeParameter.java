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
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.view.action.DropSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;
import org.openflexo.toolbox.StringUtils;

public abstract class EditionSchemeParameter extends ViewPointObject implements InspectorBindingAttribute {

	private static final Logger logger = Logger.getLogger(EditionSchemeParameter.class.getPackage().getName());

	public static enum WidgetType {
		URI,
		TEXT_FIELD,
		LOCALIZED_TEXT_FIELD,
		TEXT_AREA,
		INTEGER,
		FLOAT,
		CHECKBOX,
		DROPDOWN,
		INDIVIDUAL,
		CLASS,
		OBJECT_PROPERTY,
		DATA_PROPERTY,
		FLEXO_OBJECT;
	}

	private String name;
	private String label;
	private String description;
	private boolean usePaletteLabelAsDefaultValue;

	private EditionScheme _scheme;

	private ViewPointDataBinding conditional;
	private ViewPointDataBinding defaultValue;

	public static enum ParameterBindingAttribute implements InspectorBindingAttribute {
		conditional, baseURI, defaultValue, domainValue, rangeValue, parentClassValue, conceptValue
	}

	public EditionSchemeParameter() {
	}

	public abstract Type getType();

	public abstract WidgetType getWidget();

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);
	private BindingDefinition DEFAULT_VALUE = new BindingDefinition("defaultValue", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return EditionSchemeParameter.this.getType();
		};
	};

	public BindingDefinition getConditionalBindingDefinition() {
		return CONDITIONAL;
	}

	public BindingDefinition getDefaultValueBindingDefinition() {
		return DEFAULT_VALUE;
	}

	public void setScheme(EditionScheme scheme) {
		_scheme = scheme;
	}

	public EditionScheme getScheme() {
		return _scheme;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getScheme().getViewPoint();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		if (label == null || StringUtils.isEmpty(label)) {
			return getName();
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.EDITION_PATTERN_PARAMETER_INSPECTOR;
	}

	public boolean getUsePaletteLabelAsDefaultValue() {
		return usePaletteLabelAsDefaultValue;
	}

	public void setUsePaletteLabelAsDefaultValue(boolean usePaletteLabelAsDefaultValue) {
		this.usePaletteLabelAsDefaultValue = usePaletteLabelAsDefaultValue;
	}

	public boolean evaluateCondition(BindingEvaluationContext parameterRetriever) {
		if (getConditional().isValid()) {
			return (Boolean) getConditional().getBindingValue(parameterRetriever);
		}
		return true;
	}

	@Override
	public String toString() {
		return "EditionPatternParameter: " + getName();
	}

	public int getIndex() {
		return getScheme().getParameters().indexOf(this);
	}

	public ViewPointDataBinding getConditional() {
		if (conditional == null) {
			conditional = new ViewPointDataBinding(this, ParameterBindingAttribute.conditional, getConditionalBindingDefinition());
		}
		return conditional;
	}

	public void setConditional(ViewPointDataBinding conditional) {
		conditional.setOwner(this);
		conditional.setBindingAttribute(ParameterBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}

	public EditionPattern getEditionPattern() {
		return getScheme().getEditionPattern();
	}

	@Override
	public BindingModel getBindingModel() {
		return getScheme().getBindingModel();
	}

	public ViewPointDataBinding getDefaultValue() {
		if (defaultValue == null) {
			defaultValue = new ViewPointDataBinding(this, ParameterBindingAttribute.defaultValue, getDefaultValueBindingDefinition());
		}
		return defaultValue;
	}

	public void setDefaultValue(ViewPointDataBinding defaultValue) {
		defaultValue.setOwner(this);
		defaultValue.setBindingAttribute(ParameterBindingAttribute.defaultValue);
		defaultValue.setBindingDefinition(getDefaultValueBindingDefinition());
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue(EditionSchemeAction<?> action) {
		ViewPointPaletteElement paletteElement = (action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement()
				: null);

		// System.out.println("Default value for "+element.getName()+" ???");
		if (getUsePaletteLabelAsDefaultValue() && (paletteElement != null)) {
			return paletteElement.getName();
		}
		if (getDefaultValue().isValid()) {
			return getDefaultValue().getBindingValue(action);
		}
		return null;
	}

	public boolean isMandatory() {
		return false;
	}

	public boolean isValid(EditionSchemeAction action, Object value) {
		return !isMandatory() || value != null;
	}

}
