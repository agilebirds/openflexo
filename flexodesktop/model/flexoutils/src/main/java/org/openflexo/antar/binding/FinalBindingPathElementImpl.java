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

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;

/**
 * Default implementation by all path elements flagged as final (no children)
 * 
 * @author sylvain
 *
 */
public abstract class FinalBindingPathElementImpl<E,T> extends SimpleBindingPathElementImpl<E, T> implements FinalBindingPathElement<E,T> {

	public FinalBindingPathElementImpl(String label, Class<E> declaringClass, Type type, boolean isSettable, String tooltipText) 
	{
		super(label, declaringClass, type, isSettable, tooltipText);
	}

	@Override
	public abstract T getBindingValue(E target, BindingEvaluationContext context);
	
    @Override
    public abstract void setBindingValue(T value, E target, BindingEvaluationContext context);

 }
