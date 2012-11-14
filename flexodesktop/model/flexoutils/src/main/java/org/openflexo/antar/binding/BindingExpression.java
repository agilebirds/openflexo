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
package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.MethodCall.MethodCallArgument;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;

public class BindingExpression extends AbstractBinding {

	@SuppressWarnings("hiding")
	public static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

	static final ExpressionPrettyPrinter prettyPrinter = new DefaultExpressionPrettyPrinter() {
		@Override
		public String getStringRepresentation(Expression expression) {
			if (expression instanceof BindingValueFunction) {
				return makeStringRepresentation(((BindingValueFunction) expression).getFunction());
			} else if (expression instanceof BindingValueVariable) {
				return makeStringRepresentation(((BindingValueVariable) expression).getVariable());
			} else if (expression instanceof BindingValueConstant) {
				return makeStringRepresentation(((BindingValueConstant) expression).getConstant());
			} else {
				return super.getStringRepresentation(expression);
			}
		}
	};

	Expression expression;

	public BindingExpression() {
		super();
	}

	public BindingExpression(BindingDefinition bindingDefinition, Bindable owner) {
		super(bindingDefinition, owner);
	}

	public BindingExpression(BindingDefinition bindingDefinition, Bindable owner, AbstractBinding abstractBinding) {
		super(bindingDefinition, owner);
		if (abstractBinding instanceof BindingValue) {
			setOwner(abstractBinding.getOwner());
			setExpression(new BindingValueVariable((BindingValue) abstractBinding));
		} else if (abstractBinding instanceof StaticBinding) {
			setExpression(new BindingValueConstant((StaticBinding) abstractBinding));
		} else if (abstractBinding instanceof BindingExpression) {
			setExpression(((BindingExpression) abstractBinding).getExpression());
		}
	}

	public BindingExpression(BindingDefinition bindingDefinition, Bindable owner, Expression anExpression) {
		super(bindingDefinition, owner);
		expression = anExpression;
	}

	public BindingExpression(Expression anExpression) {
		super();
		expression = anExpression;
	}

