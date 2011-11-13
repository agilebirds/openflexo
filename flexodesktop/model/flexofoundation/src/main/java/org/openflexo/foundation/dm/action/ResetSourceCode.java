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
package org.openflexo.foundation.dm.action;

import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.dm.javaparser.SourceCodeOwner;

public class ResetSourceCode extends FlexoAction<ResetSourceCode, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(CreateDMEOEntity.class.getPackage().getName());

	public static FlexoActionType<ResetSourceCode, DMObject, DMObject> actionType = new FlexoActionType<ResetSourceCode, DMObject, DMObject>(
			"reset_source_code", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ResetSourceCode makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new ResetSourceCode(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		protected boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return object != null;
		}

	};

	ResetSourceCode(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		HashSet<DMObject> visited = new HashSet<DMObject>();
		HashSet<SourceCodeOwner> codeOwner = new HashSet<SourceCodeOwner>();
		if (getFocusedObject() != null) {
			processToAdditionOfSourceCodeOwner(getFocusedObject(), visited, codeOwner);
		}
		if (getGlobalSelection() != null) {
			for (DMObject object : getGlobalSelection()) {
				processToAdditionOfSourceCodeOwner(object, visited, codeOwner);
			}
		}
		for (SourceCodeOwner sourceCodeOwner : codeOwner) {
			try {
				sourceCodeOwner.resetSourceCode();
			} catch (ParserNotInstalledException e) {
				e.printStackTrace();
				// If parser is not installed for one object, it won't be for any.
				return;
			} catch (DuplicateMethodSignatureException e) {
				e.printStackTrace();
			}
		}
	}

	private void processToAdditionOfSourceCodeOwner(DMObject object, HashSet<DMObject> visited, HashSet<SourceCodeOwner> codeOwner) {
		if (visited.contains(object))
			return;
		visited.add(object);
		if (object instanceof SourceCodeOwner && !codeOwner.contains(object)) {
			codeOwner.add((SourceCodeOwner) object);
		}
		Vector<DMObject> embedded = object.getAllEmbeddedDMObjects();
		for (DMObject o : embedded) {
			processToAdditionOfSourceCodeOwner(o, visited, codeOwner);
		}
	}

}
