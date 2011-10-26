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
package org.openflexo.icon;

import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.EVENT_TYPE;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Utility class containing all icons used in context of WKFModule
 * 
 * @author sylvain
 *
 */
public class WKFIconLibrary extends IconLibrary {

	private static final Logger logger = Logger.getLogger(WKFIconLibrary.class.getPackage().getName());

	// Module icons
	public static final ImageIconResource WKF_SMALL_ICON = new ImageIconResource("Icons/WKF/WKF_A_Small.gif");
	public static final ImageIconResource WKF_MEDIUM_ICON = new ImageIconResource("Icons/WKF/module-wkf.png");
	public static final ImageIconResource WKF_MEDIUM_ICON_WITH_HOVER = new ImageIconResource("Icons/WKF/module-wkf-hover.png");
	public static final ImageIconResource WKF_BIG_ICON = new ImageIconResource("Icons/WKF/module-wkf-big.png");

	// Perspective icons
	public static final ImageIcon WKF_BPEP_ACTIVE_ICON = new ImageIconResource("Icons/WKF/WorkflowPerspective_A.gif");
	public static final ImageIcon WKF_BPEP_SELECTED_ICON = new ImageIconResource("Icons/WKF/WorkflowPerspective_S.gif");
	public static final ImageIcon WKF_SWLP_ACTIVE_ICON = new ImageIconResource("Icons/WKF/SwimmingLanesPerspective_A.gif");
	public static final ImageIcon WKF_SWLP_SELECTED_ICON = new ImageIconResource("Icons/WKF/SwimmingLanesPerspective_S.gif");
	public static final ImageIcon WKF_RP_ACTIVE_ICON = new ImageIconResource("Icons/WKF/RolePerspective_A.gif");
	public static final ImageIcon WKF_RP_SELECTED_ICON = new ImageIconResource("Icons/WKF/RolePerspective_S.gif");
	
	// Editor icons
	public static final ImageIcon FLAT_ICON = new ImageIconResource("Icons/WKF/Flat.gif");
	public static final ImageIcon EXPANDABLE_ICON = new ImageIconResource("Icons/WKF/Expandable.gif");
	
	public static final IconMarker WS_MARKER = new IconMarker(new ImageIconResource("Icons/WKF/MarkerWS.gif"), 12, 9);

	public static final ImageIcon MULTIPLE_INSTANCE_SUBPROCESS_ICON = new ImageIconResource("Icons/WKF/MultipleInstanceProcess.gif");
	public static final ImageIcon LOOP_SUBPROCESS_ICON = new ImageIconResource("Icons/WKF/LoopProcess.gif");
	public static final ImageIcon WS_CALL_SUBPROCESS_ICON = new ImageIconResource("Icons/WKF/WSCallProcess.gif");

	// Used in swimming lane representation
	public static final ImageIconResource MINUS = new ImageIconResource("Icons/WKF/SWL/minus.gif");
	public static final ImageIconResource PLUS = new ImageIconResource("Icons/WKF/SWL/plus.gif");
	public static final ImageIconResource TRIANGLE_UP = new ImageIconResource("Icons/WKF/SWL/ArrowUp.gif");
	public static final ImageIconResource TRIANGLE_DOWN = new ImageIconResource("Icons/WKF/SWL/ArrowDown.gif");
	public static final ImageIconResource TRIANGLE_LEFT = new ImageIconResource("Icons/WKF/SWL/ArrowLeft.gif");
	public static final ImageIconResource TRIANGLE_RIGHT = new ImageIconResource("Icons/WKF/SWL/ArrowRight.gif");

