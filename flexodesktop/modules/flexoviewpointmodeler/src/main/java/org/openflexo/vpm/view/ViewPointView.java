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

import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBTableDynamicModel;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.ModuleView;
import org.openflexo.vpm.VPMCst;
import org.openflexo.vpm.controller.VPMController;
import org.openflexo.vpm.controller.ViewPointPerspective;

/**
 * This is the {@link ModuleView} representing a {@link ViewPoint}
 * 
 * @author sguerin
 * 
 */
public class ViewPointView extends FIBModuleView<ViewPoint> implements FIBMouseClickListener {

	public ViewPointView(ViewPoint viewPoint, VPMController controller) {
		super(viewPoint, controller, VPMCst.VIEWPOINT_VIEW_FIB);
	}

	@Override
	public VPMController getFlexoController() {
		return (VPMController) super.getFlexoController();
	}

	@Override
	public ViewPointPerspective getPerspective() {
		return getFlexoController().VIEW_POINT_PERSPECTIVE;
	}

	@Override
	public void mouseClicked(FIBComponentDynamicModel data, int clickCount) {
		if (data instanceof FIBTableDynamicModel && ((FIBTableDynamicModel) data).selected instanceof FlexoModelObject && clickCount == 2) {
			FlexoObject o = (FlexoObject) ((FIBTableDynamicModel) data).selected;
			if (o instanceof ViewPoint || o instanceof EditionPattern || o instanceof ExampleDiagram || o instanceof DiagramPalette) {
				getFlexoController().selectAndFocusObject(o);
			}
		}
	}

}
