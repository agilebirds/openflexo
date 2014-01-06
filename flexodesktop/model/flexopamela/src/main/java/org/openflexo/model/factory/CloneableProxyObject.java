package org.openflexo.model.factory;

/**
 * Interface that PAMELA objects should extend in order to benefit from their default implementation handled by the
 * {@link ProxyMethodHandler} cloning facilities
 * 
 * @author sylvain
 * 
 */
public interface CloneableProxyObject {

	/**
	 * Clone current object, using meta informations provided by related class<br>
	 * All property should be annotated with a @CloningStrategy annotation which determine the way of handling this property Don't compute
	 * any closure, and clone all required objects
	 * 
	 * @return newly created clone object
	 */
	public Object cloneObject();

	/**
	 * Clone current object, using meta informations provided by related class<br>
	 * All property should be annotated with a @CloningStrategy annotation which determine the way of handling this property Supplied
	 * context is used to determine the closure of objects graph being constructed during this operation. If a property is marked as
	 * @CloningStrategy.CLONE but lead to an object outside scope of cloning (the closure being computed), then resulting value is
	 * nullified. When context is not set, don't compute any closure, and clone all required objects
	 * 
	 * @param context
	 * @return newly created clone object
	 */
	public Object cloneObject(Object... context);

	/**
	 * Returns true when <code>this</code> object is currently being created based on another object by using the "cloning" technique.
	 * 
	 * @return true if this object is currently being creating by cloning
	 */
	public boolean isCreatedByCloning();

	/**
	 * Returns true when <code>this</code> object is currently being cloned to create another object based on <code>this</code> object
	 * 
	 * @return true when <code>this</code> object is currently being cloned to create another object based on <code>this</code> object
	 */
	public boolean isBeingCloned();

}
