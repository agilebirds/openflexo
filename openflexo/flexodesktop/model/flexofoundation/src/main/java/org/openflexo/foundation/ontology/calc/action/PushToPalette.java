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
package org.openflexo.foundation.ontology.calc.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.calc.CalcDrawingObject;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;
import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.toolbox.StringUtils;


public class PushToPalette extends FlexoAction<PushToPalette,CalcDrawingShape,CalcDrawingObject> 
{

    private static final Logger logger = Logger.getLogger(PushToPalette.class.getPackage().getName());
    
    public static FlexoActionType<PushToPalette,CalcDrawingShape,CalcDrawingObject>  actionType 
    = new FlexoActionType<PushToPalette,CalcDrawingShape,CalcDrawingObject> (
    		"push_to_palette",
			FlexoActionType.defaultGroup,
			FlexoActionType.NORMAL_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public PushToPalette makeNewAction(CalcDrawingShape focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor) 
        {
            return new PushToPalette(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(CalcDrawingShape shape, Vector<CalcDrawingObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(CalcDrawingShape shape, Vector<CalcDrawingObject> globalSelection) 
        {
            return (shape != null && shape.getCalc().getPalettes().size() > 0);
        }
                
    };
    
	static {
		FlexoModelObject.addActionForClass (PushToPalette.actionType, CalcDrawingShape.class);
	}

	public Object graphicalRepresentation;
	public CalcPalette palette;
	public EditionPattern editionPattern;
	public String newElementName;

	private CalcPaletteElement _newPaletteElement;

	PushToPalette (CalcDrawingShape focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

     @Override
	protected void doAction(Object context)
     {
    	 logger.info ("Push to palette");
    	 if (getFocusedObject() != null 
    			 && palette != null)  {
    		 
    		 _newPaletteElement = palette.addPaletteElement(newElementName, getFocusedObject().getGraphicalRepresentation());
    		 _newPaletteElement.setEditionPattern(editionPattern);
    	 }
    	 else {
    		 logger.warning("Focused role is null !");
    	 }
     }

    public CalcPaletteElement getNewPaletteElement()
	{
		return _newPaletteElement;
	}
    
    public boolean isValid()
    {
    	return StringUtils.isNotEmpty(newElementName) && palette != null && editionPattern != null;
    }
}
