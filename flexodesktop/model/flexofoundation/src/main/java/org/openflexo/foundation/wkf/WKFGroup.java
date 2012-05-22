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
package org.openflexo.foundation.wkf;

import java.awt.Color;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.wkf.dm.GroupUpdated;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class WKFGroup extends WKFObject implements DeletableObject {

	private static final Logger logger = Logger.getLogger(WKFGroup.class.getPackage().getName());

	private Vector<PetriGraphNode> nodes;

	private String groupName;

	// =================================================================
	// ========================= Constructor ===========================
	// =================================================================

	/**
	 * Default constructor
	 */
	public WKFGroup(FlexoProcess process) {
		super(process);
		nodes = new Vector<PetriGraphNode>();
		setIsVisible(true);
	}

	public Vector<PetriGraphNode> getNodes() {
		return nodes;
	}

	public void setNodes(Vector<PetriGraphNode> nodes) {
		this.nodes = nodes;
	}

	public void addToNodes(PetriGraphNode node) {
		nodes.add(node);
	}

	public void removeFromNodes(PetriGraphNode node) {
		nodes.remove(node);
	}

	public void notifyGroupUpdated() {
		setChanged();
		notifyObservers(new GroupUpdated(this));
	}

	@Override
	public Vector<? extends WKFObject> getAllEmbeddedDeleted() {
		return getNodes();
	}

	@Override
	public Vector<? extends WKFObject> getAllEmbeddedWKFObjects() {
		return getNodes();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean contains(AbstractNode node) {
		return getNodes().contains(node);
	}

	public boolean isExpanded() {
		return getIsVisible();
	}

	public void setExpanded(boolean isExpanded) {
		setIsVisible(isExpanded);
	}

	public static final Color DEFAULT_GROUP_COLOR = new Color(157, 162, 132);

	public Color getColor() {
		return getBgColor(DEFAULT, DEFAULT_GROUP_COLOR);
	}

	public void setColor(Color aColor) {
		if (requireChange(getColor(), aColor)) {
			Color oldColor = getColor();
			setBgColor(aColor, DEFAULT);
			setChanged();
			notifyObservers(new WKFAttributeDataModification("color", oldColor, aColor));
		}
	}

	private FlexoPetriGraph _petriGraph;

	public FlexoPetriGraph getParentPetriGraph() {
		return _petriGraph;
	}

	public final void setParentPetriGraph(FlexoPetriGraph pg) {
		_petriGraph = pg;
	}

	@Override
	public final void delete() {
		logger.info("Called DELETE on WKFGroup");
		FlexoPetriGraph parentPetriGraph = getParentPetriGraph();
		Vector<PetriGraphNode> nodesInGroup = new Vector<PetriGraphNode>(getNodes());
		for (PetriGraphNode n : nodesInGroup) {
			n.delete();
		}
		if (parentPetriGraph != null) {
			parentPetriGraph.removeFromGroups(this);
		}
		super.delete();
		deleteObservers();
	}

	public final void ungroup() {
		Vector<PetriGraphNode> nodesThatWereInGroup = new Vector<PetriGraphNode>(getNodes());
		FlexoPetriGraph parentPetriGraph = getParentPetriGraph();
		for (PetriGraphNode n : nodesThatWereInGroup) {
			removeFromNodes(n);
		}
		if (parentPetriGraph != null) {
			parentPetriGraph.removeFromGroups(this);
			parentPetriGraph.notifyNodeUngroup(this, nodesThatWereInGroup);
		}
		super.delete();
	}

}
