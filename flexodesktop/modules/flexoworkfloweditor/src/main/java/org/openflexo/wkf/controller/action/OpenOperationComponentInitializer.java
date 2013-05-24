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
package org.openflexo.wkf.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.action.OpenOperationComponent;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class OpenOperationComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenOperationComponentInitializer(WKFControllerActionInitializer actionInitializer) {
		super(OpenOperationComponent.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenOperationComponent> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenOperationComponent>() {
			@Override
			public boolean run(EventObject e, OpenOperationComponent action) {
				if (action.getFocusedObject() instanceof OperationNode) {
					OperationNode operationNode = action.getFocusedObject();
					return operationNode.getComponentInstance() != null;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenOperationComponent> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenOperationComponent>() {
			@Override
			public boolean run(EventObject e, OpenOperationComponent action) {
				if (action.getFocusedObject() instanceof OperationNode) {
					OperationNode operationNode = action.getFocusedObject();
					if (operationNode.getComponentInstance().getComponentDefinition() != null) {
						getEditor().focusOn(operationNode.getComponentInstance());
					}
				}
				return false;
			}
		};
	}

}
