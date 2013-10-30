package org.openflexo.model.factory;

import org.openflexo.model.annotations.ComplexEmbedded;
import org.openflexo.model.annotations.Embedded;

/**
 * Interface that PAMELA objects should extend in order to benefit from their default implementation handled by the
 * {@link ProxyMethodHandler} deleting/undeleting facilities
 * 
 * @author sylvain
 * 
 */
public interface DeletableProxyObject {

	/**
	 * The deleted property identifier.
	 */
	public static final String DELETED = "deleted";

	/**
	 * Invokes the default deletion code handled by {@link ProxyMethodHandler}.
	 * 
	 * @see DeletableProxyObject#delete()
	 * @see DeletableProxyObject#isDeleted()
	 */
	// public boolean performSuperDelete();

	/**
	 * Invokes the default deletion code handled by {@link ProxyMethodHandler} with the provided <code>context</code>.
	 * 
	 * @param context
	 *            the deletion context. The context represents a list of objects which eventually will also be deleted. Objects in that
	 *            context may be deleted indirectly by deleting other objects, however, the invoker should make sure that those objects are
	 *            deleted by invoking one of the deletion methods.
	 * @see DeletableProxyObject#delete()
	 * @see DeletableProxyObject#isDeleted()
	 */
	public boolean performSuperDelete(Object... context);

	/**
	 * Invokes the default un-deletion code handled by {@link ProxyMethodHandler}.
	 * 
	 * @see DeletableProxyObject#delete()
	 * @see DeletableProxyObject#isDeleted()
	 */
	public boolean performSuperUndelete();

	/**
	 * Invokes the delete method as defined by the model entity associated with the class <code>modelEntityInterface</code>
	 * 
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity from which deletion information should be gathered.
	 * @see DeletableProxyObject#delete()
	 * @see DeletableProxyObject#isDeleted()
	 */
	// public void performSuperDelete(Class<?> modelEntityInterface);

	/**
	 * Invokes the delete method as defined by the model entity associated with the class <code>modelEntityInterface</code>
	 * 
	 * @param modelEntityInterface
	 *            the class corresponding to the model entity from which deletion information should be gathered.
	 * @param context
	 *            the deletion context. The context represents a list of objects which eventually will also be deleted. Objects in that
	 *            context may be deleted indirectly by deleting other objects, however, the invoker should make sure that those objects are
	 *            deleted by invoking one of the deletion methods.
	 * @see DeletableProxyObject#delete(Object...)
	 * @see DeletableProxyObject#isDeleted()
	 */
	public void performSuperDelete(Class<?> modelEntityInterface, Object... context);

	/**
	 * Deletes the current object and all its embedded properties as defined by the {@link Embedded} and {@link ComplexEmbedded}
	 * annotations.
	 * 
	 * @see Embedded#deletionConditions()
	 * @see ComplexEmbedded#deletionConditions()
	 */
	// public boolean delete();

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
	public boolean delete(Object... context);

	/**
	 * Un-deletes the current object
	 * 
	 * @see Embedded#deletionConditions()
	 * @see ComplexEmbedded#deletionConditions()
	 */
	public boolean undelete();

	/**
	 * Returns whether this object has been deleted or not.
	 * 
	 * @return true if this object has been deleted, false otherwise.
	 */
	public boolean isDeleted();

}
