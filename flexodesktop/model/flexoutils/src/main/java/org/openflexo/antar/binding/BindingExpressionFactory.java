/**
 * 
 */
package org.openflexo.antar.binding;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingExpression.BindingValueConstant;
import org.openflexo.antar.binding.BindingExpression.BindingValueFunction;
import org.openflexo.antar.binding.BindingExpression.BindingValueVariable;
import org.openflexo.antar.expr.BindingValueAsExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.oldparser.ExpressionParser;
import org.openflexo.antar.expr.oldparser.ExpressionParser.ConstantFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.DefaultConstantFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.DefaultFunctionFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.DefaultVariableFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.FunctionFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.VariableFactory;
import org.openflexo.antar.expr.oldparser.Value;
import org.openflexo.antar.expr.oldparser.Word;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.xmlcode.StringEncoder;

public class BindingExpressionFactory extends StringEncoder.Converter<BindingExpression> {

	static final Logger logger = Logger.getLogger(BindingExpressionFactory.class.getPackage().getName());

	private final ExpressionParser parser;
	Bindable _bindable;
	boolean warnOnFailure = true;

	public BindingExpressionFactory() {
		super(BindingExpression.class);
		parser = new DefaultExpressionParser();
		parser.setConstantFactory(new BindingExpressionConstantFactory());
		parser.setVariableFactory(new BindingExpressionVariableFactory());
		parser.setFunctionFactory(new BindingExpressionFunctionFactory());
	}

	public void setWarnOnFailure(boolean aFlag) {
		warnOnFailure = aFlag;
	}

	public ConstantFactory getConstantFactory() {
		return parser.getConstantFactory();
	}

	public VariableFactory getVariableFactory() {
		return parser.getVariableFactory();
	}

	public FunctionFactory getFunctionFactory() {
		return parser.getFunctionFactory();
	}

	@Override
	public BindingExpression convertFromString(String aValue) {
		BindingExpression returned = new BindingExpression();
		try {
			Expression expression = parseExpressionFromString(aValue);
			returned.expression = expression;
		} catch (org.openflexo.antar.expr.parser.ParseException e) {
			returned.unparsableValue = aValue;
		}
		returned.setOwner(_bindable);
		return returned;
	}

	public Expression parseExpressionFromString(String aValue) throws org.openflexo.antar.expr.parser.ParseException {
		Expression parsedExpression = org.openflexo.antar.expr.parser.ExpressionParser.parse(aValue);
		// Deprecated
		// We apply this transformer to match old binding model
		try {
			Expression returned = parsedExpression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof Constant) {
						System.out.println("Je tombe sur la constante " + e);
						return new BindingValueConstant((Constant) e, _bindable);
					} else if (e instanceof BindingValueAsExpression) {
						System.out.println("Je tombe sur le binding " + e);
						return new BindingValueVariable(((BindingValueAsExpression) e).toString(), _bindable);
					}
					return e;
				}
			});

			System.out.println("Returned = " + returned);
			return returned;

		} catch (TransformException e) {
			logger.warning("Unexpected exception during transforming: " + e);
			e.printStackTrace();
			return parsedExpression;
		}

		// return parser.parse(aValue);
	}

	@Override
	public String convertToString(BindingExpression value) {
		return value.getStringRepresentation();
	}

	public Bindable getBindable() {
		return _bindable;
	}

	public void setBindable(Bindable bindable) {
		_bindable = bindable;
	}

	public class BindingExpressionConstantFactory implements ConstantFactory {
		private final DefaultConstantFactory constantFactory = new DefaultConstantFactory();

		@Override
		public Expression makeConstant(Value value) {
			if (BindingExpression.logger.isLoggable(Level.FINE)) {
				BindingExpression.logger.fine("Make constant from " + value + " of " + value.getClass().getSimpleName());
			}
			return new BindingValueConstant(constantFactory.makeConstant(value), _bindable);
		}
	}

	public class BindingExpressionVariableFactory implements VariableFactory {
		private final DefaultVariableFactory variableFactory = new DefaultVariableFactory();

		@Override
		public Expression makeVariable(Word value) {
			return new BindingValueVariable(variableFactory.makeVariable(value), _bindable);
		}
	}

	public class BindingExpressionFunctionFactory implements FunctionFactory {
		private final DefaultFunctionFactory functionFactory = new DefaultFunctionFactory();

		@Override
		public Expression makeFunction(String functionName, Vector<Expression> args) {
			return new BindingValueFunction(functionFactory.makeFunction(functionName, args), _bindable);
		}
	}

	public ExpressionParser getParser() {
		return parser;
	}

	public ExpressionPrettyPrinter getPrettyPrinter() {
		return BindingExpression.prettyPrinter;
	}

}