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

import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.ImageIcon;

import org.openflexo.Flexo;
import org.openflexo.components.ProgressWindow;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManager;

/**
 * Represents the controller of a FIBComponent in Openflexo graphical context (at this time, Swing)<br>
 * Extends FIBController by supporting FlexoController and icon management for Openflexo objects
 * 
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class FlexoFIBController extends FIBController implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(FlexoFIBController.class.getPackage().getName());

	private FlexoController controller;

	public static final ImageIcon ARROW_DOWN = UtilsIconLibrary.ARROW_DOWN_2;
	public static final ImageIcon ARROW_UP = UtilsIconLibrary.ARROW_UP_2;
	public static final ImageIcon ARROW_BOTTOM = UtilsIconLibrary.ARROW_BOTTOM_2;
	public static final ImageIcon ARROW_TOP = UtilsIconLibrary.ARROW_TOP_2;

	public static final ImageIcon ONTOLOGY_ICON = OntologyIconLibrary.ONTOLOGY_ICON;
	public static final ImageIcon ONTOLOGY_CLASS_ICON = OntologyIconLibrary.ONTOLOGY_CLASS_ICON;
	public static final ImageIcon ONTOLOGY_INDIVIDUAL_ICON = OntologyIconLibrary.ONTOLOGY_INDIVIDUAL_ICON;
	public static final ImageIcon ONTOLOGY_DATA_PROPERTY_ICON = OntologyIconLibrary.ONTOLOGY_DATA_PROPERTY_ICON;
	public static final ImageIcon ONTOLOGY_OBJECT_PROPERTY_ICON = OntologyIconLibrary.ONTOLOGY_OBJECT_PROPERTY_ICON;
	public static final ImageIcon ONTOLOGY_ANNOTATION_PROPERTY_ICON = OntologyIconLibrary.ONTOLOGY_ANNOTATION_PROPERTY_ICON;

	public FlexoFIBController(FIBComponent component) {
		super(component);
		// Default parent localizer is the main localizer
		setParentLocalizer(FlexoLocalization.getMainLocalizer());
	}

	public FlexoFIBController(FIBComponent component, FlexoController controller) {
		super(component);
		this.controller = controller;
	}

	public FlexoController getFlexoController() {
		return controller;
	}

	public void setFlexoController(FlexoController aController) {
		controller = aController;
	}

	public FlexoEditor getEditor() {
		if (getFlexoController() != null) {
			return getFlexoController().getEditor();
		}
		return null;
	}

	public SelectionManager getSelectionManager() {
		if (getFlexoController() != null) {
			return getFlexoController().getSelectionManager();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable o, DataModification dataModification) {
		getRootView().updateDataObject(getDataObject());
	}

	@Override
	public void setDataObject(Object anObject) {
		if (anObject != getDataObject()) {
			if (getDataObject() instanceof FlexoObservable) {
				((FlexoObservable) getDataObject()).deleteObserver(this);
			}
			super.setDataObject(anObject);
			if (anObject instanceof FlexoObservable) {
				((FlexoObservable) anObject).addObserver(this);
			}
		}

		logger.fine("Set DataObject with " + anObject);
		super.setDataObject(anObject);
	}

	public void singleClick(FlexoModelObject object) {
		if (getFlexoController() != null) {
			getFlexoController().objectWasClicked(object);
		}
	}

	public void doubleClick(FlexoModelObject object) {
		if (getFlexoController() != null) {
			getFlexoController().objectWasDoubleClicked(object);
		}
	}

	public void rightClick(FlexoModelObject object, MouseEvent e) {
		if (getFlexoController() != null) {
			getFlexoController().objectWasRightClicked(object, e);
		}
	}

	public ImageIcon iconForObject(FlexoModelObject object) {
		if (getFlexoController() != null) {
			return getFlexoController().iconForObject(object);
		}
		return null;
	}

	@Override
	protected boolean allowsFIBEdition() {
		return Flexo.isDev;
	}

	@Override
	protected void openFIBEditor(FIBComponent component, MouseEvent event) {
		ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("opening_fib_editor"), 1);
		super.openFIBEditor(component, event);
		ProgressWindow.hideProgressWindow();
	}

	/**
	 * Called when a throwable has been raised during model code invocation.
	 * 
	 * @param t
	 * @return true is exception was correctely handled
	 */
	@Override
	public boolean handleException(Throwable t) {
		if (t instanceof InvalidNameException) {
			FlexoController.showError(FlexoLocalization.localizedForKey("invalid_name") + " : "
					+ ((InvalidNameException) t).getExplanation());
			return true;
		}
		return super.handleException(t);
	}

	public ImageIcon getArrowDown() {
		return ARROW_DOWN;
	}

	public ImageIcon getArrowUp() {
		return ARROW_UP;
	}

	public ImageIcon getArrowTop() {
		return ARROW_TOP;
	}

	public ImageIcon getArrowBottom() {
		return ARROW_BOTTOM;
	}

	public ImageIcon getOntologyIcon() {
		return ONTOLOGY_ICON;
	}

	public ImageIcon getOntologyClassIcon() {
		return ONTOLOGY_CLASS_ICON;
	}

	public ImageIcon getOntologyIndividualIcon() {
		return ONTOLOGY_INDIVIDUAL_ICON;
	}

	public ImageIcon getOntologyDataPropertyIcon() {
		return ONTOLOGY_DATA_PROPERTY_ICON;
	}

	public ImageIcon getOntologyObjectPropertyIcon() {
		return ONTOLOGY_OBJECT_PROPERTY_ICON;
	}

	public ImageIcon getOntologyAnnotationPropertyIcon() {
		return ONTOLOGY_ANNOTATION_PROPERTY_ICON;
	}

}
