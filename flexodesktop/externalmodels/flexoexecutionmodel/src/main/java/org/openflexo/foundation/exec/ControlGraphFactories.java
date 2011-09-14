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

import org.openflexo.antar.AlgorithmicUnit;
import org.openflexo.antar.Class;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Nop;
import org.openflexo.antar.Procedure;
import org.openflexo.antar.java.JavaFormattingException;
import org.openflexo.antar.java.JavaPrettyPrinter;

import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement.ControlGraphFactory;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement.WorkflowControlGraph;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ProgrammingLanguage;
import org.openflexo.toolbox.ToolBox;

public class ControlGraphFactories {

	static final Logger logger = Logger.getLogger(ControlGraphFactories.class.getPackage().getName());

	public static void init()
	{
		logger.info("Initializing control graph factories on ExecutableWorkflowElement");
		FlexoNode.setActivationComputingFactory(new NodeActivationControlGraphFactory());
		FlexoNode.setDesactivationComputingFactory(new NodeDesactivationControlGraphFactory());
		OperatorNode.setExecutionComputingFactory(new OperatorNodeExecutionControlGraphFactory());
		FlexoPostCondition.setExecutionComputingFactory(new SendTokenControlGraphFactory());
		FlexoPreCondition.setExecutionComputingFactory(new PreConditionExecutionControlGraphFactory());
		FlexoProcess.setExecutionComputingFactory(new ExecuteProcessControlGraphFactory());
	}
	
	protected abstract static class CommonControlGraphFactory<E extends ExecutableWorkflowElement> extends ControlGraphFactory<E>
	{
		private static final JavaPrettyPrinter prettyPrinter = new JavaPrettyPrinter();
		
		public CommonControlGraphFactory(String unlocalizedInfoLabel)
		{
			super(unlocalizedInfoLabel);
		}

		@Override
		public String prettyPrintedCode(AlgorithmicUnit algorithmicUnit,ProgrammingLanguage language) 
		{
			// Only java handled here for now !!!
			if (language == ProgrammingLanguage.JAVA) {
				return prettyPrintedJavaCode(algorithmicUnit);
			}
			else {
				return FlexoLocalization.localizedForKey("Programming language not supported yet: "+language);
			}
		}
		
		public String prettyPrintedJavaCode(AlgorithmicUnit algorithmicUnit) 
		{
			String javaCode = prettyPrinter.getStringRepresentation(algorithmicUnit);
			try {
				if (algorithmicUnit instanceof Class) {
					return JavaPrettyPrinter.formatJavaCodeAsClassCode(javaCode);
				}
				else if (algorithmicUnit instanceof Procedure) {
					return JavaPrettyPrinter.formatJavaCodeAsMethodCode(javaCode);
				}
				else if (algorithmicUnit instanceof ControlGraph) {
					return JavaPrettyPrinter.formatJavaCodeAsInlineCode(javaCode);
				}
				logger.warning("Unknown algorithmic unit type");
			} catch (JavaFormattingException e) {
				// Could not be formatted, warn it
				logger.warning("Could not format java code, escaping");
			}
			return javaCode;
		}
		
	}

