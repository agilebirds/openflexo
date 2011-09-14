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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.toc.TOCEntry;


public class MoveTOCEntry extends FlexoAction<MoveTOCEntry, TOCEntry, TOCEntry> {

	public static final FlexoActionType<MoveTOCEntry, TOCEntry, TOCEntry> actionType = new FlexoActionType<MoveTOCEntry, TOCEntry, TOCEntry>(
			"move_toc_entry") {

		@Override
		protected boolean isEnabledForSelection(TOCEntry object, Vector<TOCEntry> globalSelection) {
			if (object==null)
				return false;
			for (TOCEntry entry : globalSelection) {
				if (!object.acceptsEntryAsChild(entry))
					return false;
			}
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(TOCEntry object, Vector<TOCEntry> globalSelection) {
			return false;
		}

		@Override
		public MoveTOCEntry makeNewAction(TOCEntry focusedObject, Vector<TOCEntry> globalSelection, FlexoEditor editor) {
			return new MoveTOCEntry(focusedObject,globalSelection,editor);
		}

	};
	
	protected MoveTOCEntry(TOCEntry focusedObject, Vector<TOCEntry> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Vector<TOCEntry> entries = getGlobalSelection();
		for (TOCEntry entry : entries) {
			if (getFocusedObject().acceptsEntryAsChild(entry))
				getFocusedObject().addToTocEntries(entry);
		}
	}

	public TOCEntry getTargetEntry() {
		return getFocusedObject();
	}

}
