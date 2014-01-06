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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.technologyadapter.diagram.model.action.ResetGraphicalRepresentations;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ResetGraphicalRepresentationInitializer extends ActionInitializer<ResetGraphicalRepresentations, DiagramElement<?>, DiagramElement<?>> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ResetGraphicalRepresentationInitializer(VEControllerActionInitializer actionInitializer) {
		super(ResetGraphicalRepresentations.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ResetGraphicalRepresentations> getDefaultInitializer() {
		return new FlexoActionInitializer<ResetGraphicalRepresentations>() {
			@Override
			public boolean run(EventObject e, ResetGraphicalRepresentations action) {
				return FlexoController.confirm(FlexoLocalization
						.localizedForKey("really_reset_all_graphical_representations_for_this_view_?"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ResetGraphicalRepresentations> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ResetGraphicalRepresentations>() {
			@Override
			public boolean run(EventObject e, ResetGraphicalRepresentations action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.REFRESH_ICON;
	}

}
