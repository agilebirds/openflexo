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
package org.openflexo.foundation.wkf.dm;

import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractNode;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class NodeInserted extends WKFDataModification
{

    private AbstractNode _insertedNode;
    private FlexoPetriGraph _pg;
    private WKFObject _container;

    public NodeInserted(AbstractNode insertedNode, FlexoPetriGraph pg, WKFObject container)
    {
        super(null, insertedNode);
        _insertedNode = insertedNode;
        _pg = pg;
        _container = container;
    }

    public WKFObject getContainer()
    {
        return _container;
    }

    public FlexoPetriGraph getPetriGraph()
    {
        return _pg;
    }

   public AbstractNode getInsertedNode()
    {
        return _insertedNode;
    }

    @Override
	public String toString()
    {
        return "NodeInserted: " + _insertedNode + " in " + _container;
    }
}
