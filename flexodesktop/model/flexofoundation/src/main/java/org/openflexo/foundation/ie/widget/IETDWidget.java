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

import java.awt.Color;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.SingleWidgetComponent;
import org.openflexo.foundation.ie.action.DecreaseColSpan;
import org.openflexo.foundation.ie.action.DecreaseRowSpan;
import org.openflexo.foundation.ie.action.DeleteCol;
import org.openflexo.foundation.ie.action.DeleteRow;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.action.IncreaseColSpan;
import org.openflexo.foundation.ie.action.IncreaseRowSpan;
import org.openflexo.foundation.ie.action.InsertColAfter;
import org.openflexo.foundation.ie.action.InsertColBefore;
import org.openflexo.foundation.ie.action.InsertRowAfter;
import org.openflexo.foundation.ie.action.InsertRowBefore;
import org.openflexo.foundation.ie.dm.AlignementChanged;
import org.openflexo.foundation.ie.dm.DisplayNeedsRefresh;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.VerticalAlignementChanged;
import org.openflexo.foundation.ie.dm.table.ColSpanDecrease;
import org.openflexo.foundation.ie.dm.table.WidgetInsertedInTD;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTD;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.util.TDCSSType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Represents a cell of a row of an HTML table
 * 
 * @author bmangez
 */
public class IETDWidget extends IEWidget implements WidgetsContainer, ITableData {

	public static class TDConstraintModification extends IEDataModification {

		public TDConstraintModification(TDConstraints newValue) {
			super("constraints", null, newValue);
		}

	}

	/**
     * 
     */
	public static final String TD_WIDGET = "td_widget";

	private static final Logger logger = FlexoLogger.getLogger(IETDWidget.class.getPackage().getName());

	public static final int MIN_WIDTH = 20;

	public static final int MIN_HEIGHT = 20;

	public static final String ALIGN_CENTER = "center";

	public static final String ALIGN_RIGHT = "right";

	public static final String ALIGN_LEFT = "left";

	public static final String VALIGN_TOP = "top";

	public static final String VALIGN_MIDDLE = "middle";

	public static final String VALIGN_BOTTOM = "bottom";

	@Deprecated
	private boolean _isHidden = false;

	private int _colSpan = 1;

	private int _rowSpan = 1;

	private int _xLocation = 0;

	private IESequenceWidget _sequenceWidget;

	private String _alignement;

	private String _verticalAlignement;

	public TDConstraints constraints;

	private Vector<IESpanTDWidget> spannedTD;

	public IETDWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IETDWidget(IEWOComponent woComponent, IESequenceTD parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		_sequenceWidget = new IESequenceWidget(woComponent, this, prj);
		constraints = new TDConstraints();
		spannedTD = new Vector<IESpanTDWidget>();
	}

	public double relativeTDWeight() {
		double d = new Double(getColSpan() == 0 ? 1 : getColSpan()).doubleValue() / new Double(htmlTable().getColCount()).doubleValue();
		return d;
	}

	@Override
	public String getDefaultInspectorName() {
		return "TD.inspector";
	}

	public void removeSequence(IESequence std) {
		_sequenceWidget = new IESequenceWidget(getWOComponent(), this, getProject());
		setChanged();
		notifyObservers(new SequenceOfTDChanged(this));
	}

	@Override
	public void performOnDeleteOperations() {
		_sequenceWidget.delete();
		super.performOnDeleteOperations();
	}

	@Override
	public void setParent(IEObject parent) {
		super.setParent(parent);
		if (!isDeserializing()) {
			IETRWidget tr = tr();
			if (tr != null) {
				constraints.gridy = tr.getIndex();
				constraintsChange();
			}
		}

	}

