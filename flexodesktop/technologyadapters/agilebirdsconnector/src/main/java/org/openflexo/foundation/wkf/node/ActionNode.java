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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.ComponentInstanceOwner;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.dm.ButtonRemoved;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.dm.ButtonWidgetAssociated;
import org.openflexo.foundation.wkf.dm.DisplayOperationSet;
import org.openflexo.foundation.wkf.dm.DisplayProcessSet;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;

/**
 * This represents a FlexoNode at the level 'Action'
 * 
 * @author sguerin
 * 
 */
public class ActionNode extends FlexoNode implements ChildNode, ComponentInstanceOwner, FlexoObserver {

	private static final Logger logger = Logger.getLogger(ActionNode.class.getPackage().getName());

	protected ActionType _actionType;

	protected transient Status _newStatus;

	protected transient FlexoProcess _subProcess;

	private FlexoProcess displayProcess;

	private OperationNode displayOperation;

	// private boolean bypassLogicalWorkFlow = false;

	private long displayProcessFlexoID = -1;

	private long displayOperationFlexoID = -1;

	private IEHyperlinkWidget associatedButtonWidget;

	private long associatedButtonFlexoID = -1;

	private transient TabComponentInstance _tabComponentInstance;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public ActionNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ActionNode(FlexoProcess process) {
		super(process);
		_actionType = ActionType.FLEXO_ACTION;
	}

	public static String DEFAULT_ACTION_NODE_NAME() {
		return FlexoLocalization.localizedForKey("action_default_name");
	}

	@Override
	public String getDefaultName() {
		if (isBeginNode() || isEndNode()) {
			return super.getDefaultName();
		} else if (getActionType() != null) {
			return FlexoLocalization.localizedForKey(getActionType().getName());
		} else {
			return DEFAULT_ACTION_NODE_NAME();
		}
	}

	public String getDisplayString() {
		return FlexoLocalization.localizedForKeyWithParams("action_($name)_in_operation_($operationNode.name)", this);
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.ACTION;
	}

	public Vector getAllActionNodes() {
		return getOperationNode().getAllActionNodes();
	}

	@Override
	public String getInspectorName() {
		if (getActionType() == ActionType.DISPLAY_ACTION) {
			return Inspectors.WKF.DISPLAY_ACTION_NODE_INSPECTOR;
		} else if (getActionType() == ActionType.FLEXO_ACTION) {
			return Inspectors.WKF.FLEXO_ACTION_NODE_INSPECTOR;
		}
		if (getNodeType() == NodeType.NORMAL) {
			return Inspectors.WKF.ACTION_NODE_INSPECTOR;
		} else if (getNodeType() == NodeType.BEGIN) {
			return Inspectors.WKF.BEGIN_NODE_INSPECTOR;
		} else if (getNodeType() == NodeType.END) {
			return Inspectors.WKF.END_NODE_INSPECTOR;
		} else {
			return super.getInspectorName();
		}
	}

	@Override
	public OperationNode getFather() {
		return getOperationNode();
	}

	@Override
	public final boolean delete() {
		removeTabComponentInstance();
		// getParentPetriGraph().removeFromNodes(this);
		super.delete();
		deleteObservers();
		return true;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public String getFullyQualifiedName() {
		if (getFather() != null) {
			return getFather().getFullyQualifiedName() + "." + formattedString(getNodeName());
		} else {
			return "NULL." + formattedString(getNodeName());
		}
	}

	public ActionType getActionType() {
		return _actionType;
	}

	public void setActionType(ActionType type) {
		ActionType old = _actionType;
		_actionType = type;
		if (isDisplayAction()) {
			Vector<FlexoPostCondition> posts = (Vector<FlexoPostCondition>) getOutgoingPostConditions().clone();
			Enumeration<FlexoPostCondition> en = posts.elements();
			while (en.hasMoreElements()) {
				FlexoPostCondition post = en.nextElement();
				removeFromOutgoingPostConditions(post);
			}
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification("actionType", old, _actionType));
	}

	public boolean isDisplayAction() {
		return _actionType == ActionType.DISPLAY_ACTION;
	}

	public ImageIcon getImageIcon() {
		// if (getActionType() != null) {
		// return getActionType().getImageIcon();
		// }
		return null;
	}

	public IEHyperlinkWidget getAssociatedButtonWidget() {
		if (associatedButtonFlexoID > -1 && associatedButtonWidget == null && getOperationNode().hasWOComponent()) {
			Enumeration en = getOperationNode().getOperationComponent().getWOComponent().getAllButtonInterface().elements();
			while (en.hasMoreElements() && associatedButtonWidget == null) {
				IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
				if (((IEWidget) w).getFlexoID() == associatedButtonFlexoID) {
					associatedButtonWidget = w;
				}
			}
			if (associatedButtonWidget == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("The associated button of the action node " + getFullyQualifiedName() + " could not be found!");
				}
				associatedButtonFlexoID = -1;
			} else {
				associatedButtonWidget.addObserver(this);
			}
		}
		return associatedButtonWidget;
	}

