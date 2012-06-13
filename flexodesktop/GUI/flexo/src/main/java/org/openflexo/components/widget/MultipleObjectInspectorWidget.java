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

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.DefaultBrowserConfiguration.ObjectVisibilityDelegate;
import org.openflexo.components.browser.DefaultBrowserElementFactory;
import org.openflexo.components.widget.MultipleObjectSelector.ObjectSelectabilityDelegate;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.ParamModel;
import org.openflexo.inspector.model.PropertyModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class MultipleObjectInspectorWidget<E extends FlexoModelObject> extends CustomInspectorWidget<Vector<E>> implements
		ObjectVisibilityDelegate, ObjectSelectabilityDelegate<E> {

	protected static final Logger logger = Logger.getLogger(MultipleObjectInspectorWidget.class.getPackage().getName());

	protected MultipleObjectSelector<E> _selector;

	public MultipleObjectInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		_selector = new MultipleObjectSelector<E>(null, this, this, null) {
			@Override
			public void notifySelectionChanged() {
				super.notifySelectionChanged();
				updateModelFromWidget();
			}
		};

		if (model.hasValueForParameter("isSelectable")) {
			ParamModel selectabilityParams = model.valueForParameter("isSelectable");
			for (String key : selectabilityParams.parameters.keySet()) {
				logger.info("Selectability for " + BrowserElementType.valueOf(key) + "  is "
						+ Boolean.valueOf(selectabilityParams.getValueForParameter(key)));
				setSelectability(BrowserElementType.valueOf(key), Boolean.valueOf(selectabilityParams.getValueForParameter(key)));
			}
		}

		if (model.hasValueForParameter("visibility")) {
			ParamModel visibilityParams = model.valueForParameter("visibility");
			for (String key : visibilityParams.parameters.keySet()) {
				logger.info("Visibility for " + BrowserElementType.valueOf(key) + " is "
						+ BrowserFilterStatus.valueOf(visibilityParams.getValueForParameter(key)));
				setVisibility(BrowserElementType.valueOf(key), BrowserFilterStatus.valueOf(visibilityParams.getValueForParameter(key)));
			}
			_selector.getModel().getConfiguration().configure(_selector.getModel());
		}

		/*  _selector = new DMRepositorySelector(null, null) {
		      public void apply()
		      {
		          super.apply();
		          updateModelFromWidget();
		      }

		      public void cancel()
		      {
		          super.cancel();
		          updateModelFromWidget();
		      }
		  };*/
		/*getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
		    public void focusGained(FocusEvent arg0)
		    {
		        if (logger.isLoggable(Level.FINE))
		            logger.fine("Focus gained in " + getClass().getName());
		        super.focusGained(arg0);
		        _selector.getTextField().requestFocus();
		        _selector.getTextField().selectAll();
		    }

		    public void focusLost(FocusEvent arg0)
		    {
		        if (logger.isLoggable(Level.FINE))
		            logger.fine("Focus lost in " + getClass().getName());
		        super.focusLost(arg0);
		    }
		});*/
	}

	@Override
	public Class getDefaultType() {
		return Vector.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		_selector.setSelectedObjects(getObjectValue());
		// _selector.setRevertValue(getObjectValue());
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		setObjectValue((Vector<E>) _selector.getSelectedObjects());
		super.updateModelFromWidget();
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	private FlexoProject _project;

	@Override
	public void setProject(FlexoProject aProject) {
		super.setProject(aProject);
		_selector.setProject(aProject);
		_project = aProject;
	}

	public MultipleObjectSelector getSelector() {
		return _selector;
	}

	@Override
	public void fireEditingCanceled() {
		// if (_selector != null) _selector.closePopup();
	}

	@Override
	public void fireEditingStopped() {
		// if (_selector != null) _selector.closePopup();
	}

	protected void setRootObject(FlexoModelObject newRootObject) {
		_selector.setRootObject(newRootObject);
	}

	@Override
	protected void performModelUpdating(InspectableObject value) {
		if (hasValueForParameter("rootObject") && _project != null) {
			setRootObject((FlexoModelObject) getDynamicValueForParameter("rootObject", _project));
		}
	}

	@Override
	public BrowserFilterStatus getVisibility(BrowserElementType elementType) {
		BrowserFilterStatus returned = visibilities.get(elementType);
		if (returned != null) {
			return returned;
		}
		return BrowserFilterStatus.SHOW;
	}

	@Override
	public boolean isSelectable(E object) {
		return isSelectable(DefaultBrowserElementFactory.DEFAULT_FACTORY.makeNewElement(object, null, null).getElementType());
	}

	public boolean isSelectable(BrowserElementType elementType) {
		Boolean returned = selectabilities.get(elementType);
		if (returned != null) {
			return returned;
		}
		return false;
	}

	@Override
	public boolean defaultShouldExpandVertically() {
		return true;
	}

	@Override
	public WidgetLayout getDefaultWidgetLayout() {
		return WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT;
	}

	private Hashtable<BrowserElementType, BrowserFilterStatus> visibilities = new Hashtable<BrowserElementType, BrowserFilterStatus>();

	private Hashtable<BrowserElementType, Boolean> selectabilities = new Hashtable<BrowserElementType, Boolean>();

	public void setVisibility(BrowserElementType elementType, BrowserFilterStatus visibility) {
		visibilities.put(elementType, visibility);
	}

	public void setSelectability(BrowserElementType elementType, boolean selectable) {
		selectabilities.put(elementType, selectable);
	}

}