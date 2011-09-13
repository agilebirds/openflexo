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

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.OpenPortRegistery;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.controller.WKFController;


public class OpenPortRegisteryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenPortRegisteryInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(OpenPortRegistery.actionType,actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer()
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionVisibleCondition getVisibleCondition() {
		return new FlexoActionVisibleCondition<OpenPortRegistery, FlexoProcess, WKFObject>() {

			@Override
			public boolean isVisible(FlexoActionType<OpenPortRegistery, FlexoProcess, WKFObject> actionType, FlexoProcess object,
					Vector<WKFObject> globalSelection, FlexoEditor editor) {
				return getController().getCurrentFlexoProcess()==object;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenPortRegistery> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<OpenPortRegistery>() {
			@Override
			public boolean run(ActionEvent e, OpenPortRegistery action)
			{
				return true;
			}
		};
	}

}
