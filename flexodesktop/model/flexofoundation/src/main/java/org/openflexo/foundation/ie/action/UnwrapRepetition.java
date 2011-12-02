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
package org.openflexo.foundation.ie.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IEWidget;

public class UnwrapRepetition extends FlexoAction {

	public RepetitionOperator repetition;

	public static FlexoActionType actionType = new FlexoActionType("unwrap repetition content", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new UnwrapRepetition(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return (object instanceof IEWidget && ((IEWidget) object).getParent() instanceof IESequence && ((IESequence) ((IEWidget) object)
					.getParent()).isRepetition()) || (object instanceof IESequence && ((IESequence) object).isRepetition());
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

	};

	UnwrapRepetition(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getFocusedObject() instanceof IESequence && ((IESequence) getFocusedObject()).isRepetition()) {
			((IESequence) getFocusedObject()).getOperator().delete();
		} else if (getFocusedObject() instanceof IEWidget) {
			((IESequence) ((IEWidget) getFocusedObject()).getParent()).getOperator().delete();
		}
	}

}
