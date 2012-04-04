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
package org.openflexo.foundation.ie.widget;

import java.awt.Point;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IETopComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.TopComponentDown;
import org.openflexo.foundation.ie.action.TopComponentUp;
import org.openflexo.foundation.ie.dm.ColumnInserted;
import org.openflexo.foundation.ie.dm.ColumnRemoved;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.PercentageChanged;
import org.openflexo.foundation.ie.dm.TRInserted;
import org.openflexo.foundation.ie.dm.TRRemoved;
import org.openflexo.foundation.ie.dm.table.DisplayBorderChanged;
import org.openflexo.foundation.ie.dm.table.WidgetAddedToTable;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Widget representing an HTML Table
 * 
 * @author bmangez
 */
public class IEHTMLTableWidget extends IEWidget implements ExtensibleWidget, IETopComponent, InnerBlocWidgetInterface {
	/**
     * 
     */
	public static final String HTML_TABLE_WIDGET = "html_table_widget";

	private static final Logger logger = Logger.getLogger(IEHTMLTableWidget.class.getPackage().getName());

	private boolean _grid = false;

	private boolean _noCss = false;

	private boolean _isShowingBorder = true;

	private String _conditionals;

	private Vector<Double> pourcentageArray;

	private String _pourcentageWidths;

	private IESequenceTR _sequenceTR;

