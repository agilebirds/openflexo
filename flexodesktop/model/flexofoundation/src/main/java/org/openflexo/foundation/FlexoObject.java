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
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.DeleteFlexoProperty;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.action.SortFlexoProperties;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HTMLUtils;

/**
 * Super class for any object involved in Openflexo in model layer<br>
 * 
 * This abstract class represents an object, or "data" in the model-view paradigm.<br>
 * 
 * A {@link FlexoObject} always references its ResourceData (the global functional root object)
 * 
 * This class provides default implementation for validation (see {@link Validable} interface). When relevant, just extends interface
 * {@link Validable} and implements both methods {@link Validable#getDefaultValidationModel()} and
 * {@link Validable#getAllEmbeddedValidableObjects()}
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoObject extends FlexoObservable {

	private static final Logger logger = Logger.getLogger(FlexoObject.class.getPackage().getName());

	protected boolean isDeleted = false;
	private boolean ignoreNotifications = false;
	private boolean isModified = false;
	private Date lastMemoryUpdate = null;

	private Object context;

	private String description;

	private Map<String, String> specificDescriptions;
	private Vector<FlexoProperty> customProperties;
	private boolean hasSpecificDescriptions = false;

	/**
	 * A map that stores the different declared actions for each class
	 */
	private static final Map<Class, List<FlexoActionType<?, ?, ?>>> _declaredActionsForClass = new Hashtable<Class, List<FlexoActionType<?, ?, ?>>>();

	/**
	 * A map that stores all the actions for each class (computed with the inheritance of each class)
	 */
	private static final Hashtable<Class, List<FlexoActionType<?, ?, ?>>> _actionListForClass = new Hashtable<Class, List<FlexoActionType<?, ?, ?>>>();

	public static FlexoActionizer<AddFlexoProperty, FlexoObject, FlexoObject> addFlexoPropertyActionizer;

	public static FlexoActionizer<DeleteFlexoProperty, FlexoProperty, FlexoProperty> deleteFlexoPropertyActionizer;

	public static FlexoActionizer<SortFlexoProperties, FlexoObject, FlexoObject> sortFlexoPropertiesActionizer;

	/**
	 * Default constructor for {@link FlexoObject}
	 */
	public FlexoObject() {
		specificDescriptions = new TreeMap<String, String>();
		customProperties = new Vector<FlexoProperty>();
	}

	@Override
	public final String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	/**
	 * Returns fully qualified name for this object NOTE: not used anymore, should disappear
	 * 
	 * @return
	 */
	// @Deprecated
	// public abstract String getFullyQualifiedName();

	/**
	 * Abstract implementation of delete<br>
	 * This method should be overriden.<br>
	 * At this level, only manage {@link #isDeleted()} feature
	 * 
	 * @return flag indicating if deletion has successfully been performed
	 */
	public boolean delete() {
		setChanged();
		notifyObservers(new ObjectDeleted(this));
		if (isDeleted()) {
			return false;
		}
		isDeleted = true;
		return true;
	}

	/**
	 * Return a flag indicating if this object was deleted
	 * 
	 * @return
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * Test if changing a value from oldValue to newValue is significant
	 * 
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	protected <T> boolean requireChange(T oldValue, T newValue) {
		return oldValue == null && newValue != null || oldValue != null && newValue == null || oldValue != null && newValue != null
				&& !oldValue.equals(newValue);
	}

	/**
	 * Test if changing a value from old to newString is significant
	 * 
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	// TODO: to be merged with requireChange() ???
	public static boolean stringHasChanged(String old, String newString) {
		return old == null && newString != null || old != null && !old.equals(newString);
	}

	public boolean ignoreNotifications() {
		return ignoreNotifications;
	}

	/**
	 * Temporary method to prevent notifications. This should never be used by anyone except the screenshot generator
	 */
	public synchronized void setIgnoreNotifications() {
		ignoreNotifications = true;
	}

	/**
	 * Temporary method to reactivate notifications. This should never be used by anyone except the screenshot generator
	 */
	public synchronized void resetIgnoreNotifications() {
		ignoreNotifications = false;
	}

	public boolean isModified() {
		return isModified;
	}

	public synchronized void setIsModified() {
		if (ignoreNotifications) {
			return;
		}
		isModified = true;
		lastMemoryUpdate = new Date();
	}

	public synchronized void clearIsModified() {
		clearIsModified(false);
	}

	public synchronized void clearIsModified(boolean clearLastMemoryUpdate) {
		isModified = false;
		// GPO: I commented the line hereunder because I don't think that we need to reset this date
		if (clearLastMemoryUpdate) {
			lastMemoryUpdate = null;
		}
	}

	/**
	 * Return date of last update in memory
	 * 
	 * @return
	 */
	public Date lastMemoryUpdate() {
		return lastMemoryUpdate;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	// ***************************************************
	// Action management
	// ***************************************************

	public final List<FlexoActionType<?, ?, ?>> getActionList() {
		return getActionList(getClass());
	}

	public static <T extends FlexoObject> List<FlexoActionType<?, ?, ?>> getActionList(Class<T> aClass) {
		if (_actionListForClass.get(aClass) == null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("COMPUTE ACTION_LIST FOR " + aClass.getName());
			}
			List<FlexoActionType<?, ?, ?>> returned = updateActionListFor(aClass);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("DONE. COMPUTE ACTION_LIST FOR " + aClass.getName() + ": " + returned.size() + " action(s) :");
				for (FlexoActionType<?, ?, ?> next : returned) {
					logger.fine(" " + next.getLocalizedName());
				}
				logger.fine(".");
			}
			return returned;
		}
		List<FlexoActionType<?, ?, ?>> returned = _actionListForClass.get(aClass);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("RETURN (NO COMPUTING) ACTION_LIST FOR " + aClass.getName() + ": " + returned.size() + " action(s) :");

			for (FlexoActionType<?, ?, ?> next : returned) {
				logger.fine(" " + next.getLocalizedName());
			}
			logger.fine(".");
		}
		return returned;
	}

	public static <T1 extends FlexoObject, T extends T1> void addActionForClass(FlexoActionType<?, T1, ?> actionType, Class<T> objectClass) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("addActionForClass: " + actionType + " for " + objectClass);
		}
		List<FlexoActionType<?, ?, ?>> actions = _declaredActionsForClass.get(objectClass);
		if (actions == null) {
			actions = new ArrayList<FlexoActionType<?, ?, ?>>();
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

	private static <T extends FlexoObject> List<FlexoActionType<?, ?, ?>> updateActionListFor(Class<T> aClass) {
		List<FlexoActionType<?, ?, ?>> newActionList = new Vector<FlexoActionType<?, ?, ?>>();
		for (Map.Entry<Class, List<FlexoActionType<?, ?, ?>>> e : _declaredActionsForClass.entrySet()) {
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

	public int getActionCount() {
		return getActionList().size();
	}

	public FlexoActionType<?, ?, ?> getActionTypeAt(int index) {
		return getActionList().get(index);
	}

	// ***************************************************
	// Documentation management
	// ***************************************************

	/**
	 * Returns wheter this object is imported or not. Object implementing FlexoImportableObject should override this method
	 * 
	 * @return true if this object is imported.
	 */
	public boolean isImported() {
		return false;
	}

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

	public Map<String, String> getSpecificDescriptions() {
		return specificDescriptions;
	}

	public void setSpecificDescriptions(Map<String, String> specificDescriptions) {
		this.specificDescriptions = new TreeMap<String, String>(specificDescriptions);
	}

	/**
	 * @return
	 */
	public String getLocalizedClassName() {
		return FlexoLocalization.localizedForKey(getClass().getSimpleName());
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

	public String getNextPropertyName() {
		String base = FlexoLocalization.localizedForKey("property");
		String attempt = base;
		int i = 1;
		while (getPropertyNamed(attempt) != null) {
			attempt = base + "-" + i++;
		}
		return attempt;
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

	// ***************************************************
	// Validable support
	// ***************************************************

	/**
	 * Returns a flag indicating if this object is valid according to default validation model<br>
	 * This method only works if this is an instance of {@link Validable} interface, otherwise return true
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		if (this instanceof Validable) {
			return isValid(((Validable) this).getDefaultValidationModel());
		}
		return true;
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model<br>
	 * This method only works if this is an instance of {@link Validable} interface, otherwise return true
	 * 
	 * @return boolean
	 */
	public boolean isValid(ValidationModel validationModel) {
		if (this instanceof Validable) {
			return validationModel.isValid((Validable) this);
		}
		return true;
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.<br>
	 * This method only works if this is an instance of {@link Validable} interface, otherwise return null
	 */
	public ValidationReport validate() {
		if (this instanceof Validable) {
			return validate(((Validable) this).getDefaultValidationModel());
		}
		return null;
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.<br>
	 * This method only works if this is an instance of {@link Validable} interface, otherwise return null
	 */
	public ValidationReport validate(ValidationModel validationModel) {
		if (this instanceof Validable) {
			return validationModel.validate((Validable) this);
		}
		return null;
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	public void validate(ValidationReport report) {
		if (this instanceof Validable) {
			validate(report, ((Validable) this).getDefaultValidationModel());
		}
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	public void validate(ValidationReport report, ValidationModel validationModel) {
		if (this instanceof Validable) {
			validationModel.validate((Validable) this, report);
		}
	}

	public Collection<Validable> getAllEmbeddedValidableObjects() {
		List<Validable> returned = new ArrayList<Validable>();
		if (this instanceof Validable) {
			appendAllEmbeddedValidableObjects((Validable) this, returned);
		}
		return returned;
	}

	private void appendAllEmbeddedValidableObjects(Validable o, Collection<Validable> c) {
		c.add(o);
		Collection<? extends Validable> embeddedObjects = o.getEmbeddedValidableObjects();
		if (embeddedObjects != null) {
			for (Validable o2 : embeddedObjects) {
				appendAllEmbeddedValidableObjects(o2, c);
			}
		}
	}

	private static HelpRetriever _helpRetriever = null;

	public static interface HelpRetriever {
		public String shortHelpForObject(FlexoObject object);

		public String longHelpForObject(FlexoObject object);
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

	private static String currentUserIdentifier;

	public static String getCurrentUserIdentifier() {
		if (currentUserIdentifier == null) {
			currentUserIdentifier = "FLX".intern();
		}
		return currentUserIdentifier;
	}

	public static void setCurrentUserIdentifier(String aUserIdentifier) {
		if (aUserIdentifier != null && aUserIdentifier.indexOf('#') > -1) {
			aUserIdentifier = aUserIdentifier.replace('#', '-');
			currentUserIdentifier = aUserIdentifier.intern();
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
	}

	private long flexoID = -1;

	/**
	 * Returns the flexoID.
	 * 
	 * @return
	 */
	public long getFlexoID() {
		if (flexoID < 0) {
			if (this instanceof InnerResourceData && ((InnerResourceData) this).getResourceData() != null
					&& ((InnerResourceData) this).getResourceData().getResource() instanceof PamelaResource) {
				flexoID = ((PamelaResource<?, ?>) ((InnerResourceData) this).getResourceData().getResource()).getNewFlexoID();
			}
		}
		return flexoID;
	}

	/**
	 * Sets the flexoID
	 * 
	 * @param flexoID
	 *            The flexoID to set.
	 */
	public void setFlexoID(long flexoID) {
		if (flexoID > 0 && flexoID != this.flexoID) {
			long oldId = this.flexoID;
			this.flexoID = flexoID;
			if (this instanceof InnerResourceData && ((InnerResourceData) this).getResourceData() != null
					&& ((InnerResourceData) this).getResourceData().getResource() instanceof PamelaResource) {
				// TODO sets last id of resource ?
			}
		}
	}

}
