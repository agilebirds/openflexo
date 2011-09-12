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
package org.openflexo.ie.view.palette;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;


import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.action.ImportImage;
import org.openflexo.foundation.ie.dm.PaletteHasChanged;
import org.openflexo.foundation.ie.palette.FlexoIEPalette;
import org.openflexo.foundation.ie.palette.FlexoIEPalette.FlexoIEPaletteWidget;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.palette.PalettePanel;

public class IEPalettePanel extends PalettePanel implements FlexoObserver
{

	private FlexoIEPalette<? extends FlexoIEPaletteWidget> model;

    public FlexoIEPalette<? extends FlexoIEPaletteWidget> getModel() {
		return model;
	}

	public IEPalettePanel(IEPalette palette, FlexoIEPalette<? extends FlexoIEPaletteWidget> model, String keyName)
    {
        super(palette);
        setName(FlexoLocalization.localizedForKey(keyName,this));
    	setBackground(Color.WHITE);
    	if (ToolBox.getPLATFORM()==ToolBox.MACOS)
    		setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
    	else
    		setLayout(new FlowLayout(FlowLayout.LEFT));
        this.model = model;
        model.addObserver(this);
        refresh();
    }

    public void switchCSS() {
    	for (int i = 0; i < getComponents().length; i++) {
			Component c = getComponents()[i];
			if (c instanceof IEDSWidgetView) {
				((IEDSWidgetView)c)._model.refresh(getPalette().getController().getProject().getCssSheet());
			}
		}
    }

    private void refresh() {
    	removeAll();
    	for (FlexoIEPalette<? extends FlexoIEPaletteWidget>.FlexoIEPaletteWidget w : model.getWidgets()) {
        	IEDSWidget dsWidget = new IEDSWidget(w,model.resizeScreenshots(),getPalette().getController().getProject().getCssSheet());
        	add(new IEDSWidgetView((IEController) getPalette().getController(), dsWidget, w.canDeleteWidget()));
        	addToPaletteElements(dsWidget);
		}
    	validate();
    	repaint();
    }

    @Override
	public IEPalettePanel delete(){
    	model.deleteObserver(this);
    	super.delete();
    	return this;
    }

    @Override
	public void editPalette()
    {
        logger.info("EditIEPalette");
        ImportImage.actionType.makeNewAction(null, null,getPalette().getController().getEditor()).doAction();
    }

    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (dataModification instanceof PaletteHasChanged) {
        	refresh();
        }
    }


}
