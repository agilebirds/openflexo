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

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.viewpoint.inspector.InspectorDataBinding;

public class URIParameter extends EditionSchemeParameter {


	private InspectorDataBinding baseURI;

	private BindingDefinition BASE_URI = new BindingDefinition("baseURI", String.class, BindingDefinitionType.GET, false);
	
	public BindingDefinition getBaseURIBindingDefinition()
	{
		return BASE_URI;
	}
	
	public InspectorDataBinding getBaseURI() 
	{
		if (baseURI == null) baseURI = new InspectorDataBinding(this,ParameterBindingAttribute.baseURI,getBaseURIBindingDefinition());
		return baseURI;
	}

	public void setBaseURI(InspectorDataBinding baseURI) 
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


}
