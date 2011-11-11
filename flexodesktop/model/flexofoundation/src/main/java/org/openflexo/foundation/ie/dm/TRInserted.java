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

import org.openflexo.foundation.ie.widget.IETRWidget;

/**
 * @author bmangez
 * @version $Id: TRInserted.java,v 1.2 2011/09/12 11:47:11 gpolet Exp $ $Log: TRInserted.java,v $ Revision 1.2 2005/04/18 15:19:07 sguerin
 *          Commit on 18/04/2005, Sylvain GUERIN, version 7.0.6 See committing documentation
 * 
 *          Revision 1.1 2005/03/29 08:10:50 benoit *** empty log message ***
 * 
 * 
 *          <B>Class Description</B>
 */
public class TRInserted extends IEDataModification {

	private IETRWidget _tr;

	public TRInserted(IETRWidget insertedTR) {
		super(insertedTR, null);
		_tr = insertedTR;
	}

	public IETRWidget getTR() {
		return _tr;
	}
}
