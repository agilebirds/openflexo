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
package org.openflexo.foundation.toc.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;

public class RemoveTOCRepository extends FlexoAction<RemoveTOCRepository, TOCRepository, TOCObject> {

	public static final FlexoActionType<RemoveTOCRepository, TOCRepository, TOCObject> actionType = new FlexoActionType<RemoveTOCRepository, TOCRepository, TOCObject>(
			"remove_toc", FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(TOCRepository object, Vector<TOCObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isVisibleForSelection(TOCRepository object, Vector<TOCObject> globalSelection) {
			return object != null;
		}

		@Override
		public RemoveTOCRepository makeNewAction(TOCRepository focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
			return new RemoveTOCRepository(focusedObject, globalSelection, editor);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, TOCRepository.class);
	}

	protected RemoveTOCRepository(TOCRepository focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject() != null) {
			getFocusedObject().delete();
		}
	}

}
