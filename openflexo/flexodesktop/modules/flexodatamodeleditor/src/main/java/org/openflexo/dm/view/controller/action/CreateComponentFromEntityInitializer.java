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
import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.dm.view.popups.SelectPropertiesAndMethodsPopup;


import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateComponentFromEntity;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class CreateComponentFromEntityInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateComponentFromEntityInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(CreateComponentFromEntity.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	/**
	 * Overrides getDefaultInitializer
	 * @see org.openflexo.view.controller.ActionInitializer#getDefaultInitializer()
	 */
	@Override
	protected FlexoActionInitializer<CreateComponentFromEntity> getDefaultInitializer()
	{
	    return new FlexoActionInitializer<CreateComponentFromEntity>(){

            @Override
            public boolean run(ActionEvent event, CreateComponentFromEntity action)
            {
                if (action.getFocusedObject()==null)
                    return false;
                final RadioButtonListParameter<ComponentType> type = new RadioButtonListParameter<ComponentType>("type",FlexoLocalization.localizedForKey("component_type"),ComponentType.OPERATION_COMPONENT, ComponentType.OPERATION_COMPONENT, ComponentType.POPUP_COMPONENT, ComponentType.TAB_COMPONENT);
                final TextFieldParameter componentNameParam = new TextFieldParameter("componentName",FlexoLocalization.localizedForKey("component_name"),ToolBox.capitalize(action.getVariableName(),true)+ToolBox.capitalize(FlexoLocalization.localizedForKey("edition")));
                AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), getController().getFlexoFrame(), FlexoLocalization.localizedForKey("create_component_from_entity"), "", new AskParametersDialog.ValidationCondition() {

                    @Override
                    public boolean isValid(ParametersModel model)
                    {
                        if (type.getValue()==null) {
                            errorMessage = FlexoLocalization.localizedForKey("select_a_type");
                            return false;
                        }
                        if (componentNameParam.getValue()==null) {
                            errorMessage = FlexoLocalization.localizedForKey("component_name_cannot_be_empty");
                            return false;
                        }
                        if (componentNameParam.getValue()==null) {
                            errorMessage = FlexoLocalization.localizedForKey("component_name_cannot_be_empty");
                            return false;
                        }
                        if (!componentNameParam.getValue().matches(IERegExp.JAVA_CLASS_NAME_REGEXP)) {
                            errorMessage = FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number");
                            return false;
                        }
                        if (!getProject().getFlexoComponentLibrary().isValidForANewComponentName(componentNameParam.getValue())) {
                            errorMessage = FlexoLocalization.localizedForKey("component_name_is_already_used");
                            return false;
                        } else {
                            errorMessage="";
                            return true;
                        }
                    }
                    
                }, type, componentNameParam);
                
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
                    SelectPropertiesAndMethodsPopup popup = new SelectPropertiesAndMethodsPopup(FlexoLocalization
                            .localizedForKey("attributes"), FlexoLocalization.localizedForKey("select_properties_and_methods_description"),
                            "finish", action.getFocusedObject(), getProject(), (DMController) getController());
                    popup.setVisible(true);
                    if (popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) {
                        action.setComponentType(type.getValue());
                        action.setName(componentNameParam.getValue());
                        action.setWidgets(popup.getSelectedAccessorWidgets());
                        action.setCopyDescription(popup.getCopyDescriptions());
                        return true;
                    }
                }
                return false;
            }

        };
	}
}
