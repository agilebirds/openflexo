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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.model.Diagram;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteInserted;
import org.openflexo.technologyadapter.diagram.model.dm.DiagramPaletteRemoved;
import org.openflexo.technologyadapter.diagram.model.dm.ExampleDiagramInserted;
import org.openflexo.technologyadapter.diagram.model.dm.ExampleDiagramRemoved;
import org.openflexo.technologyadapter.diagram.rm.DiagramPaletteResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
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

	private DiagramSpecificationResource resource;

	private final List<DiagramPalette> palettes;
	private final List<Diagram> exampleDiagrams;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private final ChainedCollection<ViewPointObject> validableObjects = null;

	/**
	 * Creates a new VirtualModel on user request<br>
	 * Creates both the resource and the object
	 * 
	 * 
	 * @param baseName
	 * @param viewPoint
	 * @return
	 */
	public static DiagramSpecification newDiagramSpecification(String uri, String baseName, File diagramSpecificationDirectory,
			FlexoServiceManager serviceManager) {
		File diagramSpecificationXMLFile = new File(diagramSpecificationDirectory, baseName + ".xml");
		DiagramSpecificationResource dsRes = DiagramSpecificationResourceImpl.makeDiagramSpecificationResource(uri,
				diagramSpecificationDirectory, diagramSpecificationXMLFile, serviceManager);
		DiagramSpecification diagramSpecification = new DiagramSpecification(serviceManager);
		dsRes.setResourceData(diagramSpecification);
		diagramSpecification.setResource(dsRes);
		try {
			dsRes.save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
		return diagramSpecification;
	}

	// Used during deserialization, do not use it
	public DiagramSpecification(FlexoServiceManager serviceManager) {
		super();
		exampleDiagrams = new ArrayList<Diagram>();
		palettes = new ArrayList<DiagramPalette>();
	}

	@Override
	public FlexoServiceManager getServiceManager() {
		return getResource().getServiceManager();
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
			if (r instanceof DiagramResource) {
				((DiagramResource) r).getDiagram();
			}
		}
	}

	@Override
	public DiagramSpecificationResource getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<DiagramSpecification> resource) {
		this.resource = (DiagramSpecificationResource) resource;
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

	public List<Diagram> getExampleDiagrams() {
		loadExampleDiagramsWhenUnloaded();
		return exampleDiagrams;
	}

	public Diagram getExampleDiagram(String diagramName) {
		if (diagramName == null) {
			return null;
		}
		loadExampleDiagramsWhenUnloaded();
		for (Diagram s : getExampleDiagrams()) {
			if (diagramName.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void addToExampleDiagrams(Diagram aDiagram) {
		exampleDiagrams.add(aDiagram);
		setChanged();
		notifyObservers(new ExampleDiagramInserted(aDiagram, this));
	}

	public void removeFromExampleDiagrams(Diagram aDiagram) {
		exampleDiagrams.remove(aDiagram);
		setChanged();
		notifyObservers(new ExampleDiagramRemoved(aDiagram, this));
	}

	/*@Override
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
	*/

	/*@Override
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getEditionPatterns(), getModelSlots(), getPalettes(),
					getExampleDiagrams());
		}
		return validableObjects;
	}*/

	/*@Override
	public final void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		updateBindingModel();
	}*/

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

	@Override
	public Object performSuperGetter(String propertyIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperSetModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSerializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeserializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equalsObject(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperDelete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperUndelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean undelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object cloneObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object cloneObject(Object... context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreatedByCloning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBeingCloned() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsReadOnly(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

}
