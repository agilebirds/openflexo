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
package org.openflexo.ie.view.controller;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.ie.view.DropZoneTopComponent;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.widget.ButtonPanel;
import org.openflexo.ie.view.widget.DropTabZone;
import org.openflexo.ie.view.widget.DropTableZone;
import org.openflexo.ie.view.widget.IETDWidgetView;
import org.openflexo.ie.view.widget.IEWidgetView;
import org.openflexo.selection.ContextualMenuManager;
import org.openflexo.view.controller.FlexoController;

public class IEContextualMenuManager extends ContextualMenuManager {

	public IEContextualMenuManager(IESelectionManager selectionManager, FlexoController controler) {
		super(selectionManager, controler);
	}

	@Override
	public FlexoObject getFocusedObject(Component focusedComponent, MouseEvent e) {
		if (focusedComponent instanceof IETDWidgetView) {
			return ((IETDWidgetView) focusedComponent).td();
		}
		if (focusedComponent instanceof IEWidgetView) {
			return ((IEWidgetView) focusedComponent).getObject();
		}
		if (focusedComponent instanceof IEWOComponentView) {
			return ((IEWOComponentView) focusedComponent).getModel();
		}
		if (focusedComponent instanceof DropZoneTopComponent) {
			return ((DropZoneTopComponent) focusedComponent).getContainerModel();
		}
		if (focusedComponent instanceof DropTabZone) {
			return ((DropTabZone) focusedComponent).getContainerModel();
		}
		if (focusedComponent instanceof DropTabZone) {
			return ((DropTabZone) focusedComponent).getContainerModel();
		}
		if (focusedComponent instanceof DropTableZone) {
			return ((DropTableZone) focusedComponent).getContainerModel();
		}
		if (focusedComponent instanceof ButtonPanel) {
			return ((ButtonPanel) focusedComponent).getContainerModel();
		}
		return super.getFocusedObject(focusedComponent, e);
	}

}
