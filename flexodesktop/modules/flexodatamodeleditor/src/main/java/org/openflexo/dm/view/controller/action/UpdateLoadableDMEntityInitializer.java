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
package org.openflexo.dm.view.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.dm.view.popups.UpdateClassesPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.UpdateLoadableDMEntity;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class UpdateLoadableDMEntityInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	UpdateLoadableDMEntityInitializer(DMControllerActionInitializer actionInitializer) {
		super(UpdateLoadableDMEntity.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<UpdateLoadableDMEntity> getDefaultInitializer() {
		return new FlexoActionInitializer<UpdateLoadableDMEntity>() {
			@Override
			public boolean run(EventObject e, UpdateLoadableDMEntity action) {
				action.makeFlexoProgress(FlexoLocalization.localizedForKey("analysing"), 2);
				UpdateClassesPopup popup = new UpdateClassesPopup(action.getUpdatedEntities(), getProject(),
						getControllerActionInitializer().getDMController(), action.getFlexoProgress());
				action.hideFlexoProgress();
				popup.setVisible(true);
				if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) && (popup.getDMSet().getSelectedObjects().size() > 0)) {
					action.setUpdatedSet(popup.getDMSet());
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<UpdateLoadableDMEntity> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UpdateLoadableDMEntity>() {
			@Override
			public boolean run(EventObject e, UpdateLoadableDMEntity action) {
				getControllerActionInitializer().getDMController().getSelectionManager().setSelectedObjects(action.getUpdatedEntities());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.REFRESH_ICON;
	}

}
