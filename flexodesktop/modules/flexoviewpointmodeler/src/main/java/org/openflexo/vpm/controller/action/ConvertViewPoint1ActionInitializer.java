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
package org.openflexo.vpm.controller.action;

import java.io.FileNotFoundException;
import java.util.EventObject;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.action.ConvertViewPoint1Action;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ConvertViewPoint1ActionInitializer extends ActionInitializer<ConvertViewPoint1Action, FlexoObject, FlexoObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
			.getLogger(ConvertViewPoint1ActionInitializer.class.getPackage().getName());

	public ConvertViewPoint1ActionInitializer(ControllerActionInitializer actionInitializer) {
		super(ConvertViewPoint1Action.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<ConvertViewPoint1Action> getDefaultInitializer() {
		return new FlexoActionInitializer<ConvertViewPoint1Action>() {
			@Override
			public boolean run(EventObject e, ConvertViewPoint1Action action) {
				return FlexoController.confirm(FlexoLocalization
						.localizedForKey("would_you_really_like_to_convert_viewpoint_(undoable_action)?"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ConvertViewPoint1Action> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ConvertViewPoint1Action>() {
			@Override
			public boolean run(EventObject e, ConvertViewPoint1Action action) {
				try {
					ViewPoint loadedData = ((ViewPointResource) action.getFocusedObject()).getResourceData(null);
					getController().setCurrentEditedObjectAsModuleView(loadedData);
					getController().getSelectionManager().setSelectedObject(loadedData);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ResourceLoadingCancelledException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ResourceDependencyLoopException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FlexoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return true;
			}
		};
	}

}
