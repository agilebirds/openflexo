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
package org.openflexo.foundation.wkf.utils;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.wkf.dm.OperationComponentHasBeenSet;
import org.openflexo.foundation.wkf.node.OperationNode;


public class OperationAssociatedWithComponentSuccessfully extends /*FlexoDynamicNotification*/ FlexoException
{

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OperationAssociatedWithComponentSuccessfully.class.getPackage().getName());

	private OperationComponentHasBeenSet dm;

    private OperationNode node;

    private OperationComponentDefinition cd;
    
    private OperationComponentInstance previousComponentInstance;

    public OperationAssociatedWithComponentSuccessfully(OperationComponentHasBeenSet dm, OperationNode node, OperationComponentDefinition cd, OperationComponentInstance previousComponentInstance)
    {
        super();
        this.dm = dm;
        this.cd = cd;
        this.node = node;
        this.previousComponentInstance = previousComponentInstance;
    }

    public OperationComponentDefinition getCd()
    {
        return cd;
    }

    public OperationComponentHasBeenSet getDm()
    {
        return dm;
    }

    public OperationNode getNode()
    {
        return node;
    }

    public OperationComponentInstance getPreviousComponentInstance()
    {
        return previousComponentInstance;
    }

}