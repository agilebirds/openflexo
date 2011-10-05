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
package org.openflexo.foundation.viewpoint;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.viewpoint.EditionPatternParameter.WidgetType;
import org.openflexo.logging.FlexoLogger;


public abstract class EditionScheme extends ViewPointObject {

	protected static final Logger logger = FlexoLogger.getLogger(EditionScheme.class.getPackage().getName());

	public static enum EditionSchemeType
	{
		DropScheme,
		LinkScheme,
		ActionScheme
	}
	
	private String name;
	private String label;
	private String description;
	private Vector<EditionAction> actions;
	private Vector<EditionPatternParameter> parameters;
	private boolean skipConfirmationPanel = false;
	
	private EditionPattern _editionPattern;

	public EditionScheme() 
	{
		actions = new Vector<EditionAction>();
		parameters = new Vector<EditionPatternParameter>();
	}

	public abstract EditionSchemeType getEditionSchemeType();
	
	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getLabel() 
	{
		return label;
	}

	public void setLabel(String label) 
	{
		this.label = label;
	}

	public EditionPattern getEditionPattern() 
	{
		return _editionPattern;
	}

	public void setEditionPattern(EditionPattern editionPattern)
	{
		_editionPattern = editionPattern;
	}

	@Override
	public String getDescription() 
	{
		return description;
	}

	@Override
	public void setDescription(String description) 
	{
		this.description = description;
	}

	public EditionAction getAction(PatternRole role)
	{
		for (EditionAction a : getActions()) {
			if (a.getPatternRole() == role) return a;
		}
		return null;
	}
	
	public Vector<EditionAction> getActions() 
	{
		return actions;
	}

	public void setActions(Vector<EditionAction> actions) 
	{
		this.actions = actions;
	}

	public void addToActions(EditionAction action) 
	{
		action.setScheme(this);
		actions.add(action);
	}

	public void removeFromActions(EditionAction action)
	{
		action.setScheme(null);
		actions.remove(action);
	}
	
	public void actionFirst(EditionAction a)
	{
		actions.remove(a);
		actions.insertElementAt(a, 0);
		setChanged();
		notifyObservers();
	}
	
	public void actionUp(EditionAction a)
	{
		int index = actions.indexOf(a);
		actions.remove(a);
		actions.insertElementAt(a,index-1);
		setChanged();
		notifyObservers();
	}
	
	public void actionDown(EditionAction a)
	{
		int index = actions.indexOf(a);
		actions.remove(a);
		actions.insertElementAt(a,index+1);
		setChanged();
		notifyObservers();
	}
	
	public void actionLast(EditionAction a)
	{
		actions.remove(a);
		actions.add(a);
		setChanged();
		notifyObservers();
	}
	
	public Vector<EditionPatternParameter> getParameters() 
	{
		return parameters;
	}

	public void setParameters(Vector<EditionPatternParameter> someParameters) 
	{
		parameters = someParameters;
	}

	public void addToParameters(EditionPatternParameter parameter) 
	{
		parameter.setScheme(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(EditionPatternParameter parameter)
	{
		parameter.setScheme(null);
		parameters.remove(parameter);
	}

	public void parameterFirst(EditionPatternParameter p)
	{
		parameters.remove(p);
		parameters.insertElementAt(p, 0);
		setChanged();
		notifyObservers();
	}
	
	public void parameterUp(EditionPatternParameter p)
	{
		int index = parameters.indexOf(p);
		parameters.remove(p);
		parameters.insertElementAt(p,index-1);
		setChanged();
		notifyObservers();
	}
	
	public void parameterDown(EditionPatternParameter p)
	{
		int index = parameters.indexOf(p);
		parameters.remove(p);
		parameters.insertElementAt(p,index+1);
		setChanged();
		notifyObservers();
	}
	
	public void parameterLast(EditionPatternParameter p)
	{
		parameters.remove(p);
		parameters.add(p);
		setChanged();
		notifyObservers();
	}
	

	public EditionPatternParameter getParameter(String name)
	{
		if (name == null) return null;
		for (EditionPatternParameter p : parameters) {
			if (name.equals(p.getName())) return p;
		}
		return null;
	}
	
	@Override
	public ViewPoint getCalc() 
	{
		return getEditionPattern().getCalc();
	}
	
	public AddShape createAddShapeAction()
	{
		AddShape newAction = new AddShape();
		newAction.setContainer(EditionAction.CONTAINER);
		addToActions(newAction);
		return newAction;
	}

	public AddClass createAddClassAction()
	{
		AddClass newAction = new AddClass();
		addToActions(newAction);
		return newAction;
	}

	public AddIndividual createAddIndividualAction()
	{
		AddIndividual newAction = new AddIndividual();
		addToActions(newAction);
		return newAction;
	}

	public AddObjectProperty createAddObjectPropertyAction()
	{
		AddObjectProperty newAction = new AddObjectProperty();
		addToActions(newAction);
		return newAction;
	}

	public AddIsAProperty createAddIsAPropertyAction()
	{
		AddIsAProperty newAction = new AddIsAProperty();
		addToActions(newAction);
		return newAction;
	}

	public AddRestriction createAddRestrictionAction()
	{
		AddRestriction newAction = new AddRestriction();
		addToActions(newAction);
		return newAction;
	}

	public AddConnector createAddConnectorAction()
	{
		AddConnector newAction = new AddConnector();
		newAction._setFromShape(EditionAction.FROM_TARGET);
		newAction._setToShape(EditionAction.TO_TARGET);
		addToActions(newAction);
		return newAction;
	}

	public DeclarePatternRole createDeclarePatternRoleAction()
	{
		DeclarePatternRole newAction = new DeclarePatternRole();
		addToActions(newAction);
		return newAction;
	}

	public AddShema createAddShemaAction()
	{
		AddShema newAction = new AddShema();
		addToActions(newAction);
		return newAction;
	}

	public GoToAction createGoToAction()
	{
		GoToAction newAction = new GoToAction();
		addToActions(newAction);
		return newAction;
	}

	public EditionAction deleteAction(EditionAction anAction)
	{
		removeFromActions(anAction);
		anAction.delete();
		return anAction;
	}

	public EditionPatternParameter createTextFieldParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.TEXT_FIELD);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createLocalizedTextFieldParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.LOCALIZED_TEXT_FIELD);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createTextAreaParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.TEXT_AREA);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createIntegerParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.INTEGER);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createCheckBoxParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.CHECKBOX);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createDropDownParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.DROPDOWN);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter createCustomParameter()
	{
		EditionPatternParameter newParameter = new EditionPatternParameter();
		newParameter.setName("newParameter");
		newParameter.setLabel("label");
		newParameter.setWidget(WidgetType.CUSTOM);
		addToParameters(newParameter);
		return newParameter;
	}

	public EditionPatternParameter deleteParameter(EditionPatternParameter aParameter)
	{
		removeFromParameters(aParameter);
		aParameter.delete();
		return aParameter;
	}

	public void finalizeEditionSchemeDeserialization()
	{
		for (EditionAction a : getActions()) {
			if (a.getPatternRole() != null) {
				a.updatePatternRoleType();
			}
		}
	}

	public boolean getSkipConfirmationPanel()
	{
		return skipConfirmationPanel;
	}

	public void setSkipConfirmationPanel(boolean skipConfirmationPanel)
	{
		this.skipConfirmationPanel = skipConfirmationPanel;
	}


}
