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

package org.openflexo.fge.control.tools;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represent a tool associated with a {@link DianaInteractiveViewer}
 * 
 * @author sylvain
 * 
 * @param <C>
 *            base minimal class of components provided by this tool (eg JComponent for SWING)
 * @param <ME>
 *            type of mouse events
 */
public interface DianaTool<C, F extends DianaViewFactory<F, ?>> {

	/**
	 * Return the editor this tool is associated to
	 * 
	 * @return
	 */
	public AbstractDianaEditor<?, F, ?> getEditor();

	/**
	 * Sets the editor this tool is associated to
	 * 
	 * @param editor
	 */
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor);

	public abstract F getDianaFactory();

}
