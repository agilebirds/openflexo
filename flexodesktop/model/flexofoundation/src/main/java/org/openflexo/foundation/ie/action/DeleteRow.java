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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;

public class DeleteRow extends FlexoAction {

	public static FlexoActionType actionType = new FlexoActionType("delete_row", FlexoActionType.defaultGroup,
			FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new DeleteRow(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			Vector<FlexoModelObject> v = getGlobalSelectionAndFocusedObject(object, globalSelection);
			Enumeration<FlexoModelObject> en = v.elements();
			while (en.hasMoreElements()) {
				FlexoModelObject o = en.nextElement();
				if (!(o instanceof IETDWidget) && !(o instanceof IETRWidget)
						&& !((o instanceof IESequenceWidget) && ((IESequenceWidget) o).isInTD())) {
					return false;
				}
			}
			return true;
		}

	};

	DeleteRow(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		Enumeration<FlexoModelObject> en = getGlobalSelectionAndFocusedObject().elements();
		while (en.hasMoreElements()) {
			FlexoModelObject o = en.nextElement();
			if (o instanceof IETRWidget) {
				IETRWidget tr = (IETRWidget) o;
				if (!tr.isDeleted() && (tr.getAllTD().size() > 0) && !tr.getAllTD().firstElement().isDeleted()) {
					tr.getAllTD().firstElement().deleteRow();
				}
			} else if (o instanceof IETDWidget) {
				IETDWidget td = (IETDWidget) o;
				if (!td.isDeleted()) {
					td.deleteRow();
				}
			} else if (o instanceof IESequenceWidget) {
				IESequenceWidget seq = (IESequenceWidget) o;
				IETDWidget td = seq.td();
				if ((td != null) && !td.isDeleted()) {
					td.deleteRow();
				}
			}
		}
	}

}
