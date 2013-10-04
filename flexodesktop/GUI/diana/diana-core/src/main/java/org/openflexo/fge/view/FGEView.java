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
import org.openflexo.fge.controller.DrawingControllerImpl;
import org.openflexo.fge.controller.DrawingPalette;

/**
 * Implemented by all views representing a DrawingTreeNode
 * 
 * @author sylvain
 * 
 * @param <O>
 */
public interface FGEView<O> extends Observer, FGEConstants {

	public DrawingControllerImpl<?> getController();

	public DrawingTreeNode<O, ?> getNode();

	public DrawingView<?> getDrawingView();

	public O getDrawable();

	public LabelView<O> getLabelView();

	public double getScale();

	public void registerPalette(DrawingPalette aPalette);

	public FGEPaintManager getPaintManager();

	public void delete();

	public boolean isDeleted();

	public void rescale();
}
