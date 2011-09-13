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

import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;

/**
 * @author gpolet
 *
 */
public class ComponentLibraryStatistics extends FlexoStatistics<FlexoComponentLibrary>
{
    private int pageCount = -1;
    
    private int popupCount = -1;
    
    private int tabCount = -1;
    
    /**
     * 
     */
    public ComponentLibraryStatistics(FlexoComponentLibrary library)
    {
        super(library);
        refresh();
    }

    public int getPageCount()
    {
        return pageCount;
    }

    private void setPageCount(int pageCount)
    {
        int old = this.pageCount;
        this.pageCount = pageCount;
        if (old != pageCount) {
            setChanged();
            notifyObservers(new StatModification("pageCount", old, pageCount));
        }
    }

    public int getPopupCount()
    {
        return popupCount;
    }

    private void setPopupCount(int popupCount)
    {
        int old = this.popupCount;
        this.popupCount = popupCount;
        if (old != popupCount) {
            setChanged();
            notifyObservers(new StatModification("popupCount", old, popupCount));
        }
    }

    public int getTabCount()
    {
        return tabCount;
    }

    private void setTabCount(int tabCount)
    {
        int old = this.tabCount;
        this.tabCount = tabCount;
        if (old != tabCount) {
            setChanged();
            notifyObservers(new StatModification("tabCount", old, tabCount));
        }
    }

    /**
     * Overrides refresh
     * @see org.openflexo.foundation.stats.FlexoStatistics#refresh()
     */
    @Override
    public void refresh()
    {
        setPageCount(getObject().getOperationsComponentList().size());
        setPopupCount(getObject().getPopupsComponentList().size());
        setTabCount(getObject().getTabComponentList().size());
    }

}
