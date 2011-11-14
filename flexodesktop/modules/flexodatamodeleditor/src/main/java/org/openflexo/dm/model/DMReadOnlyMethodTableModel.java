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
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyMethodTableModel extends DMMethodTableModel {

	/**
	 * @param entity
	 * @param project
	 */
	public DMReadOnlyMethodTableModel(DMEntity entity, FlexoProject project) {
		super(entity, project);
		while (getColumnCount() > 0) {
			removeFromColumns(columnAt(0));
		}
		addToColumns(new IconColumn<DMMethod>("method_icon", 30) {
			@Override
			public Icon getIcon(DMMethod method) {
				return DMEIconLibrary.DM_METHOD_ICON;
			}
		});
		addToColumns(new IconColumn<DMMethod>("read_only", 25) {
			@Override
			public Icon getIcon(DMMethod method) {
				return (method.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
			}

			@Override
			public String getLocalizedTooltip(DMMethod method) {
				return (method.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only"));
			}
		});
		addToColumns(new StringColumn<DMMethod>("name", 150) {
			@Override
			public String getValue(DMMethod method) {
				return method.getName();
			}
		});
		/*addToColumns(new TypeSelectorColumn<DMMethod>("returns", 150, project) {
		    public DMType getValue(DMMethod method)
		    {
		    	return method.getReturnType();
		    }

		    public void setValue(DMMethod method, DMType aValue)
		    {
		    	// read only: cannot modify
		    }
		});*/

		StringColumn<DMMethod> sc = new StringColumn<DMMethod>("returns", 150) {
			@Override
			public String getValue(DMMethod method) {
				if (method.getReturnType() != null) {
					return method.getReturnType().getName();
				}
				return "void";
			}
		};
		addToColumns(sc);
		addToColumns(new StringColumn<DMMethod>("parameters", 200) {
			@Override
			public String getValue(DMMethod method) {
				return method.getParameterListAsString(false);
			}

		});

		addToColumns(new StringColumn<DMMethod>("description", 250) {
			@Override
			public String getValue(DMMethod method) {
				return method.getDescription();
			}
		});
	}

}
