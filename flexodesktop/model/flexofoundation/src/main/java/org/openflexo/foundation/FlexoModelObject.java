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
package org.openflexo.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionnable;
import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.ws.client.PPMWebService.PPMObject;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Abstract class for all objects involved in FLEXO model definition, as a part of a project, and XML serializable
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoModelObject extends FlexoXMLSerializableObject implements FlexoActionnable {

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObject.class.getPackage().toString());

	public static final String ID_SEPARATOR = "_";

	public static final Comparator<FlexoModelObject> DEFAULT_COMPARATOR = new FlexoDefaultComparator<FlexoModelObject>();

	public static final Comparator<FlexoModelObject> NAME_COMPARATOR = new FlexoNameComparator<FlexoModelObject>();

	private long flexoID = -2;

	private transient Object _context;

	private static String currentUserIdentifier;

	private boolean isRegistered = false;

	private Vector<FlexoModelObjectReference<?>> referencers;

	// Imported objects
	private boolean isDeletedOnServer = false;

	private String uri;

	private String versionURI;

	private String uriFromSourceObject;

	public static String getCurrentUserIdentifier() {
		if (currentUserIdentifier == null) {
			currentUserIdentifier = "FLX".intern();
		}
		return currentUserIdentifier;
	}

	public static void setCurrentUserIdentifier(String aUserIdentifier) {
		if (aUserIdentifier != null && aUserIdentifier.indexOf('#') > -1) {
			aUserIdentifier = aUserIdentifier.replace('#', '-');
			FlexoModelObject.currentUserIdentifier = aUserIdentifier.intern();
		}
	}

	private String userIdentifier;

	public String getUserIdentifier() {
		if (userIdentifier == null) {
			return getCurrentUserIdentifier();
		}
		return userIdentifier;
	}

	public void setUserIdentifier(String aUserIdentifier) {
		if (aUserIdentifier != null && aUserIdentifier.indexOf('#') > -1) {
			aUserIdentifier = aUserIdentifier.replace('#', '-');
		}
		userIdentifier = aUserIdentifier != null ? aUserIdentifier.intern() : null;
		if (!isDeserializing()) {
			fireSerializationIdChanged();
		}
	}

	/**
	 * Constructor used by Serializable. <blockquote> The deserialization process does not use the object's constructor - the object is
	 * instantiated without a constructor and initialized using the serialized instance data. The only requirement on the constructor for a
	 * class that implements Serializable is that the first non-serializable superclass in its inheritence hierarchy must have a no-argument
	 * constructor. <BR>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; From <a
	 * href="http://www.jguru.com/faq/view.jsp?EID=251942">http://www.jguru.com/faq/view.jsp?EID=251942</A></blockquote>
	 */
	public FlexoModelObject() {
		super();
		referencers = new Vector<FlexoModelObjectReference<?>>();
	}

	public FlexoModelObject(FlexoProject project) {
		super(project);
		registerObject(project);
	}

	public FlexoModelObject(FlexoProject project, String description) {
		this(project);
		setDescription(description);
	}

	protected void registerObject(FlexoProject project) {
		if (project != null) {
			/*
			 * if (project.getLastUniqueIDHasBeenSet() && flexoID < 0) { flexoID = project.getNewFlexoID();
			 * System.err.println("Via constructor New flexo ID: "+flexoID); }
			 */
			project.register(this);
			isRegistered = true;
		} else {
			if (logger != null && logger.isLoggable(Level.FINE) && !(this instanceof TemporaryFlexoModelObject)) {
				logger.fine("No project for object of type " + getClassNameKey());
			}
		}
	}

	/**
	 * Overrides getStringEncoder
	 * 
	 * @see org.openflexo.foundation.FlexoXMLSerializableObject#getStringEncoder()
	 */
	@Override
	public StringEncoder getStringEncoder() {
		if (getProject() != null) {
			return getProject().getStringEncoder();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project, using the default string encoder. Any elements related to a FlexoProject like BindingValues or DMType will fail to be converted");
			}
			return super.getStringEncoder();
		}
	}

	@Override
	public FlexoProject getProject() {
		if (getXMLResourceData() != null && getXMLResourceData().getFlexoResource() != null) {
			return getXMLResourceData().getFlexoResource().getProject();
		}
		if (getXMLResourceData() != null && getXMLResourceData().getResource() instanceof FlexoProjectResource) {
			return ((FlexoProjectResource) getXMLResourceData().getResource()).getProject();
		}
		return super.getProject();
	}

	@Override
	public XMLMapping getXMLMapping() {
		if (getXMLResourceData() != null) {
			return getXMLResourceData().getXMLMapping();
		}
		return null;
	}

	public boolean isDeletedOnServer() {
		return isDeletedOnServer;
	}

	public void setIsDeletedOnServer(boolean isDeletedOnServer) {
		if (this.isDeletedOnServer == isDeletedOnServer) {
			return;
		}
		this.isDeletedOnServer = isDeletedOnServer;
		setChanged();
		notifyObservers(new DataModification("isDeletedOnServer", !this.isDeletedOnServer, isDeletedOnServer));
	}

	public void markAsDeletedOnServer() {
		setIsDeletedOnServer(true);
	}

	public void copyObjectAttributesInto(PPMObject object) {
		object.setName(getName());
		object.setUri(getURI());
		object.setVersionUri(getVersionURI());
		object.setGeneralDescription(getDescription());
		object.setBusinessDescription(getBusinessDescription());
		object.setTechnicalDescription(getTechnicalDescription());
		object.setUserManualDescription(getUserManualDescription());
	}

	protected void updateFromObject(PPMObject object) {
		setIsDeletedOnServer(false);
		setURI(object.getUri());
		setVersionURI(object.getVersionUri());
		setDescription(object.getGeneralDescription());
		setBusinessDescription(object.getBusinessDescription());
		setTechnicalDescription(object.getTechnicalDescription());
		setUserManualDescription(object.getUserManualDescription());
		try {
			setName(object.getName());
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "setName threw an exception on " + this + "! This should never happen for imported objects", e);
			}
		}
	}

	public boolean isCache() {
		return false;
	}

	public FlexoModelObject getUncachedObject() {
		if (!isCache()) {
			return this;
		} else {
			throw new RuntimeException("Object of type " + getClass().getName()
					+ " is cached but does not properly override getUncachedObject()! (" + this + ")");
		}
	}

	public Class<? extends PPMObject> getEquivalentPPMClass() {
		return null;
	}

	protected boolean isEquivalentTo(PPMObject object) {
		if (object == null) {
			return false;
		}
		if (isDeletedOnServer()) {
			return false;
		}
		if (getEquivalentPPMClass() != object.getClass()) {
			return false;
		}
		if (stringHasChanged(getName(), object.getName())) {
			return false;
		}
		if (stringHasChanged(getURI(), object.getUri())) {
			return false;
		}
		if (stringHasChanged(getVersionURI(), object.getVersionUri())) {
			return false;
		}
		if (stringHasChanged(getDescription(), object.getGeneralDescription())) {
			return false;
		}
		if (stringHasChanged(getBusinessDescription(), object.getBusinessDescription())) {
			return false;
		}
		if (stringHasChanged(getTechnicalDescription(), object.getTechnicalDescription())) {
			return false;
		}
		if (stringHasChanged(getUserManualDescription(), object.getUserManualDescription())) {
			return false;
		}
		return true;
	}

	public static <O extends FlexoModelObject> O getObjectWithURI(Vector<O> objects, String uri) {
		for (O o : objects) {
			if (o.getURI().equals(uri)) {
				return o;
			}
		}
		return null;
	}

	public String getURI() {
		if (getProject() == null) {
			throw new RuntimeException("Project is undefined for object " + getClass().getName());
		}
		if (isImported() || uri != null) {
			return uri;
		}
		if (isSerializing()) {
			return null; // We never serialize URI for unimported objects
		}
		return getProject().getURI() + "fmo/" + getClass().getSimpleName() + getUserIdentifier() + "_" + getFlexoID();
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getVersionURI() {
		if (getProject() == null) {
			throw new RuntimeException("Project is undefined for object " + getClass().getName());
		}
		if (isImported() || versionURI != null) {
			return versionURI;
		}
		if (isSerializing()) {
			return null; // We never serialize URI for unimported objects
		}
		return getProject().getProjectVersionURI() + "/fmo/version_of_" + getClass().getSimpleName() + getUserIdentifier() + "_"
				+ getFlexoID();
	}

	public void setVersionURI(String versionURI) {
		this.versionURI = versionURI;
	}

	public String getURIFromSourceObject() {
		return uriFromSourceObject;
	}

	public void setURIFromSourceObject(String uri) {
		this.uriFromSourceObject = uri;
	}

	@Override
	public abstract String getFullyQualifiedName();

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) throws Exception {
		String old = this.name;
		this.name = name;
		if (!isDeserializing()) {
			setChanged();
			notifyObservers(new NameChanged(old, this.name));
		}
	}

	/**
	 * Returns a displayable name that is localized and readable by a user. This method is final because you should rather override
	 * {@link #getName()} or {@link #getClassNameKey()}
	 * 
	 * @return a displayable name that is localized and readable by a user.
	 */
	public String getDisplayableName() {
		if (getName() != null) {
			return getLocalizedClassName() + " " + getName();
		} else {
			return FlexoLocalization.localizedForKey("a") + " " + getLocalizedClassName();
		}
	}

	public String getDisplayableDescription() {
		return getDisplayableName();
	}

	/**
	 * @return
	 */
	@Override
	public String getLocalizedClassName() {
		return FlexoLocalization.localizedForKey(getClassNameKey());
	}

	/**
	 * @return Returns the flexoID.
	 */
	public long getFlexoID() {
		if (isBeingCloned()) {
			return -1;
		}
		if (flexoID < 0 && !isDeserializing() && getXMLResourceData() != null
				&& getXMLResourceData().getResource() instanceof FlexoXMLFileResource) {
			flexoID = ((FlexoXMLFileResource) getXMLResourceData().getResource()).getNewFlexoID();
		}
		if (getProject() != null) {
			if (getProject().getLastUniqueIDHasBeenSet() && flexoID < 0 && !isDeserializing()) {
				flexoID = getProject().getNewFlexoID();
				// setChanged();
			}
			if (!isRegistered) {
				registerObject(getProject());
				isRegistered = true;
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project for object of type " + getClassNameKey() + " (".getClass().getName() + ")");
			}
		}
		return flexoID;
	}

	/**
	 * @param flexoID
	 *            The flexoID to set.
	 */
	public void setFlexoID(long flexoID) {
		if (!isCreatedByCloning() && flexoID > 0 && flexoID != this.flexoID) {
			this.flexoID = flexoID;
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new DataModification("flexoID", new Long(this.flexoID), new Long(flexoID)));
				fireSerializationIdChanged();
			}
		}
		if (flexoID < 0 && !isCreatedByCloning() && !(this instanceof FlexoProject) && getXMLResourceData() != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Tried to set a negative ID on object of class " + getClass().getName());
			}
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		super.finalizeDeserialization(builder);
		if (getXMLResourceData() != null && getXMLResourceData().getResource() instanceof FlexoXMLFileResource) {
			((FlexoXMLFileResource) getXMLResourceData().getResource()).setLastID(getFlexoID());
		}
	}

	private void fireSerializationIdChanged() {
		for (FlexoModelObjectReference ref : new Vector<FlexoModelObjectReference>(referencers)) {
			ref.notifySerializationIdHasChanged();
		}
	}

	public String getSerializationIdentifier() {
		return getSerializationIdentifier(getUserIdentifier(), String.valueOf(getFlexoID()));
	}

	public static String getSerializationIdentifier(String userIdentifier, String flexoId) {
		return userIdentifier + ID_SEPARATOR + flexoId;
	}

	@Override
	public void delete() {
		// if (logger.isLoggable(Level.FINE)) logger.fine ("Delete
		// "+this.getClass().getName()+" : "+this);
		if (isDeleted()) {
			// in some case : the delete may be called twice on a sequence (in case of deletion of last widget of the sequence)...
			// and it will fail
			// a good idea would be to avoid this double invocation.
			// In the mean time, this little hack will do the trick.
			return;

		}
		if (getProject() != null) {
			if (isRegistered) {
				getProject().unregister(this);
			}
			isRegistered = false;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null for " + this.getClass().getSimpleName() + "_" + this.getFlexoID());
			}
		}

		if (referencers != null) {
			for (FlexoModelObjectReference ref : new ArrayList<FlexoModelObjectReference>(referencers)) {
				ref.notifyObjectDeletion();
			}
			referencers.clear();
		}
		referencers = null;

		super.delete();

		setChanged();
		notifyObservers(new ObjectDeleted(this));
		if (getProject() != null) {
			getProject().notifyObjectDeleted(this);
		}

	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	public void undelete() {
		// if (logger.isLoggable(Level.FINE)) logger.fine ("Delete
		// "+this.getClass().getName()+" : "+this);
		if (getProject() != null) {
			if (isRegistered) {
				getProject().register(this);
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null for " + this);
			}
		}
		isDeleted = false;
		referencers = new Vector<FlexoModelObjectReference<?>>();
		setChanged();
		if (getProject() != null) {
			getProject().notifyObjectCreated(this);
		}
	}

	/**
	 *
	 */
	public boolean getDontEscapeLatex() {
		return getDocFormat() != null && getDocFormat() == FlexoDocFormat.LATEX;
	}

	public abstract String getClassNameKey();

	public String getEnglishClassName() {
		return FlexoLocalization.localizedForKeyAndLanguage(getClassNameKey(), Language.ENGLISH);
	}

	public String getFrenchClassName() {
		return FlexoLocalization.localizedForKeyAndLanguage(getClassNameKey(), Language.FRENCH);
	}

	public String getDutchClassName() {
		return FlexoLocalization.localizedForKeyAndLanguage(getClassNameKey(), Language.DUTCH);
	}

	private static final String EMPTY_STRING = "";

	@Override
	public void setChanged() {
		if (getProject() != null && !isDeserializing()) {
			getProject().notifyObjectChanged(this);
		}
		super.setChanged();
	}

	private String expectedNotificationAttribute;
	private boolean notificationHasBeenPerformed;

	/**
	 * Override parent method by detecting unnotified setAttribute action Notify it if non-notified
	 */
	@Override
	public void setObjectForKey(Object value, String key) {
		notificationHasBeenPerformed = false;
		expectedNotificationAttribute = key;
		Object oldValue = objectForKey(key);
		super.setObjectForKey(value, key);
		if (!notificationHasBeenPerformed) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SetAttribute for [" + key + "/" + getClass().getSimpleName() + "] (old value: " + oldValue
						+ ", new value: " + value
						+ ") was not notified. Please add setChanged()/notifyObservers(...) methods in required set method.");
			}
			setChanged();
			notifyObservers(new AttributeDataModification(key, oldValue, value));
		}
	}

	/**
	  */
	@Override
	public void notifyObservers(DataModification dataModification) {
		super.notifyObservers(dataModification);
		if (expectedNotificationAttribute != null && dataModification != null
				&& expectedNotificationAttribute.equals(dataModification.propertyName())) {
			notificationHasBeenPerformed = true;
		}
	}

	/**
	  */
	public void notifyObserversAsReentrantModification(DataModification dataModification) {
		dataModification.setReentrant(true);
		super.notifyObservers(dataModification);
		if (expectedNotificationAttribute != null && expectedNotificationAttribute.equals(dataModification.propertyName())) {
			notificationHasBeenPerformed = true;
		}
	}

	public void addToReferencers(FlexoModelObjectReference<? extends FlexoModelObject> ref) {
		if (referencers != null && !referencers.contains(ref)) {
			referencers.add(ref);
		}
	}

	public void removeFromReferencers(FlexoModelObjectReference<? extends FlexoModelObject> ref) {
		if (referencers != null) {
			referencers.remove(ref);
		}
	}

	public Vector<FlexoModelObjectReference<?>> getReferencers() {
		return referencers;
	}

	public static class FlexoDefaultComparator<E extends FlexoModelObject> implements Comparator<E> {
		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(E o1, E o2) {
			if (o1.getFlexoID() > o2.getFlexoID()) {
				return 1;
			} else if (o1.getFlexoID() < o2.getFlexoID()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static class FlexoNameComparator<E extends FlexoModelObject> implements Comparator<E> {
		/**
		 * Overrides compare
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(E o1, E o2) {
			if (o1.getName() == null) {
				if (o2.getName() == null) {
					return 0;
				} else {
					return -1;
				}
			} else if (o2.getName() == null) {
				return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}

	public final int compareTo(FlexoModelObject o2) {
		if (getFlexoID() > o2.getFlexoID()) {
			return 1;
		} else if (getFlexoID() < o2.getFlexoID()) {
			return -1;
		} else {
			return 0;
		}
	}

	public final int compare(FlexoModelObject o1, FlexoModelObject o2) {
		return o1.compareTo(o2);
	}

	// ============================================
	// ============== Access to help ==============
	// ============================================

	private static HelpRetriever _helpRetriever = null;

	public static interface HelpRetriever {
		public String shortHelpForObject(FlexoModelObject object);

		public String longHelpForObject(FlexoModelObject object);
	}

	/**
	 * Return help text for supplied object, as defined in DocResourceManager as long version Note: return an HTML version, with embedding
	 * <html>...</html> tags.
	 */
	public String getHelpText() {
		if (_helpRetriever != null) {
			return _helpRetriever.longHelpForObject(this);
		}
		return null;
	}

	/**
	 * Return help text for supplied object, as defined in DocResourceManager as short version Note: return an HTML version, with embedding
	 * <html>...</html> tags.
	 */
	public String getShortHelpText() {
		if (_helpRetriever != null) {
			return _helpRetriever.shortHelpForObject(this);
		}
		return null;
	}

	public static HelpRetriever getHelpRetriever() {
		return _helpRetriever;
	}

	public static void setHelpRetriever(HelpRetriever retriever) {
		_helpRetriever = retriever;
	}

	// ================================================
	// ============== Dynamic properties ==============
	// ================================================

	private Hashtable<String, String> _dynamicProperties;

	private Hashtable<String, String> _buildDynamicPropertiesWhenRequired() {
		if (_dynamicProperties == null && !isSerializing()) {
			// logger.info("Build _dynamicProperties for "+this);
			_dynamicProperties = new Hashtable<String, String>();
		}
		return _dynamicProperties;
	}

	public Hashtable<String, String> getDynamicProperties() {
		_buildDynamicPropertiesWhenRequired();
		return _dynamicProperties;
	}

	public void setDynamicProperties(Hashtable<String, String> props) {
		_dynamicProperties = props;
	}

	public void setDynamicPropertiesForKey(String value, String key) {
		// logger.info("setDynamicPropertiesForKey:  "+key+" value: "+value);
		if (_dynamicProperties == null) {
			_dynamicProperties = new Hashtable<String, String>();
		}
		_dynamicProperties.put(key, value);
	}

	public void removeDynamicPropertiesWithKey(String key) {
		if (_dynamicProperties == null) {
			_dynamicProperties = new Hashtable<String, String>();
		}
		_dynamicProperties.remove(key);
	}

	/**
	 * Return a Vector of all embedded ModelObject
	 * 
	 * @return a Vector of FlexoModelObject instances
	 */
	public Collection<FlexoModelObject> getEmbeddedObjects() {
		return getXMLMapping().getEmbeddedObjectsForObject(this, FlexoModelObject.class);
	}

	/**
	 * Returns a vector of all objects that will be deleted if you call delete on this object.
	 * 
	 * @return
	 */
	public Collection<FlexoModelObject> getAllRecursivelyEmbeddedDeletedObjects() {
		return getXMLMapping().getEmbeddedObjectsForObject(this, FlexoModelObject.class, false, true);
	}

	public Collection<FlexoModelObject> getAllRecursivelyEmbeddedObjects() {
		return getAllRecursivelyEmbeddedObjects(false);
	}

	public Collection<FlexoModelObject> getAllRecursivelyEmbeddedObjects(boolean maintainNaturalOrder) {
		return getXMLMapping().getEmbeddedObjectsForObject(this, FlexoModelObject.class, maintainNaturalOrder, true);
	}

	/**
	 * Return a vector of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> vector = new Vector<Validable>();
		for (FlexoModelObject o : getAllRecursivelyEmbeddedObjects()) {
			if (o instanceof Validable) {
				vector.add((Validable) o);
			}
		}
		return vector;
	}

	public String getScreenshootName() {
		ScreenshotResource screen = getProject().getScreenshotResource(this, false);
		if (screen == null) {
			return null;
		}
		return screen.getFileName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + (getName() != null ? getName() : getFlexoID());
	}

	@Deprecated
	public Vector<TabModel> inspectionExtraTabs() {
		return null;
	}

	/*
	 * private Vector<TabModel> _tabList;
	 * 
	 * public Vector<TabModel> inspectionExtraTabs() { if (_tabList == null) { _tabList = new Vector<TabModel>(); if
	 * (getEditionPatternReferences() != null) { for (EditionPatternReference ref : getEditionPatternReferences()) { EditionPatternInspector
	 * inspector = ref.getEditionPattern().getInspector(); if (inspector != null) { //for (Integer i : inspector.getTabs().keySet()) { //
	 * _tabList.add(inspector.getTabs().get(i)); //} } } } } return _tabList; }
	 */

	public String getInspectorTitle() {
		// By default, take default inspector name
		return null;
	}

	public String makeReference() {
		return FlexoModelObjectReference.getSerializationRepresentationForObject(this, true);
	}

}
