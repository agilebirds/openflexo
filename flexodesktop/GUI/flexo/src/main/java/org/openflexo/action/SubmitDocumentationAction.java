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
package org.openflexo.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.inspector.InspectableObject;

public class SubmitDocumentationAction extends FlexoGUIAction<SubmitDocumentationAction, FlexoObject, FlexoObject> {

	public static class SubmitDocumentationActionType extends FlexoActionType<SubmitDocumentationAction, FlexoObject, FlexoObject> {
		protected SubmitDocumentationActionType() {
			super("submit_documentation", null, FlexoActionType.helpGroup, NORMAL_ACTION_TYPE);
		}

		private boolean allowsDocSubmission = false;

		/**
		 * Factory method
		 */
		@Override
		public SubmitDocumentationAction makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new SubmitDocumentationAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return allowsDocSubmission;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return allowsDocSubmission && object != null && object instanceof InspectableObject;
		}

		public boolean allowsDocSubmission() {
			return allowsDocSubmission;
		}

		public void setAllowsDocSubmission(boolean allows_Doc_Submission) {
			this.allowsDocSubmission = allows_Doc_Submission;
		}

	}

	public static final SubmitDocumentationActionType actionType = new SubmitDocumentationActionType();

	static {
		FlexoObjectImpl.addActionForClass(SubmitDocumentationAction.actionType, FlexoObject.class);
	}

	SubmitDocumentationAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
