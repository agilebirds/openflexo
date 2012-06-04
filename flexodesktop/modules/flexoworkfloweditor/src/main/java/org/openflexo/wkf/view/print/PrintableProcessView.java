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
package org.openflexo.wkf.view.print;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.print.FlexoPrintableComponent;
import org.openflexo.print.FlexoPrintableDelegate;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.wkf.processeditor.ProcessEditorController;
import org.openflexo.wkf.processeditor.ProcessRepresentation;
import org.openflexo.wkf.processeditor.ProcessView;

public class PrintableProcessView extends ProcessView implements FlexoPrintableComponent {

	protected static final Logger logger = Logger.getLogger(PrintableProcessView.class.getPackage().getName());

	private FlexoPrintableDelegate _printableDelegate;

	public PrintableProcessView(ProcessRepresentation processRepresentation, ProcessEditorController printController,
			WKFController controller) {
		super(processRepresentation, printController);
		_printableDelegate = new FlexoPrintableDelegate(this, controller);
	}

	@Override
	public FlexoPrintableDelegate getPrintableDelegate() {
		return _printableDelegate;
	}

	public FlexoProcess getFlexoProcess() {
		return getModel().getProcess();
	}

	@Override
	public FlexoModelObject getFlexoModelObject() {
		return getFlexoProcess();
	}

	@Override
	public String getDefaultPrintableName() {
		return getFlexoProcess().getName();
	}

	@Override
	public void paint(Graphics graphics) {
		// logger.info("graphics="+graphics);
		FlexoPrintableDelegate.PaintParameters params = _printableDelegate.paintPrelude((Graphics2D) graphics);
		super.paint(graphics);
		_printableDelegate.paintPostlude((Graphics2D) graphics, params);
	}

	@Override
	public void print(Graphics graphics) {
		super.print(graphics);
	}

	@Override
	public Rectangle getOptimalBounds() {
		return getOptimalBounds(getFlexoProcess());
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

	/**
	 * Return optimal size, regarding contained nodes
	 */
	public static Rectangle getOptimalBounds(FlexoProcess process) {
		// Please reimplement this
		return new Rectangle(0, 0, (int) process.getWidth(ProcessEditorConstants.BASIC_PROCESS_EDITOR),
				(int) process.getHeight(ProcessEditorConstants.BASIC_PROCESS_EDITOR));
	}

}
