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
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.wkf.action.WKFPaste;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.controller.WKFController;


public class WKFPasteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WKFPasteInitializer(WKFControllerActionInitializer actionInitializer)
	{
		super(WKFPaste.actionType,actionInitializer);
	}
	
	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() 
	{
		return (WKFControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<WKFPaste> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<WKFPaste>() {
            @Override
			public boolean run(ActionEvent e, WKFPaste action)
            {
            	return true;
             }
        };
	}

     @Override
	protected FlexoActionFinalizer<WKFPaste> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<WKFPaste>() {
            @Override
			public boolean run(ActionEvent e, WKFPaste action)
            {
            	getControllerActionInitializer().getWKFSelectionManager().performSelectionPaste();
                return true;
           }
        };
	}

     @Override
     protected FlexoActionEnableCondition<WKFPaste,FlexoModelObject,FlexoModelObject> getEnableCondition()
 	{
 		return new FlexoActionEnableCondition<WKFPaste,FlexoModelObject,FlexoModelObject>() {
 			@Override
			public boolean isEnabled(FlexoActionType<WKFPaste, FlexoModelObject, FlexoModelObject> actionType, FlexoModelObject object, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
                return getControllerActionInitializer().getWKFSelectionManager().hasCopiedData();
			}
        };
 	}
 	
     @Override
    public WKFController getController() {
    	return (WKFController) super.getController();
    }
     
	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.PASTE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() 
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_V, FlexoCst.META_MASK);
	}

	@Override
	protected FlexoActionVisibleCondition<WKFPaste,FlexoModelObject,FlexoModelObject> getVisibleCondition() {
		return new FlexoActionVisibleCondition<WKFPaste, FlexoModelObject, FlexoModelObject>() {

			@Override
			public boolean isVisible(
					FlexoActionType<WKFPaste, FlexoModelObject, FlexoModelObject> actionType,
					FlexoModelObject object, Vector<FlexoModelObject> globalSelection,
					FlexoEditor editor) {
				return getController().getCurrentPerspective()==getController().PROCESS_EDITOR_PERSPECTIVE
				|| getController().getCurrentPerspective()==getController().ROLE_EDITOR_PERSPECTIVE;
			}
			
		};
	}
}
