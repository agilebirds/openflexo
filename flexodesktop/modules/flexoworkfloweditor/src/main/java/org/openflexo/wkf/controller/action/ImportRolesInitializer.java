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

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.xml.rpc.ServiceException;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.imported.action.ImportRolesAction;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyCheckboxParameter;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.ws.client.PPMWebService.PPMRole;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceAuthentificationException;
import org.openflexo.ws.client.PPMWebService.PPMWebServiceClient;

public class ImportRolesInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportRolesInitializer(WKFControllerActionInitializer actionInitializer) {
		super(ImportRolesAction.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ImportRolesAction> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportRolesAction>() {
			@Override
			public boolean run(EventObject e, ImportRolesAction action) {
				boolean isFirst = true;
				PPMWebServiceClient client = null;
				PPMRole[] roles = null;
				while (roles == null) {
					client = getController().getWSClient(!isFirst);
					isFirst = false;
					if (client == null) {
						return false; // Cancelled
					}
					try {
						roles = client.getRoles();
					} catch (PPMWebServiceAuthentificationException e1) {
						getController().handleWSException(e1);
					} catch (RemoteException e1) {
						getController().handleWSException(e1);
					} catch (ServiceException e1) {
						getController().handleWSException(e1);
					}
				}
				if (roles != null) {
					Vector<PPMRole> rolesToImport = selectRolesToImport(roles);
					if (rolesToImport != null && rolesToImport.size() > 0) {
						getProject().getWorkflow().getImportedRoleList(true);
						action.setRolesToImport(rolesToImport);
						return true;
					}
				}

				return false;
			}
		};
	}

	private Vector<PPMRole> selectRolesToImport(PPMRole[] roles) {
		Arrays.sort(roles, new Comparator<PPMRole>() {
			@Override
			public int compare(PPMRole o1, PPMRole o2) {
				if (o1.getName() == null) {
					if (o2.getName() == null) {
						return 0;
					} else {
						return -1;
					}
				} else if (o2.getName() == null) {
					return 1;
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
		ParameterDefinition[] parameters = new ParameterDefinition[roles.length];
		for (int i = 0; i < roles.length; i++) {
			PPMRole itemRole = roles[i];
			if (getProject().getWorkflow().getRoleWithURI(itemRole.getUri()) == null) {
				CheckboxParameter cb = new CheckboxParameter("role" + i, itemRole.getName(), false);
				cb.setLocalizedLabel(itemRole.getName());
				parameters[i] = cb;
			} else {
				ReadOnlyCheckboxParameter cb = new ReadOnlyCheckboxParameter("role" + i, itemRole.getName(), true);
				cb.setLocalizedLabel(itemRole.getName());
				parameters[i] = cb;
			}
		}
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
				FlexoLocalization.localizedForKey("import_roles"), FlexoLocalization.localizedForKey("select_roles_to_import"), parameters);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			Vector<PPMRole> reply = new Vector<PPMRole>();
			for (int i = 0; i < roles.length; i++) {
				if (parameters[i].getBooleanValue() && !(parameters[i] instanceof ReadOnlyCheckboxParameter)) {
					reply.add(roles[i]);
				}
			}
			return reply;
		} else {
			return null;
		}
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionFinalizer<ImportRolesAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ImportRolesAction>() {
			@Override
			public boolean run(EventObject e, ImportRolesAction action) {
				if (action.getImportReport() != null && action.getImportReport().getProperlyImported().size() > 0) {
					/*getController().getSelectionManager().resetSelection();
					for (FIRole role : action.getImportReport().getProperlyImported().values()) {
						getController().getSelectionManager().addToSelected(role);
					}*/
					getController().getSelectionManager().setSelectedObject(
							action.getImportReport().getProperlyImported().values().iterator().next());
				}
				FlexoController.notify(action.getImportReport().toString());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.ROLE_ICON;
	}

}
