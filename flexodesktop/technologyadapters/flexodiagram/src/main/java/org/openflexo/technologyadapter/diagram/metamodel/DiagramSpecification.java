/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.technologyadapter.diagram.metamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.fml.DiagramEditionScheme;
import org.openflexo.technologyadapter.diagram.fml.LinkScheme;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteInserted;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteRemoved;
import org.openflexo.technologyadapter.diagram.model.dm.ExampleDiagramInserted;
import org.openflexo.technologyadapter.diagram.model.dm.ExampleDiagramRemoved;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramSpecificationResourceImpl;
import org.openflexo.toolbox.ChainedCollection;

/**
 * A {@link DiagramSpecification} is the specification of a Diagram<br>
 * 
 * A {@link DiagramSpecification} contains some palettes and example diagrams
 * 
 * @author sylvain
 * 
 */
public class DiagramSpecification extends FlexoObjectImpl implements FlexoMetaModel<DiagramSpecification>,
		ResourceData<DiagramSpecification> {

	private static final Logger logger = Logger.getLogger(DiagramSpecification.class.getPackage().getName());

	private final List<DiagramPalette> palettes;
	private final List<ExampleDiagram> exampleDiagrams;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	/**
	 * Creates a new VirtualModel on user request<br>
	 * Creates both the resource and the object
	 * 
	 * 
	 * @param baseName
	 * @param viewPoint
	 * @return
	 */
	public static DiagramSpecification newDiagramSpecification(String baseName, File diagramSpecificationDirectory) {
		File diagramSpecificationXMLFile = new File(diagramSpecificationDirectory, baseName + ".xml");
		DiagramSpecificationResource dsRes = DiagramSpecificationResourceImpl.makeDiagramSpecificationResource(
				diagramSpecificationDirectory, diagramSpecificationXMLFile, viewPoint.getResource(), viewPointLibrary);
		DiagramSpecification diagramSpecification = new DiagramSpecification(viewPoint);
		dsRes.setResourceData(diagramSpecification);
		diagramSpecification.setResource(dsRes);
		diagramSpecification.makeReflexiveModelSlot();
		diagramSpecification.save();
		return diagramSpecification;
	}

	// Used during deserialization, do not use it
	public DiagramSpecification(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
		exampleDiagrams = new ArrayList<ExampleDiagram>();
		palettes = new ArrayList<DiagramPalette>();
	}

	/**
	 * Creates a new DiagramSpecification in supplied viewpoint, with a single model slot for DiagramTechnologyAdapter called 'diagram'
	 * 
	 * @param viewPoint
	 */
	public DiagramSpecification(ViewPoint viewPoint) {
		this((VirtualModel.VirtualModelBuilder) null);
		setViewPoint(viewPoint);

		/*if (viewPoint.getViewPointLibrary().getServiceManager() != null
				&& viewPoint.getViewPointLibrary().getServiceManager().getService(TechnologyAdapterService.class) != null) {
			DiagramTechnologyAdapter diagramTA = viewPoint.getViewPointLibrary().getServiceManager()
					.getService(TechnologyAdapterService.class).getTechnologyAdapter(DiagramTechnologyAdapter.class);
			DiagramModelSlot diagramMS = diagramTA.createNewModelSlot(this);
			diagramMS.setName("diagram");
			addToModelSlots(diagramMS);
		}*/
	}

	@Override
	protected TypedDiagramModelSlot makeReflexiveModelSlot() {
		if (getViewPoint().getViewPointLibrary().getServiceManager() != null
				&& getViewPoint().getViewPointLibrary().getServiceManager().getService(TechnologyAdapterService.class) != null) {
			DiagramTechnologyAdapter diagramTA = getViewPoint().getViewPointLibrary().getServiceManager()
					.getService(TechnologyAdapterService.class).getTechnologyAdapter(DiagramTechnologyAdapter.class);
			TypedDiagramModelSlot returned = diagramTA.makeModelSlot(TypedDiagramModelSlot.class, this);
			returned.setVirtualModelResource(getResource());
			returned.setName(REFLEXIVE_MODEL_SLOT_NAME);
			addToModelSlots(returned);
			return returned;
		}
		logger.warning("Could not instanciate reflexive model slot");
		return null;
	}

	/**
	 * Load eventually unloaded VirtualModels<br>
	 * After this call return, we can assert that all {@link VirtualModel} are loaded.
	 */
	private void loadDiagramPalettesWhenUnloaded() {
		for (org.openflexo.foundation.resource.FlexoResource<?> r : getResource().getContents()) {
			if (r instanceof DiagramPaletteResource) {
				((DiagramPaletteResource) r).getDiagramPalette();
			}
		}
	}

	/**
	 * Load eventually unloaded VirtualModels<br>
	 * After this call return, we can assert that all {@link VirtualModel} are loaded.
	 */
	private void loadExampleDiagramsWhenUnloaded() {
		for (org.openflexo.foundation.resource.FlexoResource<?> r : getResource().getContents()) {
			if (r instanceof ExampleDiagramResource) {
				((ExampleDiagramResource) r).getExampleDiagram();
			}
		}
	}

	@Override
	public DiagramSpecificationResource getResource() {
		return (DiagramSpecificationResource) super.getResource();
	}

	@Override
	public String toString() {
		return "DiagramSpecification:" + getURI();
	}

	public List<DiagramPalette> getPalettes() {
		loadDiagramPalettesWhenUnloaded();
		return palettes;
	}

	public DiagramPalette getPalette(String paletteName) {
		if (paletteName == null) {
			return null;
		}
		loadDiagramPalettesWhenUnloaded();
		for (DiagramPalette p : getPalettes()) {
			if (paletteName.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	public void addToPalettes(DiagramPalette aPalette) {
		palettes.add(aPalette);
		setChanged();
		notifyObservers(new DiagramPaletteInserted(aPalette, this));
	}

	public void removeFromPalettes(DiagramPalette aPalette) {
		palettes.remove(aPalette);
		setChanged();
		notifyObservers(new DiagramPaletteRemoved(aPalette, this));
	}

	public List<ExampleDiagram> getExampleDiagrams() {
		loadExampleDiagramsWhenUnloaded();
		return exampleDiagrams;
	}

	public ExampleDiagram getExampleDiagram(String diagramName) {
		if (diagramName == null) {
			return null;
		}
		loadExampleDiagramsWhenUnloaded();
		for (ExampleDiagram s : getExampleDiagrams()) {
			if (diagramName.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void addToExampleDiagrams(ExampleDiagram aShema) {
		exampleDiagrams.add(aShema);
		setChanged();
		notifyObservers(new ExampleDiagramInserted(aShema, this));
	}

	public void removeFromExampleDiagrams(ExampleDiagram aShema) {
		exampleDiagrams.remove(aShema);
		setChanged();
		notifyObservers(new ExampleDiagramRemoved(aShema, this));
	}

	@Override
	protected void notifyEditionSchemeModified() {
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
	}

	private Vector<EditionPattern> _allEditionPatternWithDropScheme;
	private Vector<EditionPattern> _allEditionPatternWithLinkScheme;

	public Vector<EditionPattern> getAllEditionPatternWithDropScheme() {
		if (_allEditionPatternWithDropScheme == null) {
			_allEditionPatternWithDropScheme = new Vector<EditionPattern>();
			for (EditionPattern p : getEditionPatterns()) {
				if (p.hasDropScheme()) {
					_allEditionPatternWithDropScheme.add(p);
				}
			}
		}
		return _allEditionPatternWithDropScheme;
	}

	public Vector<EditionPattern> getAllEditionPatternWithLinkScheme() {
		if (_allEditionPatternWithLinkScheme == null) {
			_allEditionPatternWithLinkScheme = new Vector<EditionPattern>();
			for (EditionPattern p : getEditionPatterns()) {
				if (p.hasLinkScheme()) {
					_allEditionPatternWithLinkScheme.add(p);
				}
			}
		}
		return _allEditionPatternWithLinkScheme;
	}

	@Override
	public void addToEditionPatterns(EditionPattern pattern) {
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
		super.addToEditionPatterns(pattern);
	}

	@Override
	public void removeFromEditionPatterns(EditionPattern pattern) {
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
		super.removeFromEditionPatterns(pattern);
	}

	public Vector<LinkScheme> getAllConnectors() {
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				returned.add(s);
			}
		}
		return returned;
	}

	public Vector<LinkScheme> getConnectorsMatching(EditionPattern fromConcept, EditionPattern toConcept) {
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				if (s.isValidTarget(fromConcept, toConcept)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	@Override
	public boolean handleVariable(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.TOP_LEVEL)) {
			return true;
		}
		return super.handleVariable(variable);
	}

	@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getEditionPatterns(), getModelSlots(), getPalettes(),
					getExampleDiagrams());
		}
		return validableObjects;
	}

	@Override
	public final void finalizeDeserialization(Object builder) {
		/*if (builder instanceof VirtualModel.VirtualModelBuilder
				&& ((VirtualModel.VirtualModelBuilder) builder).getModelVersion().isLesserThan(new FlexoVersion("1.0"))) {
			// There were no model slots before 1.0, please add them
			convertTo_1_0(((VirtualModel.VirtualModelBuilder) builder).getViewPointLibrary());
		}*/
		super.finalizeDeserialization(builder);
		updateBindingModel();
	}

	public String getName() {
		if (getResource() != null) {
			return getResource().getName();
		}
		return null;
	}

	public void setName(String name) {
		if (requireChange(getName(), name)) {
			if (getResource() != null) {
				getResource().setName(name);
			}
		}
	}

}
