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
package org.openflexo.wkf.swleditor.gr;

import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.action.OpenActionLevel;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.OpenLoopedPetriGraph;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;

public class ActionPetriGraphGR extends ContainerGR<ActionPetriGraph> {

	public ActionPetriGraphGR(ActionPetriGraph object, SwimmingLaneRepresentation aDrawing) {
		super(object, aDrawing, ACTION_PG_COLOR, ACTION_PG_BACK_COLOR);
		setLayer(ACTION_PG_LAYER);
	}

	public ActionPetriGraph getActionPetriGraph() {
		return getDrawable();
	}

	@Override
	public String getLabel() {
		return (getActionPetriGraph().getContainer() instanceof AbstractNode ? ((AbstractNode) getActionPetriGraph().getContainer())
				.getName() : "???");
	}

	@Override
	public void closingRequested() {
		if (getActionPetriGraph().getContainer() instanceof SelfExecutableNode) {
			OpenExecutionPetriGraph.actionType.makeNewAction((PetriGraphNode) getActionPetriGraph().getContainer(), null,
					getDrawing().getEditor()).doAction();
		} else if (getActionPetriGraph().getContainer() instanceof LOOPOperator) {
			OpenLoopedPetriGraph.actionType.makeNewAction((LOOPOperator) getActionPetriGraph().getContainer(), null,
					getDrawing().getEditor()).doAction();
		} else if (getActionPetriGraph().getContainer() instanceof OperationNode) {
			OpenActionLevel.actionType.makeNewAction((OperationNode) getActionPetriGraph().getContainer(), null, getDrawing().getEditor())
					.doAction();
		}
		// Is now performed by receiving notification
		// getDrawing().updateGraphicalObjectsHierarchy();
	}

	protected WKFObjectGR<?> getWKFContainerGR() {
		return (WKFObjectGR<?>) getGraphicalRepresentation(getActionPetriGraph().getContainer());
	}

	/**
	 * Overriden to implement defaut automatic layout Container will be horizontal centered above parent node
	 */
	@Override
	public double getDefaultX() {
		if (getWKFContainerGR() != null) {
			WKFObjectGR containerGR = getWKFContainerGR();
			return Math.max(0, containerGR.getLocationInDrawing().x - (getWidth() - containerGR.getWidth()) / 2);
		}
		return 0;
	}

	@Override
	public double getDefaultWidth() {
		return 300;
	}

	/**
	 * Overriden to implement defaut automatic layout Container will be located 50 pixels (1.0 scale) above parent node
	 */
	@Override
	public double getDefaultY() {
		if (getWKFContainerGR() != null) {
			WKFObjectGR containerGR = getWKFContainerGR();
			return containerGR.getLocationInDrawing().y + containerGR.getHeight() + 50;
		}
		return 0;
	}

}
