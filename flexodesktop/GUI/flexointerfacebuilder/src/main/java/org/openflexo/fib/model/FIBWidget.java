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
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBComponentDynamicModel;


public abstract class FIBWidget extends FIBComponent {

	public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ENABLE = new BindingDefinition("enable", Boolean.class, BindingDefinitionType.GET, false);
	public static BindingDefinition FORMAT = new BindingDefinition("format", String.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, BindingDefinitionType.GET, false);
	public static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class, BindingDefinitionType.EXECUTE, false);
	public static BindingDefinition CLICK_ACTION = new BindingDefinition("clickAction", Void.class, BindingDefinitionType.EXECUTE, false);
	public static BindingDefinition DOUBLE_CLICK_ACTION = new BindingDefinition("doubleClickAction", Void.class, BindingDefinitionType.EXECUTE, false);

	public static enum Parameters implements FIBModelAttribute
	{
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
		valueChangedAction
	}


	private DataBinding tooltip;

	private DataBinding enable;
	private DataBinding format;
	private DataBinding icon;
	
	private Boolean manageDynamicModel = false;
	private Boolean readOnly = false;
	private Boolean localize = true;
	private String tooltipText;
	private DataBinding clickAction;
	private DataBinding doubleClickAction;
	private DataBinding valueChangedAction;

	private final FIBFormatter formatter;
	
	public FIBWidget() 
	{
		super();
		formatter = new FIBFormatter();
		FIBLibrary.instance().getBindingFactory().setBindable(this);
	}
	
	@Override
	public String getIdentifier()
	{
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

	public DataBinding getTooltip() 
	{
		if (tooltip == null) tooltip = new DataBinding(this,Parameters.tooltip,TOOLTIP);
		return tooltip;
	}

	public void setTooltip(DataBinding tooltip) 
	{
		tooltip.setOwner(this);
		tooltip.setBindingAttribute(Parameters.tooltip);
		tooltip.setBindingDefinition(TOOLTIP);
		this.tooltip = tooltip;
	}
	
	public DataBinding getEnable() 
	{
		if (enable == null) enable = new DataBinding(this,Parameters.enable,ENABLE);
		return enable;
	}

	public void setEnable(DataBinding enable) 
	{
		enable.setOwner(this);
		enable.setBindingAttribute(Parameters.enable);
		enable.setBindingDefinition(ENABLE);
		this.enable = enable;
	}
	


	@Override
	public void finalizeDeserialization() 
	{
		super.finalizeDeserialization();
		if (enable != null) enable.finalizeDeserialization();
		if (format != null) format.finalizeDeserialization();
		if (icon != null) icon.finalizeDeserialization();
		if (tooltip != null) tooltip.finalizeDeserialization();
		if (valueChangedAction != null) valueChangedAction.finalizeDeserialization();
	}
	
	@Override
	public Type getDataType()
	{
		if (getData() != null && getData().getBinding() != null) 
			return getData().getBinding().getAccessedType();
		return getDefaultDataClass();

	}
	
	@Override
	public abstract Type getDefaultDataClass();

	// Default behaviour: only data is managed
	@Override
	public Type getDynamicAccessType()
	{
		if (getManageDynamicModel()) {
			if (getData() != null && getData().isSet()) return super.getDynamicAccessType();
			else {
				Type[] args = new Type[1];
				args[0] = getDataType();
				return new ParameterizedTypeImpl(FIBComponentDynamicModel.class, args);
			}
		}
		return null;
	}

	public Boolean getManageDynamicModel()
	{
		return manageDynamicModel;
	}

	public void setManageDynamicModel(Boolean manageDynamicModel)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.manageDynamicModel, manageDynamicModel);
		if (notification != null) {
			this.manageDynamicModel = manageDynamicModel;
			updateBindingModel();
			hasChanged(notification);
		}
	}

	public Boolean getReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.readOnly, readOnly);
		if (notification != null) {
			this.readOnly = readOnly;
			hasChanged(notification);
		}
	}
	
	public String getTooltipText()
	{
		return tooltipText;
	}

	public void setTooltipText(String tooltipText)
	{
		FIBAttributeNotification<String> notification = requireChange(
				Parameters.tooltipText, tooltipText);
		if (notification != null) {
			this.tooltipText = tooltipText;
			hasChanged(notification);
		}
	}
	
	public DataBinding getFormat() 
	{
		if (format == null) format = new DataBinding(formatter,Parameters.format,FORMAT);
		return format;
	}

	public void setFormat(DataBinding format) 
	{
		FIBAttributeNotification<DataBinding> notification = requireChange(
				Parameters.format, format);
		if (notification != null) {
			format.setOwner(formatter);
			format.setBindingAttribute(Parameters.format);
			format.setBindingDefinition(FORMAT);
			this.format = format;
			hasChanged(notification);
		}
	}

	public DataBinding getIcon()
	{
		if (icon == null) icon = new DataBinding(formatter,Parameters.icon,ICON);
		return icon;
	}

	public void setIcon(DataBinding icon)
	{
		FIBAttributeNotification<DataBinding> notification = requireChange(
				Parameters.icon, icon);
		if (notification != null) {
			icon.setOwner(formatter);
			icon.setBindingAttribute(Parameters.icon);
			icon.setBindingDefinition(ICON);
			this.icon = icon;
			hasChanged(notification);
		}
	}

	public Boolean getLocalize()
	{
		return localize;
	}

	public void setLocalize(Boolean localize)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.localize, localize);
		if (notification != null) {
			this.localize = localize;
			hasChanged(notification);
		}
	}
	

	public FIBFormatter getFormatter()
	{
		return formatter;
	}
	
	public Type getFormattedObjectType()
	{
		return getDataType();
	}

	private class FIBFormatter extends FIBModelObject implements Bindable
	{
		private BindingModel formatterBindingModel = null;
		
		@Override
		public BindingModel getBindingModel() 
		{
			if (formatterBindingModel == null) createFormatterBindingModel();
			return formatterBindingModel;
		}

		private void createFormatterBindingModel()
		{
			formatterBindingModel = new BindingModel(FIBWidget.this.getBindingModel());
			formatterBindingModel.addToBindingVariables(new BindingVariableImpl<Object>(this, "object", Object.class) {
				@Override
				public Type getType()
				{
					return getFormattedObjectType();
				}
			});
		}

		@Override
		public FIBComponent getRootComponent()
		{
			return FIBWidget.this.getRootComponent();
		}

		@Override
		public String toString() 
		{
			if (FIBWidget.this instanceof FIBDropDown) {
				return "FIBFormatter["+FIBWidget.this+"] iteratorClass="+((FIBDropDown)FIBWidget.this).getIteratorClass()+" dataType="+((FIBDropDown)FIBWidget.this).getDataType()+" obtained from "+((FIBDropDown)FIBWidget.this).getDescription();
			}
			return "FIBFormatter["+FIBWidget.this+"]"+" dataType="+(FIBWidget.this).getDataType();
		}
		
		@Override
		public void notifyBindingChanged(DataBinding binding) {
			if (binding == getFormat()) {
				FIBWidget.this.notifyBindingChanged(binding);
			}
			super.notifyBindingChanged(binding);
		}
	}
	
	public DataBinding getValueChangedAction() 
	{
		if (valueChangedAction == null) valueChangedAction = new DataBinding(this,Parameters.valueChangedAction,VALUE_CHANGED_ACTION);
		return valueChangedAction;
	}

	public void setValueChangedAction(DataBinding valueChangedAction) 
	{
		valueChangedAction.setOwner(this);
		valueChangedAction.setBindingAttribute(Parameters.valueChangedAction);
		valueChangedAction.setBindingDefinition(VALUE_CHANGED_ACTION);
		this.valueChangedAction = valueChangedAction;
	}

	public boolean hasClickAction()
	{
		return (clickAction != null && clickAction.isValid());
	}
	
	public DataBinding getClickAction() 
	{
		if (clickAction == null) clickAction = new DataBinding(this,Parameters.clickAction,CLICK_ACTION);
		return clickAction;
	}

	public void setClickAction(DataBinding clickAction) 
	{
		clickAction.setOwner(this);
		clickAction.setBindingAttribute(Parameters.clickAction);
		clickAction.setBindingDefinition(CLICK_ACTION);
		this.clickAction = clickAction;
	}

	public boolean hasDoubleClickAction()
	{
		return (doubleClickAction != null && doubleClickAction.isValid());
	}
	
	public DataBinding getDoubleClickAction() 
	{
		if (doubleClickAction == null) doubleClickAction = new DataBinding(this,Parameters.doubleClickAction,DOUBLE_CLICK_ACTION);
		return doubleClickAction;
	}

	public void setDoubleClickAction(DataBinding doubleClickAction) 
	{
		doubleClickAction.setOwner(this);
		doubleClickAction.setBindingAttribute(Parameters.doubleClickAction);
		doubleClickAction.setBindingDefinition(DOUBLE_CLICK_ACTION);
		this.doubleClickAction = doubleClickAction;
	}


	
	public boolean isPaletteElement()
	{
		return getParameter("isPaletteElement") != null && getParameter("isPaletteElement").equalsIgnoreCase("true");
	}

}
