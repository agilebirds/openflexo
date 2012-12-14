package org.openflexo.foundation.resource;


/**
 * Default implementation for {@link FlexoFileResource}
 * 
 * Very first draft for implementation, only implements get/load scheme
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class FlexoXMLFileResourceImpl<RD extends ResourceData<RD>> extends FlexoFileResourceImpl<RD> implements
		FlexoXMLFileResource<RD> {

	/**
	 * Load resource data by applying a special scheme handling XML versionning, ie to find right XML version of current resource file.<br>
	 * If version of stored file is not conform to latest declared version, convert resource file and update it to latest version.
	 * 
	 * @throws ProjectLoadingCancelledException
	 * @throws MalformedXMLException
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#loadResourceData()
	 */
	/*public FlexoVersion getXmlVersion() {
		return _currentVersion;
	}

	public void setXmlVersion(FlexoVersion version) {
		_currentVersion = version;
	}

	@Override
	public XMLRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException,
			FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException {
		if (_resourceData != null) {
			// already loaded
			return _resourceData;
		}
		if (!isLoadable()) {
			return null;
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
						&& !(this instanceof FlexoComponentLibraryResource) ) {
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
			return returned;
		} finally {
			_isLoading = false;
			isConverting = false;
			if (project != null && projectWasHoldingObjectRegistration) {
				project.holdObjectRegistration();
			}
		}
	}

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

	protected FlexoXMLMappings getXmlMappings() {
		return getProject().getXmlMappings();
	}*/

	/**
	 * Converts incrementally this resource from fromVersion to toVersion. Everytime the ClassModelVersion requires a manual conversion, the
	 * converter will call the convert method
	 * 
	 * @param fromVersion
	 * @param toVersion
	 * @return
	 */

}
