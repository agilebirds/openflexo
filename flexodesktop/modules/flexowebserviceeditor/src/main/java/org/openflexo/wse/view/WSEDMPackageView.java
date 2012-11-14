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
import javax.swing.JSplitPane;

import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSEDMEntityTableModel;
import org.openflexo.wse.model.WSEDMPropertyTableModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEDMPackageView extends WSEView<DMPackage> {

	private static final Logger logger = Logger.getLogger(WSEDMPackageView.class.getPackage().getName());

	private WSEDMEntityTableModel entityTableModel;
	protected WSETabularView entityTable;

	private WSEDMPropertyTableModel propertyTableModel;
	protected WSETabularView propertyTable;

	public WSEDMPackageView(DMPackage aPackage, WSEController controller) {
		super(aPackage, controller, "package_($localizedName)");
		/* addAction(new TabularViewAction(CreateDMEntity.actionType) {
		     protected Vector getGlobalSelection()
		     {
		         return getViewSelection();
		     }

		     protected FlexoModelObject getFocusedObject() 
		     {
		         return getDMPackage();
		     }           
		 });
		 addAction(new TabularViewAction(CreateDMProperty.actionType) {
		     protected Vector getGlobalSelection()
		     {
		         return getViewSelection();
		     }

		     protected FlexoModelObject getFocusedObject() 
		     {
		         return getSelectedDMEntity();
		     }           
		 });
		 addAction(new TabularViewAction(CreateDMMethod.actionType) {
		     protected Vector getGlobalSelection()
		     {
		         return getViewSelection();
		     }

		     protected FlexoModelObject getFocusedObject() 
		     {
		         return getSelectedDMEntity();
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
		DMPackage aPackage = getDMPackage();

		entityTableModel = new WSEDMEntityTableModel(aPackage, getWSEController().getProject());
		addToMasterTabularView(entityTable = new WSETabularView(getWSEController(), entityTableModel, 10));

		propertyTableModel = new WSEDMPropertyTableModel(null, getWSEController().getProject());
		addToSlaveTabularView(propertyTable = new WSETabularView(getWSEController(), propertyTableModel, 7), entityTable);

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, entityTable, propertyTable);
	}

	public DMPackage getDMPackage() {
		return getModelObject();
	}

	public DMEntity getSelectedDMEntity() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMEntity) {
			return (DMEntity) selection.firstElement();
		}
		if (getSelectedDMProperty() != null) {
			return getSelectedDMProperty().getEntity();
		}

		return null;
	}

	public DMProperty getSelectedDMProperty() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMProperty) {
			return (DMProperty) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getEntityTable() {
		return entityTable;
	}

	public WSETabularView getPropertyTable() {
		return propertyTable;
	}

}
