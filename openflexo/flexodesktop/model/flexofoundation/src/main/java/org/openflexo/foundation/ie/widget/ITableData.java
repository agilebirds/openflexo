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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.ie.IEObject;


public interface ITableData extends IWidget
{
    public int getColSpan();

    public IEHTMLTableConstraints getConstraints();

    /**
     * Search and return the parent TRWidget
     * 
     * @return the unique parent IETRWidget
     */
    public IETRWidget tr();

    public void deleteCol();
    
    public void deleteRow();

    public Enumeration<ITableData> elements();

    public double getPourcentage();

    @Override
	public int getIndex();
    
	public Vector<IETextFieldWidget> getAllDateTextfields();

    public Vector<IETDWidget> getAllTD();
    
    /**
     * This method parses a sequenceTD recursively so that all the contained
     * IETDWidget insert SpannedTD in the model for each of their
     * colspan/rowspan. This means that at the end of this operation, all cells
     * of the table are present but some of them are SpannedTD which means that
     * they are part of another cell
     * 
     */
    public void insertSpannedTD();

    @Override
	public IEObject getParent();

    @Override
	public void setParent(IEObject parent);

    /**
     * @param b
     */
    public void makeRealDelete(boolean b);

    /**
     * @param incrementer
     */
    public void setXLocation(Incrementer incrementer);
    
    public IETDWidget getFirstTD();

	public Vector<IESequenceTab> getAllTabContainers();

}
