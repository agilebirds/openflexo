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
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

/**
 * Represents an inspector entry (a data related to an edition pattern which can be inspected)
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(InspectorEntry.InspectorEntryImpl.class)
public abstract interface InspectorEntry extends EditionPatternObject, Bindable {

	@PropertyIdentifier(type = EditionPatternInspector.class)
	public static final String INSPECTOR_KEY = "inspector";

	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_READ_ONLY_KEY = "isReadOnly";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CONDITIONAL_KEY = "conditional";

	@Getter(value = INSPECTOR_KEY, inverse = EditionPatternInspector.ENTRIES_KEY)
	@XMLElement(xmlTag = "Inspector")
	public EditionPatternInspector getInspector();

	@Setter(INSPECTOR_KEY)
	public void setInspector(EditionPatternInspector inspector);

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Override
	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Override
	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = IS_READ_ONLY_KEY, defaultValue = "false")
	@XMLAttribute(xmlTag = "readOnly")
	public boolean getIsReadOnly();

	@Setter(IS_READ_ONLY_KEY)
	public void setIsReadOnly(boolean isReadOnly);

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<?> data);

	@Getter(value = CONDITIONAL_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getConditional();

	@Setter(CONDITIONAL_KEY)
	public void setConditional(DataBinding<Boolean> conditional);

	public static abstract class InspectorEntryImpl extends EditionPatternObjectImpl implements InspectorEntry {

		static final Logger logger = Logger.getLogger(InspectorEntry.class.getPackage().getName());

		private EditionPatternInspector inspector;
		private String label;
		private boolean readOnly;

		private DataBinding<?> data;
		private DataBinding<Boolean> conditional;

		public InspectorEntryImpl() {
			super();
		}

		@Override
		public String getURI() {
			return null;
		}

		public Type getType() {
			return getDefaultDataClass();
		}

		public abstract Class getDefaultDataClass();

		public abstract String getWidgetName();

		@Override
		public VirtualModel getVirtualModel() {
			if (getEditionPattern() != null) {
				return getEditionPattern().getVirtualModel();
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

		@Override
		public EditionPatternInspector getInspector() {
			return inspector;
		}

		@Override
		public void setInspector(EditionPatternInspector inspector) {
			this.inspector = inspector;
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
			if (label != null && label.equals(getName())) {
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
		public boolean getIsReadOnly() {
			return readOnly;
		}

		@Override
		public void setIsReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}

		@Override
		public DataBinding<?> getData() {
			if (data == null) {
				data = new DataBinding<Object>(this, getType(), (getIsReadOnly() ? DataBinding.BindingDefinitionType.GET
						: DataBinding.BindingDefinitionType.GET_SET));
				data.setBindingName("data");
			}
			return data;
		}

		@Override
		public void setData(DataBinding<?> data) {
			if (data != null) {
				data.setOwner(this);
				data.setDeclaredType(getType());
				data.setBindingDefinitionType(getIsReadOnly() ? DataBinding.BindingDefinitionType.GET
						: DataBinding.BindingDefinitionType.GET_SET);
				data.setBindingName("data");
			}
			this.data = data;
			notifiedBindingChanged(this.data);
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
		public BindingModel getBindingModel() {
			EditionPatternInspector inspector = getInspector();
			if (inspector != null) {
				return getInspector().getBindingModel();
			} else
				return null;
		}

	}

	public static class DataBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<InspectorEntry> {
		public DataBindingIsRequiredAndMustBeValid() {
			super("'data'_binding_is_not_valid", InspectorEntry.class);
		}

		@Override
		public DataBinding<?> getBinding(InspectorEntry object) {
			return object.getData();
		}

	}

}
