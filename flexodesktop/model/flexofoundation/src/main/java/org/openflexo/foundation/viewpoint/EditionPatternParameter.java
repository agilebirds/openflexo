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

import java.util.Hashtable;
import java.util.Vector;

import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnresolvedExpressionException;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.toolbox.StringUtils;

import com.ibm.icu.util.StringTokenizer;

public class EditionPatternParameter extends ViewPointObject {

	public static enum WidgetType
	{
		TEXT_FIELD,
		LOCALIZED_TEXT_FIELD,
		TEXT_AREA,
		INTEGER,
		FLOAT,
		CHECKBOX,
		DROPDOWN,
		CUSTOM
	}
	
	public static enum CustomType
	{
		OntologyIndividual,
		FlexoRole
	}
	
	private String name;
	private String label;
	private String description;
	private WidgetType widget;
	private String values;
	private String defaultValue;
	private boolean usePaletteLabelAsDefaultValue;
	private boolean uniqueURI;
	private String base;
	private String conditional;
	private String conceptURI;
	private CustomType customType;
	
	private Expression condition;

	private EditionScheme _scheme;
	
	public EditionPatternParameter() {
	}
	

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

	public CustomType getCustomType()
	{
		return customType;
	}

	public void setCustomType(CustomType customType)
	{
		this.customType = customType;
	}

	public String getLabel() 
	{
		return label;
	}

	public void setLabel(String label) 
	{
		this.label = label;
	}

	public String _getConceptURI() 
	{
		return conceptURI;
	}

	public void _setConceptURI(String conceptURI) 
	{
		this.conceptURI = conceptURI;
	}
	
	public OntologyClass getConcept()
	{
		getCalc().loadWhenUnloaded();
		return getOntologyLibrary().getClass(_getConceptURI());
	}
	
	public void setConcept(OntologyClass c)
	{
		_setConceptURI(c != null ? c.getURI() : null);
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.EDITION_PATTERN_PARAMETER_INSPECTOR;
	}
	
	public WidgetType getWidget() 
	{
		return widget;
	}

	public void setWidget(WidgetType widget) 
	{
		this.widget = widget;
	}

	public String getDefaultValue() 
	{
		if (defaultValue == null) {
			return "";
		}
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
		return getDefaultValue();
	}

	public void setDefaultValue(String defaultValue) 
	{
		this.defaultValue = defaultValue;
	}

	public boolean getUsePaletteLabelAsDefaultValue() 
	{
		return usePaletteLabelAsDefaultValue;
	}
	
	public void setUsePaletteLabelAsDefaultValue(boolean usePaletteLabelAsDefaultValue) 
	{
		this.usePaletteLabelAsDefaultValue = usePaletteLabelAsDefaultValue;
	}

	public String getValues() 
	{
		return values;
	}
	
	public void setValues(String values) 
	{
		this.values = values;
		valuesList = new Vector<String>();
		StringTokenizer st = new StringTokenizer(values,",");
		while(st.hasMoreTokens()) {
			valuesList.add(st.nextToken());
		}
	}

	private Vector<String> valuesList;

	public Vector<String> getValuesList() 
	{
		return valuesList;
	}


	public boolean getUniqueURI()
	{
		return uniqueURI;
	}


	public void setUniqueURI(boolean uniqueURI) 
	{
		this.uniqueURI = uniqueURI;
	}


	public String getBase() 
	{
		return base;
	}


	public void setBase(String base) 
	{
		this.base = base;
	}

	public String getConditional() 
	{
		return conditional;
	}

	public void setConditional(String conditional) 
	{
		this.conditional = conditional;
		if (StringUtils.isNotEmpty(conditional)) {
			DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				condition = parser.parse(conditional);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean evaluateCondition(final Hashtable<String,Object> parameterValues)
	{
		if (condition == null) {
			return true;
		}
		try {
			return condition.evaluateCondition(parameterValues);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (UnresolvedExpressionException e) {
			e.printStackTrace();
		}
		return false;
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
}
