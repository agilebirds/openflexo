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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DependingObjects;
import org.openflexo.antar.binding.DependingObjects.HasDependencyBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.DataBinding;
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
		try {
			T returned = (T) getWidget().getData().getBindingValue(getController());
			if (getDynamicModel() != null) {
				getDynamicModel().setData(returned);
			}
			return returned;
		} catch (InvalidObjectSpecificationException e) {
			logger.warning("Widget " + getWidget() + " InvalidObjectSpecificationException: " + e.getMessage());
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
			}

		}

		updateDependancies();
		/*
		 * Iterator<FIBComponent> it = getWidget().getMayAltersIterator(); while(it.hasNext()) { FIBComponent c = it.next();
		 * logger.info("Modified "+aValue+" now update "+c); getController().viewForComponent(c).update(); }
		 */

		if (getWidget().getValueChangedAction().isValid()) {
			getWidget().getValueChangedAction().execute(getController());
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
					update();
				}
			});
		} else {
			update();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("Widget "+getWidget()+" : propertyChange "+evt);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					update();
				}
			});
		} else {
			update();
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

	protected void appendToDependingObjects(DataBinding binding, List<AbstractBinding> returned) {
		if (binding.isSet()) {
			returned.add(binding.getBinding());
		}
	}

	@Override
	public List<AbstractBinding> getDependencyBindings() {
		List<AbstractBinding> returned = new ArrayList<AbstractBinding>();
		appendToDependingObjects(getWidget().getData(), returned);
		appendToDependingObjects(getWidget().getVisible(), returned);
		appendToDependingObjects(getWidget().getEnable(), returned);
		return returned;
	}

	@Override
	public void update() {
		try {
			super.update();
			updateEnability();
			// logger.info("Updating "+getWidget()+" value="+getValue());
			if (isComponentVisible()) {
				updateDynamicTooltip();
				updateDependingObjects();
				if (updateWidgetFromModel()) {
					updateDependancies();
				}
			} else if ((dependingObjects == null || !dependingObjects.areDependingObjectsComputed()) && checkValidDataPath()) {
				// Even if the component is not visible, its visibility may depend
				// it self from some depending component (which in that situation,
				// are very important to know, aren'they ?)
				updateDependingObjects();
			}
		} catch (Exception e) {
			logger.warning("Unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void updateDependancies() {
		if (getController() == null) {
			return;
		}
		// logger.info("updateDependancies() for " + getWidget());
		Iterator<FIBComponent> it = getWidget().getMayAltersIterator();
		while (it.hasNext()) {
			FIBComponent c = it.next();
			// logger.info("###### Component " + getWidget() + ", has been updated, now update " + c);
			FIBView v = getController().viewForComponent(c);
			if (v != null) {
				v.update();
			} else {
				logger.warning("Cannot find FIBView for component " + c);
			}
		}
		// logger.info("END updateDependancies() for " + getWidget());
	}

	@Override
	public void updateDataObject(Object aDataObject) {
		update();
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
			Object isEnabled = getComponent().getEnable().getBindingValue(getController());
			if (isEnabled instanceof Boolean) {
				componentEnabled = (Boolean) isEnabled;
			}
		}
		return componentEnabled;
	}

	private void updateDynamicTooltip() {
		if (getComponent().getTooltip() != null && getComponent().getTooltip().isValid()) {
			String tooltipText = (String) getComponent().getTooltip().getBindingValue(getController());
			getDynamicJComponent().setToolTipText(tooltipText);
		}
	}

	public String getStringRepresentation(final Object value) {
		if (value == null) {
			return "";
		}
		if (getWidget().getFormat() != null && getWidget().getFormat().isValid()) {
			formatter.setValue(value);
			String returned = (String) getWidget().getFormat().getBindingValue(formatter);
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
			return (Icon) getWidget().getIcon().getBindingValue(formatter);
		}
		return null;
	}

	public void applySingleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		getWidget().getClickAction().execute(eventListener);
	}

	public void applyDoubleClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		getWidget().getDoubleClickAction().execute(eventListener);
	}

	public void applyRightClickAction(MouseEvent event) {
		eventListener.setEvent(event);
		getWidget().getRightClickAction().execute(eventListener);
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
