package org.openflexo.antar.expr.parser;

import java.util.Hashtable;

import org.openflexo.antar.expr.ArithmeticBinaryOperator;
import org.openflexo.antar.expr.ArithmeticUnaryOperator;
import org.openflexo.antar.expr.BinaryOperatorExpression;
import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.BooleanBinaryOperator;
import org.openflexo.antar.expr.BooleanUnaryOperator;
import org.openflexo.antar.expr.ConditionalExpression;
import org.openflexo.antar.expr.Constant.BooleanConstant;
import org.openflexo.antar.expr.Constant.FloatConstant;
import org.openflexo.antar.expr.Constant.FloatSymbolicConstant;
import org.openflexo.antar.expr.Constant.IntegerConstant;
import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.UnaryOperatorExpression;
import org.openflexo.antar.expr.parser.analysis.DepthFirstAdapter;
import org.openflexo.antar.expr.parser.node.AAcosFuncFunction;
import org.openflexo.antar.expr.parser.node.AAddExprExpr2;
import org.openflexo.antar.expr.parser.node.AAnd2ExprExpr3;
import org.openflexo.antar.expr.parser.node.AAndExprExpr3;
import org.openflexo.antar.expr.parser.node.AAsinFuncFunction;
import org.openflexo.antar.expr.parser.node.AAtanFuncFunction;
import org.openflexo.antar.expr.parser.node.ABindingTerm;
import org.openflexo.antar.expr.parser.node.ACharsValueTerm;
import org.openflexo.antar.expr.parser.node.ACondExprExpr;
import org.openflexo.antar.expr.parser.node.AConstantNumber;
import org.openflexo.antar.expr.parser.node.ACosFuncFunction;
import org.openflexo.antar.expr.parser.node.ADecimalNumberNumber;
import org.openflexo.antar.expr.parser.node.ADivExprExpr3;
import org.openflexo.antar.expr.parser.node.AEq2ExprExpr;
import org.openflexo.antar.expr.parser.node.AEqExprExpr;
import org.openflexo.antar.expr.parser.node.AExpFuncFunction;
import org.openflexo.antar.expr.parser.node.AExpr2Expr;
import org.openflexo.antar.expr.parser.node.AExpr3Expr2;
import org.openflexo.antar.expr.parser.node.AExprTerm;
import org.openflexo.antar.expr.parser.node.AFalseConstant;
import org.openflexo.antar.expr.parser.node.AFunctionTerm;
import org.openflexo.antar.expr.parser.node.AGtExprExpr;
import org.openflexo.antar.expr.parser.node.AGteExprExpr;
import org.openflexo.antar.expr.parser.node.ALogFuncFunction;
import org.openflexo.antar.expr.parser.node.ALtExprExpr;
import org.openflexo.antar.expr.parser.node.ALteExprExpr;
import org.openflexo.antar.expr.parser.node.AModExprExpr3;
import org.openflexo.antar.expr.parser.node.AMultExprExpr3;
import org.openflexo.antar.expr.parser.node.ANegativeTerm;
import org.openflexo.antar.expr.parser.node.ANeqExprExpr;
import org.openflexo.antar.expr.parser.node.ANotExprExpr3;
import org.openflexo.antar.expr.parser.node.ANullConstant;
import org.openflexo.antar.expr.parser.node.ANumberTerm;
import org.openflexo.antar.expr.parser.node.AOr2ExprExpr2;
import org.openflexo.antar.expr.parser.node.AOrExprExpr2;
import org.openflexo.antar.expr.parser.node.APiConstant;
import org.openflexo.antar.expr.parser.node.APowerExprExpr3;
import org.openflexo.antar.expr.parser.node.APreciseNumberNumber;
import org.openflexo.antar.expr.parser.node.AScientificNotationNumberNumber;
import org.openflexo.antar.expr.parser.node.ASinFuncFunction;
import org.openflexo.antar.expr.parser.node.ASqrtFuncFunction;
import org.openflexo.antar.expr.parser.node.AStringValueTerm;
import org.openflexo.antar.expr.parser.node.ASubExprExpr2;
import org.openflexo.antar.expr.parser.node.ATanFuncFunction;
import org.openflexo.antar.expr.parser.node.ATermExpr3;
import org.openflexo.antar.expr.parser.node.ATrueConstant;
import org.openflexo.antar.expr.parser.node.Node;
import org.openflexo.antar.expr.parser.node.PBinding;
import org.openflexo.antar.expr.parser.node.TCharsValue;
import org.openflexo.antar.expr.parser.node.TDecimalNumber;
import org.openflexo.antar.expr.parser.node.TPreciseNumber;
import org.openflexo.antar.expr.parser.node.TScientificNotationNumber;
import org.openflexo.antar.expr.parser.node.TStringValue;

