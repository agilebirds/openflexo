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
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

@ModelEntity(isAbstract = true)
@ImplementationClass(EditionSchemeParameter.EditionSchemeParameterImpl.class)
public interface EditionSchemeParameter extends EditionSchemeObject, FunctionArgument {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DEFAULT_VALUE_KEY = "defaultValue";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONDITIONAL_KEY = "conditional";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_REQUIRED_KEY = "isRequired";

	@Override
	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Override
	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = DEFAULT_VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getDefaultValue();

	@Setter(DEFAULT_VALUE_KEY)
	public void setDefaultValue(DataBinding<?> defaultValue);

	@Override
	@Getter(value = DESCRIPTION_KEY)
	@XMLElement
	public String getDescription();

	@Override
	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = CONDITIONAL_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getConditional();

	@Setter(CONDITIONAL_KEY)
	public void setConditional(DataBinding<Boolean> conditional);

	@Getter(value = IS_REQUIRED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRequired();

	@Setter(IS_REQUIRED_KEY)
	public void setIsRequired(boolean isRequired);

	public abstract Type getType();

	public static abstract class EditionSchemeParameterImpl extends EditionSchemeObjectImpl implements EditionSchemeParameter {

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
			EDITION_PATTERN,
			TECHNOLOGY_OBJECT;
		}

		private String label;
		// private boolean usePaletteLabelAsDefaultValue;

		private EditionScheme _scheme;

		private DataBinding<Boolean> conditional;
		private DataBinding<?> defaultValue;

		public EditionSchemeParameterImpl() {
			super();
		}

		@Override
		public String getURI() {
			return getEditionScheme().getURI() + "." + getName();
		}

		@Override
		public String getStringRepresentation() {
			return (getVirtualModel() != null ? getVirtualModel().getStringRepresentation() : "null") + "#"
					+ (getEditionPattern() != null ? getEditionPattern().getName() : "null") + "."
					+ (getEditionScheme() != null ? getEditionScheme().getName() : "null") + "." + getName();
		}

		@Override
		public abstract Type getType();

		public abstract WidgetType getWidget();

		private final BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class,
				DataBinding.BindingDefinitionType.GET, false);
		private final BindingDefinition DEFAULT_VALUE = new BindingDefinition("defaultValue", Object.class,
				DataBinding.BindingDefinitionType.GET, false) {
			@Override
			public Type getType() {
				return EditionSchemeParameterImpl.this.getType();
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
		public VirtualModel getVirtualModel() {
			if (getScheme() != null) {
				return getScheme().getVirtualModel();
			}
			return null;
		}

		@Override
		public String getLabel() {
			if (label == null || StringUtils.isEmpty(label)) {
				return getName();
			}
			return label;
		}

		@Override
		public void setLabel(String label) {
			this.label = label;
		}

		/*public boolean getUsePaletteLabelAsDefaultValue() {
			return usePaletteLabelAsDefaultValue;
		}

		public void setUsePaletteLabelAsDefaultValue(boolean usePaletteLabelAsDefaultValue) {
			this.usePaletteLabelAsDefaultValue = usePaletteLabelAsDefaultValue;
		}*/

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

		@Override
		public DataBinding<Boolean> getConditional() {
			if (conditional == null) {
				conditional = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				conditional.setBindingName("conditional");
			}
			return conditional;
		}

		@Override
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

		@Override
		public DataBinding<?> getDefaultValue() {
			if (defaultValue == null) {
				defaultValue = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
				defaultValue.setBindingName("defaultValue");
			}
			return defaultValue;
		}

		@Override
		public void setDefaultValue(DataBinding<?> defaultValue) {
			if (defaultValue != null) {
				defaultValue.setOwner(this);
				defaultValue.setBindingName("defaultValue");
				defaultValue.setDeclaredType(Object.class);
				defaultValue.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.defaultValue = defaultValue;
		}

		public Object getDefaultValue(EditionSchemeAction<?, ?, ?> action) {
			// DiagramPaletteElement paletteElement = action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() :
			// null;

			// System.out.println("Default value for "+element.getName()+" ???");
			/*if (getUsePaletteLabelAsDefaultValue() && paletteElement != null) {
				return paletteElement.getName();
			}*/
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

		@Override
		public boolean getIsRequired() {
			return isRequired;
		}

		@Override
		public final void setIsRequired(boolean flag) {
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
}
