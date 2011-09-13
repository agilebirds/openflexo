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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.foundation.ie.widget.AbstractInnerTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


/**
 * @author bmangez
 * 
 */
public abstract class AbstractInnerTableWidgetView<T extends AbstractInnerTableWidget> extends IEWidgetView<T>
{

    /**
     * @param model
     */
    public AbstractInnerTableWidgetView(IEController ieController, T model, boolean addDnDSupport, IEWOComponentView componentView)
    {
        super(ieController, model, addDnDSupport,componentView);
        setOpaque(false);
    }

    // ==========================================================================
    // ============================= Accessors
    // ==================================
    // ==========================================================================
    public Color getBackgroundColor()
    {
    	IESequenceWidget sequence = getModel().getAncestorOfClass(IESequenceWidget.class);
        if (sequence!=null)
        	return sequence.getBackground();
        return super.getBackground();
    }

    @Override
	public void setDefaultBorder()
    {
        if (getModel().getIsRootOfPartialComponent() && getParent() != null) {
            ((JPanel) getParent()).setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
        } else
        	setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }


    @Override
	public void delete()
    {
        if (getModel().getParent() != null)
            getModel().getParent().deleteObserver(this);
        super.delete();
    }

}
