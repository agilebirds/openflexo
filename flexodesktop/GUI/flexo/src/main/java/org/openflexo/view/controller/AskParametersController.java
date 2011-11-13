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
package org.openflexo.view.controller;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.HelpDelegate;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorDelegate;
import org.openflexo.inspector.InspectorWidgetConfiguration;
import org.openflexo.inspector.model.TabModel;

/**
 * Controller dedicated to preferences: there is only one instance of this controller
 * 
 * @author sguerin
 */
public class AskParametersController implements AbstractController {

	private static AskParametersController _current;

	// private InspectorWidgetConfiguration inspectorConfiguration;

	private AskParametersController() {
		super();
	}

	protected static AskParametersController createInstance() {
		_current = new AskParametersController();
		return _current;
	}

	public static AskParametersController instance() {
		if (_current == null) {
			createInstance();
		}
		return _current;
	}

	public static boolean hasInstance() {
		return (_current != null);
	}

	@Override
	public InspectorDelegate getDelegate() {
		// Handle object with key-value coding
		return null;
	}

	@Override
	public HelpDelegate getHelpDelegate() {
		// No help for this kind of controller
		return null;
	}

	public void notifiedActiveTabChange(String newActiveTabName) {
		// Dont care
	}

	@Override
	public void notifiedInspectedObjectChange(InspectableObject newInspectedObject) {
		// Dont care
	}

	@Override
	public boolean isTabPanelVisible(TabModel tab, InspectableObject inspectable) {
		return true;
	}

	@Override
	public InspectorWidgetConfiguration getConfiguration() {
		/*if(inspectorConfiguration == null)
			inspectorConfiguration = new InspectorWidgetConfiguration() {

			public boolean showViewSourceButtonInWysiwyg()
			{
				return ModuleLoader.getUserType() == UserType.DEVELOPPER || ModuleLoader.getUserType() == UserType.MAINTAINER;
			}
		};

		return inspectorConfiguration;*/
		return null;
	}

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO Auto-generated method stub
		return false;
	}

}
