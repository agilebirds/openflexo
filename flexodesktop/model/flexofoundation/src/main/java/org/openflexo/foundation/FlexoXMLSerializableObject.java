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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.Bindable;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.FlexoBuilder;
import org.openflexo.foundation.xml.FlexoXMLSerializable;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * This abstract class represents an object, or "data" in the model-view paradigm. That can be serialized/deserialized through XMLCoDe
 * scheme
 * 
 * @author sguerin
 * 
 */
public abstract class FlexoXMLSerializableObject extends FlexoProjectObject implements FlexoXMLSerializable {

	private static final Logger logger = Logger.getLogger(FlexoXMLSerializableObject.class.getPackage().getName());

	protected boolean isDeserializing = false;

	protected boolean isSerializing = false;

	private boolean isModified;

	private Date lastMemoryUpdate;

	private Thread serializingThread;

	public FlexoXMLSerializableObject() {
		super();
	}

	public FlexoXMLSerializableObject(FlexoProject project) {
		super(project);
	}

	@Override
	public abstract XMLMapping getXMLMapping();

	@Override
	public Object instanciateNewBuilder() {
		return getXMLResourceData().getFlexoXMLFileResource().instanciateNewBuilder();
	}

	/*
	 * public FlexoXMLSerializableObject cloneUsingXMLMapping() { String
	 * xmlRepresentation = getXMLRepresentation(); if
	 * (getXMLMapping().hasBuilderClass()) { Object builder =
	 * instanciateNewBuilder(); if (builder == null) { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning ("Model for encoding
	 * "+getClass().getName()+" defines a builder while builder instanciation
	 * returns null"); } if
	 * (getXMLMapping().builderClass().isAssignableFrom(builder.getClass())) {
	 * return instanciateFromXML
	 * (xmlRepresentation,getXMLMapping(),instanciateNewBuilder()); } else { if
	 * (logger.isLoggable(Level.WARNING)) logger.warning ("Model for encoding
	 * "+getClass().getName() +" defines a builder of class "
	 * +getXMLMapping().builderClass().getName() +" while builder instanciation
	 * returns an object of class " +builder.getClass().getName()); } return
	 * null; } else { return
	 * instanciateFromXML(xmlRepresentation,getXMLMapping()); } }
	 */

	@Override
	public FlexoXMLSerializable cloneUsingXMLMapping() {
		return cloneUsingXMLMapping(null, true, getXMLMapping());
	}

	public FlexoXMLSerializable cloneUsingXMLMapping(boolean setIsBeingCloned) {
		return cloneUsingXMLMapping(null, setIsBeingCloned, getXMLMapping());
	}

