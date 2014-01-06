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

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DefaultFlexoObject;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterAdded;
import org.openflexo.foundation.resource.DefaultResourceCenterService.ResourceCenterRemoved;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;

/**
 * The {@link ViewPointLibrary} manages all references to all {@link ViewPoint} known in a JVM instance.<br>
 * The {@link ViewPointLibrary} is a {@link FlexoService} working in conjunction with a {@link FlexoResourceCenterService}, with
 * synchronization performed through a {@link FlexoServiceManager} (generally this is the ApplicationContext)
 * 
 * @author sylvain
 * 
 */
public class ViewPointLibrary extends DefaultFlexoObject implements FlexoService, Validable {

	private static final Logger logger = Logger.getLogger(ViewPointLibrary.class.getPackage().getName());

	public static final ViewPointValidationModel VALIDATION_MODEL = new ViewPointValidationModel();

	private final Map<String, ViewPointResource> map;

	private FlexoServiceManager serviceManager;

	// private XMLMapping viewPointModel_0_1;
	// private XMLMapping viewPointModel_1_0;

	public ViewPointLibrary() {
		super();

		map = new Hashtable<String, ViewPointResource>();

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
		if (getViewPointResource(viewpointURI) != null) {
			return getViewPointResource(viewpointURI).getViewPoint();
		}
		return null;
	}

	/**
	 * Retrieve, load and return ViewPoint identified by supplied URI
	 * 
	 * @param viewpointURI
	 * @return
	 */
	public VirtualModel getVirtualModel(String virtualModelURI) {
		String viewPointURI = virtualModelURI.substring(0, virtualModelURI.lastIndexOf("/"));
		ViewPoint vp = getViewPoint(viewPointURI);
		if (vp != null) {
			return vp.getVirtualModelNamed(virtualModelURI.substring(virtualModelURI.lastIndexOf("/") + 1));
		}
		logger.warning("Cannot find virtual model:" + virtualModelURI + " (searched in viewpoint " + vp + ")");
		return null;
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

	/**
	 * Register supplied ViewPointResource in this library
	 * 
	 * @param vpRes
	 * @return
	 */
	public ViewPointResource unregisterViewPoint(ViewPointResource vpRes) {
		map.remove(vpRes);

		// Unregister the viewpoint resource from the viewpoint repository
		List<FlexoResourceCenter> resourceCenters = getResourceCenterService().getResourceCenters();
		for (FlexoResourceCenter rc : resourceCenters) {
			ViewPointRepository vpr = rc.getViewPointRepository();
			if ((vpr != null) && (vpr.getAllResources().contains(vpRes))) {
				vpr.unregisterResource(vpRes);
			}
		}
		setChanged();
		return vpRes;
	}

	/*protected XMLMapping getViewPointModel() {
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
	}*/

	// private XMLMapping VIEW_POINT_PALETTE_MODEL;

	/*protected XMLMapping get_VIEW_POINT_PALETTE_MODEL() {
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
	}*/

	// private XMLMapping EXAMPLE_DRAWING_MODEL;

	/*protected XMLMapping get_EXAMPLE_DRAWING_MODEL() {
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
	}*/

	public EditionPattern getEditionPattern(String editionPatternURI) {
		if (editionPatternURI.indexOf("#") > -1) {
			String virtualModelURI = editionPatternURI.substring(0, editionPatternURI.indexOf("#"));
			String editionPatternName = editionPatternURI.substring(editionPatternURI.indexOf("#") + 1);
			VirtualModel vm = getVirtualModel(virtualModelURI);
			if (vm != null) {
				return vm.getEditionPattern(editionPatternName);
			}
			logger.warning("Cannot find virtual model " + virtualModelURI + " while searching edition pattern:" + editionPatternURI + " ("
					+ editionPatternName + ")");
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
			if (notification instanceof ResourceCenterRemoved) {
				FlexoResourceCenter newRC = ((ResourceCenterRemoved) notification).getRemovedResourceCenter();
				// A new resource center has just been dereferenced
				// TODO implement this
				logger.warning("TODO: Please implement resource center dereferencing in ViewPointLibrary");
			}
		}
	}

	@Override
	public void register(FlexoServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	@Override
	public FlexoServiceManager getServiceManager() {
		return serviceManager;
	}

	public FlexoResourceCenterService getResourceCenterService() {
		return getServiceManager().getService(FlexoResourceCenterService.class);
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

	/*public void delete(ViewPoint viewPoint) {
		logger.info("Remove viewpoint " + viewPoint);
		unregisterViewPoint(viewPoint.getResource());
	}*/

}
