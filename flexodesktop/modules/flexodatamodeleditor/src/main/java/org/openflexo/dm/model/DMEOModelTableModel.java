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

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.ChoiceListColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOModelTableModel extends AbstractModel<DMEORepository, DMEOModel> {

	protected static final Logger logger = Logger.getLogger(DMEOModelTableModel.class.getPackage().getName());

	public DMEOModelTableModel(DMEORepository repository, FlexoProject project) {
		super(repository, project);
		addToColumns(new IconColumn<DMEOModel>("eoModel_icon", 30) {
			@Override
			public Icon getIcon(DMEOModel object) {
				return DMEIconLibrary.DM_EOMODEL_ICON;
			}
		});
		addToColumns(new IconColumn<DMEOModel>("read_only", 25) {
			@Override
			public Icon getIcon(DMEOModel dmEOModel) {
				return (dmEOModel.getRepository().isReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
			}

			@Override
			public String getLocalizedTooltip(DMEOModel dmEOModel) {
				return (dmEOModel.getRepository().isReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only"));
			}
		});
		addToColumns(new StringColumn<DMEOModel>("name", 200) {
			@Override
			public String getValue(DMEOModel dmEOModel) {
				return dmEOModel.getName();
			}
		});
		addToColumns(new ChoiceListColumn<DMEOModel>("adaptor", 100) {
			@Override
			public ChoiceList getValue(DMEOModel dmEOModel) {
				return dmEOModel.getAdaptorType();
			}

			@Override
			public void setValue(DMEOModel dmEOModel, ChoiceList aValue) {
				if (!dmEOModel.getRepository().isReadOnly()) {
					dmEOModel.setAdaptorType((DMEOAdaptorType) aValue);
				} else if (dmEOModel.getAdaptorType() != aValue) {
					FlexoController.notify(FlexoLocalization.localizedForKey("could_not_modify_read_only_eomodel"));
				}
			}

			@Override
			protected String renderChoiceListValue(ChoiceList value) {
				if (value != null)
					return ((DMEOAdaptorType) value).getName();
				return "";
			}
		});
		addToColumns(new StringColumn<DMEOModel>("database_server", 300) {
			@Override
			public String getValue(DMEOModel dmEOModel) {
				return dmEOModel.getDatabaseServer();
			}
		});
	}

	public DMEORepository getDMEORepository() {
		return getModel();
	}

	@Override
	public DMEOModel elementAt(int row) {
		if ((row >= 0) && (row < getRowCount())) {
			return (DMEOModel) getDMEORepository().getOrderedChildren().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEOModel eoModelAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMEORepository() != null) {
			return getDMEORepository().getOrderedChildren().size();
		}
		return 0;
	}

}
