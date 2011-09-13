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

import javax.swing.Icon;

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.action.CreateERDiagram;
import org.openflexo.foundation.param.DMRepositoryParameter;
import org.openflexo.foundation.param.MultipleObjectParameter;
import org.openflexo.foundation.param.TextFieldParameter;

public class CreateERDiagramInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateERDiagramInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(CreateERDiagram.actionType,actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer()
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<CreateERDiagram> getDefaultInitializer()
	{
		return new FlexoActionInitializer<CreateERDiagram>() {
            @Override
			public boolean run(ActionEvent e, final CreateERDiagram action)
            {
            	TextFieldParameter nameParam = new TextFieldParameter("diagramName","diagram_name",action.getDiagramName());
            	DMRepositoryParameter repositoryParam = new DMRepositoryParameter("repository","repository",action.getRepository());
            	MultipleObjectParameter<DMEntity> entitiesParam = new MultipleObjectParameter<DMEntity>("entities","select_entities_to_put_in_the_diagram",action.getEntitiesToPutInTheDiagram());
            	entitiesParam.setRootObject("dataModel");
            	entitiesParam.addSelectableElements(BrowserElementType.DM_ENTITY.name());
            	entitiesParam.addSelectableElements(BrowserElementType.DM_EOENTITY.name());
            	entitiesParam.defineVisibility(BrowserElementType.DM_PROPERTY.name(),BrowserFilterStatus.HIDE.name());
            	entitiesParam.defineVisibility(BrowserElementType.DM_EOATTRIBUTE.name(),BrowserFilterStatus.HIDE.name());
            	entitiesParam.defineVisibility(BrowserElementType.DM_EORELATIONSHIP.name(),BrowserFilterStatus.HIDE.name());
            	entitiesParam.defineVisibility(BrowserElementType.DM_METHOD.name(),BrowserFilterStatus.HIDE.name());
            	entitiesParam.defineVisibility(BrowserElementType.DM_TRANSTYPER.name(),BrowserFilterStatus.HIDE.name());

            	AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
		     			getProject(),
						null,
						action.getLocalizedName(),
		     			FlexoLocalization.localizedForKey("please_define_new_entities_relationship_parameters"),
		     			nameParam,
		     			/*repositoryParam,*/
		     			entitiesParam);


            	if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
            		action.setDiagramName(nameParam.getValue());
               		action.setRepository(repositoryParam.getValue());
               		action.setEntitiesToPutInTheDiagram(entitiesParam.getValue());

               		logger.info("Donc, j'ai ca: "+entitiesParam.getValue());

              		return true;
            	}

            	// Cancelled
            	return false;

             }

         };
	}

     @Override
	protected FlexoActionFinalizer<CreateERDiagram> getDefaultFinalizer()
	{
		return new FlexoActionFinalizer<CreateERDiagram>() {
            @Override
			public boolean run(ActionEvent e, CreateERDiagram action)
            {
            	((DMController)getController()).switchToPerspective(((DMController)getController()).DIAGRAM_PERSPECTIVE);
            	((DMController)getController()).setCurrentEditedObject(action.getNewDiagram());
                 return true;
           }
        };
	}

	@Override
	protected Icon getEnabledIcon()
	{
		return DMEIconLibrary.DIAGRAM_ICON;
	}


}
