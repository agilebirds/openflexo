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
package org.openflexo.view.controller;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.openflexo.GeneralPreferences;
import org.openflexo.ch.DefaultInspectorHelpDelegate;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.MessageDefinition;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.inspector.HelpDelegate;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorWindow;
import org.openflexo.module.Module;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.selection.SelectionManager;


public class FlexoSharedInspectorController extends FlexoInspectorController {

	private final Logger logger = Logger.getLogger(FlexoSharedInspectorController.class.getPackage().getName());

	protected InspectorWindow _inspectorWindow;

	private boolean isBoundsSaverEnabled = true;

	protected FlexoSharedInspectorController(FlexoController controller)
	{
		this(controller.new FlexoControllerInspectorDelegate(), new DefaultInspectorHelpDelegate(DocResourceManager.instance()), controller
				.getFlexoFrame());
		setInspectorNotFoundHandler(controller);
		loadAllCustomInspectors(controller.getProject());
	}

	private FlexoSharedInspectorController(InspectorDelegate inspectorDelegate, HelpDelegate helpDelegate, JFrame frame)
	{
		super(inspectorDelegate, helpDelegate);
		_inspectorWindow = createInspectorWindow(frame);
		Rectangle bounds = GeneralPreferences.getBoundForFrameWithID("Inspector");
		if (bounds != null) {
			// In case we remove a screen (if you go from 3 to 2 screen, go to hell, that's all you deserve ;-))
			if (GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length == 1) {
				Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
				if (screen.width <= bounds.x) {
					bounds.x = 0;
				} else if (screen.height <= bounds.y) {
					bounds.y = 0;
				}
			}
			_inspectorWindow.setBounds(bounds);
		}
		_inspectorWindow.addComponentListener(new ComponentAdapter() {
			/**
			 * Overrides componentResized
			 * 
			 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
			 */
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (isBoundsSaverEnabled()) {
					saveBoundsInPreferenceWhenPossible();
				}
			}

			/**
			 * Overrides componentMoved
			 * 
			 * @see java.awt.event.ComponentAdapter#componentMoved(java.awt.event.ComponentEvent)
			 */
			@Override
			public void componentMoved(ComponentEvent e)
			{
				if (isBoundsSaverEnabled()) {
					saveBoundsInPreferenceWhenPossible();
				}
			}
		});
	}

	public boolean isBoundsSaverEnabled() {
		return isBoundsSaverEnabled;
	}

	public void setBoundsSaverEnabled(boolean isBoundsSaverEnable) {
		this.isBoundsSaverEnabled = isBoundsSaverEnable;
	}

	private Thread boundsSaver;

	protected synchronized void saveBoundsInPreferenceWhenPossible()
	{
		if (!_inspectorWindow.isVisible()) {
			return;
		}
		if (boundsSaver!=null) {
			boundsSaver.interrupt();//Resets thread sleep
			return;
		}

		boundsSaver = new Thread(new Runnable() {
			/**
			 * Overrides run
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				boolean go = true;
				while (go) {
					try {
						go = false;
						Thread.sleep(800);
					} catch (InterruptedException e) {
						go = true;// interruption is used to reset sleep.
					}
				}
				saveBoundsInPreference();
			}
		});
		boundsSaver.start();
	}

	protected void saveBoundsInPreference()
	{
		try {
			if (_inspectorWindow.getBounds().x +_inspectorWindow.getBounds().width < 0) {
				return;
			}
			if (_inspectorWindow.getBounds().y + _inspectorWindow.getHeight() < 0) {
				return;
			}
			GeneralPreferences.setBoundForFrameWithID("Inspector", _inspectorWindow.getBounds());
			FlexoPreferences.savePreferences(true);
		} finally {
			boundsSaver = null;
		}
	}

	public InspectorWindow getInspectorWindow()
	{
		return _inspectorWindow;
	}

	// We override here default behaviour by using other inspectors in the context of
	// WebServiceEditor
	@Override
	public String getInspectorName(InspectableObject object, Hashtable<String,Object> inspectionContext)
	{
		if (inspectionContext.get(SelectionManager.MODULE_KEY).equals(Module.WSE_MODULE.getClassName()))
		{
			if (object instanceof FlexoProcess){
				return Inspectors.WSE.WSFLEXOPROCESS_INSPECTOR;
			}
			else if (object instanceof InPort){
				return Inspectors.WSE.WSINPORT_INSPECTOR;
			}
			else if (object instanceof OutPort){
				return Inspectors.WSE.WSOUTPORT_INSPECTOR;
			}
			else if(object instanceof InOutPort){
				return Inspectors.WSE.WSINOUTPORT_INSPECTOR;
			}
			else if (object instanceof AbstractInPort){
				return Inspectors.WSE.WSABSTRACTINPORT_INSPECTOR;
			}
			else if (object instanceof FlexoPort){
				return Inspectors.WSE.WSPORT_INSPECTOR;
			}
			else if (object instanceof MessageDefinition){
				return Inspectors.WSE.WSMESSAGEDEFINITION_INSPECTOR;
			}
			else if (object instanceof ServiceMessageDefinition){
				return Inspectors.WSE.WSMESSAGEDEFINITION_INSPECTOR;
			}
			return null;
		} else {
			return super.getInspectorName(object, inspectionContext);
		}
	}

}
