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
package org.openflexo.components.widget;

import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.CheckBoxWidget;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.DoubleWidget;
import org.openflexo.inspector.widget.IntegerWidget;
import org.openflexo.inspector.widget.TextFieldWidget;

/**
 * Please comment this class
 * 
 * @author gpolet
 * 
 */
@Deprecated
// TODO Please refactor this widget according to new FIB Technology
public class MetricsValueInspectorWidget extends CustomInspectorWidget<MetricsValue> implements
		FIBCustomComponent<MetricsValue, JComponent> {

	protected static final Logger logger = Logger.getLogger(MetricsValueInspectorWidget.class.getPackage().getName());

	protected DenaliWidget<? extends Object> currentWidget;

	private DurationInspectorWidget durationWidget;
	private IntegerWidget integerWidget;
	private DoubleWidget doubleWidget;
	private TextFieldWidget textFieldWidget;
	private CheckBoxWidget checkBoxWidget;

	public MetricsValueInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		PropertyModel localStringModel = new PropertyModel();
		localStringModel._tabModelName = model._tabModelName;
		localStringModel.name = "stringValue";
		PropertyModel localDurationModel = new PropertyModel();
		localDurationModel._tabModelName = model._tabModelName;
		localDurationModel.name = "durationValue";
		PropertyModel localIntModel = new PropertyModel();
		localIntModel._tabModelName = model._tabModelName;
		localIntModel.name = "intValue";
		PropertyModel localDoubleModel = new PropertyModel();
		localDoubleModel._tabModelName = model._tabModelName;
		localDoubleModel.name = "doubleValue";
		PropertyModel localBooleanModel = new PropertyModel();
		localBooleanModel._tabModelName = model._tabModelName;
		localBooleanModel.name = "booleanValue";
		durationWidget = new DurationInspectorWidget(localDurationModel, controller);
		integerWidget = new IntegerWidget(localIntModel, controller);
		doubleWidget = new DoubleWidget(localDoubleModel, controller);
		textFieldWidget = new TextFieldWidget(localStringModel, controller);
		checkBoxWidget = new CheckBoxWidget(localBooleanModel, controller);
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
	}

	@Override
	public Class getDefaultType() {
		return MetricsValue.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		if (currentWidget != null) {
			currentWidget.updateWidgetFromModel();
		}
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		if (currentWidget != null) {
			currentWidget.updateModelFromWidget();
		}
		super.updateModelFromWidget();
	}

	private void updateWidget() {
		if (getObjectValue() != null) {
			switch (getObjectValue().getMetricsDefinition().getType()) {
			case TEXT:
				currentWidget = textFieldWidget;
				break;
			case NUMBER:
				currentWidget = integerWidget;
				break;
			case DOUBLE:
				currentWidget = doubleWidget;
				break;
			case TIME:
				currentWidget = durationWidget;
				break;
			case TRUE_FALSE:
				currentWidget = checkBoxWidget;
				break;
			}
		}
	}

	@Override
	public void switchObserved(InspectableObject inspectable) {
		super.switchObserved(inspectable);
		updateWidget();
		if (currentWidget != null) {
			currentWidget.switchObserved(inspectable);
		}
	}

	@Override
	public void setModel(InspectableObject value) {
		if (value == getModel()) {
			return;
		}
		super.setModel(value);
		updateWidget();
		if (currentWidget != null) {
			currentWidget.switchObserved(value);
		}
	}

	@Override
	public JComponent getDynamicComponent() {
		if (currentWidget != null) {
			return currentWidget.getDynamicComponent();
		}
		return null;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		super.setProject(aProject);
	}

	@Override
	public void fireEditingCanceled() {
		if (currentWidget == durationWidget) {
			durationWidget.fireEditingCanceled();
		}
	}

	@Override
	public void fireEditingStopped() {
		if (currentWidget == durationWidget) {
			durationWidget.fireEditingStopped();
		}
	}

	@Override
	public boolean disableTerminateEditOnFocusLost() {
		return true;
	}

	@Override
	public JComponent getJComponent() {
		return getDynamicComponent();
	}

	@Override
	public MetricsValue getEditedObject() {
		return getEditedValue();
	}

	@Override
	public void setEditedObject(MetricsValue object) {
		setEditedValue(object);
	}

	@Override
	public MetricsValue getRevertValue() {
		// Not handled here
		return null;
	}

	@Override
	public void setRevertValue(MetricsValue object) {
		// Not handled here
	}

	@Override
	public void addApplyCancelListener(org.openflexo.swing.CustomPopup.ApplyCancelListener l) {
		// Not handled here
	}

	@Override
	public void removeApplyCancelListener(org.openflexo.swing.CustomPopup.ApplyCancelListener l) {
		// Not handled here
	}

	@Override
	public Class<MetricsValue> getRepresentedType() {
		return getDefaultType();
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

}