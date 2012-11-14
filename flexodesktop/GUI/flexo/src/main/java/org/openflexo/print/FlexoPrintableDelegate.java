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
package org.openflexo.print;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.utils.DrawUtils;

public class FlexoPrintableDelegate implements Printable, Pageable {

	private static final Logger logger = Logger.getLogger(FlexoPrintableDelegate.class.getPackage().getName());

	private enum PaintMode {
		PREVIEW, PRINTING
	};

	private double scale = 1.0;
	private double previewScale = 1.0;
	private PageFormat _pageFormat;
	private int _pageIndex;
	private PaintMode _paintMode = PaintMode.PREVIEW;

	private FlexoPrintableComponent _printableComponent;
	private PrintManagingController _controller;

	public FlexoPrintableDelegate(FlexoPrintableComponent printableComponent, PrintManagingController controller) {
		super();
		_controller = controller;
		_printableComponent = printableComponent;
		removeAllMouseListeners();
		_printTitle = _printableComponent.getDefaultPrintableName();
		updatePageLayout();
	}

	private void removeAllMouseListeners() {
		removeAllMouseListenersForComponent((Component) _printableComponent);
	}

	private void removeAllMouseListenersForComponent(Component aComponent) {
		Vector<MouseListener> mlToRemove = new Vector<MouseListener>();
		for (MouseListener ml : aComponent.getMouseListeners()) {
			mlToRemove.add(ml);
		}
		for (MouseListener ml : mlToRemove) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Remove MouseListener for " + aComponent);
			}
			aComponent.removeMouseListener(ml);
		}
		Vector<MouseMotionListener> mmlToRemove = new Vector<MouseMotionListener>();
		for (MouseMotionListener mml : aComponent.getMouseMotionListeners()) {
			mmlToRemove.add(mml);
		}
		for (MouseMotionListener mml : mmlToRemove) {
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Remove MouseMotionListener for " + aComponent);
			}
			aComponent.removeMouseMotionListener(mml);
		}
		if (aComponent instanceof Container) {
			for (Component comp : ((Container) aComponent).getComponents()) {
				removeAllMouseListenersForComponent(comp);
			}
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) /* throws PrinterException */
	{
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("PRINT page " + pageIndex);
			logger.fine("pageFormat= " + pageFormat);
			logger.fine("graphics= " + graphics);
		}

		_pageFormat = pageFormat;

		if (pageIndex >= getNumberOfPages()) {
			return Printable.NO_SUCH_PAGE;
		}

		_paintMode = PaintMode.PRINTING;
		_pageIndex = pageIndex;
		_printableComponent.refreshComponent();
		_printableComponent.print(graphics);

		return Printable.PAGE_EXISTS;
	}

	public void preview(PageFormat pageFormat) /* throws PrinterException */
	{
		_pageFormat = pageFormat;
		_paintMode = PaintMode.PREVIEW;
		updatePageLayout();
		refresh();
	}

	public static class PaintParameters {
		int i, j;
		double tx = 0.0;
		double ty = 0.0;
		double marginScale = 1.0;
		double leftMargin = 0.0;
		double rightMargin = 0.0;
		double topMargin = 0.0;
		double bottomMargin = 0.0;
	}

	public PaintParameters paintPrelude(Graphics2D g2) {
		PaintParameters returned = new PaintParameters();
		if (_paintMode == PaintMode.PREVIEW) {
			g2.scale(previewScale, previewScale);
		} else if (_paintMode == PaintMode.PRINTING) {
			returned.j = _pageIndex / widthPageNb;
			returned.i = _pageIndex - widthPageNb * returned.j;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CALLED paint with _pageIndex=" + _pageIndex + " i=" + returned.i + " j=" + returned.j);
			}
			Rectangle pageBounds = getPageRect(_pageFormat);
			Rectangle imageablePageBounds = getImageablePageRect(_pageFormat);

			returned.marginScale = Math.min((double) imageablePageBounds.width / (double) pageBounds.width,
					(double) imageablePageBounds.height / (double) pageBounds.height);
			g2.scale(returned.marginScale, returned.marginScale);
			if (logger.isLoggable(Level.FINER)) {
				logger.finer("marginScale =" + returned.marginScale);
			}

			returned.leftMargin = imageablePageBounds.x;
			returned.rightMargin = pageBounds.width - imageablePageBounds.x;
			returned.topMargin = imageablePageBounds.x;
			returned.bottomMargin = pageBounds.height - imageablePageBounds.y;

			returned.tx = (double) -pageBounds.width * returned.i + returned.leftMargin;
			returned.ty = (double) -pageBounds.height * returned.j + returned.topMargin;

			if (logger.isLoggable(Level.FINER)) {
				logger.finer("Translate tx=" + returned.tx + " ty=" + returned.ty);
			}
			g2.translate(returned.tx, returned.ty);
		}
		g2.scale(scale, scale);
		return returned;
	}

	public void paintPostlude(Graphics2D g2, PaintParameters params) {
		g2.scale(1 / scale, 1 / scale);
		if (_paintMode == PaintMode.PREVIEW) {
			// paintOptimalView(graphics);
			paintPages(g2);
		}
		if (showPages()) {
			showPages(g2);
		}
		if (showTitles()) {
			showTitles(g2);
		}
		if (_paintMode == PaintMode.PREVIEW) {
			g2.scale(1 / previewScale, 1 / previewScale);
		} else if (_paintMode == PaintMode.PRINTING) {
			g2.translate(-params.tx, -params.ty);
			g2.scale(1 / params.marginScale, 1 / params.marginScale);
		}
	}

	/* public void paint(Graphics graphics)
	 {
	     int i,j;
	     double tx = 0.0;
	     double ty = 0.0;
	     double marginScale = 1.0;

	     if (_paintMode == PaintMode.PREVIEW) {
	         ((Graphics2D)graphics).scale(previewScale, previewScale);
	     }
	     else if (_paintMode == PaintMode.PRINTING) {
	          j = _pageIndex / widthPageNb;
	          i = _pageIndex - widthPageNb*j;
	          if (logger.isLoggable(Level.FINE))
	              logger.fine("CALLED paint with _pageIndex="+_pageIndex+" i="+i+" j="+j);
	         Rectangle pageBounds = getPageRect(_pageFormat);
	         Rectangle imageablePageBounds = getImageablePageRect(_pageFormat);
	                     
	         marginScale = Math.min((double)imageablePageBounds.width/(double)pageBounds.width, (double)imageablePageBounds.height/(double)pageBounds.height);
	         ((Graphics2D)graphics).scale(marginScale, marginScale);
	         if (logger.isLoggable(Level.FINER))
	             logger.finer("marginScale ="+marginScale);
	        
	         double leftMargin = imageablePageBounds.x;
	         double rightMargin = pageBounds.width-imageablePageBounds.x;
	         double topMargin = imageablePageBounds.x;
	         double bottomMargin = pageBounds.height-imageablePageBounds.y;
	         
	          tx = (double)-pageBounds.width*i+leftMargin;
	          ty = (double)-pageBounds.height*j+topMargin;
	          
	          if (logger.isLoggable(Level.FINER))
	              logger.finer("Translate tx="+tx+" ty="+ty);
	         ((Graphics2D)graphics).translate(tx,ty);
	     }
	    ((Graphics2D)graphics).scale(scale, scale);
	     super.paint(graphics);
	     ((Graphics2D)graphics).scale(1/scale, 1/scale);
	     if (_paintMode == PaintMode.PREVIEW) {
	         //paintOptimalView(graphics);
	         paintPages(graphics);
	     }
	     if (showPages()) 
	         showPages(graphics);       
	     if (showTitles()) 
	         showTitles(graphics);       
	     if (_paintMode == PaintMode.PREVIEW) {
	         ((Graphics2D)graphics).scale(1/previewScale, 1/previewScale);
	     }
	     else if (_paintMode == PaintMode.PRINTING) {
	          ((Graphics2D)graphics).translate(-tx,-ty);
	          ((Graphics2D)graphics).scale(1/marginScale, 1/marginScale);
	     }
	}*/

	private void showPages(Graphics2D g2) {
		g2.setColor(Color.black);
		int fontHeight = g2.getFontMetrics().getHeight();
		int fontDesent = g2.getFontMetrics().getDescent();
		Rectangle pageBounds = getPageRect(_pageFormat);
		Rectangle imageablePageBounds = getImageablePageRect(_pageFormat);
		int bottomMargin = pageBounds.height - imageablePageBounds.height - imageablePageBounds.y;
		for (int i = 0; i < widthPageNb; i++) {
			for (int j = 0; j < heightPageNb; j++) {
				int pageIndex = j * widthPageNb + i;
				// logger.info("Print page "+(pageIndex+1)+" scaleX="+g2.getTransform().getScaleX()+" scaleY="+g2.getTransform().getScaleY());
				if (_paintMode == PaintMode.PREVIEW) {
					g2.drawString("Page: " + (pageIndex + 1) + "/" + getNumberOfPages(), pageBounds.width / 2 - 35 + i * pageBounds.width,
							pageBounds.height - fontHeight - fontDesent - bottomMargin + j * pageBounds.height);
				} else if (_paintMode == PaintMode.PRINTING) {
					g2.drawString("Page: " + (pageIndex + 1) + "/" + getNumberOfPages(), pageBounds.width / 2 - 35 + i * pageBounds.width,
							pageBounds.height - fontHeight - fontDesent + j * pageBounds.height);
				}
			}
		}
	}

	private void showTitles(Graphics2D g2) {
		g2.setColor(Color.black);
		// int fontHeight=g2.getFontMetrics().getHeight();
		// int fontDesent=g2.getFontMetrics().getDescent();
		Rectangle2D titleBounds = g2.getFontMetrics().getStringBounds(getPrintTitle(), g2);
		Rectangle pageBounds = getPageRect(_pageFormat);
		Rectangle imageablePageBounds = getImageablePageRect(_pageFormat);
		// int bottomMargin = pageBounds.height-imageablePageBounds.height-imageablePageBounds.y;
		for (int i = 0; i < widthPageNb; i++) {
			for (int j = 0; j < heightPageNb; j++) {
				// int pageIndex = j*widthPageNb+i;
				if (_paintMode == PaintMode.PREVIEW) {
					g2.drawString(getPrintTitle(), (int) (pageBounds.width / 2 - titleBounds.getWidth() / 2) + i * pageBounds.width,
							imageablePageBounds.y + 15 + j * pageBounds.height);
				} else if (_paintMode == PaintMode.PRINTING) {
					g2.drawString(getPrintTitle(), (int) (pageBounds.width / 2 - titleBounds.getWidth() / 2) + i * pageBounds.width, 15 + j
							* pageBounds.height);
				}
			}
		}
	}

	@Override
	public int getNumberOfPages() {
		if (logger.isLoggable(Level.FINER)) {
			logger.fine("numberOfPages=" + widthPageNb * heightPageNb);
		}
		return widthPageNb * heightPageNb;
	}

	/*private void paintOptimalView(Graphics g)
	{
	   Graphics2D g2 = (Graphics2D) g;
	    DrawUtils.turnOnAntiAlising(g2);
	    DrawUtils.setRenderQuality(g2);
	    DrawUtils.setColorRenderQuality(g2);
	    g2.setColor(Color.BLUE);
	    Rectangle optimalBounds = getOptimalBounds();
	    if (logger.isLoggable(Level.FINER))
	        logger.finer("Optimal: "+optimalBounds);
	     if (optimalBounds != null) {
	        g2.drawRect(optimalBounds.x, optimalBounds.y, optimalBounds.width, optimalBounds.height);
	    }
	}*/

	private static float[] dash = { 3.0f, 3.0f };
	public static final Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

	private void paintPages(Graphics2D g2) {
		DrawUtils.turnOnAntiAlising(g2);
		DrawUtils.setRenderQuality(g2);
		DrawUtils.setColorRenderQuality(g2);
		g2.setColor(Color.RED);
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(DASHED_STROKE);
		Rectangle pageBounds = getPageRect(_pageFormat);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("pageBounds: " + pageBounds);
		}
		for (int i = 0; i < widthPageNb; i++) {
			for (int j = 0; j < heightPageNb; j++) {
				paintPage(g2, i, j);
			}
		}
		g2.setColor(Color.YELLOW);
		Rectangle imageablePageBounds = getImageablePageRect(_pageFormat);
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("imageablePageBounds: " + imageablePageBounds);
		}
		if (pageBounds != null) {
			g2.drawRect(imageablePageBounds.x, imageablePageBounds.y, imageablePageBounds.width, imageablePageBounds.height);
		}
		g2.setStroke(oldStroke);
	}

	private void paintPage(Graphics2D g2, int i, int j) {
		Rectangle pageBounds = getPageRect(_pageFormat);
		if (pageBounds != null) {
			g2.drawRect(pageBounds.width * i, pageBounds.height * j, pageBounds.width, pageBounds.height);
		}
	}

	protected Rectangle getOptimalBounds() {
		return _printableComponent.getOptimalBounds();
	}

	protected Rectangle getImageablePageRect(PageFormat pageFormat) {
		return new Rectangle((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY(), (int) pageFormat.getImageableWidth(),
				(int) pageFormat.getImageableHeight());
	}

	protected Rectangle getPageRect(PageFormat pageFormat) {
		return new Rectangle(0, 0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Scale is " + scale);
		}
		updatePageLayout();
	}

	public double getPreviewScale() {
		return previewScale;
	}

	public void setPreviewScale(double previewScale) {
		this.previewScale = previewScale;
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Preview scale is " + previewScale);
		}
		updateSize();
	}

	public void fitToPage() {
		if (_pageFormat != null) {
			Rectangle optimalBounds = getOptimalBounds();
			Rectangle pageBounds = getPageRect(_pageFormat);
			double scaleX = (double) pageBounds.width / (double) (optimalBounds.x + optimalBounds.width + 1);
			double scaleY = (double) pageBounds.height / (double) (optimalBounds.y + optimalBounds.height + 1);
			if (scaleX < scaleY) {
				setScale(scaleX);
			} else {
				setScale(scaleY);
			}
		}
	}

	private int heightPageNb = 1;
	private int widthPageNb = 1;

	private void updatePageLayout() {
		if (_pageFormat != null) {
			Rectangle optimalBounds = getOptimalBounds();
			Rectangle pageBounds = getPageRect(_pageFormat);
			widthPageNb = (int) ((optimalBounds.x + optimalBounds.width) * scale / pageBounds.width) + 1;
			heightPageNb = (int) ((optimalBounds.y + optimalBounds.height) * scale / pageBounds.height) + 1;
			updateSize();
		}
	}

	private void updateSize() {
		Rectangle pageBounds = getPageRect(_pageFormat);
		Dimension viewSize = new Dimension((int) (widthPageNb * pageBounds.width * previewScale),
				(int) (heightPageNb * pageBounds.height * previewScale));
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Resized to " + viewSize);
		}
		_printableComponent.resizeComponent(viewSize);
	}

	public void refresh() {
		_printableComponent.refreshComponent();
	}

	public PaintMode getPaintMode() {
		return _paintMode;
	}

	public void setPaintMode(PaintMode paintMode) {
		_paintMode = paintMode;
	}

	public int getHeightPageNb() {
		return heightPageNb;
	}

	public int getWidthPageNb() {
		return widthPageNb;
	}

	public PageFormat getPageFormat() {
		return _pageFormat;
	}

	public void setPageFormat(PageFormat pageFormat) {
		_pageFormat = pageFormat;
		updatePageLayout();
		refresh();
	}

	private boolean _showPages;
	private boolean _showTitles;
	private String _printTitle;

	public String getPrintTitle() {
		return _printTitle;
	}

	public void setPrintTitle(String printTitle) {
		_printTitle = printTitle;
	}

	public boolean showPages() {
		return _showPages;
	}

	public void setShowPages(boolean showPages) {
		_showPages = showPages;
	}

	public boolean showTitles() {
		return _showTitles;
	}

	public void setShowTitles(boolean showTitles) {
		_showTitles = showTitles;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getPageFormat() with " + pageIndex);
		}
		_pageIndex = pageIndex;
		return _pageFormat;
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getPrintable() with " + pageIndex);
		}
		_pageIndex = pageIndex;
		return this;
	}

	public PrintManagingController getController() {
		return _controller;
	}

}
