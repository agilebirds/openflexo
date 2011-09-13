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
package org.openflexo.ced.view;

import org.openflexo.ced.CEDCst;
import org.openflexo.ced.controller.CEDController;
import org.openflexo.ced.controller.CalcPerspective;
import org.openflexo.fib.controller.FIBComponentDynamicModel;
import org.openflexo.fib.controller.FIBTableDynamicModel;
import org.openflexo.fib.model.listener.FIBMouseClickListener;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.view.FIBModuleView;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class CalcLibraryView extends FIBModuleView<CalcLibrary> implements FIBMouseClickListener
{

	public CalcLibraryView(CalcLibrary calcLibrary, CEDController controller)
	{
		super(calcLibrary,controller,CEDCst.CALC_LIBRARY_VIEW_FIB);        
	}

	@Override
	public CEDController getFlexoController()
	{
		return (CEDController)super.getFlexoController();
	}

	@Override
	public CalcPerspective getPerspective() 
	{
		return getFlexoController().CALC_PERSPECTIVE;
	}

 	@Override
	public void mouseClicked(FIBComponentDynamicModel data, int clickCount)
	{
		if (data instanceof FIBTableDynamicModel
				&& ((FIBTableDynamicModel)data).selected instanceof FlexoModelObject
				&& clickCount == 2) {
			FlexoModelObject o = (FlexoModelObject)((FIBTableDynamicModel)data).selected;
			if (o instanceof OntologyCalc || o instanceof EditionPattern) {
				getFlexoController().selectAndFocusObject(o);
			}
		}
	}
}