	public FlexoXMLSerializable cloneUsingXMLMapping(Object builder, boolean setIsBeingCloned, XMLMapping xmlMapping) {
		if (setIsBeingCloned) {
			if (getXMLResourceData() != null) {
				getXMLResourceData().initializeCloning();
			} else {
				isBeingCloned = true;
			}
		}
		if (xmlMapping.hasBuilderClass()) {
			if (builder == null) {
				builder = instanciateNewBuilder();
			}
			if (builder instanceof FlexoBuilder) {
				((FlexoBuilder<FlexoXMLStorageResource>) builder).isCloner = true;
			}
			if (xmlMapping.builderClass().isAssignableFrom(builder.getClass())) {
				try {

					FlexoXMLSerializableObject obj = (FlexoXMLSerializableObject) Cloner.cloneObjectWithMapping(this, xmlMapping, builder,
							getStringEncoder());
					return obj;
				} catch (Exception e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
					return null;
				} finally {
					if (getXMLResourceData() != null) {
						getXMLResourceData().finalizeCloning();
					}
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Model for encoding " + getClass().getName() + " defines a builder of class "
							+ xmlMapping.builderClass().getName() + " while builder instanciation returns an object of class "
							+ builder.getClass().getName());
				}
			}
			if (getXMLResourceData() != null) {
				getXMLResourceData().finalizeCloning();
			}
			isBeingCloned = false;
			return null;
		} else {
			try {
				FlexoXMLSerializableObject obj = (FlexoXMLSerializableObject) Cloner.cloneObjectWithMapping(this, xmlMapping,
						StringEncoder.getDefaultInstance(), getStringEncoder());
				return obj;
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
				return null;
			} finally {
				if (getXMLResourceData() != null) {
					getXMLResourceData().finalizeCloning();
				}
				isBeingCloned = false;
			}
		}
	}

	public String getXMLRepresentation() {
		try {
			initializeSerialization();
			String returned = XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), getStringEncoder(), null);
			finalizeSerialization();
			return returned;
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
			return null;
		}
	}

	public void saveToFile(File aFile) {
		FileOutputStream out = null;
		try {
			if (!aFile.getParentFile().exists()) {
				aFile.getParentFile().mkdirs();
			}
			out = new FileOutputStream(aFile);
			XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), out, getStringEncoder());
			out.flush();
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static FlexoXMLSerializableObject instanciateFromXML(String anXMLRepresentation, XMLMapping mapping, Object builder,
			StringEncoder stringEncoder) {
		try {
			return (FlexoXMLSerializableObject) XMLDecoder.decodeObjectWithMapping(anXMLRepresentation, mapping, builder, stringEncoder);
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * public static FlexoXMLSerializableObject instanciateFromXML (String
	 * anXMLRepresentation, XMLMapping mapping) { try { return
	 * (FlexoXMLSerializableObject)XMLDecoder.decodeObjectWithMapping(anXMLRepresentation,mapping); }
	 * catch (Exception e) { // Warns about the exception if
	 * (logger.isLoggable(Level.WARNING)) logger.warning ("Exception raised:
	 * "+e.getClass().getName()+". See console for details.");
	 * e.printStackTrace(); return null; } }
	 */
	@Override
	public void initializeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("initializeDeserialization() for " + this.getClass().getName());
		}
		isDeserializing = true;

		_builder = builder;
		if (builder instanceof FlexoBuilder) {
			if (((FlexoBuilder<FlexoXMLStorageResource>) builder).isCloner) {
				isCreatedByCloning = true;
			}
		}
		// Modified by DVA on June 2006: getBindingModel() cannot be called now. too soon !!
		try {
			if (this instanceof Bindable && ((Bindable) this).getBindingModel() != null) {
				if (builder instanceof FlexoBuilder) {
					((FlexoBuilder) builder).getProject().getAbstractBindingConverter().setBindable((Bindable) this);
					((FlexoBuilder) builder).getProject().getBindingAssignementConverter().setBindable((Bindable) this);
					// ((FlexoBuilder) builder).getProject().getBindingValueConverter().setBindable((Bindable) this);
					// ((FlexoBuilder) builder).getProject().getBindingExpressionConverter().setBindable((Bindable) this);
				} else if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("Builder is not a FlexoBuilder!!!");
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	private Object _builder;

	protected Object getBuilder() {
		return _builder;
	}

	/**
	 * Property that indicates wheter the current object is currently being created from another already existing object
	 */
	private boolean isCreatedByCloning = false;

	/**
	 * Property that indicates wheter the current object is currently being created from another already existing object
	 */
	public boolean isCreatedByCloning() {
		return isCreatedByCloning;
	}

	/**
	 * Property that indicates wheter the current object is currently copied to create a new clone
	 */
	private boolean isBeingCloned = false;

	@Override
	public void initializeCloning() {
		isBeingCloned = true;
	}

	@Override
	public void finalizeCloning() {
		isBeingCloned = false;
	}

	/**
	 * Property that indicates wheter the current object is currently copied to create a new clone
	 */
	@Override
	public boolean isBeingCloned() {
		if (getXMLResourceData() == this) {
			return isBeingCloned;
		} else {
			if (getXMLResourceData() != null) {
				return getXMLResourceData().isBeingCloned();
			} else {
				return isBeingCloned;
			}
		}
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("finalizeDeserialization() for " + this.getClass().getName());
		}
		if (builder instanceof FlexoBuilder) {
			// GPO: I really think that this line has absolutely no effect but since it was there before, I leave it
			((FlexoBuilder<FlexoXMLStorageResource>) builder).isCloner = false;
		}
		// Note: we need to clear is modified before setting isDeserializing to false
		// See method clearIsModified in FlexoProject.

		clearIsModified(true);// we just finished deserializing so all update dates are wrong and should be forgotten
		isDeserializing = false;
		isCreatedByCloning = false;
		_builder = null;
	}

	public final void finalizeDeserialization() {
		finalizeDeserialization(null);
	}

	public Thread getSerializingThread() {
		return serializingThread;
	}

	public void setSerializingThread(Thread serializingThread) {
		this.serializingThread = serializingThread;
	}

	@Override
	public void initializeSerialization() {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("initializeSerialization() for " + this.getClass().getName());
		}
		if (serializingThread == null || serializingThread == Thread.currentThread()) {
			serializingThread = Thread.currentThread();
		} else if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("Two different threads are trying to serialize " + getXMLResourceData() + "\n Thread already serializing is:"
					+ serializingThread + " and the new one is " + Thread.currentThread());
		}
		isSerializing = true;
	}

	@Override
	public void finalizeSerialization() {
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("finalizeSerialization() for " + this.getClass().getName());
		}
		isSerializing = false;
		serializingThread = null;
	}

	@Override
	public boolean isSerializing() {
		if (getXMLResourceData() == this) {
			return isSerializing && serializingThread == Thread.currentThread();
		} else {
			return getXMLResourceData() != null && getXMLResourceData().isSerializing();
		}
	}

	@Override
	public boolean isDeserializing() {
		if (getXMLResourceData() == this) {
			return isDeserializing;
		} else {
			if (getXMLResourceData() != null) {
				return getXMLResourceData().isDeserializing();
			} else {
				return isDeserializing;
			}
		}
	}

	public boolean isResourceModified() {
		if (getXMLResourceData() != null) {
			return getXMLResourceData().isModified();
		} else {
			return isModified();
		}
	}

	public Date resourceLastMemoryUpdate() {
		if (getXMLResourceData() != null) {
			return getXMLResourceData().lastMemoryUpdate();
		} else {
			return lastMemoryUpdate();
		}
	}

	public Date resourceLastUpdate() {
		if (getXMLResourceData() != null && getXMLResourceData().getFlexoResource() != null) {
			return getXMLResourceData().getFlexoResource().getLastUpdate();
		}
		return null;
	}

	@Override
	public boolean isModified() {
		return isModified;
	}

	@Override
	public Date lastMemoryUpdate() {
		if (lastMemoryUpdate == null) {
			if (getXMLResourceData() != null && getXMLResourceData() != this) {
				lastMemoryUpdate = getXMLResourceData().lastMemoryUpdate();
			}
		}
		return lastMemoryUpdate;
	}

	private boolean ignoreNotifications = false;

	@Override
	public boolean ignoreNotifications() {
		if (getXMLResourceData() == null || getXMLResourceData() == this) {
			return ignoreNotifications;
		} else if (getXMLResourceData() instanceof FlexoXMLSerializableObject) {
			return ((FlexoXMLSerializableObject) getXMLResourceData()).ignoreNotifications();
		}
		return false;
	}

	/**
	 * Temporary method to prevent notifications. This should never be used by anyone except the screenshot generator
	 */
	@Override
	public final void setIgnoreNotifications() {
		ignoreNotifications = true;
	}

	/**
	 * Temporary method to reactivate notifications. This should never be used by anyone except the screenshot generator
	 */
	@Override
	public final void resetIgnoreNotifications() {
		ignoreNotifications = false;
	}

	@Override
	public synchronized void setIsModified() {
		if (ignoreNotifications) {
			return;
		}
		if (getXMLResourceData() == this && isModified == false && getXMLResourceData().getFlexoResource() != null) {
			// (new Exception("Resource "+getXMLResourceData().getFlexoResource()+" has been modified")).printStackTrace();
			logger.info(">>>>>>> Resource " + getXMLResourceData().getFlexoResource() + " has been modified");
			// A resource has been modified, while it's wasn't before
			// Don't forget to notify resource
			isModified = true;
			lastMemoryUpdate = new Date();
			getXMLResourceData().getFlexoResource().notifyResourceStatusChanged();
		}
		isModified = true;
		lastMemoryUpdate = new Date();
	}

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	public Date getLastUpdate() {
		if (lastMemoryUpdate == null) {
			return resourceLastUpdate();
		}
		return lastMemoryUpdate;
	}

	public String getLastUpdateAsString() {
		if (getLastUpdate() != null) {
			if (getLastUpdate().equals(new Date(0))) {
				return FlexoLocalization.localizedForKey("never");
			}
			return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getLastUpdate());
		}
		return "???";
	}

	@Override
	public synchronized void clearIsModified(boolean clearLastMemoryUpdate) {
		isModified = false;
		// GPO: I commented the line hereunder because I don't think that we need to reset this date
		if (clearLastMemoryUpdate) {
			lastMemoryUpdate = null;
		}
	}

	@Override
	public void setChanged() {
		setChanged(true);
	}

	public final void setChanged(boolean propagateModified) {
		/*
		 * The final keyword is added here mainly because this part of the code
		 * is highly sensitive. A synchronized modifier could cause many
		 * problems (essentially with the auto-saving thread)
		 */

		// logger.info("called setChanged in "+getClass().getName()+" isDeserializing()="+isDeserializing());

		if (isSerializing()) {
			return;
		}
		synchronized (this) {
			super.setChanged();
			if (!isDeserializing() && !isSerializing()) {
				if (propagateModified) {
					setIsModified();
				}
				if (getXMLResourceData() != null && getXMLResourceData() != this) {
					// This object is embedded in an XMLResourceData
					if (propagateModified) {
						getXMLResourceData().setIsModified();
					}
				}
			}
		}
	}

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme
	 * 
	 * @return
	 */
	@Override
	public abstract XMLStorageResourceData getXMLResourceData();

	/**
	 * 
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#notifyRM(org.openflexo.foundation.rm.RMNotification) by forwarding
	 * @see org.openflexo.foundation.rm.RMNotification to the related resource, if and only if this object represents the resource data
	 *      itself (@see org.openflexo.foundation.rm.FlexoResourceData). Otherwise, invoking this method is ignored.
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
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification) Receive a
	 *      notification that has been propagated by the ResourceManager scheme and coming from a modification on an other resource This
	 *      method is relevant if and only if this object represents the resource data itself (@see
	 *      org.openflexo.foundation.rm.FlexoResourceData). At this level, this method is ignored and just return, so you need to override
	 *      it in subclasses if you want to get the hook to do your stuff !
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResourceData#receiveRMNotification(org.openflexo.foundation.rm.RMNotification)
	 */
	@Override
	public void receiveRMNotification(RMNotification notification) throws FlexoException {
		// Ignore it at this level: please overrides this method in relevant
		// subclasses !
	}

	/**
	 * Overrides
	 * 
	 * @see org.openflexo.foundation.FlexoObservable#notifyObservers(org.openflexo.foundation.DataModification) by propagating
	 *      dataModification if this one implements
	 * @see org.openflexo.foundation.rm.RMNotification to the related resource
	 * 
	 * @see org.openflexo.foundation.FlexoObservable#notifyObservers(org.openflexo.foundation.DataModification)
	 */
	@Override
	public void notifyObservers(DataModification dataModification) {
		if (isSerializing()) {
			return;
		}
		super.notifyObservers(dataModification);
		if (dataModification instanceof RMNotification && getXMLResourceData() != null) {
			try {
				getXMLResourceData().notifyRM((RMNotification) dataModification);
			} catch (FlexoException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("FLEXO Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
	}

}
