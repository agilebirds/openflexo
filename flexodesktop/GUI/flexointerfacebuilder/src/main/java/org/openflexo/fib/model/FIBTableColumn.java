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

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;

public abstract class FIBTableColumn extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBTableColumn.class.getPackage().getName());

	private FIBTable table;

	public static enum Parameters implements FIBModelAttribute {
		data, format, tooltip, title, tooltipText, columnWidth, resizable, displayTitle, font, color, valueChangedAction

	}

	public static enum ColumnType {
		CheckBox, Custom, DropDown, Icon, Label, Number, TextField
	}

	private static BindingDefinition DATA = new BindingDefinition("data", Object.class, BindingDefinitionType.GET, false);
	private static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, BindingDefinitionType.GET, false);
	private static BindingDefinition FORMAT = new BindingDefinition("format", String.class, BindingDefinitionType.GET, false);
	private static BindingDefinition COLOR = new BindingDefinition("color", Color.class, BindingDefinitionType.GET, false);
	private static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
			BindingDefinitionType.EXECUTE, false);

	private DataBinding data;
	private DataBinding format;
	private DataBinding tooltip;
	private DataBinding color;
	private String title;
	private String tooltipText;
	private int columnWidth = 100;
	private boolean resizable = true;
	private boolean displayTitle = true;
	private Font font;
	private DataBinding valueChangedAction;

	private final FIBFormatter formatter;

	public FIBTableColumn() {
		formatter = new FIBFormatter();
	}

	public FIBTable getTable() {
		return table;
	}

	public void setTable(FIBTable table) {
		this.table = table;
	}

	public BindingDefinition getDataBindingDefinition() {
		return DATA;
	}

	public BindingDefinition getTooltipBindingDefinition() {
		return TOOLTIP;
	}

	public BindingDefinition getFormatBindingDefinition() {
		return FORMAT;
	}

	public BindingDefinition getColorBindingDefinition() {
		return COLOR;
	}

	public BindingDefinition getValueChangedActionBindingDefinition() {
		return VALUE_CHANGED_ACTION;
	}

	@Override
	public FIBComponent getRootComponent() {
		if (getTable() != null) {
			return getTable().getRootComponent();
		}
		return null;
	}

	public DataBinding getData() {
		if (data == null) {
			data = new DataBinding(this, Parameters.data, DATA);
		}
		return data;
	}

	public void setData(DataBinding data) {
		data.setOwner(this);
		data.setBindingAttribute(Parameters.data);
		data.setBindingDefinition(DATA);
		this.data = data;
	}

	public void finalizeTableDeserialization() {
		logger.fine("finalizeDeserialization() for FIBTableColumn " + title);
		if (data != null) {
			data.finalizeDeserialization();
		}
		if (tooltip != null) {
			tooltip.finalizeDeserialization();
		}
	}

	@Override
	public BindingModel getBindingModel() {
		if (getTable() != null) {
			return getTable().getTableBindingModel();
		}
		return null;
	}

	public Type getDataClass() {
		if (getData() != null && getData().getBinding() != null && getData().getBinding() != null) {
			return getData().getBinding().getAccessedType();
		}
		return getDefaultDataClass();
	}

	public abstract Type getDefaultDataClass();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		FIBAttributeNotification<String> notification = requireChange(Parameters.title, title);
		if (notification != null) {
			this.title = title;
			hasChanged(notification);
		}
	}

	public Integer getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(Integer columnWidth) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.columnWidth, columnWidth);
		if (notification != null) {
			this.columnWidth = columnWidth;
			hasChanged(notification);
		}
	}

	public Boolean getResizable() {
		return resizable;
	}

	public void setResizable(Boolean resizable) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.resizable, resizable);
		if (notification != null) {
			this.resizable = resizable;
			hasChanged(notification);
		}
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.displayTitle, displayTitle);
		if (notification != null) {
			this.displayTitle = displayTitle;
			hasChanged(notification);
		}
	}

	public Font retrieveValidFont() {
		if (font == null && getTable() != null) {
			return getTable().retrieveValidFont();
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

	public abstract ColumnType getColumnType();

	public DataBinding getFormat() {
		if (format == null) {
			format = new DataBinding(formatter, Parameters.format, FORMAT);
		}
		return format;
	}

	public void setFormat(DataBinding format) {
		format.setOwner(formatter);
		format.setBindingAttribute(Parameters.format);
		format.setBindingDefinition(FORMAT);
		this.format = format;
	}

	public FIBFormatter getFormatter() {
		return formatter;
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
			formatterBindingModel = new BindingModel();
			formatterBindingModel.addToBindingVariables(new BindingVariableImpl<Object>(this, "object", Object.class) {
				@Override
				public Type getType() {
					return getDataClass();
				}
			});
		}

		@Override
		public FIBComponent getRootComponent() {
			return FIBTableColumn.this.getRootComponent();
		}

		@Override
		public List<? extends FIBModelObject> getEmbeddedObjects() {
			return null;
		}

	}

	public int getIndex() {
		if (getTable() != null) {
			return getTable().getColumns().indexOf(this);
		}
		return -1;
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

	public DataBinding getTooltip() {
		if (tooltip == null) {
			tooltip = new DataBinding(this, Parameters.tooltip, TOOLTIP);
		}
		return tooltip;
	}

	public void setTooltip(DataBinding tooltip) {
		tooltip.setOwner(this);
		tooltip.setBindingAttribute(Parameters.tooltip);
		tooltip.setBindingDefinition(TOOLTIP);
		this.tooltip = tooltip;
	}

	public DataBinding getColor() {
		if (color == null) {
			color = new DataBinding(this, Parameters.color, COLOR);
		}
		return color;
	}

	public void setColor(DataBinding color) {
		color.setOwner(this);
		color.setBindingAttribute(Parameters.color);
		color.setBindingDefinition(COLOR);
		this.color = color;
	}

	public DataBinding getValueChangedAction() {
		if (valueChangedAction == null) {
			valueChangedAction = new DataBinding(this, Parameters.valueChangedAction, VALUE_CHANGED_ACTION);
		}
		return valueChangedAction;
	}

	public void setValueChangedAction(DataBinding valueChangedAction) {
		valueChangedAction.setOwner(this);
		valueChangedAction.setBindingAttribute(Parameters.valueChangedAction);
		valueChangedAction.setBindingDefinition(VALUE_CHANGED_ACTION);
		this.valueChangedAction = valueChangedAction;
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

}
