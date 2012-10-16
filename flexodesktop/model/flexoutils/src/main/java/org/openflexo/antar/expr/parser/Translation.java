package org.openflexo.antar.expr.parser;

import java.util.Hashtable;

import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.analysis.DepthFirstAdapter;
import org.openflexo.antar.expr.parser.node.AAddExprExpr2;
import org.openflexo.antar.expr.parser.node.ABindingTerm;
import org.openflexo.antar.expr.parser.node.ACharsValueTerm;
import org.openflexo.antar.expr.parser.node.AExpr2Expr;
import org.openflexo.antar.expr.parser.node.AExpr3Expr2;
import org.openflexo.antar.expr.parser.node.AMultExprExpr3;
import org.openflexo.antar.expr.parser.node.AStringValueTerm;
import org.openflexo.antar.expr.parser.node.ATermExpr3;
import org.openflexo.antar.expr.parser.node.Node;
import org.openflexo.antar.expr.parser.node.PBinding;
import org.openflexo.antar.expr.parser.node.Start;

class Translation extends DepthFirstAdapter {

	private Hashtable<Node, Expression> expressionNodes;
	private Node topLevel = null;

	public Translation(Start start) {
		expressionNodes = new Hashtable<Node, Expression>();
	}

	public Expression getExpression() {
		if (topLevel != null)
			return expressionNodes.get(topLevel);
		return null;
	}

	private void registerExpressionNode(Node n, Expression e) {
		System.out.println("REGISTER " + e + " for node " + n + " as " + n.getClass());
		expressionNodes.put(n, e);
		topLevel = n;
	}

	private Expression getExpression(Node n) {
		if (n != null) {
			Expression returned = expressionNodes.get(n);
			if (returned == null) {
				System.out.println("No expression registered for " + n + " of  " + n.getClass());
			}
			return returned;
		}
		return null;
	}

	private BindingValueAsExpression makeBinding(PBinding node) {
		System.out.println("Make binding with " + node);
		BindingValueAsExpression returned = new BindingValueAsExpression(node.toString());
		registerExpressionNode(node, returned);
		return returned;
	}

	@Override
	public void outAExpr2Expr(AExpr2Expr node) {
		super.outAExpr2Expr(node);
		registerExpressionNode(node, getExpression(node.getExpr2()));
	}

	@Override
	public void outATermExpr3(ATermExpr3 node) {
		System.out.println("OUT Term-Expr3 with " + node + " term=" + node.getTerm() + " of  " + node.getTerm().getClass());
		super.outATermExpr3(node);
		registerExpressionNode(node, getExpression(node.getTerm()));
		System.out.println("***** ATermExpr3 " + node + "expression=" + getExpression(node.getTerm()));
	}

	@Override
	public void outAExpr3Expr2(AExpr3Expr2 node) {
		System.out.println("OUT Expr3-Expr2 with " + node);
		super.outAExpr3Expr2(node);
		registerExpressionNode(node, getExpression(node.getExpr3()));
		System.out.println("***** AExpr3Expr2 " + node + "expression=" + getExpression(node.getExpr3()));
	}

	@Override
	public void outAAddExprExpr2(AAddExprExpr2 node) {
		super.outAAddExprExpr2(node);
		System.out.println("OUT add with " + node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.ADDITION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAMultExprExpr3(AMultExprExpr3 node) {
		super.outAMultExprExpr3(node);
		System.out.println("OUT mult with " + node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.MULTIPLICATION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void caseABindingTerm(ABindingTerm node) {
		super.caseABindingTerm(node);
		System.out.println("CASE binding with " + node);
		BindingValueAsExpression b = makeBinding(node.getBinding());
		registerExpressionNode(node, b);
	}

	@Override
	public void inAStringValueTerm(AStringValueTerm node) {
		System.out.println("IN string " + node);
	}

	@Override
	public void inACharsValueTerm(ACharsValueTerm node) {
		System.out.println("IN chars " + node);
	}
}