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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.view.ViewLoaded;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.xml.ViewBuilder;
import org.openflexo.toolbox.RelativePathFileConverter;
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

	public FlexoViewResource(FlexoProject aProject, String aName, FlexoProjectFile viewFile, ViewPoint viewPoint)
			throws InvalidFileNameException {
		this(aProject);
		setName(aName);
		setResourceFile(viewFile);
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
		return null;
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
		return ResourceType.OE_SHEMA;
	}

	public RepositoryFolder<FlexoViewResource> getParentFolder() {
		return getProject().getViewLibrary().getParentFolder(this);
	}

}
