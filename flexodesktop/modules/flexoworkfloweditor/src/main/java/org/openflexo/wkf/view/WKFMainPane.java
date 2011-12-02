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
package org.openflexo.wkf.view;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.OpenGroup;
import org.openflexo.foundation.wkf.action.OpenLoopedPetriGraph;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.OpenPortRegistery;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.swleditor.SwimmingLaneView;

/**
 * Represents the main pane for WKF module
 * 
 * @author yourname
 */
public class WKFMainPane extends FlexoMainPane implements GraphicalFlexoObserver {

	public WKFMainPane(ModuleView moduleView, WKFFrame mainFrame, WKFController controller) {
		super(moduleView, mainFrame, controller);
	}

	public void showProcessBrowser() {
		showLeftView();
	}

	public void hideProcessBrowser() {
		hideLeftView();
	}

	@Override
	public boolean isCollapseEnabled() {
		return true;
	}

	@Override
	public void performCollapseAll() {
		if (getController().getCurrentModuleView() != null
				&& getController().getCurrentModuleView().getRepresentedObject() instanceof FlexoProcess) {
			FlexoProcess flexoProcess = ((FlexoProcess) getController().getCurrentModuleView().getRepresentedObject());
			for (AbstractActivityNode node : flexoProcess.getAllAbstractActivityNodes()) {
				if (node.hasContainedPetriGraph() && node.getOperationPetriGraph().getIsVisible()) {
					OpenOperationLevel.actionType.makeNewAction(node, null, getController().getEditor()).doAction();
				}
			}
			for (LOOPOperator node : flexoProcess.getActivityPetriGraph().getAllLoopOperators()) {
				if (node.hasExecutionPetriGraph() && node.getExecutionPetriGraph().getIsVisible()) {
					OpenLoopedPetriGraph.actionType.makeNewAction(node, null, getController().getEditor()).doAction();
				}
			}
			for (SelfExecutableNode node : flexoProcess.getActivityPetriGraph().getAllSelfExecutableNodes()) {
				if (node.hasExecutionPetriGraph() && node.getExecutionPetriGraph().getIsVisible()) {
					OpenExecutionPetriGraph.actionType.makeNewAction((PetriGraphNode) node, null, getController().getEditor()).doAction();
				}
			}
			for (WKFGroup group : flexoProcess.getActivityPetriGraph().getGroups()) {
				if (group.isExpanded()) {
					OpenGroup.actionType.makeNewAction(group, null, getController().getEditor()).doAction();
				}
			}
			if (flexoProcess.getPortRegistery() != null && flexoProcess.getPortRegistery().getIsVisible()) {
				OpenPortRegistery.actionType.makeNewAction(flexoProcess, null, getController().getEditor()).doAction();
			}

		}
	}

	@Override
	protected FlexoModelObject getParentObject(FlexoModelObject object) {
		if (object instanceof FlexoProcess) {
			return (((FlexoProcess) object).getParentProcess());
		}
		return null;
	}

	@Override
	public boolean isAutoLayoutEnabled() {
		return getController().getCurrentModuleView() instanceof SwimmingLaneView;
	}

	@Override
	public void performAutolayout() {
		if (getController().getCurrentModuleView() instanceof SwimmingLaneView) {
			SwimmingLaneView view = (SwimmingLaneView) getController().getCurrentModuleView();
			view.getController().performAutoLayout();
		}
	}
}
