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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
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
	protected void applyEditionActions() {
		beginSynchronization();
		super.applyEditionActions();
		endSynchronization();
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

	private List<EditionPatternInstance> episToBeRemoved;

	public void beginSynchronization() {
		System.out.println("BEGIN synchronization on " + getVirtualModelInstance());
		episToBeRemoved = new ArrayList<EditionPatternInstance>();
		episToBeRemoved.addAll(getVirtualModelInstance().getAllEPInstances());
	}

	public void endSynchronization() {
		System.out.println("END synchronization on " + getVirtualModelInstance());
		for (EditionPatternInstance epi : episToBeRemoved) {
			System.out.println("Deleting " + epi);
			epi.delete();
		}
	}

	public EditionPatternInstance matchEditionPatternInstance(EditionPattern editionPatternType, Hashtable<PatternRole, Object> criterias) {
		System.out.println("MATCH epi on " + getVirtualModelInstance() + " for " + editionPatternType + " with " + criterias);
		for (EditionPatternInstance epi : getVirtualModelInstance().getEPInstances(editionPatternType)) {
			boolean allCriteriasMatching = true;
			for (PatternRole pr : criterias.keySet()) {
				if (!FlexoObject.areSameValue(epi.getPatternActor(pr), criterias.get(pr))) {
					allCriteriasMatching = false;
				}
			}
			if (allCriteriasMatching) {
				return epi;
			}
		}
		return null;
	}

	public void foundMatchingEditionPatternInstance(EditionPatternInstance matchingEditionPatternInstance) {
		System.out.println("FOUND matching : " + matchingEditionPatternInstance);
		episToBeRemoved.remove(matchingEditionPatternInstance);
	}

	public void newEditionPatternInstance(EditionPatternInstance newEditionPatternInstance) {
		System.out.println("NEW EPI : " + newEditionPatternInstance);

	}

}
