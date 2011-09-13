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

import java.awt.dnd.InvalidDnDOperationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import sun.awt.dnd.SunDragSourceContextPeer;

import org.openflexo.components.ProgressWindow;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFAnnotation;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.WKFDataSource;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WKFStockObject;
import org.openflexo.foundation.wkf.dm.ArtefactInserted;
import org.openflexo.foundation.wkf.dm.ArtefactRemoved;
import org.openflexo.foundation.wkf.dm.AssociationInserted;
import org.openflexo.foundation.wkf.dm.NodeInserted;
import org.openflexo.foundation.wkf.dm.NodeRemoved;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenClosed;
import org.openflexo.foundation.wkf.dm.PortRegisteryHasBeenOpened;
import org.openflexo.foundation.wkf.dm.PostInserted;
import org.openflexo.foundation.wkf.dm.RoleInserted;
import org.openflexo.foundation.wkf.dm.RoleRemoved;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.InternalMessageEdge;
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
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.swleditor.gr.AbstractActionNodeGR;
import org.openflexo.wkf.swleditor.gr.AbstractActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.AbstractOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.ActionNodeGR;
import org.openflexo.wkf.swleditor.gr.ActionPetriGraphGR;
import org.openflexo.wkf.swleditor.gr.ActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.ActivityPetriGraphGR;
import org.openflexo.wkf.swleditor.gr.AnnotationGR;
import org.openflexo.wkf.swleditor.gr.AssociationGR;
import org.openflexo.wkf.swleditor.gr.BeginActionNodeGR;
import org.openflexo.wkf.swleditor.gr.BeginActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.BeginOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.ContainerGR;
import org.openflexo.wkf.swleditor.gr.DataObjectGR;
import org.openflexo.wkf.swleditor.gr.DataSourceGR;
import org.openflexo.wkf.swleditor.gr.EdgeGR;
import org.openflexo.wkf.swleditor.gr.EndActionNodeGR;
import org.openflexo.wkf.swleditor.gr.EndActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.EndOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.EventNodeGR;
import org.openflexo.wkf.swleditor.gr.ExpanderGR;
import org.openflexo.wkf.swleditor.gr.MessageEdgeGR;
import org.openflexo.wkf.swleditor.gr.NormalAbstractActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.OperationNodeGR;
import org.openflexo.wkf.swleditor.gr.OperationPetriGraphGR;
import org.openflexo.wkf.swleditor.gr.OperatorANDGR;
import org.openflexo.wkf.swleditor.gr.OperatorComplexGR;
import org.openflexo.wkf.swleditor.gr.OperatorExclusiveEventBasedGR;
import org.openflexo.wkf.swleditor.gr.OperatorGR;
import org.openflexo.wkf.swleditor.gr.OperatorIFGR;
import org.openflexo.wkf.swleditor.gr.OperatorInclusiveGR;
import org.openflexo.wkf.swleditor.gr.OperatorLOOPGR;
import org.openflexo.wkf.swleditor.gr.OperatorORGR;
import org.openflexo.wkf.swleditor.gr.PortGR;
import org.openflexo.wkf.swleditor.gr.PortRegisteryGR;
import org.openflexo.wkf.swleditor.gr.PortmapGR;
import org.openflexo.wkf.swleditor.gr.PortmapRegisteryGR;
import org.openflexo.wkf.swleditor.gr.PreAndBeginNodeAssociationGR;
import org.openflexo.wkf.swleditor.gr.PreConditionGR;
import org.openflexo.wkf.swleditor.gr.RoleContainerGR;
import org.openflexo.wkf.swleditor.gr.SelfExecActionNodeGR;
import org.openflexo.wkf.swleditor.gr.SelfExecActivityNodeGR;
import org.openflexo.wkf.swleditor.gr.SelfExecOperationNodeGR;
import org.openflexo.wkf.swleditor.gr.StockObjectGR;
import org.openflexo.wkf.swleditor.gr.SubProcessNodeGR;
import org.openflexo.wkf.swleditor.gr.TokenEdgeGR;
import org.openflexo.wkf.swleditor.gr.WKFObjectGR;
import org.openflexo.wkf.swleditor.gr.ExpanderGR.Expander;
import org.openflexo.wkf.swleditor.gr.PreAndBeginNodeAssociationGR.PreAndBeginNodeAssociation;

