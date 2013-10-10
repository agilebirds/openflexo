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
package org.openflexo.fge.view;

import java.util.Observer;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.tools.DianaPalette;

/**
 * Implemented by all views representing a DrawingTreeNode<br>
 * The implementation of this interface is technology specific, and is guaranteed to be an instance of (or s subclass of) C
 * 
 * @author sylvain
 * 
 * @param <O>
 *            type of object beeing represented by this view
 * @param <C>
 *            type of component this view is beeing instance (technology-specific)
 */
public interface FGEView<O, C> extends Observer, FGEConstants {

	public AbstractDianaEditor<?, ?, ? super C> getController();

	public DrawingTreeNode<O, ?> getNode();

	public DrawingView<?, ?> getDrawingView();

	public FGEContainerView<?, ?> getParentView();

	public O getDrawable();

	public double getScale();

	public void activatePalette(DianaPalette<?, ?> aPalette);

	public void delete();

	public boolean isDeleted();

	public void rescale();

	public void stopLabelEdition();
}
