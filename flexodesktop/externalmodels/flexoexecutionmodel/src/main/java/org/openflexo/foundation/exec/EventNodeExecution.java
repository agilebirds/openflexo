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
package org.openflexo.foundation.exec;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.ProcedureCall;

import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.EventNode.TriggerType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public abstract class EventNodeExecution<E extends EventNode> extends ControlGraphBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(EventNodeExecution.class.getPackage().getName());

	private E eventNode;

	public static ControlGraphBuilder getExecutionNodeBuilder(EventNode eventNode) throws NotSupportedException, InvalidModelException {
		if (TriggerType.MESSAGE.equals(eventNode.getTrigger()) && !eventNode.getIsCatching()) {
			return new MailOutExecution(eventNode);
		}

		throw new NotSupportedException("Dont know what to do with a " + eventNode.getClass().getSimpleName());

	}

	public static ControlGraph executeNode(EventNode evenNode, boolean interprocedural) throws NotSupportedException, InvalidModelException {
		ControlGraphBuilder cgBuilder = getExecutionNodeBuilder(evenNode);

		if (cgBuilder != null) {

			if (interprocedural) {
				Procedure procedure = cgBuilder.makeProcedure();
				ProcedureCall returned = new ProcedureCall(procedure);
				returned.appendHeaderComment("Event " + evenNode.getName() + " is executing", true);
				return returned;
			} else {
				ControlGraph returned = cgBuilder.makeControlGraph(interprocedural);
				returned.appendHeaderComment("Event " + evenNode.getName() + " is executing", true);
				return returned;
			}
		} else {
			throw new NotSupportedException("Dont know what to do with a " + evenNode);
		}

	}

	protected EventNodeExecution(E eventNode) {
		super();
		this.eventNode = eventNode;
	}

	@Override
	protected ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		return makeSequentialControlGraph(makeSpecificControlGraph(interprocedural), makeCommonPostludeControlGraph(interprocedural));
	}

	protected abstract ControlGraph makeSpecificControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException;

	protected ControlGraph makeCommonPostludeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		Vector<ControlGraph> sendTokensStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getEventNode().getOutgoingPostConditions()) {
			sendTokensStatements.add(SendToken.sendToken(edge, interprocedural));
		}

		ControlGraph SEND_TOKENS = makeFlowControlGraph(sendTokensStatements);

		Vector<ControlGraph> sendMessagesStatements = new Vector<ControlGraph>();

		for (FlexoPostCondition edge : getEventNode().getOutgoingPostConditions()) {
			if (edge instanceof ExternalMessageInEdge) {
				sendMessagesStatements.add(SendMessage.sendMessage((ExternalMessageInEdge) edge, interprocedural));
			}
		}

		ControlGraph SEND_MESSAGES = makeFlowControlGraph(sendMessagesStatements);

		ControlGraph returned = makeSequentialControlGraph(SEND_MESSAGES, SEND_TOKENS);

		return returned;
	}

	@Override
	protected String getProcedureName() {
		return "executeEvent_" + ToolBox.capitalize(ToolBox.getJavaName(getEventNode().getName())) + "_" + getEventNode().getFlexoID();
	}

	public E getEventNode() {
		return eventNode;
	}

	@Override
	protected String getProcedureComment() {
		StringBuffer returned = new StringBuffer();
		returned.append(FlexoLocalization.localizedForKeyWithParams(
				"this_method_represents_code_to_be_executed_when_event_($0)_is_executed", getEventNode().getName()));
		return returned.toString();
	}

}
