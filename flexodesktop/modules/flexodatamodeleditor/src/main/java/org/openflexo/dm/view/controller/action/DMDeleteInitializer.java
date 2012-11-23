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
package org.openflexo.dm.view.controller.action;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.dm.view.DMView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DMDeleteInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DMDeleteInitializer(DMControllerActionInitializer actionInitializer) {
		super(DMDelete.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DMDelete> getDefaultInitializer() {
		return new FlexoActionInitializer<DMDelete>() {
			@Override
			public boolean run(EventObject e, DMDelete action) {
				// Selection is empty, nothing to delete, forget it
				if (action.getObjectsToDelete().size() == 0) {
					return false;
				}
				// Remember parent of last deleted object, in order to
				// eventually reselect it after deletion performance
				DMObject lastDeletedParent = null;
				DMObject lastDeleted = action.getObjectsToDelete().lastElement();
				if (lastDeleted != null) {
					lastDeletedParent = lastDeleted.getParent();
					action.setContext(lastDeletedParent);
				}
				// Is there something to ask about deletion ?
				boolean askConfirmation = true;
				for (Enumeration en = action.getObjectsToDelete().elements(); en.hasMoreElements();) {
					DMObject next = (DMObject) en.nextElement();
					if (next instanceof DMEOModel && ((DMEOModel) next).getEOModelFile() != null) {
						DMEOModel dmEOModel = (DMEOModel) next;
						String dontDeleteFile = FlexoLocalization.localizedForKey("dont_delete_file");
						String deleteFile = FlexoLocalization.localizedForKey("delete_file");
						String cancel = FlexoLocalization.localizedForKey("cancel");
						String question = dmEOModel.getName() + " : " + FlexoLocalization.localizedForKey("also_delete_file") + " "
								+ dmEOModel.getEOModelFile().getFile().getName() + " ?";
						int status = FlexoController.selectOption(question, new String[] { dontDeleteFile, deleteFile, cancel }, cancel);
						if (status == 0) { // DONT DELETE FILE
							action.setDeletionContextForObject(new Boolean(false), dmEOModel);
							askConfirmation = false;
						} else if (status == 1) { // DELETE FILE
							action.setDeletionContextForObject(new Boolean(true), dmEOModel);
							askConfirmation = false;
						} else { // Abort deletion process
							return false;
						}
					} else if (next instanceof DMEORepository) {
						DMEORepository dmEORepository = (DMEORepository) next;
						if (dmEORepository.getDMEOModels().size() > 0) {
							String dontDeleteFiles = FlexoLocalization.localizedForKey("dont_delete_files");
							String deleteFiles = FlexoLocalization.localizedForKey("delete_files");
							String cancel = FlexoLocalization.localizedForKey("cancel");
							String fileList = "";
							boolean isFirst = true;
							for (Enumeration en2 = dmEORepository.getDMEOModels().elements(); en2.hasMoreElements();) {
								DMEOModel eoModel = (DMEOModel) en2.nextElement();
								fileList += (isFirst ? "" : ", ") + eoModel.getName();
								isFirst = false;
							}
							String question = dmEORepository.getName() + " : "
									+ FlexoLocalization.localizedForKey("also_delete_eomodel_files") + " " + fileList + " ?";
							int status = FlexoController.selectOption(question, new String[] { dontDeleteFiles, deleteFiles, cancel },
									cancel);
							if (status == 0) { // DONT DELETE FILES
								action.setDeletionContextForObject(new Boolean(false), dmEORepository);
								askConfirmation = false;
							} else if (status == 1) { // DELETE FILES
								action.setDeletionContextForObject(new Boolean(true), dmEORepository);
								askConfirmation = false;
							} else { // Abort deletion process
								return false;
							}
						}
					} else if (next instanceof ExternalRepository) {
						ExternalRepository jarRepository = (ExternalRepository) next;
						String dontDeleteFile = FlexoLocalization.localizedForKey("dont_delete_file");
						String deleteFile = FlexoLocalization.localizedForKey("delete_file");
						String cancel = FlexoLocalization.localizedForKey("cancel");
						String question = jarRepository.getName() + " : " + FlexoLocalization.localizedForKey("also_delete_file") + " "
								+ jarRepository.getJarFile().getFile().getName() + " ?";
						int status = FlexoController.selectOption(question, new String[] { dontDeleteFile, deleteFile, cancel }, cancel);
						if (status == 0) { // DONT DELETE FILE
							action.setDeletionContextForObject(new Boolean(false), jarRepository);
							askConfirmation = false;
						} else if (status == 1) { // DELETE FILE
							action.setDeletionContextForObject(new Boolean(true), jarRepository);
							askConfirmation = false;
						} else { // Abort deletion process
							return false;
						}
					}

				}
				boolean doIt = !askConfirmation;
				// Finally ask for a confirmation if none was already requested
				if (askConfirmation) {
					if (action.getObjectsToDelete().size() == 1) {
						DMObject objectToDelete = action.getObjectsToDelete().firstElement();
						if (objectToDelete instanceof DMRepository) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_repository"));
						} else if (objectToDelete instanceof DMPackage) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_package"));
						} else if (objectToDelete instanceof DMEOModel) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_eo_model"));
						} else if (objectToDelete instanceof DMEntity) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_entity"));
						} else if (objectToDelete instanceof DMEOAttribute) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_attribute"));
						} else if (objectToDelete instanceof DMEORelationship) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_relationship"));
						} else if (objectToDelete instanceof DMProperty) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_property"));
						} else if (objectToDelete instanceof DMMethod) {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_method"));
						} else {
							doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_object"));
						}
					} else {
						doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
					}
				}
				// Look if any toc entry is related to one or more objects of the DataModel.
				if (doIt) {
					Vector<DMObject> objects = action.getObjectsToDelete();
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
				}
				// Otherwise return false
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DMDelete> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DMDelete>() {
			@Override
			public boolean run(EventObject e, DMDelete action) {
				getControllerActionInitializer().getSelectionManager().resetSelection();
				// getDMSelectionManager().processDeletionOfSelected();
				ModuleView<?> view = getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
				if (view instanceof DMView) {
					DMObject lastDeletedParent = (DMObject) action.getContext();
					if (lastDeletedParent != null && !lastDeletedParent.isDeleted()) {
						((DMView) view).tryToSelect(lastDeletedParent);
					}
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0);
	}

}
