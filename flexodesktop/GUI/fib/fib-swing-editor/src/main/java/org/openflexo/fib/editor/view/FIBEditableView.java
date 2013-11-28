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
package org.openflexo.fib.editor.view;

import java.util.List;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JComponent;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;

public interface FIBEditableView<M extends FIBComponent, J extends JComponent> extends Observer {

	public abstract FIBEditorController getEditorController();

	public abstract Object getDataObject();

	public abstract M getComponent();

	public abstract void updateDataObject(Object anObject);

	public abstract JComponent getJComponent();

	public abstract J getDynamicJComponent();

	public boolean update(List<FIBComponent> callers);

	public abstract boolean isComponentVisible();

	public abstract boolean hasValue();

	public abstract FIBView getParentView();

	public Vector<PlaceHolder> getPlaceHolders();

	public FIBEditableViewDelegate<M, J> getDelegate();

}