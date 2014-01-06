package org.openflexo.fib.model;

import java.awt.Font;
import java.util.List;

import org.openflexo.antar.binding.BindingModel;

public interface FIBTableComponent {

	public BindingModel getTableBindingModel();

	public List<FIBTableColumn> getColumns();

	public FIBComponent getRootComponent();

	public Font retrieveValidFont();

}
