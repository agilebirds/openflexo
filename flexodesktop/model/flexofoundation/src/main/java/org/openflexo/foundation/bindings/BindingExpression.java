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
package org.openflexo.foundation.bindings;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.DefaultExpressionParser;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.EvaluationContext;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ExpressionParser.ConstantFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.DefaultConstantFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.DefaultFunctionFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.DefaultVariableFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.FunctionFactory;
import org.openflexo.antar.expr.parser.ExpressionParser.VariableFactory;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.antar.expr.parser.Value;
import org.openflexo.antar.expr.parser.Word;
import org.openflexo.antar.java.JavaExpressionPrettyPrinter;
import org.openflexo.antar.pp.ExpressionPrettyPrinter;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.BindingValue.BindingValueStringConverter;
import org.openflexo.foundation.bindings.MethodCall.MethodCallArgument;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dm.DMType;

public class BindingExpression extends AbstractBinding {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(BindingValue.class.getPackage().getName());

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

	static final ExpressionPrettyPrinter javaPrettyPrinter = new JavaExpressionPrettyPrinter() {
		@Override
		public String getStringRepresentation(Expression expression) {
			if (expression instanceof BindingValueFunction) {
				return ((BindingValueFunction) expression).getJavaCodeStringRepresentation();
			} else if (expression instanceof BindingValueVariable) {
				return ((BindingValueVariable) expression).getJavaCodeStringRepresentation();
			} else if (expression instanceof BindingValueConstant) {
				return ((BindingValueConstant) expression).getJavaCodeStringRepresentation();
			} else {
				return super.getStringRepresentation(expression);
			}
		}
	};

	Expression expression;

	public BindingExpression() {
		super();
	}

	public BindingExpression(BindingDefinition bindingDefinition, FlexoModelObject owner) {
		super(bindingDefinition, owner);
	}

	public BindingExpression(BindingDefinition bindingDefinition, FlexoModelObject owner, AbstractBinding abstractBinding) {
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

	public BindingExpression(BindingDefinition bindingDefinition, FlexoModelObject owner, Expression anExpression) {
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

	@Override
	public String getCodeStringRepresentation() {
		return getStringRepresentation();
	}

	@Override
	public String getWodStringRepresentation() {
		logger.severe("expression in wod files isn't supported yet");
		return "false";
	}

	@Override
	public String getClassNameKey() {
		return "binding_expression";
	}

	@Override
	public String getFullyQualifiedName() {
		return "BINDING_EXPRESSION=" + getStringRepresentation();
	}

	@Override
	protected void _applyNewBindingDefinition() {
		// TODO Implement this
	}

	// ==========================================================
	// ================= Serialization stuff ====================
	// ==========================================================

	public static BindingExpression makeBindingExpression(String value, FlexoModelObject owner) {
		if (owner != null && owner.getProject() != null && owner instanceof Bindable) {
			BindingExpressionStringConverter converter = owner.getProject().getBindingExpressionConverter();
			converter.setBindable((Bindable) owner);
			BindingExpression returned = converter.convertFromString(value);
			returned.setOwner(owner);
			return returned;
		}
		return null;
	}

	public BindingExpression getBindingExpressionFromString(String aValue) {
		return getConverter().convertFromString(aValue);
	}

	public static class BindingExpressionStringConverter extends AbstractBindingStringConverter<BindingExpression> {
		private ExpressionParser parser;
		Bindable _bindable;

		public BindingExpressionStringConverter() {
			super(BindingExpression.class);
			parser = new DefaultExpressionParser();
			parser.setConstantFactory(new BindingExpressionConstantFactory());
			parser.setVariableFactory(new BindingExpressionVariableFactory());
			parser.setFunctionFactory(new BindingExpressionFunctionFactory());
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
			} catch (ParseException e) {
				returned.unparsableValue = aValue;
			}
			return returned;
		}

		public Expression parseExpressionFromString(String aValue) throws ParseException {
			return parser.parse(aValue, null);
		}

		@Override
		public String convertToString(BindingExpression value) {
			return value.getStringRepresentation();
		}

		public Bindable getBindable() {
			return _bindable;
		}

		@Override
		public void setBindable(Bindable bindable) {
			_bindable = bindable;
		}

		public class BindingExpressionConstantFactory implements ConstantFactory {
			private DefaultConstantFactory constantFactory = new DefaultConstantFactory();

			@Override
			public Expression makeConstant(Value value, org.openflexo.antar.binding.Bindable bindable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Make constant from " + value + " of " + value.getClass().getSimpleName());
				}
				return new BindingValueConstant(constantFactory.makeConstant(value, bindable), _bindable);
			}
		}

		public class BindingExpressionVariableFactory implements VariableFactory {
			private DefaultVariableFactory variableFactory = new DefaultVariableFactory();

			@Override
			public Expression makeVariable(Word value, org.openflexo.antar.binding.Bindable bindable) {
				if (value.getValue().startsWith(DMType.DKV_PREFIX)) {
					return new BindingValueConstant(new Constant.EnumConstant(value.getValue()), _bindable);
				}
				return new BindingValueVariable(variableFactory.makeVariable(value, bindable), _bindable);
			}
		}

		public class BindingExpressionFunctionFactory implements FunctionFactory {
			private DefaultFunctionFactory functionFactory = new DefaultFunctionFactory();

			@Override
			public Expression makeFunction(String functionName, List<Expression> args, org.openflexo.antar.binding.Bindable bindable) {
				return new BindingValueFunction(functionFactory.makeFunction(functionName, args, bindable), _bindable);
			}
		}

		public ExpressionParser getParser() {
			return parser;
		}

		public ExpressionPrettyPrinter getPrettyPrinter() {
			return prettyPrinter;
		}

	}

