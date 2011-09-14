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
import java.util.Vector;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.util.TRCSSType;


public interface ITableRow extends IWidget
{

    @Override
	public void setParent(IEObject object);
    
    public IEHTMLTableWidget htmlTable();

    @Override
	public int getIndex();

    @Override
	public void setIndex(int i);

    public Color getBackgroundColor();

    public TRCSSType getTRCssType();

    @Override
	public void delete();

    public IESequenceTR getSequenceTR();
    
    public IEOperator getOperator();

    public int getColCount();

    public int getRowCount();

    public IETRWidget getFirstTR();

    public IESequenceTR findNextRepeatedSequence();

    public Vector<IETDWidget> getAllTD();

    public Vector<IETRWidget> getAllTR();

    public boolean containsTD(IETDWidget widget);
    
	public Vector<IETextFieldWidget> getAllDateTextfields();

	public int getSequenceDepth();
    /**
     * This methods sets an index (equivalent to the yLocation) on each TR of a
     * table. Each time an IETRWidget is found, we set the currentIndex as its
     * index and we increment the currendIndex. SequenceTR will call recursively
     * this method on its children.
     * 
     * @param currentIndex
     */
    public void setTRRowIndex(Incrementer currentIndex);

    /**
     * This method is called at the end of deserialization of a table so that
     * all the TD's located in this TR create and insert all the SpannedTD they
     * require (according to their colspan/rowspan)
     * 
     */
    public void insertSpannedTD();
    
    public void simplifySequenceTree();
    
	public Vector<IESequenceTab> getAllTabContainers();

	//public void setParentOfSingleWidgetComponentInstance(IEHTMLTableWidget htmlTable);
	
}
