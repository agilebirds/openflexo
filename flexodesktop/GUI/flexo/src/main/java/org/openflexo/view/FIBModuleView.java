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
package org.openflexo.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class FIBModuleView<O extends FlexoObject> extends SelectionSynchronizedFIBView implements
		SelectionSynchronizedModuleView<O>, GraphicalFlexoObserver, FIBSelectionListener, Scrollable {
	static final Logger logger = Logger.getLogger(FIBModuleView.class.getPackage().getName());

	// private Object representedObject;
	// private FlexoController controller;
	// private FIBView fibView;

	public FIBModuleView(O representedObject, FlexoController controller, File fibFile) {
		this(representedObject, controller, fibFile, false, controller.willLoad(fibFile));
	}

	public FIBModuleView(O representedObject, FlexoController controller, File fibFile, FlexoProgress progress) {
		this(representedObject, controller, fibFile, false, progress);
	}

	public FIBModuleView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, controller.willLoad(fibFile));
	}

	public FIBModuleView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar, FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
	}

	public FIBModuleView(O representedObject, FlexoController controller, String fibResourcePath) {
		this(representedObject, controller, fibResourcePath, false, controller.willLoad(fibResourcePath));
	}

	public FIBModuleView(O representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress) {
		this(representedObject, controller, fibResourcePath, false, progress);
	}

	public FIBModuleView(O representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
	}

	protected FIBModuleView(O representedObject, FlexoController controller, FIBComponent fibComponent, boolean addScrollBar,
			FlexoProgress progress) {
		super(representedObject, controller, fibComponent, addScrollBar, progress);
	}

	@Override
	public void deleteModuleView() {
		deleteView();
		getFlexoController().removeModuleView(this);
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

	@Override
	public abstract FlexoPerspective getPerspective();

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	@Override
	public O getRepresentedObject() {
		return (O) getDataObject();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch (orientation) {
		case SwingConstants.VERTICAL:
			return visibleRect.height / 10;
		case SwingConstants.HORIZONTAL:
			return visibleRect.width / 10;
		default:
			throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch (orientation) {
		case SwingConstants.VERTICAL:
			return visibleRect.height;
		case SwingConstants.HORIZONTAL:
			return visibleRect.width;
		default:
			throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}

}
