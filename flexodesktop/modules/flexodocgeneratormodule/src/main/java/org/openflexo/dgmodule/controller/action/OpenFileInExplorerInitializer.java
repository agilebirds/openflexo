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
package org.openflexo.dgmodule.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.OpenFileInExplorer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class OpenFileInExplorerInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenFileInExplorerInitializer(DGControllerActionInitializer actionInitializer) {
		super(OpenFileInExplorer.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenFileInExplorer> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenFileInExplorer>() {
			@Override
			public boolean run(EventObject e, OpenFileInExplorer action) {
				if (action.getFocusedObject() instanceof CGFile && ((CGFile) action.getFocusedObject()).getResource() != null) {
					action.setFileToOpen(((CGFile) action.getFocusedObject()).getResource().getFile());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenFileInExplorer> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenFileInExplorer>() {
			@Override
			public boolean run(EventObject e, OpenFileInExplorer action) {
				return true;
			}
		};
	}
}