	@Override
	public String getStringRepresentation() {
		if (expression != null) {
			return prettyPrinter.getStringRepresentation(expression);
		} else if (unparsableValue != null) {
			return "UNPARSABLE:" + unparsableValue;
		}
		return "null";
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static BindingExpression makeBindingExpression(String value, Bindable owner) {
		if (owner != null) {
			BindingExpressionFactory factory = owner.getBindingFactory().getBindingExpressionFactory();
			BindingExpression returned = factory.convertFromString(value, owner);
			returned.setOwner(owner);
			return returned;
		}
		return null;
	}

	public BindingExpression getBindingExpressionFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	@Override
	public BindingExpressionFactory getConverter() {
		if (getOwner() != null) {
			return getOwner().getBindingFactory().getBindingExpressionFactory();
		}
		return null;
		// return bindingExpressionConverter;
	}

	public static class BindingValueConstant extends Expression {
		private Constant constant;
		private StaticBinding staticBinding;
		private Bindable _bindable;

		public BindingValueConstant(Constant aConstant, Bindable bindable) {
			super();
			_bindable = bindable;
			setConstant(aConstant);
		}

		public BindingValueConstant(StaticBinding aStaticBinding) {
			super();
			setStaticBinding(aStaticBinding);
		}

		@Override
		public int getDepth() {
			return 0;
		}

		public StaticBinding getStaticBinding() {
			return staticBinding;
		}

		public void setStaticBinding(StaticBinding aStaticBinding) {
			this.staticBinding = aStaticBinding;
			if (aStaticBinding instanceof BooleanStaticBinding) {
				if (((BooleanStaticBinding) aStaticBinding).getValue()) {
					constant = Constant.BooleanConstant.TRUE;
				} else {
					constant = Constant.BooleanConstant.FALSE;
				}
			} else if (aStaticBinding instanceof IntegerStaticBinding) {
				constant = new Constant.IntegerConstant(((IntegerStaticBinding) aStaticBinding).getValue());
			} else if (aStaticBinding instanceof FloatStaticBinding) {
				constant = new Constant.FloatConstant(((FloatStaticBinding) aStaticBinding).getValue());
			} else if (aStaticBinding instanceof StringStaticBinding) {
				constant = new Constant.StringConstant(((StringStaticBinding) aStaticBinding).getValue());
			} else if (aStaticBinding instanceof NullStaticBinding) {
				constant = ObjectSymbolicConstant.NULL;
			}
		}

		public Constant getConstant() {
			return constant;
		}

		public void setConstant(Constant aConstant) {
			this.constant = aConstant;
			BindingDefinition bd = null;
			if (_bindable != null) {
				bd = new BindingDefinition("constant", Object.class, BindingDefinitionType.GET, true);
			}
			if (constant == Constant.BooleanConstant.TRUE) {
				staticBinding = new BooleanStaticBinding(bd, _bindable, true);
			} else if (constant == Constant.BooleanConstant.FALSE) {
				staticBinding = new BooleanStaticBinding(bd, _bindable, false);
			} else if (constant instanceof Constant.IntegerConstant) {
				staticBinding = new IntegerStaticBinding(bd, _bindable, ((Constant.IntegerConstant) constant).getValue());
			} else if (constant instanceof Constant.FloatConstant) {
				staticBinding = new FloatStaticBinding(bd, _bindable, ((Constant.FloatConstant) constant).getValue());
			} else if (constant instanceof Constant.StringConstant) {
				staticBinding = new StringStaticBinding(bd, _bindable, ((Constant.StringConstant) constant).getValue());
			} else if (constant == ObjectSymbolicConstant.NULL) {
				staticBinding = new NullStaticBinding(bd, _bindable);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("staticBinding=" + staticBinding + " bindable=" + staticBinding.getOwner() + " bd="
						+ staticBinding.getBindingDefinition());
			}
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
			return constant.evaluate(bindable);
		}*/

		@Override
		public Expression transform(ExpressionTransformer transformer) throws TransformException {
			return transformer.performTransformation(this);
		}

		@Override
		public String toString() {
			if (constant != null) {
				return constant.toString();
			}
			return "null";
		}

		@Override
		public EvaluationType getEvaluationType() throws TypeMismatchException {
			return constant.getEvaluationType();
		}

		@Override
		protected Vector<Expression> getChilds() {
			return null;
		}

	}

	public static class BindingValueVariable extends Expression {
		private Variable variable;
		private BindingValue bindingValue;

		public BindingValueVariable(BindingValue variableAsBindingValue) {
			super();
			variable = new Variable(variableAsBindingValue.getStringRepresentation());
			bindingValue = variableAsBindingValue;
		}

		public BindingValueVariable(String variableName, Bindable bindable) {
			this(new Variable(variableName), bindable, bindable != null ? new BindingDefinition("object", Object.class,
					BindingDefinitionType.GET, true) : null);
		}

		public BindingValueVariable(String variableName, Bindable bindable, BindingDefinition bd) {
			this(new Variable(variableName), bindable, bd);
		}

		public BindingValueVariable(Variable aVariable, Bindable bindable) {
			this(aVariable, bindable, bindable != null ? new BindingDefinition("object", Object.class, BindingDefinitionType.GET, true)
					: null);
		}

		public BindingValueVariable(Variable aVariable, Bindable bindable, BindingDefinition bd) {
			super();
			setVariable(aVariable);
			if (bindable != null) {
				BindingValueFactory factory = bindable.getBindingFactory().getBindingValueFactory();
				factory.setWarnOnFailure(false);
				bindingValue = factory.convertFromString(aVariable.getName(), bindable);
				factory.setWarnOnFailure(true);
			}
			if (bindingValue == null) {
				// System.out.println("Cannot parse " + aVariable.getName());
				// System.out.println("binding factory = " + bindable.getBindingFactory());
				bindingValue = new BindingValue(null, bindable);
				bindingValue.setUnparsableValue(aVariable.getName());
			}
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(bd);
			}
		}

		@Override
		public int getDepth() {
			return 0;
		}

		public BindingValue getBindingValue() {
			return bindingValue;
		}

		public void setBindingValue(BindingValue aBindingValue) {
			this.bindingValue = aBindingValue;
		}

		public Variable getVariable() {
			return variable;
		}

		public void setVariable(Variable aVariable) {
			this.variable = aVariable;
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
			return variable.evaluate(context, bindable);
		}*/

		@Override
		public Expression transform(ExpressionTransformer transformer) throws TransformException {
			return transformer.performTransformation(this);
		}

		@Override
		public String toString() {
			if (variable != null) {
				return variable.toString();
			}
			return "null";
		}

		@Override
		public EvaluationType getEvaluationType() throws TypeMismatchException {
			if (bindingValue != null && bindingValue.isBindingValid() && bindingValue.getAccessedType() != null) {
				return TypeUtils.kindOfType(bindingValue.getAccessedType());
			}
			return variable.getEvaluationType();
		}

		public String getName() {
			return getVariable().getName();
		}

		public boolean isValid() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("is BingValueVariable valid ? "
						+ getVariable().isValid()
						+ " getBindingValue()="
						+ getBindingValue()
						+ " return "
						+ (getVariable() != null && getVariable().isValid() && getBindingValue() != null && getBindingValue()
								.isBindingValid()));
			}
			return getVariable() != null && getVariable().isValid() && getBindingValue() != null && getBindingValue().isBindingValid();
		}

		@Override
		protected Vector<Expression> getChilds() {
			return null;
		}
	}

	public static class BindingValueFunction extends Expression {
		private Function function;
		private BindingValue bindingValue;

		public BindingValueFunction(BindingValue compoundBindingValue) {
			super();
			Vector<Expression> args = new Vector<Expression>();
			if (compoundBindingValue.isCompoundBinding()) {
				MethodCall mc = (MethodCall) compoundBindingValue.getBindingPathLastElement();
				for (MethodCallArgument arg : mc.getArgs()) {
					AbstractBinding binding = arg.getBinding();
					Expression newExp = null;
					if (binding == null) {
						newExp = new BindingValueVariable("", compoundBindingValue.getOwner());
					} else if (binding instanceof StaticBinding) {
						newExp = new BindingValueConstant((StaticBinding) binding);
					} else if (binding instanceof BindingExpression) {
						newExp = ((BindingExpression) binding).getExpression();
					} else if (binding instanceof BindingValue) {
						if (((BindingValue) binding).isCompoundBinding()) {
							newExp = new BindingValueFunction((BindingValue) binding);
						} else {
							newExp = new BindingValueVariable((BindingValue) binding);
						}
					}
					args.add(newExp);
				}
				function = new Function(compoundBindingValue.getStringRepresentationWithoutNlastElements(1), args);
			}
			bindingValue = compoundBindingValue;
		}

		public BindingValueFunction(String functionName, Vector<Expression> args, Bindable bindable) {
			this(new Function(functionName, args), bindable, bindable != null ? new BindingDefinition("object", Object.class,
					BindingDefinitionType.GET, true) : null);
		}

		public BindingValueFunction(String functionName, Vector<Expression> args, Bindable bindable, BindingDefinition bd) {
			this(new Function(functionName, args), bindable, bd);
		}

		public BindingValueFunction(Function aFunction, Bindable bindable) {
			this(aFunction, bindable, bindable != null ? new BindingDefinition("object", Object.class, BindingDefinitionType.GET, true)
					: null);
			// System.out.println("Je me fabrique une BindingValueFunction avec "+aFunction);
			// System.out.println("Mon bindable c'est "+bindable);
			// System.out.println("BindingModel="+bindable.getBindingModel());
		}

		public BindingValueFunction(Function aFunction, Bindable bindable, BindingDefinition bd) {
			super();
			setFunction(aFunction);

			StringBuffer analyzeAsBindingValue = new StringBuffer();
			analyzeAsBindingValue.append(aFunction.getName() + "(");
			boolean isFirst = true;
			for (Expression arg : aFunction.getArgs()) {
				analyzeAsBindingValue.append((!isFirst ? "," : "") + arg.toString());
				isFirst = false;
			}
			analyzeAsBindingValue.append(")");

			if (bindable != null) {
				BindingValueFactory factory = bindable.getBindingFactory().getBindingValueFactory();
				factory.setWarnOnFailure(false);
				bindingValue = factory.convertFromString(analyzeAsBindingValue.toString(), bindable);
				factory.setWarnOnFailure(true);
			}
			if (bindingValue == null) {
				bindingValue = new BindingValue(null, bindable);
				bindingValue.setUnparsableValue(aFunction.getName());
			}
			if (bindingValue != null) {
				bindingValue.setBindingDefinition(bd);
			}
		}

		/*public BindingValueFunction(Function aFunction)
		{
			super();
			setFunction(aFunction);
			StringBuffer analyzeAsBindingValue = new StringBuffer();
			analyzeAsBindingValue.append(aFunction.getName()+"(");
			for (Expression arg : aFunction.getArgs()) {
				analyzeAsBindingValue.append(arg.toString());
			}
			analyzeAsBindingValue.append(")");
			System.out.println("coucou je decode une fonction avec ca: "+analyzeAsBindingValue.toString());
				if (bindable != null) {
					BindingValueStringConverter converter = ((FlexoModelObject)bindable).getProject().getBindingValueConverter();
					converter.setBindable(bindable);
					bindingValue = converter.convertFromString(aVariable.getName());
				}
		}*/

		@Override
		public int getDepth() {
			return 0;
		}

		public BindingValue getBindingValue() {
			return bindingValue;
		}

		public void setBindingValue(BindingValue aBindingValue) {
			this.bindingValue = aBindingValue;
		}

		public Function getFunction() {
			return function;
		}

		public void setFunction(Function aFunction) {
			this.function = aFunction;
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
			return function.evaluate(context, bindable);
		}*/

		@Override
		public Expression transform(ExpressionTransformer transformer) throws TransformException {
			return transformer.performTransformation(this);
		}

		@Override
		public String toString() {
			return function.toString();
		}

		@Override
		public EvaluationType getEvaluationType() throws TypeMismatchException {
			if (bindingValue != null && bindingValue.isBindingValid() && bindingValue.getAccessedType() != null) {
				return TypeUtils.kindOfType(bindingValue.getAccessedType());
			}
			return function.getEvaluationType();
		}

		@Override
		protected Vector<Expression> getChilds() {
			return null;
		}

	}

	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return expression.getEvaluationType();
	}

	public BindingExpression evaluate() throws TypeMismatchException, NullReferenceException {
		if (expression == null) {
			return clone();
		}
		/*EvaluationContext evaluationContext = new EvaluationContext(getConverter().getConstantFactory(), getConverter()
				.getVariableFactory(), getConverter().getFunctionFactory());*/
		// Expression evaluatedExpression = expression.evaluate(evaluationContext);
		Expression evaluatedExpression = expression.evaluate();
		evaluatedExpression = BindingExpressionFactory.convertToOldBindingModel(evaluatedExpression, getOwner());
		BindingExpression returned = clone();
		returned.setExpression(evaluatedExpression);
		return returned;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression anExpression) {
		this.expression = anExpression;
	}

	@Override
	public Type getAccessedType() {
		try {
			if (getEvaluationType() == EvaluationType.LITERAL) {
				return Object.class;
			}
			if (getEvaluationType() == EvaluationType.ARITHMETIC_INTEGER) {
				return Long.class;
			}
			if (getEvaluationType() == EvaluationType.ARITHMETIC_FLOAT) {
				return Float.class;
			}
			if (getEvaluationType() == EvaluationType.BOOLEAN) {
				return Boolean.class;
			}
			if (getEvaluationType() == EvaluationType.STRING) {
				return String.class;
			}
		} catch (TypeMismatchException e) {
			// Lets return null
		}
		return null;
	}

	@Override
	public boolean isStaticValue() {
		return false;
	}

	@Override
	public boolean isBindingValid() {
		// logger.setLevel(Level.FINE);

		if (expression == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + this + " not valid because expression is null");
			}
			System.out.println("null expression " + getStringRepresentation());
			return false;
		}

		if (getAccessedType() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + this + " not valid because accessed type is null");
			}
			return false;
		}

		if (getBindingDefinition() == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + this + " not valid because binding definition is null");
			}
			return false;
		} else if (getBindingDefinition().getIsSettable()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding definition is declared as settable");
			}
			return false;
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because binding definition is declared as executable");
			}
			return false;
		}

		for (Expression e : expression.getAllAtomicExpressions()) {
			if (e instanceof BindingValueVariable && !((BindingValueVariable) e).isValid()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Binding " + this + " not valid because invalid variable " + e);
				}
				return false;
			}
			if (e instanceof BindingValueFunction && !((BindingValueFunction) e).getBindingValue().isBindingValid()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Binding " + this + " not valid because invalid function " + e);
				}
				return false;
			}
		}

		if (getAccessedType().equals(Object.class)) {
			return true;
		}

		if (getBindingDefinition().getType() == null
				|| TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true)) {
			return true;
		}

		// If valid assignability could not be found, try with type class only (we are not a compiler !!!)
		if (TypeUtils.kindOfType(getBindingDefinition().getType()) == TypeUtils.kindOfType(getAccessedType())
				&& TypeUtils.kindOfType(getBindingDefinition().getType()) != EvaluationType.LITERAL) {
			return true;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Binding " + this + " not valid because types are not matching: searched: " + getBindingDefinition().getType()
					+ " have: " + getAccessedType());
		}
		return false;

	}

	@Override
	public boolean debugIsBindingValid() {
		logger.info("Is BindingExpression " + this + " valid ?");

		if (expression == null) {
			logger.info("Binding " + this + " not valid because expression is null");
			System.out.println("null expression " + getStringRepresentation());
			return false;
		}

		if (getAccessedType() == null) {
			logger.info("Binding " + this + " not valid because accessed type is null");
			return false;
		}

		if (getBindingDefinition() == null) {
			logger.info("Binding " + this + " not valid because binding definition is null");
			return false;
		} else if (getBindingDefinition().getIsSettable()) {
			logger.info("Invalid binding because binding definition is declared as settable");
			return false;
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			logger.info("Invalid binding because binding definition is declared as executable");
			return false;
		}

		for (Expression e : expression.getAllAtomicExpressions()) {
			if (e instanceof BindingValueVariable && !((BindingValueVariable) e).isValid()) {
				logger.info("Binding " + this + " not valid because invalid part " + e);
				((BindingValueVariable) e).getBindingValue().debugIsBindingValid();
				return false;
			}
			if (e instanceof BindingValueFunction && !((BindingValueFunction) e).getBindingValue().isBindingValid()) {
				logger.info("Binding " + this + " not valid because invalid function " + e + " see following for details");
				((BindingValueFunction) e).getBindingValue().debugIsBindingValid();
				return false;
			}
		}

		if (getAccessedType().equals(Object.class)) {
			return true;
		}

		if (getBindingDefinition().getType() == null
				|| TypeUtils.isTypeAssignableFrom(getBindingDefinition().getType(), getAccessedType(), true)) {
			return true;
		}

		// If valid assignability could not be found, try with type class only (we are not a compiler !!!)
		if (TypeUtils.kindOfType(getBindingDefinition().getType()) == TypeUtils.kindOfType(getAccessedType())
				&& TypeUtils.kindOfType(getBindingDefinition().getType()) != EvaluationType.LITERAL) {
			return true;
		}

		logger.info("Binding " + this + " not valid because types are not matching: searched: " + getBindingDefinition().getType()
				+ " have: " + getAccessedType());
		return false;

	}

	@Override
	public String invalidBindingReason() {
		if (expression == null) {
			return "Binding " + this + " not valid because expression is null";
		}

		if (getAccessedType() == null) {
			return "Binding " + this + " not valid because accessed type is null";
		}

		if (getBindingDefinition() == null) {
			return "Binding " + this + " not valid because binding definition is null";
		} else if (getBindingDefinition().getIsSettable()) {
			return "Invalid binding because binding definition is declared as settable for an expression";
		} else if (getBindingDefinition().getBindingDefinitionType() == BindingDefinitionType.EXECUTE) {
			return "Invalid binding because binding definition is declared as executable for an expression";
		}

		for (Expression e : expression.getAllAtomicExpressions()) {
			if (e instanceof BindingValueVariable && !((BindingValueVariable) e).isValid()) {
				return "Binding " + this + " not valid because invalid part: [" + ((BindingValueVariable) e).getBindingValue() + " reason="
						+ ((BindingValueVariable) e).getBindingValue().invalidBindingReason() + "]";
			}
			if (e instanceof BindingValueFunction && !((BindingValueFunction) e).getBindingValue().isBindingValid()) {
				return "Binding " + this + " not valid because invalid function: ["
						+ ((BindingValueFunction) e).getBindingValue().invalidBindingReason() + "]";
			}
		}

		return "Binding " + this + " not valid because types are not matching: searched: " + getBindingDefinition().getType() + " have: "
				+ getAccessedType();

	}

	@Override
	public void setsWith(AbstractBinding aValue) {
		super.setsWith(aValue);
		if (aValue instanceof BindingExpression) {
			expression = ((BindingExpression) aValue).expression;
			unparsableValue = ((BindingExpression) aValue).unparsableValue;
		} else {
			logger.warning("setsWith called with mismatched type " + aValue.getClass().getSimpleName() + ", expected BindingExpression");
		}
	}

	@Override
	public BindingExpression clone() {
		BindingExpression returned = new BindingExpression();
		returned.setsWith(this);
		return returned;
	}

	/**
	 * Evaluates the binding as a GET with supplied binding evaluation context
	 */
	@Override
	public Object getBindingValue(final BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (expression == null) {
			return null;
		}

		// System.out.println("Evaluated " + this);

		try {
			Expression resolvedExpression = expression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValueVariable) {
						BindingValueVariable variable = (BindingValueVariable) e;
						Object evaluatedVariable = variable.getBindingValue().getBindingValue(context);
						return Constant.makeConstant(evaluatedVariable);
					} else if (e instanceof BindingValueFunction) {
						BindingValueFunction function = (BindingValueFunction) e;
						Object evaluatedFunction = function.getBindingValue().getBindingValue(context);
						return Constant.makeConstant(evaluatedFunction);
					} else if (e instanceof BindingValueConstant) {
						BindingValueConstant constant = (BindingValueConstant) e;
						return constant.getConstant();
					}
					return e;
				}
			});

			// System.out.println("Resolved expression=" + resolvedExpression);

			Expression evaluatedExpression = resolvedExpression.evaluate();

			// System.out.println("Evaluated expression=" + evaluatedExpression);

			if (evaluatedExpression instanceof Constant) {
				return ((Constant) evaluatedExpression).getValue();
			}

			/*if (evaluatedExpression instanceof BinaryOperatorExpression
					&& ((((BinaryOperatorExpression) evaluatedExpression).getLeftArgument() == ObjectSymbolicConstant.NULL) 
							|| (((BinaryOperatorExpression) evaluatedExpression).getLeftArgument() == ObjectSymbolicConstant.NULL))) {

			}*/

			logger.warning("Cannot evaluate " + getStringRepresentation() + " max reduction is " + evaluatedExpression
					+ " resolvedExpression=" + resolvedExpression);
			return null;

		} catch (NullReferenceException e1) {
			throw e1;
		} catch (TypeMismatchException e1) {
			throw e1;
		} catch (TransformException e1) {
			logger.warning("Unexpected TransformException while evaluating " + getStringRepresentation() + " " + e1.getMessage());
			e1.printStackTrace();
			return null;
		}

	}

	public static boolean TypeMismatchException_WARNING = false;

	@Override
	public void setBindingValue(Object value, BindingEvaluationContext context) {
		logger.warning("Binding " + getStringRepresentation() + " is not settable");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	/**
	 * Build and return a list of objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<Object> getConcernedObjects(final BindingEvaluationContext context) {
		if (expression == null) {
			return null;
		}

		final ArrayList<Object> returned = new ArrayList<Object>();

		try {
			expression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValueVariable) {
						BindingValueVariable variable = (BindingValueVariable) e;
						Object evaluatedVariable = variable.getBindingValue().getBindingValue(context);
						List<Object> list = variable.getBindingValue().getConcernedObjects(context);
						if (list != null) {
							returned.addAll(list);
						}
					} else if (e instanceof BindingValueFunction) {
						BindingValueFunction function = (BindingValueFunction) e;
						Object evaluatedFunction = function.getBindingValue().getBindingValue(context);
						List<Object> list = function.getBindingValue().getConcernedObjects(context);
						if (list != null) {
							returned.addAll(list);
						}
					}
					return e;
				}
			});
		} catch (TransformException e1) {
			e1.printStackTrace();
			logger.warning("TypeMismatchException while evaluating " + getStringRepresentation() + " " + e1.getMessage());
		}

		/*final ConstantFactory constantFactory = new DefaultConstantFactory();
		final DefaultVariableFactory variableFactory = new DefaultVariableFactory();
		final DefaultFunctionFactory functionFactory = new DefaultFunctionFactory();

		EvaluationContext evaluationContext = new EvaluationContext(constantFactory, new VariableFactory() {
			@Override
			public Expression makeVariable(Word value, Bindable bindable) {
				BindingValueVariable variable = new BindingValueVariable(variableFactory.makeVariable(value, bindable), getOwner());
				Object evaluatedVariable = variable.getBindingValue().getBindingValue(context);
				List<Object> list = variable.getBindingValue().getConcernedObjects(context);
				if (list != null) {
					returned.addAll(list);
				}
				return constantFactory.makeConstant(Value.createConstantValue(evaluatedVariable), bindable);
			}
		}, new FunctionFactory() {
			@Override
			public Expression makeFunction(String functionName, List<Expression> args, Bindable bindable) {
				BindingValueFunction function = new BindingValueFunction(functionFactory.makeFunction(functionName, args, bindable),
						getOwner());
				Object evaluatedFunction = function.getBindingValue().getBindingValue(context);
				List<Object> list = function.getBindingValue().getConcernedObjects(context);
				if (list != null) {
					returned.addAll(list);
				}
				return constantFactory.makeConstant(Value.createConstantValue(evaluatedFunction), bindable);
			}
		});
		Expression evaluatedExpression = null;
		try {
			evaluatedExpression = expression.evaluate(evaluationContext, getOwner());
		} catch (TypeMismatchException e) {
			// e.printStackTrace();
			logger.warning("TypeMismatchException while evaluating " + getStringRepresentation() + " " + e.getMessage());
		}*/

		return returned;

	}

	/**
	 * Build and return a list of target objects (the current object path computed from supplied context)
	 * 
	 * @param context
	 * @return
	 */
	@Override
	public List<TargetObject> getTargetObjects(final BindingEvaluationContext context) {
		if (expression == null) {
			return null;
		}

		final ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		try {
			expression.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValueVariable) {
						BindingValueVariable variable = (BindingValueVariable) e;
						Object evaluatedVariable = variable.getBindingValue().getBindingValue(context);
						List<TargetObject> list = variable.getBindingValue().getTargetObjects(context);
						if (list != null) {
							returned.addAll(list);
						}
					} else if (e instanceof BindingValueFunction) {
						BindingValueFunction function = (BindingValueFunction) e;
						Object evaluatedFunction = function.getBindingValue().getBindingValue(context);
						List<TargetObject> list = function.getBindingValue().getTargetObjects(context);
						if (list != null) {
							returned.addAll(list);
						}
					}
					return e;
				}
			});
		} catch (TransformException e1) {
			e1.printStackTrace();
			logger.warning("TypeMismatchException while evaluating " + getStringRepresentation() + " " + e1.getMessage());
		}

		/*final ConstantFactory constantFactory = new DefaultConstantFactory();
		final DefaultVariableFactory variableFactory = new DefaultVariableFactory();
		final DefaultFunctionFactory functionFactory = new DefaultFunctionFactory();

		EvaluationContext evaluationContext = new EvaluationContext(constantFactory, new VariableFactory() {
			@Override
			public Expression makeVariable(Word value, Bindable bindable) {
				BindingValueVariable variable = new BindingValueVariable(variableFactory.makeVariable(value, bindable), getOwner());
				Object evaluatedVariable = variable.getBindingValue().getBindingValue(context);
				List<TargetObject> list = variable.getBindingValue().getTargetObjects(context);
				if (list != null) {
					returned.addAll(list);
				}
				return constantFactory.makeConstant(Value.createConstantValue(evaluatedVariable), bindable);
			}
		}, new FunctionFactory() {
			@Override
			public Expression makeFunction(String functionName, List<Expression> args, Bindable bindable) {
				BindingValueFunction function = new BindingValueFunction(functionFactory.makeFunction(functionName, args, bindable),
						getOwner());
				Object evaluatedFunction = function.getBindingValue().getBindingValue(context);
				List<TargetObject> list = function.getBindingValue().getTargetObjects(context);
				if (list != null) {
					returned.addAll(list);
				}
				return constantFactory.makeConstant(Value.createConstantValue(evaluatedFunction), bindable);
			}
		});
		Expression evaluatedExpression = null;
		try {
			evaluatedExpression = expression.evaluate(evaluationContext, getOwner());
		} catch (TypeMismatchException e) {
			// e.printStackTrace();
			// SGU: I dont have time to resolve this now, but want a clean console
			// TODO: please reactivate this warning and resolve issues here !!!
			if (!TypeMismatchException_WARNING) {
				logger.warning("TypeMismatchException while evaluating " + getStringRepresentation() + " " + e.getMessage());
				TypeMismatchException_WARNING = true;
			}
		}*/

		return returned;
	}

}
