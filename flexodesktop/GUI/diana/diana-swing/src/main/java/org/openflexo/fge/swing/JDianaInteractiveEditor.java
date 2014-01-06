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
import org.openflexo.fge.control.DianaInteractiveEditor;
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
public class JDianaInteractiveEditor<M> extends DianaInteractiveEditor<M, SwingViewFactory, JComponent> {

	public JDianaInteractiveEditor(Drawing<M> aDrawing, FGEModelFactory factory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, SwingViewFactory.INSTANCE, toolFactory);
		setDelegate(new SwingEditorDelegate(this));
	}

	public JDianaInteractiveEditor(Drawing<M> aDrawing, FGEModelFactory factory, SwingViewFactory viewFactory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, viewFactory, toolFactory);
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

	@Override
	public void setCurrentTool(org.openflexo.fge.control.DianaInteractiveEditor.EditorTool aTool) {
		super.setCurrentTool(aTool);
		if (getDrawingView() != null && getDrawingView().getPaintManager() != null) {
			getDrawingView().getPaintManager().repaint(getDrawingView());
		}
	}
}
