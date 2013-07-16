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
package org.openflexo.wkf.processeditor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.WKFDataSource;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFMessageArtifact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WKFStockObject;
import org.openflexo.foundation.wkf.dm.ArtefactInserted;
import org.openflexo.foundation.wkf.dm.ArtefactRemoved;
import org.openflexo.foundation.wkf.dm.AssociationInserted;
import org.openflexo.foundation.wkf.dm.GroupInserted;
import org.openflexo.foundation.wkf.dm.GroupRemoved;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.ObjectAlignementChanged;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenOpened;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.edge.WKFAssociation;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.ComplexOperator;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.InclusiveOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.module.UserType;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.gr.AbstractActionNodeGR;
import org.openflexo.wkf.processeditor.gr.AbstractActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.AbstractOperationNodeGR;
import org.openflexo.wkf.processeditor.gr.ActionNodeGR;
import org.openflexo.wkf.processeditor.gr.ActionPetriGraphGR;
import org.openflexo.wkf.processeditor.gr.ActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.ActivityPetriGraphGR;
import org.openflexo.wkf.processeditor.gr.AnnotationGR;
import org.openflexo.wkf.processeditor.gr.AssociationGR;
import org.openflexo.wkf.processeditor.gr.BeginActionNodeGR;
import org.openflexo.wkf.processeditor.gr.BeginActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.BeginOperationNodeGR;
import org.openflexo.wkf.processeditor.gr.CollabsedActivityGroupGR;
import org.openflexo.wkf.processeditor.gr.ContainerGR;
import org.openflexo.wkf.processeditor.gr.DataObjectGR;
import org.openflexo.wkf.processeditor.gr.DataSourceGR;
import org.openflexo.wkf.processeditor.gr.EdgeGR;
import org.openflexo.wkf.processeditor.gr.EndActionNodeGR;
import org.openflexo.wkf.processeditor.gr.EndActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.EndOperationNodeGR;
import org.openflexo.wkf.processeditor.gr.EventNodeGR;
import org.openflexo.wkf.processeditor.gr.ExpandedActivityGroupGR;
import org.openflexo.wkf.processeditor.gr.ExpanderGR;
import org.openflexo.wkf.processeditor.gr.ExpanderGR.Expander;
import org.openflexo.wkf.processeditor.gr.MessageEdgeGR;
import org.openflexo.wkf.processeditor.gr.MessageGR;
import org.openflexo.wkf.processeditor.gr.NormalAbstractActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.OperationNodeGR;
import org.openflexo.wkf.processeditor.gr.OperationPetriGraphGR;
import org.openflexo.wkf.processeditor.gr.OperatorANDGR;
import org.openflexo.wkf.processeditor.gr.OperatorComplexGR;
import org.openflexo.wkf.processeditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.processeditor.gr.OperatorGR;
import org.openflexo.wkf.processeditor.gr.OperatorIFGR;
import org.openflexo.wkf.processeditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.processeditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.processeditor.gr.OperatorORGR;
import org.openflexo.wkf.processeditor.gr.PortGR;
import org.openflexo.wkf.processeditor.gr.PortRegisteryGR;
import org.openflexo.wkf.processeditor.gr.PortmapGR;
import org.openflexo.wkf.processeditor.gr.PortmapRegisteryGR;
import org.openflexo.wkf.processeditor.gr.PreAndBeginNodeAssociationGR;
import org.openflexo.wkf.processeditor.gr.PreAndBeginNodeAssociationGR.PreAndBeginNodeAssociation;
import org.openflexo.wkf.processeditor.gr.PreConditionGR;
import org.openflexo.wkf.processeditor.gr.SelfExecActionNodeGR;
import org.openflexo.wkf.processeditor.gr.SelfExecActivityNodeGR;
import org.openflexo.wkf.processeditor.gr.SelfExecOperationNodeGR;
import org.openflexo.wkf.processeditor.gr.StockObjectGR;
import org.openflexo.wkf.processeditor.gr.SubProcessNodeGR;
import org.openflexo.wkf.processeditor.gr.TokenEdgeGR;
import org.openflexo.wkf.processeditor.gr.WKFObjectGR;

public class ProcessRepresentation extends DrawingImpl<FlexoProcess> implements GraphicalFlexoObserver, ProcessEditorConstants {

