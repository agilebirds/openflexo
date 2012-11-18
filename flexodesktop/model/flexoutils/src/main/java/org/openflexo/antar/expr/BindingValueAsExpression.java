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
package org.openflexo.antar.expr;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.AbstractBinding.TargetObject;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SettableBindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

public class BindingValueAsExpression extends Expression {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	private final ArrayList<Object> EMPTY_LIST = new ArrayList<Object>();

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

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NormalBindingPathElement) {
				NormalBindingPathElement e = (NormalBindingPathElement) obj;
				return property.equals(e.property);
			}
			return super.equals(obj);
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

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodCallBindingPathElement) {
				MethodCallBindingPathElement e = (MethodCallBindingPathElement) obj;
				return method.equals(e.method) && args.equals(e.args);
			}
			return super.equals(obj);
		}

	}

	private List<AbstractBindingPathElement> parsedBindingPath;

	private BindingVariable bindingVariable;
	private List<BindingPathElement> bindingPath;

	public BindingValueAsExpression(List<AbstractBindingPathElement> aBindingPath) {
		super();
		this.parsedBindingPath = aBindingPath;
		bindingVariable = null;
		bindingPath = new ArrayList<BindingPathElement>();
	}

	public DataBinding<?> getDataBinding() {
		return dataBinding;
	}

	public void setDataBinding(DataBinding<?> dataBinding) {
		this.dataBinding = dataBinding;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public List<BindingPathElement> getBindingPath() {
		return bindingPath;
	}

	public BindingPathElement getLastBindingPathElement() {
		if (getBindingPath() != null && getBindingPath().size() > 0) {
			return getBindingPath().get(getBindingPath().size() - 1);
		}
		return getBindingVariable();
	}

	public BindingVariable getBindingVariable() {
		return bindingVariable;
	}

	public Type getAccessedType() {
		System.out.println("Quel type accede pour " + this + " ?");
		System.out.println("data binding = " + getDataBinding());
		System.out.println("valid = " + isValid());
		if (isValid() && getLastBindingPathElement() != null) {
			return getLastBindingPathElement().getType();
		}
		return null;
	}

	public List<AbstractBindingPathElement> getParsedBindingPath() {
		return parsedBindingPath;
	}

	public boolean containsAMethodCall() {
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof MethodCallBindingPathElement) {
				return true;
			}
		}
		return false;
	}

	public boolean isSimpleVariable() {
		return getParsedBindingPath().size() == 1 && getParsedBindingPath().get(0) instanceof NormalBindingPathElement;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context) throws TypeMismatchException {
		if (containsAMethodCall()) {
			ArrayList<AbstractBindingPathElement> newBindingPath = new ArrayList<AbstractBindingPathElement>();
			for (AbstractBindingPathElement e : getBindingPath()) {
				if (e instanceof NormalBindingPathElement) {
					newBindingPath.add(new NormalBindingPathElement(((NormalBindingPathElement) e).property));
				} else if (e instanceof MethodCallBindingPathElement) {
					ArrayList<Expression> newArgs = new ArrayList<Expression>();
					for (Expression arg : ((MethodCallBindingPathElement) e).args) {
						newArgs.add(arg.evaluate(context));
					}
					newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
				}
			}
			return new BindingValueAsExpression(newBindingPath);
		}
		return this;
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		ArrayList<AbstractBindingPathElement> newBindingPath = new ArrayList<AbstractBindingPathElement>();
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof NormalBindingPathElement) {
				newBindingPath.add(new NormalBindingPathElement(((NormalBindingPathElement) e).property));
			} else if (e instanceof MethodCallBindingPathElement) {
				ArrayList<Expression> newArgs = new ArrayList<Expression>();
				for (Expression arg : ((MethodCallBindingPathElement) e).args) {
					newArgs.add(arg.transform(transformer));
				}
				newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
			}
		}
		BindingValueAsExpression bv = new BindingValueAsExpression(newBindingPath);
		bv.setDataBinding(getDataBinding());
		System.out.println("BindingValue created with data binding " + getDataBinding());
		if (getDataBinding() == null) {
			Thread.dumpStack();
		}
		return transformer.performTransformation(bv);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return TypeUtils.kindOfType(getAccessedType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	public boolean isSettable() {
		if (getLastBindingPathElement() instanceof SettableBindingPathElement) {
			return ((SettableBindingPathElement) getLastBindingPathElement()).isSettable();
		}
		return false;
	}

	public boolean isValid() {
		return isValid(getDataBinding());
	}

	public boolean isValid(DataBinding<?> dataBinding) {
		setDataBinding(dataBinding);
		if (dataBinding == null) {
			return false;
		}
		return performSemanticAnalysis(dataBinding);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingValueAsExpression) {
			BindingValueAsExpression e = (BindingValueAsExpression) obj;
			return getParsedBindingPath().equals(e.getParsedBindingPath());
		}
		return super.equals(obj);
	}

	private DataBinding dataBinding;

	private boolean isSemanticAnalysisPerformed = false;
	private boolean isSemanticAnalysisSuccessfull = false;

	public boolean performSemanticAnalysis(DataBinding<?> dataBinding) {
		if (isSemanticAnalysisPerformed) {
			return isSemanticAnalysisSuccessfull;
		}
		isSemanticAnalysisPerformed = true;
		isSemanticAnalysisSuccessfull = false;
		setDataBinding(dataBinding);
		bindingVariable = null;
		bindingPath = new ArrayList<BindingPathElement>();
		if (getDataBinding() != null && getParsedBindingPath().size() > 0
				&& getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
			// Seems to be valid
			bindingVariable = dataBinding.getOwner().getBindingModel()
					.bindingVariableNamed(((NormalBindingPathElement) getParsedBindingPath().get(0)).property);
			BindingPathElement current = bindingVariable;
			// System.out.println("Found binding variable " + bindingVariable);
			int i = 0;
			for (AbstractBindingPathElement pathElement : getParsedBindingPath()) {
				if (i > 0) {
					if (pathElement instanceof NormalBindingPathElement) {
						SimplePathElement newPathElement = dataBinding.getOwner().getBindingFactory()
								.makeSimplePathElement(current, ((NormalBindingPathElement) pathElement).property);
						if (newPathElement != null) {
							bindingPath.add(newPathElement);
							current = newPathElement;
							System.out.println("> SIMPLE " + pathElement);
						} else {
							return false;
						}
					} else if (pathElement instanceof MethodCallBindingPathElement) {
						MethodCallBindingPathElement methodCall = (MethodCallBindingPathElement) pathElement;
						List<DataBinding<?>> args = new ArrayList<DataBinding<?>>();
						int argIndex = 0;
						for (Expression arg : methodCall.args) {
							DataBinding<?> argDataBinding = new DataBinding<Object>("arg" + argIndex, dataBinding.getOwner(), Object.class,
									BindingDefinitionType.GET);
							argDataBinding.setExpression(arg);
							argDataBinding.setDeclaredType(argDataBinding.getAnalyzedType());
							args.add(argDataBinding);
							argIndex++;
						}
						FunctionPathElement newPathElement = dataBinding.getOwner().getBindingFactory()
								.makeFunctionPathElement(current, ((MethodCallBindingPathElement) pathElement).method, args);
						if (newPathElement != null) {
							bindingPath.add(newPathElement);
							current = newPathElement;
							System.out.println("> FUNCTION " + pathElement);
						} else {
							return false;
						}
					} else {
						logger.warning("Unexpected " + pathElement);
						return false;
					}
				}
				i++;
			}
		}

		isSemanticAnalysisSuccessfull = true;
		return true;
	}

	public Object getBindingValue(BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (isValid()) {
			Object current = context.getValue(getBindingVariable());
			for (BindingPathElement e : getBindingPath()) {
				current = e.getBindingValue(current, context);
			}
			return current;
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (!isValid()) {
			return;
		}

		if (!isSettable()) {
			return;
		}

		// System.out.println("Sets value: "+value);
		// System.out.println("Binding: "+getStringRepresentation());

		Object returned = context.getValue(getBindingVariable());

		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		try {
			for (BindingPathElement element : getBindingPath()) {
				if (element != getLastBindingPathElement()) {
					// System.out.println("Apply "+element);
					returned = element.getBindingValue(returned, context);
					if (returned == null) {
						throw new NullReferenceException();
					}
					// System.out.println("Obtain "+returned);
				}
			}
			if (returned == null) {
				throw new NullReferenceException();
			}

			// logger.info("returned="+returned);
			// logger.info("lastElement="+getBindingPath().lastElement());

			if (getLastBindingPathElement() instanceof SettableBindingPathElement
					&& ((SettableBindingPathElement) getLastBindingPathElement()).isSettable()) {
				((SettableBindingPathElement) getLastBindingPathElement()).setBindingValue(value, returned, context);
			} else {
				logger.warning("Binding " + this + " is not settable");
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning("InvalidObjectSpecificationException raised while evaluating SET " + this + " : " + e.getMessage());
			// System.out.println("returned="+returned);
			// System.out.println("value="+value);
			// System.out.println("((KeyValueProperty)getBindingPath().lastElement()).getName()="+((KeyValueProperty)getBindingPath().lastElement()).getName());
		}

	}

	/**
	 * Build and return a list of objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	public List<Object> getConcernedObjects(BindingEvaluationContext context) {
		if (!isValid()) {
			return EMPTY_LIST;
		}
		if (!isSettable()) {
			return EMPTY_LIST;
		}

		List<Object> returned = new ArrayList<Object>();

		Object current = context.getValue(getBindingVariable());
		returned.add(current);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getLastBindingPathElement()) {
				try {
					current = element.getBindingValue(current, context);
				} catch (TypeMismatchException e) {
				} catch (NullReferenceException e) {
				}
				if (current == null) {
					return returned;
				} else {
					returned.add(current);
				}
			}
		}
		if (current == null) {
			return null;
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	public List<TargetObject> getTargetObjects(BindingEvaluationContext context) {
		if (!isValid()) {
			return null;
		}

		ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		Object current = context.getValue(getBindingVariable());

		returned.add(new TargetObject(context, getBindingVariable().getVariableName()));

		if (current == null) {
			return returned;
		}

		try {
			for (BindingPathElement element : getBindingPath()) {
				returned.add(new TargetObject(current, element.getLabel()));
				try {
					current = element.getBindingValue(current, context);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				}
				if (current == null) {
					return returned;
				}
			}
		} catch (InvalidObjectSpecificationException e) {
			logger.warning(e.getMessage());
			return returned;
		}

		return returned;
	}

}
