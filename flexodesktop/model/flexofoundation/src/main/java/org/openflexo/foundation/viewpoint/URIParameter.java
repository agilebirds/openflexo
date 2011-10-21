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

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class URIParameter extends EditionSchemeParameter {


	private ViewPointDataBinding baseURI;

	private BindingDefinition BASE_URI = new BindingDefinition("baseURI", String.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getBaseURIBindingDefinition()
	{
		return BASE_URI;
	}
	
	public ViewPointDataBinding getBaseURI() 
	{
		if (baseURI == null) baseURI = new ViewPointDataBinding(this,ParameterBindingAttribute.baseURI,getBaseURIBindingDefinition());
		return baseURI;
	}

	public void setBaseURI(ViewPointDataBinding baseURI) 
	{
		baseURI.setOwner(this);
		baseURI.setBindingAttribute(ParameterBindingAttribute.baseURI);
		baseURI.setBindingDefinition(getBaseURIBindingDefinition());
		this.baseURI = baseURI;
	}

	@Override
	public Type getType() {
		return String.class;
	}
	
	@Override
	public WidgetType getWidget() {
		return WidgetType.URI;
	}

	@Override
	public boolean isMandatory()
	{
		return true;
	}
	
	@Override
	public boolean isValid(EditionSchemeAction action, Object value)
	{
		if (! (value instanceof String)) return false;
		
		String proposedURI = (String)value;
		
		if (StringUtils.isEmpty(proposedURI)) {
			return false;
		}
		if (action.getProject().getProjectOntologyLibrary().isDuplicatedURI(action.getProject().getProjectOntology().getURI(),proposedURI)) {
			// declared_uri_must_be_unique_please_choose_an_other_uri
			return false;
		}
		else if (!action.getProject().getProjectOntologyLibrary().testValidURI(action.getProject().getProjectOntology().getURI(),proposedURI)) {
			// declared_uri_is_not_well_formed_please_choose_an_other_uri
			return false;
		}

		return true;
	}

	@Override
	public Object getDefaultValue(EditionSchemeAction<?> action, BindingEvaluationContext parameterRetriever) 
	{
		if (getBaseURI().isValid()) {
			String baseProposal = (String)getBaseURI().getBindingValue(parameterRetriever);
			if (baseProposal == null) return null;
			baseProposal = JavaUtils.getClassName(baseProposal);
			String proposal = baseProposal;
			Integer i = null;
			while (action.getProject().getProjectOntologyLibrary().isDuplicatedURI(
					action.getProject().getProjectOntology().getURI(),proposal)) {
				if (i == null) i= 1; else i++;
				proposal = baseProposal+i;
			}
			return proposal;			
		}
		return null;
	}


}
