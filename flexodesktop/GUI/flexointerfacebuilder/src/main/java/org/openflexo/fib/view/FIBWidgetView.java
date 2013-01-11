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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DependingObjects;
import org.openflexo.antar.binding.DependingObjects.HasDependencyBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * Abstract class representing a widget view
 * 
 * @author sylvain
 */
public abstract class FIBWidgetView<M extends FIBWidget, J extends JComponent, T> extends FIBView<M, J> implements FocusListener, Observer,
		PropertyChangeListener, HasDependencyBinding {

	private static final Logger logger = Logger.getLogger(FIBWidgetView.class.getPackage().getName());

	public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font DEFAULT_MEDIUM_FONT = new Font("SansSerif", Font.PLAIN, 10);

	protected boolean modelUpdating = false;
	protected boolean widgetUpdating = false;

	private boolean enabled = true;

	public static final Dimension MINIMUM_SIZE = new Dimension(30, 25);

	private final DynamicFormatter formatter;
	private final DynamicEventListener eventListener;

	private DependingObjects dependingObjects;

	protected FIBWidgetView(M model, FIBController aController) {
		super(model, aController);
		formatter = new DynamicFormatter();
		eventListener = new DynamicEventListener();
	}

	@Override
	public synchronized void delete() {
		if (dependingObjects != null) {
			dependingObjects.stopObserving();
			dependingObjects = null;
		}
		super.delete();
	}

	public M getWidget() {
		return getComponent();
	}

	/**
	 * Update the widget retrieving data from the model. This method is called when the observed property change.
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public abstract boolean updateWidgetFromModel();

	/**
	 * Update the model given the actual state of the widget
	 * 
	 * @return boolean indicating if changes were required or not
	 */
	public abstract boolean updateModelFromWidget();

	@Override
	public void focusGained(FocusEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("focusGained()");
		}
		gainFocus();
	}

	@Override
	public void focusLost(FocusEvent event) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("focusLost()");
		}

		if (event.getOppositeComponent() != null && SwingUtilities.isDescendingFrom(event.getOppositeComponent(), getJComponent())) {
			// Not relevant in this case
		} else {
			looseFocus();
		}
	}

	protected boolean _hasFocus;

	public void gainFocus() {
		if (getController().getFocusedWidget() != null && getController().getFocusedWidget()._hasFocus == true) {
			getController().getFocusedWidget().looseFocus();
		}
		logger.fine("Getting focus: " + getWidget());
		_hasFocus = true;
		getController().setFocusedWidget(this);
	}

	public void looseFocus() {
		logger.fine("Loosing focus: " + getWidget());
		if (!modelUpdating && !isDeleted()) {
			updateModelFromWidget();
		}
		_hasFocus = false;
	}

	public boolean isFocused() {
		return _hasFocus;
	}

	public T getValue() {
		if (isDeleted()) {
			return null;
		}

		if (getWidget().getData() == null || getWidget().getData().isUnset()) {
			if (getDynamicModel() != null) {
				logger.fine("Get dynamic model value: " + getDynamicModel().getData());
				return getDynamicModel().getData();
			} else {
				return null;
			}
		}

		if (getDataObject() == null) {
			return null;
		}

		Object value = null;

		try {
			value = getWidget().getData().getBindingValue(getController());
			T returned = (T) value;
			if (getDynamicModel() != null) {
				getDynamicModel().setData(returned);
			}
			return returned;
		} catch (InvalidObjectSpecificationException e) {
			logger.warning("Widget " + getWidget() + " InvalidObjectSpecificationException: " + e.getMessage());
			return null;
		} catch (TypeMismatchException e) {
			logger.warning("Widget " + getWidget() + " TypeMismatchException: " + e.getMessage());
			return null;
		} catch (NullReferenceException e) {
			// logger.warning("Widget " + getWidget() + " NullReferenceException: " + e.getMessage());
			return null;
		}

	}

	public void setValue(T aValue) {
		if (!isEnabled()) {
			return;
		}

		if (isDeleted()) {
			return;
		}

		if (getDynamicModel() != null) {
			logger.fine("Sets dynamic model value with " + aValue + " for " + getComponent());
			getDynamicModel().setData(aValue);
		} else {
			logger.fine("Dynamic model is null for " + getComponent());
		}

		if (getWidget().getData() == null || getWidget().getData().isUnset()) {
		} else {
			if (getDataObject() == null) {
				return;
			}
			try {
				getWidget().getData().setBindingValue(aValue, getController());
			} catch (AccessorInvocationException e) {
				getController().handleException(e.getCause());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}

		}

		updateDependancies(new Vector<FIBComponent>());
		/*
		 * Iterator<FIBComponent> it = getWidget().getMayAltersIterator(); while(it.hasNext()) { FIBComponent c = it.next();
		 * logger.info("Modified "+aValue+" now update "+c); getController().viewForComponent(c).update(); }
		 */

		if (getWidget().getValueChangedAction().isValid()) {
			try {
				getWidget().getValueChangedAction().execute(getController());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
		}

	}

	private synchronized void updateDependingObjects() {

		if (dependingObjects == null) {
			dependingObjects = new DependingObjects(this);
		}
		dependingObjects.refreshObserving(getController() /*
															* ,getWidget().getName() != null &&
															* getWidget().getName().equals("InspectorPropertyTable")
															*/);
	}

	@Override
	public void update(Observable o, Object arg) {
		// System.out.println("Widget "+getWidget()+" : receive notification "+o);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(new Vector<FIBComponent>());
				}
			});
		} else {
			update(new Vector<FIBComponent>());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Widget "+getWidget()+" : propertyChange "+evt);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update(new Vector<FIBComponent>());
				}
			});
		} else {
			update(new Vector<FIBComponent>());
		}
	}

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
	public List<DataBinding<?>> getDependencyBindings() {
		List<DataBinding<?>> returned = new ArrayList<DataBinding<?>>();
		returned.add(getWidget().getData());
		returned.add(getWidget().getVisible());
		returned.add(getWidget().getEnable());
		return returned;
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
	@Override
	public boolean update(List<FIBComponent> callers) {
		try {
			if (!super.update(callers)) {
				return false;
			}
			updateEnability();
			// logger.info("Updating "+getWidget()+" value="+getValue());

			// Add the component to the list of callers to avoid loops
			callers.add(getComponent());

			if (isComponentVisible()) {
				updateDynamicTooltip();
				updateDependingObjects();
				if (updateWidgetFromModel()) {
					updateDependancies(callers);
				}
			} else if ((dependingObjects == null || !dependingObjects.areDependingObjectsComputed()) && checkValidDataPath()) {
				// Even if the component is not visible, its visibility may depend
				// it self from some depending component (which in that situation,
				// are very important to know, aren'they ?)
				updateDependingObjects();
			}
			return true;
		} catch (Exception e) {
			logger.warning("Unexpected exception while updating FIBWidgetView: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	protected void updateDependancies(List<FIBComponent> callers) {
		if (getController() == null) {
			return;
		}
		// logger.info("updateDependancies() for " + getWidget());
		Iterator<FIBComponent> it = getWidget().getMayAltersIterator();
		while (it.hasNext()) {
			FIBComponent c = it.next();
			// logger.info("###### Component " + getWidget() + ", has been updated, now update " + c);
			FIBView<?, ?> v = getController().viewForComponent(c);
			if (v != null) {
				v.update(callers);
			} else {
				logger.warning("Cannot find FIBView for component " + c);
			}
		}
		// logger.info("END updateDependancies() for " + getWidget());
	}

	@Override
	public void updateDataObject(final Object aDataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(aDataObject);
				}
			});
			return;
		}
		update(new Vector<FIBComponent>());
	}

	@Override
	public void updateLanguage() {
		if (getValue() != null && getValue().getClass().isEnum() && getWidget().getLocalize()) {
			for (Object o : getValue().getClass().getEnumConstants()) {
				getStringRepresentation(o);
			}
		}
	}

	/**
	 * Return the effective base component to be added to swing hierarchy This component may be encapsulated in a JScrollPane
	 * 
	 * @return JComponent
	 */
	@Override
	public abstract JComponent getJComponent();

	/**
	 * Return the dynamic JComponent, ie the component on which dynamic is applied, and were actions are effective
	 * 
	 * @return J
	 */
	@Override
	public abstract J getDynamicJComponent();

	public boolean isReadOnly() {
		return getWidget().getReadOnly();
	}

	public final boolean isWidgetEnabled() {
		return isComponentEnabled();
	}

	public final boolean isComponentEnabled() {
		boolean componentEnabled = true;
		if (getComponent().getReadOnly()) {
			return false;
		}
		if (getComponent().getEnable() != null && getComponent().getEnable().isValid()) {
			try {
				Boolean isEnabled = getComponent().getEnable().getBindingValue(getController());
				if (isEnabled != null) {
					componentEnabled = isEnabled;
				}
			} catch (TypeMismatchException e) {
				e.printStackTrace();
				componentEnabled = true;
			} catch (NullReferenceException e) {
				// NullReferenceException is allowed, in this case, default enability is true
				componentEnabled = true;
			}
		}
		return componentEnabled;
	}

	private void updateDynamicTooltip() {
		if (getComponent().getTooltip() != null && getComponent().getTooltip().isValid()) {
			try {
				String tooltipText = getComponent().getTooltip().getBindingValue(getController());
				getDynamicJComponent().setToolTipText(tooltipText);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
		}
	}

	public String getStringRepresentation(final Object value) {
		if (value == null) {
			return "";
		}
		if (getWidget().getFormat() != null && getWidget().getFormat().isValid()) {
			formatter.setValue(value);
			String returned = null;
			try {
				returned = getWidget().getFormat().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
			if (getWidget().getLocalize() && returned != null) {
				return getLocalized(returned);
			} else {
				return returned;
			}
		}
		if (value instanceof String) {
			if (getWidget().getLocalize()) {
				return getLocalized((String) value);
			}
		}
		return value.toString();
	}

	public Icon getIconRepresentation(final Object value) {
		if (value == null) {
			return null;
		}
		if (getWidget().getIcon() != null && getWidget().getIcon().isValid()) {
			formatter.setValue(value);
			try {
				return getWidget().getIcon().getBindingValue(formatter);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void applySingleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
	}

	public void applyDoubleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getDoubleClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
	}

	public void applyRightClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		try {
			getWidget().getRightClickAction().execute(eventListener);
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public FIBComponentDynamicModel<T> createDynamicModel() {
		if (getWidget().getManageDynamicModel()) {
			return (FIBComponentDynamicModel<T>) super.createDynamicModel();
		}
		return null;
	}

	@Override
	public FIBComponentDynamicModel<T> getDynamicModel() {
		return super.getDynamicModel();
	}

	@Override
	public T getDefaultData() {
		return null;
	}

	@Override
	public void updateFont() {
		if (getFont() != null) {
			getDynamicJComponent().setFont(getFont());
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public final void updateEnability() {
		if (isComponentEnabled()) {
			if (!enabled) {
				// Becomes enabled
				logger.fine("Component becomes enabled");
				// System.out.println("Component  becomes enabled "+getJComponent());
				enableComponent(getJComponent());
				enabled = true;
			}
		} else {
			if (enabled) {
				// Becomes disabled
				logger.fine("Component becomes disabled");
				// System.out.println("Component  becomes disabled "+getJComponent());
				disableComponent(getJComponent());
				enabled = false;
			}
		}
	}

	private void enableComponent(Component component) {
		component.setEnabled(true);
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				enableComponent(c);
			}
		}
	}

	private void disableComponent(Component component) {
		component.setEnabled(false);
		if (component instanceof Container) {
			for (Component c : ((Container) component).getComponents()) {
				disableComponent(c);
			}
		}
	}

	protected class DynamicFormatter implements BindingEvaluationContext {
		private Object value;

		private void setValue(Object aValue) {
			value = aValue;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("object")) {
				return value;
			} else {
				return getController().getValue(variable);
			}
		}
	}

	protected class DynamicEventListener implements BindingEvaluationContext {
		private MouseEvent mouseEvent;

		private void setEvent(MouseEvent mouseEvent) {
			this.mouseEvent = mouseEvent;
		}

		@Override
		public Object getValue(BindingVariable variable) {
			if (variable.getVariableName().equals("event")) {
				return mouseEvent;
			} else {
				return getController().getValue(variable);
			}
		}
	}

}
