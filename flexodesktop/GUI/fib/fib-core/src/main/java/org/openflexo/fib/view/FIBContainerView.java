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
package org.openflexo.fib.view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;

public abstract class FIBContainerView<M extends FIBContainer, J extends JComponent, T> extends FIBView<M, J, T> {

	private static final Logger logger = Logger.getLogger(FIBContainerView.class.getPackage().getName());

	private Vector<JComponent> subComponents;
	private Hashtable<JComponent, Object> constraints;

	public FIBContainerView(M model, FIBController controller) {
		super(model, controller);

		subComponents = new Vector<JComponent>();
		constraints = new Hashtable<JComponent, Object>();

		createJComponent();
		buildSubComponents();

	}

	@Override
	public void delete() {
		if (isDeleted()) {
			return;
		}
		subComponents.clear();
		constraints.clear();
		subComponents = null;
		constraints = null;
		super.delete();
	}

	protected synchronized void buildSubComponents() {
		subViews.clear();
		subComponents.clear();
		constraints.clear();

		retrieveContainedJComponentsAndConstraints();

		for (JComponent j : subComponents) {
			addJComponent(j);
		}

		updatePreferredSize();

		updateFont();

		getJComponent().revalidate();
		getJComponent().repaint();
	}

	protected abstract J createJComponent();

	protected abstract void retrieveContainedJComponentsAndConstraints();

	protected void addJComponent(JComponent c) {
		// logger.info("addJComponent constraints=" + c);
		Object constraint = constraints.get(c);
		logger.fine(getComponent() + ": addJComponent " + c + " constraint=" + constraint);
		if (constraint == null) {
			getJComponent().add(c);
		} else {
			getJComponent().add(c, constraint);
		}
	}

	@Override
	public abstract J getJComponent();

	@Override
	public J getDynamicJComponent() {
		return getJComponent();
	}

	public T getValue() {
		if (getDataObject() == null) {
			return null;
		}
		if (getComponent().getData() == null || getComponent().getData().isUnset()) {
			return null;
		}
		try {
			return (T) getComponent().getData().getBindingValue(getBindingEvaluationContext());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			return null;
		} catch (NullReferenceException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean update() {
		super.update();
		for (FIBView v : new ArrayList<FIBView>(subViews.values())) {
			v.update();
		}
		return true;
	}

	/*@Override
	public void updateDataObject(final Object dataObject) {
		update(new Vector<FIBComponent>());
		if (isComponentVisible()) {
			for (FIBView v : new ArrayList<FIBView>(subViews.values())) {
				v.updateDataObject(dataObject);
			}
		}
		updateDataDynamicValue();
	}*/

	/*private void updateDataDynamicValue() {
		setData(getValue());
	}*/

	/*private void updateDataDynamicValue() {
		if (getDynamicModel() != null && getComponent().getData().isSet() && getComponent().getData().isValid()) {
			logger.fine("Container: " + getComponent() + " value data for " + getDynamicModel() + " is " + getValue());
			Object newDataValue = getValue();
			if (getDynamicModel().getData() != getValue()) {
				getDynamicModel().setData(getValue());
				notifyDynamicModelChanged();
			}
		}
	}*/

	@Override
	protected boolean checkValidDataPath() {
		if (getParentView() != null && !getParentView().checkValidDataPath()) {
			return false;
		}
		if (getComponent().getDataType() != null) {
			Object value = getValue();
			if (value != null && !TypeUtils.isTypeAssignableFrom(getComponent().getDataType(), value.getClass(), true)) {
				// logger.fine("INVALID data path for component "+getComponent());
				// logger.fine("Value is "+getValue().getClass()+" while expected type is "+getComponent().getDataType());
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateLanguage() {
		for (FIBView v : subViews.values()) {
			// if (!"True".equals(v.getComponent().getParameter(FIBContainer.INHERITED)))
			v.updateLanguage();
		}
	}

	/**
	 * This method is called to update view representing a FIBComponent.<br>
	 * Callers are all the components that have been updated during current update loop. If the callers contains the component itself, does
	 * nothing and return.
	 * 
	 * @param callers
	 *            all the components that have been previously updated during current update loop
	 * @return a flag indicating if component has been updated
	 */
	/*@Override
	public boolean update(List<FIBComponent> callers) {
		boolean returned = super.update(callers);
		updateDataDynamicValue();

		return returned;
	}*/

	protected void registerViewForComponent(FIBView view, FIBComponent component) {
		subViews.put(component, view);
	}

	protected void registerComponentWithConstraints(JComponent component, Object constraint) {
		registerComponentWithConstraints(component, constraint, -1);
	}

	protected void registerComponentWithConstraints(JComponent component, Object constraint, int index) {
		logger.fine("Register component: " + component + " constraint=" + constraint);
		if (index < 0 || index > subComponents.size()) {
			index = subComponents.size();
		}
		subComponents.add(index, component);
		if (constraint != null) {
			constraints.put(component, constraint);
		}
	}

	protected void registerComponentWithConstraints(JComponent component, int index) {
		registerComponentWithConstraints(component, null, index);
	}

	protected void registerComponentWithConstraints(JComponent component) {
		registerComponentWithConstraints(component, null, -1);
	}

	/*protected void registerComponentWithConstraints(JComponent component, int index)
	{
		logger.fine("Register component: "+component+" index="+index);
		subComponents.insertElementAt(component,index);
	}

	protected void registerComponentWithConstraints(JComponent component, Object constraint, int index)
	{
		logger.fine("Register component: "+component+" index="+index);
		subComponents.insertElementAt(component,index);
		if (constraint != null) constraints.put(component,constraint);
	}*/

	protected Vector<JComponent> getSubComponents() {
		return subComponents;
	}

	protected Hashtable<JComponent, Object> getConstraints() {
		return constraints;
	}

	@Override
	public synchronized void updateFont() {
		if (getFont() != null) {
			getJComponent().setFont(getFont());
		}
		for (FIBView v : subViews.values()) {
			v.updateFont();
		}
		getJComponent().revalidate();
		getJComponent().repaint();
	}

	public abstract void updateLayout();

}
