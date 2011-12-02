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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;

public class FGEPaintManager {

	private static final boolean ENABLE_CACHE_BY_DEFAULT = true;

	private static final Logger logger = Logger.getLogger(FGEPaintManager.class.getPackage().getName());

	protected static final Logger paintPrimitiveLogger = Logger.getLogger("PaintPrimitive");
	protected static final Logger paintRequestLogger = Logger.getLogger("PaintRequest");
	protected static final Logger paintStatsLogger = Logger.getLogger("PaintStats");

	private boolean _paintingCacheEnabled;

	private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

	private static FGERepaintManager repaintManager;

	static {
		initFGERepaintManager();
		/* Debug purposes
		paintPrimitiveLogger.setLevel(Level.FINE);
		paintRequestLogger.setLevel(Level.FINE);
		paintStatsLogger.setLevel(Level.FINE);
		*/
	}

	private DrawingView<?> _drawingView;

	private BufferedImage _paintBuffer;
	private HashSet<GraphicalRepresentation<?>> _temporaryObjects;

	public FGEPaintManager(DrawingView<?> drawingView) {
		super();
		_drawingView = drawingView;
		_paintBuffer = null;
		_temporaryObjects = new HashSet<GraphicalRepresentation<?>>();
		if (ENABLE_CACHE_BY_DEFAULT) {
			enablePaintingCache();
		} else {
			disablePaintingCache();
		}
	}

	public DrawingView<?> getDrawingView() {
		return _drawingView;
	}

	public DrawingController<?> getDrawingController() {
		return _drawingView.getController();
	}

	public boolean isPaintingCacheEnabled() {
		return _paintingCacheEnabled;
	}

