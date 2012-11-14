/**
 * 
 */
package org.openflexo.antar.binding;

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingExpression.BindingValueConstant;
import org.openflexo.antar.binding.BindingExpression.BindingValueVariable;
import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.xmlcode.StringEncoder;

public class BindingExpressionFactory extends StringEncoder.Converter<BindingExpression> {

	static final Logger logger = Logger.getLogger(BindingExpressionFactory.class.getPackage().getName());

	// private final ExpressionParser parser;
	boolean warnOnFailure = true;

	public BindingExpressionFactory() {
		super(BindingExpression.class);
		/*parser = new DefaultExpressionParser();
		parser.setConstantFactory(new BindingExpressionConstantFactory());
		parser.setVariableFactory(new BindingExpressionVariableFactory());
		parser.setFunctionFactory(new BindingExpressionFunctionFactory());*/
	}

	public void setWarnOnFailure(boolean aFlag) {
		warnOnFailure = aFlag;
	}

	/*public ConstantFactory getConstantFactory() {
		return parser.getConstantFactory();
	}

	public VariableFactory getVariableFactory() {
		return parser.getVariableFactory();
	}

	public FunctionFactory getFunctionFactory() {
		return parser.getFunctionFactory();
	}*/

	@Override
	public BindingExpression convertFromString(String value) {
		throw new UnsupportedOperationException("No bindable provided");
	}

	public BindingExpression convertFromString(String aValue, Bindable bindable) {
		BindingExpression returned = new BindingExpression();
		try {
			Expression expression = parseExpressionFromString(aValue, bindable);
			returned.expression = expression;
		} catch (org.openflexo.antar.expr.parser.ParseException e) {
			returned.unparsableValue = aValue;
		}
		returned.setOwner(bindable);

		/*System.out.println("valid=" + returned.isBindingValid());
		if (returned.getExpression() instanceof BinaryOperatorExpression) {
			BinaryOperatorExpression e = (BinaryOperatorExpression) returned.getExpression();
			System.out.println("left=" + e.getLeftArgument() + " of "
					+ (e.getLeftArgument() != null ? e.getLeftArgument().getClass() : "null"));
			System.out.println("right=" + e.getRightArgument() + " of "
					+ (e.getRightArgument() != null ? e.getRightArgument().getClass() : "null"));
			if (e.getLeftArgument() instanceof BindingValueVariable) {
				BindingValueVariable left = (BindingValueVariable) e.getLeftArgument();
				System.out.println("left is valid = " + left.isValid());
			}
			if (e.getRightArgument() instanceof BindingValueConstant) {
				BindingValueConstant right = (BindingValueConstant) e.getRightArgument();
				System.out.println("right sb = " + right.getStaticBinding());
				// System.out.println("right valid = " + right.getStaticBinding());
				// System.out.println("right invalid reason= " + right.getStaticBinding().invalidBindingReason());
			}
		}*/

		return returned;
	}

	public Expression parseExpressionFromString(String aValue, final Bindable bindable)
			throws org.openflexo.antar.expr.parser.ParseException {
		Expression parsedExpression = org.openflexo.antar.expr.parser.ExpressionParser.parse(aValue);
		return convertToOldBindingModel(parsedExpression, bindable);
		// return parser.parse(aValue);
	}

	@Override
	public String convertToString(BindingExpression value) {
		return value.getStringRepresentation();
	}

	/*public class BindingExpressionConstantFactory implements ConstantFactory {
		private final DefaultConstantFactory constantFactory = new DefaultConstantFactory();

		@Override
		public Expression makeConstant(Value value, Bindable bindable) {
			if (BindingExpression.logger.isLoggable(Level.FINE)) {
				BindingExpression.logger.fine("Make constant from " + value + " of " + value.getClass().getSimpleName());
			}
			return new BindingValueConstant(constantFactory.makeConstant(value, bindable), bindable);
		}
	}

	public class BindingExpressionVariableFactory implements VariableFactory {
		private final DefaultVariableFactory variableFactory = new DefaultVariableFactory();

		@Override
		public Expression makeVariable(Word value, Bindable bindable) {
			return new BindingValueVariable(variableFactory.makeVariable(value, bindable), bindable);
		}
	}

	public class BindingExpressionFunctionFactory implements FunctionFactory {
		private final DefaultFunctionFactory functionFactory = new DefaultFunctionFactory();

		@Override
		public Expression makeFunction(String functionName, List<Expression> args, Bindable bindable) {
			return new BindingValueFunction(functionFactory.makeFunction(functionName, args, bindable), bindable);
		}
	}*/

	/*public ExpressionParser getParser() {
		return parser;
	}*/

	public ExpressionPrettyPrinter getPrettyPrinter() {
		return BindingExpression.prettyPrinter;
	}

	// We apply this transformer to match old binding model
	@Deprecated
	protected static Expression convertToOldBindingModel(Expression e, final Bindable bindable) {
		try {
			Expression returned = e.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof Constant) {
						// System.out.println("Found constant " + e + " of " + e.getClass());
						return new BindingValueConstant((Constant) e, bindable);
					} else if (e instanceof BindingValueAsExpression) {
						// System.out.println("Found expression " + e + " of " + e.getClass());
						return new BindingValueVariable(((BindingValueAsExpression) e).toString(), bindable);
					}
					return e;
				}
			});
			// System.out.println("Returned = " + returned);
			return returned;

		} catch (TransformException ex) {
			logger.warning("Unexpected exception during transforming: " + ex);
			ex.printStackTrace();
			return e;
		}
	}

}