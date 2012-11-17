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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;

public class BindingValueAsExpression extends Expression {

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

	@Override
	public int getDepth() {
		return 0;
	}

	public List<BindingPathElement> getBindingPath() {
		return bindingPath;
	}

	public BindingVariable getBindingVariable() {
		return bindingVariable;
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
		return transformer.performTransformation(bv);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return EvaluationType.LITERAL;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	public boolean isValid() {
		if (getDataBinding() == null) {
			return false;
		}
		return performSemanticAnalysis(getDataBinding());
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

	public boolean performSemanticAnalysis(DataBinding<?> dataBinding) {
		setDataBinding(dataBinding);
		bindingVariable = null;
		bindingPath = new ArrayList<BindingPathElement>();
		if (getDataBinding() != null && getParsedBindingPath().size() > 0
				&& getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
			// Seems to be valid
			bindingVariable = dataBinding.getOwner().getBindingModel()
					.bindingVariableNamed(((NormalBindingPathElement) getParsedBindingPath().get(0)).property);
			// System.out.println("Found binding variable " + bindingVariable);
			int i = 0;
			for (AbstractBindingPathElement pathElement : getParsedBindingPath()) {
				if (i > 0) {
					System.out.println("> " + pathElement);
				}
				i++;
			}
		}

		// TODO
		return true;
	}

	public Object getBindingValue(BindingEvaluationContext context) {
		if (isValid()) {
			Object current = context.getValue(getBindingVariable());
			for (BindingPathElement e : getBindingPath()) {
				current = e.getBindingValue(current, context);
			}
			return current;
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context) {
		// TODO
	}

	public DataBinding getDataBinding() {
		return dataBinding;
	}

	public void setDataBinding(DataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}

}
