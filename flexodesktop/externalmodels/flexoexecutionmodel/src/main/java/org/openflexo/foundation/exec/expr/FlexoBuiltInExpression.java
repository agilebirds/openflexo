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
package org.openflexo.foundation.exec.expr;

import java.util.Vector;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.expr.EvaluationContext;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.java.JavaPrettyPrintable;

public abstract class FlexoBuiltInExpression extends Expression implements JavaPrettyPrintable {

	// We won't try to resolve those expression here
	@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
		return this;
	}

	@Override
	public abstract String toString();

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

}
