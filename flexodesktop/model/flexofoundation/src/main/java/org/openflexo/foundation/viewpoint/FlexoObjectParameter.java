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

import org.openflexo.foundation.FlexoModelObject;

public class FlexoObjectParameter extends EditionSchemeParameter {

	private FlexoObjectType flexoObjectType;
	
	@Override
	public Type getType() {
		return FlexoModelObject.class;
	};

	@Override
	public WidgetType getWidget() {
		return WidgetType.FLEXO_OBJECT;
	}
	
	public static enum FlexoObjectType
	{
		Process,
		ProcessFolder,
		Role,
		Activity,
		Operation,
		Action
	}
	
	public FlexoObjectType getFlexoObjectType() {
		return flexoObjectType;
	}

	public void setFlexoObjectType(FlexoObjectType flexoObjectType) {
		this.flexoObjectType = flexoObjectType;
	}

}
