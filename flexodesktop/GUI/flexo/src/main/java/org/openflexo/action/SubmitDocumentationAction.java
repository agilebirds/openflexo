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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.inspector.InspectableObject;

public class SubmitDocumentationAction extends FlexoGUIAction<SubmitDocumentationAction, FlexoModelObject, FlexoModelObject> {

	public static class SubmitDocumentationActionType extends
			FlexoActionType<SubmitDocumentationAction, FlexoModelObject, FlexoModelObject> {
		protected SubmitDocumentationActionType() {
			super("submit_documentation", null, FlexoActionType.helpGroup, NORMAL_ACTION_TYPE);
		}

		private boolean allowsDocSubmission = false;

		/**
		 * Factory method
		 */
		@Override
		public SubmitDocumentationAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new SubmitDocumentationAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return allowsDocSubmission;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return (allowsDocSubmission && (object != null) && (object instanceof InspectableObject));
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
		FlexoModelObject.addActionForClass(SubmitDocumentationAction.actionType, FlexoModelObject.class);
	}

	SubmitDocumentationAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/* public String getLocalizedName ()
	 {
	     if (getFocusedObject() != null) {
	         String shortClassName = null;
	         String extClassName = getFocusedObject().getClass().getName();
	         StringTokenizer st = new StringTokenizer(extClassName,".");
	         while (st.hasMoreTokens()) shortClassName = st.nextToken();
	         return FlexoLocalization.localizedForKey("submit_doc_for")+" "+shortClassName;
	     }
	     return null;
	 }*/
}
