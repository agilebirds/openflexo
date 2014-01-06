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
package org.openflexo.foundation.wkf.dm;

/**
 * @author bmangez
 * @version $Id: RoleNameChange.java,v 1.2 2011/09/12 11:46:54 gpolet Exp $ $Log: RoleNameChange.java,v $ Revision 1.2 2011/09/12 11:46:54
 *          gpolet Converted v2 to v3
 * 
 *          Revision 1.1 2011/05/24 01:12:21 gpolet LOW: First import of OpenFlexo
 * 
 *          Revision 1.1.2.2 2011/05/20 14:23:31 gpolet LOW: Added GPL v2 file header
 * 
 *          Revision 1.1.2.1 2011/05/20 08:26:29 gpolet Package refactor of flexofoundation
 * 
 *          Revision 1.1.2.1 2011/05/19 09:39:47 gpolet refactored package names
 * 
 *          Revision 1.3 2009/05/27 09:39:20 gpolet MEDIUM: Merge of branch b_sprint_1_3_3 with HEAD from Root_b_sprint_1_3_3 until tag
 *          FLEXO_1_3_3RC3
 * 
 *          Revision 1.2.44.1 2009/05/13 11:15:18 gpolet LOW: Fixed datamodification (property was not specified)
 * 
 *          Revision 1.2 2006/02/02 15:30:31 bmangez merge from bdev
 * 
 *          Revision 1.1.2.2 2005/10/03 11:50:43 benoit organize import format code logger test Revision 1.1.2.1 2005/06/28 12:53:53 benoit
 *          ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
public class RoleNameChange extends WKFDataModification {

	public RoleNameChange() {
		super("name", null, null);
	}

}
