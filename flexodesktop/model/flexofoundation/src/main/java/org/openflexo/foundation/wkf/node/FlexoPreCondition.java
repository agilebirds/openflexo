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
 * FlexoPreCondition.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.LevelledObject;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.PreRemoved;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ProgrammingLanguage;

/**
 * A FlexoPreCondition is a recepient container for tokens that came along TokenEdge linked to it.<BR>
 * <U>Main attributes</u><BR>
 * <B>initTokenNbr</B> The number of tokens that are initially found in the container.(Usually 0)<BR>
 * 
 * @author benoit
 */
public final class FlexoPreCondition extends Node implements InspectableObject, DeletableObject, LevelledObject, ExecutableWorkflowElement {

	private static final Logger logger = Logger.getLogger(FlexoPreCondition.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	private FlexoNode _attachedNode;

	private FlexoNode _attachedBeginNode;

	private int _initTokenNbr = 0;
	private int _triggerLevel = 1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public FlexoPreCondition(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Constructor with process
	 */
	public FlexoPreCondition(FlexoProcess process) {
		super(process);
	}

	/**
	 * Constructor with attached node and process
	 */
	public FlexoPreCondition(FlexoNode attachedNode, FlexoProcess process) {
		this(process);
		if (attachedNode.getProcess() == process) {
			setAttachedNode(attachedNode);
		} else {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Inconsistent data while building FlexoPreCondition !");
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return (getAttachedNode() != null ? getAttachedNode().getFullyQualifiedName() : "UNLINKED") + ".PRE_CONDITION"
				+ (getAttachedBeginNode() != null ? "." + getAttachedBeginNode().getFullyQualifiedName() : ".UNLINK");
	}

	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	/**
	 * Constructor with no attached node
	 */
	public FlexoPreCondition(FlexoNode attachedNode) {
		this(attachedNode, attachedNode.getProcess());
	}

	/**
	 * Constructor with attached node
	 */
	public FlexoPreCondition(FlexoNode attachedNode, FlexoNode attachedBeginNode) {
		this(attachedNode);
		setAttachedBeginNode(attachedBeginNode);
	}

	@Override
	public String getName() {
		if (_attachedBeginNode != null) {
			return _attachedBeginNode.getName();
		}
		return FlexoLocalization.localizedForKey("unbound_precondition");
	}

	@Override
	public void setName(String aName) {
		if (_attachedBeginNode != null) {
			_attachedBeginNode.setName(aName);
		}
	}

	@Override
	public String getDescription() {
		if (_attachedBeginNode != null) {
			return _attachedBeginNode.getDescription();
		}
		return null;
	}

	@Override
	public void setDescription(String description) {
		if (_attachedBeginNode != null) {
			_attachedBeginNode.setDescription(description);
		}
	}

	public int getInitTokenNbr() {
		return _initTokenNbr;
	}

	public void setInitTokenNbr(int tokenNbr) {
		_initTokenNbr = tokenNbr;
	}

	public int getTriggerLevel() {
		return _triggerLevel;
	}

	public void setTriggerLevel(int triggerLevel) {
		_triggerLevel = triggerLevel;
	}

	@Override
	public boolean mayHaveIncomingPostConditions() {
		return true;
	}

	@Override
	public boolean mayHaveOutgoingPostConditions() {
		return false;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public boolean isContainedIn(WKFObject obj) {
		if (getAttachedNode() == null)
			return false;
		return getAttachedNode().isContainedIn(obj);
	}

	@Override
	public boolean contains(WKFObject obj) {
		return false;
	}

	// ==========================================================================
	// ================================= Delete
	// =================================
	// ==========================================================================

	@Override
	public final void delete() {
		if (getAttachedNode() != null) {
			getAttachedNode().removeFromPreCondition(this);
		}
		if (getAttachedBeginNode() != null) {
			getAttachedBeginNode().setAttachedPreCondition(null);
		}
		super.delete();
		setChanged();
		notifyObservers(new PreRemoved(this));
		deleteObservers();
	}

	@Override
	public boolean isNodeValid() {
		return super.isNodeValid() && getAttachedNode() != null;
	}

	/**
	 * Build and return a vector of all the objects that will be deleted during this deletion
	 * 
	 * @param aVector
	 *            of DeletableObject
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedDeleted() {
		Vector<WKFObject> returned = getAllEmbeddedWKFObjects();
		returned.addAll(getIncomingPostConditions());
		if (_attachedBeginNode != null) {
			returned.add(this);
		}
		return returned;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.PRE_CONDITION_INSPECTOR;
	}

	@Override
	public FlexoLevel getLevel() {
		if (getAttachedNode() != null)
			return getAttachedNode().getLevel();
		return null;
	}

	@Override
	public FlexoNode getNode() {
		return getAttachedNode();
	}

	public FlexoNode getAttachedNode() {
		return _attachedNode;
	}

	public void setAttachedNode(FlexoNode newAttachedNode) {
		_attachedNode = newAttachedNode;
		if (newAttachedNode != null) {
			newAttachedNode.addToPreConditions(this);
		}
	}

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = new Vector<WKFObject>();
		returned.add(this);
		return returned;
	}

	public FlexoNode getAttachedBeginNode() {
		FlexoNode returned = _attachedBeginNode;
		if (returned != null) {
			if (returned.getParentPetriGraph() == null) {
				logger.warning("Inconsistent data: found a Node outside a PetriGraph !");
				return null;
			} else if (getAttachedNode() instanceof SelfExecutableNode) {
				if (returned.getParentPetriGraph() != ((SelfExecutableNode) getAttachedNode()).getExecutionPetriGraph()) {
					logger.warning("Inconsistent data: found a Node related to wrong ExecutionPetriGraph !");
					return null;
				}
			} else if (getAttachedNode() instanceof FatherNode
					&& returned.getParentPetriGraph() != ((FatherNode) getAttachedNode()).getContainedPetriGraph()) {
				logger.warning("Inconsistent data: found a Node related to wrong PetriGraph !");
				return null;
			}
		}
		return returned;
	}

	public void setAttachedBeginNode(FlexoNode beginNode) {
		if ((_attachedBeginNode != null) && (_attachedBeginNode != beginNode)) {
			_attachedBeginNode.setAttachedPreCondition(null);
		}
		if (beginNode != null) {
			if (beginNode.getNodeType() != NodeType.BEGIN) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Attached BEGIN node must be a BEGIN node !");
			} else {
				_attachedBeginNode = beginNode;
				beginNode.setAttachedPreCondition(this);
			}
		} else {
			_attachedBeginNode = null;
		}
	}

	public Vector getAvailableBeginNodes() {
		if (_attachedNode != null) {
			if (_attachedNode instanceof SelfExecutableNode) {
				FlexoPetriGraph pg = ((SelfExecutableNode) _attachedNode).getExecutionPetriGraph();
				if (pg != null) {
					return pg.getAllBeginNodes();
				}
			} else if (_attachedNode instanceof AbstractActivityNode) {
				OperationPetriGraph pg = ((AbstractActivityNode) _attachedNode).getOperationPetriGraph();
				if (pg != null) {
					return pg.getAllBeginNodes();
				}
			} else if (_attachedNode instanceof OperationNode) {
				ActionPetriGraph pg = ((OperationNode) _attachedNode).getActionPetriGraph();
				if (pg != null) {
					return pg.getAllBeginNodes();
				}
			}
		}
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "flexo_pre_condition";
	}

	// =========================================================
	// ============= Control graph management ==================
	// =========================================================

	private static ControlGraphFactory<FlexoPreCondition> _executionComputingFactory;

	public static void setExecutionComputingFactory(ControlGraphFactory<FlexoPreCondition> factory) {
		_executionComputingFactory = factory;
	}

	public WorkflowControlGraph<FlexoPreCondition> getExecution() {
		if (_executionComputingFactory != null)
			return _executionComputingFactory.getControlGraph(this);
		return null;
	}

	@Override
	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language) {
		if (getExecution() != null)
			getExecution().setProgrammingLanguage(language);
	}

	@Override
	public void setInterproceduralForControlGraphComputation(boolean interprocedural) {
		if (getExecution() != null)
			getExecution().setInterprocedural(interprocedural);
	}

	@Override
	public String getExecutableElementName() {
		return FlexoLocalization.localizedForKeyWithParams("pre_condition_($0)", getName());
	}

	// =========================================================
	// ===================== Validation ========================
	// =========================================================

	public static class PreConditionMustBeAttachedToANode extends ValidationRule<PreConditionMustBeAttachedToANode, FlexoPreCondition> {
		public PreConditionMustBeAttachedToANode() {
			super(FlexoPreCondition.class, "pre_condition_must_be_attached_to_a_node");
		}

		@Override
		public ValidationIssue<PreConditionMustBeAttachedToANode, FlexoPreCondition> applyValidation(FlexoPreCondition pre) {
			if (pre.getAttachedNode() == null) {
				return new ValidationError<PreConditionMustBeAttachedToANode, FlexoPreCondition>(this, pre,
						"pre_condition_is_not_attached_to_a_node",
						new DeletionFixProposal<PreConditionMustBeAttachedToANode, FlexoPreCondition>("delete_this_pre_condition"));
			}
			return null;
		}
	}

	public static class PreConditionMustHaveIncomingEdges extends ValidationRule<PreConditionMustHaveIncomingEdges, FlexoPreCondition> {
		public PreConditionMustHaveIncomingEdges() {
			super(FlexoPreCondition.class, "pre_condition_must_have_incoming_edges");
		}

		@Override
		public ValidationIssue<PreConditionMustHaveIncomingEdges, FlexoPreCondition> applyValidation(FlexoPreCondition pre) {
			if (!pre.hasIncomingPostConditions()) {
				return new ValidationError<PreConditionMustHaveIncomingEdges, FlexoPreCondition>(this, pre,
						"pre_condition_has_no_incoming_edges",
						new DeletionFixProposal<PreConditionMustHaveIncomingEdges, FlexoPreCondition>("delete_this_pre_condition"));
			}
			return null;
		}
	}

	public static class PreConditionMustBeLinkedToABeginNode extends
			ValidationRule<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition> {
		public PreConditionMustBeLinkedToABeginNode() {
			super(FlexoPreCondition.class, "pre_condition_must_be_linked_to_a_begin_node");
		}

		@Override
		public ValidationIssue<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition> applyValidation(FlexoPreCondition pre) {
			if ((pre.getAttachedNode() != null && !pre.getAttachedNode().isEndNode())
					&& (pre.getAttachedNode() instanceof FatherNode)
					&& (!(pre.getAttachedNode() instanceof AbstractActivityNode) || ((AbstractActivityNode) pre.getAttachedNode())
							.mightHaveOperationPetriGraph())) {
				ValidationError<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition> error = null;
				if (pre.getAttachedBeginNode() == null) {
					error = new ValidationError<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition>(this, pre,
							"pre_condition_is_not_linked_to_a_begin_node");
				} else if (pre.getAttachedBeginNode().getParentPetriGraph() == null) {
					error = new ValidationError<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition>(this, pre,
							"pre_condition_is_linked_to_a_begin_node_outside_a_petri_graph");
				} else if (pre.getAttachedNode() instanceof SelfExecutableNode) {
					if (pre.getAttachedBeginNode().getParentPetriGraph() != ((SelfExecutableNode) pre.getAttachedNode())
							.getExecutionPetriGraph()) {
						error = new ValidationError<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition>(this, pre,
								"pre_condition_is_linked_to_a_begin_node_related_to_wrong_petri_graph");
					}
				} else if (pre.getAttachedBeginNode().getParentPetriGraph() != ((FatherNode) pre.getAttachedNode())
						.getContainedPetriGraph()) {
					error = new ValidationError<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition>(this, pre,
							"pre_condition_is_linked_to_a_begin_node_related_to_wrong_petri_graph");
				}
				if (error != null) {
					error.addToFixProposals(new CreateAndLinkNewBeginNode((FatherNode) pre.getAttachedNode()));
					FatherNode attachedNode = (FatherNode) pre.getAttachedNode();
					FlexoPetriGraph pg = attachedNode.getContainedPetriGraph();
					if (pg != null) {
						Vector<FlexoNode> allAvailableBeginNodes = pg.getAllBeginNodes();

						for (Enumeration<FlexoNode> e = allAvailableBeginNodes.elements(); e.hasMoreElements();) {
							FlexoNode beginNode = e.nextElement();
							if (beginNode.getAttachedPreCondition() == null) {
								error.addToFixProposals(new LinkPreToExistingBeginNode(beginNode));
							}
						}
					}
				}
				return error;
			}
			return null;
		}

		public class LinkPreToExistingBeginNode extends FixProposal<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition> {
			public FlexoNode beginNode;

			public LinkPreToExistingBeginNode(FlexoNode aBeginNode) {
				super("link_pre_condition_to_($beginNode.name)");
				beginNode = aBeginNode;
			}

			@Override
			protected void fixAction() {
				getObject().setAttachedBeginNode(beginNode);
			}
		}

		public class CreateAndLinkNewBeginNode extends ParameteredFixProposal<PreConditionMustBeLinkedToABeginNode, FlexoPreCondition> {
			public CreateAndLinkNewBeginNode(FatherNode flexoNode) {
				super("create_and_link_new_begin_node", "newBeginNodeName", "enter_a_name_for_the_new_begin_node", flexoNode.getProcess()
						.findNextInitialName(FlexoLocalization.localizedForKey("begin_node"), flexoNode));
			}

			@Override
			protected void fixAction() {
				String newBeginNodeName = (String) getValueForParameter("newBeginNodeName");
				FlexoPreCondition pre = getObject();
				FatherNode attachedNode = (FatherNode) pre.getAttachedNode();
				FlexoPetriGraph pg = attachedNode instanceof SelfExecutableNode ? ((SelfExecutableNode) pre.getAttachedNode())
						.getExecutionPetriGraph() : attachedNode.getContainedPetriGraph();
				FlexoNode newBeginNode = null;
				if (pg instanceof ActivityPetriGraph) {
					newBeginNode = ((ActivityPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				} else if (pg instanceof OperationPetriGraph) {
					newBeginNode = ((OperationPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				} else if (pg instanceof ActionPetriGraph) {
					newBeginNode = ((ActionPetriGraph) pg).createNewBeginNode(newBeginNodeName);
				}
				pre.setAttachedBeginNode(newBeginNode);
			}
		}

	}

	@Override
	public String getDefaultName() {
		return null;
	}

}
