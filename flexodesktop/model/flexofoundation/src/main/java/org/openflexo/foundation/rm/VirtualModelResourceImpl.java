package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.StringEncoder;

public abstract class VirtualModelResourceImpl<VM extends VirtualModel<VM>> extends FlexoXMLFileResourceImpl<VM> implements
		VirtualModelResource<VM> {

	static final Logger logger = Logger.getLogger(VirtualModelResourceImpl.class.getPackage().getName());

	protected RelativePathFileConverter relativePathFileConverter;

	private StringEncoder encoder;

	public static VirtualModelResource makeVirtualModelResource(File virtualModelDirectory, File virtualModelXMLFile,
			ViewPointResource viewPointResource, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(VirtualModelResource.class);
			VirtualModelResourceImpl returned = (VirtualModelResourceImpl) factory.newInstance(VirtualModelResource.class);
			returned.setName(virtualModelDirectory.getName());
			returned.setDirectory(virtualModelDirectory);
			returned.setFile(virtualModelXMLFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setServiceManager(viewPointLibrary.getFlexoServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(virtualModelDirectory);
			viewPointResource.addToContents(returned);
			viewPointResource.notifyContentsAdded(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	@Override
	public final VirtualModelBuilder instanciateNewBuilder() {
		return new VirtualModelBuilder(getViewPointLibrary(), getContainer().getViewPoint(), this, getModelVersion());
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public VM getVirtualModel() {
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
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 */
	@Override
	public VM loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException, FileNotFoundException,
			ResourceDependencyLoopException {

		VM returned = super.loadResourceData(progress);
		getContainer().getViewPoint().addToVirtualModels(returned);
		return returned;
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

}
