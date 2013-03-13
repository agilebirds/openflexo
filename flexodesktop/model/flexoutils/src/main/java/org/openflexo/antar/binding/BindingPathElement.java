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
import java.util.Comparator;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;

/**
 * This interface is implemented by all classes modelizing an element of a formal binding path, whichever type it is.
 * 
 * @author sylvain
 * 
 */
public interface BindingPathElement<T> extends Typed {

	public static final Comparator<BindingPathElement> COMPARATOR = new Comparator<BindingPathElement>() {

		@Override
		public int compare(BindingPathElement o1, BindingPathElement o2) {
			if (o1.getLabel() == null) {
				if (o2.getLabel() == null) {
					return 0;
				} else {
					return -1;
				}
			} else if (o2.getLabel() == null) {
				return 1;
			}
			return o1.getLabel().compareTo(o2.getLabel());
		}
	};

	public Class<?> getDeclaringClass();

	@Override
	public Type getType();

	// public void addObserver(Observer o);

	// public void deleteObserver(Observer o);

	public String getSerializationRepresentation();

	public boolean isBindingValid();

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
	 */
	public T getBindingValue(Object target, BindingEvaluationContext context);

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
	public void setBindingValue(T value, Object target, BindingEvaluationContext context);

	/*public BindingPathElement getBindingPathElement(String propertyName);

	public List<? extends BindingPathElement> getAccessibleBindingPathElements();

	public List<? extends BindingPathElement> getAccessibleCompoundBindingPathElements();*/

}