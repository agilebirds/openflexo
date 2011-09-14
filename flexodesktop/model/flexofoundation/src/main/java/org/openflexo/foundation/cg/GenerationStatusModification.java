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
package org.openflexo.foundation.cg;

import org.openflexo.foundation.cg.dm.CGDataModification;
import org.openflexo.foundation.rm.cg.GenerationStatus;

/**
 * @author gpolet
 *
 */
public class GenerationStatusModification extends CGDataModification
{
    
    private GenerationStatus oldStatus;
    
    private GenerationStatus newStatus;

    /**
     * @param oldStatus
     * @param newStatus
     */
    public GenerationStatusModification(GenerationStatus oldStatus, GenerationStatus newStatus)
    {
        super(oldStatus, newStatus);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public GenerationStatus getNewStatus()
    {
        return newStatus;
    }

    public GenerationStatus getOldStatus()
    {
        return oldStatus;
    }

}
