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

import org.openflexo.foundation.Inspectors;
import org.openflexo.xmlcode.XMLMapping;


public class ViewPointPaletteElement extends ViewPointObject {

	private static final Logger logger = Logger.getLogger(ViewPointPaletteElement.class.getPackage().getName());

	private String name;
	private String description;
	private String _editionPatternId;
	private EditionPattern editionPattern;
	private Vector<PaletteElementPatternParameter> parameters;
	
	// We dont want to import graphical engine in foundation
    // But you can assert graphical representation here is a org.openflexo.fge.ShapeGraphicalRepresentation.
	private Object graphicalRepresentation;

	private ViewPointPalette _palette;
	
	public ViewPointPaletteElement() 
	{
		parameters = new Vector<PaletteElementPatternParameter>();
	}
	
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

	@Override
	public ViewPoint getCalc() 
	{
		if (getPalette() != null) {
			return getPalette().getCalc();
		}
		return null;
	}

	@Override
	public XMLMapping getXMLMapping() 
	{
		return getViewPointLibrary().get_VIEW_POINT_PALETTE_MODEL();
	}

	public ViewPointPalette getPalette() 
	{
		return _palette;
	}

	public void setPalette(ViewPointPalette palette) 
	{
		_palette = palette;
	}
	
	public String _getEditionPatternId() 
	{
		if (getEditionPattern() != null) {
			return getEditionPattern().getName();
		}
		return _editionPatternId;
	}

	public void _setEditionPatternId(String editableConcept) 
	{
		_editionPatternId = editableConcept;
	}

	public EditionPattern getEditionPattern() 
	{
		if (editionPattern != null) {
			return editionPattern;
		}
		if ((_editionPatternId != null) && (getCalc() != null)) {
			editionPattern = getCalc().getEditionPattern(_editionPatternId);
			updateParameters();
		}
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern anEditionPattern) 
	{
		if (anEditionPattern != editionPattern) {
			editionPattern = anEditionPattern;
			updateParameters();
		}
	}

	public Vector<PaletteElementPatternParameter> getParameters() 
	{
		return parameters;
	}

	public void setParameters(Vector<PaletteElementPatternParameter> parameters) 
	{
		this.parameters = parameters;
	}

	public void addToParameters(PaletteElementPatternParameter parameter)
	{
		parameter.setElement(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(PaletteElementPatternParameter parameter)
	{
		parameter.setElement(null);
		parameters.remove(parameter);
	}
	
	public PaletteElementPatternParameter getParameter(String name)
	{
		for (PaletteElementPatternParameter p : parameters) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	private void updateParameters()
	{
		if (editionPattern == null) {
			return;
		}
		Vector<PaletteElementPatternParameter> unusedParameterInstances = new Vector<PaletteElementPatternParameter>();
		unusedParameterInstances.addAll(parameters);
		
		for (EditionScheme es : editionPattern.getEditionSchemes()) {
			for (EditionPatternParameter parameter : es.getParameters()) {
				PaletteElementPatternParameter parameterInstance = getParameter(parameter.getName());
				if (parameterInstance != null) {
					unusedParameterInstances.remove(parameterInstance);
					parameterInstance.setParameter(parameter);
				}
				else {
					parameterInstance = new PaletteElementPatternParameter(parameter);
					addToParameters(parameterInstance);
				}
			}
		}
		
		for (PaletteElementPatternParameter p : unusedParameterInstances) {
			removeFromParameters(p);
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getEditionPattern();
		updateParameters();
	}
	
	@Override
	public void setChanged()
	{
		super.setChanged();
		if (getPalette() != null) {
			getPalette().setIsModified();
		}
	}

	@Override
	public void delete()
	{
		if (getPalette() != null) {
			getPalette().removeFromElements(this);
		}
		super.delete();
		deleteObservers();
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.CALC_PALETTE_ELEMENT_INSPECTOR;
	}

	public Object getGraphicalRepresentation() 
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(Object graphicalRepresentation)
	{
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public Vector<EditionPattern> allAvailableEditionPatterns()
	{
		return getCalc().getAllEditionPatternWithDropScheme();
	}
	

}
