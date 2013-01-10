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
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;

public abstract class FIBTableColumn extends FIBModelObject {

	private static final Logger logger = Logger.getLogger(FIBTableColumn.class.getPackage().getName());

	private FIBTableComponent table;

	public static enum Parameters implements FIBModelAttribute {
		data, format, tooltip, title, tooltipText, columnWidth, resizable, displayTitle, font, color, bgColor, valueChangedAction

	}

	public static enum ColumnType {
		CheckBox, Custom, DropDown, Icon, Label, Number, TextField, Button;
	}

	@Deprecated
	private static BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private static BindingDefinition FORMAT = new BindingDefinition("format", String.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private static BindingDefinition COLOR = new BindingDefinition("color", Color.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private static BindingDefinition BG_COLOR = new BindingDefinition("bgColor", Color.class, DataBinding.BindingDefinitionType.GET, false);
	@Deprecated
	private static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
			DataBinding.BindingDefinitionType.EXECUTE, false);

	private DataBinding<Object> data;
	private DataBinding<String> format;
	private DataBinding<String> tooltip;
	private DataBinding<Color> color;
	private DataBinding<Color> bgColor;
	private String title;
	private String tooltipText;
	private int columnWidth = 100;
	private boolean resizable = true;
	private boolean displayTitle = true;
	private Font font;
	private DataBinding<Void> valueChangedAction;

	private final FIBFormatter formatter;

	public FIBTableColumn() {
		formatter = new FIBFormatter();
	}

	public FIBTableComponent getTable() {
		return table;
	}

	public void setTable(FIBTableComponent table) {
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

	public BindingDefinition getBgColorBindingDefinition() {
		return BG_COLOR;
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

	public DataBinding<Object> getData() {
		if (data == null) {
			data = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
		}
		return data;
	}

	public void setData(DataBinding<Object> data) {
		if (data != null) {
			data.setOwner(this);
			data.setDeclaredType(Object.class);
			data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.data = data;
	}

	public void finalizeTableDeserialization() {
		logger.fine("finalizeDeserialization() for FIBTableColumn " + title);
		if (data != null) {
			data.decode();
		}
		if (tooltip != null) {
			tooltip.decode();
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
		if (getData() != null) {
			return getData().getAnalyzedType();
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

	public DataBinding<String> getFormat() {
		if (format == null) {
			format = new DataBinding<String>(formatter, String.class, DataBinding.BindingDefinitionType.GET);
		}
		return format;
	}

	public void setFormat(DataBinding<String> format) {
		if (format != null) {
			format.setOwner(formatter);
			format.setDeclaredType(String.class);
			format.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
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
			formatterBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
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

	public DataBinding<Color> getColor() {
		if (color == null) {
			color = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
		}
		return color;
	}

	public void setColor(DataBinding<Color> color) {
		if (color != null) {
			color.setOwner(this);
			color.setDeclaredType(Color.class);
			color.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.color = color;
	}

	public DataBinding<Color> getBgColor() {
		if (bgColor == null) {
			bgColor = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
		}
		return bgColor;
	}

	public void setBgColor(DataBinding<Color> bgColor) {
		if (bgColor != null) {
			bgColor.setOwner(this);
			bgColor.setDeclaredType(Color.class);
			bgColor.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.bgColor = bgColor;
	}

	public DataBinding<Void> getValueChangedAction() {
		if (valueChangedAction == null) {
			valueChangedAction = new DataBinding<Void>(this, Void.class, DataBinding.BindingDefinitionType.EXECUTE);
		}
		return valueChangedAction;
	}

	public void setValueChangedAction(DataBinding<Void> valueChangedAction) {
		if (valueChangedAction != null) {
			valueChangedAction.setOwner(this);
			valueChangedAction.setDeclaredType(Void.class);
			valueChangedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
		}
		this.valueChangedAction = valueChangedAction;
	}

	@Override
	public List<? extends FIBModelObject> getEmbeddedObjects() {
		return null;
	}

}
