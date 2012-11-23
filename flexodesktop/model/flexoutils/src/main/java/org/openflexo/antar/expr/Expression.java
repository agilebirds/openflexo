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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;

/**
 * Represents a symbolic expression
 * 
 * @author sylvain
 * 
 */
public abstract class Expression {

	private static final Logger logger = Logger.getLogger(Expression.class.getPackage().getName());

	public abstract void visit(ExpressionVisitor visitor) throws VisitorException;

	public abstract Expression transform(ExpressionTransformer transformer) throws TransformException;

	public final Expression evaluate() throws TypeMismatchException, NullReferenceException {
		try {
			return transform(new ExpressionEvaluator());
		} catch (TypeMismatchException e) {
			throw e;
		} catch (NullReferenceException e) {
			throw e;
		} catch (TransformException e) {
			logger.warning("Unexpected exception occured during evaluation " + e);
			e.printStackTrace();
			return null;
		}
	}

	public abstract int getDepth();

	@Override
	public String toString() {
		return debugPP.getStringRepresentation(this);
	}

	private static final DefaultExpressionPrettyPrinter debugPP = new DefaultExpressionPrettyPrinter();

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Expression) {
			return getClass().equals(obj.getClass()) && toString().equalsIgnoreCase(((Expression) obj).toString());
		}
		return super.equals(obj);
	}

	public abstract EvaluationType getEvaluationType() throws TypeMismatchException;

	public boolean isSemanticallyAcceptable() {
		try {
			getEvaluationType();
			return true;
		} catch (TypeMismatchException e) {
			return false;
		}
	}

	protected void _checkSemanticallyAcceptable() throws TypeMismatchException {
		getEvaluationType();
	}

	/**
	 * Returns an iterator on atomic expressions
	 */
	public Iterator<Expression> atomicExpressions() {
		return getAllAtomicExpressions().iterator();
	}

	public Vector<Expression> getAllAtomicExpressions() {
		Vector<Expression> returned = new Vector<Expression>();
		appendAllAtomicExpressions(returned, this);
		return returned;
	}

	private static void appendAllAtomicExpressions(Vector<Expression> buildVector, Expression current) {
		if (current.getChilds() == null) {
			buildVector.add(current);
		} else {
			for (Expression e : current.getChilds()) {
				appendAllAtomicExpressions(buildVector, e);
			}
		}
	}

	protected abstract Vector<Expression> getChilds();

	/**
	 * Evaluate expression considering some declared variables
	 * 
	 * @param variables
	 * @return
	 * @throws TypeMismatchException
	 */
	public Expression evaluate(final Hashtable<String, ?> variables) throws TypeMismatchException {
		try {
			Expression resolvedExpression = transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValue) {
						BindingValue bv = (BindingValue) e;
						if (bv.isSimpleVariable() && variables.get(bv.toString()) != null) {
							return Constant.makeConstant(variables.get(bv.toString()));
						}
					}
					return e;
				}
			});
			return resolvedExpression.evaluate();
		} catch (TransformException e) {
			e.printStackTrace();
			return null;
		}

		/*return evaluate(new EvaluationContext(new ExpressionParser.DefaultConstantFactory(), new VariableFactory() {
			@Override
			public Expression makeVariable(Word value, Bindable bindable) {
				Object valueObject = variables.get(value.getValue());
				if (valueObject == null) {
					return ObjectSymbolicConstant.NULL;
				}
				if (valueObject instanceof String) {
					return new Constant.StringConstant((String) valueObject);
				} else if (valueObject instanceof Enum) {
					return new Constant.EnumConstant(((Enum) valueObject).name());
				} else if (valueObject instanceof Integer) {
					return new Constant.IntegerConstant((Integer) valueObject);
				} else if (valueObject instanceof Long) {
					return new Constant.IntegerConstant((Long) valueObject);
				} else if (valueObject instanceof Short) {
					return new Constant.IntegerConstant((Short) valueObject);
				} else if (valueObject instanceof Float) {
					return new Constant.FloatConstant((Float) valueObject);
				} else if (valueObject instanceof Double) {
					return new Constant.FloatConstant((Double) valueObject);
				} else if (valueObject instanceof Boolean) {
					return (Boolean) valueObject ? Constant.BooleanConstant.TRUE : Constant.BooleanConstant.FALSE;
				}
				// TODO Handle others
				// return new Variable(value.getValue());
				return new Constant.StringConstant(value.getValue());
			}
		}, new ExpressionParser.DefaultFunctionFactory()), bindable);*/

	}

	public boolean evaluateCondition(final Hashtable<String, ?> variables) throws TypeMismatchException, UnresolvedExpressionException {
		// logger.info("evaluate "+this);
		// logger.info("variables "+variables);

		Expression evaluation = evaluate(variables);
		// logger.info("evaluation "+evaluation);
		if (evaluation == Constant.BooleanConstant.TRUE) {
			return true;
		}
		if (evaluation == Constant.BooleanConstant.FALSE) {
			return false;
		}
		logger.warning("Unresolved expression: " + evaluation);
		throw new UnresolvedExpressionException();
	}

	public static List<BindingValue> extractPrimitives(String anExpression) throws ParseException, TypeMismatchException {

		return extractPrimitives(ExpressionParser.parse(anExpression));
	}

	public static List<BindingValue> extractPrimitives(final Expression expression) throws ParseException, TypeMismatchException {

		final List<BindingValue> returned = new ArrayList<BindingValue>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.add((BindingValue) e);
					}
				}
			});
		} catch (VisitorException e) {
			logger.warning("Unexpected " + e);
		}

		return returned;
	}

	@Override
	public int hashCode() {
		return (getClass().getName() + "@[" + toString() + "]").hashCode();
	}
}
