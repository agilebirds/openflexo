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
package org.openflexo.foundation.wkf.action;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.TaskType;
import org.openflexo.foundation.wkf.node.WKFNode;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class DropWKFElement extends FlexoUndoableAction<DropWKFElement, FlexoPetriGraph, WKFObject> {

	private static final Logger logger = Logger.getLogger(DropWKFElement.class.getPackage().getName());

	public static FlexoActionType<DropWKFElement, FlexoPetriGraph, WKFObject> actionType = new FlexoActionType<DropWKFElement, FlexoPetriGraph, WKFObject>(
			"drag_wkf_element", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DropWKFElement makeNewAction(FlexoPetriGraph focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new DropWKFElement(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoPetriGraph object, Vector<WKFObject> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoPetriGraph object, Vector<WKFObject> globalSelection) {
			return ((object != null) && (object.getProcess() != null));
		}

	};

	DropWKFElement(FlexoPetriGraph focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		parameters = new Hashtable<String, FlexoModelObject>();
	}

	public static final String SUB_PROCESS = "SUB_PROCESS";

	private boolean leaveSubProcessNodeUnchanged = false;

	private WKFObject object;
	private double posX = -1;
	private double posY = -1;
	private Hashtable<String, FlexoModelObject> parameters;
	private boolean resetNodeName = true;
	private boolean editNodeLabel = false;
	private Role roleToAssociate;

	private String graphicalContext; // eg BPE, SWL ...

	private WKFGroup group;

	public FlexoProcess getProcess() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProcess();
		}
		return null;
	}

	public void setParameter(String key, FlexoModelObject value) {
		parameters.put(key, value);
	}

	@Override
	protected void doAction(Object context) throws InvalidLevelException {
		logger.info("Insert WKF element");
		if ((getProcess() != null) && (getObject() != null) && (getPetriGraph() != null)) {
			if (getGraphicalContext() != null) {
				getObject().setX(posX, getGraphicalContext());
				getObject().setY(posY, getGraphicalContext());
			}
			if (getObject() instanceof PetriGraphNode) {
				PetriGraphNode node = (PetriGraphNode) getObject();
				if (getRoleToAssociate() != null && getRoleToAssociate() != getProject().getWorkflow().getDefaultRole()) {
					if (node instanceof AbstractActivityNode) {
						((AbstractActivityNode) node).setRole(getRoleToAssociate());
					} else if (node instanceof OperatorNode) {
						((OperatorNode) node).setRole(getRoleToAssociate());
					} else if (node instanceof EventNode) {
						((EventNode) node).setRole(getRoleToAssociate());
					}
				}
				if (node instanceof OperatorNode || node instanceof EventNode || node.getLevel() == getPetriGraph().getLevel()) {
					FlexoProcess process = getPetriGraph().getProcess();
					if (resetNodeName) {
						resetNodeName(getPetriGraph(), node);
					}
					setProcessOnNode(process, node);

					if (node instanceof SubProcessNode) {
						SubProcessNode subProcessNode = (SubProcessNode) node;
						FlexoProcess subProcess = (FlexoProcess) parameters.get(SUB_PROCESS);
						if (subProcess != null) {
							subProcessNode.setName(subProcess.getName());
							subProcessNode.setSubProcess(subProcess);
						}
					}
					getProcess().getProject().register(node);
					getPetriGraph().addToNodes(node);
					if (node instanceof MetricsValueOwner) {
						((MetricsValueOwner) node).updateMetricsValues();
					}
					if (node instanceof FlexoNode) {
						if (node instanceof ActivityNode)
							((ActivityNode) node).setTaskType(TaskType.User);
						if (((FlexoNode) node).isBeginOrEndNode()) {
							node.setDontGenerate(true);
							((FlexoNode) node).resetLabelLocation(getGraphicalContext());
						}
						if (((FlexoNode) node).isSelfExecutableNode())
							((FlexoNode) node).resetLabelLocation(getGraphicalContext());
						if (node instanceof ActionNode)
							((FlexoNode) node).resetLabelLocation(getGraphicalContext());
					}
					if (getGroup() != null) {
						getGroup().addToNodes(node);
						getGroup().notifyGroupUpdated();
					}
					if (node instanceof EventNode) {
						EventNode eventNode = (EventNode) node;
						if (((EventNode) node).isStartOrEnd())
							node.setDontGenerate(true);
						eventNode.resetLabelLocation(getGraphicalContext());
						// eventNode.setLabelX(25, getGraphicalContext());
						// eventNode.setLabelY(45, getGraphicalContext());
					}
					return;
				}
			}

			else if (object instanceof WKFArtefact) {
				WKFArtefact artefact = (WKFArtefact) getObject();
				logger.info("Insert an artefact...");
				FlexoProcess process = getPetriGraph().getProcess();
				setProcessOnNode(process, artefact);
				getPetriGraph().addToArtefacts(artefact);
				artefact.updateMetricsValues();
			}

			else {
				throw new InvalidLevelException("Cannot insert this element at this level");
			}

		} else {
			logger.warning("Something strange happened: " + getProcess() + ", " + getObject() + ", " + getPetriGraph());
		}
	}

	private void setProcessOnNode(FlexoProcess process, WKFNode node) {
		node.setProcess(process);
		Collection<FlexoModelObject> embedded = node.getAllRecursivelyEmbeddedObjects();
		Iterator<FlexoModelObject> i = embedded.iterator();
		while (i.hasNext()) {
			FlexoModelObject o = i.next();
			if (o instanceof WKFObject)
				((WKFObject) o).setProcess(process);
		}
	}

	// public FlexoColor getBackgroundColor() {
	// return backgroundColor;
	// }

	// public void setBackgroundColor(FlexoColor backgroundColor) {
	// this.backgroundColor = backgroundColor;
	// }

	private void resetNodeName(FlexoPetriGraph petriGraph, AbstractNode newNode) {
		FlexoProcess process = petriGraph.getProcess();

		if (petriGraph instanceof ActivityPetriGraph) {
			if (newNode instanceof SubProcessNode) {
				newNode.setNodeName(process.findNextInitialName(newNode.getName()));
			} else {
				newNode.setNodeName(process.findNextInitialName(newNode.getDefaultName()));
			}
		} else if (petriGraph instanceof OperationPetriGraph) {
			newNode.setNodeName(process.findNextInitialName(newNode.getDefaultName(),
					((OperationPetriGraph) petriGraph).getAbstractActivityNode()));
		} else if (petriGraph instanceof ActionPetriGraph) {
			newNode.setNodeName(process.findNextInitialName(newNode.getDefaultName(), ((ActionPetriGraph) petriGraph).getOperationNode()));
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Inconsistent data: petri graph is a " + petriGraph.getClass().getName());
		}
	}

	public WKFObject getObject() {
		return object;
	}

	public void setObject(WKFObject anObject) {
		object = anObject;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public void setLocation(double posX, double posY) {
		setPosX(posX);
		setPosY(posY);
	}

	public boolean getEditNodeLabel() {
		return editNodeLabel;
	}

	public void setEditNodeLabel(boolean editNodeLabel) {
		this.editNodeLabel = editNodeLabel;
	}

	public boolean getResetNodeName() {
		return resetNodeName;
	}

	public void setResetNodeName(boolean resetNodeName) {
		this.resetNodeName = resetNodeName;
	}

	public FlexoPetriGraph getPetriGraph() {
		return getFocusedObject();
	}

	private static AbstractNode buildDroppedElement(String elementXMLRepresentation, FlexoProcess process) {
		AbstractNode node = null;
		try {
			XMLMapping wkfMapping = process.getXMLMapping();
			node = (AbstractNode) XMLDecoder.decodeObjectWithMapping(elementXMLRepresentation, wkfMapping, process.instanciateNewBuilder(),
					process.getStringEncoder());
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Failed building element: " + elementXMLRepresentation);
			e.printStackTrace();
		}
		return node;
	}

	private static AbstractNode buildDroppedElement(File elementXMLFile, FlexoProcess process) {
		AbstractNode node = null;
		try {
			XMLMapping wkfMapping = process.getXMLMapping();
			// Read the node
			AbstractNode readNode = (AbstractNode) XMLDecoder.decodeObjectWithMapping(new FileInputStream(elementXMLFile), wkfMapping,
					process.instanciateNewBuilder(), process.getStringEncoder());
			// We clone here in order to get a node with identifiers well resetted (and all other stuff)
			node = (AbstractNode) readNode.cloneUsingXMLMapping();
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Failed building element  from file " + elementXMLFile);
			e.printStackTrace();
		}
		return node;

	}

	public void setElementType(WKFElementType elementType) {
		setObject((WKFObject) elementType.instanciateNewObject().cloneUsingXMLMapping(getPetriGraph().getProcess().instanciateNewBuilder(),
				true, getPetriGraph().getProcess().getXMLMapping()));
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		doAction(context);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		getObject().delete();
	}

	public String getGraphicalContext() {
		return graphicalContext;
	}

	public void setGraphicalContext(String graphicalContext) {
		this.graphicalContext = graphicalContext;
	}

	public WKFGroup getGroup() {
		return group;
	}

	public void setGroup(WKFGroup group) {
		this.group = group;
	}

	public Role getRoleToAssociate() {
		return roleToAssociate;
	}

	public void setRoleToAssociate(Role roleToAssociate) {
		this.roleToAssociate = roleToAssociate;
	}

	public boolean leaveSubProcessNodeUnchanged() {
		return leaveSubProcessNodeUnchanged;
	}

	public void setLeaveSubProcessNodeUnchanged(boolean leaveSubProcessNodeUnchanged) {
		this.leaveSubProcessNodeUnchanged = leaveSubProcessNodeUnchanged;
	}

	private boolean handlePaletteOffset = true;

	public boolean handlePaletteOffset() {
		return handlePaletteOffset;
	}

	public void setHandlePaletteOffset(boolean handlePaletteOffset) {
		this.handlePaletteOffset = handlePaletteOffset;
	}
}