	private static final Logger logger = Logger.getLogger(ProcessRepresentation.class.getPackage().getName());

	public static interface ProcessRepresentationObjectVisibilityDelegate {

		public boolean isVisible(WKFObject object);

		public WKFObject getFirstVisibleObject(WKFObject targetObject);

	}

	public static class ProcessRepresentationDefaultVisibilityDelegate implements ProcessRepresentationObjectVisibilityDelegate {

		@Override
		public boolean isVisible(WKFObject targetObject) {
			if (targetObject == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Null object are not visible");
				}
				return false;
			}
			if (targetObject instanceof FlexoProcess) {
				return true;
			} else if (targetObject instanceof FlexoPetriGraph) {
				return (!UserType.isLite() || ((FlexoPetriGraph) targetObject).isRootPetriGraph())
						&& ((FlexoPetriGraph) targetObject).getIsVisible() && isVisible(((FlexoPetriGraph) targetObject).getContainer());
			} else if (targetObject instanceof PortRegistery) {
				return ((PortRegistery) targetObject).getIsVisible();
			} else if (targetObject instanceof WKFEdge) {
				WKFEdge post = (WKFEdge) targetObject;
				WKFObject firstVisibleStartObject = getFirstVisibleObject(post.getStartNode());
				WKFObject firstVisibleEndObject = getFirstVisibleObject(post.getEndNode());
				if (post instanceof MessageEdge) {
					if (firstVisibleStartObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleStartObject).getSubProcessNode() == firstVisibleEndObject) {
							return false;
						}
					} else if (firstVisibleEndObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleEndObject).getSubProcessNode() == firstVisibleStartObject) {
							return false;
						}
					}
				}
				if (post instanceof FlexoPostCondition<?, ?>) {
					if (((FlexoPostCondition<?, ?>) post).hideWhenInduced()
							&& (post.getStartNode() != firstVisibleStartObject || post.getEndNode() != firstVisibleEndObject)) {
						return false;
					}
				}
				return !(firstVisibleStartObject != post.getStartNode() && firstVisibleEndObject != post.getEndNode() && firstVisibleStartObject == firstVisibleEndObject)
						&& firstVisibleStartObject != null && firstVisibleEndObject != null;
			} else if (targetObject instanceof WKFArtefact) {
				return isVisible(((WKFArtefact) targetObject).getParentPetriGraph());
			} else if (targetObject instanceof WKFGroup) {
				return ((WKFGroup) targetObject).getIsVisible();
			} else if (targetObject instanceof PortMapRegistery) {
				return !((PortMapRegistery) targetObject).getIsHidden() && ((PortMapRegistery) targetObject).getPortMaps().size() > 0;
			} else if (targetObject instanceof FlexoPortMap) {
				return !((FlexoPortMap) targetObject).getIsHidden();
			}
			return getFirstVisibleObject(targetObject) == targetObject;
		}

		@Override
		public final WKFObject getFirstVisibleObject(WKFObject targetObject) {
			if (targetObject == null) {
				return null;
			}
			AbstractNode concernedNode = null;
			if (targetObject instanceof FlexoPreCondition) {
				concernedNode = ((FlexoPreCondition) targetObject).getAttachedNode();
				targetObject = concernedNode;
			} else if (targetObject instanceof AbstractNode) {
				concernedNode = (AbstractNode) targetObject;
			} else if (targetObject instanceof WKFArtefact) {
				WKFArtefact artefact = (WKFArtefact) targetObject;
				if (artefact.getParentPetriGraph() == null) {
					return null;
				}
				if (isVisible(artefact.getParentPetriGraph())) {
					return targetObject;
				} else {
					// Test if PetriGraphContainer is visible
					WKFObject container = artefact.getParentPetriGraph().getContainer();
					// Otherwise, do it recursively
					return getFirstVisibleObject(container);

					// If container visible itsef, return it
					// if (isVisible(container))
					// return container;
					// Otherwise, don't go 2 levels
					// return null;
				}
			} else {
				logger.warning("Unexpected: " + targetObject);
				return targetObject;
			}

			if (concernedNode instanceof FlexoPort) {
				if (((FlexoPort) concernedNode).getPortRegistery() == null) {
					return null;
				}
				// GPO: Shouldn't we pass the FlexoPort (and the FlexoPort would have then to check that its parent portRegistery is shown)?
				if (isVisible(((FlexoPort) concernedNode).getPortRegistery())) {
					return targetObject;
				}
				return null;
			}

			if (concernedNode instanceof FlexoPortMap) {
				if (isVisible(((FlexoPortMap) concernedNode).getSubProcessNode())) {
					if (isVisible(concernedNode) && isVisible(((FlexoPortMap) concernedNode).getPortMapRegistery())) {
						return targetObject;
					}
					return ((FlexoPortMap) concernedNode).getSubProcessNode();
				} else {
					return getFirstVisibleObject(((FlexoPortMap) concernedNode).getSubProcessNode());
				}
			}
			if (!(concernedNode instanceof PetriGraphNode)) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("concerned node is not a petri graph node: " + concernedNode);
				}
				return null;
			}
			PetriGraphNode node = (PetriGraphNode) concernedNode;

			if (node.getParentPetriGraph() == null) {
				return null;
			}
			if (node.isGrouped()) {
				WKFGroup group = node.getContainerGroup();
				if (isVisible(group)) {
					return targetObject;
				} else {
					return group;
				}
			}

			if (isVisible(node.getParentPetriGraph())) {
				return targetObject;
			} else {
				// Test if PetriGraphContainer is visible
				WKFObject container = node.getParentPetriGraph().getContainer();
				// Otherwise, do it recursively
				return getFirstVisibleObject(container);

				// If container visible itsef, return it
				// if (isVisible(container))
				// return container;
				// Otherwise, don't go 2 levels
				// return null;
			}
		}

	}

	public static class ProcessRepresentationShowAllObjectsDelegate extends ProcessRepresentationDefaultVisibilityDelegate {
		@Override
		public boolean isVisible(WKFObject targetObject) {
			return true;
		}
	}

	public static class ProcessRepresentationShowTopLevelDelegate extends ProcessRepresentationDefaultVisibilityDelegate {
		@Override
		public boolean isVisible(WKFObject targetObject) {
			if (targetObject == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Null object are not visible");
				}
				return false;
			}
			if (targetObject instanceof FlexoProcess) {
				return true;
			} else if (targetObject instanceof FlexoPreCondition) {
				return isVisible(((FlexoPreCondition) targetObject).getAttachedNode());
			} else if (targetObject instanceof ActivityPetriGraph) {
				return !(((ActivityPetriGraph) targetObject).getContainer() instanceof ActivityPetriGraph);
			} else if (targetObject instanceof PortRegistery) {
				return false;
			} else if (targetObject instanceof EventNode) {
				return isVisible(((EventNode) targetObject).getParentPetriGraph());
			} else if (targetObject instanceof FlexoNode) {
				return isVisible(((FlexoNode) targetObject).getParentPetriGraph());
			} else if (targetObject instanceof WKFEdge) {
				WKFEdge post = (WKFEdge) targetObject;
				WKFObject firstVisibleStartObject = getFirstVisibleObject(post.getStartNode());
				WKFObject firstVisibleEndObject = getFirstVisibleObject(post.getEndNode());

				if (post instanceof MessageEdge) {
					if (firstVisibleStartObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleStartObject).getSubProcessNode() == firstVisibleEndObject) {
							return false;
						}
					} else if (firstVisibleEndObject instanceof FlexoPortMap) {
						if (((FlexoPortMap) firstVisibleEndObject).getSubProcessNode() == firstVisibleStartObject) {
							return false;
						}
					}
				}
				if (post instanceof FlexoPostCondition<?, ?>) {
					if (((FlexoPostCondition<?, ?>) post).hideWhenInduced()
							&& (post.getStartNode() != firstVisibleStartObject || post.getEndNode() != firstVisibleEndObject)) {
						return false;
					}
				}
				return !(firstVisibleStartObject != post.getStartNode() && firstVisibleEndObject != post.getEndNode() && firstVisibleStartObject == firstVisibleEndObject)
						&& firstVisibleStartObject != null && firstVisibleEndObject != null;
			} else if (targetObject instanceof WKFArtefact) {
				return isVisible(((WKFArtefact) targetObject).getParentPetriGraph());
			} else if (targetObject instanceof OperatorNode) {
				return isVisible(((OperatorNode) targetObject).getParentPetriGraph());
			} else if (targetObject instanceof WKFGroup) {
				return ((WKFGroup) targetObject).getIsVisible();
			} else if (targetObject instanceof PortMapRegistery) {
				return false;
			} else if (targetObject instanceof FlexoPortMap) {
				return false;
			}
			return false;
		}

	}

	public static final ProcessRepresentationObjectVisibilityDelegate DEFAULT_VISIBILITY = new ProcessRepresentationDefaultVisibilityDelegate();
	public static final ProcessRepresentationObjectVisibilityDelegate SHOW_ALL = new ProcessRepresentationShowAllObjectsDelegate();
	public static final ProcessRepresentationObjectVisibilityDelegate SHOW_TOP_LEVEL = new ProcessRepresentationShowTopLevelDelegate();

	private final ProcessGraphicalRepresentation graphicalRepresentation;
	private ProcessRepresentationObjectVisibilityDelegate visibilityDelegate;
	private final WKFController controller;

	public ProcessRepresentation(final FlexoProcess process, WKFController controller) {
		this(process, controller, false);
	}

	/**
	 * If openAllNodes is set to true, all nodes are directly open at creation. Used by the html doc generation
	 */
	public ProcessRepresentation(final FlexoProcess process, WKFController controller, boolean visibilityDelegate2) {
		this(process, controller, visibilityDelegate2 ? SHOW_ALL : DEFAULT_VISIBILITY);
	}

	public ProcessRepresentation(final FlexoProcess process, WKFController controller,
			ProcessRepresentationObjectVisibilityDelegate visibilityDelegate) {
		super(process);
		this.controller = controller;

		if (visibilityDelegate != null) {
			this.visibilityDelegate = visibilityDelegate;
		} else {
			this.visibilityDelegate = DEFAULT_VISIBILITY;
		}

		graphicalRepresentation = new ProcessGraphicalRepresentation(this, process);
		graphicalRepresentation.addToMouseClickControls(new ProcessEditorController.ShowContextualMenuControl());
		process.getWorkflow().addObserver(this);
		process.addObserver(this);
		process.getActivityPetriGraph().addObserver(this);

		updateGraphicalObjectsHierarchy();
	}

	@Override
	public void delete() {
		if (getFlexoProcess() != null) {
			getFlexoProcess().getWorkflow().deleteObserver(this);
			getFlexoProcess().deleteObserver(this);
			getFlexoProcess().getActivityPetriGraph().deleteObserver(this);
		}
		super.delete();
	}

	private void addGroup(WKFGroup group, WKFObject container) {
		addDrawable(group, container);

		if (isVisible(group)) {
			for (AbstractNode node : group.getNodes()) {
				addNode(node, group);
			}
		}
	}

	private void addNode(AbstractNode node, WKFObject container) {
		addDrawable(node, container);
		if (node instanceof FlexoNode) {
			/*for (FlexoPreCondition pre : ((FlexoNode) node).getPreConditions()) {
				addDrawable(pre, node);
				if (pre.getAttachedBeginNode() != null && pre.getAttachedBeginNode().getParentPetriGraph() != null
						&& isVisible(pre.getAttachedBeginNode().getParentPetriGraph())) {
					addDrawable(preAndBeginNodeAssociationForPrecondition(pre), getProcess());
				}
			}*/
		}

		if (node instanceof AbstractActivityNode) {
			for (EventNode pre : ((AbstractActivityNode) node).getAllBoundaryEvents()) {
				addDrawable(pre, node);
			}
		}

		/*if (node instanceof EdgeStarting) {
			for (FlexoPostCondition post : ((EdgeStarting)node).getOutgoingPostConditions()) {
				addDrawable(post,getProcess());
			}
		}*/
		if (node instanceof SubProcessNode) {
			SubProcessNode subProcessNode = (SubProcessNode) node;
			if (subProcessNode.getPortMapRegistery() != null) {
				addDrawable(subProcessNode.getPortMapRegistery(), subProcessNode);
				for (FlexoPortMap portmap : subProcessNode.getPortMapRegistery().getPortMaps()) {
					addDrawable(portmap, subProcessNode.getPortMapRegistery());
				}
			}
		}

		if (node instanceof AbstractActivityNode) {
			AbstractActivityNode activity = (AbstractActivityNode) node;
			if (activity.hasContainedPetriGraph() && isVisible(activity.getOperationPetriGraph())) {
				addPetriGraph(activity, activity.getOperationPetriGraph());
			}
		}

		if (node instanceof OperationNode) {
			OperationNode operation = (OperationNode) node;
			if (operation.hasContainedPetriGraph() && isVisible(operation.getActionPetriGraph())) {
				addPetriGraph(operation, operation.getActionPetriGraph());
			}
		}

		if (node instanceof SelfExecutableNode) {
			SelfExecutableNode selfExecNode = (SelfExecutableNode) node;
			if (selfExecNode.hasExecutionPetriGraph() && isVisible(selfExecNode.getExecutionPetriGraph())) {
				addPetriGraph((AbstractNode) selfExecNode, selfExecNode.getExecutionPetriGraph());
			}
		}

		if (node instanceof LOOPOperator) {
			LOOPOperator loopOperator = (LOOPOperator) node;
			if (loopOperator.hasExecutionPetriGraph() && isVisible(loopOperator.getExecutionPetriGraph())) {
				addPetriGraph(loopOperator, loopOperator.getExecutionPetriGraph());
			}
		}
	}

	private void addPetriGraph(AbstractNode father, FlexoPetriGraph pg) {
		addDrawable(pg, getProcess());
		addDrawable(expanderForNodeAndPG(father, pg), getProcess());
		for (PetriGraphNode n : pg.getNodes()) {
			if (!n.isGrouped()) {
				addNode(n, pg);
			}
		}
		for (WKFArtefact annotation : pg.getArtefacts()) {
			addDrawable(annotation, pg);
		}
		for (WKFGroup group : pg.getGroups()) {
			addGroup(group, pg);
		}
	}

	@Override
	public <O> void addDrawable(O aDrawable, Object aParentDrawable) {
		// logger.info("Adding "+aDrawable+" under "+aParentDrawable);
		super.addDrawable(aDrawable, aParentDrawable);
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {

		for (WKFGroup group : getProcess().getActivityPetriGraph().getGroups()) {
			addGroup(group, getProcess());
		}

		for (PetriGraphNode node : getProcess().getActivityPetriGraph().getNodes()) {
			if (!node.isGrouped()) {
				if (!(node instanceof EventNode && ((EventNode) node).getBoundaryOf() != null)) {
					addNode(node, getProcess());
				}
			}
		}

		if (isVisible(getProcess().getPortRegistery())) {
			addDrawable(getProcess().getPortRegistery(), getProcess());
			for (FlexoPort port : getProcess().getPortRegistery().getAllPorts()) {
				addNode(port, getProcess().getPortRegistery());
			}
		}

		/*for (AbstractActivityNode activity : getProcess().getAllActivities()) {
			if (activity.hasContainedPetriGraph() && activity.getContainedPetriGraph().getIsVisible()) {
				addDrawable(activity.getContainedPetriGraph(), getProcess());
				addDrawable(expanderForNodeAndPG(activity,activity.getOperationPetriGraph()), getProcess());
				for (PetriGraphNode n : activity.getContainedPetriGraph().getNodes()) {
					addNode(n,activity.getContainedPetriGraph());
				}
				for (WKFAnnotation annotation : activity.getContainedPetriGraph().getAnnotations()) {
					addDrawable(annotation, activity.getContainedPetriGraph());
				}
				for (OperationNode operation : activity.getAllOperationNodes()) {
					if (operation.hasContainedPetriGraph() && operation.getContainedPetriGraph().getIsVisible()) {
						addDrawable(operation.getContainedPetriGraph(), getProcess());
						addDrawable(expanderForNodeAndPG(operation,operation.getActionPetriGraph()), getProcess());
						for (PetriGraphNode n : operation.getContainedPetriGraph().getNodes()) {
							addNode(n,operation.getContainedPetriGraph());
						}
						for (WKFAnnotation annotation : operation.getContainedPetriGraph().getAnnotations()) {
							addDrawable(annotation, operation.getContainedPetriGraph());
						}
					}
				}
			}
		}*/

		for (WKFArtefact artefact : getProcess().getActivityPetriGraph().getArtefacts()) {
			addDrawable(artefact, getProcess());
		}

		for (FlexoPostCondition post : getProcess().getAllPostConditions()) {
			if (isVisible(post)) {
				if (post.isEdgeDisplayable()) {
					// ConnectorGraphicalRepresentation postGR = (ConnectorGraphicalRepresentation)getGraphicalRepresentation(post);
					// GraphicalRepresentation commonAncestor =
					// GraphicalRepresentation.getFirstCommonAncestor(postGR.getStartObject(),postGR.getEndObject());
					// System.out.println("Common ancestor: "+commonAncestor.getDrawable());
					// addDrawable(post,commonAncestor.getDrawable());
					addDrawable(post, getProcess());
				} else {
					System.err.println(post + " is not displayable");
				}
			}
		}

		for (WKFAssociation post : getProcess().getAllAssociations()) {
			if (isVisible(post) && post.isEdgeDisplayable()) {
				addDrawable(post, getProcess());
			}
		}
	}

	public boolean isVisible(WKFObject object) {
		return visibilityDelegate.isVisible(object);
	}

	public WKFObject getFirstVisibleObject(WKFObject object) {
		// logger.info("getFirstVisibleObject() for "+object+" is "+visibilityDelegate.getFirstVisibleObject(object));
		return visibilityDelegate.getFirstVisibleObject(object);
	}

	private final Hashtable<AbstractNode, Hashtable<FlexoPetriGraph, Expander<?>>> expanders = new Hashtable<AbstractNode, Hashtable<FlexoPetriGraph, Expander<?>>>();

	@SuppressWarnings("unchecked")
	protected <N extends AbstractNode> Expander<?> expanderForNodeAndPG(N node, FlexoPetriGraph pg) {
		Hashtable<FlexoPetriGraph, Expander<?>> expandersForNode = expanders.get(node);
		if (expandersForNode == null) {
			expandersForNode = new Hashtable<FlexoPetriGraph, Expander<?>>();
			expanders.put(node, expandersForNode);
		}
		Expander<N> returned = (Expander<N>) expandersForNode.get(pg);
		if (returned == null) {
			returned = new ExpanderGR.Expander<N>(node, pg);
			expandersForNode.put(pg, returned);
		}
		return returned;
	}

	private final Hashtable<FlexoPreCondition, PreAndBeginNodeAssociation> preAndBeginNodeAssociationForPrecondition = new Hashtable<FlexoPreCondition, PreAndBeginNodeAssociation>();

	protected PreAndBeginNodeAssociation preAndBeginNodeAssociationForPrecondition(FlexoPreCondition pre) {
		PreAndBeginNodeAssociation returned = preAndBeginNodeAssociationForPrecondition.get(pre);
		if (returned == null) {
			returned = new PreAndBeginNodeAssociation(pre);
			preAndBeginNodeAssociationForPrecondition.put(pre, returned);
		}
		return returned;
	}

	public FlexoProcess getFlexoProcess() {
		return getModel();
	}

	public FlexoProcess getProcess() {
		return getFlexoProcess();
	}

	@Override
	public ProcessGraphicalRepresentation getDrawingGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation retrieveGraphicalRepresentation(O aDrawable) {
		return (GraphicalRepresentation) buildGraphicalRepresentation(aDrawable);
	}

	private GraphicalRepresentation buildGraphicalRepresentation(Object aDrawable) {
		if (aDrawable instanceof SelfExecutableActivityNode) {
			return new SelfExecActivityNodeGR((SelfExecutableActivityNode) aDrawable, this, false);
		}
		if (aDrawable instanceof ActivityNode) {
			if (((ActivityNode) aDrawable).isBeginNode()) {
				return new BeginActivityNodeGR((ActivityNode) aDrawable, this, false);
			}
			if (((ActivityNode) aDrawable).isEndNode()) {
				return new EndActivityNodeGR((ActivityNode) aDrawable, this, false);
			}
			return new ActivityNodeGR((ActivityNode) aDrawable, this, false);
		}
		if (aDrawable instanceof SubProcessNode) {
			return new SubProcessNodeGR((SubProcessNode) aDrawable, this, false);
		}
		if (aDrawable instanceof IFOperator) {
			return new OperatorIFGR((IFOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof InclusiveOperator) {
			return new OperatorInclusiveGR((InclusiveOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof ExclusiveEventBasedOperator) {
			return new OperatorExclusiveEventBasedGR((ExclusiveEventBasedOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof ComplexOperator) {
			return new OperatorComplexGR((ComplexOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof IFOperator) {
			return new OperatorIFGR((IFOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof ANDOperator) {
			return new OperatorANDGR((ANDOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof OROperator) {
			return new OperatorORGR((OROperator) aDrawable, this, false);
		}
		if (aDrawable instanceof LOOPOperator) {
			return new OperatorLOOPGR((LOOPOperator) aDrawable, this, false);
		}
		if (aDrawable instanceof FlexoPort) {
			return new PortGR((FlexoPort) aDrawable, this);
		}
		if (aDrawable instanceof FlexoPortMap) {
			return new PortmapGR((FlexoPortMap) aDrawable, this);
		}
		if (aDrawable instanceof PortRegistery) {
			return new PortRegisteryGR((PortRegistery) aDrawable, this);
		}
		if (aDrawable instanceof PortMapRegistery) {
			return new PortmapRegisteryGR((PortMapRegistery) aDrawable, this);
		}
		if (aDrawable instanceof ActivityGroup) {
			ActivityGroup group = (ActivityGroup) aDrawable;
			if (isVisible(group)) {
				return new ExpandedActivityGroupGR(group, this);
			} else {
				return new CollabsedActivityGroupGR(group, this);
			}
		}
		if (aDrawable instanceof ActivityPetriGraph) {
			return new ActivityPetriGraphGR((ActivityPetriGraph) aDrawable, this);
		}
		if (aDrawable instanceof OperationPetriGraph) {
			return new OperationPetriGraphGR((OperationPetriGraph) aDrawable, this);
		}
		if (aDrawable instanceof ActionPetriGraph) {
			return new ActionPetriGraphGR((ActionPetriGraph) aDrawable, this);
		}
		if (aDrawable instanceof SelfExecutableOperationNode) {
			return new SelfExecOperationNodeGR((SelfExecutableOperationNode) aDrawable, this, false);
		}
		if (aDrawable instanceof OperationNode) {
			if (((OperationNode) aDrawable).isBeginNode()) {
				return new BeginOperationNodeGR((OperationNode) aDrawable, this, false);
			}
			if (((OperationNode) aDrawable).isEndNode()) {
				return new EndOperationNodeGR((OperationNode) aDrawable, this, false);
			}
			return new OperationNodeGR((OperationNode) aDrawable, this, false);
		}
		if (aDrawable instanceof SelfExecutableActionNode) {
			return new SelfExecActionNodeGR((SelfExecutableActionNode) aDrawable, this, false);
		}
		if (aDrawable instanceof ActionNode) {
			if (((ActionNode) aDrawable).isBeginNode()) {
				return new BeginActionNodeGR((ActionNode) aDrawable, this, false);
			}
			if (((ActionNode) aDrawable).isEndNode()) {
				return new EndActionNodeGR((ActionNode) aDrawable, this, false);
			}
			return new ActionNodeGR((ActionNode) aDrawable, this, false);
		}
		if (aDrawable instanceof EventNode) {
			return new EventNodeGR((EventNode) aDrawable, this);
		}
		if (aDrawable instanceof TokenEdge) {
			return new TokenEdgeGR((TokenEdge) aDrawable, this);
		}
		if (aDrawable instanceof MessageEdge) {
			return new MessageEdgeGR((MessageEdge) aDrawable, this);
		}
		if (aDrawable instanceof WKFAssociation) {
			return new AssociationGR((WKFAssociation) aDrawable, this);
		}
		if (aDrawable instanceof FlexoPreCondition) {
			return new PreConditionGR((FlexoPreCondition) aDrawable, this);
		}
		if (aDrawable instanceof Expander) {
			return new ExpanderGR((Expander<? extends FatherNode>) aDrawable, this);
		}
		if (aDrawable instanceof PreAndBeginNodeAssociation) {
			return new PreAndBeginNodeAssociationGR((PreAndBeginNodeAssociation) aDrawable, this);
		}
		if (aDrawable instanceof WKFAnnotation) {
			return new AnnotationGR((WKFAnnotation) aDrawable, this);
		}
		if (aDrawable instanceof WKFDataSource) {
			return new DataSourceGR((WKFDataSource) aDrawable, this);
		}
		if (aDrawable instanceof WKFDataObject) {
			return new DataObjectGR((WKFDataObject) aDrawable, this);
		}
		if (aDrawable instanceof WKFStockObject) {
			return new StockObjectGR((WKFStockObject) aDrawable, this);
		}
		if (aDrawable instanceof WKFMessageArtifact) {
			return new MessageGR((WKFMessageArtifact) aDrawable, this);
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getFlexoProcess() || observable == getFlexoProcess().getActivityPetriGraph()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof NodeInserted || dataModification instanceof NodeRemoved
					|| dataModification instanceof ArtefactInserted || dataModification instanceof ArtefactRemoved
					|| dataModification instanceof PostInserted || dataModification instanceof AssociationInserted) {
				updateGraphicalObjectsHierarchy();
			}
			if (dataModification instanceof GroupInserted) {
				/*WKFGroup group = ((GroupInserted)dataModification).newValue();
				for (AbstractNode node : group.getNodes()) {
					invalidateGraphicalObjectsHierarchy(node);
				}*/
				// We have here to invalidate all, since edges can also reference disappeared GR
				// Better is to redraw all
				invalidateGraphicalObjectsHierarchy(getProcess());
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof GroupRemoved) {
				WKFGroup group = ((GroupRemoved) dataModification).oldValue();
				Vector<PetriGraphNode> nodesThatWereInGroup = ((GroupRemoved) dataModification).getNodesThatWereInGroup();
				if (nodesThatWereInGroup != null) {
					for (AbstractNode node : nodesThatWereInGroup) {
						invalidateGraphicalObjectsHierarchy(node);
						node.setX(node.getX(BASIC_PROCESS_EDITOR) + group.getX(BASIC_PROCESS_EDITOR), BASIC_PROCESS_EDITOR);
						node.setY(node.getY(BASIC_PROCESS_EDITOR) + group.getY(BASIC_PROCESS_EDITOR), BASIC_PROCESS_EDITOR);
					}
				}
				invalidateGraphicalObjectsHierarchy(getProcess());
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PortRegisteryHasBeenOpened) {
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof PortRegisteryHasBeenClosed) {
				updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ObjectSizeChanged) {
				graphicalRepresentation.updateAlignOnGridOrGridSize();
				graphicalRepresentation.notifyObjectResized(null);
			} else if (dataModification instanceof ObjectAlignementChanged) {
				graphicalRepresentation.updateAlignOnGridOrGridSize();
				getDrawingGraphicalRepresentation().notifyDrawingNeedsToBeRedrawn();
			}
		} else if (observable == getFlexoProcess().getWorkflow()) {
			if (FlexoWorkflow.GraphicalProperties.SHOW_SHADOWS.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof WKFObjectGR<?>) {
						((WKFObjectGR<?>) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.USE_TRANSPARENCY.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof ContainerGR<?>) {
						((ContainerGR<?>) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.SHOW_WO_NAME.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof OperationNodeGR) {
						((OperationNodeGR) gr).notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ACTIVITY_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof AbstractActivityNodeGR<?> || gr instanceof CollabsedActivityGroupGR || gr instanceof PortGR) {
						((WKFObjectGR<?>) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.OPERATION_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof AbstractOperationNodeGR) {
						((AbstractOperationNodeGR) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ACTION_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof AbstractActionNodeGR) {
						((AbstractActionNodeGR) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.EVENT_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof EventNodeGR || gr instanceof OperatorGR<?>) {
						((WKFObjectGR<?>) gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ROLE_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof NormalAbstractActivityNodeGR<?>) {
						NormalAbstractActivityNodeGR<?> activityGR = (NormalAbstractActivityNodeGR<?>) gr;
						activityGR.updatePropertiesFromWKFPreferences();
						activityGR.notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.COMPONENT_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof OperationNodeGR) {
						OperationNodeGR operationGR = (OperationNodeGR) gr;
						operationGR.updatePropertiesFromWKFPreferences();
						operationGR.notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName().equals(
					dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation gr = en.nextElement();
					if (gr instanceof EdgeGR<?>) {
						((EdgeGR<?>) gr).updatePropertiesFromWKFPreferences();
					}
				}
			}
		}
	}

	public FlexoEditor getEditor() {
		return controller.getEditor();
	}

}
