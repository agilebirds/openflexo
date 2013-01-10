package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Modify {

	/**
	 * The property where to forward the "modified" state of this object. This mechanism is recursive, ie, if the value of the forward
	 * property also declares a {@link Modify}, then it will also be triggered.
	 * 
	 * @return the name of the property on which the flag modified should also be turned to true.
	 */
	public String forward();

	/**
	 * Wheter this entity should watch the "modified" state of the value of the property defined by {@link Modify#forward()} and resynch
	 * itself with it, whenever it is changed from <code>true</code> to <code>false</code>. Default value is <code>true</code>
	 * 
	 * @return true if this model entity "modified" state should be synch with the property defined by the {@link Modify#forward()}.
	 */
	public boolean synchWithForward() default true;
}
