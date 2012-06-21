package org.openflexo.wkf.controller.action;

import java.awt.Color;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.param.ColorParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.action.AddRole;
import org.openflexo.foundation.wkf.action.AddToXRole;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public abstract class AddToXRoleInitializer<A extends AddToXRole<A>> extends
		ActionInitializer<A, AbstractActivityNode, AbstractActivityNode> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddToXRoleInitializer(FlexoActionType<A, AbstractActivityNode, AbstractActivityNode> actionType,
			WKFControllerActionInitializer actionInitializer) {
		super(actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<A> getDefaultInitializer() {
		return new FlexoActionInitializer<A>() {

			@Override
			public boolean run(EventObject e, A action) {
				String EXISTING = FlexoLocalization.localizedForKey("existing_role");
				String NEW = FlexoLocalization.localizedForKey("new_role");

				FlexoWorkflow workflow = action.getFocusedObject().getWorkflow();
				Vector<Role> availableRoles = getAvailableRoles(action.getFocusedObject());
				boolean hasRoles = availableRoles.size() > 0;
				Role selectedValue = hasRoles ? availableRoles.get(0) : null;
				ParameterDefinition[] parameters = new ParameterDefinition[5];
				parameters[0] = new RadioButtonListParameter<String>("select", "select_existing_new_role", hasRoles ? EXISTING : NEW,
						EXISTING, NEW);
				DynamicDropDownParameter<Role> availableRolesParameter = new DynamicDropDownParameter<Role>("role", "role", availableRoles,
						selectedValue);
				availableRolesParameter.setShowReset(false);
				availableRolesParameter.setStringFormatter("nameForInspector");
				parameters[1] = availableRolesParameter;
				parameters[1].setDepends("select");
				parameters[1].setConditional("select=\"" + EXISTING + "\"");
				if (!hasRoles) {
					parameters[0].setConditional("true=false");
					parameters[1].setConditional("true=false");
				}
				parameters[2] = new TextFieldParameter("newRoleName", "name", workflow.getRoleList().getNextNewUserRoleName());
				parameters[2].setDepends("select");
				parameters[2].setConditional("select=\"" + NEW + "\"");
				parameters[3] = new ColorParameter("color", "color", workflow.getRoleList().getNewRoleColor());
				parameters[3].setDepends("select");
				parameters[3].setConditional("select=\"" + NEW + "\"");
				parameters[4] = new TextAreaParameter("description", "description", "");
				parameters[4].addParameter("columns", "20");
				parameters[4].setDepends("select");
				parameters[4].setConditional("select=\"" + NEW + "\"");
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("select_role"), FlexoLocalization.localizedForKey("select_role"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					Role role = null;
					String choice = (String) dialog.parameterValueWithName("select");
					if (EXISTING.equals(choice)) {
						role = (Role) dialog.parameterValueWithName("role");
					} else if (NEW.equals(choice)) {
						AddRole addRole = AddRole.actionType.makeNewAction(workflow, null, getEditor());
						String newRoleName = (String) dialog.parameterValueWithName("newRoleName");
						if (newRoleName == null) {
							return false;
						}
						addRole.setNewRoleName(newRoleName);
						addRole.setNewColor((Color) dialog.parameterValueWithName("color"));
						addRole.setNewDescription((String) dialog.parameterValueWithName("description"));
						addRole.setRoleAutomaticallyCreated(true);
						addRole.doAction();
						role = addRole.getNewRole();
					} else {
						return false;
					}
					action.setRole(role);
					return role != null;
				} else {
					return false;
				}
			}
		};
	}

	public abstract Vector<Role> getAvailableRoles(AbstractActivityNode activity);

}
