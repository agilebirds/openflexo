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
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

@ModelEntity
@ImplementationClass(EMFMetaModelResourceImpl.class)
@XMLElement
public interface EMFMetaModelResource extends FlexoFileResource<EMFMetaModel>, FlexoMetaModelResource<EMFModel, EMFMetaModel> {

	public static final String EXTENSION = "extension";
	public static final String PACKAGE = "package";
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
	@Setter(RESOURCE_FACTORY)
	void setResourceFactory(Resource.Factory resourceFactory);

	/**
	 * Getter of ResourceFactory of Model for MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = RESOURCE_FACTORY, ignoreType = true)
	Resource.Factory getResourceFactory();

	public EMFMetaModel getMetaModel();
}
