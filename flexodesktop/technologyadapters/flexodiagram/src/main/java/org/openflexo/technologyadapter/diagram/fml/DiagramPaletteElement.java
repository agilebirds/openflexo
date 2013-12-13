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

import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;

public class DiagramPaletteElement extends DiagramPaletteObject {

	private static final Logger logger = Logger.getLogger(DiagramPaletteElement.class.getPackage().getName());

	private String _editionPatternId;
	private String _dropSchemeName;

	private EditionPattern editionPattern;
	private DropScheme dropScheme;
	private Vector<PaletteElementPatternParameter> parameters;
	private String patternRoleName;

	private boolean boundLabelToElementName = true;

	// Represent graphical representation to be used as representation in the palette
	private ShapeGraphicalRepresentation graphicalRepresentation;

	// Represent graphical representation to be used as overriding representation
	private Vector<OverridingGraphicalRepresentation> overridingGraphicalRepresentations;

	private DiagramPaletteElement parent = null;
	// private Vector<DiagramPaletteElement> childs;

	private DiagramPalette _palette;

	public DiagramPaletteElement(DiagramPaletteBuilder builder) {
		super(builder);
		overridingGraphicalRepresentations = new Vector<OverridingGraphicalRepresentation>();
		parameters = new Vector<PaletteElementPatternParameter>();
	}

	public DiagramPaletteFactory getFactory() {
		return _palette.getFactory();
	}

	@Override
	public String getURI() {
		return getVirtualModel().getURI() + "." + getName();
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return getOverridingGraphicalRepresentations();
	}

	public DiagramSpecification getDiagramSpecification() {
		if (getPalette() != null) {
			return getPalette().getDiagramSpecification();
		}
		return null;
	}

	@Override
	public DiagramSpecification getVirtualModel() {
		if (getPalette() != null) {
			return getPalette().getVirtualModel();
		}
		return null;
	}

	@Override
	public DiagramPalette getPalette() {
		return _palette;
	}

	public void setPalette(DiagramPalette palette) {
		_palette = palette;
	}

