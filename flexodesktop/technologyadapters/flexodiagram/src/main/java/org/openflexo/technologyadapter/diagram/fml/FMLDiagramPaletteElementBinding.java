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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelFactory;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
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
@ModelEntity
@ImplementationClass(FMLDiagramPaletteElementBinding.FMLDiagramPaletteElementBindingImpl.class)
@XMLElement
public interface FMLDiagramPaletteElementBinding extends NamedViewPointObject {

	@PropertyIdentifier(type = TypedDiagramModelSlot.class)
	public static final String DIAGRAM_MODEL_SLOT_KEY = "diagramModelSlot";
	@PropertyIdentifier(type = DiagramPaletteElement.class)
	public static final String PALETTE_ELEMENT_KEY = "paletteElement";
	@PropertyIdentifier(type = DropScheme.class)
	public static final String DROP_SCHEME_KEY = "dropScheme";
	@PropertyIdentifier(type = List.class)
	public static final String PARAMETERS_KEY = "parameters";
	@PropertyIdentifier(type = List.class)
	public static final String OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY = "overridingGraphicalRepresentations";

	@Getter(value = DIAGRAM_MODEL_SLOT_KEY, inverse = TypedDiagramModelSlot.PALETTE_ELEMENTS_BINDING_KEY)
	public TypedDiagramModelSlot getDiagramModelSlot();

	@Setter(DIAGRAM_MODEL_SLOT_KEY)
	public void setDiagramModelSlot(TypedDiagramModelSlot diagramModelSlot);

	@Getter(value = PALETTE_ELEMENT_KEY)
	public DiagramPaletteElement getPaletteElement();

	@Setter(PALETTE_ELEMENT_KEY)
	public void setPaletteElement(DiagramPaletteElement aPaletteElement);

	@Getter(value = DROP_SCHEME_KEY)
	public DropScheme getDropScheme();

	@Setter(DROP_SCHEME_KEY)
	public void setDropScheme(DropScheme aDropScheme);

	@Getter(
			value = PARAMETERS_KEY,
			cardinality = Cardinality.LIST,
			inverse = FMLDiagramPaletteElementBindingParameter.PALETTE_ELEMENT_BINDING_KEY)
	@XMLElement
	public List<FMLDiagramPaletteElementBindingParameter> getParameters();

	@Setter(PARAMETERS_KEY)
	public void setParameters(List<FMLDiagramPaletteElementBindingParameter> parameters);

	@Adder(PARAMETERS_KEY)
	public void addToParameters(FMLDiagramPaletteElementBindingParameter aParameter);

	@Remover(PARAMETERS_KEY)
	public void removeFromParameters(FMLDiagramPaletteElementBindingParameter aParameter);

	@Finder(collection = PARAMETERS_KEY, attribute = FMLDiagramPaletteElementBindingParameter.NAME_KEY)
	public FMLDiagramPaletteElementBindingParameter getParameter(String parameterName);

	@Getter(
			value = OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY,
			cardinality = Cardinality.LIST,
			inverse = OverridingGraphicalRepresentation.PALETTE_ELEMENT_BINDING_KEY)
	@XMLElement
	public List<OverridingGraphicalRepresentation> getOverridingGraphicalRepresentations();

	@Setter(OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY)
	public void setOverridingGraphicalRepresentations(List<OverridingGraphicalRepresentation> overridingGraphicalRepresentations);

