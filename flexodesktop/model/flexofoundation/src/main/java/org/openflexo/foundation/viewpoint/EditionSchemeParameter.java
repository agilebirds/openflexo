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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.viewpoint.inspector.InspectorBindingAttribute;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;
import org.openflexo.toolbox.StringUtils;

public abstract class EditionSchemeParameter extends ViewPointObject {

	public static enum WidgetType
	{
		URI {
			@Override
			public Type getType() {
				return String.class;
			}
		},
		TEXT_FIELD {
			@Override
			public Type getType() {
				return String.class;
			}
		},
		LOCALIZED_TEXT_FIELD {
			@Override
			public Type getType() {
				return String.class;
			}
		},
		TEXT_AREA {
			@Override
			public Type getType() {
				return String.class;
			}
		},
		INTEGER {
			@Override
			public Type getType() {
				return Integer.class;
			}
		},
		FLOAT {
			@Override
			public Type getType() {
				return Float.class;
			}
		},
		CHECKBOX {
			@Override
			public Type getType() {
				return Boolean.class;
			}
		},
		DROPDOWN {
			@Override
			public Type getType() {
				return Object.class;
			}
		},
		INDIVIDUAL {
			@Override
			public Type getType() {
				return OntologyIndividual.class;
			}
		},
		FLEXO_OBJECT {
			@Override
			public Type getType() {
				return OntologyIndividual.class;
			}
		};
		
		public abstract Type getType();
	}
	
	private String name;
	private String label;
	private String description;
	private boolean usePaletteLabelAsDefaultValue;

	private EditionScheme _scheme;
	
	public EditionSchemeParameter() {
	}
	
	public abstract Type getType();

	public abstract WidgetType getWidget();
	
	public void setScheme(EditionScheme scheme) 
	{
		_scheme = scheme;
	}

	public EditionScheme getScheme() 
	{
		return _scheme;
	}
	
	@Override
	public String getDescription() 
	{
		return description;
	}

	@Override
	public void setDescription(String description) 
	{
		this.description = description;
	}

	@Override
	public ViewPoint getCalc() 
	{
		return getScheme().getCalc();
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getLabel() 
	{
		return label;
	}

	public void setLabel(String label) 
	{
		this.label = label;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.EDITION_PATTERN_PARAMETER_INSPECTOR;
	}
	
	public boolean getUsePaletteLabelAsDefaultValue() 
	{
		return usePaletteLabelAsDefaultValue;
	}
	
	public void setUsePaletteLabelAsDefaultValue(boolean usePaletteLabelAsDefaultValue) 
	{
		this.usePaletteLabelAsDefaultValue = usePaletteLabelAsDefaultValue;
	}

	public boolean evaluateCondition(final Hashtable<String,Object> parameterValues)
	{
		/*if (condition == null) {
			return true;
		}
		try {
			return condition.evaluateCondition(parameterValues);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (UnresolvedExpressionException e) {
			e.printStackTrace();
		}
		return false;*/
		return true;
	}
	
	@Override
	public String toString()
	{
		return "EditionPatternParameter: "+getName();
	}

	public int getIndex()
	{
		return getScheme().getParameters().indexOf(this);
	}
	
	private InspectorDataBinding conditional;
	private InspectorDataBinding defaultValue;

	public static enum ParameterBindingAttribute implements InspectorBindingAttribute
	{
		conditional,
		baseURI,
		defaultValue
	}

	private BindingDefinition CONDITIONAL = new BindingDefinition("conditional", Boolean.class, BindingDefinitionType.GET, false);
	private BindingDefinition DEFAULT_VALUE = new BindingDefinition("defaultValue", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return EditionSchemeParameter.this.getType();
		};
	};
	
	public BindingDefinition getConditionalBindingDefinition()
	{
		return CONDITIONAL;
	}

	public BindingDefinition getDefaultValueBindingDefinition()
	{
		return DEFAULT_VALUE;
	}

	public InspectorDataBinding getConditional() 
	{
		if (conditional == null) conditional = new InspectorDataBinding(this,ParameterBindingAttribute.conditional,getConditionalBindingDefinition());
		return conditional;
	}

	public void setConditional(InspectorDataBinding conditional) 
	{
		conditional.setOwner(this);
		conditional.setBindingAttribute(ParameterBindingAttribute.conditional);
		conditional.setBindingDefinition(getConditionalBindingDefinition());
		this.conditional = conditional;
	}
	
	public EditionPattern getEditionPattern()
	{
		return getScheme().getEditionPattern();
	}
	
	@Override
	public BindingModel getBindingModel() 
	{
		return getScheme().getParametersBindingModel();
	}

	public InspectorDataBinding getDefaultValue() 
	{
		if (defaultValue == null) defaultValue = new InspectorDataBinding(this,ParameterBindingAttribute.defaultValue,getDefaultValueBindingDefinition());
		return defaultValue;
	}

	public String getDefaultValue(ViewPointPaletteElement element) 
	{
		//System.out.println("Default value for "+element.getName()+" ???");
		if (getUsePaletteLabelAsDefaultValue() && (element != null)) {
			return element.getName();
		}
		if ((element != null) && (element.getParameter(getName()) != null) && !StringUtils.isEmpty(element.getParameter(getName()).getValue())) {
			//System.out.println("Hop: "+element.getParameter(getName()).getValue());
			return element.getParameter(getName()).getValue();
		}
		return getDefaultValue().toString();
	}

	public void setDefaultValue(InspectorDataBinding defaultValue) 
	{
		defaultValue.setOwner(this);
		defaultValue.setBindingAttribute(ParameterBindingAttribute.defaultValue);
		defaultValue.setBindingDefinition(getDefaultValueBindingDefinition());
		this.defaultValue = defaultValue;
	}


}
