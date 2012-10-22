package org.openflexo.antar.expr.parser;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.node.AAdditionalArg;
import org.openflexo.antar.expr.parser.node.ABindingTerm;
import org.openflexo.antar.expr.parser.node.ACall;
import org.openflexo.antar.expr.parser.node.ACallBinding;
import org.openflexo.antar.expr.parser.node.AIdentifierBinding;
import org.openflexo.antar.expr.parser.node.ANonEmptyListArgList;
import org.openflexo.antar.expr.parser.node.ATail1Binding;
import org.openflexo.antar.expr.parser.node.ATail2Binding;
import org.openflexo.antar.expr.parser.node.PAdditionalArg;
import org.openflexo.antar.expr.parser.node.PArgList;
import org.openflexo.antar.expr.parser.node.TIdentifier;

/**
 * This class implements the semantics analyzer for a parsed AnTAR binding.<br>
 * Its main purpose is to structurally build a binding from a parsed AST.<br>
 * No semantics nor type checking is performed at this stage
 * 
 * @author sylvain
 * 
 */
class BindingSemanticsAnalyzer extends ExpressionSemanticsAnalyzer {

	private ArrayList<BindingValueAsExpression.AbstractBindingPathElement> path;

	/**
	 * This flag is used to escape binding processing that may happen in call args handling
	 */
	private boolean weAreDealingWithTheRightBinding = true;

	public BindingSemanticsAnalyzer() {
		path = new ArrayList<BindingValueAsExpression.AbstractBindingPathElement>();
	}

	public List<BindingValueAsExpression.AbstractBindingPathElement> getPath() {
		return path;
	};

	/* call = 
	  identifier arg_list ;

	 arg_list = 
	  l_par expr [additional_args]:additional_arg* r_par;

	 additional_arg = 
	  comma expr;

	 binding = 
	  {identifier} identifier |
	  {call} call |
	  {tail} identifier dot binding;*/

	protected BindingValueAsExpression.NormalBindingPathElement makeNormalBindingPathElement(TIdentifier identifier) {
		BindingValueAsExpression.NormalBindingPathElement returned = new BindingValueAsExpression.NormalBindingPathElement(
				identifier.getText());
		path.add(0, returned);
		return returned;
	}

	public BindingValueAsExpression.MethodCallBindingPathElement makeMethodCallBindingPathElement(ACall node) {
		PArgList argList = node.getArgList();
		ArrayList<Expression> args = new ArrayList<Expression>();
		if (argList instanceof ANonEmptyListArgList) {
			args.add(getExpression(((ANonEmptyListArgList) argList).getExpr()));
			for (PAdditionalArg aa : ((ANonEmptyListArgList) argList).getAdditionalArgs()) {
				AAdditionalArg additionalArg = (AAdditionalArg) aa;
				args.add(getExpression(additionalArg.getExpr()));
			}
		}
		BindingValueAsExpression.MethodCallBindingPathElement returned = new BindingValueAsExpression.MethodCallBindingPathElement(node
				.getIdentifier().getText(), args);
		path.add(0, returned);
		return returned;
	}

	@Override
	public void outAIdentifierBinding(AIdentifierBinding node) {
		super.outAIdentifierBinding(node);
		if (weAreDealingWithTheRightBinding) {
			makeNormalBindingPathElement(node.getIdentifier());
		}
	}

	@Override
	public void outACallBinding(ACallBinding node) {
		super.outACallBinding(node);
		if (weAreDealingWithTheRightBinding) {
			makeMethodCallBindingPathElement((ACall) node.getCall());
		}
	}

	@Override
	public void outATail1Binding(ATail1Binding node) {
		super.outATail1Binding(node);
		if (weAreDealingWithTheRightBinding) {
			makeNormalBindingPathElement(node.getIdentifier());
		}
	}

	@Override
	public void outATail2Binding(ATail2Binding node) {
		super.outATail2Binding(node);
		if (weAreDealingWithTheRightBinding) {
			makeMethodCallBindingPathElement((ACall) node.getCall());
		}
	}

	@Override
	public void inABindingTerm(ABindingTerm node) {
		super.inABindingTerm(node);
		// System.out.println("IN binding " + node);
		weAreDealingWithTheRightBinding = false;
	}

	@Override
	public void outABindingTerm(ABindingTerm node) {
		super.outABindingTerm(node);
		// System.out.println("OUT binding " + node);
		weAreDealingWithTheRightBinding = true;
	}

}