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
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.BasicExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.ExcelCell;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellAlignmentStyleFeature;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellBorderStyleFeature;
import org.openflexo.technologyadapter.excel.model.ExcelCell.CellStyleFeature;

@FIBPanel("Fib/CellStyleActionPanel.fib")
@ModelEntity
@ImplementationClass(CellStyleAction.CellStyleActionImpl.class)
@XMLElement
public interface CellStyleAction extends EditionAction<BasicExcelModelSlot, ExcelCell> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SUBJECT_KEY = "subject";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = CellStyleFeature.class)
	public static final String CELL_STYLE_KEY = "cellStyle";

	@Getter(value = SUBJECT_KEY)
	@XMLAttribute
	public DataBinding<ExcelCell> getSubject();

	@Setter(SUBJECT_KEY)
	public void setSubject(DataBinding<ExcelCell> subject);

	@Getter(value = VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getValue();

	@Setter(VALUE_KEY)
	public void setValue(DataBinding<?> value);

	@Getter(value = CELL_STYLE_KEY)
	@XMLAttribute
	public CellStyleFeature getCellStyle();

	@Setter(CELL_STYLE_KEY)
	public void setCellStyle(CellStyleFeature cellStyle);

	public static abstract class CellStyleActionImpl extends EditionActionImpl<BasicExcelModelSlot, ExcelCell> implements CellStyleAction {

		private static final Logger logger = Logger.getLogger(CellStyleAction.class.getPackage().getName());

		public CellStyleActionImpl() {
			super();
		}

		private CellStyleFeature cellStyle = null;

		private CellBorderStyleFeature cellBorderStyle = null;

		private CellAlignmentStyleFeature cellAlignmentStyle = null;

		private DataBinding<?> value;

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

		@Override
		public DataBinding<?> getValue() {
			if (value == null) {
				value = new DataBinding<Object>(this, getGraphicalFeatureType(), BindingDefinitionType.GET);
				value.setBindingName("value");
			}
			return value;
		}

		@Override
		public void setValue(DataBinding<?> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName("value");
				value.setDeclaredType(getGraphicalFeatureType());
				value.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.value = value;
		}

		private DataBinding<ExcelCell> subject;

		@Override
		public DataBinding<ExcelCell> getSubject() {
			if (subject == null) {
				subject = new DataBinding<ExcelCell>(this, ExcelCell.class, DataBinding.BindingDefinitionType.GET);
				subject.setBindingName("subject");
			}
			return subject;
		}

		@Override
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

		// MAIN CELL STYLES

		public java.lang.reflect.Type getGraphicalFeatureType() {
			if (getCellStyle() != null) {
				return getCellStyle().getClass();
			}
			return Object.class;
		}

		@Override
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

		@Override
		public void setCellStyle(CellStyleFeature cellStyle) {
			this.cellStyle = cellStyle;
			if (cellStyle.equals(CellStyleFeature.BorderBottom) || cellStyle.equals(CellStyleFeature.BorderTop)
					|| cellStyle.equals(CellStyleFeature.BorderLeft) || cellStyle.equals(CellStyleFeature.BorderRight)) {
				isBorderStyle = true;
			} else {
				isBorderStyle = false;
			}
			if (cellStyle.equals(CellStyleFeature.Alignment)) {
				isAlignmentStyle = true;
			} else {
				isAlignmentStyle = false;
			}
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

		// SPECIAL BORDER STYLES

		private boolean isBorderStyle = false;

		public boolean isBorderStyle() {
			return isBorderStyle;
		}

		public CellBorderStyleFeature getCellBorderStyle() {
			if (cellBorderStyle == null) {
				if (_cellBorderStyleName != null) {
					for (CellBorderStyleFeature cellBorderStyle : getAvailableCellBorderStyles()) {
						if (cellBorderStyle.name().equals(_cellBorderStyleName)) {
							return cellBorderStyle;
						}
					}
				}
			}
			return cellBorderStyle;
		}

		public void setCellBorderStyle(CellBorderStyleFeature cellBorderStyle) {
			this.cellBorderStyle = cellBorderStyle;
		}

		private List<CellBorderStyleFeature> availableCellBorderStyles = null;

		public List<CellBorderStyleFeature> getAvailableCellBorderStyles() {
			if (availableCellBorderStyles == null) {
				availableCellBorderStyles = new Vector<CellBorderStyleFeature>();
				for (CellBorderStyleFeature cellBorderStyle : ExcelCell.CellBorderStyleFeature.values()) {
					availableCellBorderStyles.add(cellBorderStyle);
				}
			}
			return availableCellBorderStyles;
		}

		private String _cellBorderStyleName = null;

		public String _getCellBorderStyleName() {
			if (getCellBorderStyle() == null) {
				return _cellBorderStyleName;
			}
			return getCellBorderStyle().name();
		}

		public void _setCellBorderStyleName(String cellBorderStyleName) {
			_cellBorderStyleName = cellBorderStyleName;
		}

		// SPECIAL ALIGNMENT STYLES

		private boolean isAlignmentStyle = false;

		public boolean isAlignmentStyle() {
			return isAlignmentStyle;
		}

		public CellAlignmentStyleFeature getCellAlignmentStyle() {
			if (cellAlignmentStyle == null) {
				if (_cellAlignmentStyleName != null) {
					for (CellAlignmentStyleFeature cellAlignmentStyle : getAvailableCellAlignmentStyles()) {
						if (cellAlignmentStyle.name().equals(_cellAlignmentStyleName)) {
							return cellAlignmentStyle;
						}
					}
				}
			}
			return cellAlignmentStyle;
		}

		public void setCellAlignmentStyle(CellAlignmentStyleFeature cellAlignmentStyle) {
			this.cellAlignmentStyle = cellAlignmentStyle;
		}

		private List<CellAlignmentStyleFeature> availableCellAlignmentStyles = null;

		public List<CellAlignmentStyleFeature> getAvailableCellAlignmentStyles() {
			if (availableCellAlignmentStyles == null) {
				availableCellAlignmentStyles = new Vector<CellAlignmentStyleFeature>();
				for (CellAlignmentStyleFeature cellAlignmentStyle : ExcelCell.CellAlignmentStyleFeature.values()) {
					availableCellAlignmentStyles.add(cellAlignmentStyle);
				}
			}
			return availableCellAlignmentStyles;
		}

		private String _cellAlignmentStyleName = null;

		public String _getCellAlignmentStyleName() {
			if (getCellAlignmentStyle() == null) {
				return _cellAlignmentStyleName;
			}
			return getCellAlignmentStyle().name();
		}

		public void _setCellAlignmentStyleName(String cellAlignmentStyleName) {
			_cellAlignmentStyleName = cellAlignmentStyleName;
		}

		// ACTION

		@Override
		public ExcelCell performAction(EditionSchemeAction action) {
			logger.info("Perform graphical action " + action);
			ExcelCell excelCell = getSubject(action);
			Object value = null;
			try {
				if (isAlignmentStyle) {
					value = getCellAlignmentStyle();
				} else if (isBorderStyle) {
					value = getCellBorderStyle();
				} else {
					value = getValue().getBindingValue(action);
				}
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
			excelCell.setCellStyle(cellStyle, value);
			return excelCell;
		}

		@Override
		public void finalizePerformAction(EditionSchemeAction action, ExcelCell initialContext) {
			// TODO Auto-generated method stub

		}

	}
}
