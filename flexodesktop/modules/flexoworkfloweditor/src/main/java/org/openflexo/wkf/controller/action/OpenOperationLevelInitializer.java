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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.processeditor.ProcessView;
import org.openflexo.wkf.swleditor.SwimmingLaneView;

public class OpenOperationLevelInitializer extends ActionInitializer<OpenOperationLevel, AbstractActivityNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenOperationLevelInitializer(WKFControllerActionInitializer actionInitializer) {
		super(OpenOperationLevel.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionFinalizer<OpenOperationLevel> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenOperationLevel>() {
			@Override
			public boolean run(EventObject e, OpenOperationLevel action) {
				OperationPetriGraph petriGraph = action.getFocusedObject().getOperationPetriGraph();
				if (petriGraph != null && petriGraph.getIsVisible()) {
					if (getController().getCurrentModuleView() instanceof ProcessView) {
						((ProcessView) getController().getCurrentModuleView()).getController().setObjectForPaletteSwitch(petriGraph);
					} else if (getController().getCurrentModuleView() instanceof SwimmingLaneView) {
						((SwimmingLaneView) getController().getCurrentModuleView()).getController().setObjectForPaletteSwitch(petriGraph);
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionVisibleCondition<OpenOperationLevel, AbstractActivityNode, WKFObject> getVisibleCondition() {
		return new FlexoActionVisibleCondition<OpenOperationLevel, AbstractActivityNode, WKFObject>() {

			@Override
			public boolean isVisible(FlexoActionType<OpenOperationLevel, AbstractActivityNode, WKFObject> actionType,
					AbstractActivityNode object, Vector<WKFObject> globalSelection, FlexoEditor editor) {
				return !UserType.isLite();
			}
		};
	}
}
