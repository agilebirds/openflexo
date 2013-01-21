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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This class represents a Flexo resource. A FlexoResource represent an object handled by Flexo Application Suite (all concerned modules),
 * which could be stored in a File, generally located in related {@link FlexoProject} project directory (see {@link FlexoFileResource}) or
 * simply stored in memory (see {@link FlexoMemoryResource}).
 * 
 * @author sguerin
 */
public abstract class FlexoResource<RD extends FlexoResourceData> extends FlexoObject implements XMLSerializable, Validable {

	private static final FlexoDependantResourceComparator dependancyComparator = new FlexoDependantResourceComparator();

	private static class FlexoDependantResourceComparator implements Comparator<FlexoResource> {

		protected FlexoDependantResourceComparator() {
		}

		@Override
		public int compare(FlexoResource resource1, FlexoResource resource2) {
			return resource1.resourceOrder - resource2.resourceOrder;
		}
	}

	public static void sortResourcesWithDependancies(List<? extends FlexoResource<? extends FlexoResourceData>> resources) {
		// 1. Reset the sort
		for (FlexoResource<? extends FlexoResourceData> r : resources) {
			r.resourceHasBeenSeen = false;
			r.resourceOrder = 0;
		}
		// 2. Resolve order
		for (FlexoResource<? extends FlexoResourceData> r : resources) {
			try {
				r.resolveOrder();
			} catch (ResourceDependencyLoopException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Loop in dependencies found! Will continue but this should not happen.");
				}
			}
		}
		// 3. Do the sort
		Collections.sort(resources, dependancyComparator);
	}

	protected int resourceOrder;

	public int getResourceOrder() {
		return resourceOrder;
	}

	protected boolean resourceHasBeenSeen;

	public int resolveOrder() throws ResourceDependencyLoopException {
		resourceHasBeenSeen = true;
		try {
			int highOrder = -1;
			for (Iterator<FlexoResource<FlexoResourceData>> iter = getDependentResources().iterator(); iter.hasNext();) {
				FlexoResource<FlexoResourceData> r = iter.next();
				if (r.resourceHasBeenSeen) {
					throw new ResourceDependencyLoopException(r);
				} else {
					highOrder = Math.max(highOrder, r.resolveOrder());
				}
			}

			// Set this order so it is one higher than the highest dependency.
			resourceOrder = highOrder + 1;
			return resourceOrder;
		} finally {
			resourceHasBeenSeen = false;
		}
	}

	private static final Logger logger = Logger.getLogger(FlexoResource.class.getPackage().getName());

	protected RD _resourceData;

	protected transient FlexoProject project;

	/**
	 * Vector of FlexoResource which this resource depends on
	 */
	protected DependentResources _dependentResources;

	/**
	 * Vector of FlexoResource which depends on this resource
	 */
	protected AlteredResources _alteredResources;

	/**
	 * Vector of FlexoResource which depends on this resource
	 */
	protected SynchronizedResources _synchronizedResources;

	/**
	 * Hashtable coding the date 'this' resource was lastly backward synchronized with the resource matching related key
	 */
	private Hashtable<FlexoResource<?>, LastSynchronizedWithResourceEntry> _lastSynchronizedForResources;

	/**
	 * Default constructor
	 * 
	 * @param aProject
	 */
	public FlexoResource(FlexoProject aProject) {
		super();
		_lastSynchronizedForResources = new Hashtable<FlexoResource<?>, LastSynchronizedWithResourceEntry>();
		project = aProject;
	}

	public ProjectLoadingHandler getLoadingHandler() {
		if (getProject() != null) {
			return getProject().getLoadingHandler();
		}
		return null;
	}

	public void notifyResourceStatusChanged() {
		if (getProject() != null) {
			getProject().notifyResourceStatusChanged(this);
		}
	}

	protected void notifyResourceChanged() {
		if (getProject() != null) {
			getProject().notifyResourceChanged(this);
		}
	}

	/**
	 * Override this
	 */
	public abstract boolean isToBeSerialized();

	/**
	 * Return project related to this resource
	 * 
	 * @return
	 */
	public final FlexoProject getProject() {
		return project;
	}

	/**
	 * This date is VERY IMPORTANT and CRITICAL since this is the date used by ResourceManager to compute dependancies between resources.
	 * This method returns the date that must be considered as last known update for this resource
	 * 
	 * @return a Date object
	 */
	public abstract Date getLastUpdate();

	@Override
	public String toString() {
		return getResourceIdentifier();
	}

	public void setResourceIdentifier(String anIdentifier) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Setting resource identifier is not allowed !");
		}
	}

	public final String getResourceIdentifier() {
		return getResourceTypeIdentifier() + "." + getName();
	}

	public String getResourceIdentifierForNewName(String newName) {
		return getResourceTypeIdentifier() + "." + newName;
	}

	public final String getResourceTypeIdentifier() {
		if (getResourceType() != null) {
			return getResourceType().getName();
		}
		return null;
	}

	public abstract ResourceType getResourceType();

	public FileFormat getResourceFormat() {
		return getResourceType().getFormat();
	}

	public abstract String getName();

	/**
	 * MUST be overriden in relevant sub-classes !
	 * 
	 * @param aName
	 */
	public void setName(String aName) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could NOT rename this kind of resource: operation not allowed !");
		}
	}

	/**
	 * Rebuild resource dependancies for this resource This method must be overriden, since only RM resource is declared to be in sync with
	 * current resource
	 */
	public void rebuildDependancies() {
		addToSynchronizedResources(getProject().getFlexoRMResource());
	}

	/**
	 * Clear resource dependancies for this resource
	 */
	public void clearDependencies() {
		getDependentResources().clear();
		getAlteredResources().clear();
		getSynchronizedResources().clear();
	}

	// ==========================================================================
	// ======================== Dependant Resources
	// =============================
	// ==========================================================================

	/**
	 * Return the dependantResources
	 * 
	 * @return Returns the dependantResources.
	 */
	public DependentResources getDependentResources() {
		if (_dependentResources == null) {
			_dependentResources = new DependentResources(this);
		}
		return _dependentResources;
	}

	/**
	 * Sets the dependantResources
	 * 
	 * @param dependantResources
	 *            The dependantResources to set.
	 */
	public void setDependentResources(DependentResources resources) {
		_dependentResources = resources;
		resources.setRelatedResource(this);
	}

	/**
	 * Add to dependantResources
	 * 
	 * @param dependantResources
	 *            The dependantResources to add.
	 */
	public void addToDependentResources(FlexoResource aDependantResource) {
		getDependentResources().addToResources(aDependantResource);
	}

	/**
	 * Remove from dependantResources
	 * 
	 * @param dependantResources
	 *            The dependantResources to remove.
	 */
	public void removeFromDependentResources(FlexoResource aDependantResource) {
		getDependentResources().removeFromResources(aDependantResource);
	}

	// ==========================================================================
	// ========================== Altered Resources
	// =============================
	// ==========================================================================

	/**
	 * Return the AlteredResources
	 * 
	 * @return Returns the AlteredResources.
	 */
	public AlteredResources getAlteredResources() {
		if (_alteredResources == null) {
			_alteredResources = new AlteredResources(this);
		}
		return _alteredResources;
	}

	/**
	 * Sets the AlteredResources
	 * 
	 * @param resources
	 *            The AlteredResources to set.
	 */
	public void setAlteredResources(AlteredResources resources) {
		_alteredResources = resources;
		resources.setRelatedResource(this);
	}

	/**
	 * Add to AlteredResources
	 * 
	 * @param aDependingResource
	 *            The FlexoResource to add.
	 */
	public void addToAlteredResources(FlexoResource aDependingResource) {
		getAlteredResources().addToResources(aDependingResource);
	}

	/**
	 * Remove from AlteredResources
	 * 
	 * @param aDependingResource
	 *            The FlexoResource to remove.
	 */
	public void removeFromAlteredResources(FlexoResource aDependingResource) {
		getAlteredResources().removeFromResources(aDependingResource);
	}

	// ==========================================================================
	// ======================== Synchronized Resources
	// ==========================
	// ==========================================================================

	/**
	 * Return the SynchronizedResources
	 * 
	 * @return Returns the SynchronizedResources.
	 */
	public SynchronizedResources getSynchronizedResources() {
		if (_synchronizedResources == null) {
			_synchronizedResources = new SynchronizedResources(this);
		}
		return _synchronizedResources;
	}

	/**
	 * Sets the SynchronizedResources
	 * 
	 * @param resources
	 *            The SynchronizedResources to set.
	 */
	public void setSynchronizedResources(SynchronizedResources resources) {
		_synchronizedResources = resources;
		resources.setRelatedResource(this);
	}

	/**
	 * Add to SynchronizedResources
	 * 
	 * @param aSynchronizedResource
	 *            The FlexoResource to add.
	 */
	public void addToSynchronizedResources(FlexoResource aSynchronizedResource) {
		getSynchronizedResources().addToResources(aSynchronizedResource);
	}

	/**
	 * Remove from SynchronizedResources
	 * 
	 * @param aSynchronizedResource
	 *            The FlexoResource to remove.
	 */
	public void removeFromSynchronizedResources(FlexoResource aSynchronizedResource) {
		getSynchronizedResources().removeFromResources(aSynchronizedResource);
	}

	@Override
	public void setChanged() {
		if (getProject() != null) {
			getProject().notifyResourceChanged(this);
		}
	}

	// ==========================================================================
	// ============================== Deletion
	// ==================================
	// ==========================================================================

	private boolean isDeleted = false;

	/**
	 * Delete this resource. Note that this method does nothing, except removing resource from projet, so this must be overriden in
	 * sub-classes
	 */
	@Override
	public void delete() {
		isDeleted = true;
		getProject().removeResource(this);
		setChanged();
		notifyObservers(new DataModification(DELETED_PROPERTY, this, null));
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	@Override
	public boolean isDeleted() {
		return isDeleted;
	}

	// ==========================================================================
	// ====================== RMNotification propagation
	// ========================
	// ==========================================================================

	/**
	 * 
	 * Receive a notification that should be handled by the ResourceManager scheme. The ResourceManager will handle and propagate this
	 * notification
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 * @see org.openflexo.foundation.rm.FlexoResource#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void notifyRM(RMNotification notification) throws FlexoException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Resource " + getResourceIdentifier() + " is notified of " + notification);
		}
		if (notification.isDeepNotification()) {
			Enumeration en = getDeepPropagationTargets(notification).elements();
			while (en.hasMoreElements()) {
				FlexoResource temp = (FlexoResource) en.nextElement();
				temp.receiveRMNotification(notification);
			}
		} else {
			Enumeration en = getSimplePropagationTargets(notification).elements();
			while (en.hasMoreElements()) {
				FlexoResource temp = (FlexoResource) en.nextElement();
				temp.receiveRMNotification(notification);
			}
		}
	}

	private Vector<FlexoResource<FlexoResourceData>> getSimplePropagationTargets(RMNotification notification) {
		Vector<FlexoResource<FlexoResourceData>> returned = new Vector<FlexoResource<FlexoResourceData>>();
		Enumeration<FlexoResource<FlexoResourceData>> en = getSynchronizedResources().elements();
		while (en.hasMoreElements()) {
			FlexoResource<FlexoResourceData> temp = en.nextElement();
			if (notification.propagateToSynchronizedResource(this, temp)) {
				returned.add(temp);
			}
		}
		en = getAlteredResources().elements();
		while (en.hasMoreElements()) {
			FlexoResource<FlexoResourceData> temp = en.nextElement();
			if (notification.propagateToAlteredResource(this, temp)) {
				returned.add(temp);
			}
		}
		return returned;
	}

	private Vector<FlexoResource<FlexoResourceData>> getDeepPropagationTargets(RMNotification notification) {
		Vector<FlexoResource<FlexoResourceData>> returned = new Vector<FlexoResource<FlexoResourceData>>();
		addDeepPropagationTargets(returned, notification);
		return returned;
	}

	private void addDeepPropagationTargets(Vector<FlexoResource<FlexoResourceData>> targetList, RMNotification notification) {
		Enumeration<FlexoResource<FlexoResourceData>> en = getSynchronizedResources().elements();
		while (en.hasMoreElements()) {
			FlexoResource<FlexoResourceData> res = en.nextElement();
			if (!targetList.contains(res) && notification.propagateToSynchronizedResource(this, res)) {
				targetList.add(res);
				res.addDeepPropagationTargets(targetList, notification);
			}
		}
		en = getAlteredResources().elements();
		while (en.hasMoreElements()) {
			FlexoResource<FlexoResourceData> res = en.nextElement();
			if (!targetList.contains(res) && notification.propagateToAlteredResource(this, res)) {
				targetList.add(res);
				res.addDeepPropagationTargets(targetList, notification);
			}
		}
	}

	/**
	 * 
	 * Receive a notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Receive RMNotification in FlexoResource IGNORED\n\treceiver:" + this + "\n\tnotification:" + notification);
		}
	}

	/**
	 * Returns true if and only if this resource is up-to-date and therefore doesn't require to be updated. (No assertion is done on the
	 * fact whether resource is loaded or not)
	 * 
	 * Note: Calling this method assert that NO MODIFICATION will be performed on the entire model
	 * 
	 * @return
	 * @throws ResourceDependencyLoopException
	 */
	public final boolean needsUpdate() throws ResourceDependencyLoopException {
		return _needsUpdate(makeSingleton());
	}

	/**
	 * Update this resource.
	 * 
	 * If this resource was up-to-date, ensure that resource will be correctely loaded (performUpdating is called during this method
	 * execution)
	 * 
	 * If this resource wasn't up-to-date, update whole required model using dependancies, and perform updating on resource
	 * 
	 * When returning, we are sure that the method is up-to-date.
	 * 
	 * @return a tree representing all resources that were updated
	 * @throws ResourceDependencyLoopException
	 * @throws FlexoException
	 * @throws ProjectLoadingCancelledException
	 * @throws FileNotFoundException
	 * @throws LoadResourceException
	 */
	public final FlexoResourceTree update() throws ResourceDependencyLoopException, LoadResourceException, FileNotFoundException,
			ProjectLoadingCancelledException, FlexoException {
		return _update(makeSingleton());
	}

	/**
	 * Abstract method representing things to do (processing) for a method to build itself (semantic differs from different kind of
	 * resources: load/backward_sync/generate/etc...)
	 * 
	 * @param updatedResources
	 *            : supply a tree representing all resources that were updated below
	 * @throws ResourceDependencyLoopException
	 * @throws ProjectLoadingCancelledException
	 * @throws FileNotFoundException
	 * @throws LoadResourceException
	 */
	protected abstract void performUpdating(FlexoResourceTree updatedResources) throws ResourceDependencyLoopException,
			LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException, FlexoException;

	/**
	 * Important method "telling" if a resource from which this resource depend is a state requiring this resource to be updated/processed
	 * (load/backward_sync/generate/etc...)
	 * 
	 * Note: here is the default implementation, which is overriden in FlexoStorageResource with LastSynchonizedWith scheme
	 * 
	 * Note2: this was your isToBeBacksynchronizedWith() method
	 * 
	 * @return flag indicating if resource is in a state requiring this resource to be updated/processed
	 */
	protected boolean requireUpdateBecauseOf(FlexoResource resource) {
		return resource.getLastUpdate() != null && getLastUpdate() != null && resource.getLastUpdate().after(getLastUpdate());
	}

	/**
	 * Internal scheme computing the dependancies tree This tree contains all resources requiring to be updated (except the root one,
	 * naturally)
	 * 
	 * Note: Calling this method assert that NO MODIFICATION will be performed on the entire model
	 * 
	 * Note2: we assert here that only current resource is requesting this data
	 * 
	 * @return a FlexoResourceTree representing all resources requiring to be updated
	 * @throws ResourceDependencyLoopException
	 *             if a loop was detected (loop in dependancies definition)
	 */
	private final FlexoResourceTree buildDependanciesTree() throws ResourceDependencyLoopException {
		return buildDependencyTree(makeSingleton());
	}

	/**
	 * Internal scheme computing the dependancies tree, given a set of resources involved in this request (used to prevent infinite loop
	 * when loop in definition) This tree contains all resources requiring to be updated (except the root one, naturally)
	 * 
	 * Note: Calling this method assert that NO MODIFICATION will be performed on the entire model
	 * 
	 * @param processedResources
	 *            : a set of resources involved in this request
	 * @return a FlexoResourceTree representing all resources requiring to be updated
	 * @throws ResourceDependencyLoopException
	 *             if a loop was detected (loop in dependancies definition)
	 */
	private final FlexoResourceTree buildDependencyTree(List<FlexoResource<FlexoResourceData>> processedResources)
			throws ResourceDependencyLoopException {
		FlexoResourceTreeImplementation returned = new FlexoResourceTreeImplementation(this);
		for (Enumeration<FlexoResource<FlexoResourceData>> e = getDependentResources().elements(false, getProject().getDependancyScheme()); e
				.hasMoreElements();) {
			FlexoResource<FlexoResourceData> resource = e.nextElement();
			if (processedResources.contains(resource)) {
				throw new ResourceDependencyLoopException(resource);
			}
			List<FlexoResource<FlexoResourceData>> newProcessesResources = new ArrayList<FlexoResource<FlexoResourceData>>(
					processedResources);
			newProcessesResources.add(resource);
			if (resource._needsUpdate(newProcessesResources) || requireUpdateBecauseOf(resource)) {
				// Add this resource to dependancies tree
				returned.add(resource.buildDependencyTree(newProcessesResources));
			}
		}
		return returned;
	}

	/**
	 * Internal method used to update the whole model above which we depend, given a set of resources involved in this request (used to
	 * prevent infinite loop when loop in definition)
	 * 
	 * @param processedResources
	 *            : a set of resources involved in this request
	 * @return a FlexoResourceTree representing all resources that were updated
	 * @throws ResourceDependencyLoopException
	 *             if a loop was detected (loop in dependancies definition)
	 * @throws FlexoException
	 * @throws ProjectLoadingCancelledException
	 * @throws FileNotFoundException
	 * @throws LoadResourceException
	 */
	protected final FlexoResourceTree performUpdateDependanciesModel(List<FlexoResource<FlexoResourceData>> processedResources)
			throws ResourceDependencyLoopException, LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException,
			FlexoException {
		FlexoResourceTreeImplementation returned = new FlexoResourceTreeImplementation(this);
		for (Enumeration<FlexoResource<FlexoResourceData>> e = getDependentResources().elements(false,
				getProject() != null ? getProject().getDependancyScheme() : DependencyAlgorithmScheme.Pessimistic); e.hasMoreElements();) {
			FlexoResource<FlexoResourceData> resource = e.nextElement();
			if (processedResources.contains(resource)) {
				throw new ResourceDependencyLoopException(resource);
			}
			List<FlexoResource<FlexoResourceData>> newProcessesResources = new ArrayList<FlexoResource<FlexoResourceData>>(
					processedResources);
			newProcessesResources.add(resource);
			try {
				if (resource._needsUpdate(newProcessesResources)) {
					// This resource require updating, update it
					FlexoResourceTree updatedResources = resource._update(newProcessesResources);
					returned.add(updatedResources);
				} else if (requireUpdateBecauseOf(resource)) {
					// Add this resource to dependancies tree
					returned.add(resource.buildDependencyTree(newProcessesResources));
				}
			} catch (ResourceDependencyLoopException e1) {
				e1.addToResourceStack(this);
				throw e1;
			}
		}
		return returned;
	}

	/**
	 * Internal method used to build a vector containing this resource
	 * 
	 * @return
	 */
	private List<FlexoResource<FlexoResourceData>> makeSingleton() {
		return Arrays.asList((FlexoResource<FlexoResourceData>) this);
	}

	/**
	 * Internal method computing needsUpdate() given a vector of calling resources allowing to prevent from infinite loop
	 * 
	 * @return a flag indicating if this resource needs to be updated
	 * @throws ResourceDependencyLoopException
	 */
	private final boolean _needsUpdate(List<FlexoResource<FlexoResourceData>> callingResources) throws ResourceDependencyLoopException {
		FlexoResourceTree tree = buildDependencyTree(callingResources);
		if (!tree.isEmpty()) {
			/*StringBuilder sb = new StringBuilder(this+" ").append('(').append(new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastUpdate())).append(") needs update because of ");
			for (FlexoResourceTree node : tree.getChildNodes()) {
			   sb.append((node.getRootNode().getNeedsUpdateReason().equals(FlexoLocalization.localizedForKey("up_to_date")))
			           ? node.getRootNode() +new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(node.getRootNode().getLastUpdate()): node.getRootNode().getNeedsUpdateReason()).append(",");
			}*/
			_needsUpdateReason = tree.toString();
		} else {
			_needsUpdateReason = FlexoLocalization.localizedForKey("up_to_date");
		}
		return !tree.isEmpty();
	}

	/**
	 * Internal method computing update() given a vector of calling resources allowing to prevent from infinite loop
	 * 
	 * @return a tree representing all resources that were updated
	 * @throws ResourceDependencyLoopException
	 * @throws FlexoException
	 * @throws ProjectLoadingCancelledException
	 * @throws FileNotFoundException
	 * @throws LoadResourceException
	 */
	private final FlexoResourceTree _update(List<FlexoResource<FlexoResourceData>> callingResources)
			throws ResourceDependencyLoopException, LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException,
			FlexoException {
		// First update all dependancies model
		FlexoResourceTree updatedResources = performUpdateDependanciesModel(callingResources);

		// Perform updating knowing which resources were updated
		performUpdating(updatedResources);

		return updatedResources;
	}

	/**
	 * Called by the resource manager on a resource to tell that an other resource
	 * 
	 * <pre>
	 * aResource
	 * </pre>
	 * 
	 * from which this resource depends, has been more recently modified and thus provides a way to synchonize this resource from
	 * 
	 * <pre>
	 * aResource
	 * </pre>
	 * 
	 * @param aResource
	 *            the resource from which this resource depends
	 * @throws FlexoException
	 */
	/*public void backwardSynchronizeWith(FlexoResource aResource) throws FlexoException
	{
	    // Does nothing here: must be overriden in subclasses !
	    if (logger.isLoggable(Level.FINEST))
	        logger.finest("Resource " + getResourceIdentifier() + " : ignore backward synchronization for " + aResource.getResourceIdentifier()
	                + " : override me in subclasses if required.");
	}*/

	private String _needsUpdateReason = null;

	/**
	 * debug
	 * 
	 * @return
	 */
	public String getNeedsUpdateReason() {
		return _needsUpdateReason;
	}

	public Date getLastSynchronizedWithResource(FlexoResource aResource) {
		LastSynchronizedWithResourceEntry entry = _lastSynchronizedForResources.get(aResource);
		if (entry != null) {
			return entry.date;
		}
		// return null;
		return new Date(0);
	}

	public void setLastSynchronizedWithResource(FlexoResource aResource, Date aDate) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setLastSynchronizedWithResource " + aResource + " with " + aDate);
		}
		_lastSynchronizedForResources.put(aResource, new LastSynchronizedWithResourceEntry(this, aResource, aDate));
		for (FlexoResource<?> alteredResource : getAlteredResources()) {
			alteredResource.resetLastSynchronizedWithResource(this);
		}
		notifyResourceChanged();
	}

	public void resetLastSynchronizedWithResource(FlexoResource aResource) {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("resetLastSynchronizedWithResource " + aResource);
		}
		/*_lastSynchronizedForResources.remove(aResource);
		// And propagate it again
		for (Enumeration en=getAlteredResources().elements(); en.hasMoreElements();) {
		    FlexoResource alteredResource = (FlexoResource)en.nextElement();
		    alteredResource.resetLastSynchronizedWithResource(this);
		}
		notifyResourceChanged();*/
		resetLastSynchronizedWithResource(aResource, new Vector<FlexoResource>());
	}

	private void resetLastSynchronizedWithResource(FlexoResource aResource, Vector<FlexoResource> alreadyDone) {
		if (!alreadyDone.contains(this)) {
			_lastSynchronizedForResources.remove(aResource);
			alreadyDone.add(this);
			// And propagate it again
			for (Enumeration en = getAlteredResources().elements(); en.hasMoreElements();) {
				FlexoResource alteredResource = (FlexoResource) en.nextElement();
				alteredResource.resetLastSynchronizedWithResource(this, alreadyDone);
			}
		}
		notifyResourceChanged();
	}

	public Hashtable<FlexoResource<?>, LastSynchronizedWithResourceEntry> getLastSynchronizedForResources() {
		return _lastSynchronizedForResources;
	}

	public void setLastSynchronizedForResources(Hashtable<FlexoResource<?>, LastSynchronizedWithResourceEntry> lastSynchronizedForResources) {
		this._lastSynchronizedForResources = lastSynchronizedForResources;
	}

	public void setLastSynchronizedForResourcesForKey(LastSynchronizedWithResourceEntry entry, FlexoResource key) {
		entry.originResource = this;
		_lastSynchronizedForResources.put(key, entry);
	}

	public void removeLastSynchronizedForResourcesWithKey(FlexoResource key) {
		_lastSynchronizedForResources.remove(key);
	}

	public static class LastSynchronizedWithResourceEntry implements XMLSerializable {
		protected FlexoResource<? extends FlexoResourceData> originResource;
		private FlexoResource<? extends FlexoResourceData> resource;
		Date date;

		public LastSynchronizedWithResourceEntry() {
			super();
		}

		public LastSynchronizedWithResourceEntry(FlexoResource<? extends FlexoResourceData> anOriginResource,
				FlexoResource<? extends FlexoResourceData> aResource, Date aDate) {
			this();
			date = aDate;
			resource = aResource;
			originResource = anOriginResource;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public FlexoResource<? extends FlexoResourceData> getResource() {
			return resource;
		}

		public FlexoResource<? extends FlexoResourceData> getOriginResource() {
			return originResource;
		}

		public void setResource(FlexoResource<? extends FlexoResourceData> resource) {
			this.resource = resource;
		}

		public String getSerializationIdentifier() {
			return originResource.getSerializationIdentifier() + "_" + getResource().getSerializationIdentifier();
		}
	}

	public String getSerializationIdentifier() {
		return getUserIdentifier() + "_" + getResourceIdentifier();
	}

	private String userIdentifier;

	public String getUserIdentifier() {
		if (userIdentifier == null) {
			return FlexoModelObject.getCurrentUserIdentifier();
		}
		return userIdentifier;
	}

	public void setUserIdentifier(String aUserIdentifier) {
		userIdentifier = aUserIdentifier;
	}

	public String getURI() {
		if (getProject() != null) {
			return getProject().getURI() + "#" + getResourceIdentifier();
		} else {
			return null;
		}
	}

	private boolean isActive = true;

	public void activate() {
		isActive = true;
		notifyResourceStatusChanged();
	}

	public void deactivate() {
		isActive = false;
		notifyResourceStatusChanged();
	}

	public boolean isActive() {
		return isActive;
	}

	public final boolean isInactive() {
		return !isActive();
	}

	public enum DependencyAlgorithmScheme {
		Pessimistic, Optimistic
	}

	// CPU-expensive
	public boolean deeplyDependsOf(FlexoResource resource) {
		return deeplyDependsOf(resource, new HashSet<FlexoResource>());
	}

	// CPU-expensive
	public boolean deeplyDependsOf(FlexoResource resource, HashSet<FlexoResource> calling) {
		if (!calling.contains(this)) {
			calling.add(this);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Loop detected " + calling + "\n" + this);
			}
			return true;
		}
		if (getDependentResources().contains(resource)) {
			return true;
		}
		for (FlexoResource r : getDependentResources()) {
			if (r == this) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.info("Loop dependancy found for resource " + r + " " + r.getFullyQualifiedName());
				}
				continue;
			}
			if (r.deeplyDependsOf(resource, calling)) {
				return true;
			}
		}
		calling.remove(this);
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
	protected final boolean dependsOf(FlexoResource resource, DependencyAlgorithmScheme dependancyScheme) {
		if (dependancyScheme == DependencyAlgorithmScheme.Pessimistic) {
			return true;
		} else if (dependancyScheme == DependencyAlgorithmScheme.Optimistic) {
			Date requestDate = getRequestDateToBeUsedForOptimisticDependencyChecking(resource);
			return optimisticallyDependsOf(resource, requestDate);
		}
		return true;
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
	 * 
	 * Please override this method when required
	 * 
	 * @param resource
	 * @param requestDate
	 *            TODO
	 * @param dependancyScheme
	 * @return
	 */
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		return true;
	}

	/**
	 * Please override this when required
	 * 
	 * @param resource
	 * @return
	 */
	protected Date getRequestDateToBeUsedForOptimisticDependencyChecking(FlexoResource resource) {
		return new Date();
	}

	public boolean isRegistered() {
		return getProject() != null && getProject().resourceForKey(getFullyQualifiedName()) == this;
	}

	// =============================================================
	// ======================== Validation =========================
	// =============================================================

	/**
	 * Returns fully qualified name for this object
	 * 
	 * @return
	 */
	@Override
	public String getFullyQualifiedName() {
		return getResourceIdentifier();
	}

	/**
	 * Overrides getAllEmbeddedValidableObjects
	 * 
	 * @see org.openflexo.foundation.validation.Validable#getAllEmbeddedValidableObjects()
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = new Vector<Validable>();
		v.add(this);
		return v;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	/**
	 * Overrides getDefaultValidationModel
	 * 
	 * @see org.openflexo.foundation.validation.Validable#getDefaultValidationModel()
	 */
	@Override
	public ValidationModel getDefaultValidationModel() {
		return getProject().getDefaultValidationModel();
	}

	/**
	 * Overrides isValid
	 * 
	 * @see org.openflexo.foundation.validation.Validable#isValid()
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Overrides isValid
	 * 
	 * @see org.openflexo.foundation.validation.Validable#isValid(org.openflexo.foundation.validation.ValidationModel)
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Overrides validate
	 * 
	 * @see org.openflexo.foundation.validation.Validable#validate()
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Overrides validate
	 * 
	 * @see org.openflexo.foundation.validation.Validable#validate(org.openflexo.foundation.validation.ValidationModel)
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Overrides validate
	 * 
	 * @see org.openflexo.foundation.validation.Validable#validate(org.openflexo.foundation.validation.ValidationReport)
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Overrides validate
	 * 
	 * @see org.openflexo.foundation.validation.Validable#validate(org.openflexo.foundation.validation.ValidationReport,
	 *      org.openflexo.foundation.validation.ValidationModel)
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	/**
	 * @return
	 */
	public boolean deeplyDependsOfItSelf() {
		for (FlexoResource r : getDependentResources()) {
			if (r.deeplyDependsOf(this)) {
				return true;
			}
		}
		return false;
	}

	public final void performUpdating() {
	}

	public void notifyDependantResourceChange(FlexoResource origin) {
		getDependentResources().update();
	}

	/**
	 * Checks if this resource is in an acceptable state. The resource should return false if it wants to be removed. Must return true
	 * otherwise.
	 * 
	 * @return true if the resource can be kept, false if the resource must be deleted.
	 */
	public boolean checkIntegrity() {
		return true;
	}

}