	// Small model icons
	public static final ImageIcon WORKFLOW_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Library_WKF.gif");
	public static final ImageIcon ROLE_LIBRARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Library_Role.gif");
	public static final ImageIcon IMPORTED_PROCESS_LIBRARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/ImportedLibrary_WKF.gif");
	public static final ImageIcon IMPORTED_ROLE_LIBRARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/ImportedLibrary_Role.gif");
	public static final ImageIconResource PROCESS_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallProcess.gif");
	public static final ImageIcon PROCESS_FOLDER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/ProcessFolder.gif");
	public static final ImageIcon IMPORTED_PROCESS_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallImportedProcess.gif");
	public static final ImageIcon SUBPROCESS_NODE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallSubProcessNode.gif");
	public static final ImageIcon ACTIVITY_NODE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallActivity.gif");
	public static final ImageIcon ACTIVITY_GROUP_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/ActivityGroup.png");
	public static final ImageIcon OPERATION_NODE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallOperation.gif");
	public static final ImageIcon ACTION_NODE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallAction.gif");
	public static final ImageIcon OPERATOR_AND_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallAnd.gif");
	public static final ImageIcon OPERATOR_INCLUSIVE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallInclusive.gif");
	public static final ImageIcon OPERATOR_EXCLUSIVE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallExclusiveEventBased.gif");
	public static final ImageIcon OPERATOR_COMPLEX_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallComplexOperator.gif");
	public static final ImageIcon OPERATOR_OR_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallOr.gif");
	public static final ImageIcon OPERATOR_IF_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallIf.gif");
	public static final ImageIcon OPERATOR_LOOP_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallLoop.gif");
	public static final ImageIcon OPERATOR_SWITCH_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallSwitch.gif");
	public static final ImageIcon ARTEFACT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallArtefact.gif");
	public static final ImageIcon EVENT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallFaultThrower.gif");
	public static final ImageIcon START_EVENT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallStart.gif");
	public static final ImageIcon END_EVENT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallEnd.gif");
	public static final ImageIcon ROLE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallRole.gif");
	public static final ImageIcon IMPORTED_ROLE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallImportedRole.gif");
	public static final ImageIcon SYSTEM_ROLE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallSystemRole.gif");
	public static final ImageIcon STATUS_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallStatus.gif");
	//public static final ImageIcon DEADLINE_ICON = new ImageIconResource("Icons/Model/WKF/SmallDeadLine.gif");
	public static final ImageIcon POSTCONDITION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPostCondition.gif");
	public static final ImageIcon PRECONDITION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPrecondition.gif");
	public static final ImageIcon PORT_REGISTERY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortRegistery.gif");
	public static final ImageIcon BEGIN_ACTIVITY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallBeginActivity.gif");
	public static final ImageIcon END_ACTIVITY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallEndActivity.gif");
	public static final ImageIcon SELF_EXECUTABLE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallSelfExecutable.gif");
	public static final ImageIcon BEGIN_OPERATION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallBeginOperation.gif");
	public static final ImageIcon END_OPERATION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallEndOperation.gif");
	public static final ImageIcon BEGIN_ACTION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallBeginAction.gif");
	public static final ImageIcon END_ACTION_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallEndAction.gif");

	public static final ImageIcon TASKTYPE_BUSINESSRULE = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskRule.gif");
	public static final ImageIcon TASKTYPE_MANUAL = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskManual.gif");
	public static final ImageIcon TASKTYPE_USER = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskUser.gif");
	public static final ImageIcon TASKTYPE_SEND = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskSend.gif");
	public static final ImageIcon TASKTYPE_RECEIVE = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskReceive.gif");
	public static final ImageIcon TASKTYPE_SCRIPT = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskScript.gif");
	public static final ImageIcon TASKTYPE_SERVICE = new ImageIconResource("Icons/Model/WKF/SmallIcons/TaskService.gif");

	public static final ImageIcon SMALL_DELETE_PORT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortDel.gif");
	public static final ImageIcon SMALL_IN_OUT_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInOutLeft.gif");
	public static final ImageIcon SMALL_IN_OUT_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInOutRight.gif");
	public static final ImageIcon SMALL_IN_OUT_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInOutTop.gif");
	public static final ImageIcon SMALL_IN_OUT_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInOutBottom.gif");
	public static final ImageIcon SMALL_IN_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInLeft.gif");
	public static final ImageIcon SMALL_IN_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInRight.gif");
	public static final ImageIcon SMALL_IN_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInTop.gif");
	public static final ImageIcon SMALL_IN_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortInBottom.gif");
	public static final ImageIcon SMALL_NEW_PORT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortNew.gif");
	public static final ImageIcon SMALL_OUT_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortOutLeft.gif");
	public static final ImageIcon SMALL_OUT_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortOutTop.gif");
	public static final ImageIcon SMALL_OUT_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortOutRight.gif");
	public static final ImageIcon SMALL_OUT_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/SmallPortOutBottom.gif");

