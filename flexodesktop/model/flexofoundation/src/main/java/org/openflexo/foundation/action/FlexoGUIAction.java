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

/**
 * 
 * Abstract representation of an action which implies no modification of the Flexo model. This kind of action is generally used by graphical
 * interfaces.
 * 
 * @author sguerin
 */
public abstract class FlexoGUIAction<A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> extends
		FlexoAction<A, T1, T2> {

	protected FlexoGUIAction(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected final void doAction(Object context) {
	}

}
