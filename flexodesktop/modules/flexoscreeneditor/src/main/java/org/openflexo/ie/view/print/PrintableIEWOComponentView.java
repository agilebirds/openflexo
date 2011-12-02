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
package org.openflexo.ie.view.print;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.print.FlexoPrintableComponent;
import org.openflexo.print.FlexoPrintableDelegate;

public class PrintableIEWOComponentView extends IEWOComponentView implements FlexoPrintableComponent {

	protected static final Logger logger = Logger.getLogger(PrintableIEWOComponentView.class.getPackage().getName());

	private FlexoPrintableDelegate _printableDelegate;

	public PrintableIEWOComponentView(ComponentInstance component, IEController controller) {
		super(controller, component);
		_printableDelegate = new FlexoPrintableDelegate(this, controller);
	}

	@Override
	public FlexoPrintableDelegate getPrintableDelegate() {
		return _printableDelegate;
	}

	@Override
	public String getDefaultPrintableName() {
		return getModel().getName();
	}

	@Override
	public FlexoModelObject getFlexoModelObject() {
		return getModel();
	}

	@Override
	public void paint(Graphics graphics) {
		FlexoPrintableDelegate.PaintParameters params = _printableDelegate.paintPrelude((Graphics2D) graphics);
		super.paint(graphics);
		_printableDelegate.paintPostlude((Graphics2D) graphics, params);
	}

	@Override
	public void print(Graphics graphics) {
		// logger.info("graphics="+graphics);
		// FlexoPrintableDelegate.PaintParameters params = _printableDelegate.paintPrelude((Graphics2D)graphics);
		super.print(graphics);
		// _printableDelegate.paintPostlude((Graphics2D)graphics, params);
	}

	@Override
	public Rectangle getOptimalBounds() {
		// return getFlexoProcess().getActivityPetriGraph().getOptimalBounds(true);
		return new Rectangle(new Point(0, 0), getSize());
	}

	@Override
	public void resizeComponent(Dimension aSize) {
		setSize(aSize);
		setPreferredSize(aSize);
	}

	@Override
	public void refreshComponent() {
		revalidate();
		repaint();
	}

}
