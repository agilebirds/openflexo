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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.DropDownColumn;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.ToggleIconColumn;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOAttributeTableModel extends AbstractModel<DMEOEntity, DMEOAttribute> {

	protected static final Logger logger = Logger.getLogger(DMEOAttributeTableModel.class.getPackage().getName());

	protected DMController controller;

	public DMEOAttributeTableModel(DMEOEntity entity, DMController ctrl) {
		super(entity, ctrl.getProject());
		this.controller = ctrl;
		addToColumns(new IconColumn<DMEOAttribute>("property_icon", 30) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return DMEIconLibrary.DM_EOATTRIBUTE_ICON;
			}
		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("primary_key", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsPrimaryKeyAttribute() ? DMEIconLibrary.KEY_ICON : null;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setIsPrimaryKeyAttribute(!attribute.getIsPrimaryKeyAttribute());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsPrimaryKeyAttribute() ? FlexoLocalization.localizedForKey("primary_key") : FlexoLocalization
						.localizedForKey("not_primary_key");
			}

		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("read_only", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsReadOnlyAttribute() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setIsReadOnlyAttribute(!attribute.getIsReadOnlyAttribute());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsReadOnlyAttribute() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("lock", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsUsedForLocking() ? DMEIconLibrary.LOCK_ICON : null;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setIsUsedForLocking(!attribute.getIsUsedForLocking());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsUsedForLocking() ? FlexoLocalization.localizedForKey("is_used_for_locking") : FlexoLocalization
						.localizedForKey("is_not_used_for_locking");
			}
		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("settable", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsSettable() ? DMEIconLibrary.GET_SET_ICON : DMEIconLibrary.GET_ICON;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setIsSettable(!attribute.getIsSettable());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsSettable() ? FlexoLocalization.localizedForKey("can_be_set") : FlexoLocalization
						.localizedForKey("cannot_be_set");
			}
		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("class_property", 25) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return attribute.getIsClassProperty() ? DMEIconLibrary.CLASS_PROPERTY_ICON : null;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setIsClassProperty(!attribute.getIsClassProperty());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getIsClassProperty() ? FlexoLocalization.localizedForKey("is_class_property") : FlexoLocalization
						.localizedForKey("is_not_class_property");
			}
		});
		addToColumns(new ToggleIconColumn<DMEOAttribute>("allows_null", 20) {
			@Override
			public Icon getIcon(DMEOAttribute attribute) {
				return !attribute.getAllowsNull() ? DMEIconLibrary.NULL_PROPERTY_ICON : null;
			}

			@Override
			public void toggleValue(DMEOAttribute attribute) {
				attribute.setAllowsNull(!attribute.getAllowsNull());
			}

			@Override
			public String getLocalizedTooltip(DMEOAttribute attribute) {
				return attribute.getAllowsNull() ? FlexoLocalization.localizedForKey("allows_null") : FlexoLocalization
						.localizedForKey("does_not_allows_null");
			}
		});
		addToColumns(new EditableStringColumn<DMEOAttribute>("name", 150) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getName();
			}

			@Override
			public void setValue(DMEOAttribute attribute, String aValue) {
				try {
					if (ReservedKeyword.contains(aValue)) {
						throw new InvalidNameException(aValue + " is a reserved keyword.");
					}
					attribute.setName(aValue);
				} catch (IllegalArgumentException e) {
					if (!DMEOAttributeTableModel.this.controller.handleException(attribute, "name", aValue, e)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("IllegalArgumentException was not handled by controller");
						}
					}
				} catch (InvalidNameException e) {
					if (!DMEOAttributeTableModel.this.controller.handleException(attribute, "name", aValue, e)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("IllegalArgumentException was not handled by controller");
						}
					}
				}
			}
		});
		addToColumns(new EditableStringColumn<DMEOAttribute>("column", 150) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getColumnName();
			}

			@Override
			public void setValue(DMEOAttribute attribute, String aValue) {
				attribute.setColumnName(aValue);
			}
		});
		addToColumns(new DropDownColumn<DMEOAttribute, DMEOPrototype>("prototype", 150) {
			@Override
			public DMEOPrototype getValue(DMEOAttribute attribute) {
				if (attribute != null) {
					return attribute.getPrototype();
				}
				return null;
			}

			@Override
			public void setValue(DMEOAttribute attribute, DMEOPrototype aValue) {
				if (attribute != null) {
					attribute.setPrototype(aValue);
				}
			}

			@Override
			protected String renderValue(DMEOPrototype value) {
				if (value != null) {
					return value.getName();
				}
				return "";
			}

			@Override
			protected Vector getAvailableValues() {
				return getDMEOEntity().getDMModel().getEOPrototypeRepository().getPrototypes();
			}
		});
		addToColumns(new EditableStringColumn<DMEOAttribute>("description", 250) {
			@Override
			public String getValue(DMEOAttribute attribute) {
				return attribute.getDescription();
			}

			@Override
			public void setValue(DMEOAttribute attribute, String aValue) {
				attribute.setDescription(aValue);
			}
		});

	}

	public DMEOEntity getDMEOEntity() {
		return getModel();
	}

	@Override
	public DMEOAttribute elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getDMEOEntity().getOrderedAttributes().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEOAttribute attributeAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMEOEntity() != null) {
			return getDMEOEntity().getOrderedAttributes().size();
		}
		return 0;
	}

}
