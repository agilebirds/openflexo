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
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;

public class WKFCopy extends FlexoAction<WKFCopy, AgileBirdsObject, AgileBirdsObject> {

	private static final Logger logger = Logger.getLogger(WKFCopy.class.getPackage().getName());

	public static FlexoActionType<WKFCopy, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<WKFCopy, AgileBirdsObject, AgileBirdsObject>(
			"copy", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public WKFCopy makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
			return new WKFCopy(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {

			return !(object instanceof FlexoPetriGraph) && !(object instanceof FlexoProcess) && !(object instanceof WKFGroup);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, WKFObject.class);
	}

	WKFCopy(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Implemented in WKF module
		logger.info("COPY on WKF");
	}

}
