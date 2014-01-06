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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.FGEUtils;
import org.openflexo.foundation.action.FlexoActionnable;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMVisibilityType;
import org.openflexo.foundation.dm.eo.DMEOAdaptorType;
import org.openflexo.foundation.dm.eo.DeleteRuleType;
import org.openflexo.foundation.dm.eo.JoinSemanticType;
import org.openflexo.foundation.ie.util.ClientSideEventType;
import org.openflexo.foundation.ie.util.DateFormatType;
import org.openflexo.foundation.ie.util.DropDownType;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.ie.util.HyperlinkActionType;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.util.IEControlOperator;
import org.openflexo.foundation.ie.util.TDCSSType;
import org.openflexo.foundation.ie.util.TRCSSType;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.ie.util.TextFieldClass;
import org.openflexo.foundation.ie.util.TextFieldFormatType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.ScreenshotResource;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.utils.FlexoDocFormat;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.node.ActionType;
import org.openflexo.foundation.wkf.node.LoopType;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.inspector.model.TabModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.converter.RectangleConverter;
import org.openflexo.swing.FlexoFont;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Abstract class for all objects involved in FLEXO model definition, as a part of a project, and XML serializable
 * 
 * @author sguerin
 * 
 */
public abstract class AgileBirdsObject extends FlexoXMLSerializableObject implements FlexoActionnable {

	private static final Logger logger = FlexoLogger.getLogger(AgileBirdsObject.class.getPackage().toString());

	public static final String ID_SEPARATOR = "_";

	public static final Comparator<AgileBirdsObject> DEFAULT_COMPARATOR = new FlexoDefaultComparator<AgileBirdsObject>();

	public static final Comparator<AgileBirdsObject> NAME_COMPARATOR = new FlexoNameComparator<AgileBirdsObject>();

	private long flexoID = -2;

	private transient Object _context;

	private static String currentUserIdentifier;

	private boolean isRegistered = false;

	// Imported objects
	private final boolean isDeletedOnServer = false;

	private String uri;

	private String versionURI;

	private String uriFromSourceObject;

	private boolean dontGenerate = false;
	private FlexoDocFormat docFormat;

	private static boolean isInitialized = false;

	static {
		initialize(true);
	}

	public static void initialize(boolean force) {
		if (!isInitialized || force) {
			initialize(StringEncoder.getDefaultInstance());
			isInitialized = true;
		}
	}

	public static void initialize(StringEncoder encoder) {
		// Ce qui suit (date format) est très important: utilise par le RM: ne pas toucher
		encoder._setDateFormat("HH:mm:ss dd/MM/yyyy");
		encoder._addConverter(Language.languageConverter);
		encoder._addConverter(ResourceType.resourceTypeConverter);
		encoder._addConverter(FileFormat.fileFormatConverter);
		encoder._addConverter(CodeType.codeTypeConverter);
		encoder._addConverter(NodeType.nodeTypeConverter);
		encoder._addConverter(ActionType.actionTypeConverter);
		encoder._addConverter(LoopType.loopTypeConverter);
		encoder._addConverter(FlexoFont.flexoFontConverter);
		encoder._addConverter(FlexoCSS.flexoCSSConverter);
		encoder._addConverter(DropDownType.dropdownTypeConverter);
		encoder._addConverter(HyperlinkType.hyperlinkTypeConverter);
		encoder._addConverter(ClientSideEventType.ClientSideEventTypeConverter);
		encoder._addConverter(TDCSSType.tdCSSTypeConverter);
		encoder._addConverter(TextCSSClass.textCSSClassConverter);
		encoder._addConverter(TextFieldFormatType.textFieldFormatTypeConverter);
		encoder._addConverter(TextFieldType.textFieldTypeConverter);
		encoder._addConverter(TRCSSType.trCSSTypeConverter);
		encoder._addConverter(TextFieldClass.textFieldClassConverter);
		encoder._addConverter(DMCardinality.cardinalityConverter);
		encoder._addConverter(DMPropertyImplementationType.propertyImplementationTypeConverter);
		encoder._addConverter(DMVisibilityType.visibilityTypeConverter);
		encoder._addConverter(DMEOAdaptorType.adaptorTypeConverter);
		encoder._addConverter(DeleteRuleType.deleteRuleTypeConverter);
		encoder._addConverter(JoinSemanticType.joinSemanticTypeConverter);
		encoder._addConverter(HyperlinkActionType.hyperlinkActionTypeConverter);
		encoder._addConverter(IEControlOperator.controlOperatorConverter);
		encoder._addConverter(FlexoDocFormat.flexoDocFormatConverter);
		encoder._addConverter(FolderType.folderTypeConverter);
		encoder._addConverter(DateFormatType.DateFormatTypeConverter);
		encoder._addConverter(RectangleConverter.instance);
		encoder._addConverter(FlexoProjectFile.converter);
		encoder._addConverter(FlexoVersion.converter);
		encoder._addConverter(CGVersionIdentifier.converter);
		encoder._addConverter(Duration.converter);
		encoder._addConverter(DataBinding.CONVERTER);
		encoder._addConverter(FGEUtils.DEPRECATED_POINT_CONVERTER);
		encoder._addConverter(FGEUtils.DEPRECATED_RECT_POLYLIN_CONVERTER);
	}

