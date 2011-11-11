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
package org.openflexo.wse.view;

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSEDMPropertyTableModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEDMEntityView extends WSEView<DMEntity> {

	private static final Logger logger = Logger.getLogger(WSEDMEntityView.class.getPackage().getName());

	private WSEDMPropertyTableModel propertyTableModel;
	protected WSETabularView propertyTable;

	public WSEDMEntityView(DMEntity entity, WSEController controller) {
		super(entity, controller, "entity_($name)");
		/*  addAction(new TabularViewAction(CreateDMProperty.actionType) {
		      protected Vector getGlobalSelection()
		      {
		          return getViewSelection();
		      }

		      protected FlexoModelObject getFocusedObject() 
		      {
		          return getDMEntity();
		      }           
		  });
		  addAction(new TabularViewAction(CreateDMMethod.actionType) {
		      protected Vector getGlobalSelection()
		      {
		          return getViewSelection();
		      }

		      protected FlexoModelObject getFocusedObject() 
		      {
		          return getDMEntity();
		      }           
		  });
		  addAction(new TabularViewAction(DMDelete.actionType) {
		      protected Vector getGlobalSelection()
		      {
		           return getViewSelection();
		      }

		      protected FlexoModelObject getFocusedObject() 
		      {
		          return null;
		      }           
		  });*/
		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {
		DMEntity entity = getDMEntity();

		propertyTableModel = new WSEDMPropertyTableModel(entity, getWSEController().getProject());
		addToMasterTabularView(propertyTable = new WSETabularView(getWSEController(), propertyTableModel, 15));

		return propertyTable;
	}

	public DMEntity getDMEntity() {
		return getModelObject();
	}

	public DMProperty getSelectedDMProperty() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof DMProperty)) {
			return (DMProperty) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getPropertyTable() {
		return propertyTable;
	}

}
