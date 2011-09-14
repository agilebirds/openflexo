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
package org.openflexo.dre.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.dre.AbstractDocItemView;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemParameter;
import org.openflexo.drm.action.AddToInheritanceChildItem;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddToInheritanceChildItemInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddToInheritanceChildItemInitializer(DREControllerActionInitializer actionInitializer)
	{
		super(AddToInheritanceChildItem.actionType,actionInitializer);
	}
	
	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() 
	{
		return (DREControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<AddToInheritanceChildItem> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddToInheritanceChildItem>() {
            @Override
			public boolean run(ActionEvent e, AddToInheritanceChildItem action)
            {
                ParameterDefinition[] parameters = new ParameterDefinition[1];
                parameters[0] = new DocItemParameter("childItem", "choose_an_item", null);
                AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
                        null, FlexoLocalization.localizedForKey("define_doc_item_relationships"), FlexoLocalization.localizedForKey("please_select_a_child_item_related_to_inheritance"), parameters);
                if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
                    DocItem newChildItem = (DocItem) dialog.parameterValueWithName("childItem");
                    if (newChildItem == null)
                        return false;
                    action.setChildDocItem(newChildItem);
                    return true;
                } else {
                    return false;
                }
           }
        };
	}

     @Override
	protected FlexoActionFinalizer<AddToInheritanceChildItem> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddToInheritanceChildItem>() {
            @Override
			public boolean run(ActionEvent e, AddToInheritanceChildItem action)
            {
                if (getControllerActionInitializer().getDREController().getCurrentDisplayedObjectAsModuleView() == action.getParentDocItem()) {
                    AbstractDocItemView docItemView = (AbstractDocItemView)getControllerActionInitializer().getDREController().getCurrentModuleView();
                    docItemView.updateViewFromModel();
                }
               return true;
          }
        };
	}

}
