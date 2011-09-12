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
package org.openflexo.foundation.ie.dm.table;

import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.widget.IEWidget;

/**
 * @author bmangez
 * @version $Id: WidgetReplacedInTable.java,v 1.2 2005/04/18 15:19:07 sguerin
 *          Exp $ $Log: WidgetReplacedInTable.java,v $
 *          Exp $ Revision 1.2  2011/09/12 11:47:26  gpolet
 *          Exp $ Converted v2 to v3
 *          Exp $
 *          Exp $ Revision 1.1  2011/05/24 01:12:48  gpolet
 *          Exp $ LOW: First import of OpenFlexo
 *          Exp $
 *          Exp $ Revision 1.1.2.2  2011/05/20 14:23:32  gpolet
 *          Exp $ LOW: Added GPL v2 file header
 *          Exp $
 *          Exp $ Revision 1.1.2.1  2011/05/20 08:26:32  gpolet
 *          Exp $ Package refactor of flexofoundation
 *          Exp $
 *          Exp $ Revision 1.1.2.1  2011/05/19 09:39:54  gpolet
 *          Exp $ refactored package names
 *          Exp $
 *          Exp $ Revision 1.4  2007/09/17 14:38:58  gpolet
 *          Exp $ IMPORTANT: First merge of branch b_1_1_0 from Root_b_1_1_0 until t_first_merge (after t_1_1_0RC10)
 *          Exp $
 *          Exp $ Revision 1.3.18.1  2007/06/01 11:04:56  bmangez
 *          Exp $ LOW/remove illegal chars
 *          Exp $
 *          Exp $ Revision 1.3  2006/02/02 15:28:55  bmangez
 *          Exp $ merge from bdev
 *          Exp $
 *          Exp $ Revision 1.2.2.1  2005/10/03 11:50:42  benoit
 *          Exp $ organize importformat codelogger test
 *          Exp $ Revision 1.2 2005/04/18
 *          15:19:07 sguerin Commit on 18/04/2005, Sylvain GUERIN, version 7.0.6
 *          See committing documentation
 * 
 * Revision 1.1 2005/02/28 14:17:05 benoit first step in cleaning IE: split
 * model and view
 * 
 * 
 * <B>Class Description</B>
 */
public class WidgetReplacedInTable extends IEDataModification
{

    private int _row;

    private int _col;

    public WidgetReplacedInTable(IEWidget oldWidget, IEWidget widget, int row, int col)
    {
        super(oldWidget, widget);
        _row = row;
        _col = col;
    }

    public int getRow()
    {
        return _row;
    }

    public int getCol()
    {
        return _col;
    }
}
