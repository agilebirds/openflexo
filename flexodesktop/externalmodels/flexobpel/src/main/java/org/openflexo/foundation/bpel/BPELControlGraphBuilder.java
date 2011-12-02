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
package org.openflexo.foundation.bpel;

import java.util.Vector;

import org.openflexo.antar.Assignment;
import org.openflexo.antar.Conditional;
import org.openflexo.antar.ControlGraph;
import org.openflexo.antar.Flow;
import org.openflexo.antar.Nop;
import org.openflexo.antar.Sequence;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingAssignment;
import org.openflexo.foundation.bindings.BindingExpression;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.StaticBinding;
import org.openflexo.foundation.exec.ControlGraphBuilder;
import org.openflexo.foundation.exec.InvalidModelException;
import org.openflexo.foundation.exec.NotSupportedException;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.ANDOperator;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.OutputPort;

public class BPELControlGraphBuilder extends ControlGraphBuilder {

	AbstractInPort operationPortIN;
	OutputPort operationPortOUT;

	boolean portReached = false;
	boolean debug = true;

	public BPELControlGraphBuilder(AbstractInPort pIN, OutputPort pOUT) {
		operationPortIN = pIN;
		operationPortOUT = pOUT;
	}

	@Override
	protected String getProcedureName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ControlGraph makeControlGraph(boolean interprocedural) throws InvalidModelException, NotSupportedException {
		// start with the Port
		if (operationPortIN == null) {
			throw new BPELInvalidModelException("No port defined for Flexo Process");
		}

		BPELNodeAccu acc = new BPELNodeAccu();
		ControlGraph toReturn = handleNode(operationPortIN, acc);
		return toReturn;
	}

	protected ControlGraph handleSequence(AbstractNode n, BPELNodeAccu acc) throws NotSupportedException, BPELInvalidModelException {
		Sequence toReturn = new Sequence();
		AbstractNode currentNode = n;

		System.out.println("Handling sequence starting at : " + currentNode.getName());

		/* a sequence stops when 
		 *  - The outport is reached
		 *  - A AND node is reached
		 *  - several edges enter the same node (case of a if)
		 */
		while (!(currentNode instanceof FlexoPort) && !(acc.lookingForAnd() && currentNode instanceof ANDOperator)
				&& (!(acc.lookingForIf() && getIncomingEdges(currentNode) != 1))) {
			ControlGraph toAdd = handleNode(currentNode, acc);
			if (toAdd != null) {
				toReturn.addToStatements(toAdd);
			}
			if (acc.getNodes().size() > 1) {
				toReturn.addToStatements(handleFlow(acc));
			}
			currentNode = acc.getNodes().get(0);
			if (acc.hasBeenHandled(currentNode)) {
				throw new BPELInvalidModelException("A cycle has been detected in your Flexo Model");
			}
		}
		if (debug) {
			System.out.println("SEQUENCE RETURNING");
		}
		return toReturn;
	}

	protected ControlGraph handleFlow(BPELNodeAccu acc) throws BPELInvalidModelException, NotSupportedException {
		Flow toReturn = new Flow();
		acc.setLookingForAnd(true);
		for (AbstractNode n : acc.getNodes()) {
			toReturn.addToStatements(handleSequence(n, acc));
		}
		acc.setLookingForAnd(false);
		if (debug) {
			System.out.println("FLOW RETURNING ON NODE");
		}
		return toReturn;
	}

	protected Vector<AbstractNode> getNextNodes(AbstractNode n) throws BPELInvalidModelException {
		Vector<AbstractNode> toReturn = new Vector<AbstractNode>();
		for (FlexoPostCondition p : n.getOutgoingPostConditions()) {
			toReturn.add(p.getNextNode());
		}
		if (toReturn.size() == 0) {
			throw new BPELInvalidModelException("Every possible path in the Workflow must lean to the IN/OUT port; Node " + n.getName()
					+ " has no outgoing edge");
		}
		return toReturn;
	}

	protected int getIncomingEdges(AbstractNode n) throws BPELInvalidModelException {
		if (n instanceof ActivityNode) {
			if (((ActivityNode) n).getPreConditions().size() != 1) {
				throw new BPELInvalidModelException("Every ActivityNode must have only one precondition");
			}
			return ((ActivityNode) n).getPreConditions().get(0).getIncomingPostConditions().size();
		} else {
			System.out.println("incoming edges of " + n.getName() + " : " + n.getIncomingPostConditions().size());
			return n.getIncomingPostConditions().size();
		}
	}