	@Adder(OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY)
	public void addToOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation);

	@Remover(OVERRIDING_GRAPHICAL_REPRESENTATIONS_KEY)
	public void removeFromOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation);

	public GraphicalRepresentation getOverridingGraphicalRepresentation(GraphicalElementPatternRole<?, ?> patternRole);

	public VirtualModel getVirtualModel();

	public abstract class FMLDiagramPaletteElementBindingImpl extends NamedViewPointObjectImpl implements FMLDiagramPaletteElementBinding {

		private static final Logger logger = Logger.getLogger(FMLDiagramPaletteElementBinding.class.getPackage().getName());

		/**
		 * The addressed palette element we want to bind to something in {@link VirtualModel}
		 */
		private DiagramPaletteElement paletteElement = null;

		private TypedDiagramModelSlot diagramModelSlot;

		private String _editionPatternId;
		private String _dropSchemeName;

		private EditionPattern editionPattern;
		private DropScheme dropScheme;
		// private Vector<FMLDiagramPaletteElementBindingParameter> parameters;
		private String patternRoleName;

		private boolean boundLabelToElementName = true;

		// Represent graphical representation to be used as overriding representation
		// private final Vector<OverridingGraphicalRepresentation> overridingGraphicalRepresentations;

		// private Vector<DiagramPaletteElement> childs;

		public FMLDiagramPaletteElementBindingImpl() {
			super();
			// overridingGraphicalRepresentations = new Vector<OverridingGraphicalRepresentation>();
			// parameters = new Vector<FMLDiagramPaletteElementBindingParameter>();
		}

		@Override
		public TypedDiagramModelSlot getDiagramModelSlot() {
			if (diagramModelSlot == null && dropScheme != null) {
				VirtualModel vm = dropScheme.getVirtualModel();
				if (vm.getModelSlots(TypedDiagramModelSlot.class).size() > 0) {
					diagramModelSlot = vm.getModelSlots(TypedDiagramModelSlot.class).get(0);
				}
			}
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			return getVirtualModel().getViewPoint();
		}

		@Override
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

		@Override
		public DiagramPaletteElement getPaletteElement() {
			return paletteElement;
		}

		@Override
		public void setPaletteElement(DiagramPaletteElement paletteElement) {
			this.paletteElement = paletteElement;
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

		@Override
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

		@Override
		public void setDropScheme(DropScheme aDropScheme) {
			if (dropScheme != aDropScheme) {
				dropScheme = aDropScheme;
				updateParameters();
			}
		}

		/*public Vector<FMLDiagramPaletteElementBindingParameter> getParameters() {
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
		}*/

		private void updateParameters() {
			if (editionPattern == null) {
				return;
			}
			List<FMLDiagramPaletteElementBindingParameter> unusedParameterInstances = new ArrayList<FMLDiagramPaletteElementBindingParameter>();
			unusedParameterInstances.addAll(getParameters());

			for (EditionScheme es : editionPattern.getEditionSchemes()) {
				for (EditionSchemeParameter parameter : es.getParameters()) {
					FMLDiagramPaletteElementBindingParameter parameterInstance = getParameter(parameter.getName());
					if (parameterInstance != null) {
						unusedParameterInstances.remove(parameterInstance);
						parameterInstance.setParameter(parameter);
					} else {
						VirtualModelModelFactory factory = getVirtualModel().getVirtualModelFactory();
						parameterInstance = factory.newInstance(FMLDiagramPaletteElementBindingParameter.class);
						parameterInstance.setParameter(parameter);
						addToParameters(parameterInstance);
					}
				}
			}

			for (FMLDiagramPaletteElementBindingParameter p : unusedParameterInstances) {
				removeFromParameters(p);
			}
		}

		/*@Override
		public Vector<OverridingGraphicalRepresentation> getOverridingGraphicalRepresentations() {
			return overridingGraphicalRepresentations;
		}

		public void setOverridingGraphicalRepresentations(Vector<OverridingGraphicalRepresentation> overridingGraphicalRepresentations) {
			this.overridingGraphicalRepresentations.addAll(overridingGraphicalRepresentations);
		}

		@Override
		public void addToOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation) {
			overridingGraphicalRepresentations.add(anOverridingGraphicalRepresentation);
			anOverridingGraphicalRepresentation.paletteElementBinding = this;
			setChanged();
			notifyObservers();
		}

		@Override
		public void removeFromOverridingGraphicalRepresentations(OverridingGraphicalRepresentation anOverridingGraphicalRepresentation) {
			overridingGraphicalRepresentations.remove(anOverridingGraphicalRepresentation);
			anOverridingGraphicalRepresentation.paletteElementBinding = null;
			setChanged();
			notifyObservers();
		}*/

		@Override
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

}
