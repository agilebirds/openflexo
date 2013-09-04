package org.openflexo.technologyadapter.excel.model;

import java.util.List;

public interface ExcelPropertyObject {

	public List<? extends ExcelPropertyValue> getPropertyValues();

	public ExcelPropertyValue getPropertyValue(ExcelProperty property);

	public ExcelPropertyValue addToPropertyValue(ExcelProperty property, Object newValue);

	public ExcelPropertyValue removeFromPropertyValue(ExcelProperty property, Object valueToRemove);
}
