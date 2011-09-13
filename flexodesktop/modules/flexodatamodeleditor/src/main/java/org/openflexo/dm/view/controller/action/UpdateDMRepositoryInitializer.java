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
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.dm.view.popups.SelectClassesPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.LoadableDMEntity;
import org.openflexo.foundation.dm.action.UpdateDMRepository;

public class UpdateDMRepositoryInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	UpdateDMRepositoryInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(UpdateDMRepository.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<UpdateDMRepository> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<UpdateDMRepository>() {
            @Override
			public boolean run(ActionEvent e, UpdateDMRepository action)
            {
                DMRepository repository = action.getRepository();
                if (repository instanceof JDKRepository) {
                    action.makeFlexoProgress(FlexoLocalization.localizedForKey("analysing"), 2);
                    Vector<LoadableDMEntity> allEntities = new Vector<LoadableDMEntity>();
                    for (Enumeration en = repository.getEntities().elements(); en.hasMoreElements();) {
                        LoadableDMEntity entity = (LoadableDMEntity) en.nextElement();
                        if (entity.getType() != null) {
                            allEntities.add(entity);
                        }
                    }
                    SelectClassesPopup popup = new SelectClassesPopup(FlexoLocalization.localizedForKey("update_jdk_repository"),
                    		FlexoLocalization.localizedForKey("please_select_classes_to_update"),
                            FlexoLocalization.localizedForKey("update_jdk_repository_description"), allEntities, getProject(),
                            getControllerActionInitializer().getDMController(), action.getFlexoProgress());
                    action.hideFlexoProgress();
                    popup.setVisible(true);
                    if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) && (popup.getDMSet().getSelectedObjects().size() > 0)) {
                        action.setUpdatedSet(popup.getDMSet());
                        return true;
                    } else {
                        return false;
                    }
                } else if (repository instanceof ExternalRepository) {
                    action.makeFlexoProgress(FlexoLocalization.localizedForKey("analysing"), 2);
                    Vector<LoadableDMEntity> allEntities = new Vector<LoadableDMEntity>();
                    for (Enumeration en = repository.getEntities().elements(); en.hasMoreElements();) {
                        LoadableDMEntity entity = (LoadableDMEntity) en.nextElement();
                        if (entity.getType() != null) {
                            allEntities.add(entity);
                        }
                    }
                    SelectClassesPopup popup = new SelectClassesPopup(
                    		FlexoLocalization.localizedForKey("update_jar"), 
                    		FlexoLocalization.localizedForKey("please_select_properties_and_methods_to_update"),
                    		FlexoLocalization.localizedForKey("update_external_repository_description"), 
                    		(ExternalRepository) repository, getProject(),
                    		getControllerActionInitializer().getDMController(), action.getFlexoProgress());
                    action.hideFlexoProgress();
                    popup.setVisible(true);
                    if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) && (popup.getDMSet().getSelectedObjects().size() > 0)) {
                        action.setUpdatedSet(popup.getDMSet());
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<UpdateDMRepository> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<UpdateDMRepository>() {
            @Override
			public boolean run(ActionEvent e, UpdateDMRepository action)
            {
                 DMRepository repository = action.getRepository();
                getControllerActionInitializer().getDMController().getSelectionManager().setSelectedObject(repository);
                return true;
          }
        };
	}


	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.REFRESH_ICON;
	}
}
