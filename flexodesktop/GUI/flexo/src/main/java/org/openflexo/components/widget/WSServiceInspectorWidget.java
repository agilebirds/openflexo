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

import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WidgetFocusListener;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSServiceInspectorWidget extends CustomInspectorWidget<WSService> {

	protected static final Logger logger = Logger.getLogger(WSServiceInspectorWidget.class.getPackage().getName());

	protected WSServiceSelector _selector;

	public WSServiceInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		int groupType = 3;
		if (hasValueForParameter("wsServiceType")) {
			String type = getValueForParameter("wsServiceType");
			if (type.equals("INTERNAL")) {
				groupType = WSServiceSelector.INTERNAL_WSService;
			} else if (type.equals("EXTERNAL")) {
				groupType = WSServiceSelector.EXTERNAL_WSService;
			}
		}
		_selector = new WSServiceSelector(null, null, groupType) {
			@Override
			public void apply() {
				super.apply();
				updateModelFromWidget();
			}

			@Override
			public void cancel() {
				super.cancel();
				updateModelFromWidget();
			}

			@Override
			public boolean isSelectable(FlexoObject object) {
				boolean returned = super.isSelectable(object);
				/*    if (returned) {
				        if (hasValueForParameter("isSelectable")) {
				            return getBooleanParameteredValue(getValueForParameter("isSelectable"),object);
				        }
				    }*/
				return returned;
			}
		};
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus gained in " + getClass().getName());
				}
				super.focusGained(arg0);
				_selector.getTextField().requestFocusInWindow();
				_selector.getTextField().selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Focus lost in " + getClass().getName());
				}
				super.focusLost(arg0);
			}
		});
	}

	@Override
	public Class getDefaultType() {
		return WSService.class;
	}

	@Override
	public synchronized void updateWidgetFromModel() {
		_selector.setEditedObject(getObjectValue());
		_selector.setRevertValue(getObjectValue());
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget() {
		setObjectValue(_selector.getEditedObject());
		super.updateModelFromWidget();
	}

	@Override
	public JComponent getDynamicComponent() {
		return _selector;
	}

	@Override
	public void setProject(FlexoProject aProject) {
		super.setProject(aProject);
		_selector.setProject(aProject);
	}

	/*protected void setRepository(DMRepository repository)
	{
	    if (logger.isLoggable(Level.FINE))
	        logger.fine("Setting repository to " + repository);
	    _selector.setRootObject(repository);
	}*/

	@Override
	protected void performModelUpdating(InspectableObject value) {
	}

	@Override
	public void fireEditingCanceled() {
		if (_selector != null) {
			_selector.closePopup();
		}
	}

	@Override
	public void fireEditingStopped() {
		if (_selector != null) {
			_selector.closePopup();
		}
	}

}