	@Override
	public BindingExpressionStringConverter getConverter() {
		if (getProject() != null) {
			return getProject().getBindingExpressionConverter();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO to be implemented
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
			} else if (aStaticBinding instanceof DateStaticBinding) {
				constant = new Constant.DateConstant(((DateStaticBinding) aStaticBinding).getValue());
			} else if (aStaticBinding instanceof DurationStaticBinding) {
				constant = new Constant.DurationConstant(((DurationStaticBinding) aStaticBinding).getValue());
			} else if (aStaticBinding instanceof DKVBinding) {
				Key key = ((DKVBinding) aStaticBinding).getValue();
				if (key != null) {
					constant = new Constant.EnumConstant(DMType.DKV_PREFIX + (key.getDomain() != null ? key.getDomain().getName() : "null")
							+ "." + key.getName());
				} else {
					constant = new Constant.EnumConstant("null");
				}
			}
		}

		public Constant getConstant() {
			return constant;
		}

		public void setConstant(Constant aConstant) {
			this.constant = aConstant;
			BindingDefinition bd = null;
			if (_bindable != null) {
				bd = new BindingDefinition("constant", DMType.makeObjectDMType(((FlexoModelObject) _bindable).getProject()),
						(FlexoModelObject) _bindable, BindingDefinitionType.GET, true);
			}
			if (constant == Constant.BooleanConstant.TRUE) {
				staticBinding = new BooleanStaticBinding(bd, (FlexoModelObject) _bindable, true);
			} else if (constant == Constant.BooleanConstant.FALSE) {
				staticBinding = new BooleanStaticBinding(bd, (FlexoModelObject) _bindable, false);
			} else if (constant instanceof Constant.IntegerConstant) {
				staticBinding = new IntegerStaticBinding(bd, (FlexoModelObject) _bindable, ((Constant.IntegerConstant) constant).getValue());
			} else if (constant instanceof Constant.FloatConstant) {
				staticBinding = new FloatStaticBinding(bd, (FlexoModelObject) _bindable, ((Constant.FloatConstant) constant).getValue());
			} else if (constant instanceof Constant.StringConstant) {
				staticBinding = new StringStaticBinding(bd, (FlexoModelObject) _bindable, ((Constant.StringConstant) constant).getValue());
			} else if (constant instanceof Constant.DateConstant) {
				staticBinding = new DateStaticBinding(bd, (FlexoModelObject) _bindable, ((Constant.DateConstant) constant).getDate());
			} else if (constant instanceof Constant.DurationConstant) {
				staticBinding = new DurationStaticBinding(bd, (FlexoModelObject) _bindable,
						((Constant.DurationConstant) constant).getDuration());
			} else if (constant instanceof Constant.EnumConstant) {
				Key key = null;
				if (_bindable != null && ((Constant.EnumConstant) constant).getName().startsWith(DMType.DKV_PREFIX)) {
					StringTokenizer st = new StringTokenizer(((Constant.EnumConstant) constant).getName().substring(
							DMType.DKV_PREFIX.length()), ".");
					if (st.hasMoreTokens()) {
						String domainName = st.nextToken();
						Domain domain = ((FlexoModelObject) _bindable).getProject().getDKVModel().getDomainNamed(domainName);
						if (domain != null && st.hasMoreTokens()) {
							key = domain.getKeyNamed(st.nextToken());
						}
					}
				}
				staticBinding = new DKVBinding(bd, (FlexoModelObject) _bindable, key);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("staticBinding=" + staticBinding + " bindable=" + staticBinding.getOwner() + " bd="
						+ staticBinding.getBindingDefinition());
			}
		}

