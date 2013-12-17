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
package org.openflexo.foundation.action;

import java.util.Vector;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor;

public class DefaultFlexoActionSource implements FlexoActionSource {

	private FlexoModelObject _focusedObject;
	private Vector _globalSelection;
	private FlexoEditor _editor;

	public DefaultFlexoActionSource(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super();
		_focusedObject = focusedObject;
		_globalSelection = globalSelection;
		_editor = editor;
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		return _focusedObject;
	}

	@Override
	public Vector getGlobalSelection() {
		return _globalSelection;
	}

	@Override
	public FlexoEditor getEditor() {
		return _editor;
	}

}
