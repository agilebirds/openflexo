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
package org.openflexo.vpm.view;

import java.io.File;

import org.openflexo.fib.view.container.FIBPanelView;
import org.openflexo.fib.view.container.FIBTabPanelView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.FIBTableWidget;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.view.FIBModuleView;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.controller.ViewPointPerspective;

/**
 * This is the module view representing an EditionPattern<br>
 * Because an EditionPattern can be of multiple forms, this class is abstract and must be subclassed with a specific FIB
 * 
 * @author sguerin
 * 
 */
public abstract class EditionPatternView<EP extends EditionPattern> extends FIBModuleView<EP> {

	public EditionPatternView(EP editionPattern, VPMController controller, File fibFile) {
		super(editionPattern, controller, fibFile);
	}

	@Override
	public VPMController getFlexoController() {
		return (VPMController) super.getFlexoController();
	}

	@Override
	public ViewPointPerspective getPerspective() {
		return getFlexoController().VIEW_POINT_PERSPECTIVE;
	}

	public void tryToSelect(EditionPatternObject object) {
		FIBTableWidget patternRoleTable = (FIBTableWidget) getFIBView("PatternRoleTable");
		FIBTabPanelView mainTabPanel = (FIBTabPanelView) getFIBView("MainTabPanel");
		FIBTableWidget editionSchemeTable = (FIBTableWidget) getFIBView("EditionSchemeTable");
		FIBPanelView editionSchemePanel = (FIBPanelView) getFIBView("EditionSchemePanel");
		FIBTableWidget parametersTable = (FIBTableWidget) getFIBView("ParametersTable");
		FIBBrowserWidget editionActionBrowser = (FIBBrowserWidget) getFIBView("EditionActionBrowser");
		FIBTableWidget inspectorPropertyTable = (FIBTableWidget) getFIBView("InspectorPropertyTable");
		FIBTableWidget localizedTable = (FIBTableWidget) getFIBView("LocalizedTable");

		if (object instanceof PatternRole) {
			if(patternRoleTable!=null){
				patternRoleTable.setSelectedObject(object);
			}	
		} else if (object instanceof EditionScheme) {
			if(mainTabPanel!=null){
				mainTabPanel.setSelectedIndex(0);
			}
			if(editionSchemeTable!=null){
				editionSchemeTable.setSelectedObject(object);
			}	
		} else if (object instanceof EditionSchemeParameter) {
			if(mainTabPanel!=null){
				mainTabPanel.setSelectedIndex(0);
			}
			if(editionSchemeTable!=null){
				editionSchemeTable.setSelectedObject(((EditionSchemeParameter) object).getEditionScheme());	
			}
			if(parametersTable!=null){
				parametersTable.setSelectedObject(object);
			}
			// this is not a tab any more
			// editionSchemePanel.setSelectedIndex(0);
		} else if (object instanceof EditionAction) {
			if(mainTabPanel!=null){
				mainTabPanel.setSelectedIndex(0);
			}
			if(editionSchemeTable!=null){
				editionSchemeTable.setSelectedObject(((EditionAction) object).getEditionScheme());
			}
			// this is not a tab any more
			// editionSchemePanel.setSelectedIndex(1);
			if(editionActionBrowser!=null){
				editionActionBrowser.setSelectedObject(object);
			}
		} else if (object instanceof EditionPatternInspector) {
			if(mainTabPanel!=null){
				mainTabPanel.setSelectedIndex(1);
			}
		} else if (object instanceof InspectorEntry) {
			if(mainTabPanel!=null){
				mainTabPanel.setSelectedIndex(1);
			}
			if(inspectorPropertyTable!=null){
				inspectorPropertyTable.setSelectedObject(object);
			}
		}
	}
}