	public IEHTMLTableWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEHTMLTableWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_sequenceTR = new IESequenceTR(woComponent, this, prj);
		pourcentageArray = new Vector<Double>();
	}

	/**
	 * Overrides finalizeDeserialization
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#finalizeDeserialization(java.lang.Object)
	 */
	@Override
	public void finalizeDeserialization(Object builder) {
		// setParentOfSingleWidgetComponentInstance(this);
		// First we parse all TR's to set their rowIndex (=yLocation)
		setTRRowIndex();
		// Then we insert all the cells that are recovered by a span
		getSequenceTR().insertSpannedTD();
		reIndexTable();
		if (!isCreatedByCloning()) {// If we are created by cloneage from an
			// original widget which already has correct
			// data, the next operations are not necessary.

			// Now we fill the table so that there are no holes inside of it.
			int colCount = getMaxColCount();
			for (int i = 0; i < getRowCount(); i++) {
				for (int j = 0; j < colCount; j++) {
					IETDWidget td = getTDAt(i, j);
					if (td == null) {
						getTR(i).insertEmptyTDAtCol(j);
					}
				}
			}
			initPourcentageArray();

			// Let's check that all percentages are correct
			if (pourcentageArray.size() < colCount) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Percentage array is to small: " + pourcentageArray.size() + " colcount=" + colCount);
				}
				int size = pourcentageArray.size();
				for (int i = size; i < colCount; i++) {
					double newColPourcentage = 1.0d / (pourcentageArray.size() + 1);
					double oldColIncrement = -1.0d * newColPourcentage / pourcentageArray.size();
					applyIncrementToCols(oldColIncrement, true);
					pourcentageArray.add(new Double(newColPourcentage));
				}
			} else if (pourcentageArray.size() > colCount) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Percentage array is to big: " + pourcentageArray.size() + " colcount=" + colCount);
				}
				while (pourcentageArray.size() > colCount) {
					Double d = pourcentageArray.lastElement();
					pourcentageArray.remove(d);
					applyIncrementToCols(d.doubleValue() / pourcentageArray.size(), true);
				}
			}

			checkAndFixColumnWidth();
			// Finally, we simplify the tree
			_sequenceTR.simplifySequenceTree();
		}
		super.finalizeDeserialization(builder);

		// SGU, 28/09/2006
		// We had here a problem because the finalize deserialisation scheme is
		// called here
		// for a widget AFTER the one for the component has been performed,
		// causing parent component
		// to be marked as modified. Just call finalizer on parent again to
		// clear modifications.
		/*        if (getWOComponent() != null) {
		            getWOComponent().finalizeDeserialization(builder);
		        }
		*/
	}

	/*private void setParentOfSingleWidgetComponentInstance(IEHTMLTableWidget widget) {
		_sequenceTR.setParentOfSingleWidgetComponentInstance(widget);
		
	}*/

	private void reIndexTable() {
		reIndexRows();
		for (int i = 0; i < getRowCount(); i++) {
			IETRWidget tr = getTR(i);
			tr.reIndexCells();
		}
	}

	public Vector<IWidget> getAllInnerTableWidget() {
		Vector<IWidget> v = new Vector<IWidget>();
		v.addAll(_sequenceTR.getAllInnerTableWidget());
		return v;
	}

	/**
	 * Reindexes all rows of this table.
	 * 
	 */
	private void reIndexRows() {
		_sequenceTR.setTRRowIndex(new Incrementer());
	}

	/**
	 * @return
	 */
	private int getMaxColCount() {
		int retval = 0;
		for (int i = 0; i < getRowCount(); i++) {
			IETRWidget tr = getTR(i);
			if (tr.getColCount() > retval) {
				retval = tr.getColCount();
			}
		}
		return retval;
	}

	/**
	 * Set the row index on each TR of this table
	 */
	public void setTRRowIndex() {
		_sequenceTR.setTRRowIndex(new Incrementer());
	}

	public String getPourcentageWidths() {
		udatePourcentageWidths();
		return _pourcentageWidths;
	}

	public void setPourcentageWidths(String s) {
		_pourcentageWidths = s;
		StringTokenizer tok = new StringTokenizer(s, ";", false);
		while (tok.hasMoreElements()) {
			pourcentageArray.add(new Double(tok.nextToken()));
		}
	}

	private void udatePourcentageWidths() {
		initPourcentageArray();
		StringBuffer buf = new StringBuffer();
		Enumeration en = pourcentageArray.elements();
		while (en.hasMoreElements()) {
			buf.append(((Double) en.nextElement()).doubleValue());
			if (en.hasMoreElements()) {
				buf.append(";");
			}
		}
		_pourcentageWidths = buf.toString();
	}

	@Override
	public String getDefaultInspectorName() {
		return "HTMLTable.inspector";
	}

	public IETRWidget getFirstTR() {
		if (_sequenceTR.size() > 0) {
			return _sequenceTR.getFirstTR();
		}
		return null;
	}

	/**
	 * Removes the column col from the table.
	 * 
	 * @param col
	 *            - the column to remove
	 */
	public void deleteCol(int col) {
		Vector<IETDWidget> spanners = new Vector<IETDWidget>();// Spanners are
		// spanned TD
		// that have a
		// span-TD in
		// the column to
		// delete
		for (int i = 0; i < getRowCount(); i++) {
			IETDWidget td = getTDAt(i, col);
			if (td instanceof IESpanTDWidget) {
				IETDWidget sp = ((IESpanTDWidget) td).getSpanner();
				if (!spanners.contains(sp)) {
					spanners.add(sp);
				}
			}
			if (td != null) {
				td.makeRealDelete(true);
			} else if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not delete TD at (" + i + "," + col + "). XML:\n" + getXMLRepresentation());
			}
		}
		Enumeration<IETDWidget> en = spanners.elements();
		while (en.hasMoreElements()) {
			IETDWidget td = en.nextElement();
			if (!td.isDeleted()) {
				td.descreaseColSpanCausedByColumnDeletion();
			}
		}
		reIndexTable();
		int newColCount = getColCount();
		double removedColPourcentage = pourcentageArray.get(col).doubleValue();
		double oldColIncrement = removedColPourcentage / newColCount;
		pourcentageArray.remove(col);
		applyIncrementToCols(oldColIncrement, true);
		notifyDisplayNeedsRefresh();
		notifyPercentageChangeFromColToCol(0, getColCount());
		setChanged();
		notifyObservers(new ColumnRemoved(col));
	}

	/**
	 * Removes the row row from the table.
	 * 
	 * @param row
	 *            - the row to remove
	 */
	public void deleteRow(IETRWidget tr) {
		Vector<IETDWidget> spanners = new Vector<IETDWidget>();
		int colCount = getColCount();
		for (int i = 0; i < colCount; i++) {
			IETDWidget td = tr.getTD(i);
			if (td instanceof IESpanTDWidget) {
				IETDWidget sp = ((IESpanTDWidget) td).getSpanner();
				if (!spanners.contains(sp)) {
					spanners.add(sp);
				}
			}
			if (td != null) {
				td.makeRealDelete(true);
			} else if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not delete TD at (" + tr + "," + i + "). XML:\n" + getXMLRepresentation());
			}

		}
		Enumeration<IETDWidget> en = spanners.elements();
		while (en.hasMoreElements()) {
			IETDWidget td = en.nextElement();
			if (!td.isDeleted()) {
				td.decreaseRowSpanCausedByRowDeletion();
			}
		}
		reIndexRows();
		setChanged();
		notifyObservers(new TRRemoved(tr));
	}

	public void insertRow(IESequenceTR str, int row) {
		Vector<IETDWidget> spanners = new Vector<IETDWidget>();
		IETRWidget tr = new IETRWidget(getWOComponent(), str, true, getProject());
		tr.setRowIndex(row);
		int colCount = getColCount();
		// Let's look at that current row
		if (row < getRowCount()) {
			for (int i = 0; i < colCount; i++) {
				IETDWidget item = getTDAt(row, i);
				if (item instanceof IESpanTDWidget) {
					if (!((IESpanTDWidget) item).isOnRowSide()) {
						if (!spanners.contains(((IESpanTDWidget) item).getSpanner())) {
							spanners.add(((IESpanTDWidget) item).getSpanner());
						}
					}
				}
			}
		}
		// If the current row (row) has spans that are in the middle of a cell,
		// we will increase the span of the spanner
		Enumeration<IETDWidget> en = spanners.elements();
		while (en.hasMoreElements()) {
			IETDWidget td = en.nextElement();
			td.increaseRowSpanCausedByRowInsertion(tr, row);
		}
		// We compute where to insert the new row
		Enumeration<ITableRow> en1 = str.elements();
		int index = 0;
		while (en1.hasMoreElements()) {
			ITableRow item = en1.nextElement();
			if (item instanceof IETRWidget) {
				if (((IETRWidget) item).getRowIndex() < row) {
					index = ((IETRWidget) item).getIndex() + 1;
				} else {
					break;
				}
			}
		}
		// Watch out that next line launches a notification while the model is
		// not completely correct (getYLocation methods are no more correct)
		str.insertElementAt(tr, index);
		reIndexRows();
		setChanged();
		notifyObservers(new TRInserted(tr));
	}

	public void insertCol(int col) {
		// Let's find if some spanned cells are in the middle of the column we
		// want to insert
		Vector<IETDWidget> spanners = new Vector<IETDWidget>();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			IETDWidget td = getTDAt(i, col);
			if (td instanceof IESpanTDWidget) {
				if (!((IESpanTDWidget) td).isOnColSide()) {
					if (!spanners.contains(((IESpanTDWidget) td).getSpanner())) {
						spanners.add(((IESpanTDWidget) td).getSpanner());
					}
				}
			}
		}

		// We adjust column width so that there is room for the new column
		int newColCount = getColCount() + 1;
		double newColPourcentage = 1.0d / newColCount;
		double oldColIncrement = -1.0d * newColPourcentage / (newColCount - 1);
		applyIncrementToCols(oldColIncrement, false);
		pourcentageArray.insertElementAt(new Double(newColPourcentage), col);
		checkAndFixColumnWidth();
		// We insert the column, i.e. a cell in each row
		for (int i = 0; i < rowCount; i++) {
			IETRWidget tr = getTR(i);
			tr.insertEmptyTDAtCol(col);// This also reIndexes cells inside that
			// row
		}

		// Now we ask spanners to increase their colspan in the column
		Enumeration<IETDWidget> en = spanners.elements();
		while (en.hasMoreElements()) {
			IETDWidget td = en.nextElement();
			td.increaseColSpanCausedByColumnInsertion(col);
		}
		setChanged();
		notifyObservers(new ColumnInserted(col));
	}

	public void setPercentageForTD(double percentage, IETDWidget td) throws InvalidPercentage {
		if (!isPercentageAcceptable(td, (int) (percentage * 100))) {
			throw new InvalidPercentage((int) (percentage * 100));
		}
		int col = td.getXLocation();
		if (col > pourcentageArray.size()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Tried to set a new percentage on a column that does not exist");
			}
			return;
		}
		if (percentage > 1) {
			return;
		}
		if (col + 1 == pourcentageArray.size()) {
			double offset = getPourcentage(col, 1) - percentage;
			for (int i = col - 1; i >= 0 && offset != 0; i--) {
				double p = pourcentageArray.get(i);
				if (p + offset < 0) {
					offset += p - 0.01;
					p = 0.01;
				} else {
					p += offset;
					offset = 0;
				}
				pourcentageArray.set(i, p);
			}
			pourcentageArray.set(col, percentage);
			checkAndFixColumnWidth();
			notifyDisplayNeedsRefresh();
			notifyPercentageChangeFromColToCol(0, getColCount());
		} else {
			double offset = getPourcentage(col, 1) - percentage;
			for (int i = col + 1; i < pourcentageArray.size(); i++) {
				double p = pourcentageArray.get(i);
				if (p + offset < 0) {
					offset += p - 0.01;
					p = 0.01;
				} else {
					p += offset;
					offset = 0;
				}
				pourcentageArray.set(i, p);
			}
			pourcentageArray.set(col, percentage);
			checkAndFixColumnWidth();
			notifyDisplayNeedsRefresh();
			notifyPercentageChangeFromColToCol(col, getColCount());
		}
	}

	public boolean isPercentageAcceptable(IETDWidget td, int newPercentage) {
		if (td.getXLocation() + 1 == getColCount()) {
			double leftPercentage = getPourcentage(0, td.getXLocation());
			if (newPercentage > td.getPercentage() && newPercentage - td.getPercentage() > leftPercentage * 100 - getColCount() - 1) {
				return false;
			}
		} else {
			double leftPercentage = (int) (getPourcentage(td.getXLocation() + 1, getColCount() - td.getXLocation() - 1) * 100);
			leftPercentage /= 100;
			if (newPercentage > td.getPercentage()
					&& newPercentage - td.getPercentage() > leftPercentage * 100 - getColCount() + td.getXLocation()) {
				return false;
			}
		}
		return true;
	}

	/*private void applyIncrementToCols(double d)
	{
		applyIncrementToCols(d, true);
	}*/

	private void applyIncrementToCols(double d, boolean checkAndFix) {
		initPourcentageArray();
		for (int i = 0; i < pourcentageArray.size(); i++) {
			Double newVal = new Double(pourcentageArray.get(i).doubleValue() + d);
			pourcentageArray.set(i, newVal);
		}
		for (int i = 0; i < pourcentageArray.size(); i++) {
			Double newVal = pourcentageArray.get(i);
			if (newVal < 0.01) {
				pourcentageArray.set(i, newVal);
			}
		}
		if (checkAndFix) {
			checkAndFixColumnWidth();
		}
		notifyDisplayNeedsRefresh();
		notifyPercentageChangeFromColToCol(0, getColCount());
	}

	private void checkAndFixColumnWidth() {
		// If there are too small values we set them to the minimum: 0.01
		for (int i = 0; i < pourcentageArray.size(); i++) {
			Double d = pourcentageArray.get(i);
			if (d < 0.01) {
				pourcentageArray.set(i, 0.01);
			}
		}

		// We check that the sum of percentages equals to 1.
		if (pourcentageArray.size() > 0) {
			double sum = 0;
			for (Double d : pourcentageArray) {
				sum += d;
			}
			sum -= 1;
			for (int i = pourcentageArray.size() - 1; i >= 0 && sum != 0; i--) {
				double d = pourcentageArray.get(i);
				if (d - 0.01 > sum) {
					pourcentageArray.set(i, d - sum);
					sum = 0;
				} else {
					sum -= d - 0.01;
					pourcentageArray.set(i, 0.01);
				}
			}
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This table has no columns, I will attempt to remove it.");
			}
			removeFromContainer();
		}
	}

	@Override
	public void performOnDeleteOperations() {
		_sequenceTR.setParent(null);
		_sequenceTR.delete();
		super.performOnDeleteOperations();
	}

	public Enumeration<ITableRow> rowsEnumeration() {
		return _sequenceTR.elements();
	}

	/**
	 * Returns the number of columns of this Table
	 * 
	 * @return - the number of columns
	 */
	public int getColCount() {
		return getSequenceTR().getColCount();
	}

	/**
	 * Returns the number of rows of this table
	 * 
	 * @return - the number of rows
	 */
	public int getRowCount() {
		return getSequenceTR().getRowCount();
	}

	public void setColCount(int colCount) throws ColumnIsNotEmpty, ColCountCannotBeZeroOrNegative {
		if (colCount < 1) {
			throw new ColCountCannotBeZeroOrNegative();
		}
		if (getColCount() > colCount) {
			int previousColCount = getColCount() + 1;
			while (getColCount() > colCount && previousColCount > getColCount()) {
				previousColCount = getColCount();
				if (columnIsEmpty(getColCount() - 1)) {
					IETDWidget td = getTDAt(0, getColCount() - 1);
					if (!td.isDeleted()) {
						td.deleteCol();
					}
				} else {
					setChanged();
					notifyObserversAsReentrantModification(new IEDataModification("colCount", previousColCount, getColCount()));
					throw new ColumnIsNotEmpty(getColCount());
				}
			}
		} else if (getColCount() < colCount) {
			while (getColCount() < colCount) {
				insertCol(getColCount());
			}
		} else if (logger.isLoggable(Level.INFO)) {
			logger.info("Setting colcount to " + colCount + " and it is already the case");
		}
	}

	/**
	 * @param i
	 * @return
	 */
	private boolean columnIsEmpty(int col) {
		return isColFree(col, 0, getRowCount() - 1);
	}

	public void setRowCount(int rowCount) throws RowIsNotEmpty, RowCountCannotBeZeroOrNegative {
		if (rowCount < 1) {
			throw new RowCountCannotBeZeroOrNegative();
		}
		if (getRowCount() > rowCount) {
			int previousRowcount = getRowCount() + 1;
			while (getRowCount() > rowCount && previousRowcount > getRowCount()) {
				previousRowcount = getRowCount();
				if (rowIsEmpty(getRowCount() - 1)) {
					getTDAt(getRowCount() - 1, 0).deleteRow();
				} else {
					setChanged();
					notifyObserversAsReentrantModification(new IEDataModification("rowCount", previousRowcount, getRowCount()));
					throw new RowIsNotEmpty(getRowCount());
				}
			}
		} else if (getRowCount() < rowCount) {
			while (getRowCount() < rowCount) {
				logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
				// TODO: Please implement this better later
				// Used editor will be null
				insertRow(getTDAt(getRowCount() - 1, 0).tr().getSequenceTR(), getRowCount());
			}
		} else if (logger.isLoggable(Level.INFO)) {
			logger.info("Setting rowCount to " + rowCount + " and it is already the case");
		}

	}

	/**
	 * @param i
	 * @return
	 */
	private boolean rowIsEmpty(int col) {
		return isRowFree(col, 0, getColCount() - 1);
	}

	/**
	 * Returns wheter all the cells in column col from startRow to endRow (included) are empty.
	 * 
	 * @param col
	 *            - the column to check
	 * @param startRow
	 *            - the starting row to check
	 * @param endRow
	 *            - the ending row to check (included too)
	 * @return - true if all cells located from (startRow,col) to (endRow,col) are empty
	 */
	public boolean isColFree(int col, int startRow, int endRow) {
		if (col > getColCount() - 1) {
			return false;
		}
		for (int i = startRow; i < endRow + 1; i++) {
			IETDWidget td = null;
			td = getTDAt(i, col);
			if (td == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("This is weird. I could not find TD at (" + i + "," + col + ")");
				}
				continue;
			}
			if (td instanceof IESpanTDWidget) {
				return false;
			}
			if (!td.isEmpty() || td.getRowSpan() > 1 || td.getColSpan() > 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all TD located in column col from row startRow until row endRow (included).
	 * 
	 * @param col
	 *            - the selected column
	 * @param startRow
	 *            - the starting row
	 * @param endRow
	 *            - the ending row (included)
	 * @return all cells from (startRow,col) until (endRow,col)
	 */
	public Vector<IETDWidget> tdsInCol(int col, int startRow, int endRow) {
		Vector<IETDWidget> answer = new Vector<IETDWidget>();
		for (int i = startRow; i < endRow + 1; i++) {
			if (getTDAt(i, col) != null) {
				answer.add(getTDAt(i, col));
			}
		}
		return answer;
	}

	public Vector<Point> tdsShownInCol(int col, int startRow, int endRow) {
		Vector<Point> answer = new Vector<Point>();
		for (int i = startRow; i < endRow + 1; i++) {
			answer.add(new Point(col, i));
		}
		return answer;
	}

	public boolean isRowFree(int row, int startCol, int endCol) {
		if (row > _sequenceTR.getWidgetCount() - 1) {
			return false;
		}
		for (int i = startCol; i < endCol + 1; i++) {
			IETDWidget td = null;
			td = getTDAt(row, i);
			if (td == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("This is weird. Could not find TD located at (" + row + "," + i + ")");
				}
				continue;
			}
			if (td instanceof IESpanTDWidget) {
				return false;
			}
			if (!td.isEmpty() || td.getRowSpan() > 1 || td.getColSpan() > 1) {
				return false;
			}
		}
		return true;
	}

	public Vector<IETDWidget> tdsInRow(int row, int startCol, int endCol) {
		Vector<IETDWidget> answer = new Vector<IETDWidget>();
		for (int i = startCol; i < endCol + 1; i++) {
			if (getTDAt(row, i) != null) {
				answer.add(getTDAt(row, i));
			}
		}
		return answer;
	}

	public Vector<Point> tdsShownInRow(int row, int startCol, int endCol) {
		Vector<Point> answer = new Vector<Point>();
		for (int i = startCol; i < endCol + 1; i++) {
			answer.add(new Point(i, row));
		}
		return answer;
	}

	/**
	 * Returns the TD located in row with rowIndex (row) and with xLocation equal to col
	 * 
	 * @param row
	 *            - the row in which the cell is located
	 * @param col
	 *            - the column at which the cell is located
	 * @return
	 */
	public IETDWidget getTDAt(int row, int col) {
		if (getTR(row) == null) {
			return null;
		}
		return getTR(row).getTD(col);
	}

	/**
	 * Returns the row (indexed from 0) with rowIndex equal to row in the whole table.
	 * 
	 * @param row
	 *            - the row index of the row to retrieve
	 * @return the row with rowIndex equal to row
	 */
	public IETRWidget getTR(int row) {
		return _sequenceTR.getTRAtRow(row);
	}

	public String getConditionals() {
		return _conditionals;
	}

	public void setConditionals(String c) {
		this._conditionals = c;
	}

	public boolean getGrid() {
		return _grid;
	}

	public void setGrid(boolean g) {
		this._grid = g;
	}

	public boolean getNoCSS() {
		return _noCss;
	}

	public void setNoCSS(boolean css) {
		boolean old = _noCss;
		_noCss = css;
		setChanged();
		notifyModification("noCSS", old, _noCss);
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(_sequenceTR);
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "HTMLTable";
	}

	public boolean isShowingBorder() {
		return _isShowingBorder;
	}

	public void setIsShowingBorder(boolean v) {
		if (v == _isShowingBorder) {
			return;
		}

		_isShowingBorder = v;
		setChanged();
		notifyObservers(new DisplayBorderChanged(this));
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> v = super.getSpecificActionListForThatClass();
		v.add(TopComponentUp.actionType);
		v.add(TopComponentDown.actionType);
		v.add(DropIEElement.actionType);
		return v;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return HTML_TABLE_WIDGET;
	}

	public IESequenceTR getSequenceTR() {
		return _sequenceTR;
	}

	public IESequenceTR getRepeatedRow() {
		return _sequenceTR.getFirstRepeatedSequence();
	}

	public void setSequenceTR(IESequenceTR _sequencetr) {
		_sequenceTR = _sequencetr;
		_sequenceTR.setParent(this);
	}

	public double getPourcentage(int start, int length) {
		initPourcentageArray();
		double reply = 0.0d;
		for (int i = start; i < start + length; i++) {
			reply = reply + pourcentageArray.get(i).doubleValue();
		}
		return reply;
	}

	private void initPourcentageArray() {
		if (pourcentageArray.size() == 0) {
			for (int i = 0; i < getColCount(); i++) {
				pourcentageArray.insertElementAt(new Double(1.0d / (0.0d + getColCount())), i);
			}
		}
	}

	public void adjustPourcentage(IETDWidget td1, double p1, IETDWidget td2) {
		initPourcentageArray();
		int td1Width = td1.getColSpan();
		int td2Width = td2.getColSpan();
		double td1increment = p1 / td1Width;
		double td2increment = -1 * p1 / td2Width;

		if (!isPercentageAcceptable(td1, (int) (td1.getPourcentage() + p1 * 100))) {
			return;
		}
		if (!isPercentageAcceptable(td2, (int) (td2.getPourcentage() - p1 * 100))) {
			return;
		}

		for (int i = 0; i < td1Width; i++) {
			pourcentageArray.set(td1.getXLocation() + i, new Double(td1increment
					+ pourcentageArray.get(td1.getXLocation() + i).doubleValue()));
		}
		for (int i = 0; i < td2Width; i++) {
			pourcentageArray.set(td2.getXLocation() + i, new Double(td2increment + pourcentageArray.get(td2.getXLocation()).doubleValue()));
		}
		udatePourcentageWidths();
		notifyDisplayNeedsRefresh();
		notifyPercentageChangeFromColToCol(td1.getXLocation(), td2.getXLocation() + 1);
	}

	@Override
	public void notifyDisplayNeedsRefresh() {
		_sequenceTR.holdDisplayRefresh();
		int rowCount = getRowCount();
		int colCount = getColCount();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				IETDWidget td = getTDAt(i, j);
				if (td != null) {
					td.notifyDisplayNeedsRefresh();
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("This is weird. Could not find TD located at (" + i + "," + j + ")");
					}
					continue;
				}
			}
		}
		_sequenceTR.activateDisplayRefresh();
		_sequenceTR.setChanged();
		_sequenceTR.notifyObservers(new PercentageChanged(this));
	}

	/**
	 * Notifies all TD located in column from startCol to endCol (not included). For example, if you want to notify all columns of column 0,
	 * then you need to pass startCol=0 and endCol=1.
	 * 
	 * @param startCol
	 *            - The starting column to notify
	 * @param endCol
	 *            - The end column (not included)
	 */
	private void notifyPercentageChangeFromColToCol(int startCol, int endCol) {
		_sequenceTR.holdDisplayRefresh();
		int rowCount = getRowCount();
		for (int i = 0; i < rowCount; i++) {
			for (int j = startCol; j < endCol; j++) {
				IETDWidget td = getTDAt(i, j);
				if (td == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("This is weird. I could not find TD located at (" + i + "," + j + ")");
					}
					continue;
				} else if (td instanceof IESpanTDWidget) {
					continue;
				} else {
					td.notifyDisplayNeedsRefresh();
				}
			}
		}
		_sequenceTR.activateDisplayRefresh();
	}

	public IESequenceTR getFirstRepeatedSequence() {
		return _sequenceTR.getFirstRepeatedSequence();
	}

	public IETRWidget getHeaderRowForSequence(ITableRow repeatedSequence) {
		return getTR(repeatedSequence.getFirstTR().getRowIndex() - 1);
	}

	/**
	 * Inserts a spanned TD at location (row,col) for the td. The method will first create that spannedTD with <code>td</code> as its
	 * spanner. Then it will look for the TR in which the spannedTD must be inserted. If the TR cannot be found, it will be automatically
	 * created and inserted. Finally, we will look for the cell located at (row, col-1) and the spanned TD will be inserted right after it
	 * in that cell's sequence.
	 * 
	 * @param row
	 * @param col
	 */
	public void insertSpannedTDAtForTD(int row, int col, IETDWidget td) {
		IESpanTDWidget span = new IESpanTDWidget(td.getWOComponent(), td.getParent(), td, td.getProject());
		span.setXLocation(col);
		IETRWidget tr = _sequenceTR.getTRAtRow(row);
		if (tr == null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Could not find tr at row " + row + ". I will create it");
			}
			tr = new IETRWidget(getWOComponent(), getSequenceTR(), false, getProject());
			tr.setRowIndex(row);
			((IESequenceTR) td.tr().getParent()).addToInnerWidgets(tr);
		}
		int index = col - 1;
		IETDWidget previous = tr.getSequenceTD().getCellAtIndex(index);
		while (previous == null && index > 0) {
			previous = tr.getSequenceTD().getCellAtIndex(--index);
		}
		if (previous == null) {
			tr.getSequenceTD().insertElementAt(span, 0);
		} else {
			previous.getParent().insertElementAt(span, previous.getIndex() + 1);
		}
	}

	/**
	 * @param widget
	 * @param list
	 * @return
	 */
	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
		Enumeration<ITableRow> en = rowsEnumeration();
		while (en.hasMoreElements()) {
			ITableRow element = en.nextElement();
			v.addAll(element.getAllDateTextfields());
		}
		return v;
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration en = _sequenceTR.elements();
		while (en.hasMoreElements()) {
			ITableRow top = (ITableRow) en.nextElement();
			reply.addAll(top.getAllTabContainers());
		}
		return reply;
	}

	public void handleResize() {
		if (getParent() instanceof IEBlocWidget) {
			((IEBlocWidget) getParent()).handleContentResize();
		}
	}

	/**
	 * @param w
	 */
	public void notifyWidgetRemoval(ITableRow w) {
		setChanged();
		notifyObservers(new WidgetRemovedFromTable((IEWidget) w));
		handleResize();
	}

	/**
	 * @param o
	 */
	public void notifyWidgetInsertion(ITableRow o) {
		setChanged();
		notifyObservers(new WidgetAddedToTable((IEWidget) o, -1, -1));
		if (getParent() instanceof IEBlocWidget) {
			((IEBlocWidget) getParent()).handleContentResize();
		}
		handleResize();
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);
		if (_sequenceTR != null) {
			_sequenceTR.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (_sequenceTR != null) {
			return _sequenceTR.areComponentInstancesValid();
		} else {
			return true;
		}
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (_sequenceTR != null) {
			_sequenceTR.removeInvalidComponentInstances();
		}
	}

	/**
	 * Overrides getTitle
	 * 
	 * @see org.openflexo.foundation.ie.IETopComponent#getTitle()
	 */
	@Override
	public String getTitle() {
		return getLabel();
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
		Enumeration<ITableRow> en = rowsEnumeration();
		while (en.hasMoreElements()) {
			ITableRow element = en.nextElement();
			v.addAll(element.getAllButtonInterface());
		}
		return v;
	}

}
