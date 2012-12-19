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
package org.openflexo.technologyadapter.emf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.dm.JarClassLoader;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * This class defines and implements the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({
/** Instances */
@DeclarePatternRole(IndividualPatternRole.class),
/** Classes */
@DeclarePatternRole(ClassPatternRole.class),
/** Data properties */
@DeclarePatternRole(DataPropertyPatternRole.class),
/** Object properties */
@DeclarePatternRole(ObjectPropertyPatternRole.class) })
@DeclareEditionActions({})
public class EMFTechnologyAdapter extends TechnologyAdapter<EMFModel, EMFMetaModel> {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyAdapter.class.getPackage().getName());

	/**
	 * 
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public EMFTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getName()
	 */
	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFMetaModelRepository(this, resourceCenter);
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidMetaModelFile(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return retrieveMetaModelResource(aMetaModelFile, technologyContextManager) != null;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return retrieveMetaModelResource(aMetaModelFile, technologyContextManager).getPackage().getNsURI();
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFMetaModelResource retrieveMetaModelResource(final File aMetaModelFile,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		EMFMetaModelResource metaModelResource = null;
		if (aMetaModelFile.isDirectory()) {
			// Read emf.properties file.
			File[] emfPropertiesFiles = aMetaModelFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return dir == aMetaModelFile && name.equalsIgnoreCase("emf.properties");
				}
			});
			if (emfPropertiesFiles.length == 1) {
				try {
					Properties emfProperties = new Properties();
					emfProperties.load(new FileReader(emfPropertiesFiles[0]));
					String extension = emfProperties.getProperty("EXTENSION");
					String ePackageClassName = emfProperties.getProperty("PACKAGE");
					String resourceFactoryClassName = emfProperties.getProperty("RESOURCE_FACTORY");
					if (extension != null && ePackageClassName != null && resourceFactoryClassName != null) {
						// Load class and instanciate.
						EPackage ePackage = null;
						Resource.Factory resourceFactory = null;
						JarClassLoader jarClassLoader = new JarClassLoader(Collections.singletonList(aMetaModelFile));
						Class<?> ePackageClass = jarClassLoader.findClass(ePackageClassName);
						if (ePackageClass != null) {
							Field ePackageField = ePackageClass.getField("eINSTANCE");
							if (ePackageField != null) {
								ePackage = (EPackage) ePackageField.get(null);
								Class<?> resourceFactoryClass = jarClassLoader.findClass(resourceFactoryClassName);
								if (resourceFactoryClass != null) {
									resourceFactory = (Resource.Factory) resourceFactoryClass.newInstance();
									if (extension != null && ePackage != null && resourceFactory != null) {
										ModelFactory factory = new ModelFactory(EMFMetaModelResource.class);
										metaModelResource = factory.newInstance(EMFMetaModelResource.class);
										metaModelResource.setTechnologyAdapter(this);
										metaModelResource.setURI(ePackage.getNsURI());
										metaModelResource.setName(ePackage.getName());
										metaModelResource.setFile(aMetaModelFile);
										metaModelResource.setModelFileExtension(extension);
										metaModelResource.setPackage(ePackage);
										metaModelResource.setResourceFactory(resourceFactory);
									}
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
			}
		}

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;
		emfContextManager.registerMetaModel(metaModelResource);

		return metaModelResource;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFModelRepository(this, resourceCenter);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(java.io.File,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		boolean isValid = false;
		if (aModelFile.exists()) {
			EMFMetaModelResource emfMetaModelResource = (EMFMetaModelResource) metaModelResource;
			if (aModelFile.getName().endsWith("." + emfMetaModelResource.getModelFileExtension())) {
				isValid = true;
			}
		}
		return isValid;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public String retrieveModelURI(File aModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return retrieveModelResource(aModelFile, technologyContextManager).getURI();
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFModelResource retrieveModelResource(File aModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		EMFModelResource emfModelResource = null;
		// FIXME TODO
		if (aModelFile.isFile()) {
			new EMFModelResource((FlexoProjectBuilder) null);
		}

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;
		emfContextManager.registerModel(emfModelResource);

		return emfModelResource;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createEmptyModel(org.openflexo.foundation.rm.FlexoProject,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public EMFModel createEmptyModel(FlexoProject project, EMFMetaModel metaModel,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO implement this
		return null;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.ViewPoint)
	 */
	@Override
	public TechnologyContextManager<EMFModel, EMFMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
		return new EMFTechnologyContextManager();
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.ViewPoint)
	 */
	@Override
	public EMFModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new EMFModelSlot(viewPoint, this);
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getTechnologyContextManager()
	 */
	@Override
	public EMFTechnologyContextManager getTechnologyContextManager() {
		return (EMFTechnologyContextManager) super.getTechnologyContextManager();
	}
}
