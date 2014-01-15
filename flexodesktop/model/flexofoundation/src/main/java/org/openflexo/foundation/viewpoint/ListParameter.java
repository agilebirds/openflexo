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
import java.util.List;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.ListParameter.ListParameterImpl.ListType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(ListParameter.ListParameterImpl.class)
@XMLElement
public interface ListParameter extends EditionSchemeParameter {

	@PropertyIdentifier(type = ListType.class)
	public static final String LIST_TYPE_KEY = "listType";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LIST_KEY = "list";

	@Getter(value = LIST_TYPE_KEY)
	@XMLAttribute
	public ListType getListType();

	@Setter(LIST_TYPE_KEY)
	public void setListType(ListType listType);

	@Getter(value = LIST_KEY)
	@XMLAttribute
	public DataBinding<List<?>> getList();

	@Setter(LIST_KEY)
	public void setList(DataBinding<List<?>> list);

	public Object getList(EditionSchemeAction<?, ?, ?> action);

	public static abstract class ListParameterImpl extends EditionSchemeParameterImpl implements ListParameter {

		public enum ListType {
			String, Property, ObjectProperty, DataProperty
		}

		private ListType listType;
		private DataBinding<List<?>> list;

		public ListParameterImpl() {
			super();
		}

		@Override
		public Type getType() {
			if (getListType() == null) {
				return List.class;
			}
			switch (getListType()) {
			case String:
				return new ParameterizedTypeImpl(List.class, String.class);
			case Property:
				return new ParameterizedTypeImpl(List.class, IFlexoOntologyStructuralProperty.class);
			case ObjectProperty:
				return new ParameterizedTypeImpl(List.class, IFlexoOntologyObjectProperty.class);
			case DataProperty:
				return new ParameterizedTypeImpl(List.class, IFlexoOntologyDataProperty.class);
			default:
				return List.class;
			}
		};

		@Override
		public WidgetType getWidget() {
			return WidgetType.LIST;
		}

		@Override
		public ListType getListType() {
			return listType;
		}

		@Override
		public void setListType(ListType listType) {
			this.listType = listType;
		}

		@Override
		public DataBinding<List<?>> getList() {
			if (list == null) {
				list = new DataBinding<List<?>>(this, getType(), BindingDefinitionType.GET);
			}
			return list;
		}

		@Override
		public void setList(DataBinding<List<?>> list) {
			if (list != null) {
				list.setOwner(this);
				list.setBindingName("list");
				list.setDeclaredType(getType());
				list.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.list = list;
		}

		@Override
		public Object getList(EditionSchemeAction<?, ?, ?> action) {
			if (getList().isValid()) {
				try {
					return getList().getBindingValue(action);
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

	}
}
