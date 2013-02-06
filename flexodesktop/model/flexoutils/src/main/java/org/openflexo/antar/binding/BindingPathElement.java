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

import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

/**
 * This interface is implemented by all classes modelizing an element of a formal binding path, whichever type it is.
 * 
 * @author sylvain
 * 
 */
public interface BindingPathElement extends Typed {
	// public Class<?> getDeclaringClass();

	@Override
	public Type getType();

	// public void addObserver(Observer o);

	// public void deleteObserver(Observer o);

	public String getSerializationRepresentation();

	// public boolean isBindingValid();

	public String getLabel();

	public String getTooltipText(Type resultingType);

	/**
	 * Return a flag indicating if this path element is settable or not (settable indicates that a new value can be set)
	 * 
	 * @return
	 */
	public boolean isSettable();

	/**
	 * Evaluate and return value for related path element, given a binding evaluation context
	 * 
	 * @param target
	 *            : adress object as target of parent path: the object on which setting will be performed
	 * @param context
	 *            : binding evaluation context
	 * @return accessed value
	 * @throws NullReferenceException
	 * @throws TypeMismatchException
	 */
	public Object getBindingValue(Object target, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetTransformException;

	public BindingPathElement getParent();

}