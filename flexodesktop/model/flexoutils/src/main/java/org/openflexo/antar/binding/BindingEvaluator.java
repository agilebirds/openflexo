package org.openflexo.antar.binding;

import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.EvaluationContext;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.oldparser.ExpressionParser;
import org.openflexo.antar.expr.oldparser.ParseException;
import org.openflexo.antar.expr.oldparser.Word;
import org.openflexo.antar.expr.oldparser.ExpressionParser.DefaultFunctionFactory;
import org.openflexo.antar.expr.oldparser.ExpressionParser.DefaultVariableFactory;

/**
 * Utility class allowing to compute binding value over an expression and a given object.<br>
 * Expression must be expressed with or without supplied object (when mentioned, use "object." prefix).<br>
 * Considering we are passing a String, valid binding path are for example:
 * <ul>
 * <li>toString</li>
 * <li>toString()</li>
 * <li>toString()+' hash='+object.hashCode()</li>
 * <li>substring(6,11)</li>
 * <li>substring(3,length()-2)+' hash='+hashCode()</li>
 * </ul>
 * 
 * @author sylvain
 * 
 */
public class BindingEvaluator implements Bindable, BindingEvaluationContext {

	private static final DefaultBindingFactory BINDING_FACTORY = new DefaultBindingFactory();

	private Object object;
	private BindingDefinition bindingDefinition;
	private BindingModel bindingModel;

	private BindingEvaluator(Object object) {
		this.object = object;
		bindingDefinition = new BindingDefinition("object", object.getClass(), BindingDefinitionType.GET, true);
		bindingModel = new BindingModel();
		bindingModel.addToBindingVariables(new BindingVariableImpl(this, "object", object.getClass()));
		BINDING_FACTORY.setBindable(this);
	}

	private static String normalizeBindingPath(String bindingPath) {
		DefaultExpressionParser parser = new DefaultExpressionParser();
		Expression expression = null;
		try {
			expression = parser.parse(bindingPath);
			Expression newExpression = expression.evaluate(new EvaluationContext(new ExpressionParser.DefaultConstantFactory(),
					new DefaultVariableFactory() {
						@Override
						public Variable makeVariable(Word value) {
							if (!value.toString().startsWith("object.")) {
								return super.makeVariable(new Word("object." + value));
							} else {
								return super.makeVariable(value);
							}
						}
					}, new DefaultFunctionFactory() {
						@Override
						public Function makeFunction(String functionName, Vector<Expression> args) {
							if (!functionName.startsWith("object.")) {
								return super.makeFunction("object." + functionName, args);
							} else {
								return super.makeFunction(functionName, args);
							}
						}
					}));
			return newExpression.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return expression.toString();
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return BINDING_FACTORY;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		return object;
	}

	private Object evaluate(String bindingPath) throws InvalidKeyValuePropertyException {
		String normalizedBindingPath = normalizeBindingPath(bindingPath);
		// System.out.println("Normalize " + bindingPath + " to " + normalizedBindingPath);
		AbstractBinding binding = BINDING_FACTORY.convertFromString(normalizedBindingPath);
		binding.setBindingDefinition(bindingDefinition);
		// System.out.println("Binding = " + binding + " valid=" + binding.isBindingValid());
		if (!binding.isBindingValid()) {
			throw new InvalidKeyValuePropertyException("Cannot interpret " + normalizedBindingPath + " for object of type "
					+ object.getClass());
		}
		return binding.getBindingValue(this);
	}

	public static Object evaluateBinding(String bindingPath, Object object) throws InvalidKeyValuePropertyException {

		BindingEvaluator evaluator = new BindingEvaluator(object);
		return evaluator.evaluate(bindingPath);
	}

	public static void main(String[] args) {
		String thisIsATest = "Hello world, this is a test";
		System.out.println(evaluateBinding("toString", thisIsATest));
		System.out.println(evaluateBinding("toString()", thisIsATest));
		System.out.println(evaluateBinding("toString()+' hash='+object.hashCode()", thisIsATest));
		System.out.println(evaluateBinding("substring(6,11)", thisIsATest));
		System.out.println(evaluateBinding("substring(3,length()-2)+' hash='+hashCode()", thisIsATest));
	}

}
