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
package org.openflexo.fge.notifications;

import org.openflexo.fge.GRParameter;
import org.openflexo.inspector.InspectableModification;

public class FGENotification implements InspectableModification {
	public GRParameter<?> parameter;
	public Object oldValue;
	public Object newValue;

	@Deprecated
	private String parameterName;

	public <T> FGENotification(GRParameter<T> parameter, T oldValue, T newValue) {
		super();
		this.parameter = parameter;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Deprecated
	public FGENotification(String parameterName, Object oldValue, Object newValue) {
		super();
		this.parameter = null;
		this.parameterName = parameterName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "FGENotification of " + getClass().getSimpleName() + " " + getParameter() + " old: " + oldValue + " new: " + newValue;
	}

	public GRParameter getParameter() {
		return parameter;
	}

	@Override
	public String propertyName() {
		if (parameter == null) {
			return parameterName;
		}
		return parameter.getName();
	}

	@Override
	public Object newValue() {
		return newValue;
	}

	@Override
	public boolean isReentrant() {
		return false;
	}

	public boolean isModelNotification() {
		if (parameter == null) {
			return false;
		}
		/*return !parameter.equals(GraphicalRepresentation.Parameters.isSelected)
				&& !parameter.equals(GraphicalRepresentation.Parameters.isFocused);*/
		return true;
	}
}