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
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.fib.model.FIBTable.SelectionMode;
import org.openflexo.fib.view.widget.FIBListWidget;

public class FIBList extends FIBMultipleValues {

	private static final Logger logger = Logger.getLogger(FIBList.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute {
		visibleRowCount,
		rowHeight,
		createNewRowOnClick,
		boundToSelectionManager,
		selectionMode,
		selected,
		textSelectionColor,
		textNonSelectionColor,
		backgroundSelectionColor,
		backgroundSecondarySelectionColor,
		backgroundNonSelectionColor,
		layoutOrientation
	}

	public static enum LayoutOrientation {
		vertical {
			@Override
			public int getSwingValue() {
				return 0;
			}
		},
		horizontal {
			@Override
			public int getSwingValue() {
				return 1;
			}
		},
		jesaispas {
			@Override
			public int getSwingValue() {
				return 2;
			}
		};

		public abstract int getSwingValue();
	}

	private BindingDefinition SELECTED;

	public BindingDefinition getSelectedBindingDefinition() {
		if (SELECTED == null) {
			SELECTED = new BindingDefinition("selected", getIteratorClass(), DataBinding.BindingDefinitionType.GET_SET, false);
		}
		return SELECTED;
	}

	private DataBinding<Object> selected;

	private Integer visibleRowCount = 5;
	private Integer rowHeight;
	private boolean createNewRowOnClick = false;
	private boolean boundToSelectionManager = false;

	private SelectionMode selectionMode = SelectionMode.MultipleIntervalSelection;

	private Color textSelectionColor;
	private Color textNonSelectionColor;
	private Color backgroundSelectionColor;
	private Color backgroundSecondarySelectionColor;
	private Color backgroundNonSelectionColor;

	private LayoutOrientation layoutOrientation = LayoutOrientation.vertical;

	public FIBList() {
	}

	@Override
	protected String getBaseName() {
		return "List";
	}

	@Override
	public Type getDynamicAccessType() {
		Type[] args = new Type[2];
		args[0] = getDataType();
		args[1] = getIteratorType();
		return new ParameterizedTypeImpl(FIBListWidget.class, args);
	}

	public DataBinding<Object> getSelected() {
		if (selected == null) {
			selected = new DataBinding<Object>(this, getIteratorType(), DataBinding.BindingDefinitionType.GET_SET);
		}
		return selected;
	}

	public void setSelected(DataBinding<Object> selected) {
		if (selected != null) {
			selected.setOwner(this);
			selected.setDeclaredType(getIteratorType());
			selected.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET_SET);
		}
		this.selected = selected;
	}

	@Override
	public void finalizeDeserialization() {
		super.finalizeDeserialization();
		if (selected != null) {
			selected.decode();
		}
	}

	public Integer getVisibleRowCount() {
		return visibleRowCount;
	}

	public void setVisibleRowCount(Integer visibleRowCount) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.visibleRowCount, visibleRowCount);
		if (notification != null) {
			this.visibleRowCount = visibleRowCount;
			hasChanged(notification);
		}
	}

	public Integer getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(Integer rowHeight) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.rowHeight, rowHeight);
		if (notification != null) {
			this.rowHeight = rowHeight;
			hasChanged(notification);
		}
	}

	public boolean getCreateNewRowOnClick() {
		return createNewRowOnClick;
	}

	public void setCreateNewRowOnClick(boolean createNewRowOnClick) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.createNewRowOnClick, createNewRowOnClick);
		if (notification != null) {
			this.createNewRowOnClick = createNewRowOnClick;
			hasChanged(notification);
		}
	}

	public boolean getBoundToSelectionManager() {
		return boundToSelectionManager;
	}

	public void setBoundToSelectionManager(boolean boundToSelectionManager) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.boundToSelectionManager, boundToSelectionManager);
		if (notification != null) {
			this.boundToSelectionManager = boundToSelectionManager;
			hasChanged(notification);
		}
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		FIBAttributeNotification<SelectionMode> notification = requireChange(Parameters.selectionMode, selectionMode);
		if (notification != null) {
			this.selectionMode = selectionMode;
			hasChanged(notification);
		}
	}

	public Color getTextSelectionColor() {
		return textSelectionColor;
	}

	public void setTextSelectionColor(Color textSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.textSelectionColor, textSelectionColor);
		if (notification != null) {
			this.textSelectionColor = textSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getTextNonSelectionColor() {
		return textNonSelectionColor;
	}

	public void setTextNonSelectionColor(Color textNonSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.textNonSelectionColor, textNonSelectionColor);
		if (notification != null) {
			this.textNonSelectionColor = textNonSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSelectionColor() {
		return backgroundSelectionColor;
	}

	public void setBackgroundSelectionColor(Color backgroundSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundSelectionColor, backgroundSelectionColor);
		if (notification != null) {
			this.backgroundSelectionColor = backgroundSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSecondarySelectionColor() {
		return backgroundSecondarySelectionColor;
	}

	public void setBackgroundSecondarySelectionColor(Color backgroundSecondarySelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundSecondarySelectionColor,
				backgroundSecondarySelectionColor);
		if (notification != null) {
			this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundNonSelectionColor() {
		return backgroundNonSelectionColor;
	}

	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor) {
		FIBAttributeNotification<Color> notification = requireChange(Parameters.backgroundNonSelectionColor, backgroundNonSelectionColor);
		if (notification != null) {
			this.backgroundNonSelectionColor = backgroundNonSelectionColor;
			hasChanged(notification);
		}
	}

	public LayoutOrientation getLayoutOrientation() {
		return layoutOrientation;
	}

	public void setLayoutOrientation(LayoutOrientation layoutOrientation) {
		FIBAttributeNotification<LayoutOrientation> notification = requireChange(Parameters.layoutOrientation, layoutOrientation);
		if (notification != null) {
			this.layoutOrientation = layoutOrientation;
			hasChanged(notification);
		}
	}

}
