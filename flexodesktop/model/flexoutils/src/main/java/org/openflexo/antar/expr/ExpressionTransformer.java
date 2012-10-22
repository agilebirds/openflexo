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

/**
 * This interface implements an expression transformer<br>
 * This utility is used when performing transformations on an expression (see {@link Expression#transform(ExpressionTransformer)}.<br>
 * Each node of the expression is recursively transformed using performTranslation(Expression) method.
 * 
 * @author sylvain
 * 
 */
public interface ExpressionTransformer {

	/**
	 * Performs the transformation of a resulting expression e, asserting that all contained expressions have already been transformed (this
	 * method is not recursive, to do so, use Expression.transform(ExpressionTransformer) API)
	 */
	public Expression performTransformation(Expression e) throws TransformException;
}
