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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.SynchronizationScheme;

public class SynchronizationSchemeAction extends EditionSchemeAction<SynchronizationSchemeAction, SynchronizationScheme> {

	private static final Logger logger = Logger.getLogger(SynchronizationSchemeAction.class.getPackage().getName());

	private SynchronizationSchemeActionType actionType;

	public SynchronizationSchemeAction(SynchronizationSchemeActionType actionType, FlexoModelObject focusedObject,
			Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		this.actionType = actionType;
	}

	public SynchronizationScheme getSynchronizationScheme() {
		if (actionType != null) {
			return actionType.getSynchronizationScheme();
		}
		return null;
	}

	@Override
	public EditionPatternInstance getEditionPatternInstance() {
		if (actionType != null) {
			return actionType.getEditionPatternInstance();
		}
		return null;
	}

	@Override
	public SynchronizationScheme getEditionScheme() {
		return getSynchronizationScheme();
	}

	@Override
	protected void doAction(Object context) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Perform action " + actionType);
		}

		if (getSynchronizationScheme() != null && getSynchronizationScheme().evaluateCondition(actionType.getEditionPatternInstance())) {
			applyEditionActions();
		}
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		if (getEditionPatternInstance() instanceof VirtualModelInstance) {
			return (VirtualModelInstance) getEditionPatternInstance();
		}
		if (getEditionPatternInstance() != null) {
			return getEditionPatternInstance().getVirtualModelInstance();
		}
		/*if (getFocusedObject() instanceof DiagramElement<?>) {
			return ((DiagramElement<?>) getFocusedObject()).getDiagram();
		}*/
		return null;
	}

}
