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
package org.openflexo.fib.view.widget.table;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBLabelColumn;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class LabelColumn extends StringColumn
{

    public LabelColumn(FIBLabelColumn columnModel, FIBTableModel tableModel, FIBController controller)
    {
    	super(columnModel,tableModel,controller);
    }

     @Override
	public String toString()
    {
        return "LabelColumn " + "@" + Integer.toHexString(hashCode());
    }

 
}
