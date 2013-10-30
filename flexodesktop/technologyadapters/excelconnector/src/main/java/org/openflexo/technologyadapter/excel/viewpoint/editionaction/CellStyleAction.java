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
package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellStyleFeature;

@FIBPanel("Fib/CellStyleActionPanel.fib")
public class CellStyleAction extends EditionAction<BasicExcelModelSlot, ExcelCell> {

	private static final Logger logger = Logger.getLogger(CellStyleAction.class.getPackage().getName());
	
	public CellStyleAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}
	
	private CellStyleFeature cellStyle = null;
	
	private DataBinding<Object> value;
	
	public Object getValue(EditionSchemeAction action) {
		try {
			return getValue().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, getGraphicalFeatureType(), BindingDefinitionType.GET);
			value.setBindingName("value");
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setBindingName("value");
			value.setDeclaredType(getGraphicalFeatureType());
			value.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.value = value;
	}

	private DataBinding<ExcelCell> subject;

	public DataBinding<ExcelCell> getSubject() {
		if (subject == null) {
			subject = new DataBinding<ExcelCell>(this, ExcelCell.class, DataBinding.BindingDefinitionType.GET);
			subject.setBindingName("subject");
		}
		return subject;
	}

	public void setSubject(DataBinding<ExcelCell> subject) {
		if (subject != null) {
			subject.setOwner(this);
			subject.setBindingName("subject");
			subject.setDeclaredType(ExcelCell.class);
			subject.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.subject = subject;
	}

	public ExcelCell getSubject(EditionSchemeAction action) {
		try {
			return getSubject().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public java.lang.reflect.Type getGraphicalFeatureType() {
		if (getCellStyle() != null) {
			return getCellStyle().getClass();
		}
		return Object.class;
	}
	
	public CellStyleFeature getCellStyle() {
		if (cellStyle == null) {
			if (_cellStyleName != null) {
				for (CellStyleFeature cellStyle : getAvailableCellStyles()) {
					if (cellStyle.name().equals(_cellStyleName)) {
						return cellStyle;
					}
				}
			}
		}
		return cellStyle;
	}

	public void setCellStyle(CellStyleFeature cellStyle) {
		this.cellStyle = cellStyle;
	}

	private List<CellStyleFeature> availableCellStyles = null;
	
	public List<CellStyleFeature> getAvailableCellStyles() {
		if (availableCellStyles == null) {
			availableCellStyles = new Vector<CellStyleFeature>();
			for (CellStyleFeature cellStyle : ExcelCell.CellStyleFeature.values()) {
				availableCellStyles.add(cellStyle);
			}
		}
		return availableCellStyles;
	}

	private String _cellStyleName = null;

	public String _getGraphicalFeatureName() {
		if (getCellStyle() == null) {
			return _cellStyleName;
		}
		return getCellStyle().name();
	}

	public void _setCellStyleName(String cellStyleName) {
		_cellStyleName = cellStyleName;
	}
	
	@Override
	public ExcelCell performAction(EditionSchemeAction action) {
		logger.info("Perform graphical action " + action);
		ExcelCell excelCell = getSubject(action);
		Object value = null;
		try {
			value = getValue().getBindingValue(action);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Element is " + excelCell);
			logger.fine("Feature is " + getCellStyle());
			logger.fine("Value is " + value);
		}
		excelCell.setCellStyle(cellStyle,value);
		return excelCell;
	}

	@Override
	public void finalizePerformAction(EditionSchemeAction action, ExcelCell initialContext) {
		// TODO Auto-generated method stub
		
	}

	

}
