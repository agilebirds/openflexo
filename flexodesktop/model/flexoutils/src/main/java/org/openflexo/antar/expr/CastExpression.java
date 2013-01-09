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

import java.util.Vector;

import org.openflexo.antar.binding.TypeUtils;

public class CastExpression extends Expression {

	private TypeReference castType;
	private Expression argument;

	public CastExpression(TypeReference castType, Expression argument) {
		super();
		this.castType = castType;
		this.argument = argument;
	}

	@Override
	public int getDepth() {
		return argument.getDepth() + 1;
	}

	public TypeReference getCastType() {
		return castType;
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Expression expression = this;
		Expression transformedArgument = argument.transform(transformer);

		if (!transformedArgument.equals(argument)) {
			expression = new CastExpression(castType, transformedArgument);
		}

		return transformer.performTransformation(expression);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		argument.visit(visitor);
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return TypeUtils.kindOfType(castType.getType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<Expression>();
		returned.add(getArgument());
		return returned;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CastExpression) {
			CastExpression e = (CastExpression) obj;
			return castType.equals(e.getCastType()) && getArgument().equals(e.getArgument());
		}
		return super.equals(obj);
	}
}
