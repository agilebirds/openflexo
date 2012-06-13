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
package org.openflexo.wkf.controller.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuItem;
import org.openflexo.wkf.processeditor.ProcessView;
import org.openflexo.wkf.view.listener.WKFKeyEventListener;

public class WKFDeleteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WKFDeleteInitializer(WKFControllerActionInitializer actionInitializer) {
		super(WKFDelete.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<WKFDelete> getDefaultInitializer() {
		return new FlexoActionInitializer<WKFDelete>() {
			@Override
			public boolean run(ActionEvent e, WKFDelete action) {
				logger.info("Delete in WKF");

				if (!getModule().isActive()) {
					return false;
				}
				Vector v = action.getObjectsToDelete();
				if (v.size() == 0) {
					return false;
				}

				boolean doIt = false;
				if (v.size() == 1 && v.firstElement() instanceof FlexoProcess) {

					// Cannot delete a process by right-clicking on process view
					if (action.getInvoker() instanceof ProcessView) {
						logger.fine("Refuse to delete a process by right-clicking on process view");
						Toolkit.getDefaultToolkit().beep();
						return false;
					}
					// Cannot delete a process by pressing delete key
					if (action.getInvoker() instanceof FlexoMenuItem || action.getInvoker() instanceof WKFKeyEventListener) {
						logger.fine("refuse to delete a process by pressing delete key");
						Toolkit.getDefaultToolkit().beep();
						return false;
					}

					FlexoProcess process = (FlexoProcess) v.firstElement();
					if (process.isRootProcess()) {
						FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_delete_the_root_process"));
						return false;
					}
					if (action.isNoConfirmation()) {
						return true;
					}
					if (process.getAllProcessInstances().size() > 0) {
						StringBuilder sb = new StringBuilder();
						for (SubProcessNode node : process.getAllProcessInstances()) {
							sb.append("\n").append("* ").append(node.getName())
									.append(FlexoLocalization.localizedForKey("located_within_the_process_") + node.getProcess().getName());
						}
						FlexoController.notify(FlexoLocalization.localizedForKey("the_process_") + process.getName()
								+ FlexoLocalization.localizedForKey("_is_used_in_the_following_sub_process_nodes:") + sb.toString());
					}
					if (process.getProject().getFlexoWSLibrary().isDeclaredAsWS(process)) {
						doIt = FlexoController.confirmWithWarning(FlexoLocalization
								.localizedForKey("would_you_like_to_delete_the_web_service")
								+ " "
								+ process.getProject().getFlexoWSLibrary().portTypeForProcess(process).getWSService().getName() + " ?");
						// I Love auto-boxing
						action.setDeletionContextForObject(doIt, process);
					} else {
						doIt = FlexoController.confirmWithWarning(FlexoLocalization.localizedForKey("would_you_like_to_delete_the_process")
								+ " " + process.getName() + " ?");
						// I Love auto-boxing
						action.setDeletionContextForObject(doIt, process);
					}
				} else if (v.size() == 1 && v.firstElement() instanceof FlexoPortMap) {
					FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_delete_a_portmap"));
					return false;
				} else if (v.size() == 1 && v.firstElement() instanceof ServiceInterface) {
					if (((ServiceInterface) v.firstElement()).isUsedInWebService()) {
						doIt = FlexoController.confirmWithWarning(FlexoLocalization
								.localizedForKey("would_you_like_to_delete_service_interface_used_in_ws_and_remove_it_from_ws"));

					} else {
						doIt = FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_the_service_interface"));
					}

				} else {
					if (action.isNoConfirmation()) {
						return true;
					}
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"))) {
						Enumeration en = v.elements();
						while (en.hasMoreElements()) {
							FlexoModelObject o = (FlexoModelObject) en.nextElement();
							if (o instanceof FlexoProcess) {
								if (((FlexoProcess) o).isRootProcess()) {
									FlexoController.notify(FlexoLocalization.localizedForKey("you_cannot_delete_the_root_process"));
									action.setDeletionContextForObject(Boolean.FALSE, (FlexoProcess) o);
								} else {
									FlexoProcess process = (FlexoProcess) o;
									if (process.getAllProcessInstances().size() > 0) {
										StringBuilder sb = new StringBuilder();
										for (SubProcessNode node : process.getAllProcessInstances()) {
											sb.append("\n")
													.append("* ")
													.append(node.getName())
													.append(FlexoLocalization.localizedForKey("located_within_the_process_")
															+ node.getProcess().getName());
										}
										FlexoController.notify(FlexoLocalization.localizedForKey("the_process_") + process.getName()
												+ FlexoLocalization.localizedForKey("_is_used_in_the_following_sub_process_nodes:")
												+ sb.toString());
									}
									action.setDeletionContextForObject(
											new Boolean(FlexoController.confirmWithWarning(FlexoLocalization
													.localizedForKey("would_you_like_to_delete_the_process")
													+ ((FlexoProcess) o).getName()
													+ " ?")), process);
								}
							}
						}
						doIt = true;
					} else {
						return false;
					}
				}
				if (doIt) {
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
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<WKFDelete> getDefaultFinalizer() {
		return new FlexoActionFinalizer<WKFDelete>() {
			@Override
			public boolean run(ActionEvent e, WKFDelete action) {
				if (action.hasBeenDeleted(getControllerActionInitializer().getWKFController().getCurrentFlexoProcess())) {
					getControllerActionInitializer().getWKFController().setCurrentFlexoProcess(getProject().getRootFlexoProcess());
				}
				if (getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject() != null
						&& getControllerActionInitializer().getWKFController().getSelectionManager().getLastSelectedObject().isDeleted()) {
					getControllerActionInitializer().getWKFController().getSelectionManager().resetSelection();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionEnableCondition getEnableCondition() {
		return new FlexoActionEnableCondition<WKFDelete, WKFObject, WKFObject>() {
			@Override
			public boolean isEnabled(FlexoActionType<WKFDelete, WKFObject, WKFObject> actionType, WKFObject object,
					Vector<WKFObject> globalSelection, FlexoEditor editor) {
				// logger.info("Tiens, je me demande si on doit activer cette action WKFDelete avec "+editor);
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
		return KeyStroke.getKeyStroke(FlexoCst.BACKSPACE_DELETE_KEY_CODE, 0);
	}

}
