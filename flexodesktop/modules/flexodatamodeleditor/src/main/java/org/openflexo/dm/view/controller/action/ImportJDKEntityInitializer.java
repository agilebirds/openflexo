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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.action.ImportJDKEntity;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportJDKEntityInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportJDKEntityInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(ImportJDKEntity.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<ImportJDKEntity> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ImportJDKEntity>() {
            @Override
			public boolean run(ActionEvent e, ImportJDKEntity action)
            {
                ParameterDefinition[] parameters = new ParameterDefinition[4];
                String defaultPackageName = "";
                if (action.getFocusedObject() instanceof DMPackage) {
                    defaultPackageName = ((DMPackage) action.getFocusedObject()).getName();
                }
                parameters[0] = new TextFieldParameter("packageName", "package_name", defaultPackageName);
                parameters[1] = new TextFieldParameter("className", "class_name", "");
                parameters[2] = new CheckboxParameter("importGetOnlyProperties", "import_get_only_properties", false);
                parameters[3] = new CheckboxParameter("importMethods", "import_methods", false);
                AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
                        null, FlexoLocalization.localizedForKey("import_class"), FlexoLocalization.localizedForKey("enter_parameters_for_the_class_import"), parameters);
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
                    action.setPackageName((String) dialog.parameterValueWithName("packageName"));
                    action.setClassName((String) dialog.parameterValueWithName("className"));
                    action.setImportGetOnlyProperties(dialog.booleanParameterValueWithName("importGetOnlyProperties"));
                    action.setImportMethods(dialog.booleanParameterValueWithName("importMethods"));
                    if (action.getClassToImport() == null) {
                        FlexoController.notify(FlexoLocalization.localizedForKey("class_not_found"));
                        return false;
                    } else if (getProject().getDataModel().getDMEntity(action.getClassToImport()) == null) {
                        return true;
                    } else {
                        FlexoController.notify(FlexoLocalization.localizedForKey("class_already_declared"));
                        return false;
                    }
                } else {
                    return false;
                }
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<ImportJDKEntity> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ImportJDKEntity>() {
            @Override
			public boolean run(ActionEvent e, ImportJDKEntity action)
            {
                if (action.getNewEntity() == null) {
                    FlexoController.showError(FlexoLocalization.localizedForKey("class_not_created"));
                } else {
                	getControllerActionInitializer().getDMController().getSelectionManager().setSelectedObject(action.getNewEntity());
                }
                /*
                 * if (getDMController().getCurrentEditedObject() ==
                 * action.getJDKRepository()) { if
                 * (logger.isLoggable(Level.FINE)) logger.fine("Finalizer for
                 * ImportJDKEntity in DMRepositoryView"); DMRepositoryView
                 * repView =
                 * (DMRepositoryView)getDMController().getCurrentEditedObjectView();
                 * repView.getPackageTable().selectObject(action.getNewEntity().getPackage());
                 * repView.getEntityTable().selectObject(action.getNewEntity()); }
                 * else if (getDMController().getCurrentEditedObject() ==
                 * action.getNewEntity().getPackage()) { if
                 * (logger.isLoggable(Level.FINE)) logger.fine("Finalizer for
                 * ImportJDKEntity in DMPackageView"); DMPackageView packageView =
                 * (DMPackageView)getDMController().getCurrentEditedObjectView();
                 * packageView.getEntityTable().selectObject(action.getNewEntity()); }
                 */
                return true;
            }
        };
	}


}
