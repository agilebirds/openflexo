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
	package org.openflexo.ced.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ontology.action.AddAnnotationStatement;

public class AddAnnotationStatementInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddAnnotationStatementInitializer(CEDControllerActionInitializer actionInitializer)
	{
		super(AddAnnotationStatement.actionType,actionInitializer);
	}
	
	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() 
	{
		return (CEDControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<AddAnnotationStatement> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddAnnotationStatement>() {
            @Override
			public boolean run(ActionEvent e, final AddAnnotationStatement action)
            {
             	return false;           	
             }
         };
	}

     @Override
	protected FlexoActionFinalizer<AddAnnotationStatement> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddAnnotationStatement>() {
            @Override
			public boolean run(ActionEvent e, AddAnnotationStatement action)
            {
            	//((OEController)getController()).getSelectionManager().setSelectedObject(action.getNewStatement());
                 return true;
           }
        };
	}
     
	@Override
	protected Icon getEnabledIcon() 
	{
		return OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON;
	}
 
}
