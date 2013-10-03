package org.openflexo.technologyadapter.excel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.technologyadapter.excel.ExcelTechnologyAdapter;

public class ExcelColumn extends ExcelObject {

	private Map<ExcelProperty, ExcelPropertyValue> values = new HashMap<ExcelProperty, ExcelPropertyValue>();

	private int colNumber;

	public ExcelColumn(int colNumber, ExcelTechnologyAdapter adapter) {
		super(adapter);
		this.colNumber = colNumber;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends ExcelPropertyValue> getPropertyValues() {
		ArrayList<ExcelPropertyValue> returned = new ArrayList<ExcelPropertyValue>();
		returned.addAll(values.values());
		return returned;
	}

	@Override
	public ExcelPropertyValue getPropertyValue(ExcelProperty property) {
		return values.get(property);
	}

	@Override
	public ExcelPropertyValue addToPropertyValue(ExcelProperty property, Object newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExcelPropertyValue removeFromPropertyValue(ExcelProperty property, Object valueToRemove) {
		// TODO Auto-generated method stub
		return null;
	}

}
