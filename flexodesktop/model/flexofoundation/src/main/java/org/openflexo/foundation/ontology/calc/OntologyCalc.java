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
package org.openflexo.foundation.ontology.calc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.jdom.JDOMException;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.calc.CalcPalette.RelativePathFileConverter;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShemaInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcDrawingShemaRemoved;
import org.openflexo.foundation.ontology.calc.dm.CalcPaletteInserted;
import org.openflexo.foundation.ontology.calc.dm.CalcPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class OntologyCalc extends CalcObject {

	private static final Logger logger = Logger.getLogger(OntologyCalc.class.getPackage().getName());

	private String name;
	private String calcURI;
	private String description;
	private Vector<EditionPattern> editionPatterns;
	private LocalizedDictionary localizedDictionary;
	
	private ImportedOntology calcOntology;
	
	private Vector<CalcPalette> palettes;
	private Vector<CalcDrawingShema> shemas;
	
	private File calcDirectory;
	private File owlFile;
	private File xmlFile;
	private CalcLibrary _library;
	private boolean isLoaded = false;
	private RelativePathFileConverter relativePathFileConverter;

	public static OntologyCalc openOntologyCalc(File calcDir, CalcLibrary library, CalcFolder folder) 
	{
		String baseName = calcDir.getName().substring(0,calcDir.getName().length()-5);
		File xmlFile = new File(calcDir,baseName+".xml");

		if (xmlFile.exists()) {
			Converter<File> previousConverter = null;
			FileInputStream inputStream = null;
			try {
				previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
				RelativePathFileConverter relativePathFileConverter = new RelativePathFileConverter(calcDir);
				StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
				inputStream = new FileInputStream(xmlFile);
				OntologyCalc returned = (OntologyCalc)XMLDecoder.decodeObjectWithMapping(inputStream, library.get_CALC_MODEL());
				returned.init(baseName,calcDir,xmlFile,library,folder);
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
			} finally {
				StringEncoder.getDefaultInstance()._addConverter(previousConverter);
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
		}
		
		else {
			logger.severe("Not found: "+xmlFile);
			// TODO: implement a search here (find the good XML file)
			return null;
		}
	}
	
	public static OntologyCalc newOntologyCalc(String baseName, String calcURI, File owlFile, File calcDir, CalcLibrary library, CalcFolder folder) 
	{
		File xmlFile = new File(calcDir,baseName+".xml");
		OntologyCalc calc = new OntologyCalc();
		calc.owlFile = owlFile;
		calc._setCalcURI(calcURI);
		calc.init(baseName,calcDir,xmlFile,library,folder);
		calc.save();
		return calc;
	}
	
	// Used during deserialization, do not use it
	public OntologyCalc() 
	{
		super();
		editionPatterns = new Vector<EditionPattern>();
	}
	

	private void init (String baseName, File calcDir, File xmlFile, CalcLibrary library, CalcFolder folder) 
	{
		logger.info("Registering calc "+baseName+ " URI="+getCalcURI());

		name = baseName;
		calcDirectory = calcDir;
		_library = library;
		
		folder.addToCalcs(this);
		
		relativePathFileConverter = new RelativePathFileConverter(calcDirectory);

		this.xmlFile = xmlFile;
		
		if (owlFile == null) {
			owlFile = new File(calcDir,baseName+".owl");
		}

		if (owlFile.exists()) {
			logger.info("Found "+owlFile);
			calcOntology = _library.getOntologyLibrary().importOntology(calcURI, owlFile);
			calcOntology.setIsReadOnly(false);
		}
		
		else {
			logger.warning("Could not find "+owlFile);
			return;
		}
		
		
		for (EditionPattern ep : getEditionPatterns()) {
			for (PatternRole pr : ep.getPatternRoles()) {
				if (pr instanceof ShapePatternRole) {
					((ShapePatternRole)pr).tryToFindAGR();
				}
			}
		}
	}
	
	@Override
	public void saveToFile(File aFile)
	{
		Converter<File> previousConverter = StringEncoder.getDefaultInstance()._converterForClass(File.class);
		StringEncoder.getDefaultInstance()._addConverter(relativePathFileConverter);
		super.saveToFile(aFile);
		StringEncoder.getDefaultInstance()._addConverter(previousConverter);
		clearIsModified(true);
	}
	
	public void save()
	{		
		logger.info("Saving calc to "+xmlFile.getAbsolutePath()+"...");
		
		File dir = xmlFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File temporaryFile = null;
		try {
			makeLocalCopy();
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			saveToFile(temporaryFile);
			temporaryFile.renameTo(xmlFile);	
			clearIsModified(true);
			logger.info("Saved calc to "+xmlFile.getAbsolutePath()+". Done.");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Could not save calc to "+xmlFile.getAbsolutePath());
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
		}
	}

	private void makeLocalCopy() throws IOException
	{
		if ((xmlFile != null) && (xmlFile.exists())) {
			String localCopyName = xmlFile.getName() + "~";
			File localCopy = new File(xmlFile.getParentFile(), localCopyName);
			FileUtils.copyFileToFile(xmlFile, localCopy);
		}
	}

	@Override
	public String getClassNameKey()
	{
		return "ontology_calc";
	}

	private void findPalettes(File dir)
	{
		if (dir == null) {
			return;
		}
		if (palettes == null) {
			palettes = new Vector<CalcPalette>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".palette")) {
				CalcPalette palette = CalcPalette.instanciateCalcPalette(this,f);
				addToCalcPalettes(palette);
			}
			else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findPalettes(f);
			}
		}
	}
	
	private void findShemas(File dir)
	{
		if (dir == null) {
			return;
		}
		if (shemas == null) {
			shemas = new Vector<CalcDrawingShema>();
		}
		for (File f : dir.listFiles()) {
			if (!f.isDirectory() && f.getName().endsWith(".shema")) {
				CalcDrawingShema palette = CalcDrawingShema.instanciateShema(this,f);
				addToCalcShemas(palette);
			}
			else if (f.isDirectory() && !f.getName().equals("CVS")) {
				findShemas(f);
			}
		}
	}
	
	public void loadWhenUnloaded()
	{
		if (!isLoaded) {
			load();
		}
	}
	
	protected void load()
	{
		//logger.info("------------------------------------------------- "+calcURI);
		logger.info("Try to load ViewPoint "+calcURI);

		//logger.info("calcOntology="+calcOntology.getURI());
		//logger.info(calcOntology.getURI()+" isLoaded="+calcOntology.isLoaded()+" isLoading="+calcOntology.isLoading());
		calcOntology.loadWhenUnloaded();
		
		if (getLocalizedDictionary() != null) {
			FlexoLocalization.addToLocalizedDelegates(getLocalizedDictionary());
		}
		
		isLoaded = true;
		
		/*logger.info("Loaded ViewPoint "+calcURI);
		for (OntologyClass clazz : getOntologyLibrary().getAllClasses()) {
			System.out.println("Found: "+clazz);
		}*/

	}

	
	public File getCalcDirectory() 
	{
		return calcDirectory;
	}

	public String getCalcURI() 
	{
		return calcURI;
	}

	public void _setCalcURI(String calcURI)
	{
		this.calcURI = calcURI;
	}

	@Override
	public String toString() 
	{
		return "OntologyCalc:"+getCalcURI();
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

	public FlexoOntology getCalcOntology() {
		return calcOntology;
	}

	public void setCalcOntology(ImportedOntology calcOntology)
	{
		this.calcOntology = calcOntology;
	}

	@Override
	public CalcLibrary getCalcLibrary() 
	{
		return _library;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.ONTOLOGY_CALC_INSPECTOR;
	}

	public Vector<CalcPalette> getPalettes() 
	{
		if (palettes == null) {
			findPalettes(calcDirectory);
		}
		return palettes;
	}

	public CalcPalette getPalette(String paletteName) 
	{
		if (paletteName == null) {
			return null;
		}
		for (CalcPalette p : getPalettes()) {
			if (paletteName.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}

	public void addToCalcPalettes(CalcPalette aPalette)
	{
		palettes.add(aPalette);		
		setChanged();
		notifyObservers(new CalcPaletteInserted(aPalette, this));
	}
	
	public void removeFromCalcPalettes(CalcPalette aPalette)
	{
		palettes.remove(aPalette);		
		setChanged();
		notifyObservers(new CalcPaletteRemoved(aPalette, this));
	}
	
	public Vector<CalcDrawingShema> getShemas() 
	{
		if (shemas == null) {
			findShemas(calcDirectory);
		}
		return shemas;
	}
	
	public CalcDrawingShema getShema(String shemaName) 
	{
		if (shemaName == null) {
			return null;
		}
		for (CalcDrawingShema s : getShemas()) {
			if (shemaName.equals(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public void addToCalcShemas(CalcDrawingShema aShema)
	{
		shemas.add(aShema);		
		setChanged();
		notifyObservers(new CalcDrawingShemaInserted(aShema, this));
	}
	
	public void removeFromCalcShemas(CalcDrawingShema aShema)
	{
		shemas.remove(aShema);		
		setChanged();
		notifyObservers(new CalcDrawingShemaRemoved(aShema, this));
	}
	

	@Override
	public OntologyCalc getCalc() 
	{
		return this;
	}

	protected void notifyEditionSchemeModified()
	{
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
	}
	
	private Vector<EditionPattern> _allEditionPatternWithDropScheme;
	private Vector<EditionPattern> _allEditionPatternWithLinkScheme;
	
	public Vector<EditionPattern> getAllEditionPatternWithDropScheme() 
	{
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

	public Vector<EditionPattern> getAllEditionPatternWithLinkScheme() 
	{
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

	public Vector<EditionPattern> getEditionPatterns() {
		return editionPatterns;
	}

	public void setEditionPatterns(Vector<EditionPattern> editionPatterns) {
		this.editionPatterns = editionPatterns;
	}

	public void addToEditionPatterns(EditionPattern pattern) 
	{
		pattern.setCalc(this);
		editionPatterns.add(pattern);
		_allEditionPatternWithDropScheme = null;
		_allEditionPatternWithLinkScheme = null;
		setChanged();
		notifyObservers(new EditionPatternCreated(pattern));
	}

	public void removeFromEditionPatterns(EditionPattern pattern)
	{
		pattern.setCalc(null);
		editionPatterns.remove(pattern);
		setChanged();
		notifyObservers(new EditionPatternDeleted(pattern));
	}

	public LocalizedDictionary getLocalizedDictionary()
	{
		return localizedDictionary;
	}

	public void setLocalizedDictionary(LocalizedDictionary localizedDictionary) 
	{
		localizedDictionary.setCalc(this);
		this.localizedDictionary = localizedDictionary;
	}


	@Override
	public XMLMapping getXMLMapping() 
	{
		return getCalcLibrary().get_CALC_MODEL();
	}

	public EditionPattern getEditionPattern(String editionPatternId) 
	{
		for (EditionPattern concept : editionPatterns) {
			if (concept.getName().equals(editionPatternId)) {
				return concept;
			}
		}
		logger.warning("Not found EditionPattern:"+editionPatternId);
		return null;
	}
	
	public Vector<LinkScheme> getAllConnectors() 
	{
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getCalc().getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				returned.add(s);
			}
		}
		return returned;
	}


	public Vector<LinkScheme> getConnectorsMatching(OntologyObject fromConcept, OntologyObject toConcept) 
	{
		Vector<LinkScheme> returned = new Vector<LinkScheme>();
		for (EditionPattern ep : getCalc().getEditionPatterns()) {
			for (LinkScheme s : ep.getLinkSchemes()) {
				if (s.isValidTarget(fromConcept, toConcept)) {
					returned.add(s);
				}
			}
		}
		return returned;
	}

	public File getOwlFile()
	{
		return owlFile;
	}

	public void setOwlFile(File owlFile) 
	{
		this.owlFile = owlFile;
	}

	public final void finalizeCalcDeserialization()
	{
		for (EditionPattern ep : getEditionPatterns()) {
			ep.finalizeEditionPatternDeserialization();
		}
	}


}
