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
package org.openflexo.fib.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.GenericArrayTypeImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.view.widget.FIBMultipleValueWidget;
import org.openflexo.toolbox.StringUtils;

import com.google.common.reflect.TypeToken;

public abstract class FIBMultipleValues extends FIBWidget {

	private static final Logger logger = Logger.getLogger(FIBMultipleValues.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute {
		staticList, list, array, showIcon, showText, iteratorClass, autoSelectFirstRow
	}

	@Deprecated
	public BindingDefinition LIST = new BindingDefinition("list", new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(Object.class)),
			DataBinding.BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return getListBindingType();
		}
	};
	@Deprecated
	public BindingDefinition ARRAY = new BindingDefinition("array", new GenericArrayTypeImpl(new WilcardTypeImpl(Object.class)),
			DataBinding.BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return getArrayBindingType();
		}
	};

	@Deprecated
	private BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET_SET, false) {
		@Override
		public Type getType() {
			return getDataType();
		};
	};

	private String staticList;

	private DataBinding<List<?>> list;
	private DataBinding<Object[]> array;

	private Class iteratorClass;
	private Class expectedIteratorClass;

	private boolean showIcon = false;
	private boolean showText = true;

	private boolean autoSelectFirstRow = false;

	public FIBMultipleValues() {
	}

	private Type LIST_BINDING_TYPE;
	private Type ARRAY_BINDING_TYPE;

	private Type getListBindingType() {
		if (LIST_BINDING_TYPE == null) {
			LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(getIteratorType()));
		}
		return LIST_BINDING_TYPE;
	}

	private Type getArrayBindingType() {
		if (ARRAY_BINDING_TYPE == null) {
			ARRAY_BINDING_TYPE = new GenericArrayTypeImpl(new WilcardTypeImpl(getIteratorType()));
		}
		return ARRAY_BINDING_TYPE;
	}

	public DataBinding<List<?>> getList() {
		if (list == null) {
			list = new DataBinding<List<?>>(this, new TypeToken<List<?>>() {
			}.getType(), DataBinding.BindingDefinitionType.GET);
		}
		return list;
	}

	public void setList(DataBinding<List<?>> list) {
		if (list != null) {
			list.setOwner(this);
			list.setDeclaredType(new TypeToken<List<?>>() {
			}.getType());
			list.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.list = list;
	}

	public DataBinding<Object[]> getArray() {
		if (array == null) {
			array = new DataBinding<Object[]>(this, new TypeToken<Object[]>() {
			}.getType(), DataBinding.BindingDefinitionType.GET);
		}
		return array;
	}

	public void setArray(DataBinding<Object[]> array) {
		if (array != null) {
			array.setOwner(this);
			array.setDeclaredType(new TypeToken<Object[]>() {
			}.getType());
			array.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.array = array;
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		if (list != null) {
			list.decode();
		}
		if (array != null) {
			array.decode();
		}
	}

	public boolean isStaticList() {
		return (getList() == null || !getList().isSet()) && (getArray() == null || !getArray().isSet())
				&& StringUtils.isNotEmpty(getStaticList());
	}

	public boolean isEnumType() {
		if (getData() != null) {
			Type type = getData().getAnalyzedType();
			if (type instanceof Class && ((Class) type).isEnum()) {
				return true;
			}
		}
		if (iteratorClass != null && iteratorClass.isEnum()) {
			return true;
		}
		if (expectedIteratorClass != null && expectedIteratorClass.isEnum()) {
			return true;
		}
		return false;
	}

	private Type iteratorType;

	public Type getIteratorType() {
		Class<?> iteratorClass = getIteratorClass();
		if (iteratorClass.getTypeParameters().length > 0) {
			if (iteratorType == null) {
				iteratorType = TypeUtils.makeInferedType(iteratorClass);
			}
			return iteratorType;
		}
		return iteratorClass;
	}

	public Class getIteratorClass() {
		if (isStaticList()) {
			return String.class;
		}
		if (iteratorClass == null) {
			if (expectedIteratorClass != null) {
				return expectedIteratorClass;
			} else {
				return Object.class;
			}
		}
		return iteratorClass;
	}

	public void setIteratorClass(Class iteratorClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.iteratorClass, iteratorClass);
		if (notification != null) {
			LIST_BINDING_TYPE = null;
			ARRAY_BINDING_TYPE = null;
			this.iteratorClass = iteratorClass;
			hasChanged(notification);
		}
	}

	@Override
	public Type getDataType() {
		if (isStaticList()) {
			return String.class;
		}
		if (iteratorClass != null) {
			return iteratorClass;
		}
		return super.getDataType();
	}

	@Override
	public Type getFormattedObjectType() {
		if (isStaticList()) {
			return String.class;
		}
		if (iteratorClass != null) {
			return iteratorClass;
		}
		return getDataType();
	}

	@Deprecated
	@Override
	public BindingDefinition getDataBindingDefinition() {
		return DATA;
	}

	/*@Override
	public final Type getDefaultDataClass()
	{
		return getIteratorClass();
	}*/

	@Override
	public final Type getDefaultDataClass() {
		return Object.class;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> binding) {
		// logger.info("******* notifyBindingChanged with "+binding);
		super.notifiedBindingChanged(binding);
		if (binding == getList()) {
			if (getList() != null) {
				Type accessedType = getList().getAnalyzedType();
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					Class newIteratorClass = TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
					if (getIteratorClass() == null || !TypeUtils.isClassAncestorOf(newIteratorClass, getIteratorClass())) {
						setIteratorClass(newIteratorClass);
					}
				}
			}
		} else if (binding == getArray()) {
			if (getArray() != null) {
				Type accessedType = getArray().getAnalyzedType();
				if (accessedType instanceof GenericArrayType) {
					Class newIteratorClass = TypeUtils.getBaseClass(((GenericArrayType) accessedType).getGenericComponentType());
					if (getIteratorClass() == null || !TypeUtils.isClassAncestorOf(newIteratorClass, getIteratorClass())) {
						setIteratorClass(newIteratorClass);
					}
				}
			}
		} else if (binding == getData()) {
			if (getData() != null) {
				Type accessedType = getData().getAnalyzedType();
				/*if (accessedType instanceof Class && ((Class)accessedType).isEnum()) {
					setIteratorClass((Class)accessedType);
				}*/
				if (accessedType instanceof Class) {
					expectedIteratorClass = (Class) accessedType;
				}

			}
		} else if (binding == getFormat()) {
			notifyChange(FIBWidget.Parameters.format.name(), null, getFormat());
		}
	}

	public String getStaticList() {
		return staticList;
	}

	public final void setStaticList(String staticList) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.staticList, staticList);
		if (notification != null) {
			this.staticList = staticList;
			LIST_BINDING_TYPE = null;
			ARRAY_BINDING_TYPE = null;
			// logger.info("FIBMultiple: setStaticList with " + staticList);
			hasChanged(notification);
		}
	}

	public Boolean getShowIcon() {
		return showIcon;
	}

	public void setShowIcon(Boolean showIcon) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.showIcon, showIcon);
		if (notification != null) {
			this.showIcon = showIcon;
			hasChanged(notification);
		}
	}

	public Boolean getShowText() {
		return showText;
	}

	public void setShowText(Boolean showText) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.showText, showText);
		if (notification != null) {
			this.showText = showText;
			hasChanged(notification);
		}
	}

	@Override
	public Type getDynamicAccessType() {
		Type[] args = new Type[2];
		args[0] = getDataType();
		args[1] = getIteratorType();
		return new ParameterizedTypeImpl(FIBMultipleValueWidget.class, args);
	}

	public boolean getAutoSelectFirstRow() {
		return autoSelectFirstRow;
	}

	public void setAutoSelectFirstRow(boolean autoSelectFirstRow) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.autoSelectFirstRow, autoSelectFirstRow);
		if (notification != null) {
			this.autoSelectFirstRow = autoSelectFirstRow;
			hasChanged(notification);
		}
	}

	@Override
	protected void applyValidation(ValidationReport report) {
		super.applyValidation(report);
		performValidation(FIBMultipleValuesMustDefineValueRange.class, report);
		performValidation(ListBindingMustBeValid.class, report);
		performValidation(ArrayBindingMustBeValid.class, report);
	}

	@Override
	public List<DataBinding<?>> getDependencyBindings() {
		List<DataBinding<?>> returned = super.getDependencyBindings();
		returned.add(getList());
		returned.add(getArray());
		return returned;
	}

	public static class FIBMultipleValuesMustDefineValueRange extends
			ValidationRule<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> {
		public FIBMultipleValuesMustDefineValueRange() {
			super(FIBMultipleValues.class, "widget_must_define_values_range_(either_static_list_or_dynamic_list_or_array_or_enumeration)");
		}

		@Override
		public ValidationIssue<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> applyValidation(FIBMultipleValues object) {
			if (StringUtils.isEmpty(object.getStaticList()) && !object.getList().isSet() && !object.getArray().isSet()
					&& !object.isEnumType()) {
				GenerateDefaultStaticList fixProposal = new GenerateDefaultStaticList();
				return new ValidationError<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues>(this, object,
						"widget_does_not_define_any_values_range_(either_static_list_or_dynamic_list_or_array_or_enumeration)", fixProposal);
			}
			return null;
		}

		protected static class GenerateDefaultStaticList extends FixProposal<FIBMultipleValuesMustDefineValueRange, FIBMultipleValues> {

			public GenerateDefaultStaticList() {
				super("generate_default_static_list");
			}

			@Override
			protected void fixAction() {
				getObject().setStaticList("Item 1 ,Item 2 ,Item 3 ");
			}

		}
	}

	public static class ListBindingMustBeValid extends BindingMustBeValid<FIBMultipleValues> {
		public ListBindingMustBeValid() {
			super("'list'_binding_is_not_valid", FIBMultipleValues.class);
		}

		@Override
		public DataBinding getBinding(FIBMultipleValues object) {
			return object.getList();
		}

	}

	public static class ArrayBindingMustBeValid extends BindingMustBeValid<FIBMultipleValues> {
		public ArrayBindingMustBeValid() {
			super("'array'_binding_is_not_valid", FIBMultipleValues.class);
		}

		@Override
		public DataBinding getBinding(FIBMultipleValues object) {
			return object.getArray();
		}

	}

}
