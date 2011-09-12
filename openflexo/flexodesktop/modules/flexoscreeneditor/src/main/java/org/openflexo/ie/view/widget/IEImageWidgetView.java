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

import java.awt.Image;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ie.widget.IEDynamicImage;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


public class IEImageWidgetView extends IEButtonWidgetView {

	private static final Image image = SEIconLibrary.DEFAULT_IMAGE_ICON.getImage();
	
	public IEImageWidgetView(IEController ieController, IEDynamicImage model, boolean addDnDSupport, IEWOComponentView componentView) {
		super(ieController, model, addDnDSupport, componentView);
	}

	public ImageIcon getIcon(){
		return SEIconLibrary.DEFAULT_IMAGE_ICON;
	}
	
	@Override
	protected Image getImage() {
		return image;
	}
	
}
