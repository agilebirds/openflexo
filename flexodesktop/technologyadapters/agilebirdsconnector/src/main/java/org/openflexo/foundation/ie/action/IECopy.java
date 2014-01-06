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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.widget.IETRWidget;

public class IECopy extends FlexoAction<IECopy, IEObject, IEObject> {

	private static final Logger logger = Logger.getLogger(IECopy.class.getPackage().getName());

	public static FlexoActionType<IECopy, IEObject, IEObject> actionType = new FlexoActionType<IECopy, IEObject, IEObject>("copy",
			FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public IECopy makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new IECopy(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return globalSelection != null && globalSelection.size() == 1
					&& !(globalSelection.firstElement() instanceof ComponentDefinition)
					&& !(globalSelection.firstElement() instanceof IEWOComponent)
					&& !(globalSelection.firstElement() instanceof IETRWidget)
					&& !(globalSelection.firstElement() instanceof FlexoComponentLibrary)
					&& !(globalSelection.firstElement() instanceof FlexoComponentFolder);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, IEObject.class);
	}

	IECopy(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {

		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Implemented in IE module
		logger.info("COPY on IE");
	}

}
