package org.openflexo.foundation.view.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
import org.openflexo.foundation.resource.FlexoResourceDefinition;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.RequiredResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SomeResources;
import org.openflexo.foundation.utils.XMLUtils;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewModelFactory;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;

/**
 * Default implementation for {@link ViewResource}
 * 
 * 
 * @author Sylvain
 * 
 */
@FlexoResourceDefinition( /* This is the resource specification*/
resourceDataClass = View.class, /* ResourceData class which is handled by this resource */
contains = { /* Defines the resources which may be embeddded in this resource */
/*@SomeResources(resourceType = ProjectDataResource.class, pattern = "*.diagram"),*/
@SomeResources(resourceType = VirtualModelInstanceResource.class, pattern = "*.vmxml") }, /* */
require = { /* Defines the resources which are required for this resource */
@RequiredResource(resourceType = ViewPointResource.class, value = ViewResource.VIEWPOINT_RESOURCE) })
public abstract class ViewResourceImpl extends PamelaResourceImpl<View, ViewModelFactory> implements ViewResource {

	static final Logger logger = Logger.getLogger(ViewResourceImpl.class.getPackage().getName());

	public static ViewResource makeViewResource(String name, RepositoryFolder<ViewResource> folder, ViewPoint viewPoint,
			ViewLibrary viewLibrary) {
		try {
			File viewDirectory = new File(folder.getFile(), name + ViewResource.VIEW_SUFFIX);
			ModelFactory factory = new ModelFactory(ViewResource.class);
			ViewResourceImpl returned = (ViewResourceImpl) factory.newInstance(ViewResource.class);
			String baseName = name;
			File xmlFile = new File(viewDirectory, baseName + ".xml");
			returned.setName(name);
			returned.setProject(viewLibrary.getProject());
			returned.setVersion(new FlexoVersion("1.0"));
			returned.setURI(viewLibrary.getProject().getURI() + "/" + name);
			returned.setFile(xmlFile);
			returned.setDirectory(viewDirectory);
			returned.setViewLibrary(viewLibrary);
			returned.setViewPointResource((ViewPointResource) viewPoint.getResource());
			returned.setFactory(new ViewModelFactory(returned));
			viewLibrary.registerResource(returned, folder);

			returned.setServiceManager(viewLibrary.getServiceManager());

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ViewResource retrieveViewResource(File viewDirectory, RepositoryFolder<ViewResource> folder, ViewLibrary viewLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ViewResource.class);
			ViewResourceImpl returned = (ViewResourceImpl) factory.newInstance(ViewResource.class);
			String baseName = viewDirectory.getName().substring(0, viewDirectory.getName().length() - ViewResource.VIEW_SUFFIX.length());
			File xmlFile = new File(viewDirectory, baseName + ".xml");
			returned.setProject(viewLibrary.getProject());
			ViewInfo vpi = findViewInfo(viewDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setFile(xmlFile);
			returned.setDirectory(viewDirectory);
			returned.setName(vpi.name);
			returned.setURI(viewLibrary.getProject().getURI() + "/" + vpi.name);
			if (StringUtils.isNotEmpty(vpi.viewPointURI)) {
				returned.setViewPointResource(viewLibrary.getServiceManager().getViewPointLibrary().getViewPointResource(vpi.viewPointURI));
			}
			returned.setViewLibrary(viewLibrary);
			returned.setFactory(new ViewModelFactory(returned));
			viewLibrary.registerResource(returned, folder);

			returned.setServiceManager(viewLibrary.getServiceManager());

			logger.fine("ViewResource " + xmlFile.getAbsolutePath() + " version " + returned.getModelVersion());

			// Now look for virtual model instances
			if (viewDirectory.exists() && viewDirectory.isDirectory()) {
				for (File virtualModelFile : viewDirectory.listFiles()) {
					if (virtualModelFile.getName().endsWith(VirtualModelInstanceResource.VIRTUAL_MODEL_SUFFIX)) {
						VirtualModelInstanceResource virtualModelInstanceResource = VirtualModelInstanceResourceImpl
								.retrieveVirtualModelInstanceResource(virtualModelFile, returned);
						returned.addToContents(virtualModelInstanceResource);
					} /*else if (virtualModelFile.getName().endsWith(ProjectDataResource.DIAGRAM_SUFFIX)) {
						ProjectDataResource diagramResource = ProjectDataResourceImpl.retrieveDiagramResource(virtualModelFile, returned);
						returned.addToContents(diagramResource);
						}*/
				}
			}
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public View getView() {
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

	@Override
	public ViewPoint getViewPoint() {
		if (getViewPointResource() != null) {
			return getViewPointResource().getViewPoint();
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
	public View loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException, InvalidXMLException,
			InconsistentDataException, InvalidModelDefinitionException {

		View returned = super.loadResourceData(progress);

		return returned;
	}

	@Override
	public Class<View> getResourceDataClass() {
		return View.class;
	}

	@Override
	public List<VirtualModelInstanceResource> getVirtualModelInstanceResources() {
		// View view = getView();
		return getContents(VirtualModelInstanceResource.class);
	}

	@Override
	public String getURI() {
		if (getProject() != null) {
			return getProject().getURI() + "/" + getName();
		}
		return null;
	}

	private static class ViewInfo {
		public String viewPointURI;
		@SuppressWarnings("unused")
		public String viewPointVersion;
		public String name;
	}

	private static ViewInfo findViewInfo(File viewDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + viewDirectory);

			String baseName = viewDirectory.getName().substring(0, viewDirectory.getName().length() - 5);
			File xmlFile = new File(viewDirectory, baseName + ".xml");

			if (xmlFile.exists()) {
				document = XMLUtils.readXMLFile(xmlFile);
				Element root = XMLUtils.getElement(document, "View");
				if (root != null) {
					ViewInfo returned = new ViewInfo();
					returned.name = baseName;
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("viewPointURI")) {
							logger.fine("Returned " + at.getValue());
							returned.viewPointURI = at.getValue();
						} else if (at.getName().equals("viewPointVersion")) {
							logger.fine("Returned " + at.getValue());
							returned.viewPointVersion = at.getValue();
						}
					}
					return returned;
				}
			} else {
				logger.warning("Cannot find file: " + xmlFile.getAbsolutePath());
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
	public synchronized boolean hasWritePermission() {
		return true;
	}

	@Override
	public boolean delete() {
		if (super.delete()) {
			getServiceManager().getResourceManager().addToFilesToDelete(getDirectory());
			return true;
		}
		return false;
	}

}
