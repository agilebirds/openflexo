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
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBButton.FIBButtonImpl.class)
@XMLElement(xmlTag = "Button")
public interface FIBButton extends FIBWidget {

	public static enum ButtonType {
		Trigger, Toggle
	}

	@PropertyIdentifier(type = DataBinding.class)
	public static final String ACTION_KEY = "action";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String BUTTON_ICON_KEY = "buttonIcon";
	@PropertyIdentifier(type = ButtonType.class)
	public static final String BUTTON_TYPE_KEY = "buttonType";
	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_DEFAULT_KEY = "isDefault";

	@Getter(value = ACTION_KEY)
	@XMLAttribute
	public DataBinding<Object> getAction();

	@Setter(ACTION_KEY)
	public void setAction(DataBinding<Object> action);

	@Getter(value = BUTTON_ICON_KEY)
	@XMLAttribute
	public DataBinding<Icon> getButtonIcon();

	@Setter(BUTTON_ICON_KEY)
	public void setButtonIcon(DataBinding<Icon> buttonIcon);

	@Getter(value = BUTTON_TYPE_KEY)
	@XMLAttribute
	public ButtonType getButtonType();

	@Setter(BUTTON_TYPE_KEY)
	public void setButtonType(ButtonType buttonType);

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = IS_DEFAULT_KEY)
	@XMLAttribute(xmlTag = "default")
	public Boolean isDefault();

	@Setter(IS_DEFAULT_KEY)
	public void setIsDefault(Boolean isDefault);

	public static abstract class FIBButtonImpl extends FIBWidgetImpl implements FIBButton {

		@Deprecated
		public static BindingDefinition BUTTON_ICON = new BindingDefinition("buttonIcon", Icon.class,
				DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition ACTION = new BindingDefinition("action", Object.class, DataBinding.BindingDefinitionType.EXECUTE,
				false);

		private DataBinding<Object> action;
		private ButtonType buttonType = ButtonType.Trigger;
		private String label;
		private Boolean isDefault;
		private DataBinding<Icon> buttonIcon;

		public FIBButtonImpl() {
		}

		@Override
		public String getBaseName() {
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

		@Override
		public DataBinding<Object> getAction() {
			if (action == null) {
				action = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
			}
			return action;
		}

		@Override
		public void setAction(DataBinding<Object> action) {
			if (action != null) {
				action.setOwner(this);
				action.setDeclaredType(Void.TYPE);
				action.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
			}
			this.action = action;
		}

		@Override
		public ButtonType getButtonType() {
			return buttonType;
		}

		@Override
		public void setButtonType(ButtonType buttonType) {
			FIBPropertyNotification<ButtonType> notification = requireChange(BUTTON_TYPE_KEY, buttonType);
			if (notification != null) {
				this.buttonType = buttonType;
				hasChanged(notification);
			}
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			FIBPropertyNotification<String> notification = requireChange(LABEL_KEY, label);
			if (notification != null) {
				this.label = label;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean isDefault() {
			return isDefault;
		}

		@Override
		public void setIsDefault(Boolean isDefault) {
			FIBPropertyNotification<Boolean> notification = requireChange(IS_DEFAULT_KEY, isDefault);
			if (notification != null) {
				this.isDefault = isDefault;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<Icon> getButtonIcon() {
			if (buttonIcon == null) {
				buttonIcon = new DataBinding<Icon>(this, Icon.class, DataBinding.BindingDefinitionType.GET);
			}
			return buttonIcon;
		}

		@Override
		public void setButtonIcon(DataBinding<Icon> buttonIcon) {
			if (buttonIcon != null) {
				buttonIcon.setOwner(this);
				buttonIcon.setDeclaredType(Icon.class);
				buttonIcon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.buttonIcon = buttonIcon;
		}

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(ActionBindingMustBeValid.class, report);
		}

		public static class ActionBindingMustBeValid extends BindingMustBeValid<FIBButton> {
			public ActionBindingMustBeValid() {
				super("'action'_binding_is_not_valid", FIBButton.class);
			}

			@Override
			public DataBinding getBinding(FIBButton object) {
				return object.getAction();
			}

		}

	}
}