public class SwimmingLaneRepresentation extends DefaultDrawing<FlexoProcess>
implements GraphicalFlexoObserver, SWLEditorConstants {

	protected static final Logger logger = Logger.getLogger(SwimmingLaneRepresentation.class.getPackage().getName());

	private FlexoEditor editor;
	private DrawingGraphicalRepresentation<FlexoProcess> graphicalRepresentation;

	private SwimmingLaneRepresentationObjectVisibilityDelegate visibilityDelegate;

	public static interface SwimmingLaneRepresentationObjectVisibilityDelegate {

		public boolean isVisible(WKFObject object);
		public WKFObject getFirstVisibleObject(WKFObject targetObject);

	}

	public static class SwimmingLaneRepresentationDefaultVisibilityDelegate implements SwimmingLaneRepresentationObjectVisibilityDelegate {

		@Override
		public boolean isVisible(WKFObject targetObject)
		{
			if (targetObject == null) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Null object are not visible");
				return false;
			}
			if (targetObject instanceof FlexoProcess) {
				return true;
			} else if (targetObject instanceof PetriGraphNode && ((PetriGraphNode)targetObject).getParentPetriGraph()==targetObject.getProcess().getActivityPetriGraph()) {
				return true;
			} else if (targetObject instanceof FlexoPetriGraph) {
				return ((FlexoPetriGraph) targetObject).getIsVisible() && isVisible(((FlexoPetriGraph) targetObject).getContainer());
			} else if (targetObject instanceof PortRegistery) {
				return ((PortRegistery)targetObject).getIsVisible();
			} else if (targetObject instanceof WKFEdge) {
				WKFEdge<?, ?> post = (WKFEdge<?, ?>) targetObject;
				WKFObject firstVisibleStartObject = getFirstVisibleObject(post.getStartNode());
				WKFObject firstVisibleEndObject = getFirstVisibleObject(post.getEndNode());
				if (post instanceof MessageEdge) {
					if (firstVisibleStartObject instanceof FlexoPortMap) {
						if (((FlexoPortMap)firstVisibleStartObject).getSubProcessNode()==firstVisibleEndObject)
							return false;
					} else if (firstVisibleEndObject instanceof FlexoPortMap) {
						if (((FlexoPortMap)firstVisibleEndObject).getSubProcessNode()==firstVisibleStartObject)
							return false;
					}
				}
				if (post instanceof FlexoPostCondition<?, ?>) {
					if (((FlexoPostCondition<?, ?>)post).hideWhenInduced() && (post.getStartNode()!=firstVisibleStartObject||post.getEndNode()!=firstVisibleEndObject)) {
						return false;
					}
				}
				return ((!(firstVisibleStartObject != post.getStartNode() && firstVisibleEndObject != post.getEndNode() && firstVisibleStartObject == firstVisibleEndObject))
						&& firstVisibleStartObject != null
						&& firstVisibleEndObject != null);
			} else if (targetObject instanceof WKFGroup) {
				return ((WKFGroup)targetObject).getIsVisible();
			} else if (targetObject instanceof PortMapRegistery) {
				return !((PortMapRegistery)targetObject).getIsHidden() && ((PortMapRegistery)targetObject).getPortMaps().size() > 0;
			} else if (targetObject instanceof FlexoPortMap) {
				return !((FlexoPortMap)targetObject).getIsHidden();
			} else if (targetObject instanceof WKFArtefact) {
				return isVisible(((WKFArtefact)targetObject).getParentPetriGraph());
			}
			return getFirstVisibleObject(targetObject) == targetObject;
		}

		@Override
		public final WKFObject getFirstVisibleObject(WKFObject targetObject)
		{
			if (targetObject==null)
				return null;
			AbstractNode concernedNode = null;
			if (targetObject instanceof FlexoPreCondition) {
				concernedNode = ((FlexoPreCondition) targetObject).getAttachedNode();
			} else if (targetObject instanceof AbstractNode) {
				concernedNode = (AbstractNode) targetObject;
			} else if (targetObject instanceof WKFArtefact) {
				WKFArtefact artefact = (WKFArtefact) targetObject;
				if (artefact.getParentPetriGraph()==null)
					return null;
				if (isVisible(artefact.getParentPetriGraph())) {
					return targetObject;
				}
				else {
					// Test if PetriGraphContainer is visible
					WKFObject container = artefact.getParentPetriGraph().getContainer();
					// Otherwise, do it recursively
					return getFirstVisibleObject(container);

					// If container visible itsef, return it
					//if (isVisible(container))
					//	return container;
					// Otherwise, don't go 2 levels
					//return null;
				}
			} else {
				logger.warning("Unexpected: " + targetObject);
				return targetObject;
			}

			if (concernedNode instanceof FlexoPort) {
				if (((FlexoPort)concernedNode).getPortRegistery() == null) return null;
				//GPO: Shouldn't we pass the FlexoPort (and the FlexoPort would have then to check that its parent portRegistery is shown)?
				if (isVisible(((FlexoPort)concernedNode).getPortRegistery())) return targetObject;
				return null;
			}

			if (concernedNode instanceof FlexoPortMap) {
				if (isVisible(((FlexoPortMap)concernedNode).getSubProcessNode())) {
					if (isVisible(concernedNode) && isVisible(((FlexoPortMap)concernedNode).getPortMapRegistery()))
						return targetObject;
					return ((FlexoPortMap)concernedNode).getSubProcessNode();
				}
				else
					return getFirstVisibleObject(((FlexoPortMap)concernedNode).getSubProcessNode());
			}

			if (!(concernedNode instanceof PetriGraphNode)) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("concerned node is not a petri graph node: "+concernedNode);
				return null;
			}
			PetriGraphNode node = (PetriGraphNode)concernedNode;

			if (node.getParentPetriGraph() == null)
				return null;

			if (isVisible(node.getParentPetriGraph())) {
				return targetObject;
			}
			else {
				// Test if PetriGraphContainer is visible
				WKFObject container = node.getParentPetriGraph().getContainer();
				// Otherwise, do it recursively
				return getFirstVisibleObject(container);

				// If container visible itsef, return it
				//if (isVisible(container))
				//	return container;
				// Otherwise, don't go 2 levels
				//return null;
			}
		}

	}

	public static class SwimmingLaneRepresentationShowAllObjectsDelegate extends SwimmingLaneRepresentationDefaultVisibilityDelegate {
		@Override
		public boolean isVisible(WKFObject object) {
			if (object instanceof InternalMessageEdge) {
				return isVisible(((InternalMessageEdge<?, ?>)object).getFlexoPort().getPortRegistery());
			}
			if (object instanceof PortRegistery) {
				return ((PortRegistery)object).getIsVisible();
			}
			return true;
		}
	}

	/**
	 * @return
	 */
	public static String getRoleVisibilityContextForProcess(FlexoProcess process) {
		return SWLEditorConstants.SWIMMING_LANE_EDITOR+"_"+process.getFlexoID();
	}

	/**
	 * @return
	 */
	public static String getRoleIndexContextedParameterForProcess(FlexoProcess process) {
		return SWLEditorConstants.SWIMMING_LANE_INDEX_KEY+"_"+process.getFlexoID();
	}

	/**
	 * @return
	 */
	public static String getRoleNumberOfLaneContextedParameterForProcess(FlexoProcess process) {
		return SWLEditorConstants.SWIMMING_LANE_NB_KEY+"_"+process.getFlexoID();
	}

	/**
	 * @return
	 */
	public static String getRoleLaneHeightContextedParameterForProcess(FlexoProcess process) {
		return SWLEditorConstants.SWIMMING_LANE_HEIGHT_KEY+"_"+process.getFlexoID();
	}

	/**
	 * @param role
	 * @return
	 */
	public static boolean roleMustBeShown(Role role, FlexoProcess process) {
		return role.isUsedInPetriGraphNodes(process.getActivityPetriGraph().getNodes());
	}

	public static final SwimmingLaneRepresentationObjectVisibilityDelegate DEFAULT_VISIBILITY = new SwimmingLaneRepresentationDefaultVisibilityDelegate();
	public static final SwimmingLaneRepresentationObjectVisibilityDelegate SHOW_ALL = new SwimmingLaneRepresentationShowAllObjectsDelegate();

	public SwimmingLaneRepresentation(FlexoProcess process, FlexoEditor anEditor) {
		this(process,anEditor,DEFAULT_VISIBILITY);
	}

	public SwimmingLaneRepresentation(FlexoProcess process, FlexoEditor anEditor, SwimmingLaneRepresentationObjectVisibilityDelegate visibilityDelegate)
	{
		super(process);
		this.editor = anEditor;
		this.visibilityDelegate = visibilityDelegate!=null?visibilityDelegate:DEFAULT_VISIBILITY;

		graphicalRepresentation = new SwimmingLaneGraphicalRepresentation(this, process);

		/*graphicalRepresentation = new DrawingGraphicalRepresentation<FlexoProcess>(this);
		graphicalRepresentation.setWidth(DEFAULT_SWIMMING_LANE_WIDTH+2*SWIMMING_LANE_BORDER);
		graphicalRepresentation.setHeight(
				process.getWorkflow().getRoleList().getRoles().size()*(DEFAULT_SWIMMING_LANE_HEIGHT+SWIMMING_LANE_BORDER)
				+PORT_REGISTERY_HEIGHT
				+2*SWIMMING_LANE_BORDER);*/

		graphicalRepresentation.addToMouseClickControls(new SwimmingLaneEditorController.ShowContextualMenuControl());

		if (!hasProcessBeenLaidOut())
			performAutoLayout();
		process.addObserver(this);
		process.getActivityPetriGraph().addObserver(this);
		process.getWorkflow().addObserver(this);
		process.getWorkflow().getRoleList().addObserver(this);
		for(Role role:process.getWorkflow().getRoleList().getRoles())
			role.addObserver(this);

		updateGraphicalObjectsHierarchy();
	}

	/**
	 * @param process
	 * @return
	 */
	public boolean hasProcessBeenLaidOut() {
		return getProcess()._booleanGraphicalPropertyForKey(SWLEditorConstants.SWL_AUTO_LAYOUT_PERFORMED_KEY, false);
	}

	protected double computeHeight()
	{
		double returned = SWIMMING_LANE_BORDER;
		for (RepresentableFlexoModelObject o : orderedMainObjects()) {
			int swlNb = getSwimmingLaneNb(o);
			int swlHeight = getSwimmingLaneHeight(o);
			returned += swlNb*swlHeight+2*SWIMMING_LANE_BORDER;
		}
		return returned;
	}

	public double yForObject(RepresentableFlexoModelObject object)
	{
		double returned = SWIMMING_LANE_BORDER;
		for (RepresentableFlexoModelObject o : orderedMainObjects()) {
			if (o == object) {
				return returned;
			}
			int swlNb = getSwimmingLaneNb(o);
			int swlHeight = getSwimmingLaneHeight(o);
			returned += swlNb*swlHeight+2*SWIMMING_LANE_BORDER;
		}
		logger.warning("Unexpected situation here");
		return returned;
	}

	private Vector<RepresentableFlexoModelObject> orderedMainObjects()
	{
		Vector<RepresentableFlexoModelObject> returned = new Vector<RepresentableFlexoModelObject>();
		if (isVisible(getProcess().getPortRegistery())) returned.add(getProcess().getPortRegistery());
		if (isVisible(getProcess().getWorkflow().getRoleList().getDefaultRole()))returned.add(getProcess().getWorkflow().getRoleList().getDefaultRole());
		for (Role role : getProcess().getWorkflow().getRoleList().getRoles()) {
			if(isVisible(role))
				returned.add(role);
		}
		if (getProcess().getWorkflow().getImportedRoleList()!=null) {
			for (Role role : getProcess().getWorkflow().getImportedRoleList().getRoles()) {
				if(isVisible(role))
					returned.add(role);
			}
		}
		int next = 0;
		for (int i=0; i<returned.size(); i++) {
			next = Math.max(next+1, returned.get(i).getIntegerParameter(SWIMMING_LANE_INDEX_KEY(), next));
		}
		Collections.sort(returned, new Comparator<RepresentableFlexoModelObject>() {
			@Override
			public int compare(RepresentableFlexoModelObject o1, RepresentableFlexoModelObject o2) {
				return  o1.getIntegerParameter(SWIMMING_LANE_INDEX_KEY())-o2.getIntegerParameter(SWIMMING_LANE_INDEX_KEY());
			}
		});
		for (int i=0; i<returned.size(); i++) {
			// Note that following is not notified (important to avoid loop)
			returned.get(i)._setGraphicalPropertyForKey(i,SWIMMING_LANE_INDEX_KEY());
		}
		return returned;
	}

	public void reindexObjectForNewVerticalLocation(RepresentableFlexoModelObject object, double y)
	{
		RepresentableFlexoModelObject after = null;
		int newIndex = 0;
		int currentIndex = -1;
		Vector<RepresentableFlexoModelObject> objects = orderedMainObjects();
		for (int i=0; i<objects.size(); i++) {
			RepresentableFlexoModelObject o = objects.get(i);
			if (o == object) currentIndex = i;
			if (o != object && y>yForObject(o) && i+1 > newIndex) {
				newIndex = i+1;
				after = o;
			}
		}
		if (newIndex > currentIndex) newIndex--;
		//System.out.println("Current index: "+currentIndex+" New index: "+newIndex+" put after "+after);
		if (newIndex != currentIndex) {
			objects.remove(currentIndex);
			objects.insertElementAt(object,newIndex);
			for (int i=0; i<objects.size(); i++) {
				// Note that following is not notified (important to avoid loop)
				objects.get(i)._setGraphicalPropertyForKey(i,SWIMMING_LANE_INDEX_KEY());
			}
		}
		object.setChanged();
	}

	public Vector<RoleContainerGR> getAllVisibleRoleContainers() {
		Vector<RoleContainerGR> returned = new Vector<RoleContainerGR>();
		for (RepresentableFlexoModelObject object : orderedMainObjects()) {
			if (object instanceof Role) {
				returned.add((RoleContainerGR)getGraphicalRepresentation((Role)object));
			}
		}
		return returned;
	}

	public void reindexForNewObjectIndex(RepresentableFlexoModelObject object)
	{
		Vector<RepresentableFlexoModelObject> objects = orderedMainObjects();
		objects.remove(object);
		objects.insertElementAt(object,Math.min(object.getIntegerParameter(SWIMMING_LANE_INDEX_KEY()),objects.size()));
		for (int i=0; i<objects.size(); i++) {
			// Note that following is not notified (important to avoid loop)
			objects.get(i)._setGraphicalPropertyForKey(i,SWIMMING_LANE_INDEX_KEY());
		}
		updateLocations();
	}

	public int getSwimmingLaneNb(RepresentableFlexoModelObject object)
	{
		return object.getIntegerParameter(SWIMMING_LANE_NB_KEY(), 1);
	}

	public void setSwimmingLaneNb(int swlNb,RepresentableFlexoModelObject object)
	{
		object.setIntegerParameter(swlNb,SWIMMING_LANE_NB_KEY());
	}

	public int getSwimmingLaneHeight(RepresentableFlexoModelObject object)
	{
		return object.getIntegerParameter(SWIMMING_LANE_HEIGHT_KEY(), DEFAULT_SWIMMING_LANE_HEIGHT);
	}

	public void setSwimmingLaneHeight(int height,RepresentableFlexoModelObject object)
	{
		object.setIntegerParameter(height,SWIMMING_LANE_HEIGHT_KEY());
	}

	public String SWIMMING_LANE_INDEX_KEY()
	{
		return getRoleIndexContextedParameterForProcess(getProcess());
	}

	public String SWIMMING_LANE_NB_KEY()
	{
		return getRoleNumberOfLaneContextedParameterForProcess(getProcess());
	}

	public String SWIMMING_LANE_HEIGHT_KEY()
	{
		return getRoleLaneHeightContextedParameterForProcess(getProcess());
	}

	private void addRole(Role role)
	{
		addDrawable(role, getProcess());
		for (AbstractNode node : getProcess().getActivityPetriGraph().getNodes()) {
			if(!(node instanceof EventNode && ((EventNode)node).getBoundaryOf()!=null)){
				Role concernedRole = getRepresentationRole(node);
				if (concernedRole == role) {
					addNodeToRole(node, role);
				}
				else if (concernedRole == null && role.isDefaultRole()) {
					addNodeToRole(node, role);
				}
			}
		}
	}

	private void addNodeToRole(AbstractNode node, Role container)
	{
		addNode(node, container);
	}

	private void addNode(AbstractNode node, FlexoModelObject container)
	{
		addDrawable(node, container);
		if (node instanceof FlexoNode) {
			for (FlexoPreCondition pre : ((FlexoNode)node).getPreConditions()) {
				addDrawable(pre, node);
				if (pre.getAttachedBeginNode() != null && isVisible(pre.getAttachedBeginNode())) {
					addDrawable(preAndBeginNodeAssociationForPrecondition(pre), getProcess());
				}
			}
		}
		/*if (node instanceof EdgeStarting) {
			for (FlexoPostCondition post : ((EdgeStarting)node).getOutgoingPostConditions()) {
				addDrawable(post,getProcess());
			}
		}*/
		if (node instanceof SubProcessNode) {
			SubProcessNode subProcessNode = (SubProcessNode)node;
			if (subProcessNode.getPortMapRegistery() != null) {
				addDrawable(subProcessNode.getPortMapRegistery(), subProcessNode);
				for (FlexoPortMap portmap : subProcessNode.getPortMapRegistery().getPortMaps()) {
					addDrawable(portmap, subProcessNode.getPortMapRegistery());
				}
			}

		}

		if (node instanceof AbstractActivityNode) {
			AbstractActivityNode activity = (AbstractActivityNode)node;
			if (activity.hasContainedPetriGraph() && isVisible(activity.getOperationPetriGraph())) {
				addPetriGraph(activity, activity.getOperationPetriGraph());
			}
			for(EventNode boundaryEvent:activity.getAllBoundaryEvents()){
				addDrawable(boundaryEvent, activity);
			}
		}

		if (node instanceof OperationNode) {
			OperationNode operation = (OperationNode)node;
			if (operation.hasContainedPetriGraph() && isVisible(operation.getActionPetriGraph())) {
				addPetriGraph(operation, operation.getActionPetriGraph());
			}
		}

		if (node instanceof SelfExecutableNode) {
			SelfExecutableNode selfExecNode = (SelfExecutableNode)node;
			if (selfExecNode.hasExecutionPetriGraph() && isVisible(selfExecNode.getExecutionPetriGraph())) {
				addPetriGraph((AbstractNode)selfExecNode, selfExecNode.getExecutionPetriGraph());
			}
		}

		if (node instanceof LOOPOperator) {
			LOOPOperator loopOperator = (LOOPOperator)node;
			if (loopOperator.hasExecutionPetriGraph() && isVisible(loopOperator.getExecutionPetriGraph())) {
				addPetriGraph(loopOperator, loopOperator.getExecutionPetriGraph());
			}
		}
	}

	private void addPetriGraph(AbstractNode father, FlexoPetriGraph pg)
	{
		addDrawable(pg, getProcess());
		addDrawable(expanderForNodeAndPG(father,pg), getProcess());
		for (AbstractNode n : pg.getNodes()) {
			addNode(n,pg);
		}
		for (WKFArtefact annotation : pg.getArtefacts()) {
			addDrawable(annotation, pg);
		}
	}


	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
		if (isVisible(getProcess().getPortRegistery())) {
			addDrawable(getProcess().getPortRegistery(), getProcess());
			for (FlexoPort port : getProcess().getPortRegistery().getAllPorts()) {
				addNode(port,getProcess().getPortRegistery());
			}
		}
		if (isVisible(getProcess().getWorkflow().getRoleList().getDefaultRole()))
			addRole(getProcess().getWorkflow().getRoleList().getDefaultRole());
		for (Role role : getProcess().getWorkflow().getRoleList().getRoles()) {
			if(isVisible(role))
				addRole(role);
		}

		if (getProcess().getWorkflow().getImportedRoleList()!=null) {
			for (Role role : getProcess().getWorkflow().getImportedRoleList().getRoles()) {
				if(isVisible(role))
					addRole(role);
			}
		}

		for (AbstractNode node : getProcess().getActivityPetriGraph().getNodes()) {
			if (node instanceof AbstractActivityNode) {
				AbstractActivityNode activity = (AbstractActivityNode) node;
				if (activity.hasContainedPetriGraph() && isVisible(activity.getContainedPetriGraph())) {
					addDrawable(activity.getContainedPetriGraph(), getProcess());
					addDrawable(expanderForNodeAndPG(activity,activity.getOperationPetriGraph()), getProcess());
					for (AbstractNode n : activity.getContainedPetriGraph().getNodes()) {
						addNode(n,activity.getContainedPetriGraph());
					}
					for (OperationNode operation : activity.getAllOperationNodes()) {
						if (operation.hasContainedPetriGraph() && isVisible(operation.getContainedPetriGraph())) {
							addDrawable(operation.getContainedPetriGraph(), getProcess());
							addDrawable(expanderForNodeAndPG(operation,operation.getActionPetriGraph()), getProcess());
							for (AbstractNode n : operation.getContainedPetriGraph().getNodes()) {
								addNode(n,operation.getContainedPetriGraph());
							}
						}
					}
				}
			}
		}
		for (WKFArtefact annotation:getProcess().getActivityPetriGraph().getArtefacts())
			if (isVisible(annotation))
				addDrawable(annotation, getProcess());
		for (FlexoPostCondition post : getProcess().getAllPostConditions()) {
			if (isVisible(post) && post.isEdgeDisplayable()) {
				addDrawable(post,getProcess());
			}
		}
		for (WKFAssociation post : getProcess().getAllAssociations()) {
			if (isVisible(post) && post.isEdgeDisplayable()) {
				addDrawable(post,getProcess());
			}
		}
		//updateDimensions();
	}

	public boolean isVisible(Role role) {
		boolean roleMustBeShown = roleMustBeShown(role,getProcess());
		if (role.isDefaultRole())
			return roleMustBeShown;
		if(role.getIsVisible(getRoleVisibilityContextForProcess(getProcess()), roleMustBeShown))
			return true;
		if (roleMustBeShown) {
			role.setIsVisible(true, getRoleVisibilityContextForProcess(getProcess()));
		}
		return roleMustBeShown;
	}

	public boolean isVisible(WKFObject object) {
		return visibilityDelegate.isVisible(object);
	}

	public WKFObject getFirstVisibleObject(WKFObject object) {
		return visibilityDelegate.getFirstVisibleObject(object);
	}

	private Hashtable<AbstractNode,Hashtable<FlexoPetriGraph,Expander<?>>> expanders = new Hashtable<AbstractNode,Hashtable<FlexoPetriGraph,Expander<?>>>();

	@SuppressWarnings("unchecked")
	protected <N extends AbstractNode> Expander<?> expanderForNodeAndPG(N node, FlexoPetriGraph pg)
	{
		Hashtable<FlexoPetriGraph,Expander<?>> expandersForNode = expanders.get(node);
		if (expandersForNode == null) {
			expandersForNode = new Hashtable<FlexoPetriGraph,Expander<?>>();
			expanders.put(node, expandersForNode);
		}
		Expander<N> returned = (Expander<N>)expandersForNode.get(pg);
		if (returned == null) {
			returned = new ExpanderGR.Expander<N>(node,pg);
			expandersForNode.put(pg,returned);
		}
		return returned;
	}

	private Hashtable<FlexoPreCondition,PreAndBeginNodeAssociation> preAndBeginNodeAssociationForPrecondition = new Hashtable<FlexoPreCondition,PreAndBeginNodeAssociation>();

	protected PreAndBeginNodeAssociation preAndBeginNodeAssociationForPrecondition(FlexoPreCondition pre)
	{
		PreAndBeginNodeAssociation returned = preAndBeginNodeAssociationForPrecondition.get(pre);
		if (returned == null) {
			returned = new PreAndBeginNodeAssociation(pre);
			preAndBeginNodeAssociationForPrecondition.put(pre, returned);
		}
		return returned;
	}


	public FlexoProcess getFlexoProcess()
	{
		return getModel();
	}

	public FlexoProcess getProcess()
	{
		return getFlexoProcess();
	}

	public RoleList getRoleList()
	{
		return getFlexoProcess().getWorkflow().getRoleList();
	}


	@Override
	public DrawingGraphicalRepresentation<FlexoProcess> getDrawingGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		return (GraphicalRepresentation<O>)buildGraphicalRepresentation(aDrawable);
	}

	private GraphicalRepresentation<?> buildGraphicalRepresentation(Object aDrawable)
	{
		if (aDrawable instanceof Role) {
			return new RoleContainerGR((Role)aDrawable,this);
		}

		if (aDrawable instanceof SelfExecutableActivityNode) {
			return new SelfExecActivityNodeGR((SelfExecutableActivityNode)aDrawable,this,false);
		}
		if (aDrawable instanceof ActivityNode) {
			if (((ActivityNode)aDrawable).isBeginNode()) return new BeginActivityNodeGR((ActivityNode)aDrawable,this,false);
			if (((ActivityNode)aDrawable).isEndNode()) return new EndActivityNodeGR((ActivityNode)aDrawable,this,false);
			return new ActivityNodeGR((ActivityNode)aDrawable,this,false);
		}
		if (aDrawable instanceof SubProcessNode) {
			return new SubProcessNodeGR((SubProcessNode)aDrawable,this,false);
		}

		if (aDrawable instanceof SelfExecutableActivityNode) {
			return new SelfExecActivityNodeGR((SelfExecutableActivityNode)aDrawable,this,false);
		}
		if (aDrawable instanceof ActivityNode) {
			if (((ActivityNode)aDrawable).isBeginNode()) return new BeginActivityNodeGR((ActivityNode)aDrawable,this,false);
			if (((ActivityNode)aDrawable).isEndNode()) return new EndActivityNodeGR((ActivityNode)aDrawable,this,false);
			return new ActivityNodeGR((ActivityNode)aDrawable,this,false);
		}
		if (aDrawable instanceof SubProcessNode) {
			return new SubProcessNodeGR((SubProcessNode)aDrawable,this,false);
		}
		if (aDrawable instanceof IFOperator) {
			return new OperatorIFGR((IFOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof ANDOperator) {
			return new OperatorANDGR((ANDOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof InclusiveOperator) {
			return new OperatorInclusiveGR((InclusiveOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof ExclusiveEventBasedOperator) {
			return new OperatorExclusiveEventBasedGR((ExclusiveEventBasedOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof ComplexOperator) {
			return new OperatorComplexGR((ComplexOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof OROperator) {
			return new OperatorORGR((OROperator)aDrawable,this,false);
		}
		if (aDrawable instanceof LOOPOperator) {
			return new OperatorLOOPGR((LOOPOperator)aDrawable,this,false);
		}
		if (aDrawable instanceof FlexoPort) {
			return new PortGR((FlexoPort)aDrawable,this);
		}
		if (aDrawable instanceof FlexoPortMap) {
			return new PortmapGR((FlexoPortMap)aDrawable,this);
		}
		if (aDrawable instanceof PortRegistery) {
			return new PortRegisteryGR((PortRegistery)aDrawable,this);
		}
		if (aDrawable instanceof PortMapRegistery) {
			return new PortmapRegisteryGR((PortMapRegistery)aDrawable,this);
		}
		if (aDrawable instanceof ActivityPetriGraph) {
			return new ActivityPetriGraphGR((ActivityPetriGraph)aDrawable,this);
		}
		if (aDrawable instanceof OperationPetriGraph) {
			return new OperationPetriGraphGR((OperationPetriGraph)aDrawable,this);
		}
		if (aDrawable instanceof ActionPetriGraph) {
			return new ActionPetriGraphGR((ActionPetriGraph)aDrawable,this);
		}
		if (aDrawable instanceof SelfExecutableOperationNode) {
			return new SelfExecOperationNodeGR((SelfExecutableOperationNode)aDrawable,this,false);
		}
		if (aDrawable instanceof OperationNode) {
			if (((OperationNode)aDrawable).isBeginNode()) return new BeginOperationNodeGR((OperationNode)aDrawable,this,false);
			if (((OperationNode)aDrawable).isEndNode()) return new EndOperationNodeGR((OperationNode)aDrawable,this,false);
			return new OperationNodeGR((OperationNode)aDrawable,this,false);
		}
		if (aDrawable instanceof SelfExecutableActionNode) {
			return new SelfExecActionNodeGR((SelfExecutableActionNode)aDrawable,this,false);
		}
		if (aDrawable instanceof ActionNode) {
			if (((ActionNode)aDrawable).isBeginNode()) return new BeginActionNodeGR((ActionNode)aDrawable,this,false);
			if (((ActionNode)aDrawable).isEndNode()) return new EndActionNodeGR((ActionNode)aDrawable,this,false);
			return new ActionNodeGR((ActionNode)aDrawable,this,false);
		}
		if (aDrawable instanceof EventNode) {
			return new EventNodeGR((EventNode)aDrawable,this);
		}
		if (aDrawable instanceof TokenEdge) {
			return new TokenEdgeGR((TokenEdge)aDrawable,this);
		}
		if (aDrawable instanceof MessageEdge) {
			return new MessageEdgeGR((MessageEdge)aDrawable,this);
		}
		if (aDrawable instanceof WKFAssociation) {
			return new AssociationGR((WKFAssociation)aDrawable,this);
		}
		if (aDrawable instanceof FlexoPreCondition) {
			return new PreConditionGR((FlexoPreCondition)aDrawable,this);
		}
		if (aDrawable instanceof Expander) {
			return new ExpanderGR((Expander<? extends FatherNode>)aDrawable,this);
		}
		if (aDrawable instanceof PreAndBeginNodeAssociation) {
			return new PreAndBeginNodeAssociationGR((PreAndBeginNodeAssociation)aDrawable,this);
		}
		if (aDrawable instanceof WKFAnnotation) {
			return new AnnotationGR((WKFAnnotation)aDrawable,this);
		}
		if (aDrawable instanceof WKFDataSource) {
			return new DataSourceGR((WKFDataSource)aDrawable,this);
		}
		if (aDrawable instanceof WKFDataObject) {
			return new DataObjectGR((WKFDataObject)aDrawable,this);
		}
		if (aDrawable instanceof WKFStockObject) {
			return new StockObjectGR((WKFStockObject)aDrawable,this);
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getFlexoProcess()
				|| observable == getFlexoProcess().getActivityPetriGraph()
				|| observable == getFlexoProcess().getWorkflow().getRoleList()) {
			if ((dataModification instanceof NodeInserted)
					|| (dataModification instanceof NodeRemoved)
					|| (dataModification instanceof ArtefactInserted)
					|| (dataModification instanceof ArtefactRemoved)
					|| dataModification instanceof AssociationInserted
					|| dataModification instanceof PostInserted) {
				updateGraphicalObjectsHierarchy();
			}
			if (dataModification instanceof RoleInserted) {
				((Role)dataModification.newValue()).addObserver(this);
				updateGraphicalObjectsHierarchy();
				updateLocations();
			}
			if (dataModification instanceof RoleRemoved) {
				((Role)dataModification.oldValue()).deleteObserver(this);
				requestRebuildCompleteHierarchy();
			}
			if (dataModification instanceof PortRegisteryHasBeenOpened) {
				updateGraphicalObjectsHierarchy();
				updateLocations();
			}
			else if (dataModification instanceof PortRegisteryHasBeenClosed) {
				updateGraphicalObjectsHierarchy();
				updateLocations();
			}
			else if (dataModification instanceof ObjectVisibilityChanged) {
				updateGraphicalObjectsHierarchy();
				updateLocations();
			}
			else if (dataModification instanceof ObjectSizeChanged) {
				updateDimensions();
			}
		} else if (observable instanceof Role) {
			if (dataModification instanceof ObjectVisibilityChanged) {
				updateGraphicalObjectsHierarchy();
				updateLocations();
			}/* else if (dataModification instanceof ActivityNodeUsingRole) {
				if(((ActivityNodeUsingRole)dataModification).getNode().getProcess()==getProcess() && getGraphicalRepresentation(((ActivityNodeUsingRole)dataModification).getRole())==null) {
					updateGraphicalObjectsHierarchy();
					updateLocations();
				}
			}*/
		} else if (observable==getFlexoProcess().getWorkflow()) {
			if (FlexoWorkflow.GraphicalProperties.SHOW_SHADOWS.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof WKFObjectGR<?>) {
						((WKFObjectGR<?>)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.USE_TRANSPARENCY.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof ContainerGR<?>) {
						((ContainerGR<?>)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.SHOW_WO_NAME.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof OperationNodeGR) {
						((OperationNodeGR)gr).notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ACTIVITY_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof AbstractActivityNodeGR<?>
						|| gr instanceof PortGR) {
						((WKFObjectGR<?>)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.OPERATION_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof AbstractOperationNodeGR) {
						((AbstractOperationNodeGR)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ACTION_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof AbstractActionNodeGR) {
						((AbstractActionNodeGR)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.EVENT_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof EventNodeGR
							|| gr instanceof OperatorGR<?>) {
						((WKFObjectGR<?>)gr).updatePropertiesFromWKFPreferences();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.ROLE_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof NormalAbstractActivityNodeGR<?>) {
						NormalAbstractActivityNodeGR<?> activityGR = (NormalAbstractActivityNodeGR<?>)gr;
						activityGR.updatePropertiesFromWKFPreferences();
						activityGR.notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.COMPONENT_FONT.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof OperationNodeGR) {
						OperationNodeGR operationGR = (OperationNodeGR)gr;
						operationGR.updatePropertiesFromWKFPreferences();
						operationGR.notifyShapeNeedsToBeRedrawn();
					}
				}
			} else if (FlexoWorkflow.GraphicalProperties.CONNECTOR_REPRESENTATION.getSerializationName().equals(dataModification.propertyName())) {
				Enumeration<GraphicalRepresentation<?>> en = getAllGraphicalRepresentations();
				while (en.hasMoreElements()) {
					GraphicalRepresentation<?> gr = en.nextElement();
					if (gr instanceof EdgeGR<?>) {
						((EdgeGR<?>)gr).updatePropertiesFromWKFPreferences();
					}
				}
			}
		}
	}

	protected boolean rebuildRequested = false;

	public synchronized void requestRebuildCompleteHierarchy() {
		if (rebuildRequested)
			return;
		rebuildRequested = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				rebuildCompleteHierarchy();
				synchronized (SunDragSourceContextPeer.class) {
					try {
						SunDragSourceContextPeer.checkDragDropInProgress();
					} catch (InvalidDnDOperationException e1) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("For some reason there was still a Dnd in progress. Will set it back to false. God knows why this happens");
						if (logger.isLoggable(Level.FINE))
							logger.log(Level.FINE,"Stacktrace for DnD still in progress",e1);
						SunDragSourceContextPeer.setDragDropInProgress(false);
					}
				}
			}
		});
	}

	protected void updateLocations()
	{
		graphicalRepresentation.notifyObjectResized(null);
		//getDrawingGraphicalRepresentation().notifyObjectHasResized();
		for (GraphicalRepresentation<?> gr : getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				((ShapeGraphicalRepresentation<?>)gr).notifyObjectHasMoved();
			}
		}
	}

	private void updateDimensions()
	{
		graphicalRepresentation.notifyObjectResized(null);
		//getDrawingGraphicalRepresentation().notifyObjectHasResized();
		for (GraphicalRepresentation<?> gr : getDrawingGraphicalRepresentation().getContainedGraphicalRepresentations()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				((ShapeGraphicalRepresentation<?>)gr).notifyObjectHasMoved();
				((ShapeGraphicalRepresentation<?>)gr).notifyObjectHasResized();
			}
		}
	}

	public FlexoEditor getEditor()
	{
		return editor;
	}

	public static final String REPRESENTATION_ROLE_KEY = "role";

	public static Role getRepresentationRole(AbstractNode activityLevelNode)
	{
		if (activityLevelNode==null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("No role for null node");
			return null;
		}
		RoleList rl = activityLevelNode.getProject().getWorkflow().getRoleList();
		Role role = null;
		if (activityLevelNode instanceof AbstractActivityNode) {
			role = ((AbstractActivityNode)activityLevelNode).getRole();
		} else if (activityLevelNode instanceof EventNode) {
			role = ((EventNode)activityLevelNode).getRole();
		} else if (activityLevelNode instanceof OperatorNode) {
			role = ((OperatorNode)activityLevelNode).getRole();
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Unexpected node type: "+activityLevelNode.getClass().getName()+" cannot get/set role!");
			role = rl.getDefaultRole();
		}
		if (role==null)
			return rl.getDefaultRole();
		return role;
	}

	public void setRepresentationRole(Role aRole, AbstractNode activityLevelNode)
	{
		//logger.info("setRepresentationRole() with "+aRole+" for "+activityLevelNode);
		
		if (aRole!=null && aRole.isDefaultRole())
			aRole = null;
		if (getRepresentationRole(activityLevelNode) != aRole) {
			if (activityLevelNode instanceof AbstractActivityNode) {
				((AbstractActivityNode)activityLevelNode).setRole(aRole);
			} else if (activityLevelNode instanceof EventNode) {
				((EventNode)activityLevelNode).setRole(aRole);
			} else if (activityLevelNode instanceof OperatorNode) {
				((OperatorNode)activityLevelNode).setRole(aRole);
			}
		}
	}

	public double getSWLWidth()
	{
		return getProcess().getWidth(SWIMMING_LANE_EDITOR,DEFAULT_WIDTH);
	}

	public void setSWLWidth(double aWidth)
	{
		getProcess().setWidth(aWidth,SWIMMING_LANE_EDITOR);
	}

	private SWLLayoutManager layoutManager;

	public SWLLayoutManager getLayoutManager()
	{
		if (layoutManager == null) {
			layoutManager = new SWLLayoutManager(this);
		}
		return layoutManager;
	}

	public void performAutoLayout()
	{
		ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("performing_auto_layout"), 9);
		try {
			getLayoutManager().layoutProcess(ProgressWindow.instance());
			getProcess()._setGraphicalPropertyForKey(Boolean.TRUE, SWLEditorConstants.SWL_AUTO_LAYOUT_PERFORMED_KEY);
		} finally {
			ProgressWindow.hideProgressWindow();
		}
	}

	/**
	 *
	 */
	private synchronized void rebuildCompleteHierarchy() {
		invalidateGraphicalObjectsHierarchy();
		updateGraphicalObjectsHierarchy();
		updateLocations();
		rebuildRequested = false;
	}

}
