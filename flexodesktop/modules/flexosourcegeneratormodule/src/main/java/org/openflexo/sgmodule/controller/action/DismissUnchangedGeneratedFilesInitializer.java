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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.generator.action.DismissUnchangedGeneratedFiles;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class DismissUnchangedGeneratedFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DismissUnchangedGeneratedFilesInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(DismissUnchangedGeneratedFiles.actionType,actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DismissUnchangedGeneratedFiles> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DismissUnchangedGeneratedFiles>() {
			@Override
			public boolean run(ActionEvent e, DismissUnchangedGeneratedFiles action)
			{
				action.getProjectGenerator().startHandleLogs();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DismissUnchangedGeneratedFiles> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DismissUnchangedGeneratedFiles>() {
			@Override
			public boolean run(ActionEvent e, DismissUnchangedGeneratedFiles action)
			{
				action.getProjectGenerator().stopHandleLogs();
				action.getProjectGenerator().flushLogs();
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.DISMISS_UNCHANGED_ICON;
	}

	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.DISMISS_UNCHANGED_DISABLED_ICON;
	}

}
