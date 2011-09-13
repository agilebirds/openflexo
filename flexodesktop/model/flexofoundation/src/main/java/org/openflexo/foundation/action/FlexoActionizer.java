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

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;


public class FlexoActionizer<A extends FlexoAction<A,T1,T2>,T1 extends FlexoModelObject, T2 extends FlexoModelObject>
{
	private FlexoActionType<A,T1,T2> _actionType;
	private FlexoEditor _editor;
	
	public FlexoActionizer(FlexoActionType<A,T1,T2> actionType,FlexoEditor editor) {
		_actionType = actionType;
		_editor = editor;
	}
	
	public void run (T1 focusedObject, Vector<T2> globalSelection)
	{
		A action = _actionType.makeNewAction(focusedObject, globalSelection, _editor);
		action.doAction();
	}
}