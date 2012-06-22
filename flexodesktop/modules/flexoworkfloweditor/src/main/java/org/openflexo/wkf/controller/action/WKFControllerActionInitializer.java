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

import java.util.logging.Logger;

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
import org.openflexo.foundation.wkf.action.AddPort;
import org.openflexo.foundation.wkf.action.AddProcessMetricsDefinition;
import org.openflexo.foundation.wkf.action.AddProcessMetricsValue;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;
import org.openflexo.foundation.wkf.action.AddStatus;
import org.openflexo.foundation.wkf.action.AddToAccountableRole;
import org.openflexo.foundation.wkf.action.AddToConsultedRole;
import org.openflexo.foundation.wkf.action.AddToInformedRole;
import org.openflexo.foundation.wkf.action.AddToResponsibleRole;
import org.openflexo.foundation.wkf.action.CreateNode;
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
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.InteractiveFlexoEditor;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.controller.WKFSelectionManager;

public class WKFControllerActionInitializer extends ControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	protected WKFController _wkfController;

	public WKFControllerActionInitializer(InteractiveFlexoEditor editor, WKFController controller) {
		super(editor, controller);
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

		new WKFSetPropertyInitializer(this);

		// Disabled copy/paste
		if (WKFCst.CUT_COPY_PASTE_ENABLED) {
			new WKFCopyInitializer(this);
			new WKFCutInitializer(this);
			new WKFPasteInitializer(this);
		}
		new WKFDeleteInitializer(this);
		new WKFSelectAllInitializer(this);
		new WKFMoveInitializer(this);
		new AddSubProcessInitializer(this);
		new OpenEmbeddedProcessInitializer(this);
		new OpenOperationComponentInitializer(this);
		new SetAndOpenOperationComponentInitializer(this);
		new OpenOperationLevelInitializer(this);
		new OpenActionLevelInitializer(this);
		new OpenExecutionPetriGraphInitializer(this);
		new OpenLoopedPetriGraphInitializer(this);
		new OpenPortRegisteryInitializer(this);
		new ShowHidePortmapRegisteryInitializer(this);
		new ShowHidePortmapInitializer(this);
		new AddRoleInitializer(this);
		new AddRoleSpecializationInitializer(this);
		new DeleteRoleInitializer(this);
		new DeleteRoleSpecializationInitializer(this);
		new AddStatusInitializer(this);
		new AddPortInitializer(AddPort.createPort, null, this);
		new AddPortInitializer(AddPort.createNewPort, WKFIconLibrary.SMALL_NEW_PORT_ICON, this);
		new AddPortInitializer(AddPort.createDeletePort, WKFIconLibrary.SMALL_DELETE_PORT_ICON, this);
		new AddPortInitializer(AddPort.createInPort, WKFIconLibrary.SMALL_IN_PORT_LEFT_ICON, this);
		new AddPortInitializer(AddPort.createOutPort, WKFIconLibrary.SMALL_OUT_PORT_LEFT_ICON, this);
		new AddPortInitializer(AddPort.createInOutPort, WKFIconLibrary.SMALL_IN_OUT_PORT_LEFT_ICON, this);

		new ImportRolesInitializer(this);
		new ImportProcessesInitializer(this);
		new ConvertIntoLocalRoleInitializer(this);
		new ConvertIntoLocalProcessInitializer(this);

		if (UserType.isDevelopperRelease() || UserType.isMaintainerRelease()) {
			new AddServiceInterfaceInitializer(this);
			new AddServiceOperationInitializer(this);
			new ShowExecutionControlGraphsInitializer(this);
		}

		new MakeActivityGroupInitializer(this);
		new UngroupActivitiesInitializer(this);

		new BindButtonsToActionNodeInitializer(this);
		new ViewNextOperationsInitializer(this);
		new PrintProcessInitializer(this);
		new DropWKFElementInitializer(this);

		new CreateNodeInitializer(CreateNode.createActivityBeginNode, WKFIconLibrary.BEGIN_ACTIVITY_ICON, this);
		new CreateNodeInitializer(CreateNode.createActivityEndNode, WKFIconLibrary.END_ACTIVITY_ICON, this);
		new CreateNodeInitializer(CreateNode.createActivityNormalNode, WKFIconLibrary.ACTIVITY_NODE_ICON, this);
		new CreateNodeInitializer(CreateNode.createOperationBeginNode, WKFIconLibrary.BEGIN_OPERATION_ICON, this);
		new CreateNodeInitializer(CreateNode.createOperationEndNode, WKFIconLibrary.END_OPERATION_ICON, this);
		new CreateNodeInitializer(CreateNode.createOperationNormalNode, WKFIconLibrary.OPERATION_NODE_ICON, this);
		new CreateNodeInitializer(CreateNode.createActionBeginNode, WKFIconLibrary.BEGIN_ACTION_ICON, this);
		new CreateNodeInitializer(CreateNode.createActionEndNode, WKFIconLibrary.END_ACTION_ICON, this);
		new CreateNodeInitializer(CreateNode.createActionNormalNode, WKFIconLibrary.ACTION_NODE_ICON, this);

		new CreatePreconditionInitializer(this);
		new CreateEdgeInitializer(this);
		new CreateAssociationInitializer(this);
		new MakeFlexoProcessContextFreeInitializer(this);
		new MoveFlexoProcessInitializer(this);
		new ShowRolesInitializer(this);
		new HideRoleInitializer(this);
		new AddProcessMetricsDefinitionInitializer(this);
		new AddProcessMetricsInitializer(this);
		new AddActivityMetricsDefinitionInitializer(this);
		new AddActivityMetricsInitializer(this);
		new AddOperationMetricsDefinitionInitializer(this);
		new AddOperationMetricsInitializer(this);
		new AddEdgeMetricsDefinitionInitializer(this);
		new AddEdgeMetricsInitializer(this);
		new AddArtefactMetricsDefinitionInitializer(this);
		new AddArtefactMetricsInitializer(this);
		new DeleteMetricsDefinitionInitializer(this);
		new DeleteMetricsInitializer(this);

		new AddToProcessFolderInitializer(this);
		new RemoveFromProcessFolderInitializer(this);
		new CreateProcessFolderInitializer(this);
		new DeleteProcessFolderInitializer(this);
		new MoveProcessFolderInitializer(this);
		new OpenProcessInNewWindowInitializer(this);

		new AddToResponsibleRoleInitializer(this);
		new AddToAccountableRoleInitializer(this);
		new AddToConsultedRoleInitializer(this);
		new AddToInformedRoleInitializer(this);
		new RemoveFromResponsibleRoleInitializer(this);
		new RemoveFromAccountableRoleInitializer(this);
		new RemoveFromConsultedRoleInitializer(this);
		new RemoveFromInformedRoleInitializer(this);

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

}
