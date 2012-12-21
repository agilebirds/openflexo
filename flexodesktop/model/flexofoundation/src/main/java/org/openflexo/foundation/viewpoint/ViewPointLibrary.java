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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterAdded;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.viewpoint.EditionPattern.EditionPatternConverter;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;
import org.xml.sax.SAXException;

/**
 * The {@link ViewPointLibrary} manages all references to all {@link ViewPoint} known in a JVM instance.<br>
 * The {@link ViewPointLibrary} is a {@link FlexoService} working in conjunction with a {@link FlexoResourceCenterService}, with
 * synchronization performed through a {@link FlexoServiceManager} (generally this is the ApplicationContext)
 * 
 * @author sylvain
 * 
 */
public class ViewPointLibrary extends FlexoObject implements FlexoService, Validable {

	private static final Logger logger = Logger.getLogger(ViewPointLibrary.class.getPackage().getName());

	public static final ViewPointValidationModel VALIDATION_MODEL = new ViewPointValidationModel();

	private final Hashtable<String, ViewPointResource> map;

	protected EditionPatternConverter editionPatternConverter;

	private FlexoServiceManager serviceManager;

	private XMLMapping viewPointModel_0_1;
	private XMLMapping viewPointModel_1_0;

	public ViewPointLibrary() {
		super();

		editionPatternConverter = new EditionPatternConverter(this);
		StringEncoder.getDefaultInstance()._addConverter(editionPatternConverter);

		map = new Hashtable<String, ViewPointResource>();
		// findCalcs(CALC_LIBRARY_DIR);
		/*for (OntologyCalc calc : calcs.values()) {
			calc.loadWhenUnloaded();
		}*/

		StringEncoder.getDefaultInstance()._addConverter(ViewPointDataBinding.CONVERTER);
		// StringEncoder.getDefaultInstance()._addConverter(anOntologyLibrary.getOntologyObjectConverter());

	}

	@Override
	public String getFullyQualifiedName() {
		return "ViewPointLibrary";
	}

	/**
	 * Retrieve, and return ViewPointResource identified by supplied URI
	 * 
	 * @param viewpointURI
	 * @return
	 */
	public ViewPointResource getViewPointResource(String viewpointURI) {
		return map.get(viewpointURI);
	}

	/**
	 * Retrieve, load and return ViewPoint identified by supplied URI
	 * 
	 * @param viewpointURI
	 * @return
	 */
	public ViewPoint getViewPoint(String viewpointURI) {
		return map.get(viewpointURI).getViewPoint();
	}

	/**
	 * Return all viewpoints contained in this library<br>
	 * No consideration is performed on underlying organization structure
	 * 
	 * @return
	 */
	public Collection<ViewPointResource> getViewPoints() {
		return map.values();
	}

	/**
	 * Return all loaded viewpoint in the current library
	 */
	public Collection<ViewPoint> getLoadedViewPoints() {
		Vector<ViewPoint> returned = new Vector<ViewPoint>();
		for (ViewPointResource vpRes : getViewPoints()) {
			if (vpRes.isLoaded()) {
				returned.add(vpRes.getViewPoint());
			}
		}
		return returned;
	}

	/**
	 * Register supplied ViewPointResource in this library
	 * 
	 * @param vpRes
	 * @return
	 */
	public ViewPointResource registerViewPoint(ViewPointResource vpRes) {
		String uri = vpRes.getURI();
		map.put(uri, vpRes);
		setChanged();
		notifyObservers(new OntologyCalcCreated(vpRes));
		return vpRes;
	}

	protected XMLMapping getViewPointModel() {
		if (viewPointModel_1_0 == null) {
			File viewPointModelFile = new FileResource("Models/ViewPointModel/viewpoint_model_1.0.xml");
			try {
				viewPointModel_1_0 = new XMLMapping(viewPointModelFile);
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
		return viewPointModel_1_0;
	}

	private XMLMapping VIEW_POINT_PALETTE_MODEL;

	protected XMLMapping get_VIEW_POINT_PALETTE_MODEL() {
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

	protected XMLMapping get_EXAMPLE_DRAWING_MODEL() {
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

	public EditionPattern getEditionPattern(String editionPatternURI) {
		if (editionPatternURI.indexOf("#") > -1) {
			String viewPointURI = editionPatternURI.substring(0, editionPatternURI.indexOf("#"));
			ViewPoint vp = getViewPoint(viewPointURI);
			if (vp != null) {
				return vp.getEditionPattern(editionPatternURI.substring(editionPatternURI.indexOf("#") + 1));
			}
		}
		logger.warning("Cannot find edition pattern:" + editionPatternURI);
		return null;
	}

	public EditionScheme getEditionScheme(String editionSchemeURI) {
		if (editionSchemeURI.lastIndexOf(".") > -1) {
			String editionPatternURI = editionSchemeURI.substring(0, editionSchemeURI.lastIndexOf("."));
			EditionPattern ep = getEditionPattern(editionPatternURI);
			if (ep != null) {
				return ep.getEditionScheme(editionSchemeURI.substring(editionSchemeURI.lastIndexOf(".") + 1));
			}
		}
		logger.warning("Cannot find edition scheme:" + editionSchemeURI);
		return null;
	}

	@Override
	public Collection<ViewPoint> getEmbeddedValidableObjects() {
		return getLoadedViewPoints();
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		if (caller instanceof FlexoResourceCenterService) {
			if (notification instanceof ResourceCenterAdded) {
				FlexoResourceCenter newRC = ((ResourceCenterAdded) notification).getAddedResourceCenter();
				// A new resource center has just been referenced, initialize it related to viewpoint exploring
				newRC.initialize(this);
			}
		}
	}

	@Override
	public void register(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	@Override
	public FlexoServiceManager getFlexoServiceManager() {
		return serviceManager;
	}

	public FlexoResourceCenterService getResourceCenterService() {
		return getFlexoServiceManager().getService(FlexoResourceCenterService.class);
	}

	@Override
	public void initialize() {
		if (getResourceCenterService() != null) {
			// At initialization, initialize all already existing FlexoResourceCenter with this ViewPointLibrary
			for (FlexoResourceCenter rc : getResourceCenterService().getResourceCenters()) {
				rc.initialize(this);
			}
		}
	}

	@Override
	public ValidationModel getDefaultValidationModel() {
		return VALIDATION_MODEL;
	}

}
