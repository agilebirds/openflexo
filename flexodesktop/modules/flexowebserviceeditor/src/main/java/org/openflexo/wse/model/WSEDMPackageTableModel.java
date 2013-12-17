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
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEDMPackageTableModel extends AbstractModel<DMRepository, DMPackage> {

	protected static final Logger logger = Logger.getLogger(WSEDMPackageTableModel.class.getPackage().getName());

	public WSEDMPackageTableModel(DMRepository repository, FlexoProject project) {
		super(repository, project);
		addToColumns(new IconColumn<DMPackage>("package_icon", 30) {
			@Override
			public Icon getIcon(DMPackage object) {
				return DMEIconLibrary.DM_PACKAGE_ICON;
			}
		});
		addToColumns(new StringColumn<DMPackage>("name", 745) {
			@Override
			public String getValue(DMPackage aPackage) {
				return aPackage.getLocalizedName();
			}

			/*  public void setValue(FlexoModelObject object, String aValue)
			  {
			      ((DMPackage) object).setName(aValue);
			  }*/
		});
	}

	public DMRepository getDMRepository() {
		return getModel();
	}

	@Override
	public DMPackage elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return (DMPackage) getDMRepository().getOrderedChildren().elementAt(row);
		} else {
			return null;
		}
	}

	public DMPackage packageAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMRepository() != null) {
			return getDMRepository().getOrderedChildren().size();
		}
		return 0;
	}

}
