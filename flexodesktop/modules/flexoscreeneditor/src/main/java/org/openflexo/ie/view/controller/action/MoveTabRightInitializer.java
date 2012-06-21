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
import org.openflexo.foundation.ie.action.MoveTabRight;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.ie.view.widget.DropTabZone;
import org.openflexo.ie.view.widget.IETabWidgetView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MoveTabRightInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveTabRightInitializer(IEControllerActionInitializer actionInitializer) {
		super(MoveTabRight.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveTabRight> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveTabRight>() {
			@Override
			public boolean run(EventObject e, MoveTabRight action) {
				IETabWidget tab = null;
				if (action.getFocusedObject() instanceof IETabWidget) {
					tab = ((IETabWidget) action.getFocusedObject());
				} else if (action.getInvoker() instanceof DropTabZone) {
					DropTabZone invoker = (DropTabZone) action.getInvoker();
					if (invoker.getSelectedComponent() != null) {
						if (invoker.getSelectedComponent() instanceof IETabWidgetView) {
							tab = ((IETabWidgetView) invoker.getSelectedComponent()).getTabWidget();
						}
					}
				}
				if (tab != null) {
					(action).setSelectedTab(tab);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveTabRight> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveTabRight>() {
			@Override
			public boolean run(EventObject e, MoveTabRight action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return UtilsIconLibrary.MOVE_RIGHT_ICON;
	}

}
