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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.WKFBindingDefinition;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.utils.FlexoModelObjectReference.ReferenceOwner;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.RoleChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.ProgrammingLanguage;

/**
 * This represents a event FlexoNode
 * 
 * @author sguerin
 * 
 */
public class EventNode extends PetriGraphNode implements ExecutableWorkflowElement, ReferenceOwner {

	public enum EVENT_TYPE {
		Start, Intermediate, End, NonInteruptive, NonInteruptiveBoundary, IntermediateDrop
	}

	public enum TriggerType {
		NONE, TIMER, MESSAGE, ERROR, CANCEL, COMPENSATION, CONDITIONAL, LINK, SIGNAL, TERMINATE, MULTIPLE, ESCALATION, MULTIPLEPARA;

		public boolean canBeStarting() {
			return this != CANCEL && this != TERMINATE && this != LINK;
		}

		public boolean canBeEnding() {
			return this != LINK && this != CONDITIONAL && this != TIMER;
		}

		public boolean canBeIntermediate() {
			return this != TERMINATE;
		}
	}

	private static final Logger logger = Logger.getLogger(EventNode.class.getPackage().getName());

	// protected static final ImageIcon MAIL_IN_ICON = new ImageIconResource("Resources/WKF/MailIn.gif");
	//
	// protected static final ImageIcon MAIL_OUT_ICON = new ImageIconResource("Resources/WKF/MailOut.gif");
	//
	// protected static final ImageIcon TIMER_ICON = new ImageIconResource("Resources/WKF/Timer.gif");
	//
	// protected static final ImageIcon TIME_OUT_ICON = new ImageIconResource("Resources/WKF/TimeOut.gif");
	//
	// protected static final ImageIcon FAULT_THROWER_ICON = new ImageIconResource("Resources/WKF/Throw.gif");
	//
	// protected static final ImageIcon FAULT_HANDLER_ICON = new ImageIconResource("Resources/WKF/Catch.gif");
	//
	// protected static final ImageIcon CANCEL_THROWER_ICON = new ImageIconResource("Resources/WKF/CancelThrower.gif");
	//
	// protected static final ImageIcon CANCEL_HANDLER_ICON = new ImageIconResource("Resources/WKF/CancelHandler.gif");
	//
	// protected static final ImageIcon COMPENSATE_THROWER_ICON = new ImageIconResource("Resources/WKF/CompensateThrower.gif");
	//
	// protected static final ImageIcon COMPENSATE_HANDLER_ICON = new ImageIconResource("Resources/WKF/CompensateHandler.gif");
	//
	// protected static final ImageIcon CHECKPOINT_ICON = new ImageIconResource("Resources/WKF/Checkpoint.gif");
	//
	// protected static final ImageIcon REVERT_ICON = new ImageIconResource("Resources/WKF/Revert.gif");

	private AbstractActivityNode boundaryOf;

	private String documentationUrl;

	private FlexoProcess linkedProcess;

	private EVENT_TYPE eventType;

	private TriggerType trigger;

	private boolean isCatching = true;

	private String _fromAddress;
	private String _toAddress;
	private String _mailSubject;
	private String _mailBody;

	private Duration _delay;

	public static final String DATE_BINDING = "dateBinding";
	private AbstractBinding dateBinding;

	private FlexoModelObjectReference<Role> roleReference;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public boolean isMessageSent() {
		return getTrigger() == TriggerType.MESSAGE && !getIsCatching();
	}

	public boolean isMessageReceive() {
		return getTrigger() == TriggerType.MESSAGE && getIsCatching();
	}

	public boolean isTriggerNone() {
		return getTrigger() == TriggerType.NONE;
	}

	public boolean isTriggerError() {
		return getTrigger() == TriggerType.ERROR;
	}

	public boolean isTriggerSignal() {
		return getTrigger() == TriggerType.SIGNAL;
	}

	public boolean isTriggerMessage() {
		return getTrigger() == TriggerType.MESSAGE;
	}

	public boolean isTriggerMultiple() {
		return getTrigger() == TriggerType.MULTIPLE;
	}

	public boolean isTriggerMultiplePara() {
		return getTrigger() == TriggerType.MULTIPLEPARA;
	}

