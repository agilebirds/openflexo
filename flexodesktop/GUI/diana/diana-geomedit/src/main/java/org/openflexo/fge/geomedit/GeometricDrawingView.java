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
package org.openflexo.fge.geomedit;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Observable;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.edition.EditionInputMethod;
import org.openflexo.fge.notifications.NodeAdded;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.logging.FlexoLogger;

public class GeometricDrawingView extends JDrawingView<GeometricDrawing> {

	private static final Logger logger = FlexoLogger.getLogger(GeometricDrawingView.class.getPackage().getName());

	private FGEPoint lastMouseLocation;

	public GeometricDrawingView(GeometricDrawing drawing, AbstractDianaEditor<GeometricDrawing> controller) {
		super(drawing, controller);
		lastMouseLocation = new FGEPoint();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point ptInView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getController().getDrawingView());
				lastMouseLocation.x = ptInView.x / getController().getScale();
				lastMouseLocation.y = ptInView.y / getController().getScale();
				getController().getPositionLabel().setText((int) lastMouseLocation.x + " x " + (int) lastMouseLocation.y);
				if (getController().getCurrentEdition() != null
						&& getController().getCurrentEdition().requireRepaint(lastMouseLocation.clone())) {
					getPaintManager().repaint(GeometricDrawingView.this);
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (getController().getCurrentEdition() != null) {
			Graphics2D g2 = (Graphics2D) g;
			graphics.createGraphics(g2, getController());
			getController().getCurrentEdition().paint(graphics, lastMouseLocation);
			graphics.releaseGraphics();
		}
	}

	@Override
	public GeomEditController getController() {
		return (GeomEditController) super.getController();
	}

	private EditionInputMethod inputMethod;

	public void enableEditionInputMethod(EditionInputMethod anInputMethod) {
		if (inputMethod != null) {
			removeMouseListener(inputMethod);
			removeMouseMotionListener(inputMethod);
		}
		inputMethod = anInputMethod;
		removeMouseListener(getMouseListener());
		removeMouseMotionListener(getMouseListener());
		addMouseListener(anInputMethod);
		addMouseMotionListener(anInputMethod);
	}

	public void disableEditionInputMethod() {
		if (inputMethod != null) {
			removeMouseListener(inputMethod);
			removeMouseMotionListener(inputMethod);
			addMouseListener(getMouseListener());
			addMouseMotionListener(getMouseListener());
			inputMethod = null;
		}
	}

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof NodeAdded) {
			getController().notifiedObjectAdded();
		} else if (notification instanceof NodeAdded) {
			getController().notifiedObjectRemoved();
		}
		super.update(o, notification);
	}

}