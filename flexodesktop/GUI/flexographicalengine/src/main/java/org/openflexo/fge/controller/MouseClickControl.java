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
package org.openflexo.fge.controller;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.toolbox.ToolBox;


public class MouseClickControl extends MouseControl
{
	public int clickCount = 1;
	public MouseClickControlAction action;
	
	public MouseClickControl(String aName, MouseButton button, int clickCount, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) 
	{
		super(aName,shiftPressed,ctrlPressed,metaPressed,altPressed,button);
		this.clickCount = clickCount;
		action = MouseClickControlAction.MouseClickControlActionType.NONE.makeAction();
	}
	
	public MouseClickControl(String aName, MouseButton button, int clickCount, MouseClickControlAction action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) 
	{
		this(aName,button,clickCount,shiftPressed,ctrlPressed,metaPressed,altPressed);
		this.action = action;
	}
	
	public MouseClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) 
	{
		this(aName,button,clickCount,shiftPressed,ctrlPressed,metaPressed,altPressed);
		setActionType(actionType);
	}
	
	public static MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount)
	{
		return new MouseClickControl(aName,button,clickCount,false,false,false,false);
	}
	
	public static MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount, MouseClickControlAction action)
	{
		return new MouseClickControl(aName,button,clickCount,action,false,false,false,false);
	}
	
	public static MouseClickControl makeMouseShiftClickControl(String aName, MouseButton button, int clickCount)
	{
		return new MouseClickControl(aName,button,clickCount,true,false,false,false);
	}
	
	public static MouseClickControl makeMouseControlClickControl(String aName, MouseButton button, int clickCount)
	{
		return new MouseClickControl(aName,button,clickCount,false,true,false,false);
	}
	
	public static MouseClickControl makeMouseMetaClickControl(String aName, MouseButton button, int clickCount)
	{
		return new MouseClickControl(aName,button,clickCount,false,false,true,false);
	}
	
	public static MouseClickControl makeMouseAltClickControl(String aName, MouseButton button, int clickCount)
	{
		return new MouseClickControl(aName,button,clickCount,false,false,false,true);
	}

	public static MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType)
	{
		return new MouseClickControl(aName,button,clickCount,actionType,false,false,false,false);
	}
	
	public static MouseClickControl makeMouseShiftClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType)
	{
		return new MouseClickControl(aName,button,clickCount,actionType,true,false,false,false);
	}
	
	public static MouseClickControl makeMouseControlClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType)
	{
		return new MouseClickControl(aName,button,clickCount,actionType,false,true,false,false);
	}
	
	public static MouseClickControl makeMouseMetaClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType)
	{
		return new MouseClickControl(aName,button,clickCount,actionType,false,false,true,false);
	}
	
	public static MouseClickControl makeMouseAltClickControl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType)
	{
		return new MouseClickControl(aName,button,clickCount,actionType,false,false,false,true);
	}

	@Override
	public boolean isApplicable(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent e)
	{
		if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
			if (e.getButton()==MouseEvent.BUTTON1 && e.isControlDown()) {
				if (logger.isLoggable(Level.FINE)) logger.fine("Translating, mod="+e.getModifiers()+" button="+e.getButton());
				boolean wasConsumed = e.isConsumed();
				int mod = (e.getModifiers()&(~InputEvent.BUTTON1_MASK)&(~InputEvent.CTRL_MASK))|InputEvent.BUTTON3_MASK;
				e = new MouseEvent((Component) e.getSource(),e.getID(),e.getWhen(),mod,e.getX(),e.getY(),e.getClickCount(),false);
				if (wasConsumed) e.consume();
			}
		}
		
		if (!super.isApplicable(graphicalRepresentation, controller, e)) return false;
		return (e.getClickCount() == clickCount);
	}

	/**
	 * Handle click event, by performing what is required here
	 * If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 */
	public void handleClick(GraphicalRepresentation<?> graphicalRepresentation,DrawingController<?> controller, MouseEvent event)
	{
		if (action.handleClick(graphicalRepresentation, controller, event))
			event.consume();
	}

	public MouseClickControlActionType getActionType()
	{
		if (action != null) return action.getActionType();
		else return MouseClickControlActionType.NONE;
	}

	public void setActionType(MouseClickControlActionType actionType)
	{
		if (actionType != null) {
			action = actionType.makeAction();
		}
		else action = MouseClickControlActionType.NONE.makeAction();
	}
	
	@Override
	public String toString()
	{
		return "MouseClickControl["+name+","+getModifiersAsString()+",ACTION="+getActionType().name()+"]";
	}
	
	@Override
	protected String getModifiersAsString()
	{
		return super.getModifiersAsString()+",clicks="+clickCount;
	}
	

}