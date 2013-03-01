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
package org.openflexo.technologyadapter.emf.rm;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.JarClassLoader;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.io.EMFMetaModelConverter;
import org.openflexo.toolbox.IProgress;

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
		// Load class and instanciate.
		JarClassLoader jarClassLoader = new JarClassLoader(Collections.singletonList(getFile()));
		Class<?> ePackageClass = jarClassLoader.findClass(getPackageClassName());
		try {
			if (ePackageClass != null) {
				Field ePackageField = ePackageClass.getField("eINSTANCE");
				if (ePackageField != null) {
					setPackage((EPackage) ePackageField.get(null));
					Class<?> resourceFactoryClass = jarClassLoader.findClass(getResourceFactoryClassName());
					if (resourceFactoryClass != null) {
						setResourceFactory((Resource.Factory) resourceFactoryClass.newInstance());
						if (getPackage() != null && getPackage().getNsURI().equalsIgnoreCase(getURI()) && getResourceFactory() != null) {

							EMFMetaModelConverter converter = new EMFMetaModelConverter((EMFTechnologyAdapter) getTechnologyAdapter());
							result = converter.convertMetaModel(getPackage());
							result.setResource(this);
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
