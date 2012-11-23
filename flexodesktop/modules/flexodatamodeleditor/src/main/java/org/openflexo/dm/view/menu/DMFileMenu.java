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
package org.openflexo.dm.view.menu;

import javax.swing.JMenu;

import org.openflexo.dataimporter.DataImporterLoader.KnownDataImporter;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.action.CreateProjectDatabaseRepository;
import org.openflexo.foundation.dm.action.CreateProjectRepository;
import org.openflexo.foundation.dm.action.ImportExternalDatabaseRepository;
import org.openflexo.foundation.dm.action.ImportJARFileRepository;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.UserType;
import org.openflexo.view.menu.FileMenu;
import org.openflexo.view.menu.FlexoMenuItem;

public class DMFileMenu extends FileMenu {

	protected DMController _controller;

	public DMFileMenu(DMController controller) {
		super(controller);
		_controller = controller;
	}

	public DMController getDMController() {
		return _controller;
	}

	@Override
	public void addSpecificItems() {
		JMenu newMenu = new JMenu();
		newMenu.setText(FlexoLocalization.localizedForKey("new", newMenu));
		newMenu.addSeparator();
		newMenu.add(new FlexoMenuItem(CreateProjectRepository.actionType, getController()));
		newMenu.add(new FlexoMenuItem(CreateProjectDatabaseRepository.actionType, getController()));

		JMenu importMenu = new JMenu();
		importMenu.setText(FlexoLocalization.localizedForKey("import", importMenu));
		importMenu.add(new FlexoMenuItem(ImportExternalDatabaseRepository.actionType, getController()));
		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			importMenu.add(new FlexoMenuItem(ImportJARFileRepository.actionType, getController()));
			if (KnownDataImporter.RATIONAL_ROSE_IMPORTER.isAvailable()) {
				importMenu.add(new FlexoMenuItem(ImportRationalRoseRepository.actionType, getController()));
			}
			// importMenu.add(new FlexoMenuItem(ImportDenaliFoundationRepository.actionType, getController()));
		}
		add(newMenu);
		add(importMenu);

		addSeparator();
	}

}
