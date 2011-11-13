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
package org.openflexo.vpm.palette;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JPanel;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.view.ModuleView;
import org.openflexo.vpm.controller.ViewPointPerspective;

public class CalcPaletteModuleView extends JPanel implements ModuleView<ViewPointPalette> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CalcPaletteModuleView.class.getPackage().getName());

	private CalcPaletteController _controller;

	public CalcPaletteModuleView(final CalcPaletteController controller) {
		super();
		setLayout(new BorderLayout());
		_controller = controller;

		add(controller.getDrawingView(), BorderLayout.CENTER);
		validate();

		controller.getCEDController().manageResource(controller.getCalcPalette());
	}

	public CalcPaletteController getController() {
		return _controller;
	}

	@Override
	public void deleteModuleView() {
		getController().delete();
	}

	@Override
	public ViewPointPerspective getPerspective() {
		return getController().getCEDController().VIEW_POINT_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public ViewPointPalette getRepresentedObject() {
		return _controller.getCalcPalette();
	}

	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
		getPerspective().focusOnPalette(getRepresentedObject());
	}

}
