package org.openflexo.foundation.rm;

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
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;

/**
 * Default implementation for {@link VirtualModelInstanceResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class VirtualModelInstanceResourceImpl<VMI extends VirtualModelInstance<VMI, ?>> extends FlexoXMLFileResourceImpl<VMI>
		implements VirtualModelInstanceResource<VMI>, AccessibleProxyObject {

	static final Logger logger = Logger.getLogger(VirtualModelInstanceResourceImpl.class.getPackage().getName());

	public static VirtualModelInstanceResource<?> makeVirtualModelInstanceResource(String name, VirtualModel virtualModel, View view,
			String title) {
		try {
			ModelFactory factory = new ModelFactory(VirtualModelInstanceResource.class);
			VirtualModelInstanceResourceImpl<?> returned = (VirtualModelInstanceResourceImpl<?>) factory
					.newInstance(VirtualModelInstanceResource.class);
			String baseName = name;
			File xmlFile = new File(view.getResource().getFile().getParentFile(), baseName
					+ VirtualModelInstanceResource.VIRTUAL_MODEL_SUFFIX);
			returned.setProject(view.getProject());
			returned.setName(name);
			returned.setFile(xmlFile);
			returned.setURI(view.getURI() + "/" + baseName);
			returned.setVirtualModelResource(virtualModel.getResource());
			returned.setTitle(title);
			returned.setServiceManager(view.getProject().getServiceManager());

			view.getResource().addToContents(returned);
			view.getResource().notifyContentsAdded(returned);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static VirtualModelInstanceResource<?> retrieveVirtualModelInstanceResource(File virtualModelInstanceFile,
			ViewResource viewResource) {
		try {
			ModelFactory factory = new ModelFactory(VirtualModelInstanceResource.class);
			VirtualModelInstanceResourceImpl<?> returned = (VirtualModelInstanceResourceImpl<?>) factory
					.newInstance(VirtualModelInstanceResource.class);
			String baseName = virtualModelInstanceFile.getName().substring(0,
					virtualModelInstanceFile.getName().length() - VirtualModelInstanceResource.VIRTUAL_MODEL_SUFFIX.length());
			File xmlFile = new File(viewResource.getFile().getParentFile(), baseName + VirtualModelInstanceResource.VIRTUAL_MODEL_SUFFIX);
			returned.setProject(viewResource.getProject());
			returned.setName(baseName);
			returned.setURI(viewResource.getURI() + "/" + baseName);
			returned.setFile(xmlFile);
			VirtualModelInstanceInfo vmiInfo = findVirtualModelInstanceInfo(xmlFile, "VirtualModelInstance");
			if (vmiInfo == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setServiceManager(viewResource.getProject().getServiceManager());
			if (StringUtils.isNotEmpty(vmiInfo.virtualModelURI)) {
				if (viewResource != null && viewResource.getViewPoint() != null
						&& viewResource.getViewPoint().getVirtualModelNamed(vmiInfo.virtualModelURI) != null) {
					returned.setVirtualModelResource(viewResource.getViewPoint().getVirtualModelNamed(vmiInfo.virtualModelURI)
							.getResource());
				}
			}
			returned.setTitle(vmiInfo.title);
			viewResource.addToContents(returned);
			viewResource.notifyContentsAdded(returned);
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

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public final VirtualModelInstanceBuilder instanciateNewBuilder() {
		return new VirtualModelInstanceBuilder(getContainer(), this);
	}

	protected static class VirtualModelInstanceInfo {
		public String virtualModelURI;
		public String name;
		public String title;
	}

	protected static VirtualModelInstanceInfo findVirtualModelInstanceInfo(File virtualModelInstanceFile, String searchedRootXMLTag) {
		Document document;
		try {
			logger.fine("Try to find infos for " + virtualModelInstanceFile);

			String baseName = virtualModelInstanceFile.getName().substring(0,
					virtualModelInstanceFile.getName().length() - VirtualModelInstanceResource.VIRTUAL_MODEL_SUFFIX.length());

			if (virtualModelInstanceFile.exists()) {
				document = FlexoXMLFileResourceImpl.readXMLFile(virtualModelInstanceFile);
				Element root = FlexoXMLFileResourceImpl.getElement(document, searchedRootXMLTag);
				if (root != null) {
					VirtualModelInstanceInfo returned = new VirtualModelInstanceInfo();
					returned.name = baseName;
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("virtualModelURI")) {
							logger.fine("Returned " + at.getValue());
							returned.virtualModelURI = at.getValue();
						}
						if (at.getName().equals("title")) {
							logger.fine("Returned " + at.getValue());
							returned.title = at.getValue();
						}
					}
					return returned;
				}
			} else {
				logger.warning("Cannot find file: " + virtualModelInstanceFile.getAbsolutePath());
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
	public VMI loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FileNotFoundException, FlexoException {
		VMI returned = super.loadResourceData(progress);
		if (returned.isSynchronizable()) {
			returned.synchronize(null);
		}
		getContainer().getView().addToVirtualModelInstances(returned);
		returned.clearIsModified();
		return returned;
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(VirtualModelTechnologyAdapter.class);
		}
		return null;
	}

	@Override
	public void setTechnologyAdapter(TechnologyAdapter technologyAdapter) {
		// Not applicable
	}

	@Override
	public ViewResource getContainer() {
		return (ViewResource) performSuperGetter(CONTAINER);
	}

}
