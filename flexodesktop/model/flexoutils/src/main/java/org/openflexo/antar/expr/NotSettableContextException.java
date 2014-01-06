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

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SettableBindingEvaluationContext;

/**
 * This exception is thrown when a set is requested on a BindingVariable not in a {@link SettableBindingEvaluationContext}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class NotSettableContextException extends TransformException {

	private String message;

	public NotSettableContextException(BindingVariable bindingVariable, BindingEvaluationContext context) {
		super();
		message = "NotSettableContextException: variable " + bindingVariable + " context=" + context;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