	public boolean isTriggerConditional() {
		return getTrigger() == TriggerType.CONDITIONAL;
	}

	public boolean isTriggerLink() {
		return getTrigger() == TriggerType.LINK;
	}

	public boolean isTriggerTerminate() {
		return getTrigger() == TriggerType.TERMINATE;
	}

	public boolean isTriggerTimer() {
		return getTrigger() == TriggerType.TIMER;
	}

	public boolean isTriggerCancel() {
		return getTrigger() == TriggerType.CANCEL;
	}

	public boolean isTriggerCompensation() {
		return getTrigger() == TriggerType.COMPENSATION;
	}

	public boolean isTriggerEscalation() {
		return getTrigger() == TriggerType.ESCALATION;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public void setTrigger(TriggerType trigger) {
		this.trigger = trigger;
		setChanged();
		notifyObservers(new DataModification("trigger", null, trigger));
	}

	public EVENT_TYPE getEventType() {
		return eventType;
	}

	public void setEventType(EVENT_TYPE eventType) {
		if (!isDeserializing() && getTrigger() != null) {
			switch (eventType) {
			case Start:
				if (!getTrigger().canBeStarting()) {
					return;
				}
				break;
			case Intermediate:
				if (!getTrigger().canBeIntermediate()) {
					return;
				}
				break;
			case End:
				if (!getTrigger().canBeEnding()) {
					return;
				}
				break;
			}
		}
		this.eventType = eventType;
		setChanged();
		notifyObservers(new DataModification("eventType", null, eventType));
	}

	/**
	 * Default constructor
	 */
	public EventNode(FlexoProcess process) {
		super(process);
	}

	public EventNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	@Override
	public String getDefaultName() {
		return FlexoLocalization.localizedForKey(getEventType() + "_" + getTrigger());
	}

	@Override
	public EventNode getNode() {
		return this;
	}

	@Override
	public FlexoLevel getLevel() {
		if (getParentPetriGraph() != null) {
			return getParentPetriGraph().getLevel();
		}
		return null;
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	@Override
	public String getInspectorName() {
		return "EventNode.inspector";
	}

	@Override
	public final boolean delete() {
		super.delete();
		deleteObservers();
		return true;
	}

	// public abstract ImageIcon getImageIcon();

	public boolean isStart() {
		return EVENT_TYPE.Start == getEventType();
	}

	public boolean isEnd() {
		return EVENT_TYPE.End == getEventType();
	}

	public boolean isIntermediate() {
		return EVENT_TYPE.Intermediate == getEventType();
	}

	public boolean isIntermediateDrop() {
		return EVENT_TYPE.IntermediateDrop == getEventType();
	}

	public boolean isStartOrEnd() {
		return isStart() || isEnd();
	}

	// Used when serializing
	public FlexoModelObjectReference<Role> getRoleReference() {
		return roleReference;
	}

	// Used when deserializing
	public void setRoleReference(FlexoModelObjectReference<Role> aRoleReference) {
		this.roleReference = aRoleReference;
	}

	public Role getRole() {
		if (roleReference != null) {
			Role object = roleReference.getObject();
			if (object != null) {
				return object;
			} else {
				return getWorkflow().getCachedRole(roleReference);
			}
		} else {
			return null;
		}
	}

	public void setRole(Role aRole) {
		if (aRole != null && aRole.isCache()) {
			aRole = aRole.getUncachedObject();
			if (aRole == null) {
				return;
			}
		}
		Role oldRole = getRole();
		if (oldRole != aRole) {
			if (roleReference != null) {
				roleReference.delete(false);
				roleReference = null;
			}
			if (aRole != null) {
				roleReference = new FlexoModelObjectReference<Role>(aRole);
				roleReference.setOwner(this);
			}
			setChanged();
			notifyObservers(new RoleChanged(oldRole, aRole));
		}
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
		// TODO Auto-generated method stub

	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		setRole(null);
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		setRole(null);
	}

	@Override
	public void objectSerializationIdChanged(FlexoModelObjectReference<?> reference) {
		setChanged();
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public FlexoProcess getLinkedProcess() {
		if (linkedProcess == null) {
			if (_deserializedReference != null && _deserializedReference.getObject() != null) {
				setLinkedProcess(_deserializedReference.getObject());
			}
		}
		return linkedProcess;
	}

	public void setLinkedProcess(FlexoProcess linkedProcess) {
		this.linkedProcess = linkedProcess;
	}

	// Used when serializing
	public FlexoModelObjectReference<FlexoProcess> getLinkedProcessReference() {
		if (getLinkedProcess() != null) {
			return new FlexoModelObjectReference<FlexoProcess>(getLinkedProcess());
		} else {
			return _deserializedReference;
		}
	}

	private FlexoModelObjectReference<FlexoProcess> _deserializedReference;

	// Used when deserializing
	public void setLinkedProcessReference(FlexoModelObjectReference<FlexoProcess> aProcessReference) {
		FlexoProcess subProcess = aProcessReference.getObject(false); // False because we never know if a loop is possible...
		if (subProcess != null) {
			setLinkedProcess(subProcess);
		} else {
			_deserializedReference = aProcessReference;
		}
	}

	public String getDefaultEnglishName() {
		return FlexoLocalization.localizedForKeyAndLanguage(getClassNameKey(), Language.ENGLISH);
	}

	// =========================================================
	// ============= Control graph management ==================
	// =========================================================

	private static ControlGraphFactory<EventNode> _executionComputingFactory;

	public static void setExecutionComputingFactory(ControlGraphFactory<EventNode> factory) {
		_executionComputingFactory = factory;
	}

	public WorkflowControlGraph<EventNode> getExecution() {
		if (_executionComputingFactory != null) {
			return _executionComputingFactory.getControlGraph(this);
		}
		return null;
	}

	@Override
	public void setProgrammingLanguageForControlGraphComputation(ProgrammingLanguage language) {
		if (getExecution() != null) {
			getExecution().setProgrammingLanguage(language);
		}
	}

	@Override
	public void setInterproceduralForControlGraphComputation(boolean interprocedural) {
		if (getExecution() != null) {
			getExecution().setInterprocedural(interprocedural);
		}
	}

	@Override
	public String getExecutableElementName() {
		return FlexoLocalization.localizedForKeyWithParams("event_($0)", getName());
	}

	@Override
	public String getClassNameKey() {
		return "event_node";
	}

	public String getFromAddress() {
		return _fromAddress;
	}

	public void setFromAddress(String address) {
		_fromAddress = address;
	}

	public String getMailBody() {
		return _mailBody;
	}

	public void setMailBody(String body) {
		_mailBody = body;
	}

	public String getMailSubject() {
		return _mailSubject;
	}

	public void setMailSubject(String subject) {
		_mailSubject = subject;
	}

	public String getToAddress() {
		return _toAddress;
	}

	public void setToAddress(String address) {
		_toAddress = address;
	}

	public Duration getDelay() {
		return _delay;
	}

	public void setDelay(Duration delay) {
		if (_delay == null || !_delay.equals(delay)) {
			Duration oldDelay = _delay;
			_delay = delay;
			setChanged();
			notifyAttributeModification("delay", oldDelay, delay);
		}
	}

	public boolean isCancelHandler() {
		return isTriggerCancel() && getIsCatching();
	}

	public boolean isCancelThrower() {
		return isTriggerCancel() && !getIsCatching();
	}

	public boolean isErrorHandler() {
		return isTriggerError() && getIsCatching();
	}

	public boolean isErrorThrower() {
		return isTriggerError() && !getIsCatching();
	}

	public boolean isCompensateHandler() {
		return isTriggerCompensation() && getIsCatching();
	}

	public boolean isCompensateThrower() {
		return isTriggerCompensation() && !getIsCatching();
	}

	/**
	 * @deprecated : used in process.js.vm one day is someone have time to look at this...
	 * @return
	 */
	@Deprecated
	public boolean isTimeOut() {
		return isTriggerTimer() && isEnd();
	}

	/**
	 * @deprecated : used in process.js.vm one day is someone have time to look at this...
	 * @return
	 */
	@Deprecated
	public boolean isTimer() {
		return isTriggerTimer() && !isEnd();
	}

	public WKFBindingDefinition getDateBindingDefinition() {
		if (getProject() != null) {
			WKFBindingDefinition returned = WKFBindingDefinition.get(this, DATE_BINDING, Date.class, BindingDefinitionType.GET, false);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Returned WKFBindingDefinition : " + returned);
			}
			return returned;
		}
		return null;
	}

	public AbstractBinding getDateBinding() {
		if (isBeingCloned()) {
			return null;
		}
		return dateBinding;
	}

	public void setDateBinding(AbstractBinding aConditionPrimitive) {
		AbstractBinding oldBindingValue = dateBinding;
		this.dateBinding = aConditionPrimitive;
		if (this.dateBinding != null) {
			this.dateBinding.setOwner(this);
			this.dateBinding.setBindingDefinition(getDateBindingDefinition());
		}
		setChanged();
		notifyObservers(new WKFAttributeDataModification(DATE_BINDING, oldBindingValue, aConditionPrimitive));
	}

	public boolean getIsCatching() {
		return isCatching;
	}

	public void setIsCatching(boolean isCatching) {
		this.isCatching = isCatching;
		setChanged();
		notifyModification("isCatching", !isCatching, isCatching);
	}

	// Usefull factory method used by Palette
	public static EventNode makeEventNode(TriggerType trigger, EVENT_TYPE eventType, boolean isCatching) {
		EventNode reply = new EventNode((FlexoProcess) null);
		reply.setEventType(eventType);
		reply.setTrigger(trigger);
		reply.setIsCatching(isCatching);
		return reply;
	}

	private String boundaryActivityID;

	// Used when serializing
	public String getBoundaryActivityID() {
		if (boundaryActivityID == null && boundaryOf == null) {
			return null;
		}
		if (boundaryActivityID == null) {
			return boundaryOf.getFlexoID() + "";
		}
		return boundaryActivityID;
	}

	// Used when deserializing
	public void setBoundaryActivityID(String aRoleReference) {
		// if(!isCreatedByCloning())
		boundaryActivityID = aRoleReference;
	}

	public void setBoundaryOf(AbstractActivityNode boundaryOf) {
		this.boundaryOf = boundaryOf;
	}

	public AbstractActivityNode getBoundaryOf() {
		if (boundaryOf == null && boundaryActivityID == null) {
			return null;
		}
		if (boundaryOf == null) {
			long id = new Long(boundaryActivityID);
			for (AbstractActivityNode node : getProcess().getAllAbstractActivityNodes()) {
				if (node.getFlexoID() == id) {
					boundaryOf = node;
					return boundaryOf;
				}
			}
		}
		return boundaryOf;
	}

	public static class StartEventCannotHaveIncomingTokenFlow extends ValidationRule<StartEventCannotHaveIncomingTokenFlow, EventNode> {
		public StartEventCannotHaveIncomingTokenFlow() {
			super(EventNode.class, "StartEventCannotHaveIncomingTokenFlow");
		}

		/**
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<StartEventCannotHaveIncomingTokenFlow, EventNode> applyValidation(EventNode node) {
			if (node.isStart() && node.getIncomingPostConditions().size() > 0) {
				for (FlexoPostCondition fpc : node.getIncomingPostConditions()) {
					if (fpc instanceof TokenEdge) {
						return new ValidationWarning<StartEventCannotHaveIncomingTokenFlow, EventNode>(this, node,
								"StartEventCannotHaveIncomingTokenFlow");
					}
				}
			}
			return null;
		}
	}

	public static class EndEventCannotHaveAStartingTokenFlow extends ValidationRule<EndEventCannotHaveAStartingTokenFlow, EventNode> {
		public EndEventCannotHaveAStartingTokenFlow() {
			super(EventNode.class, "EndEventCannotHaveAStartingTokenFlow");
		}

		/**
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<EndEventCannotHaveAStartingTokenFlow, EventNode> applyValidation(EventNode node) {
			if (node.isEnd() && node.getParentPetriGraph().isRootPetriGraph() && node.getOutgoingPostConditions().size() > 0) {
				for (FlexoPostCondition fpc : node.getOutgoingPostConditions()) {
					if (fpc instanceof TokenEdge) {
						return new ValidationWarning<EndEventCannotHaveAStartingTokenFlow, EventNode>(this, node,
								"EndEventCannotHaveAStartingTokenFlow");
					}

				}

			}
			return null;
		}
	}

	public static class IntermediateEventCannotHaveMoreThanOneIncomingEdge extends
			ValidationRule<IntermediateEventCannotHaveMoreThanOneIncomingEdge, EventNode> {
		public IntermediateEventCannotHaveMoreThanOneIncomingEdge() {
			super(EventNode.class, "IntermediateEventCannotHaveMoreThanOneIncomingEdge");
		}

		/**
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<IntermediateEventCannotHaveMoreThanOneIncomingEdge, EventNode> applyValidation(EventNode node) {
			if (node.isIntermediate() && node.getIncomingPostConditions().size() > 1) {
				return new ValidationWarning<IntermediateEventCannotHaveMoreThanOneIncomingEdge, EventNode>(this, node,
						"IntermediateEventCannotHaveMoreThanOneIncomingEdge");
			}
			return null;
		}
	}

	public static class IntermediateEventCannotHaveMoreThanOneOutgoingEdge extends
			ValidationRule<IntermediateEventCannotHaveMoreThanOneOutgoingEdge, EventNode> {
		public IntermediateEventCannotHaveMoreThanOneOutgoingEdge() {
			super(EventNode.class, "IntermediateEventCannotHaveMoreThanOneOutgoingEdge");
		}

		/**
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<IntermediateEventCannotHaveMoreThanOneOutgoingEdge, EventNode> applyValidation(EventNode node) {
			if (node.isIntermediate() && node.getOutgoingPostConditions().size() > 1) {
				return new ValidationWarning<IntermediateEventCannotHaveMoreThanOneOutgoingEdge, EventNode>(this, node,
						"IntermediateEventCannotHaveMoreThanOneOutgoingEdge");
			}
			return null;
		}
	}

	public static class NodeAfterEventBasedGatewayRules extends ValidationRule<NodeAfterEventBasedGatewayRules, EventNode> {

		public NodeAfterEventBasedGatewayRules() {
			super(EventNode.class, "node_after_event_based_gateway_rules");
		}

		@Override
		public ValidationIssue<NodeAfterEventBasedGatewayRules, EventNode> applyValidation(EventNode operator) {
			boolean hasIncomingPostConditionFromEventGateway = false;
			for (FlexoPostCondition<AbstractNode, AbstractNode> post : operator.getIncomingPostConditions()) {
				hasIncomingPostConditionFromEventGateway = post.isStartingFromAnExclusiveGateway();
				if (hasIncomingPostConditionFromEventGateway) {
					break;
				}
			}
			if (hasIncomingPostConditionFromEventGateway) {
				if (!operator.isIntermediate()) {
					return new ValidationError<NodeAfterEventBasedGatewayRules, EventNode>(this, operator,
							"event_gateway_output_must_be_intermediate_catching_events_or_activities", operator.getTrigger()
									.canBeIntermediate() ? new ChangeToIntermediateEvent() : null);
				}
				if (!operator.getIsCatching()) {
					return new ValidationError<NodeAfterEventBasedGatewayRules, EventNode>(this, operator,
							"event_gateway_output_must_be_intermediate_catching_events_or_activities", new ChangeToCatchingEvent());
				}
			}
			return null;
		}

		public static class ChangeToIntermediateEvent extends FixProposal<NodeAfterEventBasedGatewayRules, EventNode> {

			public ChangeToIntermediateEvent() {
				super("change_events_to_intermediate_event");
			}

			@Override
			protected void fixAction() {
				if (getObject().getTrigger().canBeIntermediate()) {
					getObject().setEventType(EVENT_TYPE.Intermediate);
				}
			}
		}

		public static class ChangeToCatchingEvent extends FixProposal<NodeAfterEventBasedGatewayRules, EventNode> {

			public ChangeToCatchingEvent() {
				super("change_events_to_catching_event");
			}

			@Override
			protected void fixAction() {
				getObject().setIsCatching(true);
			}
		}
	}
}
