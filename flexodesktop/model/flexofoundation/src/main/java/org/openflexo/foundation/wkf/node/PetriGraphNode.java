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

/*
 * FlexoNode.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingModel;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.StatusSetOnNode;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.localization.FlexoLocalization;

/**
 * A FlexoNode is the base element representing a node in a PetriGraph. Three levels of FlexoNode exist and correspond to layers: Activity,
 * Operation and Action. A FlexoNode is abstract and must be subsequently subclassed with ActivityFlexoNode, OperationFlexoNode and
 * ActionFlexoNode
 * 
 * @author bmangez, sguerin
 */
public abstract class PetriGraphNode extends Node implements Bindable, Sortable {

	private static final Logger logger = Logger.getLogger(PetriGraphNode.class.getPackage().getName());

	private Status _newStatus;

	private FlexoPetriGraph parentPetriGraph;

	// ==========================================================================
	// ============================= Constructor ================================
	// ==========================================================================

	/**
	 * Default constructor
	 */
	public PetriGraphNode(FlexoProcess process) {
		super(process);
		activationAssignments = new Vector<BindingAssignment>();
		desactivationAssignments = new Vector<BindingAssignment>();
	}

	@Override
	public void delete() {
		if (getParentPetriGraph() != null) {
			getParentPetriGraph().removeFromNodes(this);
		}
		super.delete();
	}

	@Override
	public BindingModel getBindingModel() {
		if (getProcess() != null) {
			return getProcess().getBindingModel();
		}
		return null;
	}

