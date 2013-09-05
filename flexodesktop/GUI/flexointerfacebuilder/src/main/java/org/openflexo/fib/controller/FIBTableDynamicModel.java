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
package org.openflexo.fib.controller;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBTable;

public class FIBTableDynamicModel<T, O> extends FIBComponentDynamicModel<T> {

	static final Logger logger = Logger.getLogger(FIBTableDynamicModel.class.getPackage().getName());

	private O selected;
	private List<O> selection;

	public FIBTableDynamicModel(T data, FIBTable component) {
		super(data, component);
	}

	@Override
	public String toString() {
		return super.toString() + ",selected=" + selected;
	}

	public O getSelected() {
		return selected;
	}

	public void setSelected(O selected) {
		// System.out.println("Set selected for " + this + " with " + selected);
		this.selected = selected;
	}

	public List<O> getSelection() {
		return selection;
	}

	public void setSelection(List<O> selection) {
		this.selection = selection;
	}

}