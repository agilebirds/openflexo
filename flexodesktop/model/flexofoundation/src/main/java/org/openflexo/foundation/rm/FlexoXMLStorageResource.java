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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.JDOMException;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoXMLSerializableObject;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.FlexoXMLMappings;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.ModelEntity;
import org.openflexo.xmlcode.ModelProperty;
import org.openflexo.xmlcode.SerializationHandler;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This class represents a File Flexo resource, where related file is an XML file A File FlexoResource represent an object handled by Flexo
 * Application Suite (all concerned modules), which could be stored in an XML-File, generally located in related {@link FlexoProject}
 * project directory.
 * 
 * @author sguerin
 */
public abstract class FlexoXMLStorageResource<XMLRD extends XMLStorageResourceData> extends FlexoStorageResource<XMLRD> {

	private static final Logger logger = Logger.getLogger(FlexoXMLStorageResource.class.getPackage().getName());

	protected boolean performLoadWithPreviousVersion = true;

	private boolean _isLoading = false;

	private boolean isConverting = false;

	/**
	 * Stores the version which one this resource has been loaded
	 */
	private FlexoVersion _currentVersion = latestVersion();

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public FlexoXMLStorageResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoXMLStorageResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public synchronized boolean hasMoreRecentThanExpectedDiskUpdate() {
		if (_isLoading) {
			return false;
		}
		return super.hasMoreRecentThanExpectedDiskUpdate();
	}

	@Override
	public XMLRD getResourceData() {
		return getResourceData(null);
	}

