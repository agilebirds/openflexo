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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMSet;
import org.openflexo.foundation.dm.DMSet.PackageReference.ClassReference;
import org.openflexo.foundation.dm.LoadableDMEntity;
import org.openflexo.localization.FlexoLocalization;

public class UpdateLoadableDMEntity extends FlexoAction<UpdateLoadableDMEntity, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(UpdateLoadableDMEntity.class.getPackage().getName());

	public static FlexoActionType<UpdateLoadableDMEntity, DMObject, DMObject> actionType = new FlexoActionType<UpdateLoadableDMEntity, DMObject, DMObject>(
			"update", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public UpdateLoadableDMEntity makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new UpdateLoadableDMEntity(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return getAllLoadableDMEntities(object, globalSelection).size() > 0;
		}

		@Override
		protected boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMModel.class);
		FlexoModelObject.addActionForClass(actionType, DMPackage.class);
		FlexoModelObject.addActionForClass(actionType, LoadableDMEntity.class);
	}

	UpdateLoadableDMEntity(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private DMSet _updatedSet;

	public DMSet getUpdatedSet() {
		return _updatedSet;
	}

	public void setUpdatedSet(DMSet updatedSet) {
		_updatedSet = updatedSet;
	}

	@Override
	protected void doAction(Object context) {
		makeFlexoProgress(FlexoLocalization.localizedForKey("updating_repository"), 3);
		setProgress(FlexoLocalization.localizedForKey("updating_classes"));
		resetSecondaryProgress(getUpdatedEntities().size());
		for (LoadableDMEntity next : getUpdatedEntities()) {
			ClassReference classReference = _updatedSet.getClassReference(next.getJavaType());
			setSecondaryProgress(FlexoLocalization.localizedForKey("updating") + " " + classReference.getName());
			if (classReference.isSelected()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Update " + next + " according to " + classReference);
				}
				next.update(classReference);
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Ignore update " + next);
				}
			}
		}
		setProgress(FlexoLocalization.localizedForKey("updating_classes_done"));
		hideFlexoProgress();
	}

	private List<LoadableDMEntity> _updatedEntities = null;

	public List<LoadableDMEntity> getUpdatedEntities() {
		if (_updatedEntities == null) {
			_updatedEntities = getAllLoadableDMEntities(getFocusedObject(), getGlobalSelection());
		}
		return _updatedEntities;
	}

	static List<LoadableDMEntity> getAllLoadableDMEntities(FlexoModelObject focusedObject, Vector<DMObject> globalSelection) {
		List<LoadableDMEntity> returned = new ArrayList<LoadableDMEntity>();
		computeLoadableDMEntitiesListWith(focusedObject, returned);
		if (globalSelection != null) {
			for (DMObject next : globalSelection) {
				computeLoadableDMEntitiesListWith(next, returned);
			}
		}
		return returned;
	}

	private static void computeLoadableDMEntitiesListWith(FlexoModelObject object, List<LoadableDMEntity> list) {
		if (object != null && object instanceof LoadableDMEntity) {
			if (!list.contains(object)) {
				list.add((LoadableDMEntity) object);
			}
		} else if (object != null && object instanceof DMPackage) {
			for (DMEntity next : ((DMPackage) object).getEntities()) {
				if (next instanceof LoadableDMEntity) {
					if (!list.contains(next)) {
						list.add((LoadableDMEntity) next);
					}
				}
			}
		}
	}

}
