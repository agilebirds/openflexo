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

import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaToolFactory;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DrawPolygonToolController;
import org.openflexo.fge.swing.control.tools.JDianaInspectors;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaScaleSelector;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;

/**
 * Represent the view factory for Swing technology
 * 
 * @author sylvain
 * 
 */
public class SwingToolFactory implements DianaToolFactory<JComponent> {

	public static SwingToolFactory INSTANCE = new SwingToolFactory();

	private SwingToolFactory() {
	}

	@Override
	public DrawPolygonToolController<?> makeDrawPolygonToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction control) {
		return new JDrawPolygonToolController(controller, control);
	}

	public JDianaToolSelector makeDianaToolSelector() {
		return new JDianaToolSelector();
	}

	public JDianaScaleSelector makeDianaScaleSelector() {
		return new JDianaScaleSelector();
	}

	public JDianaStyles makeDianaDianaStyles() {
		return new JDianaStyles();
	}

	public JDianaInspectors makeDianaInspectors() {
		return new JDianaInspectors();
	}

	public JDianaLayoutWidget makeDianaLayoutWidget() {
		return new JDianaLayoutWidget();
	}

}
