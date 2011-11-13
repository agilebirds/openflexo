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
package org.openflexo.wkf.swleditor;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.WKFLayoutManager;

public class SWLLayoutManager extends WKFLayoutManager {

	private static final Logger logger = Logger.getLogger(SWLLayoutManager.class.getPackage().getName());

	private final SwimmingLaneRepresentation _representation;

	private Hashtable<Role, AutoLayoutNodePath> isolatedNodePath;

	public SWLLayoutManager(SwimmingLaneRepresentation representation) {
		super(representation.getProcess());
		_representation = representation;
	}

	@Override
	public void recomputeProcessStructure(FlexoProgress progress) {
		super.recomputeProcessStructure(progress);
		if (progress != null)
			progress.setProgress(FlexoLocalization.localizedForKey("finding_isolated_nodes"));
		isolatedNodePath = new Hashtable<Role, AutoLayoutNodePath>();
		for (AutoLayoutNode n : getIsolatedNodes()) {
			Role role = bestRepresentationRole(n.node);
			AutoLayoutNodePath path = isolatedNodePath.get(role);
			if (path == null)
				isolatedNodePath.put(role, path = new AutoLayoutNodePath());
			path.add(n);
		}
	}

	protected Role bestRepresentationRole(PetriGraphNode node) {
		Role role = SwimmingLaneRepresentation.getRepresentationRole(node);
		if (role == null || role.isDefaultRole()) {
			if (node instanceof EventNode || node instanceof OperatorNode) {
				role = node.getBestRole();
				if (node instanceof EventNode) {
					((EventNode) node).setRole(role);
				} else {
					((OperatorNode) node).setRole(role);
				}
			}
			if (role == null)
				return node.getProcess().getWorkflow().getRoleList().getDefaultRole();
		}
		return role;
	}

	/*private Role bestRepresentationRole(AbstractNode node, Vector<AbstractNode> acc)
	{
		if (acc.contains(node))
			return getProcess().getWorkflow().getDefaultSystemRole();
		else
			acc.add(node);
		AutoLayoutNode aln = nodeMap.get(node);
		if (aln == null) return getProcess().getWorkflow().getDefaultSystemRole();
		boolean chooseSystemRole = false;
		if (node instanceof AbstractActivityNode) {
			if (((AbstractActivityNode)node).getRole() != null)
				return ((AbstractActivityNode)node).getRole();
			else
				return getProcess().getWorkflow().getDefaultSystemRole();
		}
		if ((node instanceof FlexoNode && ((FlexoNode)node).isBeginNode())
				|| (node instanceof FlexoNode && ((FlexoNode)node).isEndNode())
				|| (node instanceof SelfExecutableNode)) chooseSystemRole=true;
		Hashtable<Role,Integer> roles = new Hashtable<Role,Integer>();
		for (AutoLayoutNode previous : aln.previousNodes.values()) {
			Role r = bestRepresentationRole(previous.node,acc);
			if (r != null) roles.put(r, (roles.get(r)!=null?roles.get(r):0)+1);
		}
		for (AutoLayoutNode next : aln.followingNodes.values()) {
			Role r = bestRepresentationRole(next.node,acc);
			if (r != null) roles.put(r, (roles.get(r)!=null?roles.get(r):0)+1);
		}
		Role returned = null;
		int bestOccurence = 0;
		for (Role r : roles.keySet()) {
			if (!chooseSystemRole || r.getIsSystemRole()) {
				if (roles.get(r) > bestOccurence) {
					returned = r;
					bestOccurence = roles.get(r);
				}
			}
		}

		if (returned == null) return getProcess().getWorkflow().getDefaultSystemRole();
		
		return returned;
	}*/

	private Hashtable<Role, SwimmingPool> generalLayout;

