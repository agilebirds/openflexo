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
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DuplicateNameException;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class DMEOEntityTableModel extends AbstractModel<DMEOModel,DMEOEntity>
{

    protected static final Logger logger = Logger.getLogger(DMEOEntityTableModel.class.getPackage().getName());

    public DMEOEntityTableModel(DMEOModel dmEOModel, FlexoProject project)
    {
        super(dmEOModel, project);
        addToColumns(new IconColumn<DMEOEntity>("entity_icon", 30) {
            @Override
			public Icon getIcon(DMEOEntity entity)
            {
                return DMEIconLibrary.DM_EOENTITY_ICON;
            }
        });
        addToColumns(new IconColumn<DMEOEntity>("read_only", 25) {
            @Override
			public Icon getIcon(DMEOEntity entity)
            {
                if (entity != null)
                    return (entity.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
                return null;
            }

            @Override
            public String getLocalizedTooltip(DMEOEntity entity) {
            	return (entity.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization.localizedForKey("is_not_read_only"));
            }
        });
        addToColumns(new EditableStringColumn<DMEOEntity>("name", 150) {
            @Override
			public String getValue(DMEOEntity entity)
            {
                if (entity != null)
                    return entity.getName();
                return null;
            }

            @Override
			public void setValue(DMEOEntity entity, String aValue)
            {
                if (entity != null)
                    try {
                    	if(ReservedKeyword.contains(aValue))
                    		throw new InvalidNameException(aValue+" is a reserved keyword.");
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
        addToColumns(new EditableStringColumn<DMEOEntity>("class", 150) {
            @Override
			public String getValue(DMEOEntity entity)
            {
                if (entity != null)
                    return entity.getEntityClassName();
                return null;
            }

            @Override
			public void setValue(DMEOEntity entity, String aValue)
            {
                try {
                    entity.setEntityClassName(aValue);
                } catch (DuplicateClassNameException e) {
                    FlexoController.showError(e.getLocalizedMessage());
                } catch (InvalidNameException e) {
                    FlexoController.notify(FlexoLocalization.localizedForKey("invalid_entity_class_name"));
                }
            }
        });
        addToColumns(new EditableStringColumn<DMEOEntity>("external_name", 150) {
            @Override
			public String getValue(DMEOEntity entity)
            {
                if (entity != null)
                    return entity.getExternalName();
                return null;
            }

            @Override
			public void setValue(DMEOEntity entity, String aValue)
            {
                try {
                    entity.setExternalName(aValue);
                } catch (InvalidNameException e) {
                    FlexoController.notify(FlexoLocalization.localizedForKey("invalid_external_name"));
                }
            }
        });
        /*addToColumns(new EntitySelectorColumn<DMEOEntity,DMEntity>("parent", 150, project) {
            public DMEntity getValue(DMEOEntity entity)
            {
                if (entity != null)
                    return entity.getParentEntity();
                return null;
            }

            public void setValue(DMEOEntity entity, DMEntity aValue)
            {
                if ((entity != null) && ((aValue instanceof DMEOEntity) || (aValue == null)))
                    entity.setParentEntity(aValue);
            }
        });*/
        addToColumns(new TypeSelectorColumn<DMEOEntity>("parentType", 150, project) {
            @Override
			public DMType getValue(DMEOEntity entity)
            {
            	return entity.getParentType();
            }

            @Override
			public void setValue(DMEOEntity entity, DMType aValue)
            {
            	entity.setParentType(aValue, true);
            }
        });
        addToColumns(new EditableStringColumn<DMEOEntity>("description", 250) {
            @Override
			public String getValue(DMEOEntity entity)
            {
                if (entity != null)
                    return entity.getDescription();
                return null;
            }

            @Override
			public void setValue(DMEOEntity entity, String aValue)
            {
                if (entity != null)
                    entity.setDescription(aValue);
            }
        });
    }

    public DMEOModel getDMEOModel()
    {
        return getModel();
    }

    @Override
	public DMEOEntity elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return getDMEOModel().getOrderedChildren().elementAt(row);
        } else {
            return null;
        }
    }

    public DMEOEntity entityAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getDMEOModel() != null) {
            return getDMEOModel().getOrderedChildren().size();
        }
        return 0;
    }

}
