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
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTableColumn.FIBTableColumnImpl.class)
public abstract interface FIBTableColumn extends FIBModelObject {

	public static enum ColumnType {
		CheckBox, Custom, DropDown, Icon, Label, Number, TextField, Button;
	}

	@PropertyIdentifier(type = FIBTable.class)
	public static final String OWNER_KEY = "owner";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DATA_KEY = "data";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String FORMAT_KEY = "format";
	@PropertyIdentifier(type = String.class)
	public static final String TITLE_KEY = "title";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMN_WIDTH_KEY = "columnWidth";
	@PropertyIdentifier(type = Boolean.class)
	public static final String RESIZABLE_KEY = "resizable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String DISPLAY_TITLE_KEY = "displayTitle";
	@PropertyIdentifier(type = Font.class)
	public static final String FONT_KEY = "font";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String COLOR_KEY = "color";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String BG_COLOR_KEY = "bgColor";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String TOOLTIP_KEY = "tooltip";
	@PropertyIdentifier(type = String.class)
	public static final String TOOLTIP_TEXT_KEY = "tooltipText";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_CHANGED_ACTION_KEY = "valueChangedAction";

	@Getter(value = OWNER_KEY, inverse = FIBTable.COLUMNS_KEY)
	public FIBTable getOwner();

	@Setter(OWNER_KEY)
	public void setOwner(FIBTable customColumn);

	@Getter(value = DATA_KEY)
	@XMLAttribute
	public DataBinding<?> getData();

	@Setter(DATA_KEY)
	public void setData(DataBinding<?> data);

	@Getter(value = FORMAT_KEY)
	@XMLAttribute
	public DataBinding<String> getFormat();

	@Setter(FORMAT_KEY)
	public void setFormat(DataBinding<String> format);

	@Getter(value = TITLE_KEY)
	@XMLAttribute
	public String getTitle();

	@Setter(TITLE_KEY)
	public void setTitle(String title);

	@Getter(value = COLUMN_WIDTH_KEY)
	@XMLAttribute
	public Integer getColumnWidth();

	@Setter(COLUMN_WIDTH_KEY)
	public void setColumnWidth(Integer columnWidth);

	@Getter(value = RESIZABLE_KEY)
	@XMLAttribute
	public Boolean getResizable();

	@Setter(RESIZABLE_KEY)
	public void setResizable(Boolean resizable);

	@Getter(value = DISPLAY_TITLE_KEY)
	@XMLAttribute
	public Boolean getDisplayTitle();

	@Setter(DISPLAY_TITLE_KEY)
	public void setDisplayTitle(Boolean displayTitle);

	@Getter(value = FONT_KEY)
	@XMLAttribute
	public Font getFont();

	@Setter(FONT_KEY)
	public void setFont(Font font);

