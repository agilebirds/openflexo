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
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;

public class FIBButtonColumn extends FIBTableColumn {

	@Deprecated
	public static BindingDefinition BUTTON_ICON = new BindingDefinition("buttonIcon", Icon.class, DataBinding.BindingDefinitionType.GET,
			false);
	@Deprecated
	public static BindingDefinition ACTION = new BindingDefinition("action", Object.class, DataBinding.BindingDefinitionType.EXECUTE, false);
	private static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, BindingDefinitionType.GET, false);

	public static enum Parameters implements FIBModelAttribute {
		action, enabled;
	}

	private DataBinding<Object> action;
	private DataBinding<Boolean> enabled;

	// private DataBinding buttonIcon;

	public DataBinding<Object> getAction() {
		if (action == null) {
			action = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return action;
	}

	public void setAction(DataBinding<Object> action) {
		if (action != null) {
			action.setOwner(this);
			action.setDeclaredType(Void.TYPE);
			action.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.action = action;
	}

	public BindingDefinition getActionBindingDefinition() {
		return ACTION;
	}

	public DataBinding getEnabled() {
		if (enabled == null) {
			enabled = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return enabled;
	}

	public void setEnabled(DataBinding enabled) {
		if (enabled != null) {
			enabled.setOwner(this);
			enabled.setDeclaredType(Boolean.class);
			enabled.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
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