package org.openflexo.rest.client.model;

import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * <p>
 * Java class for ModelObject complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ModelObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelObject", propOrder = { "id" })
@XmlSeeAlso({ JobHistory.class, Project.class, Account.class, Slave.class, ProjectVersion.class, Session.class, Document.class,
		UserProject.class, Job.class, User.class })
public abstract class ModelObject implements HasPropertyChangeSupport {

	private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	@Override
	@XmlTransient
	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	@XmlTransient
	public String getDeletedProperty() {
		return null;
	}

	@XmlElement(name = "ID")
	protected Object id;

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getID() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 * 
	 */
	public void setID(Object value) {
		this.id = value;
	}

}
