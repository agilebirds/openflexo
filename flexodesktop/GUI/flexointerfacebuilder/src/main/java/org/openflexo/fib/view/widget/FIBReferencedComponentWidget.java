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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
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

	private FIBView<FIBComponent, JComponent> referencedComponentView;
	private FIBViewFactory factory;

	private final JLabel NOT_FOUND_LABEL = new JLabel("<Not found component>");

	public FIBReferencedComponentWidget(FIBReferencedComponent model, FIBController controller, FIBViewFactory factory) {
		super(model, controller);
		this.factory = factory;
		updateFont();
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		return false;
	}

	private boolean isComponentLoading = false;

	public FIBView<FIBComponent, JComponent> getReferencedComponentView() {
		if (referencedComponentView == null && !isComponentLoading) {
			isComponentLoading = true;
			if (getWidget().getComponent() instanceof FIBWidget) {
				referencedComponentView = factory.makeWidget((FIBWidget) getWidget().getComponent());
				referencedComponentView.setEmbeddingComponent(this);
			} else if (getWidget().getComponent() instanceof FIBContainer) {
				referencedComponentView = factory.makeContainer((FIBContainer) getWidget().getComponent());
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

			performAssignments();
			logger.info("Update FIBReferenceComponentWidget with " + getValue());
			referencedComponentView.getDynamicModel().setData(getValue());
			referencedComponentView.updateDataObject(getValue());

		}
		return true;

	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable.getVariableName().equals("data")) {
			return referencedComponentView.getDynamicModel().getData();
		}
		if (variable.getVariableName().equals("component")) {
			return referencedComponentView.getComponent();
		}
		return getController().getValue(variable);
	}

	@Override
	public FIBReferencedComponentDynamicModel createDynamicModel() {
		return new FIBReferencedComponentDynamicModel(null);
	}

	public void updateComponent() {
		referencedComponentView = null;
		logger.info("Updating component not implemented yet");
		getParentView().update(new ArrayList<FIBComponent>());
		((FIBContainerView) getParentView()).updateLayout();
		getParentView().updateDataObject(getValue());
	}

	@Override
	public BindingEvaluationContext getBindingEvaluationContext() {
		return this;
	}
}
