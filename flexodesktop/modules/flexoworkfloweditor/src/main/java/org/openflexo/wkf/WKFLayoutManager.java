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
package org.openflexo.wkf;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.localization.FlexoLocalization;

public abstract class WKFLayoutManager {

	private static final Logger logger = Logger.getLogger(WKFLayoutManager.class.getPackage().getName());

	protected Hashtable<PetriGraphNode, AutoLayoutNode> nodeMap;

	private FlexoProcess _process;

	private AutoLayoutNodePath mainPath;
	private Vector<AutoLayoutNodePath> secondaryPaths;
	private Vector<AutoLayoutNode> isolatedNodes;
	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> loopingEdges;
	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> shortcutEdges;
	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> uncoveredEdges;

	public WKFLayoutManager(FlexoProcess process) {
		_process = process;
		nodeMap = new Hashtable<PetriGraphNode, AutoLayoutNode>();
		isolatedNodes = new Vector<AutoLayoutNode>();
		secondaryPaths = new Vector<AutoLayoutNodePath>();
	}

	public abstract void layoutProcess(FlexoProgress progress);

	public FlexoProcess getProcess() {
		return _process;
	}

	public void recomputeProcessStructure(FlexoProgress progress) {
		buildNodeMap(progress);
		buildPaths(progress);

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Best path = " + mainPath);
			logger.fine("Secondary paths: ");
			for (AutoLayoutNodePath p : secondaryPaths) {
				System.out.println(p.toString());
			}
			logger.fine("Isolated nodes: " + isolatedNodes.size() + " : " + isolatedNodes);
			logger.fine("Looping edges: " + loopingEdges.size());
			for (FlexoPostCondition<?, ?> post : loopingEdges) {
				logger.fine("Looping edge: " + post);
			}
			logger.fine("Shortcut edges: " + shortcutEdges.size());
			for (FlexoPostCondition<?, ?> post : shortcutEdges) {
				logger.fine("Shortcut edge: " + post);
			}
			logger.fine("Uncovered edges: " + uncoveredEdges.size());
			for (FlexoPostCondition<?, ?> post : uncoveredEdges) {
				logger.fine("Uncovered edge: " + post);
			}
		}
	}

	private void buildNodeMap(FlexoProgress progress) {
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("building_node_map"));
		}
		nodeMap.clear();
		for (PetriGraphNode n : getProcess().getActivityPetriGraph().getNodes()) {
			AutoLayoutNode aln = new AutoLayoutNode(n);
			nodeMap.put(n, aln);
		}
		if (progress != null) {
			progress.resetSecondaryProgress(nodeMap.size());
		}
		for (PetriGraphNode n : nodeMap.keySet()) {
			AutoLayoutNode aln = nodeMap.get(n);
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("current_node") + " " + aln.node.getName());
			}
			aln.addEdges(n);
			if (n instanceof FatherNode && ((FatherNode) n).hasContainedPetriGraph()) {
				for (FlexoNode end : ((FatherNode) n).getContainedPetriGraph().getAllEndNodes()) {
					aln.addEdges(end);
				}
			}
			if (n instanceof SelfExecutableNode && ((SelfExecutableNode) n).hasExecutionPetriGraph()) {
				for (FlexoNode end : ((SelfExecutableNode) n).getExecutionPetriGraph().getAllEndNodes()) {
					aln.addEdges(end);
				}
			}
		}
	}

	private void buildPaths(FlexoProgress progress) {
		mainPath = findMainPath(progress);
		if (mainPath == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No main path found!");
			}
			return;
		}
		buildSecondaryPaths(progress);
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("finding_loops_and_shortcuts"));
		}
		findLoopingAndShortcutEdges();
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("finding_uncovered_edges"));
		}
		uncoveredEdges = findUncoveredEdges();
	}

	private void buildSecondaryPaths(FlexoProgress progress) {
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("building_secondary_path"));
		}
		isolatedNodes.clear();
		Vector<AutoLayoutNode> forgottenNodes = new Vector<AutoLayoutNode>();
		for (AutoLayoutNode node : nodeMap.values()) {
			if (!mainPath.contains(node)) {
				if (node.previousNodes.size() == 0 && node.followingNodes.size() == 0) {
					isolatedNodes.add(node);
				} else {
					forgottenNodes.add(node);
				}
			}
		}

		secondaryPaths.clear();
		if (progress != null) {
			progress.resetSecondaryProgress(forgottenNodes.size());
		}
		while (forgottenNodes.size() > 0) {

			Vector<AutoLayoutNodePath> potentialSubPaths = _buildAllSubPathForForForgottenNodes(forgottenNodes, mainPath, secondaryPaths);

			AutoLayoutNodePath bestSecondaryPath = potentialSubPaths.firstElement();
			bestSecondaryPath.name = "secondary" + secondaryPaths.size();
			secondaryPaths.add(bestSecondaryPath);
			for (AutoLayoutNode node : bestSecondaryPath) {
				if (forgottenNodes.contains(node)) {
					if (progress != null) {
						progress.setSecondaryProgress(FlexoLocalization.localizedForKey("from_node") + " "
								+ bestSecondaryPath.firstElement().node.getName());
					}
					forgottenNodes.remove(node);
				}
			}
		}

	}

	private Vector<AutoLayoutNodePath> _buildAllSubPathForForForgottenNodes(Vector<AutoLayoutNode> forgottenNodes,
			AutoLayoutNodePath mainPath, Vector<AutoLayoutNodePath> secondaryPaths) {
		Vector<AutoLayoutNodePath> returned = new Vector<AutoLayoutNodePath>();
		AutoLayoutNodePath alreadyVisited = new AutoLayoutNodePath(mainPath);
		for (AutoLayoutNodePath subPath : secondaryPaths) {
			alreadyVisited.addAll(subPath);
		}
		for (AutoLayoutNode node : forgottenNodes) {
			Vector<AutoLayoutNodePath> potentialSubPaths = _buildPathsFrom(node, alreadyVisited.clone(), false);
			for (AutoLayoutNodePath p : potentialSubPaths) {
				if (p.firstElement().previousNodes.size() == 0) {
					// This subpath has no begin
					p.startPath = null;
				} else {
					for (AutoLayoutNode n : p.firstElement().previousNodes.values()) {
						if (mainPath.contains(n)) { // Attach this subpath to first found node in main path
							p.insertElementAt(n, 0);
							p.startPath = mainPath;
							break;
						}
					}
					if (p.startPath == null) {
						for (AutoLayoutNode n : p.firstElement().previousNodes.values()) {
							for (AutoLayoutNodePath secondaryPath : secondaryPaths) {
								if (secondaryPath.contains(n)) { // Attach this subpath to first found node in secondary path
									p.insertElementAt(n, 0);
									p.startPath = secondaryPath;
									break;
								}
							}
							if (p.startPath != null) {
								break;
							}
						}
					}
				}
				if (p.lastElement().followingNodes.size() == 0) {
					// This subpath has no end
					p.endPath = null;
				} else {
					for (AutoLayoutNode n : p.lastElement().followingNodes.values()) {
						if (mainPath.contains(n)) { // Attach this subpath to first found node in main path
							p.add(n);
							p.endPath = mainPath;
							break;
						}
					}
					if (p.endPath == null) {
						for (AutoLayoutNode n : p.lastElement().followingNodes.values()) {
							for (AutoLayoutNodePath secondaryPath : secondaryPaths) {
								if (secondaryPath.contains(n)) { // Attach this subpath to first found node in secondary path
									p.add(n);
									p.endPath = secondaryPath;
									break;
								}
							}
							if (p.endPath != null) {
								break;
							}
						}
					}
				}
				returned.add(p);
			}
		}

		Collections.sort(returned, new Comparator<AutoLayoutNodePath>() {
			@Override
			public int compare(AutoLayoutNodePath p1, AutoLayoutNodePath p2) {
				if (p1.size() != p2.size()) {
					return p2.size() - p1.size();
				}
				int lp1 = (p1.startPath != null ? 1 : 0) + (p1.endPath != null ? 1 : 0);
				int lp2 = (p2.startPath != null ? 1 : 0) + (p2.endPath != null ? 1 : 0);
				if (lp1 != lp2) {
					return lp2 - lp1;
				}
				if (lp1 == 2) {
					if (p1.startPath == p1.endPath) {
						return -1;
					} else if (p2.startPath == p2.endPath) {
						return 1;
					}
				}
				return -1;
			}
		});

		/*for (AutoLayoutNodePath p : returned) {
			System.out.println("potential path : "+p
					+" start="+(p.startPath !=null ? p.startPath.name : "null")
					+" end="+(p.endPath != null ? p.endPath.name : "null"));
		}*/

		return returned;
	}

	private AutoLayoutNodePath findMainPath(FlexoProgress progress) {
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("building_main_path"));
		}
		AutoLayoutNodePath returned = null;
		int bestLength = 0;
		Vector<PetriGraphNode> allBeginNodes = new Vector<PetriGraphNode>(getProcess().getActivityPetriGraph().getAllBeginNodes());
		allBeginNodes.addAll(getProcess().getActivityPetriGraph().getAllStartNodes());
		if (allBeginNodes.size() == 0) {
			findBestBeginNodes(allBeginNodes, 0);
		}
		for (AbstractNode n : allBeginNodes) {
			AutoLayoutNode beginNode = nodeMap.get(n);
			for (AutoLayoutNodePath p : _buildPathsFrom(beginNode)) {
				// System.out.println("Path: "+p);
				if (p.size() > bestLength) {
					returned = p;
					bestLength = p.size();
				}
			}
		}
		if (returned != null) {
			returned.name = "main";
		}
		return returned;
	}

	/**
	 *
	 */
	private void findBestBeginNodes(Vector<PetriGraphNode> allBeginNodes, int maxIncomingEdges) {
		for (PetriGraphNode node : getProcess().getActivityPetriGraph().getNodes()) {
			int count = 0;
			if (node instanceof FlexoNode) {
				for (FlexoPreCondition pc : ((FlexoNode) node).getPreConditions()) {
					count += getIncomingNonMessageEdgeCount(pc);
				}
			}
			count += getIncomingNonMessageEdgeCount(node);

			if (count <= maxIncomingEdges) {
				allBeginNodes.add(node);
			}
		}
		if (allBeginNodes.size() == 0 && maxIncomingEdges < 10) {
			findBestBeginNodes(allBeginNodes, maxIncomingEdges + 1);
		}
	}

	/**
	 * @param allBeginNodes
	 * @param maxIncomingEdges
	 * @param node
	 */
	private int getIncomingNonMessageEdgeCount(AbstractNode node) {
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> ipc = node.getIncomingPostConditions();

		int count = 0;
		for (FlexoPostCondition<?, ?> postCondition : ipc) {
			if (postCondition instanceof MessageEdge) {
				continue;
			}
			count++;
		}
		return count;
	}

	private Vector<AutoLayoutNodePath> _buildPathsFrom(AutoLayoutNode beginNode) {
		return _buildPathsFrom(beginNode, new AutoLayoutNodePath(), true);
	}

	private Vector<AutoLayoutNodePath> _buildPathsFrom(AutoLayoutNode node, AutoLayoutNodePath alreadyVisited, boolean onlyTerminals) {
		alreadyVisited.add(node);
		Vector<AutoLayoutNodePath> returned = new Vector<AutoLayoutNodePath>();
		boolean atLeastAPathWasFound = false;
		for (FlexoPostCondition<AbstractNode, AbstractNode> post : node.followingNodes.keySet()) {
			AutoLayoutNode n = node.followingNodes.get(post);
			if (!alreadyVisited.contains(n)) {
				Vector<AutoLayoutNodePath> subPaths = _buildPathsFrom(n, alreadyVisited.clone(), onlyTerminals);
				for (AutoLayoutNodePath path : subPaths) {
					if (path.isTerminal() || !onlyTerminals) {
						returned.add(new AutoLayoutNodePath(node, path));
						atLeastAPathWasFound = true;
					} else {
						// System.out.println("Found a loop because of link :"+post);
					}
				}
			}
		}
		if (!atLeastAPathWasFound) {
			returned.add(new AutoLayoutNodePath(node));
		}
		return returned;
	}

	private Vector<FlexoPostCondition<AbstractNode, AbstractNode>> findUncoveredEdges() {
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> returned = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		for (AutoLayoutNode n1 : nodeMap.values()) {
			for (FlexoPostCondition<AbstractNode, AbstractNode> post : n1.followingNodes.keySet()) {
				AutoLayoutNode n2 = n1.followingNodes.get(post);
				// Is there a path containing n1 AND n2 ????
				boolean isContained = false;
				if (mainPath.contains(n1) && mainPath.contains(n2)) {
					isContained = true;
				}
				if (!isContained) {
					for (AutoLayoutNodePath secondaryPath : secondaryPaths) {
						if (secondaryPath.contains(n1) && secondaryPath.contains(n2)) {
							isContained = true;
							break;
						}
					}
				}
				if (!isContained && !returned.contains(post)) {
					returned.add(post);
				}
			}
		}
		return returned;
	}

	private void findLoopingAndShortcutEdges() {
		loopingEdges = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		shortcutEdges = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		_findLoopingAndShortcutEdges(mainPath, loopingEdges, shortcutEdges);
		for (AutoLayoutNodePath secondaryPath : secondaryPaths) {
			_findLoopingAndShortcutEdges(secondaryPath, loopingEdges, shortcutEdges);
		}
	}

	private void _findLoopingAndShortcutEdges(AutoLayoutNodePath path,
			Vector<FlexoPostCondition<AbstractNode, AbstractNode>> loopingEdgesVector,
			Vector<FlexoPostCondition<AbstractNode, AbstractNode>> shortcutEdgesVector) {
		/*boolean debug = false;
		if (path.name.equals("secondary0")) {
			System.out.println("On debuggue looping edges pour "+path);
			debug = true;
		}*/
		AutoLayoutNodePath currentPath = new AutoLayoutNodePath();
		for (AutoLayoutNode node : path) {
			currentPath.add(node);
			// if (debug) System.out.println("********* currentPath="+currentPath);
			for (FlexoPostCondition<AbstractNode, AbstractNode> post : node.followingNodes.keySet()) {
				AutoLayoutNode n = node.followingNodes.get(post);
				// if (debug) System.out.println("------ post="+node+" to "+n);
				if (!currentPath.contains(n)) {
					if (path.contains(n) && !(path.indexOf(n) == path.indexOf(node) + 1)) {
						// This is a shortcut to a node after current one
						if (!shortcutEdgesVector.contains(post)) {
							shortcutEdgesVector.add(post);
						}
					} else {
						Vector<AutoLayoutNodePath> subPaths = _buildPathsFrom(n, currentPath.clone(), false);
						for (AutoLayoutNodePath subPath : subPaths) {
							// if (debug) System.out.println("subPath="+subPath+" terminal="+subPath.isTerminal());
							if (!subPath.isTerminal()) {
								// Explicit loop: inter-path looping edge
								FlexoPostCondition<AbstractNode, AbstractNode> lastPost = subPath.getLastPostcondition();
								if (logger.isLoggable(Level.FINE)) {
									logger.fine("Found a loop because of subPath :" + subPath + " post=" + lastPost);
								}
								if (lastPost != null && !loopingEdgesVector.contains(lastPost)) {
									loopingEdgesVector.add(lastPost);
								}
							}
						}
					}
				} else { // Explicit loop: intra-path looping edge
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Found a loop because of post :" + post);
					}
					if (!loopingEdgesVector.contains(post)) {
						loopingEdgesVector.add(post);
					}
				}
			}
		}
		// if (debug)System.out.println("FIN debug looping edges pour "+path);
	}

	public class AutoLayoutNode {
		public PetriGraphNode node;
		public Hashtable<FlexoPostCondition<AbstractNode, AbstractNode>, AutoLayoutNode> previousNodes;
		public Hashtable<FlexoPostCondition<AbstractNode, AbstractNode>, AutoLayoutNode> followingNodes;
		public FGEPoint proposedLocation = new FGEPoint(0, 0);

		AutoLayoutNode(PetriGraphNode node) {
			super();
			this.node = node;
			followingNodes = new Hashtable<FlexoPostCondition<AbstractNode, AbstractNode>, AutoLayoutNode>();
			previousNodes = new Hashtable<FlexoPostCondition<AbstractNode, AbstractNode>, AutoLayoutNode>();
		}

		void addEdges(AbstractNode p) {
			for (FlexoPostCondition<AbstractNode, AbstractNode> post : p.getOutgoingPostConditions()) {
				AbstractNode next = post.getNextNode();
				if (!nodeMap.containsKey(next)) {
					if (next instanceof FlexoPortMap) {
						next = ((FlexoPortMap) next).getSubProcessNode();
					}
				}
				if (next != null && nodeMap.containsKey(next)) {
					followingNodes.put(post, nodeMap.get(next));
					nodeMap.get(next).previousNodes.put(post, this);
				}
			}
		}

		void debugNode() {
			System.out.println("\nNode: " + node);
			for (AutoLayoutNode aln : followingNodes.values()) {
				System.out.println("> " + aln.node.getName());
			}
			for (AutoLayoutNode aln : previousNodes.values()) {
				System.out.println("< " + aln.node.getName());
			}
		}

		public int accessibilityFrom(AutoLayoutNode aln) {
			int returned = accessibilityFrom(aln, new Vector<AutoLayoutNode>(), 1);
			// if (returned != -1) System.out.println(" // "+aln.node.getName() + " < "+node.getName()+ "["+returned+"]");
			return returned;
		}

		private int accessibilityFrom(AutoLayoutNode aln, Vector<AutoLayoutNode> allVisitedNodes, int level) {
			// System.out.println(" ???? "+aln.node.getName() + " < "+node.getName());
			allVisitedNodes.add(aln);
			Vector<AutoLayoutNode> notVisitedNodes = new Vector<AutoLayoutNode>();
			for (AutoLayoutNode next : aln.followingNodes.values()) {
				if (next == this) {
					return level;
				}
				if (!allVisitedNodes.contains(next)) {
					notVisitedNodes.add(next);
				}
			}
			int best = Integer.MAX_VALUE;
			for (AutoLayoutNode testThis : notVisitedNodes) {
				Vector<AutoLayoutNode> visitedNodes = new Vector<AutoLayoutNode>();
				visitedNodes.addAll(allVisitedNodes);
				int a = accessibilityFrom(testThis, visitedNodes, level + 1);
				if (a > 0 && a < best) {
					best = a;
				}
			}
			if (best == Integer.MAX_VALUE) {
				// System.out.println(" Cannot compare "+aln.node.getName() + " and "+node.getName());
				return -1;
			}
			// System.out.println(" // "+aln.node.getName() + " < "+node.getName()+ "["+best+"]");
			return best;
		}

		@Override
		public String toString() {
			return "ALN:" + node.getName();
		}

	}

	public class AutoLayoutNodePath extends Vector<AutoLayoutNode> {
		public String name;
		public AutoLayoutNodePath startPath;
		public AutoLayoutNodePath endPath;

		public AutoLayoutNodePath() {
			super();
		}

		public AutoLayoutNodePath(AutoLayoutNode... nodes) {
			super();
			for (AutoLayoutNode n : nodes) {
				add(n);
			}
		}

		public AutoLayoutNodePath(Vector<AutoLayoutNode> nodes) {
			super();
			addAll(nodes);
		}

		public AutoLayoutNodePath(AutoLayoutNode node, Vector<AutoLayoutNode> nodes) {
			super();
			add(node);
			addAll(nodes);
		}

		@Override
		public synchronized AutoLayoutNodePath clone() {
			return (AutoLayoutNodePath) super.clone();
		}

		public boolean isTerminal() {
			return lastElement().followingNodes.size() == 0;
		}

		public FlexoPostCondition<AbstractNode, AbstractNode> getLastPostcondition() {
			if (size() > 1) {
				AutoLayoutNode last = lastElement();
				AutoLayoutNode previous = get(size() - 2);
				for (FlexoPostCondition<AbstractNode, AbstractNode> post : previous.followingNodes.keySet()) {
					if (previous.followingNodes.get(post) == last) {
						return post;
					}
				}
			}
			return null;
		}

		@Override
		public synchronized String toString() {
			return name + ": " + super.toString() + " start_from:" + (startPath != null ? startPath.name : null) + " ends_to:"
					+ (endPath != null ? endPath.name : null);
		}
	}

	public static class AutoLayoutNodeComparator implements Comparator<AutoLayoutNode> {
		@Override
		public int compare(AutoLayoutNode n1, AutoLayoutNode n2) throws NotComparableException {
			int a1 = n1.accessibilityFrom(n2);
			int a2 = n2.accessibilityFrom(n1);
			if (a1 > -1) {
				if (a2 == -1) {
					return 1;
				} else {
					throw new InterdependantNodesException();
				}
			} else {
				if (a2 > -1) {
					return -1;
				} else {
					throw new IndependantNodesException();
				}
			}
		}

		static abstract class NotComparableException extends RuntimeException {
		};

		static class InterdependantNodesException extends NotComparableException {
		};

		static class IndependantNodesException extends NotComparableException {
		};
	}

	public Vector<AutoLayoutNode> getIsolatedNodes() {
		return isolatedNodes;
	}

	public Vector<FlexoPostCondition<AbstractNode, AbstractNode>> getLoopingEdges() {
		return loopingEdges;
	}

	public AutoLayoutNodePath getMainPath() {
		return mainPath;
	}

	public Vector<AutoLayoutNodePath> getSecondaryPaths() {
		return secondaryPaths;
	}

}