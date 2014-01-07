package org.openflexo.drm;

import java.util.logging.Logger;

import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.InconsistentDataException;
import org.openflexo.foundation.InvalidModelDefinitionException;
import org.openflexo.foundation.InvalidXMLException;
import org.openflexo.foundation.ProjectDataResource;
import org.openflexo.foundation.resource.FlexoFileNotFoundException;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;

/**
 * Default implementation for {@link ProjectDataResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class DocResourceCenterResourceImpl extends PamelaResourceImpl<DocResourceCenter, DRMModelFactory> implements
		DocResourceCenterResource {

	static final Logger logger = Logger.getLogger(DocResourceCenterResourceImpl.class.getPackage().getName());

	public static DocResourceCenterResource retrieveDocResourceCenterResource(DocResourceManager docResourceManager) {
		try {
			ModelFactory factory = new ModelFactory(DocResourceCenterResource.class);
			DocResourceCenterResourceImpl returned = (DocResourceCenterResourceImpl) factory.newInstance(DocResourceCenterResource.class);
			returned.setFactory(docResourceManager.getDRMModelFactory());
			returned.setName("DocResourceCenter");
			returned.setFile(docResourceManager.getDRMFile());
			returned.setURI("http://www.openflexo.org/DocResourceCenter");
			returned.setServiceManager(docResourceManager.getServiceManager());
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DocResourceCenter getDocResourceCenter() {
		return getLoadedResourceData();
	}

	@Override
	public Class<DocResourceCenter> getResourceDataClass() {
		return DocResourceCenter.class;
	}

	@Override
	public DocResourceCenter loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException, InvalidXMLException,
			InconsistentDataException, InvalidModelDefinitionException {
		DocResourceCenter returned = super.loadResourceData(progress);
		returned.clearIsModified();
		return returned;
	}

}