	@Getter(value = COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getColor();

	@Setter(COLOR_KEY)
	public void setColor(DataBinding<Color> color);

	@Getter(value = BG_COLOR_KEY)
	@XMLAttribute
	public DataBinding<Color> getBgColor();

	@Setter(BG_COLOR_KEY)
	public void setBgColor(DataBinding<Color> bgColor);

	@Getter(value = TOOLTIP_KEY)
	@XMLAttribute
	public DataBinding<String> getTooltip();

	@Setter(TOOLTIP_KEY)
	public void setTooltip(DataBinding<String> tooltip);

	@Getter(value = TOOLTIP_TEXT_KEY)
	@XMLAttribute
	public String getTooltipText();

	@Setter(TOOLTIP_TEXT_KEY)
	public void setTooltipText(String tooltipText);

	@Getter(value = VALUE_CHANGED_ACTION_KEY)
	@XMLAttribute
	public DataBinding<?> getValueChangedAction();

	@Setter(VALUE_CHANGED_ACTION_KEY)
	public void setValueChangedAction(DataBinding<?> valueChangedAction);

	public void finalizeTableDeserialization();

	public Font retrieveValidFont();

	public Type getDataClass();

	public static abstract class FIBTableColumnImpl extends FIBModelObjectImpl implements FIBTableColumn {

		private static final Logger logger = Logger.getLogger(FIBTableColumn.class.getPackage().getName());

		private FIBTableComponent table;

		@Deprecated
		private static BindingDefinition DATA = new BindingDefinition("data", Object.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		private static BindingDefinition TOOLTIP = new BindingDefinition("tooltip", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition FORMAT = new BindingDefinition("format", String.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition COLOR = new BindingDefinition("color", Color.class, DataBinding.BindingDefinitionType.GET, false);
		@Deprecated
		private static BindingDefinition BG_COLOR = new BindingDefinition("bgColor", Color.class, DataBinding.BindingDefinitionType.GET,
				false);
		@Deprecated
		private static BindingDefinition VALUE_CHANGED_ACTION = new BindingDefinition("valueChangedAction", Void.class,
				DataBinding.BindingDefinitionType.EXECUTE, false);

		private DataBinding<?> data;
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
		private DataBinding<?> valueChangedAction;

		private final FIBFormatter formatter;

		public FIBTableColumnImpl() {
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
		public FIBComponent getComponent() {
			if (getTable() instanceof FIBTable) {
				return ((FIBTable) getTable());
			}
			return null;
		}

		@Override
		public DataBinding<?> getData() {
			if (data == null) {
				data = new DataBinding<Object>(this, Object.class, DataBinding.BindingDefinitionType.GET);
			}
			return data;
		}

		@Override
		public void setData(DataBinding<?> data) {
			if (data != null) {
				data.setOwner(this);
				data.setDeclaredType(Object.class);
				data.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.data = data;
		}

		@Override
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

		@Override
		public Type getDataClass() {
			if (getData() != null) {
				return getData().getAnalyzedType();
			}
			return getDefaultDataClass();
		}

		public abstract Type getDefaultDataClass();

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public void setTitle(String title) {
			FIBPropertyNotification<String> notification = requireChange(TITLE_KEY, title);
			if (notification != null) {
				this.title = title;
				hasChanged(notification);
			}
		}

		@Override
		public Integer getColumnWidth() {
			return columnWidth;
		}

		@Override
		public void setColumnWidth(Integer columnWidth) {
			FIBPropertyNotification<Integer> notification = requireChange(COLUMN_WIDTH_KEY, columnWidth);
			if (notification != null) {
				this.columnWidth = columnWidth;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getResizable() {
			return resizable;
		}

		@Override
		public void setResizable(Boolean resizable) {
			FIBPropertyNotification<Boolean> notification = requireChange(RESIZABLE_KEY, resizable);
			if (notification != null) {
				this.resizable = resizable;
				hasChanged(notification);
			}
		}

		@Override
		public Boolean getDisplayTitle() {
			return displayTitle;
		}

		@Override
		public void setDisplayTitle(Boolean displayTitle) {
			FIBPropertyNotification<Boolean> notification = requireChange(DISPLAY_TITLE_KEY, displayTitle);
			if (notification != null) {
				this.displayTitle = displayTitle;
				hasChanged(notification);
			}
		}

		@Override
		public Font retrieveValidFont() {
			if (font == null && getTable() != null) {
				return getTable().retrieveValidFont();
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

		@Override
		public DataBinding<String> getFormat() {
			if (format == null) {
				format = new DataBinding<String>(formatter, String.class, DataBinding.BindingDefinitionType.GET);
			}
			return format;
		}

		@Override
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

		private class FIBFormatter implements Bindable {
			private BindingModel formatterBindingModel = null;

			@Override
			public BindingModel getBindingModel() {
				if (formatterBindingModel == null) {
					createFormatterBindingModel();
				}
				return formatterBindingModel;
			}

			private void createFormatterBindingModel() {
				formatterBindingModel = new BindingModel(getTable().getTableBindingModel());
				formatterBindingModel.addToBindingVariables(new BindingVariable("object", Object.class) {
					@Override
					public Type getType() {
						return getDataClass();
					}
				});
			}

			public FIBComponent getComponent() {
				return FIBTableColumnImpl.this.getComponent();
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

		public int getIndex() {
			if (getTable() != null) {
				return getTable().getColumns().indexOf(this);
			}
			return -1;
		}

		@Override
		public String getTooltipText() {
			return tooltipText;
		}

		@Override
		public void setTooltipText(String tooltipText) {
			FIBPropertyNotification<String> notification = requireChange(TOOLTIP_TEXT_KEY, tooltipText);
			if (notification != null) {
				this.tooltipText = tooltipText;
				hasChanged(notification);
			}
		}

		@Override
		public DataBinding<String> getTooltip() {
			if (tooltip == null) {
				tooltip = new DataBinding<String>(this, String.class, DataBinding.BindingDefinitionType.GET);
			}
			return tooltip;
		}

		@Override
		public void setTooltip(DataBinding<String> tooltip) {
			if (tooltip != null) {
				tooltip.setOwner(this);
				tooltip.setDeclaredType(String.class);
				tooltip.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.tooltip = tooltip;
		}

		@Override
		public DataBinding<Color> getColor() {
			if (color == null) {
				color = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
			}
			return color;
		}

		@Override
		public void setColor(DataBinding<Color> color) {
			if (color != null) {
				color.setOwner(this);
				color.setDeclaredType(Color.class);
				color.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.color = color;
		}

		@Override
		public DataBinding<Color> getBgColor() {
			if (bgColor == null) {
				bgColor = new DataBinding<Color>(this, Color.class, DataBinding.BindingDefinitionType.GET);
			}
			return bgColor;
		}

		@Override
		public void setBgColor(DataBinding<Color> bgColor) {
			if (bgColor != null) {
				bgColor.setOwner(this);
				bgColor.setDeclaredType(Color.class);
				bgColor.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.bgColor = bgColor;
		}

		@Override
		public DataBinding<?> getValueChangedAction() {
			if (valueChangedAction == null) {
				valueChangedAction = new DataBinding<Void>(this, Void.class, DataBinding.BindingDefinitionType.EXECUTE);
			}
			return valueChangedAction;
		}

		@Override
		public void setValueChangedAction(DataBinding<?> valueChangedAction) {
			if (valueChangedAction != null) {
				valueChangedAction.setOwner(this);
				valueChangedAction.setDeclaredType(Void.class);
				valueChangedAction.setBindingDefinitionType(DataBinding.BindingDefinitionType.EXECUTE);
			}
			this.valueChangedAction = valueChangedAction;
		}

		@Override
		protected void applyValidation(ValidationReport report) {
			super.applyValidation(report);
			performValidation(DataBindingMustBeValid.class, report);
			performValidation(FormatBindingMustBeValid.class, report);
			performValidation(TooltipBindingMustBeValid.class, report);
			performValidation(ColorBindingMustBeValid.class, report);
			performValidation(BgColorBindingMustBeValid.class, report);
			performValidation(ValueChangedActionBindingMustBeValid.class, report);
		}

	}

	public static class DataBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public DataBindingMustBeValid() {
			super("'data'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getData();
		}
	}

	public static class FormatBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public FormatBindingMustBeValid() {
			super("'format'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getFormat();
		}
	}

	public static class TooltipBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public TooltipBindingMustBeValid() {
			super("'tooltip'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getTooltip();
		}
	}

	public static class ColorBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public ColorBindingMustBeValid() {
			super("'color'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getColor();
		}
	}

	public static class BgColorBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public BgColorBindingMustBeValid() {
			super("'bg_color'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getBgColor();
		}
	}

	public static class ValueChangedActionBindingMustBeValid extends BindingMustBeValid<FIBTableColumn> {
		public ValueChangedActionBindingMustBeValid() {
			super("'value_changed_action'_binding_is_not_valid", FIBTableColumn.class);
		}

		@Override
		public DataBinding<?> getBinding(FIBTableColumn object) {
			return object.getValueChangedAction();
		}
	}

}
