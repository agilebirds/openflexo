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

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;



/**
 * @author gpolet
 *
 */
public class ProcessStatistics extends FlexoStatistics<FlexoProcess>
{

    private int actionCount = -1;
    
    private int operationCount = -1;
    
    private int activityCount = -1;

	private int realActionCount = -1;

	private int realOperationCount = -1;

	private int realActivityCount = -1;
    
    /**
	 * 
	 */
    public ProcessStatistics(FlexoProcess process)
    {
        super(process);
        refresh();
    }

    public int getRealActionCount()
    {
        return realActionCount;
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

    public int getRealActivityCount()
    {
        return realActivityCount;
    }

    private void setRealActivityCount(int realActivityCount)
    {
        int old = this.realActivityCount;
        this.realActivityCount = realActivityCount;
        if (old != realActivityCount) {
            setChanged();
            notifyObservers(new StatModification("realActivityCount", old, realActivityCount));
        }
    }

    public int getActionCount()
    {
        return actionCount;
    }

    private void setActionCount(int actionCount)
    {
        int old = this.actionCount;
        this.actionCount = actionCount;
        if (old != actionCount) {
            setChanged();
            notifyObservers(new StatModification("actionCount", old, actionCount));
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

    public int getActivityCount()
    {
        return activityCount;
    }

    private void setActivityCount(int activityCount)
    {
        int old = this.activityCount;
        this.activityCount = activityCount;
        if (old != activityCount) {
            setChanged();
            notifyObservers(new StatModification("activityCount", old, activityCount));
        }
    }

    /**
     * Overrides refresh
     * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
     */
    @Override
    public void refresh()
    {
        Vector<AbstractActivityNode> activities = getObject().getAllEmbeddedAbstractActivityNodes();
        setActivityCount(activities.size());
        int aCount = 0;
        int oCount = 0;
        
        int realACount = 0;
        int realOCount = 0;
        
        int actCount = 0;
        for (AbstractActivityNode node : activities) {
            node.getStatistics().refresh();
            if (!node.isBeginOrEndNode())
                actCount += 1;
            aCount+=node.getStatistics().getActionCount();
            oCount+=node.getStatistics().getOperationCount();
            
            realACount+=node.getStatistics().getRealActionCount();
            realOCount+=node.getStatistics().getRealOperationCount();
        }
        setActionCount(aCount);
        setOperationCount(oCount);
        setRealActionCount(realACount);
        setRealOperationCount(realOCount);
        setRealActivityCount(actCount);
    }

}
