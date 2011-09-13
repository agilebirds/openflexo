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

import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.action.OpenExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.OpenLoopedPetriGraph;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation;


public class OperationPetriGraphGR extends ContainerGR<OperationPetriGraph> {



	public OperationPetriGraphGR(OperationPetriGraph object, SwimmingLaneRepresentation aDrawing)
	{
		super(object, aDrawing,OPERATION_PG_COLOR, OPERATION_PG_BACK_COLOR);
		setLayer(OPERATION_PG_LAYER);
	}

	public OperationPetriGraph getOperationPetriGraph()
	{
		return getDrawable();
	}

	@Override
	public String getLabel()
	{
		return (getOperationPetriGraph().getContainer() instanceof AbstractNode ?
				((AbstractNode)getOperationPetriGraph().getContainer()).getName() : "???");
	}

	@Override
	public void closingRequested()
	{
		if (getOperationPetriGraph().getContainer() instanceof SelfExecutableNode) {
			OpenExecutionPetriGraph.actionType.makeNewAction((PetriGraphNode)getOperationPetriGraph().getContainer(),null,getDrawing().getEditor()).doAction();
		}
		else if (getOperationPetriGraph().getContainer() instanceof LOOPOperator) {
			OpenLoopedPetriGraph.actionType.makeNewAction((LOOPOperator)getOperationPetriGraph().getContainer(),null,getDrawing().getEditor()).doAction();
		}
		else if (getOperationPetriGraph().getContainer() instanceof AbstractActivityNode) {
			OpenOperationLevel.actionType.makeNewAction((AbstractActivityNode)getOperationPetriGraph().getContainer(),null,getDrawing().getEditor()).doAction();
		}
        // Is now performed by receiving notification
        // getDrawing().updateGraphicalObjectsHierarchy();
	}

	protected WKFObjectGR<?> getWKFContainerGR()
	{
		return (WKFObjectGR<?>)getGraphicalRepresentation(getOperationPetriGraph().getContainer());
	}

	/**
	 * Overriden to implement defaut automatic layout
	 * Container will be horizontal centered above parent node
	 */
	@Override
	public double getDefaultX()
	{
		if (getWKFContainerGR() != null) {
			WKFObjectGR containerGR = getWKFContainerGR();
			return Math.max(0,containerGR.getLocationInDrawing().x-(getWidth()-containerGR.getWidth())/2);
		}
		return 0;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 * Container will be located 50 pixels (1.0 scale) above parent node
	 */
	@Override
	public double getDefaultY()
	{
		if (getWKFContainerGR() != null) {
			WKFObjectGR containerGR = getWKFContainerGR();
			return containerGR.getLocationInDrawing().y+containerGR.getHeight()+50;
		}
		return 0;
	}

	@Override
	public double getDefaultWidth() {
		return 350;
	}

}
