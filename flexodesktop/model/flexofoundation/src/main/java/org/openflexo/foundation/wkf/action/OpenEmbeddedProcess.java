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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.SubProcessNode;

public class OpenEmbeddedProcess extends FlexoGUIAction<OpenEmbeddedProcess, SubProcessNode, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenEmbeddedProcess.class.getPackage().getName());

	public static FlexoActionType<OpenEmbeddedProcess, SubProcessNode, WKFObject> actionType = new FlexoActionType<OpenEmbeddedProcess, SubProcessNode, WKFObject>(
			"open_embedded_process", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenEmbeddedProcess makeNewAction(SubProcessNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenEmbeddedProcess(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(SubProcessNode object, Vector<WKFObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(SubProcessNode object, Vector<WKFObject> globalSelection) {
			return object != null && object.getSubProcess() != null;
		}

	};

	OpenEmbeddedProcess(SubProcessNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcessToOpen() {
		return getFocusedObject().getSubProcess();
	}
}
