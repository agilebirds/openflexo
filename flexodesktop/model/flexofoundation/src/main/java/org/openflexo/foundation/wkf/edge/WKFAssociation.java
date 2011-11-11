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
package org.openflexo.foundation.wkf.edge;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.AssociationRemoved;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

public class WKFAssociation extends WKFEdge<WKFNode, WKFNode> implements DeletableObject {

	public enum Arrow {
		NONE, START_TO_END, END_TO_START, BOTH;
	}

	private Arrow arrow = Arrow.START_TO_END;

	public WKFAssociation(FlexoProcessBuilder builder) {
		super(builder.process);
		initializeDeserialization(builder);
	}

	private WKFAssociation(FlexoProcess process) {
		super(process);
	}

	public WKFAssociation(WKFNode startNode, WKFNode endNode) {
		this(startNode.getProcess());
		setStartNode(startNode);
		setEndNode(endNode);
	}

	@Override
	public final void delete() {
		super.delete();
		setChanged();
		notifyObservers(new AssociationRemoved(this));
		deleteObservers();
	}

	@Override
	public void addIncomingEdgeToEndNode(WKFNode endNode) {
		endNode.addToIncomingAssociations(this);
	}

	@Override
	public void addOutgoingEdgeToStartNode(WKFNode startNode) {
		startNode.addToOutgoingAssociations(this);
	}

	@Override
	public void removeIncomingEdgeFromEndNode(WKFNode endNode) {
		endNode.removeFromIncomingAssociations(this);
	}

	@Override
	public void removeOutgoingEdgeFromStartNode(WKFNode startNode) {
		startNode.removeFromOutgoingAssociations(this);
	}

	public Arrow getArrow() {
		return arrow;
	}

	public void setArrow(Arrow arrow) {
		Arrow old = this.arrow;
		this.arrow = arrow;
		setChanged();
		notifyAttributeModification("arrow", old, arrow);
	}

	@Override
	public Class<WKFNode> getEndNodeClass() {
		return WKFNode.class;
	}

	@Override
	public Class<WKFNode> getStartNodeClass() {
		return WKFNode.class;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.ASSOCIATION_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "association";
	}

}
