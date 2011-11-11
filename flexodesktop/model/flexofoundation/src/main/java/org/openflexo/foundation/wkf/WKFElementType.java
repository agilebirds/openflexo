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
package org.openflexo.foundation.wkf;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.MultipleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OROperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;

public enum WKFElementType {

	NORMAL_ACTIVITY,
	BEGIN_ACTIVITY,
	END_ACTIVITY,
	SELF_EXECUTABLE_ACTIVITY,
	MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE,
	MULTIPLE_INSTANCE_SEQUENTIAL_SUB_PROCESS_NODE,
	SINGLE_INSTANCE_SUB_PROCESS_NODE,
	LOOP_SUB_PROCESS_NODE,
	WS_CALL_SUB_PROCESS_NODE,
	NORMAL_OPERATION,
	BEGIN_OPERATION,
	END_OPERATION,
	SELF_EXECUTABLE_OPERATION,
	FLEXO_ACTION,
	DISPLAY_ACTION,
	BEGIN_ACTION,
	END_ACTION,
	SELF_EXECUTABLE_ACTION,
	AND_OPERATOR,
	OR_OPERATOR,
	IF_OPERATOR,
	LOOP_OPERATOR,
	TIMER,
	TIME_OUT,
	MAIL_IN,
	MAIL_OUT,
	FAULT_HANDLER,
	FAULT_THROWER,
	CANCEL_HANDLER,
	CANCEL_THROWER,
	CHECKPOINT,
	REVERT,
	ANNOTATION,
	BOUNDING_BOX;

	private static final Logger logger = Logger.getLogger(WKFElementType.class.getPackage().getName());

	public WKFObject instanciateNewObject() {
		WKFObject returned = null;
		if (this == NORMAL_ACTIVITY) {
			ActivityNode activity = new ActivityNode((FlexoProcess) null);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == BEGIN_ACTIVITY) {
			ActivityNode activity = new ActivityNode((FlexoProcess) null);
			activity.setNodeType(NodeType.BEGIN);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == END_ACTIVITY) {
			ActivityNode activity = new ActivityNode((FlexoProcess) null);
			activity.setNodeType(NodeType.END);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == SELF_EXECUTABLE_ACTIVITY) {
			SelfExecutableActivityNode activity = new SelfExecutableActivityNode((FlexoProcess) null);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE) {
			MultipleInstanceSubProcessNode activity = new MultipleInstanceSubProcessNode((FlexoProcess) null);
			activity.setIsSequential(false);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == MULTIPLE_INSTANCE_SEQUENTIAL_SUB_PROCESS_NODE) {
			MultipleInstanceSubProcessNode activity = new MultipleInstanceSubProcessNode((FlexoProcess) null);
			activity.setIsSequential(true);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == SINGLE_INSTANCE_SUB_PROCESS_NODE) {
			SingleInstanceSubProcessNode activity = new SingleInstanceSubProcessNode((FlexoProcess) null);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == LOOP_SUB_PROCESS_NODE) {
			LoopSubProcessNode activity = new LoopSubProcessNode((FlexoProcess) null);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == WS_CALL_SUB_PROCESS_NODE) {
			WSCallSubProcessNode activity = new WSCallSubProcessNode((FlexoProcess) null);
			activity.setName(activity.getDefaultName());
			returned = activity;
		} else if (this == NORMAL_OPERATION) {
			OperationNode operation = new OperationNode((FlexoProcess) null);
			operation.setNodeType(NodeType.NORMAL);
			operation.setName(operation.getDefaultName());
			returned = operation;
		} else if (this == BEGIN_OPERATION) {
			OperationNode operation = new OperationNode((FlexoProcess) null);
			operation.setNodeType(NodeType.BEGIN);
			operation.setName(operation.getDefaultName());
			returned = operation;
		} else if (this == END_OPERATION) {
			OperationNode operation = new OperationNode((FlexoProcess) null);
			operation.setNodeType(NodeType.END);
			operation.setName(operation.getDefaultName());
			returned = operation;
		} else if (this == SELF_EXECUTABLE_OPERATION) {
			SelfExecutableOperationNode operation = new SelfExecutableOperationNode((FlexoProcess) null);
			operation.setName(operation.getDefaultName());
			returned = operation;
		} else if (this == FLEXO_ACTION) {
			ActionNode action = new ActionNode((FlexoProcess) null);
			action.setNodeType(NodeType.NORMAL);
			action.setActionType(ActionType.FLEXO_ACTION);
			action.setName(action.getDefaultName());
			returned = action;
		} else if (this == DISPLAY_ACTION) {
			ActionNode action = new ActionNode((FlexoProcess) null);
			action.setNodeType(NodeType.NORMAL);
			action.setActionType(ActionType.DISPLAY_ACTION);
			action.setName(action.getDefaultName());
			returned = action;
		} else if (this == BEGIN_ACTION) {
			ActionNode action = new ActionNode((FlexoProcess) null);
			action.setNodeType(NodeType.BEGIN);
			action.setName(action.getDefaultName());
			returned = action;
		} else if (this == END_ACTION) {
			ActionNode action = new ActionNode((FlexoProcess) null);
			action.setNodeType(NodeType.END);
			action.setName(action.getDefaultName());
			returned = action;
		} else if (this == SELF_EXECUTABLE_ACTION) {
			SelfExecutableActionNode action = new SelfExecutableActionNode((FlexoProcess) null);
			action.setName(action.getDefaultName());
			returned = action;
		} else if (this == AND_OPERATOR) {
			ANDOperator operator = new ANDOperator((FlexoProcess) null);
			operator.setName(operator.getDefaultName());
			returned = operator;
		} else if (this == OR_OPERATOR) {
			OROperator operator = new OROperator((FlexoProcess) null);
			operator.setName(operator.getDefaultName());
			returned = operator;
		} else if (this == IF_OPERATOR) {
			IFOperator operator = new IFOperator((FlexoProcess) null);
			operator.setName(operator.getDefaultName());
			returned = operator;
		} else if (this == LOOP_OPERATOR) {
			LOOPOperator operator = new LOOPOperator((FlexoProcess) null);
			operator.setName(operator.getDefaultName());
			returned = operator;
		} else {
			logger.warning("Not implemented...");
		}
		return returned;
	}
}
