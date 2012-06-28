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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.fge.DataBinding;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.viewpoint.binding.EditionPatternBindingFactory;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShemaInserted;
import org.openflexo.foundation.viewpoint.dm.CalcDrawingShemaRemoved;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteRemoved;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public class ViewPoint extends ViewPointObject {

	private static final Logger logger = Logger.getLogger(ViewPoint.class.getPackage().getName());

	// TODO: We must find a better solution
	static {
		StringEncoder.getDefaultInstance()._addConverter(DataBinding.CONVERTER);
	}

	private String name;
	private String viewPointURI;
	private String description;
	private Vector<EditionPattern> editionPatterns;
	private LocalizedDictionary localizedDictionary;

	private ImportedOntology viewpointOntology;

	private Vector<ViewPointPalette> palettes;
	private Vector<ExampleDrawingShema> shemas;

	private File viewPointDirectory;
	private File owlFile;
	private File xmlFile;
	private ViewPointLibrary _library;
	private boolean isLoaded = false;
	private boolean isLoading = false;
	private RelativePathFileConverter relativePathFileConverter;

	public static ViewPoint openViewPoint(File viewpointDirectory, ViewPointLibrary library, ViewPointFolder folder) {

		String baseName = viewpointDirectory.getName().substring(0, viewpointDirectory.getName().length() - 10);
		File xmlFile = new File(viewpointDirectory, baseName + ".xml");

		if (xmlFile.exists()) {

			ImportedOntology viewPointOntology = readViewpointOntology(xmlFile, library);

			FileInputStream inputStream = null;
			try {
				ViewPointBuilder builder = new ViewPointBuilder(viewPointOntology);
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(viewpointDirectory);
				inputStream = new FileInputStream(xmlFile);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Reading file " + xmlFile.getAbsolutePath());
				}
				ViewPoint returned = (ViewPoint) XMLDecoder.decodeObjectWithMapping(inputStream, library.get_VIEW_POINT_MODEL(), builder,
						new StringEncoder(StringEncoder.getDefaultInstance(), relativePathFileConverter));
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("DONE reading file " + xmlFile.getAbsolutePath());
				}
				returned.init(baseName, viewpointDirectory, xmlFile, library, viewPointOntology, folder);
				return returned;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidXMLDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		} else {
			logger.severe("Not found: " + xmlFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}

	public static ViewPoint newViewPoint(String baseName, String viewpointURI, File owlFile, File viewpointDir, ViewPointLibrary library,
			ViewPointFolder folder) {
		File xmlFile = new File(viewpointDir, baseName + ".xml");
		ViewPoint viewpoint = new ViewPoint();
		viewpoint.owlFile = owlFile;
		viewpoint._setViewPointURI(viewpointURI);
		viewpoint.init(baseName, viewpointDir, xmlFile, library, null, folder);
		viewpoint.save();
		return viewpoint;
	}

	private static ImportedOntology readViewpointOntology(File viewPointFile, ViewPointLibrary library) {

		if (viewPointFile == null || !viewPointFile.exists() || viewPointFile.length() == 0) {
			if (viewPointFile.length() == 0) {
				viewPointFile.delete();
			}
			return null;
		}

		File owlFile = null;
		String viewPointURI = null;
		ImportedOntology viewpointOntology = null;

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
			logger.fine("Found " + owlFile);
			String ontologyURI = FlexoOntology.findOntologyURI(owlFile);
			if (StringUtils.isEmpty(ontologyURI)) {
				ontologyURI = viewPointURI;
			}
			viewpointOntology = library.getOntologyLibrary().importOntology(ontologyURI, owlFile);
			viewpointOntology.setIsReadOnly(false);
		}

		return viewpointOntology;
	}

	private static Document readXMLFile(File f) throws JDOMException, IOException {
		FileInputStream fio = new FileInputStream(f);
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(fio);
		return reply;
	}

	private static Element getElement(Document document, String name) {
		Iterator it = document.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

	private static Element getElement(Element from, String name) {
		Iterator it = from.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

	public static class ViewPointBuilder {
		private final ImportedOntology viewPointOntology;

		public ViewPointBuilder(ImportedOntology viewPointOntology) {
			this.viewPointOntology = viewPointOntology;
			// viewPointOntology.loadWhenUnloaded();
		}

		public ImportedOntology getViewPointOntology() {
			return viewPointOntology;
		}
	}

	// Used during deserialization, do not use it
	public ViewPoint(ViewPointBuilder builder) {
		super(builder);
		editionPatterns = new Vector<EditionPattern>();
	}

	// Used during deserialization, do not use it
	private ViewPoint() {
		super(null);
		editionPatterns = new Vector<EditionPattern>();
	}

	private void init(String baseName, File viewpointDir, File xmlFile, ViewPointLibrary library, ImportedOntology ontology,
			ViewPointFolder folder) {
		logger.info("Registering viewpoint " + baseName + " URI=" + getViewPointURI());

		name = baseName;
		viewPointDirectory = viewpointDir;
		_library = library;

		folder.addToViewPoints(this);

		relativePathFileConverter = new RelativePathFileConverter(viewPointDirectory);

		this.xmlFile = xmlFile;

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

		viewpointOntology = ontology;

		for (EditionPattern ep : getEditionPatterns()) {
			for (PatternRole pr : ep.getPatternRoles()) {
				if (pr instanceof ShapePatternRole) {
					((ShapePatternRole) pr).tryToFindAGR();
				}
			}
		}
	}

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	@Override
	public void saveToFile(File aFile) {
		super.saveToFile(aFile);
		clearIsModified(true);
	}

	public void save() {
		logger.info("Saving ViewPoint to " + xmlFile.getAbsolutePath() + "...");

		// Following was used to debug (display purpose only)
		/*Converter<File> previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
		StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
		try {
			System.out.println("File: " + XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), getStringEncoder()));
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateSerializationIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringEncoder.getDefaultInstance()._addConverter(previousConverter);
		 */

		File dir = xmlFile.getParentFile();
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
	}

	private void makeLocalCopy() throws IOException {
		if (xmlFile != null && xmlFile.exists()) {
			String localCopyName = xmlFile.getName() + "~";
			File localCopy = new File(xmlFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(xmlFile, localCopy);
		}
	}

	@Override
	public String getClassNameKey() {
		return "view_point";
	}

	private void findPalettes(File dir) {
		if (dir == null) {
			return;
		}
		if (palettes == null) {
			palettes = new Vector<ViewPointPalette>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".palette")) {
				ViewPointPalette palette = ViewPointPalette.instanciateCalcPalette(this, f);
				addToCalcPalettes(palette);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findPalettes(f);
			}
		}
	}

	private void findShemas(File dir) {
		if (dir == null) {
			return;
		}
		if (shemas == null) {
			shemas = new Vector<ExampleDrawingShema>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".shema")) {
				ExampleDrawingShema palette = ExampleDrawingShema.instanciateShema(this, f);
				addToCalcShemas(palette);
			} else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findShemas(f);
			}
		}
	}

	public void loadWhenUnloaded() {
		if (!isLoaded && !isLoading && viewpointOntology != null) {
			load();
		}
	}

	protected void load() {
		// logger.info("------------------------------------------------- "+calcURI);
		logger.info("Try to load ViewPoint " + viewPointURI);

		isLoading = true;

		logger.info("viewpointOntology=" + viewpointOntology);
		if (viewpointOntology != null) {
			logger.info(viewpointOntology.getURI() + " isLoaded=" + viewpointOntology.isLoaded() + " isLoading="
					+ viewpointOntology.isLoading());
			viewpointOntology.loadWhenUnloaded();
		}

		// Deprecated code
		/*if (getLocalizedDictionary() != null) {
			FlexoLocalization.addToLocalizedDelegates(getLocalizedDictionary());
		}*/

		if (viewpointOntology != null) {
			isLoaded = true;
		}
		isLoading = false;

		logger.info("Loaded ViewPoint " + viewPointURI);

		/*for (OntologyClass clazz : getOntologyLibrary().getAllClasses()) {
			System.out.println("Found: " + clazz);
		}*/

	}

	@Override
	public String getFullyQualifiedName() {
		return getURI();
	}

	public File getViewPointDirectory() {
		return viewPointDirectory;
	}

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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public FlexoOntology getViewpointOntology() {
		if (isDeserializing()) {
			return super.getViewpointOntology();
		}
		return viewpointOntology;
	}

	public void setViewpointOntology(ImportedOntology viewpointOntology) {
		this.viewpointOntology = viewpointOntology;
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return _library;
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VPM.ONTOLOGY_CALC_INSPECTOR;
	}

	public Vector<ViewPointPalette> getPalettes() {
		if (palettes == null) {
			findPalettes(viewPointDirectory);
		}
		return palettes;
	}

	public ViewPointPalette getPalette(String paletteName) {
		if (paletteName == null) {
			return null;
		}
		for (ViewPointPalette p : getPalettes()) {
			if (paletteName.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	public void addToCalcPalettes(ViewPointPalette aPalette) {
		palettes.add(aPalette);
		setChanged();
		notifyObservers(new CalcPaletteInserted(aPalette, this));
	}

	public void removeFromCalcPalettes(ViewPointPalette aPalette) {
		palettes.remove(aPalette);
		setChanged();
		notifyObservers(new CalcPaletteRemoved(aPalette, this));
	}

	public Vector<ExampleDrawingShema> getShemas() {
		if (shemas == null) {
			findShemas(viewPointDirectory);
		}
		return shemas;
	}

	public ExampleDrawingShema getShema(String shemaName) {
		if (shemaName == null) {
			return null;
		}
		for (ExampleDrawingShema s : getShemas()) {
			if (shemaName.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void addToCalcShemas(ExampleDrawingShema aShema) {
		shemas.add(aShema);
		setChanged();
		notifyObservers(new CalcDrawingShemaInserted(aShema, this));
	}

	public void removeFromCalcShemas(ExampleDrawingShema aShema) {
		shemas.remove(aShema);
		setChanged();
		notifyObservers(new CalcDrawingShemaRemoved(aShema, this));
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

	public Vector<EditionPattern> getAllRootEditionPatterns() {
		Vector<EditionPattern> returned = new Vector<EditionPattern>();
		for (EditionPattern ep : getEditionPatterns()) {
			if (ep.isRoot()) {
				returned.add(ep);
			}
		}
		return returned;
	}

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
		return getViewPointLibrary().get_VIEW_POINT_MODEL();
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

	public File getOwlFile() {
		return owlFile;
	}

	public void setOwlFile(File owlFile) {
		this.owlFile = owlFile;
	}

	@Override
	public final void finalizeDeserialization(Object builder) {
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

	private static EditionPatternBindingFactory EDITION_PATTERN_BINDING_FACTORY = new EditionPatternBindingFactory();

}
