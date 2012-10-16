package org.openflexo.model.factory;

import org.openflexo.model.annotations.ComplexEmbedded;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Root interface that PAMELA objects should extend in order to benefit from their default implementation handled by the
 * {@link ProxyMethodHandler}. All methods starting with 'performSuper' are method-accessors allowing implementing classes to call the
 * default behaviour. These methods should be considered as <code>protected</code> instead of <code>public</code> (but Java interfaces does
 * not allow that). Therefore, these method should never be invoked externally, ie, by a class which is not implementing this interface.
 * 
 * @author Guillaume
 * 
 */
public interface AccessibleProxyObject extends HasPropertyChangeSupport {

	/**
	 * The deleted property identifier.
	 */
	public static final String DELETED = "deleted";
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
	 * Invokes the default deletion code handled by {@link ProxyMethodHandler}.
	 * 
	 * @see AccessibleProxyObject#delete()
	 * @see AccessibleProxyObject#isDeleted()
	 */
	public void performSuperDelete();

	/**
	 * Invokes the default deletion code handled by {@link ProxyMethodHandler} with the provided <code>context</code>.
	 * 
	 * @param context
	 *            the deletion context. The context represents a list of objects which eventually will also be deleted. Objects in that
	 *            context may be deleted indirectly by deleting other objects, however, the invoker should make sure that those objects are
	 *            deleted by invoking one of the deletion methods.
	 * @see AccessibleProxyObject#delete()
	 * @see AccessibleProxyObject#isDeleted()
	 */
	public void performSuperDelete(Object... context);

	/**
	 * 
	 * @param value
	 * @return
	 */
	public Object performSuperFinder(Object value);

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
	 * Invokes the delete method as defined by the model entity associated with the class <code>modelEntityInterface</code>
	 * 
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity from which deletion information should be gathered.
	 * @see AccessibleProxyObject#delete()
	 * @see AccessibleProxyObject#isDeleted()
	 */
	public void performSuperDelete(Class<?> modelEntityInterface);

	/**
	 * Invokes the delete method as defined by the model entity associated with the class <code>modelEntityInterface</code>
	 * 
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity from which deletion information should be gathered.
	 * @param context
	 *            the deletion context. The context represents a list of objects which eventually will also be deleted. Objects in that
	 *            context may be deleted indirectly by deleting other objects, however, the invoker should make sure that those objects are
	 *            deleted by invoking one of the deletion methods.
	 * @see AccessibleProxyObject#delete(Object...)
	 * @see AccessibleProxyObject#isDeleted()
	 */
	public void performSuperDelete(Class<?> modelEntityInterface, Object... context);

	public Object performSuperFinder(Object value, Class<?> modelEntityInterface);

	/**
	 * Deletes the current object and all its embedded properties as defined by the {@link Embedded} and {@link ComplexEmbedded}
	 * annotations.
	 * 
	 * @see Embedded#deletionConditions()
	 * @see ComplexEmbedded#deletionConditions()
	 */
	public void delete();

	/**
	 * Deletes the current object and all its embedded properties as defined by the {@link Embedded} and {@link ComplexEmbedded}
	 * annotations. Moreover, the provided <code>context</code> represents a list of objects that will also be eventually deleted and which
	 * should be taken into account when computing embedded objects according to the deletion conditions. Invoking this method may result in
	 * deleting indirectly the objects provided by the <code>context</code>, however the invoker should make sure that they have been
	 * actually deleted.
	 * 
	 * @param context
	 *            the list of objects that will also be deleted and which should be taken into account when computing embedded objects.
	 * @see Embedded#deletionConditions()
	 * @see ComplexEmbedded#deletionConditions()
	 */
	public void delete(Object... context);

	/**
	 * Returns whether this object has been deleted or not.
	 * 
	 * @return true if this object has been deleted, false otherwise.
	 */
	public boolean isDeleted();

	public void performSuperSetModified(boolean modified);

	public Object performSuperFinder(String finderIdentifier, Object value);

	public boolean isSerializing();

	public boolean isDeserializing();

	public boolean isModified();

	public void setModified(boolean modified);

}
