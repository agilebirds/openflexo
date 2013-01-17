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
import java.util.logging.Logger;

/**
 * Represents an expression which could not be resolved for any reason (eg during a NullReferenceException occured during an evaluation)
 * 
 * @author sylvain
 * 
 */
public class UnresolvedExpression extends Expression {

	private static final Logger logger = Logger.getLogger(UnresolvedExpression.class.getPackage().getName());

	public UnresolvedExpression() {
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {
		return this;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return EvaluationType.LITERAL;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

}
