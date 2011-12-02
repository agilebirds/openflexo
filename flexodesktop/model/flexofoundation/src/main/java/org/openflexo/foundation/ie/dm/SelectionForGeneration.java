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

/**
 * @author bmangez
 * @version $Id: SelectionForGeneration.java,v 1.1.4.2 2005/08/04 16:20:19 sguerin Exp $ $Log: SelectionForGeneration.java,v $ sguerin Exp $
 *          Revision 1.1.4.3 2005/10/03 11:50:43 benoit sguerin Exp $ organize import format code logger test sguerin Exp $ Revision 1.1.4.2
 *          2005/08/04 16:20:19 sguerin Commit on 04/08/2005, Sylvain GUERIN, version 7.1.6.alpha Temporary commit, see next commit
 * 
 *          Revision 1.1.4.1 2005/07/05 07:26:47 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.1 2005/07/05 06:43:12 benoit *** empty log message ***
 * 
 * 
 *          <B>Class Description</B>
 */
public class SelectionForGeneration extends IEDataModification {

	/**
     * 
     */
	public SelectionForGeneration(boolean b) {
		super(null, new Boolean(b));
	}

	public boolean getSelectionValue() {
		return ((Boolean) newValue()).booleanValue();
	}

}