	/**
	 * When removing cells from a row or a column, this is the method to call.
	 * 
	 * @param notify
	 *            - wheter to notify the deletion of this cell or not.
	 */
	@Override
	public void makeRealDelete(boolean notify) {
		Enumeration<IESpanTDWidget> en1 = getSpannedTD().elements();
		while (en1.hasMoreElements()) {
			IESpanTDWidget td = en1.nextElement();
			td.replaceByNormalTD();
		}
		_sequenceWidget.delete();
		IETRWidget tr = tr();
		if (tr != null) {
			if (notify) {
				tr.removeFromCols(this);
			} else {
				tr.removeFromColsNoNotification(this);
			}
		}
		super.delete();
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.add(InsertColAfter.actionType);
		returned.add(InsertColBefore.actionType);
		returned.add(InsertRowAfter.actionType);
		returned.add(InsertRowBefore.actionType);
		returned.add(IncreaseColSpan.actionType);
		returned.add(DecreaseColSpan.actionType);
		returned.add(IncreaseRowSpan.actionType);
		returned.add(DecreaseRowSpan.actionType);
		returned.add(DeleteRow.actionType);
		returned.add(DeleteCol.actionType);
		returned.remove(IEDelete.actionType);
		// returned.remove(ExportWidgetToPalette.actionType);
		return returned;
	}

	/**
	 * Overrides getParent
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getParent()
	 */
	@Override
	public IESequenceTD getParent() {
		return (IESequenceTD) super.getParent();
	}

	/**
	 * Returns the parent sequenceTD of this widget
	 * 
	 * @return the parent sequenceTD of this widget
	 */
	public IESequenceTD getSequenceTD() {
		return getParent();
	}

	public Color getBackgroundColor() {
		return tr().getBackgroundColor();
	}

	@Override
	public IETRWidget tr() {
		if (_parent == null) {
			return null;
		}
		return getParent().tr();
	}

	public int getRowIndex() {
		return tr().getIndex();
	}

	public int getColIndex() {
		return getIndex();
	}

	@Deprecated
	public boolean canBeHidden() {
		return !_isHidden && _sequenceWidget.getWidgetCount() == 0;
	}

	@Deprecated
	public boolean getIsHidden() {
		return _isHidden;
	}

	@Deprecated
	public void setIsHidden(boolean v) {
		if (canBeHidden() && v) {
			_isHidden = true;
			setChanged();
			notifyObservers(new DataModification(DataModification.TD_HIDDEN, null, null));
		} else {
			if (_isHidden && !v) {
				_isHidden = false;
				setChanged();
				notifyObservers(new DataModification(DataModification.TD_UNHIDDEN, null, null));
			}
		}
	}

	public void insertChild(IEWidget child, boolean updateIndex) {
		Enumeration en = _sequenceWidget.elements();
		int i = 0;
		while (en.hasMoreElements()) {
			if (_sequenceWidget.get(i).getIndex() < child.getIndex()) {
				i++;
			}
			en.nextElement();
		}
		child.setParent(_sequenceWidget);
		_sequenceWidget.insertElementAt(child, i);
		if (updateIndex) {
			updateChildIndex();
		}
		setChanged();
		notifyObservers(new WidgetInsertedInTD(child));
	}

	public void updateChildIndex() {
		Enumeration en = _sequenceWidget.elements();
		int i = 0;
		while (en.hasMoreElements()) {
			((IEWidget) en.nextElement()).setIndex(i);
			i++;
		}
	}

	public IEHTMLTableWidget htmlTable() {
		IEWidget o = getParent();
		while (!(o instanceof IEHTMLTableWidget) && o != null) {
			if (o.getParent() instanceof SingleWidgetComponent) {
				return null;
			}
			o = (IEWidget) o.getParent();
		}
		if (o instanceof IEHTMLTableWidget) {
			return (IEHTMLTableWidget) o;
		}
		return null;
	}

	public void replaceWidgetByReusable(IEWidget oldWidget, InnerTableReusableWidget newWidget) {
		int i = oldWidget.getIndex();
		newWidget.setIndex(i);
		oldWidget.removeFromContainer();
		insertChild(newWidget, true);

	}

	public Enumeration<IEWidget> children() {
		return _sequenceWidget.elements();
	}

