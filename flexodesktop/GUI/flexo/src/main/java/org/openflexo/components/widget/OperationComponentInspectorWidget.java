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

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OperationComponentInspectorWidget extends AbstractComponentInspectorWidget {

	protected static final Logger logger = Logger.getLogger(OperationComponentInspectorWidget.class.getPackage().getName());

	public OperationComponentInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
	}

	@Override
	protected AbstractComponentSelector createComponentSelector() {
		return new OperationComponentSelector(null, null) {
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
			public void newComponent() {
				super.newComponent();
				updateModelFromWidget();
			}

		};
	}

	@Override
	public Class getDefaultType() {
		return OperationComponentDefinition.class;
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