	// Big model icons
	public static final ImageIcon SELF_EXECUTABLE_IMAGE = new ImageIconResource("Icons/Model/WKF/BigIcons/SelfExecutable.gif");
	public static final ImageIcon AND_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/AndOperator.gif");
	public static final ImageIcon AND_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/AndOperatorDisabled.gif");
	public static final ImageIcon EXCLUSIVE_EVENT_BASED_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/ExclusiveEventBasedOperator.gif");
	public static final ImageIcon EXCLUSIVE_EVENT_BASED_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/ExclusiveEventBasedOperatorDisabled.gif");
	public static final ImageIcon COMPLEX_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/ComplexOperator.gif");
	public static final ImageIcon COMPLEX_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/ComplexOperatorDisabled.gif");
	public static final ImageIcon INCLUSIVE_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/InclusiveOperator.gif");
	public static final ImageIcon INCLUSIVE_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/InclusiveOperatorDisabled.gif");
	public static final ImageIcon OR_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/OrOperator.gif");
	public static final ImageIcon OR_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/OrOperatorDisabled.gif");
	public static final ImageIcon IF_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/IfOperator.gif");
	public static final ImageIcon IF_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/IfOperatorDisabled.gif");
	public static final ImageIcon LOOP_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/LoopOperator.gif");
	public static final ImageIcon LOOP_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/LoopOperatorDisabled.gif");
	public static final ImageIcon SWITCH_OPERATOR_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/SwitchOperator.gif");
	public static final ImageIcon SWITCH_OPERATOR_DISABLED_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/SwitchOperatorDisabled.gif");

	public static final ImageIcon BIG_DELETE_PORT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortDel.gif");
	public static final ImageIcon BIG_IN_OUT_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInOutLeft.gif");
	public static final ImageIcon BIG_IN_OUT_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInOutRight.gif");
	public static final ImageIcon BIG_IN_OUT_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInOutTop.gif");
	public static final ImageIcon BIG_IN_OUT_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInOutBottom.gif");
	public static final ImageIcon BIG_IN_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInLeft.gif");
	public static final ImageIcon BIG_IN_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInRight.gif");
	public static final ImageIcon BIG_IN_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInTop.gif");
	public static final ImageIcon BIG_IN_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortInBottom.gif");
	public static final ImageIcon BIG_NEW_PORT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortNew.gif");
	public static final ImageIcon BIG_OUT_PORT_LEFT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortOutLeft.gif");
	public static final ImageIcon BIG_OUT_PORT_TOP_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortOutTop.gif");
	public static final ImageIcon BIG_OUT_PORT_RIGHT_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortOutRight.gif");
	public static final ImageIcon BIG_OUT_PORT_BOTTOM_ICON = new ImageIconResource("Icons/Model/WKF/BigIcons/BigPortOutBottom.gif");


	// Events icons
	public static final ImageIcon CANCEL_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CancelEnd.gif");
	public static final ImageIcon CANCEL_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CancelIntermediate.gif");
	public static final ImageIcon COMPENSATION_DROP_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CompensationDropEnd.gif");
	public static final ImageIcon COMPENSATION_DROP_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CompensationDropIntermediate.gif");
	public static final ImageIcon COMPENSATION_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CompensationIntermediate.gif");
	public static final ImageIcon COMPENSATION_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/CompensationStart.gif");
	public static final ImageIcon CONDITION_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ConditionBoundary.gif");
	public static final ImageIcon CONDITION_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ConditionIntermediate.gif");
	public static final ImageIcon CONDITION_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ConditionStart.gif");
	public static final ImageIcon CONDITION_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ConditionStartNonInterupt.gif");
	public static final ImageIcon DEFAULT_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/DefaultEnd.gif");
	public static final ImageIcon DEFAULT_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/DefautIntermediate.gif");
	public static final ImageIcon ERROR_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ErrorIntermediate.gif");
	public static final ImageIcon ERROR_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ErrorStart.gif");
	public static final ImageIcon ERROR_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/ErrorTerminate.gif");
	public static final ImageIcon ESCALATION_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationBoundary.gif");
	public static final ImageIcon ESCALATION_DROP_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationDropIntermediate.gif");
	public static final ImageIcon ESCALATION_DROP_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationDropTerminate.gif");
	public static final ImageIcon ESCALATION_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationIntermediate.gif");
	public static final ImageIcon ESCALATION_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationStart.gif");
	public static final ImageIcon ESCALATION_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/EscalationStartNonInterrupt.gif");
	public static final ImageIcon MAIL_IN_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/StartNonInteruptReceiveMessage.gif");
	public static final ImageIcon MAIL_IN_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/StartReceiveMessage.gif");
	public static final ImageIcon MAIL_IN_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/IntermediateReceiveMessage.gif");
	public static final ImageIcon MAIL_IN_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/BoundaryReceiveMessage.gif");
	public static final ImageIcon MAIL_OUT_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/IntermediateSendMessage.gif");
	public static final ImageIcon MAIL_OUT_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/TerminateSendMessage.gif");
	public static final ImageIcon TIMER_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/IntermediateTimer.gif");
	public static final ImageIcon TIMER_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/BoundaryTimer.gif");
	public static final ImageIcon TIMER_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/TimerNonInterruptStart.gif");
	public static final ImageIcon TIMER_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/TimerStart.gif");
	public static final ImageIcon LINK_DROP_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/LinkDropIntermediate.gif");
	public static final ImageIcon LINK_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/LinkIntermediate.gif");
	public static final ImageIcon MULTIPLE_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleBoundary.gif");
	public static final ImageIcon MULTIPLE_DROP_END_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleDropEnd.gif");
	public static final ImageIcon MULTIPLE_DROP_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleDropIntermediate.gif");
	public static final ImageIcon MULTIPLE_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleIntermediate.gif");
	public static final ImageIcon MULTIPLE_PARA_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleParaBoundary.gif");
	public static final ImageIcon MULTIPLE_PARA_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleParaIntermediate.gif");
	public static final ImageIcon MULTIPLE_PARA_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleParaStart.gif");
	public static final ImageIcon MULTIPLE_PARA_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleParaStartNonInterrupt.gif");
	public static final ImageIcon MULTIPLE_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleStart.gif");
	public static final ImageIcon MULTIPLE_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/MultipleStartNonInterrupt.gif");
	public static final ImageIcon SIGNAL_BOUNDARY_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalBoundary.gif");
	public static final ImageIcon SIGNAL_DROP_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalDropIntermediate.gif");
	public static final ImageIcon SIGNAL_DROP_TERMINATE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalDropTerminate.gif");
	public static final ImageIcon SIGNAL_INTER_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalIntermediate.gif");
	public static final ImageIcon SIGNAL_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalStart.gif");
	public static final ImageIcon SIGNAL_START_NON_INTERRUPT_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/SignalStartNonInterrupt.gif");
	public static final ImageIcon DEFAULT_START_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/StartDefault.gif");
	public static final ImageIcon TERMINATE_ICON = new ImageIconResource("Icons/Model/WKF/SmallIcons/Events/Terminate.gif");
	
