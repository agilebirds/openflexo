package org.openflexo.fib.editor.controller;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBModelObject;

public class EditorAction {

	private String actionName;
	private Icon actionIcon;
	private ActionPerformer performer;
	private ActionAvailability availability;
	
	public EditorAction(String actionName, Icon actionIcon, ActionPerformer performer, ActionAvailability availability) 
	{
		super();
		this.actionName = actionName;
		this.actionIcon = actionIcon;
		this.performer = performer;
		this.availability = availability;
	}

	public static abstract class ActionPerformer
	{
		public abstract FIBModelObject performAction(FIBModelObject object);
	}

	public static abstract class ActionAvailability
	{
		public abstract boolean isAvailableFor(FIBModelObject object);
	}

	public String getActionName() 
	{
		return actionName;
	}

	public Icon getActionIcon()
	{
		return actionIcon;
	}

	public ActionPerformer getPerformer() 
	{
		return performer;
	}
	
	public ActionAvailability getAvailability()
	{
		return availability;
	}
}
