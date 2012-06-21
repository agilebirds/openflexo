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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.ie.action.DecreaseColSpan;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.InvalidOperation;
import org.openflexo.ie.view.widget.IETDWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DecreaseColSpanInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DecreaseColSpanInitializer(IEControllerActionInitializer actionInitializer) {
		super(DecreaseColSpan.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DecreaseColSpan> getDefaultInitializer() {
		return new FlexoActionInitializer<DecreaseColSpan>() {
			@Override
			public boolean run(EventObject e, DecreaseColSpan action) {
				if (action.getInvoker() instanceof IETDWidgetView
						&& ((IETDWidgetView) action.getInvoker()).getSequenceModel().getParent() instanceof IETDWidget) {
					IETDWidgetView invoker = (IETDWidgetView) action.getInvoker();
					(action).setSelectedTD(invoker.td());
					return true;
				} else if (action.getFocusedObject() instanceof IETDWidget) {
					(action).setSelectedTD((IETDWidget) action.getFocusedObject());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DecreaseColSpan> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DecreaseColSpan>() {
			@Override
			public boolean run(EventObject e, DecreaseColSpan action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<DecreaseColSpan> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<DecreaseColSpan>() {
			@Override
			public boolean handleException(FlexoException exception, DecreaseColSpan action) {
				FlexoController.notify(FlexoLocalization.localizedForKey("invalid_operation:")
						+ FlexoLocalization.localizedForKey(((InvalidOperation) exception).getMessage()));
				return false;
			}
		};
	}
}