	public static Icon getIconForEventNode(EventNode event)
	{
		if (event.isTriggerNone()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.DEFAULT_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.DEFAULT_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.DEFAULT_END_ICON;
			}
		} else if (event.isTriggerMessage()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.MAIL_IN_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MAIL_IN_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MAIL_IN_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MAIL_IN_BOUNDARY_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.MAIL_OUT_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.MAIL_OUT_END_ICON;
			}
		} else if (event.isTriggerTimer()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.TIMER_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.TIMER_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.TIMER_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.TIMER_BOUNDARY_ICON;
			}
		} else if (event.isTriggerError()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.ERROR_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.ERROR_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.ERROR_END_ICON;
			}
		} else if (event.isTriggerEscalation()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.ESCALATION_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.ESCALATION_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.ESCALATION_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.ESCALATION_BOUNDARY_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.ESCALATION_DROP_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.ESCALATION_DROP_END_ICON;
			}
		}else if (event.isTriggerCancel()) {
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.CANCEL_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.CANCEL_END_ICON;
			}
		} else if (event.isTriggerCompensation()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.COMPENSATION_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.COMPENSATION_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.COMPENSATION_DROP_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.COMPENSATION_DROP_END_ICON;
			}
		} else if (event.isTriggerConditional()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.CONDITION_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.CONDITION_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.CONDITION_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.CONDITION_BOUNDARY_ICON;
			}
		} else if (event.isTriggerLink()) {
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.LINK_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.LINK_DROP_INTER_ICON;
			}
		} else if (event.isTriggerSignal()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.SIGNAL_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.SIGNAL_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.SIGNAL_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.SIGNAL_BOUNDARY_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.SIGNAL_DROP_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.SIGNAL_DROP_TERMINATE_ICON;
			}
		} else if (event.isTriggerTerminate()) {
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.TERMINATE_ICON;
			}
		} else if (event.isTriggerMultiple()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.MULTIPLE_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MULTIPLE_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MULTIPLE_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MULTIPLE_BOUNDARY_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.IntermediateDrop) {
				return WKFIconLibrary.MULTIPLE_DROP_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.End) {
				return WKFIconLibrary.MULTIPLE_DROP_END_ICON;
			}
		} else if (event.isTriggerMultiplePara()) {
			if(event.getEventType()==EVENT_TYPE.Start) {
				return WKFIconLibrary.MULTIPLE_PARA_START_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptive) {
				return WKFIconLibrary.MULTIPLE_PARA_START_NON_INTERRUPT_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.Intermediate) {
				return WKFIconLibrary.MULTIPLE_PARA_INTER_ICON;
			}
			if(event.getEventType()==EVENT_TYPE.NonInteruptiveBoundary) {
				return WKFIconLibrary.MULTIPLE_PARA_BOUNDARY_ICON;
			}
		}
		return null;
	}

	public static ImageIcon getImageIconForFlexoPort(FlexoPort port)
	{
		if (port instanceof OutPort) {
			return WKFIconLibrary.BIG_OUT_PORT_LEFT_ICON;
		}
		if (port instanceof InPort) {
			return WKFIconLibrary.BIG_IN_PORT_LEFT_ICON;
		}
		if (port instanceof InOutPort) {
			return WKFIconLibrary.BIG_IN_OUT_PORT_LEFT_ICON;
		}
		if (port instanceof NewPort) {
			return WKFIconLibrary.BIG_NEW_PORT_ICON;
		}
		if (port instanceof DeletePort) {
			return WKFIconLibrary.BIG_DELETE_PORT_ICON;
		}
		return null;
	}

	public static ImageIcon getSmallImageIconForFlexoPort(FlexoPort port)
	{
		return getSmallImageIconForFlexoPort(port,PortMapRegistery.WEST);
	}

	public static ImageIcon getSmallImageIconForFlexoPort(FlexoPort port, int orientation)
	{
		if (port instanceof NewPort) {
			return WKFIconLibrary.SMALL_NEW_PORT_ICON;
		}
		if (port instanceof DeletePort) {
			return WKFIconLibrary.SMALL_DELETE_PORT_ICON;
		}
		switch (orientation) {
		case PortMapRegistery.WEST:
			if (port instanceof OutPort) {
				return WKFIconLibrary.SMALL_OUT_PORT_LEFT_ICON;
			}
			if (port instanceof InPort) {
				return WKFIconLibrary.SMALL_IN_PORT_LEFT_ICON;
			}
			if (port instanceof InOutPort) {
				return WKFIconLibrary.SMALL_IN_OUT_PORT_LEFT_ICON;
			}
		case PortMapRegistery.NORTH:
			if (port instanceof OutPort) {
				return WKFIconLibrary.SMALL_OUT_PORT_TOP_ICON;
			}
			if (port instanceof InPort) {
				return WKFIconLibrary.SMALL_IN_PORT_TOP_ICON;
			}
			if (port instanceof InOutPort) {
				return WKFIconLibrary.SMALL_IN_OUT_PORT_TOP_ICON;
			}
		case PortMapRegistery.EAST:
			if (port instanceof OutPort) {
				return WKFIconLibrary.SMALL_OUT_PORT_RIGHT_ICON;
			}
			if (port instanceof InPort) {
				return WKFIconLibrary.SMALL_IN_PORT_RIGHT_ICON;
			}
			if (port instanceof InOutPort) {
				return WKFIconLibrary.SMALL_IN_OUT_PORT_RIGHT_ICON;
			}
		case PortMapRegistery.SOUTH:
			if (port instanceof OutPort) {
				return WKFIconLibrary.SMALL_OUT_PORT_BOTTOM_ICON;
			}
			if (port instanceof InPort) {
				return WKFIconLibrary.SMALL_IN_PORT_BOTTOM_ICON;
			}
			if (port instanceof InOutPort) {
				return WKFIconLibrary.SMALL_IN_OUT_PORT_BOTTOM_ICON;
			}
		default:
			return null;
		}
	}

	public static ImageIcon getImageIconForPortmap(FlexoPortMap portmap) {
		if ((portmap.getOperation() != null) && (portmap.getOperation().getPort() != null)) {
			return getSmallImageIconForFlexoPort(portmap.getOperation().getPort());
		}
		return null;
	}

	public static ImageIcon getImageIconForPortmap(FlexoPortMap portmap,int orientation) {
		if ((portmap.getOperation() != null) && (portmap.getOperation().getPort() != null)) {
			return getSmallImageIconForFlexoPort(portmap.getOperation().getPort(),orientation);
		}
		return null;
	}


	public static ImageIcon getSmallImageIconForServiceOperation(ServiceOperation so) {
		if (so.isInOperation()) {
			return WKFIconLibrary.SMALL_IN_PORT_LEFT_ICON;
		} else if (so.isInOutOperation()) {
			return WKFIconLibrary.SMALL_IN_OUT_PORT_RIGHT_ICON;
		} else if (so.isOutOperation()) {
			return WKFIconLibrary.SMALL_OUT_PORT_LEFT_ICON;
		} else {
			return null;
		}
	}

	public static ImageIcon iconForObject(WKFObject object)
	{
		logger.warning("iconForObject(WKFObject) not implemented yet");
		return null;
	}

}
