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
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.ExportWidgetToPalette;
import org.openflexo.foundation.ie.dm.ActivateDisplayRefresh;
import org.openflexo.foundation.ie.dm.HoldDisplayRefresh;
import org.openflexo.foundation.ie.dm.TRInserted;
import org.openflexo.foundation.ie.util.TRCSSType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

public class IESequenceTR extends IESequence<ITableRow> implements ITableRow {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IESequenceTR.class.getPackage().getName());

	public IESequenceTRConstraints constraints;

	public IESequenceTR(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IESequenceTR(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		constraints = new IESequenceTRConstraints();
	}

	@Override
	public IESequenceTR getSequenceTR() {
		if (getParent() instanceof IESequenceTR)
			return (IESequenceTR) getParent();
		return this;
	}

	/**
	 * Overrides getSpecificActionListForThatClass
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEWidget#getSpecificActionListForThatClass()
	 */
	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		returned.remove(ExportWidgetToPalette.actionType);
		return returned;
	}

	@Override
	public void performOnDeleteOperations() {
		if (getParent() instanceof IEHTMLTableWidget) {
			((IEHTMLTableWidget) getParent()).delete();
		}
		super.performOnDeleteOperations();
	}

	@Override
	public IETRWidget getFirstTR() {
		if (size() == 0)
			return null;
		return (get(0)).getFirstTR();
	}

	@Override
	public void setIndex(int i) {
		if (i != getIndex()) {
			super.setIndex(i);
		}
	}

	@Override
	public Color getBackgroundColor() {
		if (getOperator() == null)
			return Color.WHITE;
		else
			return blue1;
	}

	@Override
	public TRCSSType getTRCssType() {
		return null;
	}

	@Override
	public void insertElementAt(ITableRow o, int i) {
		super.insertElementAt(o, i);
		setChanged();
		if (o instanceof IETRWidget)
			notifyObservers(new TRInserted((IETRWidget) o));
		if (!isDeserializing() && !isCreatedByCloning() && getParent() instanceof IEHTMLTableWidget) {
			((IEHTMLTableWidget) getParent()).notifyWidgetInsertion(o);
		}
	}

	/**
	 * Returns the number of column in this sequence. It will locate the first TR in this sequence or subsequence and will return the number
	 * of TD's that are located inside of it.
	 * 
	 * Overrides getColCount
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getColCount()
	 */
	@Override
	public int getColCount() {
		// We take the first row available and asks for how many columns are
		// inside of it;
		if (size() > 0) {
			return (get(0)).getColCount();
		}
		return 0;
	}

	private static final Color blue1 = new Color(0, 0, 64);

	@Override
	public boolean isSubsequence() {
		return getParent() instanceof IESequenceTR;
	}

	@Override
	public IEWidget getDraggedModel() {
		if (isInHTMLTable())
			return (IEHTMLTableWidget) getParent();
		return this;
	}

	public boolean isInHTMLTable() {
		return getParent() instanceof IEHTMLTableWidget;
	}

	@Override
	public IEHTMLTableWidget htmlTable() {
		if (isInHTMLTable())
			return (IEHTMLTableWidget) getParent();

		if (!(getParent() instanceof IESequenceTR))
			return null;
		return ((IESequenceTR) getParent()).htmlTable();
	}

	@Override
	public IESequenceTR findNextRepeatedSequence() {
		ITableRow candidate = (ITableRow) getSequenceTR().getNext(this);
		if (candidate != null) {
			if (candidate instanceof IESequenceTR) {
				if (((IESequenceTR) candidate).hasOperatorRepetition())
					return (IESequenceTR) candidate;
				return candidate.findNextRepeatedSequence();
			} else {
				return candidate.findNextRepeatedSequence();
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns wheter the specified td is located in this sequence (or one of its subsequence), or not.
	 * 
	 * Overrides containsTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#containsTD(org.openflexo.foundation.ie.widget.IETDWidget)
	 */
	@Override
	public boolean containsTD(IETDWidget widget) {
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			if (((ITableRow) en.nextElement()).containsTD(widget))
				return true;
		}
		return false;
	}

	/**
	 * This method calls setTRRowIndex on each of its children. (Therefore, if the child is a IESequenceTR, the call will be a recusrive
	 * call, if the child is a IETRWidget, the currentIndex parameter will be set as its yLocation and will be incremented.
	 * 
	 * @param currentIndex
	 */
	@Override
	public void setTRRowIndex(Incrementer currentIndex) {
		if (getParent() instanceof IESequenceTR) {
			Enumeration<ITableRow> en = elements();
			while (en.hasMoreElements()) {
				ITableRow row = en.nextElement();
				row.setTRRowIndex(currentIndex);
			}
		} else {
			Incrementer subIncrementer = new Incrementer();
			Enumeration<ITableRow> en = elements();
			while (en.hasMoreElements()) {
				ITableRow row = en.nextElement();
				row.setTRRowIndex(subIncrementer);
			}
			currentIndex.increment(subIncrementer.getValue());
		}
	}

	/**
	 * Overrides insertSpannedTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#insertSpannedTD()
	 */
	@Override
	public void insertSpannedTD() {
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow row = en.nextElement();
			row.insertSpannedTD();
		}
	}

	public IESequenceTR getFirstRepeatedSequence() {
		if (hasOperatorRepetition() && isSubsequence())
			return this;
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = en.nextElement();
			if (tr instanceof IESequenceTR) {
				IESequenceTR t = ((IESequenceTR) tr).getFirstRepeatedSequence();
				if (t != null)
					return t;
			}
		}
		return null;
	}

	/**
	 * Overrides removeFromInnerWidgets
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#removeFromInnerWidgets(org.openflexo.foundation.ie.widget.IWidget)
	 */
	@Override
	public void removeFromInnerWidgets(ITableRow w) {
		super.removeFromInnerWidgets(w);
		if (getParent() instanceof IEHTMLTableWidget) {
			((IEHTMLTableWidget) getParent()).notifyWidgetRemoval(w);
		}
	}

	/**
	 * 
	 * @param row
	 * @return
	 */
	public IETRWidget getTRAtRow(int row) {
		IETRWidget retval = null;
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = en.nextElement();
			if (tr instanceof IESequenceTR) {
				retval = ((IESequenceTR) tr).getTRAtRow(row);
				if (retval != null)
					return retval;
			} else if (tr instanceof ITableRowReusableWidget) {
				retval = ((IESequenceTR) ((ITableRowReusableWidget) tr).getRootObject()).getTRAtRow(row
						- ((ITableRowReusableWidget) tr).getRowIndex());
				if (retval != null)
					return retval;
			} else {
				if (((IETRWidget) tr).getRowIndex() == row)
					return (IETRWidget) tr;
			}
		}
		return null;
	}

	/**
	 * Overrides getAllTD
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getAllTD()
	 */
	@Override
	public Vector<IETDWidget> getAllTD() {
		Vector<IETDWidget> v = new Vector<IETDWidget>();
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = (ITableRow) en.nextElement();
			v.addAll(tr.getAllTD());
		}
		return v;
	}

	/**
	 * Returns the number of rows located within this sequence and all its subsequence.
	 * 
	 * @return the number of rows located in this sequence.
	 */
	@Override
	public int getRowCount() {
		int count = 0;
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = en.nextElement();
			count += tr.getRowCount();
		}
		return count;
	}

	/**
	 * Returns all TR located in this sequence and all its subsequence. Overrides getAllTR
	 * 
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getAllTR()
	 */
	@Override
	public Vector<IETRWidget> getAllTR() {
		Vector<IETRWidget> v = new Vector<IETRWidget>();
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = en.nextElement();
			v.addAll(tr.getAllTR());
		}
		return v;
	}

	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow element = en.nextElement();
			v.addAll(element.getAllDateTextfields());

		}
		return v;
	}

	/**
	 * Overrides simplifySequenceTree
	 * 
	 * @see org.openflexo.foundation.ie.widget.IESequence#simplifySequenceTree()
	 */
	@Override
	public void simplifySequenceTree() {
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow tr = en.nextElement();
			if (tr instanceof IETRWidget) {
				((IETRWidget) tr).simplifySequenceTree();
			}
		}
		super.simplifySequenceTree();
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		Enumeration en = elements();
		while (en.hasMoreElements()) {
			ITableRow top = (ITableRow) en.nextElement();
			reply.addAll(top.getAllTabContainers());
		}
		return reply;
	}

	/**
	 * @return
	 */
	public Vector<IWidget> getAllInnerTableWidget() {
		Vector<IWidget> v = new Vector<IWidget>();
		Enumeration en = getAllNonSequenceWidget().elements();
		while (en.hasMoreElements()) {
			IETRWidget tr = (IETRWidget) en.nextElement();
			v.addAll(tr.getAllInnerTableWidget());
		}
		return v;
	}

	public void holdDisplayRefresh() {
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow r = en.nextElement();
			if (r instanceof IESequenceTR)
				((IESequenceTR) r).holdDisplayRefresh();
		}
		setChanged();
		notifyObservers(new HoldDisplayRefresh());
		disableObserving();
	}

	public void activateDisplayRefresh() {
		Enumeration<ITableRow> en = elements();
		while (en.hasMoreElements()) {
			ITableRow r = en.nextElement();
			if (r instanceof IESequenceTR)
				((IESequenceTR) r).activateDisplayRefresh();
		}
		enableObserving();
		setChanged();
		notifyObservers(new ActivateDisplayRefresh());
	}

}
