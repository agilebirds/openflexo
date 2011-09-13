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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.calc.EditionPattern.EditionPatternConverter;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;


public class CalcLibrary extends CalcLibraryObject {

	private static final Logger logger = Logger.getLogger(CalcLibrary.class.getPackage().getName());

	//public static final File CALC_LIBRARY_DIR = new FileResource("Calcs");

	private final OntologyLibrary ontologyLibrary;
	private final Vector<OntologyCalc> calcs;
	private final Hashtable<String,OntologyCalc> map;

	private FlexoResourceCenter resourceCenter;

	protected EditionPatternConverter editionPatternConverter;

	private final CalcFolder rootFolder;

	public CalcLibrary(FlexoResourceCenter resourceCenter, OntologyLibrary anOntologyLibrary)
	{
		super();
		
		editionPatternConverter = new EditionPatternConverter(resourceCenter);
		StringEncoder.getDefaultInstance()._addConverter(editionPatternConverter);
		
		this.resourceCenter = resourceCenter;
		ontologyLibrary = anOntologyLibrary;
		calcs = new Vector<OntologyCalc>();
		map = new Hashtable<String,OntologyCalc>();
		//findCalcs(CALC_LIBRARY_DIR);
		/*for (OntologyCalc calc : calcs.values()) {
			calc.loadWhenUnloaded();
		}*/
		
		rootFolder = new CalcFolder("root", null,this);

	}

	public FlexoResourceCenter getResourceCenter()
	{
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter)
	{
		this.resourceCenter = resourceCenter;
	}

	public OntologyCalc getOntologyCalc(String ontologyCalcUri)
	{
		return map.get(ontologyCalcUri);
	}

	public Vector<OntologyCalc> getCalcs()
	{
		return calcs;
	}
	
	public OntologyCalc importCalc(File calcDirectory, CalcFolder folder)
	{
		logger.info("Import calc "+calcDirectory.getAbsolutePath());
		OntologyCalc calc = OntologyCalc.openOntologyCalc(calcDirectory,this,folder);
		registerCalc(calc);
		return calc;
	}
	
	public OntologyCalc registerCalc(OntologyCalc calc)
	{
		String uri = calc.getCalcURI();
		map.put(uri, calc);
		calcs.add(calc);
		setChanged();
		notifyObservers(new OntologyCalcCreated(calc));
		return calc;
	}
	
	@Override
	public OntologyLibrary getOntologyLibrary()
	{
		return ontologyLibrary;
	}

	private XMLMapping CALC_MODEL;

	protected XMLMapping get_CALC_MODEL()
	{
		if (CALC_MODEL == null) {
			File calcModelFile = new FileResource("Models/CalcModel/CalcModel.xml");
			try {
				CALC_MODEL = new XMLMapping(calcModelFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (IOException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (SAXException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return CALC_MODEL;
	}
	
	private XMLMapping CALC_PALETTE_MODEL;

	protected XMLMapping get_CALC_PALETTE_MODEL()
	{
		if (CALC_PALETTE_MODEL == null) {
			File calcPaletteModelFile = new FileResource("Models/CalcModel/CalcPaletteModel.xml");
			try {
				CALC_PALETTE_MODEL = new XMLMapping(calcPaletteModelFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (IOException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (SAXException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return CALC_PALETTE_MODEL;
	}
	
	private XMLMapping CALC_DRAWING_MODEL;

	protected XMLMapping get_CALC_DRAWING_MODEL()
	{
		if (CALC_DRAWING_MODEL == null) {
			File calcDrawingModelFile = new FileResource("Models/CalcModel/CalcDrawingModel.xml");
			try {
				CALC_DRAWING_MODEL = new XMLMapping(calcDrawingModelFile);
			} catch (InvalidModelException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (IOException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (SAXException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return CALC_DRAWING_MODEL;
	}
	
	@Override
	public CalcLibrary getCalcLibrary()
	{
		return this;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.CED.CALC_LIBRARY_INSPECTOR;
	}
	
	public CalcFolder getRootFolder() 
	{
		return rootFolder;
	}
	

}
