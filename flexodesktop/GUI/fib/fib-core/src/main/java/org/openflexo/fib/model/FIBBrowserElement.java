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
import java.util.Enumeration;
import java.util.List;
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
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBBrowserElement.FIBBrowserElementImpl.class)
@XMLElement(xmlTag = "BrowserElement")
public interface FIBBrowserElement extends FIBModelObject {

	@PropertyIdentifier(type = FIBBrowser.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = Class.class)
	public static final String DATA_CLASS_KEY = "dataClass";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ICON_KEY = "icon";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TOOLTIP_KEY = "tooltip";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String ENABLED_KEY = "enabled";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VISIBLE_KEY = "visible";
	@PropertyIdentifier(type = File.class)
	public static final String IMAGE_ICON_FILE_KEY = "imageIconFile";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_EDITABLE_KEY = "isEditable";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EDITABLE_LABEL_KEY = "editableLabel";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DYNAMIC_FONT_KEY = "dynamicFont";
	@PropertyIdentifier(type = boolean.class)
	public static final String FILTERED_KEY = "filtered";
	@PropertyIdentifier(type = boolean.class)
	public static final String DEFAULT_VISIBLE_KEY = "defaultVisible";
	@PropertyIdentifier(type = List.class)
	public static final String CHILDREN_KEY = "children";
	@PropertyIdentifier(type = List.class)
	public static final String ACTIONS_KEY = "actions";

