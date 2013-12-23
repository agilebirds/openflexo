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
package org.openflexo.technologyadapter.diagram.fml;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject.NamedViewPointObjectImpl;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;

public class FMLDiagramPaletteElementBindingParameter extends NamedViewPointObjectImpl {

	private EditionSchemeParameter _parameter;
	private FMLDiagramPaletteElementBinding elementBinding;
	private String value;

	public FMLDiagramPaletteElementBindingParameter() {
		super();
	}

	public FMLDiagramPaletteElementBindingParameter(EditionSchemeParameter p) {
		super();
		_parameter = p;
		setName(p.getName());
		setValue(p.getDefaultValue().toString());
	}

	@Override
	public String getURI() {
		return null;
	}

	public String getValue() {
		if (getParameter() != null) {
			if (getParameter() instanceof URIParameter) {
				return "< Computed URI >";
			}
			/*if (getParameter().getUsePaletteLabelAsDefaultValue()) {
				return "< Takes palette element label >";
			}*/
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isEditable() {
		if (getParameter() != null) {
			return !(getParameter() instanceof URIParameter) /*&& !getParameter().getUsePaletteLabelAsDefaultValue()*/;
		}
		return true;
	}

	public DiagramPalette getPalette() {
		if (getElementBinding() != null) {
			return getElementBinding().getPaletteElement().getPalette();
		}
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getVirtualModel().getViewPoint();
	}

	public VirtualModel getVirtualModel() {
		if (getElementBinding() != null) {
			return getElementBinding().getVirtualModel();
		}
		return null;
	}

	public void setElementBinding(FMLDiagramPaletteElementBinding elementBinding) {
		this.elementBinding = elementBinding;
	}

	public FMLDiagramPaletteElementBinding getElementBinding() {
		return elementBinding;
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
	public String getFMLRepresentation(FMLRepresentationContext context) {
		return "<not_implemented:" + getStringRepresentation() + ">";
	}

}
