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
package org.openflexo.ie.view.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.TopComponentUp;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class TopComponentUpInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	TopComponentUpInitializer(IEControllerActionInitializer actionInitializer) {
		super(TopComponentUp.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<TopComponentUp> getDefaultInitializer() {
		return new FlexoActionInitializer<TopComponentUp>() {
			@Override
			public boolean run(EventObject e, TopComponentUp action) {
				boolean doable = false;
				IEWidget top = action.getFocusedObject();
				if (top.getParent() instanceof IESequence) {
					IESequence<IWidget> c = (IESequence<IWidget>) top.getParent();
					doable = c.indexOf(top) > 0;
					if (doable) {
						(action).setComponent(top);
					}
				}
				return doable;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<TopComponentUp> getDefaultFinalizer() {
		return new FlexoActionFinalizer<TopComponentUp>() {
			@Override
			public boolean run(EventObject e, TopComponentUp action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return UtilsIconLibrary.MOVE_UP_ICON;
	}

}
