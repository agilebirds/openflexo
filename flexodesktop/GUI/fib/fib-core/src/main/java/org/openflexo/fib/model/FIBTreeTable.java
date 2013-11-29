package org.openflexo.fib.model;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.binding.BindingModel;

public class FIBTreeTable extends FIBBrowser implements FIBTableComponent {

	public static enum Parameters implements FIBModelAttribute {
		columns;
	}

	private List<FIBTableColumn> columns;

	public FIBTreeTable() {
		columns = new ArrayList<FIBTableColumn>();
	}

	public FIBTableColumn getColumnWithTitle(String title) {
		for (FIBTableColumn c : columns) {
			if (title.equals(c.getTitle())) {
				return c;
			}
		}
		return null;
	}

	@Override
	public List<FIBTableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<FIBTableColumn> columns) {
		this.columns = columns;
	}

	public void addToColumns(FIBTableColumn aColumn) {
		aColumn.setTable(this);
		columns.add(aColumn);
		getPropertyChangeSupport().firePropertyChange(Parameters.columns.name(), null, columns);
	}

	public void removeFromColumns(FIBTableColumn aColumn) {
		aColumn.setTable(null);
		columns.remove(aColumn);
		getPropertyChangeSupport().firePropertyChange(Parameters.columns.name(), null, columns);
	}

	@Override
	public BindingModel getTableBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
