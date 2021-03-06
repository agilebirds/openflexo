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
package org.openflexo.foundation;

import java.util.EventObject;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.action.UndoManager;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler;
import org.openflexo.foundation.utils.FlexoProgressFactory;

public interface FlexoEditor {

	public static interface FlexoEditorFactory {
		public FlexoEditor makeFlexoEditor(FlexoProject project);
	}

	public FlexoProject getProject();

	public FlexoProgressFactory getFlexoProgressFactory();

	public ResourceUpdateHandler getResourceUpdateHandler();

	public boolean isInteractive();

	public boolean performResourceScanning();

	public void focusOn(FlexoModelObject object);

	public UndoManager getUndoManager();

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e);

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e);

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoActionType(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, EventObject e);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performAction(A action,
			EventObject e);

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performUndoAction(
			A action, EventObject e);

	public <A extends FlexoUndoableAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performRedoAction(
			A action, EventObject e);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
			FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
			FlexoActionType<A, T1, T2> action);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
			FlexoActionType<A, T1, T2> action);

	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> KeyStroke getKeyStrokeFor(
			FlexoActionType<A, T1, T2> action);

	public void notifyObjectCreated(FlexoModelObject object);

	public void notifyObjectDeleted(FlexoModelObject object);

	public void notifyObjectChanged(FlexoModelObject object);

}
