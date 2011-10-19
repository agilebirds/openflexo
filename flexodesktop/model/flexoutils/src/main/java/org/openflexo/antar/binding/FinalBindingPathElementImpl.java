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
public abstract class FinalBindingPathElementImpl<T> extends SimpleBindingPathElementImpl<T> implements FinalBindingPathElement<T> {

	public FinalBindingPathElementImpl(String label, Class<?> declaringClass, Type type, boolean isSettable, String tooltipText) 
	{
		super(label, declaringClass, type, isSettable, tooltipText);
	}

	@Override
	public abstract T getBindingValue(Object target, BindingEvaluationContext context);
	
    @Override
    public abstract void setBindingValue(T value, Object target, BindingEvaluationContext context);

 }