/**
 * This class implements the semantics analyzer for a parsed AnTAR expression.<br>
 * Its main purpose is to build a syntax tree with AnTAR expression model from a parsed AST.
 * 
 * @author sylvain
 * 
 */
class ExpressionSemanticsAnalyzer extends DepthFirstAdapter {

	private Hashtable<Node, Expression> expressionNodes;
	private Node topLevel = null;

	public ExpressionSemanticsAnalyzer() {
		expressionNodes = new Hashtable<Node, Expression>();
	}

	public Expression getExpression() {
		if (topLevel != null) {
			return expressionNodes.get(topLevel);
		}
		return null;
	}

	private void registerExpressionNode(Node n, Expression e) {
		// System.out.println("REGISTER " + e + " for node " + n + " as " + n.getClass());
		expressionNodes.put(n, e);
		topLevel = n;
	}

	protected Expression getExpression(Node n) {
		if (n != null) {
			Expression returned = expressionNodes.get(n);
			if (returned == null) {
				System.out.println("No expression registered for " + n + " of  " + n.getClass());
			}
			return returned;
		}
		return null;
	}

	private BindingValue makeBinding(PBinding node) {
		// System.out.println("Make binding with " + node);

		// Apply the translation.
		BindingSemanticsAnalyzer bsa = new BindingSemanticsAnalyzer();
		node.apply(bsa);
		BindingValue returned = new BindingValue(bsa.getPath());
		// System.out.println("Made binding as " + bsa.getPath());

		registerExpressionNode(node, returned);
		return returned;
	}

	private IntegerConstant makeDecimalNumber(TDecimalNumber node) {
		// System.out.println("Make decimal number with " + node + " as " + Long.parseLong(node.getText()));
		IntegerConstant returned = new IntegerConstant(Long.parseLong(node.getText()));
		registerExpressionNode(node, returned);
		return returned;
	}

	private FloatConstant makePreciseNumber(TPreciseNumber node) {
		// System.out.println("Make precise number with " + node + " as " + Double.parseDouble(node.getText()));
		FloatConstant returned = new FloatConstant(Double.parseDouble(node.getText()));
		registerExpressionNode(node, returned);
		return returned;
	}

	private FloatConstant makeScientificNotationNumber(TScientificNotationNumber node) {
		// System.out.println("Make scientific notation number with " + node + " as " + Double.parseDouble(node.getText()));
		FloatConstant returned = new FloatConstant(Double.parseDouble(node.getText()));
		registerExpressionNode(node, returned);
		return returned;
	}

	private StringConstant makeStringValue(TStringValue node) {
		// System.out.println("Make string value with " + node);
		StringConstant returned = new StringConstant(node.getText().substring(1, node.getText().length() - 1));
		registerExpressionNode(node, returned);
		return returned;
	}

	private StringConstant makeCharsValue(TCharsValue node) {
		// System.out.println("Make chars value with " + node);
		StringConstant returned = new StringConstant(node.getText().substring(1, node.getText().length() - 1));
		registerExpressionNode(node, returned);
		return returned;
	}

	// Following methods manage following grammar fragment
	/*expr =
	  {expr2} expr2 |
	  {cond_expr} [condition]:expr if_token [then]:expr2 else_token [else]:expr2 |
	  {eq_expr} [left]:expr eq [right]:expr2 |
	  {eq2_expr} [left]:expr eq2 [right]:expr2 |
	  {neq_expr} [left]:expr neq [right]:expr2 |
	  {lt_expr} [left]:expr lt [right]:expr2 |
	  {gt_expr} [left]:expr gt [right]:expr2 |
	  {lte_expr} [left]:expr lte [right]:expr2 |
	  {gte_expr} [left]:expr gte [right]:expr2 ;*/

	@Override
	public void outAExpr2Expr(AExpr2Expr node) {
		super.outAExpr2Expr(node);
		registerExpressionNode(node, getExpression(node.getExpr2()));
	}

	@Override
	public void outACondExprExpr(ACondExprExpr node) {
		super.outACondExprExpr(node);
		// System.out.println("On chope une conditionnelle avec cond:" + node.getCondition() + " then:" + node.getThen() + " else:"+
		// node.getElse());
		registerExpressionNode(node, new ConditionalExpression(getExpression(node.getCondition()), getExpression(node.getThen()),
				getExpression(node.getElse())));
	}

