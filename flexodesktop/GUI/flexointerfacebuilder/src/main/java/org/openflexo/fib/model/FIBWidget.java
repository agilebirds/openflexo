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

import java.awt.event.MouseEvent;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.model.validation.ValidationWarning;
import org.openflexo.toolbox.StringUtils;

public abstract class FIBWidget extends FIBComponent {

	@Deprecated
	public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ENABLE = new BindingDefinition("enable", Boolean.class, BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition FORMAT = new BindingDefinition("format", String.class, BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
			BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition CLICK_ACTION = new BindingDefinition("clickAction", Void.class, BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition DOUBLE_CLICK_ACTION = new BindingDefinition("doubleClickAction", Void.class,
			BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition RIGHT_CLICK_ACTION = new BindingDefinition("rightClickAction", Void.class,
			BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition ENTER_PRESSED_ACTION = new BindingDefinition("enterPressedAction", Void.class,
			BindingDefinitionType.EXECUTE, false);

	public static enum Parameters implements FIBModelAttribute {
		enable,
		format,
		icon,
		tooltip,
		tooltipText,
		localize,
		manageDynamicModel,
		readOnly,
		clickAction,
		doubleClickAction,
		rightClickAction,
		enterPressedAction,
		valueChangedAction
	}

	private DataBinding<String> tooltip;

	private DataBinding<Boolean> enable;
	private DataBinding<String> format;
	private DataBinding<Icon> icon;

	private Boolean manageDynamicModel = false;
	private Boolean readOnly = false;
	private Boolean localize = true;
	private String tooltipText;
	private DataBinding<Void> clickAction;
	private DataBinding<Void> doubleClickAction;
	private DataBinding<Void> rightClickAction;
	private DataBinding<Void> enterPressedAction;
	private DataBinding<Void> valueChangedAction;

	private final FIBFormatter formatter;
	private final FIBEventListener eventListener;

	public FIBWidget() {
		super();
		formatter = new FIBFormatter();
		eventListener = new FIBEventListener();
	}

	@Override
	public String getIdentifier() {
		return null;
	}

	@Override
	public Enumeration children() {
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public DataBinding<String> getTooltip() {
		if (tooltip == null) {
			tooltip = new DataBinding<String>(this, String.class, BindingDefinitionType.GET);
		}
		return tooltip;
	}

	public void setTooltip(DataBinding<String> tooltip) {
		if (tooltip != null) {
			tooltip.setOwner(this);
			tooltip.setDeclaredType(String.class);
			tooltip.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.tooltip = tooltip;
	}

	public DataBinding<Boolean> getEnable() {
		if (enable == null) {
			enable = new DataBinding<Boolean>(this, Boolean.class, BindingDefinitionType.GET);
		}
		return enable;
	}

	public void setEnable(DataBinding<Boolean> enable) {
		if (enable != null) {
			enable.setOwner(this);
			enable.setDeclaredType(Boolean.class);
			enable.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.enable = enable;
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		getEventListener().createEventListenerBindingModel();
		if (enable != null) {
			enable.decode();
		}
		if (format != null) {
			format.decode();
		}
		if (icon != null) {
			icon.decode();
		}
		if (tooltip != null) {
			tooltip.decode();
		}
		if (clickAction != null) {
			clickAction.decode();
		}
		if (doubleClickAction != null) {
			doubleClickAction.decode();
		}
		if (rightClickAction != null) {
			rightClickAction.decode();
		}
		if (enterPressedAction != null) {
			enterPressedAction.decode();
		}
		if (valueChangedAction != null) {
			valueChangedAction.decode();
		}
	}

	@Override
	public Type getDataType() {
		if (getData() != null) {
			return getData().getAnalyzedType();
		}
		return getDefaultDataClass();

	}

	@Override
	public abstract Type getDefaultDataClass();

	// Default behaviour: only data is managed
	@Override
	public Type getDynamicAccessType() {
		if (getManageDynamicModel()) {
			if (getData() != null && getData().isSet()) {
				return super.getDynamicAccessType();
			} else {
				Type[] args = new Type[1];
				args[0] = getDataType();
				return new ParameterizedTypeImpl(FIBComponentDynamicModel.class, args);
			}
		}
		return null;
	}

	public Boolean getManageDynamicModel() {
		return manageDynamicModel;
	}

	public void setManageDynamicModel(Boolean manageDynamicModel) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.manageDynamicModel, manageDynamicModel);
		if (notification != null) {
			this.manageDynamicModel = manageDynamicModel;
			updateBindingModel();
			hasChanged(notification);
		}
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.readOnly, readOnly);
		if (notification != null) {
			this.readOnly = readOnly;
			hasChanged(notification);
		}
	}

	public String getTooltipText() {
		return tooltipText;
	}

	public void setTooltipText(String tooltipText) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.tooltipText, tooltipText);
		if (notification != null) {
			this.tooltipText = tooltipText;
			hasChanged(notification);
		}
	}

	public DataBinding<String> getFormat() {
		if (format == null) {
			format = new DataBinding<String>(this, String.class, BindingDefinitionType.GET);
		}
		return format;
	}

	public void setFormat(DataBinding<String> format) {
		FIBAttributeNotification<DataBinding<String>> notification = requireChange(Parameters.format, format);
		if (notification != null) {
			if (format != null) {
				format.setOwner(formatter);
				format.setDeclaredType(String.class);
				format.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.format = format;
			hasChanged(notification);
		}
	}

	public DataBinding<Icon> getIcon() {
		if (icon == null) {
			icon = new DataBinding<Icon>(formatter, Icon.class, BindingDefinitionType.GET);
		}
		return icon;
	}

	public void setIcon(DataBinding<Icon> icon) {
		FIBAttributeNotification<DataBinding<Icon>> notification = requireChange(Parameters.icon, icon);
		if (notification != null) {
			if (icon != null) {
				icon.setOwner(formatter);
				icon.setDeclaredType(Icon.class);
				icon.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.icon = icon;
			hasChanged(notification);
		}
	}

	public Boolean getLocalize() {
		return localize;
	}

	public void setLocalize(Boolean localize) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.localize, localize);
		if (notification != null) {
			this.localize = localize;
			hasChanged(notification);
		}
	}

	public FIBFormatter getFormatter() {
		return formatter;
	}

	public Type getFormattedObjectType() {
		return getDataType();
	}

	private class FIBFormatter extends FIBModelObject implements Bindable {
		private BindingModel formatterBindingModel = null;

		@Override
		public BindingModel getBindingModel() {
			if (formatterBindingModel == null) {
				createFormatterBindingModel();
			}
			return formatterBindingModel;
		}

		private void createFormatterBindingModel() {
			formatterBindingModel = new BindingModel(FIBWidget.this.getBindingModel());
			formatterBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
				@Override
				public Type getType() {
					return getFormattedObjectType();
				}
			});
		}

		@Override
		public FIBComponent getRootComponent() {
			return FIBWidget.this.getRootComponent();
		}

		@Override
		public String toString() {
			if (FIBWidget.this instanceof FIBDropDown) {
				return "FIBFormatter[" + FIBWidget.this + "] iteratorClass=" + ((FIBDropDown) FIBWidget.this).getIteratorClass()
						+ " dataType=" + ((FIBDropDown) FIBWidget.this).getDataType() + " obtained from "
						+ ((FIBDropDown) FIBWidget.this).getDescription();
			}
			return "FIBFormatter[" + FIBWidget.this + "]" + " dataType=" + FIBWidget.this.getDataType();
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			if (binding == getFormat()) {
				FIBWidget.this.notifiedBindingChanged(binding);
			}
			super.notifiedBindingChanged(binding);
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}
	}

	@Override
	public void updateBindingModel() {
		super.updateBindingModel();
		getEventListener().createEventListenerBindingModel();
	}

	public FIBEventListener getEventListener() {
		return eventListener;
	}

	private class FIBEventListener extends FIBModelObject implements Bindable {
		private BindingModel eventListenerBindingModel = null;

		@Override
		public BindingModel getBindingModel() {
			if (eventListenerBindingModel == null) {
				createEventListenerBindingModel();
			}
			return eventListenerBindingModel;
		}

		private void createEventListenerBindingModel() {
			eventListenerBindingModel = new BindingModel(FIBWidget.this.getBindingModel());
			eventListenerBindingModel.addToBindingVariables(new BindingVariable("event", MouseEvent.class));
		}

		@Override
		public FIBComponent getRootComponent() {
			return FIBWidget.this.getRootComponent();
		}

		@Override
		public String toString() {
			return "FIBEventListener[" + FIBWidget.this + "]";
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			if (binding == getClickAction() || binding == getDoubleClickAction() || binding == getRightClickAction()) {
				FIBWidget.this.notifiedBindingChanged(binding);
			}
			super.notifiedBindingChanged(binding);
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}
	}

	public DataBinding<Void> getValueChangedAction() {
		if (valueChangedAction == null) {
			valueChangedAction = new DataBinding<Void>(this, Void.class, BindingDefinitionType.EXECUTE);
		}
		return valueChangedAction;
	}

	public void setValueChangedAction(DataBinding<Void> valueChangedAction) {
		if (valueChangedAction != null) {
			valueChangedAction.setOwner(this);
			valueChangedAction.setDeclaredType(Void.class);
			valueChangedAction.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.valueChangedAction = valueChangedAction;
	}

	public boolean hasClickAction() {
		return clickAction != null && clickAction.isValid();
	}

	public final DataBinding<Void> getClickAction() {
		if (clickAction == null) {
			clickAction = new DataBinding<Void>(eventListener, Void.class, BindingDefinitionType.EXECUTE);
		}
		return clickAction;
	}

	public final void setClickAction(DataBinding<Void> clickAction) {
		if (clickAction != null) {
			clickAction.setOwner(eventListener);
			clickAction.setDeclaredType(Void.class);
			clickAction.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.clickAction = clickAction;
	}

	public boolean hasDoubleClickAction() {
		return doubleClickAction != null && doubleClickAction.isValid();
	}

	public DataBinding<Void> getDoubleClickAction() {
		if (doubleClickAction == null) {
			doubleClickAction = new DataBinding<Void>(eventListener, Void.class, BindingDefinitionType.EXECUTE);
		}
		return doubleClickAction;
	}

	public void setDoubleClickAction(DataBinding<Void> doubleClickAction) {
		if (doubleClickAction != null) {
			doubleClickAction.setOwner(eventListener);
			doubleClickAction.setDeclaredType(Void.class);
			doubleClickAction.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.doubleClickAction = doubleClickAction;
	}

	public boolean hasRightClickAction() {
		return rightClickAction != null && rightClickAction.isValid();
	}

	public DataBinding<Void> getRightClickAction() {
		if (rightClickAction == null) {
			rightClickAction = new DataBinding<Void>(eventListener, Void.class, BindingDefinitionType.EXECUTE);
		}
		return rightClickAction;
	}

	public void setRightClickAction(DataBinding<Void> rightClickAction) {
		if (rightClickAction != null) {
			rightClickAction.setOwner(eventListener);
			rightClickAction.setDeclaredType(Void.class);
			rightClickAction.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.rightClickAction = rightClickAction;
	}

	public boolean hasEnterPressedAction() {
		return enterPressedAction != null && enterPressedAction.isValid();
	}

	public DataBinding<Void> getEnterPressedAction() {
		if (enterPressedAction == null) {
			enterPressedAction = new DataBinding<Void>(eventListener, Void.class, BindingDefinitionType.EXECUTE);
		}
		return enterPressedAction;
	}

	public void setEnterPressedAction(DataBinding<Void> enterPressedAction) {
		if (enterPressedAction != null) {
			enterPressedAction.setOwner(eventListener);
			enterPressedAction.setDeclaredType(Void.class);
			enterPressedAction.setBindingDefinitionType(BindingDefinitionType.EXECUTE);
		}
		this.enterPressedAction = enterPressedAction;
	}

	public boolean isPaletteElement() {
		return getParameter("isPaletteElement") != null && getParameter("isPaletteElement").equalsIgnoreCase("true");
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

	@Override
	protected void applyValidation(ValidationReport report) {
		super.applyValidation(report);
		performValidation(FIBWidgetDeclaredAsDynamicShouldHaveAName.class, report);
		performValidation(TooltipBindingMustBeValid.class, report);
		performValidation(EnableBindingMustBeValid.class, report);
		performValidation(FormatBindingMustBeValid.class, report);
		performValidation(IconBindingMustBeValid.class, report);
		performValidation(ClickActionBindingMustBeValid.class, report);
		performValidation(DoubleClickActionBindingMustBeValid.class, report);
		performValidation(RightClickActionBindingMustBeValid.class, report);
		performValidation(ValueChangeActionBindingMustBeValid.class, report);
	}

	public static class FIBWidgetDeclaredAsDynamicShouldHaveAName extends
			ValidationRule<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {
		public FIBWidgetDeclaredAsDynamicShouldHaveAName() {
			super(FIBWidget.class, "widgets_declaring_managing_dynamic_model_should_have_a_name");
		}

		@Override
		public ValidationIssue<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> applyValidation(FIBWidget object) {
			if (object.getManageDynamicModel() && StringUtils.isEmpty(object.getName())) {
				GenerateDefaultName fixProposal1 = new GenerateDefaultName();
				DisableDynamicModelManagement fixProposal2 = new DisableDynamicModelManagement();
				return new ValidationWarning<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget>(this, object,
						"widget_($object.toString)_declares_managing_dynamic_model_but_does_not_have_a_name", fixProposal1, fixProposal2);
			}
			return null;
		}

		protected static class GenerateDefaultName extends FixProposal<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {

			public GenerateDefaultName() {
				super("generate_default_name_:_($defaultName)");
			}

			@Override
			protected void fixAction() {
				getObject().setName(getDefaultName());
			}

			public String getDefaultName() {
				return getObject().generateUniqueName(getObject().getBaseName());
			}

		}

		protected static class DisableDynamicModelManagement extends FixProposal<FIBWidgetDeclaredAsDynamicShouldHaveAName, FIBWidget> {

			public DisableDynamicModelManagement() {
				super("disable_dynamic_model_management");
			}

			@Override
			protected void fixAction() {
				getObject().setManageDynamicModel(false);
			}

		}
	}

	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getTooltip();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return TOOLTIP;
		}
	}

	public static class EnableBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public EnableBindingMustBeValid() {
			super("'enable'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getEnable();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return ENABLE;
		}
	}

	public static class FormatBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public FormatBindingMustBeValid() {
			super("'format'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getFormat();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return FORMAT;
		}
	}

	public static class IconBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public IconBindingMustBeValid() {
			super("'icon'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getIcon();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return ICON;
		}
	}

	public static class ClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public ClickActionBindingMustBeValid() {
			super("'click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getClickAction();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return CLICK_ACTION;
		}
	}

	public static class DoubleClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public DoubleClickActionBindingMustBeValid() {
			super("'double_click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getDoubleClickAction();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return DOUBLE_CLICK_ACTION;
		}
	}

	public static class RightClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public RightClickActionBindingMustBeValid() {
			super("'right_click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getRightClickAction();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return RIGHT_CLICK_ACTION;
		}
	}

	public static class ValueChangeActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public ValueChangeActionBindingMustBeValid() {
			super("'value_change_acion'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getValueChangedAction();
		}

		@Override
		public BindingDefinition getBindingDefinition(FIBWidget object) {
			return VALUE_CHANGED_ACTION;
		}
	}

}
