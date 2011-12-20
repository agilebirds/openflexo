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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.help.BadIDException;

import org.openflexo.action.HelpAction;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.MetricsDefinition;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.AddActivityMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddActivityMetricsValue;
import org.openflexo.foundation.wkf.action.AddArtefactMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddArtefactMetricsValue;
import org.openflexo.foundation.wkf.action.AddEdgeMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddEdgeMetricsValue;
import org.openflexo.foundation.wkf.action.AddMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddOperationMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddOperationMetricsValue;
import org.openflexo.foundation.wkf.action.AddProcessMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddProcessMetricsValue;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;
import org.openflexo.foundation.wkf.action.AddStatus;
import org.openflexo.foundation.wkf.action.AddToAccountableRole;
import org.openflexo.foundation.wkf.action.AddToConsultedRole;
import org.openflexo.foundation.wkf.action.AddToInformedRole;
import org.openflexo.foundation.wkf.action.AddToResponsibleRole;
import org.openflexo.foundation.wkf.action.DeleteMetricsDefinition;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.action.DeleteRole;
import org.openflexo.foundation.wkf.action.RemoveFromAccountableRole;
import org.openflexo.foundation.wkf.action.RemoveFromConsultedRole;
import org.openflexo.foundation.wkf.action.RemoveFromInformedRole;
import org.openflexo.foundation.wkf.action.RemoveFromResponsibleRole;
import org.openflexo.foundation.wkf.action.WKFDelete;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.help.FlexoHelp;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WKFSelectionManager;

