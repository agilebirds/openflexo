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

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.DataBinding;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBSelectionListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.view.controller.FlexoController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class FIBBrowserView<O extends FlexoModelObject> extends SelectionSynchronizedFIBView implements FIBSelectionListener {
	static final Logger logger = Logger.getLogger(FIBBrowserView.class.getPackage().getName());

	// private O representedObject;
	// private FlexoController controller;
	// private FIBView fibView;

	public FIBBrowserView(O representedObject, FlexoController controller, File fibFile) {
		this(representedObject, controller, fibFile, false, controller.willLoad(fibFile));
	}

	public FIBBrowserView(O representedObject, FlexoController controller, File fibFile, FlexoProgress progress) {
		this(representedObject, controller, fibFile, false, progress);
	}

	public FIBBrowserView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, controller.willLoad(fibFile));
	}

	public FIBBrowserView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar, FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
	}

	public FIBBrowserView(O representedObject, FlexoController controller, String fibResourcePath) {
		this(representedObject, controller, fibResourcePath, false, controller.willLoad(fibResourcePath));
	}

	public FIBBrowserView(O representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress) {
		this(representedObject, controller, fibResourcePath, false, progress);
	}

	public FIBBrowserView(O representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
	}

	protected FIBBrowserView(O representedObject, FlexoController controller, FIBComponent fibComponent, boolean addScrollBar,
			FlexoProgress progress) {
		super(representedObject, controller, fibComponent, addScrollBar, progress);
	}

	@Override
	protected void initializeFIBComponent() {
		super.initializeFIBComponent();
		FIBBrowser browser = retrieveFIBBrowser(getFIBComponent());
		if (browser == null) {
			logger.warning("Could not retrieve FIBBrowser for component " + getFIBComponent());
			return;
		}
		if (!browser.getClickAction().isSet() || !browser.getClickAction().isValid()) {
			browser.setClickAction(new DataBinding("controller.singleClick(" + browser.getName() + ".selected)"));
		}
		if (!browser.getDoubleClickAction().isSet() || !browser.getDoubleClickAction().isValid()) {
			browser.setDoubleClickAction(new DataBinding("controller.doubleClick(" + browser.getName() + ".selected)"));
		}
		if (!browser.getRightClickAction().isSet() || !browser.getRightClickAction().isValid()) {
			browser.setRightClickAction(new DataBinding("controller.rightClick(" + browser.getName() + ".selected,event)"));
		}
		/*
		for (FIBBrowserElement el : browser.getElements()) {
			if (el.getDataClass() != null) {
				if (FlexoModelObject.class.isAssignableFrom(el.getDataClass())) {
					Vector<FlexoActionType> actionList = FlexoModelObject.getActionList(el.getDataClass());
					for (FlexoActionType actionType : actionList) {
						el.addToActions(new FIBBrowserActionAdapter(actionType));
					}
				}
			}
		}
		*/
	}

	public FIBBrowser getFIBBrowser(FIBComponent component) {
		return retrieveFIBBrowser(getFIBComponent());
	}

	private static FIBBrowser retrieveFIBBrowser(FIBComponent component) {
		List<FIBComponent> listComponent = component.retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowser) c;
			}
		}
		return null;
	}

}