	protected ControlGraph handleNode(FlexoPort port, BPELNodeAccu acc) throws BPELInvalidModelException, NotSupportedException {
		if (portReached) {
			return null;
		}
		portReached = true;
		// A port can be seen as a single instruction that does stuff
		// like an API...
		BPELWSAPI toReturn = new BPELWSAPI(port);
		Vector<FlexoPostCondition<AbstractNode, AbstractNode>> postConditions = port.getOutgoingPostConditions();

		if (postConditions.size() != 1) {
			throw new BPELInvalidModelException("There must be one and only one edge coming out of a port.");
		}
		if (postConditions.get(0).getEndNode() == null || postConditions.get(0).getNextNode() == null) {
			throw new BPELInvalidModelException("Every edge leaving a port must be connected");
		}

		AbstractNode nodeInSequence = postConditions.get(0).getNextNode();
		toReturn.setControlGraph(handleSequence(nodeInSequence, new BPELNodeAccu()));
		return toReturn;
	}

	protected ControlGraph handleNode(SelfExecutableActivityNode n, BPELNodeAccu acc) throws BPELInvalidModelException {

		acc.setNodes(getNextNodes(n));
		Vector<BindingAssignment> assignments = n.getAssignments();
		if (assignments == null || assignments.size() == 0) {
			return new Nop();
		}
		if (assignments.size() == 1) {
			Assignment toReturn = getAssignment(assignments.get(0));

			return toReturn;
		} else {
			Sequence toReturn = new Sequence();
			for (int i = 0; i < assignments.size(); i++) {
				Assignment currentAssignment = getAssignment(assignments.get(i));
				toReturn.addToStatements(currentAssignment);
			}
			return toReturn;
		}
	}

	private Assignment getAssignment(BindingAssignment a) {

		BindingValue target = a.getReceiver();

		Variable receiver = new Variable((BPELPrettyPrinter.getInstance()).makeStringRepresentation(target));

		AbstractBinding value = a.getValue();
		Expression expr = null;
		if (value instanceof BindingExpression) {
			expr = ((BindingExpression) value).getExpression();
		}
		if (value instanceof BindingValue) {
			Variable var = new Variable(BPELPrettyPrinter.getInstance().makeStringRepresentation((BindingValue) value));
			expr = var;
		} else if (value instanceof StaticBinding) {
			expr = new Variable((String) ((StaticBinding) value).getValue());
		} else {
			try {
				expr = (new DefaultExpressionParser()).parse(value.toString());
			} catch (Exception e) {
				System.out.println("Could not parse expression... " + value.toString());
				e.printStackTrace();
			}

		}
		return new Assignment(receiver, expr);
		// a.getValue().g

		/*
			System.out.println("String rep of target : "+target.getStringRepresentation());
			
			String sTarget="processInstance";
			for (BindingPathElement el: target.getBindingPath()) {
				sTarget+=".{"+el.getEntity().getFullQualifiedName()+"}"+el.getSerializationRepresentation();
			}
			
			System.out.println("sTarget : "+sTarget);
		*/

		/*
		System.out.println("Entity : "+ ent.getFullQualifiedName()+ " "+ent.getFullyQualifiedName());
		System.out.println("parent entity :"+ent.getParentBaseEntity().getFullQualifiedName());
		*/

	}

	protected ControlGraph handleNode(OperatorNode n, BPELNodeAccu acc) throws NotSupportedException, BPELInvalidModelException {
		if (n instanceof IFOperator) {
			IFOperator ifOp = (IFOperator) n;
			// need a condition
			// a then statement
			// and a else statement
			Expression condition = null;
			ControlGraph thenClause = null;
			ControlGraph elseClause = null;

			if (ifOp.getPositiveEvaluationPostConditions().size() != 1 || ifOp.getNegativeEvaluationPostConditions().size() != 1) {
				throw new BPELInvalidModelException("If operator should have 2 outgoing edges: 1 true and 1 false");
			}

			FlexoPostCondition trueCond = ifOp.getPositiveEvaluationPostConditions().firstElement();
			FlexoPostCondition falseCond = ifOp.getNegativeEvaluationPostConditions().firstElement();

			acc.setLookingForIf(true);
			thenClause = handleSequence(trueCond.getNextNode(), acc);
			elseClause = handleSequence(falseCond.getNextNode(), acc);
			acc.setLookingForIf(false);

			if (ifOp.getConditionPrimitive() instanceof BindingExpression) {
				BindingExpression expression = (BindingExpression) ifOp.getConditionPrimitive();
				condition = expression.getExpression();

			}

			Conditional toReturn = new Conditional(condition, thenClause, elseClause);
			return toReturn;
		} else if (n instanceof ANDOperator) {
			acc.setNodes(getNextNodes(n));
			return null;
		} else {
			throw new NotSupportedException("Operator " + n.getClass().getName() + " is not supported...");
		}
	}

