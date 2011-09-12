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
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.ReusableComponentInstance;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.util.TRCSSType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

public class ITableRowReusableWidget extends IEReusableWidget<ReusableComponentDefinition,ReusableComponentInstance> implements ITableRow {

	private static final Logger logger = FlexoLogger.getLogger(ITableRowReusableWidget.class.getPackage().getName());

	public IESequenceTRConstraints constraints;

	private int rowIndex;

	public ITableRowReusableWidget(FlexoComponentBuilder builder) {
		super(builder);
		initializeDeserialization(builder);
	}

	public ITableRowReusableWidget(IEWOComponent woComponent, ReusableComponentDefinition def, IEObject parent, FlexoProject prj) {
		super(woComponent, def, parent, prj);
		constraints = new IESequenceTRConstraints();
		_rowIndexes = new Hashtable<ITableRow, Integer>();
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getRootObject().setParent(getReusableComponentInstance());
	}
	
	@Override
	protected ReusableComponentInstance createComponentInstance(ReusableComponentDefinition componentDefinition, IEWOComponent woComponent) {
		return new ReusableComponentInstance(componentDefinition,woComponent);
	}

	private ITableRow tableRow() {
		return (ITableRow) getRootObject();
	}

	@Override
	public Vector<IETextFieldWidget> getAllDateTextfields() {
		return tableRow().getAllDateTextfields();
	}

	@Override
	public String getDefaultInspectorName() {
		return null;
	}

	@Override
	public String getClassNameKey() {
		return "itablerow_reusable";
	}

	@Override
	public String getFullyQualifiedName() {
		return "ReusableRow";
	}

	@Override
	public boolean containsTD(IETDWidget widget) {
		return tableRow().containsTD(widget);
	}

	@Override
	public IESequenceTR findNextRepeatedSequence() {
		return getSequenceTR().findNextRepeatedSequence();
	}

	@Override
	public Color getBackgroundColor() {
		return getSequenceTR().getBackgroundColor();
	}

	@Override
	public int getColCount() {
		return getSequenceTR().getColCount();
	}

	@Override
	public IETRWidget getFirstTR() {
		return getSequenceTR().getFirstTR();
	}

	@Override
	public IESequenceTR getSequenceTR() {
		return (IESequenceTR) tableRow();
	}

	public int getTRCount() {
		return getSequenceTR().getRowCount();
	}

	@Override
	public TRCSSType getTRCssType() {
		return null;
	}

	/**
	 * Overrides insertSpannedTD
	 * @see org.openflexo.foundation.ie.widget.ITableRow#insertSpannedTD()
	 */
	@Override
	public void insertSpannedTD() {

	}

	/**
	 * Overrides setTRRowIndex
	 * @see org.openflexo.foundation.ie.widget.ITableRow#setTRRowIndex(org.openflexo.foundation.ie.widget.IEHTMLTableWidget.Incrementer)
	 */
	@Override
	public void setTRRowIndex(Incrementer currentIndex) {
		//Enumeration<ITableRow> en = elements();
		rowIndex = currentIndex.getValue();
		getSequenceTR().setTRRowIndex(currentIndex);
		//        while (en.hasMoreElements()) {
		//            ITableRow row = (ITableRow) en.nextElement();
		//            row.setTRRowIndex(currentIndex);
		//        }
	}

	/**
	 * Overrides getAllTD
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getAllTD()
	 */
	@Override
	public Vector<IETDWidget> getAllTD() {
		return tableRow().getAllTD();
	}

	@Override
	public Vector<IESequenceTab> getAllTabContainers() {
		Vector<IESequenceTab> reply = new Vector<IESequenceTab>();
		if (getRootObject() == null)
			return reply;
		if (getRootObject() instanceof ITableRow)
			reply.addAll(((ITableRow) getRootObject()).getAllTabContainers());
		return reply;
	}

	/**
	 * Overrides getRowCount
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return tableRow().getRowCount();
	}

	/**
	 * Overrides getAllTR
	 * @see org.openflexo.foundation.ie.widget.ITableRow#getAllTR()
	 */
	@Override
	public Vector<IETRWidget> getAllTR() {
		return tableRow().getAllTR();
	}

	/**
	 * Overrides addTDatGridXInVector
	 * @see org.openflexo.foundation.ie.widget.ITableRow#addTDatGridXInVector(int)
	 */
	public Vector<IETDWidget> addTDatGridXInVector(int gridX) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overrides simplifySequenceTree
	 * @see org.openflexo.foundation.ie.widget.ITableRow#simplifySequenceTree()
	 */
	@Override
	public void simplifySequenceTree() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areComponentInstancesValid() {
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Implement me!!! please");
		return true;
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (logger.isLoggable(Level.WARNING))
			logger.warning("Implement me!!! please");
	}

	@Override
	public IEOperator getOperator() {
		return null;
	}

	@Override
	public IEHTMLTableWidget htmlTable() {
		return getParentTable();
	}

	/**
	 * 
	 * @param row
	 * @return
	 */
	//    public IETRWidget getTRAtRow(int row)
	//    {
	//    	Enumeration<ITableRow> en = _rowIndexes.keys();
	//    	while (en.hasMoreElements()) {
	//    		ITableRow element = (ITableRow) en.nextElement();
	//			if(_rowIndexes.get(element).intValue()==row)
	//				return (IETRWidget)element;
	//		}
	//    	return null;
	//    }
	//    
	//    public IETRWidget getTRAtRowRelative(int row)
	//    {
	//    	return getSequenceTR().getTRAtRow(row+rowIndex);
	//    }
	//    
	public Enumeration<ITableRow> elements() {
		if (getSequenceTR() != null)
			return getSequenceTR().elements();
		Vector<ITableRow> v = new Vector<ITableRow>();
		v.add((ITableRow) getRootObject());
		return v.elements();
	}

	@Override
	public int getSequenceDepth() {
		return tableRow().getSequenceDepth();
	}

	/**
	 * Returns the absolute position of this row in the table. Negative values
	 * mean that this TR has not been initialized properly. This value is also
	 * known as the yLocation
	 * 
	 * @return the absolute position of this row in the table.
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		int old = this.rowIndex;
		this.rowIndex = rowIndex;
		if (!isDeserializing() && old != rowIndex) {
			setChanged();
			notifyObservers(new IEDataModification("rowIndex", old, rowIndex));
		}
	}

	private Hashtable<ITableRow, Integer> _rowIndexes;

	public int giveMyRowIndex(IETRWidget widget) {
		return _rowIndexes.get(widget);
	}

	public void setMyRowIndex(IETRWidget widget, int rowIndex) {
		_rowIndexes.put(widget, rowIndex);
	}

}
