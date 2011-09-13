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

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 *
 */
public abstract class FlexoStatistics<T extends FlexoModelObject> extends FlexoObservable
{
    private T object;
    
    private boolean includeBeginEndNode=false;
    
    /**
     * 
     */
    public FlexoStatistics(T object)
    {
        super();
        this.object = object;
        refresh();
    }

    public T getObject()
    {
        return object;
    }
    
    public String getInfoLabel()
    {
        return "<html><body>"+FlexoLocalization.localizedForKey("use_the_refresh_button_to_update_the")+"<br>"+getLocalizedName()+"</body></html>";
    }
    
    public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey("statistics_of")+" "+FlexoLocalization.localizedForKey(getObject().getClassNameKey());
    }

    /**
     * Overrides setChanged
     * @see org.openflexo.foundation.FlexoObservable#setChanged()
     */
    @Override
    protected synchronized void setChanged()
    {
        getObject().setChanged(false);
    }
    
    /**
     * Overrides notifyObservers
     * @see org.openflexo.foundation.FlexoObservable#notifyObservers(org.openflexo.foundation.DataModification)
     */
    @Override
    public void notifyObservers(DataModification arg)
    {
        getObject().notifyObservers(arg);
    }
    
    public abstract void refresh();

    public boolean getIncludeBeginEndNode()
    {
        return includeBeginEndNode;
    }

    public void setIncludeBeginEndNode(boolean includeBeginEndNode)
    {
        this.includeBeginEndNode = includeBeginEndNode;
        setChanged();
        notifyObservers(new StatModification("includeBeginEndNode",!includeBeginEndNode,includeBeginEndNode));
    }
}
