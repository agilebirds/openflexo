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
package org.openflexo.fib.model;

import java.lang.reflect.Type;

import javax.swing.Icon;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;

public class FIBButtonColumn extends FIBTableColumn {

	public static BindingDefinition BUTTON_ICON = new BindingDefinition("buttonIcon", Icon.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ACTION = new BindingDefinition("action", Object.class, BindingDefinitionType.EXECUTE, false);
	private static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, BindingDefinitionType.GET, false);

	public static enum Parameters implements FIBModelAttribute {
		action, enabled;
	}

	private DataBinding action;
	private DataBinding enabled;

	// private DataBinding buttonIcon;

	public DataBinding getAction() {
		if (action == null) {
			action = new DataBinding(this, Parameters.action, ACTION);
		}
		return action;
	}

	public void setAction(DataBinding action) {
		action.setOwner(this);
		action.setBindingAttribute(Parameters.action);
		action.setBindingDefinition(ACTION);
		this.action = action;
	}

	public BindingDefinition getActionBindingDefinition() {
		return ACTION;
	}

	public DataBinding getEnabled() {
		if (enabled == null) {
			enabled = new DataBinding(this, Parameters.enabled, ENABLED);
		}
		return enabled;
	}

	public void setEnabled(DataBinding enabled) {
		enabled.setOwner(this);
		enabled.setBindingAttribute(Parameters.enabled);
		enabled.setBindingDefinition(ENABLED);
		this.enabled = enabled;
	}

	public BindingDefinition getEnabledBindingDefinition() {
		return ENABLED;
	}

	/*
	public DataBinding getButtonIcon() {
		if (buttonIcon == null) {
			buttonIcon = new DataBinding(this, Parameters.buttonIcon, BUTTON_ICON);
		}
		return buttonIcon;
	}

	public void setButtonIcon(DataBinding buttonIcon) {
		if (buttonIcon != null) {
			buttonIcon.setOwner(this);
			buttonIcon.setBindingAttribute(Parameters.buttonIcon);
			buttonIcon.setBindingDefinition(BUTTON_ICON);
		}
		this.buttonIcon = buttonIcon;
	}
	*/
	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

	@Override
	public ColumnType getColumnType() {
		return ColumnType.Button;
	}

}
