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
package org.openflexo.foundation.ptoc.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ptoc.PTOCObject;
import org.openflexo.foundation.ptoc.PTOCRepository;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;


public class RemovePTOCRepository extends FlexoAction<RemovePTOCRepository, PTOCRepository, PTOCObject> {

	public static final FlexoActionType<RemovePTOCRepository, PTOCRepository, PTOCObject> actionType = 
		new FlexoActionType<RemovePTOCRepository, PTOCRepository, PTOCObject>("remove_toc", FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

			@Override
			protected boolean isEnabledForSelection(PTOCRepository object,
					Vector<PTOCObject> globalSelection) {
				return object!=null;
			}

			@Override
			protected boolean isVisibleForSelection(PTOCRepository object,
					Vector<PTOCObject> globalSelection) {
				return object!=null;
			}

			@Override
			public RemovePTOCRepository makeNewAction(
					PTOCRepository focusedObject,
					Vector<PTOCObject> globalSelection, FlexoEditor editor) {
				return new RemovePTOCRepository(focusedObject,globalSelection,editor);
			}
		
	};
	
	protected RemovePTOCRepository(PTOCRepository focusedObject, Vector<PTOCObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFocusedObject()!=null)
			getFocusedObject().delete();
	}

}
