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
import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.EditionPatternReference;


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
			DATA = new BindingDefinition("data", getDefaultDataClass(), BindingDefinitionType.GET_SET, false);
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
	private BindingModel _bindingModel;
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
			_bindingModel.addToBindingVariables(new PatternRolePathElement(role));
		}	
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
	

	public static enum InspectorBindingAttribute
	{
		data,
		conditional
	}

	private static DefaultBindingFactory BINDING_FACTORY = new DefaultBindingFactory() {
		@Override
		public BindingPathElement getBindingPathElement(BindingPathElement father, String propertyName) {
			if (father instanceof EditionPatternPathElement) {
				EditionPattern ep = ((EditionPatternPathElement) father).editionPattern;
				PatternRole pr = ep.getPatternRole(propertyName);
				if (pr != null) {
					return ((EditionPatternPathElement) father).getPatternRolePathElement(pr);
				}
				else {
					logger.warning("Not found pattern role: "+propertyName);
				}
			}
			return super.getBindingPathElement(father, propertyName);
		}
	};

	public static class PatternRolePathElement implements SimplePathElement, BindingVariable
	{
		private PatternRole patternRole;
		
		public PatternRolePathElement(PatternRole aPatternRole)
		{
			this.patternRole = aPatternRole;
		}
		
		@Override
		public Class getDeclaringClass() {
			return EditionPattern.class;
		}

		@Override
		public Type getType() {
			return patternRole.getAccessedClass();
		}

		@Override
		public String getSerializationRepresentation() {
			return patternRole.getName();
		}

		@Override
		public boolean isBindingValid() {
			return true;
		}

		@Override
		public String getLabel() {
			return getSerializationRepresentation();
		}

		@Override
		public String getTooltipText(Type resultingType) {
			return patternRole.getDescription();
		}

		@Override
		public boolean isSettable() {
			return false;
		}

		@Override
		public Bindable getContainer() {
			//return patternRole.getEditionPattern();
			return null;
		}

		@Override
		public String getVariableName() {
			return patternRole.getName();
		}
		
		
		@Override
		public Object evaluateBinding(Object target, BindingEvaluationContext context) 
		{
			if (target instanceof EditionPatternReference) {
				return ((EditionPatternReference) target).getEditionPatternInstance().getPatternActor(patternRole);
			}
			logger.warning("Unexpected call to evaluateBinding() target="+target+" context="+context);
 			return null;
		}
	}
	
	public static class EditionPatternPathElement implements BindingVariable
	{
		private EditionPattern editionPattern;
		private int index;
		private Hashtable<PatternRole,PatternRolePathElement> patternRoleElements;
		
		public EditionPatternPathElement(EditionPattern anEditionPattern, int index)
		{
			this.editionPattern = anEditionPattern;
			this.index = index;
			patternRoleElements = new Hashtable<PatternRole, InspectorEntry.PatternRolePathElement>();
			for (PatternRole pr : editionPattern.getPatternRoles()) {
				patternRoleElements.put(pr,new PatternRolePathElement(pr));
			}
		}
		
		public PatternRolePathElement getPatternRolePathElement(PatternRole pr)
		{
			return patternRoleElements.get(pr);
		}
		
		@Override
		public Class getDeclaringClass() {
			return null;
		}

		@Override
		public Type getType() {
			return EditionPattern.class;
		}

		@Override
		public String getSerializationRepresentation() {
			return editionPattern.getCalc().getName()+"_"+editionPattern.getName()+"_"+index;
		}

		@Override
		public boolean isBindingValid() {
			return true;
		}

		@Override
		public String getLabel() {
			return getSerializationRepresentation();
		}

		@Override
		public String getTooltipText(Type resultingType) {
			return editionPattern.getDescription();
		}

		@Override
		public boolean isSettable() {
			return false;
		}

		@Override
		public Bindable getContainer() {
			//return patternRole.getEditionPattern();
			return null;
		}

		@Override
		public String getVariableName() {
			return getSerializationRepresentation();
		}
				
		@Override
		public Object evaluateBinding(Object target, BindingEvaluationContext context) 
		{
			logger.info("evaluateBinding EditionPatternPathElement with target="+target+" context="+context);
 			return null;
		}

		public EditionPattern getEditionPattern() {
			return editionPattern;
		}

		public int getIndex() {
			return index;
		}
	}


}