public class WKFControllerActionInitializer extends ControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	protected WKFController _wkfController;

	public WKFControllerActionInitializer(WKFController controller) {
		super(controller);
		_wkfController = controller;
	}

	public WKFController getWKFController() {
		return _wkfController;
	}

	public WKFSelectionManager getWKFSelectionManager() {
		return getWKFController().getWKFSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new WKFSetPropertyInitializer(this).init();

		// Disabled copy/paste
		if (WKFCst.CUT_COPY_PASTE_ENABLED) {
			new WKFCopyInitializer(this).init();
			new WKFCutInitializer(this).init();
			new WKFPasteInitializer(this).init();
		}
		new WKFDeleteInitializer(this).init();
		new WKFSelectAllInitializer(this).init();
		new WKFMoveInitializer(this).init();
		new AddSubProcessInitializer(this).init();
		new OpenEmbeddedProcessInitializer(this).init();
		new OpenOperationComponentInitializer(this).init();
		new SetAndOpenOperationComponentInitializer(this).init();
		new OpenOperationLevelInitializer(this).init();
		new OpenActionLevelInitializer(this).init();
		new OpenExecutionPetriGraphInitializer(this).init();
		new OpenLoopedPetriGraphInitializer(this).init();
		new OpenPortRegisteryInitializer(this).init();
		new ShowHidePortmapRegisteryInitializer(this).init();
		new ShowHidePortmapInitializer(this).init();
		new AddRoleInitializer(this).init();
		new AddRoleSpecializationInitializer(this).init();
		new DeleteRoleInitializer(this).init();
		new DeleteRoleSpecializationInitializer(this).init();
		new AddStatusInitializer(this).init();
		// new AddDeadlineInitializer(this).init();
		new AddPortInitializer(this).init();

		new ImportRolesInitializer(this).init();
		new ImportProcessesInitializer(this).init();
		new ConvertIntoLocalRoleInitializer(this).init();
		new ConvertIntoLocalProcessInitializer(this).init();

		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			new AddServiceInterfaceInitializer(this).init();
			new AddServiceOperationInitializer(this).init();
		}

		new MakeActivityGroupInitializer(this).init();
		new UngroupActivitiesInitializer(this).init();

		new BindButtonsToActionNodeInitializer(this).init();
		new ViewNextOperationsInitializer(this).init();
		new PrintProcessInitializer(this).init();
		new DropWKFElementInitializer(this).init();
		new CreateNodeInitializer(this).init();
		new CreatePreconditionInitializer(this).init();
		new CreateEdgeInitializer(this).init();
		new CreateAssociationInitializer(this).init();
		new MakeFlexoProcessContextFreeInitializer(this).init();
		new MoveFlexoProcessInitializer(this).init();
		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			new ShowExecutionControlGraphsInitializer(this).init();
		}
		new ShowRolesInitializer(this).init();
		new HideRoleInitializer(this).init();
		new AddProcessMetricsDefinitionInitializer(this).init();
		new AddProcessMetricsInitializer(this).init();
		new AddActivityMetricsDefinitionInitializer(this).init();
		new AddActivityMetricsInitializer(this).init();
		new AddOperationMetricsDefinitionInitializer(this).init();
		new AddOperationMetricsInitializer(this).init();
		new AddEdgeMetricsDefinitionInitializer(this).init();
		new AddEdgeMetricsInitializer(this).init();
		new AddArtefactMetricsDefinitionInitializer(this).init();
		new AddArtefactMetricsInitializer(this).init();
		new DeleteMetricsDefinitionInitializer(this).init();
		new DeleteMetricsInitializer(this).init();

		new AddToProcessFolderInitializer(this).init();
		new RemoveFromProcessFolderInitializer(this).init();
		new CreateProcessFolderInitializer(this).init();
		new DeleteProcessFolderInitializer(this).init();
		new MoveProcessFolderInitializer(this).init();
		new OpenProcessInNewWindowInitializer(this).init();

		new AddToResponsibleRoleInitializer(this).init();
		new AddToAccountableRoleInitializer(this).init();
		new AddToConsultedRoleInitializer(this).init();
		new AddToInformedRoleInitializer(this).init();

		registerAction(RemoveFromResponsibleRole.actionType);
		registerAction(RemoveFromAccountableRole.actionType);
		registerAction(RemoveFromConsultedRole.actionType);
		registerAction(RemoveFromInformedRole.actionType);

		// Initialize action of inspectors
		RoleList.addRoleActionizer = new FlexoActionizer<AddRole, WorkflowModelObject, WorkflowModelObject>(AddRole.actionType, getEditor());
		Role.addParentRoleActionizer = new FlexoActionizer<AddRoleSpecialization, Role, WorkflowModelObject>(
				AddRoleSpecialization.actionType, getEditor());
		RoleList.deleteRoleActionizer = new FlexoActionizer<DeleteRole, Role, WorkflowModelObject>(DeleteRole.actionType, getEditor());

		FlexoProcess.addStatusActionizer = new FlexoActionizer<AddStatus, WKFObject, WKFObject>(AddStatus.actionType, getEditor());
		FlexoProcess.deleteActionizer = new FlexoActionizer<WKFDelete, WKFObject, WKFObject>(WKFDelete.actionType, getEditor());

		// MetricsDefinition actions
		FlexoWorkflow.addProcessMetricsDefinitionActionizer = new FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject>(
				AddProcessMetricsDefinition.actionType, getEditor());
		FlexoWorkflow.addActivityMetricsDefinitionActionizer = new FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject>(
				AddActivityMetricsDefinition.actionType, getEditor());
		FlexoWorkflow.addOperationMetricsDefinitionActionizer = new FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject>(
				AddOperationMetricsDefinition.actionType, getEditor());
		FlexoWorkflow.addEdgeMetricsDefinitionActionizer = new FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject>(
				AddEdgeMetricsDefinition.actionType, getEditor());
		FlexoWorkflow.addArtefactMetricsDefinitionActionizer = new FlexoActionizer<AddMetricsDefinition, FlexoWorkflow, WorkflowModelObject>(
				AddArtefactMetricsDefinition.actionType, getEditor());
		FlexoWorkflow.deleteMetricsDefinitionActionizer = new FlexoActionizer<DeleteMetricsDefinition, MetricsDefinition, MetricsDefinition>(
				DeleteMetricsDefinition.actionType, getEditor());

		// MetricsValue actions
		FlexoProcess.addMetricsActionizer = new FlexoActionizer<AddProcessMetricsValue, FlexoProcess, WKFObject>(
				AddProcessMetricsValue.actionType, getEditor());
		AbstractActivityNode.addMetricsActionizer = new FlexoActionizer<AddActivityMetricsValue, AbstractActivityNode, WKFObject>(
				AddActivityMetricsValue.actionType, getEditor());
		OperationNode.addMetricsActionizer = new FlexoActionizer<AddOperationMetricsValue, OperationNode, WKFObject>(
				AddOperationMetricsValue.actionType, getEditor());
		FlexoPostCondition.addMetricsActionizer = new FlexoActionizer<AddEdgeMetricsValue, FlexoPostCondition<?, ?>, WKFObject>(
				AddEdgeMetricsValue.actionType, getEditor());
		WKFArtefact.addMetricsActionizer = new FlexoActionizer<AddArtefactMetricsValue, WKFArtefact, WKFObject>(
				AddArtefactMetricsValue.actionType, getEditor());

		FlexoProcess.deleteMetricsActionizer = new FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue>(
				DeleteMetricsValue.actionType, getEditor());
		AbstractActivityNode.deleteMetricsActionizer = new FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue>(
				DeleteMetricsValue.actionType, getEditor());
		OperationNode.deleteMetricsActionizer = new FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue>(
				DeleteMetricsValue.actionType, getEditor());
		FlexoPostCondition.deleteMetricsActionizer = new FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue>(
				DeleteMetricsValue.actionType, getEditor());
		WKFArtefact.deleteMetricsActionizer = new FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue>(
				DeleteMetricsValue.actionType, getEditor());
		AbstractActivityNode.addResponsibleRoleActionizer = new FlexoActionizer<AddToResponsibleRole, AbstractActivityNode, AbstractActivityNode>(
				AddToResponsibleRole.actionType, getEditor());
		AbstractActivityNode.removeFromResponsibleRoleActionizer = new FlexoActionizer<RemoveFromResponsibleRole, Role, AbstractActivityNode>(
				RemoveFromResponsibleRole.actionType, getEditor());

		AbstractActivityNode.addAccountableRoleActionizer = new FlexoActionizer<AddToAccountableRole, AbstractActivityNode, AbstractActivityNode>(
				AddToAccountableRole.actionType, getEditor());
		AbstractActivityNode.removeFromAccountableRoleActionizer = new FlexoActionizer<RemoveFromAccountableRole, Role, AbstractActivityNode>(
				RemoveFromAccountableRole.actionType, getEditor());

		AbstractActivityNode.addConsultedRoleActionizer = new FlexoActionizer<AddToConsultedRole, AbstractActivityNode, AbstractActivityNode>(
				AddToConsultedRole.actionType, getEditor());
		AbstractActivityNode.removeFromConsultedRoleActionizer = new FlexoActionizer<RemoveFromConsultedRole, Role, AbstractActivityNode>(
				RemoveFromConsultedRole.actionType, getEditor());

		AbstractActivityNode.addInformedRoleActionizer = new FlexoActionizer<AddToInformedRole, AbstractActivityNode, AbstractActivityNode>(
				AddToInformedRole.actionType, getEditor());
		AbstractActivityNode.removeFromInformedRoleActionizer = new FlexoActionizer<RemoveFromInformedRole, Role, AbstractActivityNode>(
				RemoveFromInformedRole.actionType, getEditor());

	}

	private InteractiveFlexoEditor getEditor() {
		return getWKFController().getEditor();
	}

	@Override
	protected void initializeHelpAction() {
		super.initializeHelpAction();
		getController().getEditor().registerFinalizerFor(HelpAction.actionType, new FlexoActionFinalizer<HelpAction>() {
			@Override
			public boolean run(ActionEvent e, HelpAction action) {
				if (!(action.getFocusedObject() instanceof InspectableObject)) {
					return false;
				}
				// Hack for WKFInvaders
				if (action.getFocusedObject() instanceof AbstractActivityNode) {
					if (((AbstractActivityNode) action.getFocusedObject()).getName().equals(WKFCst.WKFInvaders)) {
						System.out.println("Switching to WKFInvaders...");
						getWKFController().switchToPerspective(getWKFController().WKF_INVADERS);
						return false;
					}
				}
				if (action.getFocusedObject() instanceof InspectableObject) {
					DocItem item = DocResourceManager.instance().getDocItemFor((InspectableObject) action.getFocusedObject());
					if (item != null) {
						try {
							logger.info("Trying to display help for " + item.getIdentifier());
							FlexoHelp.getHelpBroker().setCurrentID(item.getIdentifier());
							FlexoHelp.getHelpBroker().setDisplayed(true);
						} catch (BadIDException exception) {
							FlexoController.showError(FlexoLocalization.localizedForKey("sorry_no_help_available_for") + " "
									+ item.getIdentifier());
							return false;
						}
						return true;
					}
				}
				return true;
			}
		}, getController().getModule());
	}

}
