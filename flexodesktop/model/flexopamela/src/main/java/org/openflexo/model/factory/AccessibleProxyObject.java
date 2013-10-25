package org.openflexo.model.factory;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Interface that PAMELA objects should extend in order to benefit from their default implementation handled by the
 * {@link ProxyMethodHandler}. All methods starting with 'performSuper' are method-accessors allowing implementing classes to call the
 * default behaviour. These methods should be considered as <code>protected</code> instead of <code>public</code> (but Java interfaces does
 * not allow that). Therefore, these method should never be invoked externally, ie, by a class which is not implementing this interface.
 * 
 * @author Guillaume
 * 
 */
public interface AccessibleProxyObject extends HasPropertyChangeSupport, KeyValueCoding {

	/**
	 * Invokes the getter for the property with the given <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @return the value of the getter of the property identified by <code>propertyIdentifier</code>.
	 */
	public Object performSuperGetter(String propertyIdentifier);

	/**
	 * Invokes the setter for the property with the given <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 */
	public void performSuperSetter(String propertyIdentifier, Object value);

	/**
	 * Invokes the adder for the property with the given <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param value
	 *            the value to add
	 */
	public void performSuperAdder(String propertyIdentifier, Object value);

	/**
	 * Invokes the remover for the property with the given <code>propertyIdentifier</code>.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param value
	 *            the value to remove
	 */
	public void performSuperRemover(String propertyIdentifier, Object value);

	/**
	 * Returns the super getter as defined by the model entity associated with the class <code>modelEntityInterface</code>. This method is
	 * useful only in the case of conflicting multiply-inherited model properties.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity on which the model property should be looked up.
	 * @return the getter value as defined by the model entity associated with the class <code>modelEntityInterface</code>.
	 * @see AccessibleProxyObject#performSuperGetter(String)
	 */
	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface);

	/**
	 * Invokes the super setter as defined by the model entity associated with the class <code>modelEntityInterface</code>. This method is
	 * useful only in the case of conflicting multiply-inherited model properties.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity on which the model property should be looked up.
	 * @param value
	 *            the value to set
	 * @see AccessibleProxyObject#performSuperSetter(String, Object)
	 */
	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface);

	/**
	 * Invokes the super adder as defined by the model entity associated with the class <code>modelEntityInterface</code>. This method is
	 * useful only in the case of conflicting multiply-inherited model properties.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity on which the model property should be looked up.
	 * @param value
	 *            the value to add
	 * @see AccessibleProxyObject#performSuperAdder(String, Object)
	 */
	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface);

	/**
	 * Invokes the super adder as defined by the model entity associated with the class <code>modelEntityInterface</code>. This method is
	 * useful only in the case of conflicting multiply-inherited model properties.
	 * 
	 * @param propertyIdentifier
	 *            the identifier of the property
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity on which the model property should be looked up.
	 * @param value
	 *            the value to remove
	 * @see AccessibleProxyObject#performSuperRemover(String, Object)
	 */
	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface);

	/**
	 * Invokes the default <code>setModified</code> code handled by {@link ProxyMethodHandler}
	 * 
	 * @param modified
	 *            the value to set on the <code>modified</code> flag.
	 */
	public void performSuperSetModified(boolean modified);

	/**
	 * Invokes the default code for the finder identified by the given <code>finderIdentifier</code> for the provided <code>value</code>.
	 * 
	 * @param finderIdentifier
	 *            the identifier of the finder to run
	 * @param value
	 *            the value for which to look
	 * @return the object or list of objects found.
	 */
	public Object performSuperFinder(String finderIdentifier, Object value);

	/**
	 * Invokes the default code for the finder identified by the given <code>finderIdentifier</code> for the provided <code>value</code> as
	 * it has been declared on the model entity identified by the provided <code>modelEntityInterface</code>.
	 * 
	 * @param finderIdentifier
	 *            the identifier of the finder to run
	 * @param value
	 *            the value for which to look
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity from which the finder information should be gathered.
	 * @return the object or list of objects found.
	 */
	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface);

	/**
	 * Returns true if this object is currently being serialized
	 * 
	 * @return true if it is being serialized
	 */
	public boolean isSerializing();

	/**
	 * Returns true if this object is currently being deserialized
	 * 
	 * @return true if it being deserialized
	 */
	public boolean isDeserializing();

	/**
	 * Returns true if this object has been modified since:
	 * <ul>
	 * <li>it has been instantiated for the first time, or</li>
	 * <li>it has been serialized, or</li>
	 * <li>it has been deserialized.</li>
	 * </ul>
	 * 
	 * @return true if this object has been modified
	 */
	public boolean isModified();

	/**
	 * Sets the value of the <code>modified</code> flag.
	 * 
	 * @param modified
	 *            the value to set
	 */
	public void setModified(boolean modified);

	/**
	 * Return whether supplied object is equals to this, regarding persistant properties defined as PAMELA model
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equalsObject(Object obj);

	/**
	 * Destroy current object<br>
	 * After invoking this, the object won't be accessible.<br>
	 * To implements deleting/undeleting facilities, use {@link DeletableProxyObject} interface instead
	 */
	public void destroy();
}
