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
package org.openflexo.foundation.view.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;
import org.openflexo.foundation.viewpoint.SynchronizationScheme;
import org.openflexo.localization.LocalizedDelegate;

public class SynchronizationSchemeActionType extends
		FlexoActionType<SynchronizationSchemeAction, VirtualModelInstance, VirtualModelInstanceObject> {

	private final SynchronizationScheme synchronizationScheme;
	private final EditionPatternInstance editionPatternInstance;

	public SynchronizationSchemeActionType(SynchronizationScheme synchronizationScheme, EditionPatternInstance editionPatternInstance) {
		super(synchronizationScheme.getLabel(), FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE);
		this.synchronizationScheme = synchronizationScheme;
		this.editionPatternInstance = editionPatternInstance;
	}

	@Override
	public LocalizedDelegate getLocalizer() {
		return synchronizationScheme.getLocalizedDictionary();
	}

	@Override
	public boolean isEnabled(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return isEnabledForSelection(object, globalSelection);
	}

	@Override
	public boolean isEnabledForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return synchronizationScheme.evaluateCondition(editionPatternInstance);
	}

	@Override
	public boolean isVisibleForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
		return true;
	}

	@Override
	public SynchronizationSchemeAction makeNewAction(VirtualModelInstance focusedObject,
			Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		return new SynchronizationSchemeAction(this, focusedObject, globalSelection, editor);
	}

	public SynchronizationScheme getSynchronizationScheme() {
		return synchronizationScheme;
	}

	public EditionPatternInstance getEditionPatternInstance() {
		return editionPatternInstance;
	}

}
