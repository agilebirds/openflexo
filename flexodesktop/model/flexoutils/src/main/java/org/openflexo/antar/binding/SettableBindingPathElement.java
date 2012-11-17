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

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;

/**
 * Represents a BindingPathElement which has the ability to be set
 * 
 * @author sylvain
 * 
 */
public interface SettableBindingPathElement extends BindingPathElement {
	/**
	 * Return a flag indicating if this path element is settable or not (settable indicates that a new value can be set)
	 * 
	 * @return
	 */
	public boolean isSettable();

	/**
	 * Sets a new value for related path element, given a binding evaluation context If binding declared as NOT settable, this method will
	 * do nothing.
	 * 
	 * @param value
	 *            : the new value
	 * @param target
	 *            : adress object as target of parent path: the object on which setting will be performed
	 * @param context
	 *            : binding evaluation context
	 */
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context);

}