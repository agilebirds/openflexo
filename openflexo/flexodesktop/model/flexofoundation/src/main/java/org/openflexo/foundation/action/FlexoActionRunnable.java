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
package org.openflexo.foundation.action;

import java.awt.event.ActionEvent;

import org.openflexo.foundation.FlexoException;


public abstract class FlexoActionRunnable<A extends FlexoAction> implements Runnable 
{
    /*private FlexoAction _action;

    public FlexoAction getAction()
    {
        return _action;
    }

    public void setAction(FlexoAction action) 
    {
        _action = action;
    }*/

    @Override
	public void run()
    {
        try {
			run(null, null);
		} catch (FlexoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public abstract boolean run (ActionEvent event, A action) throws FlexoException;
}
