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
package org.openflexo.ie.view;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.util.FlexoConceptualColor;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.IEFocusListener;
import org.openflexo.ie.view.widget.IEWidgetView;

/**
 * Abstract parent class for all classes representing views in IE Module that must preempt the focus At this level are implemented the fact
 * that all those classes are focusable and have the same FocusListener which is the unique instance of {@link IEFocusListener}. As views
 * managing focus, they implements a way to request the focus when the mouse entered in. As views managing focus, they all have the same
 * KeyListener which is the unique instance of {@link IEKeyEventListener}.
 * 
 * <ul>
 * <li>All instances of
 * 
 * <pre>
 * IEPanel
 * </pre>
 * 
 * are able to retrieve their root component, as an instance of {@link IEWOComponentView}, see {@link getRootView()}</li>
 * <li>All instances of
 * 
 * <pre>
 * IEPanel
 * </pre>
 * 
 * are able to find the
 * 
 * <pre>
 * IEWidgetView
 * </pre>
 * 
 * representing a
 * 
 * <pre>
 * IEWidget
 * </pre>
 * 
 * , see {@link findViewForModel(IEWidget)}</li>
 * <li>All instances of
 * 
 * <pre>
 * IEPanel
 * </pre>
 * 
 * are able to internally find the
 * 
 * <pre>
 * IEWidgetView
 * </pre>
 * 
 * representing a
 * 
 * <pre>
 * IEWidget
 * </pre>
 * 
 * , see {@link findViewForModel(IEWidget)}</li>
 * </ul>
 * 
 * @author sguerin
 */
public abstract class IEPanel extends JPanel implements IEViewManaging, FlexoActionSource {

	protected static final Logger logger = Logger.getLogger(IEPanel.class.getPackage().getName());

	private IEWOComponentView _rootView;

	private IEController _ieController;

	protected IEPanel(IEController iecontroller) {
		super();
		setVisible(true);
		_ieController = iecontroller;
		setFocusable(true);
		setBackground(Color.WHITE);
		setOpaque(true);
		_rootView = null;
		addFocusListener(IEFocusListener.instance());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (logger.isLoggable(Level.FINE)) {
					logger.finest("Mouse entered in " + getClass().getName() + ", try to get the focus");
					// requestFocus();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				requestFocus();
			}
		});
	}

	public IEController getIEController() {
		return _ieController;
	}

	@Override
	public IEWOComponentView getRootView() {
		if (_rootView == null) {
			_rootView = ViewFinder.getRootView(this);
		}
		return _rootView;
	}

	@Override
	public IEWidgetView findViewForModel(IEObject object) {
		return ViewFinder.findViewForModel(this, object);
	}

	@Override
	public IEWidgetView internallyFindViewForModel(IEObject object) {
		return ViewFinder.internallyFindViewForModel(this, object);
	}

	public Color colorFromConceptualColor(FlexoConceptualColor conceptualColor, FlexoCSS css) {
		return IEViewUtils.colorFromConceptualColor(conceptualColor, css);
	}

	@Override
	public FlexoEditor getEditor() {
		return getIEController().getEditor();
	}

	@Override
	public FlexoObject getFocusedObject() {
		return getIEController().getSelectionManager().getFocusedObject();
	}

	@Override
	public Vector<FlexoObject> getGlobalSelection() {
		return getIEController().getSelectionManager().getSelection();
	}

}
