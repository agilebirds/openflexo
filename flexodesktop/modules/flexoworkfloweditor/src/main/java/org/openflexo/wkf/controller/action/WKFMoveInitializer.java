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

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.wkf.action.WKFMove;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class WKFMoveInitializer extends ActionInitializer {

    private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WKFMoveInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(WKFMove.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<WKFMove> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<WKFMove>() {
            @Override
			public boolean run(ActionEvent e, WKFMove action)
            {
               return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<WKFMove> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<WKFMove>() {
            @Override
			public boolean run(ActionEvent e, WKFMove action)
            {
            	getControllerActionInitializer().getWKFSelectionManager().setSelectedObjects(action.getGlobalSelection());
                return true;
            }
        };
	}

     @Override
  	protected FlexoActionUndoFinalizer<WKFMove> getDefaultUndoFinalizer() 
  	{
  		return new FlexoActionUndoFinalizer<WKFMove>() {
              @Override
			public boolean run(ActionEvent e, WKFMove action)
              {
              	getControllerActionInitializer().getWKFSelectionManager().setSelectedObjects(action.getGlobalSelection());
                  return true;
              }
          };
  	}

     @Override
 	protected FlexoActionRedoFinalizer<WKFMove> getDefaultRedoFinalizer() 
 	{
 		return new FlexoActionRedoFinalizer<WKFMove>() {
             @Override
			public boolean run(ActionEvent e, WKFMove action)
             {
             	getControllerActionInitializer().getWKFSelectionManager().setSelectedObjects(action.getGlobalSelection());
                 return true;
             }
         };
 	}

}
