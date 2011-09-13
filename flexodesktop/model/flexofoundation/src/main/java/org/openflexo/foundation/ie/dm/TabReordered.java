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
package org.openflexo.foundation.ie.dm;

import org.openflexo.foundation.ie.widget.IESequenceTab;

/**
 * @author bmangez
 * @version $Id: TabReordered.java,v 1.2 2011/09/12 11:47:11 gpolet Exp $
 *          $Log: TabReordered.java,v $
 *          Revision 1.2  2011/09/12 11:47:11  gpolet
 *          Converted v2 to v3
 *
 *          Revision 1.1  2011/05/24 01:12:36  gpolet
 *          LOW: First import of OpenFlexo
 *
 *          Revision 1.1.2.2  2011/05/20 14:23:30  gpolet
 *          LOW: Added GPL v2 file header
 *
 *          Revision 1.1.2.1  2011/05/20 08:26:28  gpolet
 *          Package refactor of flexofoundation
 *
 *          Revision 1.1.2.1  2011/05/19 09:39:48  gpolet
 *          refactored package names
 *
 *          Revision 1.3  2007/09/17 14:38:54  gpolet
 *          IMPORTANT: First merge of branch b_1_1_0 from Root_b_1_1_0 until t_first_merge (after t_1_1_0RC10)
 *
 *          Revision 1.2.10.1  2007/05/31 11:57:21  bmangez
 *          LOW/organize import
 *
 *          Revision 1.2  2006/09/06 12:21:29  gpolet
 *          LOW: Changed model to replace TabContainer by IESequenceTab to allow to have conditional for different tabs.
 *
 *          Revision 1.1  2006/08/31 16:14:22  gpolet
 *          LOW: new DataModification
 *
 *          Revision 1.4  2006/03/01 13:53:08  bmangez
 *          IMPORTANT/MEGA IMPORTANT/Merge with b_RC_0_8
 *
 *          Revision 1.3.2.1  2006/02/24 12:49:25  bmangez
 *          IMPORTANT/refactoring : Thumbnail renamed into Tab IN CODE
 *
 *          Revision 1.3  2006/02/02 15:28:53  bmangez
 *          merge from bdev
 *
 *          Revision 1.2.2.1  2005/10/03 11:50:43  benoit
 *          organize importformat codelogger test
 * Revision 1.2 2005/04/18 15:19:07
 *          sguerin Commit on 18/04/2005, Sylvain GUERIN, version 7.0.6 See
 *          committing documentation
 * 
 * Revision 1.1 2005/03/30 07:47:43 benoit Thumbnails ordering
 * 
 * 
 * <B>Class Description</B>
 */
public class TabReordered extends IEDataModification
{

    public TabReordered(IESequenceTab tabContainer)
    {
        super(tabContainer, tabContainer);
    }
}
