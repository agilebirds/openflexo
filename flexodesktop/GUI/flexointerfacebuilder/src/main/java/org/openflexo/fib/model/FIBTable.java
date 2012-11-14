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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBTableDynamicModel;
import org.openflexo.fib.model.FIBTableAction.FIBAddAction;
import org.openflexo.fib.model.FIBTableAction.FIBCustomAction;
import org.openflexo.fib.model.FIBTableAction.FIBRemoveAction;

public class FIBTable extends FIBWidget /*implements DynamicAccess*/{

	private static final Logger logger = Logger.getLogger(FIBTable.class.getPackage().getName());

	private BindingDefinition SELECTED;

	public BindingDefinition getSelectedBindingDefinition() {
		if (SELECTED == null) {
			SELECTED = new BindingDefinition("selected", getIteratorClass(), BindingDefinitionType.GET_SET, false);
		}
		return SELECTED;
	}

	public static enum Parameters implements FIBModelAttribute {
		iteratorClass, visibleRowCount, rowHeight, createNewRowOnClick, autoSelectFirstRow, boundToSelectionManager, selectionMode, selected, columns, actions, showFooter, textSelectionColor, textNonSelectionColor, backgroundSelectionColor, backgroundSecondarySelectionColor, backgroundNonSelectionColor
	}

	public enum SelectionMode {
		SingleSelection {
			@Override
			public int getMode() {
				return ListSelectionModel.SINGLE_SELECTION;
			}
		},
		SingleIntervalSelection {
			@Override
			public int getMode() {
				return ListSelectionModel.SINGLE_INTERVAL_SELECTION;
			}
		},
		MultipleIntervalSelection {
			@Override
			public int getMode() {
				return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
			}
		};

		public abstract int getMode();
	}

	private DataBinding selected;

	private int visibleRowCount = 5;
	private int rowHeight = 20;
	private boolean createNewRowOnClick = false;
	private boolean autoSelectFirstRow = false;
	private boolean boundToSelectionManager = false;
	private boolean showFooter = true;

	private SelectionMode selectionMode = SelectionMode.MultipleIntervalSelection;

	private Class iteratorClass;

	private Vector<FIBTableColumn> columns;
	private Vector<FIBTableAction> actions;

	private BindingModel tableBindingModel;
	private BindingModel actionBindingModel;

	private Color textSelectionColor = UIManager.getColor("Table.selectionForeground");
	private Color textNonSelectionColor = UIManager.getColor("Table.foreground");
	private Color backgroundSelectionColor = UIManager.getColor("Table.selectionBackground");
	private Color backgroundSecondarySelectionColor = SECONDARY_SELECTION_COLOR;
	private Color backgroundNonSelectionColor = UIManager.getColor("Table.background");

	public FIBTable() {
		columns = new Vector<FIBTableColumn>();
		actions = new Vector<FIBTableAction>();
	}

	@Override
	protected String getBaseName() {
		return "Table";
	}

	public FIBTableColumn getColumnWithTitle(String title) {
		for (FIBTableColumn c : columns) {
			if (title.equals(c.getTitle())) {
				return c;
			}
		}
		return null;
	}

	public Vector<FIBTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(Vector<FIBTableColumn> columns) {
		this.columns = columns;
	}

