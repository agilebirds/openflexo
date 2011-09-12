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
package org.openflexo.wse.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEDMPropertyTableModel extends AbstractModel<DMEntity,DMProperty>
{

    protected static final Logger logger = Logger.getLogger(WSEDMPropertyTableModel.class.getPackage().getName());

    public WSEDMPropertyTableModel(DMEntity entity, FlexoProject project)
    {
        super(entity, project);
        addToColumns(new IconColumn<DMProperty>("property_icon", 30) {
            @Override
			public Icon getIcon(DMProperty object)
            {
                return DMEIconLibrary.DM_PROPERTY_ICON;
            }
        });
        addToColumns(new StringColumn<DMProperty>("name", 150) {
            @Override
			public String getValue(DMProperty object)
            {
                return (object).getName();
            }

          /*  public void setValue(FlexoModelObject object, String aValue)
            {
                ((DMProperty) object).setName(aValue);
            }*/
        });
        addToColumns(new StringColumn<DMProperty>("cardinality", 150){
        		@Override
				public String getValue(DMProperty object){
        			if((object).getCardinality()!=null)
        			return (object).getCardinality().getName();
        			return "";
        		}
        });
    /*    addToColumns(new ChoiceListColumn("cardinality", 150) {
            public ChoiceList getValue(FlexoModelObject object)
            {
                return ((DMProperty) object).getCardinality();
            }

            public void setValue(FlexoModelObject object, ChoiceList aValue)
            {
                ((DMProperty) object).setCardinality((DMCardinality) aValue);
            }

            protected String renderChoiceListValue(ChoiceList value)
            {
                return ((DMCardinality) value).getName();
            }
        });
        */
        addToColumns(new StringColumn<DMProperty>("type", 150){
        		@Override
				public String getValue(DMProperty object){
        			if((object).getType()!=null)
        			return (object).getType().getName();
        			return "";
        		}
        });
/*        addToColumns(new EntitySelectorColumn("type", 150, project) {
            public FlexoModelObject getValue(FlexoModelObject object)
            {
                return ((DMProperty) object).getType();
            }

            public void setValue(FlexoModelObject object, FlexoModelObject aValue)
            {
                ((DMProperty) object).setType((DMEntity) aValue);
            }
        });
        */
/*        addToColumns(new StringColumn("description", 250) {
            public String getValue(FlexoModelObject object)
            {
                return ((DMProperty) object).getDescription();
            }

            public void setValue(FlexoModelObject object, String aValue)
            {
                ((DMProperty) object).setDescription(aValue);
            }
        });*/

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