		@Override
		public Expression evaluate(EvaluationContext context, org.openflexo.antar.binding.Bindable bindable) throws TypeMismatchException {
			return constant.evaluate(bindable);
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

		public String getJavaCodeStringRepresentation() {
			if (staticBinding != null) {
				return staticBinding.getJavaCodeStringRepresentation();
			} else {
				logger.warning("Java code string representation not implemented for BindingValueConstant. Please DO IT !!!");
				return toString();
			}
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
			this(new Variable(variableName), bindable, bindable != null ? new BindingDefinition("object",
					DMType.makeObjectDMType(((FlexoModelObject) bindable).getProject()), (FlexoModelObject) bindable,
					BindingDefinitionType.GET, true) : null);
		}

		public BindingValueVariable(String variableName, Bindable bindable, BindingDefinition bd) {
			this(new Variable(variableName), bindable, bd);
		}

		public BindingValueVariable(Variable aVariable, Bindable bindable) {
			this(aVariable, bindable, bindable != null ? new BindingDefinition("object",
					DMType.makeObjectDMType(((FlexoModelObject) bindable).getProject()), (FlexoModelObject) bindable,
					BindingDefinitionType.GET, true) : null);
		}

		public BindingValueVariable(Variable aVariable, Bindable bindable, BindingDefinition bd) {
			super();
			setVariable(aVariable);
			if (bindable != null) {
				BindingValueStringConverter converter = ((FlexoModelObject) bindable).getProject().getBindingValueConverter();
				converter.setBindable(bindable);
				converter.setWarnOnFailure(false);
				bindingValue = converter.convertFromString(aVariable.getName());
				converter.setWarnOnFailure(true);
			}
			if (bindingValue == null) {
				bindingValue = new BindingValue(null, (FlexoModelObject) bindable);
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

		@Override
		public Expression evaluate(EvaluationContext context, org.openflexo.antar.binding.Bindable bindable) throws TypeMismatchException {
			return variable.evaluate(bindable);
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
				return DMType.kindOf(bindingValue.getAccessedType());
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

		public String getJavaCodeStringRepresentation() {
			if (bindingValue != null) {
				return bindingValue.getJavaCodeStringRepresentation();
			} else {
				logger.warning("Java code string representation not implemented for BindingValueVariable. Please DO IT !!!");
				return toString();
			}
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
						newExp = new BindingValueVariable("", (Bindable) compoundBindingValue.getOwner());
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
			this(new Function(functionName, args), bindable, bindable != null ? new BindingDefinition("object",
					DMType.makeObjectDMType(((FlexoModelObject) bindable).getProject()), (FlexoModelObject) bindable,
					BindingDefinitionType.GET, true) : null);
		}

		public BindingValueFunction(String functionName, Vector<Expression> args, Bindable bindable, BindingDefinition bd) {
			this(new Function(functionName, args), bindable, bd);
		}

		public BindingValueFunction(Function aFunction, Bindable bindable) {
			this(aFunction, bindable, bindable != null ? new BindingDefinition("object",
					DMType.makeObjectDMType(((FlexoModelObject) bindable).getProject()), (FlexoModelObject) bindable,
					BindingDefinitionType.GET, true) : null);
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
				BindingValueStringConverter converter = ((FlexoModelObject) bindable).getProject().getBindingValueConverter();
				converter.setWarnOnFailure(false);
				converter.setBindable(bindable);
				bindingValue = converter.convertFromString(analyzeAsBindingValue.toString());
				converter.setWarnOnFailure(true);
			}
			if (bindingValue == null) {
				bindingValue = new BindingValue(null, (FlexoModelObject) bindable);
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

		@Override
		public Expression evaluate(EvaluationContext context, org.openflexo.antar.binding.Bindable bindable) throws TypeMismatchException {
			return function.evaluate(bindable);
		}

		@Override
		public String toString() {
			return function.toString();
		}

		@Override
		public EvaluationType getEvaluationType() throws TypeMismatchException {
			if (bindingValue != null && bindingValue.isBindingValid() && bindingValue.getAccessedType() != null) {
				return DMType.kindOf(bindingValue.getAccessedType());
			}
			return function.getEvaluationType();
		}

		@Override
		protected Vector<Expression> getChilds() {
			return null;
		}

		public String getJavaCodeStringRepresentation() {
			if (bindingValue != null) {
				return bindingValue.getJavaCodeStringRepresentation();
			} else {
				logger.warning("Java code string representation not implemented for BindingValueFunction. Please DO IT !!!");
				return toString();
			}
		}

	}

	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return expression.getEvaluationType();
	}

	public BindingExpression evaluate() throws TypeMismatchException {
		if (expression == null) {
			return clone();
		}
		EvaluationContext evaluationContext = new EvaluationContext(getConverter().getConstantFactory(), getConverter()
				.getVariableFactory(), getConverter().getFunctionFactory());
		Expression evaluatedExpression = expression.evaluate(evaluationContext, null);
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
	public DMType getAccessedType() {
		if (getProject() != null) {
			try {
				if (getEvaluationType() == EvaluationType.LITERAL) {
					return DMType.makeObjectDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.ARITHMETIC_INTEGER) {
					return DMType.makeLongDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.ARITHMETIC_FLOAT) {
					return DMType.makeDoubleDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.BOOLEAN) {
					return DMType.makeBooleanDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.DATE) {
					return DMType.makeDateDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.STRING) {
					return DMType.makeStringDMType(getProject());
				}
				if (getEvaluationType() == EvaluationType.DURATION) {
					return DMType.makeDurationDMType(getProject()); // Duration is here seen as a long (millis)
				}
			} catch (TypeMismatchException e) {
				// Lets return null
			}
		}
		return null;
	}

	@Override
	public boolean isStaticValue() {
		return false;
	}

	@Override
	public boolean isBindingValid() {
		if (expression == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + this + " not valid because expression is null");
			}
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

		if (getProject() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null because owner is null for this binding value: " + getStringRepresentation()
						+ " cannot determine if binding is valid");
			}
			return true;
		}

		if (getAccessedType().isObject()) {
			return true;
		}

		if (getBindingDefinition().getType() == null || getBindingDefinition().getType().isAssignableFrom(getAccessedType(), true)) {
			return true;
		}

		// If valid assignability could not be found, try with type class only (we are not a compiler !!!)
		if (DMType.kindOf(getBindingDefinition().getType()) == DMType.kindOf(getAccessedType())
				&& DMType.kindOf(getBindingDefinition().getType()) != EvaluationType.LITERAL) {
			return true;
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Binding " + this + " not valid because types are not matching: searched: " + getBindingDefinition().getType()
					+ " have: " + getAccessedType());
		}
		return false;

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

	@Override
	public String getUnparsableValue() {
		return unparsableValue;
	}

	@Override
	public String getJavaCodeStringRepresentation() {
		if (expression != null) {
			return javaPrettyPrinter.getStringRepresentation(expression);
		} else if (unparsableValue != null) {
			return "/* TODO: <UNPARSABLE:" + unparsableValue + ">*/";
		}
		return "null";
	}

}
