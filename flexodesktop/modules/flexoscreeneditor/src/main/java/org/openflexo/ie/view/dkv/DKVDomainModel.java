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
package org.openflexo.ie.view.dkv;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.rm.FlexoProject;


/**
 * @author gpolet
 *
 */
public class DKVDomainModel extends AbstractModel<Domain,FlexoModelObject>
{

    /**
     * @param model
     * @param project
     */
    public DKVDomainModel(Domain model, FlexoProject project)
    {
        super(model, project);
    }

    /**
     * Overrides elementAt
     * @see org.openflexo.components.tabular.model.AbstractModel#elementAt(int)
     */
    @Override
    public FlexoModelObject elementAt(int row)
    {
        return getModel().getKeys().elementAt(row);
    }

    /**
     * Overrides getRowCount
     * @see org.openflexo.components.tabular.model.AbstractModel#getRowCount()
     */
    @Override
    public int getRowCount()
    {
        return getModel().getKeys().size();
    }

}
