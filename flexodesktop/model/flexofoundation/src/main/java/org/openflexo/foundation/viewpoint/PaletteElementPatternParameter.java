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





public class PaletteElementPatternParameter extends ViewPointObject {

	private EditionPatternParameter _parameter;
	private ViewPointPaletteElement _element;
	private String name;
	private String value;
	
	public PaletteElementPatternParameter() {
	}
	
	public PaletteElementPatternParameter(EditionPatternParameter p) {
		this();
		_parameter = p;
		setName(p.getName());
		setValue(p.getDefaultValue());
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

	public String getValue()
	{
		if (getParameter() != null) {
			if (getParameter().getUniqueURI()) {
				return "< Computed URI >";
			}
			if (getParameter().getUsePaletteLabelAsDefaultValue()) {
				return "< Takes palette element label >";
			}
		}
		return value;
	}
	
	public void setValue(String value) 
	{
		this.value = value;
	}

	@Override
	public String getInspectorName() 
	{
		// never inspected alone
		return null;
	}

	public boolean isEditable()
	{
		if (getParameter() != null) {
			return !getParameter().getUniqueURI() && !getParameter().getUsePaletteLabelAsDefaultValue();
		}
		return true;
	}
	
	@Override
	public ViewPoint getCalc() 
	{
		if (getElement() != null) {
			return getElement().getCalc();
		}
		return null;
	}

	public void setElement(ViewPointPaletteElement element) 
	{
		_element = element;
	}

	public ViewPointPaletteElement getElement() 
	{
		return _element;
	}

	public EditionPatternParameter getParameter() {
		return _parameter;
	}

	public void setParameter(EditionPatternParameter parameter) {
		_parameter = parameter;
	}
	

}
