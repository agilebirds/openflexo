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
package org.openflexo.dg.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.action.GCAction;


public abstract class DGAction<A extends DGAction<A,T1>,T1 extends CGObject> extends GCAction<A,T1>
{

    private static final Logger logger = Logger.getLogger(DGAction.class.getPackage().getName());

    protected DGAction (FlexoActionType<A,T1,CGObject> actionType, T1 focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor)
    {
        super(actionType,focusedObject,globalSelection,editor);
    }
}
