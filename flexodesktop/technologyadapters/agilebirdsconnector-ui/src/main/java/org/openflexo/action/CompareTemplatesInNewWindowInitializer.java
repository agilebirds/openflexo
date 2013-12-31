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
package org.openflexo.action;

import java.util.EventObject;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.action.CompareTemplatesInNewWindow;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.popups.FileDiffEditorPopup;

/**
 * @author gpolet
 * 
 */
public class CompareTemplatesInNewWindowInitializer extends ActionInitializer<CompareTemplatesInNewWindow, CGTemplate, CGTemplate> {

	/**
	 * @param actionType
	 * @param controllerActionInitializer
	 */
	public CompareTemplatesInNewWindowInitializer(ControllerActionInitializer controllerActionInitializer) {
		super(CompareTemplatesInNewWindow.actionType, controllerActionInitializer);
	}

	/**
	 * Overrides getDefaultInitializer
	 * 
	 * @see org.openflexo.view.controller.ActionInitializer#getDefaultInitializer()
	 */
	@Override
	protected FlexoActionInitializer<CompareTemplatesInNewWindow> getDefaultInitializer() {

		return new FlexoActionInitializer<CompareTemplatesInNewWindow>() {

			@Override
			public boolean run(EventObject event, CompareTemplatesInNewWindow action) {
				return true;
			}

		};
	}

	@Override
	protected FlexoActionFinalizer<CompareTemplatesInNewWindow> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CompareTemplatesInNewWindow>() {

			@Override
			public boolean run(EventObject event, CompareTemplatesInNewWindow action) {
				FileDiffEditorPopup popup = new FileDiffEditorPopup(action.getTemplates().get(0).getNiceQualifiedName(), action
						.getTemplates().get(1).getNiceQualifiedName(), action.getTemplates().get(0).getContent(), action.getTemplates()
						.get(1).getContent(), getController());
				popup.show();
				return true;
			}

		};
	}

}
