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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.logging.FlexoLogger;

public class InsertColBefore extends FlexoAction {
	private static final Logger logger = FlexoLogger.getLogger(InsertColBefore.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("insert_col_before", FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new InsertColBefore(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return object != null
					&& (object instanceof IETDWidget || object instanceof IESequenceWidget && ((IESequenceWidget) object).isInTD());
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, IESequenceWidget.class);
		FlexoModelObject.addActionForClass(actionType, IETDWidget.class);
	}

	InsertColBefore(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getFocusedTD() != null && getFocusedTD().htmlTable() != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Inserting new col " + getFocusedTD().getXLocation());
			}
			getFocusedTD().htmlTable().insertCol(getFocusedTD().getXLocation());
		}
	}

	private IETDWidget getFocusedTD() {
		IEWidget temp = (IEWidget) getFocusedObject();
		while (temp != null) {
			if (temp instanceof IETDWidget) {
				return (IETDWidget) temp;
			}
			temp = (IEWidget) temp.getParent();
		}
		return null;
	}
}