	public void setColSpan(int colSpan) throws InvalidOperation {
		if (isDeserializing() || isCreatedByCloning() || isDeserializing) {
			_colSpan = colSpan;
			constraints.gridwidth = colSpan;
		} else {
			if (_colSpan == colSpan) {
				return;
			}
			if (_colSpan > colSpan) {
				decreaseColSpan();
			} else {
				increaseColSpan();
			}
		}
	}

	public void setRowSpan(int rowSpan) throws InvalidOperation {
		if (isDeserializing() || isCreatedByCloning() || isDeserializing) {
			_rowSpan = rowSpan;
			constraints.gridheight = rowSpan;
		} else {
			if (_rowSpan == rowSpan) {
				return;
			}
			if (_rowSpan > rowSpan) {
				decreaseRowSpan();
			} else {
				increaseRowSpan();
			}
		}

	}

	@Override
	public int getColSpan() {
		return _colSpan;
	}

	public int getRowSpan() {
		return _rowSpan;
	}

	public int getXLocation() {
		return _xLocation;
	}

	public void setXLocation(int x) {
		if (_xLocation != x || constraints.gridx != x) {
			_xLocation = x;
			constraints.gridx = x;
			constraintsChange();
		}
	}

	public int getYLocation() {
		if (tr() != null) {
			return tr().getRowIndex();
		}
		return -1;
	}

	public void constraintsChange() {
		getSequenceWidget().setChanged();
		getSequenceWidget().notifyObservers(new TDConstraintModification(constraints));
	}

