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
package org.openflexo.view.controller.action;

import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.EventObject;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.LoadResourceAction;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class LoadResourceActionInitializer extends ActionInitializer<LoadResourceAction, FlexoObject, FlexoObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(LoadResourceActionInitializer.class
			.getPackage().getName());

	public LoadResourceActionInitializer(ControllerActionInitializer actionInitializer) {
		super(LoadResourceAction.actionType, actionInitializer);
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_R, FlexoCst.META_MASK);
	}

	@Override
	protected FlexoActionInitializer<LoadResourceAction> getDefaultInitializer() {
		return new FlexoActionInitializer<LoadResourceAction>() {
			@Override
			public boolean run(EventObject e, LoadResourceAction action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<LoadResourceAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<LoadResourceAction>() {
			@Override
			public boolean run(EventObject e, LoadResourceAction action) {
				try {
					FlexoObject loadedData = (FlexoObject) ((FlexoResource) action.getFocusedObject()).getResourceData(null);
					getController().setCurrentEditedObjectAsModuleView(loadedData);
					getController().getSelectionManager().setSelectedObject(loadedData);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ResourceLoadingCancelledException e1) {
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
