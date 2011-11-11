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
package org.openflexo.dre.view;

import java.util.List;
import java.util.Vector;

import org.openflexo.components.tabular.CompoundTabularView;
import org.openflexo.dre.controller.DREController;
import org.openflexo.drm.DRMObject;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class DREView<O extends DRMObject> extends CompoundTabularView<O> implements SelectionSynchronizedModuleView<O>,
		GraphicalFlexoObserver {

	public DREView(O object, DREController controller, String title) {
		super(object, controller, title);
		object.addObserver(this);
	}

	public DREController getDREController() {
		return (DREController) getController();
	}

	public O getDRMObject() {
		return getModelObject();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof ObjectDeleted) {
			if (dataModification.oldValue() == getDRMObject()) {
				deleteModuleView();
			}
		}
	}

	@Override
	public O getRepresentedObject() {
		return getDRMObject();
	}

	@Override
	public void deleteModuleView() {
		getDREController().removeModuleView(this);
	}

	@Override
	public FlexoPerspective<DRMObject> getPerspective() {
		return getDREController().DRE_PERSPECTIVE;
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}
}
