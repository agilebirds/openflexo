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
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.CastExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.ExpressionVisitor;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.VisitorException;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Representation of a data binding.<br>
 * A data binding is defined as a symbolic expression defined on a model abstraction (the binding model), which can be evaluated given a
 * {@link BindingEvaluationContext}.
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class DataBinding<T> extends Observable implements StringConvertable<DataBinding> {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	/**
	 * Defines the access type of a binding, which is generally related to the purpose of the binding
	 * <ul>
	 * <li>GET: a binding used to retrieve a data</li>
	 * <li>SET: a binding used to set a data</li>
	 * <li>GET_SET: a binding used to retrieve and set a data</li>
	 * <li>EXECUTE: a binding used to execute some code</li>
	 * </ul>
	 */
	public static enum BindingDefinitionType {
		GET, /* GET: a binding used to retrieve a data */
		SET, /* SET: a binding used to set a data */
		GET_SET, /* GET_SET: a binding used to retrieve and set a data */
		EXECUTE /* */
	}

	public static DataBinding.DataBindingConverter CONVERTER = new DataBindingConverter();

	public static class DataBindingConverter extends Converter<DataBinding> {
		public DataBindingConverter() {
			super(DataBinding.class);
		}

		@Override
		public DataBinding<?> convertFromString(String value) {
			return new DataBinding(value);
		}

		@Override
		public String convertToString(DataBinding value) {
			return value.toString();
		};
	}

	@Override
	public Converter<? extends DataBinding> getConverter() {
		return CONVERTER;
	}

	private Bindable owner;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private Expression expression;

	private Type declaredType = null;
	private DataBinding.BindingDefinitionType bdType = null;
	private boolean mandatory = false;

	private boolean needsParsing = false;
	private String bindingName;

	public DataBinding(Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		super();
		setOwner(owner);
		this.declaredType = declaredType;
		this.bdType = bdType;
		// setBindingDefinition(new BindingDefinition("unnamed", declaredType, bdType, true));
	}

	public DataBinding(String unparsed, Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this(owner, declaredType, bdType);
		setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed) {
		super();
		setUnparsedBinding(unparsed);
	}

	@Override
	public String toString() {
		if (expression != null) {
			/*if (StringUtils.isEmpty(expression.toString())) {
				System.out.println("Pourquoi ya rien ?");
				System.out.println("l'expression est une " + expression.getClass());
				if (expression instanceof BindingValue) {
					BindingValue bv = (BindingValue) expression;
					bv.debug();
				}
			}*/
			return expression.toString();
		}
		if (StringUtils.isEmpty(unparsedBinding)) {
			return "";
		}
		return unparsedBinding;
	}

	@Deprecated
	public BindingDefinition getBindingDefinition() {
		if (bindingDefinition == null) {
			bindingDefinition = new BindingDefinition("unamed", declaredType, bdType, false);
		}
		return bindingDefinition;
	}

	@Deprecated
	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		this.bindingDefinition = bindingDefinition;
		declaredType = bindingDefinition.getType();
		bdType = bindingDefinition.getBindingDefinitionType();
	}

	public void decode() {
		if (needsParsing) {
			parseExpression();
		}
	}

	public Expression getExpression() {
		decode();
		return expression;
	}

	/*public void setBinding(AbstractBinding binding) 
	{
		this.binding = binding;
	}*/

	public void setExpression(Expression value) {
		// logger.info("setExpression() with " + value);
		needsParsing = false;
		Expression oldValue = this.expression;
		if (oldValue == null) {
			if (value == null) {
				return; // No change
			} else {
				this.expression = value;
				unparsedBinding = value != null ? value.toString() : null;
				// analyseExpressionAfterParsing();
				notifyBindingChanged(oldValue, value);
				return;
			}
		} else {
			if (oldValue.equals(value)) {
				return; // No change
			} else {
				this.expression = value;
				unparsedBinding = value != null ? expression.toString() : null;
				logger.info("Binding takes now value " + value);
				// analyseExpressionAfterParsing();
				notifyBindingChanged(oldValue, value);
				return;
			}
		}
	}

	public boolean isSettable() {
		return isBindingValue() && ((BindingValue) getExpression()).isSettable();
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getBindingName() {
		return bindingName;
	}

	public void setBindingName(String bindingName) {
		this.bindingName = bindingName;
	}

	public Type getDeclaredType() {
		return declaredType;
	}

	public void setDeclaredType(Type aDeclaredType) {
		declaredType = aDeclaredType;
		if (bindingDefinition != null) {
			bindingDefinition.setType(aDeclaredType);
		}
	}

	public DataBinding.BindingDefinitionType getBindingDefinitionType() {
		return bdType;
	}

	public void setBindingDefinitionType(DataBinding.BindingDefinitionType aBDType) {
		bdType = aBDType;
		if (bindingDefinition != null) {
			bindingDefinition.setBindingDefinitionType(aBDType);
		}
	}

	public Type getAnalyzedType() {
		if (getExpression() != null) {
			if (getExpression() instanceof BindingValue) {
				return ((BindingValue) getExpression()).getAccessedTypeNoValidityCheck();
			} else if (getExpression() instanceof CastExpression) {
				return ((CastExpression) getExpression()).getCastType().getType();
			} else {
				try {
					/*System.out.println("****** expression=" + getExpression());
					System.out.println("****** eval type=" + getExpression().getEvaluationType());
					if (getExpression() instanceof BinaryOperatorExpression) {
						BinaryOperatorExpression bope = (BinaryOperatorExpression) getExpression();
						System.out.println("**** left=" + bope.getLeftArgument() + " of " + bope.getLeftArgument().getEvaluationType());
						System.out.println("**** right=" + bope.getRightArgument() + " of " + bope.getRightArgument().getEvaluationType());
						BindingValueAsExpression left = (BindingValueAsExpression) bope.getLeftArgument();
						BindingValueAsExpression right = (BindingValueAsExpression) bope.getRightArgument();
						// left.isValid(this);
						// right.isValid(this);
						System.out.println("**** a gauche, " + left.getAccessedType());
						System.out.println("**** a droite, " + right.getAccessedType());
					}*/

					return getExpression().getEvaluationType().getType();
				} catch (TypeMismatchException e) {
					return Object.class;
				}
			}
		}
		return Object.class;
	}

	public static class InvalidBindingValue extends VisitorException {
		private BindingValue bindingValue;

		public InvalidBindingValue(BindingValue e) {
			bindingValue = e;
		}

		public BindingValue getBindingValue() {
			return bindingValue;
		}
	}

	private String invalidBindingReason;

	public boolean isValid() {

		invalidBindingReason = "unknown";

		if (getOwner() == null) {
			invalidBindingReason = "null owner";
			return false;
		}

		if (getOwner().getBindingModel() == null) {
			invalidBindingReason = "owner has null BindingModel";
			return false;
		}

		if (getExpression() == null) {
			invalidBindingReason = "null expression";
			return false;
		}

		if (getOwner() != null) {
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							if (!((BindingValue) e).isValid(DataBinding.this)) {
								throw new InvalidBindingValue((BindingValue) e);
							}
						}
					}
				});
			} catch (InvalidBindingValue e) {
				invalidBindingReason = "Invalid binding value: " + e.getBindingValue() + " reason: "
						+ e.getBindingValue().invalidBindingReason();
				return false;
			} catch (VisitorException e) {
				invalidBindingReason = "Unexpected visitor exception: " + e.getMessage();
				logger.warning("TransformException while transforming " + expression);
				return false;
			}
		}

		if (getAnalyzedType() == null) {
			invalidBindingReason = "Invalid binding because accessed type is null";
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because accessed type is null");
			}
			return false;
		}

		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.SET
				|| getBindingDefinitionType() == DataBinding.BindingDefinitionType.GET_SET) {
			// Only BindingValue may be settable
			if (!(getExpression() instanceof BindingValue) || !((BindingValue) getExpression()).isSettable()) {
				invalidBindingReason = "Invalid binding because binding definition declared as settable and definition cannot satisfy it";
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
				}
				return false;
			}
		}

		// NO need to check target type for EXECUTE bindings (we don't need return type nor value)
		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE) {
			return true;
		}

		if (getDeclaredType() != null && TypeUtils.isTypeAssignableFrom(getDeclaredType(), getAnalyzedType(), true)) {
			// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
			// System.out.println("getAccessedType()="+getAccessedType());
			invalidBindingReason = "valid binding";
			return true;
		}

		invalidBindingReason = "Invalid binding because types are not matching searched " + getDeclaredType() + " having "
				+ getAnalyzedType();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Invalid binding because types are not matching searched " + getDeclaredType() + " having " + getAnalyzedType());
		}
		return false;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
	}

	public boolean isSet() {
		return unparsedBinding != null || getExpression() != null;
	}

	public boolean isUnset() {
		return unparsedBinding == null && getExpression() == null;
	}

	public void reset() {
		unparsedBinding = null;
		expression = null;
	}

	public boolean isExpression() {
		return getExpression() != null && !(getExpression() instanceof Constant) && !(getExpression() instanceof BindingValue);
	}

	public boolean isBindingValue() {
		return getExpression() != null && getExpression() instanceof BindingValue;
	}

	public boolean isConstant() {
		return getExpression() != null && getExpression() instanceof Constant;
	}

	public boolean isCompoundBinding() {
		return isBindingValue() && ((BindingValue) getExpression()).isCompoundBinding();
	}

	public String getUnparsedBinding() {
		return unparsedBinding;
	}

	public void setUnparsedBinding(String unparsedBinding) {
		if (StringUtils.isEmpty(unparsedBinding)) {
			this.unparsedBinding = null;
			expression = null;
			needsParsing = false;
		} else {
			this.unparsedBinding = unparsedBinding;
			expression = null;
			needsParsing = true;
		}
	}

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		this.owner = owner;
	}

	/**
	 * This method is called whenever we need to parse the binding using string encoded in unparsedBinding field.<br>
	 * Syntaxic checking of the binding is performed here. This phase is followed by the semantics analysis as performed by
	 * {@link #analyseExpressionAfterParsing()} method
	 * 
	 * @return
	 */
	private Expression parseExpression() {
		if (getUnparsedBinding() == null) {
			return expression = null;
		}

		if (getOwner() != null) {
			try {
				expression = ExpressionParser.parse(getUnparsedBinding());
			} catch (ParseException e1) {
				// parse error
				expression = null;
				logger.warning(e1.getMessage());
				return null;
			}
			needsParsing = false;
			analyseExpressionAfterParsing();
		}
		needsParsing = false;

		if (!isValid()) {
			logger.warning("Invalid binding " + getUnparsedBinding() + " reason: " + invalidBindingReason());
		}

		return expression;
	}

	private Expression analyseExpressionAfterParsing() {
		if (getOwner() != null) {
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) {
						if (e instanceof BindingValue) {
							((BindingValue) e).buildBindingPathFromParsedBindingPath(DataBinding.this);
						}
					}
				});
			} catch (VisitorException e) {
				logger.warning("Unexpected " + e);
			}
		}

		notifyBindingDecoded();
		return expression;
	}

	private void notifyBindingChanged(Expression oldValue, Expression newValue) {
		getOwner().notifiedBindingChanged(this);
		// logger.info("notifyBindingChanged from " + oldValue + " to " + newValue + " of " + newValue.getClass());
	}

	private void notifyBindingDecoded() {
		getOwner().notifiedBindingDecoded(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataBinding) {
			if (toString() == null) {
				return false;
			}
			return toString().equals(obj.toString());
		} else {
			return super.equals(obj);
		}
	}

	public T getBindingValue(final BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {

		// System.out.println("Evaluating " + this + " in context " + context);
		if (isValid()) {
			try {
				Expression resolvedExpression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValue) {
							((BindingValue) e).setDataBinding(DataBinding.this);
							Object o = ((BindingValue) e).getBindingValue(context);
							return Constant.makeConstant(o);
						}
						return e;
					}
				});
				Expression evaluatedExpression = resolvedExpression.evaluate();

				if (evaluatedExpression instanceof CastExpression) {
					if (((CastExpression) evaluatedExpression).getArgument() instanceof Constant) {
						return (T) ((Constant) ((CastExpression) evaluatedExpression).getArgument()).getValue();
					}
				}

				if (evaluatedExpression instanceof Constant) {
					return (T) ((Constant) evaluatedExpression).getValue();
				}

				logger.warning("Cannot evaluate " + expression + " max reduction is " + evaluatedExpression + " resolvedExpression="
						+ resolvedExpression);
				return null;

			} catch (NullReferenceException e1) {
				throw e1;
			} catch (TypeMismatchException e1) {
				throw e1;
			} catch (TransformException e1) {
				logger.warning("Unexpected TransformException while evaluating " + expression + " " + e1.getMessage());
				e1.printStackTrace();
				return null;
			} catch (InvalidObjectSpecificationException e1) {
				System.out.println("j'ai mon probleme, binding: " + this);
				System.out.println("valid=" + isValid());
				for (BindingValue bv : getExpression().getAllBindingValues()) {
					System.out.println("bv=" + bv + " valid=" + bv.isValid() + " reason " + bv.invalidBindingReason());
				}
				System.out.println("On s'arrete");
			}
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		if (isSettable()) {
			if (isBindingValue()) {
				// At this time, only BindingValue is settable
				((BindingValue) getExpression()).setBindingValue(value, context);
			}
		}
	}

	public void execute(final BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException {
		getBindingValue(context);
	}

	/**
	 * Build and return a list of objects involved in the computation of this data binding with supplied binding evaluation context
	 * 
	 * @param context
	 * @return
	 */
	public List<Object> getConcernedObjects(final BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}
		if (!isSettable()) {
			return Collections.emptyList();
		}

		final List<Object> returned = new ArrayList<Object>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.addAll(((BindingValue) e).getConcernedObjects(context));
					}
				}
			});
		} catch (VisitorException e) {
			logger.warning("Unexpected " + e);
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects involved in the computation of this data binding with supplied binding evaluation context<br>
	 * Those target objects are the combination of an object and the property name involved by this denoted data binding
	 * 
	 * @param context
	 * @return
	 */
	public List<TargetObject> getTargetObjects(final BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}

		final ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.addAll(((BindingValue) e).getTargetObjects(context));
					}
				}
			});
		} catch (VisitorException e) {
			logger.warning("Unexpected " + e);
		}

		return returned;
	}

	@Override
	public DataBinding<T> clone() {
		DataBinding<T> returned = new DataBinding(getOwner(), getDeclaredType(), getBindingDefinitionType());
		/*if (!isSet()) {
			System.out.println("On essaie de me cloner alors que je suis null");
			Thread.dumpStack();
			System.exit(-1);
		}*/
		if (isSet()) {
			returned.setUnparsedBinding(toString());
		}
		returned.decode();
		return returned;
	}

	public static DataBinding<Boolean> makeTrueBinding() {
		return new DataBinding<Boolean>("true");
	}

	public static DataBinding<Boolean> makeFalseBinding() {
		return new DataBinding<Boolean>("false");
	}
}
