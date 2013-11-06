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

package org.openflexo.fge.swing.control;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaToolFactory;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.fge.swing.control.tools.JDianaInspectors;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;
import org.openflexo.fge.swing.control.tools.JDrawClosedCurveToolController;
import org.openflexo.fge.swing.control.tools.JDrawConnectorToolController;
import org.openflexo.fge.swing.control.tools.JDrawPolygonToolController;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingToolFactory implements DianaToolFactory<JComponent> {

	public static SwingToolFactory DEFAULT = new SwingToolFactory(null);

	private JFrame frame;

	public SwingToolFactory(JFrame frame) {
		this.frame = frame;
	}

	public JDianaToolSelector makeDianaToolSelector(AbstractDianaEditor<?, ?, ?> editor) {
		return new JDianaToolSelector((JDianaInteractiveEditor<?>) editor);
	}

	public JDianaScaleSelector makeDianaScaleSelector(AbstractDianaEditor<?, ?, ?> editor) {
		return new JDianaScaleSelector((AbstractDianaEditor<?, SwingViewFactory, ?>) editor);
	}

	public JDianaStyles makeDianaStyles() {
		return new JDianaStyles();
	}

	public JDianaInspectors makeDianaInspectors() {
		return new JDianaInspectors(frame);
	}

	public JDianaLayoutWidget makeDianaLayoutWidget() {
		return new JDianaLayoutWidget();
	}

	@Override
	public JDianaPalette makeDianaPalette(DrawingPalette palette) {
		return new JDianaPalette(palette);
	}

	@Override
	public JDrawPolygonToolController makeDrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		return new JDrawPolygonToolController(controller, control);
	}

	@Override
	public JDrawClosedCurveToolController makeDrawClosedCurveToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawShapeAction control, boolean isClosedCurve) {
		return new JDrawClosedCurveToolController(controller, control, isClosedCurve);
	}

	@Override
	public JDrawConnectorToolController makeDrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller,
			DrawConnectorAction control) {
		return new JDrawConnectorToolController(controller, control);
	}
}