	@Override
	public void layoutProcess(FlexoProgress progress) {
		recomputeProcessStructure(progress);
		if (getMainPath() == null)
			return;
		generalLayout = new Hashtable<Role, SwimmingPool>();
		if (progress != null)
			progress.setProgress(FlexoLocalization.localizedForKey("computing_layout"));
		layoutPath(getMainPath(), progress);
		for (AutoLayoutNodePath secondaryPath : getSecondaryPaths())
			layoutPath(secondaryPath, progress);
		for (Role r : isolatedNodePath.keySet()) {
			layoutPath(isolatedNodePath.get(r), progress);
		}
		logger.info("Layout has been computed, apply it");
		if (progress != null)
			progress.setProgress(FlexoLocalization.localizedForKey("applying_layout"));
		if (progress != null)
			progress.resetSecondaryProgress(generalLayout.size() + nodeMap.size());
		_representation.setSWLWidth(maxWidth + 3 * MARGIN);
		for (Role r : generalLayout.keySet()) {
			if (progress != null)
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("role") + " " + r.getName());
			SwimmingPool pool = generalLayout.get(r);
			_representation.setSwimmingLaneNb(pool.lanes.size(), r);
			for (SwimmingLane lane : pool.lanes.values()) {
				for (AutoLayoutNode node : lane.nodes)
					node.proposedLocation.y = lane.index * pool.maxLaneHeight;
			}
			_representation.setSwimmingLaneHeight((int) pool.maxLaneHeight, r);
		}
		for (AutoLayoutNode n : nodeMap.values()) {
			if (progress != null)
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("node") + " " + n.node.getName());
			n.node.setX(n.proposedLocation.x, SWLEditorConstants.SWIMMING_LANE_EDITOR);
			n.node.setY(n.proposedLocation.y, SWLEditorConstants.SWIMMING_LANE_EDITOR);
		}
	}

	private static final double MARGIN = 30;
	private static final double SPACE_IN_SAME_LANE = 45;
	private static final double SPACE_WHEN_CHANGING_LANE = 30;
	private static final int SWL_HEIGHT = 80;

	private double maxWidth = 1000 - 3 * MARGIN;

	private void layoutPath(AutoLayoutNodePath path, FlexoProgress progress) {
		SwimmingLane previousLane = null;
		AbstractNode previousNode = null;
		boolean isMainPath = (path == getMainPath());
		double x;

		if (isMainPath) {
			x = MARGIN;
		} else {
			if (path.startPath != null) {
				AutoLayoutNode firstElement = path.firstElement();
				x = firstElement.proposedLocation.x + SPACE_WHEN_CHANGING_LANE;
			} else if (path.endPath != null) {
				double requiredWidth = 0;
				for (AutoLayoutNode n : path) {
					if (n != path.lastElement()) {
						requiredWidth += n.node.getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR) + SPACE_WHEN_CHANGING_LANE;
					}
				}
				AutoLayoutNode lastElement = path.lastElement();
				x = lastElement.proposedLocation.x - requiredWidth;
			} else {
				x = MARGIN;
			}
		}
		if (progress != null)
			progress.resetSecondaryProgress(path.size());
		for (AutoLayoutNode n : path) {
			if (progress != null)
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("laying_out") + " " + n.node.getName());
			if (!isMainPath) {
				if (path.startPath != null && n == path.firstElement()) {
					// In this case, this is a secondary path attached to an other path
					// And this is the first node, which location is then already determined
					// System.out.println("Forget about "+n+" for secondary path "+path);
					continue;
				}
				if (path.endPath != null && n == path.lastElement()) {
					// In this case, this is a secondary path attached to an other path
					// And this is the last node, which location is then already determined
					// System.out.println("Forget about "+n+" for secondary path "+path);
					continue;
				}
			}

			Role role = bestRepresentationRole(n.node);

			SwimmingPool pool = generalLayout.get(role);
			if (pool == null) {
				pool = new SwimmingPool(role);
				generalLayout.put(role, pool);
			}
			SwimmingLane lane = pool.lanes.get(path);
			if (lane == null) {
				lane = new SwimmingLane(path, pool.lanes.size());
				pool.lanes.put(path, lane);
			}
			lane.nodes.add(n);
			double height = n.node.getHeight(SWLEditorConstants.SWIMMING_LANE_EDITOR);
			if (height + 2 * MARGIN > pool.maxLaneHeight) {
				pool.maxLaneHeight = height + 2 * MARGIN;
			}
			if (previousLane != null && lane != previousLane && (n != path.lastElement())) {
				x += SPACE_WHEN_CHANGING_LANE;
				if (lane.nodes.size() > 1) {
					AutoLayoutNode previousNodeInLane = lane.nodes.get(lane.nodes.size() - 2);
					x = previousNodeInLane.proposedLocation.x + previousNodeInLane.node.getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR)
							+ SPACE_IN_SAME_LANE;
				}
			} else if (previousNode != null) {
				x = x + previousNode.getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR) + SPACE_IN_SAME_LANE;
			}

			n.proposedLocation = new FGEPoint(x, lane.index * SWL_HEIGHT);

			previousLane = lane;
			previousNode = n.node;
		}
		if (x + previousNode.getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR) + MARGIN > maxWidth)
			maxWidth = x + previousNode.getWidth(SWLEditorConstants.SWIMMING_LANE_EDITOR) + MARGIN;
	}

	class SwimmingPool {
		Role role;
		Hashtable<AutoLayoutNodePath, SwimmingLane> lanes;
		double maxLaneHeight = SWL_HEIGHT;

		public SwimmingPool(Role aRole) {
			role = aRole;
			lanes = new Hashtable<AutoLayoutNodePath, SwimmingLane>();
		}
	}

	class SwimmingLane {
		int index;
		AutoLayoutNodePath path;
		Vector<AutoLayoutNode> nodes;

		SwimmingLane(AutoLayoutNodePath aPath, int anIndex) {
			index = anIndex;
			path = aPath;
			nodes = new Vector<AutoLayoutNode>();
		}
	}
}
