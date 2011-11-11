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
package org.openflexo.inspector;

import java.awt.event.ActionEvent;

import org.openflexo.kvc.KeyValueCoding;

/**
 * Interface used by widgets to delegate the task of setting a new value for a certain property.
 * 
 * @author gpolet
 * 
 */
public interface InspectorDelegate {
	public boolean setObjectValue(Object value);

	public void setTarget(KeyValueCoding target);

	public void setKey(String path);

	public void setLocalizedPropertyName(String name);

	public boolean handlesObjectOfClass(Class c);

	public void performAction(ActionEvent e, String actionName, Object object);
}