	public void setAssociatedButtonWidget(IEHyperlinkWidget associatedButtonWidget) {
		if (this.associatedButtonWidget != null) {
			this.associatedButtonWidget.deleteObserver(this);
		}
		IEHyperlinkWidget old = this.associatedButtonWidget;
		this.associatedButtonWidget = associatedButtonWidget;
		if (associatedButtonWidget != null) {
			associatedButtonFlexoID = ((IEWidget) associatedButtonWidget).getFlexoID();
			associatedButtonWidget.addObserver(this);
			setActionType(associatedButtonWidget.getIsDisplayAction() ? ActionType.DISPLAY_ACTION : ActionType.FLEXO_ACTION);
		} else {
			associatedButtonFlexoID = -1;
		}
		setChanged();
		notifyObservers(new ButtonWidgetAssociated(old, associatedButtonWidget));
	}

	public long getAssociatedButtonWidgetFlexoID() {
		return associatedButtonFlexoID;
	}

	public void setAssociatedButtonWidgetFlexoID(long buttonFlexoID) {
		long old = associatedButtonFlexoID;
		associatedButtonFlexoID = buttonFlexoID;
		associatedButtonWidget = null;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("associatedButtonWidgetFlexoID", new Long(old), new Long(buttonFlexoID)));
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "action_node";
	}

	public OperationNode getDisplayOperation() {
		if (displayOperation == null && displayOperationFlexoID > -1) {
			if (getDisplayProcess() != null) {
				displayOperation = getDisplayProcess().getOperationNodeWithFlexoID(displayOperationFlexoID);
				if (displayOperation == null) {
					if (!isSerializing() && !isDeserializing() && getProject() != null) {
						displayOperationFlexoID = -1;
						setChanged();
					}
				} else {
					displayOperation.addObserver(this);
				}

			}
		}
		return displayOperation;
	}

	public void setDisplayOperation(OperationNode displayOperation) {
		OperationNode old = this.displayOperation;
		if (old != null) {
			old.deleteObserver(this);
		}
		removeTabComponentInstance();
		this.displayOperation = displayOperation;
		if (displayOperation != null) {
			displayOperation.addObserver(this);
			displayOperationFlexoID = displayOperation.getFlexoID();
		} else {
			displayOperationFlexoID = -1;
		}
		setChanged();
		notifyObservers(new DisplayOperationSet(old, displayOperation));
	}

	public FlexoProcess getDisplayProcess() {
		if (displayProcess == null && displayProcessFlexoID > -1 && getProject() != null) {
			displayProcess = getProject().getFlexoWorkflow().getLocalFlexoProcessWithFlexoID(displayProcessFlexoID);
			if (displayProcess == null) {
				if (!isSerializing() && !isDeserializing()) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Could not find process with flexoID " + displayProcessFlexoID);
					}
					displayProcessFlexoID = -1;
					setDisplayOperation(null);
					setChanged();
				}
			} else {
				displayProcess.addObserver(this);
			}
		}
		return displayProcess;
	}

	public void setDisplayProcess(FlexoProcess displayProcess) {
		FlexoProcess old = this.displayProcess;
		if (old != null) {
			old.deleteObserver(this);
		}
		this.displayProcess = displayProcess;
		if (displayProcess != null) {
			displayProcess.addObserver(this);
			displayProcessFlexoID = displayProcess.getFlexoID();
		} else {
			logger.warning("setting display process to null");
			displayProcessFlexoID = -1;
		}
		setDisplayOperation(null);
		setChanged();
		notifyObservers(new DisplayProcessSet(old, displayProcess));
	}

	public boolean isAcceptableAsDisplayProcess(FlexoProcess process) {
		return process != null;
	}

	public long getDisplayOperationFlexoID() {
		if (getDisplayOperation() != null) {
			return getDisplayOperation().getFlexoID();
		} else if (isSerializing()) {
			return displayOperationFlexoID;
		}
		return -1;
	}

	public void setDisplayOperationFlexoID(long displayOperationFlexoID) {
		if (getDisplayOperation() != null) {
			displayOperation = null;
		}
		this.displayOperationFlexoID = displayOperationFlexoID;
	}

	public long getDisplayProcessFlexoID() {
		if (getDisplayProcess() != null) {
			return getDisplayProcess().getFlexoID();
		} else if (isSerializing()) {
			return displayProcessFlexoID;
		}
		return -1;
	}

	public void setDisplayProcessFlexoID(long displayProcessFlexoID) {
		if (getDisplayProcess() != null) {
			displayProcess = null;
		}
		this.displayProcessFlexoID = displayProcessFlexoID;
	}

	/**
	 * 
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractNode#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == associatedButtonWidget
				&& (dataModification instanceof WidgetRemovedFromTable || dataModification instanceof ButtonRemoved)) {
			associatedButtonWidget.deleteObserver(this);
			setAssociatedButtonWidget(null);
		}
		if (observable == getDisplayOperation()) {
			if (dataModification instanceof ObjectDeleted) {
				setDisplayOperation(null);
			} else if (dataModification.propertyName() != null && dataModification.propertyName().equals("flexoID")) {
				setChanged();
			}
		} else if (observable == getDisplayProcess()) {
			if (dataModification instanceof ObjectDeleted) {
				setDisplayProcess(null);
			} else if (dataModification.propertyName() != null && dataModification.propertyName().equals("flexoID")) {
				setChanged();
			}
		}
	}

	public static class ActionTypeMustMatchButtonType extends ValidationRule<ActionTypeMustMatchButtonType, ActionNode> {
		public ActionTypeMustMatchButtonType() {
			super(ActionNode.class, "action_type_must_match_button_action_type");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<ActionTypeMustMatchButtonType, ActionNode> applyValidation(ActionNode node) {
			ValidationError<ActionTypeMustMatchButtonType, ActionNode> err = null;
			if (node.getAssociatedButtonWidget() != null) {
				if (node.getAssociatedButtonWidget().getIsDisplayAction() && node.getActionType() != ActionType.DISPLAY_ACTION) {
					err = new ValidationError<ActionTypeMustMatchButtonType, ActionNode>(this, node,
							"action_type_must_match_button_action_type");
					err.addToFixProposals(new ChangeActionType(node));
				}
			}

			return err;
		}

		public class ChangeActionType extends FixProposal<ActionTypeMustMatchButtonType, ActionNode> {

			private ActionNode node;

			/**
			 * @param aMessage
			 */
			public ChangeActionType(ActionNode node) {
				super("change_action_type");
				this.node = node;
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				if (node.getAssociatedButtonWidget().getIsDisplayAction()) {
					node.setActionType(ActionType.DISPLAY_ACTION);
					Enumeration en = ((Vector) node.getOutgoingPostConditions().clone()).elements();
					while (en.hasMoreElements()) {
						FlexoPostCondition element = (FlexoPostCondition) en.nextElement();
						element.delete();
					}
					/*
					 * en = ((Vector) node.getIncomingPostConditions().clone()).elements(); while (en.hasMoreElements()) {
					 * FlexoPostCondition element = (FlexoPostCondition) en.nextElement(); element.delete(); }
					 */
				} else if (node.getAssociatedButtonWidget().getIsFlexoAction()) {
					node.setActionType(ActionType.DISPLAY_ACTION);
					OperationNode.linkActionToBeginAndEndNode(node);
				}
			}

		}
	}

	public static class OnlyOneActionNodeMustBeBoundToOperationComponentAction extends
			ValidationRule<OnlyOneActionNodeMustBeBoundToOperationComponentAction, ActionNode> {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public OnlyOneActionNodeMustBeBoundToOperationComponentAction() {
			super(ActionNode.class, "only_one_action_node_must_be_bound_to_operation_component_actions");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<OnlyOneActionNodeMustBeBoundToOperationComponentAction, ActionNode> applyValidation(ActionNode node) {
			ValidationError<OnlyOneActionNodeMustBeBoundToOperationComponentAction, ActionNode> err = null;
			Enumeration en = node.getAllActionNodes().elements();
			while (en.hasMoreElements()) {
				ActionNode act = (ActionNode) en.nextElement();
				if (act.getAssociatedButtonWidget() != null && act != node
						&& act.getAssociatedButtonWidget() == node.getAssociatedButtonWidget()) {
					err = new ValidationError<OnlyOneActionNodeMustBeBoundToOperationComponentAction, ActionNode>(this, node,
							"only_one_action_node_must_be_bound_to_operation_component_actions($object.name)");
					break;
				}
			}
			return err;

		}
	}

	/*
	 * public static class WorkFlowBypassingActionsMustSpecifyTargetNode extends
	 * ValidationRule<WorkFlowBypassingActionsMustSpecifyTargetNode, ActionNode> {
	 * 
	 * public WorkFlowBypassingActionsMustSpecifyTargetNode() { super(ActionNode.class,
	 * "work_flow_bypassing_actions_must_specify_target_node"); }
	 * 
	 * @Override public ValidationIssue<WorkFlowBypassingActionsMustSpecifyTargetNode, ActionNode> applyValidation(ActionNode object) { if
	 * (object.getActionType()==ActionType.FLEXO_ACTION && object.bypassLogicalWorkFlow() && object.getDisplayOperation()==null &&
	 * object.hasIncomingEdges()) { return new ValidationError<WorkFlowBypassingActionsMustSpecifyTargetNode, ActionNode>(this, object,
	 * "flexo_action_($object.name)_must_have_a_target_operation"); } return null; }
	 * 
	 * }
	 */

	public static class DisplayActionMustHaveADisplayProcess extends ValidationRule<DisplayActionMustHaveADisplayProcess, ActionNode> {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public DisplayActionMustHaveADisplayProcess() {
			super(ActionNode.class, "display_action_must_have_a_display_process");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<DisplayActionMustHaveADisplayProcess, ActionNode> applyValidation(ActionNode node) {
			ValidationError<DisplayActionMustHaveADisplayProcess, ActionNode> err = null;
			if (node._actionType == ActionType.DISPLAY_ACTION) {
				if (node.getDisplayProcess() == null && node.hasIncomingEdges()) {
					err = new ValidationError<DisplayActionMustHaveADisplayProcess, ActionNode>(this, node,
							"display_action_($object.name)_must_have_a_display_process");
				}
			}
			return err;

		}
	}

	public static class DisplayActionShouldHaveADisplayOperation extends
			ValidationRule<DisplayActionShouldHaveADisplayOperation, ActionNode> {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public DisplayActionShouldHaveADisplayOperation() {
			super(ActionNode.class, "display_action_should_have_a_display_operation");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<DisplayActionShouldHaveADisplayOperation, ActionNode> applyValidation(ActionNode node) {
			ValidationError<DisplayActionShouldHaveADisplayOperation, ActionNode> w = null;
			if (node._actionType == ActionType.DISPLAY_ACTION) {
				if (node.getDisplayProcess() != null && node.getDisplayOperation() == null && node.hasIncomingEdges()) {
					w = new ValidationError<DisplayActionShouldHaveADisplayOperation, ActionNode>(this, node,
							"display_action_($object.name)_should_have_a_display_operation");
				}
			}
			return w;

		}
	}

	public static class DisplayActionShouldHaveASelectedTab extends ValidationRule<DisplayActionShouldHaveASelectedTab, ActionNode> {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public DisplayActionShouldHaveASelectedTab() {
			super(ActionNode.class, "display_action_should_have_a_selected_tab");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<DisplayActionShouldHaveASelectedTab, ActionNode> applyValidation(ActionNode node) {
			ValidationError<DisplayActionShouldHaveASelectedTab, ActionNode> w = null;
			if (node._actionType == ActionType.DISPLAY_ACTION) {
				if (node.getNextOperationTabKey() == null && node.getDisplayProcess() != null && node.getDisplayOperation() != null
						&& node.hasIncomingEdges()) {
					for (WorkflowPathToOperationNode workflowPath : node.getNextOperationForDisplayAction()) {
						OperationNode nextOpNode = workflowPath.getOperationNode();
						if (nextOpNode != null) {
							if (nextOpNode.getOperationComponent() != null) {
								IEOperationComponent component = nextOpNode.getOperationComponent().getWOComponent();
								if (component.hasTabContainer() && component.hasAtLeastOneTabDefined()) {
									w = new ValidationError<DisplayActionShouldHaveASelectedTab, ActionNode>(this, node,
											"display_action_($object.name)_should_have_a_selected_tab");
								}
							}

							break;
						}
					}
				}
			}
			return w;

		}
	}

	public static class ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton extends
			ValidationRule<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode> {

		public ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton() {
			super(ActionNode.class,
					"action_node_($object.name)_can_only_be_bound_to_buttons_with_hyperlink_type_flexo_action_or_display_action");
		}

		@Override
		public ValidationIssue<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode> applyValidation(ActionNode object) {
			if (object.getAssociatedButtonWidget() != null) {
				HyperlinkType type = object.getAssociatedButtonWidget().getHyperlinkType();
				if (type == null || !type.isFlexoAction() && !type.isDisplayAction()) {
					Vector<FixProposal<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode>> fixes = new Vector<FixProposal<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode>>();
					fixes.add(new ChangeButtonType());
					fixes.add(new DeleteNode());
					return new ValidationError<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode>(this, object,
							"action_node_can_be_bound_to_buttons_with_hyperlink_type_flexo_action_or_display_action", fixes);
				}
			}
			return null;
		}

		public static class DeleteNode extends FixProposal<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode> {

			public DeleteNode() {
				super("delete_this_node");
			}

			@Override
			protected void fixAction() {
				getObject().delete();
			}

		}

		public static class ChangeButtonType extends FixProposal<ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton, ActionNode> {

			@SuppressWarnings("hiding")
			private static final Logger logger = Logger
					.getLogger(ActionNode.ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton.ChangeButtonType.class.getPackage()
							.getName());

			public ChangeButtonType() {
				super("change_button_type");
			}

			@Override
			protected void fixAction() {
				if (getObject().getAssociatedButtonWidget() != null) {
					if (getObject().getActionType() == ActionType.DISPLAY_ACTION) {
						getObject().getAssociatedButtonWidget().setHyperlinkType(HyperlinkType.DISPLAYACTION);
					} else if (getObject().getActionType() == ActionType.FLEXO_ACTION) {
						getObject().getAssociatedButtonWidget().setHyperlinkType(HyperlinkType.FLEXOACTION);
					}
				} else {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("No button associated to this node");
					}
				}
			}

		}
	}

	public List<WorkflowPathToOperationNode> getNextOperationsForAction() {
		if (getActionType() == ActionType.DISPLAY_ACTION) {
			return getNextOperationForDisplayAction();
		} else if (getActionType() == ActionType.FLEXO_ACTION) {
			return getNextOperationsForFlexoAction();
		} else if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Action type " + getActionType().getName() + " is not handled by generators");
		}
		return new ArrayList<WorkflowPathToOperationNode>();
	}

	protected List<WorkflowPathToOperationNode> getNextOperationForDisplayAction() {
		return getNextOperationForDisplayAction(new WorkflowPathToOperationNode());
	}

	/**
	 * @param action
	 * @return
	 */
	protected List<WorkflowPathToOperationNode> getNextOperationForDisplayAction(
			WorkflowPathToOperationNode currentWorkflowPathToOperationNode) {
		if (getDisplayOperation() != null) {
			if (getDisplayOperation().hasWOComponent()) {
				List<WorkflowPathToOperationNode> retval = new ArrayList<WorkflowPathToOperationNode>();
				currentWorkflowPathToOperationNode.setOperationNode(getDisplayOperation());
				if (getDisplayOperation().getNewStatus() != null) {
					currentWorkflowPathToOperationNode.addNewStatus(getDisplayOperation().getNewStatus());
				} else if (getDisplayOperation().getAbstractActivityNode().getNewStatus() != null) {
					currentWorkflowPathToOperationNode.addNewStatus(getDisplayOperation().getAbstractActivityNode().getNewStatus());
				}

				retval.add(currentWorkflowPathToOperationNode);
				return retval;
			} else {
				return getNextOperationForFlexoNode(new ArrayList<AbstractNode>(), new ArrayList<WorkflowPathToOperationNode>(),
						getDisplayOperation(), currentWorkflowPathToOperationNode);
			}
		} else {
			FlexoProcess process = getDisplayProcess();
			if (process == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Display process is null for DISPLAY_ACTION. Validation should catch this");
				}
				return new ArrayList<WorkflowPathToOperationNode>();
			} else {
				return getNextOperationForProcess(new ArrayList<AbstractNode>(), new ArrayList<WorkflowPathToOperationNode>(), process,
						currentWorkflowPathToOperationNode);
			}
		}
	}

	/**
	 * @param action
	 * @return
	 */
	private List<WorkflowPathToOperationNode> getNextOperationsForFlexoAction() {
		WorkflowPathToOperationNode currentWorkflowPath = new WorkflowPathToOperationNode();
		if (getNewStatus() != null) {
			currentWorkflowPath.addNewStatus(getNewStatus());
		}
		return getNextOperationForFlexoNode(new ArrayList<AbstractNode>(), new ArrayList<WorkflowPathToOperationNode>(), this,
				currentWorkflowPath);
	}

	private static List<WorkflowPathToOperationNode> getNextOperationForFlexoNode(List<AbstractNode> visitedNodes,
			List<WorkflowPathToOperationNode> returnedNodes, AbstractNode node,
			WorkflowPathToOperationNode currentWorkflowPathToOperationNode) {
		return getNextOperationForFlexoNode(visitedNodes, returnedNodes, node, currentWorkflowPathToOperationNode, true);
	}

	private static List<WorkflowPathToOperationNode> getNextOperationForFlexoNode(List<AbstractNode> visitedNodes,
			List<WorkflowPathToOperationNode> returnedNodes, AbstractNode node,
			WorkflowPathToOperationNode currentWorkflowPathToOperationNode, boolean includeDeadEndPath) {
		if (visitedNodes.contains(node)) {
			return returnedNodes;
		} else {
			visitedNodes.add(node);
		}
		Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> en;

		// now let's collect postconditions where a token is send
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> v = new Vector<FlexoPostCondition<AbstractNode, AbstractNode>>();
		v.addAll(node.getOutgoingPostConditions()); // there are "normal" post condition

		boolean hasInsertedPathFromContainer = false;
		if (node instanceof FlexoNode && ((FlexoNode) node).isEndNode()) { // here we have reach an end node
			WKFObject container = ((PetriGraphNode) node).getParentPetriGraph().getContainer();
			if (container instanceof AbstractNode) {
				int numberOfReturnedPath = returnedNodes.size();
				getNextOperationForFlexoNode(new ArrayList<AbstractNode>(visitedNodes), returnedNodes, (AbstractNode) container,
						currentWorkflowPathToOperationNode.clone(), false);
				hasInsertedPathFromContainer = numberOfReturnedPath != returnedNodes.size();
			}
			if (container instanceof LOOPOperator) {
				v.addAll(((LOOPOperator) container).getOutgoingPostConditions());
			}
		}
		// if (node instanceof ActionNode) {
		// boolean hasDisplayActionInNextNodes = false;
		// for (FlexoPostCondition<AbstractNode, AbstractNode> edge : v) {
		// AbstractNode end = edge.getEndNode();
		// if (end instanceof FlexoPreCondition) {
		// end = ((FlexoPreCondition) end).getAttachedBeginNode() == null ? ((FlexoPreCondition) end).getAttachedNode()
		// : ((FlexoPreCondition) end).getAttachedBeginNode();
		// }
		// if (end instanceof ActionNode && ((ActionNode)end).getActionType() == ActionType.DISPLAY_ACTION) {
		// hasDisplayActionInNextNodes = true;
		// break;
		// }
		// }
		// if (hasDisplayActionInNextNodes) {
		// //returnedNodes.clear();
		// for (FlexoPostCondition<AbstractNode, AbstractNode> edge : v) {
		// AbstractNode end = edge.getEndNode();
		// if (end instanceof FlexoPreCondition) {
		// end = ((FlexoPreCondition) end).getAttachedBeginNode() == null ? ((FlexoPreCondition) end).getAttachedNode()
		// : ((FlexoPreCondition) end).getAttachedBeginNode();
		// }
		// if (end instanceof ActionNode && ((ActionNode)end).getActionType() == ActionType.DISPLAY_ACTION) {
		// returnedNodes.addAll(((ActionNode)end).getNextOperationForDisplayAction(currentWorkflowPathToOperationNode));
		// }
		// }
		// return returnedNodes;
		// }
		// }

		if (v.size() > 0) {
			en = v.elements();
			while (en.hasMoreElements()) {
				WorkflowPathToOperationNode duplicatedWorkflowPath = currentWorkflowPathToOperationNode.clone();
				FlexoPostCondition<AbstractNode, AbstractNode> post = en.nextElement();
				if (node instanceof IFOperator) {
					duplicatedWorkflowPath.addIfOperator((IFOperator) node, ((IFOperator) node).isPositiveEvaluationPostcondition(post));
				}

				if (post.getIsConditional()
						&& (post.getConditionPrimitive() != null || post.getConditionDescription() != null
								&& post.getConditionDescription().trim().length() > 0)) {
					duplicatedWorkflowPath.addCondition(post.getProcess(), post.getConditionDescription(), post.getConditionPrimitive(),
							true);
				}

				AbstractNode end = post.getEndNode();
				if (end instanceof FlexoPreCondition) {
					end = ((FlexoPreCondition) end).getAttachedBeginNode() == null ? ((FlexoPreCondition) end).getAttachedNode()
							: ((FlexoPreCondition) end).getAttachedBeginNode();
				}
				if (end instanceof PetriGraphNode) {
					if (((PetriGraphNode) end).getNewStatus() != null) {
						duplicatedWorkflowPath.addNewStatus(((PetriGraphNode) end).getNewStatus());
					}
				}
				if (end instanceof ActionNode && ((ActionNode) end).isBeginNode()) {
					end = ((ActionNode) end).getOperationNode();
				}
				if (end instanceof PetriGraphNode) {
					if (((PetriGraphNode) end).getNewStatus() != null) {
						duplicatedWorkflowPath.addNewStatus(((PetriGraphNode) end).getNewStatus());
					}
				}
				if (end instanceof LoopSubProcessNode) {
					if (((PetriGraphNode) end).getNewStatus() != null) {
						duplicatedWorkflowPath.addNewStatus(((PetriGraphNode) end).getNewStatus());
					}
					if (((LoopSubProcessNode) end).getPortMapRegistery() != null
							&& ((LoopSubProcessNode) end).getPortMapRegistery().getAllNewPortmaps().size() > 0) {
						end = ((LoopSubProcessNode) end).getPortMapRegistery().getAllNewPortmaps().firstElement();
					}
				} else if (node instanceof LOOPOperator) {
					if (((LOOPOperator) node).getExecutionPetriGraph().getAllBeginNodes().size() > 0) {
						end = ((LOOPOperator) node).getExecutionPetriGraph().getAllBeginNodes().firstElement();
					}
				}

				if (end instanceof PetriGraphNode && isCreateProcess((PetriGraphNode) end)) {
					duplicatedWorkflowPath.getCreatedProcesses().add(end.getProcess());
				}

				if (end instanceof PetriGraphNode && isDeleteProcess((PetriGraphNode) end, false)) {
					duplicatedWorkflowPath.getDeletedProcesses().add(end.getProcess());
				}

				if (end instanceof OperationNode && ((OperationNode) end).hasWOComponent()) {
					duplicatedWorkflowPath.setOperationNode((OperationNode) end);
					if (((OperationNode) end).getNewStatus() != null) {
						duplicatedWorkflowPath.addNewStatus(((OperationNode) end).getNewStatus());
					}
					returnedNodes.add(duplicatedWorkflowPath);
				} else if (end instanceof FlexoPortMap && ((FlexoPortMap) end).getOperation().getPort() instanceof NewPort) {
					getNextOperationForFlexoNode(visitedNodes, returnedNodes, ((FlexoPortMap) end).getOperation().getPort(),
							duplicatedWorkflowPath);
				} else if (end instanceof FlexoPortMap && ((FlexoPortMap) end).getOperation().getPort() instanceof DeletePort) {
					getNextOperationForFlexoNode(visitedNodes, returnedNodes, ((FlexoPortMap) end).getOperation().getPort(),
							duplicatedWorkflowPath);
				} else if (end instanceof FlexoPortMap && ((FlexoPortMap) end).getOperation().getPort() instanceof InPort) {
					getNextOperationForFlexoNode(visitedNodes, returnedNodes, ((FlexoPortMap) end).getOperation().getPort(),
							duplicatedWorkflowPath);
				} else if (end instanceof FlexoPortMap && ((FlexoPortMap) end).getOperation().getPort() instanceof InOutPort) {
					getNextOperationForFlexoNode(visitedNodes, returnedNodes, ((FlexoPortMap) end).getOperation().getPort(),
							duplicatedWorkflowPath);
				} else if (end instanceof OutPort) {
					Vector<FlexoPortMap> portMaps = ((FlexoPort) end).getAllPortMaps();
					for (FlexoPortMap portMap : portMaps) {
						getNextOperationForFlexoNode(new ArrayList<AbstractNode>(visitedNodes), returnedNodes, portMap,
								duplicatedWorkflowPath.clone());
					}
				} else if (end instanceof ActionNode && ((ActionNode) end).getActionType() == ActionType.DISPLAY_ACTION) {
					returnedNodes.addAll(((ActionNode) end).getNextOperationForDisplayAction(currentWorkflowPathToOperationNode));
				} else {
					getNextOperationForFlexoNode(visitedNodes, returnedNodes, end, duplicatedWorkflowPath);
				}
			}
		} else if (includeDeadEndPath && !hasInsertedPathFromContainer) {
			if (node instanceof PetriGraphNode && isDeleteProcess((PetriGraphNode) node, true)) {
				currentWorkflowPathToOperationNode.getDeletedProcesses().add(node.getProcess());
			}

			returnedNodes.add(currentWorkflowPathToOperationNode);
		}

		return returnedNodes;
	}

	private static boolean isCreateProcess(PetriGraphNode node) {
		return node != null && node.isProcessLevel() && isBeginNode(node);
	}

	private static boolean isBeginNode(AbstractNode node) {
		return node instanceof FlexoNode && ((FlexoNode) node).isBeginNode() || node instanceof EventNode && ((EventNode) node).isStart();
	}

	private static boolean isDeleteProcess(PetriGraphNode node, boolean isDeadEnd) {
		if (isDeadEnd && isEndNode(node)) {
			node = node.getProcessLevelNode();
		}

		return node != null && node.isProcessLevel() && isEndNode(node);
	}

	private static boolean isEndNode(AbstractNode node) {
		return node instanceof FlexoNode && ((FlexoNode) node).isEndNode() || node instanceof EventNode && ((EventNode) node).isEnd();
	}

	private static List<WorkflowPathToOperationNode> getNextOperationForProcess(List<AbstractNode> visitedNodes,
			List<WorkflowPathToOperationNode> returnedNodes, FlexoProcess process,
			WorkflowPathToOperationNode currentWorkflowPathToOperationNode) {
		Enumeration en = process.getAllBeginNodes().elements();
		while (en.hasMoreElements()) {
			AbstractActivityNode node = (AbstractActivityNode) en.nextElement();
			if (!visitedNodes.contains(node)) {
				getNextOperationForFlexoNode(visitedNodes, returnedNodes, node, currentWorkflowPathToOperationNode.clone());
			}
		}
		return returnedNodes;
	}

	// TABS
	public String getTabComponentName() {
		if (getTabComponent() != null) {
			return getTabComponent().getComponentName();
		}
		return null;
	}

	public void setTabComponentName(String aComponentName) throws DuplicateResourceException, OperationAssociatedWithComponentSuccessfully {
		if (getTabComponentName() != null && getTabComponentName().equals(aComponentName)) {
			return;
		}
		if (_tabComponentInstance == null && (aComponentName == null || aComponentName.trim().equals(""))) {
			return;
		}
		ComponentDefinition foundComponent = getProject().getFlexoComponentLibrary().getComponentNamed(aComponentName);
		TabComponentDefinition newComponent = null;
		if (foundComponent instanceof TabComponentDefinition) {
			newComponent = (TabComponentDefinition) foundComponent;
		} else if (foundComponent != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found a component named " + aComponentName + " but this is not a TabComponent. Aborting.");
			}
			throw new DuplicateResourceException(aComponentName);
		}
		if (newComponent == null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating a new Component named:" + aComponentName);
			}
			FlexoComponentFolder selectedFolder = getProject().getFlexoComponentLibrary().getRootFolder()
					.getFolderTyped(FolderType.TAB_FOLDER);
			newComponent = new TabComponentDefinition(aComponentName, getProject().getFlexoComponentLibrary(), selectedFolder, getProject());
		}
		setTabComponent(newComponent);
	}

	public TabComponentDefinition getTabComponent() {
		if (_tabComponentInstance != null) {
			return _tabComponentInstance.getComponentDefinition();
		}
		return null;
	}

	public void setTabComponent(TabComponentDefinition aComponentDefinition) {
		if (_tabComponentInstance != null && _tabComponentInstance.getComponentDefinition() == aComponentDefinition) {
			return;
		}
		if (_tabComponentInstance != null && aComponentDefinition == null) {
			removeTabComponentInstance();
		}
		if (aComponentDefinition != null) {
			setTabActionComponentInstance(new TabComponentInstance(aComponentDefinition, this));
		}
	}

	@Override
	public void setProcess(FlexoProcess p) {
		FlexoProcess old = getProcess();
		super.setProcess(p);
		if (_tabComponentInstance != null) {
			_tabComponentInstance.updateDependancies(old, p);
		}
	}

	public TabComponentInstance getTabActionComponentInstance() {
		return _tabComponentInstance;
	}

	public void setTabActionComponentInstance(TabComponentInstance tabComponentInstance) {
		if (_tabComponentInstance != null) {
			removeTabComponentInstance();
		}
		if (tabComponentInstance.getComponentDefinition() != null || isCreatedByCloning()
				&& tabComponentInstance.getComponentName() != null) {
			_tabComponentInstance = tabComponentInstance;
			_tabComponentInstance.setActionNode(this);
			setChanged();
			notifyAttributeModification("tabActionComponentInstance", null, _tabComponentInstance);
		} else if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("TabComponentInstance does not have a component definition for component named "
					+ tabComponentInstance.getComponentName());
		}
	}

	public void removeTabComponentInstance() {
		if (_tabComponentInstance != null) {
			ComponentInstance oldComponentInstance = _tabComponentInstance;
			_tabComponentInstance.delete();
			_tabComponentInstance = null;
			setChanged();
			notifyAttributeModification("tabActionComponentInstance", oldComponentInstance, null);
		}
	}

	public boolean hasIncomingEdges() {
		/*
		 * if (getIncomingPostConditions() != null && getIncomingPostConditions().size() > 0) return true;
		 */
		Enumeration<FlexoPreCondition> en = getPreConditions().elements();
		while (en.hasMoreElements()) {
			if (en.nextElement().hasIncomingPostConditions()) {
				return true;
			}
		}
		return false;
	}

	public boolean isActivatedByAToken() {
		if (getIncomingPostConditions() != null && getIncomingPostConditions().size() > 0) {
			return true;
		}
		Enumeration<FlexoPreCondition> en = getPreConditions().elements();
		while (en.hasMoreElements()) {
			if (en.nextElement().hasIncomingPostConditions()) {
				return true;
			}
		}
		return false;
	}

	public String getNextOperationTabKey() {
		if (getActionType() == ActionType.DISPLAY_ACTION) {
			for (WorkflowPathToOperationNode workflowPath : getNextOperationForDisplayAction()) {
				OperationNode nextOp = workflowPath.getOperationNode();
				if (nextOp != null) {
					if (nextOp.getOperationComponent().getWOComponent().getTabWidgetForTabComponent(getTabComponent()) != null) {
						return nextOp.getOperationComponent().getWOComponent().getTabWidgetForTabComponent(getTabComponent())
								.getTabKeyForGenerator();
					}
					break;
				}
			}
		}

		return null;
	}

	/**
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#setIsModified()
	 */
	@Override
	public synchronized void setIsModified() {
		if (ignoreNotifications()) {
			return;
		}
		super.setIsModified();
		if (getOperationNode() != null) {
			getOperationNode().setIsModified();
		}
	}

	@Override
	public boolean isInteractive() {
		return isNormalNode();
	}

	public static class FlexoActionMustSendToken extends ValidationRule {
		public FlexoActionMustSendToken() {
			super(ActionNode.class, "flexo_action_must_send_token");
		}

		/**
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue applyValidation(Validable object) {
			ValidationWarning err = null;
			ActionNode node = (ActionNode) object;
			if (node.getActionType() == ActionType.FLEXO_ACTION && !node.isEndNode() && !node.isBeginNode()
					&& (node.getOutgoingPostConditions() == null || node.getOutgoingPostConditions().size() == 0)) {
				err = new ValidationWarning(this, object, "flexo_action_($object.name)_must_send_token");
				err.addToFixProposals(new ChangeToDisplayAction(node));
				err.addToFixProposals(new DeleteFlexoAction(node));
			}
			return err;
		}

		public class DeleteFlexoAction extends FixProposal {
			private ActionNode node;

			public DeleteFlexoAction(ActionNode node) {
				super("delete_flexo_action");
				this.node = node;
			}

			/**
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				if (node.getAssociatedButtonWidget() != null) {
					IEHyperlinkWidget button = node.getAssociatedButtonWidget();
					if (button.getHyperlinkType() == HyperlinkType.FLEXOACTION) {
						button.setHyperlinkType(null);
					}
					if (button.getIsMandatoryFlexoAction()) {
						button.setIsMandatoryFlexoAction(false);
					}
				}
				node.delete();
			}

		}

		public class ChangeToDisplayAction extends FixProposal {

			private ActionNode node;

			/**
			 * @param aMessage
			 */
			public ChangeToDisplayAction(ActionNode node) {
				super("change_flexo_action_($object.name)_into_display_action");
				this.node = node;
			}

			/**
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				node.setActionType(ActionType.DISPLAY_ACTION);
			}

		}
	}
}
