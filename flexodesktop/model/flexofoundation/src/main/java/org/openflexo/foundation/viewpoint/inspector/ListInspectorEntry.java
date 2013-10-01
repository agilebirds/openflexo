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
import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.binding.OntologyObjectPathElement.OntologyIndividualPathElement;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.toolbox.Holder;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents an inspector entry for an ontology individual
 * 
 * @author sylvain
 * 
 */
public class ListInspectorEntry extends InspectorEntry {

	public enum ListType {
		Individual(OntologyIndividual.class), String(String.class), Process(FlexoProcess.class), ProcessFolder(
				org.openflexo.foundation.wkf.ProcessFolder.class), Role(org.openflexo.foundation.wkf.Role.class), Activity(
				AbstractActivityNode.class), Operation(OperationNode.class), Action(ActionNode.class), Event(EventNode.class), Screen(
				ComponentDefinition.class);
		private Class<?> type;

		private ListType(Class<?> type) {
			this.type = type;
		}

		public Class<?> getType() {
			return type;
		}

	}

	public static class StringHolder extends Holder<String> implements StringConvertable<StringHolder> {

		public static final Converter<StringHolder> converter = new Converter<ListInspectorEntry.StringHolder>(StringHolder.class) {

			@Override
			public StringHolder convertFromString(String value) {
				return new StringHolder(value);
			}

			@Override
			public String convertToString(StringHolder value) {
				return value != null ? value.get() : null;
			}

		};

		public StringHolder() {
		}

		public StringHolder(String value) {
			setValue(value);
		}

		@Override
		public Converter<? extends StringHolder> getConverter() {
			return converter;
		}

	}

	public static final BindingDefinition FORMAT = new BindingDefinition("format", String.class, BindingDefinitionType.GET, false);

	private BindingDefinition LIST;

	// Serialized
	private ListType listType = ListType.Individual;

	// Serialized
	private String conceptURI;

	// Serialized
	private List<StringHolder> staticList = new ArrayList<StringHolder>();

	// Serialized
	private ViewPointDataBinding list;

	// Serialized
	private ViewPointDataBinding format;

	private Formatter formatter;

	private class Formatter extends ViewPointObject implements Bindable {
		public Formatter() {
			super(null);
		}

		private BindingModel formatterBindingModel = null;

		@Override
		public BindingModel getBindingModel() {
			if (formatterBindingModel == null) {
				createFormatterBindingModel();
			}
			return formatterBindingModel;
		}

		public void resetBindingModel() {
			formatterBindingModel = null;
		}

		private void createFormatterBindingModel() {
			formatterBindingModel = new BindingModel(ListInspectorEntry.this.getBindingModel());
			if (getListType() == ListType.Individual && getConcept() != null) {
				formatterBindingModel.addToBindingVariables(new OntologyIndividualPathElement("object", getConcept(), null, getConcept()
						.getFlexoOntology()));
			} else {
				formatterBindingModel.addToBindingVariables(new BindingVariableImpl<Object>(this, "object", Object.class) {
					@Override
					public Type getType() {
						return ListInspectorEntry.this.getType();
					}
				});
			}
		}

		@Override
		public BindingFactory getBindingFactory() {
			return ListInspectorEntry.this.getBindingFactory();
		}

		@Override
		public ViewPoint getViewPoint() {
			return ListInspectorEntry.this.getViewPoint();
		}

		@Override
		public String getLanguageRepresentation() {
			return ListInspectorEntry.this.getLanguageRepresentation() + " " + "formatter";
		}
	}

	public ListInspectorEntry(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getListType() == ListType.Individual) {
			if (getConcept() != null) {
				return IndividualOfClass.getIndividualOfClass(getConcept());
			}
		}
		return super.getType();
	}

	@Override
	public Class<?> getDefaultDataClass() {
		return getListType().getType();
	}

	@Override
	public String getWidgetName() {
		return "Dropdown";
	}

	public BindingDefinition getListBindingDefinition() {
		if (LIST == null) {
			LIST = new BindingDefinition("list", getType(), BindingDefinitionType.GET_SET, false) {
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
					return new ParameterizedTypeImpl(List.class, ListInspectorEntry.super.getType());
				}
			};
		}
		return LIST;
	}

	public ViewPointDataBinding getList() {
		if (list == null) {
			list = new ViewPointDataBinding(this, InspectorEntryBindingAttribute.list, LIST);
		}
		return list;
	}

	public void setList(ViewPointDataBinding list) {
		if (list != null) {
			list.setOwner(this);
			list.setBindingAttribute(InspectorEntryBindingAttribute.list);
			list.setBindingDefinition(getListBindingDefinition());
		}
		this.list = list;
		notifyBindingChanged(this.list);
		setChanged();
		notifyChange("list", null, list);
	}

	public Formatter getFormatter() {
		if (formatter == null) {
			formatter = new Formatter();
		}
		return formatter;
	}

	public BindingDefinition getFormatBindingDefinition() {
		return FORMAT;
	}

	public ViewPointDataBinding getFormat() {
		if (format == null) {
			format = new ViewPointDataBinding(getFormatter(), InspectorEntryBindingAttribute.format, FORMAT);
		}
		return format;
	}

	public void setFormat(ViewPointDataBinding format) {
		if (format != null) {
			format.setOwner(getFormatter());
			format.setBindingAttribute(InspectorEntryBindingAttribute.format);
			format.setBindingDefinition(getFormatBindingDefinition());
		}
		this.format = format;
		notifyBindingChanged(this.format);
		setChanged();
		notifyChange("format", null, format);
		this.format = format;
		setChanged();

	}

	public void createStaticListEntry() {
		addToStaticList(new StringHolder("Value-" + (getStaticList().size() + 1)));
	}

	public List<StringHolder> getStaticList() {
		return staticList;
	}

	public void setStaticList(List<StringHolder> staticList) {
		this.staticList = staticList;
		setChanged();
		notifyChange("staticList", null, staticList);
	}

	public void addToStaticList(StringHolder string) {
		staticList.add(string);
		setChanged();
		notifyChange("staticList", null, staticList);
	}

	public void removeFromStaticList(StringHolder string) {
		staticList.remove(string);
		setChanged();
		notifyChange("staticList", null, staticList);
	}

	public String _getConceptURI() {
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) {
		this.conceptURI = conceptURI;
		if (formatter != null) {
			formatter.resetBindingModel();
		}
	}

	public OntologyClass getConcept() {
		if (getViewPoint() != null) {
			getViewPoint().loadWhenUnloaded();
		}
		if (getOntologyLibrary() != null) {
			return getViewPoint().getViewpointOntology().getClass(_getConceptURI());
		}
		return null;
	}

	public void setConcept(OntologyClass c) {
		_setConceptURI(c != null ? c.getURI() : null);
	}

	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		if (listType != this.listType && listType != null) {
			if (formatter != null) {
				formatter.resetBindingModel();
			}
			this.listType = listType;
			setChanged();
			notifyChange("listType", null, listType);
		}
	}
}
