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
package org.openflexo.fps.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.fps.action.MarkAsMergedFiles;
import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MarkAsMergedFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MarkAsMergedFilesInitializer(FPSControllerActionInitializer actionInitializer) {
		super(MarkAsMergedFiles.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MarkAsMergedFiles> getDefaultInitializer() {
		return new FlexoActionInitializer<MarkAsMergedFiles>() {
			@Override
			public boolean run(EventObject e, MarkAsMergedFiles action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MarkAsMergedFiles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MarkAsMergedFiles>() {
			@Override
			public boolean run(EventObject e, MarkAsMergedFiles action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return FPSIconLibrary.FPS_MARK_AS_MERGED_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return FPSIconLibrary.FPS_MARK_AS_MERGED_DISABLED_ICON;
	}

}
