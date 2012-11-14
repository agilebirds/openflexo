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
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyEORelationshipTableModel extends DMEORelationshipTableModel {

	/**
	 * @param entity
	 * @param project
	 */
	public DMReadOnlyEORelationshipTableModel(DMEOEntity entity, DMController ctrl) {
		super(entity, ctrl);
		while (getColumnCount() > 0) {
			removeFromColumns(columnAt(0));
		}
		addToColumns(new IconColumn<DMEORelationship>("property_icon", 30) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				return DMEIconLibrary.DM_EORELATIONSHIP_ICON;
			}
		});
		addToColumns(new IconColumn<DMEORelationship>("read_only", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return relationship.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}

		});
		addToColumns(new IconColumn<DMEORelationship>("settable", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getIsSettable() ? DMEIconLibrary.GET_SET_ICON : DMEIconLibrary.GET_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return relationship.getIsSettable() ? FlexoLocalization.localizedForKey("can_be_set") : FlexoLocalization
						.localizedForKey("cannot_be_set");
			}
		});
		addToColumns(new IconColumn<DMEORelationship>("class_property", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getIsClassProperty() ? DMEIconLibrary.CLASS_PROPERTY_ICON : null;
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return relationship.getIsClassProperty() ? FlexoLocalization.localizedForKey("is_class_property") : FlexoLocalization
						.localizedForKey("is_not_class_property");
			}

		});
		addToColumns(new StringColumn<DMEORelationship>("is_to_many", 25, false, false) {
			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getIsToMany() ? ">>" : ">";
			}
		});
		addToColumns(new StringColumn<DMEORelationship>("name", 150) {
			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getName();
			}

		});

		addToColumns(new StringColumn<DMEORelationship>("destination", 150) {

			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship.getDestinationEntity() != null) {
					return relationship.getDestinationEntity().getName();
				}
				return null;
			}

		});

		addToColumns(new StringColumn<DMEORelationship>("source_attribute", 150) {
			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship.getPrimarySourceAttribute() != null) {
					return relationship.getPrimarySourceAttribute().getName();
				}
				return "-";
			}
		});
		addToColumns(new StringColumn<DMEORelationship>("destination_attribute", 150) {
			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship.getPrimaryDestinationAttribute() != null) {
					return relationship.getPrimaryDestinationAttribute().getName();
				}
				return "-";
			}
		});

		addToColumns(new StringColumn<DMEORelationship>("description", 250) {
			@Override
			public String getValue(DMEORelationship relationship) {
				return relationship.getDescription();
			}

		});
	}
}
