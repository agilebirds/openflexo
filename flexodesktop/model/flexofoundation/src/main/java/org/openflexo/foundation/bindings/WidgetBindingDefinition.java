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
package org.openflexo.foundation.bindings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.inspector.InspectableObject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WidgetBindingDefinition extends BindingDefinition implements InspectableObject {

	@SuppressWarnings("hiding")
	private static final Logger logger = Logger.getLogger(WidgetBindingDefinition.class.getPackage().getName());

	public WidgetBindingDefinition(String variableName, DMType type, IEWidget widget, BindingDefinitionType bindingType, boolean mandatory) {
		super(variableName, type, widget, bindingType, mandatory);
	}

	public static WidgetBindingDefinition get(IEWidget widget, String name, Class type, BindingDefinitionType bindingType, boolean mandatory) {
		if (widget.getProject() == null) {
			if (logger.isLoggable(Level.SEVERE))
				logger.severe("Widget " + widget.getFullyQualifiedName() + " has no project!!!");
			return null;
		}
		return widget.getProject().getWidgetBindingDefinition(widget, name, type, bindingType, mandatory);
	}

}
