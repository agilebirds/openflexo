package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * Default implementation for {@link VirtualModelInstanceResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class VirtualModelInstanceResourceImpl<VMI extends VirtualModelInstance<VMI, ?>> extends FlexoXMLFileResourceImpl<VMI>
		implements VirtualModelInstanceResource<VMI> {

	public static VirtualModelInstanceResource<?> makeVirtualModelInstanceResource(String name, VirtualModel virtualModel, View view) {
		try {
			ModelFactory factory = new ModelFactory(VirtualModelInstanceResource.class);
			VirtualModelInstanceResourceImpl<?> returned = (VirtualModelInstanceResourceImpl<?>) factory
					.newInstance(VirtualModelInstanceResource.class);
			returned.setServiceManager(view.getProject().getServiceManager());
			String baseName = name;
			File xmlFile = new File(view.getFlexoResource().getFile().getParentFile(), baseName + ".vmxml");
			returned.setName(name);
			returned.setURI(view.getProject().getURI() + "/" + baseName);
			returned.setFile(xmlFile);
			returned.setVirtualModel(virtualModel);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public VMI getVirtualModelInstance() {
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

	@Override
	public Class<VMI> getResourceDataClass() {
		return (Class<VMI>) VirtualModelInstance.class;
	}
}
