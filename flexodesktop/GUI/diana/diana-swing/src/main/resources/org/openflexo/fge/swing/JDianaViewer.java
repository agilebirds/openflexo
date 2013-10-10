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

package org.openflexo.fge.swing;

import javax.swing.JComponent;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DianaViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.swing.view.JDrawingView;

/**
 * This is the SWING implementation of a basic read-only viewer of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can only be viewed, without any editing possibility (shapes are all non-movable)
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public class JDianaViewer<M> extends DianaViewer<M, SwingFactory, JComponent> {

	public JDianaViewer(Drawing<M> aDrawing, FGEModelFactory factory) {
		super(aDrawing, factory, SwingFactory.INSTANCE, SwingToolFactory.INSTANCE);
		setDelegate(new SwingEditorDelegate(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public JDrawingView<M> getDrawingView() {
		return (JDrawingView<M>) super.getDrawingView();
	}

	@Override
	public SwingEditorDelegate getDelegate() {
		return (SwingEditorDelegate) super.getDelegate();
	}

	/*public FGEPaintManager getPaintManager() {
		if (getDrawingView() != null) {
			return getDrawingView().getPaintManager();
		}
		return null;
	}

	public void enablePaintingCache() {
		getPaintManager().enablePaintingCache();
	}

	public void disablePaintingCache() {
		getPaintManager().disablePaintingCache();
	}*/

}
