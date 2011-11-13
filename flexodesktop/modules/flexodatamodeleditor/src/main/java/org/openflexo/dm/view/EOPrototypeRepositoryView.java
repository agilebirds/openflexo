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
package org.openflexo.dm.view;

import java.util.Vector;

import javax.swing.JComponent;

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMEOPrototypeTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.controller.DMSelectionManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.action.CreateDMPrototype;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class EOPrototypeRepositoryView extends DMView<EOPrototypeRepository> {

	private DMEOPrototypeTableModel eoPrototypeTableModel;

	private DMTabularView eoPrototypeTable;

	public EOPrototypeRepositoryView(EOPrototypeRepository repository, DMController controller) {
		super(repository, controller, "eo_prototypes");

		addAction(new TabularViewAction(CreateDMPrototype.actionType, controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return getEOPrototypeRepository();
			}
		});
		addAction(new TabularViewAction(DMDelete.actionType, "delete_prototype", controller.getEditor()) {
			@Override
			protected Vector getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return null;
			}
		});

		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {
		eoPrototypeTableModel = new DMEOPrototypeTableModel(getEOPrototypeRepository(), getDMController().getProject());
		addToMasterTabularView(eoPrototypeTable = new DMTabularView(getDMController(), eoPrototypeTableModel));

		return eoPrototypeTable;
	}

	public EOPrototypeRepository getEOPrototypeRepository() {
		return getDMObject();
	}

	public DMEOPrototype getSelectedDMEOPrototype() {
		DMSelectionManager sm = getDMController().getDMSelectionManager();
		Vector selection = sm.getSelection();
		if ((selection.size() == 1) && (selection.firstElement() instanceof DMEOPrototype)) {
			return (DMEOPrototype) selection.firstElement();
		}
		return null;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

}
