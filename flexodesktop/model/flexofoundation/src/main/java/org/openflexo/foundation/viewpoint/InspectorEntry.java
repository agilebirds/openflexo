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

import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.foundation.ontology.OntologyProperty;


/**
 * Represents an inspector entry (a data related to an edition pattern which can be inspected)
 * 
 * @author sylvain
 *
 */
public abstract class InspectorEntry extends ViewPointObject implements Bindable, BindingEvaluationContext {

	static final Logger logger = Logger.getLogger(InspectorEntry.class.getPackage().getName());

	public static BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);
	private BindingDefinition DATA;
	
	public BindingDefinition getDataBindingDefinition()
	{
		if (DATA == null) {
			DATA = new BindingDefinition("data", getDefaultDataClass(), BindingDefinitionType.GET_SET, false);
		}
		return DATA;
	}

	public BindingDefinition getConditionalBindingDefinition()
	{
		return CONDITIONAL;
	}

	private EditionPatternInspector inspector;
	private String label;
	private PatternRole patternRole;
	private BindingModel _bindingModel;
	
	private OntologyProperty ontologyProperty;
	
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


	public PatternRole getPatternRole() 
	{
		return patternRole;
	}


	public void setPatternRole(PatternRole patternRole) 
	{
		this.patternRole = patternRole;
	}

	public OntologyProperty getOntologyProperty() 
	{
		return ontologyProperty;
	}

	public void setOntologyProperty(OntologyProperty ontologyProperty) 
	{
		this.ontologyProperty = ontologyProperty;
	}

	public boolean isSingleEntry()
	{
		return true;
	}
	
	public boolean isOntologicConcept()
	{
		return getPatternRole() instanceof OntologicObjectPatternRole;
	}
	
	public void notifyBindingChanged(InspectorDataBinding binding)
	{
	}
	
	public void notifyChange(InspectorBindingAttribute bindingAttribute, AbstractBinding oldValue, AbstractBinding value) 
	{
	}

	@Override
	public BindingFactory getBindingFactory() 
	{
		return BINDING_FACTORY;
	}
	
	@Override
	public BindingModel getBindingModel() 
	{
			if (_bindingModel == null) createBindingModel();
			return _bindingModel;
	}
	
	public void updateBindingModel()
	{
		logger.fine("updateBindingModel()");
		_bindingModel = null;
		createBindingModel();
	}
	
	private void createBindingModel()
	{
		_bindingModel = new BindingModel();
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			_bindingModel.addToBindingVariables(new BindingVariableImpl(this, role.getName(), role.getAccessedClass()));
		}	
	}

	public InspectorDataBinding getData() 
	{
		if (data == null) data = new InspectorDataBinding(this,InspectorBindingAttribute.data,getDataBindingDefinition());
		return data;
	}

	public void setData(InspectorDataBinding data) 
	{
		data.setOwner(this);
		data.setBindingAttribute(InspectorBindingAttribute.data);
		data.setBindingDefinition(getDataBindingDefinition());
		this.data = data;
	}
	
	public InspectorDataBinding getConditional() 
	{
		if (conditional == null) conditional = new InspectorDataBinding(this,InspectorBindingAttribute.conditional,CONDITIONAL);
		return conditional;
	}

	public void setConditional(InspectorDataBinding conditional) 
	{
		conditional.setOwner(this);
		conditional.setBindingAttribute(InspectorBindingAttribute.conditional);
		conditional.setBindingDefinition(CONDITIONAL);
		this.conditional = conditional;
	}
	

	@Override
	public Object getValue(BindingVariable variable) 
	{
		logger.warning("Prout ici, faut faire un truc...");
		return null;
	}
	
	private static DefaultBindingFactory BINDING_FACTORY = new DefaultBindingFactory();

	public static enum InspectorBindingAttribute
	{
		data,
		conditional
	}


}
