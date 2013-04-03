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
import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.Function.FunctionArgument;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.toolbox.StringUtils;

public abstract class EditionSchemeParameter extends EditionSchemeObject implements FunctionArgument {

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
		PROPERTY,
		OBJECT_PROPERTY,
		DATA_PROPERTY,
		FLEXO_OBJECT,
		LIST,
		EDITION_PATTERN;
	}

	private String label;
	private boolean usePaletteLabelAsDefaultValue;

	private EditionScheme _scheme;

	private DataBinding<Boolean> conditional;
	private DataBinding<Object> defaultValue;

	public EditionSchemeParameter(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getURI() {
		return getEditionScheme().getURI() + "." + getName();
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return (getVirtualModel() != null ? getVirtualModel().getFullyQualifiedName() : "null") + "#"
				+ (getEditionPattern() != null ? getEditionPattern().getName() : "null") + "."
				+ (getEditionScheme() != null ? getEditionScheme().getName() : "null") + "." + getName();
	}

	public abstract Type getType();

	public abstract WidgetType getWidget();

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, DataBinding.BindingDefinitionType.GET,
			false);
	private BindingDefinition DEFAULT_VALUE = new BindingDefinition("defaultValue", Object.class, DataBinding.BindingDefinitionType.GET,
			false) {
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

	@Override
	public EditionScheme getEditionScheme() {
		return _scheme;
	}

	public EditionScheme getScheme() {
		return getEditionScheme();
	}

	@Override
	public VirtualModel<?> getVirtualModel() {
		if (getScheme() != null) {
			return getScheme().getVirtualModel();
		}
		return null;
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

	public boolean getUsePaletteLabelAsDefaultValue() {
		return usePaletteLabelAsDefaultValue;
	}

	public void setUsePaletteLabelAsDefaultValue(boolean usePaletteLabelAsDefaultValue) {
		this.usePaletteLabelAsDefaultValue = usePaletteLabelAsDefaultValue;
	}

	public boolean evaluateCondition(BindingEvaluationContext parameterRetriever) {
		if (getConditional().isValid()) {
			try {
				return getConditional().getBindingValue(parameterRetriever);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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

	public DataBinding<Boolean> getConditional() {
		if (conditional == null) {
			conditional = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		return conditional;
	}

	public void setConditional(DataBinding<Boolean> conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setDeclaredType(Boolean.class);
			conditional.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		this.conditional = conditional;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return getScheme() != null ? getScheme().getEditionPattern() : null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getScheme().getBindingModel();
	}

	public DataBinding<Object> getDefaultValue() {
		if (defaultValue == null) {
			defaultValue = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
			defaultValue.setBindingName("defaultValue");
		}
		return defaultValue;
	}

	public void setDefaultValue(DataBinding<Object> defaultValue) {
		if (defaultValue != null) {
			defaultValue.setOwner(this);
			defaultValue.setBindingName("defaultValue");
			defaultValue.setDeclaredType(Object.class);
			defaultValue.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue(EditionSchemeAction<?, ?> action) {
		DiagramPaletteElement paletteElement = action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() : null;

		// System.out.println("Default value for "+element.getName()+" ???");
		if (getUsePaletteLabelAsDefaultValue() && paletteElement != null) {
			return paletteElement.getName();
		}
		if (getDefaultValue().isValid()) {
			try {
				return getDefaultValue().getBindingValue(action);
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

	private boolean isRequired = false;

	public boolean getIsRequired() {
		return false;
	}

	public void setIsRequired(boolean flag) {
		isRequired = flag;
	}

	public boolean isValid(EditionSchemeAction action, Object value) {
		return !getIsRequired() || value != null;
	}

	@Override
	public Function getFunction() {
		return getEditionScheme();
	}

	@Override
	public String getArgumentName() {
		return getName();
	}

	@Override
	public Type getArgumentType() {
		return getType();
	}

}
