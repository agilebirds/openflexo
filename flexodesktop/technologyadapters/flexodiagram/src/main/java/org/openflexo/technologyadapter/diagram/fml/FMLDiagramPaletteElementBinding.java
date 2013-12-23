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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject.NamedViewPointObjectImpl;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPalette;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramPaletteElement;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;

/**
 * Encodes the binding between {@link DiagramPaletteElement} (part of {@link DiagramSpecification}) and the current {@link VirtualModel}<br>
 * 
 * The goal is for example to associate a {@link PaletteElement} to a given {@link DropScheme} of a particular {@link EditionPattern}
 * 
 * @author sylvain
 * 
 */
public class FMLDiagramPaletteElementBinding extends NamedViewPointObjectImpl {

	private static final Logger logger = Logger.getLogger(FMLDiagramPaletteElementBinding.class.getPackage().getName());

	/**
	 * The addressed palette element we want to bind to something in {@link VirtualModel}
	 */
	private final DiagramPaletteElement paletteElement = null;

	private String _editionPatternId;
	private String _dropSchemeName;

	private EditionPattern editionPattern;
	private DropScheme dropScheme;
	private Vector<FMLDiagramPaletteElementBindingParameter> parameters;
	private String patternRoleName;

	private boolean boundLabelToElementName = true;

	// Represent graphical representation to be used as overriding representation
	private final Vector<OverridingGraphicalRepresentation> overridingGraphicalRepresentations;

	// private Vector<DiagramPaletteElement> childs;

	public FMLDiagramPaletteElementBinding() {
		super();
		overridingGraphicalRepresentations = new Vector<OverridingGraphicalRepresentation>();
		parameters = new Vector<FMLDiagramPaletteElementBindingParameter>();
	}

	public/*DiagramModelSlot*/ModelSlot<?> getDiagramModelSlot() {
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return getVirtualModel().getViewPoint();
	}

	public VirtualModel getVirtualModel() {
		return getDiagramModelSlot().getVirtualModel();
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

	public DiagramPaletteElement getPaletteElement() {
		return paletteElement;
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
		if (dropScheme == null && getEditionPattern() != null && getEditionPattern().getEditionSchemes(DropScheme.class).size() > 0) {
			dropScheme = getEditionPattern().getEditionSchemes(DropScheme.class).get(0);
		}
		return dropScheme;
	}

	public void setDropScheme(DropScheme aDropScheme) {
		if (dropScheme != aDropScheme) {
			dropScheme = aDropScheme;
			updateParameters();
		}
	}

	public Vector<FMLDiagramPaletteElementBindingParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<FMLDiagramPaletteElementBindingParameter> parameters) {
		this.parameters = parameters;
	}

	public void addToParameters(FMLDiagramPaletteElementBindingParameter parameter) {
		parameter.setElementBinding(this);
		parameters.add(parameter);
	}

	public void removeFromParameters(FMLDiagramPaletteElementBindingParameter parameter) {
		parameter.setElementBinding(null);
		parameters.remove(parameter);
	}

	public FMLDiagramPaletteElementBindingParameter getParameter(String name) {
		for (FMLDiagramPaletteElementBindingParameter p : parameters) {
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
		Vector<FMLDiagramPaletteElementBindingParameter> unusedParameterInstances = new Vector<FMLDiagramPaletteElementBindingParameter>();
		unusedParameterInstances.addAll(parameters);

		for (EditionScheme es : editionPattern.getEditionSchemes()) {
			for (EditionSchemeParameter parameter : es.getParameters()) {
				FMLDiagramPaletteElementBindingParameter parameterInstance = getParameter(parameter.getName());
				if (parameterInstance != null) {
					unusedParameterInstances.remove(parameterInstance);
					parameterInstance.setParameter(parameter);
				} else {
					parameterInstance = new FMLDiagramPaletteElementBindingParameter(parameter);
					addToParameters(parameterInstance);
				}
			}
		}

		for (FMLDiagramPaletteElementBindingParameter p : unusedParameterInstances) {
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
		anOverridingGraphicalRepresentation.paletteElementBinding = this;
		setChanged();
		notifyObservers();
	}

	public void removeFromOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation) {
		overridingGraphicalRepresentations.remove(anOverridingGraphicalRepresentation);
		anOverridingGraphicalRepresentation.paletteElementBinding = null;
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

	public void finalizeDeserialization() {
		getEditionPattern();
		updateParameters();
	}

	/*@Override
	public void setChanged() {
		super.setChanged();
		if (getPalette() != null) {
			getPalette().setIsModified();
		}
	}*/

	/*@Override
	public boolean delete() {
		if (getPalette() != null) {
			getPalette().removeFromElements(this);
		}
		super.delete();
		deleteObservers();
		return true;
	}*/

	public List<EditionPattern> allAvailableEditionPatterns() {
		if (getVirtualModel() != null) {
			List<EditionPattern> returned = new ArrayList<EditionPattern>();
			for (EditionPattern ep : getVirtualModel().getEditionPatterns()) {
				if (ep.getEditionSchemes(DropScheme.class).size() > 0) {
					returned.add(ep);
				}
			}
			return returned;
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

	public static abstract class OverridingGraphicalRepresentation extends NamedViewPointObjectImpl {
		private FMLDiagramPaletteElementBinding paletteElementBinding;
		private String patternRoleName;

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentation() {
			super();
		}

		// Do not use, required for deserialization
		public OverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
			super();
			patternRoleName = patternRole.getPatternRoleName();
		}

		@Override
		public BindingModel getBindingModel() {
			if (getPaletteElementBinding() != null) {
				return getPaletteElementBinding().getBindingModel();
			}
			return null;
		}

		public FMLDiagramPaletteElementBinding getPaletteElementBinding() {
			return paletteElementBinding;
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
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

		public DiagramPalette getPalette() {
			return paletteElementBinding.getPaletteElement().getPalette();
		}

		public VirtualModel getVirtualModel() {
			return paletteElementBinding.getVirtualModel();
		}

		@Override
		public String getURI() {
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			return getVirtualModel().getViewPoint();
		}
	}

	public static class ShapeOverridingGraphicalRepresentation extends OverridingGraphicalRepresentation {

		private ShapeGraphicalRepresentation graphicalRepresentation;

		// Do not use, required for deserialization
		public ShapeOverridingGraphicalRepresentation() {
			super();
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
		public ConnectorOverridingGraphicalRepresentation() {
			super();
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
		return "<not_implemented:" + getStringRepresentation() + ">";
	}

}
