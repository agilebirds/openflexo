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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.swing.CustomPopup.ApplyCancelListener;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

public abstract class DefaultFIBCustomComponent<T> extends JPanel implements FIBCustomComponent<T, DefaultFIBCustomComponent<T>>,
		HasPropertyChangeSupport {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DefaultFIBCustomComponent.class.getPackage().getName());

	private final Vector<ApplyCancelListener> applyCancelListener;

	private T editedObject;

	protected FIBComponent fibComponent;
	protected FIBView<?, ?> fibView;
	protected FIBController controller;
	protected LocalizedDelegate localizer;

	private PropertyChangeSupport pcSupport;

	public DefaultFIBCustomComponent(FIBComponent component, T editedObject, LocalizedDelegate parentLocalizer) {
		super();
		localizer = parentLocalizer;
		setOpaque(false);
		pcSupport = new PropertyChangeSupport(this);

		this.editedObject = editedObject;

		fibComponent = component;
		controller = makeFIBController(fibComponent, parentLocalizer);
		fibView = controller.buildView(fibComponent);

		controller.setDataObject(editedObject);

		setLayout(new BorderLayout());
		add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		applyCancelListener = new Vector<ApplyCancelListener>();
	}

	public DefaultFIBCustomComponent(FileResource fibFile, T editedObject, LocalizedDelegate parentLocalizer) {
		this(FIBLibrary.instance().retrieveFIBComponent(fibFile), editedObject, parentLocalizer);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	protected FIBController makeFIBController(FIBComponent fibComponent, LocalizedDelegate parentLocalizer) {
		return FIBController.instanciateController(fibComponent, parentLocalizer);
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public DefaultFIBCustomComponent<T> getJComponent() {
		return this;
	}

	@Override
	public void addApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.add(l);
	}

	@Override
	public void removeApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.remove(l);
	}

	@Override
	public T getEditedObject() {
		return editedObject;
	}

	@Override
	public void setEditedObject(T object) {
		if (editedObject == null || !editedObject.equals(object)) {
			editedObject = object;
			fireEditedObjectChanged();
		}
	}

	public void fireEditedObjectChanged() {
		controller.setDataObject(editedObject, true);
	}

	@Override
	public T getRevertValue() {
		// Not implemented here, implement in sub-classes
		return getEditedObject();
	}

	@Override
	public void setRevertValue(T oldValue) {
		// Not implemented here, implement in sub-classes
	}

	public FIBComponent getFIBComponent() {
		return fibComponent;
	}

	public FIBController getController() {
		return controller;
	}

}
