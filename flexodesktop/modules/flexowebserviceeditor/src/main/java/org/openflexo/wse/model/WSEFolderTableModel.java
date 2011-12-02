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
package org.openflexo.wse.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.ws.ExternalWSFolder;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.WSFolder;
import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEFolderTableModel extends AbstractModel<FlexoWSLibrary, WSFolder> {

	protected static final Logger logger = Logger.getLogger(WSEFolderTableModel.class.getPackage().getName());

	public WSEFolderTableModel(FlexoWSLibrary library, FlexoProject project) {
		super(library, project);
		addToColumns(new IconColumn<WSFolder>("ws_folder_icon", 30) {
			@Override
			public Icon getIcon(WSFolder object) {
				if (object instanceof ExternalWSFolder) {
					return WSEIconLibrary.WS_EXTERNAL_FOLDER_ICON;
				} else {
					return WSEIconLibrary.WS_INTERNAL_FOLDER_ICON;
				}
			}

			@Override
			public String getLocalizedTooltip(WSFolder object) {
				if (object instanceof ExternalWSFolder) {
					return FlexoLocalization.localizedForKey("external_ws_folder");
				} else {
					return FlexoLocalization.localizedForKey("internal_ws_folder");
				}
			}
		});
		addToColumns(new StringColumn<WSFolder>("name", 200) {
			@Override
			public String getValue(WSFolder object) {
				return (object).getLocalizedName();
			}
		});
		addToColumns(new StringColumn<WSFolder>("description", 570) {
			@Override
			public String getValue(WSFolder object) {
				return (object).getLocalizedDescription();
			}
		});
		setRowHeight(20);
	}

	public FlexoWSLibrary getWSLibrary() {
		return getModel();
	}

	@Override
	public WSFolder elementAt(int row) {
		if (row == 0) {
			return getWSLibrary().getExternalWSFolder();
		}
		if (row == 1) {
			return getWSLibrary().getInternalWSFolder();
		}
		return null;
	}

	public WSFolder wsFolderAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getWSLibrary() != null) {
			int returned = 2;
			return returned;
		}
		return 0;
	}

}
