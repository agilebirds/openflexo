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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.SingleWidgetComponentInstance;
import org.openflexo.foundation.ie.action.DeleteRow;
import org.openflexo.foundation.ie.action.IECopy;
import org.openflexo.foundation.ie.action.IECut;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.action.IEPaste;
import org.openflexo.foundation.ie.action.InsertRowAfter;
import org.openflexo.foundation.ie.action.InsertRowBefore;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.TDRemoved;
import org.openflexo.foundation.ie.dm.WidgetAddedToSequence;
import org.openflexo.foundation.ie.dm.WidgetRemovedFromSequence;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.util.TRCSSType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Represents a row of an HTML table
 * 
 * @author bmangez
 */
public class IETRWidget extends IEWidget implements ITableRow {
	/**
     * 
     */
	public static final String TR_WIDGET = "tr_widget";

	private static final Logger logger = Logger.getLogger(IETRWidget.class.getPackage().getName());

	private TRCSSType _trCssType;

	private IESequenceTD _sequenceTD;

	private int rowIndex = -1;

	public IETRWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, false, builder.getProject());
		initializeDeserialization(builder);
	}

	/**
	 * @param parent
	 */
	public IETRWidget(IEWOComponent woComponent, IESequenceTR parent, boolean createTDs, FlexoProject prj) {
		super(woComponent, parent, prj);
		_sequenceTD = new IESequenceTD(woComponent, this, prj);
		int colCount = -1;
		if (parent != null) {
			colCount = parent.getColCount();
		}
		if (createTDs && colCount > 0) {
			for (int i = 0; i < colCount; i++) {
				IETDWidget newTD = new IETDWidget(woComponent, _sequenceTD, prj);
				newTD.setIndex(i);
				newTD.setXLocation(i);
				_sequenceTD.addToInnerWidgets(newTD);
			}
			_sequenceTD.refreshIndexes();
		}
	}

	@Override
	public IEOperator getOperator() {
		return getSequenceTR().getOperator();
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.remove(IECopy.actionType);
		returned.remove(IECut.actionType);
		returned.remove(IEPaste.actionType);
		returned.remove(IEDelete.actionType);
		returned.add(InsertRowBefore.actionType);
		returned.add(InsertRowAfter.actionType);
		returned.add(DeleteRow.actionType);
		returned.add(InsertRowAfter.actionType);
		return returned;
	}

	@Override
	public IESequenceTR getSequenceTR() {
		if (getParent() instanceof IESequenceTR) {
			return (IESequenceTR) getParent();
		}
		logger.severe("Found a TR not in any sequence !!! but in a " + getParent().getClass());
		return null;
	}

	public IESequenceTR getRootTRSequence() {
		IESequenceTR reply = getSequenceTR();
		while (reply.getParent() instanceof IESequenceTR) {
			reply = (IESequenceTR) reply.getParent();
		}
		return reply;
	}

	@Override
	public IETRWidget getFirstTR() {
		return this;
	}

	@Override
	public String getDefaultInspectorName() {
		return "TR.inspector";
	}

	public IETDWidget getTDatXLocation(int c) {
		if (c < 0) {
			return null;
		}
		Enumeration en = colsEnumeration();
		IETDWidget temp = null;
		while (en.hasMoreElements()) {
			temp = (IETDWidget) en.nextElement();
			if (temp.getXLocation() == c) {
				return temp;
			}
		}
		return null;
	}

	@Override
	public void performOnDeleteOperations() {
		Enumeration<ITableData> en = _sequenceTD.elements();
		Vector<ITableData> temp = new Vector<ITableData>();
		while (en.hasMoreElements()) {
			temp.add(en.nextElement());
		}
		en = temp.elements();
		while (en.hasMoreElements()) {
			(en.nextElement()).makeRealDelete(true);
		}
		_sequenceTD.delete();
		super.performOnDeleteOperations();
	}

	@Override
	public IEHTMLTableWidget htmlTable() {
		if (getParent() instanceof IEHTMLTableWidget) {
			return (IEHTMLTableWidget) getParent();
		} else {
			return ((IESequenceTR) getParent()).htmlTable();
		}
	}

	public Enumeration<ITableData> colsEnumeration() {
		return _sequenceTD.elements();
	}

	public IETDWidget getTD(int col) {
		return _sequenceTD.getTDAtCol(col);
	}

	public ITableData getTDAtIndex(int col) {
		return _sequenceTD.get(col);
	}

	@Override
	public Color getBackgroundColor() {
		return Color.WHITE;
	}

	/**
	 * Returns the number of TD (spanned or not) located in this Table row
	 * 
	 * @return the number of column in this table row.
	 */
	@Override
	public int getColCount() {
		return getAllTD().size();
	}

	@Override
	public TRCSSType getTRCssType() {
		return _trCssType;
	}

	public void setTRCssType(TRCSSType cssType) {
		_trCssType = cssType;
	}

	public void addToCols(ITableData widget) {
		widget.setParent(_sequenceTD);
		_sequenceTD.addToInnerWidgets(widget);
	}

	@Override
	public void setIndex(int y) {
		super.setIndex(y);
		Enumeration en = colsEnumeration();
		IEWidget cur = null;
		IETDWidget td = null;
		while (en.hasMoreElements()) {
			cur = ((IEWidget) en.nextElement());
			if (cur instanceof IETDWidget) {
				((IETDWidget) cur).constraints.gridy = y;
				// here cur's view have to be notified that constraints change
				((IETDWidget) cur).constraintsChange();
			} else {
				Enumeration en2 = ((IESequenceTD) cur).elements();
				while (en2.hasMoreElements()) {
					td = (IETDWidget) en2.nextElement();
					td.constraints.gridy = y;
					// here cur's view have to be notified that constraints
					// change
					td.constraintsChange();
				}
			}
		}
	}

	public void removeFromCols(IETDWidget widget) {
		_sequenceTD.removeFromInnerWidgets(widget);
		notifyRemoval(widget);
		if (_sequenceTD.getWidgetCount() == 0) {
			delete();
		}
	}

	public void removeFromColsNoNotification(IETDWidget widget) {
		_sequenceTD.removeFromInnerWidgets(widget);
	}

	private void notifyRemoval(IEWidget w) {
		setChanged();
		if (w instanceof IETDWidget) {
			notifyObservers(new TDRemoved((IETDWidget) w));
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
		answer.add(_sequenceTD);
		return answer;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Row";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return TR_WIDGET;
	}

	public IESequenceTD getSequenceTD() {
		return _sequenceTD;
	}

	public void setSequenceTD(IESequenceTD _sequencetd) {
		_sequenceTD = _sequencetd;
		_sequenceTD.setParent(this);
	}

	public int getTRCount() {
		return 1;
	}

	@Override
	public IESequenceTR findNextRepeatedSequence() {
		ITableRow candidate = (ITableRow) getSequenceTR().getNext(this);
		if (candidate != null) {
			if (candidate instanceof IESequenceTR) {
				if (((IESequenceTR) candidate).hasOperatorRepetition()) {
					return (IESequenceTR) candidate;
				}
				return candidate.findNextRepeatedSequence();
			} else {
				return candidate.findNextRepeatedSequence();
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean containsTD(IETDWidget widget) {
		if (widget == null) {
			return false;
		}
		if (widget.tr() == this) {
			return true;
		} else {
			return false;
			/*
			 * Enumeration en = colsEnumeration(); while (en.hasMoreElements()) { if
			 * (en.nextElement().equals(widget)) return true; } return false;
			 */
		}
	}

	public boolean isLastCell(IETDWidget widget) {
		return widget.isLastCell();
	}

	@Override
	public boolean isSearchRowForList() {
		HTMLListDescriptor desc = getHTMLListDescriptor();
		return desc != null && desc.isSearchRow(this);
	}

	/**
	 * Overrides setTRRowIndex
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#setTRRowIndex(int)
	 */
	@Override
	public void setTRRowIndex(Incrementer currentIndex) {
		setRowIndex(currentIndex.getValue());
		currentIndex.increment();
	}

	/**
	 * Overrides insertSpannedTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#insertSpannedTD()
	 */
	@Override
	public void insertSpannedTD() {
		getSequenceTD().insertSpannedTD();
	}

	public void insertSpannedTDAtLocation(IESpanTDWidget span, int xLocation) {
	}

	/**
	 * Retrieves recursively all the TD's of this row.
	 * 
	 * @return all the TD's located in this row.
	 */
	@Override
	public Vector<IETDWidget> getAllTD() {
		return getSequenceTD().getAllTD();
	}

	/**
	 * Overrides getRowCount
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return 1;
	}

	/**
	 * Overrides getAllTR
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getAllTR()
	 */
	@Override
	public Vector<IETRWidget> getAllTR() {
		Vector<IETRWidget> v = new Vector<IETRWidget>();
		v.add(this);
		return v;
	}

	/**
	 * Inserts an empty TD at column j (supposing that there are none yet at such position)
	 * 
	 * @param col
	 *            - the column number at which the TD needs to be inserted
	 */
	public IETDWidget insertEmptyTDAtCol(int col) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Inserting empty TD at column " + col);
		}
		IETDWidget previous = null;
		Enumeration<IETDWidget> en = getAllTD().elements();
		while (en.hasMoreElements()) {
			IETDWidget td = en.nextElement();
			if (td.getXLocation() < col) {
				previous = td;
			}
		}
		IESequenceTD std;
		if (previous == null) {
			std = getSequenceTD();
		} else {
			std = previous.getSequenceTD();
		}
		IETDWidget td = new IETDWidget(getWOComponent(), std, getProject());
		td.setXLocation(col);
		int insertionIndex = 0;
		if (previous != null) {
			insertionIndex = std.indexOf(previous) + 1;
		}
		std.insertElementAt(td, insertionIndex);
		reIndexCells();
		return td;
	}

	/**
	 * @param w
	 */
	public void notifyTDRemoved(ITableData w) {
		setChanged();
		notifyObservers(new WidgetRemovedFromSequence((IEObject) w));
	}

	/**
	 * @param w
	 */
	public void notifyTDAdded(ITableData w) {
		setChanged();
		notifyObservers(new WidgetAddedToSequence((IEObject) w, w.getIndex()));
	}

	/**
	 * Sets the xLocation on every cell of this row.
	 */
	public void reIndexCells() {
		_sequenceTD.setXLocation(new Incrementer());
	}

	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
		Enumeration<ITableData> en = _sequenceTD.elements();
		while (en.hasMoreElements()) {
			ITableData element = en.nextElement();
			v.addAll(element.getAllDateTextfields());

		}
		return v;
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration en = _sequenceTD.elements();
		while (en.hasMoreElements()) {
			ITableData top = (ITableData) en.nextElement();
			reply.addAll(top.getAllTabContainers());
		}
		return reply;
	}

	/**
	 * Overrides simplifySequenceTree
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#simplifySequenceTree()
	 */
	@Override
	public void simplifySequenceTree() {
		_sequenceTD.simplifySequenceTree();
	}

	public boolean isReusableRow() {
		return getParent() instanceof IESequenceTR && ((IESequenceTR) getParent()).getParent() instanceof SingleWidgetComponentInstance;
	}

	public ITableRowReusableWidget geTableRowReusableWidget() {
		return (ITableRowReusableWidget) ((SingleWidgetComponentInstance) (((IESequenceTR) getParent()).getParent())).getReusableWidget();
	}

	/**
	 * Returns the absolute position of this row in the table. Negative values mean that this TR has not been initialized properly. This
	 * value is also known as the yLocation
	 * 
	 * @return the absolute position of this row in the table.
	 */
	public int getRowIndex() {
		if (isReusableRow()) {
			return geTableRowReusableWidget().giveMyRowIndex(this);
		}
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		if (isReusableRow()) {
			geTableRowReusableWidget().setMyRowIndex(this, rowIndex);
		}
		int old = this.rowIndex;
		this.rowIndex = rowIndex;
		if (!isDeserializing() && old != this.rowIndex) {
			setChanged();
			notifyObservers(new IEDataModification("rowIndex", old, rowIndex));
		}
	}

	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		super.setWOComponent(woComponent);
		if (_sequenceTD != null) {
			_sequenceTD.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
		}
	}

	/**
	 * @return
	 */
	public Vector<IWidget> getAllInnerTableWidget() {
		return _sequenceTD.getAllInnerTableWidget();
	}

	@Override
	public boolean areComponentInstancesValid() {
		if (_sequenceTD != null) {
			return _sequenceTD.areComponentInstancesValid();
		} else {
			return true;
		}
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (_sequenceTD != null) {
			_sequenceTD.removeInvalidComponentInstances();
		}

	}

	public boolean isAloneInParentSequence() {
		return getSequenceTR().size() == 1;
	}

	@Override
	public int getSequenceDepth() {
		return 0;
	}

	public void setParentOfSingleWidgetComponentInstance(IEHTMLTableWidget htmlTable) {
		// nothing todo here
	}

	@Override
	public Vector<IEHyperlinkWidget> getAllButtonInterface() {
		return _sequenceTD.getAllButtonInterface();
	}

}
