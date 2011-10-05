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

import java.awt.Font;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.fib.model.FIBBrowserAction.FIBAddAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBCustomAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBRemoveAction;



public class FIBBrowserElement extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBBrowserElement.class.getPackage().getName());

	private FIBBrowser browser;

	public static enum Parameters implements FIBModelAttribute
	{
		dataClass,
		label,
		icon,
		tooltip,
		enabled,
		visible,
		imageIconFile,
		isEditable,
		editableLabel,
		font,
		filtered,
		defaultVisible,
		children,
		actions
	}


	public static BindingDefinition LABEL = new BindingDefinition("label", String.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, BindingDefinitionType.GET, false);
	public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, BindingDefinitionType.GET, false);
	public static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, BindingDefinitionType.GET, false);
	public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, BindingDefinitionType.GET, false);
	public static BindingDefinition EDITABLE_LABEL = new BindingDefinition("editableLabel", String.class, BindingDefinitionType.GET_SET, false);

	private Class dataClass;

	private DataBinding label;
	private DataBinding icon;
	private DataBinding tooltip;
	private DataBinding enabled;
	private DataBinding visible;
	private File imageIconFile;
	private ImageIcon imageIcon;
	private boolean isEditable = false;
	private DataBinding editableLabel;

	private boolean filtered = false;
	private boolean defaultVisible = true;

	private Font font;
	
	private Vector<FIBBrowserAction> actions;
	private Vector<FIBBrowserElementChildren> children;
	
	private BindingModel actionBindingModel;
	
	private FIBBrowserElementIterator iterator;
	
	public FIBBrowserElement()
	{
		iterator = new FIBBrowserElementIterator();
		children = new Vector<FIBBrowserElementChildren>();
		actions = new Vector<FIBBrowserAction>();
	}
	
    public FIBBrowser getBrowser() 
    {
		return browser;
	}

	public void setBrowser(FIBBrowser browser)
	{
		this.browser = browser;
	}

	@Override
	public FIBComponent getRootComponent()
	{
		if (getBrowser() != null) return getBrowser().getRootComponent();
		return null;
	}

	public DataBinding getLabel() 
	{
		if (label == null) label = new DataBinding(this,Parameters.label,LABEL);
		return label;
	}

	public void setLabel(DataBinding label) 
	{
		label.setOwner(iterator);
		label.setBindingAttribute(Parameters.label);
		label.setBindingDefinition(LABEL);
		this.label = label;
	}
	
	public DataBinding getIcon() 
	{
		if (icon == null) icon = new DataBinding(this,Parameters.icon,ICON);
		return icon;
	}

	public void setIcon(DataBinding icon) 
	{
		icon.setOwner(iterator);
		icon.setBindingAttribute(Parameters.icon);
		icon.setBindingDefinition(ICON);
		this.icon = icon;
	}
	
	public DataBinding getTooltip() 
	{
		if (tooltip == null) tooltip = new DataBinding(this,Parameters.tooltip,TOOLTIP);
		return tooltip;
	}

	public void setTooltip(DataBinding tooltip) 
	{
		tooltip.setOwner(iterator);
		tooltip.setBindingAttribute(Parameters.tooltip);
		tooltip.setBindingDefinition(TOOLTIP);
		this.tooltip = tooltip;
	}
	
	public DataBinding getEnabled() 
	{
		if (enabled == null) enabled = new DataBinding(this,Parameters.enabled,ENABLED);
		return enabled;
	}

	public void setEnabled(DataBinding enabled) 
	{
		enabled.setOwner(iterator);
		enabled.setBindingAttribute(Parameters.enabled);
		enabled.setBindingDefinition(ENABLED);
		this.enabled = enabled;
	}
	
	public DataBinding getVisible() 
	{
		if (visible == null) visible = new DataBinding(this,Parameters.visible,VISIBLE);
		return visible;
	}

	public void setVisible(DataBinding visible) 
	{
		visible.setOwner(iterator);
		visible.setBindingAttribute(Parameters.visible);
		visible.setBindingDefinition(VISIBLE);
		this.visible = visible;
	}
	
	public boolean getIsEditable() 
	{
		return isEditable;
	}

	public void setIsEditable(boolean isEditable) 
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.isEditable, isEditable);
		if (notification != null) {
			this.isEditable = isEditable;
			hasChanged(notification);
		}
	}

	public DataBinding getEditableLabel() 
	{
		if (editableLabel == null) editableLabel = new DataBinding(this,Parameters.editableLabel,EDITABLE_LABEL);
		return editableLabel;
	}

	public void setEditableLabel(DataBinding editableLabel) 
	{
		editableLabel.setOwner(iterator);
		editableLabel.setBindingAttribute(Parameters.editableLabel);
		editableLabel.setBindingDefinition(EDITABLE_LABEL);
		this.editableLabel = editableLabel;
	}
	
	public boolean getFiltered() 
	{
		return filtered;
	}

	public void setFiltered(boolean filtered) 
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.filtered, filtered);
		if (notification != null) {
			this.filtered = filtered;
			hasChanged(notification);
		}
	}

	public boolean getDefaultVisible() 
	{
		return defaultVisible;
	}

	public void setDefaultVisible(boolean defaultVisible) 
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.defaultVisible, defaultVisible);
		if (notification != null) {
			this.defaultVisible = defaultVisible;
			hasChanged(notification);
		}
	}
	
	public void finalizeBrowserDeserialization() 
	{
		logger.fine("finalizeBrowserDeserialization() for FIBBrowserElement "+dataClass);
		if (label != null) {
			label.finalizeDeserialization();
		}
		if (icon != null) {
			icon.finalizeDeserialization();
		}
		if (tooltip != null) {
			tooltip.finalizeDeserialization();
		}
		if (enabled != null) {
			enabled.finalizeDeserialization();
		}
		if (visible != null) {
			visible.finalizeDeserialization();
		}
		if (editableLabel != null) {
			editableLabel.finalizeDeserialization();
		}
		for (FIBBrowserElementChildren c : children) {
			c.finalizeBrowserDeserialization();
		}
	}

	@Override
	public BindingModel getBindingModel() 
	{
		if (getBrowser() != null) {
			return getBrowser().getBindingModel();
		}
		return null;
	}
	
	public BindingModel getActionBindingModel() 
	{
		if (actionBindingModel == null) createActionBindingModel();
		return actionBindingModel;
	}

	private void createActionBindingModel()
	{
		actionBindingModel = new BindingModel(getBindingModel());
		
		actionBindingModel.addToBindingVariables(new BindingVariableImpl(this, "selected", getDataClass()));
		//System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);
		
		//logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
	}

	public void notifiedBindingModelRecreated()
	{
		createActionBindingModel();
	}
		

	public Font retrieveValidFont()
	{
		if (font == null && getBrowser() != null) return getBrowser().retrieveValidFont();
		return getFont();	
	}
	
	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		FIBAttributeNotification<Font> notification = requireChange(
				Parameters.font, font);
		if (notification != null) {
			this.font = font;
			hasChanged(notification);
		}
	}

	public boolean getHasSpecificFont()
	{
		return getFont() != null;
	}

	public void setHasSpecificFont(boolean aFlag)
	{
		if (aFlag) {
			setFont(retrieveValidFont());
		}
		else {
			setFont(null);
		}
	}
	
	public File getImageIconFile() 
	{
		return imageIconFile;
	}

	public void setImageIconFile(File imageIconFile) 
	{
		FIBAttributeNotification<File> notification = requireChange(
				Parameters.imageIconFile, imageIconFile);
		if (notification != null) {
			this.imageIconFile = imageIconFile;
			this.imageIcon = new ImageIcon(imageIconFile.getAbsolutePath());
			hasChanged(notification);
		}
	}

	public ImageIcon getImageIcon()
	{
		return imageIcon;
	}
	
	public FIBBrowserElementIterator getIterator()
	{
		return iterator;
	}
	
	private class FIBBrowserElementIterator extends FIBModelObject implements Bindable
	{
		private BindingModel iteratorBindingModel = null;
		
		@Override
		public BindingModel getBindingModel() 
		{
			if (iteratorBindingModel == null) createFormatterBindingModel();
			return iteratorBindingModel;
		}

		private void createFormatterBindingModel()
		{
			iteratorBindingModel = new BindingModel(FIBBrowserElement.this.getBindingModel());
			iteratorBindingModel.addToBindingVariables(new BindingVariableImpl(this, "object", Object.class) {
				@Override
				public Type getType()
				{
					return getDataClass();
				}
				@Override
				public String getVariableName()
				{
					return FIBBrowserElement.this.getName();
				}
			});
		}

		@Override
		public FIBComponent getRootComponent()
		{
			return FIBBrowserElement.this.getRootComponent();
		}

	}

	
	public Class getDataClass()
	{
		if (dataClass == null && getBrowser() != null) return getBrowser().getIteratorClass();
		return dataClass;

	}

	public void setDataClass(Class dataClass) 
	{
		FIBAttributeNotification<Class> notification = requireChange(
				Parameters.dataClass, dataClass);
		if (notification != null) {
			this.dataClass = dataClass;
			hasChanged(notification);
		}
	}

	public Vector<FIBBrowserAction> getActions() 
	{
		return actions;
	}

	public void setActions(Vector<FIBBrowserAction> actions) 
	{
		this.actions = actions;
	}

	public void addToActions(FIBBrowserAction anAction) 
	{
		logger.fine("Add to actions "+anAction);
		anAction.setBrowserElement(this);
		actions.add(anAction);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserAction>(Parameters.actions, anAction));
	}

	public void removeFromActions(FIBBrowserAction anAction) 
	{
		anAction.setBrowserElement(null);
		actions.remove(anAction);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBBrowserAction>(Parameters.actions, anAction));
	}

	public FIBAddAction createAddAction()
	{
		FIBAddAction newAction = new FIBAddAction();
		newAction.setName("add_action");
		addToActions(newAction);
		return newAction;
	}

	public FIBRemoveAction createRemoveAction()
	{
		FIBRemoveAction newAction = new FIBRemoveAction();
		newAction.setName("delete_action");
		addToActions(newAction);
		return newAction;
	}
	
	public FIBCustomAction createCustomAction()
	{
		FIBCustomAction newAction = new FIBCustomAction();
		newAction.setName("custom_action");
		addToActions(newAction);
		return newAction;
	}
	
	public FIBBrowserAction deleteAction(FIBBrowserAction actionToDelete)
	{
		logger.info("Called deleteAction() with "+actionToDelete);
		removeFromActions(actionToDelete);
		return actionToDelete;
	}

	public Vector<FIBBrowserElementChildren> getChildren() 
	{
		return children;
	}

	public void setChildren(Vector<FIBBrowserElementChildren> children) 
	{
		this.children = children;
	}

	public void addToChildren(FIBBrowserElementChildren aChildren) 
	{
		aChildren.setBrowserElement(this);
		children.add(aChildren);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElementChildren>(Parameters.children, aChildren));
	}

	public void removeFromChildren(FIBBrowserElementChildren aChildren) 
	{
		aChildren.setBrowserElement(null);
		children.remove(aChildren);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBBrowserElementChildren>(Parameters.children, aChildren));
	}


	public FIBBrowserElementChildren createChildren()
	{
		logger.info("Called createChildren()");
		FIBBrowserElementChildren newChildren = new FIBBrowserElementChildren();
		newChildren.setName("children"+(children.size()>0?children.size():""));
		addToChildren(newChildren);
		return newChildren;
	}

	public FIBBrowserElementChildren deleteChildren(FIBBrowserElementChildren elementToDelete)
	{
		logger.info("Called elementToDelete() with "+elementToDelete);
		removeFromChildren(elementToDelete);
		return elementToDelete;
	}

	public void moveToTop(FIBBrowserElementChildren e)
	{
		if (e == null) return;
		children.remove(e);
		children.insertElementAt(e,0);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElementChildren>(Parameters.children, e));
	}
	
	public void moveUp(FIBBrowserElementChildren e)
	{
		if (e == null) return;
		int index = children.indexOf(e);
		children.remove(e);
		children.insertElementAt(e,index-1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElementChildren>(Parameters.children, e));
	}
	
	public void moveDown(FIBBrowserElementChildren e)
	{
		if (e == null) return;
		int index = children.indexOf(e);
		children.remove(e);
		children.insertElementAt(e,index+1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElementChildren>(Parameters.children, e));
	}
	
	public void moveToBottom(FIBBrowserElementChildren e)
	{
		if (e == null) return;
		children.remove(e);
		children.add(e);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBBrowserElementChildren>(Parameters.children, e));
	}


	public static class FIBBrowserElementChildren extends FIBModelObject
	{
		private FIBBrowserElement browserElement;
		private DataBinding data;
		private DataBinding visible;
		
		public BindingDefinition DATA = new BindingDefinition("data", Object.class, BindingDefinitionType.GET, false);
		public BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, BindingDefinitionType.GET, false);

		public static enum Parameters implements FIBModelAttribute
		{
			data,
			visible
		}

		public FIBBrowserElementChildren() {
		}
		
		public FIBBrowser getBrowser()
		{
			return getBrowserElement().getBrowser();
		}
		
		public DataBinding getData() 
		{
			if (data == null) data = new DataBinding(this,Parameters.data,DATA);
			return data;
		}

		public void setData(DataBinding data) 
		{
			data.setOwner(browserElement != null ? browserElement.getIterator() : null);
			data.setBindingAttribute(Parameters.data);
			data.setBindingDefinition(DATA);
			this.data = data;
		}
		
		public DataBinding getVisible() 
		{
			if (visible == null) visible = new DataBinding(this,Parameters.visible,VISIBLE);
			return visible;
		}

		public void setVisible(DataBinding visible) 
		{
			visible.setOwner(browserElement != null ? browserElement.getIterator() : null);
			visible.setBindingAttribute(Parameters.visible);
			visible.setBindingDefinition(VISIBLE);
			this.visible = visible;
		}
		
		public FIBBrowserElement getBrowserElement()
		{
			return browserElement;
		}

		public void setBrowserElement(FIBBrowserElement browserElement) 
		{
			this.browserElement = browserElement;
		}

		@Override
		public FIBComponent getRootComponent()
		{
			if (getBrowserElement() != null) return getBrowserElement().getRootComponent();
			return null;
		}

		public void finalizeBrowserDeserialization() 
		{
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserElementChildren ");
			if (data != null) {
				data.setOwner(browserElement.getIterator());
				data.setBindingAttribute(Parameters.data);
				data.finalizeDeserialization();
			}
			if (visible != null) {
				visible.setOwner(browserElement.getIterator());
				visible.setBindingAttribute(Parameters.visible);
				visible.finalizeDeserialization();
			}
		}

		public ImageIcon getImageIcon()
		{
			if (getBaseClass() == null) return null;
			FIBBrowserElement e = getBrowser().elementForClass(getBaseClass());
			if (e != null) return e.getImageIcon();
			return null;
		}
		
		public Type getAccessedType()
		{
			if (data != null && data.isSet()) return data.getBinding().getAccessedType();
			return null;
		}
		
		public boolean isMultipleAccess()
		{
			/*System.out.println("This="+this);
			System.out.println("getAccessedType()="+getAccessedType());
			System.out.println("TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(accessedType))="+TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(getAccessedType())));
			System.out.println("accessedType instanceof ParameterizedType="+(getAccessedType() instanceof ParameterizedType));
			System.out.println("((ParameterizedType)accessedType).getActualTypeArguments().length > 0="+(((ParameterizedType)getAccessedType()).getActualTypeArguments().length > 0));*/
			Type accessedType = getAccessedType();
			return accessedType != null
				&& TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(accessedType))
				&& accessedType instanceof ParameterizedType 
				&& ((ParameterizedType)accessedType).getActualTypeArguments().length > 0;
			// ??? Too restrictive: type might be undefined, eg List
		}

		public Class getBaseClass()
		{
			Type accessedType = getAccessedType();
			if (accessedType == null) return null;
			if (isMultipleAccess()) {
				return TypeUtils.getBaseClass(((ParameterizedType)accessedType).getActualTypeArguments()[0]);
			}
			else return TypeUtils.getBaseClass(getAccessedType());
		}
	}


}
