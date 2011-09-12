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
package org.openflexo.foundation.stats;

import java.util.Vector;

import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;


/**
 * @author gpolet
 *
 */
public class AbstractActivityStatistics extends FlexoStatistics<AbstractActivityNode>
{

    private int actionCount = -1;
    
    private int operationCount = -1;
    
    private int realActionCount = -1;
    
    private int realOperationCount = -1;
    
    /**
     * @param object
     */
    public AbstractActivityStatistics(AbstractActivityNode object)
    {
        super(object);
        refresh();
    }
    
    public int getActionCount()
    {
        return actionCount;
    }

    public int getRealActionCount()
    {
        return realActionCount;
    }

    private void setActionCount(int actionCount)
    {
        int old = this.actionCount;
        this.actionCount = actionCount;
        if (old != 0 && old != actionCount) {
            setChanged();
            notifyObservers(new StatModification("actionCount", old, actionCount));
        }
    }

    private void setRealActionCount(int realActionCount)
    {
        int old = this.realActionCount;
        this.realActionCount = realActionCount;
        if (old != realActionCount) {
            setChanged();
            notifyObservers(new StatModification("realActionCount", old, realActionCount));
        }
    }

    public int getOperationCount()
    {
        return operationCount;
    }

    private void setOperationCount(int operationCount)
    {
        int old = this.operationCount;
        this.operationCount = operationCount;
        if (old != operationCount) {
            setChanged();
            notifyObservers(new StatModification("operationCount", old, operationCount));
        }
    }

    public int getRealOperationCount()
    {
        return realOperationCount;
    }

    private void setRealOperationCount(int realOperationCount)
    {
        int old = this.realOperationCount;
        this.realOperationCount = realOperationCount;
        if (old != realOperationCount) {
            setChanged();
            notifyObservers(new StatModification("realOperationCount", old, realOperationCount));
        }
    }

    /**
     * Overrides refresh
     * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
     */
    @Override
    public void refresh()
    {
        Vector<OperationNode> operations = getObject().getAllEmbeddedOperationNodes(); 
        setOperationCount(operations.size());
        int aCount = 0;
        int oCount = 0;
        int realACount = 0;
        for (OperationNode node : operations) {
            node.getStatistics().refresh();
            aCount+=node.getStatistics().getActionCount();
            if (!node.isBeginOrEndNode())
                oCount +=1;
            realACount += node.getStatistics().getRealActionCount();
        }
        setActionCount(aCount);
        setRealActionCount(realACount);
        setRealOperationCount(oCount);
    }

}
