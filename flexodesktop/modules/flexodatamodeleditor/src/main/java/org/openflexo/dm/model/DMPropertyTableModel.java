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
import org.openflexo.components.tabular.model.CheckColumn;
import org.openflexo.components.tabular.model.ChoiceListColumn;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.ToggleIconColumn;
import org.openflexo.components.tabular.model.TypeSelectorColumn;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicatePropertyNameException;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class DMPropertyTableModel extends AbstractModel<DMEntity,DMProperty>
{

    protected static final Logger logger = Logger.getLogger(DMPropertyTableModel.class.getPackage().getName());

    public DMPropertyTableModel(DMEntity entity, FlexoProject project)
    {
        super(entity, project);
        addToColumns(new IconColumn<DMProperty>("property_icon", 30) {
            @Override
			public Icon getIcon(DMProperty property)
            {
                return DMEIconLibrary.DM_PROPERTY_ICON;
            }
        });
        addToColumns(new IconColumn<DMProperty>("read_only", 25) {
            @Override
			public Icon getIcon(DMProperty property)
            {
                return (property.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON);
            }

            @Override
            public String getLocalizedTooltip(DMProperty property) {
            	return (property.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization.localizedForKey("is_not_read_only"));
            }
        });
        addToColumns(new ToggleIconColumn<DMProperty>("settable", 25) {
            @Override
			public Icon getIcon(DMProperty property)
            {
                return (property.getIsSettable() ? DMEIconLibrary.GET_SET_ICON : DMEIconLibrary.GET_ICON);
            }

            @Override
			public void toggleValue(DMProperty property)
            {
                property.setIsSettable(!property.getIsSettable());
            }
            @Override
            public String getLocalizedTooltip(DMProperty property) {
            	return (property.getIsSettable() ? FlexoLocalization.localizedForKey("can_be_set"):FlexoLocalization.localizedForKey("cannot_be_set"));
            }
        });
        if(entity instanceof ComponentDMEntity){
        	addToColumns(new ToggleIconColumn<DMProperty>("bindable", 25) {
        		@Override
				public Icon getIcon(DMProperty property)
        		{
        			return (((ComponentDMEntity)property.getEntity()).isBindable(property) ? DMEIconLibrary.BINDABLE_ICON : DMEIconLibrary.NOT_BINDABLE_ICON);
        		}

        		@Override
				public void toggleValue(DMProperty property)
        		{
        			((ComponentDMEntity)property.getEntity()).setBindable(property,!((ComponentDMEntity)property.getEntity()).isBindable(property));
        		}

        		@Override
        		public String getLocalizedTooltip(DMProperty property) {
        			return (((ComponentDMEntity)property.getEntity()).isBindable(property) ? FlexoLocalization.localizedForKey("is_bindable"): FlexoLocalization.localizedForKey("is_not_bindable"));
        		}
        	});
			addToColumns(new CheckColumn<DMProperty>("mandatory", 25) {
				@Override
        		public Boolean getBooleanValue(DMProperty property) {
        			return ((ComponentDMEntity)property.getEntity()).isMandatory(property);
        		}

				@Override
				public void setBooleanValue(DMProperty property, Boolean b) {
					((ComponentDMEntity)property.getEntity()).setMandatory(property, b);
				}
        		@Override
        		public String getLocalizedTooltip(DMProperty property) {
        			return getBooleanValue(property) ? FlexoLocalization.localizedForKey("is_mandatory"): FlexoLocalization.localizedForKey("is_not_mandatory");
        		}
        	});
        }
        addToColumns(new EditableStringColumn<DMProperty>("name", 150) {
            @Override
			public String getValue(DMProperty property)
            {
                return property.getName();
            }

            @Override
			public void setValue(DMProperty property, String aValue)
            {
                try {
                	if(ReservedKeyword.contains(aValue))throw new InvalidNameException(aValue+" is a reserved keyword.");
                    property.setName(aValue);
				} catch (InvalidNameException e) {
					FlexoController.showError(e.getMessage());
				} catch (DuplicatePropertyNameException e) {
					FlexoController.showError(FlexoLocalization.localizedForKey("sorry_this_name_matches_an_other_property_name"));
				}
            }
        });
        addToColumns(new ChoiceListColumn<DMProperty>("cardinality", 150) {
            @Override
			public ChoiceList getValue(DMProperty property)
            {
                return property.getCardinality();
            }

            @Override
			public void setValue(DMProperty property, ChoiceList aValue)
            {
                property.setCardinality((DMCardinality) aValue);
            }

            @Override
			protected String renderChoiceListValue(ChoiceList value)
            {
            	if(value==null)
            		return "";
                return ((DMCardinality) value).getName();
            }
        });
        addToColumns(new TypeSelectorColumn<DMProperty>("type", 150, project) {
            @Override
			public DMType getValue(DMProperty property)
            {
            	return property.getType();
            }

            @Override
			public void setValue(DMProperty property, DMType aValue)
            {
               property.setType(aValue);
            }
        });
        addToColumns(new EditableStringColumn<DMProperty>("description", 250) {
            @Override
			public String getValue(DMProperty property)
            {
                return property.getDescription();
            }

            @Override
			public void setValue(DMProperty property, String aValue)
            {
                property.setDescription(aValue);
            }
        });

    }

    public DMEntity getDMEntity()
    {
        return getModel();
    }

    @Override
	public DMProperty elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return getDMEntity().getOrderedSingleProperties().elementAt(row);
        } else {
            return null;
        }
    }

    public DMProperty entityAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getDMEntity() != null) {
            return getDMEntity().getOrderedSingleProperties().size();
        }
        return 0;
    }

}
