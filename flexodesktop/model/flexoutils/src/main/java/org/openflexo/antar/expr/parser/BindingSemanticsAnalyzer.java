package org.openflexo.antar.expr.parser;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.parser.node.AAdditionalArg;
import org.openflexo.antar.expr.parser.node.AArgList;
import org.openflexo.antar.expr.parser.node.ACall;
import org.openflexo.antar.expr.parser.node.ACallBinding;
import org.openflexo.antar.expr.parser.node.AIdentifierBinding;
import org.openflexo.antar.expr.parser.node.ATail1Binding;
import org.openflexo.antar.expr.parser.node.ATail2Binding;
import org.openflexo.antar.expr.parser.node.PAdditionalArg;
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

	public static abstract class AbstractBindingPathElement {

	}

	public static class NormalBindingPathElement extends AbstractBindingPathElement {
		public String property;

		public NormalBindingPathElement(String aProperty) {
			property = aProperty;
		}

		@Override
		public String toString() {
			return "Normal[" + property + "]";
		}
	}

	public static class MethodCallBindingPathElement extends AbstractBindingPathElement {
		public String method;
		public List<Expression> args;

		public MethodCallBindingPathElement(String aMethod, List<Expression> someArgs) {
			method = aMethod;
			args = someArgs;
		}

		@Override
		public String toString() {
			return "Call[" + method + "(" + args + ")" + "]";
		}
	}

	private ArrayList<AbstractBindingPathElement> path;

	// private Hashtable<>

	public BindingSemanticsAnalyzer() {
		path = new ArrayList<AbstractBindingPathElement>();
	}

	public List<AbstractBindingPathElement> getPath() {
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

	protected NormalBindingPathElement makeNormalBindingPathElement(TIdentifier identifier) {
		NormalBindingPathElement returned = new NormalBindingPathElement(identifier.getText());
		path.add(0, returned);
		return returned;
	}

	public MethodCallBindingPathElement makeMethodCallBindingPathElement(ACall node) {
		AArgList argList = (AArgList) node.getArgList();
		ArrayList<Expression> args = new ArrayList<Expression>();
		args.add(getExpression(argList.getExpr()));
		for (PAdditionalArg aa : argList.getAdditionalArgs()) {
			AAdditionalArg additionalArg = (AAdditionalArg) aa;
			args.add(getExpression(additionalArg.getExpr()));
		}
		MethodCallBindingPathElement returned = new MethodCallBindingPathElement(node.getIdentifier().getText(), args);
		path.add(0, returned);
		return returned;
	}

	@Override
	public void outAIdentifierBinding(AIdentifierBinding node) {
		super.outAIdentifierBinding(node);
		makeNormalBindingPathElement(node.getIdentifier());
	}

	@Override
	public void outACallBinding(ACallBinding node) {
		super.outACallBinding(node);
		makeMethodCallBindingPathElement((ACall) node.getCall());
	}

	@Override
	public void outATail1Binding(ATail1Binding node) {
		super.outATail1Binding(node);
		makeNormalBindingPathElement(node.getIdentifier());
	}

	@Override
	public void outATail2Binding(ATail2Binding node) {
		super.outATail2Binding(node);
		makeMethodCallBindingPathElement((ACall) node.getCall());
	}

}