	/**
	 * Return data related to this resource, as an instance of an object implementing
	 * 
	 * @param loadingHandler
	 *            TODO
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData
	 * @return
	 * @throws Exception
	 */
	public XMLRD getResourceData(FlexoProgress progress) {
		if (_resourceData == null) {
			try {
				_resourceData = loadResourceData(progress, getLoadingHandler());
				// Now that the resource is loaded, we try to resolve pending EP refs
				getProject().resolvePendingEditionPatternReferences();
			} catch (LoadXMLResourceException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not load resource data for resource " + getResourceIdentifier() + " message: " + e.getMessage());
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine(e.getExtendedMessage());
					e.printStackTrace();
				}
			} catch (FlexoException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not load resource data for resource " + getResourceIdentifier() + " message: " + e.getMessage());
				}
				e.printStackTrace();
			}
		}

		if (_resourceData == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Resource data for resource " + getResourceIdentifier() + " is null !");
			}
		}

		return _resourceData;
	}

	/**
	 * Load resource data by applying a special scheme handling XML versionning, ie to find right XML version of current resource file.<br>
	 * If version of stored file is not conform to latest declared version, convert resource file and update it to latest version.
	 * 
	 * @throws ProjectLoadingCancelledException
	 * @throws MalformedXMLException
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#loadResourceData()
	 */
	@Override
	public XMLRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException,
			FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		if (_resourceData != null) {
			// already loaded
			return _resourceData;
		}
		_isLoading = true;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading") + " " + this.getName());
			progress.resetSecondaryProgress(4);
			progress.setProgress(FlexoLocalization.localizedForKey("loading_from_disk"));
		}
		notifyResourceStatusChanged();
		boolean requiresRMFileSaving = false;
		LoadXMLResourceException exception = null;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Load resource data for " + getResourceIdentifier());
		}
		if (!getFile().exists()) {
			recoverFile();
			if (!getFile().exists()) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("File " + getFile().getAbsolutePath() + " does not exist, throwing exception now!");
				}
				throw new FlexoFileNotFoundException(this);
			}
		}
		XMLRD returned = null;
		boolean projectWasHoldingObjectRegistration = project != null && project.isHoldingProjectRegistration();
		FlexoVersion[] availableVersions = getXmlMappings().getAvailableVersionsForClass(getResourceDataClass());
		FlexoVersion[] availableVersionsNew = new FlexoVersion[availableVersions.length + 1];

		for (int k = 0; k < availableVersions.length; k++) {
			availableVersionsNew[k] = availableVersions[k];
		}
		availableVersionsNew[availableVersions.length] = getXmlVersion();
		// int i = availableVersions.length-1;
		int i = availableVersionsNew.length - 1;
		boolean notCorrectelyDeserialized = true;
		FlexoVersion triedVersion = null;
		if (projectWasHoldingObjectRegistration) {
			project.unholdObjectRegistration();
		}
		try {
			while (notCorrectelyDeserialized
					&& (i >= 0 && performLoadWithPreviousVersion || i == availableVersionsNew.length - 1 && !performLoadWithPreviousVersion)) {
				// triedVersion = availableVersions[i];
				triedVersion = availableVersionsNew[i];
				i--;
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("tried version = " + triedVersion.toString());
				}

				FlexoXMLMappings.ClassModelVersion cmv = getXmlMappings().getClassModelVersion(getResourceDataClass(), triedVersion);
				if (cmv == null) {
					throw new LoadXMLResourceException(this, "Class model version could not be found for class '"
							+ getResourceDataClass().getName() + "' and version " + triedVersion);
				}
				if (cmv.needsManualConversion && !(this instanceof FlexoRMResource) && !(this instanceof FlexoWorkflowResource)
						&& !(this instanceof FlexoComponentLibraryResource) /*
																			 * Little hack because GPO used it differently with conversion
																			 * from 3.5 to 4.0 in RM
																			 */) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("This resource " + getResourceIdentifier() + " must be converted from " + triedVersion + " into "
								+ cmv.toVersion);
					}
					if (progress != null) {
						progress.setProgress(FlexoLocalization.localizedForKey("converting from version ") + triedVersion + " "
								+ FlexoLocalization.localizedForKey("to") + " " + cmv.toVersion);
					}
					isConverting = true;
					if (convertResourceFileFromVersionToVersion(triedVersion, cmv.toVersion)) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Conversion from " + triedVersion + " into " + cmv.toVersion + " was successfull.");
						}
						// Load again with the new version !
						try {
							if (project != null) {
								project.holdObjectRegistration(); // Anyway, we will reload it later
							}
							// TODO: project should not be used for this but rather the resource. If by any chances, the loading of resource
							// initiates the conversion of another one, that other resource will unset this property on the project and
							// there is
							// a good chance that objects that should not be registered will be registered in the project anyway.
							returned = tryToLoadResourceDataWithVersion(cmv.toVersion);
							requiresRMFileSaving = true;
						} catch (JDOMException e) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Malformed XML File: " + e.getMessage());
							}
							return null;
						} catch (XMLOperationException e) {
							e.printStackTrace();
							if (e.getCause() != null) {
								e.getCause().printStackTrace();
							}
							if (exception == null) {
								exception = new LoadXMLResourceException(this, e.getCause() != null ? e.getCause().getMessage()
										: e.getMessage());
							}
							exception.addLoadException(e);
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("Could not load Resource " + getResourceIdentifier() + ": failed to reload after conversion!");
							}
							throw exception;
						} finally {
							if (project != null) {
								project.unholdObjectRegistration();
							}
						}
						triedVersion = cmv.toVersion;
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Conversion FAILED: succeeding to load Resource " + getResourceIdentifier()
									+ " with model version " + triedVersion + " but this requires a conversion from version "
									+ triedVersion + " to version " + cmv.toVersion + " which seem to be not implemented.");
						}
						/*
						 * try { backwardSynchronizeWith(newerResourcesToSynchronizeWith); } catch (FlexoException e1) { if (exception ==
						 * null) { exception = new LoadXMLResourceException(this); } exception.addLoadException(new
						 * XMLOperationException(e1, triedVersion)); }
						 */
						return returned;
					}
				}

				if (!triedVersion.equals(latestVersion())) {
					isConverting = true;
					if (project != null) {
						project.holdObjectRegistration();
					}
				} else {
					isConverting = false;
				}
				try {
					returned = tryToLoadResourceDataWithVersion(triedVersion);
				} catch (JDOMException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Malformed XML File: " + e.getMessage());
					}
					_isLoading = false;
					throw new MalformedXMLException(this, e);
				} catch (XMLOperationException e) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("ERROR:" + "tried version = " + triedVersion.toString());
						e.getException().printStackTrace();
						if (e.getException() instanceof AccessorInvocationException) {
							((AccessorInvocationException) e.getException()).getTargetException().printStackTrace();
							if (((AccessorInvocationException) e.getException()).getTargetException() instanceof Error) {
								throw new LoadXMLResourceException(this, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							}
						}
					}
					if (exception == null) {
						exception = new LoadXMLResourceException(this, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
					}
					exception.addLoadException(e);
				} finally {
					if (!triedVersion.equals(latestVersion()) && project != null) {
						project.unholdObjectRegistration();
					}
				}
				if (returned != null) {
					notCorrectelyDeserialized = false;
				} else {
					if (!loadingHandler.useOlderMappingWhenLoadingFailure(this)) {
						throw new LoadXMLResourceException(this, "Mapping used '" + cmv + "' could not load this resource.");
					}
				}
			}

			if (isDeleted()) {
				return null;
			}

			if (notCorrectelyDeserialized) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Could not load Resource " + getResourceIdentifier() + ": no valid XML model found !");
				}
				_isLoading = false;
				throw exception;
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Found a version to load resource " + getResourceIdentifier());
				}
				_currentVersion = triedVersion;
				FlexoXMLMappings.ClassModelVersion cmv = getXmlMappings().getClassModelVersion(getResourceDataClass(), triedVersion);

				boolean convertToLatestVersion = false;
				if (cmv.needsManualConversion || !triedVersion.equals(latestVersion())) {
					convertToLatestVersion = loadingHandler.upgradeResourceToLatestVersion(this);
				}

				if (cmv.needsManualConversion && convertToLatestVersion) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("This resource " + getResourceIdentifier() + "must be converted from " + triedVersion + " into "
								+ cmv.toVersion);
					}
					_resourceData = returned;
					if (progress != null) {
						progress.setProgress(FlexoLocalization.localizedForKey("converting from version ") + triedVersion + " "
								+ FlexoLocalization.localizedForKey("to") + " " + cmv.toVersion);
					}
					isConverting = true;
					if (convertResourceFileFromVersionToVersion(triedVersion, cmv.toVersion)) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Conversion from " + triedVersion + " into " + cmv.toVersion + " was successfull.");
						}
						// Load again with the new version !
						try {
							if (!cmv.toVersion.equals(latestVersion())) {
								isConverting = true;
								if (project != null) {
									project.holdObjectRegistration();
								}
							} else {
								isConverting = false;
								if (project != null) {
									project.unholdObjectRegistration();
								}
							}
							returned = tryToLoadResourceDataWithVersion(cmv.toVersion);
							requiresRMFileSaving = true;
						} catch (JDOMException e) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Malformed XML File: " + e.getMessage());
							}
							return null;
						} catch (XMLOperationException e) {
							e.printStackTrace();
							if (e.getCause() != null) {
								e.getCause().printStackTrace();
							}
							if (exception == null) {
								exception = new LoadXMLResourceException(this, e.getCause() != null ? e.getCause().getMessage()
										: e.getMessage());
							}
							exception.addLoadException(e);
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("Could not load Resource " + getResourceIdentifier() + ": failed to reload after conversion!");
							}
							throw exception;
						} finally {
							if (!cmv.toVersion.equals(latestVersion()) && project != null) {
								project.unholdObjectRegistration();
							}
						}
						triedVersion = cmv.toVersion;
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Conversion FAILED: succeeding to load Resource " + getResourceIdentifier()
									+ " with model version " + triedVersion + " but this requires a conversion from version "
									+ triedVersion + " to version " + cmv.toVersion + " which seem to be not implemented.");
						}
						/*
						 * try { backwardSynchronizeWith(newerResourcesToSynchronizeWith); } catch (FlexoException e1) { if (exception ==
						 * null) { exception = new LoadXMLResourceException(this); } exception.addLoadException(new
						 * XMLOperationException(e1, triedVersion)); }
						 */
						return returned;
					}
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Succeeding to load Resource " + getResourceIdentifier() + " with model version " + triedVersion);
				}
				_resourceData = returned;
				if (!triedVersion.equals(latestVersion()) && convertToLatestVersion) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Converting Resource " + getResourceIdentifier() + " to latest version " + latestVersion());
					}
					if (progress != null) {
						progress.setProgress(FlexoLocalization.localizedForKey("converting from version ") + triedVersion + " "
								+ FlexoLocalization.localizedForKey("to") + " " + latestVersion());
					}
					isConverting = true;
					if (project != null) {
						project.holdObjectRegistration();
					}
					if (incrementalConversionFromVersionToVersion(triedVersion, latestVersion())) {
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Conversion from " + triedVersion + " into latest version :" + latestVersion() + " was successful.");
						}
						try {
							isConverting = false;
							if (project != null) {
								project.unholdObjectRegistration();
							}
							returned = tryToLoadResourceDataWithVersion(latestVersion());
						} catch (Exception exce) {
							exce.printStackTrace();
						}
						_resourceData = returned;
						_isLoading = false;
						_currentVersion = latestVersion();
					} else {
						if (project != null) {
							project.unholdObjectRegistration();
						}
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Conversion from " + triedVersion + " into latest version : " + latestVersion() + " FAILED.");
						}
					}

					try {
						if (progress != null) {
							progress.setProgress(FlexoLocalization.localizedForKey("saving") + " " + getName());
						}
						saveResourceData();
						requiresRMFileSaving = true;
					} catch (SaveXMLResourceException e) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.log(Level.SEVERE, "Could not load Resource " + getResourceIdentifier()
									+ ": failed to convert to new version !", e);
						}
						throw new LoadXMLResourceException(this, e.getMessage());
					} catch (SaveResourcePermissionDeniedException e) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.log(Level.SEVERE, "Could not load Resource " + getResourceIdentifier()
									+ ": failed to convert to new version because file is read-only !", e);
						}
						throw new LoadXMLResourceException(this, e.getMessage());
					} catch (SaveResourceException e) {
						if (logger.isLoggable(Level.SEVERE)) {
							logger.log(Level.SEVERE, "Could not load Resource " + getResourceIdentifier()
									+ ": failed to convert to new version !", e);
						}
						throw new LoadXMLResourceException(this, e.getMessage());
					}
				} else {
					// _currentVersion has normally been set while converting during saveResourceData()
					_currentVersion = triedVersion;
				}

				// _currentVersion = triedVersion;
				if (requiresRMFileSaving) {
					try {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Save RM file.");
						}
						if (this instanceof FlexoRMResource) {
							this.saveResourceData(true);
						} else {
							getProject().getFlexoXMLFileResource().saveResourceData(true);
						}
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("RM file saving succeeded.");
						}
					} catch (SaveResourceException e1) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Could not save RM file: see logs for details.");
						}
						e1.printStackTrace();
						throw new LoadXMLResourceException(this, e1.getMessage());
					}
				}
			}
			_resourceData = returned;
			_isLoading = false;
			try {
				_resourceData.setFlexoResource(this);
			} catch (DuplicateResourceException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
			/*
			 * try { backwardSynchronizeWith(newerResourcesToSynchronizeWith); } catch (FlexoException e1) { if (exception == null) {
			 * exception = new LoadXMLResourceException(this); } exception.addLoadException(new XMLOperationException(e1, _currentVersion)); }
			 */
			return returned;
		} finally {
			_isLoading = false;
			isConverting = false;
			if (project != null && projectWasHoldingObjectRegistration) {
				project.holdObjectRegistration();
			}
		}
	}

	/**
	 * Converts incrementally this resource from fromVersion to toVersion. Everytime the ClassModelVersion requires a manual conversion, the
	 * converter will call the convert method
	 * 
	 * @param fromVersion
	 * @param toVersion
	 * @return
	 */
	private boolean incrementalConversionFromVersionToVersion(FlexoVersion fromVersion, FlexoVersion toVersion) {
		FlexoVersion[] v = getXmlMappings().getAvailableVersionsForClass(getResourceDataClass());
		int i = 0;
		// Let's find the index of the version in the array
		for (; i < v.length; i++) {
			FlexoVersion version = v[i];
			if (version.equals(fromVersion)) {
				break;
			}
		}
		// We try to convert until toVersion
		for (; i < v.length; i++) {
			if (!v[i].isGreaterThan(toVersion)) {// As long as the current version is smaller than toVersion
				FlexoXMLMappings.ClassModelVersion cmv = getXmlMappings().getClassModelVersion(getResourceDataClass(), v[i]);
				if (cmv.needsManualConversion) {
					if (convertResourceFileFromVersionToVersion(v[i], cmv.toVersion)) {
						_currentVersion = cmv.toVersion;
						if (logger.isLoggable(Level.INFO)) {
							logger.info("Successfully converted resource " + getResourceIdentifier() + " from " + v[i] + " to "
									+ cmv.toVersion);
						}
						while (i + 1 < v.length && !v[i + 1].equals(cmv.toVersion)) {
							i++;
						}
						if (i + 1 == v.length || !v[i + 1].equals(cmv.toVersion)) {
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("This is weird. I tried to convert from " + cmv.version + " to " + cmv.toVersion
										+ " but I can't find that version in the mapping.");
							}
							return false;
						}
					} else {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("FAILED to convert resource " + getResourceIdentifier() + " from " + v[i] + " to "
									+ cmv.toVersion);
						}
					}
				} else {
					_currentVersion = cmv.version;
					try {
						logger.info("Trying to save resource " + getResourceIdentifier() + " with model version " + _currentVersion);
						_saveResourceData(_currentVersion, true);
					} catch (SaveXMLResourceException e) {
						logger.warning("Cound not save with version " + _currentVersion + " " + e);
						e.printStackTrace();
					}
				}
			} else {
				break;// We are done here
			}
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Incremental conversion from " + fromVersion + " to " + toVersion + " performed successfully");
		}
		return true;
	}

	/**
	 * @return
	 */
	protected FlexoXMLMappings getXmlMappings() {
		return getProject().getXmlMappings();
	}

	/**
	 * Save current resource data to current XML resource file.<br>
	 * Forces XML version to be the latest one.
	 * 
	 * @return
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveXMLResourceException, SaveResourcePermissionDeniedException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			_saveResourceData(latestVersion(), clearIsModified);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName() + " with date "
						+ FileUtils.getDiskLastModifiedDate(getFile()));
			}
		}
		if (clearIsModified) {
			try {
				getResourceData().clearIsModified(false);// No need to reset the last memory update since it is valid
				notifyResourceStatusChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void revertToReleaseVersion(FlexoVersion releaseVersion) throws SaveXMLResourceException,
			SaveResourcePermissionDeniedException {
		// 1st, be sure that data are loaded
		getResourceData();
		// Then find version
		final FlexoVersion version = getXmlMappings().getVersionForClassAndRelease(getResourceDataClass(), releaseVersion);
		// And save to this version
		if (version != null) {
			logger.info("Trying to convert " + getResourceIdentifier() + " from " + latestVersion() + " to " + version);
			_currentVersion = version;
			if (!hasWritePermission()) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Permission denied : " + getFile().getAbsolutePath());
				}
				throw new SaveResourcePermissionDeniedException(this);
			}
			if (_resourceData != null) {
				final XMLMapping currentMapping = getXmlMappings().getMappingForClassAndVersion(getResourceDataClass(), latestVersion());
				final XMLMapping revertedMapping = getXmlMappings().getMappingForClassAndVersion(getResourceDataClass(), version);
				_saveResourceData(version, new SerializationHandler() {
					@Override
					public void objectWillBeSerialized(XMLSerializable object) {
						if (object instanceof FlexoModelObject) {
							fillInUnmappedAttributesAsDynamicProperties((FlexoModelObject) object, currentMapping, revertedMapping);
							((FlexoModelObject) object).initializeSerialization();
						}
					}

					@Override
					public void objectHasBeenSerialized(XMLSerializable object) {
						if (object instanceof FlexoModelObject) {
							((FlexoModelObject) object).finalizeSerialization();
						}
					}
				}, true);
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
				}
			}
			_currentVersion = version;
			getResourceData().clearIsModified(false);
		}
	}

	void fillInUnmappedAttributesAsDynamicProperties(FlexoModelObject object, XMLMapping currentMapping, XMLMapping revertedMapping) {
		ModelEntity currentEntity = currentMapping.entityForClass(object.getClass());
		ModelEntity revertedEntity = revertedMapping.entityForClass(object.getClass());
		for (Enumeration<ModelProperty> en = currentEntity.getModelProperties(); en.hasMoreElements();) {
			ModelProperty p = en.nextElement();
			if (p.getIsAttribute()) {
				if (revertedEntity.getModelPropertyWithName(p.getName()) == null) {
					// Found unmapped property
					String value = object.valueForKey(p.getName());
					if (value != null) {
						// logger.info("Object "+object+" found unmapped non-null attribute "+p.getName()+" value="+value);
						object.setDynamicPropertiesForKey(value, p.getName());
					}
				}
			}
		}
	}

	protected synchronized void saveResourceDataWithVersion(FlexoVersion version) throws SaveXMLResourceException,
			SaveResourcePermissionDeniedException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			_saveResourceData(version, true);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
			}
		}
		try {
			_currentVersion = version;
			getResourceData().clearIsModified(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _saveResourceData(FlexoVersion version, boolean clearIsModified) throws SaveXMLResourceException {
		_saveResourceData(version, new SerializationHandler() {
			@Override
			public void objectWillBeSerialized(XMLSerializable object) {
				if (object instanceof FlexoModelObject) {
					((FlexoModelObject) object).initializeSerialization();
				}
			}

			@Override
			public void objectHasBeenSerialized(XMLSerializable object) {
				if (object instanceof FlexoModelObject) {
					((FlexoModelObject) object).finalizeSerialization();
				}
			}
		}, clearIsModified);
	}

	private void _saveResourceData(FlexoVersion version, SerializationHandler handler, boolean clearIsModified)
			throws SaveXMLResourceException {
		File temporaryFile = null;
		FileWritingLock lock = willWriteOnDisk();
		try {
			File dir = getFile().getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// Make local copy
			makeLocalCopy();
			// Using temporary file
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Creating temp file " + temporaryFile.getAbsolutePath());
			}
			try {
				performXMLSerialization(version, handler, temporaryFile);
				// Renaming temporary file
				if (logger.isLoggable(Level.FINE)) {
					logger.finer("Renaming temp file " + temporaryFile.getAbsolutePath() + " to " + getFile().getAbsolutePath());
				}
				// temporaryFile.renameTo(getFile());
				postXMLSerialization(version, temporaryFile, lock, clearIsModified);
			} catch (DuplicateSerializationIdentifierException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Duplicate serialization identifier: " + e.getMessage(), e);
				}
				if (isDuplicateSerializationIdentifierRepairable()) {
					if (repairDuplicateSerializationIdentifier()) {
						performXMLSerialization(version, handler, temporaryFile);
						postXMLSerialization(version, temporaryFile, lock, clearIsModified);
						return;
					}
				}
				hasWrittenOnDisk(lock);
				((FlexoXMLSerializableObject) getResourceData()).finalizeSerialization();
				throw new SaveXMLResourceException(this, e, version);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to save resource " + getResourceIdentifier() + " with model version " + version);
			}
			hasWrittenOnDisk(lock);
			((FlexoXMLSerializableObject) getResourceData()).finalizeSerialization();
			throw new SaveXMLResourceException(this, e, version);
		}
	}

	/**
	 * @param version
	 * @param temporaryFile
	 * @param lock
	 * @param clearIsModified
	 * @throws IOException
	 */
	private void postXMLSerialization(FlexoVersion version, File temporaryFile, FileWritingLock lock, boolean clearIsModified)
			throws IOException {
		FileUtils.rename(temporaryFile, getFile());
		hasWrittenOnDisk(lock);
		((FlexoXMLSerializableObject) getResourceData()).finalizeSerialization();
		_currentVersion = version;
		if (clearIsModified) {
			notifyResourceStatusChanged();
		}
	}

	/**
	 * @param version
	 * @param handler
	 * @param temporaryFile
	 * @throws FileNotFoundException
	 * @throws InvalidObjectSpecificationException
	 * @throws InvalidModelException
	 * @throws AccessorInvocationException
	 * @throws DuplicateSerializationIdentifierException
	 * @throws IOException
	 */
	private void performXMLSerialization(FlexoVersion version, SerializationHandler handler, File temporaryFile)
			throws FileNotFoundException, InvalidObjectSpecificationException, InvalidModelException, AccessorInvocationException,
			DuplicateSerializationIdentifierException, IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(temporaryFile);
			FlexoXMLSerializableObject dataToSerialize = (FlexoXMLSerializableObject) getResourceData();
			dataToSerialize.initializeSerialization();
			XMLCoder.encodeObjectWithMapping(dataToSerialize, getXmlMappings()
					.getMappingForClassAndVersion(getResourceDataClass(), version), out, getProject().getStringEncoder(), handler);
			dataToSerialize.finalizeSerialization();
			out.flush();
			out.close();
			out = null;
		} finally {
			if (out != null) {
				out.close();
			}
			out = null;
		}
	}

	protected abstract boolean isDuplicateSerializationIdentifierRepairable();

	protected abstract boolean repairDuplicateSerializationIdentifier();

	private void makeLocalCopy() throws IOException {
		if (getFile() != null && getFile().exists()) {
			String localCopyName = getFile().getName() + "~";
			File localCopy = new File(getFile().getParentFile(), localCopyName);
			FileUtils.copyFileToFile(getFile(), localCopy);

		}
	}

	protected XMLRD tryToLoadResourceDataWithVersion(FlexoVersion version) throws XMLOperationException, JDOMException {
		XMLRD returned = null;
		try {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Trying to load " + getResourceDataClass().getName() + " with model version " + version);
			}
			XMLMapping mapping = getXmlMappings().getMappingForClassAndVersion(getResourceDataClass(), version);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Model version " + version + " has been loaded.");
			}

			if (hasBuilder() && mapping.hasBuilderClass()) {
				if (getProject() != null) {
					returned = (XMLRD) XMLDecoder.decodeObjectWithMapping(new FileInputStream(getFile()), mapping, instanciateNewBuilder(),
							getProject().getStringEncoder());
				} else {
					if (!(this instanceof FlexoRMResource)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Project is not set on " + this.getFullyQualifiedName());
						}
					}
					returned = (XMLRD) XMLDecoder.decodeObjectWithMapping(new FileInputStream(getFile()), mapping, instanciateNewBuilder());
				}
			} else {
				if (getProject() != null) {
					returned = (XMLRD) XMLDecoder.decodeObjectWithMapping(new FileInputStream(getFile()), mapping, null, getProject()
							.getStringEncoder());
				} else {
					if (!(this instanceof FlexoRMResource)) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Project is not set on " + this.getFullyQualifiedName());
						}
					}
					returned = (XMLRD) XMLDecoder.decodeObjectWithMapping(new FileInputStream(getFile()), mapping, null, getProject()
							.getStringEncoder());
				}
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Succeeded loading " + getResourceDataClass().getName() + " with model version " + version);
			}
			if (returned != null) {
				returned.setFlexoResource(this);
			}
			return returned;
		} catch (AccessorInvocationException e) {
			if (logger.isLoggable(Level.FINE)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("FAILED loading " + getResourceDataClass().getName() + " with model version " + version + " Exception: "
							+ e.getTargetException().getMessage());
				}
			}
			if (logger.isLoggable(Level.FINER)) {
				e.getTargetException().printStackTrace();
			}
			e.printStackTrace();
			throw new XMLOperationException(e, version);
		} catch (Exception e) {
			if (logger.isLoggable(Level.FINE)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("FAILED loading " + getResourceDataClass().getName() + " with model version " + version + " Exception: "
							+ e.getMessage());
				}
			}
			if (logger.isLoggable(Level.FINEST)) {
				e.printStackTrace();
			}
			// e.printStackTrace();
			throw new XMLOperationException(e, version);
		}
	}

	public FlexoVersion latestVersion() {
		return getXmlMappings().getLatestVersionForClass(getResourceDataClass());
	}

	public FlexoVersion getXmlVersion() {
		return _currentVersion;
	}

	public void setXmlVersion(FlexoVersion version) {
		_currentVersion = version;
	}

	public XMLMapping getCurrentMapping() {
		return getXmlMappings().getMappingForClassAndVersion(getResourceDataClass(), _currentVersion);
	}

	public String getResourceXMLRepresentation() throws XMLOperationException {
		try {
			return XMLCoder.encodeObjectWithMapping(getXMLResourceData(), getCurrentMapping(), getProject().getStringEncoder(), null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLOperationException(e, _currentVersion);
		}
	}

	/**
	 * Return data related to this resource, as an instance of an object implementing
	 * 
	 * @see org.openflexo.foundation.rm.XMLStorageResourceData
	 * @return
	 */
	public XMLStorageResourceData getXMLResourceData() {
		try {
			return getResourceData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This methods only warns and does nothing, and must be overriden in
	 * subclasses !
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was sucessfull
	 */
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Unable to find converter for resource " + getResourceIdentifier() + " from version " + v1 + " to version " + v2);
		}
		return false;
	}

	public abstract Class<XMLRD> getResourceDataClass();

	/**
	 * Returns a boolean indicating if this resource needs a builder to be loaded Returns false and thus must be overriden in subclasses
	 * 
	 * @return boolean
	 */
	public boolean hasBuilder() {
		return false;
	}

	/**
	 * Returns the required newly instancied builder if this resource needs a builder to be loaded
	 * 
	 * @return boolean
	 */
	public abstract Object instanciateNewBuilder();

	/**
	 * Thrown when an exception was raised during XML operation try with a specified version
	 * 
	 * @author sguerin
	 * 
	 */
	public static class XMLOperationException extends FlexoException {

		private Exception exception;

		private FlexoVersion version;

		public XMLOperationException(Exception exception, FlexoVersion version) {
			super();
			this.exception = exception;
			this.version = version;
		}

		public FlexoVersion getVersion() {
			return version;
		}

		public Exception getException() {
			return exception;
		}

		@Override
		public String getMessage() {
			return "XMLOperationException caused by " + exception.getClass().getName() + " : " + exception.getMessage();
		}
	}

	/**
	 * Thrown when an exception was raised during saving
	 * 
	 * @author sguerin
	 * 
	 */
	public static class SaveXMLResourceException extends SaveResourceException {

		private XMLOperationException exception;

		public SaveXMLResourceException(FlexoXMLStorageResource thisResource, Exception exception, FlexoVersion version) {
			super(thisResource);
			this.exception = new XMLOperationException(exception, version);
		}

		@Override
		public String getMessage() {
			return "SaveXMLResourceException caused by : " + exception.getMessage();
		}
	}

	/**
	 * Thrown when an exception was raised during loading
	 * 
	 * @author sguerin
	 * 
	 */
	public static class LoadXMLResourceException extends LoadResourceException {

		/**
		 * Vector of LoadResourceWithVersionException
		 */
		private List<XMLOperationException> loadResourceExceptions;

		public LoadXMLResourceException(FlexoXMLStorageResource thisResource, String message) {
			super(thisResource, message);
			loadResourceExceptions = new Vector<FlexoXMLStorageResource.XMLOperationException>();
		}

		@Deprecated
		public LoadXMLResourceException(FlexoXMLStorageResource thisResource) {
			this(thisResource, (String) null);
		}

		@Deprecated
		public LoadXMLResourceException(FlexoXMLStorageResource thisResource, LoadResourceException exception) {
			this(thisResource);
		}

		public void addLoadException(XMLOperationException exception) {
			loadResourceExceptions.add(exception);
		}

		@Override
		public String getMessage() {
			String returned = "LoadXMLResourceException caused by multiple exceptions:\n";
			for (XMLOperationException temp : loadResourceExceptions) {
				returned += "Trying to load with version " + temp.getVersion() + " exception raised: " + temp.getMessage() + "\n";
				temp.exception.printStackTrace();
			}
			return returned;
		}

		public String getExtendedMessage() {
			String returned = "LoadXMLResourceException caused by multiple exceptions:\n";
			for (XMLOperationException temp : loadResourceExceptions) {
				returned += "Trying to load with version " + temp.getVersion() + " exception raised: " + temp.getMessage() + "\n";
				returned += "StackTrace:\n";
				if (temp.getException().getStackTrace() != null) {
					for (int i = 0; i < temp.getException().getStackTrace().length; i++) {
						returned += "\tat " + temp.getException().getStackTrace()[i] + "\n";
					}
				}
				if (temp.getException() instanceof AccessorInvocationException) {
					returned += "Caused by :\n";
					for (int i = 0; i < ((AccessorInvocationException) temp.getException()).getTargetException().getStackTrace().length; i++) {
						returned += "\tat " + ((AccessorInvocationException) temp.getException()).getTargetException().getStackTrace()[i]
								+ "\n";
					}
				}
			}
			return returned;
		}
	}

	/**
	 * Thrown when an exception was raised during loading
	 * 
	 * @author sguerin
	 * 
	 */
	public static class MalformedXMLException extends LoadResourceException {

		private JDOMException jDOMException;

		public MalformedXMLException(FlexoXMLStorageResource thisResource, JDOMException exception) {
			super(thisResource, null);
			jDOMException = exception;
		}

		@Override
		public String getMessage() {
			return jDOMException.getMessage();
		}

	}

	public boolean getIsLoading() {
		return _isLoading;
	}

	public boolean isConverting() {
		return isConverting;
	}

}
