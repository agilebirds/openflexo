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
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyEOAttributeTableModel extends DMEOAttributeTableModel {

	/**
	 * @param entity
	 * @param project
	 */
	public DMReadOnlyEOAttributeTableModel(DMEOEntity entity, DMController ctrl) {
		super(entity, ctrl);
		while (getColumnCount() > 0) {
			removeFromColumns(columnAt(0));
		}
		addToColumns(new IconColumn<DMEOAttribute>("property_icon", 30) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return DMEIconLibrary.DM_EOATTRIBUTE_ICON;
			}
		});
		addToColumns(new IconColumn<DMEOAttribute>("primary_key", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsPrimaryKeyAttribute() ? DMEIconLibrary.KEY_ICON : null;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsPrimaryKeyAttribute() ? FlexoLocalization.localizedForKey("primary_key") : FlexoLocalization
						.localizedForKey("not_primary_key");
			}

		});
		addToColumns(new IconColumn<DMEOAttribute>("read_only", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsReadOnlyAttribute() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsReadOnlyAttribute() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new IconColumn<DMEOAttribute>("lock", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsUsedForLocking() ? DMEIconLibrary.LOCK_ICON : null;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsUsedForLocking() ? FlexoLocalization.localizedForKey("is_used_for_locking") : FlexoLocalization
						.localizedForKey("is_not_used_for_locking");
			}
		});
		addToColumns(new IconColumn<DMEOAttribute>("settable", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsSettable() ? DMEIconLibrary.GET_SET_ICON : DMEIconLibrary.GET_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsSettable() ? FlexoLocalization.localizedForKey("can_be_set") : FlexoLocalization
						.localizedForKey("cannot_be_set");
			}
		});
		addToColumns(new IconColumn<DMEOAttribute>("class_property", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsClassProperty() ? DMEIconLibrary.CLASS_PROPERTY_ICON : null;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsClassProperty() ? FlexoLocalization.localizedForKey("is_class_property") : FlexoLocalization
						.localizedForKey("is_not_class_property");
			}
		});
		addToColumns(new IconColumn<DMEOAttribute>("allows_null", 20) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return !attribute.getAllowsNull() ? DMEIconLibrary.NULL_PROPERTY_ICON : null;
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getAllowsNull() ? FlexoLocalization.localizedForKey("allows_null") : FlexoLocalization
						.localizedForKey("does_not_allows_null");
			}
		});
		addToColumns(new StringColumn<DMEOAttribute>("name", 150) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getName();
			}

		});
		addToColumns(new StringColumn<DMEOAttribute>("column", 150) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getColumnName();
			}

		});
		addToColumns(new StringColumn<DMEOAttribute>("prototype", 150) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				if (attribute != null && attribute.getPrototype() != null) {
					return attribute.getPrototype().getName();
				}
				return null;
			}
		});
		addToColumns(new StringColumn<DMEOAttribute>("description", 250) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getDescription();
			}

		});
	}

}
