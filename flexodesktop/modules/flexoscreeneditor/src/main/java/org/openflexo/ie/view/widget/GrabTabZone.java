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
package org.openflexo.ie.view.widget;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEContainer;


;

/**
 * @author gpolet
 * 
 */
public class GrabTabZone extends JPanel implements IEContainer
{

    private IESequenceTab tabs;

    /**
     * 
     */
    public GrabTabZone(IESequenceTab tabs)
    {
        super();
        this.tabs = tabs;
        setOpaque(false);
        //setBackground(Color.WHITE);
    }

    public IESequenceTab getTabs()
    {
        return tabs;
    }

    /**
     * Overrides getContainerModel
     * @see org.openflexo.ie.view.IEContainer#getContainerModel()
     */
    @Override
	public IEWidget getContainerModel()
    {
        return getTabs();
    }
    
    /**
     * Overrides getPreferredSize
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.height = 10;
        return d;
    }

	@Override
	public IEWOComponent getWOComponent() {
		return tabs.getWOComponent();
	}
}
