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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.DataBinding;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.dm.OEDataModification;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.view.diagram.DiagramModelSlot;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.foundation.view.diagram.viewpoint.ShapePatternRole;
import org.openflexo.foundation.viewpoint.binding.EditionPatternBindingFactory;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteRemoved;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramRemoved;
import org.openflexo.foundation.viewpoint.dm.ModelSlotAdded;
import org.openflexo.foundation.viewpoint.dm.ModelSlotRemoved;
import org.openflexo.toolbox.ChainedCollection;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;

public class ViewPoint extends NamedViewPointObject implements XMLStorageResourceData<ViewPoint> {

	private static final Logger logger = Logger.getLogger(ViewPoint.class.getPackage().getName());

	// TODO: We must find a better solution
	static {
		StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);
	}

	private String viewPointURI;
	private String version;

	private Vector<EditionPattern> editionPatterns;
	private LocalizedDictionary localizedDictionary;

	private List<DiagramPalette> palettes;
	private List<ExampleDiagram> exampleDiagrams;

	// private File viewPointDirectory;
	// private File owlFile;
	// private File xmlFile;
	private ViewPointLibrary _library;
	// private boolean isLoaded = false;
	// private boolean isLoading = false;
	// private RelativePathFileConverter relativePathFileConverter;

	private List<ModelSlot<?, ?>> modelSlots;

	private ViewPointResource resource;

	/**
	 * Stores a chained collections of objects which are involved in validation
	 */
	private ChainedCollection<ViewPointObject> validableObjects = null;

	/*public static ViewPoint openViewPoint(File viewpointDirectory, ViewPointLibrary library, FlexoVersion modelVersion) throws Exception {

		String baseName = viewpointDirectory.getName().substring(0, viewpointDirectory.getName().length() - 10);
		File xmlFile = new File(viewpointDirectory, baseName + ".xml");

		if (xmlFile.exists()) {

			// ImportedOntology viewPointOntology = readViewpointOntology(xmlFile, library);

			FileInputStream inputStream = null;
			try {
				ViewPointBuilder builder = new ViewPointBuilder(library, modelVersion);
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(viewpointDirectory);
				inputStream = new FileInputStream(xmlFile);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Reading file " + xmlFile.getAbsolutePath());
				}
				ViewPoint returned = (ViewPoint) XMLDecoder.decodeObjectWithMapping(inputStream, library.getViewPointModel(), builder,
						new StringEncoder(StringEncoder.getDefaultInstance(), relativePathFileConverter));
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("DONE reading file " + xmlFile.getAbsolutePath());
				}
				returned.init(baseName, viewpointDirectory, xmlFile, library);
				return returned;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw e;
			} catch (InvalidXMLDataException e) {
				e.printStackTrace();
				throw e;
			} catch (InvalidObjectSpecificationException e) {
				e.printStackTrace();
				throw e;
			} catch (AccessorInvocationException e) {
				e.printStackTrace();
				throw e;
			} catch (InvalidModelException e) {
				e.printStackTrace();
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} catch (JDOMException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		} else {
			logger.severe("Not found: " + xmlFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}*/

	public static ViewPoint newViewPoint(String baseName, String viewpointURI/*, File owlFile*/, File viewpointDir,
			ViewPointLibrary library/*, ViewPointFolder folder*/) {
		File xmlFile = new File(viewpointDir, baseName + ".xml");
		ViewPoint viewpoint = new ViewPoint();
		// viewpoint.owlFile = owlFile;
		viewpoint._setViewPointURI(viewpointURI);

		// ImportedOntology viewPointOntology = loadViewpointOntology(viewpointURI, owlFile, library);

		viewpoint.init(baseName, /*viewpointDir, xmlFile,*/library/*, folder*/);
		viewpoint.loadViewpointMetaModels();
		viewpoint.save();
		return viewpoint;
	}

	/*private static ImportedOntology readViewpointOntology(File viewPointFile, ViewPointLibrary library) {

		if (viewPointFile == null || !viewPointFile.exists() || viewPointFile.length() == 0) {
			if (viewPointFile.length() == 0) {
				viewPointFile.delete();
			}
			return null;
		}

		File owlFile = null;
		String viewPointURI = null;

		Document document;
		try {
			logger.fine("Try to find ontology for viewpoint" + viewPointFile);
			document = readXMLFile(viewPointFile);
			Element root = getElement(document, "ViewPoint");
			owlFile = new File(viewPointFile.getParent(), root.getAttributeValue("owlFile"));
			viewPointURI = root.getAttributeValue("uri");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (owlFile.exists()) {
			return loadViewpointOntology(viewPointURI, owlFile, library);
		}

		return null;
	}*/

	private void loadViewpointMetaModels() {
		logger.warning("loadViewpointMetaModels() : not implemented yet");
	}

	public FlexoVersion getModelVersion() {
		return getResource().getModelVersion();
	}

	public void setModelVersion(FlexoVersion aVersion) {
	}

	/*private static ImportedOntology loadViewpointOntology(String viewPointURI, File owlFile, ViewPointLibrary library) {

		ImportedOntology viewpointOntology = null;

		if (owlFile.exists()) {
			logger.fine("Found " + owlFile);
			String ontologyURI = OWLOntology.findOntologyURI(owlFile);
			if (StringUtils.isEmpty(ontologyURI)) {
				ontologyURI = viewPointURI;
			}
			viewpointOntology = library.getOntologyLibrary().importMetaModel(ontologyURI, owlFile);
			viewpointOntology.setIsReadOnly(false);
		}

		return viewpointOntology;
	}*/

	public static class ViewPointBuilder {
		private ViewPoint viewPoint;
		private FlexoVersion modelVersion;
		private ViewPointLibrary viewPointLibrary;

		public ViewPointBuilder(ViewPointLibrary vpLibrary) {
			this.viewPointLibrary = vpLibrary;
		}

		public ViewPointBuilder(ViewPointLibrary vpLibrary, ViewPoint viewPoint) {
			this.viewPoint = viewPoint;
			this.viewPointLibrary = vpLibrary;
		}

		public ViewPointBuilder(ViewPointLibrary vpLibrary, FlexoVersion modelVersion) {
			this.modelVersion = modelVersion;
			this.viewPointLibrary = vpLibrary;
		}

		public ViewPointLibrary getViewPointLibrary() {
			return viewPointLibrary;
		}

		public FlexoVersion getModelVersion() {
			return modelVersion;
		}

		public ViewPoint getViewPoint() {
			return viewPoint;
		}

		public void setViewPoint(ViewPoint viewPoint) {
			this.viewPoint = viewPoint;
		}
	}

	// Used during deserialization, do not use it
	public ViewPoint(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			builder.setViewPoint(this);
		}
		editionPatterns = new Vector<EditionPattern>();
		modelSlots = new ArrayList<ModelSlot<?, ?>>();
		exampleDiagrams = new ArrayList<ExampleDiagram>();
		palettes = new ArrayList<DiagramPalette>();
	}

	// Used during deserialization, do not use it
	private ViewPoint() {
		this(null);
	}

	public void init(String baseName, /* File viewpointDir, File xmlFile,*/ViewPointLibrary library/*, ViewPointFolder folder*/) {
		logger.info("Registering viewpoint " + baseName + " URI=" + getViewPointURI());

		setName(baseName);
		// viewPointDirectory = viewpointDir;
		_library = library;

		// relativePathFileConverter = new RelativePathFileConverter(viewPointDirectory);

		// this.xmlFile = xmlFile;

		/*if (owlFile == null) {
			owlFile = new File(viewpointDir, baseName + ".owl");
		}

		if (owlFile.exists()) {
			logger.fine("Found " + owlFile);
			viewpointOntology = _library.getOntologyLibrary().importOntology(viewPointURI, owlFile);
			viewpointOntology.setIsReadOnly(false);
		}

		else {
			logger.warning("Could not find " + owlFile);
			return;
		}*/

		loadViewpointMetaModels();

		// viewpointOntology = ontology;

		for (EditionPattern ep : getEditionPatterns()) {
			for (PatternRole pr : ep.getPatternRoles()) {
				if (pr instanceof ShapePatternRole) {
					((ShapePatternRole) pr).tryToFindAGR();
				}
			}
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getURI();
	}

	private StringEncoder encoder;

	/*@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}*/

	/*private void findPalettes(File dir) {
		if (dir == null) {
			return;
		}
		if (palettes == null) {
			palettes = new Vector<DiagramPalette>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".palette")) {
				DiagramPalette palette = DiagramPalette.instanciateDiagramPalette(this, f);
				addToPalettes(palette);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findPalettes(f);
			}
		}
	}

	private void findShemas(File dir) {
		if (dir == null) {
			return;
		}
		if (exampleDiagrams == null) {
			exampleDiagrams = new Vector<ExampleDiagram>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".shema")) {
				ExampleDiagram palette = ExampleDiagram.instanciateExampleDiagram(this, f);
				addToExampleDiagrams(palette);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findShemas(f);
			}
		}
	}*/

	/*public void loadWhenUnloaded() {
		if (!isLoaded && !isLoading) {
			load();
		}
	}*/

	/*protected void load() {
		// logger.info("------------------------------------------------- "+calcURI);
		logger.fine("Try to load ViewPoint " + viewPointURI);

		isLoading = true;

		isLoading = false;

		logger.info("Loaded ViewPoint " + viewPointURI);

		//for (IFlexoOntologyClass clazz : getOntologyLibrary().getAllClasses()) {
		//	System.out.println("Found: " + clazz);
		//}

	}*/

	/*@Override
	public String getFullyQualifiedName() {
		return getURI();
	}*/

	/*public File getViewPointDirectory() {
		return viewPointDirectory;
	}*/

	@Override
	public String getURI() {
		return getViewPointURI();
	}

	public String getViewPointURI() {
		return viewPointURI;
	}

	public void _setViewPointURI(String vpURI) {
		if (vpURI != null) {
			// We prevent ',' so that we can use it as a delimiter in tags.
			vpURI = vpURI.replace(",", "");
		}
		this.viewPointURI = vpURI;
	}

	@Override
	public String toString() {
		return "ViewPoint:" + getViewPointURI();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String aVersion) {
		if (requireChange(version, aVersion)) {
			String old = this.version;
			this.version = aVersion;
			setChanged();
			notifyObservers(new OEDataModification("version", old, version));
		}
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return _library;
	}

	public List<DiagramPalette> getPalettes() {
		/*if (palettes == null) {
			findPalettes(viewPointDirectory);
		}*/
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
		/*if (exampleDiagrams == null) {
			findShemas(viewPointDirectory);
		}*/
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
	public ViewPoint getViewPoint() {
		return this;
	}

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

	/**
	 * Return all {@link EditionPattern} defined in this {@link ViewPoint} which have no parent
	 * 
	 * @return
	 */
	public Vector<EditionPattern> getAllRootEditionPatterns() {
		Vector<EditionPattern> returned = new Vector<EditionPattern>();
		for (EditionPattern ep : getEditionPatterns()) {
			if (ep.isRoot()) {
				returned.add(ep);
			}
		}
		return returned;
	}

	/**
	 * Return all {@link EditionPattern} defined in this {@link ViewPoint}
	 * 
	 * @return
	 */
	public Vector<EditionPattern> getEditionPatterns() {
		return editionPatterns;
	}

	public void setEditionPatterns(Vector<EditionPattern> editionPatterns) {
		this.editionPatterns = editionPatterns;
	}

	public void addToEditionPatterns(EditionPattern pattern) {
		pattern.setCalc(this);
		editionPatterns.add(pattern);
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
		setChanged();
		notifyObservers(new EditionPatternCreated(pattern));
	}

	public void removeFromEditionPatterns(EditionPattern pattern) {
		pattern.setCalc(null);
		editionPatterns.remove(pattern);
		setChanged();
		notifyObservers(new EditionPatternDeleted(pattern));
	}

	@Override
	public LocalizedDictionary getLocalizedDictionary() {
		if (localizedDictionary == null) {
			localizedDictionary = new LocalizedDictionary(null);
			localizedDictionary.setCalc(this);
		}
		return localizedDictionary;
	}

	public void setLocalizedDictionary(LocalizedDictionary localizedDictionary) {
		localizedDictionary.setCalc(this);
		this.localizedDictionary = localizedDictionary;
	}

	@Override
	public XMLMapping getXMLMapping() {
		return getViewPointLibrary().getViewPointModel();
	}

	public EditionPattern getEditionPattern(String editionPatternId) {
		for (EditionPattern concept : editionPatterns) {
			if (concept.getName().equals(editionPatternId)) {
				return concept;
			}
		}
		logger.warning("Not found EditionPattern:" + editionPatternId);
		return null;
	}

	public Vector<LinkScheme> getAllConnectors() {
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getViewPoint().getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				returned.add(s);
			}
		}
		return returned;
	}

	public Vector<LinkScheme> getConnectorsMatching(EditionPattern fromConcept, EditionPattern toConcept) {
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getViewPoint().getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				if (s.isValidTarget(fromConcept, toConcept)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	/*public File getOwlFile() {
		return owlFile;
	}

	public void setOwlFile(File owlFile) {
		this.owlFile = owlFile;
	}*/

	@Deprecated
	private void convertTo_1_0(ViewPointLibrary viewPointLibrary) {
		logger.info("Converting viewpoint from Openflexo 1.4.5 version");
		// For all "old" viewpoints, we consider a OWL model slot
		try {
			Class owlTechnologyAdapterClass = Class.forName("org.openflexo.technologyadapter.owl.OWLTechnologyAdapter");
			TechnologyAdapter<?, ?> OWL = viewPointLibrary.getFlexoServiceManager().getTechnologyAdapterService()
					.getTechnologyAdapter(owlTechnologyAdapterClass);
			ModelSlot<?, ?> ms = OWL.createNewModelSlot(this);
			ms.setName("owl");
			ms.setMetaModelResource(null);
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

	@Override
	public final void finalizeDeserialization(Object builder) {
		if (builder instanceof ViewPointBuilder && ((ViewPointBuilder) builder).getModelVersion().isLesserThan(new FlexoVersion("1.0"))) {
			// There were no model slots before 1.0, please add them
			convertTo_1_0(((ViewPointBuilder) builder).getViewPointLibrary());
		}
		for (EditionPattern ep : getEditionPatterns()) {
			ep.finalizeEditionPatternDeserialization();
		}
		super.finalizeDeserialization(builder);
	}

	private BindingModel _bindingModel;

	@Override
	public BindingModel getBindingModel() {
		if (_bindingModel == null) {
			createBindingModel();
		}
		return _bindingModel;
	}

	public void updateBindingModel() {
		logger.fine("updateBindingModel()");
		_bindingModel = null;
		createBindingModel();
	}

	private void createBindingModel() {
		_bindingModel = new BindingModel();
		for (EditionPattern ep : getEditionPatterns()) {
			_bindingModel.addToBindingVariables(new EditionPatternPathElement<ViewPoint>(ep, this));
		}
	}

	@Override
	public BindingFactory getBindingFactory() {
		return EDITION_PATTERN_BINDING_FACTORY;
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return ViewPointLibrary.VALIDATION_MODEL;
	}

	private static EditionPatternBindingFactory EDITION_PATTERN_BINDING_FACTORY = new EditionPatternBindingFactory();

	// ==========================================================================
	// ============================== Model Slots ===============================
	// ==========================================================================

	public void setModelSlots(List<ModelSlot<?, ?>> modelSlots) {
		this.modelSlots = modelSlots;
	}

	public List<ModelSlot<?, ?>> getModelSlots() {
		return modelSlots;
	}

	public void addToModelSlots(ModelSlot<?, ?> modelSlot) {
		modelSlots.add(modelSlot);
		setChanged();
		notifyObservers(new ModelSlotAdded(modelSlot, this));
	}

	public void removeFromModelSlots(ModelSlot<?, ?> modelSlot) {
		modelSlots.remove(modelSlot);
		setChanged();
		notifyObservers(new ModelSlotRemoved(modelSlot, this));
	}

	public <MS extends ModelSlot<?, ?>> List<MS> getModelSlots(Class<MS> msType) {
		List<MS> returned = new ArrayList<MS>();
		for (ModelSlot<?, ?> ms : getModelSlots()) {
			if (TypeUtils.isTypeAssignableFrom(msType, ms.getClass())) {
				returned.add((MS) ms);
			}
		}
		return returned;
	}

	public ModelSlot<?, ?> getModelSlot(String modelSlotName) {
		for (ModelSlot<?, ?> ms : getModelSlots()) {
			if (ms.getName().equals(modelSlotName)) {
				return ms;
			}
		}
		return null;
	}

	public List<ModelSlot<?, ?>> getRequiredModelSlots() {
		List<ModelSlot<?, ?>> requiredModelSlots = new ArrayList<ModelSlot<?, ?>>();
		for (ModelSlot<?, ?> modelSlot : getModelSlots()) {
			if (modelSlot.getIsRequired()) {
				requiredModelSlots.add(modelSlot);
			}
		}
		return modelSlots;
	}

	@Override
	public String getLanguageRepresentation() {
		// Voir du cote de GeneratorFormatter pour formatter tout ca
		StringBuffer sb = new StringBuffer();
		/*System.out.println("loaded: " + getViewpointOntology().isLoaded());
		for (IFlexoOntology o : getViewpointOntology().getImportedOntologies()) {
			if (o != getOntologyLibrary().getOWLOntology()) {
				String modelName = JavaUtils.getVariableName(o.getName());
				sb.append("import " + modelName + " as " + o.getURI() + ";" + StringUtils.LINE_SEPARATOR);
			}
		}*/
		sb.append("ViewDefinition " + getName() + " uri=\"" + getURI() + "\"");
		sb.append(" {" + StringUtils.LINE_SEPARATOR);
		// TODO iterate on slots here
		sb.append("ModelSlot defaultModelSlot implements toto;");
		sb.append(StringUtils.LINE_SEPARATOR);
		for (EditionPattern ep : getEditionPatterns()) {
			sb.append(ep.getLanguageRepresentation());
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		sb.append("}" + StringUtils.LINE_SEPARATOR);
		return sb.toString();
	}

	/**
	 * Retrieve object referenced by its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public Object getObject(String uri) {
		for (FlexoMetaModel<?> mm : getAllReferencedMetaModels()) {
			if (mm != null) {
				Object o = mm.getObject(uri);
				if (o != null)
					return o;
			}
		}
		return null;
	}

	/**
	 * Retrieve ontology object from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyObject getOntologyObject(String uri) {
		Object returned = getObject(uri);
		if (returned instanceof IFlexoOntologyObject) {
			return (IFlexoOntologyObject) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology class from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyClass getOntologyClass(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyClass) {
			return (IFlexoOntologyClass) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology individual from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyIndividual getOntologyIndividual(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyIndividual) {
			return (IFlexoOntologyIndividual) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyStructuralProperty getOntologyProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyStructuralProperty) {
			return (IFlexoOntologyStructuralProperty) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology object property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyObjectProperty getOntologyObjectProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyObjectProperty) {
			return (IFlexoOntologyObjectProperty) returned;
		}
		return null;
	}

	/**
	 * Retrieve ontology object property from its URI.<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param uri
	 * @return
	 */
	public IFlexoOntologyDataProperty getOntologyDataProperty(String uri) {
		Object returned = getOntologyObject(uri);
		if (returned instanceof IFlexoOntologyDataProperty) {
			return (IFlexoOntologyDataProperty) returned;
		}
		return null;
	}

	/**
	 * Return true if URI is well formed and valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		if (StringUtils.isEmpty(conceptURI)) {
			return false;
		}
		if (StringUtils.isEmpty(conceptURI.trim())) {
			return false;
		}
		return conceptURI.equals(ToolBox.getJavaName(conceptURI, true, false)) && !isDuplicatedURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is duplicated in the context of this project
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String modelURI, String conceptURI) {
		FlexoMetaModel<?> m = getMetaModel(modelURI);
		if (m != null) {
			return m.getObject(modelURI + "#" + conceptURI) != null;
		}
		return false;
	}

	/**
	 * Retrieve metamodel referenced by its URI<br>
	 * Note that search is performed in the scope of current project only
	 * 
	 * @param modelURI
	 * @return
	 */
	public FlexoMetaModel<?> getMetaModel(String metaModelURI) {
		for (FlexoMetaModel<?> m : getAllReferencedMetaModels()) {
			if (m.getURI().equals(metaModelURI))
				return m;
		}
		return null;
	}

	/**
	 * Return the list of all models used in the scope of current project<br>
	 * To compute this this, iterate on each View, then each ModelSlotInstance
	 * 
	 * @return
	 */
	public Set<FlexoMetaModel<?>> getAllReferencedMetaModels() {
		HashSet<FlexoMetaModel<?>> returned = new HashSet<FlexoMetaModel<?>>();
		for (ModelSlot<?, ?> modelSlot : getModelSlots()) {
			if (modelSlot.getMetaModelResource() != null) {
				returned.add(modelSlot.getMetaModelResource().getMetaModelData());
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

	// Implementation of XMLStorageResourceData

	@Override
	public FlexoStorageResource<ViewPoint> getFlexoResource() {
		return (FlexoStorageResource<ViewPoint>) getResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		setResource((ViewPointResource) resource);
	}

	@Override
	public ViewPointResource getResource() {
		return resource;
	}

	@Override
	public void setResource(org.openflexo.foundation.resource.FlexoResource<ViewPoint> resource) {
		this.resource = (ViewPointResource) resource;
	}

	@Override
	public ViewPointResource getFlexoXMLFileResource() {
		return getResource();
	}

	/*@Override
	public void saveToFile(File aFile) {
		FileOutputStream out = null;
		try {
			if (!aFile.getParentFile().exists()) {
				aFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(aFile);
			XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), out, getStringEncoder());
			out.flush();
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		clearIsModified(true);
	}*/

	@Override
	public void save() {
		logger.info("Saving ViewPoint to " + getResource().getFile().getAbsolutePath() + "...");

		/*File dir = xmlFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temporaryFile = null;
		try {
			makeLocalCopy();
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			saveToFile(temporaryFile);
			FileUtils.rename(temporaryFile, xmlFile);
			clearIsModified(true);
			logger.info("Saved ViewPoint to " + xmlFile.getAbsolutePath() + ". Done.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not save ViewPoint to " + xmlFile.getAbsolutePath());
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
		}
		clearIsModified(false);*/
		try {
			getResource().save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

	/*private void makeLocalCopy() throws IOException {
		if (xmlFile != null && xmlFile.exists()) {
			String localCopyName = xmlFile.getName() + "~";
			File localCopy = new File(xmlFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(xmlFile, localCopy);
		}
	}*/

}
