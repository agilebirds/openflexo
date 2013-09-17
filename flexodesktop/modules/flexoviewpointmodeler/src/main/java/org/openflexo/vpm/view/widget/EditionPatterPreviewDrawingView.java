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
package org.openflexo.vpm.view.widget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.openflexo.fge.view.DrawingView;

public class EditionPatterPreviewDrawingView extends DrawingView<EditionPatternPreviewRepresentation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EditionPatterPreviewDrawingView.class.getPackage().getName());

	public EditionPatterPreviewDrawingView(EditionPatternPreviewRepresentation aDrawing, EditionPatternPreviewController controller) {
		super(aDrawing, controller);
	}

	@Override
	public EditionPatternPreviewController getController() {
		return (EditionPatternPreviewController) super.getController();
	}

	@Override
	public Dimension getPreferredSize() {
		Rectangle rect = new Rectangle(0, 0, 300, 200);
		for (Component c : getComponents()) {
			rect = rect.union(c.getBounds());
		}
		return rect.getSize();
	}
}
