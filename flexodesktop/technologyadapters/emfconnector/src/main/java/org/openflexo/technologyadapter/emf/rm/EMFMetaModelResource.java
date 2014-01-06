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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

@ModelEntity
@ImplementationClass(EMFMetaModelResourceImpl.class)
@XMLElement
public interface EMFMetaModelResource extends FlexoFileResource<EMFMetaModel>,
		FlexoMetaModelResource<EMFModel, EMFMetaModel, EMFTechnologyAdapter> {

	public static final String EXTENSION = "extension";
	public static final String PACKAGE_CLASSNAME = "package.classname";
	public static final String PACKAGE = "package";
	public static final String RESOURCE_FACTORY_CLASSNAME = "resourcefactory.classname";
	public static final String RESOURCE_FACTORY = "resourcefactory";

	/**
	 * Setter of extension for model files related to this MtaModel.
	 * 
	 * @return
	 */
	@Setter(EXTENSION)
	void setModelFileExtension(String modelFileExtension);

	/**
	 * Getter of extension for model files related to this MtaModel.
	 * 
	 * @return
	 */
	@Getter(EXTENSION)
	String getModelFileExtension();

	/**
	 * Setter of Package MetaModel.
	 * 
	 * @param ePackage
	 */
	@Setter(value = PACKAGE_CLASSNAME)
	void setPackageClassName(String ePackage);

	/**
	 * Getter of Package MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = PACKAGE_CLASSNAME, ignoreType = true)
	String getPackageClassName();

	/**
	 * Setter of Package MetaModel.
	 * 
	 * @param ePackage
	 */
	@Setter(value = PACKAGE)
	void setPackage(EPackage ePackage);

	/**
	 * Getter of Package MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = PACKAGE, ignoreType = true)
	EPackage getPackage();

	/**
	 * Setter of ResourceFactory of Model for MetaModel.
	 * 
	 * @param resourceFactory
	 */
	@Setter(RESOURCE_FACTORY_CLASSNAME)
	void setResourceFactoryClassName(String resourceFactory);

	/**
	 * Getter of ResourceFactory of Model for MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = RESOURCE_FACTORY_CLASSNAME, ignoreType = true)
	String getResourceFactoryClassName();

	/**
	 * Setter of ResourceFactory of Model for MetaModel.
	 * 
	 * @param resourceFactory
	 */
	@Setter(RESOURCE_FACTORY)
	void setResourceFactory(Resource.Factory resourceFactory);

	/**
	 * Getter of ResourceFactory of Model for MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = RESOURCE_FACTORY, ignoreType = true)
	Resource.Factory getResourceFactory();

	/**
	 * Get the MetaModel stored in the Resource..
	 * 
	 * @return
	 */
	@Override
	public EMFMetaModel getMetaModelData();
}
