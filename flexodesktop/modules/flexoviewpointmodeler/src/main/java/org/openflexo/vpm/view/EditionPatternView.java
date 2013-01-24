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
import org.openflexo.vpm.CEDCst;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.controller.ViewPointPerspective;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class EditionPatternView extends FIBModuleView<EditionPattern> {

	public EditionPatternView(EditionPattern editionPattern, VPMController controller) {
		super(editionPattern, controller, CEDCst.EDITION_PATTERN_VIEW_FIB);

		controller.manageResource(editionPattern.getVirtualModel());

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
		FIBTabPanelView editionSchemePanel = (FIBTabPanelView) getFIBView("EditionSchemePanel");
		FIBTableWidget parametersTable = (FIBTableWidget) getFIBView("ParametersTable");
		FIBBrowserWidget editionActionBrowser = (FIBBrowserWidget) getFIBView("EditionActionBrowser");
		FIBTableWidget inspectorPropertyTable = (FIBTableWidget) getFIBView("InspectorPropertyTable");
		FIBTableWidget localizedTable = (FIBTableWidget) getFIBView("LocalizedTable");

		if (object instanceof PatternRole) {
			patternRoleTable.setSelectedObject(object);
		} else if (object instanceof EditionScheme) {
			mainTabPanel.setSelectedIndex(0);
			editionSchemeTable.setSelectedObject(object);
		} else if (object instanceof EditionSchemeParameter) {
			mainTabPanel.setSelectedIndex(0);
			editionSchemeTable.setSelectedObject(((EditionSchemeParameter) object).getEditionScheme());
			editionSchemePanel.setSelectedIndex(0);
			parametersTable.setSelectedObject(object);
		} else if (object instanceof EditionAction) {
			mainTabPanel.setSelectedIndex(0);
			editionSchemeTable.setSelectedObject(((EditionAction) object).getEditionScheme());
			editionSchemePanel.setSelectedIndex(1);
			editionActionBrowser.setSelectedObject(object);
		} else if (object instanceof EditionPatternInspector) {
			mainTabPanel.setSelectedIndex(1);
		} else if (object instanceof InspectorEntry) {
			mainTabPanel.setSelectedIndex(1);
			inspectorPropertyTable.setSelectedObject(object);
		}
	}
}
