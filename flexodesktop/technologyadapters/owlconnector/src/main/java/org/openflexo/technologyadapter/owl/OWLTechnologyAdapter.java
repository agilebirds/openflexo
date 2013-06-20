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
package org.openflexo.technologyadapter.owl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.DeclareModelSlot;
import org.openflexo.foundation.technologyadapter.DeclareModelSlots;
import org.openflexo.foundation.technologyadapter.DeclareRepositoryType;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntology.OntologyNotFoundException;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.technologyadapter.owl.model.OWLOntologyRepository;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResourceImpl;
import org.openflexo.technologyadapter.owl.viewpoint.binding.OWLBindingFactory;

/**
 * This class defines and implements the OWL technology adapter
 * 
 * @author sylvain, luka
 * 
 */
@DeclareModelSlots({ // ModelSlot(s) declaration
@DeclareModelSlot(FML = "OWLModelSlot", modelSlotClass = OWLModelSlot.class) // Classical type-safe interpretation
})
@DeclareRepositoryType({ OWLOntologyRepository.class })
public class OWLTechnologyAdapter extends TechnologyAdapter {

	private static final Logger logger = Logger.getLogger(OWLTechnologyAdapter.class.getPackage().getName());

	private static final OWLBindingFactory BINDING_FACTORY = new OWLBindingFactory();

	public OWLTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "OWL technology adapter";
	}

	/**
	 * Creates and return a new {@link ModelSlot} of supplied class.<br>
	 * This responsability is delegated to the {@link TechnologyAdapter} which manages with introspection its own {@link ModelSlot} types
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass, VirtualModel<?> virtualModel) {
		if (OWLModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new OWLModelSlot(virtualModel, this);
		}
		logger.warning("Unexpected model slot: " + modelSlotClass.getName());
		return null;
	}

	/**
	 * Return the {@link TechnologyContextManager} for this technology shared by all {@link FlexoResourceCenter} declared in the scope of
	 * {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	@Override
	public OWLOntologyLibrary getTechnologyContextManager() {
		return (OWLOntologyLibrary) super.getTechnologyContextManager();
	}

	public OWLOntologyLibrary getOntologyLibrary() {
		return getTechnologyContextManager();
	}

	/**
	 * Initialize the supplied resource center with the technology<br>
	 * ResourceCenter is scanned, ResourceRepositories are created and new technology-specific resources are build and registered.
	 * 
	 * @param resourceCenter
	 */
	@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

		OWLOntologyLibrary ontologyLibrary = getOntologyLibrary();

		OWLOntologyRepository ontRepository = resourceCenter.getRepository(OWLOntologyRepository.class, this);
		if (ontRepository == null) {
			ontRepository = createOntologyRepository(resourceCenter);
		}

		Iterator<I> it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				OWLOntologyResource ontRes = tryToLookupOntology(resourceCenter, candidateFile);
			}
		}
	}

	protected OWLOntologyResource tryToLookupOntology(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		if (isValidOntologyFile(candidateFile)) {
			OWLOntologyResource ontRes = retrieveOntologyResource(candidateFile);
			OWLOntologyRepository ontRepository = resourceCenter.getRepository(OWLOntologyRepository.class, this);
			if (ontRes != null) {
				RepositoryFolder<OWLOntologyResource> folder;
				try {
					folder = ontRepository.getRepositoryFolder(candidateFile, true);
					ontRepository.registerResource(ontRes, folder);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// Also register the resource in the ResourceCenter seen as a ResourceRepository
				if (resourceCenter instanceof ResourceRepository) {
					try {
						((ResourceRepository) resourceCenter).registerResource(ontRes,
								((ResourceRepository<?>) resourceCenter).getRepositoryFolder(candidateFile, true));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return ontRes;
			}
		}
		return null;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	/**
	 * Return flag indicating if supplied file represents a valid OWL ontology
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public boolean isValidOntologyFile(File aMetaModelFile) {
		// TODO: also check that file is valid
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".owl");
	}

	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			File candidateFile = (File) contents;
			if (tryToLookupOntology(resourceCenter, candidateFile) != null) {
				// This is a meta-model, this one has just been registered
			}
		}
	}

	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			System.out
					.println("File DELETED " + ((File) contents).getName() + " in " + ((File) contents).getParentFile().getAbsolutePath());
		}
	}

	public OWLOntologyResource retrieveOntologyResource(File owlFile) {

		// logger.info("Retrieving OWL MetaModelResource for " + aMetaModelFile.getAbsolutePath());

		OWLOntologyResource ontologyResource = OWLOntologyResourceImpl.retrieveOWLOntologyResource(owlFile, getOntologyLibrary());
		logger.info("Found OWL ontology " + ontologyResource.getURI() + " file:" + owlFile.getAbsolutePath());
		return ontologyResource;

	}

	public OWLOntologyResource createNewOntology(FlexoProject project, String filename, String modelUri,
			FlexoResource<OWLOntology> metaModel) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewOWLModel(), project=" + project);
		}
		logger.info("-------------> Create ontology for " + project.getProjectName());

		File owlFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		OWLOntologyResource returned = OWLOntologyResourceImpl.makeOWLOntologyResource(modelUri, owlFile, getOntologyLibrary());
		OWLOntology ontology = returned.getModel();
		if (metaModel != null) {
			try {
				ontology.importOntology(metaModel.getResourceData(null));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OntologyNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceDependencyLoopException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			returned.save(null);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return returned;
	}

	public OWLOntologyResource createNewOntology(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, OWLOntologyResource metaModelResource) {
		logger.warning("Not implemented yet");
		return null;
	}

	public OWLOntologyRepository createOntologyRepository(FlexoResourceCenter resourceCenter) {
		OWLOntologyRepository returned = new OWLOntologyRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, OWLOntologyRepository.class, this);
		return returned;
	}

	@Override
	public OWLOntologyLibrary createTechnologyContextManager(FlexoResourceCenterService resourceCenterService) {
		return new OWLOntologyLibrary(this, resourceCenterService);
	}

	public String retrieveModelURI(File aModelFile, FlexoResource<OWLOntology> metaModelResource) {
		return OWLOntology.findOntologyURI(aModelFile);
	}

	/**
	 * Provides a hook to finalize initialization of a TechnologyAdapter.<br>
	 * This method is called:
	 * <ul>
	 * <li>after all TechnologyAdapter have been loaded</li>
	 * <li>after all {@link FlexoResourceCenter} have been initialized</li>
	 * </ul>
	 */
	@Override
	public void initialize() {
		getTechnologyContextManager().init();
	}

	/**
	 * Provides a hook to detect when a new resource center was added or discovered
	 * 
	 * @param newResourceCenter
	 */
	@Override
	public void resourceCenterAdded(FlexoResourceCenter newResourceCenter) {
		getTechnologyContextManager().init();
	}

	@Override
	public OWLBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	public String getExpectedOntologyExtension() {
		return ".owl";
	}

}
