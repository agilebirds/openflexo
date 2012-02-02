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
/**
 * 
 */
/*
 * 
 */

package org.openflexo.foundation.ptoc.action;
/**
 * MOS
 * @author MOSTAFA
 */
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ptoc.PTOCEntry;
import org.openflexo.foundation.toc.TOCEntry;


public class MovePTOCEntry extends FlexoAction<MovePTOCEntry, PTOCEntry, PTOCEntry> {

	public static final FlexoActionType<MovePTOCEntry, PTOCEntry, PTOCEntry> actionType = new FlexoActionType<MovePTOCEntry, PTOCEntry, PTOCEntry>(
			"move_toc_entry") {

		@Override
		protected boolean isEnabledForSelection(PTOCEntry object, Vector<PTOCEntry> globalSelection) {
//			if (object==null)
//				return false;
//			for (PTOCEntry entry : globalSelection) {
//				if (!object.acceptsEntryAsChild(entry))
//					return false;
//			}
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(PTOCEntry object, Vector<PTOCEntry> globalSelection) {
			return false;
		}

		@Override
		public MovePTOCEntry makeNewAction(PTOCEntry focusedObject, Vector<PTOCEntry> globalSelection, FlexoEditor editor) {
			return new MovePTOCEntry(focusedObject,globalSelection,editor);
		}

	};
	
	protected MovePTOCEntry(PTOCEntry focusedObject, Vector<PTOCEntry> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
//		Vector<PTOCEntry> entries = getGlobalSelection();
//		for (PTOCEntry entry : entries) {
//			if (getFocusedObject().acceptsEntryAsChild(entry))
//				getFocusedObject().addToPTocUnits(entry);
//		}
	}

	public PTOCEntry getTargetEntry() {
		return getFocusedObject();
	}

}
