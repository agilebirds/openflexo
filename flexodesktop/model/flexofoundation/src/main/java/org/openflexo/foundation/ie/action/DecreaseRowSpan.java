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
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableData;
import org.openflexo.foundation.ie.widget.InvalidOperation;

public class DecreaseRowSpan extends FlexoUndoableAction {

	private IETDWidget selectedTD;
	public static FlexoActionType actionType = new FlexoActionType("decrease_row_span", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new DecreaseRowSpan(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return object != null
					&& (object instanceof IETDWidget || object instanceof IESequenceWidget && ((IESequenceWidget) object).isInTD())
					&& getFocusedTD((IEWidget) object).canDecreaseRowSpan();
		}

	};

	DecreaseRowSpan(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidOperation {
		if (selectedTD != null) {
			selectedTD.decreaseRowSpan();
		}

	}

	@Override
	protected void undoAction(Object context) throws InvalidOperation {
		if (selectedTD != null) {
			selectedTD.increaseRowSpan();
		}
	}

	@Override
	protected void redoAction(Object context) throws InvalidOperation {
		if (selectedTD != null) {
			selectedTD.decreaseRowSpan();
		}
	}

	public IETDWidget getSelectedTD() {
		return selectedTD;
	}

	public void setSelectedTD(ITableData td) {
		if (td instanceof IETDWidget) {
			this.selectedTD = (IETDWidget) td;
		}
	}

	static IETDWidget getFocusedTD(IEWidget w) {
		IEWidget temp = w;
		while (temp != null) {
			if (temp instanceof IETDWidget) {
				return (IETDWidget) temp;
			}
			temp = (IEWidget) temp.getParent();
		}
		return null;
	}
}
