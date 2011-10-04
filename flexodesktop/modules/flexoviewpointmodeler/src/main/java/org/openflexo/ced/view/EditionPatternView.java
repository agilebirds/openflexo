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
import org.openflexo.ced.controller.ViewPointPerspective;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.view.FIBModuleView;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class EditionPatternView extends FIBModuleView<EditionPattern>
{

    public EditionPatternView(EditionPattern editionPattern, CEDController controller)
    {
        super(editionPattern,controller,CEDCst.EDITION_PATTERN_VIEW_FIB);
        
		controller.manageResource(editionPattern.getCalc());
		
    }

    @Override
    public CEDController getFlexoController()
    {
    	return (CEDController)super.getFlexoController();
    }
    
     @Override
	public ViewPointPerspective getPerspective() 
    {
    	return getFlexoController().VIEW_POINT_PERSPECTIVE;
    }
}
