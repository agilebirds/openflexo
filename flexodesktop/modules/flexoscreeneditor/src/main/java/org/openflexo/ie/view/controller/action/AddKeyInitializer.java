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
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.DuplicateDKVObjectException;
import org.openflexo.foundation.dkv.EmptyStringException;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.dkv.action.AddKeyAction;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.ie.view.popups.AskNewKeyDialog;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddKeyInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddKeyInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddKeyAction.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddKeyAction> getDefaultInitializer() {
		return new FlexoActionInitializer<AddKeyAction>() {
			@Override
			public boolean run(ActionEvent e, AddKeyAction action) {
				Domain d = null;
				if (action.getFocusedObject() instanceof Domain) {
					d = (Domain) action.getFocusedObject();
				} else if (action.getFocusedObject() instanceof Domain.KeyList) {
					d = ((Domain.KeyList) action.getFocusedObject()).getDomain();
				} else {
					return false;
				}
				boolean ok = false;
				while (!ok) {
					AskNewKeyDialog newKeyDialog = new AskNewKeyDialog(getControllerActionInitializer().getIEController().getFlexoFrame(),
							getControllerActionInitializer().getIEController().getProject().getFlexoComponentLibrary(), lastCreatedKey);
					if (newKeyDialog.getStatus() == AskNewKeyDialog.VALIDATE
							|| newKeyDialog.getStatus() == AskNewKeyDialog.VALIDATE_AND_REDO) {
						String name = newKeyDialog.getNewKey();
						if (name == null) {
							return false;
						}
						if (name.trim().length() == 0) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_key_cannot_be_empty"));
							continue;
						}
						name = name.trim();
						if (name.length() > 10) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_key_cannot_be_longer_than_10_chars"));
							continue;
						}
						try {
							if (d.isKeyNameLegal(name)) {
								action.setDomain(d);
								action.setKeyName(name);
								action.setKeyDescription(newKeyDialog.getKeyDescrition());
								for (Language lang : d.getDkvModel().getLanguageList().getLanguages()) {
									action.setValueForLanguage(newKeyDialog.getValueForLanguage(lang), lang);
								}
							}
							ok = true;
							if (newKeyDialog.getStatus() == AskNewKeyDialog.VALIDATE) {
								lastCreatedKey = null;
							} else if (newKeyDialog.getStatus() == AskNewKeyDialog.VALIDATE_AND_REDO) {
								action.setContext(new Boolean(true));
							}
						} catch (DuplicateDKVObjectException e2) {
							FlexoController.notify(FlexoLocalization.localizedForKey("name_of_key_already_exists"));
						} catch (EmptyStringException e2) {
						}

						/*
						 * try { Key k = d.addKeyNamed(name); Enumeration en =
						 * d.getDkvModel().getLanguageList().getLanguages().elements();
						 * Language lg = null; while (en.hasMoreElements()) { lg =
						 * (Language) en.nextElement(); d.getValue(k,
						 * lg).setValue(newKeyDialog.getValueForLanguage(lg)); }
						 * k.setDescription(newKeyDialog.getKeyDescrition()); ok =
						 * newKeyDialog.getStatus() == AskNewKeyDialog.VALIDATE;
						 * if (!ok) lastCreatedKey = k; ((AddKeyAction)
						 * action).setNewKey(k); } catch
						 * (DuplicateDKVObjectException e) {
						 * FlexoController.notify(FlexoLocalization.localizedForKey("name_of_key_already_exists")); }
						 * catch (EmptyStringException e) { }
						 */
					} else {
						return false;
					}
				}
				return ok;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddKeyAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddKeyAction>() {
			@Override
			public boolean run(ActionEvent e, final AddKeyAction action) {
				getControllerActionInitializer().getIEController().getIESelectionManager().setSelectedObject(action.getNewKey());
				// getController().setCurrentEditedObjectAsModuleView(((AddKeyAction)
				// action).getNewKey());
				if (action.getContext() != null && action.getContext() instanceof Boolean && ((Boolean) action.getContext()).booleanValue()) {
					// Redo
					lastCreatedKey = action.getNewKey();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							AddKeyAction.actionType.makeNewAction(action.getDomain(), null, action.getEditor()).doAction();
						}
					});
				}
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DMEIconLibrary.KEY_ICON;
	}

	Key lastCreatedKey = null;

}
