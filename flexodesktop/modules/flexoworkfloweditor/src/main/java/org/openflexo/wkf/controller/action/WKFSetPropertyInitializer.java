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

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.components.validation.PartialConsistencyCheckDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.InvalidProcessReferencesException;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WKFExceptionHandler;

public class WKFSetPropertyInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public WKFSetPropertyInitializer(ControllerActionInitializer actionInitializer) {
		super(SetPropertyAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<SetPropertyAction> getDefaultInitializer() {
		return new FlexoActionInitializer<SetPropertyAction>() {
			@Override
			public boolean run(EventObject e, SetPropertyAction action) {
				if (action.getFocusedObject() == null) {
					return false;
				}

				logger.info("SetPropertyAction in WKF: focusedObject=" + action.getFocusedObject() + " key=" + action.getKey());

				if (action.getFocusedObject() instanceof FlexoProcess && action.getKey().equals("parentProcess")) {
					FlexoProcess newParentProcess = (FlexoProcess) action.getValue();
					FlexoProcess movedProcess = (FlexoProcess) action.getFocusedObject();
					if (!movedProcess.isAcceptableAsParentProcess(newParentProcess)) {
						FlexoController.notify(FlexoLocalization.localizedForKey("could_not_move_process_inconsistent_process_hierarchy"));
						return false;
					}
					String message;
					if (newParentProcess == null) {
						message = "would_you_really_like_to_make_process_($0)_without_context";
					} else {
						message = "would_you_really_like_to_move_process_($0)_to_($1)";
					}
					return FlexoController.confirm(FlexoLocalization.localizedForKeyWithParams(message, movedProcess.getName(),
							(newParentProcess != null ? newParentProcess.getName() : null)));
				}

				/*if (exception instanceof ProcessMovingConfirmation) {
				    String message;
				    FlexoProcess newParentProcess = ((ProcessMovingConfirmation) exception).params.newParentProcess;
				    FlexoProcess movedProcess = ((ProcessMovingConfirmation) exception).params.movedProcess;
				    if (!movedProcess.isAcceptableAsParentProcess(newParentProcess)) {
				        notify(FlexoLocalization.localizedForKey("could_not_move_process_inconsistent_process_hierarchy"));
				        return true;
				    }
				    if (newParentProcess == null) {
				        message = "would_you_really_like_to_make_process_($movedProcess)_without_context";
				    } else {
				        message = "would_you_really_like_to_move_process_($movedProcess)_to_($newParentProcess)";
				    }
				    if (confirm(FlexoLocalization.localizedForKeyWithParams(message, ((ProcessMovingConfirmation) exception).params))) {
				        try {
				            movedProcess.setParentProcess(newParentProcess);
				            getWorkflowBrowser().focusOn(movedProcess);
				        } catch (InvalidParentProcessException e) {
				            showError(FlexoLocalization.localizedForKey("could_not_move_process_inconsistent_process_hierarchy"));
				        } catch (InvalidProcessReferencesException e) {
				            logger.info("InvalidProcessReferencesException: launching CheckConsistency dialog");
				            e.report.setLocalizedTitle(FlexoLocalization.localizedForKey("refactoring_has_generated_following_inconsistency"));
				            PartialConsistencyCheckDialog pccd = new PartialConsistencyCheckDialog(FlexoLocalization
				                    .localizedForKey("moving_process"), this, e.report);
				            pccd.show();
				        }
				    }
				    return true;
				}*/

				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SetPropertyAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SetPropertyAction>() {
			@Override
			public boolean run(EventObject e, SetPropertyAction action) {
				if (action.getFocusedObject() instanceof FlexoProcess && action.getKey().equals("parentProcess")) {
					FlexoProcess movedProcess = (FlexoProcess) action.getFocusedObject();
					((WKFController) getController()).getWorkflowBrowser().focusOn(movedProcess);
				} else if (action.getFocusedObject() instanceof SubProcessNode && action.getKey().equals("subProcess")) {
					if (action.getPreviousValue() instanceof FlexoProcess && ((FlexoProcess) action.getPreviousValue()).isImported()
							&& !(action.getValue() instanceof FlexoProcess && ((FlexoProcess) action.getValue()).isImported())) {
						if ((action.getFocusedObject() instanceof SingleInstanceSubProcessNode
								|| action.getFocusedObject() instanceof WSCallSubProcessNode || action.getFocusedObject() instanceof LoopSubProcessNode)) {
							if (((SubProcessNode) action.getFocusedObject()).getPreConditions().size() > 0) {
								WKFDelete delete = WKFDelete.actionType
										.makeNewAction(null,
												new Vector<WKFObject>(((SubProcessNode) action.getFocusedObject()).getPreConditions()),
												getEditor());
								delete.setNoConfirmation(true);
								delete.doAction();
							}
							if (((SubProcessNode) action.getFocusedObject()).getOutgoingPostConditions().size() > 0) {
								WKFDelete delete = WKFDelete.actionType.makeNewAction(null,
										new Vector<WKFObject>(((SubProcessNode) action.getFocusedObject()).getOutgoingPostConditions()),
										getEditor());
								delete.setNoConfirmation(true);
								delete.doAction();
							}
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SetPropertyAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SetPropertyAction>() {
			@Override
			public boolean handleException(FlexoException exception, final SetPropertyAction action) {
				if (action.getFocusedObject() instanceof OperationNode && exception instanceof OperationAssociatedWithComponentSuccessfully) {
					WKFExceptionHandler.handleAssociation((OperationAssociatedWithComponentSuccessfully) exception,
							(WKFController) getController());
					return true;
				} else if (action.getFocusedObject() instanceof FlexoProcess) {
					if (action.getKey().equals("parentProcess")) {
						if (exception instanceof InvalidParentProcessException) {
							if (((FlexoProcess) action.getFocusedObject()).getIsWebService()) {
								FlexoController.showError(FlexoLocalization.localizedForKey("web_service_cannot_be_moved"));
								return true;
							}
							FlexoController.showError(FlexoLocalization
									.localizedForKey("could_not_move_process_inconsistent_process_hierarchy"));
							return true;
						} else if (exception instanceof InvalidProcessReferencesException) {
							if (action.getPerformValidate()) {
								logger.info("InvalidProcessReferencesException: launching CheckConsistency dialog");
								((InvalidProcessReferencesException) exception).report.setLocalizedTitle(FlexoLocalization
										.localizedForKey("refactoring_has_generated_following_inconsistency"));
								PartialConsistencyCheckDialog pccd = new PartialConsistencyCheckDialog(
										FlexoLocalization.localizedForKey("moving_process"), (WKFController) getController(),
										((InvalidProcessReferencesException) exception).report);
								pccd.show();
							}
							return true;
						}
					} else if (action.getKey().equals("name")) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								FlexoController.notify(FlexoLocalization.localizedForKey("could_not_rename_process_to")
										+ " "
										+ (action.getValue() == null || action.getValue().equals("") ? FlexoLocalization
												.localizedForKey("empty_value") : action.getValue()) + " "
										+ FlexoLocalization.localizedForKey("because_there_is_already_a_process_with_that_name"));
							}
						});
						return true;
					}
				}
				exception.printStackTrace();
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_set_property")
						+ " "
						+ (action.getLocalizedPropertyName() != null ? "'" + action.getLocalizedPropertyName() + "' " : "")
						+ FlexoLocalization.localizedForKey("to")
						+ " "
						+ (action.getValue() == null || action.getValue().equals("") ? FlexoLocalization.localizedForKey("empty_value")
								: action.getValue())
						+ (exception.getLocalizedMessage() != null ? "\n(" + FlexoLocalization.localizedForKey("details: ")
								+ exception.getLocalizedMessage() + ")" : ""));
				return true;
			}
		};
	}

}
