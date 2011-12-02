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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EOEntitySelectorColumn;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.tabular.model.ToggleIconColumn;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEORelationshipTableModel extends AbstractModel<DMEOEntity, DMEORelationship> {

	protected static final Logger logger = Logger.getLogger(DMEORelationshipTableModel.class.getPackage().getName());

	protected DMController controller;

	public DMEORelationshipTableModel(DMEOEntity entity, DMController ctrl) {
		super(entity, ctrl.getProject());
		this.controller = ctrl;
		FlexoProject project = controller.getProject();
		addToColumns(new IconColumn<DMEORelationship>("property_icon", 30) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				return DMEIconLibrary.DM_EORELATIONSHIP_ICON;
			}
		});
		addToColumns(new ToggleIconColumn<DMEORelationship>("read_only", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return (relationship.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
			}

			@Override
			public void toggleValue(DMEORelationship relationship) {
				if (relationship == null) {
					return;
				}
				relationship.setIsReadOnly(!relationship.getIsReadOnly());
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return (relationship.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only"));
			}
		});
		addToColumns(new ToggleIconColumn<DMEORelationship>("settable", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return (relationship.getIsSettable() ? DMEIconLibrary.GET_SET_ICON : DMEIconLibrary.GET_ICON);
			}

			@Override
			public void toggleValue(DMEORelationship relationship) {
				if (relationship == null) {
					return;
				}
				relationship.setIsSettable(!relationship.getIsSettable());
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return (relationship.getIsSettable() ? FlexoLocalization.localizedForKey("can_be_set") : FlexoLocalization
						.localizedForKey("cannot_be_set"));
			}
		});
		addToColumns(new ToggleIconColumn<DMEORelationship>("class_property", 25) {
			@Override
			public Icon getIcon(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return (relationship.getIsClassProperty() ? DMEIconLibrary.CLASS_PROPERTY_ICON : null);
			}

			@Override
			public void toggleValue(DMEORelationship relationship) {
				if (relationship == null) {
					return;
				}
				relationship.setIsClassProperty(!relationship.getIsClassProperty());
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return relationship.getIsClassProperty() ? FlexoLocalization.localizedForKey("is_class_property") : FlexoLocalization
						.localizedForKey("is_not_class_property");
			}
		});
		addToColumns(new ToggleIconColumn<DMEORelationship>("is_to_many", 25) {

			@Override
			public void toggleValue(DMEORelationship relationship) {
				relationship.setIsToMany(!relationship.getIsToMany());
			}

			@Override
			public Icon getIcon(DMEORelationship relationship) {
				return relationship.getIsToMany() ? DMEIconLibrary.TO_MANY_ICON : DMEIconLibrary.TO_ONE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEORelationship relationship) {
				return (relationship.getIsToMany() ? FlexoLocalization.localizedForKey("to_many") : FlexoLocalization
						.localizedForKey("to_one"));
			}

		});
		addToColumns(new EditableStringColumn<DMEORelationship>("name", 150) {
			@Override
			public String getValue(DMEORelationship relationship) {
				if (relationship == null) {
					return null;
				}
				return relationship.getName();
			}

			@Override
			public void setValue(DMEORelationship relationship, String aValue) {
				if (relationship == null) {
					return;
				}
				try {
					if (ReservedKeyword.contains(aValue)) {
						throw new InvalidNameException(aValue + " is a reserved keyword.");
					}
					relationship.setName(aValue);
				} catch (IllegalArgumentException e) {
					if (!DMEORelationshipTableModel.this.controller.handleException(relationship, "name", aValue, e)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("IllegalArgumentException was not handled by controller");
						}
					}
				} catch (InvalidNameException e) {
					if (!DMEORelationshipTableModel.this.controller.handleException(relationship, "name", aValue, e)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("IllegalArgumentException was not handled by controller");
						}
					}
				}
			}
		});

		addToColumns(new EOEntitySelectorColumn<DMEORelationship, DMEOEntity>("destination", 150, project, DMEOEntity.class) {

			/**
			 * Overrides getValueClass
			 * 
			 * @see org.openflexo.components.tabular.model.EntitySelectorColumn#getValueClass()
			 */
			@Override
			public Class getValueClass() {
				return DMEOEntity.class;
			}

			@Override
			public DMEOEntity getValue(DMEORelationship relationship) {
				if (relationship.getDestinationEntity() != null) {
					return relationship.getDestinationEntity();
				}
				return null;
			}

			@Override
			public void setValue(DMEORelationship relationship, DMEOEntity aValue) {
				if (relationship != null) {
					relationship.setDestinationEntity(aValue);
				}
			}

		});

		addToColumns(new StringColumn<DMEORelationship>("definition", 300) {
			@Override
			public String getValue(DMEORelationship relationship) {
				return relationship.getRelationshipDefinition();
			}
		});

		addToColumns(new EditableStringColumn<DMEORelationship>("description", 250) {
			@Override
			public String getValue(DMEORelationship relationship) {
				return relationship.getDescription();
			}

			@Override
			public void setValue(DMEORelationship relationship, String aValue) {
				relationship.setDescription(aValue);
			}
		});

	}

	public DMEOEntity getDMEOEntity() {
		return getModel();
	}

	@Override
	public DMEORelationship elementAt(int row) {
		if ((row >= 0) && (row < getRowCount())) {
			return getDMEOEntity().getOrderedRelationships().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEORelationship relationshipAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getOrderedRelationships().size();
		}
		return 0;
	}

}
