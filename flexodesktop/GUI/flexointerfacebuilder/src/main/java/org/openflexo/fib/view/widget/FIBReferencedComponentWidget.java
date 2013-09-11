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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.controller.FIBReferencedComponentDynamicModel;
import org.openflexo.fib.controller.FIBViewFactory;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBReferencedComponent;
import org.openflexo.fib.model.FIBReferencedComponent.FIBReferenceAssignment;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBContainerView;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;

/**
 * Defines an abstract custom widget
 * 
 * @author sguerin
 * 
 */
public class FIBReferencedComponentWidget extends FIBWidgetView<FIBReferencedComponent, JComponent, Object> implements
		BindingEvaluationContext {

	private static final Logger logger = Logger.getLogger(FIBReferencedComponentWidget.class.getPackage().getName());

	private FIBComponent referencedComponent = null;
	private FIBView<FIBComponent, JComponent> referencedComponentView;
	private FIBViewFactory factory;
	private boolean isComponentLoading = false;

	private JLabel NOT_FOUND_LABEL = new JLabel("<Not found component>");

	public FIBReferencedComponentWidget(FIBReferencedComponent model, FIBController controller, FIBViewFactory factory) {
		super(model, controller);
		this.factory = factory;
		updateFont();
		NOT_FOUND_LABEL = new JLabel("Component Not Found " + model.getName());
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	public FIBComponent getReferencedComponent() {

		if (referencedComponent == null) {
			referencedComponent = retrieveReferencedComponent();
		}
		return referencedComponent;
	}

	private FIBComponent retrieveReferencedComponent() {

		if (getWidget().getDynamicComponentFile() != null && getWidget().getDynamicComponentFile().isSet()
				&& getWidget().getDynamicComponentFile().isValid()) {
			// The component file is dynamically defined, use it
			File componentFile;
			try {
				componentFile = getWidget().getDynamicComponentFile().getBindingValue(getBindingEvaluationContext());
				if (componentFile != null) {
					return FIBLibrary.instance().retrieveFIBComponent(componentFile);
				}
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		else if (getWidget().getComponentFile() != null) {
			// The component file is statically defined, use it
			return FIBLibrary.instance().retrieveFIBComponent(getWidget().getComponentFile());
		}

		return null;
	}

	@Override
	public final void updateDataObject(final Object dataObject) {
		if (!SwingUtilities.isEventDispatchThread()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Update data object invoked outside the EDT!!! please investigate and make sure this is no longer the case. \n\tThis is a very SERIOUS problem! Do not let this pass.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateDataObject(dataObject);
				}
			});
			return;
		}
		updateDynamicallyReferencedComponentWhenRequired();
		super.updateDataObject(dataObject);
	}

	private void updateDynamicallyReferencedComponentWhenRequired() {
		// We now check that the referenced component is still valid
		if (referencedComponent != retrieveReferencedComponent()) {
			// We have detected that referenced component has changed
			// We reset internal values and call updateLayout on the container
			referencedComponent = null;
			if (referencedComponentView != null) {
				referencedComponentView.delete();
				referencedComponentView = null;
			}
			((FIBContainerView<?, ?>) getParentView()).updateLayout();
		}
	}

	public FIBView<FIBComponent, JComponent> getReferencedComponentView() {
		if (referencedComponentView == null && !isComponentLoading) {
			isComponentLoading = true;
			// System.out.println(">>>>>>> Making new FIBView for " + getWidget() + " for " + getWidget().getComponent());

			FIBComponent loaded = getReferencedComponent();

			if (loaded instanceof FIBWidget) {
				referencedComponentView = factory.makeWidget((FIBWidget) loaded);
				referencedComponentView.setEmbeddingComponent(this);
			} else if (loaded instanceof FIBContainer) {
				referencedComponentView = factory.makeContainer((FIBContainer) loaded);
				referencedComponentView.setEmbeddingComponent(this);
			}
			isComponentLoading = false;
		}
		return referencedComponentView;
	}

	@Override
	public synchronized JComponent getJComponent() {
		if (getReferencedComponentView() != null) {
			JComponent returned = getReferencedComponentView().getJComponent();
			/*if (returned != null && getWidget().getOpaque() != null) {
				returned.setOpaque(getWidget().getOpaque());
			}*/
			return returned;
		}
		return NOT_FOUND_LABEL;
	}

	@Override
	public JComponent getDynamicJComponent() {
		return getJComponent();
	}

	private void performAssignments() {
		for (FIBReferenceAssignment assign : getWidget().getAssignments()) {
			DataBinding<?> variableDB = assign.getVariable();
			DataBinding<?> valueDB = assign.getValue();
			if (valueDB != null && valueDB.isValid()) {
				Object value = null;
				try {
					value = valueDB.getBindingValue(getBindingEvaluationContext());
					if (variableDB.isValid()) {
						// System.out.println("Assignment " + assign + " set value with " + value);
						variableDB.setBindingValue(value, this);
					}
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NotSettableContextException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean updateWidgetFromModel() {
		// We need here to "force" update while some assignments may be required

		// if (notEquals(getValue(), customComponent.getEditedObject())) {

		/*if (getWidget().getComponentClass().getName().endsWith("FIBForegroundStyleSelector")) {
			logger.info("GET updateWidgetFromModel() with " + getValue() + " for " + customComponent);
		}*/

		if (getReferencedComponentView() != null) {

			// getReferencedComponentView().getDynamicModel().setData(getValue());

			performAssignments();
			// logger.info("********* Update FIBReferenceComponentWidget " + getWidget().getComponentFile() + " with " + getValue());
			// logger.info("getData()=" + getWidget().getData());
			// logger.info("valid=" + getWidget().getData().isValid());
			referencedComponentView.getDynamicModel().setData(getValue());
			referencedComponentView.updateDataObject(getValue());
		}
		return true;

	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("data")) {
			/*if (getWidget().getName() != null && getWidget().getName().equals("DropSchemePanel")) {
				System.out.println("Returns data for DropSchemePanel as " + referencedComponentView.getDynamicModel().getData());
			}
			if (getWidget() != null && getWidget().getName() != null && getWidget().getName().equals("DropSchemeWidget")) {
				System.out.println("Returns data for DropSchemeWidget as " + referencedComponentView.getDynamicModel().getData());
			}*/
			if (referencedComponentView != null) {
				return referencedComponentView.getDynamicModel().getData();
			}
			return null;
		}
		if (variable.getVariableName().equals("component")) {
			return referencedComponentView.getComponent();
		}
		BindingEvaluationContext evCtxt = getBindingEvaluationContext();
		// NPE Protection
		if (evCtxt != null && variable != null){
			return getBindingEvaluationContext().getValue(variable);
		}
		else {
			return null;
		}
	}

	@Override
	public FIBReferencedComponentDynamicModel createDynamicModel() {
		return new FIBReferencedComponentDynamicModel(null, getComponent());
	}

	public void updateComponent() {
		referencedComponentView = null;
		logger.info("Updating component not implemented yet");
		getParentView().update(new ArrayList<FIBComponent>());
		((FIBContainerView) getParentView()).updateLayout();
		getParentView().updateDataObject(getValue());
	}

	public BindingEvaluationContext getEmbeddedBindingEvaluationContext() {
		return this;
	}

	@Override
	public String toString() {
		if (referencedComponentView != null) {
			return super.toString() + " referencedComponentView=" + referencedComponentView + " dynamicModel="
					+ referencedComponentView.getDynamicModel() + " data=" + referencedComponentView.getDynamicModel().getData();
		} else {
			return super.toString();
		}
	}
}
