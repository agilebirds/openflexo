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

import javax.swing.UIManager;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.fib.controller.FIBListDynamicModel;
import org.openflexo.fib.model.FIBTable.SelectionMode;

public class FIBList extends FIBMultipleValues {
 
	private static final Logger logger = Logger.getLogger(FIBList.class.getPackage().getName());

	public static enum Parameters implements FIBModelAttribute
	{
		visibleRowCount,
		rowHeight,
		createNewRowOnClick,
		autoSelectFirstRow,
		boundToSelectionManager,
		selectionMode,
		selected,
		textSelectionColor,
		textNonSelectionColor,
		backgroundSelectionColor,
		backgroundSecondarySelectionColor,
		backgroundNonSelectionColor
	}

	private BindingDefinition SELECTED;
	
	public BindingDefinition getSelectedBindingDefinition()
	{
		if (SELECTED == null) {
			SELECTED = new BindingDefinition("selected", getIteratorClass(), BindingDefinitionType.GET_SET, false);
		}
		return SELECTED;
	}
	
	private DataBinding selected;

	private int visibleRowCount = 5;
	private int rowHeight = 20;
	private boolean createNewRowOnClick = false;
	private boolean autoSelectFirstRow = false;
	private boolean boundToSelectionManager = false;

	private SelectionMode selectionMode = SelectionMode.MultipleIntervalSelection;
	
	 private Color textSelectionColor = UIManager.getColor("List.selectionForeground");
	 private Color textNonSelectionColor = UIManager.getColor("List.foreground");
	 private Color backgroundSelectionColor = UIManager.getColor("List.selectionBackground");
	 private Color backgroundSecondarySelectionColor = SECONDARY_SELECTION_COLOR;
	 private Color backgroundNonSelectionColor = UIManager.getColor("List.background");

	public FIBList()
    {
	}
    
	@Override
	public Type getDynamicAccessType()
	{
		Type[] args = new Type[2];
		args[0] = getDataType();
		args[1] = getIteratorClass();
		return new ParameterizedTypeImpl(FIBListDynamicModel.class, args);
	}
	

	public DataBinding getSelected() 
	{
		if (selected == null) selected = new DataBinding(this,Parameters.selected,getSelectedBindingDefinition());
		return selected;
	}

	public void setSelected(DataBinding selected) 
	{
		selected.setOwner(this);
		selected.setBindingAttribute(Parameters.selected);
		selected.setBindingDefinition(getSelectedBindingDefinition());
		this.selected = selected;
	}
	
	@Override
	public void finalizeDeserialization() 
	{
		super.finalizeDeserialization();
		if (selected != null) selected.finalizeDeserialization();
	}
	

	public int getVisibleRowCount()
	{
		return visibleRowCount;
	}

	public void setVisibleRowCount(int visibleRowCount)
	{
		FIBAttributeNotification<Integer> notification = requireChange(
				Parameters.visibleRowCount, visibleRowCount);
		if (notification != null) {
			this.visibleRowCount = visibleRowCount;
			hasChanged(notification);
		}
	}

	public int getRowHeight()
	{
		return rowHeight;
	}

	public void setRowHeight(int rowHeight)
	{
		FIBAttributeNotification<Integer> notification = requireChange(
				Parameters.rowHeight, rowHeight);
		if (notification != null) {
			this.rowHeight = rowHeight;
			hasChanged(notification);
		}
	}

	public boolean getCreateNewRowOnClick()
	{
		return createNewRowOnClick;
	}

	public void setCreateNewRowOnClick(boolean createNewRowOnClick)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.createNewRowOnClick, createNewRowOnClick);
		if (notification != null) {
			this.createNewRowOnClick = createNewRowOnClick;
			hasChanged(notification);
		}
	}

	public boolean getAutoSelectFirstRow()
	{
		return autoSelectFirstRow;
	}

	public void setAutoSelectFirstRow(boolean autoSelectFirstRow)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.autoSelectFirstRow, autoSelectFirstRow);
		if (notification != null) {
			this.autoSelectFirstRow = autoSelectFirstRow;
			hasChanged(notification);
		}
	}
	
	public boolean getBoundToSelectionManager()
	{
		return boundToSelectionManager;
	}

	public void setBoundToSelectionManager(boolean boundToSelectionManager)
	{
		FIBAttributeNotification<Boolean> notification = requireChange(
				Parameters.boundToSelectionManager, boundToSelectionManager);
		if (notification != null) {
			this.boundToSelectionManager = boundToSelectionManager;
			hasChanged(notification);
		}
	}

	public SelectionMode getSelectionMode()
	{
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode)
	{
		FIBAttributeNotification<SelectionMode> notification = requireChange(
				Parameters.selectionMode, selectionMode);
		if (notification != null) {
			this.selectionMode = selectionMode;
			hasChanged(notification);
		}
	}

	public Color getTextSelectionColor() 
	{
		return textSelectionColor;
	}

	public void setTextSelectionColor(Color textSelectionColor)
	{
		FIBAttributeNotification<Color> notification = requireChange(
				Parameters.textSelectionColor, textSelectionColor);
		if (notification != null) {
			this.textSelectionColor = textSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getTextNonSelectionColor() 
	{
		return textNonSelectionColor;
	}

	public void setTextNonSelectionColor(Color textNonSelectionColor) 
	{
		FIBAttributeNotification<Color> notification = requireChange(
				Parameters.textNonSelectionColor, textNonSelectionColor);
		if (notification != null) {
			this.textNonSelectionColor = textNonSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSelectionColor()
	{
		return backgroundSelectionColor;
	}

	public void setBackgroundSelectionColor(Color backgroundSelectionColor) 
	{
		FIBAttributeNotification<Color> notification = requireChange(
				Parameters.backgroundSelectionColor, backgroundSelectionColor);
		if (notification != null) {
			this.backgroundSelectionColor = backgroundSelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundSecondarySelectionColor()
	{
		return backgroundSecondarySelectionColor;
	}

	public void setBackgroundSecondarySelectionColor(
			Color backgroundSecondarySelectionColor)
	{
		FIBAttributeNotification<Color> notification = requireChange(
				Parameters.backgroundSecondarySelectionColor, backgroundSecondarySelectionColor);
		if (notification != null) {
			this.backgroundSecondarySelectionColor = backgroundSecondarySelectionColor;
			hasChanged(notification);
		}
	}

	public Color getBackgroundNonSelectionColor() 
	{
		return backgroundNonSelectionColor;
	}

	public void setBackgroundNonSelectionColor(Color backgroundNonSelectionColor)
	{
		FIBAttributeNotification<Color> notification = requireChange(
				Parameters.backgroundNonSelectionColor, backgroundNonSelectionColor);
		if (notification != null) {
			this.backgroundNonSelectionColor = backgroundNonSelectionColor;
			hasChanged(notification);
		}
	}


}
