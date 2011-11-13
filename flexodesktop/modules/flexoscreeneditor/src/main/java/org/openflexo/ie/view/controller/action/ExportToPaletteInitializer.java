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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ImageUtils;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.ExportWidgetToPalette;

public class ExportToPaletteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ExportToPaletteInitializer(IEControllerActionInitializer actionInitializer) {
		super(ExportWidgetToPalette.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public IEController getController() {
		return (IEController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<ExportWidgetToPalette> getDefaultInitializer() {
		return new FlexoActionInitializer<ExportWidgetToPalette>() {
			@Override
			public boolean run(ActionEvent e, ExportWidgetToPalette action) {
				boolean ok = false;
				while (!ok) {
					String name = FlexoController.askForStringMatchingPattern(FlexoLocalization.localizedForKey("new_widget_name"),
							Pattern.compile(FileUtils.GOOD_CHARACTERS_REG_EXP + "+"),
							FlexoLocalization.localizedForKey("name_cannot_contain_\\\"*/<>:"));
					if (name != null) {
						if (getProject().getCustomWidgetPalette().widgetWithNameExists(name)) {
							String[] options = FlexoLocalization.localizedForKey(new String[] { "change_name", "replace", "cancel" });
							int result = FlexoController.selectOption(
									FlexoLocalization.localizedForKey("a_widget_with_that_name_already_exists"), options, options[0]);
							switch (result) {
							case 0:
								continue;
							case 1:
								break;
							case 2:
								return false;
							}
						}
						action.setWidgetName(name);
						JComponent component = getController().viewForObject(action.getWidget(), true);
						if (component != null)
							action.setScreenshot(ImageUtils.createImageFromComponent(component));
						return true;
					} else
						return false;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ExportWidgetToPalette> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ExportWidgetToPalette>() {
			@Override
			public boolean run(ActionEvent e, ExportWidgetToPalette action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.EXPORT_ICON;
	}

}
