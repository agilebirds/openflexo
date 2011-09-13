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

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;


import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * @author gpolet
 * 
 */
public class DMReadOnlyEntityTableModel extends DMEntityTableModel
{

    /**
     * @param aPackage
     * @param project
     */
    public DMReadOnlyEntityTableModel(DMPackage aPackage, FlexoProject project)
    {
        super(aPackage, project);
        while (getColumnCount()>0)
            removeFromColumns(columnAt(0));
        addToColumns(new IconColumn<DMEntity>("entity_icon", 30) {
            @Override
			public Icon getIcon(DMEntity entity)
            {
              	if (entity != null) {
            		if (entity.getIsNormalClass()) return DMEIconLibrary.DM_ENTITY_CLASS_ICON;
            		else if (entity.getIsInterface()) return DMEIconLibrary.DM_ENTITY_INTERFACE_ICON;
            		else if (entity.getIsEnumeration()) return DMEIconLibrary.DM_ENTITY_ENUMERATION_ICON;
            	}
                return DMEIconLibrary.DM_ENTITY_ICON;
            }
            
            @Override
            public String getLocalizedTooltip(DMEntity entity) {
            	if (entity != null) {
            		if (entity.getIsNormalClass()) return FlexoLocalization.localizedForKey("class");
            		else if (entity.getIsInterface()) return FlexoLocalization.localizedForKey("interface");
            		else if (entity.getIsEnumeration()) return FlexoLocalization.localizedForKey("enumeration");
            	}
            	return FlexoLocalization.localizedForKey("entity");
            }
        });
        addToColumns(new IconColumn<DMEntity>("read_only", 25) {
            @Override
			public Icon getIcon(DMEntity entity)
            {
                return (entity.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
            }
            
            @Override
            public String getLocalizedTooltip(DMEntity entity) {
            	return (entity.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization.localizedForKey("is_not_read_only"));
            }
        });
        addToColumns(new StringColumn<DMEntity>("name", 150) {
            @Override
			public String getValue(DMEntity entity)
            {
                return entity.getName();
            }
        });
        addToColumns(new StringColumn<DMEntity>("class", 150) {
            @Override
			public String getValue(DMEntity entity)
            {
                return entity.getEntityClassName();
            }
        });
        addToColumns(new StringColumn<DMEntity>("parent", 150) {
            @Override
			public String getValue(DMEntity entity)
            {
                if (entity != null && entity.getParentType() != null)
                    return entity.getParentType().getName();
                return null;
            }

        });
        /*TypeSelectorColumn<DMEntity> tsc = new TypeSelectorColumn<DMEntity>("parentType", 150, project) {
            public DMType getValue(DMEntity entity)
            {
            	return entity.getParentType();
            }

            public void setValue(DMEntity entity, DMType aValue)
            {
            	entity.setParentType(aValue, true);
            }
        };
        tsc.setReadOnly(true); //GPO: Does not work!
        addToColumns(tsc);*/
       addToColumns(new StringColumn<DMEntity>("description", 250) {
            @Override
			public String getValue(DMEntity entity)
            {
                return entity.getDescription();
            }
        });
    }
}
