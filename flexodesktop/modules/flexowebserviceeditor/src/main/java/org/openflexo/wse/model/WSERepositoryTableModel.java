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

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.foundation.ws.WSRepositoryFolder;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author dvanvyve
 * 
 */
public class WSERepositoryTableModel extends AbstractModel<WSService, DMRepository> {

	protected static final Logger logger = Logger.getLogger(WSERepositoryTableModel.class.getPackage().getName());

	public WSERepositoryTableModel(WSService model, FlexoProject project) {
		super(model, project);
		addToColumns(new IconColumn<DMRepository>("ws_repository_icon", 30) {
			@Override
			public Icon getIcon(DMRepository object) {
				return WSEIconLibrary.WS_REPOSITORY_ICON;
			}
		});
		addToColumns(new IconColumn<DMRepository>("read_only", 25) {
			@Override
			public Icon getIcon(DMRepository object) {
				return object.isReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMRepository repository) {
				return repository.isReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new StringColumn<DMRepository>("name", 190) {
			@Override
			public String getValue(DMRepository object) {
				return object.getLocalizedName();
			}
		});
		addToColumns(new StringColumn<DMRepository>("type", 190) {
			@Override
			public String getValue(DMRepository object) {
				return FlexoLocalization.localizedForKey("web_service_repository");
			}
		});
		addToColumns(new StringColumn<DMRepository>("description", 365) {
			@Override
			public String getValue(DMRepository object) {
				return object.getDescription();
			}
			/*
			            public void setValue(FlexoModelObject object, String aValue)
			            {
			                ((DMRepository) object).setDescription(aValue);
			            }
			            */
		});
		setRowHeight(20);
	}

	public WSService getWSService() {
		return getModel();
	}

	public WSRepositoryFolder getWSDLRepositoryFolder() {
		if (getWSService() != null) {
			return getWSService().getWSRepositoryFolder();
		}
		return null;
	}

	public Vector getWSDLRepositories() {
		return getWSDLRepositoryFolder().getWSRepositories();
	}

	@Override
	public DMRepository elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return ((WSRepository) getWSDLRepositories().get(row)).getWSDLRepository();
		} else {
			return null;
		}
	}

	public DMRepository repositoryAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getWSDLRepositoryFolder() != null) {
			return getWSDLRepositories().size();
		}
		return 0;
	}

}
