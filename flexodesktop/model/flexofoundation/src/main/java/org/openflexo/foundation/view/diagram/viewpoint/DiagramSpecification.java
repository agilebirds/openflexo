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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.view.diagram.DiagramModelSlot;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteRemoved;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramRemoved;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.FlexoVersion;

/**
 * A {@link DiagramSpecification} is the specification of a Diagram
 * 
 * @author sylvain
 * 
 */
public class DiagramSpecification extends VirtualModel<DiagramSpecification> {

	private static final Logger logger = Logger.getLogger(DiagramSpecification.class.getPackage().getName());

	private List<DiagramPalette> palettes;
	private List<ExampleDiagram> exampleDiagrams;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	// Used during deserialization, do not use it
	public DiagramSpecification(ViewPointBuilder builder) {
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
		this((ViewPointBuilder) null);
		if (viewPoint.getViewPointLibrary().getFlexoServiceManager() != null
				&& viewPoint.getViewPointLibrary().getFlexoServiceManager().getService(TechnologyAdapterService.class) != null) {
			DiagramTechnologyAdapter diagramTA = viewPoint.getViewPointLibrary().getFlexoServiceManager()
					.getService(TechnologyAdapterService.class).getTechnologyAdapter(DiagramTechnologyAdapter.class);
			DiagramModelSlot diagramMS = diagramTA.createNewModelSlot(this);
			diagramMS.setName("diagram");
			addToModelSlots(diagramMS);
		}
	}

	@Override
	public String toString() {
		return "DiagramSpecification:" + getURI();
	}

	public List<DiagramPalette> getPalettes() {
		return palettes;
	}

	public DiagramPalette getPalette(String paletteName) {
		if (paletteName == null) {
			return null;
		}
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
		return exampleDiagrams;
	}

	public ExampleDiagram getExampleDiagram(String shemaName) {
		if (shemaName == null) {
			return null;
		}
		for (ExampleDiagram s : getExampleDiagrams()) {
			if (shemaName.equals(s.getName())) {
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
	public Collection<ViewPointObject> getEmbeddedValidableObjects() {
		if (validableObjects == null) {
			validableObjects = new ChainedCollection<ViewPointObject>(getEditionPatterns(), getModelSlots(), getPalettes(),
					getExampleDiagrams());
		}
		return validableObjects;
	}

	@Override
	public final void finalizeDeserialization(Object builder) {
		if (builder instanceof ViewPointBuilder && ((ViewPointBuilder) builder).getModelVersion().isLesserThan(new FlexoVersion("1.0"))) {
			// There were no model slots before 1.0, please add them
			convertTo_1_0(((ViewPointBuilder) builder).getViewPointLibrary());
		}
		super.finalizeDeserialization(builder);
	}

	@Deprecated
	private void convertTo_1_0(ViewPointLibrary viewPointLibrary) {
		logger.info("Converting diagram specification from Openflexo 1.4.5 version");
		// For all "old" viewpoints, we consider a OWL model slot
		try {
			Class owlTechnologyAdapterClass = Class.forName("org.openflexo.technologyadapter.owl.OWLTechnologyAdapter");
			TechnologyAdapter<?, ?> OWL = viewPointLibrary.getFlexoServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(owlTechnologyAdapterClass);

			String importedOntology = null;
			for (File owlFile : getResource().getDirectory().listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return (name.endsWith(".owl"));
				}
			})) {
				if (owlFile.exists()) {
					importedOntology = ViewPoint.findOntologyImports(owlFile);
					owlFile.delete();
				}
			}

			FlexoMetaModelResource r = OWL.getMetaModelResource(importedOntology);
			if (r == null) {
				r = OWL.getMetaModelResource("http://www.agilebirds.com" + importedOntology);
			}
			if (r != null) {
				logger.info("************************ For ViewPoint " + getURI() + " declaring OWL model slot targetting meta-model "
						+ r.getURI());
			}

			ModelSlot<?, ?> ms = OWL.createNewModelSlot(this);
			ms.setName("owl");
			ms.setMetaModelResource(r);
			addToModelSlots(ms);
			DiagramTechnologyAdapter diagramTA = null;
			if (viewPointLibrary.getFlexoServiceManager() != null
					&& viewPointLibrary.getFlexoServiceManager().getService(TechnologyAdapterService.class) != null) {
				diagramTA = viewPointLibrary.getFlexoServiceManager().getService(TechnologyAdapterService.class)
						.getTechnologyAdapter(DiagramTechnologyAdapter.class);
			} else {
				diagramTA = new DiagramTechnologyAdapter();
			}
			DiagramModelSlot diagramMS = diagramTA.createNewModelSlot(this);
			diagramMS.setName("diagram");
			addToModelSlots(diagramMS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
