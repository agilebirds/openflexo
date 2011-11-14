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
package org.openflexo.dm.model;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyPackageTableModel extends DMPackageTableModel {

	/**
	 * @param repository
	 * @param project
	 */
	public DMReadOnlyPackageTableModel(DMRepository repository, FlexoProject project) {
		super(repository, project);
		while (getColumnCount() > 0) {
			removeFromColumns(columnAt(0));
		}
		addToColumns(new IconColumn<DMPackage>("package_icon", 30) {
			@Override
			public Icon getIcon(DMPackage aPackage) {
				return DMEIconLibrary.DM_PACKAGE_ICON;
			}
		});
		addToColumns(new IconColumn<DMPackage>("read_only", 25) {
			@Override
			public Icon getIcon(DMPackage aPackage) {
				return (aPackage.getRepository() == null || aPackage.getRepository().isReadOnly() ? DMEIconLibrary.READONLY_ICON
						: DMEIconLibrary.MODIFIABLE_ICON);
			}

			@Override
			public String getLocalizedTooltip(DMPackage aPackage) {
				return (aPackage.getRepository() == null || aPackage.getRepository().isReadOnly() ? FlexoLocalization
						.localizedForKey("is_read_only") : FlexoLocalization.localizedForKey("is_not_read_only"));
			}
		});
		addToColumns(new StringColumn<DMPackage>("name", 745) {
			@Override
			public String getValue(DMPackage aPackage) {
				return aPackage.getLocalizedName();
			}
		});
	}

}