	protected static class NodeActivationControlGraphFactory extends CommonControlGraphFactory<FlexoNode>
	{
		public NodeActivationControlGraphFactory()
		{
			super("control_flow_graph_executed_when_node_is_activated");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoNode node,boolean interprocedural) 
		{
			return computeAlgorithmicUnit(node,null,interprocedural);
		}
		
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoNode node, FlexoPreCondition pre,boolean interprocedural)
		{
			try {
				ControlGraphBuilder cgBuilder = NodeActivation.getActivationNodeBuilder(node, pre);
				if (interprocedural) return cgBuilder.makeProcedure();
				else return cgBuilder.makeControlGraph(interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		

		@Override
		public WorkflowControlGraph<FlexoNode> makeWorkflowControlGraph(FlexoNode object)
		{
			return new NodeActivationWorkflowControlGraph(object,this);
		}

		protected static class NodeActivationWorkflowControlGraph extends WorkflowControlGraph<FlexoNode>
		{
			private FlexoPreCondition precondition;
			
			public NodeActivationWorkflowControlGraph(FlexoNode node, NodeActivationControlGraphFactory factory) {
				super(node, factory);
				if (node.getPreConditions().size() > 0) 
				{
					precondition = node.getPreConditions().firstElement();
					refresh();
				}
			}

			public FlexoPreCondition getPrecondition() 
			{
				return precondition;
			}

			public void setPrecondition(FlexoPreCondition precondition) 
			{
				this.precondition = precondition;
				refresh();
			}
			
			public Vector<FlexoPreCondition> getAllPreconditions() 
			{
				return getObject().getPreConditions();
			}

			@Override
			protected void refreshAlgorithmicUnit()
			{
				logger.info("Recomputing control flow graph for "+getObject()+" and precondition "+getPrecondition()+" interprocedural="+isInterprocedural());
				algorithmicUnit = getFactory().computeAlgorithmicUnit(getObject(),getPrecondition(),isInterprocedural());
				refreshPrettyPrintedCode();
			}

			@Override
			public NodeActivationControlGraphFactory getFactory() 
			{
				return (NodeActivationControlGraphFactory)super.getFactory();
			}

			@Override
			public String getInfoLabel() 
			{
				if (getAllPreconditions().size() == 0
						|| (getAllPreconditions().size() == 1 && getAllPreconditions().firstElement().getAttachedBeginNode() == null)
						|| getPrecondition() == null) {
					return FlexoLocalization.localizedForKey("control_flow_graph_executed_when_node_is_activated");
				}
				else {
					return FlexoLocalization.localizedForKeyWithParams("control_flow_graph_executed_when_node_is_activated_by_precondition_($0)",getPrecondition().getName());
				}
			}

		}

	}
	
	protected static class NodeDesactivationControlGraphFactory extends CommonControlGraphFactory<FlexoNode>
	{
		public NodeDesactivationControlGraphFactory()
		{
			super("control_flow_graph_executed_when_node_is_desactivated");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoNode node,boolean interprocedural)
		{
			try {
				ControlGraphBuilder cgBuilder = NodeDesactivation.getDesactivationNodeBuilder(node);
				if (interprocedural) return cgBuilder.makeProcedure();
				else return cgBuilder.makeControlGraph(interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
	}
	
	protected static class OperatorNodeExecutionControlGraphFactory extends CommonControlGraphFactory<OperatorNode>
	{
		public OperatorNodeExecutionControlGraphFactory()
		{
			super("control_flow_graph_executed_when_operator_node_is_reached");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(OperatorNode operatorNode,boolean interprocedural)
		{
			return computeAlgorithmicUnit(operatorNode,null,interprocedural);
		}
		
		public AlgorithmicUnit computeAlgorithmicUnit(OperatorNode operatorNode, FlexoPostCondition<?, ?> edge, boolean interprocedural)
		{
			try {
				ControlGraphBuilder cgBuilder = OperatorNodeExecution.getExecutionNodeBuilder(operatorNode,edge);
				if (interprocedural) return cgBuilder.makeProcedure();
				else return cgBuilder.makeControlGraph(interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
		
		@Override
		public WorkflowControlGraph<OperatorNode> makeWorkflowControlGraph(OperatorNode operatorNode)
		{
			return new OperatorNodeExecutionWorkflowControlGraph(operatorNode,this);
		}

		protected static class OperatorNodeExecutionWorkflowControlGraph extends WorkflowControlGraph<OperatorNode>
		{
			private FlexoPostCondition<?, ?> edge;
			
			public OperatorNodeExecutionWorkflowControlGraph(OperatorNode operatorNode, OperatorNodeExecutionControlGraphFactory factory) {
				super(operatorNode, factory);
				if (operatorNode.getIncomingPostConditions().size() > 0) 
				{
					edge = operatorNode.getIncomingPostConditions().firstElement();
					refresh();
				}
			}

			public FlexoPostCondition<?, ?> getEdge() 
			{
				return edge;
			}

			public void setEdge(FlexoPostCondition<?, ?> edge) 
			{
				this.edge = edge;
				refresh();
			}
			
			public Vector getAllEdges() 
			{
				return getObject().getIncomingPostConditions();
			}

			@Override
			protected void refreshAlgorithmicUnit()
			{
				logger.info("Recomputing control flow graph for "+getObject()+" and edge "+getEdge()+" interprocedural="+interprocedural);
				algorithmicUnit = getFactory().computeAlgorithmicUnit(getObject(),getEdge(),isInterprocedural());
				refreshPrettyPrintedCode();
			}

			@Override
			public OperatorNodeExecutionControlGraphFactory getFactory() 
			{
				return (OperatorNodeExecutionControlGraphFactory)super.getFactory();
			}

			@Override
			public String getInfoLabel() 
			{
				if (getAllEdges().size() <=1 || getEdge() == null) {
					return FlexoLocalization.localizedForKey("control_flow_graph_executed_when_operator_node_is_reached");
				}
				else {
					return FlexoLocalization.localizedForKeyWithParams("control_flow_graph_executed_when_operator_node_is_reached_from_edge_($0)",((FlexoPostCondition)edge).getDerivedNameFromStartingObject());
				}
			}

		}


	}
	
	protected static class EventNodeExecutionControlGraphFactory extends CommonControlGraphFactory<EventNode>
	{
		public EventNodeExecutionControlGraphFactory()
		{
			super("control_flow_graph_executed_when_event_node_is_reached");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(EventNode operatorNode, boolean interprocedural)
		{
			try {
				ControlGraphBuilder cgBuilder = EventNodeExecution.getExecutionNodeBuilder(operatorNode);
				if (interprocedural) 
					return cgBuilder.makeProcedure();
				else
					return cgBuilder.makeControlGraph(interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
		
		@Override
		public WorkflowControlGraph<EventNode> makeWorkflowControlGraph(EventNode operatorNode)
		{
			return new EventNodeExecutionWorkflowControlGraph(operatorNode,this);
		}

		protected static class EventNodeExecutionWorkflowControlGraph extends WorkflowControlGraph<EventNode>
		{
			public EventNodeExecutionWorkflowControlGraph(EventNode operatorNode, EventNodeExecutionControlGraphFactory factory) {
				super(operatorNode, factory);
			}

			public Vector getAllEdges() 
			{
				return getObject().getIncomingPostConditions();
			}

			@Override
			protected void refreshAlgorithmicUnit()
			{
				logger.info("Recomputing control flow graph for "+getObject()+" interprocedural="+interprocedural);
				algorithmicUnit = getFactory().computeAlgorithmicUnit(getObject(),isInterprocedural());
				refreshPrettyPrintedCode();
			}

			@Override
			public EventNodeExecutionControlGraphFactory getFactory() 
			{
				return (EventNodeExecutionControlGraphFactory)super.getFactory();
			}

			@Override
			public String getInfoLabel() 
			{
				return FlexoLocalization.localizedForKey("control_flow_graph_executed_when_event_node_is_reached");
			}

		}


	}
	
	protected static class SendTokenControlGraphFactory extends CommonControlGraphFactory<FlexoPostCondition<?, ?>>
	{
		public SendTokenControlGraphFactory()
		{
			super("control_flow_graph_executed_when_a_token_is_sent_through_edge");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoPostCondition<?, ?> edge,boolean interprocedural)
		{
			try {
				return SendToken.sendToken(edge, interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
	}
	
	protected static class PreConditionExecutionControlGraphFactory extends CommonControlGraphFactory<FlexoPreCondition>
	{
		public PreConditionExecutionControlGraphFactory()
		{
			super("control_flow_graph_executed_when_precondition_receives_a_new_token");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoPreCondition pre,boolean interprocedural)
		{
			return computeAlgorithmicUnit(pre, null, interprocedural);
		}		

		public AlgorithmicUnit computeAlgorithmicUnit(FlexoPreCondition pre, FlexoPostCondition<?, ?> edge, boolean interprocedural)
		{
			try {
				ControlGraphBuilder cgBuilder = SendTokenToPrecondition.getSendTokenToPreconditionBuilder(pre, edge);
				if (interprocedural) return cgBuilder.makeProcedure();
				else return cgBuilder.makeControlGraph(interprocedural);
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
		
		@Override
		public WorkflowControlGraph<FlexoPreCondition> makeWorkflowControlGraph(FlexoPreCondition pre)
		{
			return new PreConditionExecutionWorkflowControlGraph(pre,this);
		}

		protected static class PreConditionExecutionWorkflowControlGraph extends WorkflowControlGraph<FlexoPreCondition>
		{
			private FlexoPostCondition<?, ?> edge;
			
			public PreConditionExecutionWorkflowControlGraph(FlexoPreCondition pre, PreConditionExecutionControlGraphFactory factory) {
				super(pre, factory);
				if (pre.getIncomingPostConditions().size() > 0) 
				{
					edge = pre.getIncomingPostConditions().firstElement();
					refresh();
				}
			}

			public FlexoPostCondition<?, ?> getEdge() 
			{
				return edge;
			}

			public void setEdge(FlexoPostCondition<?, ?> edge) 
			{
				this.edge = edge;
				refresh();
			}
			
			public Vector<FlexoPostCondition<?, ?>> getAllEdges() 
			{
				return new Vector<FlexoPostCondition<?,?>>(getObject().getIncomingPostConditions());
			}

			@Override
			protected void refreshAlgorithmicUnit()
			{
				logger.info("Recomputing control flow graph for "+getObject()+" and edge "+getEdge()+" interprocedural="+interprocedural);
				algorithmicUnit = getFactory().computeAlgorithmicUnit(getObject(),getEdge(),isInterprocedural());
				refreshPrettyPrintedCode();
			}

			@Override
			public PreConditionExecutionControlGraphFactory getFactory() 
			{
				return (PreConditionExecutionControlGraphFactory)super.getFactory();
			}

			@Override
			public String getInfoLabel() 
			{
				if (getAllEdges().size() <=1 || getEdge() == null) {
					return FlexoLocalization.localizedForKey("control_flow_graph_executed_when_precondition_receives_a_new_token");
				}
				else {
					return FlexoLocalization.localizedForKeyWithParams("control_flow_graph_executed_when_precondition_receives_a_new_token_from_edge_($0)",((FlexoPostCondition)edge).getDerivedNameFromStartingObject());
				}
			}

		}


	}
	
	protected static class ExecuteProcessControlGraphFactory extends CommonControlGraphFactory<FlexoProcess>
	{
		public ExecuteProcessControlGraphFactory()
		{
			super("control_flow_graph_executed_when_a_token_is_sent_through_edge");
		}

		@Override
		public AlgorithmicUnit computeAlgorithmicUnit(FlexoProcess process,boolean interprocedural)
		{
			try {
				Vector<Procedure> allProcedures = new Vector<Procedure>();
				for (AbstractActivityNode n : process.getAllAbstractActivityNodes()) {
					appendProcedureForActivity(n,allProcedures);
				}
				for (OperatorNode op : process.getAllOperatorNodes()) {
					appendProcedureForOperator(op,allProcedures);
				}
				for (EventNode op : process.getAllEventNodes()) {
					appendProcedureForEvent(op,allProcedures);
				}
				for (FlexoPort port : process.getPortRegistery().getAllPorts()) {
					appendProcedureForPort(port,allProcedures);
				}
				Class returned = new Class(ToolBox.capitalize(process.getExecutionClassName()),process.getExecutionGroupName(),allProcedures);
				returned.setComment(FlexoLocalization.localizedForKeyWithParams("this_method_represents_all_code_to_be_executed_in_the_context_of_process_($0)_execution",process.getName()));
				return returned;
			} catch (NotSupportedException e) {
				return new Nop("NotSupportedException: "+e.getMessage());
			} catch (InvalidModelException e) {
				return new Nop("InvalidModelException: "+e.getMessage());
			}
		}		
		
		private void appendProcedureForActivity(AbstractActivityNode node, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(NodeActivation.getActivationNodeBuilder(node,null).makeProcedure());
			allProcedures.add(NodeDesactivation.getDesactivationNodeBuilder(node).makeProcedure());
			for (FlexoPreCondition pre : node.getPreConditions()) {
				allProcedures.add(SendTokenToPrecondition.getSendTokenToPreconditionBuilder(pre, null).makeProcedure());
			}
			for (OperationNode operation : node.getAllOperationNodes()) {
				appendProcedureForOperation(operation,allProcedures);
			}
			for (OperatorNode op : node.getAllOperatorNodes()) {
				appendProcedureForOperator(op,allProcedures);
			}
			for (EventNode op : node.getAllEventNodes()) {
				appendProcedureForEvent(op,allProcedures);
			}
		}
		
		private void appendProcedureForOperation(OperationNode node, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(NodeActivation.getActivationNodeBuilder(node,null).makeProcedure());
			allProcedures.add(NodeDesactivation.getDesactivationNodeBuilder(node).makeProcedure());
			for (FlexoPreCondition pre : node.getPreConditions()) {
				allProcedures.add(SendTokenToPrecondition.getSendTokenToPreconditionBuilder(pre, null).makeProcedure());
			}
			for (ActionNode action : node.getAllActionNodes()) {
				appendProcedureForAction(action,allProcedures);
			}
			for (OperatorNode op : node.getAllOperatorNodes()) {
				appendProcedureForOperator(op,allProcedures);
			}
			for (EventNode op : node.getAllEventNodes()) {
				appendProcedureForEvent(op,allProcedures);
			}
		}
		
		private void appendProcedureForAction(ActionNode node, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(NodeActivation.getActivationNodeBuilder(node,null).makeProcedure());
			allProcedures.add(NodeDesactivation.getDesactivationNodeBuilder(node).makeProcedure());
			for (FlexoPreCondition pre : node.getPreConditions()) {
				allProcedures.add(SendTokenToPrecondition.getSendTokenToPreconditionBuilder(pre, null).makeProcedure());
			}
		}
		
		private void appendProcedureForOperator(OperatorNode operatorNode, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(OperatorNodeExecution.getExecutionNodeBuilder(operatorNode,null).makeProcedure());
		}

		private void appendProcedureForEvent(EventNode eventNode, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(EventNodeExecution.getExecutionNodeBuilder(eventNode).makeProcedure());
		}
		
		private void appendProcedureForPort(FlexoPort port, Vector<Procedure> allProcedures) throws InvalidModelException, NotSupportedException
		{
			allProcedures.add(PortActivation.getActivationPortBuilder(port).makeProcedure());
			allProcedures.add(PortDesactivation.getDesactivationPortBuilder(port).makeProcedure());
		}
}


	

}
