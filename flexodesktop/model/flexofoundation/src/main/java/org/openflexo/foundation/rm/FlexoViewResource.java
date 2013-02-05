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
package org.openflexo.foundation.rm;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLoaded;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;

/**
 * Represents a view resource
 * 
 * @author sguerin
 * 
 */
public class FlexoViewResource extends FlexoXMLStorageResource<View> {

	private static final Logger logger = Logger.getLogger(FlexoViewResource.class.getPackage().getName());

	protected String name;
	private ViewPointResource vpResource;
	private File viewDirectory;

	/**
	 * Constructor used for explicit construction of a new View when the {@link ViewPoint} is known.<br>
	 * Supplied file does't exist or is empty
	 * 
	 * @param aProject
	 * @param aName
	 * @param viewFile
	 * @param viewPoint
	 * @throws InvalidFileNameException
	 */
	public FlexoViewResource(FlexoProject aProject, String aName, RepositoryFolder<FlexoViewResource> folder, ViewPoint viewPoint)
			throws InvalidFileNameException {
		this(aProject);

		viewDirectory = new File(folder.getFile(), aName + ".view");
		FlexoProjectFile viewFile = new FlexoProjectFile(new File(viewDirectory, aName + ".xml"), aProject);
		setName(aName);
		setResourceFile(viewFile);
		vpResource = viewPoint.getResource();
	}

	/**
	 * Constructor used for {@link FlexoViewResource} instanciation from file stored on disk<br>
	 * ViewPoint informations are retrieved from file on disk which should exists
	 * 
	 * @param aProject
	 * @param aName
	 * @param viewFile
	 * @throws InvalidFileNameException
	 */
	public FlexoViewResource(FlexoProject aProject, File viewDirectory) throws InvalidFileNameException {
		this(aProject);
		ViewInfo viewInfo = findViewPointInfo(viewDirectory);
		if (StringUtils.isNotEmpty(viewInfo.viewPointURI)) {
			vpResource = project.getServiceManager().getViewPointLibrary().getViewPointResource(viewInfo.viewPointURI);
		}
		setName(viewInfo.name);

		String baseName = viewDirectory.getName().substring(0, viewDirectory.getName().length() - 5);
		File xmlFile = new File(viewDirectory, baseName + ".xml");

		setResourceFile(new FlexoProjectFile(xmlFile, aProject));

	}

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoViewResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoViewResource(FlexoProject aProject) {
		super(aProject, aProject.getServiceManager());
	}

	/**
	 * Return resource corresponding to ViewPoint this View is conform to
	 * 
	 * @return
	 */
	public ViewPointResource getViewPointResource() {
		return vpResource;
	}

	/**
	 * Return (eventually load when unloaded) the ViewPoint this View is conform to
	 * 
	 * @return
	 */
	public ViewPoint getViewPoint() {
		ViewPointResource vpRes = getViewPointResource();
		if (vpRes != null) {
			return vpRes.getViewPoint();
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String aName) {
		name = aName;
		setChanged();
	}

	@Override
	public void setResourceData(View view) {
		_resourceData = view;
	}

	public View getView() {
		return getResourceData();
	}

	public View getView(FlexoProgress progress) {
		return getResourceData(progress);
	}

	public final View createNewView() {
		return new View(getProject());
	}

	private StringEncoder encoder;

	@Override
	protected boolean isLoadable() {
		return super.isLoadable() && getViewPointResource() != null;
	}

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), new RelativePathFileConverter(getViewPointResource()
					.getDirectory()));
		}
		return encoder;
	}

	@Override
	public View performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException,
			FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		View view;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Loading view " + getName());
		}
		try {
			view = super.performLoadResourceData(progress, loadingHandler);
		} catch (FlexoFileNotFoundException e) {
			// OK, i create the resource by myself !
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating new component " + getName());
			}
			view = createNewView();
			try {
				view.setFlexoResource(this);
			} catch (DuplicateResourceException e1) {
				e1.printStackTrace();
				logger.warning("DuplicateResourceException !!!");
			}
			_resourceData = view;
		}
		logger.fine("Notify loading for view " + getName());
		setChanged();
		notifyObservers(new ViewLoaded(view));

		logger.info("OK, on a charge le view");

		return view;
	}

	@Override
	protected void saveResourceData(boolean clearIsModified)
			throws org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException, SaveResourcePermissionDeniedException {
		viewDirectory.mkdirs();
		super.saveResourceData(clearIsModified);
	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that process deserializing
	 * requires a FlexoProcessBuilder instance: in this case: YES !
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Class getResourceDataClass() {
		return View.class;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		ViewBuilder builder = new ViewBuilder(this);
		builder.view = _resourceData;
		return builder;
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return true;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		ValidationReport report = getProject().validate();
		for (ValidationIssue issue : report.getValidationIssues()) {
			if (issue instanceof DuplicateObjectIDIssue) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.VIEW;
	}

	public RepositoryFolder<FlexoViewResource> getParentFolder() {
		return getProject().getViewLibrary().getParentFolder(this);
	}

	@Override
	public String getURI() {
		return getProject().getURI() + "/" + getName();
	}

	private static class ViewInfo {
		public String viewPointURI;
		public String viewPointVersion;
		public String name;
	}

	private static ViewInfo findViewPointInfo(File viewDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + viewDirectory);

			String baseName = viewDirectory.getName().substring(0, viewDirectory.getName().length() - 5);
			File xmlFile = new File(viewDirectory, baseName + ".xml");

			if (xmlFile.exists()) {
				document = FlexoXMLFileResourceImpl.readXMLFile(xmlFile);
				Element root = FlexoXMLFileResourceImpl.getElement(document, "View");
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

}
