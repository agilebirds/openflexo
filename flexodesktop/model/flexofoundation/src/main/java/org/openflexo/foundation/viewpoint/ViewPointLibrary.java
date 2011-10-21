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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.viewpoint.EditionPattern.EditionPatternConverter;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;


public class ViewPointLibrary extends ViewPointLibraryObject {

	private static final Logger logger = Logger.getLogger(ViewPointLibrary.class.getPackage().getName());

	//public static final File CALC_LIBRARY_DIR = new FileResource("Calcs");

	private final OntologyLibrary ontologyLibrary;
	private final Vector<ViewPoint> calcs;
	private final Hashtable<String,ViewPoint> map;

	private FlexoResourceCenter resourceCenter;

	protected EditionPatternConverter editionPatternConverter;

	private final ViewPointFolder rootFolder;

	public ViewPointLibrary(FlexoResourceCenter resourceCenter, OntologyLibrary anOntologyLibrary)
	{
		super();
		
		editionPatternConverter = new EditionPatternConverter(resourceCenter);
		StringEncoder.getDefaultInstance()._addConverter(editionPatternConverter);
		
		this.resourceCenter = resourceCenter;
		ontologyLibrary = anOntologyLibrary;
		calcs = new Vector<ViewPoint>();
		map = new Hashtable<String,ViewPoint>();
		//findCalcs(CALC_LIBRARY_DIR);
		/*for (OntologyCalc calc : calcs.values()) {
			calc.loadWhenUnloaded();
		}*/
		
		rootFolder = new ViewPointFolder("root", null,this);

		StringEncoder.getDefaultInstance()._addConverter(ViewPointDataBinding.CONVERTER);
		StringEncoder.getDefaultInstance()._addConverter(anOntologyLibrary.getOntologyObjectConverter());

	}

	public FlexoResourceCenter getResourceCenter()
	{
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter resourceCenter)
	{
		this.resourceCenter = resourceCenter;
	}

	public ViewPoint getOntologyCalc(String ontologyCalcUri)
	{
		return map.get(ontologyCalcUri);
	}

	public Vector<ViewPoint> getViewPoints()
	{
		return calcs;
	}
	
	public ViewPoint importViewPoint(File calcDirectory, ViewPointFolder folder)
	{
		logger.info("Import view point "+calcDirectory.getAbsolutePath());
		ViewPoint calc = ViewPoint.openViewPoint(calcDirectory,this,folder);
		registerViewPoint(calc);
		return calc;
	}
	
	public ViewPoint registerViewPoint(ViewPoint vp)
	{
		String uri = vp.getViewPointURI();
		map.put(uri, vp);
		calcs.add(vp);
		setChanged();
		notifyObservers(new OntologyCalcCreated(vp));
		return vp;
	}
	
	@Override
	public OntologyLibrary getOntologyLibrary()
	{
		return ontologyLibrary;
	}

	private XMLMapping VIEW_POINT_MODEL;

	protected XMLMapping get_VIEW_POINT_MODEL()
	{
		if (VIEW_POINT_MODEL == null) {
			File viewPointModelFile = new FileResource("Models/ViewPointModel/ViewPointModel.xml");
			try {
				VIEW_POINT_MODEL = new XMLMapping(viewPointModelFile);
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
		return VIEW_POINT_MODEL;
	}
	
	private XMLMapping VIEW_POINT_PALETTE_MODEL;

	protected XMLMapping get_VIEW_POINT_PALETTE_MODEL()
	{
		if (VIEW_POINT_PALETTE_MODEL == null) {
			File calcPaletteModelFile = new FileResource("Models/ViewPointModel/ViewPointPaletteModel.xml");
			try {
				VIEW_POINT_PALETTE_MODEL = new XMLMapping(calcPaletteModelFile);
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
		return VIEW_POINT_PALETTE_MODEL;
	}
	
	private XMLMapping EXAMPLE_DRAWING_MODEL;

	protected XMLMapping get_EXAMPLE_DRAWING_MODEL()
	{
		if (EXAMPLE_DRAWING_MODEL == null) {
			File calcDrawingModelFile = new FileResource("Models/ViewPointModel/ExampleDrawingModel.xml");
			try {
				EXAMPLE_DRAWING_MODEL = new XMLMapping(calcDrawingModelFile);
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
		return EXAMPLE_DRAWING_MODEL;
	}
	
	@Override
	public ViewPointLibrary getViewPointLibrary()
	{
		return this;
	}

	@Override
	public String getInspectorName() 
	{
		return Inspectors.VPM.CALC_LIBRARY_INSPECTOR;
	}
	
	public ViewPointFolder getRootFolder() 
	{
		return rootFolder;
	}

	public EditionPattern getEditionPattern(String editionPatternURI) 
	{
		if (editionPatternURI.indexOf("#") > -1) {
			String viewPointURI = editionPatternURI.substring(0,editionPatternURI.indexOf("#"));
			ViewPoint vp = getOntologyCalc(viewPointURI);
			if (vp != null) {
				return vp.getEditionPattern(editionPatternURI.substring(editionPatternURI.indexOf("#")+1));
			}
		} 
		logger.warning("Cannot find edition pattern:"+editionPatternURI);
		return null;
	}
	

}
