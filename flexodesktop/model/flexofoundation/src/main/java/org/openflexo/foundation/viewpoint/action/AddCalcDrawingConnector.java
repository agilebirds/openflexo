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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.CalcDrawingConnector;
import org.openflexo.foundation.viewpoint.CalcDrawingObject;
import org.openflexo.foundation.viewpoint.CalcDrawingShape;


public class AddCalcDrawingConnector extends FlexoAction<AddCalcDrawingConnector,CalcDrawingShape,CalcDrawingObject> 
{

    private static final Logger logger = Logger.getLogger(AddCalcDrawingConnector.class.getPackage().getName());
    
    public static FlexoActionType<AddCalcDrawingConnector,CalcDrawingShape,CalcDrawingObject>  actionType 
    = new FlexoActionType<AddCalcDrawingConnector,CalcDrawingShape,CalcDrawingObject> (
    		"add_connector",
    		FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

        /**
         * Factory method
         */
        @Override
		public AddCalcDrawingConnector makeNewAction(CalcDrawingShape focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor) 
        {
            return new AddCalcDrawingConnector(focusedObject, globalSelection, editor);
        }

        @Override
		protected boolean isVisibleForSelection(CalcDrawingShape shape, Vector<CalcDrawingObject> globalSelection) 
        {
            return true;
        }

        @Override
		protected boolean isEnabledForSelection(CalcDrawingShape shape, Vector<CalcDrawingObject> globalSelection) 
        {
            return (shape != null);
        }
                
    };
    
	static {
		FlexoModelObject.addActionForClass (AddCalcDrawingConnector.actionType, CalcDrawingShape.class);
	}


	private CalcDrawingShape _fromShape;
	public CalcDrawingShape toShape;
	public String newConnectorName;
	public String annotation;
	public Object graphicalRepresentation;

	private CalcDrawingConnector _newConnector;

	AddCalcDrawingConnector (CalcDrawingShape focusedObject, Vector<CalcDrawingObject> globalSelection, FlexoEditor editor)
    {
        super(actionType, focusedObject, globalSelection, editor);
    }

     @Override
	protected void doAction(Object context)
     {
    	 logger.info ("Add connector");
    	 if (getFocusedObject() != null 
    			 && getFromShape() != null
    			 && toShape != null)  {
    		 CalcDrawingObject parent = CalcDrawingObject.getFirstCommonAncestor(getFromShape(), toShape);
    		 logger.info("Parent="+parent);
    		 if (parent == null) throw new IllegalArgumentException("No common ancestor");
    		 _newConnector = new CalcDrawingConnector(getFromShape(),toShape);
    		 if (graphicalRepresentation != null) _newConnector.setGraphicalRepresentation(graphicalRepresentation);
    		 _newConnector.setName(newConnectorName);
    		 _newConnector.setDescription(annotation);
    		 parent.addToChilds(_newConnector);
    	 }
    	 else {
    		 logger.warning("Focused shape is null !");
    	 }
     }

	public CalcDrawingShape getFromShape() 
	{
		if (_fromShape == null) return getFocusedObject();
		return _fromShape;
	}

	public void setFromShape(CalcDrawingShape fromShape)
	{
		_fromShape = fromShape;
	}

	public CalcDrawingConnector getNewConnector() 
	{
		return _newConnector;
	}
	

}
