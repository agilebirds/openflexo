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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.action.UpdateDMRepository;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.StringEncoder;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoDMResource extends FlexoXMLStorageResource<DMModel> {

	private static final Logger logger = Logger.getLogger(FlexoDMResource.class.getPackage().getName());

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoDMResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoDMResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoDMResource(FlexoProject aProject, FlexoProjectFile dmFile) throws InvalidFileNameException {
		this(aProject);
		setResourceFile(dmFile);
	}

	public FlexoDMResource(FlexoProject aProject, DMModel dmModel, FlexoProjectFile dmFile) throws InvalidFileNameException {
		this(aProject, dmFile);
		_resourceData = dmModel;
		dmModel.setFlexoResource(this);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.DATA_MODEL;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	public Class getResourceDataClass() {
		return DMModel.class;
	}

	/*public FlexoResourceData loadResourceData(FlexoProgress progress) throws LoadXMLResourceException
	{
	    return loadResourceData(new Vector(), progress);
	}

	public StorageResourceData loadResourceData(Vector requestingResources) throws LoadXMLResourceException
	{
	    return loadResourceData(requestingResources, null);
	}*/

	@Override
	public DMModel performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException,
			ProjectLoadingCancelledException, MalformedXMLException {
		DMModel dmModel;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading_data_model"));
		}

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("loadResourceData() in FlexoDMResource");
		}

		if (getXmlVersion().isLesserThan(new FlexoVersion("1.1"))) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Recreates the DataModel...");
			}
			List<FlexoResource<? extends FlexoResourceData>> resourcesToDelete = new ArrayList<FlexoResource<? extends FlexoResourceData>>();
			for (FlexoResource<? extends FlexoResourceData> r : getProject()) {
				if (r.getResourceType() == ResourceType.EOMODEL) {
					resourcesToDelete.add(r);
				}
			}
			for (FlexoResource<? extends FlexoResourceData> r : resourcesToDelete) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Removing resource " + r);
				}
				r.delete();
			}
			dmModel = DMModel.createNewDMModel(getProject(), this);
			_resourceData = dmModel;
		} else {
			try {
				dmModel = super.performLoadResourceData(progress, loadingHandler);
			} catch (FlexoFileNotFoundException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("File " + getFile().getName() + " NOT found");
				}
				e.printStackTrace();
				return null;
			}
		}

		dmModel.setProject(getProject());

		return dmModel;
	}

	public void _setDeserializingDataModel(DMModel dmModel) {
		_resourceData = dmModel;
	}

	/**
	 * Overrides saveResourceData
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#saveResourceData()
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveXMLResourceException, SaveResourcePermissionDeniedException {
		StringEncoder encoder = getProject() != null ? getProject().getStringEncoder() : StringEncoder.getDefaultInstance();
		String s = encoder._getDateFormat();
		try {
			encoder._setDateFormat("HH:mm:ss dd/MM/yyyy SSS");
			super.saveResourceData(clearIsModified);
		} catch (SaveXMLResourceException e) {
			throw e;
		} catch (SaveResourcePermissionDeniedException e) {
			throw e;
		} finally {
			if (s != null) {
				encoder._setDateFormat(s);
			}
		}
	}

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns true to indicate that process deserializing
	 * requires a FlexoProcessBuilder instance
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Returns the required newly instancied FlexoProcessBuilder
	 * 
	 * @return boolean
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoDMBuilder returned = new FlexoDMBuilder(this);
		returned.dmModel = _resourceData;
		return returned;
	}

	@Override
	public void backwardSynchronizeWith(FlexoResource resource) throws FlexoException {
		if (resource instanceof FlexoJarResource) {
			ExternalRepository jarRepository = getResourceData().getExternalRepository((FlexoJarResource) resource);
			if (jarRepository != null) {
				logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
				// TODO: Please implement this better later
				// Used editor will be null
				try {
					UpdateDMRepository.actionType.makeNewAction(jarRepository, null).doAction();
				} catch (LinkageError e) {
					if (logger.isLoggable(Level.SEVERE)) {
						logger.log(Level.SEVERE, "LinkageError in jars!!!", e);
					}
					e.printStackTrace();
				}
			}
		}
		super.backwardSynchronizeWith(resource);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToDependentResources(getProject().getFlexoDKVResource());
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This method implements conversion from v1.0 to v1.1
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was sucessfull
	 */
	@Override
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		if (v1.equals(new FlexoVersion("1.0")) && v2.equals(new FlexoVersion("1.1"))) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Recreates the DataModel...");
			}
			// If there is a resource already registered, we remove it.
			if (getProject().getResources().get(getResourceIdentifier()) != null) {
				getProject().getResources().remove(getResourceIdentifier());
			}
			FileWritingLock lock = willWriteOnDisk();
			_resourceData = DMModel.createNewDMModel(getProject(), this);
			hasWrittenOnDisk(lock);
			return true;
		} else if (v1.equals(new FlexoVersion("2.0")) && v2.equals(new FlexoVersion("2.1"))) {
			getResourceData().getWORepository().convertFromVersion_2_0();
			return true;
		} else if (v1.equals(new FlexoVersion("2.3")) && v2.equals(new FlexoVersion("2.4"))) {
			getResourceData().getWORepository().convertFromVersion_2_3_prelude();
			getResourceData().loadInitializingModel();
			getResourceData().getWORepository().convertFromVersion_2_3_postlude();
			for (DMEntity componentEntity : getResourceData().getComponentRepository().getEntities().values()) {
				if (componentEntity instanceof ComponentDMEntity) {
					logger.info("Sets " + componentEntity + " to have " + getResourceData().getWORepository().getCustomComponentEntity()
							+ " as parent entity");
					componentEntity.setParentType(
							DMType.makeResolvedDMType(getResourceData().getWORepository().getCustomComponentEntity()), true);
				}
			}
			for (DMEntity entity : getResourceData().getEntities().values()) {
				if (entity instanceof DMEOEntity) {
					logger.info("Sets " + entity + " to have " + getResourceData().getDefaultParentDMEOEntity() + " as parent entity");
					entity.setParentType(DMType.makeResolvedDMType(getResourceData().getDefaultParentDMEOEntity()), true);
				}
			}
			try {
				getResourceData().save();
			} catch (SaveResourceException e) {
				e.printStackTrace();
			}
			return true;
		} else if (v2.equals("2.8")) {
			FlexoExecutionModelRepository.copyExecutionModelIntoPrj(getProject().getProjectDirectory());
			return true;
		} else if (v2.equals("2.9")) {
			// getResourceData().initializeProcessBusinessDataRepository();
			return true;
		} else {
			return super.convertResourceFileFromVersionToVersion(v1, v2);
		}
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	/*public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate)
	{
		if (resource instanceof FlexoJarResource) {
			if (resource.getLastUpdate().before(requestDate)) {
				logger.info("Finalement, "+this+" ne depend pas de "+resource);
				logger.info("jarDate["+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(resource.getLastUpdate())+"]"
						+" < requestDate["+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(requestDate)+"]");
				return false;
			}
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}*/

}
