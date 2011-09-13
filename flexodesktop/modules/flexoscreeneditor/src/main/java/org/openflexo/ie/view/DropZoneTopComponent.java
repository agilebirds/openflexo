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
package org.openflexo.ie.view;

import java.util.logging.Logger;

import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.widget.IESequenceWidgetWidgetView;


/**
 * @author bmangez
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DropZoneTopComponent extends IESequenceWidgetWidgetView implements IEContainer, GraphicalFlexoObserver/* ,ComponentListener */
{

    private static final Logger logger = Logger.getLogger(DropZoneTopComponent.class.getPackage().getName());

    public DropZoneTopComponent(IEController ieController, IESequenceWidget model,IEWOComponentView componentView)
    {
        super(ieController,model,model.isSubsequence(),componentView);
    }

    @Override
    public int getVerticalGap() {
    	return 5;
    }
    
    @Override
	public int getAvailableWidth() {
    	return getMaximumSize().width;
    }
    
    @Override
    protected void handleWidgetInserted(IEWidget widget) {
    	super.handleWidgetInserted(widget);
    	_componentView.validate();
        _componentView.repaint();
        _componentView.updatePreferredSize();
    }
    
    @Override
    protected void handleWidgetRemoved(IEWidget widget) {
    	super.handleWidgetRemoved(widget);
    	_componentView.validate();
        _componentView.repaint();
        _componentView.updatePreferredSize();
    }
    
}
