package org.openflexo.model.factory;

public interface CloneableProxyObject {

	/**
	 * Clone current object, using meta informations provided by related class All property should be annotated with a @CloningStrategy
	 * annotation which determine the way of handling this property Don't compute any closure, and clone all required objects
	 * 
	 * @return newly created clone object
	 */
	public Object cloneObject();

	/**
	 * Clone current object, using meta informations provided by related class All property should be annotated with a @CloningStrategy
	 * annotation which determine the way of handling this property Supplied context is used to determine the closure of objects graph being
	 * constructed during this operation. If a property is marked as @CloningStrategy.CLONE but lead to an object outside scope of cloning
	 * (the closure being computed), then resulting value is nullified. When context is not set, don't compute any closure, and clone all
	 * required objects
	 * 
	 * @param context
	 * @return newly created clone object
	 */
	public Object cloneObject(Object... context);

}
