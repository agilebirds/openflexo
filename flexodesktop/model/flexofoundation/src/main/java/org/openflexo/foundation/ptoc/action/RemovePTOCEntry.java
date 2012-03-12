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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ptoc.PTOCEntry;
import org.openflexo.foundation.toc.TOCEntry;


public class RemovePTOCEntry extends FlexoAction<RemovePTOCEntry, PTOCEntry,PTOCEntry>
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RemovePTOCEntry.class.getPackage().getName());

    public static FlexoActionType<RemovePTOCEntry, PTOCEntry, PTOCEntry> actionType = new FlexoActionType<RemovePTOCEntry, PTOCEntry, PTOCEntry>(
            "remove_toc_entry", FlexoActionType.defaultGroup, FlexoActionType.DELETE_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
        public RemovePTOCEntry makeNewAction(PTOCEntry focusedObject, Vector<PTOCEntry> globalSelection, FlexoEditor editor)
        {
            return new RemovePTOCEntry(focusedObject, globalSelection, editor);
        }

        @Override
        protected boolean isVisibleForSelection(PTOCEntry object, Vector<PTOCEntry> globalSelection)
        {
            return true;
        }

        @Override
        protected boolean isEnabledForSelection(PTOCEntry object, Vector<PTOCEntry> globalSelection)
        {
            return object!=null ;
        }

    };

    RemovePTOCEntry(PTOCEntry focusedObject, Vector<PTOCEntry> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

    @Override
    protected void doAction(Object context)
    {
    	getFocusedObject().delete();
    }

}
