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

import javax.naming.InvalidNameException;
import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.TypeSelectorColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.dm.eo.DuplicateNameException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.view.controller.FlexoController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEntityTableModel extends AbstractModel<DMPackage, DMEntity> {

	protected static final Logger logger = Logger.getLogger(DMEntityTableModel.class.getPackage().getName());

	public DMEntityTableModel(DMPackage aPackage, FlexoProject project) {
		super(aPackage, project);
		addToColumns(new IconColumn<DMEntity>("entity_icon", 30) {
			@Override
			public Icon getIcon(DMEntity entity) {
				if (entity != null) {
					if (entity.getIsNormalClass()) {
						return DMEIconLibrary.DM_ENTITY_CLASS_ICON;
					} else if (entity.getIsInterface()) {
						return DMEIconLibrary.DM_ENTITY_INTERFACE_ICON;
					} else if (entity.getIsEnumeration()) {
						return DMEIconLibrary.DM_ENTITY_ENUMERATION_ICON;
					}
				}
				return DMEIconLibrary.DM_ENTITY_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEntity entity) {
				if (entity != null) {
					if (entity.getIsNormalClass()) {
						return FlexoLocalization.localizedForKey("class");
					} else if (entity.getIsInterface()) {
						return FlexoLocalization.localizedForKey("interface");
					} else if (entity.getIsEnumeration()) {
						return FlexoLocalization.localizedForKey("enumeration");
					}
				}
				return FlexoLocalization.localizedForKey("entity");
			}
		});
		addToColumns(new IconColumn<DMEntity>("read_only", 25) {
			@Override
			public Icon getIcon(DMEntity entity) {
				return entity.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMEntity entity) {
				return entity.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new EditableStringColumn<DMEntity>("name", 150) {
			@Override
			public String getValue(DMEntity entity) {
				return entity.getName();
			}

			@Override
			public void setValue(DMEntity entity, String aValue) {
				try {
					if (ReservedKeyword.contains(aValue)) {
						throw new InvalidNameException();
					}
					entity.setName(aValue);
				} catch (InvalidNameException e) {
					if (e.getCause() instanceof DuplicateClassNameException || e instanceof DuplicateNameException) {
						FlexoController.notify(FlexoLocalization.localizedForKey("this_name_is_already_used"));
					} else {
						FlexoController.notify(FlexoLocalization.localizedForKey("invalid_entity_name"));
					}
				}
			}
		});
		addToColumns(new EditableStringColumn<DMEntity>("class", 150) {
			@Override
			public String getValue(DMEntity entity) {
				return entity.getEntityClassName();
			}

			@Override
			public void setValue(DMEntity entity, String aValue) {
				try {
					entity.setEntityClassName(aValue);
				} catch (DuplicateClassNameException e) {
					FlexoController.notify(e.getLocalizedMessage());
				} catch (InvalidNameException e) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_entity_class_name"));
				}
				selectObject(entity);
			}
		});
		/*addToColumns(new EntitySelectorColumn<DMEntity,DMEntity>("parent", 150, project) {
		    public DMEntity getValue(DMEntity entity)
		    {
		        return entity.getParentEntity();
		    }

		    public void setValue(DMEntity entity, DMEntity aValue)
		    {
		        entity.setParentEntity(aValue);
		    }
		});*/
		addToColumns(new TypeSelectorColumn<DMEntity>("parentType", 150, project) {
			@Override
			public DMType getValue(DMEntity entity) {
				return entity.getParentType();
			}

			@Override
			public void setValue(DMEntity entity, DMType aValue) {
				entity.setParentType(aValue, true);
			}
		});
		addToColumns(new EditableStringColumn<DMEntity>("description", 250) {
			@Override
			public String getValue(DMEntity entity) {
				return entity.getDescription();
			}

			@Override
			public void setValue(DMEntity entity, String aValue) {
				entity.setDescription(aValue);
			}
		});
	}

	public DMPackage getDMPackage() {
		return getModel();
	}

	@Override
	public DMEntity elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getDMPackage().getOrderedChildren().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEntity entityAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMPackage() != null) {
			return getDMPackage().getOrderedChildren().size();
		}
		return 0;
	}

}