	public void increaseColSpan() throws InvalidOperation {
		if (canIncreaseColSpan() && htmlTable() != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Increasing colspan from " + _colSpan + " to " + (_colSpan + 1));
			}
			for (int i = 0; i < _rowSpan; i++) {
				IETDWidget td = htmlTable().getTDAt(getYLocation() + i, getXLocation() + _colSpan);
				if (td != null) {
					td.replaceBySpanTD(this);
				} else {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.severe("Could not find TD at (" + (getYLocation() + i) + "," + (getXLocation() + _colSpan) + ")");
					}
				}
			}
			_colSpan++;
			constraints.gridwidth++;
			notifyDisplayNeedsRefresh();
			setChanged();
			notifyObservers(new ColSpanDecrease(this));
		} else {
			throw new InvalidOperation("increase_colspan");
		}
	}

	/**
	 * Replace this TD by a span TD with spanner as its originating cell
	 * 
	 * @param spanner
	 *            - the cell that is extending
	 */
	private void replaceBySpanTD(IETDWidget spanner) {
		getParent().replaceTDBySpanTD(this, spanner);
	}

	public void increaseRowSpan() throws InvalidOperation {
		if (canIncreaseRowSpan()) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Increasing rowspan from " + _rowSpan + " to " + (_rowSpan + 1));
			}

			for (int i = 0; i < _colSpan; i++) {
				IETDWidget td = htmlTable().getTDAt(getYLocation() + _rowSpan, getXLocation() + i);
				if (td != null) {
					td.replaceBySpanTD(this);
				}
			}
			_rowSpan++;
			constraints.gridheight++;
			notifyDisplayNeedsRefresh();
		} else {
			throw new InvalidOperation("increase_rowspan");
		}
	}

	public void decreaseColSpan() throws InvalidOperation {
		if (canDecreaseColSpan()) {
			_colSpan--;
			Enumeration<IESpanTDWidget> en = ((Vector<IESpanTDWidget>) getSpannedTD().clone()).elements();
			while (en.hasMoreElements()) {
				IESpanTDWidget span = en.nextElement();
				if (span.getXLocation() == getXLocation() + _colSpan && span.getYLocation() >= getYLocation()
						&& span.getYLocation() < getYLocation() + getRowSpan()) {
					span.replaceByNormalTD();
					getSpannedTD().remove(span);
				}
			}

			constraints.gridwidth--;
			notifyDisplayNeedsRefresh();
			setChanged();
			notifyObservers(new ColSpanDecrease(this));
		} else {
			throw new InvalidOperation("decrease_colspan");
		}
	}

	public void decreaseRowSpan() throws InvalidOperation {
		if (canDecreaseRowSpan()) {
			_rowSpan--;
			Enumeration<IESpanTDWidget> en = ((Vector<IESpanTDWidget>) getSpannedTD().clone()).elements();
			while (en.hasMoreElements()) {
				IESpanTDWidget span = en.nextElement();
				if (span.getYLocation() == getYLocation() + _rowSpan && span.getXLocation() >= getXLocation()
						&& span.getXLocation() < getXLocation() + getColSpan()) {
					span.replaceByNormalTD();
					getSpannedTD().remove(span);
				}
			}
			constraints.gridheight--;
			notifyDisplayNeedsRefresh();
		} else {
			throw new InvalidOperation("decrease_rowspan");
		}
	}

	public boolean canIncreaseRowSpan() {
		if (htmlTable() == null) {
			return false;
		}
		boolean b = htmlTable().isRowFree(getYLocation() + getRowSpan(), getXLocation(), getXLocation() + getColSpan() - 1);
		if (!b) {
			return b;
		}
		IETRWidget tr = htmlTable().getTR(getYLocation() + getRowSpan());
		return tr != null && tr.getParent() == tr().getParent();
	}

	public boolean canIncreaseColSpan() {
		if (htmlTable() == null) {
			return false;
		}
		return htmlTable().isColFree(getXLocation() + getColSpan(), getYLocation(), getYLocation() + getRowSpan() - 1);
	}

	public boolean canDecreaseRowSpan() {
		return getRowSpan() > 1;
	}

	public boolean canDecreaseColSpan() {
		return getColSpan() > 1;
	}

	public Color getBackground() {
		if (tr() != null) {
			if (tr().getRowIndex() % 2 == 0) {
				return getFlexoCSS().getOtherLineColor();
			}
			return getFlexoCSS().getOddLineColor();
		}
		return Color.GREEN;
	}

	public boolean isHeaderCell() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc != null) {
			if (desc.isHeaderCell(this)) {
				return true;
			}
		}
		return false;
	}

	public TDCSSType getTDCssType() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		if (desc != null) {
			if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IEHeaderWidget) {
				return TDCSSType.DL_LIST_ROW_TITLE;
			}
			if (desc.isHeaderCell(this)) {
				if (isLastCell()) {
					return TDCSSType.DL_LIST_ROW_LAST_TITLE;
				} else {
					return TDCSSType.DL_LIST_ROW_TITLE;
				}
			}
			if (desc.isRepeatedCell(this)) {
				if (isLastCell()) {
					return TDCSSType.DL_LIST_LAST_CELL;
				} else {
					return TDCSSType.DL_LIST_CELL;
				}
			}
			if (desc.isInSearchArea(this)) {
				if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IELabelWidget) {
					return TDCSSType.DL_BLOCKTOOLS_CONTENT_TITLE;
				}
				if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
					return TDCSSType.DL_BLOCKTOOLS_CONTENT;
				}
				if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IETextFieldWidget) {
					return TDCSSType.DL_BLOCKTOOLS_CONTENT_CONTENT;
				}
				if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IEDropDownWidget) {
					return TDCSSType.DL_BLOCKTOOLS_CONTENT_CONTENT;
				}

			}

		} else {
			if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IELabelWidget) {
				if (((IELabelWidget) getSequenceWidget().get(0)).getTextCSSClass() != null) {
					return ((IELabelWidget) getSequenceWidget().get(0)).getTDCSSTypeDerivedFrowTextCSSSlass();
				} else {
					return TDCSSType.DL_BLOCK_BODY_TITLE;
				}
			} else if (getSequenceWidget().size() == 1 && getSequenceWidget().get(0) instanceof IEHeaderWidget) {
				return TDCSSType.DL_LIST_ROW_TITLE;
			}
			return TDCSSType.DL_BLOCK_BODY_CONTENT;
		}
		return null;
	}

	public void setTDCssType(TDCSSType type) {
		// Nothing to do here. Css can be computed on the fly
	}

	public boolean isLastCell() {
		return tr().getSequenceTD().getAllRealTD().lastElement() == this;
	}

	public Vector<Point> usedArea() {
		Vector<Point> answer = new Vector<Point>();
		if (getColSpan() > 0 && getRowSpan() > 0) {
			for (int i = constraints.gridx; i < constraints.gridx + getColSpan(); i++) {
				for (int j = constraints.gridy; i < constraints.gridy + getRowSpan(); j++) {
					answer.add(new Point(i, j));
				}
			}
		}
		return answer;
	}

	public class TDConstraints extends IEHTMLTableConstraints {

		public TDConstraints() {
			super();
			fill = BOTH;
			anchor = NORTHWEST;
			weightx = 1.0;
			weighty = 1.0;
			gridwidth = 1;
		}

	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		Vector<IObject> answer = new Vector<IObject>();
		answer.add(_sequenceWidget);
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Cell";
	}

	public String getAlignement() {
		return _alignement;
	}

	public void setAlignement(String value) {
		_alignement = value;
		setChanged();
		notifyObservers(new AlignementChanged(_alignement));
	}

	public String getVerticalAlignement() {
		return _verticalAlignement;
	}

	public void setVerticalAlignement(String value) {
		_verticalAlignement = value;
		setChanged();
		notifyObservers(new VerticalAlignementChanged(_verticalAlignement));
	}

	public void addToInnerWidgets(IEWidget w) {
		_sequenceWidget.addToInnerWidgets(w);
		w.setParent(_sequenceWidget);
	}

	public void removeFromInnerWidgets(IEWidget w) {
		_sequenceWidget.removeFromInnerWidgets(w);
		setChanged();
		notifyObservers(new WidgetRemovedFromTable(w));
	}

	public String weightx() {
		return String.valueOf(constraints.weightx);
	}

	public void setWeightx(String value) {
		// constraints.weightx = Double.parseDouble(value);
	}

	public String weighty() {
		return String.valueOf(constraints.weighty);
	}

	public void setWeighty(String value) {
		constraints.weighty = Double.parseDouble(value);
	}

	public String gridx() {
		return String.valueOf(constraints.gridx);
	}

	public void setGridx(String value) {
		constraints.gridx = Integer.parseInt(value);
	}

	public String gridy() {
		return String.valueOf(constraints.gridy);
	}

	public void setGridy(String value) {
		constraints.gridy = Integer.parseInt(value);
	}

	@Override
	public void deleteCol() {
		if (htmlTable() != null) {
			htmlTable().deleteCol(getXLocation());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not delete column because there is no Table");
			}
		}

	}

	@Override
	public void deleteRow() {
		if (htmlTable() != null) {
			htmlTable().deleteRow(tr());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not delete row because there is no Table");
			}
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return TD_WIDGET;
	}

	public IESequenceWidget getSequenceWidget() {
		return _sequenceWidget;
	}

	public void setSequenceWidget(IESequenceWidget widget) {
		_sequenceWidget = widget;
		_sequenceWidget.setParent(this);
	}

	@Override
	public Vector<IEWidget> getInnerWidgets() {
		return _sequenceWidget.getInnerWidgets();
	}

	/**
	 * Returns the previous TD in this row. Previous TD can be a SpanTD.
	 * 
	 * @return - the previous TD
	 */
	public IETDWidget getPrevious() {
		return tr().getTDatXLocation(getXLocation() - 1);
	}

	/**
	 * Returns the next TD in this row. Next TD can be a SpanTD
	 * 
	 * @return - the next TD
	 */
	public IETDWidget getNext() {
		return tr().getTDatXLocation(getXLocation() + 1);
	}

	@Override
	public IEHTMLTableConstraints getConstraints() {
		return constraints;
	}

	@Override
	public Enumeration<ITableData> elements() {
		Vector<ITableData> v = new Vector<ITableData>();
		v.add(this);
		return v.elements();
	}

	@Override
	public double getPourcentage() {
		if (htmlTable() != null) {
			return htmlTable().getPourcentage(getXLocation(), getColSpan());
		}
		return 0.25d;
	}

	public String getPourcentageInString() {
		return String.valueOf(getPercentage());
	}

	public boolean isEmpty() {
		return _sequenceWidget.isEmpty();
	}

	public boolean isSpannedTD() {
		return this instanceof IESpanTDWidget;
	}

	public Vector<IESpanTDWidget> getSpannedTD() {
		return spannedTD;
	}

	public void setSpannedTD(Vector<IESpanTDWidget> spannedTD) {
		this.spannedTD = spannedTD;
	}

	public void addToSpannedTD(IESpanTDWidget td) {
		if (!this.spannedTD.contains(td)) {
			this.spannedTD.add(td);
		}
	}

	public void removeFromSpannedTD(IESpanTDWidget td) {
		this.spannedTD.remove(td);
	}

	/**
	 * Overrides insertSpannedTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableData#insertSpannedTD()
	 */
	@Override
	public void insertSpannedTD() {
		IEHTMLTableWidget htmlTable = htmlTable();
		if (_colSpan > 1) {
			IESequenceTD parent = getParent();
			for (int i = 1; i < _colSpan; i++) {
				if (htmlTable.getTDAt(getYLocation(), getXLocation() + i) != null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("It seems that spanned td has already been inserted or something is in the way. Not inserting."
								+ htmlTable.getTDAt(getYLocation(), getXLocation() + i));
					}
					continue;
				}
				IESpanTDWidget span = new IESpanTDWidget(getWOComponent(), parent, this, getProject());
				span.setXLocation(getXLocation() + i);
				parent.insertElementAt(span, getIndex() + i);
			}
		}
		if (_rowSpan > 1) {
			if (htmlTable != null) {
				for (int i = 1; i < _rowSpan; i++) {
					for (int j = 0; j < _colSpan; j++) {
						if (htmlTable.getTDAt(getYLocation() + i, getXLocation() + j) != null) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("It seems that spanned td has already been inserted or something is in the way. Not inserting."
										+ htmlTable.getTDAt(getYLocation() + i, getXLocation() + j));
							}
							continue;
						}
						htmlTable.insertSpannedTDAtForTD(getYLocation() + i, getXLocation() + j, this);
					}
				}
			}
		}
	}

	public void descreaseColSpanCausedByColumnDeletion() {
		_colSpan--;
		constraints.gridwidth = _colSpan;
		constraintsChange();
	}

	public void decreaseRowSpanCausedByRowDeletion() {
		_rowSpan--;
		constraints.gridheight = _rowSpan;
		constraintsChange();
	}

	/**
	 * Overrides setXLocation
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableData#setXLocation(org.openflexo.foundation.ie.widget.IEHTMLTableWidget.Incrementer)
	 */
	@Override
	public void setXLocation(Incrementer incrementer) {
		setXLocation(incrementer.getValue());
		incrementer.increment();
	}

	/**
	 * Overrides toString
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder(50).append(getClass().getName()).append(" TD located at (").append(getYLocation()).append(',')
				.append(getXLocation()).append(')').append(" contains ").append(_sequenceWidget != null ? _sequenceWidget.size() : "---")
				.append(" elements").toString();
	}

	/**
	 * Overrides getFirstTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableData#getFirstTD()
	 */
	@Override
	public IETDWidget getFirstTD() {
		return this;
	}

	/**
	 * Inserts all the spanTD after a rowInsertion if the inserted row is in the middle of the cell
	 * 
	 * @param tr
	 *            - the inserted row
	 */
	public void increaseRowSpanCausedByRowInsertion(IETRWidget tr, int rowIndex) {
		if (rowIndex >= getYLocation() && rowIndex < getYLocation() + getRowSpan()) {
			for (int i = getXLocation(); i < getXLocation() + getColSpan(); i++) {
				IETDWidget td = tr.getTDatXLocation(i);
				td.replaceBySpanTD(this);
			}
			_rowSpan++;
			constraints.gridheight++;
			notifyDisplayNeedsRefresh();
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Tried to increaseRowSpanCausedByRowInsertion while the TR is not in the middle of the cell.");
		}
	}

	/**
	 * Replaces all cells of the column col by a span TD of this cell.
	 * 
	 * @param col
	 */
	public void increaseColSpanCausedByColumnInsertion(int col) {
		if (htmlTable() != null) {
			for (int i = 0; i < _rowSpan; i++) {
				IETDWidget td = htmlTable().getTDAt(getYLocation() + i, col);
				td.replaceBySpanTD(this);
			}
			_colSpan++;
			constraints.gridwidth++;
			notifyDisplayNeedsRefresh();
		}
	}

	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
		v.addAll(_sequenceWidget.getAllDateTextfields());
		return v;
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration<IEWidget> en = children();
		while (en.hasMoreElements()) {
			IEWidget element = en.nextElement();
			if (element instanceof IESequenceTab) {
				reply.add((IESequenceTab) element);
			}
		}
		return reply;
	}

	/**
	 * Overrides getAllTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableData#getAllTD()
	 */
	@Override
	public Vector<IETDWidget> getAllTD() {
		Vector<IETDWidget> v = new Vector<IETDWidget>();
		v.add(this);
		return v;
	}

	/**
	 * @param o
	 */
	public void notifyWidgetInserted(IEWidget o) {
		setChanged();
		notifyObservers(new WidgetInsertedInTD(o));
	}

	/**
	 * @param o
	 */
	public void notifyWidgetRemoved(IEWidget o) {
		setChanged();
		notifyObservers(new WidgetRemovedFromTD(o));
	}

	@Override
	public void notifyDisplayNeedsRefresh() {
		Enumeration<IEWidget> en = _sequenceWidget.elements();
		while (en.hasMoreElements()) {
			IEWidget w = en.nextElement();
			w.notifyDisplayNeedsRefresh();
		}
		_sequenceWidget.setChanged();
		_sequenceWidget.notifyObservers(new DisplayNeedsRefresh(this));
	}

	public boolean isSearchBlockToolTD() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		IEBlocWidget bloc = findBlocInParent();
		if (desc != null && bloc != null && htmlTable().equals(bloc.getContent())) {
			IEBlocWidget block = findBlocInParent();
			if (block != null && htmlTable().equals(block.getContent())) {
				if (htmlTable().getColCount() == 1 && htmlTable().getRowCount() == 2) {
					if (this.equals(htmlTable().getTDAt(0, 0))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isTDSurroundingAList() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		IEBlocWidget block = findBlocInParent();
		if (desc != null && block != null && htmlTable().equals(block.getContent())) {
			if (htmlTable().getColCount() == 1 && htmlTable().getRowCount() == 2) {
				if (this.equals(htmlTable().getTDAt(1, 0))) {
					return true;
				}
			}
		}
		return false;
	}

	public int getPercentage() {
		if (htmlTable() != null) {
			return (int) Math.round(htmlTable().getPourcentage(getXLocation(), 1) * 100);
		} else {
			return 25;
		}
	}

	public void setPercentage(int newPercentage) throws InvalidPercentage, NotEnoughRoomOnTheLeft, NotEnoughRoomOnTheRight {
		if (newPercentage > 100 || newPercentage < 1) {
			throw new InvalidPercentage(newPercentage);
		}
		if (newPercentage == getPercentage()) {
			return;
		}
		if (!htmlTable().isPercentageAcceptable(this, newPercentage)) {
			if (getXLocation() + 1 == htmlTable().getColCount()) {
				throw new NotEnoughRoomOnTheLeft();
			} else {
				throw new NotEnoughRoomOnTheRight();
			}
		}
		if (htmlTable() != null) {
			htmlTable().setPercentageForTD((double) newPercentage / 100, this);
		}
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
		if (_sequenceWidget != null) {
			_sequenceWidget.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components
														// cache
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (_sequenceWidget != null) {
			return _sequenceWidget.areComponentInstancesValid();
		} else {
			return true;
		}
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (_sequenceWidget != null) {
			_sequenceWidget.removeInvalidComponentInstances();
		}
	}

	/**
	 * @return
	 */
	public Vector<IWidget> getAllInnerTableWidget() {
		return _sequenceWidget.getAllNonSequenceWidget();
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return _sequenceWidget.getAllButtonInterface();
	}
}
