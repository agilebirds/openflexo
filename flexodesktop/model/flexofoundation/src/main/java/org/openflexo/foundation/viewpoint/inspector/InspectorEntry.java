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

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;


/**
 * Represents an inspector entry (a data related to an edition pattern which can be inspected)
 * 
 * @author sylvain
 *
 */
public abstract class InspectorEntry extends ViewPointObject implements Bindable  {

	static final Logger logger = Logger.getLogger(InspectorEntry.class.getPackage().getName());

	
	public static BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);
	private BindingDefinition DATA;
	
	public BindingDefinition getDataBindingDefinition()
	{
		if (DATA == null) {
			DATA = new BindingDefinition("data", getDefaultDataClass(), BindingDefinitionType.GET_SET, false) {
				@Override
				public BindingDefinitionType getBindingDefinitionType() {
					if (getIsReadOnly()) return BindingDefinitionType.GET;
					else return BindingDefinitionType.GET_SET;
				}
			};
		}
		return DATA;
	}

	public BindingDefinition getConditionalBindingDefinition()
	{
		return CONDITIONAL;
	}

	private EditionPatternInspector inspector;
	private String name;
	private String label;
	private boolean readOnly;
	
	private InspectorDataBinding data;
	private InspectorDataBinding conditional;
	
	public InspectorEntry() 
	{
		super();
	}

	public abstract Class getDefaultDataClass();
	
	public abstract String getWidgetName();
	
	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public ViewPoint getCalc() 
	{
		if (getEditionPattern() != null) return getEditionPattern().getCalc();
		return null;
	}

	public EditionPattern getEditionPattern() 
	{
		if (getInspector() != null) return getInspector().getEditionPattern();
		return null;
	}

	public EditionPatternInspector getInspector() {
		return inspector;
	}


	public void setInspector(EditionPatternInspector inspector) {
		this.inspector = inspector;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) 
	{
		this.label = label;
	}


	public boolean isSingleEntry()
	{
		return true;
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
	
	public InspectorDataBinding getData() 
	{
		if (data == null) data = new InspectorDataBinding(this,InspectorEntryBindingAttribute.data,getDataBindingDefinition());
		return data;
	}

	public void setData(InspectorDataBinding data) 
	{
		data.setOwner(this);
		data.setBindingAttribute(InspectorEntryBindingAttribute.data);
		data.setBindingDefinition(getDataBindingDefinition());
		this.data = data;
	}
	
	public InspectorDataBinding getConditional() 
	{
		if (conditional == null) conditional = new InspectorDataBinding(this,InspectorEntryBindingAttribute.conditional,CONDITIONAL);
		return conditional;
	}

	public void setConditional(InspectorDataBinding conditional) 
	{
		conditional.setOwner(this);
		conditional.setBindingAttribute(InspectorEntryBindingAttribute.conditional);
		conditional.setBindingDefinition(CONDITIONAL);
		this.conditional = conditional;
	}
	

	public static enum InspectorEntryBindingAttribute implements InspectorBindingAttribute
	{
		data,
		conditional
	}

	@Override
	public BindingModel getBindingModel() 
	{
		return getInspector().getBindingModel();
	}
	


}
