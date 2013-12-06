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
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.model.validation.FixProposal;
import org.openflexo.fib.model.validation.ValidationIssue;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.model.validation.ValidationRule;
import org.openflexo.fib.model.validation.ValidationWarning;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.toolbox.StringUtils;

public abstract class FIBWidget extends FIBComponent {

	@Deprecated
	public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ENABLE = new BindingDefinition("enable", Boolean.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition FORMAT = new BindingDefinition("format", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition CLICK_ACTION = new BindingDefinition("clickAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static BindingDefinition DOUBLE_CLICK_ACTION = new BindingDefinition("doubleClickAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static final BindingDefinition RIGHT_CLICK_ACTION = new BindingDefinition("rightClickAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);
	@Deprecated
	public static final BindingDefinition ENTER_PRESSED_ACTION = new BindingDefinition("enterPressedAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);

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
	private DataBinding<?> clickAction;
	private DataBinding<?> doubleClickAction;
	private DataBinding<?> rightClickAction;
	private DataBinding<?> enterPressedAction;
	private DataBinding<?> valueChangedAction;
	private DataBinding<Boolean> valueValidator;

	private final FIBFormatter formatter;
	private final FIBValueBindable valueBindable;
	private final FIBEventListener eventListener;
	private DataBinding<Object> valueTransform;

	public FIBWidget() {
		super();
		formatter = new FIBFormatter();
		valueBindable = new FIBValueBindable();
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
		return -1;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public DataBinding<String> getTooltip() {
		if (tooltip == null) {
			tooltip = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
		}
		return tooltip;
	}

	public void setTooltip(DataBinding<String> tooltip) {
		if (tooltip != null) {
			tooltip.setOwner(this);
			tooltip.setDeclaredType(String.class);
			tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.tooltip = tooltip;
	}

	public DataBinding<Boolean> getEnable() {
		if (enable == null) {
			enable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return enable;
	}

	public void setEnable(DataBinding<Boolean> enable) {
		if (enable != null) {
			enable.setOwner(this);
			enable.setDeclaredType(Boolean.class);
			enable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
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
		if (valueTransform != null) {
			valueTransform.decode();
		}
		if (valueValidator != null) {
			valueValidator.decode();
		}
	}

	@Override
	public Type getDataType() {
		/*if (getData() != null && getData().isSet()) {
			return getData().getAnalyzedType();
		}*/
		return getDefaultDataClass();

	}

	@Override
	public abstract Type getDefaultDataClass();

	@Override
	public Type getDynamicAccessType() {
		if (getManageDynamicModel()) {
			if (getData() != null && getData().isSet()) {
				return super.getDynamicAccessType();
			} else {
				Type[] args = new Type[1];
				args[0] = getDataType();
				return new ParameterizedTypeImpl(FIBWidgetView.class, args);
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

	public DataBinding<Object> getValueTransform() {
		if (valueTransform == null) {
			valueTransform = new DataBinding<Object>(valueBindable, getDataType(), BindingDefinitionType.GET) {
				@Override
				public Type getDeclaredType() {
					return getDataType();
				}
			};
		}
		return valueTransform;
	}

	public void setValueTransform(DataBinding<Object> valueTransform) {
		if (valueTransform != null) {
			this.valueTransform = new DataBinding<Object>(valueTransform.toString(), this, valueTransform.getDeclaredType(),
					valueTransform.getBindingDefinitionType()) {
				@Override
				public Type getDeclaredType() {
					return getDataType();
				}
			};
		} else {
			this.valueTransform = null;
		}
	}

	public FIBValueBindable getValueBindable() {
		return valueBindable;
	}

	public DataBinding<String> getFormat() {
		if (format == null) {
			format = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
		}
		return format;
	}

	public void setFormat(DataBinding<String> format) {
		FIBAttributeNotification<DataBinding<String>> notification = requireChange(Parameters.format, format);
		if (notification != null) {
			if (format != null) {
				format.setOwner(formatter);
				format.setDeclaredType(String.class);
				format.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.format = format;
			hasChanged(notification);
		}
	}

	public DataBinding<Icon> getIcon() {
		if (icon == null) {
			icon = new DataBinding<Icon>(formatter, Icon.class, DataBinding.BindingDefinitionType.GET);
		}
		return icon;
	}

	public void setIcon(DataBinding<Icon> icon) {
		FIBAttributeNotification<DataBinding<Icon>> notification = requireChange(Parameters.icon, icon);
		if (notification != null) {
			if (icon != null) {
				icon.setOwner(formatter);
				icon.setDeclaredType(Icon.class);
				icon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
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

	@Override
	public void notifiedBindingModelRecreated() {
		super.notifiedBindingModelRecreated();
		if (getFormatter() != null) {
			getFormatter().notifiedBindingModelRecreated();
		}
	}

	private class FIBFormatter extends FIBModelObject implements Bindable {
		private BindingModel formatterBindingModel = null;

		public void notifiedBindingModelRecreated() {
			createFormatterBindingModel();
		}

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
		public FIBComponent getComponent() {
			return FIBWidget.this;
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
			super.notifiedBindingChanged(binding);
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

	private class FIBValueBindable extends FIBModelObject implements Bindable {
		private BindingModel valueTransformerBindingModel = null;

		@Override
		public BindingModel getBindingModel() {
			if (valueTransformerBindingModel == null) {
				createValueTransformerBindingModel();
			}
			return valueTransformerBindingModel;
		}

		private void createValueTransformerBindingModel() {
			valueTransformerBindingModel = new BindingModel(FIBWidget.this.getBindingModel());
			valueTransformerBindingModel.addToBindingVariables(new BindingVariable("value", Object.class) {
				@Override
				public Type getType() {
					return getDataType();
				}
			});
		}

		@Override
		public FIBComponent getComponent() {
			return FIBWidget.this;
		}

		@Override
		public String toString() {
			if (FIBWidget.this instanceof FIBDropDown) {
				return "FIBValueBindable[" + FIBWidget.this + "] iteratorClass=" + ((FIBDropDown) FIBWidget.this).getIteratorClass()
						+ " dataType=" + ((FIBDropDown) FIBWidget.this).getDataType() + " obtained from "
						+ ((FIBDropDown) FIBWidget.this).getDescription();
			}
			return "FIBValueBindable[" + FIBWidget.this + "]" + " dataType=" + FIBWidget.this.getDataType();
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			if (binding == getValueTransform()) {
				FIBWidget.this.notifiedBindingChanged(binding);
			} else if (binding == getValueValidator()) {
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
		if (deserializationPerformed) {
			getEventListener().createEventListenerBindingModel();
			getFormatter().createFormatterBindingModel();
		}
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
		public FIBComponent getComponent() {
			return FIBWidget.this;
		}

		@Override
		public String toString() {
			return "FIBEventListener[" + FIBWidget.this + "]";
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> binding) {
			super.notifiedBindingChanged(binding);
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

	public DataBinding<Boolean> getValueValidator() {
		if (valueValidator == null) {
			valueValidator = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return valueValidator;
	}

	public void setValueValidator(DataBinding<Boolean> valueValidator) {
		if (valueValidator != null) {
			valueValidator.setOwner(this);
			valueValidator.setDeclaredType(Boolean.class);
			valueValidator.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.valueValidator = valueValidator;
	}

	public DataBinding<?> getValueChangedAction() {
		if (valueChangedAction == null) {
			valueChangedAction = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return valueChangedAction;
	}

	public void setValueChangedAction(DataBinding<?> valueChangedAction) {
		if (valueChangedAction != null) {
			valueChangedAction.setOwner(this);
			valueChangedAction.setDeclaredType(Object.class);
			valueChangedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.valueChangedAction = valueChangedAction;
	}

	public boolean hasClickAction() {
		return clickAction != null && clickAction.isValid();
	}

	public final DataBinding<?> getClickAction() {
		if (clickAction == null) {
			clickAction = new DataBinding<Object>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return clickAction;
	}

	public final void setClickAction(DataBinding<?> clickAction) {
		if (clickAction != null) {
			clickAction.setOwner(eventListener);
			clickAction.setDeclaredType(Object.class);
			clickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.clickAction = clickAction;
	}

	public boolean hasDoubleClickAction() {
		return doubleClickAction != null && doubleClickAction.isValid();
	}

	public DataBinding<?> getDoubleClickAction() {
		if (doubleClickAction == null) {
			doubleClickAction = new DataBinding<Object>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return doubleClickAction;
	}

	public void setDoubleClickAction(DataBinding<?> doubleClickAction) {
		if (doubleClickAction != null) {
			doubleClickAction.setOwner(eventListener);
			doubleClickAction.setDeclaredType(Object.class);
			doubleClickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.doubleClickAction = doubleClickAction;
	}

	public boolean hasRightClickAction() {
		return rightClickAction != null && rightClickAction.isValid();
	}

	public DataBinding<?> getRightClickAction() {
		if (rightClickAction == null) {
			rightClickAction = new DataBinding<Object>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return rightClickAction;
	}

	public void setRightClickAction(DataBinding<?> rightClickAction) {
		if (rightClickAction != null) {
			rightClickAction.setOwner(eventListener);
			rightClickAction.setDeclaredType(Object.class);
			rightClickAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.rightClickAction = rightClickAction;
	}

	public boolean hasEnterPressedAction() {
		return enterPressedAction != null && enterPressedAction.isValid();
	}

	public DataBinding<?> getEnterPressedAction() {
		if (enterPressedAction == null) {
			enterPressedAction = new DataBinding<Object>(eventListener, Object.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return enterPressedAction;
	}

	public void setEnterPressedAction(DataBinding<?> enterPressedAction) {
		if (enterPressedAction != null) {
			enterPressedAction.setOwner(eventListener);
			enterPressedAction.setDeclaredType(Object.class);
			enterPressedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.enterPressedAction = enterPressedAction;
	}

	public boolean isPaletteElement() {
		return getParameter("isPaletteElement") != null && getParameter("isPaletteElement").equalsIgnoreCase("true");
	}

	@Override
	public Collection<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

	/**
	 * Return a list of all bindings declared in the context of this component
	 * 
	 * @return
	 */
	public List<DataBinding<?>> getDeclaredBindings() {
		List<DataBinding<?>> returned = super.getDeclaredBindings();
		returned.add(getEnable());
		return returned;
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

	}

	public static class EnableBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public EnableBindingMustBeValid() {
			super("'enable'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getEnable();
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

	}

	public static class IconBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public IconBindingMustBeValid() {
			super("'icon'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getIcon();
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

	}

	public static class DoubleClickActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public DoubleClickActionBindingMustBeValid() {
			super("'double_click_action'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getDoubleClickAction();
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

	}

	public static class ValueChangeActionBindingMustBeValid extends BindingMustBeValid<FIBWidget> {
		public ValueChangeActionBindingMustBeValid() {
			super("'value_change_acion'_binding_is_not_valid", FIBWidget.class);
		}

		@Override
		public DataBinding getBinding(FIBWidget object) {
			return object.getValueChangedAction();
		}

	}

}
