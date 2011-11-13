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

import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.ws.WSRepositoryFolder;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.controller.WSESelectionManager;
import org.openflexo.wse.model.WSERepositoryTableModel;

/**
 * View allowing to represent/edit a DMRepositoryFolder object
 * 
 * @author sguerin
 * 
 */
public class WSERepositoryFolderView extends WSEView<WSRepositoryFolder> {

	private static final Logger logger = Logger.getLogger(WSERepositoryFolderView.class.getPackage().getName());

	private WSETabularView repositoriesTable;
	private WSERepositoryTableModel repositoriesTableModel;

	public WSERepositoryFolderView(WSRepositoryFolder repositoryList, WSEController controller) {
		super(repositoryList, controller, repositoryList.getName());
		/*   addAction(new TabularViewAction(CreateDMRepository.actionType,"add_repository") {
		       protected Vector getGlobalSelection()
		       {
		           return getViewSelection();
		       }

		       protected FlexoModelObject getFocusedObject() 
		       {
		           return getWSRepositoryFolder();
		       }           
		   });
		   addAction(new TabularViewAction(UpdateDMRepository.actionType) {
		       protected Vector getGlobalSelection()
		       {
		           return getViewSelection();
		       }

		       protected FlexoModelObject getFocusedObject() 
		       {
		           return getSelectedDMRepository();
		       }           
		   });
		  addAction(new TabularViewAction(DMDelete.actionType,"delete_repository") {
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
		repositoriesTableModel = new WSERepositoryTableModel(getWSRepositoryFolder().getWSGroup(), getWSEController().getProject());
		addToMasterTabularView(repositoriesTable = new WSETabularView(getWSEController(), repositoriesTableModel, 15));

		return repositoriesTable;
	}

	public WSRepositoryFolder getWSRepositoryFolder() {
		return getModelObject();
	}

	public DMRepository getSelectedDMRepository() {
		WSESelectionManager sm = getWSEController().getWSESelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof DMRepository)) {
			return (DMRepository) selection.firstElement();
		}
		return null;
	}

	public WSETabularView getRepositoriesTable() {
		return repositoriesTable;
	}

}