	protected ControlGraph handleNode(FlexoPortMap n, BPELNodeAccu acc) throws BPELInvalidModelException {
		acc.setNodes(getNextNodes(n));
		BPELWSInvocation invoc = new BPELWSInvocation(n.getSubProcessNode());
		System.out.println("added service invocation");

		if ((n.getSubProcessNode().getActivationAssignments() != null && n.getSubProcessNode().getActivationAssignments().size() > 0)
				|| (n.getSubProcessNode().getDesactivationAssignments() != null && n.getSubProcessNode().getDesactivationAssignments()
						.size() > 0)) {

			Sequence seq = new Sequence();
			if (n.getSubProcessNode().getActivationAssignments() != null) {
				for (BindingAssignment ba : n.getSubProcessNode().getActivationAssignments()) {
					Assignment a = getAssignment(ba);
					seq.addToStatements(a);
				}
			}

			seq.addToStatements(invoc);

			if (n.getSubProcessNode().getActivationAssignments() != null) {
				for (BindingAssignment ba : n.getSubProcessNode().getDesactivationAssignments()) {
					Assignment a = getAssignment(ba);
					seq.addToStatements(a);
				}
			}
			return seq;
		} else {
			return invoc;
		}
	}

	protected ControlGraph handleNode(ActivityNode n, BPELNodeAccu acc) throws BPELInvalidModelException, NotSupportedException {
		// we should create here an invocation object... which we will leave empty, but to show that the user has to do something here.
		System.out.println("Handling activity : " + n.getName());
		Vector<FlexoNode> endNodes = n.getOperationPetriGraph().getAllEndNodes();
		if (endNodes.size() == 1) {
			acc.setNodes(getNextNodes(endNodes.get(0)));
			return new BPELWSInvocation();
		}
		if (endNodes.size() == 2) {
			Sequence toReturn = new Sequence();
			toReturn.addToStatements(new BPELWSInvocation());
			// in this case, we will create a if with no condition for now.
			acc.setLookingForIf(true);
			System.out.println("2 edges at Activity :" + endNodes.get(0).getName() + " " + endNodes.get(1).getName());

			System.out.println(" End Nodes :" + getNextNodes(endNodes.get(0)).get(0).getClass().getName() + " "
					+ getNextNodes(endNodes.get(1)).get(0).getClass().getName());

			ControlGraph firstCase = handleSequence(getNextNodes(endNodes.get(0)).get(0), acc);
			ControlGraph secondCase = handleSequence(getNextNodes(endNodes.get(1)).get(0), acc);
			acc.setLookingForIf(false);
			Conditional cond = new Conditional(null, firstCase, secondCase);
			toReturn.addToStatements(cond);
			return toReturn;
		} else {
			throw new BPELInvalidModelException("When generating BPEL, there can be only one or two endNodes inside an Activity");
		}
	}

	protected ControlGraph handleNode(AbstractNode n, BPELNodeAccu acc) throws BPELInvalidModelException, NotSupportedException {
		if (n instanceof FlexoPort) {
			return handleNode((FlexoPort) n, acc);
		}
		acc.nodeIsHandled(n);
		if (n instanceof SelfExecutableActivityNode) {
			return handleNode((SelfExecutableActivityNode) n, acc);
		}
		if (n instanceof OperatorNode) {
			return handleNode((OperatorNode) n, acc);
		}
		if (n instanceof FlexoPortMap) {
			return handleNode((FlexoPortMap) n, acc);
		}
		if (n instanceof ActivityNode) {
			return handleNode((ActivityNode) n, acc);
		}

		throw new NotSupportedException("The FlexoNode " + n.getClass().getName() + " is not supported for BPEL generation");
	}
}
