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

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoInitializer;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgressFactory;

public interface FlexoEditor {

	public FlexoProject getProject();

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void executeAction(A action)
			throws FlexoException;

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBePerformed(
			FlexoAction<A, T1, T2> action);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenPerformed(
			FlexoAction<A, T1, T2> action, boolean success);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeUndone(
			FlexoAction<A, T1, T2> action);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenUndone(
			FlexoAction<A, T1, T2> action, boolean success);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionWillBeRedone(
			FlexoAction<A, T1, T2> action);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void actionHasBeenRedone(
			FlexoAction<A, T1, T2> action, boolean success);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionVisible(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> boolean isActionEnabled(
			FlexoActionType<A, T1, T2> actionType);

	public void notifyObjectCreated(FlexoModelObject object);

	public void notifyObjectDeleted(FlexoModelObject object);

	public void notifyObjectChanged(FlexoModelObject object);

	public boolean performResourceScanning();

	public FlexoProgressFactory getFlexoProgressFactory();

	public static interface FlexoEditorFactory {
		public FlexoEditor makeFlexoEditor(FlexoProject project);
	}

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionFinalizer<? super A> getFinalizerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionInitializer<? super A> getInitializerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoFinalizer<? super A> getUndoFinalizerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionUndoInitializer<? super A> getUndoInitializerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoFinalizer<? super A> getRedoFinalizerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionRedoInitializer<? super A> getRedoInitializerFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionEnableCondition<? super A, T1, T2> getEnableConditionFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoActionVisibleCondition<? super A, T1, T2> getVisibleConditionFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> FlexoExceptionHandler<? super A> getExceptionHandlerFor(
			FlexoActionType<A, T1, T2> actionType);

	public void focusOn(FlexoModelObject object);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getEnabledIconFor(
			FlexoActionType<A, T1, T2> actionType);

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> Icon getDisabledIconFor(
			FlexoActionType<A, T1, T2> actionType);

}
