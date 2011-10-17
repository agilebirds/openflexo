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
package org.openflexo.foundation.viewpoint.inspector;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DefaultBindingFactory;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.dm.InspectorEntryInserted;
import org.openflexo.foundation.viewpoint.dm.InspectorEntryRemoved;
import org.openflexo.logging.FlexoLogger;

/**
 * Represents inspector associated with an Edition Pattern
 * 
 * @author sylvain
 *
 */
public class EditionPatternInspector extends ViewPointObject implements Bindable {

	private static final Logger logger = FlexoLogger.getLogger(EditionPatternInspector.class.getPackage().toString());

	private String inspectorTitle;
	private EditionPattern _editionPattern;
	private Vector<InspectorEntry> entries;
	
	private BindingModel _bindingModel;

	public static EditionPatternInspector makeEditionPatternInspector(EditionPattern ep)
	{
		EditionPatternInspector returned = new EditionPatternInspector();
		returned.setInspectorTitle(ep.getName());
		ep.setInspector(returned);
		return returned;
	}
	
	public EditionPatternInspector() 
	{
		super();
		entries = new Vector<InspectorEntry>();
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
	public ViewPoint getCalc() 
	{
		if (getEditionPattern() != null)
			return getEditionPattern().getCalc();
		return null;
	}

	public ViewPointLibrary getCalcLibrary() 
	{
		return getCalc().getViewPointLibrary();
	}

	@Override
	public String getInspectorName() 
	{
		return null;
	}

	@Override
	public String getInspectorTitle()
	{
		return inspectorTitle;
	}

	public void setInspectorTitle(String inspectorTitle)
	{
		this.inspectorTitle = inspectorTitle;
	}

	public Vector<InspectorEntry> getEntries() 
	{
		return entries;
	}

	public void setEntries(Vector<InspectorEntry> someEntries)
	{
		entries = someEntries;
	}

	public void addToEntries(InspectorEntry anEntry)
	{
		anEntry.setInspector(this);
		entries.add(anEntry);
		setChanged();
		notifyObservers(new InspectorEntryInserted(anEntry, this));
	}

	public void removeFromEntries(InspectorEntry anEntry)
	{
		anEntry.setInspector(null);
		entries.remove(anEntry);
		setChanged();
		notifyObservers(new InspectorEntryRemoved(anEntry, this));
	}
	
	public TextFieldInspectorEntry createNewTextField()
	{
		TextFieldInspectorEntry newEntry = new TextFieldInspectorEntry();
		newEntry.setName("textfield");
		newEntry.setLabel("textfield");
		addToEntries(newEntry);
		return newEntry;
	}
	
	public TextAreaInspectorEntry createNewTextArea()
	{
		TextAreaInspectorEntry newEntry = new TextAreaInspectorEntry();
		newEntry.setName("textarea");
		newEntry.setLabel("textarea");
		addToEntries(newEntry);
		return newEntry;
	}
	
	public IntegerInspectorEntry createNewInteger()
	{
		IntegerInspectorEntry newEntry = new IntegerInspectorEntry();
		newEntry.setName("integer");
		newEntry.setLabel("integer");
		addToEntries(newEntry);
		return newEntry;
	}
	
	public CheckboxInspectorEntry createNewCheckbox()
	{
		CheckboxInspectorEntry newEntry = new CheckboxInspectorEntry();
		newEntry.setName("checkbox");
		newEntry.setLabel("checkbox");
		addToEntries(newEntry);
		return newEntry;
	}
	
	@Override
	public BindingFactory getBindingFactory() 
	{
		return BINDING_FACTORY;
	}
	
	@Override
	public BindingModel getBindingModel() 
	{
			if (_bindingModel == null) createBindingModel();
			return _bindingModel;
	}
	
	public void updateBindingModel()
	{
		logger.fine("updateBindingModel()");
		_bindingModel = null;
		createBindingModel();
	}
	
	private void createBindingModel()
	{
		_bindingModel = new BindingModel();
		for (PatternRole role : getEditionPattern().getPatternRoles()) {
			_bindingModel.addToBindingVariables(PatternRolePathElement.makePatternRolePathElement(role));
		}	
	}

	private static DefaultBindingFactory BINDING_FACTORY = new EditionPatternInspectorBindingFactory();

 }