	public static String getCurrentUserIdentifier() {
		if (currentUserIdentifier == null) {
			currentUserIdentifier = "FLX".intern();
		}
		return currentUserIdentifier;
	}

	public static void setCurrentUserIdentifier(String aUserIdentifier) {
		if (aUserIdentifier != null && aUserIdentifier.indexOf('#') > -1) {
			aUserIdentifier = aUserIdentifier.replace('#', '-');
			AgileBirdsObject.currentUserIdentifier = aUserIdentifier.intern();
		}
	}

	private String userIdentifier;

	@Override
	public String getUserIdentifier() {
		if (userIdentifier == null) {
			return getCurrentUserIdentifier();
		}
		return userIdentifier;
	}

	@Override
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
	public AgileBirdsObject() {
		this(null);
	}

	public AgileBirdsObject(FlexoProject project) {
		super(project);
		referencers = new Vector<FlexoObjectReference<?>>();
		registerObject(project);
	}

	public AgileBirdsObject(FlexoProject project, String description) {
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

	public boolean isCache() {
		return false;
	}

	public AgileBirdsObject getUncachedObject() {
		if (!isCache()) {
			return this;
		} else {
			throw new RuntimeException("Object of type " + getClass().getName()
					+ " is cached but does not properly override getUncachedObject()! (" + this + ")");
		}
	}

	public static <O extends AgileBirdsObject> O getObjectWithURI(Vector<O> objects, String uri) {
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
		if (versionURI != null) {
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

	// @Override
	// public abstract String getFullyQualifiedName();

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
	@Override
	public long getFlexoID() {
		if (isBeingCloned()) {
			return -1;
		}
		if (flexoID < 0 && !isDeserializing() && getXMLResourceData() != null
				&& getXMLResourceData().getResource() instanceof FlexoXMLFileResource) {
			flexoID = ((FlexoXMLFileResource) getXMLResourceData().getResource()).getNewFlexoID();
		}
		if (getProject() != null) {
			if (flexoID < 0 && getProject().getLastUniqueIDHasBeenSet() && !isDeserializing()) {
				flexoID = getProject().getNewFlexoID();
				logger.info(">>>>>>>>>>>>>>> Registering new FlexoID " + flexoID + " for " + this);
				System.out.println(">>>>>>>>>>>>>>> Registering new FlexoID " + flexoID + " for " + this);
				// setChanged();
				if (!isRegistered) {
					registerObject(getProject());
					isRegistered = true;
				}
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
	@Override
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
		for (FlexoObjectReference ref : new Vector<FlexoObjectReference>(referencers)) {
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
	public boolean delete() {
		// if (logger.isLoggable(Level.FINE)) logger.fine ("Delete
		// "+this.getClass().getName()+" : "+this);
		if (isDeleted()) {
			// in some case : the delete may be called twice on a sequence (in case of deletion of last widget of the sequence)...
			// and it will fail
			// a good idea would be to avoid this double invocation.
			// In the mean time, this little hack will do the trick.
			return false;

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
			for (FlexoObjectReference ref : new ArrayList<FlexoObjectReference>(referencers)) {
				ref.notifyObjectDeletion();
			}
			referencers.clear();
		}
		referencers = null;

		super.delete();

		/*setChanged();
		notifyObservers(new ObjectDeleted(this));*/
		if (getProject() != null) {
			getProject().notifyObjectDeleted(this);
		}
		return true;

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
		referencers = new Vector<FlexoObjectReference<?>>();
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

	public static class FlexoDefaultComparator<E extends AgileBirdsObject> implements Comparator<E> {
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

	public static class FlexoNameComparator<E extends AgileBirdsObject> implements Comparator<E> {
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

	public final int compareTo(AgileBirdsObject o2) {
		if (getFlexoID() > o2.getFlexoID()) {
			return 1;
		} else if (getFlexoID() < o2.getFlexoID()) {
			return -1;
		} else {
			return 0;
		}
	}

	public final int compare(AgileBirdsObject o1, AgileBirdsObject o2) {
		return o1.compareTo(o2);
	}

	// ============================================
	// ============== Access to help ==============
	// ============================================

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
	 * @return a Vector of AgileBirdsObject instances
	 */
	public Collection<AgileBirdsObject> getEmbeddedObjects() {
		return getXMLMapping().getEmbeddedObjectsForObject(this, AgileBirdsObject.class);
	}

	/**
	 * Returns a vector of all objects that will be deleted if you call delete on this object.
	 * 
	 * @return
	 */
	public Collection<AgileBirdsObject> getAllRecursivelyEmbeddedDeletedObjects() {
		return getXMLMapping().getEmbeddedObjectsForObject(this, AgileBirdsObject.class, false, true);
	}

	public Collection<AgileBirdsObject> getAllRecursivelyEmbeddedObjects() {
		return getAllRecursivelyEmbeddedObjects(false);
	}

	public Collection<AgileBirdsObject> getAllRecursivelyEmbeddedObjects(boolean maintainNaturalOrder) {
		return getXMLMapping().getEmbeddedObjectsForObject(this, AgileBirdsObject.class, maintainNaturalOrder, true);
	}

	/**
	 * Return a vector of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> vector = new Vector<Validable>();
		for (AgileBirdsObject o : getAllRecursivelyEmbeddedObjects()) {
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
		return FlexoObjectReference.getSerializationRepresentationForObject(this, true);
	}

	/**
	 * 
	 * Notify ResourceManager by forwarding {@link RMNotification} to the related resource, if and only if this object represents the
	 * resource data itself (@see org.openflexo.foundation.rm.FlexoResourceData). Otherwise, invoking this method is ignored.
	 * 
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void notifyRM(RMNotification notification) throws FlexoException {
		if (this instanceof FlexoResourceData && ((FlexoResourceData) this).getFlexoResource() != null) {
			((FlexoResourceData) this).getFlexoResource().notifyRM(notification);
		}
	}

	/**
	 * Receive a notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource
	 * This method is relevant if and only if this object represents the resource data itself (@see
	 * org.openflexo.foundation.rm.FlexoResourceData). At this level, this method is ignored and just return, so you need to override it in
	 * subclasses if you want to get the hook to do your stuff !
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		// Ignore it at this level: please overrides this method in relevant
		// subclasses !
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

	// PRI REQUIREMENT
	/**
	 * @param freePropertiesDescription
	 */
	public void setFreePropertiesDescription(String freePropertiesDescription) {
		if (freePropertiesDescription != null) {
			setSpecificDescriptionsForKey(freePropertiesDescription, DocType.DefaultDocType.FreeProperties.name());
		} else {
			removeSpecificDescriptionsWithKey(DocType.DefaultDocType.FreeProperties.name());
		}
		setChanged();
		notifyObservers(new DataModification("freePropertiesDescription", null, freePropertiesDescription));
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

	@Override
	public List<DocType> getDocTypes() {
		return null;
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

}
