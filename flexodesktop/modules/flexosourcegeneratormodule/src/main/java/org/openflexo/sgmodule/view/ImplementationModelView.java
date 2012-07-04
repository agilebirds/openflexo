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
package org.openflexo.sgmodule.view;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.view.FIBModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ImplementationModelView extends FIBModuleView<ImplementationModel> {

	private FlexoPerspective declaredPerspective;

	public ImplementationModelView(ImplementationModel implementationModel, SGController controller,
			FlexoPerspective perspective) {
		super(implementationModel, controller, SGCst.IMPLEMENTATION_MODEL_VIEW_FIB, true);
		declaredPerspective = perspective;
	}

	@Override
	public SGController getFlexoController() {
		return (SGController) super.getFlexoController();
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FlexoFIBController<ImplementationModel> createFibController(FIBComponent fibComponent, FlexoController controller) {
		return new ImplementationModelFibController(fibComponent, controller);
	}
}