	public void enablePaintingCache() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Painting cache: ENABLED");
		}
		_paintingCacheEnabled = true;
	}

	public void disablePaintingCache() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Painting cache: DISABLED");
		}
		_paintingCacheEnabled = false;
	}

	public HashSet<GraphicalRepresentation<?>> getTemporaryObjects() {
		return _temporaryObjects;
	}

	public void resetTemporaryObjects() {
		_temporaryObjects.clear();
	}

	public boolean containsTemporaryObject(GraphicalRepresentation<?> gr) {
		if (gr == null) {
			return false;
		}
		if (isTemporaryObject(gr)) {
			return true;
		}
		if (gr.getContainedGraphicalRepresentations() == null) {
			return false;
		}
		for (GraphicalRepresentation<?> child : gr.getContainedGraphicalRepresentations()) {
			if (containsTemporaryObject(child)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTemporaryObject(GraphicalRepresentation<?> gr) {
		return _temporaryObjects.contains(gr);
	}

	public boolean isTemporaryObjectOrParentIsTemporaryObject(GraphicalRepresentation<?> gr) {
		if (isTemporaryObject(gr)) {
			return true;
		}
		if (gr.getContainerGraphicalRepresentation() != null) {
			return isTemporaryObjectOrParentIsTemporaryObject(gr.getContainerGraphicalRepresentation());
		}
		return false;
	}

	public void addToTemporaryObjects(GraphicalRepresentation<?> gr) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("addToTemporaryObjects() " + gr);
		}
		_temporaryObjects.add(gr);
	}

	public void removeFromTemporaryObjects(GraphicalRepresentation<?> gr) {
		_temporaryObjects.remove(gr);
	}

	// CPU-expensive because it will ask to recreate the whole buffer
	public void invalidate(GraphicalRepresentation<?> object) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("CALLED invalidate on FGEPaintManager");
		}
		_paintBuffer = null;
		// repaintManager.clearTemporaryRepaintArea();
	}

	public void clearPaintBuffer() {
		if (paintRequestLogger.isLoggable(Level.INFO)) {
			paintRequestLogger.info("CALLED clear paint buffer on FGEPaintManager");
		}
		_paintBuffer = null;
	}

	public void repaint(FGEView view, Rectangle bounds) {
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view + " for " + bounds);
		}
		((JComponent) view).repaint(bounds.x, bounds.y, bounds.width, bounds.height);
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);
		repaintManager.repaintTemporaryRepaintAreas(_drawingView);
	}

	public void addTemporaryRepaintArea(Rectangle r, JComponent view) {
		repaintManager.addTemporaryRepaintArea(r, view);
	}

	public void repaint(final FGEView view) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaint(view);
				}
			});
			return;
		}
		if (!_drawingView.contains(view)) {
			return;
		}

		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for view " + view);
		}
		if (view == _drawingView) {
			// clearTemporaryRepaintArea();
			// paintRequestLogger.warning("Called repaint on whole DrawingView. Is it really necessary ?");
		}
		repaintManager.repaintTemporaryRepaintAreas(_drawingView);
		((JComponent) view).repaint();
		if (view.getGraphicalRepresentation().hasFloatingLabel()) {
			LabelView<?> label = null;
			if (view instanceof ConnectorView) {
				label = ((ConnectorView<?>) view).getLabelView();
			} else if (view instanceof ShapeView) {
				label = ((ShapeView<?>) view).getLabelView();
			}
			if (label != null) {
				label.repaint();
			}
		}
		// repaintManager.repaintTemporaryRepaintAreas((JComponent)view);

		if (view instanceof ShapeView /*&& isPaintingCacheEnabled()*/) {
			if (((Component) view).getParent() == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Parent view to repaint is null: "
							+ (view.getGraphicalRepresentation() != null ? view.getModel() : view));
				}
				return;
			}
			// What may happen here ?
			// Control points displayed focus or selection might changed, and to be refresh correctely
			// we mut assume that a request to an extended area embedding those control points
			// must be performed (in case of border is not sufficient)
			ShapeGraphicalRepresentation<?> gr = ((ShapeView) view).getGraphicalRepresentation();
			int requiredControlPointSpace = FGEConstants.CONTROL_POINT_SIZE;
			if (gr.getBorder().top * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						((Component) view).getWidth() + requiredControlPointSpace * 2, requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, ((Component) view).getParent());
				((Component) view).getParent().repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().bottom * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, ((Component) view).getHeight()
						- requiredControlPointSpace, ((Component) view).getWidth() + requiredControlPointSpace * 2,
						requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, ((Component) view).getParent());
				((Component) view).getParent().repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().left * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(-requiredControlPointSpace, -requiredControlPointSpace,
						requiredControlPointSpace * 2, ((Component) view).getHeight() + requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, ((Component) view).getParent());
				((Component) view).getParent().repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
			if (gr.getBorder().right * view.getScale() < requiredControlPointSpace) {
				Rectangle repaintAlsoThis = new Rectangle(((Component) view).getWidth() - requiredControlPointSpace,
						-requiredControlPointSpace, requiredControlPointSpace * 2, ((Component) view).getHeight()
								+ requiredControlPointSpace * 2);
				repaintAlsoThis = SwingUtilities.convertRectangle((Component) view, repaintAlsoThis, ((Component) view).getParent());
				((Component) view).getParent().repaint(repaintAlsoThis.x, repaintAlsoThis.y, repaintAlsoThis.width, repaintAlsoThis.height);
				// System.out.println("Repaint "+repaintAlsoThis+" for "+((Component)view).getParent());
			}
		}
	}

	public void repaint(GraphicalRepresentation<?> gr) {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Called REPAINT for graphical representation " + gr);
		}
		FGEView view = _drawingView.viewForObject(gr);
		if (view != null) {
			repaint(view);
		}
	}

	public BufferedImage getScreenshot(GraphicalRepresentation<?> gr) {
		/*Component view = getDrawingView();
		BufferedImage bufferedImage = new BufferedImage(view.getWidth(), view.getHeight(), DEFAULT_IMAGE_TYPE);
		Graphics2D g = bufferedImage.createGraphics();
		view.print(g);
		return bufferedImage;*/
		FGEView<?> v = getDrawingView().viewForObject(gr);
		Rectangle rect = new Rectangle(((JComponent) v).getX(), ((JComponent) v).getY(), ((JComponent) v).getWidth(),
				((JComponent) v).getHeight());
		if (v instanceof ShapeView) {
			if (((ShapeView<?>) v).getLabelView() != null) {
				rect = rect.union(((ShapeView<?>) v).getLabelView().getBounds());
			}
		}
		return getPaintBuffer().getSubimage(rect.x, rect.y, rect.width, rect.height);
	}

	private synchronized BufferedImage bufferDrawingView() {
		if (paintRequestLogger.isLoggable(Level.FINE)) {
			paintRequestLogger.fine("Buffering whole DrawingView. Is it really necessary ?");
		}
		Component view = getDrawingView();
		// GraphicsConfiguration config = view.getGraphicsConfiguration();
		// VolatileImage image = config.createCompatibleVolatileImage(view.getWidth(), view.getHeight());
		BufferedImage image = new BufferedImage(view.getWidth(), view.getHeight(), DEFAULT_IMAGE_TYPE);
		Graphics2D g = image.createGraphics();
		getDrawingView().prepareForBuffering(g);
		view.print(g);
		g.dispose();
		return image;
	}

	private synchronized BufferedImage getPaintBuffer() {
		if (_paintBuffer == null) {
			_paintBuffer = bufferDrawingView();
		}
		/*try {
			File f = File.createTempFile("MyScreenshot", new SimpleDateFormat("HH-mm-ss SSS").format(new Date())+".png");
			ImageUtils.saveImageToFile(_paintBuffer, f, ImageType.PNG);
			if (logger.isLoggable(Level.INFO))
				logger.info("Saved buffer to "+f.getAbsolutePath());
			ToolBox.openFile(f);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return _paintBuffer;
	}

	// *******************************************************************************
	// * Repaint manager *
	// *******************************************************************************

	static class FGERepaintManager extends RepaintManager {

		public static final boolean MANAGE_DIRTY_REGIONS = true;

		// For now temporary repaint areas are registered only for DrawingView !!!!!!
		// Later, we might extend this scheme to the whole view hierarchy
		// Using for example Hashtable<JComponent,Vector<Rectangle>> structure
		private WeakHashMap<JComponent, Vector<Rectangle>> temporaryRepaintAreas;

		public FGERepaintManager() {
			temporaryRepaintAreas = new WeakHashMap<JComponent, Vector<Rectangle>>();
		}

		public synchronized void addTemporaryRepaintArea(Rectangle r, JComponent view) {
			if (MANAGE_DIRTY_REGIONS) {
				Vector<Rectangle> allRect = temporaryRepaintAreas.get(view);
				if (allRect == null) {
					allRect = new Vector<Rectangle>();
					temporaryRepaintAreas.put(view, allRect);
				}
				allRect.add(r);
				if (paintRequestLogger.isLoggable(Level.FINER)) {
					paintRequestLogger.finer("addTemporaryRepaintArea(" + r + ") for " + view.getClass().getSimpleName()
							+ " temporaryRepaintAreas size=" + allRect.size());
				}
			}
		}

		private void repaintTemporaryRepaintAreas(JComponent component) {
			if (MANAGE_DIRTY_REGIONS) {
				Vector<Rectangle> allRect = temporaryRepaintAreas.get(component);
				if (allRect != null) {
					for (Rectangle r : allRect) {
						component.repaint(r);
						if (paintRequestLogger.isLoggable(Level.FINER)) {
							paintRequestLogger.finer("repaint(" + r + ") for " + component.getClass().getSimpleName());
						}
					}
					allRect.clear();
				}
			}
		}

		@Override
		public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
			if (paintRequestLogger.isLoggable(Level.FINEST)) {
				paintRequestLogger.finest("adding DirtyRegion: " + c.getName() + ", " + x + "," + y + " " + w + "x" + h);
			}
			// paintRequestLogger.warning("adding DirtyRegion: "+c.getName()+", "+x+","+y+" "+w+"x"+h);
			super.addDirtyRegion(c, x, y, w, h);
			/*if (MANAGE_DIRTY_REGIONS) {
			Rectangle r2 = new Rectangle(x,y,w,h);
			Iterator<Rectangle> it = temporaryRepaintAreas.iterator();
			while(it.hasNext()) {
				Rectangle next = it.next();
				if (r2.contains(next)) {
					if (paintRequestLogger.isLoggable(Level.FINEST))
						paintRequestLogger.finer("Remove temporary repaint area "+next);
					it.remove();
				}
			}
			}*/
		}

		@Override
		public void paintDirtyRegions() {
			// Unfortunately most of the RepaintManager state is package
			// private and not accessible from the subclass at the moment,
			// so we can't print more info about what's being painted.
			if (paintRequestLogger.isLoggable(Level.FINEST)) {
				paintRequestLogger.finest("painting DirtyRegions");
			}
			super.paintDirtyRegions();
		}

	}

	public static void initFGERepaintManager() {
		logger.info("@@@@@@@@@@@@@@@@ initFGERepaintManager()");
		repaintManager = new FGERepaintManager();
		RepaintManager.setCurrentManager(repaintManager);
	}

	/**
	 * 
	 * @param g
	 * @param renderingBounds
	 * @param gr
	 * @return
	 */
	/*protected boolean renderUsingBuffer(Graphics g, Rectangle renderingBounds, GraphicalRepresentation gr)
	{
	    //	Use buffer
		Image buffer = getPaintBuffer();
		Point p1 = renderingBounds.getLocation();
		Point p2 = new Point(renderingBounds.x+renderingBounds.width,renderingBounds.y+renderingBounds.height);
		if ((p1.x < 0)
				|| (p1.x > buffer.getWidth(null))
				|| (p1.y < 0)
				|| (p1.y > buffer.getHeight(null))
				|| (p2.x < 0)
				|| (p2.x > buffer.getWidth(null))
				|| (p2.y < 0)
				|| (p2.y > buffer.getHeight(null))) {
			// We have here a request for render outside cached image
			// We cannot do that, so skip buffer use and do normal painting
			if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				FGEPaintManager.paintPrimitiveLogger.fine("GraphicalRepresentation:"+gr+" / request to render outside image buffer, use normal rendering clip="+renderingBounds);
			invalidate(gr);
			return false;
		}
		else {
			// OK, we are in our bounds
			if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE))
				FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: use image buffer, copy area "+renderingBounds);
			g.drawImage(buffer,
					p1.x,p1.y,p2.x,p2.y,
					p1.x,p1.y,p2.x,p2.y,
					null);
			return true;
		}
	}*/

	/**
	 * 
	 * @param g
	 * @param renderingBounds
	 * @param gr
	 * @return
	 */
	protected boolean renderUsingBuffer(Graphics2D g, Rectangle renderingBounds, GraphicalRepresentation gr, double scale) {
		if (renderingBounds == null) {
			return false;
		}
		// Use buffer
		BufferedImage buffer = getPaintBuffer();
		Rectangle viewBoundsInDrawingView = GraphicalRepresentation.convertRectangle(gr, renderingBounds,
				gr.getDrawingGraphicalRepresentation(), scale);
		Point dp1 = renderingBounds.getLocation();
		Point dp2 = new Point(renderingBounds.x + renderingBounds.width, renderingBounds.y + renderingBounds.height);
		Point sp1 = viewBoundsInDrawingView.getLocation();
		Point sp2 = new Point(viewBoundsInDrawingView.x + viewBoundsInDrawingView.width, viewBoundsInDrawingView.y
				+ viewBoundsInDrawingView.height);

		if ((sp1.x < 0) || (sp1.x > buffer.getWidth()) || (sp1.y < 0) || (sp1.y > buffer.getHeight()) || (sp2.x < 0)
				|| (sp2.x > buffer.getWidth()) || (sp2.y < 0) || (sp2.y > buffer.getHeight())) {
			// We have here a request for render outside cached image
			// We cannot do that, so skip buffer use and do normal painting
			if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
				FGEPaintManager.paintPrimitiveLogger.fine("GraphicalRepresentation:" + gr
						+ " / request to render outside image buffer, use normal rendering clip=" + renderingBounds);
			}
			// invalidate(gr);
			return false;
		} else {
			// OK, we are in our bounds
			if (FGEPaintManager.paintPrimitiveLogger.isLoggable(Level.FINE)) {
				FGEPaintManager.paintPrimitiveLogger.fine("DrawingView: use image buffer, copy area " + renderingBounds);
			}

			// Below was the previous implementation, using i think a too complex drawing primitive
			// (image was resized and so on)
			/*g.drawImage(buffer,
					dp1.x,dp1.y,dp2.x,dp2.y,
					sp1.x,sp1.y,sp2.x,sp2.y,
					null);*/

			// Alternative implementation: improve performances (hope so)
			Graphics2D newGraphics = (Graphics2D) g.create();
			/** Unactivation of anti-aliasing */
			newGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			newGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			/** Fast rendering required here */
			newGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			newGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			newGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			newGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);

			BufferedImage partialImage = buffer.getSubimage(sp1.x, sp1.y, viewBoundsInDrawingView.width, viewBoundsInDrawingView.height);
			newGraphics.drawImage(partialImage, dp1.x, dp1.y, null);
			newGraphics.dispose();

			return true;
		}
	}

}
