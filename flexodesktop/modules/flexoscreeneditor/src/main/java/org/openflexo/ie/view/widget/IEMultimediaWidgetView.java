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
import java.awt.Graphics;
import java.awt.Image;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.widget.IEMultimediaWidget;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.swing.MouseResizer;
import org.openflexo.swing.MouseResizer.MouseResizerDelegate;


public class IEMultimediaWidgetView extends AbstractInnerTableWidgetView<IEMultimediaWidget> implements MouseResizerDelegate{

	private final MouseResizer resizer;
	private static final Image image = SEIconLibrary.MULTIMEDIA_ICON.getImage();

	public IEMultimediaWidgetView(IEController ieController, IEMultimediaWidget model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
        setBorder(null);
        setOpaque(false);
        resizer = new MouseResizer(this,this);

	}

    @Override
    public boolean isDragEnabled() {
    	if (resizer.getMode()!=MouseResizer.ResizeMode.NONE) {
			return false;
		}
    	return super.isDragEnabled();
    }
    
    @Override
    public void paint(Graphics g) {
    	g.drawImage(image, 0, 0, getModel().getWidthPixel(), getModel().getHeightPixel(), null);
    	super.paint(g);
    }
    
	@Override
	public void resizeBy(int deltaX, int deltaY) {
	}

	@Override
	public void resizeDirectlyBy(int deltaX, int deltaY) {
		Dimension d = new Dimension(getModel().getWidthPixel(),getModel().getHeightPixel());
		if (deltaY!=0) {
			getModel().setHeightPixel(d.height+deltaY);
		}
		if (deltaX!=0) {
			getModel().setWidthPixel(d.width+deltaX);
		}
	}

    /**
     * Overrides getPreferredSize
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
    	if (getHoldsNextComputedPreferredSize()){
        	Dimension storedSize = storedPrefSize();
            if(storedSize!=null) {
				return storedSize;
			}
        }
        Dimension d = new Dimension(getModel().getWidthPixel(),getModel().getHeightPixel());
        if (getHoldsNextComputedPreferredSize()) {
			storePrefSize(d);
		}
        return d;
    }
    
    @Override
    public void update(FlexoObservable observable, DataModification dataModification) {
        if ((getModel()==observable) && ((dataModification.propertyName()=="heightPixel") || (dataModification.propertyName()=="widthPixel"))){
	    	doLayout();
	    	paintImmediately(getBounds());
        }
    	super.update(observable, dataModification);
    }
}
