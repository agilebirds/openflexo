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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Observer;
import java.util.logging.Level;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingPalette;

public interface FGEView<O> extends Observer, FGEConstants {

	public class BufferedViewHelper {

		private BufferedImage buffer;
		private FGEView<?> view;

		protected BufferedViewHelper(FGEView<?> view) {
			super();
			this.view = view;
		}

		public void invalidateBuffer() {
			buffer = null;
		}

		public void delete() {
			view = null;
			buffer = null;
		}

		public final void paint(Graphics g) {
			if (view.isDeleted()) {
				return;
			}
			if (!view.useBuffer()) {
				// This object is declared to be a temporary object, to be redrawn
				// continuously, so we need to ignore it: do nothing
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					FGEPaintManager.paintPrimitiveLogger.fine("View: buffering paint, ignore: " + view.getGraphicalRepresentation());
				}
				view.doPaint(g);
			} else {
				if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
					FGEPaintManager.paintPrimitiveLogger.fine("View: buffering paint, draw: " + view.getGraphicalRepresentation()
							+ " clip=" + g.getClip());
				}
				g.drawImage(getBuffer(), 0, 0, null);
			}
			view.superPaint(g);
			view.doUnbufferedPaint(g);
		}

		public BufferedImage getBuffer() {
			if (buffer == null) {
				buffer = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
				Graphics g = buffer.createGraphics();
				try {
					view.doPaint(g);
				} finally {
					g.dispose();
				}
			}
			return buffer;
		}
	}

	public int getWidth();

	public void superPaint(Graphics g);

	public int getHeight();

	public O getModel();

	public DrawingController<?> getController();

	public GraphicalRepresentation<O> getGraphicalRepresentation();

	public DrawingView<?> getDrawingView();

	public LabelView<O> getLabelView();

	public double getScale();

	public void registerPalette(DrawingPalette aPalette);

	public FGEPaintManager getPaintManager();

	public void delete();

	public boolean isDeleted();

	public void rescale();

	public BufferedViewHelper getPaintDelegate();

	public boolean useBuffer();

	public void doPaint(Graphics g);

	public void doUnbufferedPaint(Graphics g);
}
