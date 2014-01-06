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

/**
 * Implemented by all classes defining a domain in which bindings take values
 * 
 * @author sguerin
 * 
 */
public interface Bindable {
	/**
	 * Return the Binding model for the current object.
	 * 
	 * @return
	 */
	public BindingModel getBindingModel();

	/**
	 * Return the factory used to build bindings related to this bindable
	 * 
	 * @return
	 */
	public BindingFactory getBindingFactory();

	/**
	 * Called when supplied data binding changed its value
	 * 
	 * @param dataBinding
	 */
	public void notifiedBindingChanged(DataBinding<?> dataBinding);

	/**
	 * Called when supplied data binding has been decoded (syntaxic and semantics analysis performed)
	 * 
	 * @param dataBinding
	 */
	public void notifiedBindingDecoded(DataBinding<?> dataBinding);
}
