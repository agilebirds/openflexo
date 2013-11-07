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

import java.util.logging.Logger;

import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.graphics.FGEGraphics;

/**
 * Abstract implementation for the controller of the DrawShape tool
 * 
 * @author sylvain
 * 
 * @param <ME>
 */
public abstract class DrawShapeToolController<ME> extends ToolController<ME> {

	private static final Logger logger = Logger.getLogger(DrawShapeToolController.class.getPackage().getName());

	public DrawShapeToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawShapeAction toolAction) {
		super(controller, toolAction);
	}

	public FGEGraphics getGraphics() {
		return null;
	}

	@Override
	public DrawShapeAction getToolAction() {
		return (DrawShapeAction) super.getToolAction();
	}

	protected void startMouseEdition(ME e) {
		super.startMouseEdition(e);
	}

	protected void stopMouseEdition() {
		super.stopMouseEdition();
	}

	public void delete() {
		logger.warning("Please implement deletion for DrawShapeToolController");
		super.delete();
	}

	public boolean mouseClicked(ME e) {
		System.out.println("DrawShapeToolController: mouseClicked() on " + getPoint(e));
		return false;
	}

}
