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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.DeleteFlexoProperty;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.action.FlexoActionnable;
import org.openflexo.foundation.action.SortFlexoProperties;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.ws.client.PPMWebService.PPMObject;
import org.openflexo.xmlcode.StringEncoder;

/**
 * Abstract class for all objects involved in FLEXO model definition
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoModelObject extends FlexoXMLSerializableObject implements FlexoActionnable {

	public static boolean stringHasChanged(String old, String newString) {
		return old == null && newString != null || old != null && !old.equals(newString);
	}

	private static final Logger logger = FlexoLogger.getLogger(FlexoModelObject.class.getPackage().toString());

	public static final String ID_SEPARATOR = "_";

	public static final Comparator<FlexoModelObject> DEFAULT_COMPARATOR = new FlexoDefaultComparator<FlexoModelObject>();

	public static final Comparator<FlexoModelObject> NAME_COMPARATOR = new FlexoNameComparator<FlexoModelObject>();

	private long flexoID = -2;

	private transient Object _context;

	private FlexoDocFormat docFormat;

	private static String currentUserIdentifier;

	private boolean isRegistered = false;

	private Map<String, String> specificDescriptions;

	private Vector<FlexoProperty> customProperties;

	public static FlexoActionizer<AddFlexoProperty, FlexoModelObject, FlexoModelObject> addFlexoPropertyActionizer;

	public static FlexoActionizer<DeleteFlexoProperty, FlexoProperty, FlexoProperty> deleteFlexoPropertyActionizer;

	public static FlexoActionizer<SortFlexoProperties, FlexoModelObject, FlexoModelObject> sortFlexoPropertiesActionizer;

	private boolean hasSpecificDescriptions = false;

	private Vector<FlexoModelObjectReference> referencers;

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
		}
		FlexoModelObject.currentUserIdentifier = aUserIdentifier.intern();
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
		userIdentifier = aUserIdentifier.intern();
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
		this(null);
	}

	/**
	 *
	 */
	public FlexoModelObject(FlexoProject project) {
		super();
		referencers = new Vector<FlexoModelObjectReference>();
		specificDescriptions = new TreeMap<String, String>();
		customProperties = new Vector<FlexoProperty>();
		_editionPatternReferences = new Vector<EditionPatternReference>();
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

	public FlexoModelObject(FlexoProject project, String description) {
		this(project);
		this.description = description;
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

	public FlexoProject getProject() {
		if (getXMLResourceData() != null) {
			return getXMLResourceData().getProject();
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

	public Vector<FlexoLink> getLinks() {
		return getProject().getFlexoLinks().getLinksFor(this);
	}

	public void notifyLinksChanged() {

	}

	/**
	 * Returns wheter this object is imported or not. Object implementing FlexoImportableObject should override this method
	 * 
	 * @return true if this object is imported.
	 */
	public boolean isImported() {
		return false;
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
	public final String getDisplayableName() {
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
		if (getProject() != null) {
			if (getProject().getLastUniqueIDHasBeenSet() && flexoID < 0 && !isDeserializing()) {
				flexoID = getProject().getNewFlexoID();
				// setChanged();
			}
			if (!isRegistered) {
				getProject().register(this);
				isRegistered = true;
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No project for object of type " + getClassNameKey());
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

	/**
	 * A map that stores the different declared actions for each class
	 */
	private static final Map<Class, Vector<FlexoActionType>> _declaredActionsForClass = new Hashtable<Class, Vector<FlexoActionType>>();
	/**
	 * A map that stores all the actions for each class (computed with the inheritance of each class)
	 */
	private static final Hashtable<Class, Vector<FlexoActionType>> _actionListForClass = new Hashtable<Class, Vector<FlexoActionType>>();

	public Vector<FlexoActionType> getActionList() {
		return getActionList(getClass());
	}

	public static <T extends FlexoModelObject> Vector<FlexoActionType> getActionList(Class<T> aClass) {
		if (_actionListForClass.get(aClass) == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("COMPUTE ACTION_LIST FOR " + aClass.getName());
			}
			Vector<FlexoActionType> returned = updateActionListFor(aClass);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("DONE. COMPUTE ACTION_LIST FOR " + aClass.getName() + ": " + returned.size() + " action(s) :");
				for (Enumeration<FlexoActionType> en = returned.elements(); en.hasMoreElements();) {
					FlexoActionType next = en.nextElement();
					logger.fine(" " + next.getLocalizedName());
				}
				logger.fine(".");
			}
			return returned;
		}
		Vector<FlexoActionType> returned = _actionListForClass.get(aClass);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("RETURN (NO COMPUTING) ACTION_LIST FOR " + aClass.getName() + ": " + returned.size() + " action(s) :");
			for (Enumeration<FlexoActionType> en = returned.elements(); en.hasMoreElements();) {
				FlexoActionType next = en.nextElement();
				logger.fine(" " + next.getLocalizedName());
			}
			logger.fine(".");
		}
		return returned;
	}

	public static <T1 extends FlexoModelObject, T extends T1> void addActionForClass(FlexoActionType<?, T1, ?> actionType,
			Class<T> objectClass) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("addActionForClass: " + actionType + " for " + objectClass);
		}
		Vector<FlexoActionType> actions = _declaredActionsForClass.get(objectClass);
		if (actions == null) {
			actions = new Vector<FlexoActionType>();
			_declaredActionsForClass.put(objectClass, actions);
		}
		if (actionType != null) {
			if (!actions.contains(actionType)) {
				actions.add(actionType);
			}
		} else {
			logger.warning("Trying to declare null action !");
		}

		if (_actionListForClass != null) {
			Vector<Class> entriesToRemove = new Vector<Class>();
			for (Class aClass : _actionListForClass.keySet()) {
				if (objectClass.isAssignableFrom(aClass)) {
					entriesToRemove.add(aClass);
				}
			}
			for (Class aClass : entriesToRemove) {
				logger.info("Recompute actions list for " + aClass);
				_actionListForClass.remove(aClass);
			}
		}

	}

	private static <T extends FlexoModelObject> Vector<FlexoActionType> updateActionListFor(Class<T> aClass) {
		Vector<FlexoActionType> newActionList = new Vector<FlexoActionType>();
		for (Map.Entry<Class, Vector<FlexoActionType>> e : _declaredActionsForClass.entrySet()) {
			if (e.getKey().isAssignableFrom(aClass)) {
				newActionList.addAll(e.getValue());
			}
		}
		_actionListForClass.put(aClass, newActionList);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateActionListFor() class: " + aClass);
			for (FlexoActionType a : newActionList) {
				logger.finer(" > " + a);
			}
		}
		return newActionList;
	}

	@Override
	public int getActionCount() {
		return getActionList().size();
	}

	@Override
	public FlexoActionType getActionTypeAt(int index) {
		return getActionList().elementAt(index);
	}

	public void delete() {
		// if (logger.isLoggable(Level.FINE)) logger.fine ("Delete
		// "+this.getClass().getName()+" : "+this);
		if (isDeleted) {
			// in some case : the delete may be called twice on a sequence (in case of deletion of last widget of the sequence)...
			// and it will fail
			// a good idea would be to avoid this double invocation.
			// In the mean time, this little hack will do the trick.
			return;

		}
		isDeleted = true;
		if (getProject() != null) {
			if (isRegistered) {
				getProject().unregister(this);
			}
			isRegistered = false;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Project is null for " + this);
			}
		}

		for (EditionPatternReference ref : getEditionPatternReferences()) {
			ref.delete();
		}
		_editionPatternReferences.clear();
		_editionPatternReferences = null;

		for (FlexoModelObjectReference ref : new ArrayList<FlexoModelObjectReference>(referencers)) {
			ref.notifyObjectDeletion();
		}
		referencers.clear();
		referencers = null;
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
		referencers = new Vector<FlexoModelObjectReference>();
		setChanged();
		if (getProject() != null) {
			getProject().notifyObjectCreated(this);
		}
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	protected boolean isDeleted = false;

	private boolean dontGenerate = false;

	private String description;

	public Object getContext() {
		return _context;
	}

	public void setContext(Object context) {
		_context = context;
	}

	/**
	 *
	 */
	public boolean getDontEscapeLatex() {
		return getDocFormat() != null && getDocFormat() == FlexoDocFormat.LATEX;
	}

	/**
	 * @deprecated
	 * @param dontEscapeLatex
	 */
	@Deprecated
	public void setDontEscapeLatex(boolean dontEscapeLatex) {
		if (dontEscapeLatex) {
			setDocFormat(FlexoDocFormat.LATEX);
		}
		setChanged();
		notifyObservers(new DataModification("dontEscapeLatex", null, new Boolean(dontEscapeLatex)));
	}

	public FlexoDocFormat getDocFormat() {
		return docFormat;
	}

	public void setDocFormat(FlexoDocFormat docFormat) {
		setDocFormat(docFormat, true);
	}

	public void setDocFormat(FlexoDocFormat docFormat, boolean notify) {
		FlexoDocFormat old = this.docFormat;
		this.docFormat = docFormat;
		if (notify) {
			setChanged();
			notifyObservers(new DataModification("docFormat", old, docFormat));
		}
	}

	/**
	 * @return Returns the dontGenerate.
	 */
	public boolean getDontGenerate() {
		return dontGenerate;
	}

	/**
	 * @param dontGenerate
	 *            The dontGenerate to set.
	 */
	public void setDontGenerate(boolean dontGenerate) {
		boolean old = this.dontGenerate;
		if (old != dontGenerate) {
			this.dontGenerate = dontGenerate;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("dontGenerate", new Boolean(old), new Boolean(dontGenerate)));
		}
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

	public boolean isDocEditable() {
		return !isImported();
	}

	public String getDescription() {
		if (description != null && description.startsWith("<html>First")) {
			new Exception().printStackTrace();
		}
		return description;
	}

	public void setDescription(String description) {
		if (!stringHasChanged(this.description, description)) {
			return;
		}
		String old = this.description;
		if (old == null && description == null) {
			return;
		}
		if (old == null || !old.equals(description)) {
			this.description = description;
			setChanged();
			notifyObservers(new DataModification("description", old, description));
		}
	}

	public String getFullDescriptionWithOnlyBodyContent(String specificDescriptionType) {
		StringBuilder sb = new StringBuilder();

		if (getDescription() != null) {
			String description = HTMLUtils.extractBodyContent(getDescription());
			sb.append(description != null ? description : getDescription());
		}

		if (getHasSpecificDescriptions() && specificDescriptionType != null
				&& getSpecificDescriptionForKey(specificDescriptionType) != null) {
			String specifDesc = HTMLUtils.extractBodyContent(getSpecificDescriptionForKey(specificDescriptionType));
			sb.append(specifDesc != null ? specifDesc : getSpecificDescriptionForKey(specificDescriptionType));
		}

		return sb.toString().trim();
	}

	public Map<String, String> getSpecificDescriptions() {
		return specificDescriptions;
	}

	public void setSpecificDescriptions(Map<String, String> specificDescriptions) {
		this.specificDescriptions = new TreeMap<String, String>(specificDescriptions);
	}

	public Vector<FlexoProperty> getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(Vector<FlexoProperty> customProperties) {
		if (this.customProperties != null) {
			for (FlexoProperty property : this.customProperties) {
				property.setOwner(null);
			}
		}
		this.customProperties = customProperties;
		if (this.customProperties != null) {
			for (FlexoProperty property : new Vector<FlexoProperty>(this.customProperties)) {
				property.setOwner(this);
			}
		}

	}

	public void addToCustomProperties(FlexoProperty property) {
		addToCustomProperties(property, false);
	}

	public void addToCustomProperties(FlexoProperty property, boolean insertSorted) {
		if (insertSorted && property.getName() != null) {
			int i = 0;
			for (FlexoProperty p : customProperties) {
				if (p.getName() != null && p.getName().compareTo(property.getName()) > 0) {
					break;
				}
				i++;
			}
			customProperties.insertElementAt(property, i);
		} else {
			customProperties.add(property);
		}
		if (property != null) {
			property.setOwner(this);
		}
		setChanged();
		DataModification dm = new DataModification("customProperties", null, property);
		notifyObservers(dm);
	}

	public void removeFromCustomProperties(FlexoProperty property) {
		customProperties.remove(property);
	}

	public boolean hasPropertyNamed(String name) {
		return getPropertyNamed(name) != null;
	}

	public FlexoProperty getPropertyNamed(String name) {
		if (name == null) {
			for (FlexoProperty p : getCustomProperties()) {
				if (p.getName() == null) {
					return p;
				}
			}
		} else {
			for (FlexoProperty p : getCustomProperties()) {
				if (name.equals(p.getName())) {
					return p;
				}
			}
		}
		return null;
	}

	public Vector<FlexoProperty> getProperties(String name) {
		Vector<FlexoProperty> v = new Vector<FlexoProperty>();
		if (name == null) {
			for (FlexoProperty p : getCustomProperties()) {
				if (p.getName() == null) {
					v.add(p);
				}
			}
		} else {
			for (FlexoProperty p : getCustomProperties()) {
				if (name.equals(p.getName())) {
					v.add(p);
				}
			}
		}
		return v;
	}

	public void addProperty() {
		if (addFlexoPropertyActionizer != null) {
			addFlexoPropertyActionizer.run(this, null);
		}
	}

	public boolean canSortProperties() {
		return customProperties.size() > 1;
	}

	public void sortProperties() {
		if (sortFlexoPropertiesActionizer != null) {
			sortFlexoPropertiesActionizer.run(this, null);
		}
	}

	public void deleteProperty(FlexoProperty property) {
		if (deleteFlexoPropertyActionizer != null) {
			deleteFlexoPropertyActionizer.run(property, null);
		}
	}

	public boolean hasDescription() {
		return getDescription() != null && getDescription().trim().length() > 0;
	}

	/**
	 * This property is used by the hightlightUncommentedItem mode. The decoration meaning that the description is missing will only appears
	 * on object for wich this method return true. So this method has to be overridden in subclass.
	 * 
	 * @return
	 */
	public boolean isDescriptionImportant() {
		return false;
	}

	public boolean hasSpecificHelp(String key) {
		return getSpecificDescriptionForKey(key) != null && getSpecificDescriptionForKey(key).length() > 0;
	}

	public boolean hasSpecificDescriptionForKey(String key) {
		return getSpecificDescriptionForKey(key) != null && getSpecificDescriptionForKey(key).trim().length() > 0;
	}

	public String getUserManualDescription() {
		return getSpecificDescriptionForKey(DocType.DefaultDocType.UserManual.name());
	}

	public String getTechnicalDescription() {
		return getSpecificDescriptionForKey(DocType.DefaultDocType.Technical.name());
	}

	public String getBusinessDescription() {
		return getSpecificDescriptionForKey(DocType.DefaultDocType.Business.name());
	}

	/**
	 * @param businessDescription
	 */
	public void setBusinessDescription(String businessDescription) {
		if (businessDescription != null) {
			setSpecificDescriptionsForKey(businessDescription, DocType.DefaultDocType.Business.name());
		} else {
			removeSpecificDescriptionsWithKey(DocType.DefaultDocType.Business.name());
		}
		setChanged();
		notifyObservers(new DataModification("businessDescription", null, businessDescription));
	}

	/**
	 * @param technicalDescription
	 */
	public void setTechnicalDescription(String technicalDescription) {
		if (technicalDescription != null) {
			setSpecificDescriptionsForKey(technicalDescription, DocType.DefaultDocType.Technical.name());
		} else {
			removeSpecificDescriptionsWithKey(DocType.DefaultDocType.Technical.name());
		}
		setChanged();
		notifyObservers(new DataModification("technicalDescription", null, technicalDescription));
	}

	/**
	 * @param userManualDescription
	 */
	public void setUserManualDescription(String userManualDescription) {
		if (userManualDescription != null) {
			setSpecificDescriptionsForKey(userManualDescription, DocType.DefaultDocType.UserManual.name());
		} else {
			removeSpecificDescriptionsWithKey(DocType.DefaultDocType.UserManual.name());
		}
		setChanged();
		notifyObservers(new DataModification("userManualDescription", null, userManualDescription));
	}

	public String getSpecificDescriptionForKey(String key) {
		return specificDescriptions.get(key);
	}

	public void setSpecificDescriptionsForKey(String description, String key) {
		specificDescriptions.put(key, description);
		setChanged();
		DataModification dm = new DataModification("specificDescriptions", null, description);
		notifyObservers(dm);
	}

	public void removeSpecificDescriptionsWithKey(String key) {
		specificDescriptions.remove(key);
	}

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

	public Vector<FlexoModelObjectReference> getReferencers() {
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

	public boolean getHasSpecificDescriptions() {
		return hasSpecificDescriptions;
	}

	public void setHasSpecificDescriptions(boolean hasSpecificDescription) {
		if (this.hasSpecificDescriptions == hasSpecificDescription) {
			return;
		}
		boolean old = this.hasSpecificDescriptions;
		this.hasSpecificDescriptions = hasSpecificDescription;
		setChanged();
		notifyObservers(new DataModification("hasSpecificDescriptions", old, hasSpecificDescription));
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

	protected <T> boolean requireChange(T oldValue, T newValue) {
		return oldValue == null && newValue != null || oldValue != null && newValue == null || oldValue != null && newValue != null
				&& !oldValue.equals(newValue);
	}

	public String getNextPropertyName() {
		String base = FlexoLocalization.localizedForKey("property");
		String attempt = base;
		int i = 1;
		while (getPropertyNamed(attempt) != null) {
			attempt = base + "-" + i++;
		}
		return attempt;
	}

	private Vector<EditionPatternReference> _editionPatternReferences;

	public Vector<EditionPatternReference> getEditionPatternReferences() {
		return _editionPatternReferences;
	}

	public void setEditionPatternReferences(Vector<EditionPatternReference> editionPatternReferences) {
		_editionPatternReferences = editionPatternReferences;
	}

	public boolean addToEditionPatternReferences(EditionPatternReference e) {
		return _editionPatternReferences.add(e);
	}

	public boolean removeFromEditionPatternReferences(EditionPatternReference o) {
		return _editionPatternReferences.remove(o);
	}

	public EditionPatternReference getEditionPatternReference(String editionPatternId, long instanceId) {
		if (editionPatternId == null) {
			return null;
		}
		if (_editionPatternReferences == null) {
			return null;
		}
		for (EditionPatternReference r : _editionPatternReferences) {
			if (r.getEditionPattern().getName().equals(editionPatternId) && r.getInstanceId() == instanceId) {
				return r;
			}
		}
		return null;
	}

	public EditionPatternReference getEditionPatternReference(EditionPatternInstance epInstance) {
		if (_editionPatternReferences == null) {
			logger.warning("Unexpected _editionPatternReferences=null !!!");
			return null;
		}
		for (EditionPatternReference r : _editionPatternReferences) {
			if (r.getEditionPatternInstance() == epInstance) {
				return r;
			}
		}
		return null;
	}

	// Return first one if many
	public EditionPatternReference getEditionPatternReference(String editionPatternId) {
		if (editionPatternId == null) {
			return null;
		}
		for (EditionPatternReference r : _editionPatternReferences) {
			if (r.getEditionPattern().getName().equals(editionPatternId)) {
				return r;
			}
		}
		return null;
	}

	// Return first one if many
	public EditionPatternReference getEditionPatternReference(EditionPattern editionPattern) {
		if (editionPattern == null) {
			return null;
		}
		for (EditionPatternReference r : _editionPatternReferences) {
			// System.out.println("1: " + r.getEditionPattern().getName() + "  2: " + editionPattern.getName());
			if (r.getEditionPattern().getName().equals(editionPattern.getName())) {
				return r;
			}
		}
		return null;
	}

	public void registerEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole) {
		EditionPatternReference existingReference = getEditionPatternReference(editionPatternInstance);
		if (existingReference == null) {
			// logger.info("registerEditionPatternReference for " + editionPatternInstance.debug());
			EditionPatternReference newReference = new EditionPatternReference(editionPatternInstance, patternRole);
			addToEditionPatternReferences(newReference);
			setChanged();
		} else {
			if (existingReference.getPatternRole() != patternRole) {
				logger.warning("Called for register a new EditionPatternReference with an already existing EditionPatternReference with a different PatternRole");
			}
		}
	}

	public void unregisterEditionPatternReference(EditionPatternInstance editionPatternInstance, PatternRole patternRole) {
		EditionPatternReference referenceToRemove = getEditionPatternReference(editionPatternInstance);
		if (referenceToRemove == null) {
			logger.warning("Called for unregister EditionPatternReference for unexisting reference to edition pattern instance EP="
					+ editionPatternInstance.getPattern().getName() + " id=" + editionPatternInstance.getInstanceId());
			for (EditionPatternReference ref : getEditionPatternReferences()) {
				logger.warning("* Reference:");
				logger.warning(ref.debug());
			}
		} else {
			removeFromEditionPatternReferences(referenceToRemove);
			setChanged();
		}
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

	/**
	 * Return true is this object is somewhere involved as a primary representation pattern role in any of its EditionPatternReferences
	 * 
	 * @return
	 */
	public boolean providesSupportAsPrimaryRole() {
		if (getEditionPatternReferences() != null) {
			if (getEditionPatternReferences().size() > 0) {
				for (EditionPatternReference r : getEditionPatternReferences()) {
					if (r.getPatternRole() == null) {
						logger.warning("Found an EditionPatternReference with a null pattern role. Please investigate...");
					} else if (r.getPatternRole().getIsPrimaryRole()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}