	@Getter(value = OWNER_KEY, inverse = FIBBrowser.ELEMENTS_KEY)
	public FIBBrowser getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBBrowser customColumn);

	@Getter(DATA_CLASS_KEY)
	@XMLAttribute(xmlTag = "dataClassName")
	public Class<?> getDataClass();

	@Setter(DATA_CLASS_KEY)
	public void setDataClass(Class<?> dataClass);

	@Getter(LABEL_KEY)
	@XMLAttribute
	public DataBinding<String> getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(DataBinding<String> label);

	@Getter(ICON_KEY)
	@XMLAttribute
	public DataBinding<Icon> getIcon();

	@Setter(ICON_KEY)
	public void setIcon(DataBinding<Icon> icon);

	@Getter(TOOLTIP_KEY)
	@XMLAttribute
	public DataBinding<String> getTooltip();

	@Setter(TOOLTIP_KEY)
	public void setTooltip(DataBinding<String> tooltip);

	@Getter(ENABLED_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEnabled();

	@Setter(ENABLED_KEY)
	public void setEnabled(DataBinding<Boolean> enabled);

	@Getter(VISIBLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getVisible();

	@Setter(VISIBLE_KEY)
	public void setVisible(DataBinding visible);

	@Getter(value = IS_EDITABLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsEditable();

	@Setter(IS_EDITABLE_KEY)
	public void setIsEditable(boolean isEditable);

	@Getter(EDITABLE_LABEL_KEY)
	@XMLAttribute
	public DataBinding<String> getEditableLabel();

	@Setter(EDITABLE_LABEL_KEY)
	public void setEditableLabel(DataBinding<String> editableLabel);

	@Getter(FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	public boolean getHasSpecificFont();

	public void setHasSpecificFont(boolean aFlag);

	@Getter(DYNAMIC_FONT_KEY)
	@XMLAttribute
	public DataBinding<Font> getDynamicFont();

	@Setter(DYNAMIC_FONT_KEY)
	public void setDynamicFont(DataBinding<Font> dynamicFont);

	@Getter(value = FILTERED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getFiltered();

	@Setter(FILTERED_KEY)
	public void setFiltered(boolean filtered);

	@Getter(value = DEFAULT_VISIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getDefaultVisible();

	@Setter(DEFAULT_VISIBLE_KEY)
	public void setDefaultVisible(boolean defaultVisible);

	@Getter(value = CHILDREN_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserElementChildren.OWNER_KEY)
	@XMLElement
	public List<FIBBrowserElementChildren> getChildren();

	@Setter(CHILDREN_KEY)
	public void setChildren(List<FIBBrowserElementChildren> children);

	@Adder(CHILDREN_KEY)
	public void addToChildren(FIBBrowserElementChildren aChildren);

	@Remover(CHILDREN_KEY)
	public void removeFromChildren(FIBBrowserElementChildren aChildren);

	@Getter(value = ACTIONS_KEY, cardinality = Cardinality.LIST, inverse = FIBBrowserAction.OWNER_KEY)
	@XMLElement
	public List<FIBBrowserAction> getActions();

	@Setter(ACTIONS_KEY)
	public void setActions(List<FIBBrowserAction> actions);

	@Adder(ACTIONS_KEY)
	public void addToActions(FIBBrowserAction anAction);

	@Remover(ACTIONS_KEY)
	public void removeFromActions(FIBBrowserAction anAction);

	public void finalizeBrowserDeserialization();

	public void updateBindingModel();

	public void notifiedBindingModelRecreated();

	public Bindable getIterator();

	public ImageIcon getImageIcon();

	public BindingModel getActionBindingModel();

	public Font retrieveValidFont();

	public static abstract class FIBBrowserElementImpl extends FIBModelObjectImpl implements FIBBrowserElement {

		private static final Logger logger = Logger.getLogger(FIBBrowserElement.class.getPackage().getName());

		@Deprecated
		public static BindingDefinition LABEL = new BindingDefinition("label", String.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition ICON = new BindingDefinition("icon", Icon.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		public static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition ENABLED = new BindingDefinition("enabled", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		public static BindingDefinition EDITABLE_LABEL = new BindingDefinition("editableLabel", String.class,
				DataBinding.BindingDefinitionType.GET_SET, false);
		@Deprecated
		public static BindingDefinition DYNAMIC_FONT = new BindingDefinition("dynamicFont", Font.class,
				DataBinding.BindingDefinitionType.GET, false);

		// private Class dataClass;

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

		// private List<FIBBrowserAction> actions;
		// private List<FIBBrowserElementChildren> children;

		private BindingModel actionBindingModel;

		private final FIBBrowserElementIterator iterator;

		public FIBBrowserElementImpl() {
			iterator = new FIBBrowserElementIterator();
			// children = new Vector<FIBBrowserElementChildren>();
			// actions = new Vector<FIBBrowserAction>();
		}

		@Override
		public FIBComponent getComponent() {
			return getOwner();
		}

		@Override
		public DataBinding<String> getLabel() {
			if (label == null) {
				label = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET);
			}
			return label;
		}

		@Override
		public void setLabel(DataBinding<String> label) {
			FIBPropertyNotification<DataBinding<String>> notification = requireChange(LABEL_KEY, label);
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

		@Override
		public DataBinding<Icon> getIcon() {
			if (icon == null) {
				icon = new DataBinding<Icon>(iterator, Icon.class, DataBinding.BindingDefinitionType.GET);
			}
			return icon;
		}

		@Override
		public void setIcon(DataBinding<Icon> icon) {
			FIBPropertyNotification<DataBinding<Icon>> notification = requireChange(ICON_KEY, icon);
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

		@Override
		public DataBinding<String> getTooltip() {
			if (tooltip == null) {
				tooltip = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET);
			}
			return tooltip;
		}

		@Override
		public void setTooltip(DataBinding<String> tooltip) {
			if (tooltip != null) {
				tooltip.setOwner(iterator);
				tooltip.setDeclaredType(String.class);
				tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.tooltip = tooltip;
		}

		@Override
		public DataBinding<Boolean> getEnabled() {
			if (enabled == null) {
				enabled = new DataBinding<Boolean>(iterator, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return enabled;
		}

		@Override
		public void setEnabled(DataBinding<Boolean> enabled) {
			if (enabled != null) {
				enabled.setOwner(iterator);
				enabled.setDeclaredType(Boolean.class);
				enabled.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.enabled = enabled;
		}

		@Override
		public DataBinding<Boolean> getVisible() {
			if (visible == null) {
				visible = new DataBinding<Boolean>(iterator, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return visible;
		}

		@Override
		public void setVisible(DataBinding visible) {
			if (visible != null) {
				visible.setOwner(iterator);
				visible.setDeclaredType(Boolean.class);
				visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.visible = visible;
		}

		@Override
		public boolean getIsEditable() {
			return isEditable;
		}

		@Override
		public void setIsEditable(boolean isEditable) {
			FIBPropertyNotification<Boolean> notification = requireChange(IS_EDITABLE_KEY, isEditable);
			if (notification != null) {
				this.isEditable = isEditable;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<String> getEditableLabel() {
			if (editableLabel == null) {
				editableLabel = new DataBinding<String>(iterator, String.class, DataBinding.BindingDefinitionType.GET_SET);
			}
			return editableLabel;
		}

		@Override
		public void setEditableLabel(DataBinding<String> editableLabel) {
			FIBPropertyNotification<DataBinding<String>> notification = requireChange(EDITABLE_LABEL_KEY, editableLabel);
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

		@Override
		public boolean getFiltered() {
			return filtered;
		}

		@Override
		public void setFiltered(boolean filtered) {
			FIBPropertyNotification<Boolean> notification = requireChange(FILTERED_KEY, filtered);
			if (notification != null) {
				this.filtered = filtered;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getDefaultVisible() {
			return defaultVisible;
		}

		@Override
		public void setDefaultVisible(boolean defaultVisible) {
			FIBPropertyNotification<Boolean> notification = requireChange(DEFAULT_VISIBLE_KEY, defaultVisible);
			if (notification != null) {
				this.defaultVisible = defaultVisible;
				hasChanged(notification);
			}
		}

		@Override
		public void finalizeBrowserDeserialization() {
			logger.fine("finalizeBrowserDeserialization() for FIBBrowserElement " + getDataClass());
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
			for (FIBBrowserElementChildren c : getChildren()) {
				c.finalizeBrowserDeserialization();
			}
		}

		@Override
		public BindingModel getBindingModel() {
			if (getOwner() != null) {
				return getOwner().getBindingModel();
			}
			return null;
		}

		@Override
		public void updateBindingModel() {
			actionBindingModel = null;
		}

		@Override
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

		@Override
		public void notifiedBindingModelRecreated() {
			createActionBindingModel();
		}

		@Override
		public Font retrieveValidFont() {
			if (font == null && getOwner() != null) {
				return getOwner().retrieveValidFont();
			}
			return getFont();
		}

		@Override
		public Font getFont() {
			return font;
		}

		@Override
		public void setFont(Font font) {
			FIBPropertyNotification<Font> notification = requireChange(FONT_KEY, font);
			if (notification != null) {
				this.font = font;
				hasChanged(notification);
			}
		}

		@Override
		public boolean getHasSpecificFont() {
			return getFont() != null;
		}

		@Override
		public void setHasSpecificFont(boolean aFlag) {
			if (aFlag) {
				setFont(retrieveValidFont());
			} else {
				setFont(null);
			}
		}

		@Override
		public DataBinding<Font> getDynamicFont() {
			if (dynamicFont == null) {
				dynamicFont = new DataBinding<Font>(iterator, Font.class, DataBinding.BindingDefinitionType.GET);
			}
			return dynamicFont;
		}

		@Override
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
			FIBPropertyNotification<File> notification = requireChange(IMAGE_ICON_FILE_KEY, imageIconFile);
			if (notification != null) {
				this.imageIconFile = imageIconFile;
				this.imageIcon = new ImageIcon(imageIconFile.getAbsolutePath());
				hasChanged(notification);
			}
		}

		@Override
		public ImageIcon getImageIcon() {
			return imageIcon;
		}

		@Override
		public FIBBrowserElementIterator getIterator() {
			return iterator;
		}

		private class FIBBrowserElementIterator implements Bindable {
			private BindingModel iteratorBindingModel = null;

			@Override
			public BindingModel getBindingModel() {
				if (iteratorBindingModel == null) {
					createFormatterBindingModel();
				}
				return iteratorBindingModel;
			}

			private void createFormatterBindingModel() {
				iteratorBindingModel = new BindingModel(FIBBrowserElementImpl.this.getBindingModel());
				iteratorBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
					@Override
					public Type getType() {
						return getDataClass();
					}

					@Override
					public String getVariableName() {
						return FIBBrowserElementImpl.this.getName();
					}
				});
			}

			public FIBComponent getComponent() {
				return FIBBrowserElementImpl.this.getComponent();
			}

			@Override
			public BindingFactory getBindingFactory() {
				return getComponent().getBindingFactory();
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			}

		}

		@Override
		public Class<?> getDataClass() {
			Class<?> returned = (Class<?>) performSuperGetter(DATA_CLASS_KEY);
			if (returned == null && getOwner() != null) {
				return getOwner().getIteratorClass();
			}
			return returned;
		}

		@Override
		public void setDataClass(Class<?> dataClass) {
			System.out.println("For browser element " + getName() + " set data class " + dataClass);
			performSuperSetter(DATA_CLASS_KEY, dataClass);
			/*FIBPropertyNotification<Class> notification = requireChange(DATA_CLASS_KEY, dataClass);
			if (notification != null) {
				this.dataClass = dataClass;
				hasChanged(notification);
			}*/
		}

		/*@Override
		public List<FIBBrowserAction> getActions() {
			return actions;
		}

		@Override
		public void setActions(List<FIBBrowserAction> actions) {
			this.actions = actions;
		}

		@Override
		public void addToActions(FIBBrowserAction anAction) {
			logger.fine("Add to actions " + anAction);
			anAction.setOwner(this);
			actions.add(anAction);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, actions);
		}

		@Override
		public void removeFromActions(FIBBrowserAction anAction) {
			anAction.setOwner(null);
			actions.remove(anAction);
			getPropertyChangeSupport().firePropertyChange(ACTIONS_KEY, null, actions);
		}*/

		public FIBAddAction createAddAction() {
			FIBAddAction newAction = getFactory().newInstance(FIBAddAction.class);
			newAction.setName("add_action");
			addToActions(newAction);
			return newAction;
		}

		public FIBRemoveAction createRemoveAction() {
			FIBRemoveAction newAction = getFactory().newInstance(FIBRemoveAction.class);
			newAction.setName("delete_action");
			addToActions(newAction);
			return newAction;
		}

		public FIBCustomAction createCustomAction() {
			FIBCustomAction newAction = getFactory().newInstance(FIBCustomAction.class);
			newAction.setName("custom_action");
			addToActions(newAction);
			return newAction;
		}

		public FIBBrowserAction deleteAction(FIBBrowserAction actionToDelete) {
			logger.info("Called deleteAction() with " + actionToDelete);
			removeFromActions(actionToDelete);
			return actionToDelete;
		}

		/*@Override
		public List<FIBBrowserElementChildren> getChildren() {
			return children;
		}

		@Override
		public void setChildren(List<FIBBrowserElementChildren> children) {
			this.children = children;
		}

		@Override
		public void addToChildren(FIBBrowserElementChildren aChildren) {
			aChildren.setOwner(this);
			children.add(aChildren);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, children);
		}

		@Override
		public void removeFromChildren(FIBBrowserElementChildren aChildren) {
			aChildren.setOwner(null);
			children.remove(aChildren);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, children);
		}*/

		public FIBBrowserElementChildren createChildren() {
			logger.info("Called createChildren()");
			FIBBrowserElementChildren newChildren = getFactory().newInstance(FIBBrowserElementChildren.class);
			newChildren.setName("children" + (getChildren().size() > 0 ? getChildren().size() : ""));
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
			getChildren().remove(e);
			getChildren().add(0, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		public void moveUp(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			int index = getChildren().indexOf(e);
			getChildren().remove(e);
			getChildren().add(index - 1, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		public void moveDown(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			int index = getChildren().indexOf(e);
			getChildren().remove(e);
			getChildren().add(index + 1, e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		public void moveToBottom(FIBBrowserElementChildren e) {
			if (e == null) {
				return;
			}
			getChildren().remove(e);
			getChildren().add(e);
			getPropertyChangeSupport().firePropertyChange(CHILDREN_KEY, null, getChildren());
		}

		@Override
		public String toString() {
			return "FIBBrowserElement(name" + getName() + ",type=" + getDataClass() + ")";
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

	@ModelEntity
	@ImplementationClass(FIBBrowserElementChildren.FIBBrowserElementChildrenImpl.class)
	@XMLElement(xmlTag = "Children")
	public static interface FIBBrowserElementChildren extends FIBModelObject {

		@PropertyIdentifier(type = DataBinding.class)
		public static final String OWNER_KEY = "owner";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String DATA_KEY = "data";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String VISIBLE_KEY = "visible";
		@PropertyIdentifier(type = DataBinding.class)
		public static final String CAST_KEY = "cast";

		@Getter(value = OWNER_KEY, inverse = FIBBrowserElement.CHILDREN_KEY)
		public FIBBrowserElement getOwner();

		@Setter(OWNER_KEY)
		public void setOwner(FIBBrowserElement customColumn);

		@Getter(value = DATA_KEY)
		@XMLAttribute
		public DataBinding<Object> getData();

		@Setter(DATA_KEY)
		public void setData(DataBinding<Object> data);

		@Getter(value = VISIBLE_KEY)
		@XMLAttribute
		public DataBinding<Boolean> getVisible();

		@Setter(VISIBLE_KEY)
		public void setVisible(DataBinding<Boolean> visible);

		@Getter(value = CAST_KEY)
		@XMLAttribute
		public DataBinding<Object> getCast();

		@Setter(CAST_KEY)
		public void setCast(DataBinding<Object> cast);

		public void finalizeBrowserDeserialization();

		public boolean isMultipleAccess();

		public ImageIcon getImageIcon();

		public Class<?> getBaseClass();

		public Type getAccessedType();

		public static abstract class FIBBrowserElementChildrenImpl extends FIBModelObjectImpl implements FIBBrowserElementChildren {

			private static final Logger logger = Logger.getLogger(FIBBrowserElementChildren.class.getPackage().getName());

			// private FIBBrowserElement browserElement;
			private DataBinding<Object> data;
			private DataBinding<Boolean> visible;
			private DataBinding<Object> cast;
			private FIBChildBindable childBindable;

			@Deprecated
			public static BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET, false);
			@Deprecated
			public static BindingDefinition VISIBLE = new BindingDefinition("visible", Boolean.class,
					DataBinding.BindingDefinitionType.GET, false);
			@Deprecated
			public static BindingDefinition CAST = new BindingDefinition("cast", Object.class, DataBinding.BindingDefinitionType.GET, false);

			private class FIBChildBindable implements Bindable {
				private BindingModel childBindingModel = null;

				@Override
				public BindingFactory getBindingFactory() {
					return FIBBrowserElementChildrenImpl.this.getBindingFactory();
				}

				@Override
				public BindingModel getBindingModel() {
					if (childBindingModel == null) {
						createChildBindingModel();
					}
					return childBindingModel;
				}

				private void createChildBindingModel() {
					childBindingModel = new BindingModel(FIBBrowserElementChildrenImpl.this.getBindingModel());
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

				public FIBComponent getComponent() {
					return getOwner().getComponent();
				}

				@Override
				public void notifiedBindingChanged(DataBinding<?> dataBinding) {
				}

				@Override
				public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
				}

			}

			public FIBChildBindable getChildBindable() {
				if (childBindable == null) {
					childBindable = new FIBChildBindable();
				}
				return childBindable;
			}

			public FIBBrowser getBrowser() {
				return getOwner().getOwner();
			}

			@Override
			public DataBinding<Object> getData() {
				if (data == null) {
					data = new DataBinding<Object>(getOwner() != null ? getOwner().getIterator() : null, Object.class,
							DataBinding.BindingDefinitionType.GET);
				}
				return data;
			}

			@Override
			public void setData(DataBinding<Object> data) {
				if (data != null) {
					data.setOwner(getOwner() != null ? getOwner().getIterator() : null);
					data.setDeclaredType(Object.class);
					data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				}
				this.data = data;
			}

			@Override
			public DataBinding<Boolean> getVisible() {
				if (visible == null) {
					visible = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
				}
				return visible;
			}

			@Override
			public void setVisible(DataBinding<Boolean> visible) {
				if (visible != null) {
					visible.setOwner(getOwner() != null ? getOwner().getIterator() : null);
					visible.setDeclaredType(Boolean.class);
					visible.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				}
				this.visible = visible;
			}

			@Override
			public DataBinding<Object> getCast() {
				if (cast == null) {
					cast = new DataBinding<Object>(getChildBindable(), Object.class, DataBinding.BindingDefinitionType.GET);
				}
				return cast;
			}

			@Override
			public void setCast(DataBinding<Object> cast) {
				cast.setOwner(getChildBindable());
				cast.setDeclaredType(Object.class);
				cast.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
				this.cast = cast;
			}

			@Override
			public FIBComponent getComponent() {
				if (getOwner() != null) {
					return getOwner().getComponent();
				}
				return null;
			}

			@Override
			public void finalizeBrowserDeserialization() {
				logger.fine("finalizeBrowserDeserialization() for FIBBrowserElementChildren ");
				if (data != null) {
					data.setOwner(getOwner().getIterator());
					data.decode();
				}
				if (visible != null) {
					visible.setOwner(getOwner().getIterator());
					visible.decode();
				}
			}

			@Override
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

			@Override
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

			@Override
			public Type getAccessedType() {
				if (data != null && data.isSet()) {
					return data.getAnalyzedType();
				}
				return null;
			}

			@Override
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
	}

}
