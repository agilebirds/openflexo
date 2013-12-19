package org.openflexo.foundation.viewpoint.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.InconsistentDataException;
import org.openflexo.foundation.InvalidModelDefinitionException;
import org.openflexo.foundation.InvalidXMLException;
import org.openflexo.foundation.resource.FlexoFileNotFoundException;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelModelFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;

public abstract class VirtualModelResourceImpl extends PamelaResourceImpl<VirtualModel, VirtualModelModelFactory> implements
		VirtualModelResource, AccessibleProxyObject {

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
			returned.setURI(viewPointResource.getURI() + "/" + virtualModelDirectory.getName());
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(virtualModelDirectory);
			viewPointResource.addToContents(returned);
			viewPointResource.notifyContentsAdded(returned);

			returned.setFactory(new VirtualModelModelFactory());

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static VirtualModelResource retrieveVirtualModelResource(File virtualModelDirectory, File virtualModelXMLFile,
			ViewPointResource viewPointResource, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(VirtualModelResource.class);
			VirtualModelResourceImpl returned = (VirtualModelResourceImpl) factory.newInstance(VirtualModelResource.class);
			String baseName = virtualModelDirectory.getName();
			File xmlFile = new File(virtualModelDirectory, baseName + ".xml");
			VirtualModelInfo vpi = findVirtualModelInfo(virtualModelDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setFile(xmlFile);
			returned.setDirectory(virtualModelDirectory);
			returned.setName(vpi.name);
			returned.setURI(viewPointResource.getURI() + "/" + virtualModelDirectory.getName());
			if (StringUtils.isNotEmpty(vpi.version)) {
				returned.setVersion(new FlexoVersion(vpi.version));
			}
			returned.setModelVersion(new FlexoVersion(StringUtils.isNotEmpty(vpi.modelVersion) ? vpi.modelVersion : "0.1"));
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setServiceManager(viewPointLibrary.getServiceManager());

			logger.fine("VirtualModelResource " + xmlFile.getAbsolutePath() + " version " + returned.getModelVersion());

			returned.relativePathFileConverter = new RelativePathFileConverter(virtualModelDirectory);

			returned.setFactory(new VirtualModelModelFactory());

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

	/**
	 * Return virtual model stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	@Override
	public VirtualModel getVirtualModel() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return virtual model stored by this resource when loaded<br>
	 * Do not force the resource data to be loaded
	 */
	@Override
	public VirtualModel getLoadedVirtualModel() {
		if (isLoaded()) {
			return getVirtualModel();
		}
		return null;
	}

	@Override
	public Class<VirtualModel> getResourceDataClass() {
		return VirtualModel.class;
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
	public VirtualModel loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException, InvalidXMLException,
			InconsistentDataException, InvalidModelDefinitionException {
		VirtualModel returned = super.loadResourceData(progress);
		getContainer().getViewPoint().addToVirtualModels(returned);
		returned.clearIsModified();
		return returned;
	}

	private static class VirtualModelInfo {
		public String version;
		public String name;
		public String modelVersion;
	}

	private static VirtualModelInfo findVirtualModelInfo(File virtualModelDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + virtualModelDirectory);

			String baseName = virtualModelDirectory.getName();
			File xmlFile = new File(virtualModelDirectory, baseName + ".xml");

			if (xmlFile.exists()) {

				document = readXMLFile(xmlFile);
				Element root = getElement(document, "VirtualModel");
				if (root != null) {
					VirtualModelInfo returned = new VirtualModelInfo();
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("name")) {
							logger.fine("Returned " + at.getValue());
							returned.name = at.getValue();
						} else if (at.getName().equals("version")) {
							logger.fine("Returned " + at.getValue());
							returned.version = at.getValue();
						} else if (at.getName().equals("modelVersion")) {
							logger.fine("Returned " + at.getValue());
							returned.modelVersion = at.getValue();
						}
					}
					if (StringUtils.isEmpty(returned.name)) {
						returned.name = virtualModelDirectory.getName();
					}
					return returned;
				}
			} else {
				logger.warning("While analysing virtual model candidate: " + virtualModelDirectory.getAbsolutePath() + " cannot find file "
						+ xmlFile.getAbsolutePath());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	@Override
	public boolean delete() {
		if (super.delete()) {
			getServiceManager().getResourceManager().addToFilesToDelete(getDirectory());
			return true;
		}
		return false;
	}

	@Override
	public ViewPointResource getContainer() {
		return (ViewPointResource) performSuperGetter(CONTAINER);
	}

}
