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
package org.openflexo.ie.view.dkv.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.DuplicateDKVObjectException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.SEIconLibrary;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DKVDomainTableModel extends AbstractModel<DKVModel.DomainList,Domain>
{

    protected static final Logger logger = Logger.getLogger(DKVDomainTableModel.class.getPackage().getName());

    public DKVDomainTableModel(DKVModel.DomainList domainList, FlexoProject project)
    {
        super(domainList, project);
        addToColumns(new IconColumn<Domain>("domain_icon", 30) {
            @Override
			public Icon getIcon(Domain object)
            {
                return SEIconLibrary.DOMAIN_ICON;
            }
        });
        addToColumns(new EditableStringColumn<Domain>("name", 150) {
            @Override
			public String getValue(Domain domain)
            {
                return domain.getName();
            }

            @Override
			public void setValue(Domain domain, String aValue)
            {
                try {
                    domain.setName(aValue);
               } catch (DuplicateDKVObjectException e) {
                   
               }
            }
        });
        addToColumns(new StringColumn<Domain>("keys", 50) {
            @Override
			public String getValue(Domain domain)
            {
                return ""+domain.getKeys().size();
            }
        });
        addToColumns(new EditableStringColumn<Domain>("description", 250) {
            @Override
			public String getValue(Domain domain)
            {
                return domain.getDescription();
            }

            @Override
			public void setValue(Domain domain, String aValue)
            {
                domain.setDescription(aValue);
              }
        });
    }

    public DKVModel.DomainList getDomainList()
    {
        return getModel();
    }

    public DKVModel getDKVModel()
    {
        return getModel().getDkvModel();
    }

    @Override
	public Domain elementAt(int row)
    {
        if ((row >= 0) && (row < getRowCount())) {
            return getDomainList().getDomains().elementAt(row);
        } else {
            return null;
        }
    }

    public Domain entityAt(int row)
    {
        return elementAt(row);
    }

    @Override
	public int getRowCount()
    {
        if (getDomainList() != null) {
            return getDomainList().getDomains().size();
        }
        return 0;
    }

}