	@Override
	public PetriGraphNode getNode() {
		return this;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getParentPetriGraph() != null && getParentPetriGraph().getContainer() != null) {
			return getParentPetriGraph().getContainer().getFullyQualifiedName() + "." + formattedString(getNodeName());
		}
		return "???";
	}

	@Override
	protected Vector<FlexoActionType> getSpecificActionListForThatClass() {
		Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
		// returned.add(ShowExecutionControlGraphs.actionType);
		return returned;
	}

	public FlexoPetriGraph getParentPetriGraph() {
		return parentPetriGraph;
	}

	public final void setParentPetriGraph(FlexoPetriGraph pg) {
		parentPetriGraph = pg;
	}

	public int getDepth() {
		if (getParentPetriGraph() != null && getParentPetriGraph().getContainer() instanceof PetriGraphNode) {
			return ((PetriGraphNode) getParentPetriGraph().getContainer()).getDepth() + 1;
		} else {
			return 1;
		}
	}

	public boolean isInRootPetriGraph() {
		return getParentPetriGraph() != null && getParentPetriGraph().getContainer() == getProcess();
	}

	public boolean getDontGenerateRecursive() {
		if (getDontGenerate()) {
			return true;
		}

		if (getParentPetriGraph() != null && getParentPetriGraph().getContainer() != null) {
			if (getParentPetriGraph().getContainer() instanceof PetriGraphNode) {
				return ((PetriGraphNode) getParentPetriGraph().getContainer()).getDontGenerateRecursive();
			} else {
				return getParentPetriGraph().getContainer().getDontGenerate();
			}
		}

		return false;
	}

	public final AbstractActivityNode getAbstractActivityNode() {
		if (this instanceof AbstractActivityNode) {
			return (AbstractActivityNode) this;
		}
		FlexoPetriGraph currentPetriGraph = getParentPetriGraph();

		while (currentPetriGraph != null) {
			if (currentPetriGraph.getContainer() instanceof AbstractActivityNode) {
				return (AbstractActivityNode) currentPetriGraph.getContainer();
			}
			if (currentPetriGraph.getContainer() == null) {
				return null;
			}
			if (currentPetriGraph.getContainer() instanceof PetriGraphNode) {
				currentPetriGraph = ((PetriGraphNode) currentPetriGraph.getContainer()).getParentPetriGraph();
			} else {
				return null;
			}
		}
		return null;
	}

	public PetriGraphNode getProcessLevelNode() {
		PetriGraphNode node = this;

		while (node != null && node.getParentPetriGraph() != null) {
			if (node.isProcessLevel()) {
				return node;
			}
			if (node.getParentPetriGraph().getContainer() instanceof PetriGraphNode) {
				node = (PetriGraphNode) node.getParentPetriGraph().getContainer();
			} else {
				return null;
			}
		}
		return null;
	}

	public boolean isProcessLevel() {
		return getParentPetriGraph() != null && getParentPetriGraph().getContainer() instanceof FlexoProcess;
	}

	public final ActivityPetriGraph getActivityPetriGraph() {
		FlexoPetriGraph currentPetriGraph = getParentPetriGraph();

		while (currentPetriGraph != null) {
			if (currentPetriGraph instanceof ActivityPetriGraph) {
				return (ActivityPetriGraph) currentPetriGraph;
			}
			if (currentPetriGraph.getContainer() == null) {
				return null;
			}
			if (currentPetriGraph.getContainer() instanceof PetriGraphNode) {
				currentPetriGraph = ((PetriGraphNode) currentPetriGraph.getContainer()).getParentPetriGraph();
			} else {
				return null;
			}
		}
		return null;
	}

	public final OperationNode getOperationNode() {
		FlexoPetriGraph currentPetriGraph = getParentPetriGraph();

		while (currentPetriGraph != null) {
			if (currentPetriGraph.getContainer() instanceof OperationNode) {
				return (OperationNode) currentPetriGraph.getContainer();
			}
			if (currentPetriGraph.getContainer() == null) {
				return null;
			}
			if (currentPetriGraph.getContainer() instanceof PetriGraphNode) {
				currentPetriGraph = ((PetriGraphNode) currentPetriGraph.getContainer()).getParentPetriGraph();
			} else {
				return null;
			}
		}
		return null;
	}

	private String newStatusAsString;

	public Status getNewStatus() {
		if (_newStatus == null && newStatusAsString != null) {
			if (getProject() != null) {
				_newStatus = getProject().getGlobalStatus().get(newStatusAsString);
				if (_newStatus == null && !isDeserializing()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Status with name " + newStatusAsString + " could not be found.");
					}
					newStatusAsString = null;
				}
			} else if (!isDeserializing()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("No project for node " + getName());
				}
			}
		}
		return _newStatus;
	}

	public void setNewStatus(Status newStatus) {
		Status old = _newStatus;
		_newStatus = newStatus;
		setChanged();
		notifyObservers(new StatusSetOnNode(this, old, newStatus));
	}

	public String getNewStatusAsString() {
		if (getNewStatus() != null) {
			return getNewStatus().getFullyQualifiedName();
		} else {
			return null;
		}
	}

	public void setNewStatusAsString(String statusName) {
		this.newStatusAsString = statusName;
	}

	// ==========================================================================
	// ============================= Accessors
	// ==================================
	// ==========================================================================

	/**
	 * This attribute isn't supposed to be changed after node creation. Activity nodes are level ACTIVITY. Operations are level OPERATION.
	 * Actions are level ACTION. Other specific node can exist at level 3 or more.
	 * 
	 * @return the node level.
	 */
	@Override
	public abstract FlexoLevel getLevel();

	@Override
	public boolean mayHaveIncomingPostConditions() {
		return true;
	}

	@Override
	public boolean mayHaveOutgoingPostConditions() {
		return true;
	}

	// ==========================================================================
	// ================== Activation/Desactivation primitives ===================
	// ==========================================================================

	public static final String ACTIVATION_PRIMITIVE = "activationPrimitive";
	private BindingValue _activationPrimitive;

	public WKFBindingDefinition getActivationPrimitiveBindingDefinition() {
		return WKFBindingDefinition.get(this, ACTIVATION_PRIMITIVE, (DMType) null, BindingDefinitionType.EXECUTE, false);
	}

	public BindingValue getActivationPrimitive() {
		if (isBeingCloned()) {
			return null;
		}
		return _activationPrimitive;
	}

	public void setActivationPrimitive(BindingValue activationPrimitive) {
		BindingValue oldBindingValue = _activationPrimitive;
		_activationPrimitive = activationPrimitive;
		if (_activationPrimitive != null) {
			_activationPrimitive.setOwner(this);
			_activationPrimitive.setBindingDefinition(getActivationPrimitiveBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(ACTIVATION_PRIMITIVE, oldBindingValue, activationPrimitive));
	}

	public static final String DESACTIVATION_PRIMITIVE = "desactivationPrimitive";
	private BindingValue _desactivationPrimitive;

	public WKFBindingDefinition getDesactivationPrimitiveBindingDefinition() {
		return WKFBindingDefinition.get(this, DESACTIVATION_PRIMITIVE, (DMType) null, BindingDefinitionType.EXECUTE, false);
	}

	public BindingValue getDesactivationPrimitive() {
		if (isBeingCloned()) {
			return null;
		}
		return _desactivationPrimitive;
	}

	public void setDesactivationPrimitive(BindingValue desactivationPrimitive) {
		BindingValue oldBindingValue = _desactivationPrimitive;
		_desactivationPrimitive = desactivationPrimitive;
		if (_desactivationPrimitive != null) {
			_desactivationPrimitive.setOwner(this);
			_desactivationPrimitive.setBindingDefinition(getDesactivationPrimitiveBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(DESACTIVATION_PRIMITIVE, oldBindingValue, desactivationPrimitive));
	}

	private Vector<BindingAssignment> activationAssignments;
	private Vector<BindingAssignment> desactivationAssignments;

	public Vector<BindingAssignment> getActivationAssignments() {
		return activationAssignments;
	}

	public void setActivationAssignments(Vector<BindingAssignment> someAssignments) {
		this.activationAssignments = someAssignments;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("activationAssignments", null, null)); // TODO notify better
	}

	public void addToActivationAssignments(BindingAssignment assignment) {
		assignment.setOwner(this);
		activationAssignments.add(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("activationAssignments", null, null)); // TODO notify better
	}

	public void removeFromActivationAssignments(BindingAssignment assignment) {
		assignment.setOwner(null);
		activationAssignments.remove(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("activationAssignments", null, null)); // TODO notify better
	}

	public BindingAssignment createActivationAssignement() {
		BindingAssignment returned = new BindingAssignment(this);
		addToActivationAssignments(returned);
		return returned;
	}

	public void deleteActivationAssignement(BindingAssignment assignment) {
		removeFromActivationAssignments(assignment);
	}

	public boolean isActivationAssignementDeletable(BindingAssignment assignment) {
		return true;
	}

	public Vector<BindingAssignment> getDesactivationAssignments() {
		return desactivationAssignments;
	}

	public void setDesactivationAssignments(Vector<BindingAssignment> someAssignments) {
		this.desactivationAssignments = someAssignments;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("desactivationAssignments", null, null)); // TODO notify better
	}

	public void addToDesactivationAssignments(BindingAssignment assignment) {
		assignment.setOwner(this);
		desactivationAssignments.add(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("desactivationAssignments", null, null)); // TODO notify better
	}

	public void removeFromDesactivationAssignments(BindingAssignment assignment) {
		assignment.setOwner(null);
		desactivationAssignments.remove(assignment);
		setChanged();
		notifyObservers(new WKFAttributeDataModification("desactivationAssignments", null, null)); // TODO notify better
	}

	public BindingAssignment createDesactivationAssignement() {
		BindingAssignment returned = new BindingAssignment(this);
		addToDesactivationAssignments(returned);
		return returned;
	}

	public void deleteDesactivationAssignement(BindingAssignment assignment) {
		removeFromDesactivationAssignments(assignment);
	}

	public boolean isDesactivationAssignementDeletable(BindingAssignment assignment) {
		return true;
	}

	private int index = -1;

	@Override
	public int getIndex() {
		if (isBeingCloned()) {
			return -1;
		}
		if (index == -1 && getCollection() != null) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return getIndex();
	}

	@Override
	public void setIndexValue(int index) {
		if (this.index == index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyAttributeModification("index", old, index);
		if (!isDeserializing() && !isCreatedByCloning() && getParentPetriGraph() != null) {
			getParentPetriGraph().setChanged();
			getParentPetriGraph().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public PetriGraphNode[] getCollection() {
		if (getParentPetriGraph() == null) {
			return null;
		}
		return getParentPetriGraph().getNodes().toArray(new PetriGraphNode[0]);
	}

	public boolean isGrouped() {
		return getContainerGroup() != null;
	}

	public boolean isEmbeddedInSelfExecutableNode() {
		return isEmbeddedInObjectType(SelfExecutableNode.class);
	}

	public WKFGroup getContainerGroup() {
		FlexoPetriGraph pg = getParentPetriGraph();
		if (pg == null) {
			return null;
		}
		for (WKFGroup group : pg.getGroups()) {
			if (group.contains(this)) {
				return group;
			}
		}
		return null;
	}

	@Override
	public boolean isNodeValid() {
		if (this instanceof OperatorNode) {
			if (getProcess() == null) {
				return false;
			}
			return isEmbeddedInPetriGraph(getProcess().getActivityPetriGraph());
		}
		return getProcess() != null && getParentPetriGraph() != null;
	}

	@Override
	public boolean isContainedIn(WKFObject obj) {
		if (obj instanceof SelfExecutableNode) {
			if (((SelfExecutableNode) obj).hasExecutionPetriGraph()) {
				return isEmbeddedInPetriGraph(((SelfExecutableNode) obj).getExecutionPetriGraph());
			}
			return false;
		} else if (obj instanceof LOOPOperator) {
			if (((LOOPOperator) obj).hasExecutionPetriGraph()) {
				return isEmbeddedInPetriGraph(((LOOPOperator) obj).getExecutionPetriGraph());
			}
			return false;
		} else if (obj instanceof FatherNode) {
			if (((FatherNode) obj).hasContainedPetriGraph()) {
				return isEmbeddedInPetriGraph(((FatherNode) obj).getContainedPetriGraph());
			}
			return false;
		} else if (obj instanceof FlexoPetriGraph) {
			return isEmbeddedInPetriGraph((FlexoPetriGraph) obj);
		} else if (obj instanceof WKFGroup) {
			return getContainerGroup() == obj;
		}
		return super.isContainedIn(obj);
	}

	/**
	 * Recursive method to determine if the current node is embedded in the Petri graph <code>petriGraph</code>
	 */
	public boolean isEmbeddedInPetriGraph(FlexoPetriGraph petriGraph) {
		if (getParentPetriGraph() == petriGraph) {
			return true;
		} else if (getParentPetriGraph() != null && getParentPetriGraph().getContainer() instanceof PetriGraphNode) {
			return ((PetriGraphNode) getParentPetriGraph().getContainer()).isEmbeddedInPetriGraph(petriGraph);
		}
		return false;
	}

	/**
	 * Recursive method to determine if the current node is embedded in an object of type <code>klass</code>
	 */
	public boolean isEmbeddedInObjectType(Class<?> klass) {
		if (getParentPetriGraph() == null) {
			return false;
		}
		WKFObject parent = getParentPetriGraph();
		while (parent instanceof PetriGraphNode || parent instanceof FlexoPetriGraph) {
			if (klass.isAssignableFrom(parent.getClass())) {
				return true;
			}
			if (parent instanceof AbstractNode) {
				parent = ((PetriGraphNode) parent).getParentPetriGraph();
			} else if (parent instanceof FlexoPetriGraph) {
				parent = ((FlexoPetriGraph) parent).getContainer();
			}
		}
		return false;
	}

	public Role getBestRole() {
		TreeMap<Integer, Vector<Role>> map = new TreeMap<Integer, Vector<Role>>();
		getBestRole(new Vector<Node>(), map, 0);
		Iterator<Integer> i = map.keySet().iterator();
		while (i.hasNext()) {
			Vector<Role> v = map.get(i.next());
			if (v.size() > 0) {
				return v.firstElement();
			}
		}
		return null;
	}

	public boolean isAccessible() {
		return hasIncomingPostConditions();
	}

	// ==========================================================================
	// ============================= Validation =================================
	// ==========================================================================

	public static class PetriGraphNodeNameCannotBeEmpty extends ValidationRule<PetriGraphNodeNameCannotBeEmpty, PetriGraphNode> {

		public PetriGraphNodeNameCannotBeEmpty() {
			super(PetriGraphNode.class, "flexo_node_name_cannot_be_empty");
		}

		@Override
		public ValidationIssue<PetriGraphNodeNameCannotBeEmpty, PetriGraphNode> applyValidation(PetriGraphNode object) {
			PetriGraphNode node = object;
			if (node.getName() == null || node.getName().trim().length() == 0) {
				String proposal = node.findNextNonAmbigousName();
				return new ValidationWarning<PetriGraphNodeNameCannotBeEmpty, PetriGraphNode>(this, node,
						FlexoLocalization.localizedForKey("name_is_empty"), new RenameThisNode(node, proposal));
			}
			return null;
		}

	}

	public static class RenameThisNode extends ParameteredFixProposal<PetriGraphNodeNameCannotBeEmpty, PetriGraphNode> {
		public RenameThisNode(AbstractNode node, String proposal) {
			super("rename_this_node", "newName", "enter_a_non_ambigous_name", proposal);
		}

		@Override
		protected void fixAction() {
			String newName = (String) getValueForParameter("newName");
			getObject().setName(newName);
		}
	}

	public static class PetriGraphNodeShouldBeAccessible extends ValidationRule<PetriGraphNodeShouldBeAccessible, PetriGraphNode> {
		public PetriGraphNodeShouldBeAccessible() {
			super(PetriGraphNode.class, "node_should_be_accessible");
		}

		@Override
		public ValidationIssue<PetriGraphNodeShouldBeAccessible, PetriGraphNode> applyValidation(PetriGraphNode node) {
			if (node instanceof ActionNode && ((ActionNode) node).getActionType() == ActionType.DISPLAY_ACTION) {
				return null;
			}
			if (!node.isAccessible()) {
				ValidationWarning<PetriGraphNodeShouldBeAccessible, PetriGraphNode> warning = new ValidationWarning<PetriGraphNodeShouldBeAccessible, PetriGraphNode>(
						this, node, "node_($object.name)_is_not_accessible");
				warning.addToFixProposals(new DeletionFixProposal<PetriGraphNodeShouldBeAccessible, PetriGraphNode>("delete_this_node"));
				return warning;
			}
			return null;
		}
	}

}
