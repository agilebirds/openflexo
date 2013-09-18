/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2013 Openflexo
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
package org.openflexo.technologyadapter.emf.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelConverter;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.JarInDirClassLoader;

/**
 * EMF MetaModel Resource Implementation.
 * 
 * @author gbesancon
 */
public abstract class EMFMetaModelResourceImpl extends FlexoFileResourceImpl<EMFMetaModel> implements EMFMetaModelResource {

	protected static final Logger logger = Logger.getLogger(EMFMetaModelResourceImpl.class.getPackage().getName());

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource#getMetaModel()
	 */
	@Override
	public EMFMetaModel getMetaModelData() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#loadResourceData(org.openflexo.toolbox.IProgress)
	 */
	@Override
	public EMFMetaModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException {
		EMFMetaModel result = null;
		Class<?> ePackageClass = null;
		ClassLoader classLoader = null;
		File f = getFile();

		// Load class and instanciate.

		try {
			if (f != null){
				ClassLoader currentThreadClassLoader
				= Thread.currentThread().getContextClassLoader();

				// Add the conf dir to the classpath
				// Chain the current thread classloader

				classLoader = new JarInDirClassLoader(Collections.singletonList(getFile()));

				System.out.println("**************** A Classloader to load : " + getFile().getCanonicalPath());
				URLClassLoader child = new URLClassLoader (new URL[]{f.toURI().toURL()}, this.getClass().getClassLoader());

				
				ePackageClass = classLoader.loadClass(getPackageClassName());

				// Replace the thread classloader - assumes
				// you have permissions to do so
				Thread.currentThread().setContextClassLoader(classLoader);
			}
			else {
				classLoader = EMFMetaModelResourceImpl.class.getClassLoader();
				ePackageClass = classLoader.loadClass(getPackageClassName());
			}
			if (ePackageClass != null) {
				Field ePackageField = ePackageClass.getField("eINSTANCE");
				if (ePackageField != null) {
					setPackage((EPackage) ePackageField.get(null));
					Class<?> resourceFactoryClass = classLoader.loadClass(getResourceFactoryClassName());
					if (resourceFactoryClass != null) {
						setResourceFactory((Resource.Factory) resourceFactoryClass.newInstance());
						if (getPackage() != null && getPackage().getNsURI().equalsIgnoreCase(getURI()) && getResourceFactory() != null) {

							EMFMetaModelConverter converter = new EMFMetaModelConverter((EMFTechnologyAdapter) getTechnologyAdapter());
							result = converter.convertMetaModel(getPackage());
							result.setResource(this);
							this.resourceData = result;
						}
					}
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#save(org.openflexo.toolbox.IProgress)
	 */
	@Override
	public void save(IProgress progress) {
		logger.info("MetaModel is not supposed to be modified.");
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.resource.FlexoResource#update()
	 */
	@Override
	public FlexoResourceTree update() {
		logger.info("MetaModel is not supposed to be updated.");
		return null;
	}

	@Override
	public Class<EMFMetaModel> getResourceDataClass() {
		return EMFMetaModel.class;
	}

}