	// Deserialization only, do not use
	public String _getEditionPatternId() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getName();
		}
		return _editionPatternId;
	}

	// Deserialization only, do not use
	public void _setEditionPatternId(String editionPatternId) {
		_editionPatternId = editionPatternId;
	}

	// Deserialization only, do not use
	public String _getDropSchemeName() {
		if (getDropScheme() != null) {
			return getDropScheme().getName();
		}
		return _dropSchemeName;
	}

	// Deserialization only, do not use
	public void _setDropSchemeName(String _dropSchemeName) {
		this._dropSchemeName = _dropSchemeName;
	}

	public DiagramPaletteElement getParent() {
		return parent;
	}

	public EditionPattern getEditionPattern() {
		if (editionPattern != null) {
			return editionPattern;
		}
		if (_editionPatternId != null && getVirtualModel() != null) {
			editionPattern = getVirtualModel().getEditionPattern(_editionPatternId);
			updateParameters();
		}
		return editionPattern;
	}

	public void setEditionPattern(EditionPattern anEditionPattern) {
		if (anEditionPattern != editionPattern) {
			editionPattern = anEditionPattern;
			updateParameters();
		}
	}

	public DropScheme getDropScheme() {
		if (dropScheme != null) {
			return dropScheme;
		}
		if (_dropSchemeName != null && getEditionPattern() != null
				&& getEditionPattern().getEditionScheme(_dropSchemeName) instanceof DropScheme) {
			dropScheme = (DropScheme) getEditionPattern().getEditionScheme(_dropSchemeName);
			updateParameters();
		}
		if (dropScheme == null && getEditionPattern() != null && getEditionPattern().getDropSchemes().size() > 0) {
			dropScheme = getEditionPattern().getDropSchemes().firstElement();
		}
		return dropScheme;
	}

	public void setDropScheme(DropScheme aDropScheme) {
		if (dropScheme != aDropScheme) {
			dropScheme = aDropScheme;
			updateParameters();
		}
	}

	public Vector<PaletteElementPatternParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<PaletteElementPatternParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(PaletteElementPatternParameter parameter) {
		parameter.setElement(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(PaletteElementPatternParameter parameter) {
		parameter.setElement(null);
		parameters.remove(parameter);
	}

	public PaletteElementPatternParameter getParameter(String name) {
		for (PaletteElementPatternParameter p : parameters) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	private void updateParameters() {
		if (editionPattern == null) {
			return;
		}
		Vector<PaletteElementPatternParameter> unusedParameterInstances = new Vector<PaletteElementPatternParameter>();
		unusedParameterInstances.addAll(parameters);

		for (EditionScheme es : editionPattern.getEditionSchemes()) {
			for (EditionSchemeParameter parameter : es.getParameters()) {
				PaletteElementPatternParameter parameterInstance = getParameter(parameter.getName());
				if (parameterInstance != null) {
					unusedParameterInstances.remove(parameterInstance);
					parameterInstance.setParameter(parameter);
				} else {
					parameterInstance = new PaletteElementPatternParameter(parameter);
					addToParameters(parameterInstance);
				}
			}
		}

		for (PaletteElementPatternParameter p : unusedParameterInstances) {
			removeFromParameters(p);
		}
	}

	public Vector<OverridingGraphicalRepresentation> getOverridingGraphicalRepresentations() {
		return overridingGraphicalRepresentations;
	}

	public void setOverridingGraphicalRepresentations(Vector<OverridingGraphicalRepresentation> overridingGraphicalRepresentations) {
		this.overridingGraphicalRepresentations.addAll(overridingGraphicalRepresentations);
	}

	public void addToOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation) {
		overridingGraphicalRepresentations.add(anOverridingGraphicalRepresentation);
		anOverridingGraphicalRepresentation.paletteElement = this;
		setChanged();
		notifyObservers();
	}

	public void removeFromOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation) {
		overridingGraphicalRepresentations.remove(anOverridingGraphicalRepresentation);
		anOverridingGraphicalRepresentation.paletteElement = null;
		setChanged();
		notifyObservers();
	}

	public GraphicalRepresentation getOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
		for (OverridingGraphicalRepresentation ogr : getOverridingGraphicalRepresentations()) {
			if (ogr.getPatternRoleName().equals(patternRole.getPatternRoleName())) {
				return ogr.getGraphicalRepresentation();
			}
		}
		return null;
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		getEditionPattern();
		updateParameters();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getPalette() != null) {
			getPalette().setIsModified();
		}
	}

	@Override
	public boolean delete() {
		if (getPalette() != null) {
			getPalette().removeFromElements(this);
		}
		super.delete();
		deleteObservers();
		return true;
	}

	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public Vector<EditionPattern> allAvailableEditionPatterns() {
		if (getVirtualModel() != null) {
			return getVirtualModel().getAllEditionPatternWithDropScheme();
		}
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getVirtualModel().getBindingModel();
	}

	public String getPatternRoleName() {
		return patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
	}

	public static abstract class OverridingGraphicalRepresentation extends DiagramPaletteObject {
		private DiagramPaletteElement paletteElement;
		private String patternRoleName;

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentation(DiagramPaletteBuilder builder) {
			super(builder);
		}

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
			super(null);
			patternRoleName = patternRole.getPatternRoleName();
		}

		@Override
		public BindingModel getBindingModel() {
			if (getPaletteElement() != null) {
				return getPaletteElement().getBindingModel();
			}
			return null;
		}

		@Override
		public DiagramSpecification getVirtualModel() {
			if (getPaletteElement() != null) {
				return getPaletteElement().getVirtualModel();
			}
			return null;
		}

		public DiagramPaletteElement getPaletteElement() {
			return paletteElement;
		}

		public String getPatternRoleName() {
			return patternRoleName;
		}

		public void setPatternRoleName(String patternRoleName) {
			this.patternRoleName = patternRoleName;
		}

		public abstract GraphicalRepresentation getGraphicalRepresentation();

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getFullyQualifiedName() + ">";
		}

		@Override
		public DiagramPalette getPalette() {
			return getPaletteElement().getPalette();
		}

		@Override
		public String getURI() {
			return null;
		}
	}

	public static class ShapeOverridingGraphicalRepresentation extends OverridingGraphicalRepresentation {

		private ShapeGraphicalRepresentation graphicalRepresentation;

		// Do not use, required for deserialization
		public ShapeOverridingGraphicalRepresentation(DiagramPaletteBuilder builder) {
			super(builder);
		}

		// Do not use, required for deserialization
		public ShapeOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole, ShapeGraphicalRepresentation gr) {
			super(patternRole);
			graphicalRepresentation = gr;
		}

		@Override
		public ShapeGraphicalRepresentation getGraphicalRepresentation() {
			return graphicalRepresentation;
		}

		public void setGraphicalRepresentation(ShapeGraphicalRepresentation graphicalRepresentation) {
			this.graphicalRepresentation = graphicalRepresentation;
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return null;
		}

	}

	public static class ConnectorOverridingGraphicalRepresentation extends OverridingGraphicalRepresentation {

		private ConnectorGraphicalRepresentation graphicalRepresentation;

		// Do not use, required for deserialization
		public ConnectorOverridingGraphicalRepresentation(DiagramPaletteBuilder builder) {
			super(builder);
		}

		// Do not use, required for deserialization
		public ConnectorOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole, ConnectorGraphicalRepresentation gr) {
			super(patternRole);
			graphicalRepresentation = gr;
		}

		@Override
		public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
			return graphicalRepresentation;
		}

		public void setGraphicalRepresentation(ConnectorGraphicalRepresentation graphicalRepresentation) {
			this.graphicalRepresentation = graphicalRepresentation;
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			return null;
		}
	}

	public boolean getBoundLabelToElementName() {
		return boundLabelToElementName;
	}

	public void setBoundLabelToElementName(boolean boundLabelToElementName) {
		if (this.boundLabelToElementName != boundLabelToElementName) {
			this.boundLabelToElementName = boundLabelToElementName;
			setChanged();
			notifyObservers(new DataModification("boundLabelToElementName", !boundLabelToElementName, boundLabelToElementName));
		}
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

}
