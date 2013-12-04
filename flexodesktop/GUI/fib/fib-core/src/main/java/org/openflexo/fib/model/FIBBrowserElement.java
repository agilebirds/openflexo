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
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.model.FIBBrowserAction.FIBAddAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBCustomAction;
import org.openflexo.fib.model.FIBBrowserAction.FIBRemoveAction;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.toolbox.ChainedCollection;

public class FIBBrowserElement extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBBrowserElement.class.getPackage().getName());

	private FIBBrowser browser;

	public static enum Parameters implements FIBModelAttribute {
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
		dynamicFont,
		filtered,
		defaultVisible,
		children,
		actions;
	}

	@Deprecated
	public static BindingDefinition LABEL = new BindingDefinition("label", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	public static BindingDefinition EDITABLE_LABEL = new BindingDefinition("editableLabel", String.class,
			DataBinding.BindingDefinitionType.GET_SET, false);
	@Deprecated
	public static BindingDefinition DYNAMIC_FONT = new BindingDefinition("dynamicFont", Font.class, DataBinding.BindingDefinitionType.GET,
			false);

	private Class dataClass;

	private DataBinding<String> label;
	private DataBinding<Icon> icon;
	private DataBinding<String> tooltip;
	private DataBinding<Boolean> enabled;
	private DataBinding<Boolean> visible;

	private File imageIconFile;
	private ImageIcon imageIcon;
	private boolean isEditable = false;
	private DataBinding<String> editableLabel;
	private DataBinding<Font> dynamicFont;

	private boolean filtered = false;
	private boolean defaultVisible = true;

	private Font font;

	private Vector<FIBBrowserAction> actions;
	private Vector<FIBBrowserElementChildren> children;

	private BindingModel actionBindingModel;

	private FIBBrowserElementIterator iterator;

	public FIBBrowserElement() {
		iterator = new FIBBrowserElementIterator();
		children = new Vector<FIBBrowserElementChildren>();
		actions = new Vector<FIBBrowserAction>();
	}

	public FIBBrowser getBrowser() {
		return browser;
	}

	public void setBrowser(FIBBrowser browser) {
		this.browser = browser;
	}

	@Override
	public FIBComponent getComponent() {
		return getBrowser();
	}

	public DataBinding<String> getLabel() {
		if (label == null) {
			label = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET);
		}
		return label;
	}

	public void setLabel(DataBinding<String> label) {
		FIBAttributeNotification<DataBinding<String>> notification = requireChange(Parameters.label, label);
		if (notification != null) {
			if (label != null) {
				label.setOwner(iterator);
				label.setDeclaredType(String.class);
				label.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.label = label;
			hasChanged(notification);
		}
	}

	public DataBinding<Icon> getIcon() {
		if (icon == null) {
			icon = new DataBinding<Icon>(iterator, Icon.class, DataBinding.BindingDefinitionType.GET);
		}
		return icon;
	}

	public void setIcon(DataBinding<Icon> icon) {
		FIBAttributeNotification<DataBinding<Icon>> notification = requireChange(Parameters.icon, icon);
		if (notification != null) {
			if (icon != null) {
				icon.setOwner(iterator);
				icon.setDeclaredType(Icon.class);
				icon.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.icon = icon;
			hasChanged(notification);
		}
	}

	public DataBinding<String> getTooltip() {
		if (tooltip == null) {
			tooltip = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET);
		}
		return tooltip;
	}

	public void setTooltip(DataBinding<String> tooltip) {
		if (tooltip != null) {
			tooltip.setOwner(iterator);
			tooltip.setDeclaredType(String.class);
			tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.tooltip = tooltip;
	}

	public DataBinding<Boolean> getEnabled() {
		if (enabled == null) {
			enabled = new DataBinding<Boolean>(iterator, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return enabled;
	}

	public void setEnabled(DataBinding<Boolean> enabled) {
		if (enabled != null) {
			enabled.setOwner(iterator);
			enabled.setDeclaredType(Boolean.class);
			enabled.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.enabled = enabled;
	}

	public DataBinding<Boolean> getVisible() {
		if (visible == null) {
			visible = new DataBinding<Boolean>(iterator, Boolean.class, DataBinding.BindingDefinitionType.GET);
		}
		return visible;
	}

	public void setVisible(DataBinding visible) {
		if (visible != null) {
			visible.setOwner(iterator);
			visible.setDeclaredType(Boolean.class);
			visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.visible = visible;
	}

	public boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(boolean isEditable) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.isEditable, isEditable);
		if (notification != null) {
			this.isEditable = isEditable;
			hasChanged(notification);
		}
	}

	public DataBinding<String> getEditableLabel() {
		if (editableLabel == null) {
			editableLabel = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET_SET);
		}
		return editableLabel;
	}

	public void setEditableLabel(DataBinding<String> editableLabel) {
		FIBAttributeNotification<DataBinding<String>> notification = requireChange(Parameters.editableLabel, editableLabel);
		if (notification != null) {
			if (editableLabel != null) {
				editableLabel.setOwner(iterator);
				editableLabel.setDeclaredType(String.class);
				editableLabel.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
			}
			this.editableLabel = editableLabel;
			hasChanged(notification);
		}
	}

	public boolean getFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.filtered, filtered);
		if (notification != null) {
			this.filtered = filtered;
			hasChanged(notification);
		}
	}

	public boolean getDefaultVisible() {
		return defaultVisible;
	}

	public void setDefaultVisible(boolean defaultVisible) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.defaultVisible, defaultVisible);
		if (notification != null) {
			this.defaultVisible = defaultVisible;
			hasChanged(notification);
		}
	}

	public void finalizeBrowserDeserialization() {
		logger.fine("finalizeBrowserDeserialization() for FIBBrowserElement " + dataClass);
		if (label != null) {
			label.decode();
		}
		if (icon != null) {
			icon.decode();
		}
		if (tooltip != null) {
			tooltip.decode();
		}
		if (enabled != null) {
			enabled.decode();
		}
		if (visible != null) {
			visible.decode();
		}
		if (editableLabel != null) {
			editableLabel.decode();
		}
		for (FIBBrowserElementChildren c : children) {
			c.finalizeBrowserDeserialization();
		}
	}

	@Override
	public BindingModel getBindingModel() {
		if (getBrowser() != null) {
			return getBrowser().getBindingModel();
		}
		return null;
	}

	public void updateBindingModel() {
		actionBindingModel = null;
	}

	public BindingModel getActionBindingModel() {
		if (actionBindingModel == null) {
			createActionBindingModel();
		}
		return actionBindingModel;
	}

	private void createActionBindingModel() {
		actionBindingModel = new BindingModel(getBindingModel());

		actionBindingModel.addToBindingVariables(new BindingVariable("selected", getDataClass()));
		// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);

		// logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
	}

	public void notifiedBindingModelRecreated() {
		createActionBindingModel();
	}

	public Font retrieveValidFont() {
		if (font == null && getBrowser() != null) {
			return getBrowser().retrieveValidFont();
		}
		return getFont();
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		FIBAttributeNotification<Font> notification = requireChange(Parameters.font, font);
		if (notification != null) {
			this.font = font;
			hasChanged(notification);
		}
	}

	public boolean getHasSpecificFont() {
		return getFont() != null;
	}

	public void setHasSpecificFont(boolean aFlag) {
		if (aFlag) {
			setFont(retrieveValidFont());
		} else {
			setFont(null);
		}
	}

	public DataBinding<Font> getDynamicFont() {
		if (dynamicFont == null) {
			dynamicFont = new DataBinding<Font>(iterator, Font.class, DataBinding.BindingDefinitionType.GET);
		}
		return dynamicFont;
	}

	public void setDynamicFont(DataBinding<Font> dynamicFont) {
		if (dynamicFont != null) {
			dynamicFont.setOwner(iterator);
			dynamicFont.setDeclaredType(Font.class);
			dynamicFont.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.dynamicFont = dynamicFont;
	}

	public File getImageIconFile() {
		return imageIconFile;
	}

	public void setImageIconFile(File imageIconFile) {
		FIBAttributeNotification<File> notification = requireChange(Parameters.imageIconFile, imageIconFile);
		if (notification != null) {
			this.imageIconFile = imageIconFile;
			this.imageIcon = new ImageIcon(imageIconFile.getAbsolutePath());
			hasChanged(notification);
		}
	}

	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public FIBBrowserElementIterator getIterator() {
		return iterator;
	}

	private class FIBBrowserElementIterator extends FIBModelObject implements Bindable {
		private BindingModel iteratorBindingModel = null;

		@Override
		public BindingModel getBindingModel() {
			if (iteratorBindingModel == null) {
				createFormatterBindingModel();
			}
			return iteratorBindingModel;
		}

		private void createFormatterBindingModel() {
			iteratorBindingModel = new BindingModel(FIBBrowserElement.this.getBindingModel());
			iteratorBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
				@Override
				public Type getType() {
					return getDataClass();
				}

				@Override
				public String getVariableName() {
					return FIBBrowserElement.this.getName();
				}
			});
		}

		@Override
		public FIBComponent getComponent() {
			return FIBBrowserElement.this.getComponent();
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

	}

	public Class getDataClass() {
		if (dataClass == null && getBrowser() != null) {
			return getBrowser().getIteratorClass();
		}
		return dataClass;

	}

	public void setDataClass(Class dataClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.dataClass, dataClass);
		if (notification != null) {
			this.dataClass = dataClass;
			hasChanged(notification);
		}
	}

	public Vector<FIBBrowserAction> getActions() {
		return actions;
	}

	public void setActions(Vector<FIBBrowserAction> actions) {
		this.actions = actions;
	}

	public void addToActions(FIBBrowserAction anAction) {
		logger.fine("Add to actions " + anAction);
		anAction.setBrowserElement(this);
		actions.add(anAction);
		getPropertyChangeSupport().firePropertyChange(Parameters.actions.name(), null, actions);
	}

	public void removeFromActions(FIBBrowserAction anAction) {
		anAction.setBrowserElement(null);
		actions.remove(anAction);
		getPropertyChangeSupport().firePropertyChange(Parameters.actions.name(), null, actions);
	}

	public FIBAddAction createAddAction() {
		FIBAddAction newAction = new FIBAddAction();
		newAction.setName("add_action");
		addToActions(newAction);
		return newAction;
	}

	public FIBRemoveAction createRemoveAction() {
		FIBRemoveAction newAction = new FIBRemoveAction();
		newAction.setName("delete_action");
		addToActions(newAction);
		return newAction;
	}

	public FIBCustomAction createCustomAction() {
		FIBCustomAction newAction = new FIBCustomAction();
		newAction.setName("custom_action");
		addToActions(newAction);
		return newAction;
	}

	public FIBBrowserAction deleteAction(FIBBrowserAction actionToDelete) {
		logger.info("Called deleteAction() with " + actionToDelete);
		removeFromActions(actionToDelete);
		return actionToDelete;
	}

	public Vector<FIBBrowserElementChildren> getChildren() {
		return children;
	}

	public void setChildren(Vector<FIBBrowserElementChildren> children) {
		this.children = children;
	}

	public void addToChildren(FIBBrowserElementChildren aChildren) {
		aChildren.setBrowserElement(this);
		children.add(aChildren);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public void removeFromChildren(FIBBrowserElementChildren aChildren) {
		aChildren.setBrowserElement(null);
		children.remove(aChildren);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public FIBBrowserElementChildren createChildren() {
		logger.info("Called createChildren()");
		FIBBrowserElementChildren newChildren = new FIBBrowserElementChildren();
		newChildren.setName("children" + (children.size() > 0 ? children.size() : ""));
		addToChildren(newChildren);
		return newChildren;
	}

	public FIBBrowserElementChildren deleteChildren(FIBBrowserElementChildren elementToDelete) {
		logger.info("Called elementToDelete() with " + elementToDelete);
		removeFromChildren(elementToDelete);
		return elementToDelete;
	}

	public void moveToTop(FIBBrowserElementChildren e) {
		if (e == null) {
			return;
		}
		children.remove(e);
		children.insertElementAt(e, 0);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public void moveUp(FIBBrowserElementChildren e) {
		if (e == null) {
			return;
		}
		int index = children.indexOf(e);
		children.remove(e);
		children.insertElementAt(e, index - 1);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public void moveDown(FIBBrowserElementChildren e) {
		if (e == null) {
			return;
		}
		int index = children.indexOf(e);
		children.remove(e);
		children.insertElementAt(e, index + 1);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public void moveToBottom(FIBBrowserElementChildren e) {
		if (e == null) {
			return;
		}
		children.remove(e);
		children.add(e);
		getPropertyChangeSupport().firePropertyChange(Parameters.children.name(), null, children);
	}

	public static class FIBBrowserElementChildren extends FIBModelObject {
		private FIBBrowserElement browserElement;
		private DataBinding<Object> data;
		private DataBinding<Boolean> visible;
		private DataBinding<Object> cast;
		private FIBChildBindable childBindable;

		@Deprecated
		public static BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition CAST = new BindingDefinition("cast", Object.class, DataBinding.BindingDefinitionType.GET, false);

		private class FIBChildBindable extends FIBModelObject implements Bindable {
			private BindingModel childBindingModel = null;

			@Override
			public BindingFactory getBindingFactory() {
				return FIBBrowserElementChildren.this.getBindingFactory();
			}

			@Override
			public BindingModel getBindingModel() {
				if (childBindingModel == null) {
					createChildBindingModel();
				}
				return childBindingModel;
			}

			private void createChildBindingModel() {
				childBindingModel = new BindingModel(FIBBrowserElementChildren.this.getBindingModel());
				childBindingModel.addToBindingVariables(new BindingVariable("child", Object.class) {
					@Override
					public Type getType() {
						if (getData().isSet()) {
							Type type = getData().getAnalyzedType();
							if (isSupportedListType(type)) {
								if (type instanceof ParameterizedType) {
									return ((ParameterizedType) type).getActualTypeArguments()[0];
								}
								logger.warning("Found supported list type " + type
										+ " but it is not parameterized so I can't guess its content");
								return Object.class;
							}
							return type;
						}
						return Object.class;
					}
				});
			}

			@Override
			public FIBComponent getComponent() {
				return browserElement.getComponent();
			}

			@Override
			public List<? extends FIBModelObject> getEmbeddedObjects() {
				return null;
			}

		}

		public static enum Parameters implements FIBModelAttribute {
			data, visible, cast;
		}

		public FIBBrowserElementChildren() {
		}

		public FIBChildBindable getChildBindable() {
			if (childBindable == null) {
				childBindable = new FIBChildBindable();
			}
			return childBindable;
		}

		public FIBBrowser getBrowser() {
			return getBrowserElement().getBrowser();
		}

		public DataBinding<Object> getData() {
			if (data == null) {
				data = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return data;
		}

		public void setData(DataBinding<Object> data) {
			if (data != null) {
				data.setOwner(browserElement != null ? browserElement.getIterator() : null);
				data.setDeclaredType(Object.class);
				data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.data = data;
		}

		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return visible;
		}

		public void setVisible(DataBinding<Boolean> visible) {
			if (visible != null) {
				visible.setOwner(browserElement != null ? browserElement.getIterator() : null);
				visible.setDeclaredType(Boolean.class);
				visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.visible = visible;
		}

		public DataBinding<Object> getCast() {
			if (cast == null) {
				cast = new DataBinding<Object>(getChildBindable(), Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return cast;
		}

		public void setCast(DataBinding<Object> cast) {
			cast.setOwner(getChildBindable());
			cast.setDeclaredType(Object.class);
			cast.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			this.cast = cast;
		}

		public FIBBrowserElement getBrowserElement() {
			return browserElement;
		}

		public void setBrowserElement(FIBBrowserElement browserElement) {
			this.browserElement = browserElement;
		}

		@Override
		public FIBComponent getComponent() {
			if (getBrowserElement() != null) {
				return getBrowserElement().getComponent();
			}
			return null;
		}

		public void finalizeBrowserDeserialization() {
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserElementChildren ");
			if (data != null) {
				data.setOwner(browserElement.getIterator());
				data.decode();
			}
			if (visible != null) {
				visible.setOwner(browserElement.getIterator());
				visible.decode();
			}
		}

		public ImageIcon getImageIcon() {
			if (getBaseClass() == null) {
				return null;
			}
			FIBBrowserElement e = getBrowser().elementForClass(getBaseClass());
			if (e != null) {
				return e.getImageIcon();
			}
			return null;
		}

		public Class getBaseClass() {
			Type accessedType = getAccessedType();
			if (accessedType == null) {
				return null;
			}
			if (isMultipleAccess()) {
				return TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
			} else {
				return TypeUtils.getBaseClass(getAccessedType());
			}
		}

		public Type getAccessedType() {
			if (data != null && data.isSet()) {
				return data.getAnalyzedType();
			}
			return null;
		}

		public boolean isMultipleAccess() {
			/*System.out.println("This="+this);
			System.out.println("getAccessedType()="+getAccessedType());
			System.out.println("TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(accessedType))="+TypeUtils.isClassAncestorOf(List.class, TypeUtils.getBaseClass(getAccessedType())));
			System.out.println("accessedType instanceof ParameterizedType="+(getAccessedType() instanceof ParameterizedType));
			System.out.println("((ParameterizedType)accessedType).getActualTypeArguments().length > 0="+(((ParameterizedType)getAccessedType()).getActualTypeArguments().length > 0));*/
			Type accessedType = getAccessedType();
			if (accessedType == null) {
				return false;
			}
			return isSupportedListType(accessedType);
		}

		private boolean isSupportedListType(Type accessedType) {
			return TypeUtils.isClassAncestorOf(Iterable.class, TypeUtils.getBaseClass(accessedType))
					|| TypeUtils.isClassAncestorOf(Enumeration.class, TypeUtils.getBaseClass(accessedType));
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(DataBindingMustBeValid.class, report);
			performValidation(VisibleBindingMustBeValid.class, report);
			performValidation(CastBindingMustBeValid.class, report);
		}

		public static class DataBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
			public DataBindingMustBeValid() {
				super("'data'_binding_is_not_valid", FIBBrowserElementChildren.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
				return object.getData();
			}
		}

		public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
			public VisibleBindingMustBeValid() {
				super("'visible'_binding_is_not_valid", FIBBrowserElementChildren.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
				return object.getVisible();
			}
		}

		public static class CastBindingMustBeValid extends BindingMustBeValid<FIBBrowserElementChildren> {
			public CastBindingMustBeValid() {
				super("'cast'_binding_is_not_valid", FIBBrowserElementChildren.class);
			}

			@Override
			public DataBinding<?> getBinding(FIBBrowserElementChildren object) {
				return object.getCast();
			}
		}

	}

	@Override
	public String toString() {
		return "FIBBrowserElement(name" + getName() + ",type=" + getDataClass() + ")";
	}

	@Override
	public Collection<? extends FIBModelObject> getEmbeddedObjects() {
		return new ChainedCollection(getActions(), getChildren());
	}

	@Override
	protected void applyValidation(ValidationReport report) {
		super.applyValidation(report);
		performValidation(LabelBindingMustBeValid.class, report);
		performValidation(IconBindingMustBeValid.class, report);
		performValidation(TooltipBindingMustBeValid.class, report);
		performValidation(EnabledBindingMustBeValid.class, report);
		performValidation(VisibleBindingMustBeValid.class, report);
		performValidation(EditableLabelBindingMustBeValid.class, report);
		performValidation(DynamicFontBindingMustBeValid.class, report);
	}

	public static class LabelBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public LabelBindingMustBeValid() {
			super("'label'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getLabel();
		}
	}

	public static class IconBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public IconBindingMustBeValid() {
			super("'icon'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getIcon();
		}
	}

	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getTooltip();
		}
	}

	public static class EnabledBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public EnabledBindingMustBeValid() {
			super("'enabled'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getEnabled();
		}
	}

	public static class VisibleBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public VisibleBindingMustBeValid() {
			super("'visible'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getVisible();
		}
	}

	public static class EditableLabelBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public EditableLabelBindingMustBeValid() {
			super("'editable_label'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getEditableLabel();
		}
	}

	public static class DynamicFontBindingMustBeValid extends BindingMustBeValid<FIBBrowserElement> {
		public DynamicFontBindingMustBeValid() {
			super("'dynamic_font'_binding_is_not_valid", FIBBrowserElement.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBBrowserElement object) {
			return object.getDynamicFont();
		}
	}

}