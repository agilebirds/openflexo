package org.openflexo.antar.binding;

import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.BindingValue.AbstractBindingPathElement;
import org.openflexo.antar.expr.BindingValue.NormalBindingPathElement;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;

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

	private static final BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	private Object object;
	private BindingDefinition bindingDefinition;
	private BindingModel bindingModel;

	private BindingEvaluator(Object object) {
		this.object = object;
		bindingDefinition = new BindingDefinition("object", object.getClass(), BindingDefinitionType.GET, true);
		bindingModel = new BindingModel();
		bindingModel.addToBindingVariables(new BindingVariable("object", object.getClass()));
	}

	private static String normalizeBindingPath(String bindingPath) {
		Expression expression = null;
		try {
			expression = ExpressionParser.parse(bindingPath);
			expression = expression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValue) {
						BindingValue bv = (BindingValue) e;
						if (bv.getParsedBindingPath().size() > 0) {
							AbstractBindingPathElement firstPathElement = bv.getParsedBindingPath().get(0);
							if (!(firstPathElement instanceof NormalBindingPathElement)
									|| !((NormalBindingPathElement) firstPathElement).property.equals("object")) {
								bv.getParsedBindingPath().add(0, new NormalBindingPathElement("object"));
							}
						}
						return bv;
					}
					return e;
				}
			});

			return expression.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
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

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	private Object evaluate(String bindingPath) throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException {
		String normalizedBindingPath = normalizeBindingPath(bindingPath);
		System.out.println("Normalize " + bindingPath + " to " + normalizedBindingPath);
		DataBinding binding = new DataBinding<Object>(normalizedBindingPath, this, Object.class, BindingDefinitionType.GET);
		binding.setBindingDefinition(bindingDefinition);
		System.out.println("Binding = " + binding + " valid=" + binding.isValid() + " as " + binding.getClass());
		if (!binding.isValid()) {
			System.out.println("not valid: " + binding.invalidBindingReason());
			throw new InvalidKeyValuePropertyException("Cannot interpret " + normalizedBindingPath + " for object of type "
					+ object.getClass());
		}
		return binding.getBindingValue(this);
	}

	public static Object evaluateBinding(String bindingPath, Object object) throws InvalidKeyValuePropertyException, TypeMismatchException,
			NullReferenceException {

		BindingEvaluator evaluator = new BindingEvaluator(object);
		return evaluator.evaluate(bindingPath);
	}

	public static void main(String[] args) {
		String thisIsATest = "Hello world, this is a test";
		try {
			System.out.println(evaluateBinding("toString", thisIsATest));
			System.out.println(evaluateBinding("toString()", thisIsATest));
			System.out.println(evaluateBinding("toString()+' hash='+object.hashCode()", thisIsATest));
			System.out.println(evaluateBinding("substring(6,11)", thisIsATest));
			System.out.println(evaluateBinding("substring(3,length()-2)+' hash='+hashCode()", thisIsATest));
		} catch (InvalidKeyValuePropertyException e) {
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
	}

}
