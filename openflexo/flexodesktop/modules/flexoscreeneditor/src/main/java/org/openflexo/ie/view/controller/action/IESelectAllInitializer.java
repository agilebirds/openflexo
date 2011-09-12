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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.action.IESelectAll;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class IESelectAllInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	IESelectAllInitializer(IEControllerActionInitializer actionInitializer)
	{
		super(IESelectAll.actionType,actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() 
	{
		return (IEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<IESelectAll> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<IESelectAll>() {
			@Override
			public boolean run(ActionEvent e, IESelectAll action)
			{
				return true;
			}
		};
	}
	
	@Override
	protected FlexoActionEnableCondition getEnableCondition() {
		return new FlexoActionEnableCondition<IESelectAll,IEObject,IEObject>() {

			@Override
			public boolean isEnabled(FlexoActionType<IESelectAll,IEObject,IEObject> actionType, IEObject object,
					Vector<IEObject> globalSelection, FlexoEditor editor) {
				return false;
			}
			
		};
	}

	@Override
	protected FlexoActionFinalizer<IESelectAll> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<IESelectAll>() {
			@Override
			public boolean run(ActionEvent e, IESelectAll action)
			{
				getControllerActionInitializer().getIESelectionManager().performSelectionSelectAll();
				return true;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_A, FlexoCst.META_MASK);
	}

}
