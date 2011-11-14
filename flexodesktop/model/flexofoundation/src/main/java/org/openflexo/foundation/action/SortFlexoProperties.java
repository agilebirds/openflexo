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

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProperty;

public class SortFlexoProperties extends FlexoAction<SortFlexoProperties, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<SortFlexoProperties, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<SortFlexoProperties, FlexoModelObject, FlexoModelObject>(
			"sort_flexo_properties") {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return false;
		}

		@Override
		public SortFlexoProperties makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new SortFlexoProperties(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoModelObject.class);
	}

	private String name;
	private String value;

	private FlexoProperty createdProperty;

	public SortFlexoProperties(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Vector<FlexoModelObject> v = getGlobalSelectionAndFocusedObject();
		for (FlexoModelObject object : v) {
			sortPropertiesForObject(object);
		}
	}

	private void sortPropertiesForObject(FlexoModelObject object) {
		Collections.sort(object.getCustomProperties(), new Comparator<FlexoProperty>() {

			@Override
			public int compare(FlexoProperty o1, FlexoProperty o2) {
				if (o1.getName() == null) {
					if (o2.getName() == null) {
						return 0;
					} else {
						return -1;
					}
				} else {
					if (o2.getName() == null) {
						return 1;
					}
					return o1.getName().compareTo(o2.getName());
				}
			}

		});
	}

}
