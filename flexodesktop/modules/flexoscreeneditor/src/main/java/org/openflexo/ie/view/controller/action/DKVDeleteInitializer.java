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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.action.DKVDelete;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.SelectionManagingController;

public class DKVDeleteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DKVDeleteInitializer(IEControllerActionInitializer actionInitializer) {
		super(DKVDelete.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DKVDelete> getDefaultInitializer() {
		return new FlexoActionInitializer<DKVDelete>() {
			@Override
			public boolean run(ActionEvent e, DKVDelete action) {
				if (!FlexoController.confirmWithWarning(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"))) {
					return false;
				}
				Vector<FlexoModelObject> v = (Vector) action.getGlobalSelection().clone();
				if (action.getFocusedObject() != null && !v.contains(action.getFocusedObject())) {
					v.add(action.getFocusedObject());
				}
				(action).setObjectsToDelete(v);
				Vector<FlexoModelObject> objects = action.getGlobalSelectionAndFocusedObject();
				Vector<TOCEntry> tocEntries = new Vector<TOCEntry>();
				for (FlexoModelObject object : objects) {
					if (!object.isDeleted()) {
						for (FlexoModelObjectReference ref : object.getReferencers()) {
							if (ref.getOwner() instanceof TOCEntry) {
								tocEntries.add((TOCEntry) ref.getOwner());
							}
						}
					}
				}
				if (tocEntries.size() == 0) {
					return true;
				}
				CheckboxListParameter<TOCEntry>[] def = new CheckboxListParameter[1];
				def[0] = new CheckboxListParameter<TOCEntry>("entries", "select_entries_to_delete", tocEntries, new Vector<TOCEntry>());
				def[0].setFormatter("displayString");
				AskParametersDialog dialog = AskParametersDialog
						.createAskParametersDialog(
								getProject(),
								getController().getFlexoFrame(),
								FlexoLocalization.localizedForKey("select_toc_entries_to_delete"),
								FlexoLocalization
										.localizedForKey("<html>the_following_toc_entries_are_linked_to_the_objects_you_are_about_to_delete.<br>select_the_ones_you_would_like_to_delete</html>"),
								def);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					Vector<TOCEntry> entriesToDelete = def[0].getValue();
					action.setEntriesToDelete(entriesToDelete);
					return true;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DKVDelete> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DKVDelete>() {
			@Override
			public boolean run(ActionEvent e, DKVDelete action) {
				Domain domainToSelect = null;
				if (action.getFocusedObject() instanceof Key) {
					if (((Key) action.getFocusedObject()).getDomain() != null && ((Key) action.getFocusedObject()).getDomain() != null) {
						domainToSelect = ((Key) action.getFocusedObject()).getDomain();
					}
				}
				if (domainToSelect == null && action.getGlobalSelection() != null && action.getGlobalSelection().size() > 0
						&& action.getGlobalSelection().firstElement() instanceof Key) {
					domainToSelect = ((Key) action.getGlobalSelection().firstElement()).getDomain();
				}
				if (domainToSelect != null) {
					((SelectionManagingController) getController()).getSelectionManager().setSelectedObject(domainToSelect);
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
