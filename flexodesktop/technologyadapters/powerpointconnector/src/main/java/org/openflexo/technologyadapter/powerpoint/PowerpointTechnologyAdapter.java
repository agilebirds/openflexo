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
package org.openflexo.technologyadapter.powerpoint;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.technologyadapter.DeclareModelSlot;
import org.openflexo.foundation.technologyadapter.DeclareModelSlots;
import org.openflexo.foundation.technologyadapter.DeclareRepositoryType;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.powerpoint.rm.PowerpointSlideShowRepository;
import org.openflexo.technologyadapter.powerpoint.rm.PowerpointSlideshowResource;
import org.openflexo.technologyadapter.powerpoint.rm.PowerpointSlideshowResourceImpl;
import org.openflexo.technologyadapter.powerpoint.viewpoint.binding.PowerpointBindingFactory;

/**
 * This class defines and implements the Powerpoint technology adapter
 * 
 * @author sylvain, vincent, Christophe
 * 
 */
@DeclareModelSlots({ // ModelSlot(s) declaration
@DeclareModelSlot(FML = "BasicPowerpointModelSlot", modelSlotClass = BasicPowerpointModelSlot.class) // Pure spreadsheet interpretation
})
@DeclareRepositoryType({ PowerpointSlideShowRepository.class})
public class PowerpointTechnologyAdapter extends TechnologyAdapter {

	protected static final Logger logger = Logger.getLogger(PowerpointTechnologyAdapter.class.getPackage().getName());

	private static final PowerpointBindingFactory BINDING_FACTORY = new PowerpointBindingFactory();

	/**
	 * 
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public PowerpointTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Powerpoint technology adapter";
	}

	@Override
	public TechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new PowerpointTechnologyContextManager(this, service);
	}

	/**
	 * Creates and return a new {@link ModelSlot} of supplied class.<br>
	 * This responsability is delegated to the {@link TechnologyAdapter} which manages with introspection its own {@link ModelSlot} types
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass, VirtualModel virtualModel) {
		if (BasicPowerpointModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new BasicPowerpointModelSlot(virtualModel, this);
		} else{
			logger.warning("Unexpected model slot: " + modelSlotClass.getName());
		}
		
		return null;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	/**
	 * Initialize the supplied resource center with the technology<br>
	 * ResourceCenter is scanned, ResourceRepositories are created and new technology-specific resources are build and registered.
	 * 
	 * @param resourceCenter
	 */
	@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

		PowerpointSlideShowRepository ssRepository = resourceCenter.getRepository(PowerpointSlideShowRepository.class, this);
		if (ssRepository == null) {
			ssRepository = createSlideshowRepository(resourceCenter);
		}

		Iterator<I> it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				//System.out.println("searching " + item);
				File candidateFile = (File) item;
				PowerpointSlideshowResource ssRes = tryToLookupSlideshow(resourceCenter, candidateFile);
			}
		}

	}

	protected PowerpointSlideshowResource tryToLookupSlideshow(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		PowerpointTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		if (isValidSlideshowFile(candidateFile)) {
			PowerpointSlideshowResource ssRes = retrieveSlideshowResource(candidateFile);
			PowerpointSlideShowRepository ssRepository = resourceCenter.getRepository(PowerpointSlideShowRepository.class, this);
			if (ssRes != null) {
				RepositoryFolder<PowerpointSlideshowResource> folder;
				try {
					folder = ssRepository.getRepositoryFolder(candidateFile, true);
					ssRepository.registerResource(ssRes, folder);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// Also register the resource in the ResourceCenter seen as a ResourceRepository
				if (resourceCenter instanceof ResourceRepository) {
					try {
						((ResourceRepository) resourceCenter).registerResource(ssRes,
								((ResourceRepository<?>) resourceCenter).getRepositoryFolder(candidateFile, true));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return ssRes;
			}
		}
		return null;
	}

	/**
	 * Instantiate new workbook resource stored in supplied model file<br>
	 * *
	 */
	public PowerpointSlideshowResource retrieveSlideshowResource(File slideshowFile) {
		PowerpointSlideshowResource ssResource = null;

		// TODO: try to look-up already found file

		ssResource = PowerpointSlideshowResourceImpl.retrievePowerpointSlideshowResource(slideshowFile, getTechnologyContextManager());

		return ssResource;
	}

	/**
	 * Return flag indicating if supplied file appears as a valid slide show
	 * 
	 * @param candidateFile
	 * 
	 * @return
	 */
	public boolean isValidSlideshowFile(File candidateFile) {
		return candidateFile.getName().endsWith(".pptx") || candidateFile.getName().endsWith(".ppt");
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			File candidateFile = (File) contents;
			if (tryToLookupSlideshow(resourceCenter, candidateFile) != null) {
			}
		}
	}

	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub

	}

	@Override
	public PowerpointTechnologyContextManager getTechnologyContextManager() {
		return (PowerpointTechnologyContextManager) super.getTechnologyContextManager();
	}

	/**
	 * 
	 * Create a workbook repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 */
	public PowerpointSlideShowRepository createSlideshowRepository(FlexoResourceCenter<?> resourceCenter) {
		PowerpointSlideShowRepository returned = new PowerpointSlideShowRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, PowerpointSlideShowRepository.class, this);
		return returned;
	}
	
	
	/**
	 * Create empty model.
	 * 
	 * @param modelFile
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public PowerpointSlideshowResource createNewSlideshow(FlexoProject project, String pptFilename, String modelUri) {

		File pptFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), pptFilename);

		modelUri = pptFile.toURI().toString();

		PowerpointSlideshowResource slideshowResource = PowerpointSlideshowResourceImpl.makePowerpointSlideshowResource(modelUri, pptFile, getTechnologyContextManager());

		getTechnologyContextManager().registerResource(slideshowResource);

		return slideshowResource;
	}

}