	public void addToColumns(FIBTableColumn aColumn) {
		aColumn.setTable(this);
		columns.add(aColumn);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableColumn>(Parameters.columns, aColumn));
	}

	public void removeFromColumns(FIBTableColumn aColumn) {
		aColumn.setTable(null);
		columns.remove(aColumn);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBTableColumn>(Parameters.columns, aColumn));
	}

	public Vector<FIBTableAction> getActions() {
		return actions;
	}

	public void setActions(Vector<FIBTableAction> actions) {
		this.actions = actions;
	}

	public void addToActions(FIBTableAction anAction) {
		logger.fine("Add to actions " + anAction);
		anAction.setTable(this);
		actions.add(anAction);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableAction>(Parameters.actions, anAction));
	}

	public void removeFromActions(FIBTableAction anAction) {
		anAction.setTable(null);
		actions.remove(anAction);
		setChanged();
		notifyObservers(new FIBRemovingNotification<FIBTableAction>(Parameters.actions, anAction));
	}

	public BindingModel getTableBindingModel() {
		if (tableBindingModel == null) {
			createTableBindingModel();
		}
		return tableBindingModel;
	}

	private void createTableBindingModel() {
		tableBindingModel = new BindingModel(getBindingModel());

		tableBindingModel.addToBindingVariables(new BindingVariableImpl(this, "iterator", getIteratorClass()));
		// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);

		// logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
	}

	public BindingModel getActionBindingModel() {
		if (actionBindingModel == null) {
			createActionBindingModel();
		}
		return actionBindingModel;
	}

	private void createActionBindingModel() {
		actionBindingModel = new BindingModel(getBindingModel());

		actionBindingModel.addToBindingVariables(new BindingVariableImpl(this, "selected", getIteratorClass()));
		// System.out.println("dataClass="+getDataClass()+" dataClassName="+dataClassName);

		// logger.info("******** Table: "+getName()+" Add BindingVariable: iterator type="+getIteratorClass());
	}

	@Override
	public void notifiedBindingModelRecreated() {
		super.notifiedBindingModelRecreated();
		createTableBindingModel();
		createActionBindingModel();
	}

	public DataBinding getSelected() {
		if (selected == null) {
			selected = new DataBinding(this, Parameters.selected, getSelectedBindingDefinition());
		}
		return selected;
	}

	public void setSelected(DataBinding selected) {
		selected.setOwner(this);
		selected.setBindingAttribute(Parameters.selected);
		selected.setBindingDefinition(getSelectedBindingDefinition());
		this.selected = selected;
	}

	@Override
	public void finalizeDeserialization() {
		logger.fine("finalizeDeserialization() for FIBTable " + getName());

		/*if (tableBindingModel == null)*/createTableBindingModel();
		/*if (actionBindingModel == null)*/createActionBindingModel();

		super.finalizeDeserialization();

		for (FIBTableColumn column : getColumns()) {
			column.finalizeTableDeserialization();
		}
		if (selected != null) {
			selected.finalizeDeserialization();
		}
	}

	/*public boolean hasDynamicKeyValueProperty(String name) 
	{
		if (name.equals("selected")) return getDataClass() != null;
		return false;
	}
	
	private DynamicKeyValueProperty SELECTED_DKVP;
	
	public DynamicKeyValueProperty getDynamicKeyValueProperty(String name)
	{
		if (name.equals("selected")) {
			if (SELECTED_DKVP == null) SELECTED_DKVP = new DynamicKeyValueProperty("selected", getClass(), getDataClass());
			return SELECTED_DKVP;
		}
		return null;
	}*/

	public Class getIteratorClass() {
		if (iteratorClass == null) {
			iteratorClass = Object.class;
		}
		return iteratorClass;

	}

	public void setIteratorClass(Class iteratorClass) {
		FIBAttributeNotification<Class> notification = requireChange(Parameters.iteratorClass, iteratorClass);
		if (notification != null) {
			this.iteratorClass = iteratorClass;
			createTableBindingModel();
			createActionBindingModel();
			hasChanged(notification);
		}
	}

	@Override
	public Type getDefaultDataClass() {
		Type[] args = new Type[1];
		args[0] = getIteratorClass();
		return new ParameterizedTypeImpl(List.class, args);
	}

	@Override
	public Type getDynamicAccessType() {
		Type[] args = new Type[2];
		args[0] = getDataType();
		args[1] = getIteratorClass();
		return new ParameterizedTypeImpl(FIBTableDynamicModel.class, args);
	}

	@Override
	public Boolean getManageDynamicModel() {
		return true;
	}

	@Override
	public void notifyBindingChanged(DataBinding binding) {
		logger.fine("notifyBindingChanged with " + binding);
		if (binding == getData()) {
			if (getData() != null && getData().getBinding() != null) {
				Type accessedType = getData().getBinding().getAccessedType();
				if (accessedType instanceof ParameterizedType && ((ParameterizedType) accessedType).getActualTypeArguments().length > 0) {
					Class newIteratorClass = TypeUtils.getBaseClass(((ParameterizedType) accessedType).getActualTypeArguments()[0]);
					if (getIteratorClass() == null || !TypeUtils.isClassAncestorOf(newIteratorClass, getIteratorClass())) {
						setIteratorClass(newIteratorClass);
					}
				}
			}
		}

	}

	/*public String getIteratorClassName()
	{
		return iteratorClassName;
	}
	
	public void setIteratorClassName(String iteratorClassName)
	{
		FIBAttributeNotification<String> notification = requireChange(
				Parameters.iteratorClassName, iteratorClassName);
		if (notification != null) {
			this.iteratorClassName = iteratorClassName;
			iteratorClass = null;
			createTableBindingModel();
			hasChanged(notification);
		}
	}*/

	public boolean getAutoSelectFirstRow() {
		return autoSelectFirstRow;
	}

	public void setAutoSelectFirstRow(boolean autoSelectFirstRow) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.autoSelectFirstRow, autoSelectFirstRow);
		if (notification != null) {
			this.autoSelectFirstRow = autoSelectFirstRow;
			hasChanged(notification);
		}
	}

	public int getVisibleRowCount() {
		return visibleRowCount;
	}

	public void setVisibleRowCount(int visibleRowCount) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.visibleRowCount, visibleRowCount);
		if (notification != null) {
			this.visibleRowCount = visibleRowCount;
			hasChanged(notification);
		}
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
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

	public boolean getShowFooter() {
		return showFooter;
	}

	public void setShowFooter(boolean showFooter) {
		FIBAttributeNotification<Boolean> notification = requireChange(Parameters.showFooter, showFooter);
		if (notification != null) {
			this.showFooter = showFooter;
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

	public FIBTableAction deleteAction(FIBTableAction actionToDelete) {
		logger.info("Called deleteAction() with " + actionToDelete);
		removeFromActions(actionToDelete);
		return actionToDelete;
	}

	public FIBLabelColumn createLabelColumn() {
		FIBLabelColumn newColumn = new FIBLabelColumn();
		newColumn.setName("label");
		newColumn.setTitle("label");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBTextFieldColumn createTextFieldColumn() {
		FIBTextFieldColumn newColumn = new FIBTextFieldColumn();
		newColumn.setName("textfield");
		newColumn.setTitle("textfield");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBCheckBoxColumn createCheckBoxColumn() {
		FIBCheckBoxColumn newColumn = new FIBCheckBoxColumn();
		newColumn.setName("checkbox");
		newColumn.setTitle("checkbox");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBDropDownColumn createDropDownColumn() {
		FIBDropDownColumn newColumn = new FIBDropDownColumn();
		newColumn.setName("dropdown");
		newColumn.setTitle("dropdown");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBNumberColumn createNumberColumn() {
		FIBNumberColumn newColumn = new FIBNumberColumn();
		newColumn.setName("number");
		newColumn.setTitle("number");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBIconColumn createIconColumn() {
		FIBIconColumn newColumn = new FIBIconColumn();
		newColumn.setName("icon");
		newColumn.setTitle("icon");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBCustomColumn createCustomColumn() {
		FIBCustomColumn newColumn = new FIBCustomColumn();
		newColumn.setName("custom");
		newColumn.setTitle("custom");
		addToColumns(newColumn);
		return newColumn;
	}

	public FIBTableColumn deleteColumn(FIBTableColumn columnToDelete) {
		logger.info("Called deleteColumn() with " + columnToDelete);
		removeFromColumns(columnToDelete);
		return columnToDelete;
	}

	public void moveToTop(FIBTableColumn c) {
		if (c == null) {
			return;
		}
		columns.remove(c);
		columns.insertElementAt(c, 0);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableColumn>(Parameters.columns, c));
	}

	public void moveUp(FIBTableColumn c) {
		if (c == null) {
			return;
		}
		int index = columns.indexOf(c);
		columns.remove(c);
		columns.insertElementAt(c, index - 1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableColumn>(Parameters.columns, c));
	}

	public void moveDown(FIBTableColumn c) {
		if (c == null) {
			return;
		}
		int index = columns.indexOf(c);
		columns.remove(c);
		columns.insertElementAt(c, index + 1);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableColumn>(Parameters.columns, c));
	}

	public void moveToBottom(FIBTableColumn c) {
		if (c == null) {
			return;
		}
		columns.remove(c);
		columns.add(c);
		setChanged();
		notifyObservers(new FIBAddingNotification<FIBTableColumn>(Parameters.columns, c));
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

}
