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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;

public abstract class Expression {

	private static final Logger logger = Logger.getLogger(Expression.class.getPackage().getName());

	public abstract Expression transform(ExpressionTransformer transformer) throws TransformException;

	public final Expression evaluate() throws TypeMismatchException {
		try {
			return transform(new ExpressionEvaluator());
		} catch (TypeMismatchException e) {
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
					if (e instanceof BindingValueAsExpression) {
						BindingValueAsExpression bv = (BindingValueAsExpression) e;
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

	public static Vector<Variable> extractVariables(String anExpression) throws ParseException, TypeMismatchException {
		final Hashtable<String, Variable> returnedHash = new Hashtable<String, Variable>();
		try {
			Expression e = ExpressionParser.parse(anExpression);
			e.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValueAsExpression) {
						String variableName = ((BindingValueAsExpression) e).toString();
						Variable returned = returnedHash.get(variableName);
						if (returned == null) {
							returned = new Variable(variableName);
							returnedHash.put(variableName, returned);
						}
						return returned;
					}
					return e;
				}
			});
		} catch (ParseException e1) {
			throw e1;
		} catch (TransformException e) {
			e.printStackTrace();
		}

		/*DefaultExpressionParser parser = new DefaultExpressionParser();
		Expression expression = parser.parse(anExpression, bindable);
		expression.evaluate(new EvaluationContext(new ExpressionParser.DefaultConstantFactory(), new VariableFactory() {
			@Override
			public Expression makeVariable(Word value, Bindable bindable) {
				Variable returned = returnedHash.get(value.getValue());
				if (returned == null) {
					returned = new Variable(value.getValue());
					returnedHash.put(value.getValue(), returned);
				}
				return returned;
			}
		}, new ExpressionParser.DefaultFunctionFactory()), bindable);*/

		Vector<Variable> returned = new Vector<Variable>();
		for (String v : returnedHash.keySet()) {
			returned.add(returnedHash.get(v));
		}
		return returned;
	}

	public static Vector<Expression> extractPrimitives(String anExpression) throws ParseException, TypeMismatchException {
		final Hashtable<String, Expression> returnedHash = new Hashtable<String, Expression>();

		try {
			Expression e = ExpressionParser.parse(anExpression);
			e.transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValueAsExpression) {
						String variableName = ((BindingValueAsExpression) e).toString();
						Expression returned = returnedHash.get(variableName);
						if (returned == null) {
							returned = new Variable(variableName);
							returnedHash.put(variableName, returned);
						}
						return returned;
					}
					return e;
				}
			});
		} catch (org.openflexo.antar.expr.parser.ParseException e1) {
			e1.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}

		/*DefaultExpressionParser parser = new DefaultExpressionParser();
		Expression expression = parser.parse(anExpression, bindable);
		expression.evaluate(new EvaluationContext(new ExpressionParser.DefaultConstantFactory(), new VariableFactory() {
			@Override
			public Expression makeVariable(Word value, Bindable bindable) {
				Expression returned = returnedHash.get(value.getValue());
				if (returned == null) {
					returned = new Variable(value.getValue());
					returnedHash.put(value.getValue(), returned);
				}
				return returned;
			}
		}, new FunctionFactory() {
			@Override
			public Expression makeFunction(String functionName, List<Expression> args, Bindable bindable) {
				StringBuffer key = new StringBuffer();
				key.append(functionName + "(");
				for (int i = 0; i < args.size(); i++) {
					key.append((i > 0 ? "," : "") + "arg" + i);
				}
				key.append(")");
				Expression returned = returnedHash.get(key);
				if (returned == null) {
					returned = new Function(functionName, args);
					returnedHash.put(key.toString(), returned);
				}
				return returned;
			}
		}), bindable);
		*/

		Vector<Expression> returned = new Vector<Expression>();
		for (String v : returnedHash.keySet()) {
			returned.add(returnedHash.get(v));
		}
		return returned;
	}

	@Override
	public int hashCode() {
		return (getClass().getName() + "@[" + toString() + "]").hashCode();
	}
}
