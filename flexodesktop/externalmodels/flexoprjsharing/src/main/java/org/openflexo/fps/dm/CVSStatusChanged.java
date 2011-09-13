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
package org.openflexo.fps.dm;

import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSStatus;


public class CVSStatusChanged extends FPSDataModification
{
	private CVSStatus _oldStatus;
	private CVSStatus _newStatus;
	
    public CVSStatusChanged(CVSFile file, CVSStatus oldStatus, CVSStatus newStatus)
    {
        super(null,file);
        _oldStatus = oldStatus;
        _newStatus = newStatus;
    }

	public CVSStatus getNewStatus() 
	{
		return _newStatus;
	}

	public CVSStatus getOldStatus()
	{
		return _oldStatus;
	}

}
