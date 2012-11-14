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
package org.openflexo.foundation.dm.action;

import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionExecutionFailed;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.BindingVariable;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.bindings.MethodCall;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.Typed;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.ColumnIsNotEmpty;
import org.openflexo.foundation.ie.widget.IEAbstractListWidget;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEEditableTextWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.RowIsNotEmpty;
import org.openflexo.localization.FlexoLocalization;

public class CreateComponentFromEntity extends FlexoAction<CreateComponentFromEntity, DMEntity, DMEntity> {

	public static class DMAccessorWidget {

		public WidgetType widget;
		public DMProperty property;
		public DMMethod method;

		/**
		 * Overrides toString
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "DMAccessorWidget: accessor=" + (property != null ? property : method) + " widget=" + widget;
		}
	}

	static final Logger logger = Logger.getLogger(CreateComponentFromEntity.class.getPackage().getName());

	public static FlexoActionType<CreateComponentFromEntity, DMEntity, DMEntity> actionType = new FlexoActionType<CreateComponentFromEntity, DMEntity, DMEntity>(
			"create_component_from_entity", FlexoActionType.exportMenu, FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public CreateComponentFromEntity makeNewAction(DMEntity focusedObject, Vector<DMEntity> globalSelection, FlexoEditor editor) {
			return new CreateComponentFromEntity(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMEntity object, Vector<DMEntity> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DMEntity object, Vector<DMEntity> globalSelection) {
			return object != null
					&& (globalSelection == null || globalSelection.size() == 0 || globalSelection.size() == 1
							&& globalSelection.firstElement() == object);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMEntity.class);
	}

	private ComponentType componentType;

	private String name;

	private Vector<DMAccessorWidget> widgets;

	private boolean copyDescription = true;

	protected CreateComponentFromEntity(DMEntity focusedObject, Vector<DMEntity> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @throws ColumnIsNotEmpty
	 * @throws RowIsNotEmpty
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException, ColumnIsNotEmpty, RowIsNotEmpty {
		if (getFocusedObject() == null) {
			throw new NullPointerException("No entity selected");
		}
		AddComponent addComponent = AddComponent.actionType.makeNewEmbeddedAction(getFocusedObject().getProject()
				.getFlexoComponentLibrary().getRootFolder(), null, this);
		addComponent.setComponentType(getComponentType());
		addComponent.setNewComponentName(getName());
		addComponent.doAction();
		if (!addComponent.hasActionExecutionSucceeded()) {
			throw new FlexoActionExecutionFailed(addComponent);
		}
		ComponentBindingDefinition cbd = addComponent.getNewComponent().createNewBinding();
		cbd.setVariableName(getVariableName());
		cbd.setIsMandatory(true);
		cbd.setIsSettable(true);
		cbd.setType(DMType.makeResolvedDMType(getFocusedObject()));
		DropIEElement dropBlock = DropIEElement.actionType.makeNewEmbeddedAction(addComponent.getNewComponent().getWOComponent(), null,
				this);
		dropBlock.setElementType(WidgetType.BLOCK);
		dropBlock.setContainer(addComponent.getNewComponent().getWOComponent().getRootSequence());
		dropBlock.setIndex(0);
		dropBlock.doAction();
		if (!dropBlock.hasActionExecutionSucceeded()) {
			throw new FlexoActionExecutionFailed(dropBlock);
		}
		((IEBlocWidget) dropBlock.getDroppedWidget()).setTitle(FlexoLocalization.localizedForKey("edit") + " " + cbd.getVariableName());
		DropIEElement dropTable = DropIEElement.actionType.makeNewEmbeddedAction(dropBlock.getDroppedWidget(), null, this);
		dropTable.setElementType(WidgetType.HTMLTable);
		dropTable.setContainer(dropBlock.getDroppedWidget());
		dropTable.doAction();
		if (!dropTable.hasActionExecutionSucceeded()) {
			throw new FlexoActionExecutionFailed(dropTable);
		}
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		table.setColCount(4);
		int rows = (getWidgets().size() + 1) / 2;
		table.setRowCount(rows);
		for (int i = 0; i < getWidgets().size(); i++) {
			DMAccessorWidget aw = getWidgets().get(i);
			if (aw.widget == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No widget selected for " + aw);
				}
				continue;
			}
			String label;
			String description;
			Map<String, String> descriptions;
			if (aw.property != null) {
				label = extractLabel(aw.property.getName());
				description = aw.property.getDescription();
				descriptions = aw.property.getSpecificDescriptions();
				// The next snippet is a little hack so that attributes declared
				// as boolean ends up bound on the methods that really returns a
				// boolean (instead of the default accessor that return a
				// String)
				if (aw.property instanceof DMEOAttribute) {
					if (((DMEOAttribute) aw.property).isBoolean()) {
						DMProperty p = aw.property.getEntity().getDMProperty(getName() + DMEOAttribute.BOOLEAN_METHOD_POSTFIX);
						if (p != null) {
							aw.property = p;
						}
					}
				}
			} else if (aw.method != null) {
				label = extractLabel(aw.method.getName());
				description = aw.method.getDescription();
				descriptions = aw.method.getSpecificDescriptions();
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Left a widget without property nor method");
				}
				continue;
			}
			// Here nor label nor widget are null, `continue´ calls automatically skip those
			DropIEElement dropLabel = DropIEElement.actionType.makeNewEmbeddedAction(addComponent.getNewComponent().getWOComponent(), null,
					this);
			dropLabel.setElementType(WidgetType.LABEL);
			dropLabel.setContainer(table.getTDAt(i < rows ? i : i - rows, i < rows ? 0 : 2).getSequenceWidget());
			dropLabel.setIndex(0);
			dropLabel.doAction();
			if (!dropLabel.hasActionExecutionSucceeded()) {
				throw new FlexoActionExecutionFailed(dropLabel);
			}
			((IELabelWidget) dropLabel.getDroppedWidget()).setValue(label + ":");

			DropIEElement dropWidget = DropIEElement.actionType.makeNewEmbeddedAction(addComponent.getNewComponent().getWOComponent(),
					null, this);
			dropWidget.setElementType(aw.widget);
			dropWidget.setContainer(table.getTDAt(i < rows ? i : i - rows, i < rows ? 1 : 3).getSequenceWidget());
			dropWidget.setIndex(0);
			dropWidget.doAction();
			if (!dropWidget.hasActionExecutionSucceeded()) {
				throw new FlexoActionExecutionFailed(dropLabel);
			}
			dropWidget.getDroppedWidget().setLabel(label);
			if (copyDescription) {
				dropWidget.getDroppedWidget().setDescription(description);
				dropWidget.getDroppedWidget().setSpecificDescriptions(descriptions);
			}
			IEWidget droppedWidget = dropWidget.getDroppedWidget();
			BindingVariable var = addComponent.getNewComponent().getBindingModel().getBindingVariableAt(0);
			switch (aw.widget) {
			case BROWSER:
			case DROPDOWN:
				BindingValue bv = new BindingValue(((IEAbstractListWidget) droppedWidget).getBindingSelectionDefinition(), droppedWidget);
				bv.setBindingVariable(var);
				DMProperty property = var.getType().getBaseEntity().getProperty(cbd.getVariableName());
				bv.addBindingPathElement(property);
				bv.addBindingPathElement(aw.property != null ? aw.property : new MethodCall(bv, aw.method));
				((IEAbstractListWidget) droppedWidget).setBindingSelection(bv);
				break;
			case STRING:
				bv = new BindingValue(((IEStringWidget) droppedWidget).getBindingValueDefinition(), droppedWidget);
				bv.setBindingVariable(var);
				property = var.getType().getBaseEntity().getProperty(cbd.getVariableName());
				bv.addBindingPathElement(property);
				bv.addBindingPathElement(aw.property != null ? aw.property : new MethodCall(bv, aw.method));
				((IEStringWidget) droppedWidget).setBindingValue(bv);
				Typed p = aw.property != null ? aw.property : aw.method;
				if (p.getType() == null) {
					break;
				}
				/*
				 * if(p.getType().isBoolean()) ((IEStringWidget)droppedWidget).setFieldType(TextFieldType.) else
				 */
				if (p.getType().isString() || p.getType().isChar()) {
					((IEStringWidget) droppedWidget).setFieldType(TextFieldType.TEXT);
				} else if (p.getType().isInteger()) {
					((IEStringWidget) droppedWidget).setFieldType(TextFieldType.INTEGER);
				} else if (p.getType().isFloat()) {
					((IEStringWidget) droppedWidget).setFieldType(TextFieldType.FLOAT);
				} else if (p.getType().isDouble()) {
					((IEStringWidget) droppedWidget).setFieldType(TextFieldType.DOUBLE);
				} else if (p.getType().isDate()) {
					((IEStringWidget) droppedWidget).setFieldType(TextFieldType.DATE);
				}
				break;
			case TEXTFIELD:
			case TEXTAREA:
				bv = new BindingValue(((IEEditableTextWidget) droppedWidget).getBindingValueDefinition(), droppedWidget);
				bv.setBindingVariable(var);
				property = var.getType().getBaseEntity().getProperty(cbd.getVariableName());
				bv.addBindingPathElement(property);
				bv.addBindingPathElement(aw.property != null ? aw.property : new MethodCall(bv, aw.method));
				((IEEditableTextWidget) droppedWidget).setBindingValue(bv);
				p = aw.property != null ? aw.property : aw.method;
				if (aw.widget == WidgetType.TEXTFIELD) {
					if (p.getType().isString() || p.getType().isChar()) {
						((IETextFieldWidget) droppedWidget).setFieldType(TextFieldType.TEXT);
					} else if (p.getType().isInteger()) {
						((IETextFieldWidget) droppedWidget).setFieldType(TextFieldType.INTEGER);
					} else if (p.getType().isFloat()) {
						((IETextFieldWidget) droppedWidget).setFieldType(TextFieldType.FLOAT);
					} else if (p.getType().isDouble()) {
						((IETextFieldWidget) droppedWidget).setFieldType(TextFieldType.DOUBLE);
					} else if (p.getType().isDate()) {
						((IETextFieldWidget) droppedWidget).setFieldType(TextFieldType.DATE);

					}
				}
				break;
			case CHECKBOX:
				bv = new BindingValue(((IECheckBoxWidget) droppedWidget).getBindingCheckedDefinition(), droppedWidget);
				bv.setBindingVariable(var);
				property = var.getType().getBaseEntity().getProperty(cbd.getVariableName());
				bv.addBindingPathElement(property);
				bv.addBindingPathElement(aw.property != null ? aw.property : new MethodCall(bv, aw.method));
				((IECheckBoxWidget) droppedWidget).setBindingChecked(bv);
				break;
			case RADIO:
				bv = new BindingValue(((IERadioButtonWidget) droppedWidget).getBindingCheckedDefinition(), droppedWidget);
				bv.setBindingVariable(var);
				property = var.getType().getBaseEntity().getProperty(cbd.getVariableName());
				bv.addBindingPathElement(property);
				bv.addBindingPathElement(aw.property != null ? aw.property : new MethodCall(bv, aw.method));
				((IERadioButtonWidget) droppedWidget).setBindingChecked(bv);
				break;
			default:
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unimplemented type for export " + aw.widget);
				}
				break;
			}
		}
	}

	/**
	 * @return
	 */
	public String getVariableName() {
		return extractNameFromEntity(getFocusedObject());
	}

	private String extractLabel(String name) {
		StringBuilder sb = new StringBuilder();
		boolean previousCharsAreUpperCase = false;
		while (name.startsWith("_")) {
			name = name.substring(1);
		}
		if (name.startsWith("get")) {
			name = name.substring(3);
		}
		if (name.startsWith("set")) {
			name = name.substring(3);
		}
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i == 0 || previousCharsAreUpperCase) {
					sb.append(c);
				} else {
					if (i + 1 < name.length() && Character.isLowerCase(name.charAt(i + 1))) {
						sb.append(' ').append(Character.toLowerCase(c));
					} else {
						if (previousCharsAreUpperCase) {
							sb.append(c);
						} else {
							sb.append(' ').append(c);
						}
					}
				}
				previousCharsAreUpperCase = true;
			} else {
				if (i == 0) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(c);
				}
				previousCharsAreUpperCase = false;
			}

		}
		return sb.toString();
	}

	private String extractNameFromEntity(DMEntity entity) {
		String name = entity.getName();
		int i = 0;
		while (i + 1 < name.length() && Character.isUpperCase(name.charAt(i + 1))) {
			i++;
		}
		return Character.toLowerCase(name.charAt(i)) + name.substring(i + 1);
	}

	public ComponentType getComponentType() {
		if (componentType == null) {
			return ComponentType.OPERATION_COMPONENT;
		}
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<DMAccessorWidget> getWidgets() {
		return widgets;
	}

	public void setWidgets(Vector<DMAccessorWidget> widgets) {
		this.widgets = widgets;
	}

	public boolean getCopyDescription() {
		return copyDescription;
	}

	public void setCopyDescription(boolean copyDescription) {
		this.copyDescription = copyDescription;
	}

}
