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

import org.openflexo.antar.binding.BindingModel;

public class PaletteElementPatternParameter extends ViewPointObject {

	private EditionSchemeParameter _parameter;
	private ViewPointPaletteElement _element;
	private String name;
	private String value;

	public PaletteElementPatternParameter() {
	}

	public PaletteElementPatternParameter(EditionSchemeParameter p) {
		this();
		_parameter = p;
		setName(p.getName());
		setValue(p.getDefaultValue().toString());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		if (getParameter() != null) {
			if (getParameter() instanceof URIParameter) {
				return "< Computed URI >";
			}
			if (getParameter().getUsePaletteLabelAsDefaultValue()) {
				return "< Takes palette element label >";
			}
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getInspectorName() {
		// never inspected alone
		return null;
	}

	public boolean isEditable() {
		if (getParameter() != null) {
			return !(getParameter() instanceof URIParameter) && !getParameter().getUsePaletteLabelAsDefaultValue();
		}
		return true;
	}

	@Override
	public ViewPoint getCalc() {
		if (getElement() != null) {
			return getElement().getCalc();
		}
		return null;
	}

	public void setElement(ViewPointPaletteElement element) {
		_element = element;
	}

	public ViewPointPaletteElement getElement() {
		return _element;
	}

	public EditionSchemeParameter getParameter() {
		return _parameter;
	}

	public void setParameter(EditionSchemeParameter parameter) {
		_parameter = parameter;
	}

	@Override
	public BindingModel getBindingModel() {
		return getCalc().getBindingModel();
	}

}
