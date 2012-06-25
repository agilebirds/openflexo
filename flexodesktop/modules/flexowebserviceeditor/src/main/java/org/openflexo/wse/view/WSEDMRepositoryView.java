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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.selection.SelectionManager;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.model.WSEDMEntityTableModel;
import org.openflexo.wse.model.WSEDMPackageTableModel;

/**
 * View allowing to represent/edit a DMRepository object
 * 
 * @author sguerin
 * 
 */
public class WSEDMRepositoryView extends WSEView<DMRepository> {

	protected static final Logger logger = Logger.getLogger(WSEDMRepositoryView.class.getPackage().getName());

	private WSEDMPackageTableModel packageTableModel;

	protected WSETabularView packageTable;

	private WSEDMEntityTableModel entityTableModel;

	protected WSETabularView entityTable;

	public WSEDMRepositoryView(DMRepository repository, WSEController controller) {
		super(repository, controller, "repository_($name)");
		/*    addAction(new TabularViewAction(CreateDMPackage.actionType) {
		        protected Vector getGlobalSelection()
		        {
		            return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return getDMRepository();
		        }           
		    });
		    addAction(new TabularViewAction(CreateDMEntity.actionType) {
		        protected Vector getGlobalSelection()
		        {
		            return getViewSelection();
		        }

		        protected FlexoModelObject getFocusedObject() 
		        {
		            return getSelectedDMPackage();
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
		DMRepository repository = getDMRepository();

		packageTableModel = new WSEDMPackageTableModel(repository, getWSEController().getProject());
		addToMasterTabularView(packageTable = new WSETabularView(getWSEController(), packageTableModel, 10));

		entityTableModel = new WSEDMEntityTableModel(null, getWSEController().getProject());
		addToSlaveTabularView(entityTable = new WSETabularView(getWSEController(), entityTableModel, 15), packageTable);

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, packageTable, entityTable);
	}

	public DMRepository getDMRepository() {
		return getModelObject();
	}

	public DMPackage getSelectedDMPackage() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMPackage) {
			return (DMPackage) selection.firstElement();
		}
		if (getSelectedDMEntity() != null) {
			return getSelectedDMEntity().getPackage();
		}
		return null;
	}

	public DMEntity getSelectedDMEntity() {
		SelectionManager sm = getWSEController().getSelectionManager();
		Vector<FlexoModelObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMEntity) {
			return (DMEntity) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getEntityTable() {
		return entityTable;
	}

	public WSETabularView getPackageTable() {
		return packageTable;
	}

}
