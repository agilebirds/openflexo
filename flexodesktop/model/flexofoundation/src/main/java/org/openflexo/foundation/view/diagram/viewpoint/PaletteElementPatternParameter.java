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
package org.openflexo.foundation.view.diagram.viewpoint;

import java.util.Collection;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.URIParameter;

public class PaletteElementPatternParameter extends DiagramPaletteObject {

	private EditionSchemeParameter _parameter;
	private DiagramPaletteElement _element;
	private String value;

	public PaletteElementPatternParameter(DiagramPaletteBuilder builder) {
		super(builder);
	}

	public PaletteElementPatternParameter(EditionSchemeParameter p) {
		super((DiagramPaletteBuilder) null);
		_parameter = p;
		setName(p.getName());
		setValue(p.getDefaultValue().toString());
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
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

	public boolean isEditable() {
		if (getParameter() != null) {
			return !(getParameter() instanceof URIParameter) && !getParameter().getUsePaletteLabelAsDefaultValue();
		}
		return true;
	}

	@Override
	public DiagramPalette getPalette() {
		if (getElement() != null) {
			return getElement().getPalette();
		}
		return null;
	}

	@Override
	public DiagramSpecification getVirtualModel() {
		if (getElement() != null) {
			return getElement().getVirtualModel();
		}
		return null;
	}

	public void setElement(DiagramPaletteElement element) {
		_element = element;
	}

	public DiagramPaletteElement getElement() {
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
		return getViewPoint().getBindingModel();
	}

	@Override
	public String getLanguageRepresentation(LanguageRepresentationContext context) {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

}
