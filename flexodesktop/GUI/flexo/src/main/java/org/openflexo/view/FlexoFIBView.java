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

import java.awt.BorderLayout;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.fib.view.FIBView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoFIBView<O extends FlexoModelObject> extends JPanel implements GraphicalFlexoObserver {
	static final Logger logger = Logger.getLogger(FlexoFIBView.class.getPackage().getName());

	private O representedObject;
	private FlexoController controller;
	private FIBView fibView;

	public FlexoFIBView(O representedObject, FlexoController controller, File fibFile, FlexoProgress progress) {
		this(representedObject, controller, fibFile, false, progress);
	}

	public FlexoFIBView(O representedObject, FlexoController controller, File fibFile, boolean addScrollBar, FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibFile), addScrollBar, progress);
	}

	public FlexoFIBView(O representedObject, FlexoController controller, String fibResourcePath, FlexoProgress progress) {
		this(representedObject, controller, fibResourcePath, false, progress);
	}

	public FlexoFIBView(O representedObject, FlexoController controller, String fibResourcePath, boolean addScrollBar,
			FlexoProgress progress) {
		this(representedObject, controller, FIBLibrary.instance().retrieveFIBComponent(fibResourcePath), addScrollBar, progress);
	}

	protected FlexoFIBView(O representedObject, FlexoController controller, FIBComponent fibComponent, boolean addScrollBar,
			FlexoProgress progress) {
		super(new BorderLayout());
		this.representedObject = representedObject;
		this.controller = controller;
		representedObject.addObserver(this);

		FlexoFIBController<O> fibController = createFibController(fibComponent, controller);

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("build_view"));
		}

		fibView = fibController.buildView(fibComponent);

		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("init_view"));
		}

		fibController.setDataObject(representedObject);

		if (this instanceof FIBMouseClickListener) {
			fibView.getController().addMouseClickListener((FIBMouseClickListener) this);
		}

		if (addScrollBar) {
			add(new JScrollPane(fibView.getJComponent()), BorderLayout.CENTER);
		} else {
			add(fibView.getJComponent(), BorderLayout.CENTER);
		}

		validate();
		revalidate();

		if (progress != null) {
			progress.hideWindow();
		}
	}

	/**
	 * Create the Fib Controller to be used for this view. Can be overrided to add functionalities to this Fib View.
	 * 
	 * @param fibComponent
	 * @param controller
	 * @return the newly created FlexoFIBController
	 */
	protected FlexoFIBController<O> createFibController(FIBComponent fibComponent, FlexoController controller) {
		FIBController returned = FIBController.instanciateController(fibComponent, FlexoLocalization.getMainLocalizer());
		if (returned instanceof FlexoFIBController) {
			((FlexoFIBController) returned).setFlexoController(controller);
			return (FlexoFIBController) returned;
		} else if (fibComponent.getControllerClass() != null) {
			logger.warning("Controller for component " + fibComponent + " is not an instanceof FlexoFIBController");
		}
		return new FlexoFIBController<O>(fibComponent, controller);
	}

	public FlexoController getFlexoController() {
		return controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		/* if (dataModification instanceof ObjectDeleted) {
		     if (dataModification.oldValue() == getOntologyObject()) {
		         deleteModuleView();
		      }
		 } else if (dataModification.propertyName()!=null && dataModification.propertyName().equals("name")) {
		     getOEController().getFlexoFrame().updateTitle();
		     updateTitlePanel();
		 }*/

	}

	public O getRepresentedObject() {
		representedObject.deleteObserver(this);
		return representedObject;
	}

	public FIBView getFIBView() {
		return fibView;
	}

	public void deleteView() {
		if (this instanceof FIBMouseClickListener) {
			fibView.getController().removeMouseClickListener((FIBMouseClickListener) this);
		}
		representedObject.deleteObserver(this);
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	public boolean isAutoscrolled() {
		return false;
	}

}
