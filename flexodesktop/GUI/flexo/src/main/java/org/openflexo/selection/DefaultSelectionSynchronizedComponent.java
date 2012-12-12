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
package org.openflexo.selection;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoObject;

/**
 * Default implementation for a SelectionSynchronizedComponent
 * 
 * @author sguerin
 */
public abstract class DefaultSelectionSynchronizedComponent implements SelectionSynchronizedComponent {

	private SelectionManager _selectionManager;

	public DefaultSelectionSynchronizedComponent(SelectionManager selectionManager) {
		super();
		_selectionManager = selectionManager;
	}

	@Override
	public SelectionManager getSelectionManager() {
		return _selectionManager;
	}

	@Override
	public Vector getSelection() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getSelection();
		}
		return null;
	}

	@Override
	public void resetSelection() {
		if (getSelectionManager() != null) {
			getSelectionManager().resetSelection();
		} else {
			fireResetSelection();
		}
	}

	@Override
	public void addToSelected(FlexoObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().addToSelected(object);
			} else {
				fireObjectSelected(object);
			}
		}
	}

	@Override
	public void removeFromSelected(FlexoObject object) {
		if (mayRepresents(object)) {
			if (getSelectionManager() != null) {
				getSelectionManager().removeFromSelected(object);
			} else {
				fireObjectDeselected(object);
			}
		}
	}

	@Override
	public void addToSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().addToSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration en = objects.elements(); en.hasMoreElements();) {
				FlexoObject next = (FlexoObject) en.nextElement();
				fireObjectSelected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void removeFromSelected(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().removeFromSelected(objects);
		} else {
			fireBeginMultipleSelection();
			for (Enumeration en = objects.elements(); en.hasMoreElements();) {
				FlexoObject next = (FlexoObject) en.nextElement();
				fireObjectDeselected(next);
			}
			fireEndMultipleSelection();
		}
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoObject> objects) {
		if (getSelectionManager() != null) {
			getSelectionManager().setSelectedObjects(objects);
		} else {
			resetSelection();
			addToSelected(objects);
		}
	}

	@Override
	public FlexoObject getFocusedObject() {
		if (getSelectionManager() != null) {
			return getSelectionManager().getFocusedObject();
		}
		return null;
	}

	@Override
	public boolean mayRepresents(FlexoObject anObject) {
		return true;
	}

	@Override
	public abstract void fireObjectSelected(FlexoObject object);

	@Override
	public abstract void fireObjectDeselected(FlexoObject object);

	@Override
	public abstract void fireResetSelection();

	@Override
	public abstract void fireBeginMultipleSelection();

	@Override
	public abstract void fireEndMultipleSelection();

}
