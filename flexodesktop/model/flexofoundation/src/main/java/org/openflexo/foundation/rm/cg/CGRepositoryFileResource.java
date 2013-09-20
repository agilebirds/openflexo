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
package org.openflexo.foundation.rm.cg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.dm.CGContentRegenerated;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.generator.IGenerationException;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.validation.Validable;

/**
 * @author sylvain
 * 
 */
public abstract class CGRepositoryFileResource<GRD extends GeneratedResourceData, G extends IFlexoResourceGenerator, F extends CGFile>
		extends FlexoGeneratedResource<GRD> {

	private static final Logger logger = Logger.getLogger(CGRepositoryFileResource.class.getPackage().getName());

	private F _cgFile;
	private String _name;

	private boolean _forceRegenerate = false;

	private Date _lastAcceptingDate;
	private Date _lastGenerationCheckedDate;

	private File _lastGeneratedFile;
	private File _lastAcceptedFile;

	private boolean generationChangedContent = false;

	private G _generator;

	// final only to ensure that it wasn't overrided somewhere
	// but if there is a real need to override it : final modifier may be removed
	final public G getGenerator() {
		return _generator;
	}

	// final only to ensure that it wasn't overrided somewhere
	// but if there is a real need to override it : final modifier may be removed
	final public void setGenerator(G generator) {
		if (_generator == generator) {
			return;
		}
		Vector<CGTemplate> templates = null;
		if (_generator != null) {
			templates = _generator.getUsedTemplates();
			_generator.removeFromGeneratedResourcesGeneratedByThisGenerator(this);
		}
		_generator = generator;
		if (generator != null) {
			generator.addToGeneratedResourcesGeneratedByThisGenerator(this);
			if (getCGFile() != null) {
				generator.addObserver(getCGFile());
				if (templates == null) {
					templates = new Vector<CGTemplate>();
				}
				if (templates.size() == 0) {
					for (String templateRelativePath : getCGFile().getUsedTemplates()) {
						CGTemplate file;
						if (getCGFile().getRepository().getPreferredTemplateRepository() != null) {
							file = getCGFile().getRepository().getPreferredTemplateRepository()
									.getTemplateWithRelativePath(templateRelativePath);
						} else {
							file = getCGFile().getGeneratedCode().getTemplates().getApplicationRepository()
									.getTemplateWithRelativePath(templateRelativePath);
						}
						if (file != null) {
							templates.add(file);
						}
					}
				}
				generator.setUsedTemplates(templates);
				if (generator.getTemplateLocator() != null) {
					addToDependentResources(generator.getTemplateLocator());
				}
			}
		}
	}

	@Override
	public synchronized boolean delete(boolean deleteFile) {
		if (getGenerator() != null && getCGFile() != null) {
			getGenerator().deleteObserver(getCGFile());
		}
		return super.delete(deleteFile);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String aName) {
		_name = aName;
	}

	/**
	 * @param builder
	 */
	public CGRepositoryFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public CGRepositoryFileResource(FlexoProject aProject) {
		super(aProject, aProject.getServiceManager());
	}

	public GenerationRepository getRepository() {
		if (getCGFile() != null) {
			return getCGFile().getRepository();
		}
		return null;
	}

	public final F getCGFile() {
		return _cgFile;
	}

	public final void setCGFile(F cgFile) {
		if (getCGFile() != cgFile && cgFile != null && getGenerator() != null) {
			getGenerator().addObserver(cgFile);
		}
		_cgFile = cgFile;
	}

	public FlexoProjectFile makeFlexoProjectFile(String relativePath, String name) {
		if (relativePath == null) {
			relativePath = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getCGFile().getSymbolicDirectory().getDirectory().getRelativePath());
		addPathFrom(sb, extractSignificantPathFrom(sb.toString(), relativePath));
		addPathFrom(sb, name);
		String pathFromRepository = sb.toString();

		return new FlexoProjectFile(getProject(), getCGFile().getRepository().getSourceCodeRepository(), pathFromRepository);
	}

	private void addPathFrom(StringBuffer result, String aPath) {
		aPath = aPath.replace('\\', '/');
		StringTokenizer st = new StringTokenizer(aPath, "/");
		while (st.hasMoreElements()) {
			String next = st.nextToken();
			if (result.toString().endsWith("/")) {
				result.append(next);
			} else {
				result.append("/" + next);
			}
		}
	}

	private String extractSignificantPathFrom(String previousPath, String aPath) {
		previousPath = previousPath.replace('\\', '/');
		aPath = aPath.replace('\\', '/');
		Vector<String> path1 = new Vector<String>();
		StringTokenizer st = new StringTokenizer(previousPath, "/");
		while (st.hasMoreElements()) {
			path1.add(st.nextToken());
		}
		Vector<String> path2 = new Vector<String>();
		st = new StringTokenizer(aPath, "/");
		while (st.hasMoreElements()) {
			path2.add(st.nextToken());
		}
		int removeThat = 0;
		while (removeThat < path1.size() && removeThat < path2.size() && path1.get(removeThat).equals(path2.get(removeThat))) {
			removeThat++;
		}
		StringBuffer returned = new StringBuffer();
		boolean isFirst = true;
		for (int i = removeThat; i < path2.size(); i++) {
			returned.append((!isFirst ? "/" : "") + path2.get(i));
			isFirst = false;
		}
		return returned.toString();
	}

	public Date getLastAcceptingDate() {
		if (_lastAcceptingDate == null || getLastGenerationDate().getTime() > _lastAcceptingDate.getTime()) {
			_lastAcceptingDate = getLastGenerationDate();
		}
		return _lastAcceptingDate;
	}

	public void setLastAcceptingDate(Date aDate) {
		_lastAcceptingDate = aDate;
	}

	public Date getLastGenerationCheckedDate() {
		if (_lastGenerationCheckedDate == null || getLastGenerationDate().getTime() > _lastGenerationCheckedDate.getTime() || isConnected()
				&& !getFile().exists()) {
			_lastGenerationCheckedDate = getLastGenerationDate();
		}
		return _lastGenerationCheckedDate;
	}

	public void setLastGenerationCheckedDate(Date aDate) {
		_lastGenerationCheckedDate = aDate;
	}

	public Date getMemoryLastGenerationDate() {
		IFlexoResourceGenerator generator = getGenerator();
		if (generator != null) {
			return generator.getMemoryLastGenerationDate();
		}
		return null;
	}

	/**
	 * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager to compute dependancies between resources.
	 * This method returns the date that must be considered as last known update for this resource
	 * 
	 * The date to be considered for generated resources are typically the date when this resource was generated for the last time BUT here,
	 * we override this basic scheme by maintaining an other date which correspond to the date when this resource has been checked and
	 * declared unchanged compared to the version on the disk (see DismissUnchangedGeneratedFiles).
	 * 
	 * @return a Date object
	 */
	@Override
	public final Date getLastUpdate() {
		if (_memoryUpdateComputation) {
			return getMemoryLastGenerationDate();
		}
		return getLastGenerationCheckedDate();
	}

	private GenerationStatus retrieveGenerationStatus() {
		boolean diskUpdate = getDiskLastModifiedDate().getTime() > getLastAcceptingDate().getTime() + ACCEPTABLE_FS_DELAY;
		if (logger.isLoggable(Level.FINE) && diskUpdate) {
			logger.fine("fileName=" + getFileName());
			logger.fine("getLastGenerationDate()[" + new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastGenerationDate()) + "]");
			logger.fine("getLastAcceptingDate()[" + new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastAcceptingDate()) + "]"
					+ " < getDiskLastModifiedDate()[" + new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getDiskLastModifiedDate()) + "]");
		}

		if (getGenerator() != null) {
			if (hasGenerationError()) {
				return GenerationStatus.GenerationError;
			} else if (getGenerator().getGeneratedCode() == null) {
				return GenerationStatus.NotGenerated;
			}
		}
		if (!(this instanceof GenerationAvailableFileResourceInterface)) {
			return GenerationStatus.CodeGenerationNotAvailable;
		} else if (getCGFile() == null || getFile() == null) {
			return GenerationStatus.Unknown;
		} else if (getCGFile().isMarkedForDeletion()) {
			return GenerationStatus.GenerationRemoved;
		} else if (!isCodeGenerationAvailable()) {
			return GenerationStatus.CodeGenerationNotSynchronized;
		} else if (getCGFile().isOverrideScheduled()) {
			return GenerationStatus.OverrideScheduled;
		} else if (!getFile().exists() || needsGeneration()) {
			if (diskUpdate && getProject() != null && getProject().isComputeDiff()) {
				return getCGFile().isMarkedAsMerged() ? GenerationStatus.ConflictingMarkedAsMerged : GenerationStatus.ConflictingUnMerged;
			} else {
				if (!getFile().exists()) {
					return GenerationStatus.GenerationAdded;
				} else {
					if (getProject() != null && getProject().isComputeDiff() && getCGFile().isGenerationConflicting()) {
						return getCGFile().isMarkedAsMerged() ? GenerationStatus.ConflictingMarkedAsMerged
								: GenerationStatus.ConflictingUnMerged;
					} else {
						return GenerationStatus.GenerationModified;
					}
				}
			}
		} else {
			if (diskUpdate) {
				if (getFile().exists()) {
					return GenerationStatus.DiskModified;
				} else {
					return GenerationStatus.DiskRemoved;
				}
			}
			return GenerationStatus.UpToDate;
		}

	}

	private GenerationStatus previousGenerationStatus;

	public final GenerationStatus getGenerationStatus() {
		GenerationStatus returnedStatus = retrieveGenerationStatus();
		if (returnedStatus != previousGenerationStatus) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("File " + getFileName() + " changed status from " + previousGenerationStatus + " to " + returnedStatus);
			}
			GenerationStatus old = previousGenerationStatus;
			previousGenerationStatus = returnedStatus;
			if (getCGFile() != null) {
				getCGFile().notifyGenerationStatusChange(old, returnedStatus);
				if (getCGFile().getRepository() != null) {
					getCGFile().getRepository().refresh();
				}
			}
		}
		return returnedStatus;
	}

	/**
	 * Overrides addToDependantResources
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#addToDependentResources(org.openflexo.foundation.rm.FlexoResource)
	 */
	/*    @Override
	    public void addToDependantResources(FlexoResource aDependantResource)
	    {
	        super.addToDependantResources(aDependantResource);
	        //getGenerationStatus();
	    }*/

	private String _resourceClassName;

	public String _getResourceClassName() {
		if (_resourceClassName != null) {
			return _resourceClassName;
		} else {
			return this.getClass().getName();
		}
	}

	public void _setResourceClassName(String aClassName) {
		_resourceClassName = aClassName;
	}

	public File getLastGeneratedFile() {
		if (_lastGeneratedFile == null && getCGFile() != null) {
			_lastGeneratedFile = new File(getCGFile().getRepository().getCodeGenerationWorkingDirectory(), getResourceFile()
					.getRelativePath() + ".LAST_GENERATED");
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("_lastGeneratedFile" + _lastGeneratedFile.getAbsolutePath());
			}
		}
		return _lastGeneratedFile;
	}

	public File getLastAcceptedFile() {
		if (_lastAcceptedFile == null && getCGFile() != null) {
			_lastAcceptedFile = new File(getCGFile().getRepository().getCodeGenerationWorkingDirectory(), getResourceFile()
					.getRelativePath() + ".LAST_ACCEPTED");
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("_lastAcceptedFile" + _lastAcceptedFile.getAbsolutePath());
			}
		}
		return _lastAcceptedFile;
	}

	@Override
	public final boolean needsGeneration() {
		// logger.info("File: "+getFileName()+" super.needsGeneration()="+super.needsGeneration());
		// logger.info("File: "+getFileName()+" generator="+(getGenerator() != null &&
		// getGenerator().needsRegenerationBecauseOfTemplateUpdated(getLastUpdate())));
		// logger.info("File: "+getFileName()+" _forceRegenerate="+_forceRegenerate);
		// Take care that because of template updating, memory generation can be up-to-date
		// while disk writing is still required
		boolean returned = generationChangedContent || super.needsGeneration() || getGenerator() != null
				&& getGenerator().needsRegenerationBecauseOfTemplateUpdated(getLastUpdate()) || _forceRegenerate;
		// We do this here, otherwise retrieveNeedsMemoryGenerationFlag may override this
		// needsUpdateReason = super.getNeedsUpdateReason();
		return returned;
	}

	private boolean previousNeedsMemoryGeneration = false;

	/**
	 * Returns a flag indicating if generation in memory needs to be re-run. Returns true if generator is not null and if result coded by
	 * the generator is not up-to-date and therefore generator must be re-run. To compute this, use the RM.
	 * 
	 * @return
	 */
	public boolean needsMemoryGeneration() {
		if (getGenerator() == null) {
			return false;
		}

		boolean returnedFlag = getGenerator().needsGeneration();

		if (returnedFlag != previousNeedsMemoryGeneration) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("File " + getFileName() + " changed needsMemoryGeneration status");
			}
			previousNeedsMemoryGeneration = returnedFlag;
			if (getCGFile() != null) {
				getCGFile().notifyGenerationStatusChange(null, null);
				getCGFile().getRepository().refresh();
			}
		}

		return returnedFlag;
	}

	/**
	 * Returns a flag indicating if generation in memory needs to be re-run. Returns true if generator is not null and if result coded by
	 * the generator is not up-to-date and therefore generator must be re-run. To compute this, use the RM.
	 * 
	 * @return
	 */
	public boolean retrieveNeedsMemoryGenerationFlag() {

		if (getGenerator() == null) {
			return false;
		}
		try {
			IFlexoResourceGenerator generator = getGenerator();
			/*if (generator.needsGeneration()) return true;*/
			Date lastTimeItWasGenerated = generator.getMemoryLastGenerationDate();
			// OK, from this point are some explanations required:
			// Since we use the same dependancies here to perform 2 computations (needsUpdate - on disk - and
			// needsUpdate - on memory - we have here to change initial request date, and we must use
			// the last time it was generated given by the generator itself.
			// But this is not enough because internal RM scheme also uses
			// getLastSynchronizedWithResource(FlexoResource) method (backward synchronization scheme).
			// So we also must override this method by returning lastTimeItWasGenerated
			// in the special case of "memory-needs-update" computation
			// Hope you understand what i mean..
			// 07/12/2006 / Sylvain
			getDependentResources().update(); // Clears the dependancy cache
			_memoryUpdateComputation = true;
			boolean returned = needsUpdate();
			_memoryUpdateComputation = false;
			getDependentResources().update();// Clears the dependancy cache
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Resource " + getFileName() + " lastTimeItWasGenerated="
						+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(lastTimeItWasGenerated) + " returns " + returned + " reason "
						+ getNeedsUpdateReason());
			}
			return returned;
		} catch (ResourceDependencyLoopException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Loop found in dependant resources of " + this + "!", e);
			}
			return false;
		}
	}

	private boolean _memoryUpdateComputation = false;

	public final boolean isCodeGenerationAvailable() {
		return getGenerator() != null;
	}

	@Override
	public boolean ensureGenerationIsUpToDate() throws FlexoException {
		if (getGenerationException() == null) {
			getDependentResources().update();
			return super.ensureGenerationIsUpToDate();
		}
		return false;
	}

	public final IGenerationException getGenerationException() {
		if (isCodeGenerationAvailable()) {
			return getGenerator().getGenerationException();
		}
		return null;
	}

	public boolean hasGenerationError() {
		return getGenerationException() != null;
	}

	public abstract void saveEditedVersion(FileContentEditor editor) throws SaveResourceException;

	public void notifyRegenerated(CGContentRegenerated notification) {
		if (getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			((AbstractGeneratedFile) getGeneratedResourceData()).notifyRegenerated(notification);
			generationChangedContent = !((AbstractGeneratedFile) getGeneratedResourceData()).doesGenerationKeepFileUnchanged();
		}
	}

	public void notifyResourceDismissal() {
		generationChangedContent = false;
	}

	public void notifyResourceHasBeenWritten() {
		generationChangedContent = false;
	}

	public void notifyResourceChangedOnDisk() {
		if (isConnected()) {
			if (getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
				((AbstractGeneratedFile) getGeneratedResourceData()).notifyVersionChangedOnDisk();
			}
			if (getCGFile() != null) {
				getCGFile().notifyResourceChangedOnDisk();
			}
		}
	}

	public boolean isForceRegenerate() {
		return _forceRegenerate;
	}

	public void setForceRegenerate(boolean forceRegenerate) {
		_forceRegenerate = forceRegenerate;
	}

	/**
	 * This method is intended to be overidden by sub-classes that need to free resources and data.
	 * 
	 */
	@Override
	public void finalizeGeneration() {
		super.finalizeGeneration();
		_forceRegenerate = false;
	}

	public void acceptDiskVersion() throws SaveGeneratedResourceIOException {
		if (getGeneratedResourceData() != null && getGeneratedResourceData() instanceof AbstractGeneratedFile) {
			setLastAcceptingDate(new Date());
			((AbstractGeneratedFile) getGeneratedResourceData()).acceptDiskVersion();
		}
	}

	public boolean doesGenerationKeepFileUnchanged() {
		if (getGeneratedResourceData() != null
				&& getGeneratedResourceData() instanceof AbstractGeneratedFile
				&& (getGenerationStatus() == GenerationStatus.GenerationModified || getGenerationStatus() == GenerationStatus.OverrideScheduled)) {
			return ((AbstractGeneratedFile) getGeneratedResourceData()).doesGenerationKeepFileUnchanged();
		}
		// logger.info("getGeneratedResourceData()="+getGeneratedResourceData());
		// logger.info("getGenerationStatus()="+getGenerationStatus());
		return false;
	}

	/*	private String needsUpdateReason;

	    /**
	     * debug
	     * @return
	     */
	/*    @Override
	    public String getNeedsUpdateReason()
	    {
	    	return needsUpdateReason;
	    }
	*/
	/**
	 * Overrides renameFileTo
	 * 
	 * @throws IOException
	 * 
	 * @see org.openflexo.foundation.rm.FlexoFileResource#renameFileTo(java.lang.String)
	 */
	@Override
	public boolean renameFileTo(String name) throws InvalidFileNameException, IOException {
		String old = getFileName();
		boolean succeed = super.renameFileTo(name);
		if (succeed && getCGFile() != null) {
			getCGFile().notifyFileNameChanged(old, name);
		}
		return succeed;
	}

	@Override
	protected void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependencyLoopException, FlexoException,
			FileNotFoundException {
		if (getGenerator() != null && (needsMemoryGeneration() || isForceRegenerate())) {
			getGenerator().generate(isForceRegenerate());
		}
		super.performUpdating(updatedResources);
	}

	/**
     *
     */
	public void getDependantResourcesUpToDate() {
		try {
			FlexoResourceTree updatedResources = performUpdateDependanciesModel(new Vector<FlexoResource<FlexoResourceData>>());
			if (!updatedResources.isEmpty()) {
				for (Enumeration<FlexoResource<FlexoResourceData>> e = getDependentResources().elements(false,
						getProject().getDependancyScheme()); e.hasMoreElements();) {
					FlexoResource<FlexoResourceData> resource = e.nextElement();
					resource.update();
				}
			}
		} catch (LoadResourceException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Load resource exception.", e);
			}
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "File not found exception.", e);
			}
		} catch (ProjectLoadingCancelledException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Project loading cancel exception.", e);
			}
		} catch (ResourceDependencyLoopException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Loop in dependancies exception.", e);
			}
		} catch (FlexoException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Flexo exception.", e);
			}
		}
	}

	private boolean isNotifiedDependantResourceChange = false;

	@Override
	public void notifyDependantResourceChange(FlexoResource origin) {
		if (!isNotifiedDependantResourceChange && isActive()) {
			isNotifiedDependantResourceChange = true;
			super.notifyDependantResourceChange(origin);
			if (isConnected()) {
				if (getCGFile() != null && getGenerator() != null) {
					getGenerationStatus();
				}
			}
			isNotifiedDependantResourceChange = false;
		}
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}
}