	@Override
	public void outAEqExprExpr(AEqExprExpr node) {
		super.outAEqExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAEq2ExprExpr(AEq2ExprExpr node) {
		super.outAEq2ExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outANeqExprExpr(ANeqExprExpr node) {
		super.outANeqExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.NOT_EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outALtExprExpr(ALtExprExpr node) {
		super.outALtExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.LESS_THAN, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outALteExprExpr(ALteExprExpr node) {
		super.outALteExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.LESS_THAN_OR_EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAGtExprExpr(AGtExprExpr node) {
		super.outAGtExprExpr(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.GREATER_THAN, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAGteExprExpr(AGteExprExpr node) {
		super.outAGteExprExpr(node);
		registerExpressionNode(node,
				new BinaryOperatorExpression(BooleanBinaryOperator.GREATER_THAN_OR_EQUALS, getExpression(node.getLeft()),
						getExpression(node.getRight())));
	}

	// Following methods manage following grammar fragment
	/* expr2 =
	  {expr3} expr3 |
	  {or_expr} [left]:expr2 or [right]:expr3 |
	  {or2_expr} [left]:expr2 or2 [right]:expr3 |
	  {add_expr} [left]:expr2 plus [right]:expr3 |
	  {sub_expr} [left]:expr2 minus [right]:expr3; */

	@Override
	public void outAExpr3Expr2(AExpr3Expr2 node) {
		// System.out.println("OUT Expr3-Expr2 with " + node);
		super.outAExpr3Expr2(node);
		registerExpressionNode(node, getExpression(node.getExpr3()));
		// System.out.println("***** AExpr3Expr2 " + node + "expression=" + getExpression(node.getExpr3()));
	}

	@Override
	public void outAOrExprExpr2(AOrExprExpr2 node) {
		super.outAOrExprExpr2(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.OR, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAOr2ExprExpr2(AOr2ExprExpr2 node) {
		super.outAOr2ExprExpr2(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.OR, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAAddExprExpr2(AAddExprExpr2 node) {
		super.outAAddExprExpr2(node);
		// System.out.println("OUT add with " + node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.ADDITION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outASubExprExpr2(ASubExprExpr2 node) {
		super.outASubExprExpr2(node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.SUBSTRACTION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	// Following methods manage following grammar fragment
	/* expr3 =
		  {term} term |
		  {and_expr} [left]:expr3 and [right]:term |
		  {and2_expr} [left]:expr3 and2 [right]:term |
		  {mult_expr} [left]:expr3 mult [right]:term |
		  {div_expr} [left]:expr3 div [right]:term |
		  {mod_expr} [left]:expr3 mod [right]:term |
	      {power_expr} [left]:expr3 power [right]:term |
		  {not_expr} not term; */

	@Override
	public void outATermExpr3(ATermExpr3 node) {
		// System.out.println("OUT Term-Expr3 with " + node + " term=" + node.getTerm() + " of  " + node.getTerm().getClass());
		super.outATermExpr3(node);
		registerExpressionNode(node, getExpression(node.getTerm()));
		// System.out.println("***** ATermExpr3 " + node + "expression=" + getExpression(node.getTerm()));
	}

	@Override
	public void outAAndExprExpr3(AAndExprExpr3 node) {
		super.outAAndExprExpr3(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.AND, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAAnd2ExprExpr3(AAnd2ExprExpr3 node) {
		super.outAAnd2ExprExpr3(node);
		registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.AND, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAMultExprExpr3(AMultExprExpr3 node) {
		super.outAMultExprExpr3(node);
		// System.out.println("OUT mult with " + node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.MULTIPLICATION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outADivExprExpr3(ADivExprExpr3 node) {
		super.outADivExprExpr3(node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.DIVISION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAModExprExpr3(AModExprExpr3 node) {
		super.outAModExprExpr3(node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.MOD, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAPowerExprExpr3(APowerExprExpr3 node) {
		super.outAPowerExprExpr3(node);
		registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.POWER, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outANotExprExpr3(ANotExprExpr3 node) {
		super.outANotExprExpr3(node);
		registerExpressionNode(node, new UnaryOperatorExpression(BooleanUnaryOperator.NOT, getExpression(node.getTerm())));
	}

	// Following methods manage following grammar fragment
	/* function =
	  {cos_func} cos l_par expr2 r_par |
	  {acos_func} acos l_par expr2 r_par |
	  {sin_func} sin l_par expr2 r_par |
	  {asin_func} asin l_par expr2 r_par |
	  {tan_func} tan l_par expr2 r_par |
	  {atan_func} atan l_par expr2 r_par |
	  {exp_func} exp l_par expr2 r_par |
	  {log_func} log l_par expr2 r_par |
	  {sqrt_func} sqrt l_par expr2 r_par; */

	@Override
	public void outACosFuncFunction(ACosFuncFunction node) {
		super.outACosFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.COS, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAcosFuncFunction(AAcosFuncFunction node) {
		super.outAAcosFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ACOS, getExpression(node.getExpr2())));
	}

	@Override
	public void outASinFuncFunction(ASinFuncFunction node) {
		super.outASinFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.SIN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAsinFuncFunction(AAsinFuncFunction node) {
		super.outAAsinFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ASIN, getExpression(node.getExpr2())));
	}

	@Override
	public void outATanFuncFunction(ATanFuncFunction node) {
		super.outATanFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.TAN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAtanFuncFunction(AAtanFuncFunction node) {
		super.outAAtanFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ATAN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAExpFuncFunction(AExpFuncFunction node) {
		super.outAExpFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.EXP, getExpression(node.getExpr2())));
	}

	@Override
	public void outALogFuncFunction(ALogFuncFunction node) {
		super.outALogFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.LOG, getExpression(node.getExpr2())));
	}

	@Override
	public void outASqrtFuncFunction(ASqrtFuncFunction node) {
		super.outASqrtFuncFunction(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.SQRT, getExpression(node.getExpr2())));
	}

	// Following methods manage following grammar fragment
	/* constant = 
		  {true} true |
		  {false} false |
		  {null} null |
		  {this} this |
		  {pi} pi;*/

	@Override
	public void outATrueConstant(ATrueConstant node) {
		super.outATrueConstant(node);
		registerExpressionNode(node, BooleanConstant.TRUE);
	}

	@Override
	public void outAFalseConstant(AFalseConstant node) {
		super.outAFalseConstant(node);
		registerExpressionNode(node, BooleanConstant.FALSE);
	}

	@Override
	public void outAPiConstant(APiConstant node) {
		super.outAPiConstant(node);
		registerExpressionNode(node, FloatSymbolicConstant.PI);
	}

	@Override
	public void outANullConstant(ANullConstant node) {
		super.outANullConstant(node);
		registerExpressionNode(node, ObjectSymbolicConstant.NULL);
	}

	// Following methods manage following grammar fragment
	/* number =
		  {decimal_number} decimal_number |
		  {precise_number} precise_number |
		  {scientific_notation_number} scientific_notation_number |
		  {constant} constant; */

	@Override
	public void outADecimalNumberNumber(ADecimalNumberNumber node) {
		super.outADecimalNumberNumber(node);
		registerExpressionNode(node, makeDecimalNumber(node.getDecimalNumber()));
	}

	@Override
	public void outAPreciseNumberNumber(APreciseNumberNumber node) {
		super.outAPreciseNumberNumber(node);
		registerExpressionNode(node, makePreciseNumber(node.getPreciseNumber()));
	}

	@Override
	public void outAScientificNotationNumberNumber(AScientificNotationNumberNumber node) {
		super.outAScientificNotationNumberNumber(node);
		registerExpressionNode(node, makeScientificNotationNumber(node.getScientificNotationNumber()));
	}

	@Override
	public void outAConstantNumber(AConstantNumber node) {
		super.outAConstantNumber(node);
		registerExpressionNode(node, getExpression(node.getConstant()));
	}

	// Following methods manage following grammar fragment
	/* term =
		  {negative} minus term |
		  {number} number |
		  {string_value} string_value |
		  {chars_value} chars_value |
		  {function} function |
		  {binding} binding |
		  {expr} l_par expr r_par;*/

	@Override
	public void outANegativeTerm(ANegativeTerm node) {
		super.outANegativeTerm(node);
		registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.UNARY_MINUS, getExpression(node.getTerm())));
	}

	@Override
	public void outANumberTerm(ANumberTerm node) {
		super.outANumberTerm(node);
		registerExpressionNode(node, getExpression(node.getNumber()));
	}

	@Override
	public void outAStringValueTerm(AStringValueTerm node) {
		super.outAStringValueTerm(node);
		registerExpressionNode(node, makeStringValue(node.getStringValue()));
	}

	@Override
	public void outACharsValueTerm(ACharsValueTerm node) {
		super.outACharsValueTerm(node);
		registerExpressionNode(node, makeCharsValue(node.getCharsValue()));
	}

	@Override
	public void outAFunctionTerm(AFunctionTerm node) {
		super.outAFunctionTerm(node);
		registerExpressionNode(node, getExpression(node.getFunction()));
	}

	@Override
	public void outABindingTerm(ABindingTerm node) {
		super.outABindingTerm(node);
		registerExpressionNode(node, makeBinding(node.getBinding()));
	}

	@Override
	public void outAExprTerm(AExprTerm node) {
		super.outAExprTerm(node);
		registerExpressionNode(node, getExpression(node.getExpr()));
	}

}