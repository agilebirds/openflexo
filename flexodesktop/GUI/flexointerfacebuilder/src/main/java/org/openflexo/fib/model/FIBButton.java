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

public class FIBButton extends FIBWidget {

	public static BindingDefinition BUTTON_ICON = new BindingDefinition("buttonIcon", Icon.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ACTION = new BindingDefinition("action", Void.class, BindingDefinitionType.EXECUTE, false);

	public static enum ButtonType {
		Trigger, Toggle
	}

	public static enum Parameters implements FIBModelAttribute {
		action, buttonType, label, isDefault, buttonIcon;
	}

	private DataBinding action;
	private ButtonType buttonType = ButtonType.Trigger;
	private String label;
	private Boolean isDefault;
	private DataBinding buttonIcon;

	public FIBButton() {
	}

	@Override
	protected String getBaseName() {
		return "Button";
	}

	@Override
	public String getIdentifier() {
		return getLabel();
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

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

	public ButtonType getButtonType() {
		return buttonType;
	}

	public void setButtonType(ButtonType buttonType) {
		FIBAttributeNotification<ButtonType> notification = requireChange(Parameters.buttonType, buttonType);
		if (notification != null) {
			this.buttonType = buttonType;
			hasChanged(notification);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.label, label);
		if (notification != null) {
			this.label = label;
			hasChanged(notification);
		}
	}

	public Boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.isDefault, isDefault);
		if (notification != null) {
			this.isDefault = isDefault;
			hasChanged(notification);
		}
	}

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

}
