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
package org.openflexo.foundation.wkf.node;

import java.util.Vector;

import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.CreatePetriGraph;
import org.openflexo.foundation.wkf.dm.PetriGraphSet;

/**
 * Represents a FlexoNode that way contains imbricated PetriGraph: this is an intermediate parent class for ActivityNode and OperationNode
 * 
 * @author sguerin
 * 
 */
public abstract class FatherNode extends FlexoNode {

	private FlexoPetriGraph containedPetriGraph;

	public FatherNode(FlexoProcess process) {
		super(process);
	}

	public FlexoPetriGraph getContainedPetriGraph() {
		return containedPetriGraph;
	}

	public boolean hasContainedPetriGraph() {
		return (containedPetriGraph != null);
	}

	public void setContainedPetriGraph(FlexoPetriGraph aPetriGraph) {
		containedPetriGraph = aPetriGraph;
		if (containedPetriGraph != null) {
			if (this instanceof AbstractActivityNode) {
				containedPetriGraph.setContainer(this, FlexoProcess.OPERATION_CONTEXT);
			} else if (this instanceof OperationNode) {
				containedPetriGraph.setContainer(this, FlexoProcess.ACTION_CONTEXT);
			}
		}
		if (!isDeserializing()) {
			setChanged();
			notifyObservers(new PetriGraphSet(aPetriGraph));
		}
	}

	public boolean containsNormalNodes() {
		if (getContainedPetriGraph() == null)
			return false;
		for (AbstractNode node : getContainedPetriGraph().getNodes()) {
			if (node instanceof FlexoNode && !((FlexoNode) node).isBeginOrEndNode())
				return true;
		}
		return false;
	}

	@Override
	public void delete() {
		if (containedPetriGraph != null) {
			containedPetriGraph.delete();
		}
		setChanged();
		super.delete();
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		if (containedPetriGraph != null) {
			returned.add(containedPetriGraph);
			returned.addAll(containedPetriGraph.getAllEmbeddedWKFObjects());
		}
		return returned;
	}

	/**
	 * The insertion or removal of subNodes has to be done with Constructor or removeFromSubNodes method.
	 * 
	 * @return Vector of FlexoNode
	 */
	public Vector getSubNodes() {
		if (containedPetriGraph != null) {
			return containedPetriGraph.getNodes();
		} else {
			return new Vector();
		}
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		// returned.add(OpenOperationLevel.actionType);
		returned.add(CreatePetriGraph.actionType);
		return returned;
	}

	@Override
	public boolean isInteractive() {
		return isNormalNode();
	}

}
