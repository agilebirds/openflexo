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
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyEOEntityTableModel extends DMEOEntityTableModel {

	/**
	 * @param dmEOModel
	 * @param project
	 */
	public DMReadOnlyEOEntityTableModel(DMEOModel dmEOModel, FlexoProject project) {
		super(dmEOModel, project);
		while (getColumnCount() > 0) {
			removeFromColumns(columnAt(0));
		}
		addToColumns(new IconColumn<DMEOEntity>("entity_icon", 30) {
			@Override
			public Icon getIcon(DMEOEntity dmEOEntity) {
				return DMEIconLibrary.DM_EOENTITY_ICON;
			}
		});
		addToColumns(new IconColumn<DMEOEntity>("read_only", 25) {
			@Override
			public Icon getIcon(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					return dmEOEntity.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
				}
				return null;
			}

			@Override
			public String getLocalizedTooltip(DMEOEntity entity) {
				return entity.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new StringColumn<DMEOEntity>("name", 150) {
			@Override
			public String getValue(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					return dmEOEntity.getName();
				}
				return null;
			}

		});
		addToColumns(new StringColumn<DMEOEntity>("class", 150) {
			@Override
			public String getValue(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					return dmEOEntity.getEntityClassName();
				}
				return null;
			}

		});
		addToColumns(new StringColumn<DMEOEntity>("external_name", 150) {
			@Override
			public String getValue(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					return dmEOEntity.getExternalName();
				}
				return null;
			}
		});
		addToColumns(new StringColumn<DMEOEntity>("parent", 150) {
			@Override
			public String getValue(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					if (dmEOEntity.getParentType() != null) {
						return dmEOEntity.getParentType().getName();
					}
				}
				return null;
			}

		});
		/*TypeSelectorColumn<DMEOEntity> tsc = new TypeSelectorColumn<DMEOEntity>("parentType", 150, project) {
		    public DMType getValue(DMEOEntity entity)
		    {
		    	return entity.getParentType();
		    }

		    public void setValue(DMEOEntity entity, DMType aValue)
		    {
		    	entity.setParentType(aValue, true);
		    }
		};
		tsc.setReadOnly(true); // GPO: it does not work, it is still editable
		addToColumns(tsc);*/
		addToColumns(new StringColumn<DMEOEntity>("description", 250) {
			@Override
			public String getValue(DMEOEntity dmEOEntity) {
				if (dmEOEntity != null) {
					return dmEOEntity.getDescription();
				}
				return null;
			}

		});
	}

}
